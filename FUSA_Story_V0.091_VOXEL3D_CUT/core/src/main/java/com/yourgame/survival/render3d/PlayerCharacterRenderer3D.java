package com.yourgame.survival.render3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.MathUtils;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.world.World;

import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

/**
 * Renders the PLAYER as a 3D animated character (glb).
 * Visual-only: gameplay/collision remain unchanged.
 */
public final class PlayerCharacterRenderer3D {
  // Must match VoxelWorldRenderer / WorldPropRenderer3D vertical step.
  private static final float VOXEL_VZ = 6f;

  // DefaultShader has a low default bone limit (12). Our GLB player rig needs more.
  private final ModelBatch modelBatch;
  private final Environment env = new Environment();

  private SceneAsset rogueAsset;
  private Model rogueModel;
  private ModelInstance rogueInst;

  // KayKit GLBs are typically Y-up. Our world is Z-up => rotate model upright once.
  private static final float MODEL_UP_FIX_DEG = 90f;
  // Knight.glb pivot is not perfectly on the feet; lift it so it doesn't clip into the ground.
  private static final float MODEL_Z_OFFSET = 90f;

  // Control: player faces cursor (top-down) via yaw around body axis.
  // Disabled (requested): facing is driven by gameplay direction (N/E/S/W), not the mouse.
  private static final boolean FACE_CURSOR = false;
  // Model forward axis offset (tune if Knight looks 90/180deg off).
  private static final float MODEL_YAW_OFFSET_DEG = 90f;
  private AnimationController anim;

  private String idleAnimId;
  private String runAnimId;
  private String curAnimId;

  public PlayerCharacterRenderer3D() {
    env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.95f, 0.95f, 0.95f, 1f));
    env.add(new DirectionalLight().set(0.85f, 0.85f, 0.85f, -0.25f, -0.55f, -1f));

    // Increase bone limit so skinned GLB rigs don't crash DefaultShader.
    DefaultShader.Config cfg = new DefaultShader.Config();
    cfg.numBones = 32; // Rogue.glb currently needs 23
    modelBatch = new ModelBatch(new DefaultShaderProvider(cfg));
  }

  private void ensureLoaded() {
    if (rogueInst != null) return;

    try {
      // Load from game assets (no AssetManager to keep this minimal).
      // Requested: use KayKit Knight (with textures).
      rogueAsset = new GLBLoader().load(Gdx.files.internal("models/adventurers/Knight.glb"));
      rogueModel = (rogueAsset == null || rogueAsset.scene == null) ? null : rogueAsset.scene.model;
      if (rogueModel == null) throw new RuntimeException("Knight.glb loaded but model was null");
      rogueInst = new ModelInstance(rogueModel);
    } catch (Throwable t) {
      // Visual-only: if GLB fails to load (bad file, missing dependency, etc.), disable 3D player.
      rogueAsset = null;
      rogueModel = null;
      rogueInst = null;
      anim = null;
      idleAnimId = null;
      runAnimId = null;
      curAnimId = null;
      try { if (Gdx.app != null) Gdx.app.error("3D", "GLB player model load failed; disabling 3D player", t); } catch (Throwable ignored) {}
      return;
    }

    anim = new AnimationController(rogueInst);

    // Pick animation ids.
    idleAnimId = pickAnim("idle");
    runAnimId = pickAnim("run", "walk");
    if (idleAnimId == null) idleAnimId = firstAnim();
    if (runAnimId == null) runAnimId = idleAnimId;

    if (idleAnimId != null) {
      curAnimId = idleAnimId;
      anim.setAnimation(curAnimId, -1);
    }
  }

  private String firstAnim() {
    if (rogueModel == null || rogueModel.animations == null || rogueModel.animations.size == 0) return null;
    return rogueModel.animations.first().id;
  }

  private String pickAnim(String... containsLower) {
    if (rogueModel == null || rogueModel.animations == null) return null;
    for (int i = 0; i < rogueModel.animations.size; i++) {
      String id = rogueModel.animations.get(i).id;
      if (id == null) continue;
      String low = id.toLowerCase();
      for (String c : containsLower) {
        if (c != null && !c.isEmpty() && low.contains(c)) return id;
      }
    }
    return null;
  }

  public void draw(float dt, World world, Camera cam, Entities es, int minCx, int maxCx, int minCy, int maxCy) {
    if (world == null || cam == null || es == null) return;

    ensureLoaded();
    if (rogueInst == null) return;

    // Find player entity.
    int p = -1;
    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      if (es.type[i] == EntityType.PLAYER) { p = i; break; }
    }
    if (p < 0) return;

    // Cull same as sprite renderer.
    if (!es.isAlwaysActive(p)) {
      int ecx = (int) Math.floor((es.x[p] / World.TILE_WORLD) / World.CHUNK_SIZE);
      int ecy = (int) Math.floor((es.y[p] / World.TILE_WORLD) / World.CHUNK_SIZE);
      if (ecx < minCx || ecx > maxCx || ecy < minCy || ecy > maxCy) return;
    }

    float x = es.x[p];
    float y = es.y[p];

    int h = world.heightLevelAtWorldPeek(x, y, 0);
    float z = (1f + Math.max(0, h)) * VOXEL_VZ;

    float yawDeg;

    if (!FACE_CURSOR) {
      // Facing yaw from dir: 0=N,1=E,2=S,3=W
      yawDeg = switch (es.dir[p]) {
        case 0 -> 90f;
        case 1 -> 0f;
        case 2 -> -90f;
        default -> 180f;
      };
      yawDeg += MODEL_YAW_OFFSET_DEG;
    }

    // Animation select by movement speed.
    float sp2 = es.vx[p] * es.vx[p] + es.vy[p] * es.vy[p];
    boolean moving = sp2 > (3.5f * 3.5f);
    String target = moving ? runAnimId : idleAnimId;
    if (target != null && (curAnimId == null || !curAnimId.equals(target))) {
      curAnimId = target;
      anim.setAnimation(curAnimId, -1);
    }

    if (anim != null) anim.update(dt);

    // FIXED SCALE CHANGE (requested): +500% size.
    // Old: 0.45f. New: 2.25f.
    float s = 2.25f;

    // Build transform so that:
    // - rotation is ONLY yaw around world-Z (head->feet axis in our world)
    // - translation is NOT affected by rotation/scale (prevents orbiting/clipping)
    rogueInst.transform.idt();

    // Yaw around world-Z FIRST (ensures pure horizontal rotation in our world).
    rogueInst.transform.rotate(0f, 0f, 1f, yawDeg);

    // Then make model stand upright in our Z-up world (KayKit GLB is Y-up by default).
    rogueInst.transform.rotate(1f, 0f, 0f, MODEL_UP_FIX_DEG);

    rogueInst.transform.scale(s, s, s);

    // Apply world position last so it doesn't get rotated/scaled.
    rogueInst.transform.setTranslation(x, y, z + MODEL_Z_OFFSET);

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glDepthMask(true);

    modelBatch.begin(cam);
    modelBatch.render(rogueInst, env);
    modelBatch.end();

    Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
  }

  public void dispose() {
    try { if (modelBatch != null) modelBatch.dispose(); } catch (Throwable ignored) {}
    // SceneAsset owns the Model; dispose the asset.
    try { if (rogueAsset != null) rogueAsset.dispose(); } catch (Throwable ignored) {}
  }
}

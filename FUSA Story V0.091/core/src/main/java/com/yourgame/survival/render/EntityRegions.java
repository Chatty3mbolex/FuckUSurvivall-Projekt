package com.yourgame.survival.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
// Nicht fertiges Feature: // import com.yourgame.survival.entity.Entities; // (unused)
import com.yourgame.survival.entity.EntityType;

import com.badlogic.gdx.utils.Array;

/** Block E: atlas-backed sprite lookup for EntityRenderer (minimal, desktop-only). */
public final class EntityRegions {
  // Atlases are split:
  // - living: animated entities (player/animals/merchants)
  // - static: world nodes, builds, item icons/drops
  private final TextureAtlas livingAtlas;
  private final TextureAtlas staticAtlas;
  private final Texture propsTex;

  // cached regions (static)
  private final TextureRegion nodeTree;
  private final TextureRegion nodeStump;
  private final TextureRegion nodeRock;
  private final TextureRegion nodeOreIron;
  private final TextureRegion nodeBush;
  private final TextureRegion nodeFishSpot;

  private final TextureRegion buildChest;
  private final TextureRegion buildWorkbench;
  private final TextureRegion buildBed;
  private final TextureRegion buildCampfire;
  private final TextureRegion buildLamp;

  // HOME landmarks
  private final TextureRegion landmarkCastle;
  private final TextureRegion landmarkBridge;

  public EntityRegions() {
    this.livingAtlas = AtlasLoader.loadPreferFs("living");
    this.staticAtlas = AtlasLoader.loadPreferFs("static");

	    propsTex = new Texture(Gdx.files.internal("props.png"));
	    propsTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

	    // props.png regions (pixel coords in the sheet)
	    nodeTree = new TextureRegion(propsTex, 180, 38, 154, 199);
	    nodeStump = new TextureRegion(propsTex, 255, 267, 70, 31);
	    nodeRock = new TextureRegion(propsTex, 250, 496, 36, 35);
	    nodeOreIron = new TextureRegion(propsTex, 251, 535, 36, 35);
	    nodeBush = new TextureRegion(propsTex, 121, 498, 46, 46);
	    nodeFishSpot = reqStatic("node_fish_spot");

    // Builds are in the static atlas.
    // NOTE: build_chest.png is reserved for the multiplayer POI chest (server-spawned).
    // Player-buildable chest must never use that sprite.
    buildChest = reqStatic("build_crate_small");
    buildWorkbench = reqStatic("build_workbench");
    buildBed = reqStatic("build_bed");
    buildCampfire = reqStatic("build_campfire");
    buildLamp = reqStatic("build_lamp");

    // HOME landmarks
    landmarkCastle = reqStatic("landmark_castle");
    landmarkBridge = reqStatic("landmark_bridge");
  }

  /** Direct access to a static-atlas region by name (used by MP POI markers). Returns null if missing. */
  public TextureRegion staticRegion(String name) {
    if (name == null || name.isEmpty()) return null;
    try {
      return staticAtlas.findRegion(name);
    } catch (Throwable t) {
      return null;
    }
  }

  // Nicht fertiges Feature: unused convenience overload (kept for later).
  /*
  private TextureRegion reqLiving(String name) {
    TextureAtlas.AtlasRegion r = livingAtlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing living atlas region: " + name);
    return r;
  }
  */

  private TextureRegion reqLiving(String name, int index) {
    TextureAtlas.AtlasRegion r = livingAtlas.findRegion(name, index);
    if (r == null) r = livingAtlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing living atlas region: " + name + " (idx=" + index + ")");
    return r;
  }

  private Array<TextureAtlas.AtlasRegion> reqSeqLiving(String name) {
    Array<TextureAtlas.AtlasRegion> rs = livingAtlas.findRegions(name);
    if (rs == null || rs.size == 0) throw new IllegalStateException("Missing living atlas region sequence: " + name);
    return rs;
  }

  private TextureRegion reqStatic(String name) {
    TextureAtlas.AtlasRegion r = staticAtlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing static atlas region: " + name);
    return r;
  }

  private TextureRegion reqStatic(String name, int index) {
    TextureAtlas.AtlasRegion r = staticAtlas.findRegion(name, index);
    if (r == null) r = staticAtlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing static atlas region: " + name + " (idx=" + index + ")");
    return r;
  }

  public TextureRegion forEntity(EntityType t, float stateTime, int itemId, float vx, float vy, byte lastDir) {
    boolean moving = (vx * vx + vy * vy) > (5f * 5f);

    // Facing selection:
    // - PLAYER: trust es.dir[] (aim/fov), do NOT re-quantize from velocity.
    // - ORK/DEER: trust es.dir[] (already stabilized by AI), do NOT re-quantize from vx/vy here.
    // - Others: keep legacy behavior.
    String dir = switch (t) {
      case PLAYER, ORK_GRUNT, ANIMAL_DEER -> dirLetter(lastDir);
      default -> dir(vx, vy, lastDir);
    };

    // Some packs have different direction row conventions.
    // We map the desired facing dir (world) -> atlas dir per entity.
    String atlasDir = mapDir(t, dir);

    return switch (t) {
      case PLAYER -> moving
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          ? animFrame("player_walk_" + atlasDir, stateTime, 10f)
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          : animFrame("player_idle_" + atlasDir, stateTime, 6f);

      case ORK_GRUNT -> moving
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          ? animFrame("ork_grunt_walk_" + atlasDir, stateTime, 10f)
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          : animFrame("ork_grunt_idle_" + atlasDir, stateTime, 6f);

      case ANIMAL_DEER -> moving
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          ? animFrame("animal_deer_walk_" + atlasDir, stateTime, 10f)
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          : animFrame("animal_deer_idle_" + atlasDir, stateTime, 6f);

      case MERCHANT_ELF -> {
        // Fixed merchant: can use walk animation when moving.
        if (moving) {
          yield animFrameOr("merchant_elf_walk_" + atlasDir, stateTime, 10f, reqLiving("merchant_elf_idle", 0));
        }

        // Idle: prefer directional idle if it exists; otherwise use the non-directional idle sequence (animated).
        TextureRegion fb = animFrameOr("merchant_elf_idle", stateTime, 6f, reqLiving("merchant_elf_idle", 0));
        yield animFrameOr("merchant_elf_idle_" + atlasDir, stateTime, 6f, fb);
      }

      case MERCHANT_WANDERING -> {
        // Wandering merchant: use walk animation when moving.
        if (moving) {
          yield animFrameOr("merchant_elf_walk_" + atlasDir, stateTime, 10f, reqLiving("merchant_elf_idle", 0));
        }

        // Idle: prefer directional idle if it exists; otherwise use the non-directional idle sequence (animated).
        TextureRegion fb = animFrameOr("merchant_elf_idle", stateTime, 6f, reqLiving("merchant_elf_idle", 0));
        yield animFrameOr("merchant_elf_idle_" + atlasDir, stateTime, 6f, fb);
      }

      case NODE_TREE -> nodeTree;
      case NODE_STUMP -> nodeStump;
      case NODE_ROCK -> nodeRock;
      case NODE_ORE_IRON -> nodeOreIron;
      case NODE_BUSH -> nodeBush;
      case NODE_FISH_SPOT -> nodeFishSpot;

      case BUILD_CHEST -> buildChest;
      case BUILD_WORKBENCH -> buildWorkbench;
      case BUILD_BED -> buildBed;
      case BUILD_CAMPFIRE ->
          // Some packs provide an animated campfire sequence (build_campfire, indexed by TexturePacker).
          // If only a single frame exists, animFrameOr returns the cached static fallback.
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          animFrameOr("build_campfire", stateTime, 6f, buildCampfire);

      case BUILD_LAMP ->
          // Some packs provide an animated lamp sequence (build_lamp, indexed by TexturePacker).
          // If only a single frame exists, animFrameOr returns the cached static fallback.
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          animFrameOr("build_lamp", stateTime, 6f, buildLamp);

      case LANDMARK_CASTLE -> landmarkCastle;
      case LANDMARK_BRIDGE -> landmarkBridge;

      case ITEM_DROP -> {
        // GENERISCH, MUSS GEWECHSELT WERDEN !!
        // TexturePacker groups item_0..item_59.png as atlas base name "item" with indices.
        if (itemId >= 0) {
          // GENERISCH, MUSS GEWECHSELT WERDEN !!
          TextureAtlas.AtlasRegion r = staticAtlas.findRegion("item", itemId);
          if (r != null) yield r;
        }
        TextureAtlas.AtlasRegion fallback = staticAtlas.findRegion("item", 0);
        if (fallback != null) yield fallback;
        yield buildChest;
      }
    };
  }

  private String dir(float vx, float vy, byte lastDir) {
    float ax = Math.abs(vx);
    float ay = Math.abs(vy);
    if (ax < 1e-3f && ay < 1e-3f) return dirLetter(lastDir);
    if (ax > ay) return (vx >= 0f) ? "E" : "W";
    return (vy >= 0f) ? "N" : "S";
  }

  private String dirLetter(byte d) {
    return switch (d) {
      case 0 -> "N";
      case 1 -> "E";
      case 3 -> "W";
      default -> "S";
    };
  }

  private TextureRegion animFrame(String base, float stateTime, float fps) {
    Array<TextureAtlas.AtlasRegion> rs = reqSeqLiving(base);
    int idx = (int) (stateTime * fps) % rs.size;
    return rs.get(idx);
  }

  private TextureRegion animFrameOr(String base, float stateTime, float fps, TextureRegion fallback) {
    try {
      return animFrame(base, stateTime, fps);
    } catch (Throwable t) {
      return fallback;
    }
  }

  private String mapDir(EntityType t, String desired) {
    // Mapping derived from your observation (current in-game orientations).
    // Desired = world direction (N/E/S/W). Return = atlas direction to use for that entity.
    return switch (t) {
      case PLAYER -> switch (desired) {
        // Updated from latest test report:
        // Left->Left, Down->Up, Right->Down, Up->Right
        case "W" -> "W";
        case "S" -> "N";
        case "E" -> "S";
        case "N" -> "E";
        default -> desired;
      };

      case ORK_GRUNT -> switch (desired) {
        // Orc atlas labels: N=DOWN(front), S=UP(back), W=LEFT, E=RIGHT.
        // Current in-game report: moving LEFT triggers "up"; so we rotate mapping (keep E fixed) until it matches.
        // Desired: N/E/S/W (world). Return: atlas dir label to use.
        case "N" -> "W";
        case "E" -> "E";
        case "S" -> "N";
        case "W" -> "S";
        default -> desired;
      };

      // ANIMAL_DEER: after reslicing, it should be identity mapping.

      default -> desired;
    };
  }

  public TextureRegion itemIcon(int itemId) {
    TextureAtlas.AtlasRegion r = staticAtlas.findRegion("item", itemId);
    if (r != null) return r;
    TextureAtlas.AtlasRegion fb = staticAtlas.findRegion("item", 0);
    if (fb != null) return fb;
    return buildChest;
  }

  public void dispose() {
    try { livingAtlas.dispose(); } catch (Throwable ignored) {}
    try { staticAtlas.dispose(); } catch (Throwable ignored) {}
    propsTex.dispose();
  }
}

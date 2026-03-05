package com.yourgame.survival.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityMetrics;
import com.yourgame.survival.entity.EntityType;


/** Block E: sprite-based entity renderer with Y-sorting (painter's algorithm). */
public final class EntityRenderer {
  // Player hand overlay can be disabled when UI blocks interaction.
  private boolean playerHandVisible = true;

  /**
   * When false, the equipped-in-hand overlay + swing effect are suppressed for PLAYER.
   * (Used by GameScreen when a modal UI is open.)
   */
  public void setPlayerHandVisible(boolean visible) {
    this.playerHandVisible = visible;
  }

  private final EntityRegions regions;

  private float stateTime = 0f;

  // reuse buffers to avoid per-frame allocations
  private final int[] order = new int[Entities.MAX];
  private final float[] sortKey = new float[Entities.MAX];
  private final int[] loStack = new int[64];
  private final int[] hiStack = new int[64];

  // Swing FX tuning (purely visual). GameScreen drives the timer via entities.aiF0[playerE].
  private static final float SWING_DUR = 0.18f;

  public EntityRenderer(EntityRegions regions) {
    this.regions = regions;
  }

  public void tick(float dt) {
    stateTime += dt;
  }

  public void draw(SpriteBatch batch, Entities es, int minCx, int maxCx, int minCy, int maxCy) {
    int n = 0;
    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      if (!es.isAlwaysActive(i)) {
        int ecx = (int) Math.floor((es.x[i] / com.yourgame.survival.world.World.TILE_WORLD) / com.yourgame.survival.world.World.CHUNK_SIZE);
        int ecy = (int) Math.floor((es.y[i] / com.yourgame.survival.world.World.TILE_WORLD) / com.yourgame.survival.world.World.CHUNK_SIZE);
        if (ecx < minCx || ecx > maxCx || ecy < minCy || ecy > maxCy) continue;
      }
      order[n++] = i;
    }

    // sort by "ground contact" y (ascending) -> draw from back to front
    // Tie-breaker: stable by entity index (prevents equal-y flicker).
    for (int k = 0; k < n; k++) {
      int i = order[k];
      sortKey[i] = groundY(es.type[i], es.y[i]) + (i * 1e-6f);
    }
    sortByY(order, n, sortKey);

    for (int k = n - 1; k >= 0; k--) {
      int i = order[k];
      EntityType t = es.type[i];
      float x = es.x[i];
      float y = es.y[i];

      TextureRegion r = regions.forEntity(t, stateTime, es.itemId[i], es.vx[i], es.vy[i], es.dir[i]);

      float w = EntityMetrics.drawW(t);
      float h = EntityMetrics.drawH(t);

      float a = 1f;
      if ((t == EntityType.NODE_BUSH || t == EntityType.NODE_FISH_SPOT) && es.hp[i] <= 0f) a = 0.25f;
      batch.setColor(1f, 1f, 1f, a);
      batch.draw(r, x - w / 2f, y - h / 2f, w, h);

      // Visible equipped weapon/tool (body-attached; NOT cursor-coupled).
      // NOTE: equipped item id is stored in data0[].
      int eq = es.data0[i];
      if ((t == EntityType.PLAYER || t == EntityType.ORK_GRUNT) && eq >= 14 && eq <= 27) {
        if (t == EntityType.PLAYER && !playerHandVisible) {
          // still allow swing timer to decay; just don't render.
          continue;
        }

        // Facing: 0=N,1=E,2=S,3=W
        final int dir = es.dir[i];

        // Base weapon: NOT drawn when facing North (dir==0). Swing FX still shows.
        boolean drawBaseWeapon = (dir != 0);

        // Item icon region
        TextureRegion icon = regions.itemIcon(eq);
        float iw = 18f;
        float ih = 18f;

        // Base hand position (world units)
        // IMPORTANT: Offsets are now hard numbers (no World.TILE_WORLD coupling).
        float ox, oy;
        switch (dir) {
          case 1 -> { // E
            // Right: hand offset
            ox = 7.2f;
            oy = 2f;
          }
          case 2 -> { // S
            // Down: mirrored + slightly more left
            ox = 4.4f;
            oy = 2f;
          }
          case 3 -> { // W
            // Left: MUST stay in front of the body (not behind the back).
            // Mirror is handled below via scaleX.
            ox = -17.2f;
            oy = 2f;
          }
          default -> { // N (hidden base)
            ox = 0f; oy = 10f;
          }
        }

        // Per-item fine tuning must always be relative to the shared base offsets.
        // Default: no per-item shift (keeps all weapons/tools aligned the same).
        float addOx = 0f;
        float addOy = 0f;
        ox += addOx;
        oy += addOy;

        float bob = (float) Math.sin(stateTime * 12f + i * 0.1f) * 2.0f;

        // Rotation (degrees). Icon art is treated as "pointing right".
        // Request: when facing LEFT and when facing DOWN, the icon must be horizontally mirrored.
        // For LEFT we use mirroring instead of 180° rotation.
        float baseRotDeg = switch (dir) {
          case 0 -> 90f;   // N
          case 1 -> 0f;    // E
          case 2 -> -90f;  // S
          case 3 -> 0f;    // W (mirrored)
          default -> 0f;
        };

        float originX = iw * 0.2f;
        float originY = ih * 0.5f;
        float scaleX = 1f;

        // Mirror when looking LEFT or DOWN.
        if (dir == 3 || dir == 2) {
          scaleX = -1f;
          // keep the "hand grip" point consistent when mirrored
          originX = iw - originX;
        }

        // Swing timer: GameScreen drives entities.aiF0[playerE] as remaining seconds.
        float swingT = (t == EntityType.PLAYER) ? es.aiF0[i] : 0f;
        boolean swinging = swingT > 0f;

        // Swing effect (trail) — visible in all directions, including North.
        if (swinging) {
          float p = 1f - (swingT / SWING_DUR);
          if (p < 0f) p = 0f;
          if (p > 1f) p = 1f;

          // Arc sweep around the hand: -70° .. +70° (relative)
          float sweep = -70f + 140f * p;

          // Draw a small trail with decreasing alpha.
          for (int tstep = 0; tstep < 4; tstep++) {
            float tt = tstep / 3f;
            float trailRot = baseRotDeg + sweep - (18f * tt);

            float alpha = 0.55f * (1f - tt);
            batch.setColor(1f, 1f, 1f, alpha);

            float tx = x + ox;
            float ty = y + oy + bob;

            batch.draw(
              icon,
              (tx - iw / 2f), (ty - ih / 2f),
              originX, originY,
              iw, ih,
              scaleX, 1f,
              trailRot
            );
          }
          batch.setColor(1f, 1f, 1f, 1f);
        }

        // Base equipped weapon/tool in hand (suppressed when facing North).
        if (drawBaseWeapon) {
          float tx = x + ox;
          float ty = y + oy + bob;

          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(
            icon,
            (tx - iw / 2f), (ty - ih / 2f),
            originX, originY,
            iw, ih,
            scaleX, 1f,
            baseRotDeg
          );
        }
      }

      batch.setColor(1f, 1f, 1f, 1f);
    }
  }

  private float groundY(EntityType t, float yCenter) {
    float h = EntityMetrics.drawH(t);
    float bottom = yCenter - h / 2f;

    // tweak "foot" point a little per entity (so tall sprites don't mess up layering)
    return switch (t) {
      case NODE_TREE -> bottom + 14f;   // trunk/foot
      case NODE_STUMP -> bottom + 10f;
      case NODE_BUSH -> bottom + 8f;
      case NODE_FISH_SPOT -> bottom + 6f;

      // HOME landmark layering (explicit levels):
      // ground (lvl0) -> castle (lvl1) -> bridge (lvl2)
      // Implementation detail: lower sort key is drawn LATER (on top), because we draw from high->low.
      case LANDMARK_CASTLE -> bottom + 22f;
      case LANDMARK_BRIDGE -> bottom + 6f;

      default -> bottom + 6f;
    };
  }

  private void sortByY(int[] a, int n, float[] y) {
    // in-place quicksort (iterative stack) to avoid allocations
    int sp = 0;
    loStack[sp] = 0;
    hiStack[sp] = n - 1;
    sp++;

    while (sp > 0) {
      sp--;
      int lo = loStack[sp];
      int hi = hiStack[sp];
      if (lo >= hi) continue;

      int i = lo;
      int j = hi;
      float pivot = y[a[(lo + hi) >>> 1]];

      while (i <= j) {
        while (y[a[i]] < pivot) i++;
        while (y[a[j]] > pivot) j--;
        if (i <= j) {
          int tmp = a[i];
          a[i] = a[j];
          a[j] = tmp;
          i++;
          j--;
        }
      }

      // push larger partition first to keep stack shallow
      if (j - lo > hi - i) {
        if (lo < j) { loStack[sp] = lo; hiStack[sp] = j; sp++; }
        if (i < hi) { loStack[sp] = i; hiStack[sp] = hi; sp++; }
      } else {
        if (i < hi) { loStack[sp] = i; hiStack[sp] = hi; sp++; }
        if (lo < j) { loStack[sp] = lo; hiStack[sp] = j; sp++; }
      }
    }
  }
}

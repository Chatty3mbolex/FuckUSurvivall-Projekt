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

  // Swing FX tuning (purely visual).
  // PLAYER: GameScreen drives the timer via entities.aiF0[playerE].
  private static final float SWING_DUR = 0.18f;
  // ORK: derive swing from attack cooldown (entities.data0[ork] in ms).
  private static final float ORK_SWING_DUR = 0.78f;

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
        // IMPORTANT: chunk culling must use the entity's *ground contact* Y, not its sprite center.
        // Tall sprites (trees) have yCenter well above the tile and can otherwise get mis-bucketed
        // into the next chunk near chunk borders => invisible despite being spawned.
        float gy = groundY(es.type[i], es.y[i]);
        int ecx = (int) Math.floor((es.x[i] / com.yourgame.survival.world.World.TILE_WORLD) / com.yourgame.survival.world.World.CHUNK_SIZE);
        int ecy = (int) Math.floor((gy / com.yourgame.survival.world.World.TILE_WORLD) / com.yourgame.survival.world.World.CHUNK_SIZE);
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

        // --- Hand position offsets per direction ---
        // All offsets are relative to entity center. Positive X = right, Positive Y = up.
        // East and West are mirrors of each other. South is in front.
        float ox, oy;
        switch (dir) {
          case 1 -> { // E (right hand, right side)
            ox = 7.2f;
            oy = 2f;
          }
          case 2 -> { // S (hand in front, slightly right)
            ox = 5f;
            oy = -2f;
          }
          case 3 -> { // W (left hand, left side — mirror of East)
            ox = -7.2f;
            oy = 2f;
          }
          default -> { // N (hidden base)
            ox = 0f;
            oy = 10f;
          }
        }

        float bob = (float) Math.sin(stateTime * 12f + i * 0.1f) * 2.0f;

        // --- Rotation per direction ---
        // Icon art is "pointing right" by default.
        // E = 0° (default), N = 90° (point up), S = -90° (point down), W = 0° (mirrored)
        float baseRotDeg = switch (dir) {
          case 0 -> 90f;   // N: point up
          case 1 -> 0f;    // E: point right (default)
          case 2 -> -90f;  // S: point down
          case 3 -> 0f;    // W: mirrored, so 0° + flip = point left
          default -> 0f;
        };

        // Rotation origin: grip point near the handle end of the icon.
        float originX = iw * 0.2f;
        float originY = ih * 0.5f;
        float scaleX = 1f;

        // Mirror ONLY when facing West (left). NOT when facing South.
        // South uses rotation (-90°) which is correct on its own.
        if (dir == 3) {
          scaleX = -1f;
        }

        // --- Swing direction per facing ---
        // The swing arc must go in the natural direction for each facing.
        // E: sweep left-to-right (positive). W: sweep right-to-left (negative, but mirrored = positive visual).
        // S: sweep left-to-right. N: sweep left-to-right.
        float swingSign = 1f; // default: counter-clockwise arc
        if (dir == 3) {
          swingSign = -1f; // West: reverse arc (mirror handles visual direction)
        }

        // Swing timer:
        float swingT = 0f;
        float swingDur = SWING_DUR;
        if (t == EntityType.PLAYER) {
          swingT = es.aiF0[i];
          swingDur = SWING_DUR;
        } else if (t == EntityType.ORK_GRUNT) {
          float cdSec = Math.max(0f, es.data0[i]) / 1000f;
          swingT = Math.min(ORK_SWING_DUR, cdSec);
          swingDur = ORK_SWING_DUR;
        }
        boolean swinging = swingT > 0.01f;

        // Swing effect (trail)
        if (swinging) {
          float p = 1f - (swingT / swingDur);
          if (p < 0f) p = 0f;
          if (p > 1f) p = 1f;

          float halfArc = (t == EntityType.ORK_GRUNT) ? 95f : 70f;
          float sweep = swingSign * (-halfArc + 2f * halfArc * p);

          for (int tstep = 0; tstep < 4; tstep++) {
            float tt = tstep / 3f;
            float trailRot = baseRotDeg + sweep - (swingSign * 18f * tt);

            float alpha = (t == EntityType.ORK_GRUNT ? 0.75f : 0.55f) * (1f - tt);
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

        // Base equipped weapon/tool in hand
        if (drawBaseWeapon || (t == EntityType.ORK_GRUNT && swinging)) {
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

  // ============================================================
  // Tile-tree rendering (FUSA Story)
  // ============================================================

  private static int bitIndex(int tx, int ty, int wTiles) {
    return tx + ty * wTiles;
  }

  private static boolean bitGet(byte[] bits, int bit) {
    if (bits == null || bit < 0) return false;
    int i = bit >>> 3;
    if (i < 0 || i >= bits.length) return false;
    int m = 1 << (bit & 7);
    return (bits[i] & m) != 0;
  }

  /**
   * Draws dense tile-trees directly from presence/cut bitmasks.
   *
   * This avoids allocating Entities slots for forests.
   */
  public void drawTileTrees(
      SpriteBatch batch,
      com.yourgame.survival.world.World world,
      byte[] presentBits,
      byte[] cutBits,
      int areaW,
      int areaH,
      float camX,
      float camY,
      int radiusChunks,
      boolean drawTrees,
      boolean drawStumps,
      boolean ignoreCutBits
  ) {
    if (batch == null || world == null) return;
    if (presentBits == null || cutBits == null) return;
    if (areaW <= 0 || areaH <= 0) return;

    // Ensure we don't inherit a tinted/transparent color state from other renderers.
    batch.setColor(1f, 1f, 1f, 1f);

    // Draw around the camera center so we don't waste budget on offscreen chunks.
    int ccx = (int) Math.floor((camX / com.yourgame.survival.world.World.TILE_WORLD) / com.yourgame.survival.world.World.CHUNK_SIZE);
    int ccy = (int) Math.floor((camY / com.yourgame.survival.world.World.TILE_WORLD) / com.yourgame.survival.world.World.CHUNK_SIZE);
    int rChunks = Math.max(0, radiusChunks);

    // Safety/perf: cap how many trees we draw per frame.
    // Dense masks can easily mean 10k+ trees in view; that will tank FPS.
    int budget = 20000;

    // Iterate chunks from center outward so budget is spent on the visible area first.
    for (int rr = 0; rr <= rChunks; rr++) {
      // top/bottom rows of the ring
      for (int dx = -rr; dx <= rr; dx++) {
        int cx = ccx + dx;
        int cyTop = ccy + rr;
        int cyBot = ccy - rr;

        // top
        {
          com.yourgame.survival.world.Chunk c = world.peekChunk(cx, cyTop);
          if (c != null && c.layers != null) {
            budget = drawTileTreesChunk(batch, c, presentBits, cutBits, areaW, areaH, cx, cyTop, budget, drawTrees, drawStumps, ignoreCutBits);
            if (budget <= 0) return;
          }
        }

        // bottom (avoid duplicate when rr==0)
        if (rr != 0) {
          com.yourgame.survival.world.Chunk c = world.peekChunk(cx, cyBot);
          if (c != null && c.layers != null) {
            budget = drawTileTreesChunk(batch, c, presentBits, cutBits, areaW, areaH, cx, cyBot, budget, drawTrees, drawStumps, ignoreCutBits);
            if (budget <= 0) return;
          }
        }
      }

      // left/right columns of the ring (excluding corners already drawn)
      for (int dy = -(rr - 1); dy <= (rr - 1); dy++) {
        if (rr == 0) break;
        int cy = ccy + dy;
        int cxR = ccx + rr;
        int cxL = ccx - rr;

        {
          com.yourgame.survival.world.Chunk c = world.peekChunk(cxR, cy);
          if (c != null && c.layers != null) {
            budget = drawTileTreesChunk(batch, c, presentBits, cutBits, areaW, areaH, cxR, cy, budget, drawTrees, drawStumps, ignoreCutBits);
            if (budget <= 0) return;
          }
        }

        {
          com.yourgame.survival.world.Chunk c = world.peekChunk(cxL, cy);
          if (c != null && c.layers != null) {
            budget = drawTileTreesChunk(batch, c, presentBits, cutBits, areaW, areaH, cxL, cy, budget, drawTrees, drawStumps, ignoreCutBits);
            if (budget <= 0) return;
          }
        }
      }
    }
  }

  private int drawTileTreesChunk(
      SpriteBatch batch,
      com.yourgame.survival.world.Chunk c,
      byte[] presentBits,
      byte[] cutBits,
      int areaW,
      int areaH,
      int cx,
      int cy,
      int budget,
      boolean drawTrees,
      boolean drawStumps,
      boolean ignoreCutBits
  ) {
    if (budget <= 0) return 0;

    int baseTx = cx * com.yourgame.survival.world.World.CHUNK_SIZE;
    int baseTy = cy * com.yourgame.survival.world.World.CHUNK_SIZE;

    // Draw higher tiles first so lower tiles end up on top.
    for (int ly = com.yourgame.survival.world.World.CHUNK_SIZE - 1; ly >= 0; ly--) {
      int ty = baseTy + ly;
      if (ty < 0 || ty >= areaH) continue;
      for (int lx = 0; lx < com.yourgame.survival.world.World.CHUNK_SIZE; lx++) {
        int tx = baseTx + lx;
        if (tx < 0 || tx >= areaW) continue;

        int bit = bitIndex(tx, ty, areaW);
        if (!bitGet(presentBits, bit)) continue;

        int idx = lx + ly * com.yourgame.survival.world.World.CHUNK_SIZE;
        if ((c.layers.roadMask[idx] & 0xFF) != 0) continue;
        if ((c.layers.waterMask[idx] & 0xFF) != 0) continue;

        boolean cut = (!ignoreCutBits) && bitGet(cutBits, bit);
        if (cut) {
          if (!drawStumps) continue;
        } else {
          if (!drawTrees) continue;
        }
        EntityType want = cut ? EntityType.NODE_STUMP : EntityType.NODE_TREE;

        float wx = (tx + 0.5f) * com.yourgame.survival.world.World.TILE_WORLD;
        float wy = (ty * com.yourgame.survival.world.World.TILE_WORLD) + (cut ? 2.0f : 34.0f);

        TextureRegion r = regions.forEntity(want, stateTime, -1, 0f, 0f, (byte) 2);
        float w = EntityMetrics.drawW(want);
        float h = EntityMetrics.drawH(want);
        batch.draw(r, wx - w / 2f, wy - h / 2f, w, h);

        budget--;
        if (budget <= 0) return 0;
      }
    }
    return budget;
  }
}

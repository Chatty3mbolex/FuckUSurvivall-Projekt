package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.entity.EntityMetrics;
import com.yourgame.survival.world.World;

/**
 * Minimal entity-vs-entity separation (no physics engine).
 * Only affects PLAYER/ORK_GRUNT/ANIMAL_DEER to prevent overlap/standing-inside.
 */
public final class EntityCollisionSystem {
  public void resolve(Entities es, int minCx, int maxCx, int minCy, int maxCy) {
    // Collect only relevant live entities to keep the O(n^2) pair loop small.
    int[] idx = new int[256];
    int n = 0;

    // Nicht fertiges Feature: reserved for special-case player-vs-entity collision tuning
    // int player = -1;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      EntityType t = es.type[i];
      if (!es.isAlwaysActive(i)) {
        int ecx = (int) Math.floor((es.x[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
        int ecy = (int) Math.floor((es.y[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
        if (ecx < minCx || ecx > maxCx || ecy < minCy || ecy > maxCy) continue;
      }

      // Nicht fertiges Feature: if (t == EntityType.PLAYER) player = i; // (unused)
      if (t != EntityType.PLAYER && t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;
      if (n < idx.length) idx[n++] = i;
    }

    // One deterministic pass is enough for the current entity counts.
    for (int a = 0; a < n; a++) {
      int i = idx[a];
      EntityType ti = es.type[i];
      float rI = EntityMetrics.collisionRadius(ti);

      for (int b = a + 1; b < n; b++) {
        int j = idx[b];
        EntityType tj = es.type[j];
        float rJ = EntityMetrics.collisionRadius(tj);

        float dx = es.x[j] - es.x[i];
        float dy = es.y[j] - es.y[i];
        float d2 = dx * dx + dy * dy;

        float minDist = rI + rJ;
        float min2 = minDist * minDist;
        if (d2 >= min2) continue;

        // If perfectly overlapping, choose a stable axis.
        if (d2 <= 1e-6f) {
          dx = 1f;
          dy = 0f;
          d2 = 1f;
        }

        float d = (float) Math.sqrt(d2);
        float nx = dx / d;
        float ny = dy / d;

        float push = (minDist - d);

        // Balancing: push should be much weaker (80% reduction).
        push *= 0.20f;

        // Default: split push 50/50.
        float wi = 0.5f;
        float wj = 0.5f;

        // Optional bias: keep player slightly "heavier" so enemies are pushed a bit more.
        if (ti == EntityType.PLAYER && tj != EntityType.PLAYER) { wi = 0.35f; wj = 0.65f; }
        if (tj == EntityType.PLAYER && ti != EntityType.PLAYER) { wi = 0.65f; wj = 0.35f; }

        es.x[i] -= nx * push * wi;
        es.y[i] -= ny * push * wi;
        es.x[j] += nx * push * wj;
        es.y[j] += ny * push * wj;
      }
    }

    // Player + Deer vs world nodes: rectangular (AABB) trunk collider to avoid odd sliding.
    // Important: node positions are sprite-centers; the trunk is visually near the bottom of the sprite.
    //
    // Requested changes:
    // - Deer must have collision with trees.
    // - Tree collider/hitbox should be ~20px further DOWN than before.
    // - Tree collider should be ~10% smaller.
    //
    // Design decision:
    // - We keep node collision as AABB (not circle) because it is more stable for "thin trunks".
    // - We reuse the same resolver for both Player and Deer to keep behavior consistent.
    for (int mover = 0; mover < Entities.MAX; mover++) {
      if (!es.alive[mover]) continue;
      EntityType mt = es.type[mover];
      if (mt != EntityType.PLAYER && mt != EntityType.ANIMAL_DEER) continue;

      float px = es.x[mover];
      float py = es.y[mover];

      // Treat mover as a small box (not a circle) for stable resolution.
      float ph = EntityMetrics.collisionRadius(mt);
      float moverHalf = Math.max(3f, ph);

      for (int j = 0; j < Entities.MAX; j++) {
        if (!es.alive[j]) continue;
        EntityType t = es.type[j];
        if (t != EntityType.NODE_TREE && t != EntityType.NODE_STUMP) continue;

        float cx = es.x[j];
        float cy = es.y[j];

        if (t == EntityType.NODE_TREE) {
          // Old code placed trunk collider at: bottom + 24 + 8 = bottom + 32.
          // Requested: move collider DOWN by ~20px.
          float h = EntityMetrics.drawH(EntityType.NODE_TREE);
          float bottom = cy - h / 2f;
          // FIXED trunk center offset from sprite bottom (see TuningEntities).
          cy = bottom + com.yourgame.survival.tuning.TuningEntities.TREE_TRUNK_CENTER_FROM_BOTTOM_PX;
        }

        if (t == EntityType.NODE_STUMP) {
          // Stump collider: fixed offset (NO formulas). Tuned to sit around the visible stump.
          cy = cy - 6f;
        }

        // trunk AABB (square)
        // Requested: tree collider ~10% smaller.
        //
        // Implementation choice (as requested by user):
        // - We looked up the current collider baseline and set the explicit new value.
        // - Baseline (previous code): trunkHalfTree = 6f * 0.85f = 5.10
        // - New target: 10% smaller => 5.10 * 0.90 = 4.59
        //
        // Note: If we change the underlying art/metrics later, we must re-check this constant.
        float trunkHalf = (t == EntityType.NODE_TREE)
            ? com.yourgame.survival.tuning.TuningEntities.TREE_TRUNK_HALF
            : 3.5f;

        float dx = px - cx;
        float dy = py - cy;

        float overlapX = (moverHalf + trunkHalf) - Math.abs(dx);
        if (overlapX <= 0f) continue;
        float overlapY = (moverHalf + trunkHalf) - Math.abs(dy);
        if (overlapY <= 0f) continue;

        // Resolve along the axis of least penetration (reduces "sliding" / slingshot).
        if (overlapX < overlapY) {
          float sx = (dx < 0f) ? -1f : 1f;
          px += sx * overlapX;
        } else {
          float sy = (dy < 0f) ? -1f : 1f;
          py += sy * overlapY;
        }
      }

      es.x[mover] = px;
      es.y[mover] = py;
    }
  }
}

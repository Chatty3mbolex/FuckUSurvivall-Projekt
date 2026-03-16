package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityMetrics;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.sim.SimContext;

/**
 * Minimal harvesting: tree->stump, rock/ore -> removed.
 * Tool gating:
 * - Axe (itemId 14) for trees
 * - Pickaxe (itemId 15) for rocks/ores
 */
public final class HarvestSystem {
  public record HarvestResult(int dropItemId, int dropAmount, boolean replaceWithStump) {}

  /** Extra info so the caller can persist removal (procedural nodes must not respawn). */
  public record HarvestEvent(int dropItemId, int dropAmount, boolean replaceWithStump, EntityType harvestedType, float x, float y) {}

  /** Tick result for continuous harvesting. */
  public record HarvestTick(boolean didWork, HarvestEvent event) {}

  // ---- Tunables (see docs/Wertetabelle.md) ----
  private static final float DPS_TREE_AXE = com.yourgame.survival.tuning.TuningGameplay.DPS_TREE_AXE;
  private static final float DPS_TREE_OTHER_BLADE = com.yourgame.survival.tuning.TuningGameplay.DPS_TREE_OTHER_BLADE;
  private static final float DPS_ROCK_PICKAXE = com.yourgame.survival.tuning.TuningGameplay.DPS_ROCK_PICKAXE;
  private static final float DPS_ORE_PICKAXE = com.yourgame.survival.tuning.TuningGameplay.DPS_ORE_PICKAXE;

  // Deterministic RNG state (xorshift64*). Used for cooldown randomization.
  private long rngState;

  public HarvestSystem() {
    this(0x0ABC0DE5L);
  }

  public HarvestSystem(final long seed) {
    setSeed(seed);
  }

  public void setSeed(final long seed) {
    rngState = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L;
  }

  // --- SimContext overloads (preferred) ---

  public HarvestResult tryHarvest(final SimContext ctx, final float px, final float py, final float range, final int toolItemId) {
    return tryHarvest(ctx.entities, px, py, range, toolItemId);
  }

  public HarvestEvent tryHarvestEvent(final SimContext ctx, final float px, final float py, final float range, final int toolItemId) {
    return tryHarvestEvent(ctx.entities, px, py, range, toolItemId);
  }

  public HarvestTick tickHarvestFov(
      final SimContext ctx,
      final float px, final float py,
      final float aimX, final float aimY,
      final float fwdX, final float fwdY,
      final float fovDeg,
      final float range,
      final int toolItemId,
      final float dt
  ) {
    return tickHarvestFov(ctx.entities, px, py, aimX, aimY, fwdX, fwdY, fovDeg, range, toolItemId, dt);
  }

  public HarvestEvent tryHarvestEventFov(
      final SimContext ctx,
      final float px, final float py,
      final float aimX, final float aimY,
      final float fwdX, final float fwdY,
      final float fovDeg,
      final float range,
      final int toolItemId
  ) {
    return tryHarvestEventFov(ctx.entities, px, py, aimX, aimY, fwdX, fwdY, fovDeg, range, toolItemId);
  }

  // --- Legacy API (kept) ---

  public HarvestResult tryHarvest(final Entities es, final float px, final float py, final float range, final int toolItemId) {
    HarvestEvent ev = tryHarvestEvent(es, px, py, range, toolItemId);
    if (ev == null) return null;
    return new HarvestResult(ev.dropItemId, ev.dropAmount, ev.replaceWithStump);
  }

  public HarvestEvent tryHarvestEvent(final Entities es, final float px, final float py, final float range, final int toolItemId) {
    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.NODE_TREE && t != EntityType.NODE_ROCK && t != EntityType.NODE_ORE_IRON && t != EntityType.NODE_BUSH && t != EntityType.NODE_FISH_SPOT) continue;

      final float dx = es.x[i] - px;
      final float dy = es.y[i] - py;
      final float eff = range + EntityMetrics.radius(t);
      final float r2 = eff * eff;
      if (dx * dx + dy * dy > r2) continue;

      if (t == EntityType.NODE_TREE) {
        if (toolItemId != 14) return null;

        // Only harvest from below / lower side (not from the crown).
        // Nicht fertiges Feature: // final float treeW = EntityMetrics.drawW(EntityType.NODE_TREE); // (unused)
        final float treeH = EntityMetrics.drawH(EntityType.NODE_TREE);
        final float treeX = es.x[i];
        final float treeY = es.y[i];

        // Requested: tree hitbox should be ~20px further down and ~10% smaller.
        // We mirror the collision-system intent here so harvesting feels consistent with collisions.
        final boolean below = px /* unused */ >= -Float.MAX_VALUE && py <= (treeY - treeH * 0.10f + com.yourgame.survival.tuning.TuningEntities.TREE_HARVEST_BELOW_EXTRA_Y_PX);

        // Requested: tree hitbox ~10% smaller.
        // Implementation choice (as requested by user): explicit recomputed constant.
        // - Current EntityMetrics.drawW(NODE_TREE) = 72
        // - Baseline nearTrunk window: 72 * 0.35 = 25.20
        // - New target: 10% smaller => 25.20 * 0.90 = 22.68
        //
        // Note: If NODE_TREE drawW changes later, this must be re-checked.
        final boolean nearTrunk = Math.abs(dx) <= com.yourgame.survival.tuning.TuningEntities.TREE_HARVEST_NEAR_TRUNK_HALF_W;
        if (!below || !nearTrunk) return null;

        // kill tree, replace with stump
        final float x = treeX;
        final float y = treeY;
        es.kill(i);
        es.spawn(EntityType.NODE_STUMP, x - 1f, y - 30f);
        return new HarvestEvent(0, 5, true, t, x, y);
      }

      if (t == EntityType.NODE_ROCK) {
        if (toolItemId != 15) return null;
        final float x = es.x[i];
        final float y = es.y[i];
        es.kill(i);
        return new HarvestEvent(1, 4, false, t, x, y);
      }

      if (t == EntityType.NODE_ORE_IRON) {
        if (toolItemId != 15) return null;
        final float x = es.x[i];
        final float y = es.y[i];
        es.kill(i);
        return new HarvestEvent(2, 2, false, t, x, y);
      }

      if (t == EntityType.NODE_BUSH) {
        // Only hand (unarmed). Recurring: regrows after cooldown.
        if (toolItemId >= 0) return null;
        if (es.hp[i] <= 0f || es.aiT[i] > 0f) return null;

        final float x = es.x[i];
        final float y = es.y[i];
        // 15..60 min randomized
        es.hp[i] = 0f;
        es.aiT[i] = (15f * 60f) + nextFloat01() * (45f * 60f);
        return new HarvestEvent(34, 1, false, t, x, y);
      }

      if (t == EntityType.NODE_FISH_SPOT) {
        // Only fishing rod (Angel).
        if (toolItemId != 36) return null;
        if (es.hp[i] <= 0f || es.aiT[i] > 0f) return null;

        final float x = es.x[i];
        final float y = es.y[i];
        // 15..60 min randomized
        es.hp[i] = 0f;
        es.aiT[i] = (15f * 60f) + nextFloat01() * (45f * 60f);
        return new HarvestEvent(35, 1, false, t, x, y);
      }
    }
    return null;
  }

  /**
   * Continuous harvest tick (hold RMB).
   *
   * Rules:
   * - Trees: only blades/saws can damage. Axe is fastest.
   * - Rocks/Ores: only pickaxe yields resources.
   * - Requires target within reach AND inside FOV.
   *
   * Selection: harvestable node closest to the aim point.
   */
  public HarvestTick tickHarvestFov(
      final Entities es,
      final float px,
      final float py,
      final float aimX,
      final float aimY,
      final float fwdX,
      final float fwdY,
      final float fovDeg,
      final float range,
      final int toolItemId,
      final float dt
  ) {
    // Aim direction (for initial FOV gate)
    final float adx = aimX - px;
    final float ady = aimY - py;
    final float al2 = adx * adx + ady * ady;
    if (al2 <= 1e-6f) return new HarvestTick(false, null);

    final float ainv = (float) (1.0 / Math.sqrt(al2));
    final float ax = adx * ainv;
    final float ay = ady * ainv;

    final float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));
    if (fwdX * ax + fwdY * ay < cosHalf) return new HarvestTick(false, null);

    int best = -1;
    float bestAimD2 = Float.POSITIVE_INFINITY;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.NODE_TREE && t != EntityType.NODE_ROCK && t != EntityType.NODE_ORE_IRON && t != EntityType.NODE_BUSH && t != EntityType.NODE_FISH_SPOT) continue;
      if ((t == EntityType.NODE_BUSH || t == EntityType.NODE_FISH_SPOT) && (es.hp[i] <= 0f || es.aiT[i] > 0f)) continue;

      final float dx = es.x[i] - px;
      final float dy = es.y[i] - py;
      final float eff = range + EntityMetrics.radius(t);
      if (dx * dx + dy * dy > eff * eff) continue;

      // node must be inside cone
      final float d2 = dx * dx + dy * dy;
      final float inv = (float) (1.0 / Math.sqrt(Math.max(d2, 1e-6f)));
      final float nx = dx * inv;
      final float ny = dy * inv;
      if (fwdX * nx + fwdY * ny < cosHalf) continue;

      final float ax2 = es.x[i] - aimX;
      final float ay2 = es.y[i] - aimY;
      final float aimD2 = ax2 * ax2 + ay2 * ay2;
      if (aimD2 < bestAimD2) {
        bestAimD2 = aimD2;
        best = i;
      }
    }

    if (best < 0) return new HarvestTick(false, null);

    final EntityType t = es.type[best];

    // Recurring nodes: instant harvest with cooldown.
    if (t == EntityType.NODE_BUSH) {
      if (toolItemId >= 0) return new HarvestTick(false, null); // hand only
      if (es.hp[best] <= 0f || es.aiT[best] > 0f) return new HarvestTick(false, null);
      final float x = es.x[best];
      final float y = es.y[best];
      es.hp[best] = 0f;
      es.aiT[best] = (15f * 60f) + nextFloat01() * (45f * 60f);
      return new HarvestTick(true, new HarvestEvent(34, 1, false, t, x, y));
    }

    if (t == EntityType.NODE_FISH_SPOT) {
      if (toolItemId != 36) return new HarvestTick(false, null); // Angel only
      if (es.hp[best] <= 0f || es.aiT[best] > 0f) return new HarvestTick(false, null);
      final float x = es.x[best];
      final float y = es.y[best];
      es.hp[best] = 0f;
      es.aiT[best] = (15f * 60f) + nextFloat01() * (45f * 60f);
      return new HarvestTick(true, new HarvestEvent(35, 1, false, t, x, y));
    }

    // Tool schema
    final boolean bladed = (toolItemId == 14 || toolItemId == 16 || toolItemId == 20 || toolItemId == 21 || toolItemId == 23 || toolItemId == 25);
    final boolean pickaxe = (toolItemId == 15);

    float dps = 0f;
    if (t == EntityType.NODE_TREE) {
      if (!bladed) return new HarvestTick(false, null);
      dps = (toolItemId == 14) ? DPS_TREE_AXE : DPS_TREE_OTHER_BLADE;

      // Only harvest from below / lower side (not from the crown).
      final float treeW = EntityMetrics.drawW(EntityType.NODE_TREE);
      final float treeH = EntityMetrics.drawH(EntityType.NODE_TREE);
      final float treeX = es.x[best];
      final float treeY = es.y[best];

      final float pdx = treeX - px;
      final boolean below = py <= (treeY - treeH * 0.10f);
      final boolean nearTrunk = Math.abs(pdx) <= (treeW * 0.35f);
      if (!below || !nearTrunk) return new HarvestTick(false, null);
    } else if (t == EntityType.NODE_ROCK) {
      if (!pickaxe) return new HarvestTick(false, null);
      dps = DPS_ROCK_PICKAXE;
    } else { // ORE
      if (!pickaxe) return new HarvestTick(false, null);
      dps = DPS_ORE_PICKAXE;
    }

    if (dps <= 0f) return new HarvestTick(false, null);

    es.hp[best] -= dps * dt;
    if (es.hp[best] > 0f) {
      return new HarvestTick(true, null);
    }

    // destroyed
    final float x = es.x[best];
    final float y = es.y[best];

    if (t == EntityType.NODE_TREE) {
      es.kill(best);
      es.spawn(EntityType.NODE_STUMP, x - 1f, y - 30f);
      return new HarvestTick(true, new HarvestEvent(0, 5, true, t, x, y));
    }

    if (t == EntityType.NODE_ROCK) {
      es.kill(best);
      return new HarvestTick(true, new HarvestEvent(1, 4, false, t, x, y));
    }

    es.kill(best);
    return new HarvestTick(true, new HarvestEvent(2, 2, false, t, x, y));
  }

  /**
   * One-shot harvest (legacy) – kept for compatibility.
   */
  public HarvestEvent tryHarvestEventFov(
      final Entities es,
      final float px,
      final float py,
      final float aimX,
      final float aimY,
      final float fwdX,
      final float fwdY,
      final float fovDeg,
      final float range,
      final int toolItemId
  ) {
    // Aim direction (for initial FOV gate)
    final float adx = aimX - px;
    final float ady = aimY - py;
    final float al2 = adx * adx + ady * ady;
    if (al2 <= 1e-6f) return null;

    final float ainv = (float) (1.0 / Math.sqrt(al2));
    final float ax = adx * ainv;
    final float ay = ady * ainv;

    final float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));
    if (fwdX * ax + fwdY * ay < cosHalf) return null;

    int best = -1;
    float bestAimD2 = Float.POSITIVE_INFINITY;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.NODE_TREE && t != EntityType.NODE_ROCK && t != EntityType.NODE_ORE_IRON && t != EntityType.NODE_BUSH && t != EntityType.NODE_FISH_SPOT) continue;
      if ((t == EntityType.NODE_BUSH || t == EntityType.NODE_FISH_SPOT) && (es.hp[i] <= 0f || es.aiT[i] > 0f)) continue;

      final float dx = es.x[i] - px;
      final float dy = es.y[i] - py;
      final float eff = range + EntityMetrics.radius(t);
      if (dx * dx + dy * dy > eff * eff) continue;

      // node must be inside facing cone
      final float d2 = dx * dx + dy * dy;
      final float inv = (float) (1.0 / Math.sqrt(Math.max(d2, 1e-6f)));
      final float nx = dx * inv;
      final float ny = dy * inv;
      if (fwdX * nx + fwdY * ny < cosHalf) continue;

      final float ax2 = es.x[i] - aimX;
      final float ay2 = es.y[i] - aimY;
      final float aimD2 = ax2 * ax2 + ay2 * ay2;
      if (aimD2 < bestAimD2) {
        bestAimD2 = aimD2;
        best = i;
      }
    }

    if (best < 0) return null;

    final EntityType t = es.type[best];

    if (t == EntityType.NODE_BUSH) {
      if (toolItemId >= 0) return null; // hand only
      if (es.hp[best] <= 0f || es.aiT[best] > 0f) return null;
      final float x = es.x[best];
      final float y = es.y[best];
      es.hp[best] = 0f;
      es.aiT[best] = (15f * 60f) + nextFloat01() * (45f * 60f);
      return new HarvestEvent(34, 1, false, t, x, y);
    }

    if (t == EntityType.NODE_FISH_SPOT) {
      if (toolItemId != 36) return null; // Angel only
      if (es.hp[best] <= 0f || es.aiT[best] > 0f) return null;
      final float x = es.x[best];
      final float y = es.y[best];
      es.hp[best] = 0f;
      es.aiT[best] = (15f * 60f) + nextFloat01() * (45f * 60f);
      return new HarvestEvent(35, 1, false, t, x, y);
    }

    if (t == EntityType.NODE_TREE) {
      if (toolItemId != 14) return null;

      // Only harvest from below / lower side (not from the crown).
      final float treeW = EntityMetrics.drawW(EntityType.NODE_TREE);
      final float treeH = EntityMetrics.drawH(EntityType.NODE_TREE);
      final float treeX = es.x[best];
      final float treeY = es.y[best];

      final float dx = treeX - px;
      final boolean below = py <= (treeY - treeH * 0.10f);
      final boolean nearTrunk = Math.abs(dx) <= (treeW * 0.35f);
      if (!below || !nearTrunk) return null;

      es.kill(best);
      es.spawn(EntityType.NODE_STUMP, treeX - 1f, treeY - 30f);
      return new HarvestEvent(0, 5, true, t, treeX, treeY);
    }

    if (t == EntityType.NODE_ROCK) {
      if (toolItemId != 15) return null;
      final float x = es.x[best];
      final float y = es.y[best];
      es.kill(best);
      return new HarvestEvent(1, 4, false, t, x, y);
    }

    // ORE_IRON
    if (toolItemId != 15) return null;
    final float x = es.x[best];
    final float y = es.y[best];
    es.kill(best);
    return new HarvestEvent(2, 2, false, t, x, y);
  }

  // === RNG helpers ===

  private long nextLong() {
    long x = rngState;
    x ^= x >>> 12;
    x ^= x << 25;
    x ^= x >>> 27;
    rngState = x;
    return x * 2685821657736338717L;
  }

  private float nextFloat01() {
    return ((nextLong() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
  }
}

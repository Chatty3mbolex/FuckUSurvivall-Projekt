package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityMetrics;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.sim.SimContext;
import com.yourgame.survival.world.World;

public final class BuildingSystem {
  public boolean canPlace(final SimContext ctx, final float wx, final float wy) {
    return canPlace(ctx.world, wx, wy);
  }

  public boolean canPlace(final World world, final float wx, final float wy) {
    // minimal: not on water (MUST NOT generate chunks)
    return !world.isWaterAtWorldPeek(wx, wy, true);
  }

  public int place(final SimContext ctx, final EntityType t, final float wx, final float wy, final float rotDeg, final int data0) {
    return place(ctx.entities, t, wx, wy, rotDeg, data0);
  }

  public int place(final Entities es, final EntityType t, final float wx, final float wy, final float rotDeg, final int data0) {
    final int e = es.spawn(t, wx, wy);
    if (e >= 0) {
      es.rot[e] = rotDeg;
      es.data0[e] = data0;
    }
    return e;
  }

  public boolean removeNearest(final SimContext ctx, final float wx, final float wy, final float range) {
    return removeNearest(ctx.entities, wx, wy, range);
  }

  public boolean removeNearest(final Entities es, final float wx, final float wy, final float range) {
    int best = -1;
    float bestD2 = Float.MAX_VALUE;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (!isBuild(t)) continue;
      final float dx = es.x[i] - wx;
      final float dy = es.y[i] - wy;
      final float d2 = dx * dx + dy * dy;
      final float eff = range + EntityMetrics.radius(t);
      final float r2 = eff * eff;
      if (d2 <= r2 && d2 < bestD2) {
        best = i;
        bestD2 = d2;
      }
    }

    if (best >= 0) {
      es.kill(best);
      return true;
    }
    return false;
  }

  private static boolean isBuild(final EntityType t) {
    return t == EntityType.BUILD_CHEST
        || t == EntityType.BUILD_WORKBENCH
        || t == EntityType.BUILD_BED
        || t == EntityType.BUILD_CAMPFIRE
        || t == EntityType.BUILD_LAMP;
  }
}

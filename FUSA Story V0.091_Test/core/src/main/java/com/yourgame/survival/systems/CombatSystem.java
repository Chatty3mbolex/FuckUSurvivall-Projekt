package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityMetrics;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.sim.SimContext;

public final class CombatSystem {
  public record Kill(EntityType type, float x, float y) {}

  // Deterministic RNG state (xorshift64*). Used for spread.
  private long rngState;

  public CombatSystem() {
    this(0xC0FFEE01L);
  }

  public CombatSystem(final long seed) {
    setSeed(seed);
  }

  public void setSeed(final long seed) {
    rngState = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L;
  }

  // --- SimContext overloads (preferred) ---

  /** Returns first kill (if any) caused by this melee swing (no FOV constraint). */
  public Kill melee(final SimContext ctx, final float ax, final float ay, final float range, final float dmg) {
    return melee(ctx.entities, ax, ay, range, dmg);
  }

  /** Returns first kill (if any) caused by this melee swing, but only if aim is inside the given FOV cone. */
  public Kill meleeFov(
      final SimContext ctx,
      final float ox, final float oy,
      final float aimX, final float aimY,
      final float fwdX, final float fwdY,
      final float fovDeg,
      final float range,
      final float dmg
  ) {
    return meleeFov(ctx.entities, ox, oy, aimX, aimY, fwdX, fwdY, fovDeg, range, dmg);
  }

  /**
   * Ranged hit-scan (no projectile system yet).
   *
   * FOV-limited: aim direction is clamped/validated against facing dir.
   * Accuracy: apply angular spread (radians) based on skill.
   */
  public Kill shootHitscan(
      final SimContext ctx,
      final float ox, final float oy,
      final float aimX, final float aimY,
      final float fwdX, final float fwdY,
      final float fovDeg,
      final float range,
      final float dmg,
      final float spreadRad
  ) {
    return shootHitscan(ctx.entities, ox, oy, aimX, aimY, fwdX, fwdY, fovDeg, range, dmg, spreadRad);
  }

  // --- Legacy API (kept) ---

  /** Returns first kill (if any) caused by this melee swing (no FOV constraint). */
  public Kill melee(final Entities es, final float ax, final float ay, final float range, final float dmg) {
    int best = -1;
    float bestD2 = Float.POSITIVE_INFINITY;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;

      final float dx = es.x[i] - ax;
      final float dy = es.y[i] - ay;

      final float eff = range + EntityMetrics.hitRadius(t);
      final float r2 = eff * eff;
      final float d2 = dx * dx + dy * dy;
      if (d2 <= r2 && d2 < bestD2) {
        bestD2 = d2;
        best = i;
      }
    }

    if (best < 0) return null;

    final EntityType t = es.type[best];
    es.hp[best] -= dmg;
    if (es.hp[best] <= 0f) {
      final float x = es.x[best];
      final float y = es.y[best];
      es.kill(best);
      return new Kill(t, x, y);
    }
    return null;
  }

  /** Returns first kill (if any) caused by this melee swing, but only if aim is inside the given FOV cone. */
  public Kill meleeFov(
      final Entities es,
      final float ox,
      final float oy,
      final float aimX,
      final float aimY,
      final float fwdX,
      final float fwdY,
      final float fovDeg,
      final float range,
      final float dmg
  ) {
    final float dx = aimX - ox;
    final float dy = aimY - oy;
    final float len2 = dx * dx + dy * dy;
    if (len2 <= 1e-6f) return null;

    final float inv = (float) (1.0 / Math.sqrt(len2));
    final float ax = dx * inv;
    final float ay = dy * inv;

    final float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));
    final float dot = fwdX * ax + fwdY * ay;
    if (dot < cosHalf) {
      return null;
    }

    int best = -1;
    float bestD2 = Float.POSITIVE_INFINITY;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;

      final float tx = es.x[i] - ox;
      final float ty = es.y[i] - oy;

      final float d2 = tx * tx + ty * ty;
      final float eff = range + EntityMetrics.hitRadius(t);
      if (d2 > eff * eff) continue;

      // target must also lie within FOV
      final float invT = (float) (1.0 / Math.sqrt(Math.max(d2, 1e-6f)));
      final float nx = tx * invT;
      final float ny = ty * invT;
      if (fwdX * nx + fwdY * ny < cosHalf) continue;

      if (d2 < bestD2) {
        bestD2 = d2;
        best = i;
      }
    }

    if (best < 0) return null;

    final EntityType t = es.type[best];
    es.hp[best] -= dmg;
    if (es.hp[best] <= 0f) {
      final float x = es.x[best];
      final float y = es.y[best];
      es.kill(best);
      return new Kill(t, x, y);
    }
    return null;
  }

  public Kill shootHitscan(
      final Entities es,
      final float ox,
      final float oy,
      final float aimX,
      final float aimY,
      final float fwdX,
      final float fwdY,
      final float fovDeg,
      final float range,
      final float dmg,
      final float spreadRad
  ) {
    float dx = aimX - ox;
    float dy = aimY - oy;
    final float len2 = dx * dx + dy * dy;
    if (len2 <= 1e-6f) return null;

    final float inv = (float) (1.0 / Math.sqrt(len2));
    float ax = dx * inv;
    float ay = dy * inv;

    final float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));
    final float dot = fwdX * ax + fwdY * ay;
    if (dot < cosHalf) {
      // outside sight-cone: no shot
      return null;
    }

    // Apply spread (rotate by random angle)
    if (spreadRad > 1e-6f) {
      final double ang = (nextFloatSigned() * spreadRad);
      final float c = (float) Math.cos(ang);
      final float s = (float) Math.sin(ang);
      final float rx = ax * c - ay * s;
      final float ry = ax * s + ay * c;
      ax = rx;
      ay = ry;
    }

    int best = -1;
    float bestT = Float.POSITIVE_INFINITY;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;

      // Ray vs circle (center at entity position)
      final float cx = es.x[i] - ox;
      final float cy = es.y[i] - oy;

      // projection length along ray
      final float proj = cx * ax + cy * ay;
      if (proj < 0f || proj > range) continue;

      // closest approach squared
      final float px2 = cx * cx + cy * cy - proj * proj;
      final float r = EntityMetrics.hitRadius(t);
      if (px2 > r * r) continue;

      if (proj < bestT) {
        bestT = proj;
        best = i;
      }
    }

    if (best < 0) return null;

    final EntityType t = es.type[best];
    es.hp[best] -= dmg;
    if (es.hp[best] <= 0f) {
      final float x = es.x[best];
      final float y = es.y[best];
      es.kill(best);
      return new Kill(t, x, y);
    }
    return null;
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

  private float nextFloatSigned() {
    // [-1, +1)
    return nextFloat01() * 2f - 1f;
  }
}

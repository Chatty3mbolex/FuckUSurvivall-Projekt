package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.sim.SimContext;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.World;

public final class AiSystem {
  // Deterministic RNG state (xorshift64*).
  private long rngState;

  // Scratch to avoid per-iteration allocations.
  // (No vectors/arrays needed currently; this is future-proof.)

  public AiSystem() {
    this(0xA11BEEFL);
  }

  public AiSystem(final long seed) {
    setSeed(seed);
  }

  public void setSeed(final long seed) {
    // xorshift must never be 0.
    rngState = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L;
  }

  /**
   * @param stealthMul 1.0 = default. Lower means enemies see you at shorter distance.
   * @param intimidateChance 0..1. Chance per second that an orc that sees the player will flee instead of chase.
   */
  public void tick(
      final SimContext ctx,
      final int player,
      final int minCx,
      final int maxCx,
      final int minCy,
      final int maxCy,
      final float dt,
      final float stealthMul,
      final float intimidateChance
  ) {
    tick(ctx.entities, player, ctx.world, minCx, maxCx, minCy, maxCy, dt, stealthMul, intimidateChance);
  }

  /** Legacy signature kept for compatibility. */
  public void tick(
      final Entities es,
      final int player,
      final World world,
      final int minCx,
      final int maxCx,
      final int minCy,
      final int maxCy,
      final float dt,
      final float stealthMul,
      final float intimidateChance
  ) {
    tick(es, player, world, minCx, maxCx, minCy, maxCy, dt, stealthMul, intimidateChance, false, false, false);
  }

  public void tick(
      final Entities es,
      final int player,
      final World world,
      final int minCx,
      final int maxCx,
      final int minCy,
      final int maxCy,
      final float dt,
      final float stealthMul,
      final float intimidateChance,
      final boolean playerSneaking,
      final boolean playerMoving,
      final boolean playerSprinting
  ) {
    final float px = es.x[player];
    final float py = es.y[player];

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;

      // Tick only if inside loaded area or always-active.
      if (!es.isAlwaysActive(i)) {
        final int ecx = (int) Math.floor((es.x[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
        final int ecy = (int) Math.floor((es.y[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
        if (ecx < minCx || ecx > maxCx || ecy < minCy || ecy > maxCy) continue;
      }

      final EntityType type = es.type[i];
      if (type != EntityType.ORK_GRUNT && type != EntityType.ANIMAL_DEER) continue;

      // Motion is updated by brains (tickOrc/tickDeer); do not zero here.

      if (type == EntityType.ORK_GRUNT) {
        tickOrc(es, world, i, player, px, py, dt, stealthMul, intimidateChance, playerSneaking, playerMoving, playerSprinting);
      } else {
        tickDeer(es, world, i, px, py, dt, playerSneaking);
      }
    }
  }

  // === Entity brains ===

  private void tickOrc(
      final Entities es,
      final World world,
      final int i,
      final int player,
      final float px,
      final float py,
      final float dt,
      final float stealthMul,
      final float intimidateChance,
      final boolean playerSneaking,
      final boolean playerMoving,
      final boolean playerSprinting
  ) {
    // Universal-ish tuning for ORK_GRUNT (kept centralized here for now)
    // Requested: attention radius -10%.
    final float viewRange = 135f * stealthMul * 0.90f;

    // Relative FOV / attention model:
    // - If the player is moving (walk/sprint), the orc is more likely to notice.
    // - Front attention is stronger (+50% range), sides/back a bit stronger (+20% range).
    // - If the player is sneaking and is behind the orc, the orc is blind until the player reaches the silhouette/front.
    // Requested: ORC FOV narrower.
    final float cosFront = (float) Math.cos(Math.toRadians(75f * 0.5)); // 75° front cone

    final float dx = px - es.x[i];
    final float dy = py - es.y[i];
    final float d2 = dx * dx + dy * dy;

    // --- Idle scan (look left/right) ---
    // Use `rot[i]` as a look-angle (radians). When the orc is idle, it slowly scans.
    // When the player is detected, we lock the look to the target.
    final float v2 = es.vx[i] * es.vx[i] + es.vy[i] * es.vy[i];
    if (v2 < 1e-4f) {
      es.aiT2[i] -= dt;
      if (es.aiT2[i] <= 0f || es.aiF0[i] == 0f) {
        // choose a new scan omega (+/-) much less often (zäher): every ~10.9..12.4s
        es.aiT2[i] = 10.9f + nextFloat01() * 1.5f;
        float omega = 0.55f + nextFloat01() * 0.55f; // rad/s
        if (nextFloat01() < 0.5f) omega = -omega;
        es.aiF0[i] = omega;
      }
      es.rot[i] += es.aiF0[i] * dt;

      // Keep visual facing in sync with look direction while scanning.
      es.dir[i] = dir4FromAngle(es.rot[i]);
    } else {
      // Moving: look where you're going.
      es.rot[i] = (float) Math.atan2(es.vy[i], es.vx[i]);
      es.dir[i] = dir4FromAngle(es.rot[i]);
    }

    boolean see = false;
    if (d2 < (viewRange * 1.50f) * (viewRange * 1.50f)) { // quick outer gate
      final float inv = invLen(dx, dy);
      final float tx = dx * inv;
      final float ty = dy * inv;

      // Forward vector from look-angle
      float fx = (float) Math.cos(es.rot[i]);
      float fy = (float) Math.sin(es.rot[i]);

      final float dot = fx * tx + fy * ty; // +1 front, -1 behind

      // Sneak behind => blind
      if (playerSneaking && dot < 0f) {
        see = false;
      } else {
        // Attention multipliers
        final boolean playerLoud = playerMoving && !playerSneaking; // walk/sprint
        final float frontMul = playerLoud ? 1.50f : 1.0f;
        final float sideBackMul = playerLoud ? 1.20f : 1.0f;

        // If you're not sneaking, the orc should notice you from any direction (realistic peripheral/back awareness).
        // Front is stronger (+50% range) and sides/back are slightly stronger (+20% range) when you're moving.
        final float mul = (dot >= cosFront) ? frontMul : sideBackMul;
        final float r = viewRange * mul;
        see = d2 < r * r;
      }

      if (see) {
        // Lock gaze to target when detected.
        es.rot[i] = (float) Math.atan2(dy, dx);
        es.dir[i] = dir4FromAngle(es.rot[i]);
      }
    }

    // Alertness ramp: reduce the "snap" when the orc first detects the player.
    // aiF1: 0..1 where 0=unaware, 1=fully engaged.
    final float alertUp = 3.0f;   // ~0.33s to reach 1
    final float alertDown = 0.9f; // ease down when losing sight
    if (see) {
      es.aiF1[i] = Math.min(1f, es.aiF1[i] + alertUp * dt);
    } else {
      es.aiF1[i] = Math.max(0f, es.aiF1[i] - alertDown * dt);
    }

    // Desired velocity
    float desiredVx = 0f;
    float desiredVy = 0f;
    if (see) {
      final float inv = invLen(dx, dy);
      final float vx = dx * inv;
      final float vy = dy * inv;

      // Intimidation: sometimes flee instead of chase.
      boolean flee = false;
      if (intimidateChance > 0f) {
        // Use existing aiT as a cheap timer to avoid rolling every frame.
        // When aiT <= 0, we roll and then reset aiT to ~0.6s.
        es.aiT[i] -= dt;
        if (es.aiT[i] <= 0f) {
          es.aiT[i] = 0.6f;
          final float p = Math.min(0.95f, intimidateChance * 0.6f);
          flee = nextFloat01() < p;
        }
      }

      // Requested tuning: ORK end speed 110.
      final float speed = 110f;

      // Keep distance to avoid pushing the player around via collision separation.
      // Requested: stop ~25px from the player and attack from there.
      final float attackRange = 25f;
      final float attackR2 = attackRange * attackRange;

      if (flee) {
        desiredVx = -vx * speed;
        desiredVy = -vy * speed;
      } else {
        // If already in attack range, stop moving (don't ram the player).
        if (d2 <= attackR2) {
          desiredVx = 0f;
          desiredVy = 0f;

          // Attack cooldown uses data0 (ms). Do NOT generate chunks; just apply damage.
          int cd = es.data0[i];
          cd -= (int) (dt * 1000f);
          if (cd < 0) cd = 0;
          if (cd == 0) {
            // Simple melee hit.
            final float dmg = 4f;
            if (player >= 0 && player < Entities.MAX && es.alive[player]) {
              es.hp[player] = Math.max(0f, es.hp[player] - dmg);
            }
            cd = 650; // ~0.65s between hits
          }
          es.data0[i] = cd;
        } else {
          desiredVx = vx * speed;
          desiredVy = vy * speed;
        }
      }
    }

    // Accel fast, decel gently (eases out instead of snapping back to idle).
    // Use alertness to smooth the initial "discovery" acceleration.
    final float curSpd = (float) Math.sqrt(es.vx[i] * es.vx[i] + es.vy[i] * es.vy[i]);
    final float desSpd = (float) Math.sqrt(desiredVx * desiredVx + desiredVy * desiredVy);
    final float accelUpBase = 11.34f; // -30% then -10% more (total -37% vs original 18.0)
    final float accelUp = accelUpBase * (0.35f + 0.65f * es.aiF1[i]);
    final float decelDown = 2.2f;
    final float a = Math.min(1f, ((desSpd >= curSpd) ? accelUp : decelDown) * dt);
    es.vx[i] = es.vx[i] + (desiredVx - es.vx[i]) * a;
    es.vy[i] = es.vy[i] + (desiredVy - es.vy[i]) * a;

    // Update facing (stable)
    es.faceLock[i] = Math.max(0f, es.faceLock[i] - dt);
    es.dir[i] = stableDir4(es.vx[i], es.vy[i], es.dir[i], es.faceLock, i);

    final float nx = es.x[i] + es.vx[i] * dt;
    final float ny = es.y[i] + es.vy[i] * dt;

    // IMPORTANT: AI must NOT generate chunks while probing movement.
    final int biomeId = world.biomeIdAtWorldPeek(nx, ny, Biome.GRASSLAND.id & 0xff);
    final Biome biome = Biome.byId(biomeId);
    final boolean water = world.isWaterAtWorldPeek(nx, ny, true);
    final boolean coll = world.isBlockedAtWorldPeek(nx, ny, true);

    // Orcs are allowed to traverse mountains without tool; still block water + lava.
    boolean blocked = water || biome == Biome.LAVA;
    if (coll && biome != Biome.MOUNTAIN && biome != Biome.SNOWHIGHLAND) {
      blocked = true;
    }

    if (!blocked) {
      es.x[i] = nx;
      es.y[i] = ny;
    } else {
      // Brake if blocked
      es.vx[i] = es.vx[i] * 0.40f;
      es.vy[i] = es.vy[i] * 0.40f;
    }
  }

  private void tickDeer(
      final Entities es,
      final World world,
      final int i,
      final float px,
      final float py,
      final float dt,
      final boolean playerSneaking
  ) {
    // Deer: slow wander; flee fast when player is very close.
    // IMPORTANT RULE: the deer should keep doing normal behavior until the player is inside the deer's FOV.
    final float ddx = es.x[i] - px;
    final float ddy = es.y[i] - py;
    final float dd2 = ddx * ddx + ddy * ddy;
    // Requested: start fleeing earlier (+30%).
    final float fleeR = 90f * 1.30f; // "knapp" um das Reh herum

    // Deer FOV gate: only react when the player is in front of the deer.
    // Use movement direction as primary forward (more robust than dir), fallback to dir if stationary.
    final float deerFovDeg = 120f;
    final float deerCosHalfFov = (float) Math.cos(Math.toRadians(deerFovDeg * 0.5));
    boolean seePlayerInFov = false;
    if (dd2 < fleeR * fleeR) {
      // Vector from deer -> player
      final float dx = px - es.x[i];
      final float dy = py - es.y[i];
      final float invT = invLen(dx, dy);
      if (invT > 0f) {
        final float tx = dx * invT;
        final float ty = dy * invT;

        // Forward vector: prefer current velocity direction
        float fx = 0f;
        float fy = 0f;
        final float v2 = es.vx[i] * es.vx[i] + es.vy[i] * es.vy[i];
        if (v2 > 1e-4f) {
          final float invF = invLen(es.vx[i], es.vy[i]);
          fx = es.vx[i] * invF;
          fy = es.vy[i] * invF;
        } else {
          // Fallback: from facing dir
          switch (es.dir[i]) {
            case 0 -> {
              fx = 0f;
              fy = 1f;
            } // N
            case 1 -> {
              fx = 1f;
              fy = 0f;
            } // E
            case 2 -> {
              fx = 0f;
              fy = -1f;
            } // S
            default -> {
              fx = -1f;
              fy = 0f;
            } // W
          }
        }

        final float dot = fx * tx + fy * ty;
        seePlayerInFov = dot >= deerCosHalfFov;
      }
    }

    // Sneaking should not trigger deer flee.
    // Default: only flee if player is inside the deer's FOV.
    // Bugfix/gameplay: add a small "panic radius" so the deer still flees when the player gets very close from behind.
    final float panicR = 42f; // world units (~2.6 tiles)
    final boolean panicClose = dd2 < panicR * panicR;

    // Flee persistence: once the deer panics, keep fleeing for a bit and run further away.
    // NOTE: we reuse `rot[i]` as a simple flee-hold timer for ANIMAL_DEER (rot is otherwise unused for animals).
    if (es.rot[i] > 0f) es.rot[i] = Math.max(0f, es.rot[i] - dt);
    final float fleeStopR = fleeR * 2.2f;

    final boolean fleeTriggerNow = (!playerSneaking && (seePlayerInFov || panicClose) && dd2 < fleeR * fleeR);
    if (fleeTriggerNow) {
      // 2.5 .. 4.5 seconds of "keep running" (deterministic RNG)
      es.rot[i] = 2.5f + nextFloat01() * 2.0f;
    }

    final boolean fleeing = (!playerSneaking && es.rot[i] > 0f && dd2 < fleeStopR * fleeStopR);

    if (fleeing) {
      // Run away from player.
      final float inv = invLen(ddx, ddy);
      final float vx = ddx * inv;
      final float vy = ddy * inv;
      // Requested tuning: DEER end speed 100.
      final float speed = 100f;

      final float desiredVx = vx * speed;
      final float desiredVy = vy * speed;

      // Accel up fast, but decel down gently (so it doesn't snap back to wander speed).
      final float curSpd = (float) Math.sqrt(es.vx[i] * es.vx[i] + es.vy[i] * es.vy[i]);
      final float desSpd = (float) Math.sqrt(desiredVx * desiredVx + desiredVy * desiredVy);
      final float accelUp = 32.0f;
      final float decelDown = 2.4f;
      final float a = Math.min(1f, ((desSpd >= curSpd) ? accelUp : decelDown) * dt);
      es.vx[i] = es.vx[i] + (desiredVx - es.vx[i]) * a;
      es.vy[i] = es.vy[i] + (desiredVy - es.vy[i]) * a;

      // Keep facing stable while fleeing.
      es.faceLock[i] = Math.max(0f, es.faceLock[i] - dt);
      es.dir[i] = stableDir4(es.vx[i], es.vy[i], es.dir[i], es.faceLock, i);

      final float nx = es.x[i] + es.vx[i] * dt;
      final float ny = es.y[i] + es.vy[i] * dt;

      // IMPORTANT: AI must NOT generate chunks while probing movement.
      final boolean water = world.isWaterAtWorldPeek(nx, ny, true);
      final boolean coll = world.isBlockedAtWorldPeek(nx, ny, true);
      final int bid = world.biomeIdAtWorldPeek(nx, ny, Biome.GRASSLAND.id & 0xff);

      if (!water && !coll && Biome.byId(bid) != Biome.LAVA) {
        es.x[i] = nx;
        es.y[i] = ny;
      } else {
        es.vx[i] = 0f;
        es.vy[i] = 0f;
      }
      return;
    }

    // --- Wander speed changes ---
    // Change normal wander speed in a random rhythm: 10 .. 45 seconds.
    // Speed is capped at 90% of deer top speed (top=100 => max wander=90).
    es.aiT2[i] -= dt;
    if (es.aiT2[i] <= 0f || es.aiF0[i] <= 0f) {
      es.aiT2[i] = 10f + nextFloat01() * 35f;
      final float maxWander = 100f * 0.90f;
      final float minWander = 14f;
      es.aiF0[i] = minWander + nextFloat01() * (maxWander - minWander);
    }

    // Simple wandering: keep a direction for a while, then change.
    es.aiT[i] -= dt;
    if (es.aiT[i] <= 0f) {
      // Pick new direction (4-way) and duration.
      // Less zig-zag: keep direction more often.
      final boolean keep = nextFloat01() < 0.65f;
      if (!keep) {
        final int pick = nextInt(4);
        es.dir[i] = (byte) switch (pick) {
          case 0 -> 0; // N
          case 1 -> 1; // E
          case 2 -> 2; // S
          default -> 3; // W
        };
      }
      es.aiT[i] = 2.2f + nextFloat01() * 3.4f;
    }

    final float speed = es.aiF0[i];

    // Apply same smoothing style as player (accel/brake)
    float desiredVx = 0f;
    float desiredVy = 0f;
    switch (es.dir[i]) {
      case 0 -> {
        desiredVx = 0f;
        desiredVy = speed;
      }
      case 1 -> {
        desiredVx = speed;
        desiredVy = 0f;
      }
      case 2 -> {
        desiredVx = 0f;
        desiredVy = -speed;
      }
      default -> {
        desiredVx = -speed;
        desiredVy = 0f;
      }
    }

    // Wander: accelerate a bit, but decelerate slowly so the deer eases down from sprint/flee.
    final float curSpd = (float) Math.sqrt(es.vx[i] * es.vx[i] + es.vy[i] * es.vy[i]);
    final float desSpd = (float) Math.sqrt(desiredVx * desiredVx + desiredVy * desiredVy);
    final float accelUp = 10.5f;
    final float decelDown = 1.6f;
    final float a = Math.min(1f, ((desSpd >= curSpd) ? accelUp : decelDown) * dt);
    es.vx[i] = es.vx[i] + (desiredVx - es.vx[i]) * a;
    es.vy[i] = es.vy[i] + (desiredVy - es.vy[i]) * a;

    final float nx = es.x[i] + es.vx[i] * dt;
    final float ny = es.y[i] + es.vy[i] * dt;

    // IMPORTANT: AI must NOT generate chunks while probing movement.
    final boolean water = world.isWaterAtWorldPeek(nx, ny, true);
    final boolean coll = world.isBlockedAtWorldPeek(nx, ny, true);
    final int bid = world.biomeIdAtWorldPeek(nx, ny, Biome.GRASSLAND.id & 0xff);

    if (!water && !coll && Biome.byId(bid) != Biome.LAVA) {
      es.x[i] = nx;
      es.y[i] = ny;
    } else {
      es.vx[i] = 0f;
      es.vy[i] = 0f;
    }
  }

  // === Math helpers ===

  // Quantize a continuous look angle (atan2(y,x)) to the game's 4-way dir.
  // Byte mapping: 0=N,1=E,2=S,3=W.
  private static byte dir4FromAngle(final float a) {
    // normalize to [-pi,pi] via atan2(sin,cos)
    final double aa = Math.atan2(Math.sin(a), Math.cos(a));
    final double step = Math.PI * 0.5; // 90deg
    int sector = (int) Math.floor((aa + step * 0.5) / step);
    sector = ((sector % 4) + 4) % 4;
    return (byte) switch (sector) {
      case 0 -> 1; // E
      case 1 -> 0; // N
      case 2 -> 3; // W
      default -> 2; // S
    };
  }

  private static float invLen(final float x, final float y) {
    final float d2 = x * x + y * y;
    if (d2 <= 1e-6f) return 0f;
    return (float) (1.0 / Math.sqrt(d2));
  }

  // Stable 4-way facing derived from continuous velocity.
  // Uses: speed epsilon (no update), lock (min hold time), hysteresis (needs clear dominance).
  // Byte mapping: 0=N,1=E,2=S,3=W.
  private static byte stableDir4(final float vx, final float vy, final byte last, final float[] lock, final int i) {
    // 0) ignore tiny velocities (prevents noise when near-stationary)
    final float v2 = vx * vx + vy * vy;
    if (v2 < 1e-4f) return last;

    // 1) If still locked, keep current.
    if (lock[i] > 0f) return last;

    // 2) Continuous angle (world: +x=E, +y=N)
    double a = Math.atan2(vy, vx); // -pi..pi

    // 3) Quantize to nearest of 4 sectors (centers: E=0, N=pi/2, W=pi, S=-pi/2)
    final double step = Math.PI * 0.5; // 90deg
    int sector = (int) Math.floor((a + step * 0.5) / step);
    sector = ((sector % 4) + 4) % 4; // 0..3

    final byte desired = switch (sector) {
      case 0 -> 1; // E
      case 1 -> 0; // N
      case 2 -> 3; // W
      default -> 2; // S
    };

    if (desired == last) return last;

    // 4) Hysteresis: only switch if angle is clearly away from current direction center.
    // Current dir center angles:
    final double cur = switch (last) {
      case 1 -> 0.0; // E
      case 0 -> Math.PI * 0.5; // N
      case 3 -> Math.PI; // W
      default -> -Math.PI * 0.5; // S
    };

    double d = a - cur;
    // normalize to [-pi, +pi]
    d = Math.atan2(Math.sin(d), Math.cos(d));

    // Need to exceed half-sector + extra (in radians)
    final double hysteresis = Math.toRadians(18.0); // tuneable (higher = less jitter)
    if (Math.abs(d) <= (step * 0.5 + hysteresis)) {
      return last;
    }

    // 5) Accept switch and set lock.
    lock[i] = 0.26f; // tuneable min hold seconds (higher = smoother facing)
    return desired;
  }

  // === RNG helpers ===

  private long nextLong() {
    // xorshift64*
    long x = rngState;
    x ^= x >>> 12;
    x ^= x << 25;
    x ^= x >>> 27;
    rngState = x;
    return x * 2685821657736338717L;
  }

  private float nextFloat01() {
    // Use top 24 bits -> [0,1)
    return ((nextLong() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
  }

  private int nextInt(final int boundExclusive) {
    if (boundExclusive <= 1) return 0;
    // Simple (slightly biased) is fine for gameplay randomness.
    final long r = nextLong();
    final int v = (int) (Math.floorMod(r, boundExclusive));
    return v;
  }
}

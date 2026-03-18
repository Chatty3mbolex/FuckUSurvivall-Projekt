package com.yourgame.survival.systems;

import com.yourgame.survival.data.DataRegistry;
import com.yourgame.survival.data.PlayerProgress;
import com.yourgame.survival.data.PriceBook;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Per-merchant shop offers + per-merchant refresh timers.
 *
 * Requirements:
 * - wandering merchants: rotate every 15..60 minutes (real time)
 * - fixed merchants: rotate every 6 hours (real time)
 * - wandering merchants walk around; they stop when player enters player action-range
 */
public final class MerchantSystem {

  // Deterministic RNG for wandering movement (xorshift64*).
  private long moveRngState;

  public static final class ShopState {
    public int offerCount;
    public final int[] itemId = new int[12];
    public final int[] buy = new int[12];
    public final int[] sell = new int[12];

    public float refreshT;
    public int refreshIndex;

    public boolean wandering;
    public int uid;
  }

  private final Map<Integer, ShopState> byEntity = new HashMap<>();
  private int nextUid = 1;
  private final long worldSeed;

  public long exportMoveRngState() { return moveRngState; }
  public int exportNextUid() { return nextUid; }

  /**
   * Import merchant state from save JSON.
   * Expects merchant entities to already exist in {@code es}.
   * Maps by UID (stored in entity.data0 when saved).
   */
  public void importFromSave(com.badlogic.gdx.utils.JsonValue merchantSys, Entities es) {
    if (merchantSys == null || es == null) return;
    byEntity.clear();
    try {
      moveRngState = merchantSys.getLong("moveRng", moveRngState);
      nextUid = merchantSys.getInt("nextUid", nextUid);
      com.badlogic.gdx.utils.JsonValue shops = merchantSys.get("shops");
      if (shops == null) return;

      // For each shop entry, find matching merchant entity by uid in es.data0.
      for (com.badlogic.gdx.utils.JsonValue s = shops.child; s != null; s = s.next) {
        int uid = s.getInt("uid", -1);
        if (uid <= 0) continue;
        int merchantE = -1;
        for (int i = 0; i < Entities.MAX; i++) {
          if (!es.alive[i]) continue;
          EntityType t = es.type[i];
          if (t != EntityType.MERCHANT_ELF && t != EntityType.MERCHANT_WANDERING) continue;
          if (es.data0[i] == uid) { merchantE = i; break; }
        }
        if (merchantE < 0) continue;

        ShopState st = new ShopState();
        st.uid = uid;
        st.wandering = s.getInt("wandering", 0) != 0;
        st.offerCount = s.getInt("offerCount", 0);
        st.refreshT = s.getFloat("refreshT", -1f);
        st.refreshIndex = s.getInt("refreshIndex", 0);

        com.badlogic.gdx.utils.JsonValue itemId = s.get("itemId");
        com.badlogic.gdx.utils.JsonValue buy = s.get("buy");
        com.badlogic.gdx.utils.JsonValue sell = s.get("sell");
        if (itemId != null) {
          int n = Math.min(itemId.size, st.itemId.length);
          for (int i = 0; i < n; i++) st.itemId[i] = itemId.getInt(i);
        }
        if (buy != null) {
          int n = Math.min(buy.size, st.buy.length);
          for (int i = 0; i < n; i++) st.buy[i] = buy.getInt(i);
        }
        if (sell != null) {
          int n = Math.min(sell.size, st.sell.length);
          for (int i = 0; i < n; i++) st.sell[i] = sell.getInt(i);
        }

        byEntity.put(merchantE, st);
      }
    } catch (Throwable ignored) {
      // keep empty (system will regen)
    }
  }

  public MerchantSystem(long worldSeed) {
    this.worldSeed = worldSeed;
    // xorshift must never be 0.
    long s = worldSeed ^ 0x4D45524348414E54L; // "MERCHANT"
    this.moveRngState = (s != 0L) ? s : 0x9E3779B97F4A7C15L;
  }

  public ShopState stateFor(int merchantE, boolean wandering) {
    ShopState s = byEntity.get(merchantE);
    if (s == null) {
      s = new ShopState();
      s.wandering = wandering;
      s.uid = nextUid++;
      s.refreshT = -1f; // force regen
      s.refreshIndex = 0;
      byEntity.put(merchantE, s);
    } else {
      s.wandering = wandering;
    }
    return s;
  }

  public ShopState get(int merchantE) {
    return byEntity.get(merchantE);
  }

  public void cleanupDead(Entities es) {
    byEntity.entrySet().removeIf(e -> {
      int id = e.getKey();
      return id < 0 || id >= Entities.MAX || !es.alive[id] || (es.type[id] != EntityType.MERCHANT_ELF && es.type[id] != EntityType.MERCHANT_WANDERING);
    });
  }

  /** Spawn merchants scattered around the player inside the loaded chunk area. */
  public void ensureMerchantsAround(Entities es, World world, float px, float py, int streamRadiusChunks,
                                   int targetFixed, int targetWandering,
                                   DataRegistry data, PriceBook prices, PlayerProgress progress) {
    // Count merchants in loaded area (separately)
    int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);
    int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);

    int minCx = ccx - streamRadiusChunks;
    int maxCx = ccx + streamRadiusChunks;
    int minCy = ccy - streamRadiusChunks;
    int maxCy = ccy + streamRadiusChunks;

    int fixedCount = 0;
    int wanderingCount = 0;
    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      EntityType t = es.type[i];
      if (t != EntityType.MERCHANT_ELF && t != EntityType.MERCHANT_WANDERING) continue;

      int ecx = (int) Math.floor((es.x[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
      int ecy = (int) Math.floor((es.y[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
      if (ecx < minCx || ecx > maxCx || ecy < minCy || ecy > maxCy) continue;
      if (t == EntityType.MERCHANT_WANDERING) wanderingCount++; else fixedCount++;
    }

    int needFixed = Math.max(0, targetFixed - fixedCount);
    int needWandering = Math.max(0, targetWandering - wanderingCount);
    if (needFixed == 0 && needWandering == 0) return;

    // Spawn fixed first, then wandering.
    for (int n = 0; n < needFixed; n++) {
      boolean wandering = false;
      EntityType type = EntityType.MERCHANT_ELF;

      int e = trySpawnMerchant(es, world, px, py, minCx, maxCx, minCy, maxCy, type);
      if (e >= 0) {
        es.setAlwaysActive(e, true);
        ShopState s = stateFor(e, wandering);
        // force initial offers
        regenOffers(s, data, prices, progress);
      }
    }

    for (int n = 0; n < needWandering; n++) {
      boolean wandering = true;
      EntityType type = EntityType.MERCHANT_WANDERING;

      int e = trySpawnMerchant(es, world, px, py, minCx, maxCx, minCy, maxCy, type);
      if (e >= 0) {
        es.setAlwaysActive(e, true);
        ShopState s = stateFor(e, wandering);
        regenOffers(s, data, prices, progress);
      }
    }
  }

  private static int trySpawnMerchant(Entities es, World world, float px, float py,
                                     int minCx, int maxCx, int minCy, int maxCy,
                                     EntityType type) {
    float minPlayerDist = 12f * World.TILE_WORLD;
    float min2 = minPlayerDist * minPlayerDist;

    for (int attempt = 0; attempt < 120; attempt++) {
      int cx = randInt(minCx, maxCx);
      int cy = randInt(minCy, maxCy);
      int tx = cx * World.CHUNK_SIZE + randInt(0, World.CHUNK_SIZE - 1);
      int ty = cy * World.CHUNK_SIZE + randInt(0, World.CHUNK_SIZE - 1);

      float wx = (tx + 0.5f) * World.TILE_WORLD;
      float wy = (ty + 0.5f) * World.TILE_WORLD;

      // IMPORTANT: must NOT generate chunks while probing merchant spawns.
      if (world.isWaterAtWorldPeek(wx, wy, true)) continue;
      if (world.isBlockedAtWorldPeek(wx, wy, true)) continue;
      int bid = world.biomeIdAtWorldPeek(wx, wy, com.yourgame.survival.world.Biome.GRASSLAND.id & 0xff);
      if (com.yourgame.survival.world.Biome.byId(bid) == com.yourgame.survival.world.Biome.LAVA) continue;

      float dx = wx - px;
      float dy = wy - py;
      if (dx * dx + dy * dy < min2) continue;

      // avoid stacking merchants
      float minOther = 7f * World.TILE_WORLD;
      float minO2 = minOther * minOther;
      for (int k = 0; k < Entities.MAX; k++) {
        if (!es.alive[k]) continue;
        EntityType ot = es.type[k];
        if (ot != EntityType.MERCHANT_ELF && ot != EntityType.MERCHANT_WANDERING) continue;
        float ox = es.x[k] - wx;
        float oy = es.y[k] - wy;
        if (ox * ox + oy * oy < minO2) { wx = Float.NaN; break; }
      }
      if (Float.isNaN(wx)) continue;

      return es.spawn(type, wx, wy);
    }

    return -1;
  }

  /** Tick refresh timers + wandering movement. */
  public void tick(Entities es, World world, float playerX, float playerY, float playerActionRange, float dt,
                   DataRegistry data, PriceBook prices, PlayerProgress progress,
                   int loadedMinCx, int loadedMaxCx, int loadedMinCy, int loadedMaxCy) {

    // 1) movement (wandering merchants)
    float stopR2 = playerActionRange * playerActionRange;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      if (es.type[i] != EntityType.MERCHANT_WANDERING) continue;

      // only tick movement in loaded area (plus a small margin)
      if (!es.isAlwaysActive(i)) {
        int ecx = (int) Math.floor((es.x[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
        int ecy = (int) Math.floor((es.y[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
        if (ecx < loadedMinCx || ecx > loadedMaxCx || ecy < loadedMinCy || ecy > loadedMaxCy) continue;
      }

      float dx = playerX - es.x[i];
      float dy = playerY - es.y[i];
      float d2 = dx * dx + dy * dy;

      // stop if player comes close (action range)
      if (d2 <= stopR2) {
        es.vx[i] = 0f;
        es.vy[i] = 0f;
        // face player
        es.dir[i] = dirFrom(dx, dy, es.dir[i]);
        continue;
      }

      // wander: pick a continuous heading (not grid-locked) and keep it for some seconds
      es.aiT[i] -= dt;
      if (es.aiT[i] <= 0f) {
        es.aiT[i] = 2.5f + nextFloat01() * 4.0f;
        // Store heading angle in rot[] (radians, world: +x=E, +y=N)
        es.rot[i] = nextFloat01() * (float) (Math.PI * 2.0);
      }

      float speed = 28f;
      float desiredVx = (float) Math.cos(es.rot[i]) * speed;
      float desiredVy = (float) Math.sin(es.rot[i]) * speed;

      // Face based on movement direction (still 4-way facing for current atlas)
      es.dir[i] = dirFrom(desiredVx, desiredVy, es.dir[i]);

      float accel = 10f;
      float a = Math.min(1f, accel * dt);
      es.vx[i] = es.vx[i] + (desiredVx - es.vx[i]) * a;
      es.vy[i] = es.vy[i] + (desiredVy - es.vy[i]) * a;

      float nx = es.x[i] + es.vx[i] * dt;
      float ny = es.y[i] + es.vy[i] * dt;

      // IMPORTANT: must NOT generate chunks while probing movement.
      boolean water = world.isWaterAtWorldPeek(nx, ny, true);
      boolean coll = world.isBlockedAtWorldPeek(nx, ny, true);
      int bid = world.biomeIdAtWorldPeek(nx, ny, com.yourgame.survival.world.Biome.GRASSLAND.id & 0xff);
      var biome = com.yourgame.survival.world.Biome.byId(bid);
      boolean blocked = water || coll || biome == com.yourgame.survival.world.Biome.LAVA;

      if (!blocked) {
        es.x[i] = nx;
        es.y[i] = ny;
      } else {
        // bounce: shorten timer so we repick soon
        es.vx[i] *= 0.35f;
        es.vy[i] *= 0.35f;
        es.aiT[i] = Math.min(es.aiT[i], 0.2f);
      }
    }

    // 2) refresh timers (per merchant)
    cleanupDead(es);

    for (var entry : byEntity.entrySet()) {
      int e = entry.getKey();
      ShopState s = entry.getValue();
      if (e < 0 || e >= Entities.MAX) continue;
      if (!es.alive[e]) continue;

      // tick timer
      s.refreshT -= dt;
      if (s.refreshT <= 0f) {
        s.refreshIndex++;
        regenOffers(s, data, prices, progress);
      }
    }
  }

  /** Force immediate refresh (e.g. on shop-open). */
  public void ensureFresh(ShopState s, DataRegistry data, PriceBook prices, PlayerProgress progress) {
    if (s == null) return;
    if (s.refreshT > 0f && s.offerCount > 0) return;
    s.refreshIndex++;
    regenOffers(s, data, prices, progress);
  }

  private void regenOffers(ShopState s, DataRegistry data, PriceBook prices, PlayerProgress progress) {
    // next refresh (REAL-TIME seconds, not in-game)
    if (s.wandering) {
      // 15..60 minutes
      s.refreshT = randRange(15f * 60f, 60f * 60f);
    } else {
      // 6 hours
      s.refreshT = 6f * 60f * 60f;
    }

    // deterministic-ish rng per merchant + refreshIndex
    long seed = worldSeed;
    seed ^= (long) s.uid * 0x9E3779B97F4A7C15L;
    seed ^= (long) s.refreshIndex * 0xBF58476D1CE4E5B9L;
    Random rng = new Random(seed);

    s.offerCount = 0;

    // fixed offers first
    if (data.fixedShop != null) {
      for (int i = 0; i < data.fixedShop.offerCount && s.offerCount < s.itemId.length; i++) {
        int id = data.fixedShop.itemId[i];
        addOffer(s, id, prices);
      }
    }

    // wandering picks appended (for BOTH merchant types)
    if (data.wanderingPoolItems != null && data.wanderingPoolItems.length > 0) {
      int tradingLv = (progress != null && progress.skillLv != null && progress.skillLv.length > 0)
          ? progress.skillLv[com.yourgame.survival.data.SkillDefs.indexOf("Trading")] : 1;
      int want = 3 + Math.max(0, (tradingLv - 1) / 10);

      for (int k = 0; k < want && s.offerCount < s.itemId.length; k++) {
        int pick = -1;
        for (int tries = 0; tries < 30; tries++) {
          int id = data.wanderingPoolItems[rng.nextInt(data.wanderingPoolItems.length)];
          if (!hasOffer(s, id)) { pick = id; break; }
        }
        if (pick < 0) break;
        addOffer(s, pick, prices);
      }
    }
  }

  private static void addOffer(ShopState s, int itemId, PriceBook prices) {
    int base = Math.max(1, prices.getBaseCopper(itemId));
    s.itemId[s.offerCount] = itemId;
    s.sell[s.offerCount] = base;
    s.buy[s.offerCount] = Math.max(1, base * 3);
    s.offerCount++;
  }

  private static boolean hasOffer(ShopState s, int itemId) {
    for (int i = 0; i < s.offerCount; i++) if (s.itemId[i] == itemId) return true;
    return false;
  }

  private static float randRange(float a, float b) {
    if (b < a) b = a;
    return a + (float) (Math.random() * (b - a));
  }

  private static int randInt(int a, int b) {
    if (b < a) b = a;
    return a + (int) Math.floor(Math.random() * (b - a + 1));
  }

  private long nextLongMove() {
    long x = moveRngState;
    x ^= x >>> 12;
    x ^= x << 25;
    x ^= x >>> 27;
    moveRngState = x;
    return x * 2685821657736338717L;
  }

  private float nextFloat01() {
    return ((nextLongMove() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
  }

  private static byte dirFrom(float dx, float dy, byte last) {
    float ax = Math.abs(dx);
    float ay = Math.abs(dy);
    if (ax < 1e-3f && ay < 1e-3f) return last;
    if (ax > ay) return (byte) (dx > 0 ? 1 : 3);
    return (byte) (dy > 0 ? 0 : 2);
  }
}

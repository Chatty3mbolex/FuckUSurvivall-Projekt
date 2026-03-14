package com.yourgame.survival.systems;

import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.sim.SimContext;
import com.yourgame.survival.world.World;

/**
 * Respawn + cap spawner for encounters (orcs/animals) inside the currently loaded chunk area.
 * Designed to replace the old "respawn immediately if missing" logic.
 */
public final class EncounterSpawner {
  // Defaults (used when no biome-specific encounterStampRules are configured)
  private static final int MAX_ORCS_LOADED = 25;
  // Real-time respawn window: 0.5h .. 1.5h
  private static final float RESPAWN_ORC_MIN = 0.5f * 3600f;
  private static final float RESPAWN_ORC_MAX = 1.5f * 3600f;

  private static final int MAX_ANIMALS_LOADED = 15;
  private static final float RESPAWN_ANIMAL_MIN = 0.5f * 3600f;
  private static final float RESPAWN_ANIMAL_MAX = 1.5f * 3600f;

  // Respawn timers (seconds)
  private float orcT = 0f;
  private float animalT = 0f;

  // Deterministic RNG state (xorshift64*).
  private long rngState;

  // Debug/telemetry (read-only from HUD)
  public int lastOrcsLoaded = 0;
  public int lastAnimalsLoaded = 0;
  public float lastOrcT = 0f;
  public float lastAnimalT = 0f;
  public int lastSpawnAttemptsOrc = 0;
  public int lastSpawnAttemptsAnimal = 0;
  public boolean lastSpawnOkOrc = false;
  public boolean lastSpawnOkAnimal = false;

  public EncounterSpawner() {
    this(0x51A95EEDL);
  }

  public EncounterSpawner(final long seed) {
    setSeed(seed);
  }

  public void setSeed(final long seed) {
    rngState = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L;
  }

  public void resetTimers() {
    orcT = 0f;
    animalT = 0f;
  }

  public void onKilled(final EntityType t) {
    if (t == EntityType.ORK_GRUNT) {
      orcT = randRange(RESPAWN_ORC_MIN, RESPAWN_ORC_MAX);
    } else if (t == EntityType.ANIMAL_DEER) {
      animalT = randRange(RESPAWN_ANIMAL_MIN, RESPAWN_ANIMAL_MAX);
    }
  }

  public void tick(final SimContext ctx, final float px, final float py, final int streamRadiusChunks, final float dt) {
    tick(ctx.entities, ctx.world, px, py, streamRadiusChunks, dt);
  }

  /** Legacy signature kept for compatibility. */
  public void tick(final Entities es, final World world, final float px, final float py, final int streamRadiusChunks, final float dt) {
    if (orcT > 0f) orcT -= dt;
    if (animalT > 0f) animalT -= dt;

    lastOrcT = orcT;
    lastAnimalT = animalT;
    lastSpawnAttemptsOrc = 0;
    lastSpawnAttemptsAnimal = 0;
    lastSpawnOkOrc = false;
    lastSpawnOkAnimal = false;

    // Count alive entities in loaded area
    final int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);
    final int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);

    final int minCx = ccx - streamRadiusChunks;
    final int maxCx = ccx + streamRadiusChunks;
    final int minCy = ccy - streamRadiusChunks;
    final int maxCy = ccy + streamRadiusChunks;

    // Despawn margin to avoid thrashing on edge.
    final int margin = 2;
    final int despawnMinCx = minCx - margin;
    final int despawnMaxCx = maxCx + margin;
    final int despawnMinCy = minCy - margin;
    final int despawnMaxCy = maxCy + margin;

    int orcs = 0;
    int animals = 0;

    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      final EntityType t = es.type[i];
      if (t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;

      final int ecx = (int) Math.floor((es.x[i] / World.TILE_WORLD) / World.CHUNK_SIZE);
      final int ecy = (int) Math.floor((es.y[i] / World.TILE_WORLD) / World.CHUNK_SIZE);

      // Hard despawn far outside the loaded area (CPU guardrail).
      if (ecx < despawnMinCx || ecx > despawnMaxCx || ecy < despawnMinCy || ecy > despawnMaxCy) {
        es.kill(i);
        continue;
      }

      if (ecx < minCx || ecx > maxCx || ecy < minCy || ecy > maxCy) continue;

      if (t == EntityType.ORK_GRUNT) orcs++;
      else animals++;
    }

    lastOrcsLoaded = orcs;
    lastAnimalsLoaded = animals;

    // Player biome drives encounter caps + respawn timing when configured.
    final int pBiomeId = world.biomeIdAtWorldPeek(px, py, com.yourgame.survival.world.Biome.GRASSLAND.id & 0xFF);
    final com.yourgame.survival.world.Biome pBiome = com.yourgame.survival.world.Biome.byId(pBiomeId);
    final com.yourgame.survival.biome.BiomeSystem.BiomeDef bd = world.biomes().def(pBiome);

    com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef orcRule = findEncounterRule(bd, EntityType.ORK_GRUNT);
    com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef deerRule = findEncounterRule(bd, EntityType.ANIMAL_DEER);

    int orcCap = (orcRule != null && orcRule.maxLoaded > 0) ? orcRule.maxLoaded : MAX_ORCS_LOADED;
    int deerCap = (deerRule != null && deerRule.maxLoaded > 0) ? deerRule.maxLoaded : MAX_ANIMALS_LOADED;

    float orcMin = (orcRule != null) ? orcRule.respawnMin : RESPAWN_ORC_MIN;
    float orcMax = (orcRule != null) ? orcRule.respawnMax : RESPAWN_ORC_MAX;
    float deerMin = (deerRule != null) ? deerRule.respawnMin : RESPAWN_ANIMAL_MIN;
    float deerMax = (deerRule != null) ? deerRule.respawnMax : RESPAWN_ANIMAL_MAX;

    // Spawn logic: at most one spawn attempt per tick and category.
    if (orcs < orcCap && orcT <= 0f) {
      final int signedAttempts = trySpawn(world, es, EntityType.ORK_GRUNT, px, py, minCx, maxCx, minCy, maxCy, orcRule);
      lastSpawnAttemptsOrc = Math.abs(signedAttempts);
      lastSpawnOkOrc = signedAttempts > 0;
      orcT = randRange(orcMin, orcMax);
      if (signedAttempts <= 0) orcT = 2f; // retry soon if no valid spot found
    }

    if (animals < deerCap && animalT <= 0f) {
      final int signedAttempts = trySpawn(world, es, EntityType.ANIMAL_DEER, px, py, minCx, maxCx, minCy, maxCy, deerRule);
      lastSpawnAttemptsAnimal = Math.abs(signedAttempts);
      lastSpawnOkAnimal = signedAttempts > 0;
      animalT = randRange(deerMin, deerMax);
      if (signedAttempts <= 0) animalT = 2.5f;
    }
  }

  private static com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef findEncounterRule(
      com.yourgame.survival.biome.BiomeSystem.BiomeDef bd,
      EntityType t
  ) {
    if (bd == null || bd.encounterStampRules == null) return null;
    String want = (t == EntityType.ORK_GRUNT) ? "ORK_GRUNT" : (t == EntityType.ANIMAL_DEER ? "ANIMAL_DEER" : null);
    if (want == null) return null;
    for (int i = 0; i < bd.encounterStampRules.size(); i++) {
      var r = bd.encounterStampRules.get(i);
      if (r == null || r.type == null) continue;
      if (r.type.equalsIgnoreCase(want)) return r;
    }
    return null;
  }

  // === Spawn logic ===

  /**
   * @return +attempts on success, -attempts on failure. (0 = no attempts; should not happen currently)
   */
  private int trySpawn(
      final World world,
      final Entities es,
      final EntityType t,
      final float px,
      final float py,
      final int minCx,
      final int maxCx,
      final int minCy,
      final int maxCy,
      final com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef rule
  ) {
    // Keep spawns away from the player.
    final float minPlayerDist = 10f * World.TILE_WORLD;
    final float min2 = minPlayerDist * minPlayerDist;

    // Avoid spawning on top of another encounter entity.
    final float minEntityDist = 4f * World.TILE_WORLD;
    final float minE2 = minEntityDist * minEntityDist;

    int attempts = 0;

    for (int attempt = 0; attempt < 48; attempt++) {
      attempts = attempt + 1;

      final int cx = randInt(minCx, maxCx);
      final int cy = randInt(minCy, maxCy);

      final int tx = cx * World.CHUNK_SIZE + randInt(0, World.CHUNK_SIZE - 1);
      final int ty = cy * World.CHUNK_SIZE + randInt(0, World.CHUNK_SIZE - 1);

      final float wx = (tx + 0.5f) * World.TILE_WORLD;
      final float wy = (ty + 0.5f) * World.TILE_WORLD;

      // IMPORTANT: must NOT generate chunks while probing spawns.
      if (world.isWaterAtWorldPeek(wx, wy, true)) continue;
      if (world.isBlockedAtWorldPeek(wx, wy, true)) continue;

      // Optional mask constraint (editor entity stamp).
      if (rule != null && rule.mask != null && !rule.mask.isBlank()) {
        int biomeId = world.biomeIdAtWorldPeek(wx, wy, com.yourgame.survival.world.Biome.GRASSLAND.id & 0xFF);
        com.yourgame.survival.world.Biome b = com.yourgame.survival.world.Biome.byId(biomeId);
        int lx = Math.floorMod(tx, World.CHUNK_SIZE);
        int ly = Math.floorMod(ty, World.CHUNK_SIZE);
        if (!world.biomes().zoneMaskAt(b, rule.mask, lx, ly)) continue;
      }

      final float dx = wx - px;
      final float dy = wy - py;
      if (dx * dx + dy * dy < min2) continue;

      boolean nearOther = false;
      for (int k = 0; k < Entities.MAX; k++) {
        if (!es.alive[k]) continue;
        final EntityType ot = es.type[k];
        if (ot != EntityType.ORK_GRUNT && ot != EntityType.ANIMAL_DEER) continue;
        final float ox = es.x[k] - wx;
        final float oy = es.y[k] - wy;
        if (ox * ox + oy * oy < minE2) {
          nearOther = true;
          break;
        }
      }
      if (nearOther) continue;

      final int slot = es.spawn(t, wx, wy);
      if (slot >= 0) {
        // Anchor the entity to its spawn point so AI keeps it in zone.
        es.homeX[slot] = wx;
        es.homeY[slot] = wy;
        // Default wander radius: ~8 tiles. If a rule defines a zone mask,
        // entities are already mask-constrained at spawn, so the radius
        // keeps them from drifting out after spawn.
        es.wanderRadius[slot] = 8f * World.TILE_WORLD;
        return attempts;
      }
      return -attempts;
    }

    return -attempts;
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

  private float randRange(final float a, float b) {
    if (b < a) b = a;
    return a + nextFloat01() * (b - a);
  }

  private int randInt(final int a, int b) {
    if (b < a) b = a;
    final int span = b - a + 1;
    if (span <= 1) return a;
    // Simple (slightly biased) is fine for gameplay randomness.
    final int off = (int) Math.floorMod(nextLong(), span);
    return a + off;
  }
}

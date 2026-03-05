package com.yourgame.survival.world;

import com.yourgame.survival.biome.BiomeSystem;
import com.yourgame.survival.worldgen.pipeline.ChunkGenOrchestrator;

/** World wrapper: chunk streaming + queries. */
public final class World {
  public static final int CHUNK_SIZE = 64; // tiles
  public static final float TILE_WORLD = 16f; // world units per tile (arbitrary)

  private final BiomeSystem biomes;
  private final ChunkStore store;

  public World(long seed) {
    this(seed, new BiomeSystem(seed));
  }

  /** Allows editor/preview to inject a BiomeSystem instance (in-memory edits). */
  public World(long seed, BiomeSystem biomes) {
    this.biomes = (biomes != null) ? biomes : new BiomeSystem(seed);
    this.store = new ChunkStore(ChunkGenOrchestrator.create(seed, this.biomes));
  }

  /**
   * Advanced/Editor constructor: inject a custom generator.
   * This is used by the replacement WorldEditor to force biomes per chunk.
   */
  public World(long seed, BiomeSystem biomes, com.yourgame.survival.worldgen.WorldGenerator generator) {
    this.biomes = (biomes != null) ? biomes : new BiomeSystem(seed);
    this.store = new ChunkStore(generator != null ? generator : ChunkGenOrchestrator.create(seed, this.biomes));
  }

  /**
   * Editor/preview: force a single biome for all generated tiles.
   * This does NOT affect the main game unless explicitly used.
   */
  public World(long seed, BiomeSystem biomes, Biome forcedBiome) {
    this.biomes = (biomes != null) ? biomes : new BiomeSystem(seed);
    this.store = new ChunkStore(ChunkGenOrchestrator.create(seed, this.biomes, forcedBiome));
  }

  public BiomeSystem biomes() {
    return biomes;
  }

  /** Ensure chunk exists: generates if missing. */
  public Chunk chunk(int cx, int cy) {
    return store.get(cx, cy);
  }

  /** Peek chunk: returns existing chunk or null, does NOT generate. */
  public Chunk peekChunk(int cx, int cy) {
    return store.peek(cx, cy);
  }

  /** Request chunk generation (non-blocking). */
  public void requestChunk(int cx, int cy) {
    store.request(cx, cy);
  }

  /** Request chunk generation in a square radius around a world position (non-blocking). */
  public void requestAroundWorld(float wx, float wy, int radiusChunks) {
    int ccx = (int) Math.floor((wx / TILE_WORLD) / CHUNK_SIZE);
    int ccy = (int) Math.floor((wy / TILE_WORLD) / CHUNK_SIZE);
    int r = Math.max(0, radiusChunks);
    for (int dy = -r; dy <= r; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        store.request(ccx + dx, ccy + dy);
      }
    }
  }

  /** Generate up to maxNew chunks from the request queue (call once per frame). */
  public int tickStreaming(int maxNewChunks) {
    return store.tickGenerationBudget(maxNewChunks);
  }

  public TileSample sample(float wx, float wy) {
    // Compatibility API: allocates TileSample (do NOT use in hot render loops).
    final int biome = biomeIdAtWorld(wx, wy);
    final boolean blocked = isBlockedAtWorld(wx, wy);
    final boolean water = isWaterAtWorld(wx, wy);
    return new TileSample(biome, blocked, water);
  }

  /** Non-generating sample: returns defaults if the chunk is not loaded. */
  public TileSample samplePeek(float wx, float wy, int defaultBiomeId, boolean defaultBlocked, boolean defaultWater) {
    final int biome = biomeIdAtWorldPeek(wx, wy, defaultBiomeId);
    final boolean blocked = isBlockedAtWorldPeek(wx, wy, defaultBlocked);
    final boolean water = isWaterAtWorldPeek(wx, wy, defaultWater);
    return new TileSample(biome, blocked, water);
  }

  /** Compatibility record — keep, but avoid using it in hot paths. */
  public record TileSample(int biomeId, boolean blocked, boolean water) {}

  // --- Allocation-free queries (preferred for hot paths) ---

  public int biomeIdAtTile(final int tx, final int ty) {
    final int cx = floorDivInt(tx, CHUNK_SIZE);
    final int cy = floorDivInt(ty, CHUNK_SIZE);
    final int lx = modPositive(tx, CHUNK_SIZE);
    final int ly = modPositive(ty, CHUNK_SIZE);

    final Chunk c = store.get(cx, cy);
    final int idx = lx + ly * CHUNK_SIZE;
    return c.layers.biomeId[idx] & 0xff;
  }

  public boolean isWaterAtTile(final int tx, final int ty) {
    final int cx = floorDivInt(tx, CHUNK_SIZE);
    final int cy = floorDivInt(ty, CHUNK_SIZE);
    final int lx = modPositive(tx, CHUNK_SIZE);
    final int ly = modPositive(ty, CHUNK_SIZE);

    final Chunk c = store.get(cx, cy);
    final int idx = lx + ly * CHUNK_SIZE;
    return (c.layers.waterMask[idx] & 0xff) != 0;
  }

  public boolean isBlockedAtTile(final int tx, final int ty) {
    final int cx = floorDivInt(tx, CHUNK_SIZE);
    final int cy = floorDivInt(ty, CHUNK_SIZE);
    final int lx = modPositive(tx, CHUNK_SIZE);
    final int ly = modPositive(ty, CHUNK_SIZE);

    final Chunk c = store.get(cx, cy);
    final int idx = lx + ly * CHUNK_SIZE;
    return (c.layers.collisionMask[idx] & 0xff) != 0;
  }

  public int biomeIdAtWorld(final float wx, final float wy) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return biomeIdAtTile(tx, ty);
  }

  public boolean isWaterAtWorld(final float wx, final float wy) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return isWaterAtTile(tx, ty);
  }

  public boolean isBlockedAtWorld(final float wx, final float wy) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return isBlockedAtTile(tx, ty);
  }

  // --- Non-generating queries (safe for AI/movement near the loaded border) ---

  public int biomeIdAtTilePeek(final int tx, final int ty, final int defaultBiomeId) {
    final Chunk c = peekChunkForTile(tx, ty);
    if (c == null) return defaultBiomeId;
    final int idx = idxForTile(tx, ty);
    return c.layers.biomeId[idx] & 0xff;
  }

  public boolean isWaterAtTilePeek(final int tx, final int ty, final boolean defaultValue) {
    final Chunk c = peekChunkForTile(tx, ty);
    if (c == null) return defaultValue;
    final int idx = idxForTile(tx, ty);
    return (c.layers.waterMask[idx] & 0xff) != 0;
  }

  public boolean isBlockedAtTilePeek(final int tx, final int ty, final boolean defaultValue) {
    final Chunk c = peekChunkForTile(tx, ty);
    if (c == null) return defaultValue;
    final int idx = idxForTile(tx, ty);
    return (c.layers.collisionMask[idx] & 0xff) != 0;
  }

  /** Terraced height level at tile (0..255). Returns defaultValue if chunk not loaded. */
  public int heightLevelAtTilePeek(final int tx, final int ty, final int defaultValue) {
    final Chunk c = peekChunkForTile(tx, ty);
    if (c == null || c.layers == null || c.layers.heightLevel == null) return defaultValue;
    final int idx = idxForTile(tx, ty);
    return c.layers.heightLevel[idx] & 0xFF;
  }

  public int biomeIdAtWorldPeek(final float wx, final float wy, final int defaultBiomeId) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return biomeIdAtTilePeek(tx, ty, defaultBiomeId);
  }

  public boolean isWaterAtWorldPeek(final float wx, final float wy, final boolean defaultValue) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return isWaterAtTilePeek(tx, ty, defaultValue);
  }

  public boolean isBlockedAtWorldPeek(final float wx, final float wy, final boolean defaultValue) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return isBlockedAtTilePeek(tx, ty, defaultValue);
  }

  /** Terraced height level at world position. Returns defaultValue if chunk not loaded. */
  public int heightLevelAtWorldPeek(final float wx, final float wy, final int defaultValue) {
    final int tx = (int) Math.floor(wx / TILE_WORLD);
    final int ty = (int) Math.floor(wy / TILE_WORLD);
    return heightLevelAtTilePeek(tx, ty, defaultValue);
  }

  private Chunk peekChunkForTile(final int tx, final int ty) {
    final int cx = floorDivInt(tx, CHUNK_SIZE);
    final int cy = floorDivInt(ty, CHUNK_SIZE);
    return store.peek(cx, cy);
  }

  private static int idxForTile(final int tx, final int ty) {
    final int lx = modPositive(tx, CHUNK_SIZE);
    final int ly = modPositive(ty, CHUNK_SIZE);
    return lx + ly * CHUNK_SIZE;
  }

  private static int floorDivInt(final int a, final int b) {
    final int r = a / b;
    // Java / rounds toward 0
    if ((a ^ b) < 0 && (r * b != a)) return r - 1;
    return r;
  }

  private static int modPositive(final int a, final int b) {
    int m = a % b;
    if (m < 0) m += b;
    return m;
  }
}


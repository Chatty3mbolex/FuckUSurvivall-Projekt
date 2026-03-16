package com.yourgame.survival.worldgen;

import com.yourgame.survival.biome.BiomeSystem;

/**
 * Immutable context used by worldgen modules.
 *
 * NOTE: This is intentionally minimal in Request 2 (structure only).
 */
public final class WorldGenContext {
  public final long seed;
  public final BiomeSystem biomes;
  public final WorldGenConfig config;

  public WorldGenContext(long seed, BiomeSystem biomes, WorldGenConfig config) {
    this.seed = seed;
    this.biomes = biomes;
    this.config = (config != null) ? config : WorldGenConfig.defaults();
  }
}

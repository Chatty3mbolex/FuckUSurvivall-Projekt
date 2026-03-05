package com.yourgame.survival.worldgen.biome;

import com.yourgame.survival.world.Biome;

/** Default biome rules (must match legacy biome classification). */
public final class DefaultBiomeClassifier implements BiomeClassifier {
  @Override
  public Biome classify(float height, float heat, float moist) {
    // crude rule-based biomes to satisfy Block 3 visibility
    if (height < 0.18f) return Biome.WATER;
    if (height < 0.22f) return Biome.BEACH;

    // mountain bands
    if (height > 0.83f) return (heat < 0.35f) ? Biome.SNOWHIGHLAND : Biome.MOUNTAIN;

    // volcanic hotspot
    if (heat > 0.82f && moist < 0.35f) {
      if (height > 0.65f) return Biome.VOLCANIC;
      if (height > 0.55f) return Biome.ASHFIELD;
    }

    if (moist > 0.72f && heat > 0.45f) return Biome.SWAMP;
    if (moist > 0.60f) return Biome.FOREST;

    // islands: medium height but surrounded by low height in our simplified model isn't computed;
    // so use a rare climate combination.
    if (height > 0.45f && height < 0.55f && heat > 0.55f && moist > 0.50f) return Biome.ISLANDS;

    return Biome.GRASSLAND;
  }
}

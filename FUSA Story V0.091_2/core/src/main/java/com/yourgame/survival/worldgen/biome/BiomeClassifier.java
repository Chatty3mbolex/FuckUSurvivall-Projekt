package com.yourgame.survival.worldgen.biome;

import com.yourgame.survival.world.Biome;

/** Maps (height, heat, moist) in 0..1 to a Biome id. */
public interface BiomeClassifier {
  Biome classify(float height01, float heat01, float moist01);
}

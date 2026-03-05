package com.yourgame.survival.worldgen.noise;

/** Noise interface used by worldgen modules (fbm/value-noise style). */
public interface NoiseSampler {
  float fbm01(long seed, int tx, int ty, float baseFreq, int octaves);
}

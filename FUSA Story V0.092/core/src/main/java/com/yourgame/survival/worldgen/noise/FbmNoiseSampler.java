package com.yourgame.survival.worldgen.noise;

/**
 * Default deterministic fbm/value-noise sampler.
 *
 * Kept compatible with the legacy behavior to stay deterministic.
 */
public final class FbmNoiseSampler implements NoiseSampler {

  @Override
  public float fbm01(long seed, int tx, int ty, float baseFreq, int octaves) {
    float amp = 1f;
    float sum = 0f;
    float norm = 0f;
    float freq = baseFreq;

    for (int o = 0; o < octaves; o++) {
      sum += amp * valueNoise01(seed + o * 1013L, tx * freq, ty * freq);
      norm += amp;
      amp *= 0.5f;
      freq *= 2f;
    }
    return clamp01(sum / norm);
  }

  private static float valueNoise01(long seed, float x, float y) {
    int x0 = fastFloor(x);
    int y0 = fastFloor(y);
    int x1 = x0 + 1;
    int y1 = y0 + 1;

    float tx = x - x0;
    float ty = y - y0;

    float sx = fade(tx);
    float sy = fade(ty);

    float a = hash01(seed, x0, y0);
    float b = hash01(seed, x1, y0);
    float c = hash01(seed, x0, y1);
    float d = hash01(seed, x1, y1);

    float ab = lerp(a, b, sx);
    float cd = lerp(c, d, sx);
    return lerp(ab, cd, sy);
  }

  private static int fastFloor(float v) {
    int i = (int) v;
    return (v < i) ? (i - 1) : i;
  }

  private static float fade(float t) {
    // smoothstep
    return t * t * (3f - 2f * t);
  }

  private static float lerp(float a, float b, float t) {
    return a + (b - a) * t;
  }

  private static float hash01(long seed, int x, int y) {
    long h = seed;
    h ^= (long) x * 0x9E3779B97F4A7C15L;
    h ^= (long) y * 0xC2B2AE3D27D4EB4FL;
    h = mix64(h);
    // 24-bit mantissa-ish
    return ((h >>> 40) & 0xFFFFFF) / (float) 0x1000000;
  }

  public static long mix64(long z) {
    z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
    z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
    return z ^ (z >>> 33);
  }

  public static float clamp01(float v) {
    return v < 0f ? 0f : Math.min(1f, v);
  }
}

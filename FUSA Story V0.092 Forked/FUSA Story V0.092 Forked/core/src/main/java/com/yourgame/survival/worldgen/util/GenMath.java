package com.yourgame.survival.worldgen.util;

/** Small shared helpers for worldgen (clamp/mod/floorDiv). */
public final class GenMath {
  private GenMath() {}

  public static float clamp01(float v) {
    return v < 0f ? 0f : Math.min(1f, v);
  }

  public static int clamp(int v, int lo, int hi) {
    return (v < lo) ? lo : (v > hi) ? hi : v;
  }

  public static int modPositive(final int a, final int b) {
    int m = a % b;
    if (m < 0) m += b;
    return m;
  }

  public static int floorDivInt(final int a, final int b) {
    final int r = a / b;
    if ((a ^ b) < 0 && (r * b != a)) return r - 1;
    return r;
  }
}

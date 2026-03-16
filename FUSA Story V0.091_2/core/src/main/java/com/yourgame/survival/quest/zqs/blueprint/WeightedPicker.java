package com.yourgame.survival.quest.zqs.blueprint;

import java.util.ArrayList;

/**
 * Lightweight deterministic weighted picker.
 */
public final class WeightedPicker {
  private long rng;

  public WeightedPicker(long seed) {
    rng = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L;
  }

  public <T> T pick(ArrayList<T> list, WeightFn<T> fn) {
    if (list == null || list.isEmpty()) return null;

    float sum = 0f;
    for (int i = 0; i < list.size(); i++) {
      T e = list.get(i);
      float w = (fn != null) ? fn.weightOf(e) : 1f;
      if (w > 0f) sum += w;
    }
    if (sum <= 0f) return null;

    float r = nextFloat01() * sum;
    float acc = 0f;
    for (int i = 0; i < list.size(); i++) {
      T e = list.get(i);
      float w = (fn != null) ? fn.weightOf(e) : 1f;
      if (w <= 0f) continue;
      acc += w;
      if (r <= acc) return e;
    }
    return list.get(0);
  }

  public interface WeightFn<T> { float weightOf(T t); }

  private long nextLong() {
    long x = rng;
    x ^= x >>> 12;
    x ^= x << 25;
    x ^= x >>> 27;
    rng = x;
    return x * 2685821657736338717L;
  }

  private float nextFloat01() {
    return ((nextLong() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
  }
}


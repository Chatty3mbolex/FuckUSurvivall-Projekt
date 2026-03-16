package com.yourgame.survival.util;

import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;

/** Minimal long-key set with iterable keys (no extra deps). */
public final class LongKeySet {
  private static final Object PRESENT = new Object();
  private final LongMap<Object> map;
  private final LongArray keys = new LongArray(false, 256);

  public LongKeySet(int initialCapacity) {
    this.map = new LongMap<>(initialCapacity);
  }

  public boolean contains(long k) {
    return map.containsKey(k);
  }

  public void add(long k) {
    if (map.containsKey(k)) return;
    map.put(k, PRESENT);
    keys.add(k);
  }

  public void clear() {
    map.clear();
    keys.clear();
  }

  public LongArray keys() {
    return keys;
  }
}

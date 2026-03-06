package com.yourgame.survival.util;

/** Packs two 32-bit signed ints into one long key. */
public final class PackCoord {
  private PackCoord() {}

  public static long key(int x, int y) {
    return (((long)x) << 32) ^ (y & 0xffffffffL);
  }
}

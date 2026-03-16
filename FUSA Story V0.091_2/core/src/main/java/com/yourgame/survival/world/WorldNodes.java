package com.yourgame.survival.world;

import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.util.LongKeySet;

/**
 * Tracks procedural world nodes (trees/rocks/ores) that were removed by gameplay,
 * so they don't respawn after save/load.
 */
public final class WorldNodes {
  public final LongKeySet removed = new LongKeySet(2048);

  public static long key(EntityType t, int tx, int ty) {
    return keyRaw(t.ordinal() & 0xFF, tx, ty);
  }

  /**
   * Generic key packer.
   * Format: 8 bits typeId + 28 bits x + 28 bits y (signed biased).
   */
  public static long keyRaw(int typeId, int tx, int ty) {
    int tid = typeId & 0xFF;
    int bx = tx + 0x08000000;
    int by = ty + 0x08000000;
    return ((long) tid << 56) | ((long) (bx & 0x0FFFFFFF) << 28) | (long) (by & 0x0FFFFFFF);
  }

  public static int rawTypeId(long k) {
    return (int) ((k >>> 56) & 0xFFL);
  }

  public static int rawTx(long k) {
    int bx = (int) ((k >>> 28) & 0x0FFFFFFFL);
    return bx - 0x08000000;
  }

  public static int rawTy(long k) {
    int by = (int) (k & 0x0FFFFFFFL);
    return by - 0x08000000;
  }

  public boolean isRemoved(EntityType t, int tx, int ty) {
    return removed.contains(key(t, tx, ty));
  }

  public void markRemoved(EntityType t, int tx, int ty) {
    removed.add(key(t, tx, ty));
  }
}

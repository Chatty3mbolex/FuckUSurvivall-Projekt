package com.yourgame.survival.editor;

import com.yourgame.survival.world.World;

/** Constants for the new WorldEditor. */
public final class EditorConstants {
  private EditorConstants() {}

  /** Hard edit bounds in chunk coordinates (inclusive). */
  public static final int MIN_C = -1;
  public static final int MAX_C = 1;

  public static final int CHUNK_SIZE = World.CHUNK_SIZE;
  public static final float TILE_WORLD = World.TILE_WORLD;
}

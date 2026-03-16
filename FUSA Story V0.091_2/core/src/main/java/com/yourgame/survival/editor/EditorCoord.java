package com.yourgame.survival.editor;

import com.yourgame.survival.world.World;

/** Coordinate helpers shared by editor tools. */
public final class EditorCoord {
  private EditorCoord() {}

  public static int worldToTile(float w) {
    return (int) Math.floor(w / World.TILE_WORLD);
  }

  public static int floorDiv(int a, int b) {
    int r = a / b;
    if ((a ^ b) < 0 && (r * b != a)) r--;
    return r;
  }

  public static int mod(int a, int b) {
    int m = a % b;
    if (m < 0) m += b;
    return m;
  }
}

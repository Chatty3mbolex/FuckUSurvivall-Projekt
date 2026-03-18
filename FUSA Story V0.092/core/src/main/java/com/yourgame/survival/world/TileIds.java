package com.yourgame.survival.world;

/**
 * Block B: Tile ID constants (kept tiny + stable). These IDs are purely logical and
 * will later be mapped to TextureAtlas region names.
 */
public final class TileIds {
  private TileIds() {}

  // Ground IDs
  public static final short GROUND_GRASS = 0;
  public static final short GROUND_DIRT  = 1;
  public static final short GROUND_SAND  = 2;
  public static final short GROUND_ROCK  = 3;
  public static final short GROUND_SNOW  = 4;
  public static final short GROUND_LAVA  = 5;
}

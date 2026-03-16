package com.yourgame.survival.world;

public enum Biome {
  GRASSLAND(0, 0xFF3FA34D),
  FOREST(1, 0xFF1F7A1F),
  SWAMP(2, 0xFF2E5F3E),
  BEACH(3, 0xFFE6D690),
  RIVERBANK(4, 0xFF9BCB8A),
  WATER(5, 0xFF1D6FA3),
  MOUNTAIN(6, 0xFF808080),
  SNOWHIGHLAND(7, 0xFFE6F2FF),
  VOLCANIC(8, 0xFF4A3A2A),
  ASHFIELD(9, 0xFF6B6B6B),
  ISLANDS(10, 0xFF4DBD8B),
  LAVA(11, 0xFFE6452E);

  public final byte id;
  public final int argb;

  Biome(int id, int argb) {
    this.id = (byte) id;
    this.argb = argb;
  }

  public static Biome byId(int id) {
    for (Biome b : values()) if ((b.id & 0xff) == id) return b;
    return GRASSLAND;
  }
}

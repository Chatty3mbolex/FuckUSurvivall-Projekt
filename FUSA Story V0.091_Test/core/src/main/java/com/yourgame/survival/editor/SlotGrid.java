package com.yourgame.survival.editor;

import com.yourgame.survival.world.Biome;

/**
 * 3x3 biome slot grid for EXAMPLE_MAP.
 * Coordinates: sx,sy in [0..2] where (0,0)=bottom-left slot.
 */
public final class SlotGrid {
  private final Biome[][] slots = new Biome[3][3];

  public SlotGrid() {
    // default: fill with first biome (safe fallback)
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) slots[x][y] = Biome.GRASSLAND;
    }
  }

  public void set(int sx, int sy, Biome b) {
    if (sx < 0 || sy < 0 || sx > 2 || sy > 2) return;
    slots[sx][sy] = (b != null) ? b : Biome.GRASSLAND;
  }

  public Biome get(int sx, int sy) {
    if (sx < 0 || sy < 0 || sx > 2 || sy > 2) return Biome.GRASSLAND;
    Biome b = slots[sx][sy];
    return (b != null) ? b : Biome.GRASSLAND;
  }

  /**
   * Maps chunk coords (cx,cy) in [-1..1] to slot coords [0..2].
   */
  public static int slotX(int cx) { return cx + 1; }
  public static int slotY(int cy) { return cy + 1; }
}

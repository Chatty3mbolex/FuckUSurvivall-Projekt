package com.yourgame.survival.editor;

import com.badlogic.gdx.graphics.Pixmap;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.World;

import java.util.HashMap;

/** Applies zone-based height rules onto heightLevel layer for the editor preview. */
public final class ZoneHeightApplier {
  private ZoneHeightApplier() {}

  /**
   * Applies a single rule (zoneName -> delta) to all chunks in editor bounds.
   * NOTE: This is an editor preview operation. It destructively modifies heightLevel.
   */
  public static void apply(World world,
                           EditorMode mode,
                           Biome selectedBiome,
                           SlotGrid slotGrid,
                           HashMap<String, HashMap<String, Pixmap>> zonePix,
                           ZoneHeightRule rule) {
    apply(world, mode, selectedBiome, slotGrid, zonePix, rule, null);
  }

  /**
   * Applies rule, optionally restricted to a single biome (onlyBiome).
   * If onlyBiome is null, applies to all biomes (old behavior).
   */
  public static void apply(World world,
                           EditorMode mode,
                           Biome selectedBiome,
                           SlotGrid slotGrid,
                           HashMap<String, HashMap<String, Pixmap>> zonePix,
                           ZoneHeightRule rule,
                           Biome onlyBiome) {
    if (world == null || rule == null) return;
    String z = (rule.zoneName == null || rule.zoneName.isBlank()) ? "zone" : rule.zoneName.trim();
    int d = rule.clampDelta();

    for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
      for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
        Biome b = (mode == EditorMode.EXAMPLE_MAP)
            ? slotGrid.get(SlotGrid.slotX(cx), SlotGrid.slotY(cy))
            : selectedBiome;
        if (b == null) continue;
        if (onlyBiome != null && b != onlyBiome) continue;

        Pixmap pm = null;
        try {
          HashMap<String, Pixmap> bm = zonePix.get(b.name());
          if (bm != null) pm = bm.get(z);
        } catch (Throwable ignored) {}
        if (pm == null) continue;

        Chunk c = world.chunk(cx, cy);
        if (c == null || c.layers == null || c.layers.heightLevel == null) continue;

        for (int ly = 0; ly < World.CHUNK_SIZE; ly++) {
          for (int lx = 0; lx < World.CHUNK_SIZE; lx++) {
            int a = pm.getPixel(lx, ly) & 0xFF; // alpha
            if (a == 0) continue;
            int idx = lx + ly * World.CHUNK_SIZE;

            // Height is only allowed on specific ground types.
            int gid = c.layers.groundId[idx] & 0xFF;
            boolean allow = (gid == com.yourgame.survival.world.TileIds.GROUND_GRASS
                || gid == com.yourgame.survival.world.TileIds.GROUND_DIRT
                || gid == com.yourgame.survival.world.TileIds.GROUND_ROCK
                || gid == com.yourgame.survival.world.TileIds.GROUND_SNOW);
            if (!allow) continue;

            int h = (c.layers.heightLevel[idx] & 0xFF) + d;
            if (h < 0) h = 0;
            if (h > 15) h = 15;
            c.layers.heightLevel[idx] = (byte) h;
          }
        }
      }
    }
  }
}

package com.yourgame.survival.editor;

import com.yourgame.survival.biome.BiomeSystem;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.worldgen.WorldGenConfig;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.WorldGenerator;
import com.yourgame.survival.worldgen.pipeline.ChunkGenOrchestrator;

/**
 * Editor-only WorldGenerator.
 *
 * Behavior:
 * - NUR_BIOME: force the selected biome for all chunks.
 * - EXAMPLE_MAP: force biome per 3x3 slot (chunk coords -1..1). Outside the 3x3 window uses selected biome.
 *
 * NOTE: This is used only by the new WorldEditor and should not affect the main game.
 */
public final class EditorWorldGenerator implements WorldGenerator {
  private final long seed;
  private final BiomeSystem biomes;

  private final SlotGrid slotGrid;
  private final WorldGenContext ctx;

  private EditorMode mode;
  private Biome selectedBiome;

  public EditorWorldGenerator(long seed, BiomeSystem biomes, SlotGrid slotGrid, EditorMode mode, Biome selectedBiome) {
    this.seed = seed;
    this.biomes = biomes;
    this.slotGrid = (slotGrid != null) ? slotGrid : new SlotGrid();
    this.mode = (mode != null) ? mode : EditorMode.NUR_BIOME;
    this.selectedBiome = (selectedBiome != null) ? selectedBiome : Biome.GRASSLAND;

    WorldGenConfig cfg = WorldGenConfig.loadOrDefault();
    this.ctx = new WorldGenContext(seed, this.biomes, cfg);
  }

  public void setMode(EditorMode mode) {
    if (mode != null) this.mode = mode;
  }

  public void setSelectedBiome(Biome b) {
    if (b != null) this.selectedBiome = b;
  }

  private Biome forcedBiomeForChunk(int cx, int cy) {
    if (mode == EditorMode.EXAMPLE_MAP) {
      if (cx >= EditorConstants.MIN_C && cx <= EditorConstants.MAX_C && cy >= EditorConstants.MIN_C && cy <= EditorConstants.MAX_C) {
        int sx = SlotGrid.slotX(cx);
        int sy = SlotGrid.slotY(cy);
        return slotGrid.get(sx, sy);
      }
    }
    return selectedBiome;
  }

  @Override
  public Chunk generate(int cx, int cy) {
    Biome forced = forcedBiomeForChunk(cx, cy);
    // Create a short-lived orchestrator with a shared context but per-call forced biome.
    ChunkGenOrchestrator gen = new ChunkGenOrchestrator(ctx, forced);
    return gen.generate(cx, cy);
  }
}

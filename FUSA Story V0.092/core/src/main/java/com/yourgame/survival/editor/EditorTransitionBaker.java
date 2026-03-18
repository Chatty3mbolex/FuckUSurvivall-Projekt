package com.yourgame.survival.editor;

import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.world.World;
import com.yourgame.survival.world.TransitionRules;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.FbmNoiseSampler;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.util.FinalTileSampler;

/**
 * Editor-only rebaker for transition corner masks after manual ground edits.
 *
 * Uses current loaded chunks for groundId sampling. Falls back to deterministic FinalTileSampler
 * when a tile is outside the loaded 3x3 window.
 */
public final class EditorTransitionBaker {
  private final World world;

  // Fallback sampler dependencies
  private final NoiseSampler noise = new FbmNoiseSampler();
  private final BiomeClassifier classifier = new com.yourgame.survival.worldgen.biome.DefaultBiomeClassifier();
  private final com.yourgame.survival.worldgen.WorldGenContext ctx;

  public EditorTransitionBaker(World world) {
    this.world = world;
    // Cache a context for deterministic fallback sampling.
    this.ctx = (world != null)
        ? new com.yourgame.survival.worldgen.WorldGenContext(worldSeed(), world.biomes(), com.yourgame.survival.worldgen.WorldGenConfig.loadOrDefault())
        : null;
  }

  public void rebakeRectTiles(int minTx, int minTy, int maxTx, int maxTy) {
    if (world == null) return;

    // Clamp to editor bounds (3x3 chunks)
    int minCx = EditorConstants.MIN_C;
    int maxCx = EditorConstants.MAX_C;
    int minCy = EditorConstants.MIN_C;
    int maxCy = EditorConstants.MAX_C;

    int loTx = minCx * World.CHUNK_SIZE;
    int hiTx = (maxCx + 1) * World.CHUNK_SIZE - 1;
    int loTy = minCy * World.CHUNK_SIZE;
    int hiTy = (maxCy + 1) * World.CHUNK_SIZE - 1;

    if (minTx < loTx) minTx = loTx;
    if (maxTx > hiTx) maxTx = hiTx;
    if (minTy < loTy) minTy = loTy;
    if (maxTy > hiTy) maxTy = hiTy;

    if (minTx > maxTx || minTy > maxTy) return;

    for (int ty = minTy; ty <= maxTy; ty++) {
      for (int tx = minTx; tx <= maxTx; tx++) {
        int cx = EditorCoord.floorDiv(tx, World.CHUNK_SIZE);
        int cy = EditorCoord.floorDiv(ty, World.CHUNK_SIZE);
        if (!EditorBounds.isChunkInside(cx, cy)) continue;

        Chunk c = world.chunk(cx, cy); // editor wants chunk present
        if (c == null || c.layers == null) continue;
        TileLayers l = c.layers;

        int lx = EditorCoord.mod(tx, World.CHUNK_SIZE);
        int ly = EditorCoord.mod(ty, World.CHUNK_SIZE);
        int idx = lx + ly * World.CHUNK_SIZE;

        // do not compute on water tiles
        if ((l.waterMask[idx] & 0xFF) != 0) {
          l.grassCornerMask16[idx] = 0;
          l.dirtCornerMask16[idx] = 0;
          l.sandCornerMask16[idx] = 0;
          l.rockCornerMask16[idx] = 0;
          l.snowCornerMask16[idx] = 0;
          continue;
        }

        short base = (short) (l.groundId[idx] & 0xFF);

        l.grassCornerMask16[idx] = (base == TileIds.GROUND_GRASS || !TransitionRules.allowTransition(base, TileIds.GROUND_GRASS))
            ? 0
            : (byte) computeCornerMask(tx, ty, TileIds.GROUND_GRASS);

        l.dirtCornerMask16[idx] = (base == TileIds.GROUND_DIRT || !TransitionRules.allowTransition(base, TileIds.GROUND_DIRT))
            ? 0
            : (byte) computeCornerMask(tx, ty, TileIds.GROUND_DIRT);

        l.sandCornerMask16[idx] = (base == TileIds.GROUND_SAND || !TransitionRules.allowTransition(base, TileIds.GROUND_SAND))
            ? 0
            : (byte) computeCornerMask(tx, ty, TileIds.GROUND_SAND);

        l.rockCornerMask16[idx] = (base == TileIds.GROUND_ROCK || !TransitionRules.allowTransition(base, TileIds.GROUND_ROCK))
            ? 0
            : (byte) computeCornerMask(tx, ty, TileIds.GROUND_ROCK);

        l.snowCornerMask16[idx] = (base == TileIds.GROUND_SNOW || !TransitionRules.allowTransition(base, TileIds.GROUND_SNOW))
            ? 0
            : (byte) computeCornerMask(tx, ty, TileIds.GROUND_SNOW);
      }
    }
  }

  private int computeCornerMask(int tx, int ty, short targetGroundId) {
    // This matches DefaultTransitionMaskBuilder.computeCornerMaskLocal semantics,
    // but directly samples 2x2 tiles around each corner in global tile coords.
    int nw = cornerInside(tx, ty, targetGroundId);
    int ne = cornerInside(tx + 1, ty, targetGroundId);
    int se = cornerInside(tx + 1, ty - 1, targetGroundId);
    int sw = cornerInside(tx, ty - 1, targetGroundId);
    return (nw) | (ne << 1) | (se << 2) | (sw << 3);
  }

  private int cornerInside(int cx, int cy, short target) {
    int c = 0;
    if (groundAt(cx, cy) == target) c++;
    if (groundAt(cx - 1, cy) == target) c++;
    if (groundAt(cx, cy - 1) == target) c++;
    if (groundAt(cx - 1, cy - 1) == target) c++;
    return c >= 2 ? 1 : 0;
  }

  private short groundAt(int tx, int ty) {
    int cx = EditorCoord.floorDiv(tx, World.CHUNK_SIZE);
    int cy = EditorCoord.floorDiv(ty, World.CHUNK_SIZE);
    if (EditorBounds.isChunkInside(cx, cy)) {
      Chunk c = world.peekChunk(cx, cy);
      if (c != null && c.layers != null) {
        int lx = EditorCoord.mod(tx, World.CHUNK_SIZE);
        int ly = EditorCoord.mod(ty, World.CHUNK_SIZE);
        int idx = lx + ly * World.CHUNK_SIZE;
        return (short) (c.layers.groundId[idx] & 0xFF);
      }
    }
    // Fallback to deterministic worldgen output (keeps borders stable).
    if (ctx == null) return TileIds.GROUND_GRASS;
    return FinalTileSampler.finalGroundIdAt(tx, ty, ctx, noise, classifier);
  }

  private long worldSeed() {
    // World doesn't expose seed; editor uses deterministic seed anyway.
    // We accept that fallback may differ if seed changes. For now, use 1234567.
    return 1234567L;
  }
}

package com.yourgame.survival.worldgen.adjacency;

import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.world.TransitionRules;
import com.yourgame.survival.world.World;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.util.FinalTileSampler;

/**
 * Bakes marching-squares corner masks (0..15) for terrain transitions.
 *
 * This is the fix for chunk-border transition flicker:
 * - Renderer must NOT sample neighbor chunks.
 * - All neighbor-dependent masks are computed here using global deterministic sampling.
 */
public final class DefaultTransitionMaskBuilder {
  private final NoiseSampler noise;
  private final BiomeClassifier classifier;

  public DefaultTransitionMaskBuilder(NoiseSampler noise, BiomeClassifier classifier) {
    this.noise = noise;
    this.classifier = classifier;
  }

  public void build(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    final int size = l.size;

    // Cache final ground in a padded grid so we don't call FinalTileSampler 262k times per chunk.
    // Padding must cover cornerInsideLocal() which samples (cx,cy),(cx-1,cy),(cx,cy-1),(cx-1,cy-1).
    // computeCornerMaskLocal() can call cornerInsideLocal(lx+1, ly-1) so cx/cy can be -1,
    // and inside that we still access (cx-1)/(cy-1) => we must cover down to -2.
    final int pad = 2;
    final int stride = size + pad * 2;
    final short[] g = new short[stride * stride];

    final int baseTx = cx * World.CHUNK_SIZE;
    final int baseTy = cy * World.CHUNK_SIZE;

    for (int y = -pad; y <= size + (pad - 1); y++) {
      for (int x = -pad; x <= size + (pad - 1); x++) {
        final int tx = baseTx + x;
        final int ty = baseTy + y;
        g[(x + pad) + (y + pad) * stride] = FinalTileSampler.finalGroundIdAt(tx, ty, ctx, noise, classifier);
      }
    }

    for (int ly = 0; ly < size; ly++) {
      for (int lx = 0; lx < size; lx++) {
        final int idx = lx + ly * size;

        // Only meaningful on land tiles; keep 0 on water.
        if ((l.waterMask[idx] & 0xFF) != 0) {
          l.grassCornerMask16[idx] = 0;
          l.dirtCornerMask16[idx] = 0;
          l.sandCornerMask16[idx] = 0;
          l.rockCornerMask16[idx] = 0;
          l.snowCornerMask16[idx] = 0;
          continue;
        }

        final short base = (short) (l.groundId[idx] & 0xFF);

        // Hard transition rules (see transitions.md): only compute masks that are allowed to appear
        // on top of this base ground. Disallowed transitions are forced to 0.

        // Grass edges
        l.grassCornerMask16[idx] = (base == TileIds.GROUND_GRASS || !TransitionRules.allowTransition(base, TileIds.GROUND_GRASS))
            ? 0
            : (byte) computeCornerMaskLocal(lx, ly, TileIds.GROUND_GRASS, g, stride);

        l.dirtCornerMask16[idx] = (base == TileIds.GROUND_DIRT || !TransitionRules.allowTransition(base, TileIds.GROUND_DIRT))
            ? 0
            : (byte) computeCornerMaskLocal(lx, ly, TileIds.GROUND_DIRT, g, stride);

        l.sandCornerMask16[idx] = (base == TileIds.GROUND_SAND || !TransitionRules.allowTransition(base, TileIds.GROUND_SAND))
            ? 0
            : (byte) computeCornerMaskLocal(lx, ly, TileIds.GROUND_SAND, g, stride);

        l.rockCornerMask16[idx] = (base == TileIds.GROUND_ROCK || !TransitionRules.allowTransition(base, TileIds.GROUND_ROCK))
            ? 0
            : (byte) computeCornerMaskLocal(lx, ly, TileIds.GROUND_ROCK, g, stride);

        l.snowCornerMask16[idx] = (base == TileIds.GROUND_SNOW || !TransitionRules.allowTransition(base, TileIds.GROUND_SNOW))
            ? 0
            : (byte) computeCornerMaskLocal(lx, ly, TileIds.GROUND_SNOW, g, stride);
      }
    }
  }

  private static int computeCornerMaskLocal(final int lx, final int ly, final short targetGroundId, final short[] g, final int stride) {
    int nw = cornerInsideLocal(lx, ly, targetGroundId, g, stride);
    int ne = cornerInsideLocal(lx + 1, ly, targetGroundId, g, stride);
    int se = cornerInsideLocal(lx + 1, ly - 1, targetGroundId, g, stride);
    int sw = cornerInsideLocal(lx, ly - 1, targetGroundId, g, stride);
    return (nw) | (ne << 1) | (se << 2) | (sw << 3);
  }

  private static int cornerInsideLocal(final int cx, final int cy, final short targetGroundId, final short[] g, final int stride) {
    // This function operates in "corner" coordinates, which can go down to -1 from computeCornerMaskLocal().
    // We require pad=2 in the caller and therefore shift by +2/+1 here.
    int c = 0;

    // Samples: (cx,cy),(cx-1,cy),(cx,cy-1),(cx-1,cy-1) in tile coordinates.
    // With pad=2, tile (0,0) is stored at g[(0+2)+(0+2)*stride].
    if (g[(cx + 2) + (cy + 2) * stride] == targetGroundId) c++;
    if (g[(cx + 1) + (cy + 2) * stride] == targetGroundId) c++;
    if (g[(cx + 2) + (cy + 1) * stride] == targetGroundId) c++;
    if (g[(cx + 1) + (cy + 1) * stride] == targetGroundId) c++;

    return c >= 2 ? 1 : 0;
  }
}

package com.yourgame.survival.worldgen.water;

import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;

import java.util.Arrays;

/** Default water processor extracted from the legacy water post-process. */
public final class DefaultWaterPostProcessor implements WaterPostProcessor {
  private final NoiseSampler noise;
  private final BiomeClassifier classifier;

  public DefaultWaterPostProcessor(NoiseSampler noise, BiomeClassifier classifier) {
    this.noise = noise;
    this.classifier = classifier;
  }

  @Override
  public void process(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    // 1) smooth water decision (global-stable), write waterMask
    applySmoothWaterMask(l, cx, cy, ctx);
    // 2) distance-to-water field (for shore bands + deco)
    computeWaterDist(l, cx, cy, Math.max(0, ctx.config.waterDistMaxR));
    // 3) apply shore band materials (multi-tile shore)
    applyShoreBandMaterials(l);
  }

  private void applySmoothWaterMask(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    // Use a global-stable majority filter over the base water decision.
    final int size = l.size;
    for (int ly = 0; ly < size; ly++) {
      for (int lx = 0; lx < size; lx++) {
        final int idx = lx + ly * size;
        final int tx = cx * size + lx;
        final int ty = cy * size + ly;
        l.waterMask[idx] = (byte) (smoothWaterAtGlobalTile(tx, ty, ctx) ? 1 : 0);
      }
    }
  }

  private boolean smoothWaterAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    // 3x3 majority over the base classifier.
    int c = 0;
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        if (waterAtGlobalTile(tx + dx, ty + dy, ctx)) c++;
      }
    }
    // >=5 of 9 => water
    return c >= 5;
  }

  private boolean waterAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);
    final Biome b = classifier.classify(height, heat, moist);
    return (b == Biome.WATER || b == Biome.RIVERBANK);
  }

  private void computeWaterDist(final TileLayers l, final int cx, final int cy, final int maxR) {
    // Distance from any tile to nearest water tile, capped.
    // 0=water, 1..maxR=distance, 255=unknown/too far.
    final int size = l.size;
    final int n = size * size;

    Arrays.fill(l.waterDist, (byte) 0xFF);

    // queue arrays (no allocations)
    int[] q = new int[n];
    int qh = 0, qt = 0;

    for (int i = 0; i < n; i++) {
      if (l.waterMask[i] != 0) {
        l.waterDist[i] = 0;
        q[qt++] = i;
      }
    }

    while (qh < qt) {
      int idx = q[qh++];
      int d = l.waterDist[idx] & 0xFF;
      if (d >= maxR) continue;

      int x = idx % size;
      int y = idx / size;

      // 4-neighbor BFS
      if (x > 0) {
        int ni = idx - 1;
        if ((l.waterDist[ni] & 0xFF) > d + 1) {
          l.waterDist[ni] = (byte) (d + 1);
          q[qt++] = ni;
        }
      }
      if (x < size - 1) {
        int ni = idx + 1;
        if ((l.waterDist[ni] & 0xFF) > d + 1) {
          l.waterDist[ni] = (byte) (d + 1);
          q[qt++] = ni;
        }
      }
      if (y > 0) {
        int ni = idx - size;
        if ((l.waterDist[ni] & 0xFF) > d + 1) {
          l.waterDist[ni] = (byte) (d + 1);
          q[qt++] = ni;
        }
      }
      if (y < size - 1) {
        int ni = idx + size;
        if ((l.waterDist[ni] & 0xFF) > d + 1) {
          l.waterDist[ni] = (byte) (d + 1);
          q[qt++] = ni;
        }
      }
    }
  }

  private void applyShoreBandMaterials(final TileLayers l) {
    // Multi-tile shore band driven by waterDist.
    final int n = l.size * l.size;
    for (int i = 0; i < n; i++) {
      int d = l.waterDist[i] & 0xFF;
      if (d == 0) {
        // water stays water
        l.waterMask[i] = 1;
      } else if (d == 1) {
        // immediate shore ring only (strict)
        l.groundId[i] = TileIds.GROUND_DIRT;
      }
    }
  }

  // Exposed for adjacency builder (keeps behavior identical). Optional use.
  public boolean smoothWaterAtGlobalTilePublic(int tx, int ty, WorldGenContext ctx) {
    return smoothWaterAtGlobalTile(tx, ty, ctx);
  }
}

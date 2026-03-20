package com.yourgame.survival.worldgen.adjacency;

import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;

/** Default adjacency builder extracted from the legacy adjacency mask builder. */
public final class DefaultAdjacencyBuilder implements AdjacencyBuilder {
  private final NoiseSampler noise;
  private final BiomeClassifier classifier;

  public DefaultAdjacencyBuilder(NoiseSampler noise, BiomeClassifier classifier) {
    this.noise = noise;
    this.classifier = classifier;
  }

  @Override
  public void build(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    final int size = l.size;

    for (int ly = 0; ly < size; ly++) {
      for (int lx = 0; lx < size; lx++) {
        final int idx = lx + ly * size;

        final int tx = cx * size + lx;
        final int ty = cy * size + ly;

        final boolean nW = smoothWaterAtGlobalTile(tx, ty + 1, ctx);
        final boolean eW = smoothWaterAtGlobalTile(tx + 1, ty, ctx);
        final boolean sW = smoothWaterAtGlobalTile(tx, ty - 1, ctx);
        final boolean wW = smoothWaterAtGlobalTile(tx - 1, ty, ctx);

        l.shoreMask4[idx] = (byte) ((nW ? 1 : 0) | (eW ? 2 : 0) | (sW ? 4 : 0) | (wW ? 8 : 0));

        // Road adjacency is baked by the road module (RoadAdjacencyBaker).
        // Terrain transition masks are baked separately (corner masks) to keep renderer border-stable.
      }
    }
  }

  private boolean smoothWaterAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    int c = 0;
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        if (waterAtGlobalTile(tx + dx, ty + dy, ctx)) c++;
      }
    }
    return c >= 5;
  }

  private boolean waterAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);
    final Biome b = classifier.classify(height, heat, moist);
    return (b == Biome.WATER || b == Biome.RIVERBANK);
  }

}


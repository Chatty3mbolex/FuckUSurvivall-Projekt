package com.yourgame.survival.worldgen.adjacency;

import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.util.GenMath;

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

  // Nicht fertiges Feature:
  // groundAtGlobalTile(...) (unused helper right now)
  /*
  private short groundAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    // Must match the legacy ground decision for deterministic edge masks.
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);

    final Biome b = classifier.classify(height, heat, moist);
    final boolean water = (b == Biome.WATER || b == Biome.RIVERBANK);

    short g = ctx.biomes.def(b).groundId;

    // mirror micro-classify (forcedBiome is never applied in global query)
    if (!water) {
      float veg01mc = GenMath.clamp01(moist * (1f - GenMath.clamp01((height - 0.70f) * 2.2f))
          * (0.65f + 0.35f * noise.fbm01(ctx.seed ^ 0x55AA55AA55AA55AAL, tx, ty, ctx.config.vegFreq, ctx.config.vegOctaves)));

      float rock01mc = GenMath.clamp01(GenMath.clamp01((height - 0.55f) * 1.6f)
          * (0.55f + 0.45f * noise.fbm01(ctx.seed ^ 0xCC33CC33CC33CC33L, tx, ty, ctx.config.rockFreq, ctx.config.rockOctaves)));

      float path01mc = GenMath.clamp01(noise.fbm01(ctx.seed ^ 0x0F0E0D0C0B0A0908L, tx, ty, ctx.config.pathFreq, ctx.config.pathOctaves)
          * (1f - veg01mc * 0.6f));

      if ((height > 0.78f && rock01mc > 0.60f) || b == Biome.MOUNTAIN) {
        g = TileIds.GROUND_ROCK;
      } else if ((path01mc > 0.68f && veg01mc < 0.62f) || (moist < 0.32f && veg01mc < 0.55f)) {
        g = TileIds.GROUND_DIRT;
      } else if (b == Biome.BEACH) {
        g = TileIds.GROUND_SAND;
      }
    }

    return g;
  }
  */
}


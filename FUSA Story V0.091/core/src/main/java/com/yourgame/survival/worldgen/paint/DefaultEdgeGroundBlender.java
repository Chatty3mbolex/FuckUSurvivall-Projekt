package com.yourgame.survival.worldgen.paint;

import com.yourgame.survival.world.Biome;
// Nicht fertiges Feature: // import com.yourgame.survival.world.TileIds; // (unused)
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.util.GenMath;

/** Default edge blender extracted from the legacy edge-ground override pass. */
public final class DefaultEdgeGroundBlender implements EdgeGroundBlender {
  private final NoiseSampler noise;
  private final BiomeClassifier classifier;

  public DefaultEdgeGroundBlender(NoiseSampler noise, BiomeClassifier classifier) {
    this.noise = noise;
    this.classifier = classifier;
  }

  @Override
  public void blend(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    final int size = l.size;

    for (int ly = 0; ly < size; ly++) {
      for (int lx = 0; lx < size; lx++) {
        final int idx = lx + ly * size;
        final int tx = cx * size + lx;
        final int ty = cy * size + ly;

        final Biome center = Biome.byId(l.biomeId[idx] & 0xff);

        // Skip edge blending inside the shore band (keeps coasts organic and avoids N/E/S/W bias artifacts)
        int wd = l.waterDist[idx] & 0xff;
        if (wd > 0 && wd <= 3) continue;

        int edgeW = Math.max(0, Math.min(size / 2, ctx.biomes.def(center).edgeWidthTiles));
        edgeW = Math.min(edgeW, Math.max(0, ctx.config.edgeWidthCapTiles));
        if (edgeW <= 0) continue;

        // Use global sampling so borders match deterministically.
        // Edge band: look outward up to edgeW tiles in stable order (N,E,S,W).
        Biome nb = null;
        for (int d = 1; d <= edgeW && nb == null; d++) {
          Biome n = biomeAtGlobalTile(tx, ty + d, ctx);
          if (n != center) { nb = n; break; }
        }
        for (int d = 1; d <= edgeW && nb == null; d++) {
          Biome e = biomeAtGlobalTile(tx + d, ty, ctx);
          if (e != center) { nb = e; break; }
        }
        for (int d = 1; d <= edgeW && nb == null; d++) {
          Biome s = biomeAtGlobalTile(tx, ty - d, ctx);
          if (s != center) { nb = s; break; }
        }
        for (int d = 1; d <= edgeW && nb == null; d++) {
          Biome w = biomeAtGlobalTile(tx - d, ty, ctx);
          if (w != center) { nb = w; break; }
        }

        if (nb != null) {
          // Strict shore: only allow water-edge overrides right next to water.
          if ((nb == Biome.WATER || nb == Biome.RIVERBANK) && ((l.waterDist[idx] & 0xff) > 1)) {
            nb = null;
          }
        }

        if (nb != null) {
          l.groundId[idx] = ctx.biomes.groundFor(center, nb, true);
        }

        // NOTE: shore materials already applied; we do not touch water tiles here.
        if (l.waterMask[idx] != 0) {
          // Keep water stable.
          l.groundId[idx] = l.groundId[idx];
        }
      }
    }
  }

  private Biome biomeAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);
    return classifier.classify(height, heat, moist);
  }
}

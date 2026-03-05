package com.yourgame.survival.worldgen.util;

import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;

/**
 * Global, deterministic sampling helpers for the FINAL intended worldgen result at a global tile (tx,ty).
 *
 * Purpose:
 * - make border sampling stable (no dependency on neighbor chunk existence)
 * - keep generator modules deterministic by (seed,tx,ty,config,biomeDefs)
 *
 * IMPORTANT: These helpers must stay in lockstep with the worldgen pipeline modules:
 * - DefaultTilePainter (base fields + micro-classify)
 * - DefaultWaterPostProcessor (smooth water + shore ring)
 * - DefaultEdgeGroundBlender (edge overrides)
 */
public final class FinalTileSampler {
  private FinalTileSampler() {}

  // ---- biome + water ----

  public static Biome biomeAt(final int tx, final int ty, final WorldGenContext ctx, final NoiseSampler noise, final BiomeClassifier classifier) {
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);
    return classifier.classify(height, heat, moist);
  }

  /** Base water decision (pre-smoothing): biome==WATER/RIVERBANK. */
  public static boolean baseWaterAt(final int tx, final int ty, final WorldGenContext ctx, final NoiseSampler noise, final BiomeClassifier classifier) {
    final Biome b = biomeAt(tx, ty, ctx, noise, classifier);
    return (b == Biome.WATER || b == Biome.RIVERBANK);
  }

  /**
   * Smooth water decision: 3x3 majority of baseWaterAt (>=5 of 9).
   * Must match DefaultWaterPostProcessor.smoothWaterAtGlobalTile().
   */
  public static boolean smoothWaterAt(final int tx, final int ty, final WorldGenContext ctx, final NoiseSampler noise, final BiomeClassifier classifier) {
    int c = 0;
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        if (baseWaterAt(tx + dx, ty + dy, ctx, noise, classifier)) c++;
      }
    }
    return c >= 5;
  }

  /**
   * Approximate distance-to-water for small radii using ONLY global sampling.
   * Returns: 0..maxR (Manhattan), or 255 if no water within radius.
   *
   * This intentionally avoids BFS and avoids any chunk dependencies.
   */
  public static int waterDistUpToR(final int tx, final int ty, final int maxR,
                                  final WorldGenContext ctx, final NoiseSampler noise, final BiomeClassifier classifier) {
    if (maxR <= 0) return smoothWaterAt(tx, ty, ctx, noise, classifier) ? 0 : 255;
    if (smoothWaterAt(tx, ty, ctx, noise, classifier)) return 0;

    int best = Integer.MAX_VALUE;
    for (int dy = -maxR; dy <= maxR; dy++) {
      for (int dx = -maxR; dx <= maxR; dx++) {
        int md = Math.abs(dx) + Math.abs(dy);
        if (md == 0 || md > maxR) continue;
        if (md >= best) continue;
        if (smoothWaterAt(tx + dx, ty + dy, ctx, noise, classifier)) {
          best = md;
        }
      }
    }
    return (best == Integer.MAX_VALUE) ? 255 : best;
  }

  // ---- final ground ----

  /**
   * Final intended ground id at global tile (tx,ty), including:
   * - biome default ground
   * - micro-classify overrides
   * - shore ring material (dist==1)
   * - edge blending overrides (per-biome edgeWidthTiles + edgeGroundOverride)
   */
  public static short finalGroundIdAt(final int tx, final int ty,
                                     final WorldGenContext ctx, final NoiseSampler noise, final BiomeClassifier classifier) {
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);

    final Biome b = classifier.classify(height, heat, moist);

    // Base ground from biome defs
    short g = ctx.biomes.def(b).groundId;

    final boolean waterBiome = (b == Biome.WATER || b == Biome.RIVERBANK);

    // Micro-classify inside land biomes (must mirror DefaultTilePainter)
    if (!waterBiome) {
      float veg01 = GenMath.clamp01(moist * (1f - GenMath.clamp01((height - 0.70f) * 2.2f))
          * (0.65f + 0.35f * noise.fbm01(ctx.seed ^ 0x55AA55AA55AA55AAL, tx, ty, ctx.config.vegFreq, ctx.config.vegOctaves)));

      float rock01 = GenMath.clamp01(GenMath.clamp01((height - 0.55f) * 1.6f)
          * (0.55f + 0.45f * noise.fbm01(ctx.seed ^ 0xCC33CC33CC33CC33L, tx, ty, ctx.config.rockFreq, ctx.config.rockOctaves)));

      float path01 = GenMath.clamp01(noise.fbm01(ctx.seed ^ 0x0F0E0D0C0B0A0908L, tx, ty, ctx.config.pathFreq, ctx.config.pathOctaves)
          * (1f - veg01 * 0.6f));

      if ((height > 0.78f && rock01 > 0.60f) || b == Biome.MOUNTAIN) {
        g = TileIds.GROUND_ROCK;
      } else if ((path01 > 0.68f && veg01 < 0.62f) || (moist < 0.32f && veg01 < 0.55f)) {
        g = TileIds.GROUND_DIRT;
      } else if (b == Biome.BEACH) {
        g = TileIds.GROUND_SAND;
      }
    }

    // Shore ring materials (must mirror DefaultWaterPostProcessor.applyShoreBandMaterials)
    int wd = waterDistUpToR(tx, ty, 3, ctx, noise, classifier);
    if (wd == 1) {
      g = TileIds.GROUND_DIRT;
    }

    // Edge blending intent (must mirror DefaultEdgeGroundBlender)
    // Skip inside shore band (wd=1..3). Water tiles are left alone anyway.
    if (!waterBiome && wd != 0 && wd <= 3) {
      return g;
    }

    int edgeW = Math.max(0, Math.min(32, ctx.biomes.def(b).edgeWidthTiles));
    edgeW = Math.min(edgeW, Math.max(0, ctx.config.edgeWidthCapTiles));
    if (edgeW <= 0) return g;

    Biome nb = null;
    for (int d = 1; d <= edgeW && nb == null; d++) {
      Biome n = biomeAt(tx, ty + d, ctx, noise, classifier);
      if (n != b) { nb = n; break; }
    }
    for (int d = 1; d <= edgeW && nb == null; d++) {
      Biome e = biomeAt(tx + d, ty, ctx, noise, classifier);
      if (e != b) { nb = e; break; }
    }
    for (int d = 1; d <= edgeW && nb == null; d++) {
      Biome s = biomeAt(tx, ty - d, ctx, noise, classifier);
      if (s != b) { nb = s; break; }
    }
    for (int d = 1; d <= edgeW && nb == null; d++) {
      Biome w = biomeAt(tx - d, ty, ctx, noise, classifier);
      if (w != b) { nb = w; break; }
    }

    if (nb != null) {
      // Strict shore: only allow water-edge overrides right next to water.
      if ((nb == Biome.WATER || nb == Biome.RIVERBANK) && wd > 1) {
        nb = null;
      }
    }

    if (nb != null) {
      g = ctx.biomes.groundFor(b, nb, true);
    }

    return g;
  }
}

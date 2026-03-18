package com.yourgame.survival.worldgen.deco;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.noise.FbmNoiseSampler;

/** Default deco scatter extracted from the legacy deco scatter pass. */
public final class DefaultDecoScatter implements DecoScatter {
  @Override
  public void scatter(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    // Purely visual decorations (deterministic): tufts/flowers near shore; small rocks in deep water.
    final int size = l.size;
    final int n = size * size;
    for (int idx = 0; idx < n; idx++) {
      l.decoId[idx] = 0;
      l.decoVar[idx] = 0;

      int lx = idx % size;
      int ly = idx / size;
      int tx = cx * size + lx;
      int ty = cy * size + ly;

      int wd = l.waterDist[idx] & 0xFF;
      int veg = l.vegetation[idx] & 0xFF;
      // Nicht fertiges Feature: // int rock = l.rockiness[idx] & 0xFF; // (unused)

      int var = (int) (hash01(ctx.seed ^ 0xABCDEF1122334455L, tx, ty) * 4f) & 3;
      float n1 = hash01(ctx.seed ^ 0x1234AA55CC33EE77L, tx, ty);

      // Editor-authored deco density mask (0..255); 255 = default, 0 = none.
      com.yourgame.survival.world.Biome b = com.yourgame.survival.world.Biome.byId(l.biomeId[idx]);
      int mv = ctx.biomes.decoMaskValueAt(b, lx, ly);
      if (mv <= 0) continue;
      float m01 = (mv & 0xFF) / 255f;
      n1 *= m01;

      // No deco in water (and no water rocks for now).
      if (l.waterMask[idx] != 0) {
        continue;
      }

      // No deco in lava biome.
      if (b == com.yourgame.survival.world.Biome.LAVA) {
        continue;
      }

      // No grass tufts/flowers on sand. (Later we can add dedicated BEACH deco here.)
      if ((l.groundId[idx] & 0xFF) == com.yourgame.survival.world.TileIds.GROUND_SAND) {
        continue;
      }

      // Grass tufts: act as "jitter" detail for grass areas.
      // - Dense near shore, but also sprinkled across grassland tiles.
      // - Deterministic from seed+tile.

      // shore band tufts/flowers
      if (wd >= 2 && wd <= 5) {
        if (veg > 160 && n1 > 0.70f) {
          l.decoId[idx] = 1; // tuft
          l.decoVar[idx] = (byte) var;
        } else if (veg > 120 && n1 > 0.82f) {
          l.decoId[idx] = 2; // flower
          l.decoVar[idx] = (byte) var;
        }
        continue;
      }

      // core grassland sprinkling (not on shore band)
      // Only on grass ground tiles; avoids dirt/sand/rock/snow clutter.
      if ((l.groundId[idx] & 0xFF) == com.yourgame.survival.world.TileIds.GROUND_GRASS) {
        // avoid roads (keeps paths readable)
        if (l.roadMask[idx] != 0) continue;
        // SAFTIGER: much higher density on grass tiles.
        // (veg gate keeps deserts clean; n1 is the random selector)
        if (veg > 90 && n1 > 0.55f) {
          l.decoId[idx] = 1; // tuft
          l.decoVar[idx] = (byte) var;
        } else if (veg > 120 && n1 > 0.90f) {
          l.decoId[idx] = 2; // flower
          l.decoVar[idx] = (byte) var;
        }
      }
    }
  }

  private static float hash01(long seed, int x, int y) {
    long h = seed;
    h ^= (long) x * 0x9E3779B97F4A7C15L;
    h ^= (long) y * 0xC2B2AE3D27D4EB4FL;
    h = FbmNoiseSampler.mix64(h);
    return ((h >>> 40) & 0xFFFFFF) / (float) 0x1000000;
  }
}

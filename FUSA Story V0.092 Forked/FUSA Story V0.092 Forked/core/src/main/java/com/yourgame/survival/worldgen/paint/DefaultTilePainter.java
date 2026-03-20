package com.yourgame.survival.worldgen.paint;

import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.util.GenMath;

/**
 * Default implementation extracted from the legacy tile-paint main loop.
 *
 * This module writes the base fields. Water smoothing / roads / adjacency / deco are done later.
 */
public final class DefaultTilePainter implements TilePainter {
  private final NoiseSampler noise;
  private final BiomeClassifier classifier;

  /** Editor-only: if set, force this biome for all tiles in generated chunks. */
  private final Biome forcedBiome;

  public DefaultTilePainter(NoiseSampler noise, BiomeClassifier classifier) {
    this(noise, classifier, null);
  }

  public DefaultTilePainter(NoiseSampler noise, BiomeClassifier classifier, Biome forcedBiome) {
    this.noise = noise;
    this.classifier = classifier;
    this.forcedBiome = forcedBiome;
  }

  @Override
  public void paint(TileLayers l, int cx, int cy, WorldGenContext ctx) {
    final int chunkSize = l.size;

    for (int y = 0; y < chunkSize; y++) {
      for (int x = 0; x < chunkSize; x++) {
        int idx = x + y * chunkSize;

        int tx = cx * chunkSize + x;
        int ty = cy * chunkSize + y;

        // Coherent fields (fbm)
        float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
        float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
        float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);

        int h = (int) (height * 255f);
        int ht = (int) (heat * 255f);
        int m = (int) (moist * 255f);

        l.height[idx] = (byte) h;
        l.heat[idx] = (byte) ht;
        l.moisture[idx] = (byte) m;

        // Extra fields for nicer worldgen + spawns (0..1)
        float veg01 = GenMath.clamp01(moist * (1f - GenMath.clamp01((height - 0.70f) * 2.2f))
            * (0.65f + 0.35f * noise.fbm01(ctx.seed ^ 0x55AA55AA55AA55AAL, tx, ty, ctx.config.vegFreq, ctx.config.vegOctaves)));

        float rock01 = GenMath.clamp01(GenMath.clamp01((height - 0.55f) * 1.6f)
            * (0.55f + 0.45f * noise.fbm01(ctx.seed ^ 0xCC33CC33CC33CC33L, tx, ty, ctx.config.rockFreq, ctx.config.rockOctaves)));

        float path01 = GenMath.clamp01(noise.fbm01(ctx.seed ^ 0x0F0E0D0C0B0A0908L, tx, ty, ctx.config.pathFreq, ctx.config.pathOctaves)
            * (1f - veg01 * 0.6f));

        l.vegetation[idx] = (byte) (veg01 * 255f);
        l.rockiness[idx] = (byte) (rock01 * 255f);
        l.pathField[idx] = (byte) (path01 * 255f);

        Biome b = (forcedBiome != null) ? forcedBiome : classifier.classify(height, heat, moist);
        l.biomeId[idx] = b.id;

        // Terraced height: from per-biome height mask (0..15). Defaults to 0 when no mask is configured.
        l.heightLevel[idx] = (byte) ctx.biomes.heightMaskLevelAt(b, x, y);

        // Ground tile id from BiomeSystem (editor controlled)
        l.groundId[idx] = ctx.biomes.def(b).groundId;

        boolean water = (b == Biome.WATER || b == Biome.RIVERBANK);

        // micro-classify inside land biomes (only when NOT forcing biome in editor)
        if (forcedBiome == null && !water) {
          float veg01mc = (l.vegetation[idx] & 0xFF) / 255f;
          float rock01mc = (l.rockiness[idx] & 0xFF) / 255f;
          float path01mc = (l.pathField[idx] & 0xFF) / 255f;
          if ((height > 0.78f && rock01mc > 0.60f) || b == Biome.MOUNTAIN) {
            l.groundId[idx] = TileIds.GROUND_ROCK;
          } else if ((path01mc > 0.68f && veg01mc < 0.62f) || (moist < 0.32f && veg01mc < 0.55f)) {
            l.groundId[idx] = TileIds.GROUND_DIRT;
          } else if (b == Biome.BEACH) {
            l.groundId[idx] = TileIds.GROUND_SAND;
          }
        }

        // Enforce height only on allowed ground types.
        int gid = l.groundId[idx] & 0xFF;
        boolean allowH = (gid == TileIds.GROUND_GRASS
            || gid == TileIds.GROUND_DIRT
            || gid == TileIds.GROUND_ROCK
            || gid == TileIds.GROUND_SNOW);
        if (!allowH) l.heightLevel[idx] = 0;

        l.waterMask[idx] = (byte) (water ? 1 : 0);
        l.collisionMask[idx] = (byte) ((b == Biome.WATER || b == Biome.LAVA) ? 1 : 0);

        // Overlay id (renderer is null-safe until NODE_60_ASSETS maps real tiles)
        float vegOv = (l.vegetation[idx] & 0xFF) / 255f;
        float rockOv = (l.rockiness[idx] & 0xFF) / 255f;
        float pathOv = (l.pathField[idx] & 0xFF) / 255f;
        byte ov = 0;
        if (!water) {
          if (rockOv > 0.55f) ov = 3;
          else if (vegOv > 0.75f && pathOv < 0.40f) ov = 2;
          else if (vegOv > 0.55f && pathOv < 0.55f) ov = 1;
        }
        l.overlayId[idx] = ov;

        // roadMask is filled later by RoadPlanner
        l.roadMask[idx] = 0;

        // Initialize other masks here to avoid any accidental uninitialized usage.
        l.shoreMask4[idx] = 0;
        l.roadMask4[idx] = 0;
        l.grassCornerMask16[idx] = 0;
        l.dirtCornerMask16[idx] = 0;
        l.sandCornerMask16[idx] = 0;
        l.rockCornerMask16[idx] = 0;
        l.snowCornerMask16[idx] = 0;
        l.decoId[idx] = 0;
        l.decoVar[idx] = 0;
        l.waterDist[idx] = (byte) 0xFF;
      }
    }
  }

  /** Expose forced biome for orchestration decisions (optional). */
  public Biome forcedBiome() {
    return forcedBiome;
  }
}

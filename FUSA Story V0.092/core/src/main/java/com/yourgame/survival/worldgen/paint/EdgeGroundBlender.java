package com.yourgame.survival.worldgen.paint;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;

/** Visual-only edge blending for ground tiles based on adjacent biome (post-waterDist). */
public interface EdgeGroundBlender {
  void blend(TileLayers layers, int cx, int cy, WorldGenContext ctx);
}

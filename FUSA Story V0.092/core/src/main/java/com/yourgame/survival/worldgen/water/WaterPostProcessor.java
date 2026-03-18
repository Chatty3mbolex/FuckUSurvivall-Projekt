package com.yourgame.survival.worldgen.water;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;

/** Post-process water: smooth water mask, compute water distance fields, apply shore materials. */
public interface WaterPostProcessor {
  void process(TileLayers layers, int cx, int cy, WorldGenContext ctx);
}

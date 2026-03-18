package com.yourgame.survival.worldgen.deco;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;

/** Writes purely visual decorations (decoId/decoVar). */
public interface DecoScatter {
  void scatter(TileLayers layers, int cx, int cy, WorldGenContext ctx);
}

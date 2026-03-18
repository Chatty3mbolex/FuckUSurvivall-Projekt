package com.yourgame.survival.worldgen.paint;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;

/** Base worldgen pass: writes climate/derived fields, biomeId, groundId, and initial masks. */
public interface TilePainter {
  void paint(TileLayers layers, int cx, int cy, WorldGenContext ctx);
}

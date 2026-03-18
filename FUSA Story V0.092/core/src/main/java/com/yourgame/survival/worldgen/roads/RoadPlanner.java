package com.yourgame.survival.worldgen.roads;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;

/** Computes roadMask (0/1) for a chunk. */
public interface RoadPlanner {
  void buildRoadMask(TileLayers layers, int cx, int cy, WorldGenContext ctx);
}

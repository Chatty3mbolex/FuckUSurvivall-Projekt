package com.yourgame.survival.worldgen.adjacency;

import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.worldgen.WorldGenContext;

/** Builds adjacency masks (shoreMask4/roadMask4 + dirt/sand edge masks). */
public interface AdjacencyBuilder {
  void build(TileLayers layers, int cx, int cy, WorldGenContext ctx);
}

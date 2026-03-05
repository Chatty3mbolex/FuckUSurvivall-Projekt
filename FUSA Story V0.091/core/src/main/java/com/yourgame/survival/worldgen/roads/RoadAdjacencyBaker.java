package com.yourgame.survival.worldgen.roads;

import com.yourgame.survival.world.TileLayers;
// Nicht fertiges Feature: // import com.yourgame.survival.world.World; // (unused)
import com.yourgame.survival.worldgen.WorldGenContext;

/**
 * Bakes 4-neighbor road adjacency (bit0=N,bit1=E,bit2=S,bit3=W) into TileLayers.roadMask4.
 *
 * This is computed at generation time to keep rendering fast and border-stable.
 */
public final class RoadAdjacencyBaker {
  private final WorldRoadPlanner planner;

  // Reused neighbor masks to avoid per-chunk allocations.
  private byte[] nMask;
  private byte[] eMask;
  private byte[] sMask;
  private byte[] wMask;

  public RoadAdjacencyBaker(WorldRoadPlanner planner) {
    this.planner = planner;
  }

  private void ensureCapacity(int n) {
    if (nMask == null || nMask.length < n) nMask = new byte[n];
    if (eMask == null || eMask.length < n) eMask = new byte[n];
    if (sMask == null || sMask.length < n) sMask = new byte[n];
    if (wMask == null || wMask.length < n) wMask = new byte[n];
  }

  public void bake(final TileLayers l, final int cx, final int cy, final WorldGenContext ctx) {
    final int size = l.size;

    // Build neighbor road masks once (deterministic, independent of chunk load order).
    ensureCapacity(size * size);

    planner.buildRoadMask(nMask, cx, cy + 1, ctx);
    planner.buildRoadMask(eMask, cx + 1, cy, ctx);
    planner.buildRoadMask(sMask, cx, cy - 1, ctx);
    planner.buildRoadMask(wMask, cx - 1, cy, ctx);

    final byte[] self = l.roadMask;

    for (int ly = 0; ly < size; ly++) {
      for (int lx = 0; lx < size; lx++) {
        final int idx = lx + ly * size;

        boolean n = (ly < size - 1) ? (self[idx + size] != 0) : (nMask[lx + 0 * size] != 0);
        boolean e = (lx < size - 1) ? (self[idx + 1] != 0) : (eMask[0 + ly * size] != 0);
        boolean s = (ly > 0) ? (self[idx - size] != 0) : (sMask[lx + (size - 1) * size] != 0);
        boolean w = (lx > 0) ? (self[idx - 1] != 0) : (wMask[(size - 1) + ly * size] != 0);

        l.roadMask4[idx] = (byte) ((n ? 1 : 0) | (e ? 2 : 0) | (s ? 4 : 0) | (w ? 8 : 0));
      }
    }
  }
}

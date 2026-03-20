package com.yourgame.survival.worldmap;

import com.yourgame.survival.screens.AreaHost;

/**
 * Thin glue layer: WorldMapRuntime (state/rules) + AreaWorldLoader (world loading).
 *
 * Design decision:
 * - Keep this out of GameScreen so the travel logic stays testable and self-contained.
 * - GameScreen should call one method like "travel(dir)" and not care about internals.
 */
public final class WorldMapTravelController {
  private final WorldMapRuntime rt;
  private final AreaWorldLoader loader;

  public WorldMapTravelController(WorldMapRuntime rt, AreaWorldLoader loader) {
    this.rt = rt;
    this.loader = loader;
  }

  /**
   * Attempts to travel from the current area to a neighbor.
   *
   * Important:
   * - This mutates the WorldMapState (discover/enter).
   * - Then it invokes the loader to apply the chosen template to the actual game world.
   */
  public AreaTravelResult travel(AreaHost gs, WorldMapState st, Dir4 dir, long seed) {
    if (rt == null || loader == null || st == null) return AreaTravelResult.invalid();

    // First: ask the runtime to discover/enter.
    String tid = rt.discoverOrEnterNeighbor(st, dir, seed);
    if (tid == null) {
      // Two possible causes in our current runtime:
      // 1) no exit in that direction
      // 2) invalid placement (against-check failed)
      //
      // Decision:
      // - We keep the API simple for scaffolding. If we need better UI feedback later,
      //   we will extend WorldMapRuntime to return a richer result.
      //
      // For now we return INVALID_PLACEMENT for both cases.
      // If we need to distinguish NO_EXIT later, we will return a richer result from WorldMapRuntime.
      return AreaTravelResult.invalid();
    }

    // Second: load the template into the runtime world.
    boolean ok = loader.loadInto(gs, tid, dir);
    if (!ok) return AreaTravelResult.loadFailed(tid);

    return AreaTravelResult.ok(tid);
  }
}

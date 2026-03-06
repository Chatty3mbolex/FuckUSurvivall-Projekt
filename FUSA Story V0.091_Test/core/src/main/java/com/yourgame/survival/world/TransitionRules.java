package com.yourgame.survival.world;

/**
 * Centralized allow-matrix for ground transitions (overlay/edge rules).
 *
 * IMPORTANT: This must stay consistent across worldgen mask baking, editor rebake and rendering.
 */
public final class TransitionRules {
  private TransitionRules() {}

  /** Returns true if an overlay of targetGroundId may appear on baseGroundId. */
  public static boolean allowTransition(final short baseGroundId, final short targetGroundId) {
    if (baseGroundId == targetGroundId) return false;

    return switch (baseGroundId) {
      // Grass may appear on dirt, but dirt must NOT appear on grass.
      // So: allow GRASS -> (SAND/SNOW/ROCK) but NOT -> DIRT.
      case TileIds.GROUND_GRASS -> (targetGroundId == TileIds.GROUND_SAND
          || targetGroundId == TileIds.GROUND_SNOW
          || targetGroundId == TileIds.GROUND_ROCK);

      case TileIds.GROUND_DIRT -> (targetGroundId == TileIds.GROUND_SAND
          || targetGroundId == TileIds.GROUND_GRASS
          || targetGroundId == TileIds.GROUND_SNOW
          || targetGroundId == TileIds.GROUND_ROCK);

      // Sand rule: sand may appear on top of everything, but NOTHING may appear on sand.
      case TileIds.GROUND_SAND -> false;

      // Snow must NOT border sand (no beach snow).
      case TileIds.GROUND_SNOW -> (targetGroundId == TileIds.GROUND_GRASS
          || targetGroundId == TileIds.GROUND_DIRT
          || targetGroundId == TileIds.GROUND_ROCK);

      case TileIds.GROUND_ROCK -> (targetGroundId == TileIds.GROUND_SAND
          || targetGroundId == TileIds.GROUND_DIRT
          || targetGroundId == TileIds.GROUND_SNOW
          || targetGroundId == TileIds.GROUND_ROCK);

      default -> true;
    };
  }
}

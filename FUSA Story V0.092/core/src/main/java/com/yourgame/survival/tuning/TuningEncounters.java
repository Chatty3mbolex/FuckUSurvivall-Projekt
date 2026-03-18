package com.yourgame.survival.tuning;

/**
 * Encounter/spawn tuning values (FIXED numbers).
 *
 * Goal:
 * - Keep spawn caps, pack parameters, and distance constraints in ONE place.
 * - Use fixed WORLD-UNIT numbers (no World.TILE_WORLD multiplications inside gameplay code).
 */
public final class TuningEncounters {
  private TuningEncounters() {}

  // Initial encounter targets (start-of-game)
  public static final int INITIAL_ORC_CAP = 25;
  public static final int INITIAL_DEER_CAP = 15;

  // Distances (WORLD UNITS). World.TILE_WORLD is 16f in live code.
  // 10 tiles => 160
  public static final float ENCOUNTER_MIN_PLAYER_DIST_WU = 160f;
  // 4 tiles => 64
  public static final float ENCOUNTER_MIN_ENTITY_DIST_WU = 64f;

  // Ork pack spawn (WORLD UNITS)
  public static final int ORK_PACK_MIN = 3;
  public static final int ORK_PACK_MAX = 6;
  // 2.5 tiles => 40
  public static final float ORK_PACK_RADIUS_WU = 40f;
  // 1.2 tiles => 19.2
  public static final float ORK_PACK_MIN_DIST_WU = 19.2f;
}

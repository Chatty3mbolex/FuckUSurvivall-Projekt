package com.yourgame.survival.tuning;

/**
 * Core gameplay tuning values (FIXED numbers).
 *
 * Goal:
 * - Put all "feel" numbers (DPS, reach, FOV, zoom, UI font scale) into ONE file.
 * - Values are FIXED numbers so you can change them directly.
 */
public final class TuningGameplay {
  private TuningGameplay() {}

  // Harvest DPS (from live code)
  public static final float DPS_TREE_AXE = 18f;
  public static final float DPS_TREE_OTHER_BLADE = 8f;
  public static final float DPS_ROCK_PICKAXE = 20f;
  public static final float DPS_ORE_PICKAXE = 18f;

  // Action reach / FOV (from GameScreen live code)
  public static final float REACH_HARVEST = 40f;
  public static final float REACH_COMBAT = 60f;
  public static final float REACH_PICKUP = 18f;
  public static final float REACH_BUILD_REMOVE = 120f;
  public static final float REACH_SHOP = 120f;
  public static final float REACH_BED = 140f;

  public static final float ACTION_FOV_DEG = 84f;
  public static final float HAND_SWING_DUR = 0.18f;
  public static final float ACTION_RADIUS_DEFAULT = 30f;

  // Chest open interaction range (should be around the silhouette, not a huge radius)
  public static final float CHEST_OPEN_RANGE_WU = 24f;

  // UI/Camera zoom (from GameScreen live code)
  public static final float UI_FONT_SCALE = 2.0f;

  public static final float ZOOM_DEFAULT = 0.40f;
  public static final float ZOOM_MIN = 0.20f;
  // Was formula in code: ZOOM_DEFAULT * 0.35 => 0.14 (fixed number per requirement)
  public static final float ZOOM_MAX = 0.14f;
  public static final float ZOOM_STEP_WHEEL = 0.06f;
  public static final float ZOOM_STEP_KEYS = 0.08f;
}

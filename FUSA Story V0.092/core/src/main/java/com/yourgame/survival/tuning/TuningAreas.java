package com.yourgame.survival.tuning;

/**
 * Area/WorldMap loader tuning (FIXED numbers).
 *
 * Goal:
 * - Keep area size and area-specific populations in ONE place.
 */
public final class TuningAreas {
  private TuningAreas() {}

  // ============================================================
  // Area geometry (FIXED numbers)
  // ============================================================
  // Requirement: one Area = 6x6 chunks.
  // Chunk size is 64x64 tiles => Area = 384x384 tiles.
  public static final int AREA_CHUNKS_W = 6;
  public static final int AREA_CHUNKS_H = 6;

  // Backward-compat alias used by earlier scaffolding (keep, but fixed to the new spec).
  public static final int AREA_W = 384;
  public static final int AREA_H = 384;

  // Canonical tile dimensions for the new Areas system.
  public static final int AREA_W_TILES = 384;
  public static final int AREA_H_TILES = 384;

  // Red warning zone thickness along the Area bounds.
  public static final int RED_ZONE_TILES = 10;

  // VOID strip width outside the area bounds (black region).
  // Player can step into this strip, but cannot leave it further without traveling.
  public static final int VOID_STRIP_TILES = 20;

  // Void penalty (player outside the area bounds)
  public static final float VOID_TIMER_SEC = 60f;
  public static final float VOID_HP_PENALTY_MUL = 0.5f;

  // ============================================================
  // Fog of War (MAP only)
  // ============================================================
  // Minimap downsample scale for both War-Stand and Live map.
  public static final int MAP_MINI_SCALE = 4;

  // FoW alpha map resolution (tiles per fog cell). Lower = smoother but heavier.
  // 2 => 192x192 for a 384x384 area.
  public static final int FOW_ALPHA_SCALE_TILES = 2;

  // Persistent reveal update interval while playing.
  public static final float FOW_REVEAL_STEP_SEC = 0.25f;

  // Persistent explored radius around the player while moving/seeing (in tiles).
  // This is what stays explored forever once visited.
  public static final int FOW_EXPLORE_RADIUS_TILES = 8;

  // Soft edge (falloff) width for the permanent explore brush (in tiles).
  public static final int FOW_EXPLORE_FALLOFF_TILES = 4;

  // Visible radius = action ring radius + this margin.
  public static final int FOW_VISIBLE_MARGIN_TILES = 6;

  // Fog overlay alpha for "explored but not currently visible".
  // Requested: explored should stay visible with only a light translucent fog.
  public static final float FOW_EXPLORED_ALPHA = 0.35f;

  // Focus/visibility shape tuning (tiles).
  // Visible window is an oval that extends forward more than sideways.
  // It starts at the player's back (0 behind) and ends a bit before the max reach.
  public static final int FOW_CLEAR_CUTOFF_FORWARD_TILES = 2;
  public static final float FOW_CLEAR_SIDE_MUL = 0.55f;

  // Explored (permanent discovery) is also directional, but tighter than the clear window.
  public static final float FOW_EXPLORE_FORWARD_MUL = 0.55f;
  public static final float FOW_EXPLORE_SIDE_MUL = 0.65f;

  // Soft edge fade width (in tiles) at the visible boundary.
  public static final int FOW_EDGE_FADE_TILES = 8;

  // ============================================================
  // Forest content (GEN_LIGHT_FOREST)
  // ============================================================
  public static final int FOREST_TREE_STEP_TILES = 5;
  public static final int FOREST_DEER_TARGET = 40;

  // 4 tiles * 16 = 64 world units
  public static final float FOREST_DEER_MIN_DIST_WU = 64f;

  // Deer must not spawn inside the tree trunk collider.
  // Fixed world-unit distance between deer center and tree center used at spawn time.
  public static final float FOREST_DEER_MIN_TREE_DIST_WU = 16f;
}

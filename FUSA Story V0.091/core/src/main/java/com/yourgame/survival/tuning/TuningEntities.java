package com.yourgame.survival.tuning;

/**
 * Entity tuning values (FIXED numbers).
 *
 * Goal:
 * - All entity-related sizes and hit/collision tuning live in ONE place.
 * - Values are FIXED numbers (no formulas/multipliers), so you can change them directly.
 */
public final class TuningEntities {
  private TuningEntities() {}

  // ------------------------------------------------------------
  // Draw sizes (pixels / world units) – copied from the current live code
  // ------------------------------------------------------------

  public static final float DRAW_W_PLAYER = 34f;
  public static final float DRAW_W_ORK_GRUNT = 34f;
  public static final float DRAW_W_ANIMAL_DEER = 30f;
  public static final float DRAW_W_MERCHANT = 34f;
  public static final float DRAW_W_ITEM_DROP = 16f;

  public static final float DRAW_W_NODE_TREE = 72f;
  public static final float DRAW_H_NODE_TREE = 96f;

  // Stump draw size (world units ~= pixels at current art scale).
  // Requested: reduce by 60% from the previous tuned size.
  public static final float DRAW_W_NODE_STUMP = 16.64f;
  public static final float DRAW_H_NODE_STUMP = 15.36f;

  public static final float DRAW_W_NODE_ROCK = 30f;
  public static final float DRAW_W_NODE_ORE_IRON = 30f;
  public static final float DRAW_W_NODE_BUSH = 34f;
  public static final float DRAW_W_NODE_FISH_SPOT = 34f;

  public static final float DRAW_W_BUILD_CHEST = 30f;
  public static final float DRAW_W_BUILD_WORKBENCH = 34f;
  public static final float DRAW_W_BUILD_BED = 21f;
  public static final float DRAW_W_BUILD_CAMPFIRE = 30f;
  public static final float DRAW_W_BUILD_LAMP = 26f;
  public static final float DRAW_H_BUILD_LAMP = 40f;

  // Landmarks (HOME): big static sprites (world units ~= pixels at current art scale)
  public static final float DRAW_W_LANDMARK_CASTLE = 576f;
  public static final float DRAW_H_LANDMARK_CASTLE = 476f;
  public static final float DRAW_W_LANDMARK_BRIDGE = 463f;
  public static final float DRAW_H_LANDMARK_BRIDGE = 197f;

  // ------------------------------------------------------------
  // Collision / Hit radii (tight per-sprite tuning)
  // ------------------------------------------------------------

  public static final float COLLISION_RADIUS_PLAYER = 6.7f;
  public static final float COLLISION_RADIUS_DEER = 6.4f;
  public static final float COLLISION_RADIUS_ORK = 6.7f;

  public static final float HIT_RADIUS_PLAYER = 9.8f;
  public static final float HIT_RADIUS_DEER = 9.5f;
  public static final float HIT_RADIUS_ORK = 9.8f;

  // ------------------------------------------------------------
  // Tree trunk collider + harvest tuning (requested adjustments)
  // ------------------------------------------------------------

  // Tree trunk collider center offset from sprite bottom (px).
  // Requested: collision box up by ~10px (relative to the previous tuned state).
  // Old tuned state: bottom+12. New: bottom+22.
  public static final float TREE_TRUNK_CENTER_FROM_BOTTOM_PX = 22f;

  // Requested: tree collider about 10% smaller.
  // Old baseline trunkHalf: 6f*0.85f = 5.10 -> 10% smaller => 4.59.
  public static final float TREE_TRUNK_HALF = 4.59f;

  // Harvest: allow slightly higher hits again (was tuned too low).
  // Old tuned state: -20px. New: -10px (10px higher).
  public static final float TREE_HARVEST_BELOW_EXTRA_Y_PX = -10f;

  // Harvest: make the trunk hit window larger again so trees are easier to chop.
  // Baseline was ~25.20 (72*0.35). We go slightly above baseline for feel.
  public static final float TREE_HARVEST_NEAR_TRUNK_HALF_W = 28.0f;
}

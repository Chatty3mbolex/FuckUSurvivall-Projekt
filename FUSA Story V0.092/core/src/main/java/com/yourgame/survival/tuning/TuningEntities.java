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
  // Draw sizes (world units)
  //
  // Reference scale: Player sprite is 64x64 px, drawn at 34 WU.
  // Ratio: ~0.53 WU per pixel. All sizes derived from actual sprite
  // dimensions using this ratio to maintain consistent proportions.
  //
  // Confirmed good: Player (34), Ork (34), Tree (72x96).
  // ------------------------------------------------------------

  public static final float DRAW_W_PLAYER = 34f;
  public static final float DRAW_W_ORK_GRUNT = 34f;
  public static final float DRAW_W_ANIMAL_DEER = 17f;   // was 30 — 32px sprite, deer is half player size
  public static final float DRAW_W_ANIMAL_CHICKEN = 17f; // start with deer scale; adjust once chicken art is final
  public static final float DRAW_W_MERCHANT = 34f;
  public static final float DRAW_W_WANDER_QUEST_GUY = 34f; // placeholder: reuse merchant scale until custom art exists
  public static final float DRAW_W_ITEM_DROP = 14f;     // was 16 — small ground item

  public static final float DRAW_W_NODE_TREE = 72f;
  public static final float DRAW_H_NODE_TREE = 96f;

  // Stump: 55x48 canvas, but only 48x36 visible (35% transparent border) → ~24x18 WU
  public static final float DRAW_W_NODE_STUMP = 24f;
  public static final float DRAW_H_NODE_STUMP = 18f;

  // Rock: 77x33 px → ~39x17 WU (wide and flat, not square)
  public static final float DRAW_W_NODE_ROCK = 39f;
  public static final float DRAW_H_NODE_ROCK = 17f;

  // Iron ore: 67x39 px → ~34x20 WU (wide and flat)
  public static final float DRAW_W_NODE_ORE_IRON = 34f;
  public static final float DRAW_H_NODE_ORE_IRON = 20f;

  // Bush: 48x48 px → ~25x25 WU (smaller than player)
  public static final float DRAW_W_NODE_BUSH = 25f;

  // Fish spot: 32x32 px → ~16x16 WU (one tile)
  public static final float DRAW_W_NODE_FISH_SPOT = 16f;

  // Chest: 48x24 px → ~24x12 WU (wide and low)
  public static final float DRAW_W_BUILD_CHEST = 24f;
  public static final float DRAW_H_BUILD_CHEST = 12f;

  // Workbench: 32x27 px → ~16x14 WU (one tile, compact)
  public static final float DRAW_W_BUILD_WORKBENCH = 16f;
  public static final float DRAW_H_BUILD_WORKBENCH = 14f;

  // Bed: 75x103 canvas, but only 48x83 visible (48% transparent border) → ~24x42 WU
  public static final float DRAW_W_BUILD_BED = 24f;
  public static final float DRAW_H_BUILD_BED = 42f;

  // Campfire: 32x19 px → ~16x10 WU (small, flat)
  public static final float DRAW_W_BUILD_CAMPFIRE = 16f;
  public static final float DRAW_H_BUILD_CAMPFIRE = 10f;

  // Lamp: 32x148 px → ~16x74 WU (thin and VERY tall — it's a lamp post!)
  public static final float DRAW_W_BUILD_LAMP = 16f;
  public static final float DRAW_H_BUILD_LAMP = 74f;

  // Landmarks (HOME): large sprites, scaled at ~0.50 WU/px (slightly smaller
  // than entity scale so they don't overwhelm the area)
  public static final float DRAW_W_LANDMARK_CASTLE = 450f;   // was 576
  public static final float DRAW_H_LANDMARK_CASTLE = 372f;   // was 476
  public static final float DRAW_W_LANDMARK_BRIDGE = 362f;   // was 463
  public static final float DRAW_H_LANDMARK_BRIDGE = 154f;   // was 197

  // ------------------------------------------------------------
  // Collision / Hit radii (tight per-sprite tuning)
  // ------------------------------------------------------------

  public static final float COLLISION_RADIUS_PLAYER = 6.7f;
  public static final float COLLISION_RADIUS_DEER = 3.6f;    // was 6.4 — scaled to new smaller deer
  public static final float COLLISION_RADIUS_CHICKEN = 3.6f; // reuse deer tuning for now
  public static final float COLLISION_RADIUS_ORK = 6.7f;

  public static final float HIT_RADIUS_PLAYER = 9.8f;
  public static final float HIT_RADIUS_DEER = 5.4f;          // was 9.5 — scaled to new smaller deer
  public static final float HIT_RADIUS_CHICKEN = 5.4f;        // reuse deer tuning for now
  public static final float HIT_RADIUS_ORK = 9.8f;

  // ------------------------------------------------------------
  // Tree trunk collider + harvest tuning (requested adjustments)
  // ------------------------------------------------------------

  // Tree trunk collider center offset from sprite bottom (px).
  // Requested: collision box up by ~10px (relative to the previous tuned state).
  // Old tuned state: bottom+12. New: bottom+22.
  // Update (2026-03-09): move trunk collision down by 6px total => bottom+16.
  public static final float TREE_TRUNK_CENTER_FROM_BOTTOM_PX = 16f;

  // Requested: tree collider about 10% smaller.
  // Old baseline trunkHalf: 6f*0.85f = 5.10 -> 10% smaller => 4.59.
  public static final float TREE_TRUNK_HALF = 4.59f;

  // Harvest: allow slightly higher hits again (was tuned too low).
  // Old tuned state: -20px. New: -10px (10px higher).
  public static final float TREE_HARVEST_BELOW_EXTRA_Y_PX = -10f;

  // Harvest: make the trunk hit window larger again so trees are easier to chop.
  // Baseline was ~25.20 (72*0.35). We go slightly above baseline for feel.
  public static final float TREE_HARVEST_NEAR_TRUNK_HALF_W = 28.0f;

  // ------------------------------------------------------------
  // Stump collider (tile-trees)
  // ------------------------------------------------------------

  // Stump collider center offset from sprite bottom (px).
  // Tuned so the collider hugs the visible stump silhouette without shifting the sprite.
  public static final float STUMP_COLLIDER_CENTER_FROM_BOTTOM_PX = 10f;

  // Stump collider half-extents (AABB). Sized to approximate the stump silhouette.
  // Reduced by 2f in each axis (requested): tighter stump collision.
  public static final float STUMP_COLLIDER_HALF_W = 6.8f;
  public static final float STUMP_COLLIDER_HALF_H = 4.8f;
}

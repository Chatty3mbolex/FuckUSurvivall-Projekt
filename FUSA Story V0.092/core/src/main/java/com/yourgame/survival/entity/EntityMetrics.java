package com.yourgame.survival.entity;

/** Centralized draw sizes + interaction/hit radii for entities. */
public final class EntityMetrics {
  private EntityMetrics() {}

  public static float drawW(EntityType t) {
    return switch (t) {
      case PLAYER -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_PLAYER;
      case ORK_GRUNT -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_ORK_GRUNT;
      case ANIMAL_DEER -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_ANIMAL_DEER;
      case MERCHANT_ELF, MERCHANT_WANDERING -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_MERCHANT;
      case WANDER_QUEST_GUY -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_WANDER_QUEST_GUY;
      case ITEM_DROP -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_ITEM_DROP;

      case NODE_TREE -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_NODE_TREE;
      case NODE_STUMP -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_NODE_STUMP;
      case NODE_ROCK -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_NODE_ROCK;
      case NODE_ORE_IRON -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_NODE_ORE_IRON;
      case NODE_BUSH -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_NODE_BUSH;
      case NODE_FISH_SPOT -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_NODE_FISH_SPOT;

      case BUILD_CHEST, POI_CHEST_HIDDEN -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_BUILD_CHEST;
      case BUILD_WORKBENCH -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_BUILD_WORKBENCH;
      case BUILD_BED -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_BUILD_BED;
      case BUILD_CAMPFIRE -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_BUILD_CAMPFIRE;
      case BUILD_LAMP -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_BUILD_LAMP;

      case LANDMARK_CASTLE -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_LANDMARK_CASTLE;
      case LANDMARK_BRIDGE -> com.yourgame.survival.tuning.TuningEntities.DRAW_W_LANDMARK_BRIDGE;
    };
  }

  public static float drawH(EntityType t) {
    return switch (t) {
      case NODE_TREE -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_NODE_TREE;
      case NODE_STUMP -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_NODE_STUMP;
      case NODE_ROCK -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_NODE_ROCK;
      case NODE_ORE_IRON -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_NODE_ORE_IRON;
      case BUILD_CHEST, POI_CHEST_HIDDEN -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_BUILD_CHEST;
      case BUILD_WORKBENCH -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_BUILD_WORKBENCH;
      case BUILD_BED -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_BUILD_BED;
      case BUILD_CAMPFIRE -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_BUILD_CAMPFIRE;
      case BUILD_LAMP -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_BUILD_LAMP;

      case LANDMARK_CASTLE -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_LANDMARK_CASTLE;
      case LANDMARK_BRIDGE -> com.yourgame.survival.tuning.TuningEntities.DRAW_H_LANDMARK_BRIDGE;

      default -> drawW(t);
    };
  }

  /**
   * Interaction radius in world units.
   * Slightly smaller than the sprite extents, so "reach" isn't overly generous.
   */
  /**
   * Interaction radius in world units.
   * Slightly smaller than the sprite extents, so "reach" isn't overly generous.
   */
  public static float radius(EntityType t) {
    float w = drawW(t);
    float h = drawH(t);
    float halfMax = Math.max(w, h) * 0.5f;
    return halfMax * 0.24f;
  }

  /**
   * Collision/body radius (used for entity-vs-entity separation).
   * Tight: should fit inside the visible body (no "floating" collisions), but still prevent overlaps.
   */
  public static float collisionRadius(EntityType t) {
    return switch (t) {
      // Tight per-sprite tuning for the current art.
      case ORK_GRUNT -> com.yourgame.survival.tuning.TuningEntities.COLLISION_RADIUS_ORK;
      case ANIMAL_DEER -> com.yourgame.survival.tuning.TuningEntities.COLLISION_RADIUS_DEER;
      case PLAYER -> com.yourgame.survival.tuning.TuningEntities.COLLISION_RADIUS_PLAYER;

      // Landmarks are purely visual (no entity-vs-entity physics).
      case LANDMARK_CASTLE, LANDMARK_BRIDGE -> 0f;

      default -> {
        // Keep derived fallback for new entity types.
        // (If you want this fully fixed too, we can add explicit radii per type.)
        float w = drawW(t);
        float h = drawH(t);
        float halfMin = Math.min(w, h) * 0.5f;
        yield halfMin * 0.39f;
      }
    };
  }

  /**
   * Combat hit radius (used for melee + projectiles).
   * Tight: close to the body silhouette (slightly more forgiving than collision radius).
   */
  public static float hitRadius(EntityType t) {
    return switch (t) {
      // Tight per-sprite tuning for the current art.
      case ORK_GRUNT -> com.yourgame.survival.tuning.TuningEntities.HIT_RADIUS_ORK;
      case ANIMAL_DEER -> com.yourgame.survival.tuning.TuningEntities.HIT_RADIUS_DEER;
      case PLAYER -> com.yourgame.survival.tuning.TuningEntities.HIT_RADIUS_PLAYER;

      // Landmarks are not attackable.
      case LANDMARK_CASTLE, LANDMARK_BRIDGE -> 0f;

      default -> {
        // Keep derived fallback for new entity types.
        float w = drawW(t);
        float h = drawH(t);
        float halfMin = Math.min(w, h) * 0.5f;
        yield halfMin * 0.60f;
      }
    };
  }
}

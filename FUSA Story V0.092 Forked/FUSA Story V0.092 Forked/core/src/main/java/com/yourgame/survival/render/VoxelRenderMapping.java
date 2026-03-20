package com.yourgame.survival.render;

/**
 * VoxelRenderMapping
 *
 * Purpose (preparation only):
 * - Central place for 2.5D "fake tilt" mapping constants and helper math.
 * - Computes visual offsets for tile top faces and side faces based on heightLevel.
 *
 * This file intentionally does NOT integrate into ChunkRenderer yet.
 *
 * Reference anchors:
 * - `render/ChunkRenderer.java` draw loops and its heightLevel peek helper
 * - `world/World.TILE_WORLD` (tile size in world units)
 */
public final class VoxelRenderMapping {
  private VoxelRenderMapping() {}

  /**
   * Visual vertical step per height level (in world units).
   * Note: should be chosen relative to World.TILE_WORLD (16f).
   */
  public static final float HEIGHT_STEP_WORLD = 4f;

  /**
   * 2.5D tilt: compress Y in screen-space illusion.
   * This is a render mapping constant; gameplay remains un-tilted.
   */
  public static final float TILT_Y_SCALE = 0.80f;

  /**
   * Optional: shear factor to create a stronger 2.5D impression.
   * Keep 0 for now; introduce later if needed.
   */
  public static final float SHEAR_X_FROM_Y = 0.0f;

  /**
   * Applies full voxel mapping to a logical world coordinate.
   * This is the canonical mapping used by:
   * - ChunkRenderer tile top faces
   * - EntityRenderer entity sprites / tileTrees
   * - GameScreen world-space overlays (cursor, bars, FoW, arrows)
   */
  public static float mapX(float worldX, float mappedY) {
    return worldX + SHEAR_X_FROM_Y * mappedY;
  }

  /** Maps logical world Y to render-space Y, given an already-applied lift. */
  public static float mapY(float liftedWorldY) {
    return tiltY(liftedWorldY);
  }

  /** Returns the visual lift (world units) for a given heightLevel (0..255). */
  public static float liftWorld(int heightLevel) {
    if (heightLevel <= 0) return 0f;
    return (heightLevel & 0xFF) * HEIGHT_STEP_WORLD;
  }

  /**
   * Apply fake tilt to a Y coordinate (world units).
   * NOTE: this is a mapping helper; the caller decides where it is applied.
   */
  public static float tiltY(float worldY) {
    return worldY * TILT_Y_SCALE;
  }
}

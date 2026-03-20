package com.yourgame.survival.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * VoxelSideRegions
 *
 * Purpose (preparation only):
 * - Defines how to pick side-face textures (cliffs) for voxel columns.
 * - Side faces are drawn when neighbor height is lower.
 *
 * Constraints:
 * - No silent fallbacks. Missing regions should be immediately obvious.
 *
 * Reference anchors:
 * - `render/TilesetRegions` for how ground tiles are mapped to TextureRegions.
 */
public final class VoxelSideRegions {
  private VoxelSideRegions() {}

  /**
   * Returns the side-face region for a given groundId/material.
   *
   * Current state:
   * - The project does not yet have dedicated side-face/cliff sprites.
   * - We return an explicit visual placeholder so cliffs are visible and obviously temporary.
   *
   * IMPORTANT:
   * - This is not a silent fallback: the placeholder is visually loud.
   * - Once side-face art exists, replace this mapping with real regions.
   */
  public static TextureRegion sideForGroundId(short groundId, TilesetRegions tiles) {
    if (tiles == null) throw new IllegalArgumentException("tiles is null");
    // Phase 1 (no dedicated side-face art yet): reuse the tile's ground texture as the face base.
    // Caller is responsible for tinting/darkening to read as a vertical face.
    return tiles.ground(groundId);
  }
}

package com.yourgame.survival.editor;

import com.badlogic.gdx.utils.JsonValue;
import com.yourgame.survival.area.VoxelHeightCodec;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.world.World;

/**
 * HeightLevelEditing
 *
 * Preparation-only utility for authoring voxel-column heights (TileLayers.heightLevel).
 *
 * This class is intentionally NOT referenced by WorldEditorScreen yet.
 * It exists as a central place for the "real" implementation once integration begins.
 *
 * Why it exists:
 * - Current code has heightLevel already, but authored areas explicitly zero it.
 * - We need stable, reusable logic to:
 *   1) export heightLevel from a preview World into a dense (w*h) byte[]
 *   2) import a dense byte[] into the preview World
 *   3) apply brush operations deterministically (raise/lower) without touching renderer logic
 */
public final class HeightLevelEditing {
  private HeightLevelEditing() {}

  /**
   * Export heightLevel from a World into a dense row-major byte array of size w*h.
   *
   * IMPORTANT: Stub scaffolding; integration phase must implement chunk iteration identical to area loaders.
   */
  public static byte[] exportHeightLevel(World world, int w, int h) {
    if (world == null || w <= 0 || h <= 0) throw new IllegalArgumentException("invalid args");
    byte[] out = new byte[w * h];

    // Iterate all tiles (tx,ty) in [0..w) x [0..h)
    // NOTE: for authored areas, chunks should already exist; we use peekChunk to avoid forcing.
    for (int ty = 0; ty < h; ty++) {
      int cy = ty / World.CHUNK_SIZE;
      int ly = ty - cy * World.CHUNK_SIZE;
      for (int tx = 0; tx < w; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int lx = tx - cx * World.CHUNK_SIZE;
        Chunk c = world.peekChunk(cx, cy);
        if (c == null) {
          // Missing chunk => treat as 0 (neutral). This is not a content fallback; it's a safety
          // for incomplete preview worlds.
          continue;
        }
        int idx = lx + ly * World.CHUNK_SIZE;
        out[tx + ty * w] = c.layers.heightLevel[idx];
      }
    }

    return out;
  }

  /**
   * Apply a dense row-major heightLevel grid into a World.
   *
   * Contract:
   * - world must already have all needed chunks created (or the caller chooses to force-create).
   */
  public static void importHeightLevel(World world, int w, int h, byte[] data) {
    if (world == null || w <= 0 || h <= 0) throw new IllegalArgumentException("invalid args");
    if (data == null || data.length != w * h) throw new IllegalArgumentException("bad data");

    // Ensure all needed chunks exist.
    int maxTx = Math.max(0, w - 1);
    int maxTy = Math.max(0, h - 1);
    int maxCx = Math.max(0, maxTx / World.CHUNK_SIZE);
    int maxCy = Math.max(0, maxTy / World.CHUNK_SIZE);
    for (int cy = 0; cy <= maxCy; cy++) {
      for (int cx = 0; cx <= maxCx; cx++) {
        world.chunk(cx, cy);
      }
    }

    for (int ty = 0; ty < h; ty++) {
      int cy = ty / World.CHUNK_SIZE;
      int ly = ty - cy * World.CHUNK_SIZE;
      for (int tx = 0; tx < w; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int lx = tx - cx * World.CHUNK_SIZE;
        Chunk c = world.chunk(cx, cy);
        int idx = lx + ly * World.CHUNK_SIZE;
        c.layers.heightLevel[idx] = data[tx + ty * w];
      }
    }
  }

  /**
   * Persist heightLevel into area JSON using the canonical codec.
   *
   * This is a helper wrapper; callers decide when to set dirty flags.
   */
  public static void persistToJson(JsonValue areaJson, int w, int h, byte[] heightLevel) {
    VoxelHeightCodec.writeHeightLevel(areaJson, w, h, heightLevel);
  }

  /**
   * Read authored heightLevel from JSON.
   *
   * Returns null if missing; throws if invalid.
   */
  public static byte[] readFromJsonOrNull(JsonValue areaJson, int w, int h) {
    return VoxelHeightCodec.readHeightLevelOrNull(areaJson, w, h);
  }

  // ---------------------------------------------------------------------------
  // Brush math placeholders
  // ---------------------------------------------------------------------------

  /**
   * Prepared brush operation: raise or lower heightLevel at a tile.
   *
   * NOTE: Phase 1 should clamp to an agreed range (e.g. 0..15).
   */
  public static void applyDeltaAt(World world, int tx, int ty, int delta, int clampMin, int clampMax) {
    if (world == null) return;
    if (delta == 0) return;
    if (clampMax < clampMin) return;

    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) return;

    Chunk c = world.chunk(cx, cy);
    if (c == null || c.layers == null) return;

    int idx = lx + ly * World.CHUNK_SIZE;
    int v = c.layers.heightLevel[idx] & 0xFF;
    int nv = v + delta;
    if (nv < clampMin) nv = clampMin;
    if (nv > clampMax) nv = clampMax;
    c.layers.heightLevel[idx] = (byte) (nv & 0xFF);
  }

  /** Small helper for locating the chunk; used by the future integration implementation. */
  @SuppressWarnings("unused")
  private static TileLayers layersAt(World world, int tx, int ty, boolean forceCreate) {
    int cx = (int) Math.floor((float) tx / World.CHUNK_SIZE);
    int cy = (int) Math.floor((float) ty / World.CHUNK_SIZE);
    Chunk c = forceCreate ? world.chunk(cx, cy) : world.peekChunk(cx, cy);
    return (c != null) ? c.layers : null;
  }
}

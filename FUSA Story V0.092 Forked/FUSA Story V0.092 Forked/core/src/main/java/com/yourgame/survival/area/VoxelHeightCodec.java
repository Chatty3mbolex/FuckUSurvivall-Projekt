package com.yourgame.survival.area;

import com.badlogic.gdx.utils.JsonValue;

/**
 * VoxelHeightCodec
 *
 * Purpose (preparation only):
 * - Encode/decode authored per-tile height (heightLevel) to/from Base64.
 * - Read/write this data to `.area.json` under `layers.heightLevel`.
 *
 * IMPORTANT RULES
 * - No implicit fallbacks that hide missing content.
 * - Validation must be strict: w/h and byte length must match.
 *
 * Reference anchors (existing patterns in codebase):
 * - `WorldEditorScreen.persistTileTreeBitsToJson()` (Base64 persistence pattern)
 * - `JsonAreaWorldLoader` tileTrees read path (w/h validation)
 *
 * Target JSON schema (Phase 1 preferred):
 * {
 *   "layers": {
 *     "heightLevel": { "w": <int>, "h": <int>, "b64": "..." }
 *   }
 * }
 */
public final class VoxelHeightCodec {
  private VoxelHeightCodec() {}

  public static final String K_LAYERS = "layers";
  public static final String K_HEIGHT_LEVEL = "heightLevel";
  public static final String K_W = "w";
  public static final String K_H = "h";
  public static final String K_B64 = "b64";

  /**
   * Reads `layers.heightLevel` and returns decoded bytes.
   *
   * Contract:
   * - returns null if block missing
   * - throws IllegalStateException if block present but invalid
   */
  public static byte[] readHeightLevelOrNull(final JsonValue areaJson, final int wantW, final int wantH) {
    if (areaJson == null) return null;

    final JsonValue layers = areaJson.get(K_LAYERS);
    if (layers == null) return null;

    final JsonValue hl = layers.get(K_HEIGHT_LEVEL);
    if (hl == null) return null;

    final int w = hl.getInt(K_W, -1);
    final int h = hl.getInt(K_H, -1);
    final String b64 = hl.getString(K_B64, "");

    if (w != wantW || h != wantH) {
      throw new IllegalStateException("heightLevel dims mismatch: got=" + w + "x" + h + " want=" + wantW + "x" + wantH);
    }
    if (b64 == null || b64.isEmpty()) {
      throw new IllegalStateException("heightLevel present but b64 empty");
    }

    final byte[] data;
    try {
      data = java.util.Base64.getDecoder().decode(b64);
    } catch (Throwable t) {
      throw new IllegalStateException("heightLevel b64 decode failed", t);
    }

    final int wantLen = Math.max(0, wantW) * Math.max(0, wantH);
    if (data.length != wantLen) {
      throw new IllegalStateException("heightLevel byte length mismatch: got=" + data.length + " want=" + wantLen);
    }

    return data;
  }

  /**
   * Writes/overwrites `layers.heightLevel` with (w,h,b64).
   *
   * Contract:
   * - Throws on invalid args.
   */
  public static void writeHeightLevel(final JsonValue areaJson, final int w, final int h, final byte[] heightLevel) {
    if (areaJson == null) throw new IllegalArgumentException("areaJson is null");
    if (w <= 0 || h <= 0) throw new IllegalArgumentException("invalid dims: " + w + "x" + h);
    if (heightLevel == null) throw new IllegalArgumentException("heightLevel is null");
    if (heightLevel.length != w * h) throw new IllegalArgumentException("heightLevel length mismatch");

    JsonValue layers = areaJson.get(K_LAYERS);
    if (layers == null) {
      layers = new JsonValue(JsonValue.ValueType.object);
      areaJson.addChild(K_LAYERS, layers);
    }

    JsonValue hl = layers.get(K_HEIGHT_LEVEL);
    if (hl == null) {
      hl = new JsonValue(JsonValue.ValueType.object);
      layers.addChild(K_HEIGHT_LEVEL, hl);
    }

    final String b64 = java.util.Base64.getEncoder().encodeToString(heightLevel);

    // Overwrite keys
    hl.remove(K_W);
    hl.remove(K_H);
    hl.remove(K_B64);
    hl.addChild(K_W, new JsonValue(w));
    hl.addChild(K_H, new JsonValue(h));
    hl.addChild(K_B64, new JsonValue(b64));
  }
}

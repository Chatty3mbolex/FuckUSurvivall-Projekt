package com.yourgame.survival.editor;

/** Hard bounds for editability (3x3 chunks around origin). */
public final class EditorBounds {
  private EditorBounds() {}

  public static boolean isChunkInside(int cx, int cy) {
    return cx >= EditorConstants.MIN_C && cx <= EditorConstants.MAX_C
        && cy >= EditorConstants.MIN_C && cy <= EditorConstants.MAX_C;
  }

  public static boolean isTileInside(int tx, int ty) {
    int cx = EditorCoord.floorDiv(tx, EditorConstants.CHUNK_SIZE);
    int cy = EditorCoord.floorDiv(ty, EditorConstants.CHUNK_SIZE);
    return isChunkInside(cx, cy);
  }
}

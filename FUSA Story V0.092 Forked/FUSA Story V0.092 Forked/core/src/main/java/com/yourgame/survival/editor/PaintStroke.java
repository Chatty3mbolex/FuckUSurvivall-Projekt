package com.yourgame.survival.editor;

/** Represents a single paint stroke (for future undo/redo + dirty rect). */
public final class PaintStroke {
  public int minTx = Integer.MAX_VALUE;
  public int minTy = Integer.MAX_VALUE;
  public int maxTx = Integer.MIN_VALUE;
  public int maxTy = Integer.MIN_VALUE;

  public void includeTile(int tx, int ty) {
    if (tx < minTx) minTx = tx;
    if (ty < minTy) minTy = ty;
    if (tx > maxTx) maxTx = tx;
    if (ty > maxTy) maxTy = ty;
  }

  public boolean isEmpty() {
    return minTx == Integer.MAX_VALUE;
  }
}

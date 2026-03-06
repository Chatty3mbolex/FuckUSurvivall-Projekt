package com.yourgame.survival.world;

public final class Chunk {
  public final int cx;
  public final int cy;
  public final TileLayers layers;

  public Chunk(int cx, int cy, TileLayers layers) {
    this.cx = cx;
    this.cy = cy;
    this.layers = layers;
  }
}

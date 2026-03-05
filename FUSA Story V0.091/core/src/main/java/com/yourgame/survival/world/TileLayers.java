package com.yourgame.survival.world;

/**
 * Minimal chunk layers for Block 3.
 * Arrays are flattened row-major: idx = x + y*size.
 */
public final class TileLayers {
  public final int size;

  // climate/world data
  public final byte[] height;
  public final byte[] moisture;
  public final byte[] heat;

  // derived generator fields (0..255) for nicer procedural placement
  public final byte[] vegetation;
  public final byte[] rockiness;
  public final byte[] pathField;

  // Voxel-style terraced height level (editor + future gameplay). 0 = baseline.
  public final byte[] heightLevel;

  // legacy gameplay/source-of-truth data (keep)
  public final byte[] biomeId;
  public final byte[] waterMask;     // 1 = water
  public final byte[] collisionMask; // 1 = blocked

  // Block B: new logical tile ids (rendering-oriented, derived from biomeId for now)
  public final short[] groundId;

  // small visual overlays (0=none, >0 = overlay variant id)
  public final byte[] overlayId;

  // WorldGen detail layers (purely visual)
  // waterDist: 0=water, 1..maxR = distance to nearest water tile, 255=uncomputed/outside radius
  public final byte[] waterDist;
  // decoId: 0=none, >0 = decoration type
  public final byte[] decoId;
  public final byte[] decoVar;

  // Block D3: minimal road logic mask (0/1). Later can become roadId/byte ids.
  public final byte[] roadMask;

  // Block 2: precomputed 4-neighbor adjacency masks (bit0=N, bit1=E, bit2=S, bit3=W)
  public final byte[] shoreMask4;
  public final byte[] roadMask4;

  // Edges for transitions.
  // Corner masks are marching-squares style (NW,NE,SE,SW) bits 0..3 => 0..15.
  // These are baked in worldgen to keep borders stable (renderer must NOT sample neighbor chunks).
  public final byte[] grassCornerMask16;
  public final byte[] dirtCornerMask16;
  public final byte[] sandCornerMask16;
  public final byte[] rockCornerMask16;
  public final byte[] snowCornerMask16;

  public TileLayers(int size) {
    this.size = size;
    int n = size * size;
    this.height = new byte[n];
    this.moisture = new byte[n];
    this.heat = new byte[n];

    this.vegetation = new byte[n];
    this.rockiness = new byte[n];
    this.pathField = new byte[n];

    this.heightLevel = new byte[n];

    this.biomeId = new byte[n];
    this.waterMask = new byte[n];
    this.collisionMask = new byte[n];

    this.groundId = new short[n];
    this.overlayId = new byte[n];
    this.waterDist = new byte[n];
    this.decoId = new byte[n];
    this.decoVar = new byte[n];
    this.roadMask = new byte[n];

    this.shoreMask4 = new byte[n];
    this.roadMask4 = new byte[n];

    this.grassCornerMask16 = new byte[n];
    this.dirtCornerMask16 = new byte[n];
    this.sandCornerMask16 = new byte[n];
    this.rockCornerMask16 = new byte[n];
    this.snowCornerMask16 = new byte[n];
  }
}

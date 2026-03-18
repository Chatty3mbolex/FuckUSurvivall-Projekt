package com.yourgame.survival.tools.asseteditor.model;

import com.badlogic.gdx.utils.Array;

/** Sidecar saved next to the original asset source (atlas/src/<name>.meta.json). */
public final class CollisionMeta {
  public String regionName;
  public AssetKind kind;

  // Background detection
  public int bgR = 0;
  public int bgG = 0;
  public int bgB = 0;
  public int bgA = 255;
  public int bgTolerance = 12;      // 0..255 per channel
  public int alphaThreshold = 16;   // 0..255

  // Tiles: merged rects of solid pixels (pixel coords)
  public Array<RectI> rects = new Array<>();

  // Entities/Items/etc: polygon(s) (pixel coords)
  public Array<Poly> polys = new Array<>();

  public CollisionMeta() {}

  public static final class RectI {
    public int x;
    public int y;
    public int w;
    public int h;
    public RectI() {}
    public RectI(int x, int y, int w, int h) { this.x=x; this.y=y; this.w=w; this.h=h; }
  }

  public static final class Poly {
    /** x1,y1,x2,y2,... in pixel coords */
    public float[] pts;
    public Poly() {}
    public Poly(float[] pts) { this.pts = pts; }
  }
}

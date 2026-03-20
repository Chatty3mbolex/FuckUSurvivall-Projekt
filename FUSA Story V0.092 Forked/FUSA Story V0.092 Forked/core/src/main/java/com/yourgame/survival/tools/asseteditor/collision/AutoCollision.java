package com.yourgame.survival.tools.asseteditor.collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.yourgame.survival.tools.asseteditor.model.AssetKind;
import com.yourgame.survival.tools.asseteditor.model.CollisionMeta;

public final class AutoCollision {
  private AutoCollision() {}

  public static CollisionMeta compute(String regionName, AssetKind kind, Pixmap pm, Color bg, int bgTol, int alphaTh) {
    CollisionMeta meta = new CollisionMeta();
    meta.regionName = regionName;
    meta.kind = kind;
    meta.bgR = (int)(bg.r * 255f);
    meta.bgG = (int)(bg.g * 255f);
    meta.bgB = (int)(bg.b * 255f);
    meta.bgA = (int)(bg.a * 255f);
    meta.bgTolerance = bgTol;
    meta.alphaThreshold = alphaTh;

    boolean[][] solid = MaskBuilder.build(pm, alphaTh, bg, bgTol);

    if (kind == AssetKind.TILE) {
      meta.rects = MaskRects.build(solid);
    } else {
      Array<Vector2> contour = MarchingSquares.traceOuter(solid);
      contour = PolylineSimplify.rdp(contour, 1.5f);
      // clamp vertices
      if (contour.size > 64) {
        Array<Vector2> down = new Array<>();
        int step = Math.max(1, contour.size / 64);
        for (int i = 0; i < contour.size; i += step) down.add(contour.get(i));
        contour = down;
      }
      if (contour.size >= 3) {
        float[] pts = new float[contour.size * 2];
        for (int i = 0; i < contour.size; i++) {
          pts[i * 2] = contour.get(i).x;
          pts[i * 2 + 1] = contour.get(i).y;
        }
        meta.polys.add(new CollisionMeta.Poly(pts));
      }
    }

    return meta;
  }
}

package com.yourgame.survival.tools.asseteditor.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public final class PolylineSimplify {
  private PolylineSimplify() {}

  public static Array<Vector2> rdp(Array<Vector2> pts, float epsilon) {
    if (pts == null || pts.size < 4) return pts;

    boolean[] keep = new boolean[pts.size];
    keep[0] = true;
    keep[pts.size - 1] = true;

    rdpRec(pts, 0, pts.size - 1, epsilon, keep);

    Array<Vector2> out = new Array<>();
    for (int i = 0; i < pts.size; i++) if (keep[i]) out.add(new Vector2(pts.get(i)));
    return out;
  }

  private static void rdpRec(Array<Vector2> pts, int a, int b, float eps, boolean[] keep) {
    if (b <= a + 1) return;

    Vector2 p1 = pts.get(a);
    Vector2 p2 = pts.get(b);

    float maxD = -1f;
    int idx = -1;

    for (int i = a + 1; i < b; i++) {
      float d = distPointSegmentSq(pts.get(i), p1, p2);
      if (d > maxD) { maxD = d; idx = i; }
    }

    if (maxD >= eps * eps && idx >= 0) {
      keep[idx] = true;
      rdpRec(pts, a, idx, eps, keep);
      rdpRec(pts, idx, b, eps, keep);
    }
  }

  private static float distPointSegmentSq(Vector2 p, Vector2 a, Vector2 b) {
    float vx = b.x - a.x;
    float vy = b.y - a.y;
    float wx = p.x - a.x;
    float wy = p.y - a.y;

    float c1 = vx * wx + vy * wy;
    if (c1 <= 0) return wx * wx + wy * wy;

    float c2 = vx * vx + vy * vy;
    if (c2 <= c1) {
      float dx = p.x - b.x;
      float dy = p.y - b.y;
      return dx * dx + dy * dy;
    }

    float t = c1 / c2;
    float px = a.x + t * vx;
    float py = a.y + t * vy;
    float dx = p.x - px;
    float dy = p.y - py;
    return dx * dx + dy * dy;
  }
}

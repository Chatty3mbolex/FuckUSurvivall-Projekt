package com.yourgame.survival.tools.asseteditor.collision;

import com.badlogic.gdx.utils.Array;
import com.yourgame.survival.tools.asseteditor.model.CollisionMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts a solid mask into a small set of merged axis-aligned rects.
 * Deterministic greedy merge: horizontal runs merged vertically if identical.
 */
public final class MaskRects {
  private MaskRects() {}

  public static Array<CollisionMeta.RectI> build(boolean[][] solid) {
    int h = solid.length;
    int w = h == 0 ? 0 : solid[0].length;

    // For each row: list runs (x0,x1)
    // Then merge identical runs in consecutive rows.
    Map<String, CollisionMeta.RectI> open = new HashMap<>();
    Array<CollisionMeta.RectI> out = new Array<>();

    for (int y = 0; y < h; y++) {
      Map<String, CollisionMeta.RectI> nextOpen = new HashMap<>();
      int x = 0;
      while (x < w) {
        while (x < w && !solid[y][x]) x++;
        if (x >= w) break;
        int x0 = x;
        while (x < w && solid[y][x]) x++;
        int x1 = x; // exclusive

        String key = x0 + ":" + x1;
        CollisionMeta.RectI r = open.remove(key);
        if (r != null) {
          r.h += 1;
          nextOpen.put(key, r);
        } else {
          r = new CollisionMeta.RectI(x0, y, x1 - x0, 1);
          nextOpen.put(key, r);
        }
      }

      // Close any remaining open rects.
      for (CollisionMeta.RectI r : open.values()) out.add(r);
      open = nextOpen;
    }

    for (CollisionMeta.RectI r : open.values()) out.add(r);
    return out;
  }
}

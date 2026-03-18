package com.yourgame.survival.tools.asseteditor.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Minimal marching squares contour tracing for a single outer contour.
 * Input mask is solid[y][x] (y=0 bottom).
 */
public final class MarchingSquares {
  private MarchingSquares() {}

  public static Array<Vector2> traceOuter(boolean[][] solid) {
    int h = solid.length;
    if (h == 0) return new Array<>();
    int w = solid[0].length;

    // Find a starting cell where solid exists.
    int sx = -1, sy = -1;
    outer:
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (solid[y][x]) { sx = x; sy = y; break outer; }
      }
    }
    if (sx < 0) return new Array<>();

    // Start at leftmost boundary pixel on that row.
    while (sx > 0 && solid[sy][sx - 1]) sx--;

    // Walk along edges using a simple wall-following on pixel grid.
    // We trace in continuous coords at pixel corners.
    int x = sx;
    int y = sy;
    int dir = 0; // 0=up,1=right,2=down,3=left (edge-walk)

    Array<Vector2> pts = new Array<>();
    int guard = (w * h) * 8;

    // We walk around the solid region by keeping solid on our left.
    // Start on bottom-left corner of the start pixel.
    float cx = x;
    float cy = y;
    pts.add(new Vector2(cx, cy));

    for (int i = 0; i < guard; i++) {
      // Try turn left if possible, else go straight, else turn right, else back.
      int left = (dir + 3) & 3;
      if (canMove(solid, w, h, x, y, left)) {
        dir = left;
      } else if (canMove(solid, w, h, x, y, dir)) {
        // keep
      } else {
        int right = (dir + 1) & 3;
        if (canMove(solid, w, h, x, y, right)) dir = right;
        else dir = (dir + 2) & 3;
      }

      // Move one step on the boundary grid.
      switch (dir) {
        case 0: y += 1; break;
        case 1: x += 1; break;
        case 2: y -= 1; break;
        case 3: x -= 1; break;
      }

      cx = x;
      cy = y;
      Vector2 last = pts.peek();
      if (last.x != cx || last.y != cy) pts.add(new Vector2(cx, cy));

      // Stop if we've returned to start with enough points.
      if (x == sx && y == sy && pts.size > 8) break;
    }

    return pts;
  }

  private static boolean isSolid(boolean[][] solid, int w, int h, int x, int y) {
    if (x < 0 || y < 0 || x >= w || y >= h) return false;
    return solid[y][x];
  }

  /**
   * Whether we can move along boundary grid while keeping solid on left.
   * This is an approximation; works decently for pixel silhouettes.
   */
  private static boolean canMove(boolean[][] solid, int w, int h, int x, int y, int dir) {
    // We examine the cell to our left relative to movement.
    // Movement happens on grid lines; sample the pixel that would be on the left.
    int lx = x;
    int ly = y;
    switch (dir) {
      case 0: // up, left is pixel at (x-1,y)
        lx = x - 1; ly = y;
        break;
      case 1: // right, left is pixel at (x, y)
        lx = x; ly = y;
        break;
      case 2: // down, left is pixel at (x, y-1)
        lx = x; ly = y - 1;
        break;
      case 3: // left, left is pixel at (x-1, y-1)
        lx = x - 1; ly = y - 1;
        break;
    }
    return isSolid(solid, w, h, lx, ly);
  }
}

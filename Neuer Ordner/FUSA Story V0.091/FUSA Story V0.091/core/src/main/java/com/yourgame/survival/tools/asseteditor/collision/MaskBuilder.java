package com.yourgame.survival.tools.asseteditor.collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

/** Builds a solid mask from pixmap using alpha + optional background color key. */
public final class MaskBuilder {
  private MaskBuilder() {}

  public static boolean[][] build(Pixmap pm, int alphaThreshold, Color bg, int tol) {
    int w = pm.getWidth();
    int h = pm.getHeight();
    boolean[][] solid = new boolean[h][w]; // y, x (Pixmap origin is top-left for getPixel? Pixmap uses y from top? Actually Pixmap pixel coords are bottom-left in libgdx. We'll treat y=0 bottom.

    int bgR = (int)(bg.r * 255f);
    int bgG = (int)(bg.g * 255f);
    int bgB = (int)(bg.b * 255f);

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int rgba = pm.getPixel(x, y);
        int a = (rgba) & 0xff;
        int b = (rgba >>> 8) & 0xff;
        int g = (rgba >>> 16) & 0xff;
        int r = (rgba >>> 24) & 0xff;

        boolean isBg = false;
        if (a < alphaThreshold) isBg = true;
        else {
          // color-key background (even if opaque)
          if (Math.abs(r - bgR) <= tol && Math.abs(g - bgG) <= tol && Math.abs(b - bgB) <= tol) isBg = true;
        }
        solid[y][x] = !isBg;
      }
    }
    return solid;
  }
}

package com.yourgame.survival.biome;

import com.badlogic.gdx.graphics.Pixmap;

/** Decode height levels (0..15) from RGBA8888 pixmaps (value in red channel). */
public final class HeightMaskUtil {
  private HeightMaskUtil() {}

  public static int decodeHeightLevel(Pixmap pm, int lx, int ly) {
    if (pm == null) return 0;
    int rgba = pm.getPixel(lx, ly);
    int r = (rgba >>> 24) & 0xFF;
    int h = (int) Math.round((r / 255.0) * 15.0);
    if (h < 0) h = 0;
    if (h > 15) h = 15;
    return h;
  }
}

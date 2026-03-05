package com.yourgame.survival.biome;

import com.badlogic.gdx.graphics.Pixmap;

/** Decode deco density (0..255) from RGBA8888 pixmaps (value in red channel). */
public final class DecoMaskUtil {
  private DecoMaskUtil() {}

  public static int decode(Pixmap pm, int lx, int ly) {
    if (pm == null) return 255;
    int rgba = pm.getPixel(lx, ly);
    return (rgba >>> 24) & 0xFF;
  }
}

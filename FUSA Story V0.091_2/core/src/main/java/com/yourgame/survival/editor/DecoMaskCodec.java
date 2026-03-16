package com.yourgame.survival.editor;

import com.badlogic.gdx.graphics.Pixmap;

/** Encode/decode deco density (0..255) into an RGBA8888 Pixmap via red channel. */
public final class DecoMaskCodec {
  private DecoMaskCodec() {}

  public static int get(Pixmap pm, int x, int y) {
    if (pm == null) return 255;
    int rgba = pm.getPixel(x, y);
    return (rgba >>> 24) & 0xFF;
  }

  public static void set(Pixmap pm, int x, int y, int v) {
    if (pm == null) return;
    if (v < 0) v = 0;
    if (v > 255) v = 255;
    int rgba = (v << 24) | (0 << 16) | (0 << 8) | 0xFF;
    pm.drawPixel(x, y, rgba);
  }

  public static void clear(Pixmap pm) {
    if (pm == null) return;
    pm.setColor(1f, 0f, 0f, 1f); // red=255
    pm.fill();
  }
}

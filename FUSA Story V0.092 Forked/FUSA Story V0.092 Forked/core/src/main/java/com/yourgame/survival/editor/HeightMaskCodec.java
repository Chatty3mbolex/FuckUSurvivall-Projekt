package com.yourgame.survival.editor;

import com.badlogic.gdx.graphics.Pixmap;

/** Encode/decode terraced height levels (0..15) into an RGBA8888 Pixmap. */
public final class HeightMaskCodec {
  private HeightMaskCodec() {}

  /** Read height level from pixel. Uses red channel 0..255 mapped to 0..15 (clamped). */
  public static int get(Pixmap pm, int x, int y) {
    if (pm == null) return 0;
    int rgba = pm.getPixel(x, y);
    int r = (rgba >>> 24) & 0xFF;
    int h = (int) Math.round((r / 255.0) * 15.0);
    if (h < 0) h = 0;
    if (h > 15) h = 15;
    return h;
  }

  /** Write height level (0..15) into red channel; alpha set to 255. */
  public static void set(Pixmap pm, int x, int y, int h) {
    if (pm == null) return;
    if (h < 0) h = 0;
    if (h > 15) h = 15;
    int r = (int) Math.round((h / 15.0) * 255.0);
    int rgba = (r << 24) | (0 << 16) | (0 << 8) | 0xFF;
    pm.drawPixel(x, y, rgba);
  }

  public static void clear(Pixmap pm) {
    if (pm == null) return;
    pm.setColor(0f, 0f, 0f, 1f);
    pm.fill();
  }
}

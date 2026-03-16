package com.yourgame.survival.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Minimal debug HUD (screen/world/tile/chunk) for input sanity. */
public final class EditorDebugHud {
  private final BitmapFont font;
  public boolean enabled = true;

  public float screenX, screenY;
  public float worldX, worldY;
  public int tileX, tileY;
  public int chunkX, chunkY;
  public int heightLevel;
  public int groundId;

  public EditorDebugHud(BitmapFont font) {
    this.font = font;
  }

  public void draw(SpriteBatch batch) {
    if (!enabled || font == null) return;

    float x = 12f;
    float y = Gdx.graphics.getHeight() - 12f;
    font.draw(batch, "HUD", x, y);
    y -= 18f;
    font.draw(batch, "screen: (" + (int)screenX + "," + (int)screenY + ")", x, y);
    y -= 18f;
    font.draw(batch, "world:  (" + fmt(worldX) + "," + fmt(worldY) + ")", x, y);
    y -= 18f;
    font.draw(batch, "tile:   (" + tileX + "," + tileY + ")", x, y);
    y -= 18f;
    font.draw(batch, "chunk:  (" + chunkX + "," + chunkY + ")", x, y);
    y -= 18f;
    font.draw(batch, "groundId: " + groundId + "   heightLevel: " + heightLevel, x, y);
  }

  private static String fmt(float v) {
    return String.format(java.util.Locale.ROOT, "%.2f", v);
  }
}

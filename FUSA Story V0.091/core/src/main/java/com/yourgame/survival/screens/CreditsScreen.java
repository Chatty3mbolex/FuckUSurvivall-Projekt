package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.render.UiRegions;

public final class CreditsScreen extends ScreenAdapter {
  public interface BackAction { void goBack(); }

  private final SurvivalGame game;
  private final BackAction back;

  private static final float UI_SCALE = 1.65f;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private UiRegions ui;

  public CreditsScreen(SurvivalGame game, BackAction back) {
    this.game = game;
    this.back = back;
  }

  @Override
  public void show() {
    batch = new SpriteBatch();
    font = new BitmapFont();
    layout = new GlyphLayout();
    ui = new UiRegions();
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      back.goBack();
      return;
    }

    if (Gdx.input.justTouched()) {
      float mx = Gdx.input.getX();
      float my = Gdx.graphics.getHeight() - Gdx.input.getY();
      float bx = (Gdx.graphics.getWidth() - 220 * UI_SCALE) * 0.5f;
      if (mx >= bx && mx <= bx + 220 * UI_SCALE && my >= 40 && my <= 40 + 44 * UI_SCALE) {
        back.goBack();
        return;
      }
    }

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    float panelW = 920 * UI_SCALE;
    float panelH = 520 * UI_SCALE;
    float px0 = (Gdx.graphics.getWidth() - panelW) * 0.5f;
    float py0 = (Gdx.graphics.getHeight() - panelH) * 0.5f;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.panelSlots, px0, py0, panelW, panelH);

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(2f * UI_SCALE);
    layout.setText(font, "Credits");
    font.draw(batch, layout, px0 + (panelW - layout.width) * 0.5f, py0 + panelH - 40 * UI_SCALE);

    font.getData().setScale(1.35f * UI_SCALE);
    float y = py0 + panelH - 160 * UI_SCALE;
    layout.setText(font, "Embolex");
    font.draw(batch, layout, px0 + (panelW - layout.width) * 0.5f, y);
    layout.setText(font, "ChatGPT 5.2");
    font.draw(batch, layout, px0 + (panelW - layout.width) * 0.5f, y - 44 * UI_SCALE);

    font.getData().setScale(1.0f * UI_SCALE);
    float bx = px0 + (panelW - 220 * UI_SCALE) * 0.5f;
    float by = py0 + 40 * UI_SCALE;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.button, bx, by, 220 * UI_SCALE, 44 * UI_SCALE);
    layout.setText(font, "Back");
    font.draw(batch, layout, bx + (220 * UI_SCALE - layout.width) * 0.5f, by + (44 * UI_SCALE) * 0.68f);

    batch.end();
  }

  @Override
  public void dispose() {
    if (batch != null) batch.dispose();
    if (font != null) font.dispose();
    if (ui != null) ui.dispose();
  }
}

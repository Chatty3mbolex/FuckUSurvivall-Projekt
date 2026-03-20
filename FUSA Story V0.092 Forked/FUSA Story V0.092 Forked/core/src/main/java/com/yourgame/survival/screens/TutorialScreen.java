package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.render.UiRegions;

/** Block 10: tutorial screen driven by assets/tutorial/tutorial.json */
public final class TutorialScreen extends ScreenAdapter {
  public interface BackAction { void goBack(); }

  private final BackAction back;

  private static final float UI_SCALE = 1.65f;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private UiRegions ui;

  private String[] lines = new String[0];

  public TutorialScreen(SurvivalGame game, BackAction back) {
    this.back = back;
  }

  @Override
  public void show() {
    batch = new SpriteBatch();
    font = new BitmapFont();
    layout = new GlyphLayout();
    ui = new UiRegions();

    loadTutorial();
  }

  private void loadTutorial() {
    try {
      JsonValue root = new JsonReader().parse(Gdx.files.internal("tutorial/tutorial.json"));
      JsonValue arr = root.get("steps");
      if (arr == null) return;
      lines = new String[arr.size];
      int i = 0;
      for (JsonValue s = arr.child; s != null; s = s.next) {
        String t = s.getString("text", "");
        lines[i++] = t;
      }
    } catch (Throwable t) {
      lines = new String[] {"(tutorial.json load failed)", String.valueOf(t)};
    }
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

    Gdx.gl.glClearColor(0.01f, 0.01f, 0.03f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    float panelW = 1020 * UI_SCALE;
    float panelH = 720 * UI_SCALE;
    float px0 = (Gdx.graphics.getWidth() - panelW) * 0.5f;
    float py0 = (Gdx.graphics.getHeight() - panelH) * 0.5f;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.panelSlots, px0, py0, panelW, panelH);

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(2f * UI_SCALE);
    layout.setText(font, "Tutorial");
    font.draw(batch, layout, px0 + (panelW - layout.width) * 0.5f, py0 + panelH - 40 * UI_SCALE);

    font.getData().setScale(1.15f * UI_SCALE);
    float x0 = px0 + 60 * UI_SCALE;
    float y = py0 + panelH - 120 * UI_SCALE;
    for (int i=0; i<lines.length && i<18; i++) {
      font.draw(batch, "• " + lines[i], x0, y - i * (34 * UI_SCALE));
    }

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

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

/** Block 10: pause menu that can resume the existing GameScreen instance. */
public final class PauseScreen extends ScreenAdapter {
  private final SurvivalGame game;
  private final GameScreen resumeTo;

  private static final float UI_SCALE = 1.65f;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private UiRegions ui;

  private float x;
  private float yTop;
  private float w;
  private float h;

  // Prevent accidental click-through from the frame that opened the pause menu (ESC).
  private float inputLockT = 0.25f;

  public PauseScreen(SurvivalGame game, GameScreen resumeTo) {
    this.game = game;
    this.resumeTo = resumeTo;
  }

  @Override
  public void show() {
    // Ensure the cursor is visible while paused.
    game.setCursorCrosshair();

    batch = new SpriteBatch();
    font = new BitmapFont();
    layout = new GlyphLayout();
    ui = new UiRegions();

    w = 320 * UI_SCALE;
    h = 44 * UI_SCALE;
    recalcLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  @Override
  public void resize(int width, int height) {
    recalcLayout(width, height);
  }

  private void recalcLayout(int wScreen, int hScreen) {
    x = (wScreen - w) * 0.5f;

    float gap = 16f * UI_SCALE;
    int n = 8;
    float blockH = n * h + (n - 1) * gap;
    float top = hScreen * 0.5f + blockH * 0.5f;
    yTop = top - h;
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      resumeTo.forceCursorResync();
      game.setScreen(resumeTo);
      return;
    }

    if (inputLockT > 0f) inputLockT -= delta;

    if (inputLockT <= 0f && Gdx.input.justTouched()) {
      float mx = Gdx.input.getX();
      float my = Gdx.graphics.getHeight() - Gdx.input.getY();

      if (hit(mx, my, x, yTop, w, h)) {
        resumeTo.forceCursorResync();
        game.setScreen(resumeTo);
        return;
      }
      if (hit(mx, my, x, yTop - 60, w, h)) {
        game.setScreen(new SaveLoadScreen(game, resumeTo, SaveLoadScreen.Mode.SAVE));
        return;
      }
      if (hit(mx, my, x, yTop - 120, w, h)) {
        game.setScreen(new SaveLoadScreen(game, resumeTo, SaveLoadScreen.Mode.LOAD));
        return;
      }
      if (hit(mx, my, x, yTop - 180, w, h)) {
        game.setScreen(new OptionsScreen(game, () -> game.setScreen(this)));
        return;
      }
      if (hit(mx, my, x, yTop - 240, w, h)) {
        game.setScreen(new TutorialScreen(game, () -> game.setScreen(this)));
        return;
      }
      if (hit(mx, my, x, yTop - 300, w, h)) {
        game.setScreen(new CreditsScreen(game, () -> game.setScreen(this)));
        return;
      }
      if (hit(mx, my, x, yTop - 360, w, h)) {
        game.setScreen(new MenuScreen(game));
        return;
      }
      if (hit(mx, my, x, yTop - 420, w, h)) {
        Gdx.app.exit();
        return;
      }
    }

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    font.getData().setScale(2f * UI_SCALE);
    layout.setText(font, "Paused");
    font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, Gdx.graphics.getHeight() - 40);

    font.getData().setScale(1.2f * UI_SCALE);
    drawButton("Resume", x, yTop);
    drawButton("Save Game", x, yTop - 60);
    drawButton("Load Game", x, yTop - 120);
    drawButton("Options", x, yTop - 180);
    drawButton("Tutorial", x, yTop - 240);
    drawButton("Credits", x, yTop - 300);
    drawButton("Back to Menu", x, yTop - 360);
    drawButton("Exit", x, yTop - 420);

    font.getData().setScale(1f * UI_SCALE);
    layout.setText(font, "ESC resumes");
    font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, 60 * UI_SCALE);

    batch.end();
  }

  private void drawButton(String text, float x, float y) {
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.button, x, y, w, h);

    layout.setText(font, text);
    float tx = x + (w - layout.width) * 0.5f;
    float ty = y + h * 0.68f;
    font.draw(batch, layout, tx, ty);
  }

  private static boolean hit(float px, float py, float x, float y, float w, float h) {
    return px >= x && px <= x + w && py >= y && py <= y + h;
  }

  @Override
  public void dispose() {
    if (batch != null) batch.dispose();
    if (font != null) font.dispose();
    if (ui != null) ui.dispose();
  }
}

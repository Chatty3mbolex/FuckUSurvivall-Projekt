package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.yourgame.survival.GameSettings;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.data.SettingsIO;
import com.yourgame.survival.render.UiRegions;

/** Block 10: options (not dead; values are live in SurvivalGame.settings). */
public final class OptionsScreen extends ScreenAdapter {
  public interface BackAction { void goBack(); }

  private final SurvivalGame game;
  private final BackAction back;

  private static final float UI_SCALE = 1.65f;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private UiRegions ui;

  // Nicht fertiges Feature: unused layout cache (can be re-enabled if we reuse absolute anchors)
  // private float x0;
  // private float y0;
  // Nicht fertiges Feature: unused (button hit-tests use local sizes)
  // private float w;
  private float h;

  // Simple UI controls
  private int dragSlider = -1; // 0 master,1 music,2 sfx

  private static final int[][] COMMON_RES = {
      {3840, 2160},
      {3200, 1800},
      {2560, 1600},
      {2560, 1440},
      {2048, 1152},
      {1920, 1200},
      {1920, 1080},
      {1680, 1050},
      {1600, 900},
      {1440, 900},
      {1366, 768},
      {1280, 720},
  };

  public OptionsScreen(SurvivalGame game, BackAction back) {
    this.game = game;
    this.back = back;
  }

  @Override
  public void show() {
    // Options is UI-only; cursor must be visible.
    game.setCursorCrosshair();

    batch = new SpriteBatch();
    // UI font
    {
      FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ui.ttf"));
      FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
      p.size = 19; // ~15% smaller
      p.color = new com.badlogic.gdx.graphics.Color(0.05f, 0.05f, 0.05f, 1f);
      p.borderWidth = 1;
      p.borderColor = new com.badlogic.gdx.graphics.Color(0f, 0f, 0f, 1f);
      p.shadowOffsetX = 1;
      p.shadowOffsetY = -1;
      p.shadowColor = new com.badlogic.gdx.graphics.Color(0f, 0f, 0f, 0.45f);
      p.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
      p.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
      p.hinting = FreeTypeFontGenerator.Hinting.Full;
      font = gen.generateFont(p);
      font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
      gen.dispose();
    }
    layout = new GlyphLayout();
    ui = new UiRegions();

    // Nicht fertiges Feature: w = 520 * UI_SCALE; // (unused)
    h = 44 * UI_SCALE;
    recalcLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  @Override
  public void resize(int width, int height) {
    recalcLayout(width, height);
  }

  private void recalcLayout(int wScreen, int hScreen) {
    // Nicht fertiges Feature: unused layout cache
    // x0 = (wScreen - w) * 0.5f;
    // y0 = hScreen * 0.5f + 140;
    // Suppress "never read" hints for the params while the cache is disabled.
    if (wScreen == Integer.MIN_VALUE || hScreen == Integer.MIN_VALUE) throw new IllegalStateException();
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      SettingsIO.save(game.settings);
      back.goBack();
      return;
    }

    GameSettings s = game.settings;

    boolean changed = false;

    // Mouse UI
    float mx = Gdx.input.getX();
    float my = Gdx.graphics.getHeight() - Gdx.input.getY();

    float panelW0 = 820 * UI_SCALE;
    float panelH0 = 520 * UI_SCALE;
    float px00 = (Gdx.graphics.getWidth() - panelW0) * 0.5f;
    float py00 = (Gdx.graphics.getHeight() - panelH0) * 0.5f;

    float tx0 = px00 + 70 * UI_SCALE;
    float ty0 = py00 + panelH0 - 140 * UI_SCALE;

    // sliders
    float trackW0 = 420 * UI_SCALE;
    float knobW0 = 28 * UI_SCALE;
    float knobH0 = 34 * UI_SCALE;

    float s0y0 = ty0 - 16 * UI_SCALE;
    float s1y0 = ty0 - 58 * UI_SCALE;
    float s2y0 = ty0 - 100 * UI_SCALE;

    if (Gdx.input.justTouched() || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
      // Back button
      float bx = px00 + (panelW0 - 220 * UI_SCALE) * 0.5f;
      float by = py00 + 40 * UI_SCALE;
      if (hit(mx, my, bx, by, 220 * UI_SCALE, h)) {
        SettingsIO.save(game.settings);
        back.goBack();
        return;
      }

      if (hit(mx, my, tx0, s0y0, trackW0, knobH0)) dragSlider = 0;
      else if (hit(mx, my, tx0, s1y0, trackW0, knobH0)) dragSlider = 1;
      else if (hit(mx, my, tx0, s2y0, trackW0, knobH0)) dragSlider = 2;

      // Resolution buttons
      float resY = ty0 - 168 * UI_SCALE;
      float btnSize = 38 * UI_SCALE;
      float leftX = tx0 + 220 * UI_SCALE;
      float rightX = leftX + btnSize + 10 * UI_SCALE;
      if (hit(mx, my, leftX, resY - 26 * UI_SCALE, btnSize, btnSize)) {
        if (cycleResolution(s, +1)) changed = true;
      }
      if (hit(mx, my, rightX, resY - 26 * UI_SCALE, btnSize, btnSize)) {
        if (cycleResolution(s, -1)) changed = true;
      }
    }

    if (!Gdx.input.isTouched()) {
      dragSlider = -1;
    }

    if (dragSlider >= 0) {
      float v = (mx - tx0) / Math.max(1f, (trackW0 - knobW0));
      v = Math.max(0f, Math.min(1f, v));
      if (dragSlider == 0) { s.masterVolume = v; changed = true; }
      if (dragSlider == 1) { s.musicVolume = v; changed = true; }
      if (dragSlider == 2) { s.sfxVolume = v; changed = true; }
    }

    // Debug overlay hotkey is handled centrally via DebugCommands (keeps screens clean).

    if (changed) {
      s.clamp();
      game.audio.applySettings(s);
      SettingsIO.save(s);
      game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(s));
    }

    // Failsafe: ensure full-screen viewport (MenuScreen uses FitViewport which would otherwise persist).
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    Gdx.gl.glClearColor(0.02f, 0.02f, 0.04f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    // Panel background (same assets as inventory)
    float panelW = 820 * UI_SCALE;
    float panelH = 520 * UI_SCALE;
    float px0 = (Gdx.graphics.getWidth() - panelW) * 0.5f;
    float py0 = (Gdx.graphics.getHeight() - panelH) * 0.5f;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.panelSlots, px0, py0, panelW, panelH);

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(2f * UI_SCALE);
    layout.setText(font, "Options");
    font.draw(batch, layout, px0 + (panelW - layout.width) * 0.5f, py0 + panelH - 40 * UI_SCALE);

    font.getData().setScale(1.25f * UI_SCALE);
    float tx = px0 + 70 * UI_SCALE;
    float ty = py0 + panelH - 140 * UI_SCALE;

    font.draw(batch, "Master volume:  " + pct(s.masterVolume), tx, ty);
    font.draw(batch, "Music volume:   " + pct(s.musicVolume),  tx, ty - 42 * UI_SCALE);
    font.draw(batch, "SFX volume:     " + pct(s.sfxVolume),    tx, ty - 84 * UI_SCALE);
    font.draw(batch, "Debug overlay:  " + (s.showDebug ? "ON" : "OFF"), tx, ty - 126 * UI_SCALE);
    font.draw(batch, "Resolution:     " + resLabel(s), tx, ty - 168 * UI_SCALE);

    // sliders
    float trackW = 420 * UI_SCALE;
    float trackH = 16 * UI_SCALE;
    float knobW = 28 * UI_SCALE;
    float knobH = 34 * UI_SCALE;

    float s0y = ty - 16 * UI_SCALE;
    float s1y = ty - 58 * UI_SCALE;
    float s2y = ty - 100 * UI_SCALE;

    drawSlider(tx, s0y, trackW, trackH, knobW, knobH, s.masterVolume);
    drawSlider(tx, s1y, trackW, trackH, knobW, knobH, s.musicVolume);
    drawSlider(tx, s2y, trackW, trackH, knobW, knobH, s.sfxVolume);

    // Resolution buttons
    float resY = ty - 168 * UI_SCALE;
    float btnSize = 38 * UI_SCALE;
    float leftX = tx + 220 * UI_SCALE;
    float rightX = leftX + btnSize + 10 * UI_SCALE;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.button, leftX, resY - 26 * UI_SCALE, btnSize, btnSize);
    batch.draw(ui.button, rightX, resY - 26 * UI_SCALE, btnSize, btnSize);
    font.getData().setScale(1.15f * UI_SCALE);
    font.setColor(0.05f, 0.05f, 0.05f, 1f);
    layout.setText(font, "+");
    font.draw(batch, layout, leftX + (btnSize - layout.width) * 0.5f, resY - 26 * UI_SCALE + btnSize * 0.70f);
    layout.setText(font, "-");
    font.draw(batch, layout, rightX + (btnSize - layout.width) * 0.5f, resY - 26 * UI_SCALE + btnSize * 0.70f);

    // Back button
    float bx = px0 + (panelW - 220 * UI_SCALE) * 0.5f;
    float by = py0 + 40 * UI_SCALE;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.button, bx, by, 220 * UI_SCALE, h);
    font.getData().setScale(1.25f * UI_SCALE);
    layout.setText(font, "Back");
    font.draw(batch, layout, bx + (220 * UI_SCALE - layout.width) * 0.5f, by + h * 0.68f);

    batch.end();
  }

  /** Debug/Dev: toggles the global debug overlay setting (F3 in OptionsScreen). */
  public void debugToggleOverlaySetting() {
    try {
      GameSettings s = game.settings;
      s.showDebug = !s.showDebug;
      s.clamp();
      game.audio.applySettings(s);
      SettingsIO.save(s);
      game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(s));
    } catch (Throwable ignored) {}
  }

  private static String resLabel(GameSettings s) {
    int w = (s != null) ? s.fullscreenW : 0;
    int h = (s != null) ? s.fullscreenH : 0;
    if (w <= 0 || h <= 0) {
      // fallback: current mode
      try {
        return Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight();
      } catch (Throwable ignored) {}
      return "(default)";
    }
    return w + "x" + h;
  }

  private static boolean cycleResolution(GameSettings s, int dir) {
    if (s == null) return false;

    // Find current index
    int idx = -1;
    for (int i = 0; i < COMMON_RES.length; i++) {
      if (COMMON_RES[i][0] == s.fullscreenW && COMMON_RES[i][1] == s.fullscreenH) { idx = i; break; }
    }
    if (idx < 0) idx = 0;

    int next = idx + dir;
    if (next < 0) next = 0;
    if (next >= COMMON_RES.length) next = COMMON_RES.length - 1;
    if (next == idx) return false;

    int w = COMMON_RES[next][0];
    int h = COMMON_RES[next][1];

    // Apply only if a matching fullscreen mode exists.
    com.badlogic.gdx.Graphics.DisplayMode best = null;
    for (com.badlogic.gdx.Graphics.DisplayMode m : Gdx.graphics.getDisplayModes()) {
      if (m.width == w && m.height == h) {
        if (best == null || m.refreshRate > best.refreshRate) best = m;
      }
    }
    if (best == null) return false;

    try {
      Gdx.graphics.setFullscreenMode(best);
      s.fullscreenW = w;
      s.fullscreenH = h;
      return true;
    } catch (Throwable ignored) {
      return false;
    }
  }

  private void drawSlider(float x, float y, float trackW, float trackH, float knobW, float knobH, float value01) {
    float v = value01;
    if (v < 0f) v = 0f;
    if (v > 1f) v = 1f;

    // track
    batch.setColor(0.75f, 0.75f, 0.75f, 1f);
    batch.draw(ui.button, x, y + (knobH - trackH) * 0.5f, trackW, trackH);

    // fill
    batch.setColor(0.35f, 0.75f, 0.35f, 1f);
    batch.draw(ui.button, x, y + (knobH - trackH) * 0.5f, (trackW - knobW) * v + knobW, trackH);

    // knob
    float kx = x + (trackW - knobW) * v;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.button, kx, y, knobW, knobH);
  }

  private static String pct(float v) {
    int p = (int)(v * 100f);
    if (p < 0) p = 0;
    if (p > 100) p = 100;
    return p + "%";
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

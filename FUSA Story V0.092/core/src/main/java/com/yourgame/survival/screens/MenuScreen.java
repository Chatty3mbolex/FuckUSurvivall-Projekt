package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
// Music is managed globally by SurvivalGame
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.render.UiRegions;

public final class MenuScreen extends ScreenAdapter {
  private final SurvivalGame game;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private UiRegions ui;
  private Texture titleTex;
  private Texture bgTex;

  // Main menu music is managed globally by SurvivalGame.

  // Menu decorations
  private Texture middleFingerTex;
  private float pulseT = 0f;

  // Lightning overlay
  private Texture lightning1x1;
  private final Random lightningRnd = new Random();
  private float nextLightningIn = 7f;
  private int flashesLeft = 0;
  private float flashGap = 0f;
  private float flashT = 0f;
  private float flashDur = 0f;
  private float flashPeak = 0f;
  private float lightningAlpha = 0f;

  private static final float VW = 1920f;
  private static final float VH = 1080f;
  private static final float UI_SCALE = 1.65f; // applied inside virtual coords

  private OrthographicCamera cam;
  private Viewport viewport;

  private float btnX;
  private float btnYNew;
  private float btnYLoad;
  private float btnYLoadLast;
  private float btnYTutorial;
  private float btnYOptions;
  private float btnYCredits;
  private float btnYExit;
  private float btnW;
  private float btnH;

  public MenuScreen(SurvivalGame game) {
    this.game = game;
  }

  @Override
  public void show() {
    // Main menu is UI-only; cursor must be visible.
    game.setCursorCrosshair();

    // Menu music is managed globally (cross-screen) by SurvivalGame.
    game.ensureMenuMusicPlaying();

    batch = new SpriteBatch();
    // UI font
    {
      FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ui.ttf"));
      FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
      p.size = 15; // ~15% smaller
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
      // reduce pixelation when scaled
      font.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear, com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
      gen.dispose();
    }
    layout = new GlyphLayout();
    ui = new UiRegions();
    titleTex = new Texture(Gdx.files.internal("ui/title.png"));
    // Static main-menu background. Drawn stretched to fullscreen (requested).
    bgTex = new Texture(Gdx.files.internal("ui/mainmenu_bg.png"));
    bgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    // Decorative sprites
    // IMPORTANT: use internal assets so the game runs on other PCs (no absolute paths).
    try {
      middleFingerTex = new Texture(Gdx.files.internal("ui/MittelFinger.png"));
      middleFingerTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    } catch (Throwable ignored) {
      middleFingerTex = null;
    }

    // Lightning overlay texture (1x1 white)
    try {
      Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
      pm.setColor(Color.WHITE);
      pm.fill();
      lightning1x1 = new Texture(pm);
      pm.dispose();
    } catch (Throwable ignored) {
      lightning1x1 = null;
    }

    nextLightningIn = 5f + lightningRnd.nextFloat() * 10f; // 5..15s
    flashesLeft = 0;
    flashGap = 0f;
    flashT = 0f;
    flashDur = 0f;
    flashPeak = 0f;
    lightningAlpha = 0f;

    cam = new OrthographicCamera();
    viewport = new FitViewport(VW, VH, cam);
    viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    btnW = 260 * UI_SCALE * 0.85f; // 15% narrower
    btnH = 44 * UI_SCALE * 0.50f;  // 50% lower (height)
    recalcLayout((int)VW, (int)VH);
  }

  @Override
  public void resize(int width, int height) {
    if (viewport != null) viewport.update(width, height, true);
    recalcLayout((int)VW, (int)VH);
  }

  private void recalcLayout(int w, int h) {
    btnX = (w - btnW) * 0.5f;

    float gap = 16f * UI_SCALE * 0.50f; // 50% lower (spacing)

    // Anchor bottom button to screen bottom margin.
    float bottomMargin = 60f * UI_SCALE;

    btnYExit = bottomMargin;
    btnYCredits = btnYExit + (btnH + gap);
    btnYOptions = btnYCredits + (btnH + gap);
    btnYTutorial = btnYOptions + (btnH + gap);
    btnYLoadLast = btnYTutorial + (btnH + gap);
    btnYLoad = btnYLoadLast + (btnH + gap);
    btnYNew = btnYLoad + (btnH + gap);
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

    pulseT += delta;

    // --- lightning controller ---
    lightningAlpha = 0f;

    // wait between flashes
    if (flashGap > 0f) {
      flashGap -= delta;
      if (flashGap < 0f) flashGap = 0f;
    }

    // active flash
    if (flashDur > 0f) {
      flashT += delta;
      float rampUp = MathUtils.clamp(flashDur * 0.12f, 0.02f, 0.08f); // blitzschnell
      if (flashT <= rampUp) {
        lightningAlpha = flashPeak * (flashT / Math.max(0.0001f, rampUp));
      } else {
        float downT = flashT - rampUp;
        float downDur = Math.max(0.0001f, flashDur - rampUp);
        lightningAlpha = flashPeak * (1f - downT / downDur);
      }
      lightningAlpha = MathUtils.clamp(lightningAlpha, 0f, flashPeak);

      if (flashT >= flashDur) {
        flashDur = 0f;
        flashT = 0f;
        flashesLeft = Math.max(0, flashesLeft - 1);
        if (flashesLeft > 0) {
          flashGap = 0.05f + lightningRnd.nextFloat() * 0.20f;
        } else {
          nextLightningIn = 5f + lightningRnd.nextFloat() * 10f;
        }
      }
    } else {
      // no active flash
      if (flashesLeft > 0 && flashGap <= 0f) {
        flashDur = 0.10f + lightningRnd.nextFloat() * 0.40f; // 0.10..0.50
        flashPeak = 0.25f + lightningRnd.nextFloat() * 0.40f; // 0.25..0.65
        flashT = 0f;
      } else if (flashesLeft == 0) {
        nextLightningIn -= delta;
        if (nextLightningIn <= 0f) {
          flashesLeft = 1 + lightningRnd.nextInt(4); // 1..4
          flashGap = 0f;
        }
      }
    }

    // NOTE: windowed mode toggle removed by request (fullscreen only).

    // Mouse position (virtual coords if viewport is active)
    float mx = Gdx.input.getX();
    float my = Gdx.input.getY();
    if (viewport != null) {
      com.badlogic.gdx.math.Vector2 v = viewport.unproject(new com.badlogic.gdx.math.Vector2(mx, my));
      mx = v.x;
      my = v.y;
    } else {
      my = Gdx.graphics.getHeight() - my;
    }

    if (Gdx.input.justTouched()) {
      float x = mx;
      float y = my;
      if (hit(x, y, btnX, btnYNew, btnW, btnH)) {
        // New Game must always use a fresh random seed.
        stopMenuMusicForGameplay();
        game.setScreen(new GameScreen(game));
        return;
      }
      if (hit(x, y, btnX, btnYLoad, btnW, btnH)) {
        // Load screen is still "menu" context => keep menu music running.
        GameScreen gs = new GameScreen(game);
        game.setScreen(new SaveLoadScreen(game, gs, SaveLoadScreen.Mode.LOAD, () -> game.setScreen(new MenuScreen(game))));
        return;
      }
      if (hit(x, y, btnX, btnYLoadLast, btnW, btnH)) {
        int bestSlot = findMostRecentSlot();
        if (bestSlot > 0) {
          stopMenuMusicForGameplay();
          GameScreen gs = new GameScreen(game);
          gs.doLoad(bestSlot);
          game.setScreen(gs);
        }
        return;
      }
      if (hit(x, y, btnX, btnYTutorial, btnW, btnH)) {
        game.setScreen(new TutorialScreen(game, () -> game.setScreen(new MenuScreen(game))));
        return;
      }
      if (hit(x, y, btnX, btnYOptions, btnW, btnH)) {
        game.setScreen(new OptionsScreen(game, () -> game.setScreen(new MenuScreen(game))));
        return;
      }
      if (hit(x, y, btnX, btnYCredits, btnW, btnH)) {
        game.setScreen(new CreditsScreen(game, () -> game.setScreen(new MenuScreen(game))));
        return;
      }
      if (hit(x, y, btnX, btnYExit, btnW, btnH)) {
        Gdx.app.exit();
        return;
      }
    }

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (viewport != null) {
      viewport.apply();
      batch.setProjectionMatrix(cam.combined);
    }

    batch.begin();

    // Background: stretch-to-fill.
    if (bgTex != null) {
      batch.setColor(1f, 1f, 1f, 1f);
      batch.draw(bgTex, 0f, 0f, VW, VH);
    }

    // Debug failsafe: force visible text color
    font.setColor(1f, 1f, 1f, 1f);

    // Title sprite (transparent PNG)
    float tw = 960f * 1.10f; // +10% width
    float th = 640f * 1.15f; // +15% height
    float tx = (VW - tw) * 0.5f;
    float ty = VH - th - 20f; // ~50% less top margin than before (40 -> 20)

    // Decorative middle fingers (SOLL)
    if (middleFingerTex != null) {
      float baseW = 512f;
      float baseH = 512f;

      // pulse: every 3s, for ~1.2s grow/shrink up to +10% with smooth interpolation
      float t = pulseT % 3f;
      float a = 0f;
      if (t < 0.6f) a = t / 0.6f;          // grow 0..1
      else if (t < 1.2f) a = 1f - (t - 0.6f) / 0.6f; // shrink 1..0
      a = MathUtils.clamp(a, 0f, 1f);
      // smoothstep
      a = a * a * (3f - 2f * a);
      float s = 1f + 0.10f * a;

      float dw = baseW * s;
      float dh = baseH * s;

      // positions roughly matching the mock (left/right of buttons)
      float lx = 300f;
      float ly = 65.0f;
      float rx = VW - 300f - dw;
      float ry = 65.0f;

      // left: rotate +12Â° clockwise
      batch.setColor(1f, 1f, 1f, 1f);
      batch.draw(middleFingerTex,
          lx, ly,
          dw * 0.5f, dh * 0.5f,
          dw, dh,
          1f, 1f,
          -12f,
          0, 0,
          middleFingerTex.getWidth(), middleFingerTex.getHeight(),
          false, false);

      // right: mirror + rotate 12Â° counter-clockwise
      batch.draw(middleFingerTex,
          rx, ry,
          dw * 0.5f, dh * 0.5f,
          dw, dh,
          1f, 1f,
          12f,
          0, 0,
          middleFingerTex.getWidth(), middleFingerTex.getHeight(),
          true, false);
    }

    if (titleTex != null) {
      // Blue glow outline: draw the title several times slightly offset behind the real sprite.
      // Keeps the original title asset unchanged.
      batch.setColor(0.25f, 0.65f, 1.0f, 0.35f);
      float g = 3.0f; // glow offset in px
      batch.draw(titleTex, tx - g, ty, tw, th);
      batch.draw(titleTex, tx + g, ty, tw, th);
      batch.draw(titleTex, tx, ty - g, tw, th);
      batch.draw(titleTex, tx, ty + g, tw, th);
      batch.draw(titleTex, tx - g, ty - g, tw, th);
      batch.draw(titleTex, tx + g, ty - g, tw, th);
      batch.draw(titleTex, tx - g, ty + g, tw, th);
      batch.draw(titleTex, tx + g, ty + g, tw, th);

      // Real title on top
      batch.setColor(1f, 1f, 1f, 1f);
      batch.draw(titleTex, tx, ty, tw, th);
    }

    font.getData().setScale(1.2f * UI_SCALE);

    drawButton("New Game", btnX, btnYNew, hit(mx, my, btnX, btnYNew, btnW, btnH));
    drawButton("Load", btnX, btnYLoad, hit(mx, my, btnX, btnYLoad, btnW, btnH));
    drawButton("Load Last", btnX, btnYLoadLast, hit(mx, my, btnX, btnYLoadLast, btnW, btnH));
    drawButton("Tutorial", btnX, btnYTutorial, hit(mx, my, btnX, btnYTutorial, btnW, btnH));
    drawButton("Options", btnX, btnYOptions, hit(mx, my, btnX, btnYOptions, btnW, btnH));
    drawButton("Credits", btnX, btnYCredits, hit(mx, my, btnX, btnYCredits, btnW, btnH));
    drawButton("Exit", btnX, btnYExit, hit(mx, my, btnX, btnYExit, btnW, btnH));

    // Lightning overlay (over everything, does not block input)
    if (lightning1x1 != null && lightningAlpha > 0.001f) {
      batch.setColor(0.85f, 0.92f, 1.00f, lightningAlpha);
      batch.draw(lightning1x1, 0f, 0f, VW, VH);
      batch.setColor(1f, 1f, 1f, 1f);
    }

    batch.end();
  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    if (bgTex != null) {
      bgTex.dispose();
      bgTex = null;
    }
    if (titleTex != null) {
      titleTex.dispose();
      titleTex = null;
    }
    if (ui != null) {
      ui.dispose();
      ui = null;
    }
    if (font != null) {
      font.dispose();
      font = null;
    }
    if (batch != null) {
      batch.dispose();
      batch = null;
    }
    if (middleFingerTex != null) {
      middleFingerTex.dispose();
      middleFingerTex = null;
    }
    if (lightning1x1 != null) {
      lightning1x1.dispose();
      lightning1x1 = null;
    }
    // menu music is managed globally by SurvivalGame
  }

  private void drawButton(String text, float x, float y, boolean hover) {
    // Hover effect: you can't "brighten" above 1.0 reliably, so we darken normal and use full bright on hover.
    if (hover) batch.setColor(1.00f, 1.00f, 1.00f, 1f);
    else batch.setColor(0.85f, 0.85f, 0.85f, 1f);
    batch.draw(ui.button, x, y, btnW, btnH);

    // Text: slightly brighter on hover.
    if (hover) font.setColor(0.00f, 0.00f, 0.00f, 1f);
    else font.setColor(0.10f, 0.10f, 0.10f, 1f);

    layout.setText(font, text);
    float tx = x + (btnW - layout.width) * 0.5f;
    float ty = y + btnH * 1.08f; // move text up (requested)
    font.draw(batch, layout, tx, ty);

    font.setColor(1f, 1f, 1f, 1f);
  }

  private static boolean hit(float px, float py, float x, float y, float w, float h) {
    return px >= x && px <= x + w && py >= y && py <= y + h;
  }

  private void stopMenuMusicForGameplay() {
    // Menu music is managed globally by SurvivalGame; this just triggers fade-out.
    game.fadeOutMenuMusic(1.0f);
  }

  private static int findMostRecentSlot() {
    int bestSlot = -1;
    long bestMtime = -1L;
    for (int slot = 1; slot <= 8; slot++) {
      com.badlogic.gdx.files.FileHandle fh = com.badlogic.gdx.Gdx.files.local(com.yourgame.survival.data.SaveManager.slotPath(slot));
      if (!fh.exists()) continue;
      long m = 0L;
      try { m = fh.file().lastModified(); } catch (Throwable ignored) {}
      if (m > bestMtime) { bestMtime = m; bestSlot = slot; }
    }
    return bestSlot;
  }

}

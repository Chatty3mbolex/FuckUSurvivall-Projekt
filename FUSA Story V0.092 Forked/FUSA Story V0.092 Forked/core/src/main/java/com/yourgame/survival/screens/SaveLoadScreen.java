package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.data.SaveManager;
import com.yourgame.survival.render.UiRegions;

/** Save/Load slot picker screen (8 vertical slots). */
public final class SaveLoadScreen extends ScreenAdapter {
  public enum Mode { SAVE, LOAD }

  private final SurvivalGame game;
  private final GameScreen resumeTo;
  private final Mode mode;
  private final Runnable onBack;

  private static final float UI_SCALE = 1.65f;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private UiRegions ui;

  private float x;
  private float yTop;
  private float w;
  private float h;

  // Delete mode (LOAD screen)
  private boolean deleteMode = false;
  private int hoverSlot = -1;
  private int pendingDeleteSlot = -1;

  // Save naming (SAVE screen)
  private boolean namingMode = false;
  private int namingSlot = -1;
  private String nameBuf = "";
  private InputAdapter input;

  // Delete UI button
  private float delX;
  private float delY;
  private float delW;
  private float delH;

  // Confirm UI
  private float confirmX;
  private float confirmY;
  private float confirmW;
  private float confirmH;
  private float cancelX;
  private float cancelY;
  private float cancelW;
  private float cancelH;

  public SaveLoadScreen(SurvivalGame game, GameScreen resumeTo, Mode mode) {
    this(game, resumeTo, mode, () -> game.setScreen(new PauseScreen(game, resumeTo)));
  }

  public SaveLoadScreen(SurvivalGame game, GameScreen resumeTo, Mode mode, Runnable onBack) {
    this.game = game;
    this.resumeTo = resumeTo;
    this.mode = mode;
    this.onBack = onBack;
  }

  @Override
  public void show() {
    batch = new SpriteBatch();
    font = new BitmapFont();
    layout = new GlyphLayout();
    ui = new UiRegions();

    w = 380 * UI_SCALE;
    h = 44 * UI_SCALE;
    recalcLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    input = new InputAdapter() {
      @Override
      public boolean keyDown(int keycode) {
        if (!namingMode) return false;
        if (keycode == Input.Keys.ESCAPE) {
          namingMode = false;
          namingSlot = -1;
          nameBuf = "";
          return true;
        }
        if (keycode == Input.Keys.ENTER) {
          String n = (nameBuf == null || nameBuf.trim().isEmpty()) ? ("Slot " + namingSlot) : nameBuf.trim();
          resumeTo.doSave(namingSlot, n);
          namingMode = false;
          namingSlot = -1;
          nameBuf = "";
          onBack.run();
          return true;
        }
        if (keycode == Input.Keys.BACKSPACE) {
          if (!nameBuf.isEmpty()) nameBuf = nameBuf.substring(0, nameBuf.length() - 1);
          return true;
        }
        return false;
      }

      @Override
      public boolean keyTyped(char character) {
        if (!namingMode) return false;
        if (character < 32) return false;
        if (nameBuf.length() >= 24) return true;
        // allow basic ascii + space
        if (character == ' ' || character == '-' || character == '_' || character == '.'
            || (character >= '0' && character <= '9')
            || (character >= 'A' && character <= 'Z')
            || (character >= 'a' && character <= 'z')) {
          nameBuf += character;
          return true;
        }
        return false;
      }
    };
    Gdx.input.setInputProcessor(input);
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

    // Delete button (below slot list)
    delW = 200 * UI_SCALE;
    delH = 40 * UI_SCALE;
    delX = (wScreen - delW) * 0.5f;
    delY = yTop - (n - 1) * 60f - 80f;

    // Confirm/cancel buttons (centered)
    confirmW = 260 * UI_SCALE;
    confirmH = 44 * UI_SCALE;
    cancelW = 260 * UI_SCALE;
    cancelH = 44 * UI_SCALE;
    float midX = (wScreen - confirmW) * 0.5f;
    float midY = hScreen * 0.5f;

    confirmX = midX;
    confirmY = midY + 18f * UI_SCALE;
    cancelX = midX;
    cancelY = midY - 60f * UI_SCALE;
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      if (namingMode) {
        namingMode = false;
        namingSlot = -1;
        nameBuf = "";
      } else {
        onBack.run();
      }
      return;
    }

    float mx = Gdx.input.getX();
    float my = Gdx.graphics.getHeight() - Gdx.input.getY();

    // Hover slot tracking (for red highlight in delete mode)
    hoverSlot = -1;
    for (int i = 0; i < 8; i++) {
      float by = yTop - i * 60f;
      if (hit(mx, my, x, by, w, h)) {
        hoverSlot = i + 1;
        break;
      }
    }

    if (Gdx.input.justTouched()) {
      // Confirm delete overlay handling
      if (pendingDeleteSlot > 0) {
        if (hit(mx, my, confirmX, confirmY, confirmW, confirmH)) {
          com.badlogic.gdx.files.FileHandle fh = Gdx.files.local(SaveManager.slotPath(pendingDeleteSlot));
          if (fh.exists()) fh.delete();
          pendingDeleteSlot = -1;
          return;
        }
        if (hit(mx, my, cancelX, cancelY, cancelW, cancelH)) {
          pendingDeleteSlot = -1;
          return;
        }
        // click elsewhere ignored while confirm is up
        return;
      }

      // Toggle delete mode (only on LOAD screen)
      if (mode == Mode.LOAD && hit(mx, my, delX, delY, delW, delH)) {
        deleteMode = !deleteMode;
        pendingDeleteSlot = -1;
        return;
      }

      // Slot click
      for (int i = 0; i < 8; i++) {
        float by = yTop - i * 60f;
        if (!hit(mx, my, x, by, w, h)) continue;

        int slot = i + 1;
        boolean exists = SaveManager.slotExists(slot);

        if (mode == Mode.SAVE) {
          // Ask for a save name (feature). Enter confirms, ESC cancels.
          namingMode = true;
          namingSlot = slot;
          String existing = SaveManager.slotName(slot);
          nameBuf = (existing == null || existing.isEmpty()) ? ("Slot " + slot) : existing;
          return;
        }

        if (mode == Mode.LOAD) {
          if (!exists) return;

          if (deleteMode) {
            // Arm confirmation; hovered slot becomes red via draw.
            pendingDeleteSlot = slot;
            return;
          }

          // Normal load behavior
          // Fade out menu music when entering gameplay (music is managed globally in SurvivalGame).
          game.fadeOutMenuMusic(1.0f);
          resumeTo.doLoad(slot);
          game.setScreen(resumeTo);
          return;
        }
      }
    }

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    font.getData().setScale(2f * UI_SCALE);
    String title = (mode == Mode.SAVE) ? "Save Game (ESC back)" : "Load Game (ESC back)";
    layout.setText(font, title);
    font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, Gdx.graphics.getHeight() - 40);

    font.getData().setScale(1.2f * UI_SCALE);
    for (int i = 0; i < 8; i++) {
      int slot = i + 1;
      boolean exists = SaveManager.slotExists(slot);
      String nm = exists ? SaveManager.slotName(slot) : "";
      String text = exists
          ? (nm != null && !nm.isEmpty() ? ("Slot " + slot + " - " + nm) : ("Slot " + slot))
          : ("Slot " + slot + " (EMPTY)");

      float by = yTop - i * 60f;

      boolean highlightRed = (mode == Mode.LOAD) && deleteMode && (pendingDeleteSlot <= 0) && (hoverSlot == slot) && exists;
      drawButton(text, x, by, highlightRed ? 1f : 1f, highlightRed ? 0.25f : 1f, highlightRed ? 0.25f : 1f);
    }

    // Delete button only for LOAD
    if (mode == Mode.LOAD) {
      String delText = deleteMode ? "Delete (ON)" : "Delete";
      boolean red = deleteMode;
      drawButton(delText, delX, delY, red ? 1f : 1f, red ? 0.35f : 1f, red ? 0.35f : 1f, delW, delH);
    }

    // Naming overlay (SAVE)
    if (namingMode && mode == Mode.SAVE && namingSlot > 0) {
      font.getData().setScale(1.3f * UI_SCALE);
      String msg = "Save name (Slot " + namingSlot + ")";
      layout.setText(font, msg);
      font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, Gdx.graphics.getHeight() * 0.5f + 160f * UI_SCALE);

      font.getData().setScale(1.2f * UI_SCALE);
      String line = "> " + nameBuf;
      layout.setText(font, line);
      font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, Gdx.graphics.getHeight() * 0.5f + 110f * UI_SCALE);

      font.getData().setScale(1.0f * UI_SCALE);
      layout.setText(font, "ENTER = save   |   ESC = cancel   |   BACKSPACE = delete char");
      font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, Gdx.graphics.getHeight() * 0.5f + 75f * UI_SCALE);
    }

    // Confirm overlay
    if (pendingDeleteSlot > 0) {
      font.getData().setScale(1.4f * UI_SCALE);
      String msg = "Delete Slot " + pendingDeleteSlot + " ?";
      layout.setText(font, msg);
      font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) * 0.5f, Gdx.graphics.getHeight() * 0.5f + 120f * UI_SCALE);

      font.getData().setScale(1.2f * UI_SCALE);
      drawButton("CONFIRM DELETE", confirmX, confirmY, 1f, 0.25f, 0.25f, confirmW, confirmH);
      drawButton("CANCEL", cancelX, cancelY, 1f, 1f, 1f, cancelW, cancelH);
    }

    batch.end();
  }


  private void drawButton(String text, float x, float y, float r, float g, float b) {
    drawButton(text, x, y, r, g, b, w, h);
  }

  private void drawButton(String text, float x, float y, float r, float g, float b, float bw, float bh) {
    batch.setColor(r, g, b, 1f);
    batch.draw(ui.button, x, y, bw, bh);

    batch.setColor(1f, 1f, 1f, 1f);
    layout.setText(font, text);
    float tx = x + (bw - layout.width) * 0.5f;
    float ty = y + bh * 0.68f;
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
    // Restore default input processing.
    try { if (Gdx.input.getInputProcessor() == input) Gdx.input.setInputProcessor(null); } catch (Throwable ignored) {}
  }
}

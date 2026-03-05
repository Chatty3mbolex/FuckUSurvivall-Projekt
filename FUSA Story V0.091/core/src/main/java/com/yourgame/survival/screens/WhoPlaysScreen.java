package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.data.PlayerProfile;
import com.yourgame.survival.data.PlayerProfileManager;

import java.util.ArrayList;

/**
 * First screen (before Menu): asks "Wer spielt?" and lets user create/select a local profile.
 *
 * Stores only:
 * - localId (random 8 digits)
 * - username
 */
public final class WhoPlaysScreen implements Screen {
  private static final float UI_SCALE = 1f;

  private final SurvivalGame game;
  private final Runnable onDone;

  private SpriteBatch batch;
  private BitmapFont font;
  private GlyphLayout layout;
  private com.yourgame.survival.render.UiRegions ui;

  private ArrayList<PlayerProfile> profiles;
  private int selected = 0;

  private boolean creating = false;
  private String newName = "";
  private String pendingId = "";

  private float btnW = 520f * UI_SCALE;
  private float btnH = 52f * UI_SCALE;

  private float panelW;
  private float panelH;
  private float px0;
  private float py0;

  public WhoPlaysScreen(SurvivalGame game, Runnable onDone) {
    this.game = game;
    this.onDone = onDone;
  }

  @Override
  public void show() {
    batch = new SpriteBatch();
    font = new BitmapFont();
    layout = new GlyphLayout();
    ui = new com.yourgame.survival.render.UiRegions();

    profiles = PlayerProfileManager.loadAll();
    selected = Math.min(Math.max(0, selected), Math.max(0, profiles.size() - 1));
    recalcLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // Start in "Schonmal gespielt?" mode: if no profiles exist, go directly to create.
    if (profiles.isEmpty()) {
      creating = true;
      pendingId = PlayerProfileManager.generateUniqueLocalId(profiles);
    }
  }

  @Override
  public void resize(int width, int height) {
    recalcLayout(width, height);
  }

  private void recalcLayout(int w, int h) {
    panelW = 920f * UI_SCALE;
    panelH = 560f * UI_SCALE;
    px0 = (w - panelW) * 0.5f;
    py0 = (h - panelH) * 0.5f;
  }

  @Override
  public void render(float delta) {
    // ESC: do nothing here; this must be the first screen.

    // Input typing for create
    if (creating) {
      handleTyping();
    } else {
      handleSelectInput();
    }

    // Mouse UI
    float mx = Gdx.input.getX();
    float my = Gdx.graphics.getHeight() - Gdx.input.getY();

    // Buttons
    float y = py0 + panelH - 130f * UI_SCALE;

    boolean hitNew = hit(mx, my, px0 + (panelW - btnW) * 0.5f, y - 80f * UI_SCALE, btnW, btnH);
    boolean hitExisting = hit(mx, my, px0 + (panelW - btnW) * 0.5f, y - 150f * UI_SCALE, btnW, btnH);

    if (!profiles.isEmpty()) {
      if (Gdx.input.justTouched()) {
        if (hitExisting) creating = false;
        if (hitNew) {
          creating = true;
          newName = "";
          pendingId = PlayerProfileManager.generateUniqueLocalId(profiles);
        }
      }
    }

    // confirm buttons
    if (Gdx.input.justTouched()) {
      if (creating) {
        float bx = px0 + (panelW - btnW) * 0.5f;
        float by = py0 + 70f * UI_SCALE;
        if (hit(mx, my, bx, by, btnW, btnH)) {
          confirmCreate();
        }
      } else {
        float bx = px0 + (panelW - btnW) * 0.5f;
        float by = py0 + 70f * UI_SCALE;
        if (hit(mx, my, bx, by, btnW, btnH)) {
          confirmSelect();
        }
      }
    }

    // Draw
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    // Panel bg
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(ui.panelSlots, px0, py0, panelW, panelH);

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(2.2f * UI_SCALE);
    drawText("WER SPIELT?", px0 + 50f * UI_SCALE, py0 + panelH - 40f * UI_SCALE);

    font.getData().setScale(1.2f * UI_SCALE);

    if (profiles.isEmpty()) {
      drawText("Noch kein Profil gefunden.", px0 + 50f * UI_SCALE, py0 + panelH - 95f * UI_SCALE);
    } else {
      // Choice buttons (only if profiles exist)
      float bx = px0 + (panelW - btnW) * 0.5f;
      drawButton("Schonmal gespielt? (Profil waehlen)", bx, y - 150f * UI_SCALE, !creating);
      drawButton("Neues Profil erstellen", bx, y - 80f * UI_SCALE, creating);
    }

    // Content
    if (creating) {
      drawCreateUI();
    } else {
      drawSelectUI();
    }

    batch.end();
  }

  private void drawCreateUI() {
    float x = px0 + 70f * UI_SCALE;
    float y = py0 + panelH - 220f * UI_SCALE;

    drawText("Wie heisst du?", x, y);
    drawText("Name: " + (newName.length() == 0 ? "_" : newName), x, y - 40f * UI_SCALE);

    drawText("Lokale PlayerID (8 digits): " + pendingId, x, y - 90f * UI_SCALE);
    drawText("(ENTER = OK)", x, y - 125f * UI_SCALE);

    float bx = px0 + (panelW - btnW) * 0.5f;
    float by = py0 + 70f * UI_SCALE;
    drawButton("OK", bx, by, true);
  }

  private void drawSelectUI() {
    float x = px0 + 70f * UI_SCALE;
    float y = py0 + panelH - 220f * UI_SCALE;

    drawText("Waehle ein Profil:", x, y);

    float rowY = y - 50f * UI_SCALE;
    for (int i = 0; i < profiles.size(); i++) {
      PlayerProfile p = profiles.get(i);
      String line = (i == selected ? "> " : "  ") + p.username + "  (#" + p.localId + ")";
      drawText(line, x, rowY - i * 34f * UI_SCALE);
    }

    drawText("UP/DOWN = Auswahl   ENTER = OK", x, py0 + 150f * UI_SCALE);

    float bx = px0 + (panelW - btnW) * 0.5f;
    float by = py0 + 70f * UI_SCALE;
    drawButton("OK", bx, by, true);
  }

  private void confirmCreate() {
    String name = newName == null ? "" : newName.trim();
    if (name.length() == 0) return;

    // Create + save
    PlayerProfile p = new PlayerProfile(pendingId, name);
    profiles.add(p);
    PlayerProfileManager.saveAll(profiles);

    // Select it
    applyProfile(p);
  }

  private void confirmSelect() {
    if (profiles.isEmpty()) return;
    selected = Math.min(Math.max(0, selected), profiles.size() - 1);
    applyProfile(profiles.get(selected));
  }

  private void applyProfile(PlayerProfile p) {
    if (p == null) return;
    game.setLocalPlayerIdentity(p.localId, p.username);
    if (onDone != null) onDone.run();
  }

  private void handleSelectInput() {
    if (profiles == null) return;
    if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) selected--;
    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) selected++;
    if (profiles.size() > 0) {
      if (selected < 0) selected = profiles.size() - 1;
      if (selected >= profiles.size()) selected = 0;
    } else {
      selected = 0;
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      confirmSelect();
    }
  }

  private void handleTyping() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      confirmCreate();
      return;
    }
    // backspace: delete char (IMPORTANT: do not use it for navigation!)
    if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
      if (newName.length() > 0) newName = newName.substring(0, newName.length() - 1);
    }

    // letters A-Z
    for (int k = Input.Keys.A; k <= Input.Keys.Z; k++) {
      if (Gdx.input.isKeyJustPressed(k)) {
        char c = (char) ('a' + (k - Input.Keys.A));
        // shift -> uppercase
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
          c = Character.toUpperCase(c);
        }
        newName = appendLimited(newName, c, 18);
      }
    }

    // digits
    for (int k = Input.Keys.NUM_0; k <= Input.Keys.NUM_9; k++) {
      if (Gdx.input.isKeyJustPressed(k)) {
        char c = (char)('0' + (k - Input.Keys.NUM_0));
        newName = appendLimited(newName, c, 18);
      }
    }

    // space
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      newName = appendLimited(newName, ' ', 18);
    }

    // hyphen/underscore/period
    if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) newName = appendLimited(newName, '-', 18);
    if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) newName = appendLimited(newName, '.', 18);
  }

  private static String appendLimited(String s, char c, int maxLen) {
    if (s == null) s = "";
    if (s.length() >= maxLen) return s;
    return s + c;
  }

  private void drawButton(String text, float x, float y, boolean hover) {
    float bw = btnW;
    float bh = btnH;
    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(hover ? ui.buttonPressed : ui.button, x, y, bw, bh);
    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(1.2f * UI_SCALE);
    layout.setText(font, text);
    batch.setColor(1f, 1f, 1f, 1f);
    font.draw(batch, text, x + (bw - layout.width) * 0.5f, y + (bh + layout.height) * 0.62f);
  }

  private void drawText(String text, float x, float y) {
    if (text == null) text = "";
    layout.setText(font, text);
    font.draw(batch, text, x, y);
  }

  private static boolean hit(float mx, float my, float x, float y, float w, float h) {
    return mx >= x && mx <= x + w && my >= y && my <= y + h;
  }

  @Override public void pause() {}
  @Override public void resume() {}
  @Override public void hide() {}

  @Override
  public void dispose() {
    try { if (batch != null) batch.dispose(); } catch (Throwable ignored) {}
    try { if (font != null) font.dispose(); } catch (Throwable ignored) {}
    try { if (ui != null) ui.dispose(); } catch (Throwable ignored) {}
    batch = null;
    font = null;
    ui = null;
  }
}

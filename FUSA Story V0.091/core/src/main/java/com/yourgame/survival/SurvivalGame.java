package com.yourgame.survival;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.yourgame.survival.data.SettingsIO;
import com.yourgame.survival.editor.WorldEditorScreen;
import com.yourgame.survival.screens.MenuScreen;
import com.yourgame.survival.screens.TileEditorScreen;
import com.yourgame.survival.screens.WhoPlaysScreen;
import com.yourgame.survival.tools.asseteditor.AssetEditorScreen;

public final class SurvivalGame extends Game {
  public final GameSettings settings = new GameSettings();
  public final com.yourgame.survival.audio.AudioBus audio = new com.yourgame.survival.audio.AudioBus();

  // Player identity (selected on the first screen before the menu)
  private String localPlayerId = ""; // 8 digits
  private String username = "";

  // Menu music is managed globally (cross-screen) and can be faded out on transitions to gameplay.
  private com.badlogic.gdx.audio.Music menuMusic;
  private float menuMusicFadeT = 20f;
  private float menuMusicFadeDur = 20f;
  private boolean menuMusicFadingOut = true;
  private float menuMusicFadeK = 1f;

  private final boolean startTileEditor;
  private final boolean startWorldEditor;

  private boolean bootAssetEditor = false;
  private boolean bootAssetEditorBatch = false;

  private Cursor cursorCrosshair;
  private Cursor cursorHidden;
  private boolean cursorIsHidden = false;

  public SurvivalGame() {
    this(false, false);
  }

  /** Desktop-only: when true, launches directly into the external World Editor or Tile Editor. */
  public SurvivalGame(boolean startWorldEditor, boolean startTileEditor) {
    this.startWorldEditor = startWorldEditor;
    this.startTileEditor = startTileEditor;
  }

  public void setBootModeAssetEditor(boolean batch) {
    this.bootAssetEditor = true;
    this.bootAssetEditorBatch = batch;
  }

  @Override
  public void create() {
    // load non-savegame settings
    SettingsIO.loadInto(settings);
    settings.clamp();
    audio.applySettings(settings);

    // apply preferred fullscreen resolution if set
    if (settings.fullscreenW > 0 && settings.fullscreenH > 0) {
      try {
        com.badlogic.gdx.Graphics.DisplayMode best = null;
        for (com.badlogic.gdx.Graphics.DisplayMode m : com.badlogic.gdx.Gdx.graphics.getDisplayModes()) {
          if (m.width == settings.fullscreenW && m.height == settings.fullscreenH) {
            if (best == null || m.refreshRate > best.refreshRate) best = m;
          }
        }
        if (best != null) com.badlogic.gdx.Gdx.graphics.setFullscreenMode(best);
      } catch (Throwable ignored) {}
    }

    if (bootAssetEditor) {
      setScreen(new AssetEditorScreen(this, bootAssetEditorBatch));
    } else if (startTileEditor) {
      setScreen(new TileEditorScreen(this));
    } else if (startWorldEditor) {
      setScreen(new WorldEditorScreen(this));
    } else {
      // Always ask "Wer spielt?" first (before the menu)
      setScreen(new WhoPlaysScreen(this, () -> setScreen(new MenuScreen(this))));
    }

    // Desktop cursor: default is a small crosshair; gameplay can temporarily hide it.
    initCursors();
    setCursorCrosshair();
  }

  public void setLocalPlayerIdentity(String localPlayerId, String username) {
    this.localPlayerId = (localPlayerId == null) ? "" : localPlayerId.trim();
    this.username = (username == null) ? "" : username.trim();
  }

  public String getLocalPlayerId() {
    return localPlayerId;
  }

  public String getUsername() {
    return username;
  }

  public String getPlayerLabel() {
    if (username == null || username.trim().isEmpty()) return "";
    if (localPlayerId == null || localPlayerId.trim().isEmpty()) return username;
    return username + " (#" + localPlayerId + ")";
  }

  @Override
  public void render() {
    tickMenuMusic(Gdx.graphics.getDeltaTime());
    super.render();
  }

  /** Ensures menu music is playing (it is managed globally, cross-screen). */
  public void ensureMenuMusicPlaying() {
    try {
      if (menuMusic == null) {
        menuMusic = Gdx.audio.newMusic(Gdx.files.absolute(
            "C:/Users/kuehn/Downloads/beetpro-deep-sea-connect-dolphins-gaia-lemuria-pachamama-spiritual-frequency-16-12360.mp3"));
        menuMusic.setLooping(true);
        menuMusicFadeT = 0f;
        menuMusicFadeDur = 0f;
        menuMusicFadingOut = false;
        menuMusicFadeK = 1f;
        menuMusic.play();
      } else {
        if (!menuMusic.isPlaying()) menuMusic.play();
      }
    } catch (Throwable ignored) {
      try { if (menuMusic != null) menuMusic.dispose(); } catch (Throwable ignored2) {}
      menuMusic = null;
      menuMusicFadeT = 0f;
      menuMusicFadeDur = 0f;
      menuMusicFadingOut = false;
      menuMusicFadeK = 1f;
    }
  }

  /** Starts fading out the menu music over {@code seconds}. Music is disposed when fade completes. */
  public void fadeOutMenuMusic(float seconds) {
    if (menuMusic == null) return;
    if (seconds <= 0f) seconds = 0.001f;
    menuMusicFadeT = 0f;
    menuMusicFadeDur = seconds;
    menuMusicFadingOut = true;
  }

  private void tickMenuMusic(float delta) {
    if (menuMusic == null) return;

    // Fade factor
    if (menuMusicFadingOut) {
      menuMusicFadeT += delta;
      float t = (menuMusicFadeDur <= 0f) ? 1f : (menuMusicFadeT / menuMusicFadeDur);
      if (t < 0f) t = 0f;
      if (t > 1f) t = 1f;
      menuMusicFadeK = 1f - t;
      if (menuMusicFadeK < 0f) menuMusicFadeK = 0f;

      if (t >= 1f || menuMusicFadeK <= 0.001f) {
        try { menuMusic.stop(); } catch (Throwable ignored) {}
        try { menuMusic.dispose(); } catch (Throwable ignored) {}
        menuMusic = null;
        menuMusicFadeT = 0f;
        menuMusicFadeDur = 0f;
        menuMusicFadingOut = false;
        menuMusicFadeK = 1f;
        return;
      }
    }

    // Apply settings volume continuously
    float v = settings.masterVolume * settings.musicVolume * menuMusicFadeK;
    if (v < 0f) v = 0f;
    if (v > 1f) v = 1f;
    try { menuMusic.setVolume(v); } catch (Throwable ignored) {}
  }

  private void initCursors() {
    try {
      if (Gdx.app == null || Gdx.app.getType() != Application.ApplicationType.Desktop) return;

      // Crosshair cursor (assets/ui/cursor_crosshair.png)
      Pixmap pm = new Pixmap(Gdx.files.internal("ui/cursor_crosshair.png"));
      cursorCrosshair = Gdx.graphics.newCursor(pm, pm.getWidth() / 2, pm.getHeight() / 2);
      pm.dispose();

      // Fully transparent cursor for "hidden" mode.
      Pixmap blank = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
      blank.setColor(0f, 0f, 0f, 0f);
      blank.fill();
      cursorHidden = Gdx.graphics.newCursor(blank, 0, 0);
      blank.dispose();
    } catch (Throwable ignored) {
      cursorCrosshair = null;
      cursorHidden = null;
    }
  }

  public void setCursorCrosshair() {
    try {
      if (cursorCrosshair != null) {
        Gdx.graphics.setCursor(cursorCrosshair);
        cursorIsHidden = false;
      }
    } catch (Throwable ignored) {}
  }

  public void setCursorHidden() {
    try {
      if (cursorHidden != null) {
        Gdx.graphics.setCursor(cursorHidden);
        cursorIsHidden = true;
      }
    } catch (Throwable ignored) {}
  }

  public boolean isCursorHidden() {
    return cursorIsHidden;
  }

  @Override
  public void dispose() {
    // persist settings on exit (NOT savegame)
    SettingsIO.save(settings);
    super.dispose();
    audio.dispose();

    if (menuMusic != null) {
      try { menuMusic.stop(); } catch (Throwable ignored) {}
      try { menuMusic.dispose(); } catch (Throwable ignored) {}
      menuMusic = null;
    }

    try {
      if (cursorCrosshair != null) cursorCrosshair.dispose();
      if (cursorHidden != null) cursorHidden.dispose();
    } catch (Throwable ignored) {}
  }
}


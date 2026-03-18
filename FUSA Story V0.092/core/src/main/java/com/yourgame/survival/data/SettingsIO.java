package com.yourgame.survival.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.yourgame.survival.GameSettings;

/** Simple settings persistence (NOT part of savegames). */
public final class SettingsIO {
  private SettingsIO() {}

  public static String path() { return "settings.json"; }

  public static void loadInto(GameSettings out) {
    if (out == null) return;
    FileHandle fh = Gdx.files.local(path());
    if (!fh.exists()) return;
    try {
      Json json = new Json();
      GameSettings s = json.fromJson(GameSettings.class, fh);
      if (s == null) return;
      out.masterVolume = s.masterVolume;
      out.musicVolume = s.musicVolume;
      out.sfxVolume = s.sfxVolume;
      out.showDebug = s.showDebug;
      out.fullscreenW = s.fullscreenW;
      out.fullscreenH = s.fullscreenH;
    } catch (Throwable ignored) {
      // ignore broken settings file
    }
    out.clamp();
  }

  public static void save(GameSettings s) {
    if (s == null) return;
    s.clamp();
    try {
      Json json = new Json();
      FileHandle fh = Gdx.files.local(path());
      fh.writeString(json.prettyPrint(s), false, "UTF-8");
    } catch (Throwable ignored) {
    }
  }
}

package com.yourgame.survival.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.yourgame.survival.GameSettings;

import java.util.HashMap;

/** Block 12: minimal audio integration (music loop + small SFX palette). */
public final class AudioBus {
  private final HashMap<String, Sound> sfx = new HashMap<>();
  private Music music;
  private String musicPath = "";

  public void playMusicLoop(String path) {
    if (path == null) path = "";
    if (path.equals(musicPath) && music != null) return;

    stopMusic();

    if (path.isEmpty()) return;
    musicPath = path;
    music = Gdx.audio.newMusic(Gdx.files.internal(path));
    music.setLooping(true);
    music.play();
  }

  public void stopMusic() {
    musicPath = "";
    if (music != null) {
      try { music.stop(); } catch (Throwable ignored) {}
      try { music.dispose(); } catch (Throwable ignored) {}
      music = null;
    }
  }

  public void sfx(String path, float volume01) {
    if (path == null || path.isEmpty()) return;
    Sound snd = sfx.get(path);
    if (snd == null) {
      snd = Gdx.audio.newSound(Gdx.files.internal(path));
      sfx.put(path, snd);
    }
    float v = volume01;
    if (v < 0f) v = 0f;
    if (v > 1f) v = 1f;
    snd.play(v);
  }

  public void applySettings(GameSettings settings) {
    if (settings == null) return;
    settings.clamp();

    if (music != null) {
      float v = settings.masterVolume * settings.musicVolume;
      if (v < 0f) v = 0f;
      if (v > 1f) v = 1f;
      music.setVolume(v);
    }
  }

  public float sfxVolume(GameSettings settings) {
    if (settings == null) return 0.8f;
    settings.clamp();
    float v = settings.masterVolume * settings.sfxVolume;
    if (v < 0f) v = 0f;
    if (v > 1f) v = 1f;
    return v;
  }

  public void dispose() {
    stopMusic();
    for (Sound s : sfx.values()) {
      try { s.dispose(); } catch (Throwable ignored) {}
    }
    sfx.clear();
  }
}

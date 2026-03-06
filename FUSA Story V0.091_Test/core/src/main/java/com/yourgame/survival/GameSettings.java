package com.yourgame.survival;

/** Minimal runtime settings (Block 10). Persistence comes later (Block 11). */
public final class GameSettings {
  // 0..1
  // Requirement: first run defaults to 25% for music + SFX (master stays at 100%).
  public float masterVolume = 0.50f;
  public float musicVolume = 0.25f;
  public float sfxVolume = 0.25f;

  public boolean showDebug = true;

  // Display (desktop): fullscreen resolution preference (0/0 = use default display mode)
  public int fullscreenW = 0;
  public int fullscreenH = 0;

  public void clamp() {
    masterVolume = clamp01(masterVolume);
    musicVolume = clamp01(musicVolume);
    sfxVolume = clamp01(sfxVolume);
  }

  private static float clamp01(float v) {
    if (v < 0f) return 0f;
    if (v > 1f) return 1f;
    return v;
  }
}

package com.yourgame.survival;

public final class GameConfig {
  private GameConfig() {}

  public static final int LOGIC_HZ = 60;
  public static final float LOGIC_DT = 1f / LOGIC_HZ;

  public static final int WINDOW_W = 1280;
  public static final int WINDOW_H = 720;
}

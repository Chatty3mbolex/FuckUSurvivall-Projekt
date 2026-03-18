package com.yourgame.survival.systems;

import com.yourgame.survival.GameConfig;

/** Fixed timestep scheduler (60 Hz). Render must not mutate game state. */
public final class SystemScheduler {
  private float accumulator = 0f;

  public interface Tickable {
    void tick(float dt);
  }

  public void update(float frameDelta, Tickable tickable) {
    // Clamp to avoid spiral of death.
    float dt = frameDelta;
    if (dt > 0.25f) dt = 0.25f;

    accumulator += dt;
    while (accumulator >= GameConfig.LOGIC_DT) {
      tickable.tick(GameConfig.LOGIC_DT);
      accumulator -= GameConfig.LOGIC_DT;
    }
  }
}

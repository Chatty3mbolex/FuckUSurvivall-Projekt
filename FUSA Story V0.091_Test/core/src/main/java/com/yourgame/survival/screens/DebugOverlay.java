package com.yourgame.survival.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

final class DebugOverlay {
  private final GameScreen gs;

  DebugOverlay(GameScreen gs) {
    this.gs = gs;
  }

  void render(SpriteBatch batch) {
    // Keep behavior identical: drawHoverIdsAtCursor() only if debugHoverIds is enabled.
    if (gs.debugHoverIds) {
      gs.drawHoverIdsAtCursor();
    }
  }
}

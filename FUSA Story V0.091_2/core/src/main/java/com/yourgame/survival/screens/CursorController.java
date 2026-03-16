package com.yourgame.survival.screens;

final class CursorController {
  private final GameScreen gs;

  CursorController(GameScreen gs) {
    this.gs = gs;
  }

  void renderWorldCursor(float worldX, float worldY) {
    gs.drawDotCursorWorld(worldX, worldY);
  }
}

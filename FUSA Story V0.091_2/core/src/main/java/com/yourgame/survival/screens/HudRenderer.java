package com.yourgame.survival.screens;

final class HudRenderer {
  private final GameScreen gs;

  HudRenderer(GameScreen gs) {
    this.gs = gs;
  }

  void renderUI() {
    gs.renderUI();
  }
}

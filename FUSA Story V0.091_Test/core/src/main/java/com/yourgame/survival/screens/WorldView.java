package com.yourgame.survival.screens;

final class WorldView {
  private final GameScreen gs;

  WorldView(GameScreen gs) {
    this.gs = gs;
  }

  void renderWorld(float delta, int selectedTool) {
    gs.renderWorld(delta, selectedTool);
  }
}

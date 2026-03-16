package com.yourgame.survival.screens;

final class CombatController {
  private final GameScreen gs;

  CombatController(GameScreen gs) {
    this.gs = gs;
  }

  void renderWorldOverlays() {
    gs.drawEntityHealthBarsWorld();
    gs.drawArrowsWorld();
  }
}

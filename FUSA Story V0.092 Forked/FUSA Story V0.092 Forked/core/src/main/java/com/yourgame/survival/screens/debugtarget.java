package com.yourgame.survival.screens;

/**
 * Debug action surface used by {@code DebugCommands}.
 *
 * Purpose:
 * - Keep {@code DebugCommands} decoupled from {@code GameScreen}.
 * - Allow future screen/controller extraction without re-wiring debug keybinds.
 */
public interface DebugTarget {

  void debugTravelNorth();

  void debugTravelEast();

  void debugTravelSouth();

  void debugTravelWest();

  void debugRerollQuestGuyOffers();

  void debugToggleHoverIds();

  void debugCycleDayNightMode();

  void debugToggleLamp();

  void debugToggleLampXL();

  void debugToggleRoadBlocksMovement();

  void debugToggleTileTreeStreaming();

  void debugTogglePricingEditor();

  boolean isPricingEditorOpen();

  void debugPricingSave();

  void debugPricingResetDefaults();

  void debugPricingOpenPresetPopup();
}


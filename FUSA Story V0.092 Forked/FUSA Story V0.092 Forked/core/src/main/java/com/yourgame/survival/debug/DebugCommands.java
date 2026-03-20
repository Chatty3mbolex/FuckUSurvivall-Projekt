package com.yourgame.survival.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.screens.DebugTarget;
import com.yourgame.survival.screens.OptionsScreen;

/**
 * Central debug command hub.
 *
 * Goals:
 * - Keep GameScreen clean (orchestrator, not a dumping ground).
 * - Compile-time only (no runtime JSON parsing).
 * - Debug commands are only active in-game (GameScreen) when debug mode is enabled.
 */
public final class DebugCommands {
  private DebugCommands() {}

  /** Debug mode (in-game only). */
  private static boolean enabled = false;

  public static boolean isEnabled() {
    return enabled;
  }

  /**
   * Tick debug command processing.
   *
   * Intended call site: SurvivalGame.render() (central, cross-screen).
   */
  public static void tick(SurvivalGame game) {
    if (game == null) return;

    final Screen screen = game.getScreen();

    // OptionsScreen: global debug overlay setting (kept available even when debug mode is off).
    if (screen instanceof OptionsScreen) {
      OptionsScreen os = (OptionsScreen) screen;
      if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
        os.debugToggleOverlaySetting();
      }
      return;
    }

    // In-game only.
    if (!(screen instanceof DebugTarget)) return;
    DebugTarget gs = (DebugTarget) screen;

    boolean shiftHeld = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

    // Toggle debug mode: Shift+F10
    if (shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
      enabled = !enabled;
      // Optional: could toast here; keep minimal/no side effects.
    }

    if (!enabled) return;

    // If the pricing editor is open, reserve F5/F8/F9 for pricing actions.
    // This avoids conflicts with the general debug keys (e.g., F5 lamp).
    if (gs.isPricingEditorOpen()) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) gs.debugPricingSave();
      if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) gs.debugPricingResetDefaults();
      if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) gs.debugPricingOpenPresetPopup();
      // Allow closing the pricing editor via Shift+F12 handled below.
    }

    // ===== Debug/Dev-Keybinds (GameScreen) =====

    // WorldMap debug travel hotkeys (hard to trigger): SHIFT+CTRL+ALT + F6..F9
    boolean ctrlHeld = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
    boolean altHeld = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
    if (shiftHeld && ctrlHeld && altHeld) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) gs.debugTravelNorth();
      if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) gs.debugTravelEast();
      if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) gs.debugTravelSouth();
      if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) gs.debugTravelWest();
    }

    // Quest debug (easier): Shift+F6
    if (shiftHeld && !ctrlHeld && !altHeld && Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
      gs.debugRerollQuestGuyOffers();
    }

    // Cursor hover IDs (F3)
    if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
      gs.debugToggleHoverIds();
    }

    // Day/Night debug mode (F4)
    if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
      gs.debugCycleDayNightMode();
    }

    // Day/Night debug lamp (F5) (skip when pricing editor is open; it uses F5 for save)
    if (!gs.isPricingEditorOpen() && Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
      gs.debugToggleLamp();
    }

    // Day/Night debug lamp XL (Shift+L)
    if (shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.L)) {
      gs.debugToggleLampXL();
    }

    // RoadMask debug (Shift+F7)
    if (shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
      gs.debugToggleRoadBlocksMovement();
    }

    // Tile-tree streaming debug (F10) - only when NOT using Shift+F10 (mode toggle).
    if (!shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
      gs.debugToggleTileTreeStreaming();
    }

    // Pricing editor (dev tool)
    if (shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
      gs.debugTogglePricingEditor();
    }
    // F5/F8/F9 pricing actions are handled above (conflict-safe).
  }
}

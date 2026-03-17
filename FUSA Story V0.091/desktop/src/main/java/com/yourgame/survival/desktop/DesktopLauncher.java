package com.yourgame.survival.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.yourgame.survival.SurvivalGame;

public final class DesktopLauncher {
  public static void main(String[] args) {
    // Always capture runtime logs + uncaught exceptions to ERRORLOG/...
    DesktopLogBootstrap.init();

    // World editor replaces legacy biome editor.
    boolean worldEditor = hasArg(args, "--biome-editor") || hasArg(args, "-biome-editor") || hasArg(args, "--editor") || hasArg(args, "--world-editor");
    boolean tileEditor = hasArg(args, "--tile-editor") || hasArg(args, "-tile-editor");

    // Asset Editor flags
    boolean assetEditor = hasArg(args, "--asset-editor");
    boolean assetEditorBatch = hasArg(args, "--asset-editor#");
    if (assetEditorBatch) assetEditor = true;

    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle(tileEditor ? "FuckUSurvival - Tile Editor" : (worldEditor ? "FuckUSurvival - World Editor" : "FuckUSurvival"));

    // Start fullscreen by default (requested). Editor and game both fullscreen.
    config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
    config.setResizable(false);

    config.useVsync(true);
    config.setForegroundFPS(60);

    SurvivalGame game = new SurvivalGame(worldEditor, tileEditor);
    if (assetEditor) {
      game.setBootModeAssetEditor(assetEditorBatch);
    }

    new Lwjgl3Application(game, config);
  }

  private static boolean hasArg(String[] args, String want) {
    if (args == null || want == null) return false;
    for (String a : args) {
      if (a == null) continue;
      if (a.equalsIgnoreCase(want)) return true;
    }
    return false;
  }
}

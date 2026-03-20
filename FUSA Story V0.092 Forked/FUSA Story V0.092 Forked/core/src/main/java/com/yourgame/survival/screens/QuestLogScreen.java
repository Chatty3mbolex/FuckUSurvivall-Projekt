package com.yourgame.survival.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.yourgame.survival.SurvivalGame;

/**
 * QuestLogScreen (template)
 *
 * Toggle: Shift+Q
 * Close: ESC
 *
 * Behaviour should match inventory modal behaviour (mouse free, world paused similarly).
 * Uses the same UI skin/assets as inventory.
 */
public final class QuestLogScreen extends ScreenAdapter {

    private final SurvivalGame game;
    private final Stage stage;

    public QuestLogScreen(SurvivalGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        // TODO: build UI (dropdown/tabs: main/side + accepted/completed)
    }

    @Override
    public void render(float delta) {
        // TODO: stage.act/draw; close on ESC
        // NOTE: no implementation yet, just a placeholder.
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

package com.yourgame.survival.screens;

import com.badlogic.gdx.InputAdapter;

/**
 * Wrapper around the original anonymous InputAdapter created in GameScreen.show().
 * This keeps behavior identical while providing a stable named controller class.
 */
final class GameInputController extends InputAdapter {
  private final InputAdapter delegate;

  GameInputController(InputAdapter delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return delegate.scrolled(amountX, amountY);
  }

  @Override
  public boolean keyTyped(char character) {
    return delegate.keyTyped(character);
  }

  @Override
  public boolean keyDown(int keycode) {
    return delegate.keyDown(keycode);
  }

  @Override
  public boolean keyUp(int keycode) {
    return delegate.keyUp(keycode);
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return delegate.touchDown(screenX, screenY, pointer, button);
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return delegate.touchUp(screenX, screenY, pointer, button);
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return delegate.touchDragged(screenX, screenY, pointer);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return delegate.mouseMoved(screenX, screenY);
  }
}

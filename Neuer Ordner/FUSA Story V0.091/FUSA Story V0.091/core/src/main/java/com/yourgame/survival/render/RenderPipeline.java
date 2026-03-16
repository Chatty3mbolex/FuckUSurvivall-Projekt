package com.yourgame.survival.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class RenderPipeline {
  public final Texture whiteTex;
  public final TextureRegion white;

  public RenderPipeline() {
    Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
    pm.setColor(1,1,1,1);
    pm.fill();
    whiteTex = new Texture(pm);
    pm.dispose();
    white = new TextureRegion(whiteTex);
  }

  public void dispose() {
    whiteTex.dispose();
  }
}

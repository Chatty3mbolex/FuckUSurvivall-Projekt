package com.yourgame.survival.tools.asseteditor.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * No external skin files. Cheap, deterministic, enough for tooling.
 */
public final class MiniSkin {
  private MiniSkin() {}

  public static Skin build() {
    Skin skin = new Skin();

    BitmapFont font = new BitmapFont();
    // Slightly larger base font so the tooling UI stays readable.
    font.getData().setScale(1.15f);
    skin.add("default-font", font);

    Texture tex = solidTex(1, 1);
    skin.add("white", tex);

    Drawable white = new TextureRegionDrawable(new TextureRegion(tex));

    Label.LabelStyle ls = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);
    skin.add("default", ls);

    TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
    tbs.font = font;
    tbs.up = white;
    tbs.down = white;
    tbs.checked = white;
    tbs.over = white;
    skin.add("default", tbs);

    TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
    tfs.font = font;
    tfs.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
    tfs.background = white;
    tfs.cursor = white;
    tfs.selection = white;
    skin.add("default", tfs);

    List.ListStyle lls = new List.ListStyle();
    lls.font = font;
    lls.fontColorSelected = com.badlogic.gdx.graphics.Color.BLACK;
    lls.fontColorUnselected = com.badlogic.gdx.graphics.Color.WHITE;
    lls.selection = white;
    skin.add("default", lls);

    ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
    sps.background = white;
    skin.add("default", sps);

    SelectBox.SelectBoxStyle sbs = new SelectBox.SelectBoxStyle();
    sbs.font = font;
    sbs.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
    sbs.background = white;
    sbs.scrollStyle = sps;
    sbs.listStyle = lls;
    skin.add("default", sbs);

    // CheckBox is used by tooling panels (e.g. packer settings). Our minimal skin
    // must provide a default CheckBoxStyle, otherwise Skin#get() throws at runtime.
    CheckBox.CheckBoxStyle cbs = new CheckBox.CheckBoxStyle();
    cbs.font = font;
    cbs.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
    // Minimal visuals: reuse the same 1x1 drawable for on/off.
    cbs.checkboxOn = white;
    cbs.checkboxOff = white;
    skin.add("default", cbs);

    Slider.SliderStyle ss = new Slider.SliderStyle();
    ss.background = white;
    ss.knob = white;
    skin.add("default-horizontal", ss);

    Window.WindowStyle ws = new Window.WindowStyle(font, com.badlogic.gdx.graphics.Color.WHITE, white);
    skin.add("default", ws);

    return skin;
  }

  private static Texture solidTex(int w, int h) {
    Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
    pm.setColor(0.18f, 0.18f, 0.18f, 1f);
    pm.fill();
    Texture t = new Texture(pm);
    pm.dispose();
    return t;
  }
}

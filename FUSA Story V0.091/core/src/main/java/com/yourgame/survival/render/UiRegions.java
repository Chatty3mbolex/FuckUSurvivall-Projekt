package com.yourgame.survival.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Shared UI sprite regions from the main atlas. */
public final class UiRegions {
  private final TextureAtlas atlas;

  public final TextureRegion panel;
  public final TextureRegion panelSlots;
  public final TextureRegion button;
  public final TextureRegion buttonPressed;
  public final TextureRegion slot;
  public final TextureRegion slotPressed;

  // Stat bars
  public final TextureRegion barHpFrame;
  public final TextureRegion barHpFill;
  public final TextureRegion barStaminaFrame;
  public final TextureRegion barStaminaFill;
  public final TextureRegion barHungerFrame;
  public final TextureRegion barHungerFill;
  public final TextureRegion barSleepFrame;
  public final TextureRegion barSleepFill;

  public UiRegions() {
    this.atlas = AtlasLoader.loadPreferFs("ui");

    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    // UI sprites may still be placeholder/generated and must be reviewed/replaced with final art.
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    this.panel = req("ui_panel");
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    this.panelSlots = req("ui_panel_slots");
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    this.button = req("ui_button");
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    this.buttonPressed = req("ui_button_pressed");
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    this.slot = req("ui_slot");
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    this.slotPressed = req("ui_slot_pressed");

    // Stat bars
    this.barHpFrame = req("ui_bar_hp_frame");
    this.barHpFill = req("ui_bar_hp_fill");
    this.barStaminaFrame = req("ui_bar_stamina_frame");
    this.barStaminaFill = req("ui_bar_stamina_fill");
    this.barHungerFrame = req("ui_bar_hunger_frame");
    this.barHungerFill = req("ui_bar_hunger_fill");
    this.barSleepFrame = req("ui_bar_sleep_frame");
    this.barSleepFill = req("ui_bar_sleep_fill");
  }

  private TextureRegion req(String name) {
    // GENERISCH, MUSS GEWECHSELT WERDEN !!
    TextureAtlas.AtlasRegion r = atlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing atlas region: " + name);
    return r;
  }

  public void dispose() {
    atlas.dispose();
  }
}

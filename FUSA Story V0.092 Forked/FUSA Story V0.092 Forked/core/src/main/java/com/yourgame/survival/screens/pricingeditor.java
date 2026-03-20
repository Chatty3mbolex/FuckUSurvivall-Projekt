package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.yourgame.survival.data.DataRegistry;
import com.yourgame.survival.data.PriceBook;
import com.yourgame.survival.render.UiRegions;

/**
 * Pricing editor (dev tool) extracted from GameScreen.
 *
 * Constraints:
 * - Must preserve existing behavior exactly (no functional changes).
 * - State is kept inside this controller; GameScreen delegates.
 */
final class PricingEditor {

  private boolean open = false;
  private boolean presetPopup = false;
  private int sel = 0;
  private int scroll = 0;
  private final int[] editCopper = new int[60];
  private String editBuffer = "";
  private String[] presetNames = new String[0];

  boolean isOpen() {
    return open;
  }

  void onInitFromPriceBook(PriceBook priceBook) {
    if (priceBook == null) return;
    priceBook.copyTo(editCopper);
    sel = 0;
    scroll = 0;
    editBuffer = String.valueOf(editCopper[0]);
    presetPopup = false;
  }

  void close() {
    open = false;
    presetPopup = false;
  }

  void toggleOpen(Runnable toastPricingOpened, Runnable toastPricingClosed) {
    open = !open;
    presetPopup = false;
    if (open) {
      pricingClampSelection();
      editBuffer = String.valueOf(editCopper[sel]);
      if (toastPricingOpened != null) toastPricingOpened.run();
    } else {
      if (toastPricingClosed != null) toastPricingClosed.run();
    }
  }

  void debugSave(PriceBook priceBook, Runnable toastOk, Runnable toastFail) {
    if (presetPopup) return;
    if (priceBook == null) return;
    priceBook.copyFrom(editCopper);
    boolean ok = priceBook.saveCurrentLocal();
    if (ok) {
      if (toastOk != null) toastOk.run();
    } else {
      if (toastFail != null) toastFail.run();
    }
  }

  void debugResetDefaults(PriceBook priceBook, DataRegistry data, Runnable toastOk) {
    if (presetPopup) return;
    if (priceBook == null) return;
    if (data == null) return;
    priceBook.setDefaultsFromItems(data.items);
    priceBook.copyTo(editCopper);
    editBuffer = String.valueOf(editCopper[sel]);
    if (toastOk != null) toastOk.run();
  }

  void debugOpenPresetPopup() {
    if (!open) return;
    presetPopup = true;
  }

  boolean keyTyped(char character) {
    if (!open) return false;
    if (presetPopup) return false;

    if (character >= '0' && character <= '9') {
      if (editBuffer.length() < 9) {
        editBuffer += character;
        pricingApplyBufferToSelected();
      }
      return true;
    }
    return false;
  }

  boolean keyDown(int keycode, PriceBook priceBook, Runnable toastPresetLoaded, Runnable toastPresetFailed) {
    if (!open) return false;

    if (!presetPopup) {
      if (keycode == Input.Keys.BACKSPACE) {
        if (!editBuffer.isEmpty()) {
          editBuffer = editBuffer.substring(0, editBuffer.length() - 1);
          pricingApplyBufferToSelected();
        }
        return true;
      }
      if (keycode == Input.Keys.ENTER) {
        pricingApplyBufferToSelected();
        return true;
      }
      if (keycode == Input.Keys.ESCAPE) {
        // keep ESC semantics unchanged: close preset popup elsewhere; editor close is Shift+F12.
        return false;
      }
      return false;
    }

    // preset popup
    int pick = -1;
    if (keycode == Input.Keys.NUM_1) pick = 0;
    if (keycode == Input.Keys.NUM_2) pick = 1;
    if (keycode == Input.Keys.NUM_3) pick = 2;
    if (keycode == Input.Keys.NUM_4) pick = 3;
    if (keycode == Input.Keys.NUM_5) pick = 4;
    if (keycode == Input.Keys.NUM_6) pick = 5;
    if (keycode == Input.Keys.NUM_7) pick = 6;
    if (keycode == Input.Keys.NUM_8) pick = 7;
    if (keycode == Input.Keys.NUM_9) pick = 8;

    if (pick >= 0 && presetNames != null && pick < presetNames.length && priceBook != null) {
      PriceBook tmp = new PriceBook(60);
      tmp.copyFrom(editCopper);
      boolean ok = tmp.applyPresetLocal(presetNames[pick]);
      if (ok) {
        tmp.copyTo(editCopper);
        editBuffer = String.valueOf(editCopper[sel]);
        if (toastPresetLoaded != null) toastPresetLoaded.run();
      } else {
        if (toastPresetFailed != null) toastPresetFailed.run();
      }
      presetPopup = false;
      return true;
    }

    if (keycode == Input.Keys.ESCAPE) {
      presetPopup = false;
      return true;
    }

    return false;
  }

  void handleInput(PriceBook priceBook, Runnable toastPresetLoaded, Runnable toastPresetFailed) {
    if (!open) return;

    // navigation
    if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
      sel--;
      pricingClampSelection();
      editBuffer = String.valueOf(editCopper[sel]);
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
      sel++;
      pricingClampSelection();
      editBuffer = String.valueOf(editCopper[sel]);
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {
      sel -= pricingVisibleRows();
      pricingClampSelection();
      editBuffer = String.valueOf(editCopper[sel]);
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {
      sel += pricingVisibleRows();
      pricingClampSelection();
      editBuffer = String.valueOf(editCopper[sel]);
    }

    // actions (F5/F8/F9) are handled centrally via DebugCommands.

    if (presetPopup) {
      // choose preset 1..9 (keep old polling behavior)
      int pick = -1;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) pick = 0;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) pick = 1;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) pick = 2;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) pick = 3;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) pick = 4;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) pick = 5;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) pick = 6;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) pick = 7;
      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) pick = 8;

      if (pick >= 0 && presetNames != null && pick < presetNames.length && priceBook != null) {
        PriceBook tmp = new PriceBook(60);
        tmp.copyFrom(editCopper);
        boolean ok = tmp.applyPresetLocal(presetNames[pick]);
        if (ok) {
          tmp.copyTo(editCopper);
          editBuffer = String.valueOf(editCopper[sel]);
          if (toastPresetLoaded != null) toastPresetLoaded.run();
        } else {
          if (toastPresetFailed != null) toastPresetFailed.run();
        }
        presetPopup = false;
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        presetPopup = false;
      }
    }
  }

  void refreshPresetNames() {
    presetNames = pricingListPresetNames();
  }

  private void pricingClampSelection() {
    if (sel < 0) sel = 0;
    if (sel >= editCopper.length) sel = editCopper.length - 1;
    if (sel < scroll) scroll = sel;
    int visible = pricingVisibleRows();
    if (sel >= scroll + visible) scroll = sel - visible + 1;
    if (scroll < 0) scroll = 0;
    int maxScroll = Math.max(0, editCopper.length - visible);
    if (scroll > maxScroll) scroll = maxScroll;
  }

  private int pricingVisibleRows() {
    int h = Gdx.graphics.getHeight();
    return MathUtils.clamp((h - 260) / 22, 6, 26);
  }

  private void pricingApplyBufferToSelected() {
    if (sel < 0 || sel >= editCopper.length) return;
    if (editBuffer == null || editBuffer.isEmpty()) {
      editCopper[sel] = 0;
      return;
    }
    try {
      long v = Long.parseLong(editBuffer);
      if (v < 0) v = 0;
      if (v > 2_000_000_000L) v = 2_000_000_000L;
      editCopper[sel] = (int) v;
    } catch (NumberFormatException ignored) {
      // ignore
    }
  }

  private String[] pricingListPresetNames() {
    try {
      com.badlogic.gdx.files.FileHandle dir = Gdx.files.local("pricing_presets");
      if (!dir.exists()) return new String[0];
      com.badlogic.gdx.files.FileHandle[] files = dir.list("json");
      int n = Math.min(9, files.length);
      String[] names = new String[n];
      for (int i = 0; i < n; i++) names[i] = files[i].name();
      return names;
    } catch (Throwable t) {
      return new String[0];
    }
  }

  void drawMenu(SpriteBatch batch,
                UiRegions uiRegions,
                DataRegistry data,
                PriceBook priceBook,
                com.badlogic.gdx.graphics.g2d.BitmapFont font,
                float uiFontScale,
                Runnable toastNoPresets) {
    if (!open) return;
    float w = Math.min(1120f, Gdx.graphics.getWidth() - 40f);
    float h = Math.min(720f, Gdx.graphics.getHeight() - 40f);
    float x0 = (Gdx.graphics.getWidth() - w) * 0.5f;
    float y0 = (Gdx.graphics.getHeight() - h) * 0.5f;

    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(uiRegions.panelSlots, x0, y0, w, h);

    float pad = 26f;
    float x = x0 + pad;
    float yTop = y0 + h - pad;

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(1.6f * 1.15f);
    font.draw(batch, "PRICING (Shift+F12 close) | F5 Save | F8 Reset(defaults) | F9 Preset", x, yTop);
    font.getData().setScale(1.0f * uiFontScale);

    float y = yTop - 40f;
    font.getData().setScale(1.0f * 1.15f);
    font.draw(batch, "[Item]   Current(Copper)   Current(Gold)     New(Copper)   New(Gold)", x, y);
    font.getData().setScale(1.0f * uiFontScale);
    y -= 22f;

    int visible = pricingVisibleRows();
    int start = scroll;
    int end = Math.min(editCopper.length, start + visible);

    for (int itemId = start; itemId < end; itemId++) {
      boolean isSel = (itemId == sel);
      if (isSel) {
        batch.setColor(1f, 1f, 1f, 0.10f);
        batch.draw(uiRegions.slotPressed, x - 10f, y - 16f, w - pad * 2f + 20f, 22f);
        batch.setColor(1f, 1f, 1f, 1f);
      }

      String name = (itemId >= 0 && data != null && data.items != null && itemId < data.items.length && data.items[itemId] != null)
          ? data.items[itemId].name
          : ("item_" + itemId);
      int cur = (priceBook != null) ? priceBook.getBaseCopper(itemId) : 0;
      int neu = editCopper[itemId];

      String curGold = PriceBook.fmtGoldFromCopper(cur);
      String newGold = PriceBook.fmtGoldFromCopper(neu);

      String newCopperStr = (isSel ? (editBuffer.isEmpty() ? "" : editBuffer) : String.valueOf(neu));

      font.draw(batch,
          String.format("%02d %-18s %8d   %-10s   %8s   %-10s", itemId, name, cur, curGold, newCopperStr, newGold),
          x,
          y);
      y -= 22f;
    }

    if (presetPopup) {
      float popupY = y0 + 120f;
      font.getData().setScale(1.3f);
      font.draw(batch, "PRESET wählen (1..9) | ESC abbrechen", x, popupY);
      font.getData().setScale(1.0f * uiFontScale);
      popupY -= 24f;
      if (presetNames == null || presetNames.length == 0) {
        font.draw(batch, "(keine Presets gefunden in: pricing_presets/*.json)", x, popupY);
        if (toastNoPresets != null) toastNoPresets.run();
      } else {
        for (int i = 0; i < presetNames.length; i++) {
          font.draw(batch, (i + 1) + ") " + presetNames[i], x, popupY - i * 18f);
        }
      }
    }
  }

  // Expose buffer to keep GameScreen behavior where it must read it directly (rare).
  String getEditBuffer() {
    return editBuffer;
  }

  void setEditBuffer(String s) {
    editBuffer = (s == null) ? "" : s;
  }

  int[] getEditCopperUnsafe() {
    return editCopper;
  }
}


package com.yourgame.survival.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Arrays;

/**
 * Central pricing DB.
 *
 * Prices are stored in COPPER as integers.
 *
 * Persistence:
 * - defaults come from items.json (ItemDef.value)
 * - current editable prices can be stored in local storage (config/pricing_current.json)
 * - optional presets can be loaded from local storage (pricing_presets/*.json)
 */
public final class PriceBook {
  public static final int COPPER_PER_SILVER = 100;
  // Rule (latest from user): 1000 silver = 1 gold => 100000 copper = 1 gold
  public static final int COPPER_PER_GOLD = 100_000;

  private final int[] baseCopperByItemId;

  public PriceBook(int itemCount) {
    baseCopperByItemId = new int[itemCount];
  }

  public void setDefaultsFromItems(ItemDef[] items) {
    Arrays.fill(baseCopperByItemId, 0);
    for (int i = 0; i < baseCopperByItemId.length && i < items.length; i++) {
      ItemDef d = items[i];
      baseCopperByItemId[i] = (d != null) ? Math.max(0, d.value) : 0;
    }
  }

  public int getBaseCopper(int itemId) {
    if (itemId < 0 || itemId >= baseCopperByItemId.length) return 0;
    return baseCopperByItemId[itemId];
  }

  public void setBaseCopper(int itemId, int copper) {
    if (itemId < 0 || itemId >= baseCopperByItemId.length) return;
    baseCopperByItemId[itemId] = Math.max(0, copper);
  }

  public int itemCount() {
    return baseCopperByItemId.length;
  }

  public void copyTo(int[] out) {
    System.arraycopy(baseCopperByItemId, 0, out, 0, Math.min(out.length, baseCopperByItemId.length));
  }

  public void copyFrom(int[] in) {
    System.arraycopy(in, 0, baseCopperByItemId, 0, Math.min(in.length, baseCopperByItemId.length));
  }

  public static String fmtGoldFromCopper(int copper) {
    if (copper <= 0) return "0.0000G";
    int abs = copper;
    int gold = abs / COPPER_PER_GOLD;
    int rem = abs % COPPER_PER_GOLD;
    // show 5 decimals of gold (== copper precision for 100000)
    return gold + "." + String.format("%05d", rem) + "G";
  }

  // ---------- persistence ----------

  public static String localCurrentPath() {
    return "config/pricing_current.json";
  }

  public boolean loadCurrentIfExists() {
    FileHandle fh = Gdx.files.local(localCurrentPath());
    if (!fh.exists()) return false;
    try {
      String txt = fh.readString("UTF-8");
      applyJson(txt);
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

  public boolean saveCurrent() {
    try {
      FileHandle fh = Gdx.files.local(localCurrentPath());
      fh.parent().mkdirs();
      fh.writeString(toJsonString(), false, "UTF-8");
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

  public boolean applyPresetLocal(String presetFileName) {
    if (presetFileName == null || presetFileName.isEmpty()) return false;
    FileHandle fh = Gdx.files.local("pricing_presets/" + presetFileName);
    if (!fh.exists()) return false;
    try {
      applyJson(fh.readString("UTF-8"));
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

  private void applyJson(String txt) {
    JsonValue root = new JsonReader().parse(txt);
    JsonValue prices = root.get("prices");
    if (prices == null) return;
    for (JsonValue p = prices.child; p != null; p = p.next) {
      int itemId = p.getInt("itemId", -1);
      int copper = p.getInt("copper", 0);
      setBaseCopper(itemId, copper);
    }
  }

  private String toJsonString() {
    StringBuilder sb = new StringBuilder(16 * 1024);
    sb.append('{');
    sb.append("\"prices\":[");
    boolean first = true;
    for (int itemId = 0; itemId < baseCopperByItemId.length; itemId++) {
      int c = baseCopperByItemId[itemId];
      if (!first) sb.append(',');
      first = false;
      sb.append('{');
      sb.append("\"itemId\":").append(itemId).append(',');
      sb.append("\"copper\":").append(c);
      sb.append('}');
    }
    sb.append("]}");
    return sb.toString();
  }
}

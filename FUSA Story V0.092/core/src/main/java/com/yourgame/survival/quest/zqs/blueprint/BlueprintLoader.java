package com.yourgame.survival.quest.zqs.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

/**
 * Strict loader for `assets/data/zqs/blueprints_v1.json`.
 */
public final class BlueprintLoader {

  public ArrayList<QuestBlueprint> load(String internalPath) {
    if (internalPath == null || internalPath.isEmpty()) throw new IllegalArgumentException("path missing");

    JsonValue root = parseInternal(internalPath);
    JsonValue arr = root.get("blueprints");
    if (arr == null) throw new IllegalStateException("blueprints array missing: " + internalPath);

    ArrayList<QuestBlueprint> out = new ArrayList<>();
    for (JsonValue b = arr.child; b != null; b = b.next) {
      QuestBlueprint bp = new QuestBlueprint();
      bp.blueprintId = b.getString("blueprintId", "");
      bp.objectiveFamily = b.getString("objectiveFamily", "NQ");
      bp.questType = b.getString("questType", "");
      bp.questSubtype = b.getString("questSubtype", "");
      bp.repeatFamilyKey = b.getString("repeatFamilyKey", "");
      bp.minPlayerLevel = b.getInt("minPlayerLevel", 1);
      if (b.has("maxPlayerLevel")) {
        try {
          bp.maxPlayerLevel = b.getInt("maxPlayerLevel");
        } catch (Throwable ignored) {
          bp.maxPlayerLevel = null;
        }
      }
      bp.weight = (float) b.getDouble("weight", 1.0);
      bp.rewardProfileId = b.getString("rewardProfileId", "");
      bp.textProfileId = b.getString("textProfileId", "");

      JsonValue at = b.get("allowedTargetKinds");
      if (at != null) {
        String[] ks = new String[at.size];
        int i = 0;
        for (JsonValue k = at.child; k != null; k = k.next) ks[i++] = k.asString();
        bp.allowedTargetKinds = ks;
      }

      JsonValue ar = b.get("amountRules");
      if (ar != null) {
        bp.amountMin = ar.getInt("min", 1);
        bp.amountMax = ar.getInt("max", bp.amountMin);
      }

      JsonValue rr = b.get("repeatRules");
      if (rr != null) {
        bp.repeatRules.cooldownHours = rr.getInt("cooldownHours", 0);
        bp.repeatRules.denySameTarget = rr.getBoolean("denySameTarget", false);
        bp.repeatRules.denySameFamily = rr.getBoolean("denySameFamily", false);
      }

      if (bp.blueprintId == null || bp.blueprintId.isEmpty()) {
        throw new IllegalStateException("Blueprint missing blueprintId");
      }

      // Strict (A5): rewardProfileId is mandatory.
      if (bp.rewardProfileId == null || bp.rewardProfileId.trim().isEmpty()) {
        throw new IllegalStateException("Blueprint missing rewardProfileId: " + bp.blueprintId);
      }

      out.add(bp);
    }

    if (out.isEmpty()) throw new IllegalStateException("No blueprints loaded from: " + internalPath);
    return out;
  }

  private static JsonValue parseInternal(String path) {
    try {
      FileHandle fh = Gdx.files.internal(path);
      if (fh == null || !fh.exists()) throw new RuntimeException("Missing blueprint file: " + path);
      return new JsonReader().parse(fh);
    } catch (Throwable t) {
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }
  }
}

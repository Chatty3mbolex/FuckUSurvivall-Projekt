package com.yourgame.survival.quest.zqs.generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.yourgame.survival.quest.zqs.runtime.RewardTextMode;

/**
 * Strict loader for `assets/data/zqs/reward_profiles_v1.json`.
 */
public final class RewardProfilesLoader {

  public RewardProfiles load(String internalPath) {
    if (internalPath == null || internalPath.isEmpty()) throw new IllegalArgumentException("path missing");
    JsonValue root = parseInternal(internalPath);

    RewardProfiles out = new RewardProfiles();
    JsonValue arr = root.get("profiles");
    if (arr == null) throw new IllegalStateException("profiles array missing: " + internalPath);

    for (JsonValue p = arr.child; p != null; p = p.next) {
      RewardProfileDef rp = new RewardProfileDef();
      rp.rewardProfileId = p.getString("rewardProfileId", "");
      rp.rewardFormulaType = p.getString("rewardFormulaType", "");
      rp.rewardTextMode = p.getString("rewardTextMode", "currency");
      rp.allowItemRewards = p.getBoolean("allowItemRewards", false);
      rp.allowCurrencyRewards = p.getBoolean("allowCurrencyRewards", true);
      rp.allowMixedRewards = p.getBoolean("allowMixedRewards", false);

      if (rp.rewardProfileId == null || rp.rewardProfileId.isEmpty()) {
        throw new IllegalStateException("RewardProfile missing rewardProfileId");
      }

      // Strict validation (A5): profiles must provide a known formula type.
      String ft = (rp.rewardFormulaType != null) ? rp.rewardFormulaType.trim().toLowerCase() : "";
      if (ft.isEmpty()) {
        throw new IllegalStateException("RewardProfile missing rewardFormulaType: " + rp.rewardProfileId);
      }
      if (!("collect".equals(ft) || "deliver".equals(ft) || "craft".equals(ft) || "find".equals(ft) || "escort".equals(ft))) {
        throw new IllegalStateException("Unknown rewardFormulaType: '" + ft + "' (profile=" + rp.rewardProfileId + ")");
      }
      rp.rewardFormulaType = ft;

      // Strict validation (A5): rewardTextMode must be known.
      String rtm = (rp.rewardTextMode != null) ? rp.rewardTextMode.trim().toLowerCase() : "";
      if (RewardTextMode.byId(rtm) == null) {
        throw new IllegalStateException("Unknown rewardTextMode: '" + rtm + "' (profile=" + rp.rewardProfileId + ")");
      }
      rp.rewardTextMode = rtm;
      if (out.byId.containsKey(rp.rewardProfileId)) {
        throw new IllegalStateException("Duplicate rewardProfileId: " + rp.rewardProfileId);
      }
      out.byId.put(rp.rewardProfileId, rp);
    }

    if (out.byId.isEmpty()) throw new IllegalStateException("No reward profiles loaded: " + internalPath);
    return out;
  }

  private static JsonValue parseInternal(String path) {
    try {
      FileHandle fh = Gdx.files.internal(path);
      if (fh == null || !fh.exists()) throw new RuntimeException("Missing reward profiles file: " + path);
      return new JsonReader().parse(fh);
    } catch (Throwable t) {
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }
  }
}

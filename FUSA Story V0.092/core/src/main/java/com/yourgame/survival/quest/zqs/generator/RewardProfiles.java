package com.yourgame.survival.quest.zqs.generator;

import java.util.HashMap;

public final class RewardProfiles {
  public final HashMap<String, RewardProfileDef> byId = new HashMap<>();

  public RewardProfileDef get(String id) {
    if (id == null || id.isEmpty()) return null;
    return byId.get(id);
  }
}


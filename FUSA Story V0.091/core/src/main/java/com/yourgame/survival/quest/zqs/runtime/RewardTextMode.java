package com.yourgame.survival.quest.zqs.runtime;

public enum RewardTextMode {
  ITEM("item"),
  CURRENCY("currency"),
  MIXED("mixed"),
  FAILED("failed");

  public final String id;
  RewardTextMode(String id) { this.id = id; }

  public static RewardTextMode byId(String id) {
    if (id == null) return null;
    for (RewardTextMode m : values()) if (m.id.equals(id)) return m;
    return null;
  }
}

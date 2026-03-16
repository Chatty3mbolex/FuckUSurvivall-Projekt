package com.yourgame.survival.quest.zqs.runtime;

public enum RewardState {
  CLAIMABLE("claimable"),
  ACCEPTED("accepted"),
  FAILED("failed");

  public final String id;
  RewardState(String id) { this.id = id; }
}


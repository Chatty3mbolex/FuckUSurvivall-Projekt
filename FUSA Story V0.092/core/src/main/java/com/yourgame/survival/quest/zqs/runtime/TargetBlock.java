package com.yourgame.survival.quest.zqs.runtime;

import java.util.ArrayList;
import java.util.List;

/** Technical target block of a quest (Codeplan v1). */
public final class TargetBlock {
  public String targetKind = ""; // item|harvestable|poi|npc|object|region|living
  public String targetId = "";
  public String targetName = "";
  public int targetAmount = 0;
  public int targetValueCopper = 0;
  public final List<String> targetTags = new ArrayList<>();

  public boolean knownFlagRequired = false;
  public boolean knownFlagState = false;

  public int slId = 1;
  public String regionId = "";
  public String escortRuleSource = "";

  // Optional roles
  public String giverNpcKey = "";
  public String receiverNpcKey = "";

  public boolean isValid() {
    return targetKind != null && !targetKind.isEmpty()
        && targetId != null && !targetId.isEmpty()
        && targetAmount >= 0;
  }
}

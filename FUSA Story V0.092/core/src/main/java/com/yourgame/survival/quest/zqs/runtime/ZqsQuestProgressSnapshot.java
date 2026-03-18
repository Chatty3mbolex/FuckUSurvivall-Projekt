package com.yourgame.survival.quest.zqs.runtime;

import java.util.HashMap;
import java.util.HashSet;

public final class ZqsQuestProgressSnapshot {
  public long epochSec;
  public String currentAreaTemplateId = "";
  public final HashSet<String> consumedPois = new HashSet<>();
  public final HashSet<String> removedAuthoredNodes = new HashSet<>();
  public final HashMap<Integer, Integer> inventoryCounts = new HashMap<>();
  public final HashMap<Integer, Integer> craftedOutputCounts = new HashMap<>();
}

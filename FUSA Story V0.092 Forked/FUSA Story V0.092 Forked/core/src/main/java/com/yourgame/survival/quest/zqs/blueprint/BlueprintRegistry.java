package com.yourgame.survival.quest.zqs.blueprint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Runtime registry for all quest blueprints.
 */
public final class BlueprintRegistry {

  public final ArrayList<QuestBlueprint> all = new ArrayList<>();
  public final HashMap<String, QuestBlueprint> byId = new HashMap<>();

  public void clear() {
    all.clear();
    byId.clear();
  }

  public void loadFrom(ArrayList<QuestBlueprint> list) {
    clear();
    if (list == null || list.isEmpty()) throw new IllegalStateException("Blueprint list empty");
    for (QuestBlueprint bp : list) {
      if (bp == null) continue;
      if (bp.blueprintId == null || bp.blueprintId.isEmpty()) continue;
      all.add(bp);
      byId.put(bp.blueprintId, bp);
    }
    if (all.isEmpty()) throw new IllegalStateException("No valid blueprints indexed");
  }

  public ArrayList<QuestBlueprint> filter(String family, int playerLevel) {
    ArrayList<QuestBlueprint> out = new ArrayList<>();
    for (QuestBlueprint bp : all) {
      if (bp == null) continue;
      if (family != null && !family.isEmpty() && !family.equals(bp.objectiveFamily)) continue;
      if (playerLevel < bp.minPlayerLevel) continue;
      if (bp.maxPlayerLevel != null && playerLevel > bp.maxPlayerLevel) continue;
      out.add(bp);
    }
    return out;
  }
}


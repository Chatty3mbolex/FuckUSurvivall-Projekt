package com.yourgame.survival.quest.zqs.runtime;

import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

/**
 * Resolves quest progress from a progress snapshot.
 *
 * Pflicht-Mapping laut INDEX_ZQS_FINALISIERUNG_6PUNKTE.md (A3).
 */
public final class ZqsQuestProgressResolver {

  public static final class Resolved {
    public String progressKey = "";
    public int current = 0;
  }

  private ZqsQuestProgressResolver() {}

  public static Resolved resolve(ZqsSaveBlock.PersistentQuestRecordSave pr, ZqsQuestProgressSnapshot s) {
    Resolved out = new Resolved();
    if (pr == null || pr.questSubtype == null) return out;

    String subtype = pr.questSubtype;
    String targetId = (pr.target != null) ? pr.target.targetId : "";
    int targetAmount = (pr.target != null) ? pr.target.targetAmount : 0;

    // sammeln.item / liefern.item
    if (subtype.equals("sammeln.item") || subtype.equals("liefern.item")) {
      int itemId = parseIntSafe(targetId, -1);
      out.progressKey = "inventory:item:" + itemId;
      out.current = (s != null) ? getMapInt(s.inventoryCounts, itemId, 0) : 0;
      return out;
    }

    // sammeln.harvestable
    if (subtype.equals("sammeln.harvestable")) {
      out.progressKey = "harvestable:" + safe(targetId);
      if (s != null && s.removedAuthoredNodes != null && targetId != null && !targetId.isEmpty()) {
        out.current = countContainsToken(s.removedAuthoredNodes, "|" + targetId + "|");
      }
      return out;
    }

    // craften.recipe_output
    if (subtype.equals("craften.recipe_output")) {
      int itemId = parseIntSafe(targetId, -1);
      out.progressKey = "craft:item:" + itemId;
      out.current = (s != null) ? getMapInt(s.craftedOutputCounts, itemId, 0) : 0;
      return out;
    }

    // craften.delivery
    if (subtype.equals("craften.delivery")) {
      int itemId = parseIntSafe(targetId, -1);
      out.progressKey = "inventory:item:" + itemId;
      out.current = (s != null) ? getMapInt(s.inventoryCounts, itemId, 0) : 0;
      return out;
    }

    // finden.poi / finden.poi_loot
    if (subtype.equals("finden.poi") || subtype.equals("finden.poi_loot")) {
      String poiKey = safe(targetId);
      out.progressKey = "poi:" + poiKey;
      out.current = (s != null && s.consumedPois != null && s.consumedPois.contains(poiKey)) ? 1 : 0;
      return out;
    }

    // finden.object
    if (subtype.equals("finden.object")) {
      String key = safe(targetId);
      out.progressKey = "object:" + key;
      out.current = (s != null && s.consumedPois != null && s.consumedPois.contains(key)) ? 1 : 0;
      return out;
    }

    // finden.person
    if (subtype.equals("finden.person")) {
      out.progressKey = "person:" + safe(targetId);
      out.current = 0;
      return out;
    }

    // eskortieren.route
    if (subtype.equals("eskortieren.route")) {
      out.progressKey = "escort:" + safe(targetId);
      out.current = 0;
      return out;
    }

    // Default: no mapping.
    out.progressKey = (pr.objectiveMeta != null) ? safe(pr.objectiveMeta.progressKey) : "";
    out.current = (targetAmount > 0) ? 0 : 0;
    return out;
  }

  private static int getMapInt(java.util.HashMap<Integer, Integer> m, int key, int fallback) {
    if (m == null) return fallback;
    Integer v = m.get(key);
    return (v != null) ? v : fallback;
  }

  private static int countContainsToken(java.util.HashSet<String> set, String token) {
    if (set == null || token == null || token.isEmpty()) return 0;
    int c = 0;
    for (String s : set) {
      if (s != null && s.contains(token)) c++;
    }
    return c;
  }

  private static int parseIntSafe(String s, int fallback) {
    if (s == null) return fallback;
    try { return Integer.parseInt(s.trim()); } catch (Throwable ignored) { return fallback; }
  }

  private static String safe(String s) { return (s == null) ? "" : s; }
}

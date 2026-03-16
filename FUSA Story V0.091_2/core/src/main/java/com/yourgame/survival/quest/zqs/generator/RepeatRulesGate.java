package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

import java.util.HashSet;

/**
 * Helper for enforcing RepeatRules (cooldowns / deny-same gates).
 *
 * Canonical storage for cooldowns is {@link ZqsSaveBlock.BlueprintState#cooldowns}.
 */
public final class RepeatRulesGate {

  private RepeatRulesGate() {}

  public static boolean isOnCooldown(ZqsSaveBlock save, String key, long runtimeSec) {
    if (save == null || save.blueprintState == null || save.blueprintState.cooldowns == null) return false;
    if (key == null || key.isEmpty()) return false;
    for (int i = 0; i < save.blueprintState.cooldowns.size(); i++) {
      ZqsSaveBlock.Cooldown cd = save.blueprintState.cooldowns.get(i);
      if (cd == null) continue;
      if (!key.equals(cd.key)) continue;
      return cd.untilRuntimeSec > runtimeSec;
    }
    return false;
  }

  public static void putCooldown(ZqsSaveBlock save, String key, long untilRuntimeSec) {
    if (save == null || save.blueprintState == null) throw new IllegalStateException("ZqsSaveBlock.blueprintState missing");
    if (key == null || key.isEmpty()) throw new IllegalArgumentException("cooldown key missing");

    // remove old
    for (int i = save.blueprintState.cooldowns.size() - 1; i >= 0; i--) {
      ZqsSaveBlock.Cooldown cd = save.blueprintState.cooldowns.get(i);
      if (cd != null && key.equals(cd.key)) save.blueprintState.cooldowns.remove(i);
    }

    ZqsSaveBlock.Cooldown cd = new ZqsSaveBlock.Cooldown();
    cd.key = key;
    cd.untilRuntimeSec = Math.max(0L, untilRuntimeSec);
    save.blueprintState.cooldowns.add(cd);
  }

  public static void cleanupExpired(ZqsSaveBlock save, long runtimeSec) {
    if (save == null || save.blueprintState == null || save.blueprintState.cooldowns == null) return;
    for (int i = save.blueprintState.cooldowns.size() - 1; i >= 0; i--) {
      ZqsSaveBlock.Cooldown cd = save.blueprintState.cooldowns.get(i);
      if (cd == null) { save.blueprintState.cooldowns.remove(i); continue; }
      if (cd.untilRuntimeSec <= runtimeSec) save.blueprintState.cooldowns.remove(i);
    }
  }

  /**
   * Collects active quest target keys from save for denySameTarget.
   * Format: targetType + ":" + targetId
   */
  public static HashSet<String> activeTargetKeys(ZqsSaveBlock save) {
    HashSet<String> out = new HashSet<>();
    if (save == null || save.playerQuestDb == null || save.playerQuestDb.records == null) return out;
    if (save.questHistoryIndex == null || save.questHistoryIndex.activeQuestIds == null) return out;

    for (int i = 0; i < save.playerQuestDb.records.size(); i++) {
      ZqsSaveBlock.PersistentQuestRecordSave r = save.playerQuestDb.records.get(i);
      if (r == null) continue;
      if (r.questId == null || r.questId.isEmpty()) continue;
      if (!save.questHistoryIndex.activeQuestIds.contains(r.questId)) continue;
      if (r.target == null) continue;
      String k = safe(r.target.targetType) + ":" + safe(r.target.targetId);
      if (!k.equals(":")) out.add(k);
    }
    return out;
  }

  public static HashSet<String> activeFamilies(ZqsSaveBlock save) {
    HashSet<String> out = new HashSet<>();
    if (save == null || save.playerQuestDb == null || save.playerQuestDb.records == null) return out;
    if (save.questHistoryIndex == null || save.questHistoryIndex.activeQuestIds == null) return out;

    for (int i = 0; i < save.playerQuestDb.records.size(); i++) {
      ZqsSaveBlock.PersistentQuestRecordSave r = save.playerQuestDb.records.get(i);
      if (r == null) continue;
      if (r.questId == null || r.questId.isEmpty()) continue;
      if (!save.questHistoryIndex.activeQuestIds.contains(r.questId)) continue;
      if (r.questFamily != null && !r.questFamily.isEmpty()) out.add(r.questFamily);
    }
    return out;
  }

  /**
   * Collects active quest type keys for denySameFamily behavior.
   * In current data model this uses questType as family surrogate.
   */
  public static HashSet<String> activeQuestTypes(ZqsSaveBlock save) {
    HashSet<String> out = new HashSet<>();
    if (save == null || save.playerQuestDb == null || save.playerQuestDb.records == null) return out;
    if (save.questHistoryIndex == null || save.questHistoryIndex.activeQuestIds == null) return out;

    for (int i = 0; i < save.playerQuestDb.records.size(); i++) {
      ZqsSaveBlock.PersistentQuestRecordSave r = save.playerQuestDb.records.get(i);
      if (r == null) continue;
      if (r.questId == null || r.questId.isEmpty()) continue;
      if (!save.questHistoryIndex.activeQuestIds.contains(r.questId)) continue;
      if (r.questType != null && !r.questType.isEmpty()) out.add(r.questType);
    }
    return out;
  }

  private static String safe(String s) { return (s == null) ? "" : s; }
}


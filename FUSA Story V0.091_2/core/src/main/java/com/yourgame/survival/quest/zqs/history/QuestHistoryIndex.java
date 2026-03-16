package com.yourgame.survival.quest.zqs.history;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Canonical in-memory quest history index for ZQS.
 *
 * Purpose:
 * - RepeatRules support (cooldowns/denySameTarget/denySameFamily)
 * - UI status overview
 *
 * Persistence is done via {@link com.yourgame.survival.quest.zqs.save.ZqsSaveBlock.QuestHistoryIndex}.
 */
public final class QuestHistoryIndex {

  private final LinkedHashSet<String> seenQuestIds = new LinkedHashSet<>();
  private final LinkedHashSet<String> activeQuestIds = new LinkedHashSet<>();
  private final LinkedHashSet<String> completedQuestIds = new LinkedHashSet<>();
  private final LinkedHashSet<String> expiredQuestIds = new LinkedHashSet<>();
  private final LinkedHashSet<String> declinedQuestIds = new LinkedHashSet<>();

  public void clear() {
    seenQuestIds.clear();
    activeQuestIds.clear();
    completedQuestIds.clear();
    expiredQuestIds.clear();
    declinedQuestIds.clear();
  }

  public void markSeen(String questId) {
    if (questId == null || questId.isEmpty()) return;
    seenQuestIds.add(questId);
  }

  public void markActive(String questId) {
    if (questId == null || questId.isEmpty()) return;
    seenQuestIds.add(questId);
    activeQuestIds.add(questId);
    completedQuestIds.remove(questId);
    expiredQuestIds.remove(questId);
    declinedQuestIds.remove(questId);
  }

  public void markCompleted(String questId) {
    if (questId == null || questId.isEmpty()) return;
    seenQuestIds.add(questId);
    activeQuestIds.remove(questId);
    completedQuestIds.add(questId);
    expiredQuestIds.remove(questId);
    declinedQuestIds.remove(questId);
  }

  public void markExpired(String questId) {
    if (questId == null || questId.isEmpty()) return;
    seenQuestIds.add(questId);
    activeQuestIds.remove(questId);
    expiredQuestIds.add(questId);
    completedQuestIds.remove(questId);
    declinedQuestIds.remove(questId);
  }

  public void markDeclined(String questId) {
    if (questId == null || questId.isEmpty()) return;
    // IMPORTANT: Master/SAVE rule: non-accepted offers must NOT enter history.
    // Therefore: this method must only be used for persisted/accepted quests that were later declined.
    seenQuestIds.add(questId);
    activeQuestIds.remove(questId);
    declinedQuestIds.add(questId);
    completedQuestIds.remove(questId);
    expiredQuestIds.remove(questId);
  }

  public boolean wasSeen(String questId) { return seenQuestIds.contains(questId); }
  public boolean isActive(String questId) { return activeQuestIds.contains(questId); }
  public boolean isCompleted(String questId) { return completedQuestIds.contains(questId); }
  public boolean isExpired(String questId) { return expiredQuestIds.contains(questId); }
  public boolean isDeclined(String questId) { return declinedQuestIds.contains(questId); }

  // ---------------- persistence helpers ----------------

  public ArrayList<String> exportSeen() { return new ArrayList<>(seenQuestIds); }
  public ArrayList<String> exportActive() { return new ArrayList<>(activeQuestIds); }
  public ArrayList<String> exportCompleted() { return new ArrayList<>(completedQuestIds); }
  public ArrayList<String> exportExpired() { return new ArrayList<>(expiredQuestIds); }
  public ArrayList<String> exportDeclined() { return new ArrayList<>(declinedQuestIds); }

  public void importAll(
      ArrayList<String> seen,
      ArrayList<String> active,
      ArrayList<String> completed,
      ArrayList<String> expired,
      ArrayList<String> declined) {
    clear();
    addAll(seenQuestIds, seen);
    addAll(activeQuestIds, active);
    addAll(completedQuestIds, completed);
    addAll(expiredQuestIds, expired);
    addAll(declinedQuestIds, declined);
  }

  private static void addAll(LinkedHashSet<String> set, ArrayList<String> src) {
    if (src == null) return;
    for (String s : src) {
      if (s == null || s.isEmpty()) continue;
      set.add(s);
    }
  }
}

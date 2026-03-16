package com.yourgame.survival.quest.zqs.history;

import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

/**
 * Converts between runtime {@link QuestHistoryIndex} and save-model
 * {@link com.yourgame.survival.quest.zqs.save.ZqsSaveBlock.QuestHistoryIndex}.
 */
public final class QuestHistoryCodec {
  private QuestHistoryCodec() {}

  public static QuestHistoryIndex fromSave(ZqsSaveBlock.QuestHistoryIndex s) {
    QuestHistoryIndex h = new QuestHistoryIndex();
    if (s == null) return h;
    h.importAll(s.seenQuestIds, s.activeQuestIds, s.completedQuestIds, s.expiredQuestIds, s.declinedQuestIds);
    return h;
  }

  public static void toSave(QuestHistoryIndex runtime, ZqsSaveBlock.QuestHistoryIndex out) {
    if (out == null) return;
    out.clear();
    if (runtime == null) return;

    out.seenQuestIds.addAll(runtime.exportSeen());
    out.activeQuestIds.addAll(runtime.exportActive());
    out.completedQuestIds.addAll(runtime.exportCompleted());
    out.expiredQuestIds.addAll(runtime.exportExpired());
    out.declinedQuestIds.addAll(runtime.exportDeclined());
  }
}


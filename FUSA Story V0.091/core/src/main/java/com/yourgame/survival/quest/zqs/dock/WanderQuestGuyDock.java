package com.yourgame.survival.quest.zqs.dock;

import com.yourgame.survival.quest.QuestDef;
import com.yourgame.survival.quest.QuestLog;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;
import com.yourgame.survival.quest.zqs.runtime.GeneratedQuestOffer;
import com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext;
import com.yourgame.survival.quest.zqs.runtime.ZqsRuntime;

/**
 * Docking layer for WanderQuestGuySystem.
 *
 * Rule:
 * - WQG must not talk to generator/text/save directly.
 * - WQG talks only to this dock.
 */
public final class WanderQuestGuyDock {

  private final ZqsRuntime rt;

  public WanderQuestGuyDock(ZqsRuntime rt) {
    if (rt == null) throw new IllegalArgumentException("ZqsRuntime missing");
    this.rt = rt;
  }

  public String buildGreeting(ZqsConversationContext ctx, int currentOfferCount) {
    if (ctx == null) ctx = new ZqsConversationContext();
    ctx.nqOfferCount = Math.max(0, currentOfferCount);
    if (!ctx.nqGenerationPossible && currentOfferCount <= 0 && ctx.blockReason != null && !ctx.blockReason.isEmpty() && !"none".equals(ctx.blockReason)) {
      ctx.conversationNextStep = "blocked";
    } else {
      ctx.conversationNextStep = (currentOfferCount > 0) ? "offer" : "no_offer";
    }
    return rt.buildGreeting(ctx);
  }

  /** Generates up to desiredCount offers and returns QuestDefs for legacy UI. */
  public QuestDef[] generateOffers(ZqsConversationContext ctx, int desiredCount) {
    if (ctx == null) ctx = new ZqsConversationContext();
    rt.generateNqOffers(ctx);

    int n = Math.max(0, Math.min(3, desiredCount));
    QuestDef[] out = new QuestDef[n];
    int m = Math.min(n, rt.offerCount());
    for (int i = 0; i < m; i++) {
      GeneratedQuestOffer o = rt.offer(i);
      if (o == null) continue;
      String title = rt.buildAssignmentText(o);
      out[i] = new QuestDef(o.questId, QuestDef.Kind.SIDE, title, "");
    }
    return out;
  }

  public boolean acceptOffer(String questId, QuestLog log, long runtimeSec) {
    if (questId == null || questId.isEmpty()) return false;
    if (log == null) return false;
    rt.acceptOffer(questId, runtimeSec);

    // Legacy questLog view entry. The canonical record is persisted in ZQS save block.
    ZqsSaveBlock.LogbookEntry le = rt.findLogbookEntry(questId);
    if (le == null) throw new IllegalStateException("Accepted quest missing from ZQS logbook: " + questId);

    QuestDef def = new QuestDef(questId, QuestDef.Kind.SIDE, le.title, le.acceptedText);
    log.acceptZqs(def, runtimeSec, le.logbookEntryNr, le.finalStatus);
    return true;
  }
}

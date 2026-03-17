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
      // Full offer text: assignment + reward (reward amounts are computed by reward system).
      String offerText = rt.buildOfferText(o);
      out[i] = new QuestDef(o.questId, QuestDef.Kind.SIDE, offerText, "");
    }
    return out;
  }

  /** N/A phrase when no offers can be shown (blocked or rolled zero). */
  public String buildAssignmentNa(ZqsConversationContext ctx) {
    if (ctx == null) ctx = new ZqsConversationContext();
    return rt.buildAssignmentNaText(ctx);
  }

  /** Farewell text; caller must set ctx.conversationResult appropriately (accepted|declined|no_offer|left|...). */
  public String buildFarewell(ZqsConversationContext ctx) {
    if (ctx == null) ctx = new ZqsConversationContext();
    return rt.buildFarewellText(ctx);
  }

  /** Persist decline in ZQS history view (no quest record created). */
  public void declineOffer(String questId, long epochSec) {
    rt.declineOffer(questId, epochSec);
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

  public QuestDef[] listTurnInReady(int desiredCount) {
    int n = Math.max(0, Math.min(3, desiredCount));
    QuestDef[] out = new QuestDef[n];
    if (n <= 0) return out;

    java.util.ArrayList<ZqsSaveBlock.LogbookEntry> list = rt.listTurnInReadyForNpc("WANDER_QUEST_GUY");
    int m = Math.min(n, (list != null) ? list.size() : 0);
    for (int i = 0; i < m; i++) {
      ZqsSaveBlock.LogbookEntry le = list.get(i);
      if (le == null || le.questId == null || le.questId.isEmpty()) continue;

      int rewardCopper = 0;
      ZqsSaveBlock.PersistentQuestRecordSave pr = rt.findQuestRecord(le.questId);
      if (pr != null && pr.reward != null) rewardCopper = Math.max(0, pr.reward.rewardTotalCopper);

      String desc = "Belohnung: " + rewardCopper + " Kupfer";
      out[i] = new QuestDef(le.questId, QuestDef.Kind.SIDE, le.title, desc);
    }
    return out;
  }

  public boolean claimReward(String questId, QuestLog log,
                             com.yourgame.survival.data.Wallet wallet,
                             com.yourgame.survival.data.Inventory inv,
                             long epochSec) {
    if (questId == null || questId.isEmpty()) return false;
    if (log == null) return false;

    boolean ok = rt.claimQuestReward(questId, wallet, inv, epochSec);
    if (!ok) return false;

    ZqsSaveBlock.LogbookEntry le = rt.findLogbookEntry(questId);
    String fs = (le != null) ? le.finalStatus : "erledigt";
    log.updateZqsState(questId, fs);
    return true;
  }
}

package com.yourgame.survival.quest.zqs.runtime;

import com.yourgame.survival.data.DataRegistry;
import com.yourgame.survival.data.PlayerProgress;
import com.yourgame.survival.quest.zqs.blueprint.BlueprintLoader;
import com.yourgame.survival.quest.zqs.blueprint.BlueprintRegistry;
import com.yourgame.survival.quest.zqs.catalog.CatalogIndexes;
import com.yourgame.survival.quest.zqs.catalog.CatalogRuntimeLoader;
import com.yourgame.survival.quest.zqs.catalog.ContentCatalogRuntime;
import com.yourgame.survival.quest.zqs.generator.RewardProfiles;
import com.yourgame.survival.quest.zqs.generator.RewardProfilesLoader;
import com.yourgame.survival.quest.zqs.generator.RepeatRulesGate;
import com.yourgame.survival.quest.zqs.generator.ZqsOfferGenerator;
import com.yourgame.survival.quest.zqs.knowledge.PlayerKnowledgeState;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;
import com.yourgame.survival.quest.zqs.text.SnippetPool;
import com.yourgame.survival.quest.zqs.text.ZqsSnippetLoader;
import com.yourgame.survival.quest.zqs.text.ZqsTextAssembler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Canonical ZQS facade. All UI/NPC docking must go through this runtime.
 *
 * IMPORTANT:
 * - OfferBuffer is runtime-only and not persisted.
 * - Persisted data lives in ZqsSaveBlock.
 */
public final class ZqsRuntime {

  private final long worldSeed;

  private DataRegistry data;
  private PlayerProgress progress;
  private ZqsSaveBlock save;

  private com.yourgame.survival.worldmap.WorldMapState worldMap;
  private com.yourgame.survival.entity.Entities entities;

  // Catalog/Blueprints/Profiles
  private ContentCatalogRuntime catalog;
  private final CatalogIndexes catalogIdx = new CatalogIndexes();
  private final BlueprintRegistry blueprints = new BlueprintRegistry();
  private RewardProfiles rewardProfiles;

  // Knowledge (runtime view)
  private final PlayerKnowledgeState knowledge = new PlayerKnowledgeState();

  // Text
  private SnippetPool snippetPool;
  private ZqsTextAssembler text;

  // OfferBuffer (runtime-only)
  private final ArrayList<GeneratedQuestOffer> offerBuffer = new ArrayList<>(3);
  private final HashMap<String, GeneratedQuestOffer> offersById = new HashMap<>();

  // Persisted view indexes
  private final HashMap<String, ZqsSaveBlock.LogbookEntry> logbookByQuestId = new HashMap<>();

  // Generator
  private final ZqsOfferGenerator generator = new ZqsOfferGenerator();

  public ZqsRuntime(long worldSeed) {
    this.worldSeed = worldSeed;
  }

  public void bindWorldMap(com.yourgame.survival.worldmap.WorldMapState worldMap) {
    this.worldMap = worldMap;
  }

  public void bindEntities(com.yourgame.survival.entity.Entities entities) {
    this.entities = entities;
  }

  public void bind(DataRegistry data, PlayerProgress progress, ZqsSaveBlock save) {
    this.data = data;
    this.progress = progress;
    this.save = save;
    if (this.save == null) throw new IllegalStateException("ZqsSaveBlock missing");

    // Load DBs (no fallback allowed)
    this.catalog = new CatalogRuntimeLoader().load("data/zqs/catalog_runtime_v1.json");
    this.catalogIdx.index(catalog);

    this.blueprints.loadFrom(new BlueprintLoader().load("data/zqs/blueprints_v1.json"));
    this.rewardProfiles = new RewardProfilesLoader().load("data/zqs/reward_profiles_v1.json");

    // Knowledge from save
    this.knowledge.importFromSave(this.save);

    // Text
    this.snippetPool = new ZqsSnippetLoader().load("data/zqs/text_snippets_de_DE_v1.json");
    long seed = this.save.rngState.generatorRng;
    this.text = new ZqsTextAssembler(snippetPool, seed);

    // Import persisted view indexes
    rebuildIndexesFromSave();
  }

  private void rebuildIndexesFromSave() {
    logbookByQuestId.clear();
    if (save == null || save.logbook == null || save.logbook.entries == null) return;
    for (int i = 0; i < save.logbook.entries.size(); i++) {
      ZqsSaveBlock.LogbookEntry e = save.logbook.entries.get(i);
      if (e == null || e.questId == null || e.questId.isEmpty()) continue;
      logbookByQuestId.put(e.questId, e);
    }
  }

  public ZqsSaveBlock.LogbookEntry findLogbookEntry(String questId) {
    if (questId == null || questId.isEmpty()) return null;
    return logbookByQuestId.get(questId);
  }

  // ---------------- Offers (runtime-only) ----------------

  public void generateNqOffers(ZqsConversationContext ctx) {
    clearOffers();

    int desired = 3;
    long runtimeSec = (ctx != null) ? ctx.runtimeSec : 0L;
    int playerLevel = (progress != null) ? progress.level : 1;
    int playerSlId = (save != null && save.storyState != null) ? save.storyState.playerSlId : 0;
    if (playerSlId <= 0) throw new IllegalStateException("ZQS storyState.playerSlId missing");
    int openQuestsCount = (ctx != null) ? ctx.openQuestsCount : 0;

    ZqsOfferGenerator.Result r = generator.generateNqOffers(
        worldSeed,
        runtimeSec,
        playerLevel,
        playerSlId,
        openQuestsCount,
        knowledge,
        catalog,
        catalogIdx,
        blueprints,
        rewardProfiles,
        save,
        desired);

    if (r != null) {
      if (ctx != null) {
        ctx.nqGenerationPossible = r.nqGenerationPossible;
        ctx.blockReason = (r.blockReason != null && !r.blockReason.isEmpty()) ? r.blockReason : "none";
        ctx.naReason = (r.naReason != null) ? r.naReason : "";
      }

      for (int i = 0; i < r.offers.size() && offerBuffer.size() < 3; i++) {
        GeneratedQuestOffer o = r.offers.get(i);
        if (o == null || o.questId == null || o.questId.isEmpty()) continue;
        offerBuffer.add(o);
        offersById.put(o.questId, o);
      }
    }

    if (ctx != null) {
      ctx.nqOfferCount = offerBuffer.size();
    }
  }

  private void clearOffers() {
    offerBuffer.clear();
    offersById.clear();
  }

  public int offerCount() { return offerBuffer.size(); }

  public GeneratedQuestOffer offer(int idx) {
    if (idx < 0 || idx >= offerBuffer.size()) return null;
    return offerBuffer.get(idx);
  }

  // ---------------- Accept ----------------

  public PersistentQuestRecord acceptOffer(String questId, long runtimeSec) {
    if (save == null) throw new IllegalStateException("ZqsSaveBlock missing (cannot persist accepted quest)");
    if (questId == null || questId.isEmpty()) throw new IllegalArgumentException("questId missing");

    GeneratedQuestOffer offer = offersById.get(questId);
    if (offer == null) throw new IllegalStateException("Offer not found: " + questId);

    // RepeatRules cooldown is persisted on accept (OfferBuffer is runtime-only)
    if (offer.blueprintId != null && !offer.blueprintId.isEmpty()) {
      com.yourgame.survival.quest.zqs.blueprint.QuestBlueprint bp = blueprints.byId.get(offer.blueprintId);
      if (bp != null && bp.repeatRules != null && bp.repeatRules.cooldownHours > 0) {
        long until = runtimeSec + (long) bp.repeatRules.cooldownHours * 3600L;
        RepeatRulesGate.putCooldown(save, "bp:" + bp.blueprintId, until);
      }
    }

    PersistentQuestRecord rec = new PersistentQuestRecord(offer);
    rec.acceptedAtRuntimeSec = runtimeSec;
    rec.finalStatus = "aktiv";

    // Text composition + snippet ids (strict)
    String title;
    String acceptedText;

    java.util.ArrayList<String> assignIds = new java.util.ArrayList<>();
    java.util.ArrayList<String> rewardIds = new java.util.ArrayList<>();

    com.yourgame.survival.quest.zqs.text.ConversationContext c = new com.yourgame.survival.quest.zqs.text.ConversationContext();
    c.questType = rec.questType;
    c.questSubtype = rec.questSubtype;
    if (rec.reward != null) {
      c.rewardState = (rec.reward.rewardState != null) ? rec.reward.rewardState.id : "";
      c.rewardTextMode = (rec.reward.rewardTextMode != null) ? rec.reward.rewardTextMode.id : "";
    }

    var builtAssign = text.buildAssignment(c);
    title = (builtAssign != null) ? fillQuestPlaceholders(builtAssign.full, offer, rec.reward) : "";
    acceptedText = title;
    rec.acceptedText = acceptedText;

    if (builtAssign != null) {
      if (builtAssign.main != null && nonEmpty(builtAssign.main.snippetId)) assignIds.add(builtAssign.main.snippetId);
      if (builtAssign.middle != null && nonEmpty(builtAssign.middle.snippetId)) assignIds.add(builtAssign.middle.snippetId);
      if (builtAssign.end != null && nonEmpty(builtAssign.end.snippetId)) assignIds.add(builtAssign.end.snippetId);
    }

    var builtReward = text.buildReward(c);
    if (builtReward != null) {
      if (builtReward.main != null && nonEmpty(builtReward.main.snippetId)) rewardIds.add(builtReward.main.snippetId);
      if (builtReward.middle != null && nonEmpty(builtReward.middle.snippetId)) rewardIds.add(builtReward.middle.snippetId);
      if (builtReward.end != null && nonEmpty(builtReward.end.snippetId)) rewardIds.add(builtReward.end.snippetId);
    }

    // acceptedText: assignment + reward (filled strictly from offer/record)
    String rewardText = (builtReward != null) ? fillQuestPlaceholders(builtReward.full, offer, rec.reward) : "";
    acceptedText = join2(acceptedText, rewardText);
    rec.acceptedText = acceptedText;

    // Logbook numbering
    int logNr = Math.max(1, save.logbook.nextLogbookEntryNr);
    save.logbook.nextLogbookEntryNr = logNr + 1;
    rec.logbookEntryNr = logNr;

    // Upsert record
    ZqsSaveBlock.PersistentQuestRecordSave pr = new ZqsSaveBlock.PersistentQuestRecordSave();
    pr.questId = rec.questId;
    pr.questFamily = (rec.questFamily != null && !rec.questFamily.isEmpty()) ? rec.questFamily : "NQ";
    pr.questType = (rec.questType != null) ? rec.questType : "";
    pr.questSubtype = (rec.questSubtype != null) ? rec.questSubtype : "";
    pr.blueprintId = (rec.blueprintId != null) ? rec.blueprintId : "";

    pr.target.targetType = (rec.targetType != null) ? rec.targetType : "";
    pr.target.targetId = (rec.targetId != null) ? rec.targetId : "";
    pr.target.targetName = (rec.targetName != null) ? rec.targetName : "";
    pr.target.targetAmount = rec.targetQuantity;
    pr.target.targetValueCopper = offer.targetValueCopper;

    pr.expectedTimeSec = rec.expectedTimeSec;

    if (rec.reward != null) {
      pr.reward.rewardTotalCopper = rec.reward.rewardTotalCopper;
      pr.reward.rewardTextMode = (rec.reward.rewardTextMode != null) ? rec.reward.rewardTextMode.id : "currency";
      pr.reward.rewardCurrencyCopper = rec.reward.rewardCurrencyCopper;
      pr.reward.rewardCurrencySilver = rec.reward.rewardCurrencySilver;
      pr.reward.rewardCurrencyGold = rec.reward.rewardCurrencyGold;
      pr.reward.rewardItems.clear();
      if (rec.reward.rewardItems != null) {
        for (RewardBlock.RewardItem ri : rec.reward.rewardItems) {
          if (ri == null) continue;
          ZqsSaveBlock.RewardItem x = new ZqsSaveBlock.RewardItem();
          x.itemId = ri.itemId;
          x.amount = ri.amount;
          pr.reward.rewardItems.add(x);
        }
      }
    }

    pr.text.textProfileId = (rec.textProfileId != null) ? rec.textProfileId : "";
    pr.text.generatedTextIds.clear();
    pr.text.generatedTextIds.assignment.addAll(assignIds);
    pr.text.generatedTextIds.reward.addAll(rewardIds);
    pr.text.acceptedText = (acceptedText != null) ? acceptedText : "";

    pr.source.sourceNpcId = (rec.sourceNpcId != null) ? rec.sourceNpcId : "";
    pr.source.giverNpcId = (rec.sourceNpcId != null) ? rec.sourceNpcId : "";

    pr.status.finalStatus = (rec.finalStatus != null && !rec.finalStatus.isEmpty()) ? rec.finalStatus : "aktiv";

    pr.timestamps.generatedAt = rec.generatedAtRuntimeSec;
    // offeredAt is when this offer existed in the runtime offer buffer (generation timestamp)
    pr.timestamps.offeredAt = rec.generatedAtRuntimeSec;
    pr.timestamps.acceptedAt = rec.acceptedAtRuntimeSec;
    pr.timestamps.completedAt = rec.completedAtRuntimeSec;
    pr.timestamps.failedAt = rec.failedAtRuntimeSec;

    pr.logbookEntryNr = logNr;

    for (int i = save.playerQuestDb.records.size() - 1; i >= 0; i--) {
      ZqsSaveBlock.PersistentQuestRecordSave old = save.playerQuestDb.records.get(i);
      if (old != null && questId.equals(old.questId)) save.playerQuestDb.records.remove(i);
    }
    save.playerQuestDb.records.add(pr);

    // Logbook entry
    ZqsSaveBlock.LogbookEntry le = new ZqsSaveBlock.LogbookEntry();
    le.logbookEntryNr = logNr;
    le.questId = rec.questId;
    le.questFamily = pr.questFamily;
    le.title = (title != null) ? title : "";
    le.acceptedText = pr.text.acceptedText;
    le.giverNpcId = pr.source.giverNpcId;
    le.acceptedLocation = "";
    le.acceptedAt = rec.acceptedAtRuntimeSec;
    le.finalStatus = pr.status.finalStatus;
    le.completedAt = rec.completedAtRuntimeSec;
    save.logbook.entries.add(le);

    // History index (save-side)
    // Seen list: accepted quests must be seen.
    if (!save.questHistoryIndex.seenQuestIds.contains(rec.questId)) {
      save.questHistoryIndex.seenQuestIds.add(rec.questId);
    }
    removeAll(save.questHistoryIndex.completedQuestIds, rec.questId);
    removeAll(save.questHistoryIndex.expiredQuestIds, rec.questId);
    removeAll(save.questHistoryIndex.declinedQuestIds, rec.questId);
    removeAll(save.questHistoryIndex.activeQuestIds, rec.questId);
    save.questHistoryIndex.activeQuestIds.add(rec.questId);

    // Rebuild indexes for lookup
    rebuildIndexesFromSave();

    return rec;
  }

  private static void removeAll(java.util.ArrayList<String> list, String id) {
    if (list == null || id == null || id.isEmpty()) return;
    for (int i = list.size() - 1; i >= 0; i--) {
      String s = list.get(i);
      if (id.equals(s)) list.remove(i);
    }
  }

  private static boolean nonEmpty(String s) { return s != null && !s.isEmpty(); }

  // ---------------- Text ----------------

  public String buildGreeting(ZqsConversationContext ctx) {
    com.yourgame.survival.quest.zqs.text.ConversationContext c = mapCtx(ctx);
    return text.buildGreeting(c).full;
  }

  public String buildAssignmentText(GeneratedQuestOffer offer) {
    if (offer == null) return "";
    com.yourgame.survival.quest.zqs.text.ConversationContext c = new com.yourgame.survival.quest.zqs.text.ConversationContext();
    c.questType = offer.questType;
    c.questSubtype = offer.questSubtype;
    String raw = text.buildAssignment(c).full;
    return fillQuestPlaceholders(raw, offer, offer.reward);
  }

  private String fillQuestPlaceholders(String tpl, GeneratedQuestOffer o, RewardBlock reward) {
    if (tpl == null) return "";
    if (o == null) return tpl;
    String out = tpl;

    out = out.replace("{target_quantity}", String.valueOf(Math.max(0, o.targetQuantity)));
    out = out.replace("{target_name}", safe(o.targetName));
    out = out.replace("{target_region}", "");
    out = out.replace("{target_entity}", "");

    out = out.replace("{output_quantity}", String.valueOf(Math.max(0, o.targetQuantity)));
    out = out.replace("{output_item_name}", safe(o.targetName));

    RewardBlock r = reward;
    if (r != null) {
      out = out.replace("{reward_currency_copper}", String.valueOf(Math.max(0, r.rewardCurrencyCopper)));
      out = out.replace("{reward_currency_silver}", String.valueOf(Math.max(0, r.rewardCurrencySilver)));
      out = out.replace("{reward_currency_gold}", String.valueOf(Math.max(0, r.rewardCurrencyGold)));
      out = out.replace("{reward_total_copper}", String.valueOf(Math.max(0, r.rewardTotalCopper)));
      out = out.replace("{reward_items}", formatRewardItems(r));
    } else {
      out = out.replace("{reward_currency_copper}", "0");
      out = out.replace("{reward_currency_silver}", "0");
      out = out.replace("{reward_currency_gold}", "0");
      out = out.replace("{reward_total_copper}", "0");
      out = out.replace("{reward_items}", "");
    }

    return out;
  }

  private String formatRewardItems(RewardBlock r) {
    if (r == null || r.rewardItems == null || r.rewardItems.isEmpty()) return "";
    StringBuilder sb = new StringBuilder();
    for (RewardBlock.RewardItem it : r.rewardItems) {
      if (it == null) continue;
      if (it.amount <= 0) continue;
      String name = "Item#" + it.itemId;
      if (data != null && it.itemId >= 0 && it.itemId < data.items.length) {
        var def = data.items[it.itemId];
        if (def != null && def.name != null && !def.name.isEmpty()) name = def.name;
      }
      if (sb.length() > 0) sb.append(", ");
      sb.append(name).append(" x").append(it.amount);
    }
    return sb.toString();
  }

  private static String safe(String s) { return (s == null) ? "" : s; }

  private static String join2(String a, String b) {
    String x = (a == null) ? "" : a.trim();
    String y = (b == null) ? "" : b.trim();
    if (x.isEmpty()) return y;
    if (y.isEmpty()) return x;
    return x + " " + y;
  }

  private static com.yourgame.survival.quest.zqs.text.ConversationContext mapCtx(ZqsConversationContext ctx) {
    com.yourgame.survival.quest.zqs.text.ConversationContext c = new com.yourgame.survival.quest.zqs.text.ConversationContext();
    if (ctx == null) return c;
    c.timeOfDay = ctx.timeOfDay;
    c.worldstressZone = ctx.worldstressZone;
    c.debugHqMode = ctx.debugHqMode;
    c.conversationResult = ctx.conversationResult;
    c.conversationNextStep = ctx.conversationNextStep;
    c.openQuestsCount = ctx.openQuestsCount;
    c.completedQuestsCount = ctx.completedQuestsCount;
    c.nqGenerationPossible = ctx.nqGenerationPossible;
    c.blockReason = ctx.blockReason;
    c.naReason = ctx.naReason;
    c.nqOfferCount = ctx.nqOfferCount;
    return c;
  }
}

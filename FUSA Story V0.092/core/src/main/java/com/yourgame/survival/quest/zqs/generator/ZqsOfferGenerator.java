package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.blueprint.BlueprintRegistry;
import com.yourgame.survival.quest.zqs.blueprint.QuestBlueprint;
import com.yourgame.survival.quest.zqs.catalog.CatalogEntryRt;
import com.yourgame.survival.quest.zqs.catalog.CatalogKind;
import com.yourgame.survival.quest.zqs.catalog.ContentCatalogRuntime;
import com.yourgame.survival.quest.zqs.catalog.CatalogIndexes;
import com.yourgame.survival.quest.zqs.knowledge.KnowledgeQuery;
import com.yourgame.survival.quest.zqs.knowledge.PlayerKnowledgeState;
import com.yourgame.survival.quest.zqs.runtime.GeneratedQuestOffer;
import com.yourgame.survival.quest.zqs.runtime.RewardBlock;
import com.yourgame.survival.quest.zqs.runtime.RewardTextMode;
import com.yourgame.survival.quest.zqs.runtime.TargetBlock;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Generates runtime-only NQ offers from runtime catalog + blueprints.
 * Offers are NOT persisted.
 */
public final class ZqsOfferGenerator {

  public static final class Result {
    public final ArrayList<GeneratedQuestOffer> offers = new ArrayList<>(3);
    public boolean nqGenerationPossible = true;
    public String blockReason = "none";
    public String naReason = "";
  }

  private final RewardCalculator rewardCalc = new RewardCalculator();

  public Result generateNqOffers(
      long worldSeed,
      long runtimeSec,
      int playerLevel,
      int playerSlId,
      int openQuestsCount,
      PlayerKnowledgeState knowledge,
      ContentCatalogRuntime catalog,
      CatalogIndexes catalogIdx,
      BlueprintRegistry blueprints,
      RewardProfiles rewardProfiles,
      ZqsSaveBlock save,
      int desiredCount
  ) {
    if (blueprints == null) throw new IllegalArgumentException("blueprints missing");
    if (catalog == null) throw new IllegalArgumentException("catalog missing");
    if (knowledge == null) throw new IllegalArgumentException("knowledge missing");
    if (save == null) throw new IllegalArgumentException("save missing");

    Result out = new Result();

    // Hard gate: cap reached
    // (No fallback; explicit behavior.)
    if (openQuestsCount > 10) {
      out.nqGenerationPossible = false;
      out.blockReason = "cap_reached";
      out.naReason = "cap_reached";
      return out;
    }

    // Cleanup expired cooldown entries (persisted)
    RepeatRulesGate.cleanupExpired(save, runtimeSec);

    // Timer gate (persisted)
    long due = (save.nqTimerState != null) ? save.nqTimerState.nextNqDueRuntimeSec : 0L;
    if (due > 0L && runtimeSec < due) {
      out.nqGenerationPossible = false;
      out.blockReason = "timer_not_due";
      out.naReason = "timer_reset";
      return out;
    }

    int n = Math.max(0, Math.min(3, desiredCount));

    // RNG state (persisted)
    long rngSeed = (save.rngState != null) ? save.rngState.generatorRng : 0L;
    if (rngSeed == 0L) {
      long s = worldSeed ^ 0x5A51535F535953L;
      rngSeed = (s != 0L) ? s : 0x9E3779B97F4A7C15L;
    }
    Rng rng = new Rng(rngSeed);

    HashSet<String> idsInBatch = new HashSet<>();
    HashSet<String> batchBlueprintIds = new HashSet<>();
    HashSet<String> batchQuestShapeKeys = new HashSet<>();
    HashSet<String> blockedTargetKeys = RepeatRulesGate.activeTargetKeys(save);
    HashSet<String> activeRepeatFamilyKeys = RepeatRulesGate.activeRepeatFamilyKeys(save);

    for (int i = 0; i < n; i++) {
      ArrayList<QuestBlueprint> candidates = blueprints.filter("NQ", playerLevel);
      if (candidates.isEmpty()) break;

      ArrayList<QuestBlueprint> eligibleBps = new ArrayList<>();
      for (QuestBlueprint b : candidates) {
        if (b == null) continue;
        if (b.repeatRules != null && b.repeatRules.cooldownHours > 0) {
          String cdKey = "bp:" + b.blueprintId;
          if (RepeatRulesGate.isOnCooldown(save, cdKey, runtimeSec)) continue;
        }
        if (b.repeatRules != null && b.repeatRules.denySameFamily) {
          String rfk = safe(b.repeatFamilyKey);
          if (rfk.isEmpty()) {
            throw new IllegalStateException("Blueprint missing repeatFamilyKey (denySameFamily=true): " + safe(b.blueprintId));
          }
          if (activeRepeatFamilyKeys.contains(rfk)) continue;
        }
        eligibleBps.add(b);
      }
      if (eligibleBps.isEmpty()) {
        out.nqGenerationPossible = false;
        out.blockReason = "no_valid_blueprints";
        break;
      }

      QuestBlueprint bp = pickDiversifiedBlueprint(rng, eligibleBps, batchBlueprintIds, batchQuestShapeKeys);
      if (bp == null) break;

      TargetBlock tb = pickTarget(rng, bp, catalog, knowledge, playerSlId, blockedTargetKeys);
      if (tb == null) {
        out.nqGenerationPossible = false;
        out.blockReason = "no_valid_targets";
        out.naReason = "no_valid_targets";
        break;
      }

      int qty = pickAmount(rng, bp.amountMin, bp.amountMax);
      tb.targetAmount = qty;

      int expectedTimeSec = (int) Math.floor(qty * 120f * 0.86f);

      if (rewardProfiles == null) {
        throw new IllegalStateException("RewardProfiles missing (cannot compute rewards)");
      }
      String rpid = safe(bp.rewardProfileId);
      if (rpid.isEmpty()) {
        throw new IllegalStateException("Blueprint missing rewardProfileId: " + safe(bp.blueprintId));
      }

      RewardProfileDef rp = rewardProfiles.get(rpid);
      if (rp == null) {
        throw new IllegalStateException("Unknown rewardProfileId: " + rpid + " (blueprint=" + safe(bp.blueprintId) + ")");
      }

      // Strict: profiles must explicitly define formula type; no questType-based fallback.
      String rewardFormulaType = safe(rp.rewardFormulaType).trim();
      if (rewardFormulaType.isEmpty()) {
        throw new IllegalStateException("RewardProfile missing rewardFormulaType: " + rpid);
      }

      // Strict: v1 payout is wallet copper -> profile must allow currency rewards.
      if (!rp.allowCurrencyRewards) {
        throw new IllegalStateException("RewardProfile disallows currency rewards (unsupported in v1): " + rpid);
      }

      RewardBlock reward = rewardCalc.computeBaseReward(rewardFormulaType, bp.questType, bp.questSubtype, tb, expectedTimeSec);

      // Strict: rewardTextMode must resolve.
      RewardTextMode m = RewardTextMode.byId(rp.rewardTextMode);
      if (m == null) {
        throw new IllegalStateException("Unknown rewardTextMode: " + safe(rp.rewardTextMode) + " (profile=" + rpid + ")");
      }
      reward.rewardTextMode = m;

      int qnr = (save.counters != null) ? save.counters.questNrCounter : 1;
      String questId = ZqsQuestIdFactory.buildNqId(playerSlId, qnr, "P0", bp.blueprintId, tb.targetKind, tb.targetId, qty);
      if (questId == null || questId.isEmpty()) throw new IllegalStateException("QuestID build failed");
      if (idsInBatch.contains(questId)) {
        qnr++;
        questId = ZqsQuestIdFactory.buildNqId(playerSlId, qnr, "P0", bp.blueprintId, tb.targetKind, tb.targetId, qty);
      }
      idsInBatch.add(questId);
      if (save.counters != null) save.counters.questNrCounter = qnr + 1;

      GeneratedQuestOffer offer = new GeneratedQuestOffer();
      offer.questId = questId;
      offer.questFamily = safe(bp.objectiveFamily);
      offer.questType = bp.questType;
      offer.questSubtype = bp.questSubtype;
      offer.blueprintId = bp.blueprintId;
      offer.repeatFamilyKey = safe(bp.repeatFamilyKey);
      offer.targetType = tb.targetKind;
      offer.targetId = tb.targetId;
      offer.targetName = tb.targetName;
      offer.targetQuantity = qty;
      offer.targetValueCopper = tb.targetValueCopper;
      offer.targetRegionId = safe(tb.regionId);
      offer.targetRegionName = safeNameByKey(catalogIdx, offer.targetRegionId);

      // Entity meta (receiver preferred, else giver)
      offer.targetEntityId = !safe(tb.receiverNpcKey).isEmpty() ? safe(tb.receiverNpcKey) : safe(tb.giverNpcKey);
      offer.targetEntityName = safeNameByKey(catalogIdx, offer.targetEntityId);

      offer.progressKey = buildProgressKey(safe(bp.questSubtype), tb);
      offer.expectedTimeSec = expectedTimeSec;
      offer.reward = reward;
      offer.textProfileId = (bp.textProfileId != null) ? bp.textProfileId : "";
      offer.status = "offered";
      offer.sourceNpcId = "WANDER_QUEST_GUY";
      offer.generatedAtRuntimeSec = runtimeSec;

      out.offers.add(offer);

      batchBlueprintIds.add(safe(bp.blueprintId));
      batchQuestShapeKeys.add(buildQuestShapeKey(bp.questType, bp.questSubtype));
      blockedTargetKeys.add(buildTargetKey(tb.targetKind, tb.targetId));
    }

    if (out.offers.isEmpty() && out.nqGenerationPossible && (out.blockReason == null || out.blockReason.isEmpty() || "none".equals(out.blockReason))) {
      // generation was possible but the roll produced 0 offers (e.g. desiredCount=0)
      out.naReason = "rolled_zero";
    }

    // Roll next due time after generation attempt.
    if (save.nqTimerState != null) {
      int min = 60 * 60;
      int max = 6 * 60 * 60;
      int span = max - min + 1;
      int off = (int) Math.floor(rng.nextFloat01() * span);
      if (off < 0) off = 0;
      if (off >= span) off = span - 1;
      int delta = min + off;

      save.nqTimerState.lastRolledRefreshMinSec = min;
      save.nqTimerState.lastRolledRefreshMaxSec = max;
      save.nqTimerState.lastNqGeneratedAtRuntimeSec = runtimeSec;
      save.nqTimerState.nextNqDueRuntimeSec = runtimeSec + delta;
    }

    if (save.rngState != null) save.rngState.generatorRng = rng.exportState();
    return out;
  }

  private static final class Rng {
    private long s;
    Rng(long seed) { s = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L; }

    long nextLong() {
      long x = s;
      x ^= x >>> 12;
      x ^= x << 25;
      x ^= x >>> 27;
      s = x;
      return x * 2685821657736338717L;
    }

    float nextFloat01() {
      return ((nextLong() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
    }

    long exportState() { return s; }
  }

  private static int pickAmount(Rng rng, int min, int max) {
    int lo = Math.max(1, min);
    int hi = Math.max(lo, max);
    int span = hi - lo + 1;
    int off = (int) Math.floor(rng.nextFloat01() * span);
    if (off < 0) off = 0;
    if (off >= span) off = span - 1;
    return lo + off;
  }

  private static QuestBlueprint pickDiversifiedBlueprint(Rng rng,
                                                         ArrayList<QuestBlueprint> list,
                                                         HashSet<String> usedBlueprintIds,
                                                         HashSet<String> usedQuestShapeKeys) {
    if (list == null || list.isEmpty()) return null;

    float sum = 0f;
    for (QuestBlueprint b : list) {
      sum += diversifiedBlueprintWeight(b, usedBlueprintIds, usedQuestShapeKeys);
    }
    if (sum <= 0f) return pickWeightedFallback(list);

    float r = rng.nextFloat01() * sum;
    float acc = 0f;
    for (QuestBlueprint b : list) {
      float w = diversifiedBlueprintWeight(b, usedBlueprintIds, usedQuestShapeKeys);
      if (w <= 0f) continue;
      acc += w;
      if (r <= acc) return b;
    }
    return pickWeightedFallback(list);
  }

  private static float diversifiedBlueprintWeight(QuestBlueprint b,
                                                  HashSet<String> usedBlueprintIds,
                                                  HashSet<String> usedQuestShapeKeys) {
    if (b == null) return 0f;
    float w = (b.weight > 0f) ? b.weight : 0f;
    if (w <= 0f) return 0f;

    if (usedBlueprintIds != null && usedBlueprintIds.contains(safe(b.blueprintId))) {
      w *= 0.15f;
    }
    if (usedQuestShapeKeys != null && usedQuestShapeKeys.contains(buildQuestShapeKey(b.questType, b.questSubtype))) {
      w *= 0.35f;
    }
    return w;
  }

  private static QuestBlueprint pickWeightedFallback(ArrayList<QuestBlueprint> list) {
    for (QuestBlueprint b : list) {
      if (b != null && b.weight > 0f) return b;
    }
    return list.get(0);
  }

  private static TargetBlock pickTarget(Rng rng, QuestBlueprint bp, ContentCatalogRuntime cat,
                                       PlayerKnowledgeState knowledge, int playerSlId,
                                       HashSet<String> blockedTargetKeys) {
    if (bp == null) return null;

    ArrayList<TargetBlock> candidates = new ArrayList<>();

    if (allows(bp, "item")) {
      collectTargets(candidates, CatalogKind.ITEM, cat.items, knowledge, playerSlId, blockedTargetKeys);
      collectTargets(candidates, CatalogKind.RESOURCE, cat.resources, knowledge, playerSlId, blockedTargetKeys);
    }
    if (allows(bp, "harvestable")) {
      collectTargets(candidates, CatalogKind.HARVESTABLE, cat.harvestables, knowledge, playerSlId, blockedTargetKeys);
    }
    if (allows(bp, "living")) {
      collectTargets(candidates, CatalogKind.LIVING, cat.livings, knowledge, playerSlId, blockedTargetKeys);
    }
    if (allows(bp, "poi")) {
      collectTargets(candidates, CatalogKind.POI, cat.pois, knowledge, playerSlId, blockedTargetKeys);
    }
    if (allows(bp, "npc")) {
      collectTargets(candidates, CatalogKind.NPC, cat.npcs, knowledge, playerSlId, blockedTargetKeys);
    }
    if (allows(bp, "region")) {
      collectTargets(candidates, CatalogKind.REGION, cat.regions, knowledge, playerSlId, blockedTargetKeys);
    }

    if (candidates.isEmpty()) return null;

    int idx = (int) Math.floor(rng.nextFloat01() * candidates.size());
    if (idx < 0) idx = 0;
    if (idx >= candidates.size()) idx = candidates.size() - 1;
    return candidates.get(idx);
  }

  private static boolean allows(QuestBlueprint bp, String kind) {
    if (bp.allowedTargetKinds == null) return false;
    for (String k : bp.allowedTargetKinds) if (kind.equals(k)) return true;
    return false;
  }

  private static void collectTargets(ArrayList<TargetBlock> out,
                                     CatalogKind kind,
                                     java.util.List<CatalogEntryRt> list,
                                     PlayerKnowledgeState knowledge,
                                     int playerSlId,
                                     HashSet<String> blockedTargetKeys) {
    if (out == null || list == null || list.isEmpty()) return;

    for (CatalogEntryRt e : list) {
      if (e == null || !e.isValid()) continue;
      if (e.slIdMax > playerSlId) continue;
      if (!KnowledgeQuery.passesKnownGate(knowledge, e)) continue;

      String targetKey = buildTargetKey(kind.id, e.key());
      if (blockedTargetKeys != null && blockedTargetKeys.contains(targetKey)) continue;

      TargetBlock tb = new TargetBlock();
      tb.targetKind = kind.id;
      tb.targetId = e.key();
      tb.targetName = (e.name != null) ? e.name : "";
      tb.targetValueCopper = e.valueCopper;
      tb.knownFlagRequired = e.knownRequired;
      tb.knownFlagState = true;
      tb.slId = Math.max(1, playerSlId);
      out.add(tb);
    }
  }

  private static String buildQuestShapeKey(String questType, String questSubtype) {
    return safe(questType) + "|" + safe(questSubtype);
  }

  private static String safeNameByKey(CatalogIndexes idx, String key) {
    if (idx == null) return "";
    String k = safe(key);
    if (k.isEmpty()) return "";
    CatalogEntryRt e = idx.get(k);
    return (e != null && e.name != null) ? e.name : "";
  }

  private static String buildProgressKey(String questSubtype, TargetBlock tb) {
    String st = safe(questSubtype);
    String tid = (tb != null) ? safe(tb.targetId) : "";

    if (st.equals("sammeln.item") || st.equals("liefern.item")) {
      return "inventory:item:" + tid;
    }
    if (st.equals("sammeln.harvestable")) {
      return "harvestable:" + tid;
    }
    if (st.equals("craften.recipe_output")) {
      return "craft:item:" + tid;
    }
    if (st.equals("craften.delivery")) {
      return "inventory:item:" + tid;
    }
    if (st.equals("finden.poi") || st.equals("finden.poi_loot")) {
      return "poi:" + tid;
    }
    if (st.equals("finden.object")) {
      return "object:" + tid;
    }
    if (st.equals("finden.person")) {
      return "person:" + tid;
    }
    if (st.equals("eskortieren.route")) {
      return "escort:" + tid;
    }
    throw new IllegalStateException("Unsupported questSubtype for progressKey mapping: '" + st + "'");
  }

  private static String buildTargetKey(String targetType, String targetId) {
    return safe(targetType) + ":" + safe(targetId);
  }

  private static String safe(String s) {
    return (s != null) ? s : "";
  }
}

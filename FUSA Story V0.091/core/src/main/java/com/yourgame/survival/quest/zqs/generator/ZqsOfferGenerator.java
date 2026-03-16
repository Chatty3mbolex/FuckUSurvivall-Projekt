package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.blueprint.BlueprintRegistry;
import com.yourgame.survival.quest.zqs.blueprint.QuestBlueprint;
import com.yourgame.survival.quest.zqs.catalog.CatalogEntryRt;
import com.yourgame.survival.quest.zqs.catalog.CatalogIndexes;
import com.yourgame.survival.quest.zqs.catalog.CatalogKind;
import com.yourgame.survival.quest.zqs.catalog.ContentCatalogRuntime;
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
    if (catalog == null || catalogIdx == null) throw new IllegalStateException("CatalogRuntime missing");
    if (blueprints == null) throw new IllegalStateException("BlueprintRegistry missing");
    if (save == null) throw new IllegalStateException("ZqsSaveBlock missing");

    Result out = new Result();

    // Eligibility gate: cap reached
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

    // RepeatRules inputs from save (active quests)
    HashSet<String> activeTargetKeys = RepeatRulesGate.activeTargetKeys(save);
    HashSet<String> activeQuestTypes = RepeatRulesGate.activeQuestTypes(save);

    for (int i = 0; i < n; i++) {
      ArrayList<QuestBlueprint> candidates = blueprints.filter("NQ", playerLevel);
      if (candidates.isEmpty()) break;

      // Apply RepeatRules gate: cooldown + denySameFamily
      ArrayList<QuestBlueprint> eligibleBps = new ArrayList<>();
      for (QuestBlueprint b : candidates) {
        if (b == null) continue;
        if (b.repeatRules != null && b.repeatRules.cooldownHours > 0) {
          String cdKey = "bp:" + b.blueprintId;
          if (RepeatRulesGate.isOnCooldown(save, cdKey, runtimeSec)) continue;
        }
        if (b.repeatRules != null && b.repeatRules.denySameFamily) {
          // v1 interpretation: questType is the repeat-family surrogate.
          if (activeQuestTypes.contains(b.questType)) continue;
        }
        eligibleBps.add(b);
      }
      if (eligibleBps.isEmpty()) {
        out.nqGenerationPossible = false;
        out.blockReason = "no_valid_blueprints";
        break;
      }

      QuestBlueprint bp = pickWeighted(rng, eligibleBps);
      if (bp == null) break;

      TargetBlock tb = pickTarget(rng, bp, catalog, knowledge, playerSlId, activeTargetKeys);
      if (tb == null) {
        out.nqGenerationPossible = false;
        out.blockReason = "no_valid_targets";
        out.naReason = "no_valid_targets";
        break;
      }

      int qty = pickAmount(rng, bp.amountMin, bp.amountMax);
      tb.targetAmount = qty;

      int expectedTimeSec = (int) Math.floor(qty * 120f * 0.86f);

      // Reward (copper-based)
      QuestBlueprintDef bpd = new QuestBlueprintDef();
      bpd.rewardProfileId = (bp.rewardProfileId != null) ? bp.rewardProfileId : "";
      RewardBlock reward = rewardCalc.computeBaseReward(bpd.rewardProfileId, bp.questType, bp.questSubtype, tb, expectedTimeSec);

      // rewardTextMode from profile for text filtering (payout remains copper)
      RewardProfileDef rp = (rewardProfiles != null) ? rewardProfiles.get(bp.rewardProfileId) : null;
      if (rp != null && rp.rewardTextMode != null) {
        RewardTextMode m = RewardTextMode.byId(rp.rewardTextMode);
        if (m != null) reward.rewardTextMode = m;
      }

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
      offer.questFamily = "NQ";
      offer.questType = bp.questType;
      offer.questSubtype = bp.questSubtype;
      offer.blueprintId = bp.blueprintId;
      offer.targetType = tb.targetKind;
      offer.targetId = tb.targetId;
      offer.targetName = tb.targetName;
      offer.targetQuantity = qty;
      offer.targetValueCopper = tb.targetValueCopper;
      offer.expectedTimeSec = expectedTimeSec;
      offer.reward = reward;
      offer.textProfileId = (bp.textProfileId != null) ? bp.textProfileId : "";
      offer.status = "offered";
      offer.sourceNpcId = "WANDER_QUEST_GUY";
      offer.generatedAtRuntimeSec = runtimeSec;

      out.offers.add(offer);
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

  private static QuestBlueprint pickWeighted(Rng rng, ArrayList<QuestBlueprint> list) {
    float sum = 0f;
    for (QuestBlueprint b : list) {
      if (b == null) continue;
      float w = (b.weight > 0f) ? b.weight : 0f;
      sum += w;
    }
    if (sum <= 0f) return null;
    float r = rng.nextFloat01() * sum;
    float acc = 0f;
    for (QuestBlueprint b : list) {
      if (b == null) continue;
      float w = (b.weight > 0f) ? b.weight : 0f;
      if (w <= 0f) continue;
      acc += w;
      if (r <= acc) return b;
    }
    return list.get(0);
  }

  private static TargetBlock pickTarget(Rng rng, QuestBlueprint bp, ContentCatalogRuntime cat,
                                       PlayerKnowledgeState knowledge, int playerSlId,
                                       HashSet<String> activeTargetKeys) {
    if (bp == null) return null;

    if (allows(bp, "item")) {
      TargetBlock tb = pickFromList(rng, CatalogKind.ITEM, cat.items, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
      tb = pickFromList(rng, CatalogKind.RESOURCE, cat.resources, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
    }
    if (allows(bp, "harvestable")) {
      TargetBlock tb = pickFromList(rng, CatalogKind.HARVESTABLE, cat.harvestables, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
    }
    if (allows(bp, "living")) {
      TargetBlock tb = pickFromList(rng, CatalogKind.LIVING, cat.livings, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
    }
    if (allows(bp, "poi")) {
      TargetBlock tb = pickFromList(rng, CatalogKind.POI, cat.pois, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
    }
    if (allows(bp, "npc")) {
      TargetBlock tb = pickFromList(rng, CatalogKind.NPC, cat.npcs, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
    }
    if (allows(bp, "region")) {
      TargetBlock tb = pickFromList(rng, CatalogKind.REGION, cat.regions, knowledge, playerSlId, activeTargetKeys);
      if (tb != null) return tb;
    }

    return null;
  }

  private static boolean allows(QuestBlueprint bp, String kind) {
    if (bp.allowedTargetKinds == null) return false;
    for (String k : bp.allowedTargetKinds) if (kind.equals(k)) return true;
    return false;
  }

  private static TargetBlock pickFromList(Rng rng, CatalogKind kind,
                                         java.util.List<CatalogEntryRt> list,
                                         PlayerKnowledgeState knowledge,
                                         int playerSlId,
                                         HashSet<String> activeTargetKeys) {
    if (list == null || list.isEmpty()) return null;

    ArrayList<CatalogEntryRt> candidates = new ArrayList<>();
    for (CatalogEntryRt e : list) {
      if (e == null || !e.isValid()) continue;
      if (e.slIdMax > playerSlId) continue;
      if (!KnowledgeQuery.passesKnownGate(knowledge, e)) continue;

      if (activeTargetKeys != null && !activeTargetKeys.isEmpty()) {
        String k = kind.id + ":" + e.key();
        if (activeTargetKeys.contains(k)) continue;
      }

      candidates.add(e);
    }
    if (candidates.isEmpty()) return null;

    int idx = (int) Math.floor(rng.nextFloat01() * candidates.size());
    if (idx < 0) idx = 0;
    if (idx >= candidates.size()) idx = candidates.size() - 1;
    CatalogEntryRt ce = candidates.get(idx);
    if (ce == null) return null;

    TargetBlock tb = new TargetBlock();
    tb.targetKind = kind.id;
    tb.targetId = ce.key();
    tb.targetName = (ce.name != null) ? ce.name : "";
    tb.targetValueCopper = ce.valueCopper;
    tb.knownFlagRequired = ce.knownRequired;
    tb.knownFlagState = true;
    tb.slId = Math.max(1, playerSlId);
    return tb;
  }
}

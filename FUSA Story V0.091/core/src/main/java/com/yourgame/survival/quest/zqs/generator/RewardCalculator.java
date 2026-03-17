package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.runtime.RewardBlock;
import com.yourgame.survival.quest.zqs.runtime.RewardState;
import com.yourgame.survival.quest.zqs.runtime.RewardTextMode;
import com.yourgame.survival.quest.zqs.runtime.TargetBlock;

/**
 * Only place where reward is computed.
 * Must follow: flow4/zqs_reward_logik_referenz_v1.md
 */
public final class RewardCalculator {

  /**
   * Canonical reward computation (copper-based).
   *
   * Primary driver is rewardFormulaType from RewardProfiles.
   * questType / questSubtype remain as a subtype-level fallback for formulas
   * that need finer branching (for example find.poi vs find.object).
   *
   * Reference: flow 4/zqs_reward_logik_referenz_v1.md
   */
  public RewardBlock computeBaseReward(String rewardFormulaType, String questType, String questSubtype,
                                      TargetBlock target, int expectedTimeSec) {
    if (target == null) throw new IllegalArgumentException("target missing");

    String formula = normalizeRewardFormulaTypeStrict(rewardFormulaType);
    String qst = normalizeQuestSubtype(questSubtype);

    RewardBlock r = new RewardBlock();
    r.rewardFormulaType = formula;

    int value = Math.max(0, target.targetValueCopper);
    int amount = Math.max(0, target.targetAmount);
    int timePart = Math.max(0, expectedTimeSec) * 33;

    int base;
    switch (formula) {
      case "escort":
        base = timePart;
        break;
      case "find":
        base = computeFindRewardStrict(qst, value, amount, timePart);
        break;
      case "collect":
      case "deliver":
      case "craft":
        base = (amount * value) + timePart;
        break;
      default:
        throw new IllegalStateException("Unknown rewardFormulaType: '" + formula + "'");
    }

    r.baseRewardCopper = base;
    r.penaltyCopper = 0;
    r.finalRewardCopper = base;
    r.rewardTotalCopper = base;
    r.rewardState = RewardState.ACCEPTED;
    r.rewardTextMode = RewardTextMode.CURRENCY;
    r.normalizeCurrency();

    return r;
  }

  public void applyPenaltyAndFinalize(RewardBlock r, int expectedTimeSec, int elapsedTimeSec) {
    if (r == null) return;
    int penalty = 0;
    if (elapsedTimeSec > expectedTimeSec) {
      penalty = (elapsedTimeSec - expectedTimeSec) * 150;
    }
    r.penaltyCopper = penalty;
    r.finalRewardCopper = r.baseRewardCopper - penalty;
    if (r.finalRewardCopper < 0) {
      r.rewardState = RewardState.FAILED;
      r.rewardTextMode = RewardTextMode.FAILED;
      r.rewardTotalCopper = 0;
    } else {
      r.rewardState = RewardState.CLAIMABLE;
      r.rewardTotalCopper = r.finalRewardCopper;
    }
    r.normalizeCurrency();
  }

  private static int computeFindRewardStrict(String questSubtype, int value, int amount, int timePart) {
    if ("finden.poi".equals(questSubtype)
        || "finden.poi_loot".equals(questSubtype)
        || "finden.object".equals(questSubtype)) {
      return value + timePart;
    }
    // Strict: no fallback for unsupported find-subtypes.
    throw new IllegalStateException("Unsupported questSubtype for rewardFormulaType=find: '" + safe(questSubtype) + "'");
  }

  private static String normalizeRewardFormulaTypeStrict(String rewardFormulaType) {
    String v = safe(rewardFormulaType).trim().toLowerCase();
    if (v.isEmpty()) throw new IllegalStateException("rewardFormulaType missing");

    // Strict allow-list. Any new type must be added explicitly.
    if ("collect".equals(v) || "deliver".equals(v) || "craft".equals(v) || "find".equals(v) || "escort".equals(v)) {
      return v;
    }
    throw new IllegalStateException("Unknown rewardFormulaType: '" + v + "'");
  }

  private static String normalizeQuestSubtype(String questSubtype) {
    return safe(questSubtype).trim().toLowerCase();
  }

  private static String safe(String s) {
    return (s != null) ? s : "";
  }
}

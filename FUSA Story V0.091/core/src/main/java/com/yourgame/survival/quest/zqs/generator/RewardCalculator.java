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

  public RewardBlock computeBaseReward(QuestBlueprintDef bp, TargetBlock target, int expectedTimeSec) {
    if (bp == null) throw new IllegalArgumentException("bp missing");
    if (target == null) throw new IllegalArgumentException("target missing");

    // Legacy adapter: when only rewardProfileId is provided, treat as generic formula.
    return computeBaseReward(
        (bp.rewardProfileId != null) ? bp.rewardProfileId : "",
        null,
        null,
        target,
        expectedTimeSec);
  }

  /**
   * Canonical reward computation (copper-based).
   *
   * Reference: flow 4/zqs_reward_logik_referenz_v1.md
   */
  public RewardBlock computeBaseReward(String rewardFormulaType, String questType, String questSubtype,
                                      TargetBlock target, int expectedTimeSec) {
    if (target == null) throw new IllegalArgumentException("target missing");

    RewardBlock r = new RewardBlock();
    r.rewardFormulaType = (rewardFormulaType != null) ? rewardFormulaType : "";

    // No invented data: we compute only from the canonical target value + expected time.
    // Quest-type specific formulas:
    // - sammeln / liefern: (amount * value) + (time * 33)
    // - finden.poi / finden.poi_loot: (value) + (time * 33)
    // - eskortieren: (time * 33)
    int value = Math.max(0, target.targetValueCopper);
    int timePart = Math.max(0, expectedTimeSec) * 33;

    String qt = (questType == null) ? "" : questType.trim().toLowerCase();
    String qst = (questSubtype == null) ? "" : questSubtype.trim().toLowerCase();

    int base;
    if (qt.equals("eskortieren")) {
      base = timePart;
    } else if (qt.equals("finden") && (qst.equals("finden.poi") || qst.equals("finden.poi_loot"))) {
      base = value + timePart;
    } else {
      base = (Math.max(0, target.targetAmount) * value) + timePart;
    }

    r.baseRewardCopper = base;
    r.penaltyCopper = 0;
    r.finalRewardCopper = base;
    r.rewardTotalCopper = base;
    r.rewardState = RewardState.ACCEPTED;
    r.rewardTextMode = RewardTextMode.CURRENCY;
    r.normalizeCurrency();

    // Reward distribution is currently copper-only payout (wallet.copper) as the canonical currency.
    // rewardTextMode may still be used for text filtering.

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
}

package com.yourgame.survival.quest.zqs.generator;

/**
 * Reward profile definition loaded from `assets/data/zqs/reward_profiles_v1.json`.
 */
public final class RewardProfileDef {
  public String rewardProfileId = "";
  public String rewardFormulaType = "";
  public String rewardTextMode = "currency";

  // v1 policy: rewards are paid as wallet copper; item rewards can be enabled by data but are optional.
  public boolean allowItemRewards = false;
  public boolean allowCurrencyRewards = true;
  public boolean allowMixedRewards = false;
}


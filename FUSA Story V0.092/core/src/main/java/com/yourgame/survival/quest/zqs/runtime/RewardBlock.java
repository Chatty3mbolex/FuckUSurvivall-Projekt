package com.yourgame.survival.quest.zqs.runtime;

import java.util.ArrayList;
import java.util.List;

/** Full reward state (Codeplan v1). */
public final class RewardBlock {
  public String rewardFormulaType = "";

  public int baseRewardCopper = 0;
  public int penaltyCopper = 0;
  public int finalRewardCopper = 0;

  public RewardState rewardState = RewardState.ACCEPTED;
  public RewardTextMode rewardTextMode = RewardTextMode.CURRENCY;

  public int rewardTotalCopper = 0;
  public int rewardCurrencyCopper = 0;
  public int rewardCurrencySilver = 0;
  public int rewardCurrencyGold = 0;

  public static final class RewardItem {
    public int itemId = -1;
    public int amount = 0;
    public RewardItem() {}
    public RewardItem(int itemId, int amount) { this.itemId = itemId; this.amount = amount; }
  }

  public final List<RewardItem> rewardItems = new ArrayList<>();

  public void normalizeCurrency() {
    int total = Math.max(0, rewardTotalCopper);
    rewardCurrencyGold = total / 100000;
    total %= 100000;
    rewardCurrencySilver = total / 100;
    total %= 100;
    rewardCurrencyCopper = total;
  }

  public static RewardBlock currencyFromTotalCopper(int totalCopper) {
    RewardBlock r = new RewardBlock();
    int v = Math.max(0, totalCopper);
    r.rewardTotalCopper = v;
    r.rewardTextMode = RewardTextMode.CURRENCY;
    r.rewardState = RewardState.ACCEPTED;
    r.baseRewardCopper = v;
    r.finalRewardCopper = v;
    r.normalizeCurrency();
    return r;
  }
}

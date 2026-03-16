package com.yourgame.survival.quest.zqs.runtime;

/** Runtime offer (OfferBuffer). Not persistent until accepted. */
public final class GeneratedQuestOffer {
  public String questId;
  public String questFamily; // NQ|HQ
  public String questType; // sammeln|liefern|craften|finden|eskortieren
  public String questSubtype;

  public String blueprintId;
  public String targetType;

  public String targetId;
  public String targetName;
  public int targetQuantity;
  public int targetValueCopper;

  public int expectedTimeSec;
  public RewardBlock reward;

  // Text profile selection (placeholder)
  public String textProfileId;

  public String status; // generated|offered
  public String sourceNpcId;

  public long generatedAtRuntimeSec;
}

package com.yourgame.survival.quest.zqs.blueprint;

/**
 * Blueprint definition loaded from `assets/data/zqs/blueprints_v1.json`.
 */
public final class QuestBlueprint {
  public String blueprintId = "";
  public String objectiveFamily = "NQ"; // NQ|HQ
  public String questType = "";
  public String questSubtype = "";

  /** Allowed catalog kind ids (e.g. item, harvestable, npc, region, ...). */
  public String[] allowedTargetKinds = new String[0];

  public int amountMin = 1;
  public int amountMax = 1;

  public int minPlayerLevel = 1;
  public Integer maxPlayerLevel = null;

  public float weight = 1.0f;

  public String rewardProfileId = "";
  public String textProfileId = "";

  public final RepeatRules repeatRules = new RepeatRules();
}


package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.runtime.QuestSubtype;
import com.yourgame.survival.quest.zqs.runtime.QuestType;

/** Blueprint definition (code representation of blueprints_v1.json). */
public final class QuestBlueprintDef {
  public String blueprintId = "";
  public String objectiveFamily = "NQ"; // NQ|HQ

  public QuestType questType = QuestType.SAMMELN;
  public QuestSubtype questSubtype = QuestSubtype.SAMMELN_ITEM;

  public String[] allowedTargetKinds = new String[0];

  public int amountMin = 1;
  public int amountMax = 1;

  public int minPlayerLevel = 1;
  public Integer maxPlayerLevel = null;

  public float weight = 1.0f;

  public String rewardProfileId = "";
  public String textProfileId = "";

  public final RepeatRules repeatRules = new RepeatRules();

  public static final class RepeatRules {
    public int cooldownHours = 0;
    public boolean denySameTarget = false;
    public boolean denySameFamily = false;
  }
}


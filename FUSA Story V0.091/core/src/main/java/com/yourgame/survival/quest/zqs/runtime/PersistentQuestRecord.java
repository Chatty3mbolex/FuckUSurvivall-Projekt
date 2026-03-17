package com.yourgame.survival.quest.zqs.runtime;

/** Persistent quest record (PlayerQuestDB). */
public final class PersistentQuestRecord {
  public String questId;
  public String questFamily;
  public String questType;
  public String questSubtype;

  public String blueprintId;
  public String targetType;

  public String targetId;
  public String targetName;
  public int targetQuantity;
  public int targetValueCopper;
  public int expectedTimeSec;

  // Objective meta (used for repeat rules, placeholders, progress resolver)
  public String repeatFamilyKey;
  public String targetRegionId;
  public String targetRegionName;
  public String targetEntityId;
  public String targetEntityName;
  public String progressKey;
  public int progressBaseline;
  public int progressCurrent;
  public long readyAtRuntimeSec;
  public long deadlineAtRuntimeSec;

  public RewardBlock reward;

  public String textProfileId;
  public String acceptedText;

  public String sourceNpcId;

  public long generatedAtRuntimeSec;
  public long acceptedAtRuntimeSec;
  public long completedAtRuntimeSec;
  public long failedAtRuntimeSec;

  public int logbookEntryNr;

  public String finalStatus; // aktiv|abgabebereit|erledigt|fehlgeschlagen

  public PersistentQuestRecord() {}

  public PersistentQuestRecord(GeneratedQuestOffer o) {
    if (o == null) return;
    this.questId = o.questId;
    this.questFamily = o.questFamily;
    this.questType = o.questType;
    this.questSubtype = o.questSubtype;
    this.blueprintId = o.blueprintId;
    this.targetType = o.targetType;
    this.targetId = o.targetId;
    this.targetName = o.targetName;
    this.targetQuantity = o.targetQuantity;
    this.targetValueCopper = o.targetValueCopper;
    this.expectedTimeSec = o.expectedTimeSec;

    this.repeatFamilyKey = o.repeatFamilyKey;
    this.targetRegionId = o.targetRegionId;
    this.targetRegionName = o.targetRegionName;
    this.targetEntityId = o.targetEntityId;
    this.targetEntityName = o.targetEntityName;
    this.progressKey = o.progressKey;

    // progress fields are set on accept (baseline/current/ready/deadline)
    this.progressBaseline = 0;
    this.progressCurrent = 0;
    this.readyAtRuntimeSec = 0L;
    this.deadlineAtRuntimeSec = 0L;

    this.reward = o.reward;
    this.textProfileId = o.textProfileId;
    this.sourceNpcId = o.sourceNpcId;
    this.generatedAtRuntimeSec = o.generatedAtRuntimeSec;
  }
}

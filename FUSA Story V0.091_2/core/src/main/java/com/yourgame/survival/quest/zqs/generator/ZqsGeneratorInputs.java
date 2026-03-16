package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.blueprint.BlueprintRegistry;
import com.yourgame.survival.quest.zqs.catalog.CatalogIndexes;
import com.yourgame.survival.quest.zqs.catalog.ContentCatalogRuntime;
import com.yourgame.survival.quest.zqs.history.QuestHistoryIndex;
import com.yourgame.survival.quest.zqs.knowledge.PlayerKnowledgeState;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

/**
 * Normalized inputs for generator/eligibility checks.
 */
public final class ZqsGeneratorInputs {
  public int playerLevel = 1;
  public int playerSlId = 1;

  public long runtimeSec = 0;

  public int openQuestsCount = 0;
  public int completedQuestsCount = 0;

  public PlayerKnowledgeState knowledge;
  public ContentCatalogRuntime catalog;
  public CatalogIndexes catalogIdx;
  public QuestHistoryIndex history;

  public BlueprintRegistry blueprints;
  public RewardProfiles profiles;

  /** Canonical save block; used for timer/counters/rng state. */
  public ZqsSaveBlock save;
}


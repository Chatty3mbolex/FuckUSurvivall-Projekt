package com.yourgame.survival.quest.zqs.save;

import java.util.ArrayList;

/**
 * In-memory representation of the ZQS save block (root key: "zqs").
 *
 * Must match SAVE_ZQS.md.
 */
public final class ZqsSaveBlock {

  public int zqsVersion = 1;

  // --- knowledge ---
  public final Knowledge knowledge = new Knowledge();

  // --- playerQuestDb ---
  public final PlayerQuestDb playerQuestDb = new PlayerQuestDb();

  // --- history ---
  public final QuestHistoryIndex questHistoryIndex = new QuestHistoryIndex();

  // --- logbook ---
  public final Logbook logbook = new Logbook();

  // --- counters ---
  public final Counters counters = new Counters();

  // --- story/progress ---
  public final StoryState storyState = new StoryState();

  // --- optional ---
  public final RngState rngState = new RngState();
  public final BlueprintState blueprintState = new BlueprintState();

  // --- optional catalog snapshot ---
  public final CatalogSnapshot catalogSnapshot = new CatalogSnapshot();

  // --- nq timer state (persisted) ---
  public final NqTimerState nqTimerState = new NqTimerState();

  public void setDefaults() {
    zqsVersion = 1;
    knowledge.clear();
    playerQuestDb.clear();
    questHistoryIndex.clear();
    logbook.setDefaults();
    counters.setDefaults();
    storyState.setDefaults();
    rngState.setDefaults();
    blueprintState.clear();
    catalogSnapshot.clear();
    nqTimerState.setDefaults();
  }

  // ------------------ nested models ------------------

  public static final class Knowledge {
    public final ArrayList<Integer> knownItems = new ArrayList<>();
    public final ArrayList<String> knownRegions = new ArrayList<>();
    public final ArrayList<String> knownHarvestables = new ArrayList<>();
    public final ArrayList<String> knownLivings = new ArrayList<>();
    public final ArrayList<String> knownPois = new ArrayList<>();
    public final ArrayList<String> knownNpcs = new ArrayList<>();

    public void clear() {
      knownItems.clear();
      knownRegions.clear();
      knownHarvestables.clear();
      knownLivings.clear();
      knownPois.clear();
      knownNpcs.clear();
    }
  }

  public static final class PlayerQuestDb {
    public final ArrayList<PersistentQuestRecordSave> records = new ArrayList<>();
    public void clear() { records.clear(); }
  }

  public static final class PersistentQuestRecordSave {
    public String questId = "";
    public String questFamily = "NQ";
    public String questType = "";
    public String questSubtype = "";
    public String blueprintId = "";

    public final Target target = new Target();
    public int expectedTimeSec = 0;
    public final Reward reward = new Reward();
    public final Text text = new Text();
    public final Source source = new Source();
    public final Status status = new Status();
    public final Timestamps timestamps = new Timestamps();
    public int logbookEntryNr = 0;
    public final Locations locations = new Locations();

    // --- phase A1 additions (do not replace existing blocks) ---
    public final ObjectiveMeta objectiveMeta = new ObjectiveMeta();
    public final Progress progress = new Progress();
  }

  public static final class ObjectiveMeta {
    public String repeatFamilyKey = "";
    public String targetRegionId = "";
    public String targetRegionName = "";
    public String targetEntityId = "";
    public String targetEntityName = "";
    public String progressKey = "";
  }

  public static final class Progress {
    public int baseline = 0;
    public int current = 0;
    public long readyAt = 0L;
    public long deadlineAt = 0L;
  }

  public static final class Target {
    public String targetType = "";
    public String targetId = "";
    public String targetName = "";
    public int targetAmount = 0;
    public int targetValueCopper = 0;
  }

  public static final class Reward {
    public int rewardTotalCopper = 0;
    public String rewardTextMode = "currency";
    public int rewardCurrencyCopper = 0;
    public int rewardCurrencySilver = 0;
    public int rewardCurrencyGold = 0;
    public final ArrayList<RewardItem> rewardItems = new ArrayList<>();
  }

  public static final class RewardItem {
    public int itemId = -1;
    public int amount = 0;
  }

  public static final class Text {
    public String textProfileId = "";
    // generated_text_ids stored as snippet_id lists
    public final GeneratedTextIds generatedTextIds = new GeneratedTextIds();
    public String acceptedText = "";
  }

  public static final class GeneratedTextIds {
    public final ArrayList<String> greeting = new ArrayList<>();
    public final ArrayList<String> assignment = new ArrayList<>();
    public final ArrayList<String> reward = new ArrayList<>();
    public final ArrayList<String> farewell = new ArrayList<>();

    public void clear() {
      greeting.clear();
      assignment.clear();
      reward.clear();
      farewell.clear();
    }
  }

  public static final class Source {
    public String sourceNpcId = "";
    public String giverNpcId = "";
  }

  public static final class Status {
    public String finalStatus = "aktiv";
  }

  public static final class Timestamps {
    public long generatedAt = 0;
    public long offeredAt = 0;
    public long acceptedAt = 0;
    public long completedAt = 0;
    public long failedAt = 0;
  }

  public static final class Locations {
    public String acceptedLocation = "";
    public String completedLocation = "";
  }

  public static final class QuestHistoryIndex {
    public final ArrayList<String> seenQuestIds = new ArrayList<>();
    public final ArrayList<String> activeQuestIds = new ArrayList<>();
    public final ArrayList<String> completedQuestIds = new ArrayList<>();
    public final ArrayList<String> expiredQuestIds = new ArrayList<>();
    public final ArrayList<String> declinedQuestIds = new ArrayList<>();

    public void clear() {
      seenQuestIds.clear();
      activeQuestIds.clear();
      completedQuestIds.clear();
      expiredQuestIds.clear();
      declinedQuestIds.clear();
    }
  }

  public static final class Logbook {
    public int nextLogbookEntryNr = 1;
    public final ArrayList<LogbookEntry> entries = new ArrayList<>();

    public void setDefaults() {
      nextLogbookEntryNr = 1;
      entries.clear();
    }
  }

  public static final class LogbookEntry {
    public int logbookEntryNr = 0;
    public String questId = "";
    public String questFamily = "NQ";
    public String title = "";
    public String acceptedText = "";
    public String giverNpcId = "";
    public String acceptedLocation = "";
    public long acceptedAt = 0;
    public String finalStatus = "aktiv";
    public long completedAt = 0;
  }

  public static final class Counters {
    public int questNrCounter = 1;
    public void setDefaults() { questNrCounter = 1; }
  }

  /**
   * Persisted story/progress inputs used by generator.
   *
   * Canonical source for playerSlId (StoryLinePhase).
   */
  public static final class StoryState {
    public int playerSlId = 1;

    public void setDefaults() {
      playerSlId = 1;
    }
  }

  public static final class RngState {
    public long generatorRng = 0;
    public void setDefaults() { generatorRng = 0; }
  }

  public static final class BlueprintState {
    public final ArrayList<Cooldown> cooldowns = new ArrayList<>();
    public void clear() { cooldowns.clear(); }
  }

  public static final class Cooldown {
    public String key = "";
    public long untilRuntimeSec = 0;
  }

  public static final class CatalogSnapshot {
    public String runtimeCatalogId = "catalog_runtime_v1";
    public String hash = "";
    public void clear() { runtimeCatalogId = "catalog_runtime_v1"; hash = ""; }
  }

  public static final class NqTimerState {
    public long nextNqDueRuntimeSec = 0L;
    public long lastNqGeneratedAtRuntimeSec = 0L;
    public int lastRolledRefreshMinSec = 0;
    public int lastRolledRefreshMaxSec = 0;

    public void setDefaults() {
      nextNqDueRuntimeSec = 0L;
      lastNqGeneratedAtRuntimeSec = 0L;
      lastRolledRefreshMinSec = 0;
      lastRolledRefreshMaxSec = 0;
    }
  }
}

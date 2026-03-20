package com.yourgame.survival.quest.zqs.knowledge;

import com.yourgame.survival.quest.zqs.catalog.CatalogKind;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

import java.util.HashSet;

/**
 * Runtime wrapper over persisted save knowledge.
 *
 * Canonical storage remains in {@link ZqsSaveBlock#knowledge}.
 */
public final class PlayerKnowledgeState {

  private final HashSet<Integer> knownItems = new HashSet<>();
  private final HashSet<String> knownRegions = new HashSet<>();
  private final HashSet<String> knownHarvestables = new HashSet<>();
  private final HashSet<String> knownLivings = new HashSet<>();
  private final HashSet<String> knownPois = new HashSet<>();
  private final HashSet<String> knownNpcs = new HashSet<>();

  public void clear() {
    knownItems.clear();
    knownRegions.clear();
    knownHarvestables.clear();
    knownLivings.clear();
    knownPois.clear();
    knownNpcs.clear();
  }

  public void importFromSave(ZqsSaveBlock save) {
    clear();
    if (save == null || save.knowledge == null) return;

    knownItems.addAll(save.knowledge.knownItems);
    knownRegions.addAll(save.knowledge.knownRegions);
    knownHarvestables.addAll(save.knowledge.knownHarvestables);
    knownLivings.addAll(save.knowledge.knownLivings);
    knownPois.addAll(save.knowledge.knownPois);
    knownNpcs.addAll(save.knowledge.knownNpcs);
  }

  /**
   * Key-based check used by generator after it selected a CatalogEntry.
   * For ITEM/RESOURCE keys, the key must be parseable int.
   */
  public boolean isKnown(CatalogKind kind, String key) {
    if (kind == null) return false;
    if (key == null || key.isEmpty()) return false;

    return switch (kind) {
      case ITEM, RESOURCE -> {
        try {
          yield knownItems.contains(Integer.parseInt(key));
        } catch (Throwable t) {
          yield false;
        }
      }
      case REGION -> knownRegions.contains(key);
      case HARVESTABLE -> knownHarvestables.contains(key);
      case LIVING -> knownLivings.contains(key);
      case POI -> knownPois.contains(key);
      case NPC -> knownNpcs.contains(key);
    };
  }
}


package com.yourgame.survival.quest.zqs.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Canonical runtime view of the ZQS content catalog.
 *
 * The generator must only work on this structure (no side channels).
 */
public final class ContentCatalogRuntime {

  public final List<CatalogEntryRt> resources = new ArrayList<>();
  public final List<CatalogEntryRt> items = new ArrayList<>();
  public final List<CatalogEntryRt> harvestables = new ArrayList<>();
  public final List<CatalogEntryRt> livings = new ArrayList<>();
  public final List<CatalogEntryRt> pois = new ArrayList<>();
  public final List<CatalogEntryRt> npcs = new ArrayList<>();
  public final List<CatalogEntryRt> regions = new ArrayList<>();

  public void clear() {
    resources.clear();
    items.clear();
    harvestables.clear();
    livings.clear();
    pois.clear();
    npcs.clear();
    regions.clear();
  }

  public List<CatalogEntryRt> listByKind(CatalogKind kind) {
    if (kind == null) return Collections.emptyList();
    return switch (kind) {
      case RESOURCE -> resources;
      case ITEM -> items;
      case HARVESTABLE -> harvestables;
      case LIVING -> livings;
      case POI -> pois;
      case NPC -> npcs;
      case REGION -> regions;
    };
  }
}


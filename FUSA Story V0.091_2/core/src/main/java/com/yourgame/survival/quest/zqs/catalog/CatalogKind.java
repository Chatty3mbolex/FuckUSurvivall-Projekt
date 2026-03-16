package com.yourgame.survival.quest.zqs.catalog;

/**
 * Kind of catalog entry in the runtime catalog.
 *
 * Canonical string ids must match the DB (`assets/data/zqs/catalog_runtime_v1.json`).
 */
public enum CatalogKind {
  RESOURCE("resource"),
  ITEM("item"),
  HARVESTABLE("harvestable"),
  LIVING("living"),
  POI("poi"),
  NPC("npc"),
  REGION("region");

  public final String id;

  CatalogKind(String id) {
    this.id = id;
  }

  public static CatalogKind byId(String id) {
    if (id == null) return null;
    for (CatalogKind k : values()) {
      if (k.id.equals(id)) return k;
    }
    return null;
  }
}


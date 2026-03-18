package com.yourgame.survival.quest.zqs.catalog;

import java.util.HashMap;
import java.util.List;

/**
 * Runtime indexes for fast lookup.
 */
public final class CatalogIndexes {

  public final HashMap<String, CatalogEntryRt> byKey = new HashMap<>();

  public void clear() {
    byKey.clear();
  }

  public void index(ContentCatalogRuntime cat) {
    clear();
    if (cat == null) return;
    indexList(cat.resources);
    indexList(cat.items);
    indexList(cat.harvestables);
    indexList(cat.livings);
    indexList(cat.pois);
    indexList(cat.npcs);
    indexList(cat.regions);
  }

  private void indexList(List<CatalogEntryRt> list) {
    if (list == null) return;
    for (CatalogEntryRt e : list) {
      if (e == null || !e.isValid()) continue;
      byKey.put(e.key(), e);
    }
  }

  public CatalogEntryRt get(String key) {
    if (key == null || key.isEmpty()) return null;
    return byKey.get(key);
  }
}


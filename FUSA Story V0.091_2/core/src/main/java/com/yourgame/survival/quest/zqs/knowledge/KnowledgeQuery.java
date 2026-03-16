package com.yourgame.survival.quest.zqs.knowledge;

import com.yourgame.survival.quest.zqs.catalog.CatalogEntryRt;

public final class KnowledgeQuery {
  private KnowledgeQuery() {}

  public static boolean passesKnownGate(PlayerKnowledgeState k, CatalogEntryRt e) {
    if (e == null) return false;
    if (!e.knownRequired) return true;
    if (k == null) return false;
    return k.isKnown(e.kind, e.key());
  }
}


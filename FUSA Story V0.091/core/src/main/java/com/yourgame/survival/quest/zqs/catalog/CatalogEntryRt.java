package com.yourgame.survival.quest.zqs.catalog;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime catalog entry (already normalized from JSON).
 */
public final class CatalogEntryRt {

  public CatalogKind kind;

  /** String id (for non-items), optional if itemId is present. */
  public String id = "";

  /** Numeric id for items/resources, optional if id is present. */
  public int itemId = -1;

  public String name = "";

  /** Copper value used for reward calculation. */
  public int valueCopper = 0;

  /** Max storyline phase where this entry is valid. 0 = no limit. */
  public int slIdMax = 0;

  /** If true, player must know this entry (knowledge gate). */
  public boolean knownRequired = false;

  public final List<String> tags = new ArrayList<>();

  public String key() {
    if (id != null && !id.isEmpty()) return id;
    if (itemId >= 0) return String.valueOf(itemId);
    return "";
  }

  public boolean isValid() {
    return kind != null && key() != null && !key().isEmpty();
  }

  public boolean hasTag(String tag) {
    if (tag == null || tag.isEmpty()) return false;
    for (String t : tags) {
      if (tag.equals(t)) return true;
    }
    return false;
  }
}


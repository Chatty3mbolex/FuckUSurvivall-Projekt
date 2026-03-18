package com.yourgame.survival.worldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory registry for templates.
 *
 * Design decision:
 * - We keep a registry object instead of using static globals.
 *   Reason: later we may want different registries for editor/testing/modding.
 */
public final class AreaTemplateRegistry {
  private final Map<String, AreaTemplate> byId = new HashMap<>();

  public void register(AreaTemplate t) {
    if (t == null) return;
    if (t.id == null || t.id.isEmpty()) return;
    byId.put(t.id, t);
  }

  public AreaTemplate get(String id) {
    if (id == null) return null;
    return byId.get(id);
  }

  public ArrayList<AreaTemplate> allPickable() {
    ArrayList<AreaTemplate> out = new ArrayList<>();
    for (AreaTemplate t : byId.values()) {
      if (t != null && t.pickable) out.add(t);
    }
    return out;
  }
}

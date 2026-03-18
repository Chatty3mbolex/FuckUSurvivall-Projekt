package com.yourgame.survival.data;

import java.util.ArrayList;

public final class ChestStore {
  private final ArrayList<Inventory> chests = new ArrayList<>(64);
  private ItemDef[] defs;

  public void setItemDefs(ItemDef[] defs) {
    this.defs = defs;
    // Apply to existing chests (for loads where chests are created before defs are set).
    if (defs != null) {
      for (int i = 0; i < chests.size(); i++) {
        Inventory inv = chests.get(i);
        if (inv != null) inv.setItemDefs(defs);
      }
    }
  }

  public void clear() {
    chests.clear();
  }

  public int createChest() {
    Inventory inv = new Inventory();
    if (defs != null) inv.setItemDefs(defs);
    chests.add(inv);
    return chests.size() - 1;
  }

  public Inventory get(int idx) {
    if (idx < 0 || idx >= chests.size()) return null;
    return chests.get(idx);
  }
}

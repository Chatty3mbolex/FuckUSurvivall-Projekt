package com.yourgame.survival.systems;

import com.yourgame.survival.data.Inventory;
import com.yourgame.survival.data.RecipeDef;

import java.util.List;

public final class CraftSystem {
  public boolean canCraft(Inventory inv, RecipeDef r) {
    for (int i=0;i<r.inItemId.length;i++) {
      int id = r.inItemId[i];
      int need = r.inAmount[i];
      if (inv.getCount(id) < need) return false;
    }
    return true;
  }

  public boolean craft(Inventory inv, RecipeDef r) {
    if (!canCraft(inv, r)) return false;
    for (int i=0;i<r.inItemId.length;i++) {
      if (!inv.spend(r.inItemId[i], r.inAmount[i])) return false;
    }
    inv.add(r.outItemId, r.outAmount);
    return true;
  }

  public RecipeDef findByOutput(List<RecipeDef> recipes, int outItemId) {
    for (RecipeDef r : recipes) {
      if (r.outItemId == outItemId) return r;
    }
    return null;
  }
}

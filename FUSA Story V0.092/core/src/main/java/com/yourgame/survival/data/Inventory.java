package com.yourgame.survival.data;

import java.util.Arrays;

/**
 * Slot based inventory.
 *
 * - Normal items live in the normal grid.
 * - Tools/Weapons live in the tool grid (below inventory), max 3 rows.
 * - countsById is kept for compatibility with existing systems + save format.
 */
public final class Inventory {
  public static final int GRID_W = 5;
  public static final int GRID_MIN_H = 2;
  /** 0 = "no max" for normal grid rendering (we expand on demand). */
  public static final int GRID_MAX_H = 0;

  public static final int TOOL_ALWAYS_ROWS = 1;
  public static final int TOOL_MAX_ROWS = 3;
  public static final int TOOL_MAX_SLOTS = GRID_W * TOOL_MAX_ROWS;

  /** Backwards-compatible totals, also used by save/load. */
  public final int[] countsById = new int[60];

  private ItemDef[] defs;

  private int[] normalItemId = new int[GRID_W * GRID_MIN_H];
  private int[] normalCount  = new int[GRID_W * GRID_MIN_H];

  private final int[] toolItemId = new int[TOOL_MAX_SLOTS];
  private final int[] toolCount  = new int[TOOL_MAX_SLOTS];

  public Inventory() {
    Arrays.fill(normalItemId, -1);
    Arrays.fill(toolItemId, -1);
  }

  public void setItemDefs(ItemDef[] defs) {
    this.defs = defs;
    rebuildSlotsFromCounts();
  }

  public boolean isToolItem(int itemId) {
    if (itemId < 0 || itemId >= countsById.length) return false;
    if (defs != null && itemId < defs.length && defs[itemId] != null) {
      String t = defs[itemId].type;
      return "tool".equals(t) || "weapon".equals(t);
    }
    // fallback for old saves / before registry assignment
    return (itemId >= 14 && itemId <= 27);
  }

  public int stackMax(int itemId) {
    if (itemId < 0 || itemId >= countsById.length) return 1;
    if (defs != null && itemId < defs.length && defs[itemId] != null) {
      int m = defs[itemId].stackMax;
      if (m <= 0) m = 1;
      return m;
    }
    // fallback (legacy UI used 25)
    return isToolItem(itemId) ? 1 : 25;
  }

  public int getCount(int itemId) {
    if (itemId < 0 || itemId >= countsById.length) return 0;
    return countsById[itemId];
  }

  public boolean hasItem(int itemId) { return getCount(itemId) > 0; }

  public int normalRowsUsed() {
    int last = -1;
    for (int i = 0; i < normalItemId.length; i++) {
      if (normalItemId[i] >= 0 && normalCount[i] > 0) last = i;
    }
    if (last < 0) return GRID_MIN_H;
    int rows = (last / GRID_W) + 1;
    return Math.max(GRID_MIN_H, rows);
  }

  public int visibleToolRows() {
    // Rule: 1 tool row is always visible.
    // Row 2 becomes visible when row 1 is full (or row 2 has any content).
    // Row 3 becomes visible when row 2 is full (or row 3 has any content).
    int rows = TOOL_ALWAYS_ROWS;

    boolean row1Full = true;
    for (int i = 0; i < GRID_W; i++) {
      if (toolItemId[i] < 0 || toolCount[i] <= 0) { row1Full = false; break; }
    }

    boolean row2Has = false;
    boolean row2Full = true;
    for (int i = GRID_W; i < GRID_W * 2; i++) {
      if (toolItemId[i] >= 0 && toolCount[i] > 0) row2Has = true;
      if (toolItemId[i] < 0 || toolCount[i] <= 0) row2Full = false;
    }

    boolean row3Has = false;
    for (int i = GRID_W * 2; i < GRID_W * 3; i++) {
      if (toolItemId[i] >= 0 && toolCount[i] > 0) { row3Has = true; break; }
    }

    if (row1Full || row2Has) rows = 2;
    if ((rows >= 2 && row2Full) || row3Has) rows = 3;
    return rows;
  }

  public int normalSlotCount() { return normalItemId.length; }
  public int toolSlotCount() { return TOOL_MAX_SLOTS; }

  public int getNormalItemId(int slot) {
    if (slot < 0 || slot >= normalItemId.length) return -1;
    return normalItemId[slot];
  }
  public int getNormalCount(int slot) {
    if (slot < 0 || slot >= normalCount.length) return 0;
    return normalCount[slot];
  }

  public int getToolItemId(int slot) {
    if (slot < 0 || slot >= TOOL_MAX_SLOTS) return -1;
    return toolItemId[slot];
  }
  public int getToolCount(int slot) {
    if (slot < 0 || slot >= TOOL_MAX_SLOTS) return 0;
    return toolCount[slot];
  }

  public int[] exportNormalItemId() { return Arrays.copyOf(normalItemId, normalItemId.length); }
  public int[] exportNormalCount() { return Arrays.copyOf(normalCount, normalCount.length); }
  public int[] exportToolItemId() { return Arrays.copyOf(toolItemId, toolItemId.length); }
  public int[] exportToolCount() { return Arrays.copyOf(toolCount, toolCount.length); }

  public void importSlots(int[] normalIds, int[] normalCnt, int[] toolIds, int[] toolCnt) {
    if (normalIds == null || normalCnt == null || toolIds == null || toolCnt == null) return;
    // Normal grid: allow arbitrary length; tool grid is capped.
    normalItemId = Arrays.copyOf(normalIds, normalIds.length);
    normalCount = Arrays.copyOf(normalCnt, normalCnt.length);
    Arrays.fill(this.toolItemId, -1);
    Arrays.fill(this.toolCount, 0);
    int n = Math.min(toolIds.length, TOOL_MAX_SLOTS);
    for (int i = 0; i < n; i++) this.toolItemId[i] = toolIds[i];
    n = Math.min(toolCnt.length, TOOL_MAX_SLOTS);
    for (int i = 0; i < n; i++) this.toolCount[i] = toolCnt[i];
    recomputeCountsById();
  }

  public void swapToolSlots(int a, int b) {
    if (a < 0 || a >= TOOL_MAX_SLOTS) return;
    if (b < 0 || b >= TOOL_MAX_SLOTS) return;
    int ia = toolItemId[a]; int ca = toolCount[a];
    toolItemId[a] = toolItemId[b]; toolCount[a] = toolCount[b];
    toolItemId[b] = ia; toolCount[b] = ca;
    recomputeCountsById();
  }

  public void clear() {
    Arrays.fill(countsById, 0);
    normalItemId = new int[GRID_W * GRID_MIN_H];
    normalCount  = new int[GRID_W * GRID_MIN_H];
    Arrays.fill(normalItemId, -1);
    Arrays.fill(toolItemId, -1);
    Arrays.fill(toolCount, 0);
  }

  /** Legacy API: auto-route tools to tool grid, everything else to normal. */
  public void add(int itemId, int amount) {
    addPreferred(itemId, amount, isToolItem(itemId), -1);
  }

  // Block 16: clearer API names (no logic change)

  /** Returns how many of this item could be added right now. */
  public int canAdd(int itemId, int amount) {
    if (amount <= 0) return 0;
    int max = maxAddable(itemId);
    if (max <= 0) return 0;
    return Math.min(amount, max);
  }

  /** Adds items and returns how many were actually added. */
  public int addItem(int itemId, int amount) {
    return addPreferred(itemId, amount, isToolItem(itemId), -1);
  }

  /** Removes items; returns true if fully removed (alias for spend). */
  public boolean remove(int itemId, int amount) {
    return spend(itemId, amount);
  }

  public boolean spend(int itemId, int amount) {
    if (itemId < 0 || itemId >= countsById.length) return false;
    if (amount <= 0) return true;
    if (countsById[itemId] < amount) return false;

    int remaining = amount;
    remaining = removeFromSlots(itemId, remaining, normalItemId, normalCount);
    remaining = removeFromSlots(itemId, remaining, toolItemId, toolCount);
    recomputeCountsById();
    return remaining == 0;
  }

  private static int removeFromSlots(int itemId, int remaining, int[] ids, int[] cnt) {
    if (remaining <= 0) return 0;
    for (int i = 0; i < ids.length && remaining > 0; i++) {
      if (ids[i] != itemId) continue;
      int take = Math.min(cnt[i], remaining);
      cnt[i] -= take;
      remaining -= take;
      if (cnt[i] <= 0) { cnt[i] = 0; ids[i] = -1; }
    }
    return remaining;
  }

  /**
   * Adds into the area implied by item type.
   * preferredSlot is the slot index within that area (normal or tool). If invalid, auto-placement is used.
   */
  public int addPreferred(int itemId, int amount, boolean preferredToolArea, int preferredSlot) {
    if (itemId < 0 || itemId >= countsById.length) return 0;
    if (amount <= 0) return 0;

    boolean tool = isToolItem(itemId);
    if (tool) preferredToolArea = true;

    int max = stackMax(itemId);
    if (max <= 0) max = 1;

    int remaining = amount;
    if (preferredToolArea) {
      remaining = addPreferredInto(itemId, remaining, max, preferredSlot, toolItemId, toolCount, true);
    } else {
      remaining = addPreferredInto(itemId, remaining, max, preferredSlot, normalItemId, normalCount, false);
    }
    recomputeCountsById();
    return amount - remaining;
  }

  private int addPreferredInto(int itemId, int remaining, int stackMax, int preferredSlot, int[] ids, int[] cnt, boolean isToolArea) {
    if (remaining <= 0) return 0;

    int len = ids.length;

    // 1) Top off preferred slot if it's same item.
    if (preferredSlot >= 0 && preferredSlot < len && ids[preferredSlot] == itemId && cnt[preferredSlot] < stackMax) {
      int add = Math.min(stackMax - cnt[preferredSlot], remaining);
      cnt[preferredSlot] += add;
      remaining -= add;
    }

    // 2) Top off other stacks.
    for (int i = 0; i < len && remaining > 0; i++) {
      if (i == preferredSlot) continue;
      if (ids[i] != itemId) continue;
      if (cnt[i] >= stackMax) continue;
      int add = Math.min(stackMax - cnt[i], remaining);
      cnt[i] += add;
      remaining -= add;
    }

    // 3) Place new stacks.
    int cursor = (preferredSlot >= 0) ? preferredSlot : -1;
    while (remaining > 0) {
      int target;
      if (cursor >= 0 && cursor < len && ids[cursor] < 0) {
        target = cursor;
      } else {
        int start = (cursor >= 0) ? (cursor + 1) : 0;
        target = findNextFree(ids, start);
      }

      if (target < 0) {
        if (isToolArea) break; // capped
        ensureNormalSlots(normalItemId.length + GRID_W);
        ids = normalItemId;
        cnt = normalCount;
        len = ids.length;
        int start = (cursor >= 0) ? Math.min(cursor + 1, len - 1) : 0;
        target = findNextFree(ids, start);
        if (target < 0) target = findNextFree(ids, 0);
        if (target < 0) break;
      }

      ids[target] = itemId;
      int add = Math.min(stackMax, remaining);
      cnt[target] = add;
      remaining -= add;
      cursor = target;
    }

    return remaining;
  }

  private static int findNextFree(int[] ids, int start) {
    for (int i = start; i < ids.length; i++) if (ids[i] < 0) return i;
    for (int i = 0; i < start; i++) if (ids[i] < 0) return i;
    return -1;
  }

  private void ensureNormalSlots(int slotCount) {
    if (slotCount <= normalItemId.length) return;
    int wantRows = (int) Math.ceil(slotCount / (float) GRID_W);
    wantRows = Math.max(wantRows, GRID_MIN_H);
    int newSlots = wantRows * GRID_W;
    int[] newIds = new int[newSlots];
    int[] newCnt = new int[newSlots];
    Arrays.fill(newIds, -1);
    System.arraycopy(normalItemId, 0, newIds, 0, normalItemId.length);
    System.arraycopy(normalCount, 0, newCnt, 0, normalCount.length);
    normalItemId = newIds;
    normalCount = newCnt;
  }

  public int maxAddable(int itemId) {
    if (itemId < 0 || itemId >= countsById.length) return 0;
    if (!isToolItem(itemId)) return Integer.MAX_VALUE; // normal expands
    int free = 0;
    for (int i = 0; i < TOOL_MAX_SLOTS; i++) {
      if (toolItemId[i] < 0 || toolCount[i] <= 0) free++;
    }
    return free;
  }

  /** Rebuild slot layout from countsById totals (used after load or registry assignment). */
  public void rebuildSlotsFromCounts() {
    int[] src = Arrays.copyOf(countsById, countsById.length);

    normalItemId = new int[GRID_W * GRID_MIN_H];
    normalCount  = new int[GRID_W * GRID_MIN_H];
    Arrays.fill(normalItemId, -1);
    Arrays.fill(toolItemId, -1);
    Arrays.fill(toolCount, 0);

    for (int itemId = 0; itemId < src.length; itemId++) {
      int c = src[itemId];
      if (c <= 0) continue;
      boolean tool = isToolItem(itemId);
      int max = stackMax(itemId);
      if (max <= 0) max = 1;

      if (tool) {
        int remaining = c;
        for (int i = 0; i < TOOL_MAX_SLOTS && remaining > 0; i++) {
          if (toolItemId[i] >= 0) continue;
          toolItemId[i] = itemId;
          int put = Math.min(max, remaining);
          toolCount[i] = put;
          remaining -= put;
        }
      } else {
        int remaining = c;
        while (remaining > 0) {
          int slot = findNextFree(normalItemId, 0);
          if (slot < 0) {
            ensureNormalSlots(normalItemId.length + GRID_W);
            slot = findNextFree(normalItemId, 0);
            if (slot < 0) break;
          }
          normalItemId[slot] = itemId;
          int put = Math.min(max, remaining);
          normalCount[slot] = put;
          remaining -= put;
        }
      }
    }

    recomputeCountsById();
  }

  private void recomputeCountsById() {
    Arrays.fill(countsById, 0);
    for (int i = 0; i < normalItemId.length; i++) {
      int id = normalItemId[i];
      int c = normalCount[i];
      if (id >= 0 && id < countsById.length && c > 0) countsById[id] += c;
    }
    for (int i = 0; i < TOOL_MAX_SLOTS; i++) {
      int id = toolItemId[i];
      int c = toolCount[i];
      if (id >= 0 && id < countsById.length && c > 0) countsById[id] += c;
    }
  }

  /** Legacy helper used by old UI; now returns ONLY the normal inventory stacks (slot order). */
  public com.badlogic.gdx.utils.IntArray buildUiStacks() {
    com.badlogic.gdx.utils.IntArray out = new com.badlogic.gdx.utils.IntArray();
    for (int i = 0; i < normalItemId.length; i++) {
      int id = normalItemId[i];
      int c = normalCount[i];
      if (id < 0 || c <= 0) continue;
      out.add(id);
      out.add(c);
    }
    return out;
  }
}

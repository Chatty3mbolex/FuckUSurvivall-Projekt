package com.yourgame.survival.data;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;

/** Block 11: minimal save/load to JSON (slots 1..8). Desktop-only, offline. */
public final class SaveManager {
  private SaveManager() {}

  // Block 15: save keys + versioning
  private static final int SAVE_VERSION = 5;

  private static final String K_SAVE_VERSION = "saveVersion";
  private static final String K_SEED = "seed";
  private static final String K_NAME = "name";
  private static final String K_PLAYER = "player";
  private static final String K_X = "x";
  private static final String K_Y = "y";
  private static final String K_INV = "inv";
  private static final String K_WALLET = "wallet";
  private static final String K_COPPER = "copper";
  private static final String K_PROGRESS = "progress";
  private static final String K_LEVEL = "level";
  private static final String K_XP = "xp";
  private static final String K_XP_TO_NEXT = "xpToNext";
  private static final String K_SP = "sp";
  private static final String K_SKILL_LV = "skillLv";
  private static final String K_NEEDS = "needs";
  private static final String K_HP = "hp";
  private static final String K_STAMINA = "stamina";
  private static final String K_MANA = "mana";
  private static final String K_HUNGER = "hunger";
  private static final String K_SLEEP = "sleep";
  private static final String K_DAY_T = "dayT";
  private static final String K_DAY_INDEX = "dayIndex";
  private static final String K_HOTBAR = "hotbar";
  private static final String K_HOTBAR_SEL = "hotbarSel";
  private static final String K_INV_SLOTS = "invSlots";
  private static final String K_NORMAL_IDS = "normalIds";
  private static final String K_NORMAL_CNT = "normalCnt";
  private static final String K_TOOL_IDS = "toolIds";
  private static final String K_TOOL_CNT = "toolCnt";
  private static final String K_ENTITIES = "entities";
  private static final String K_MERCHANT_SYS = "merchantSys";
  private static final String K_MOVE_RNG = "moveRng";
  private static final String K_NEXT_UID = "nextUid";
  private static final String K_SHOPS = "shops";
  private static final String K_UID = "uid";
  private static final String K_WANDERING = "wandering";
  // private static final String K_OFFERS = "offers"; // unused
  private static final String K_OFFER_COUNT = "offerCount";
  private static final String K_ITEM_ID = "itemId";
  private static final String K_BUY = "buy";
  private static final String K_SELL = "sell";
  private static final String K_REFRESH_T = "refreshT";
  private static final String K_REFRESH_INDEX = "refreshIndex";
  private static final String K_SAVED_AT = "savedAt";
  private static final String K_FLAGS = "flags";
  private static final String K_BOAT = "boat";
  private static final String K_CLIMB = "climb";
  private static final String K_BUILDS = "builds";
  private static final String K_TYPE = "type";
  private static final String K_ROT = "rot";
  private static final String K_D0 = "d0";
  private static final String K_CHEST = "chest";
  private static final String K_REMOVED_NODES = "removedNodes";

  // WorldMap (new – v3)
  private static final String K_WORLD_MAP = "worldMap";
  private static final String K_WM_CUR = "cur";
  private static final String K_WM_AX = "ax";
  private static final String K_WM_AY = "ay";
  private static final String K_WM_TID = "templateId";
  private static final String K_WM_KNOWN = "knownAreas";
  private static final String K_WM_FRONTIER = "frontier";
  private static final String K_WM_EDGES = "edges";
  private static final String K_WM_AX1 = "ax1";
  private static final String K_WM_AY1 = "ay1";
  private static final String K_WM_AX2 = "ax2";
  private static final String K_WM_AY2 = "ay2";
  private static final String K_WM_TYPE = "type";

  // WorldMap extra (new – v4)
  private static final String K_WM_EXITS = "exits";
  private static final String K_WM_N = "n";
  private static final String K_WM_E = "e";
  private static final String K_WM_S = "s";
  private static final String K_WM_W = "w";

  private static final String K_WM_AREA_STATES = "areaStates";
  private static final String K_WM_MINI_W = "miniW";
  private static final String K_WM_MINI_H = "miniH";
  private static final String K_WM_MINI_SCALE = "miniScale";
  private static final String K_WM_MINI_B64 = "miniB64";

  // Fog of war (v5)
  private static final String K_WM_FOG_W = "fogW";
  private static final String K_WM_FOG_H = "fogH";
  private static final String K_WM_FOG_SCALE = "fogScale";
  private static final String K_WM_FOG_B64 = "fogB64";

  private static final String K_WM_LEFT_DAY_INDEX = "leftDayIndex";
  private static final String K_WM_LEFT_DAY_T = "leftDayT";

  public static String slotPath(int slot) {
    return "saves/slot" + slot + ".json";
  }

  public static boolean slotExists(int slot) {
    return Gdx.files.local(slotPath(slot)).exists();
  }

  public static void saveSlot(
      int slot,
      String name,
      long seed,
      float px, float py,
      Inventory inv,
      com.yourgame.survival.data.Wallet wallet,
      com.yourgame.survival.data.PlayerProgress progress,
      com.yourgame.survival.data.SurvivalNeeds needs,
      float dayT,
      int dayIndex,
      int[] hotbar,
      int hotbarSel,
      boolean hasBoat,
      boolean hasClimb,
      Entities entities,
      ChestStore chestStore,
      com.yourgame.survival.world.WorldNodes worldNodes,
      com.yourgame.survival.systems.MerchantSystem merchants,
      com.yourgame.survival.worldmap.WorldMapState worldMap
  ) {
    FileHandle fh = Gdx.files.local(slotPath(slot));
    fh.parent().mkdirs();

    StringBuilder sb = new StringBuilder(64 * 1024);
    sb.append('{');

    sb.append('"').append(K_SAVE_VERSION).append('"').append(':').append(SAVE_VERSION).append(',');

    sb.append('"').append(K_NAME).append('"').append(':').append('"').append(escapeJson(name)).append('"').append(',');
    sb.append('"').append(K_SAVED_AT).append('"').append(':').append(System.currentTimeMillis()).append(',');
    sb.append('"').append(K_SEED).append('"').append(':').append(seed).append(',');
    sb.append('"').append(K_PLAYER).append('"').append(':').append('{');
    sb.append('"').append(K_X).append('"').append(':').append(px).append(',');
    sb.append('"').append(K_Y).append('"').append(':').append(py);
    sb.append("},");

    // inventory counts (full array; small enough)
    sb.append('"').append(K_INV).append('"').append(':').append('[');
    for (int i=0;i<inv.countsById.length;i++) {
      if (i>0) sb.append(',');
      sb.append(inv.countsById[i]);
    }
    sb.append("],");

    // wallet (copper)
    sb.append('"').append(K_WALLET).append('"').append(':').append('{');
    sb.append('"').append(K_COPPER).append('"').append(':').append(wallet.copper);
    sb.append("},");

    // progress
    sb.append('"').append(K_PROGRESS).append('"').append(':').append('{');
    sb.append('"').append(K_LEVEL).append('"').append(':').append(progress.level).append(',');
    sb.append('"').append(K_XP).append('"').append(':').append(progress.xp).append(',');
    sb.append('"').append(K_XP_TO_NEXT).append('"').append(':').append(progress.xpToNext).append(',');
    sb.append('"').append(K_SP).append('"').append(':').append(progress.skillPoints).append(',');

    // skill levels (full array; forward compatible)
    sb.append('"').append(K_SKILL_LV).append('"').append(':').append('[');
    for (int i=0;i<progress.skillLv.length;i++) {
      if (i>0) sb.append(',');
      sb.append(progress.skillLv[i]);
    }
    sb.append(']');

    sb.append("},");

    // needs
    sb.append('"').append(K_NEEDS).append('"').append(':').append('{');
    sb.append('"').append(K_HP).append('"').append(':').append(needs.hp).append(',');
    sb.append('"').append(K_STAMINA).append('"').append(':').append(needs.stamina).append(',');
    sb.append('"').append(K_MANA).append('"').append(':').append(needs.mana).append(',');
    sb.append('"').append(K_HUNGER).append('"').append(':').append(needs.hunger).append(',');
    sb.append('"').append(K_SLEEP).append('"').append(':').append(needs.sleep);
    sb.append("},");

    sb.append('"').append(K_DAY_T).append('"').append(':').append(dayT).append(',');
    sb.append('"').append(K_DAY_INDEX).append('"').append(':').append(dayIndex).append(',');

    // hotbar
    sb.append('"').append(K_HOTBAR).append('"').append(':').append('[');
    for (int i = 0; i < 8; i++) {
      if (i > 0) sb.append(',');
      sb.append((hotbar != null && i < hotbar.length) ? hotbar[i] : -1);
    }
    sb.append(']').append(',');
    sb.append('"').append(K_HOTBAR_SEL).append('"').append(':').append(hotbarSel).append(',');

    // inventory slot layout (preserve player organization)
    sb.append('"').append(K_INV_SLOTS).append('"').append(':').append('{');
    sb.append('"').append(K_NORMAL_IDS).append('"').append(':').append('[');
    int[] nIds = inv.exportNormalItemId();
    int[] nCnt = inv.exportNormalCount();
    for (int i = 0; i < nIds.length; i++) { if (i > 0) sb.append(','); sb.append(nIds[i]); }
    sb.append(']').append(',');
    sb.append('"').append(K_NORMAL_CNT).append('"').append(':').append('[');
    for (int i = 0; i < nCnt.length; i++) { if (i > 0) sb.append(','); sb.append(nCnt[i]); }
    sb.append(']').append(',');
    sb.append('"').append(K_TOOL_IDS).append('"').append(':').append('[');
    int[] tIds = inv.exportToolItemId();
    int[] tCnt = inv.exportToolCount();
    for (int i = 0; i < tIds.length; i++) { if (i > 0) sb.append(','); sb.append(tIds[i]); }
    sb.append(']').append(',');
    sb.append('"').append(K_TOOL_CNT).append('"').append(':').append('[');
    for (int i = 0; i < tCnt.length; i++) { if (i > 0) sb.append(','); sb.append(tCnt[i]); }
    sb.append(']');
    sb.append('}').append(',');

    sb.append('"').append(K_FLAGS).append('"').append(':').append('{');
    sb.append('"').append(K_BOAT).append('"').append(':').append(hasBoat ? 1 : 0).append(',');
    sb.append('"').append(K_CLIMB).append('"').append(':').append(hasClimb ? 1 : 0);
    sb.append("},");

    // entities (FULL snapshot; player must return exactly to saved state)
    sb.append('"').append(K_ENTITIES).append('"').append(':').append('[');
    boolean firstEnt = true;
    for (int e = 0; e < Entities.MAX; e++) {
      if (!entities.alive[e]) continue;
      EntityType t = entities.type[e];

      if (!firstEnt) sb.append(',');
      firstEnt = false;

      sb.append('{');
      sb.append('"').append(K_TYPE).append('"').append(':').append('"').append(t.name()).append('"').append(',');
      sb.append('"').append(K_X).append('"').append(':').append(entities.x[e]).append(',');
      sb.append('"').append(K_Y).append('"').append(':').append(entities.y[e]).append(',');
      sb.append('"').append("vx").append('"').append(':').append(entities.vx[e]).append(',');
      sb.append('"').append("vy").append('"').append(':').append(entities.vy[e]).append(',');
      sb.append('"').append("dir").append('"').append(':').append((int) entities.dir[e]).append(',');
      sb.append('"').append("faceLock").append('"').append(':').append(entities.faceLock[e]).append(',');
      sb.append('"').append("aiT").append('"').append(':').append(entities.aiT[e]).append(',');
      sb.append('"').append("aiT2").append('"').append(':').append(entities.aiT2[e]).append(',');
      sb.append('"').append(K_ROT).append('"').append(':').append(entities.rot[e]).append(',');
      sb.append('"').append("aiF0").append('"').append(':').append(entities.aiF0[e]).append(',');
      sb.append('"').append("aiF1").append('"').append(':').append(entities.aiF1[e]).append(',');
      int d0 = entities.data0[e];
      if (merchants != null && (t == EntityType.MERCHANT_ELF || t == EntityType.MERCHANT_WANDERING)) {
        var s = merchants.get(e);
        if (s == null) s = merchants.stateFor(e, t == EntityType.MERCHANT_WANDERING);
        d0 = s.uid; // store merchant uid into d0 so load can map shop state
      }
      sb.append('"').append(K_D0).append('"').append(':').append(d0).append(',');
      sb.append('"').append("hp").append('"').append(':').append(entities.hp[e]).append(',');
      sb.append('"').append("hpMax").append('"').append(':').append(entities.hpMax[e]).append(',');
      sb.append('"').append("flags").append('"').append(':').append((int) entities.flags[e]).append(',');

      // drop payload
      sb.append('"').append("itemId").append('"').append(':').append(entities.itemId[e]).append(',');
      sb.append('"').append("itemAmount").append('"').append(':').append(entities.itemAmount[e]);

      // chest payload for builds
      if (t == EntityType.BUILD_CHEST) {
        int idx = entities.data0[e];
        Inventory c = chestStore.get(idx);
        if (c != null) {
          sb.append(',');
          sb.append('"').append(K_CHEST).append('"').append(':').append('[');
          boolean first = true;
          for (int id = 0; id < c.countsById.length; id++) {
            int cnt = c.countsById[id];
            if (cnt <= 0) continue;
            if (!first) sb.append(',');
            first = false;
            sb.append('[').append(id).append(',').append(cnt).append(']');
          }
          sb.append(']');
        }
      }

      sb.append('}');
    }
    sb.append(']').append(',');

    // merchant system state (offers + timers)
    sb.append('"').append(K_MERCHANT_SYS).append('"').append(':').append('{');
    sb.append('"').append(K_MOVE_RNG).append('"').append(':').append(merchants != null ? merchants.exportMoveRngState() : 0L).append(',');
    sb.append('"').append(K_NEXT_UID).append('"').append(':').append(merchants != null ? merchants.exportNextUid() : 1).append(',');
    sb.append('"').append(K_SHOPS).append('"').append(':').append('[');
    boolean firstShop = true;
    if (merchants != null) {
      for (int e = 0; e < Entities.MAX; e++) {
        if (!entities.alive[e]) continue;
        EntityType t = entities.type[e];
        if (t != EntityType.MERCHANT_ELF && t != EntityType.MERCHANT_WANDERING) continue;
        var s = merchants.get(e);
        if (s == null) s = merchants.stateFor(e, t == EntityType.MERCHANT_WANDERING);

        if (!firstShop) sb.append(',');
        firstShop = false;
        sb.append('{');
        sb.append('"').append(K_UID).append('"').append(':').append(s.uid).append(',');
        sb.append('"').append(K_WANDERING).append('"').append(':').append(s.wandering ? 1 : 0).append(',');
        sb.append('"').append(K_OFFER_COUNT).append('"').append(':').append(s.offerCount).append(',');
        sb.append('"').append(K_REFRESH_T).append('"').append(':').append(s.refreshT).append(',');
        sb.append('"').append(K_REFRESH_INDEX).append('"').append(':').append(s.refreshIndex).append(',');

        sb.append('"').append(K_ITEM_ID).append('"').append(':').append('[');
        for (int i = 0; i < s.itemId.length; i++) { if (i > 0) sb.append(','); sb.append(s.itemId[i]); }
        sb.append(']').append(',');
        sb.append('"').append(K_BUY).append('"').append(':').append('[');
        for (int i = 0; i < s.buy.length; i++) { if (i > 0) sb.append(','); sb.append(s.buy[i]); }
        sb.append(']').append(',');
        sb.append('"').append(K_SELL).append('"').append(':').append('[');
        for (int i = 0; i < s.sell.length; i++) { if (i > 0) sb.append(','); sb.append(s.sell[i]); }
        sb.append(']');
        sb.append('}');
      }
    }
    sb.append(']');
    sb.append('}');

    // procedural node removals (so they don't respawn)
    sb.append(',');
    sb.append('"').append(K_REMOVED_NODES).append('"').append(':').append('[');
    boolean firstRemoved = true;
    for (int i=0;i<worldNodes.removed.keys().size;i++) {
      long k = worldNodes.removed.keys().get(i);
      if (!firstRemoved) sb.append(',');
      firstRemoved = false;
      sb.append(k);
    }
    sb.append(']');

    // WorldMap (v3)
    if (worldMap != null) {
      sb.append(',');
      sb.append('"').append(K_WORLD_MAP).append('"').append(':').append('{');

      // current
      sb.append('"').append(K_WM_CUR).append('"').append(':').append('{');
      sb.append('"').append(K_WM_AX).append('"').append(':').append(worldMap.curAx).append(',');
      sb.append('"').append(K_WM_AY).append('"').append(':').append(worldMap.curAy).append(',');
      sb.append('"').append(K_WM_TID).append('"').append(':').append('"').append(escapeJson(worldMap.curTemplateId)).append('"');
      sb.append('}').append(',');

      // known areas
      sb.append('"').append(K_WM_KNOWN).append('"').append(':').append('[');
      boolean firstKnown = true;
      for (java.util.Map.Entry<com.yourgame.survival.worldmap.AreaCoord, String> e : worldMap.knownAreas.entrySet()) {
        if (!firstKnown) sb.append(',');
        firstKnown = false;
        sb.append('{');
        sb.append('"').append(K_WM_AX).append('"').append(':').append(e.getKey().ax).append(',');
        sb.append('"').append(K_WM_AY).append('"').append(':').append(e.getKey().ay).append(',');
        sb.append('"').append(K_WM_TID).append('"').append(':').append('"').append(escapeJson(e.getValue())).append('"');
        sb.append('}');
      }
      sb.append(']').append(',');

      // frontier
      sb.append('"').append(K_WM_FRONTIER).append('"').append(':').append('[');
      boolean firstF = true;
      for (com.yourgame.survival.worldmap.AreaCoord c : worldMap.frontier) {
        if (!firstF) sb.append(',');
        firstF = false;
        sb.append('{');
        sb.append('"').append(K_WM_AX).append('"').append(':').append(c.ax).append(',');
        sb.append('"').append(K_WM_AY).append('"').append(':').append(c.ay);
        sb.append('}');
      }
      sb.append(']').append(',');

      // edges
      sb.append('"').append(K_WM_EDGES).append('"').append(':').append('[');
      boolean firstE = true;
      for (int i = 0; i < worldMap.edges.size(); i++) {
        com.yourgame.survival.worldmap.WorldMapState.Edge ed = worldMap.edges.get(i);
        if (ed == null) continue;
        if (!firstE) sb.append(',');
        firstE = false;
        sb.append('{');
        sb.append('"').append(K_WM_AX1).append('"').append(':').append(ed.ax1).append(',');
        sb.append('"').append(K_WM_AY1).append('"').append(':').append(ed.ay1).append(',');
        sb.append('"').append(K_WM_AX2).append('"').append(':').append(ed.ax2).append(',');
        sb.append('"').append(K_WM_AY2).append('"').append(':').append(ed.ay2).append(',');
        sb.append('"').append(K_WM_TYPE).append('"').append(':').append('"').append(escapeJson(ed.type)).append('"');
        sb.append('}');
      }
      sb.append(']').append(',');

      // exits by area (v4)
      sb.append('"').append(K_WM_EXITS).append('"').append(':').append('[');
      boolean firstX = true;
      for (java.util.Map.Entry<com.yourgame.survival.worldmap.AreaCoord, com.yourgame.survival.worldmap.WorldMapState.AreaExits> e : worldMap.exitsByArea.entrySet()) {
        if (!firstX) sb.append(',');
        firstX = false;
        com.yourgame.survival.worldmap.WorldMapState.AreaExits ex = e.getValue();
        sb.append('{');
        sb.append('"').append(K_WM_AX).append('"').append(':').append(e.getKey().ax).append(',');
        sb.append('"').append(K_WM_AY).append('"').append(':').append(e.getKey().ay).append(',');
        sb.append('"').append(K_WM_N).append('"').append(':').append((ex != null && ex.n) ? 1 : 0).append(',');
        sb.append('"').append(K_WM_E).append('"').append(':').append((ex != null && ex.e) ? 1 : 0).append(',');
        sb.append('"').append(K_WM_S).append('"').append(':').append((ex != null && ex.s) ? 1 : 0).append(',');
        sb.append('"').append(K_WM_W).append('"').append(':').append((ex != null && ex.w) ? 1 : 0);
        sb.append('}');
      }
      sb.append(']').append(',');

      // area states ("war stand" snapshots) (v4)
      sb.append('"').append(K_WM_AREA_STATES).append('"').append(':').append('[');
      boolean firstAS = true;
      for (java.util.Map.Entry<com.yourgame.survival.worldmap.AreaCoord, com.yourgame.survival.worldmap.WorldMapState.AreaState> e : worldMap.areaStates.entrySet()) {
        if (!firstAS) sb.append(',');
        firstAS = false;
        com.yourgame.survival.worldmap.WorldMapState.AreaState st = e.getValue();
        if (st == null) st = new com.yourgame.survival.worldmap.WorldMapState.AreaState();
        sb.append('{');
        sb.append('"').append(K_WM_AX).append('"').append(':').append(e.getKey().ax).append(',');
        sb.append('"').append(K_WM_AY).append('"').append(':').append(e.getKey().ay).append(',');
        sb.append('"').append(K_WM_TID).append('"').append(':').append('"').append(escapeJson(st.templateId)).append('"').append(',');
        sb.append('"').append(K_WM_MINI_W).append('"').append(':').append(st.miniW).append(',');
        sb.append('"').append(K_WM_MINI_H).append('"').append(':').append(st.miniH).append(',');
        sb.append('"').append(K_WM_MINI_SCALE).append('"').append(':').append(st.miniScale).append(',');
        sb.append('"').append(K_WM_MINI_B64).append('"').append(':').append('"').append(escapeJson(st.miniMapB64)).append('"').append(',');

        // fog of war (v5)
        sb.append('"').append(K_WM_FOG_W).append('"').append(':').append(st.fogW).append(',');
        sb.append('"').append(K_WM_FOG_H).append('"').append(':').append(st.fogH).append(',');
        sb.append('"').append(K_WM_FOG_SCALE).append('"').append(':').append(st.fogScale).append(',');
        sb.append('"').append(K_WM_FOG_B64).append('"').append(':').append('"').append(escapeJson(st.fogBitsB64)).append('"').append(',');

        sb.append('"').append(K_WM_LEFT_DAY_INDEX).append('"').append(':').append(st.leftDayIndex).append(',');
        sb.append('"').append(K_WM_LEFT_DAY_T).append('"').append(':').append(st.leftDayT);
        sb.append('}');
      }
      sb.append(']');

      sb.append('}'); // worldMap
    }

    sb.append('}');

    fh.writeString(sb.toString(), false, "UTF-8");
  }

  public static boolean loadSlot(
      int slot,
      long[] outSeed,
      float[] outPxPy,
      Inventory inv,
      com.yourgame.survival.data.Wallet wallet,
      com.yourgame.survival.data.PlayerProgress progress,
      com.yourgame.survival.data.SurvivalNeeds needs,
      float[] outDayT,
      int[] outDayIndex,
      int[] outHotbar,
      int[] outHotbarSel,
      boolean[] outHasBoat,
      boolean[] outHasClimb,
      Entities entities,
      ChestStore chestStore,
      com.yourgame.survival.world.WorldNodes worldNodes,
      com.yourgame.survival.systems.MerchantSystem merchants,
      com.yourgame.survival.worldmap.WorldMapState outWorldMap
  ) {
    FileHandle fh = Gdx.files.local(slotPath(slot));
    if (!fh.exists()) return false;

    String txt = fh.readString("UTF-8");
    com.badlogic.gdx.utils.JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(txt);

    // Version (tolerant)
    // int saveVersion = root.getInt(K_SAVE_VERSION, 0);
    // name is optional

    outSeed[0] = root.getLong(K_SEED, 1337L);

    com.badlogic.gdx.utils.JsonValue p = root.get(K_PLAYER);
    if (p != null) {
      outPxPy[0] = p.getFloat(K_X, 0f);
      outPxPy[1] = p.getFloat(K_Y, 0f);
    }

    com.badlogic.gdx.utils.JsonValue invArr = root.get(K_INV);
    Arrays.fill(inv.countsById, 0);
    if (invArr != null) {
      int n = Math.min(invArr.size, inv.countsById.length);
      for (int i=0;i<n;i++) inv.countsById[i] = invArr.getInt(i);
    }

    com.badlogic.gdx.utils.JsonValue w = root.get(K_WALLET);
    if (w != null) {
      wallet.copper = w.getLong(K_COPPER, 0L);
    } else {
      wallet.copper = 0L;
    }

    com.badlogic.gdx.utils.JsonValue pr = root.get(K_PROGRESS);
    if (pr != null) {
      progress.level = pr.getInt(K_LEVEL, 1);
      progress.xp = pr.getInt(K_XP, 0);
      progress.xpToNext = pr.getInt(K_XP_TO_NEXT, 20);
      progress.skillPoints = pr.getInt(K_SP, 0);

      // skills (optional for backward compatibility)
      // IMPORTANT: when new skills are added (array grows), reset defaults first so missing tail entries don't keep stale runtime values.
      Arrays.fill(progress.skillLv, 1);
      com.badlogic.gdx.utils.JsonValue sl = pr.get(K_SKILL_LV);
      if (sl != null) {
        int n = Math.min(sl.size, progress.skillLv.length);
        for (int i = 0; i < n; i++) progress.skillLv[i] = sl.getInt(i);
      }
    }

    com.badlogic.gdx.utils.JsonValue nd = root.get(K_NEEDS);
    if (nd != null) {
      needs.hp = nd.getFloat(K_HP, 100f);
      needs.stamina = nd.getFloat(K_STAMINA, 100f);
      needs.mana = nd.getFloat(K_MANA, 100f);
      needs.hunger = nd.getFloat(K_HUNGER, 100f);
      needs.sleep = nd.getFloat(K_SLEEP, 100f);
    }

    outDayT[0] = root.getFloat(K_DAY_T, 0f);
    if (outDayIndex != null && outDayIndex.length > 0) outDayIndex[0] = root.getInt(K_DAY_INDEX, 0);

    // hotbar (optional)
    if (outHotbar != null) {
      com.badlogic.gdx.utils.JsonValue hb = root.get(K_HOTBAR);
      if (hb != null) {
        int n = Math.min(hb.size, outHotbar.length);
        for (int i = 0; i < n; i++) outHotbar[i] = hb.getInt(i);
        for (int i = n; i < outHotbar.length; i++) outHotbar[i] = -1;
      } else {
        Arrays.fill(outHotbar, -1);
      }
    }
    if (outHotbarSel != null && outHotbarSel.length > 0) outHotbarSel[0] = root.getInt(K_HOTBAR_SEL, 0);

    // inventory slot layout (optional)
    com.badlogic.gdx.utils.JsonValue invSlots = root.get(K_INV_SLOTS);
    if (invSlots != null) {
      com.badlogic.gdx.utils.JsonValue nIds = invSlots.get(K_NORMAL_IDS);
      com.badlogic.gdx.utils.JsonValue nCnt = invSlots.get(K_NORMAL_CNT);
      com.badlogic.gdx.utils.JsonValue tIds = invSlots.get(K_TOOL_IDS);
      com.badlogic.gdx.utils.JsonValue tCnt = invSlots.get(K_TOOL_CNT);
      if (nIds != null && nCnt != null && tIds != null && tCnt != null) {
        int[] nnIds = new int[nIds.size];
        int[] nnCnt = new int[nCnt.size];
        int[] ttIds = new int[tIds.size];
        int[] ttCnt = new int[tCnt.size];
        for (int i = 0; i < nnIds.length; i++) nnIds[i] = nIds.getInt(i);
        for (int i = 0; i < nnCnt.length; i++) nnCnt[i] = nCnt.getInt(i);
        for (int i = 0; i < ttIds.length; i++) ttIds[i] = tIds.getInt(i);
        for (int i = 0; i < ttCnt.length; i++) ttCnt[i] = tCnt.getInt(i);
        inv.importSlots(nnIds, nnCnt, ttIds, ttCnt);
      }
    }

    com.badlogic.gdx.utils.JsonValue fl = root.get(K_FLAGS);
    outHasBoat[0] = (fl != null) && fl.getInt(K_BOAT, 0) != 0;
    outHasClimb[0] = (fl != null) && fl.getInt(K_CLIMB, 0) != 0;

    // rebuild world entities: wipe and re-add full entity snapshot
    for (int i=0;i<Entities.MAX;i++) entities.alive[i] = false;
    chestStore.clear();

    // procedural node removals
    worldNodes.removed.clear();
    com.badlogic.gdx.utils.JsonValue rn = root.get(K_REMOVED_NODES);
    if (rn != null) {
      for (int i = 0; i < rn.size; i++) {
        worldNodes.removed.add(rn.getLong(i));
      }
    }

    // v2+: full entity snapshot
    com.badlogic.gdx.utils.JsonValue ents = root.get(K_ENTITIES);
    if (ents != null) {
      for (com.badlogic.gdx.utils.JsonValue b = ents.child; b != null; b = b.next) {
        String tn = b.getString(K_TYPE, "");
        EntityType t = EntityType.valueOf(tn);
        float x = b.getFloat(K_X, 0f);
        float y = b.getFloat(K_Y, 0f);
        float vx = b.getFloat("vx", 0f);
        float vy = b.getFloat("vy", 0f);
        byte dir = (byte) b.getInt("dir", 2);
        float faceLock = b.getFloat("faceLock", 0f);
        float aiT = b.getFloat("aiT", 0f);
        float aiT2 = b.getFloat("aiT2", 0f);
        float rot = b.getFloat(K_ROT, 0f);
        float aiF0 = b.getFloat("aiF0", 0f);
        float aiF1 = b.getFloat("aiF1", 0f);
        int d0 = b.getInt(K_D0, -1);
        float hp = b.getFloat("hp", 0f);
        float hpMax = b.getFloat("hpMax", 0f);
        byte flags = (byte) b.getInt("flags", 0);
        int itemId = b.getInt("itemId", -1);
        int itemAmount = b.getInt("itemAmount", 0);

        if (t == EntityType.BUILD_CHEST) {
          // re-allocate chest index
          d0 = chestStore.createChest();
        }

        int e = entities.spawn(t, x, y);
        if (e >= 0) {
          entities.vx[e] = vx;
          entities.vy[e] = vy;
          entities.dir[e] = dir;
          entities.faceLock[e] = faceLock;
          entities.aiT[e] = aiT;
          entities.aiT2[e] = aiT2;
          entities.rot[e] = rot;
          entities.aiF0[e] = aiF0;
          entities.aiF1[e] = aiF1;
          entities.data0[e] = d0;
          entities.hp[e] = hp;
          entities.hpMax[e] = hpMax;
          entities.flags[e] = flags;
          entities.itemId[e] = itemId;
          entities.itemAmount[e] = itemAmount;

          if (t == EntityType.BUILD_CHEST) {
            Inventory c = chestStore.get(d0);
            com.badlogic.gdx.utils.JsonValue chest = b.get(K_CHEST);
            if (c != null && chest != null) {
              for (com.badlogic.gdx.utils.JsonValue pair = chest.child; pair != null; pair = pair.next) {
                int id = pair.getInt(0);
                int cnt = pair.getInt(1);
                c.add(id, cnt);
              }
            }
          }
        }
      }
    } else {
      // v1 fallback: builds-only
      com.badlogic.gdx.utils.JsonValue builds = root.get(K_BUILDS);
      if (builds != null) {
        for (com.badlogic.gdx.utils.JsonValue b = builds.child; b != null; b = b.next) {
          String tn = b.getString(K_TYPE, "");
          EntityType t = EntityType.valueOf(tn);
          float x = b.getFloat(K_X, 0f);
          float y = b.getFloat(K_Y, 0f);
          float rot = b.getFloat(K_ROT, 0f);
          int d0 = b.getInt(K_D0, -1);

          if (t == EntityType.BUILD_CHEST) {
            d0 = chestStore.createChest();
          }

          int e = entities.spawn(t, x, y);
          if (e >= 0) {
            entities.rot[e] = rot;
            entities.data0[e] = d0;

            if (t == EntityType.BUILD_CHEST) {
              Inventory c = chestStore.get(d0);
              com.badlogic.gdx.utils.JsonValue chest = b.get(K_CHEST);
              if (c != null && chest != null) {
                for (com.badlogic.gdx.utils.JsonValue pair = chest.child; pair != null; pair = pair.next) {
                  int id = pair.getInt(0);
                  int cnt = pair.getInt(1);
                  c.add(id, cnt);
                }
              }
            }
          }
        }
      }
    }

    // WorldMap (v3). Optional for backward compatibility.
    if (outWorldMap != null) {
      try {
        outWorldMap.knownAreas.clear();
        outWorldMap.frontier.clear();
        outWorldMap.edges.clear();
        outWorldMap.exitsByArea.clear();
        outWorldMap.areaStates.clear();
        outWorldMap.curAx = 0;
        outWorldMap.curAy = 0;
        outWorldMap.curTemplateId = "";

        com.badlogic.gdx.utils.JsonValue wm = root.get(K_WORLD_MAP);
        if (wm != null) {
          com.badlogic.gdx.utils.JsonValue cur = wm.get(K_WM_CUR);
          if (cur != null) {
            outWorldMap.curAx = cur.getInt(K_WM_AX, 0);
            outWorldMap.curAy = cur.getInt(K_WM_AY, 0);
            outWorldMap.curTemplateId = cur.getString(K_WM_TID, "");
          }

          com.badlogic.gdx.utils.JsonValue known = wm.get(K_WM_KNOWN);
          if (known != null) {
            for (com.badlogic.gdx.utils.JsonValue a = known.child; a != null; a = a.next) {
              int ax = a.getInt(K_WM_AX, 0);
              int ay = a.getInt(K_WM_AY, 0);
              String tid = a.getString(K_WM_TID, "");
              outWorldMap.knownAreas.put(new com.yourgame.survival.worldmap.AreaCoord(ax, ay), tid);
            }
          }

          com.badlogic.gdx.utils.JsonValue fr = wm.get(K_WM_FRONTIER);
          if (fr != null) {
            for (com.badlogic.gdx.utils.JsonValue c = fr.child; c != null; c = c.next) {
              int ax = c.getInt(K_WM_AX, 0);
              int ay = c.getInt(K_WM_AY, 0);
              outWorldMap.frontier.add(new com.yourgame.survival.worldmap.AreaCoord(ax, ay));
            }
          }

          com.badlogic.gdx.utils.JsonValue edges = wm.get(K_WM_EDGES);
          if (edges != null) {
            for (com.badlogic.gdx.utils.JsonValue e = edges.child; e != null; e = e.next) {
              com.yourgame.survival.worldmap.WorldMapState.Edge ed = new com.yourgame.survival.worldmap.WorldMapState.Edge();
              ed.ax1 = e.getInt(K_WM_AX1, 0);
              ed.ay1 = e.getInt(K_WM_AY1, 0);
              ed.ax2 = e.getInt(K_WM_AX2, 0);
              ed.ay2 = e.getInt(K_WM_AY2, 0);
              ed.type = e.getString(K_WM_TYPE, "");
              outWorldMap.edges.add(ed);
            }
          }

          // exits by area (v4)
          com.badlogic.gdx.utils.JsonValue ex = wm.get(K_WM_EXITS);
          if (ex != null) {
            for (com.badlogic.gdx.utils.JsonValue a = ex.child; a != null; a = a.next) {
              int ax = a.getInt(K_WM_AX, 0);
              int ay = a.getInt(K_WM_AY, 0);
              boolean n = a.getInt(K_WM_N, 0) != 0;
              boolean ee = a.getInt(K_WM_E, 0) != 0;
              boolean s = a.getInt(K_WM_S, 0) != 0;
              boolean ww = a.getInt(K_WM_W, 0) != 0;
              outWorldMap.exitsByArea.put(
                  new com.yourgame.survival.worldmap.AreaCoord(ax, ay),
                  new com.yourgame.survival.worldmap.WorldMapState.AreaExits(n, ee, s, ww));
            }
          }

          // area states (v4)
          com.badlogic.gdx.utils.JsonValue st = wm.get(K_WM_AREA_STATES);
          if (st != null) {
            for (com.badlogic.gdx.utils.JsonValue a = st.child; a != null; a = a.next) {
              int ax = a.getInt(K_WM_AX, 0);
              int ay = a.getInt(K_WM_AY, 0);
              com.yourgame.survival.worldmap.AreaCoord c = new com.yourgame.survival.worldmap.AreaCoord(ax, ay);
              com.yourgame.survival.worldmap.WorldMapState.AreaState s0 = new com.yourgame.survival.worldmap.WorldMapState.AreaState();
              s0.ax = ax;
              s0.ay = ay;
              s0.templateId = a.getString(K_WM_TID, "");
              s0.miniW = a.getInt(K_WM_MINI_W, 0);
              s0.miniH = a.getInt(K_WM_MINI_H, 0);
              s0.miniScale = a.getInt(K_WM_MINI_SCALE, 0);
              s0.miniMapB64 = a.getString(K_WM_MINI_B64, "");

              // fog of war (v5) (optional)
              s0.fogW = a.getInt(K_WM_FOG_W, 0);
              s0.fogH = a.getInt(K_WM_FOG_H, 0);
              s0.fogScale = a.getInt(K_WM_FOG_SCALE, 0);
              s0.fogBitsB64 = a.getString(K_WM_FOG_B64, "");

              s0.leftDayIndex = a.getInt(K_WM_LEFT_DAY_INDEX, 0);
              s0.leftDayT = a.getFloat(K_WM_LEFT_DAY_T, 0f);
              outWorldMap.areaStates.put(c, s0);
            }
          }
        }
      } catch (Throwable ignored) {
        // tolerate older/corrupt saves
      }
    }

    // merchant system state is imported by caller AFTER world reset/merchant system recreation.

    return true;
  }

  public static com.badlogic.gdx.utils.JsonValue readMerchantSys(int slot) {
    FileHandle fh = Gdx.files.local(slotPath(slot));
    if (!fh.exists()) return null;
    try {
      String txt = fh.readString("UTF-8");
      com.badlogic.gdx.utils.JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(txt);
      return root.get(K_MERCHANT_SYS);
    } catch (Throwable t) {
      return null;
    }
  }

  public static String slotName(int slot) {
    FileHandle fh = Gdx.files.local(slotPath(slot));
    if (!fh.exists()) return "";
    try {
      String txt = fh.readString("UTF-8");
      com.badlogic.gdx.utils.JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(txt);
      return root.getString(K_NAME, "");
    } catch (Throwable t) {
      return "";
    }
  }

  private static String escapeJson(String s) {
    if (s == null) return "";
    // minimal escape for quotes + backslash + newlines
    String out = s;
    out = out.replace("\\", "\\\\");
    out = out.replace("\"", "\\\"");
    out = out.replace("\r", " ");
    out = out.replace("\n", " ");
    return out;
  }

  // private static boolean isBuild(EntityType t) {
    //return t == EntityType.BUILD_CHEST
      //  || t == EntityType.BUILD_WORKBENCH
        //|| t == EntityType.BUILD_BED
        //|| t == EntityType.BUILD_CAMPFIRE
        //|| t == EntityType.BUILD_LAMP;
 // }
}

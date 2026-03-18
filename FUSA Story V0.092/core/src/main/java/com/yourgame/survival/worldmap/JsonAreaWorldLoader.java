package com.yourgame.survival.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.screens.GameScreen;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.World;

import java.util.Random;

/**
 * Asset-backed loader for {@code assets/areas/*.area.json}.
 *
 * Scope (story scaffolding):
 * - Load authored ground layer (TileLayers.groundId).
 * - Optionally apply an authored water mask.
 * - Clear collision/roads/overlays.
 * - Spawn a bit of area-local life for HOME/FOREST so it feels like a real place.
 */
public final class JsonAreaWorldLoader implements AreaWorldLoader {

  // Keep in sync with GameScreen tile-tree policy.
  private static final boolean TILE_TREES_FELL_ON_HARVEST = true;

  /** Folder under assets/ (internal). */
  private static final String AREA_DIR = "areas";

  @Override
  public boolean loadInto(GameScreen gs, String templateId, Dir4 enteredFrom) {
    if (gs == null) return false;
    if (templateId == null || templateId.isEmpty()) return false;

    // 1) Read JSON asset
    JsonValue root = readAreaJson(templateId);
    if (root == null) return false;

    // 2) Reset world for the area.
    long areaSeed = areaSeed(gs, templateId);
    gs.areaResetWorld(areaSeed);

    // 3) Clear non-persistent entities (nodes + encounters) so the area is clean.
    gs.areaClearAreaLocalEntities();

    // 4) Apply layers into chunks.
    int w = root.get("size").getInt("w", com.yourgame.survival.tuning.TuningAreas.AREA_W_TILES);
    int h = root.get("size").getInt("h", com.yourgame.survival.tuning.TuningAreas.AREA_H_TILES);

    JsonValue ground = root.get("layers") != null ? root.get("layers").get("ground") : null;
    int defaultId = (ground != null) ? ground.getInt("defaultId", 0) : 0;
    applyGround(gs.areaWorld(), w, h, defaultId, ground);

    // Optional: water mask layer for authored areas.
    JsonValue water = root.get("layers") != null ? root.get("layers").get("water") : null;
    if (water != null) {
      applyWater(gs.areaWorld(), w, h, water);
    }

    // Optional: authored road mask layer.
    JsonValue road = root.get("layers") != null ? root.get("layers").get("road") : null;
    if (road != null) {
      applyRoad(gs.areaWorld(), w, h, road);
    }

    // 4b) Apply pre-baked corner masks from JSON (V2 schema), OR compute them from ground grid.
    JsonValue cornerMasks = root.get("layers") != null ? root.get("layers").get("cornerMasks") : null;
    if (cornerMasks != null) {
      applyCornerMasksFromJson(gs.areaWorld(), w, h, cornerMasks);
    } else {
      // Fallback: compute corner masks from the ground grid (V1 schema or HOME).
      bakeCornerMasksFromGround(gs.areaWorld(), w, h);
    }

    // 4c) Bake road adjacency (roadMask4) + fix road heightLevel.
    bakeRoadAdjacencyAndFixHeight(gs.areaWorld(), w, h);

    // 5) HOME shaping is authored only.
    // No legacy/procedural terrain shaping here: runtime must reflect exactly what is in the .area.json.

    // 6) Apply authored markers: playerSpawn, nodes, enemy zones, POIs.
    JsonValue markers = root.get("markers");
    if (markers != null) {
      // Authored objects (new schema): markers.objects[]
      // These spawn entity sprites (landmarks, props, etc.) and can carry layerBias.
      applyAuthoredObjects(gs.areaEntities(), templateId, markers);

      // Enemy zones
      gs.areaClearEnemyZones();
      JsonValue ez = markers.get("enemyZones");
      if (ez != null) {
        for (JsonValue z = ez.child; z != null; z = z.next) {
          int cx = z.getInt("cx", -1);
          int cy = z.getInt("cy", -1);
          int r = z.getInt("r", 0);
          int min = z.getInt("min", 0);
          int max = z.getInt("max", 0);
          int dmin = z.getInt("respawnDaysMin", 1);
          int dmax = z.getInt("respawnDaysMax", 2);
          if (cx < 0 || cy < 0 || r <= 0) continue;
          gs.areaAddEnemyZone(cx, cy, r, min, max, dmin, dmax);
        }
      }

      // Authored nodes (non-tree). Trees are streamed from a dense tile mask.
      JsonValue ns = markers.get("nodes");
      if (ns != null) {
        Entities es = gs.areaEntities();
        var wm = gs.areaWorldMapState();
        for (JsonValue n = ns.child; n != null; n = n.next) {
          String tn = n.getString("t", "");
          int tx = n.getInt("x", -1);
          int ty = n.getInt("y", -1);
          if (tx < 0 || ty < 0) continue;
          EntityType t;
          try { t = EntityType.valueOf(tn); } catch (Throwable ignored) { continue; }
          if (t == EntityType.NODE_TREE) continue;

          String key = templateId + "|" + t.name() + "|" + tx + "|" + ty;
          if (wm != null && wm.removedAuthoredNodes.contains(key)) continue;
          float wx = (tx + 0.5f) * World.TILE_WORLD;
          float wy = (ty + 0.5f) * World.TILE_WORLD;
          es.spawn(t, wx, wy);
        }
      }

      // Dense tile-trees (60% coverage) for FOREST_01.
      if ("FOREST_01".equalsIgnoreCase(templateId)) {
        // Prefer authored/persisted tileTrees bitmasks if present in JSON.
        byte[] present = null;
        byte[] cut = null;
        try {
          JsonValue tt = root.get("tileTrees");
          if (tt != null && tt.getInt("w", -1) == w && tt.getInt("h", -1) == h) {
            String pb64 = tt.getString("presentB64", "");
            String cb64 = tt.getString("cutB64", "");
            if (pb64 != null && !pb64.isEmpty()) present = java.util.Base64.getDecoder().decode(pb64);
            if (cb64 != null && !cb64.isEmpty()) cut = java.util.Base64.getDecoder().decode(cb64);
          }
        } catch (Throwable ignored) {}
        if (present == null) present = buildForest01TreePresence(gs.areaWorld(), w, h, areaSeed);
        gs.areaSetTreePresentBits(w, h, present);

        // DEBUG: report presence bit count once.
        try {
          int set = 0;
          if (present != null) {
            for (int i = 0; i < present.length; i++) set += Integer.bitCount(present[i] & 0xFF);
          }
          gs.areaDebugToastOnce("TileTrees init: presentBits=" + set + "/" + (w * h) + " templateId=" + templateId);
        } catch (Throwable ignored) {}

        // Load (or init) cut mask from per-area state unless authored.
        if (cut == null) cut = loadOrInitTreeCutBits(gs, w, h);
        gs.areaSetTreeCutBits(w, h, cut);

        try {
          int set2 = 0;
          if (cut != null) {
            for (int i = 0; i < cut.length; i++) set2 += Integer.bitCount(cut[i] & 0xFF);
          }
          gs.areaDebugToastOnce("TileTrees init: cutBits=" + set2);
        } catch (Throwable ignored) {}
      }

      // HOME tile-trees (sparser, excludes dirt paths/rocks/water). These used to come from beautifyHome().
      if (WorldMapRuntime.T_HOME.equals(templateId)) {
        byte[] present = null;
        byte[] cut = null;
        try {
          JsonValue tt = root.get("tileTrees");
          if (tt != null && tt.getInt("w", -1) == w && tt.getInt("h", -1) == h) {
            String pb64 = tt.getString("presentB64", "");
            String cb64 = tt.getString("cutB64", "");
            if (pb64 != null && !pb64.isEmpty()) present = java.util.Base64.getDecoder().decode(pb64);
            if (cb64 != null && !cb64.isEmpty()) cut = java.util.Base64.getDecoder().decode(cb64);
          }
        } catch (Throwable ignored) {}
        if (present == null) present = buildHomeTreePresence(gs.areaWorld(), w, h, areaSeed);
        gs.areaSetTreePresentBits(w, h, present);

        if (cut == null) cut = loadOrInitTreeCutBits(gs, w, h);
        gs.areaSetTreeCutBits(w, h, cut);
      }

      // POIs
      JsonValue pois = markers.get("poi");
      if (pois != null) {
        Entities es = gs.areaEntities();
        var wm = gs.areaWorldMapState();
        for (JsonValue p = pois.child; p != null; p = p.next) {
          String kind = p.getString("kind", "");
          int tx = p.getInt("x", -1);
          int ty = p.getInt("y", -1);
          if (tx < 0 || ty < 0) continue;

          String poiKey = templateId + "|" + kind + "|" + tx + "|" + ty;
          if (wm != null && wm.consumedPois.contains(poiKey)) continue;

          if ("HIDDEN_CHEST".equalsIgnoreCase(kind)) {
            float wx = (tx + 0.5f) * World.TILE_WORLD;
            float wy = (ty + 0.5f) * World.TILE_WORLD;

            // If a persistent POI chest already exists (from save snapshot), do not duplicate.
            boolean exists = false;
            for (int i = 0; i < Entities.MAX; i++) {
              if (!es.alive[i]) continue;
              if (es.type[i] != EntityType.POI_CHEST_HIDDEN) continue;
              float dx = es.x[i] - wx;
              float dy = es.y[i] - wy;
              if (dx * dx + dy * dy <= (24f * 24f)) { exists = true; break; }
            }

            if (!exists) {
              int e = es.spawn(EntityType.POI_CHEST_HIDDEN, wx, wy);
              if (e >= 0) {
                int idx = gs.areaChestStore().createChest();
                es.data0[e] = idx;
                fillHiddenChestLoot(gs, templateId, areaSeed, tx, ty, idx);
              }
            }
          }
        }
      }

      // Player spawn last (so entities exist when we place player, if needed)
      JsonValue sp = markers.get("playerSpawn");
      if (sp != null) {
        int tx = sp.getInt("x", 64);
        int ty = sp.getInt("y", 64);
        gs.areaSetPlayerWorldPos((tx + 0.5f) * World.TILE_WORLD, (ty + 0.5f) * World.TILE_WORLD);
      }
    } else {
      // Markers absent: clear zones to avoid leaking previous area state.
      gs.areaClearEnemyZones();
    }

    return true;
  }

  private static JsonValue readAreaJson(String templateId) {
    try {
      FileHandle fh = Gdx.files.internal(AREA_DIR + "/" + templateId + ".area.json");
      if (fh == null || !fh.exists()) return null;
      String txt = fh.readString("UTF-8");
      return new JsonReader().parse(txt);
    } catch (Throwable t) {
      return null;
    }
  }

  // ============================================================================
  // Editor/shared apply helpers
  // ============================================================================

  /**
   * Editor helper: apply an already-parsed .area.json onto a preview world + entity set.
   * This intentionally skips GameScreen-specific side effects (enemy zones/chest store/etc.).
   */
  public static boolean applyForEditor(World world, com.yourgame.survival.entity.Entities es, String templateId, JsonValue area) {
    if (world == null || es == null || area == null) return false;

    try {
      JsonValue size = area.get("size");
      int areaW = (size != null) ? size.getInt("w", 0) : 0;
      int areaH = (size != null) ? size.getInt("h", 0) : 0;
      if (areaW <= 0 || areaH <= 0) return false;

      JsonValue layers = area.get("layers");
      JsonValue ground = (layers != null) ? layers.get("ground") : null;
      JsonValue road = (layers != null) ? layers.get("road") : null;

      int defaultId = (ground != null) ? ground.getInt("defaultId", 0) : 0;

      applyGround(world, areaW, areaH, defaultId, ground);
      applyRoad(world, areaW, areaH, road);

      // IMPORTANT (Editor correctness):
      // Do NOT apply legacy HOME procedural terrain shaping in the editor.
      // Reason: it overwrites authored tiles after every load/reload, making saved edits appear
      // to "not stick" (e.g. HOME dirt paths repainting on top of user edits).
      // The editor must reflect *only* what is in the .area.json.
      // Runtime may still apply legacyHomeTerrain() for backwards compatibility.

      // Clear existing entities in preview.
      for (int i = 0; i < com.yourgame.survival.entity.Entities.MAX; i++) {
        if (es.alive[i]) es.kill(i);
      }

      // Apply authored objects (new schema) + legacy markers (nodes/poi) if desired.
      JsonValue markers = area.get("markers");
      if (markers != null) {
        applyAuthoredObjects(es, templateId, markers);
        // Also show legacy marker-based spawns (nodes/poi) so editor preview matches game feel.
        applyLegacyMarkerSpawns(es, templateId, markers);
      }
      return true;
    } catch (Throwable t) {
      return false;
    }
  }

  /** Apply markers.objects[]: {type,x,y,layerBias} -> spawn EntityType at tile center. */
  private static void applyAuthoredObjects(com.yourgame.survival.entity.Entities es, String templateId, JsonValue markers) {
    if (es == null || markers == null) return;
    JsonValue objs = markers.get("objects");
    if (objs == null) return;

    for (JsonValue o = objs.child; o != null; o = o.next) {
      String type = o.getString("type", null);
      if (type == null || type.trim().isEmpty()) continue;
      com.yourgame.survival.entity.EntityType et;
      try {
        et = com.yourgame.survival.entity.EntityType.valueOf(type.trim());
      } catch (Throwable ignored) {
        continue;
      }

      int tx = o.getInt("x", 0);
      int ty = o.getInt("y", 0);
      float wx = (tx + 0.5f) * World.TILE_WORLD;
      float wy = (ty + 0.5f) * World.TILE_WORLD;
      int e = es.spawn(et, wx, wy);
      if (e >= 0) {
        es.layerBias[e] = o.getFloat("layerBias", 0f);
        // For zone-leashed wandering behaviors, keep home at spawn.
        es.homeX[e] = wx;
        es.homeY[e] = wy;
      }
    }
  }

  /**
   * Legacy spawns from markers used by the runtime loader.
   * This is a visual/editor convenience to keep preview close to the game.
   */
  private static void applyLegacyMarkerSpawns(com.yourgame.survival.entity.Entities es, String templateId, JsonValue markers) {
    if (es == null || markers == null) return;

    // nodes[]: {t,x,y}
    JsonValue nodes = markers.get("nodes");
    if (nodes != null) {
      for (JsonValue n = nodes.child; n != null; n = n.next) {
        String t = n.getString("t", "");
        int tx = n.getInt("x", 0);
        int ty = n.getInt("y", 0);
        float wx = (tx + 0.5f) * World.TILE_WORLD;
        float wy = (ty + 0.5f) * World.TILE_WORLD;

        // Use real EntityType names if provided (preferred). Fallback for legacy short names.
        com.yourgame.survival.entity.EntityType et = null;
        String k = (t == null) ? "" : t.trim();
        if (!k.isEmpty()) {
          try {
            et = com.yourgame.survival.entity.EntityType.valueOf(k);
          } catch (Throwable ignored) {
            String u = k.toUpperCase();
            if ("TREE".equals(u)) et = com.yourgame.survival.entity.EntityType.NODE_TREE;
            else if ("BUSH".equals(u)) et = com.yourgame.survival.entity.EntityType.NODE_BUSH;
            else if ("ROCK".equals(u)) et = com.yourgame.survival.entity.EntityType.NODE_ROCK;
            else if ("STUMP".equals(u)) et = com.yourgame.survival.entity.EntityType.NODE_STUMP;
            else if ("IRON".equals(u) || "ORE_IRON".equals(u)) et = com.yourgame.survival.entity.EntityType.NODE_ORE_IRON;
            else if ("FISH".equals(u) || "FISH_SPOT".equals(u)) et = com.yourgame.survival.entity.EntityType.NODE_FISH_SPOT;
          }
        }
        if (et != null) es.spawn(et, wx, wy);
      }
    }

    // poi[] are not necessarily entities, but we can visualize some.
    JsonValue pois = markers.get("poi");
    if (pois != null) {
      for (JsonValue p = pois.child; p != null; p = p.next) {
        String kind = p.getString("kind", "");
        int tx = p.getInt("x", 0);
        int ty = p.getInt("y", 0);
        float wx = (tx + 0.5f) * World.TILE_WORLD;
        float wy = (ty + 0.5f) * World.TILE_WORLD;

        // Minimal: hidden chest
        if (kind != null && kind.trim().equalsIgnoreCase("CHEST_HIDDEN")) {
          es.spawn(com.yourgame.survival.entity.EntityType.POI_CHEST_HIDDEN, wx, wy);
        }
      }
    }
  }

  /** Deterministic area seed derived from worldSeed + templateId. */
  private static long areaSeed(GameScreen gs, String templateId) {
    long s = gs.areaWorldSeed();
    long h = templateId.hashCode();
    return s ^ (h * 0x9E3779B97F4A7C15L);
  }

  private static void applyGround(World world, int areaW, int areaH, int defaultId, JsonValue ground) {
    if (world == null) return;

    // Chunks to cover the area.
    int maxTx = Math.max(0, areaW - 1);
    int maxTy = Math.max(0, areaH - 1);
    int minCx = 0;
    int minCy = 0;
    int maxCx = Math.min((com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W - 1), maxTx / World.CHUNK_SIZE);
    int maxCy = Math.min((com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H - 1), maxTy / World.CHUNK_SIZE);

    // 1) Ensure all required chunks exist.
    for (int cy = minCy; cy <= maxCy; cy++) {
      for (int cx = minCx; cx <= maxCx; cx++) {
        world.chunk(cx, cy);
      }
    }

    // 2) Fill default ground + clear masks.
    for (int cy = minCy; cy <= maxCy; cy++) {
      for (int cx = minCx; cx <= maxCx; cx++) {
        Chunk c = world.chunk(cx, cy);
        TileLayers L = c.layers;
        for (int ly = 0; ly < World.CHUNK_SIZE; ly++) {
          int ty = cy * World.CHUNK_SIZE + ly;
          if (ty >= areaH) continue;
          for (int lx = 0; lx < World.CHUNK_SIZE; lx++) {
            int tx = cx * World.CHUNK_SIZE + lx;
            if (tx >= areaW) continue;
            int idx = lx + ly * World.CHUNK_SIZE;
            L.groundId[idx] = (short) defaultId;
            L.waterMask[idx] = 0;
            L.collisionMask[idx] = 0;
            L.roadMask[idx] = 0;
            L.overlayId[idx] = 0;
            L.decoId[idx] = 0;
            L.decoVar[idx] = 0;

            // FUSA Story: authored areas must not carry procedural biome/generator fields.
            // Keep everything flat + neutral so no "mixed biomes" or jitter occurs.
            L.biomeId[idx] = 0;
            L.height[idx] = 0;
            L.moisture[idx] = 0;
            L.heat[idx] = 0;
            L.vegetation[idx] = 0;
            L.rockiness[idx] = 0;
            L.pathField[idx] = 0;
            L.heightLevel[idx] = 0;
            L.waterDist[idx] = (byte) 255;

            L.shoreMask4[idx] = 0;
            L.roadMask4[idx] = 0;
            L.grassCornerMask16[idx] = 0;
            L.dirtCornerMask16[idx] = 0;
            L.sandCornerMask16[idx] = 0;
            L.rockCornerMask16[idx] = 0;
            L.snowCornerMask16[idx] = 0;
          }
        }
      }
    }

    // 3) Apply rectangle fills.
    // Supported formats:
    // A) { "rect": {x,y,w,h}, "id": <tileId> }
    // B) { "x":x, "y":y, "w":w, "h":h, "id": <tileId> }  (used by offline layout converter)
    JsonValue fills = (ground != null) ? ground.get("fills") : null;
    if (fills != null) {
      for (JsonValue f = fills.child; f != null; f = f.next) {
        JsonValue r = f.get("rect");
        int x0, y0, w, h;
        if (r != null) {
          x0 = r.getInt("x", 0);
          y0 = r.getInt("y", 0);
          w = r.getInt("w", 0);
          h = r.getInt("h", 0);
        } else {
          x0 = f.getInt("x", 0);
          y0 = f.getInt("y", 0);
          w = f.getInt("w", 0);
          h = f.getInt("h", 0);
        }
        int id = f.getInt("id", defaultId);
        fillRect(world, areaW, areaH, x0, y0, w, h, id);
      }
    }

    // 4) Apply single-tile patches.
    JsonValue patches = (ground != null) ? ground.get("patches") : null;
    if (patches != null) {
      for (JsonValue p = patches.child; p != null; p = p.next) {
        int x = p.getInt("x", 0);
        int y = p.getInt("y", 0);
        int id = p.getInt("id", defaultId);
        setTile(world, areaW, areaH, x, y, id);
      }
    }
  }

  private static void fillRect(World world, int areaW, int areaH, int x0, int y0, int w, int h, int id) {
    int x1 = Math.min(areaW, x0 + Math.max(0, w));
    int y1 = Math.min(areaH, y0 + Math.max(0, h));
    for (int y = Math.max(0, y0); y < y1; y++) {
      for (int x = Math.max(0, x0); x < x1; x++) {
        setTile(world, areaW, areaH, x, y, id);
      }
    }
  }

  private static void setTile(World world, int areaW, int areaH, int tx, int ty, int id) {
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    if (cx >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W) return;
    if (cy >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H) return;

    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    Chunk c = world.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.groundId[idx] = (short) id;
    c.layers.waterMask[idx] = 0;
    c.layers.collisionMask[idx] = 0;
  }

  private static void applyWater(World world, int areaW, int areaH, JsonValue water) {
    if (world == null || water == null) return;

    JsonValue fills = water.get("fills");
    if (fills != null) {
      for (JsonValue f = fills.child; f != null; f = f.next) {
        JsonValue r = f.get("rect");
        if (r == null) continue;
        int x0 = r.getInt("x", 0);
        int y0 = r.getInt("y", 0);
        int w = r.getInt("w", 0);
        int h = r.getInt("h", 0);
        fillWaterRect(world, areaW, areaH, x0, y0, w, h);
      }
    }

    JsonValue patches = water.get("patches");
    if (patches != null) {
      for (JsonValue p = patches.child; p != null; p = p.next) {
        int x = p.getInt("x", 0);
        int y = p.getInt("y", 0);
        setWaterTile(world, areaW, areaH, x, y);
      }
    }
  }

  private static void applyRoad(World world, int areaW, int areaH, JsonValue road) {
    if (world == null || road == null) return;

    // clear existing road mask inside area
    for (int ty = 0; ty < areaH; ty++) {
      for (int tx = 0; tx < areaW; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        Chunk c = world.peekChunk(cx, cy);
        if (c == null) continue;
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;
        c.layers.roadMask[idx] = 0;
      }
    }

    JsonValue fills = road.get("fills");
    if (fills != null) {
      for (JsonValue f = fills.child; f != null; f = f.next) {
        int x0 = f.getInt("x", 0);
        int y0 = f.getInt("y", 0);
        int w = f.getInt("w", 0);
        int h = f.getInt("h", 0);
        int v = f.getInt("v", 1);
        fillRoadRect(world, areaW, areaH, x0, y0, w, h, v != 0);
      }
    }

    JsonValue patches = road.get("patches");
    if (patches != null) {
      for (JsonValue p = patches.child; p != null; p = p.next) {
        int x = p.getInt("x", 0);
        int y = p.getInt("y", 0);
        int v = p.getInt("v", 1);
        setRoadTile(world, areaW, areaH, x, y, v != 0);
      }
    }
  }

  private static void fillRoadRect(World world, int areaW, int areaH, int x0, int y0, int w, int h, boolean on) {
    int x1 = Math.min(areaW, x0 + Math.max(0, w));
    int y1 = Math.min(areaH, y0 + Math.max(0, h));
    for (int y = Math.max(0, y0); y < y1; y++) {
      for (int x = Math.max(0, x0); x < x1; x++) {
        setRoadTile(world, areaW, areaH, x, y, on);
      }
    }
  }

  private static void setRoadTile(World world, int areaW, int areaH, int tx, int ty, boolean on) {
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    Chunk c = world.peekChunk(cx, cy);
    if (c == null) return;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.roadMask[idx] = (byte) (on ? 1 : 0);
  }

  private static void fillHiddenChestLoot(GameScreen gs, String templateId, long areaSeed, int tx, int ty, int chestIdx) {
    if (gs == null) return;
    com.yourgame.survival.data.Inventory c = gs.areaChestStore().get(chestIdx);
    if (c == null) return;

    // Deterministic RNG: stable per save + template + tile location.
    long seed = areaSeed ^ (long) (templateId != null ? templateId.hashCode() : 0) * 0x9E3779B97F4A7C15L;
    seed ^= (long) tx * 0xC2B2AE3D27D4EB4FL;
    seed ^= (long) ty * 0x165667B19E3779F9L;
    Random r = new Random(seed);

    // Always random amount (can be 0), per spec.
    int arrows = r.nextInt(21);      // 0..20
    int copper = r.nextInt(501);     // 0..500
    int iron = r.nextInt(21);        // 0..20

    // Very rare: 0..1
    int gold = (r.nextFloat() < 0.03f) ? 1 : 0;
    int sword = (r.nextFloat() < 0.02f) ? 1 : 0;

    if (arrows > 0) c.add(47, arrows);
    if (copper > 0) c.add(31, copper);
    if (iron > 0) c.add(2, iron);
    if (gold > 0) c.add(33, gold);
    if (sword > 0) c.add(20, sword);
  }

  private static void fillWaterRect(World world, int areaW, int areaH, int x0, int y0, int w, int h) {
    int x1 = Math.min(areaW, x0 + Math.max(0, w));
    int y1 = Math.min(areaH, y0 + Math.max(0, h));
    for (int y = Math.max(0, y0); y < y1; y++) {
      for (int x = Math.max(0, x0); x < x1; x++) {
        setWaterTile(world, areaW, areaH, x, y);
      }
    }
  }

  private static void setWaterTile(World world, int areaW, int areaH, int tx, int ty) {
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    if (cx >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W) return;
    if (cy >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H) return;

    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    Chunk c = world.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.waterMask[idx] = 1;
    c.layers.collisionMask[idx] = 0;
  }

  // ---------------------------------------------------------------------------
  // Legacy HOME terrain shaping (tiles only)
  // ---------------------------------------------------------------------------

  /**
   * Applies the old HOME visual shaping (rocks ring + clearing + dirt paths + lake + deco) as tile edits.
   *
   * This does NOT spawn entities/landmarks. Landmarks must be authored via markers.objects[].
   */
  private static void legacyHomeTerrain(World world, int areaW, int areaH, long seed) {
    if (world == null) return;
    if (areaW <= 0 || areaH <= 0) return;
    try {
      // Ensure chunks exist.
      for (int cy = 0; cy < com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H; cy++) {
        for (int cx = 0; cx < com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W; cx++) {
          world.chunk(cx, cy);
        }
      }

      final int cx0 = areaW / 2;
      final int cy0 = areaH / 2;

      // 1) Organic ROCK ridge ring near the border (not a rectangle).
      final float ringR = Math.min(cx0, cy0) - 18f;
      final float ringW = 5.5f;
      final int passHalfW = 10;
      final int passLen = 26;

      for (int ty = 0; ty < areaH; ty++) {
        for (int tx = 0; tx < areaW; tx++) {
          float dx = (tx + 0.5f) - cx0;
          float dy = (ty + 0.5f) - cy0;
          float dist = (float) Math.sqrt(dx * dx + dy * dy);

          float wobble = (hash01(seed ^ 0x3344556677L, tx, ty) - 0.5f) * 6.0f;
          float d = dist - (ringR + wobble);

          if (Math.abs(d) <= ringW) {
            boolean passN = (Math.abs(dx) <= passHalfW) && (dy > 0) && (dist > ringR - passLen);
            boolean passS = (Math.abs(dx) <= passHalfW) && (dy < 0) && (dist > ringR - passLen);
            boolean passE = (Math.abs(dy) <= passHalfW) && (dx > 0) && (dist > ringR - passLen);
            boolean passW = (Math.abs(dy) <= passHalfW) && (dx < 0) && (dist > ringR - passLen);
            if (!(passN || passS || passE || passW)) {
              setRockWall(world, areaW, areaH, tx, ty);
            }
          }
        }
      }

      // 2) Central clearing + dirt paths to passes.
      fillCircleGround(world, areaW, areaH, 192, 168, 22, TileIds.GROUND_DIRT);
      drawDirtCorridor(world, areaW, areaH, 192, 168, 192, areaH - 8);
      drawDirtCorridor(world, areaW, areaH, 192, 168, 192, 8);
      drawDirtCorridor(world, areaW, areaH, 192, 168, areaW - 8, 168);
      drawDirtCorridor(world, areaW, areaH, 192, 168, 8, 168);

      // 3) Sprinkle deco on grass (keeps the "büschelchen" feel consistent).
      scatterDecoOnGrass(world, seed ^ 0xD00D00DL, areaW, areaH);
    } catch (Throwable ignored) {}
  }

  private static void beautifyHome(GameScreen gs, int areaW, int areaH, long seed) {
    try {
      World world = gs.areaWorld();
      Entities es = gs.areaEntities();
      if (world == null || es == null) return;

      // Ensure chunks exist (HOME is a full 6x6-chunk area).
      for (int cy = 0; cy < com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H; cy++) {
        for (int cx = 0; cx < com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W; cx++) {
          world.chunk(cx, cy);
        }
      }

      // Nicht fertiges Feature: // Random r = new Random(seed ^ 0xBEEFF00DL); // (unused)

      final int cx0 = areaW / 2;
      final int cy0 = areaH / 2;

      // ------------------------------------------------------------
      // 1) Organic ROCK ridge ring near the border (not a rectangle).
      //    Leave 4 passes at N/E/S/W so exits are reachable.
      // ------------------------------------------------------------
      final float ringR = Math.min(cx0, cy0) - 18f; // ~174
      final float ringW = 5.5f;                    // thickness
      final int passHalfW = 10;
      final int passLen = 26;

      for (int ty = 0; ty < areaH; ty++) {
        for (int tx = 0; tx < areaW; tx++) {
          float dx = (tx + 0.5f) - cx0;
          float dy = (ty + 0.5f) - cy0;
          float dist = (float) Math.sqrt(dx * dx + dy * dy);

          // small deterministic wobble so it doesn't look like a perfect circle
          float wobble = (hash01(seed ^ 0x3344556677L, tx, ty) - 0.5f) * 6.0f;
          float d = dist - (ringR + wobble);

          if (Math.abs(d) <= ringW) {
            // Carve 4 passes (corridors) through the ridge.
            boolean passN = (Math.abs(dx) <= passHalfW) && (dy > 0) && (dist > ringR - passLen);
            boolean passS = (Math.abs(dx) <= passHalfW) && (dy < 0) && (dist > ringR - passLen);
            boolean passE = (Math.abs(dy) <= passHalfW) && (dx > 0) && (dist > ringR - passLen);
            boolean passW = (Math.abs(dy) <= passHalfW) && (dx < 0) && (dist > ringR - passLen);
            if (!(passN || passS || passE || passW)) {
              setRockWall(world, areaW, areaH, tx, ty);
            }
          }
        }
      }

      // ------------------------------------------------------------
      // 2) Central clearing + dirt paths to passes.
      // ------------------------------------------------------------
      fillCircleGround(world, areaW, areaH, 192, 168, 22, TileIds.GROUND_DIRT);
      // cross paths
      drawDirtCorridor(world, areaW, areaH, 192, 168, 192, areaH - 8);
      drawDirtCorridor(world, areaW, areaH, 192, 168, 192, 8);
      drawDirtCorridor(world, areaW, areaH, 192, 168, areaW - 8, 168);
      drawDirtCorridor(world, areaW, areaH, 192, 168, 8, 168);

      // ------------------------------------------------------------
      // 2b) HOME landmark: castle + bridge (hand-placed sprites)
      //     Place them into the reserved "future_houses" rectangle from HOME.area.json
      //     (x=174,y=160,w=36,h=28)
      // ------------------------------------------------------------
      placeHomeCastleAndBridge(es);

      // ------------------------------------------------------------
      // (Old test landmark: terraced mountain) — removed to free HOME space.
      // ------------------------------------------------------------
      // placeTerracedMountain(world, areaW, areaH, 96, 96, 14, 3);

      // ------------------------------------------------------------
      // 3) A small lake + wider sandy shore (2-4 tiles), no grass gap.
      // ------------------------------------------------------------
      int lakeX = 292;
      int lakeY = 268;
      int lakeR = 17;
      fillCircleWater(world, areaW, areaH, lakeX, lakeY, lakeR, seed ^ 0xABC123L);

      // Sandy shore: distance band around water (2..4-ish), with some noise.
      paintSandAroundWater(world, areaW, areaH, 4, seed ^ 0x0A0B0C0DL);

      // ------------------------------------------------------------
      // 4) A small snow patch up in the NW (subtle).
      // ------------------------------------------------------------
      fillCircleGround(world, areaW, areaH, 70, 318, 18, TileIds.GROUND_SNOW);
      fillCircleGround(world, areaW, areaH, 92, 300, 10, TileIds.GROUND_SNOW);

      // ------------------------------------------------------------
      // 5) Bake shore masks (so shore overlay matches authored water).
      // ------------------------------------------------------------
      bakeShoreMask4(world, areaW, areaH);

      // ------------------------------------------------------------
      // 6) Spawn life ("lebendig", but not flooded).
      // ------------------------------------------------------------
      // Merchant near clearing edge.
      float mwx = (200.5f) * World.TILE_WORLD;
      float mwy = (182.5f) * World.TILE_WORLD;
      es.spawn(EntityType.MERCHANT_ELF, mwx, mwy);

      // Deer: a few groups (avoid lake + clearing).
      // NOTE: hex literals must contain only [0-9a-f].
      spawnDeerCluster(es, seed ^ 0xDEED0001L, 250, 210, 6);
      spawnDeerCluster(es, seed ^ 0xDEED0002L, 110, 220, 5);
      spawnDeerCluster(es, seed ^ 0xDEED0003L, 240, 120, 4);

      // Trees: mostly outside the clearing and away from paths/water.
      spawnTreesOrganic(world, es, seed ^ 0x7BEEF00DL, areaW, areaH, 260);

      // Flowers + tufts: sprinkle on grass (avoid clearing/path/sand/rock/snow).
      scatterDecoOnGrass(world, seed ^ 0xD00D00DL, areaW, areaH);

    } catch (Throwable ignored) {}
  }

  private static void placeHomeCastleAndBridge(Entities es) {
    if (es == null) return;

    // Reserved rect center in tiles (future_houses): x=174,y=160,w=36,h=28
    final int centerTx = 174 + (36 / 2);
    final int centerTy = 160 + (28 / 2);

    // Convert to world units.
    final float cx = (centerTx + 0.5f) * World.TILE_WORLD;
    final float cy = (centerTy + 0.5f) * World.TILE_WORLD;

    // Castle sits slightly back (higher y). Bridge sits in front (lower y) so it's drawn on top.
    // NOTE: These offsets are artistic placement and can be adjusted once you mark collisions.
    final float castleX = cx;
    final float castleY = cy + 90f;

    // Bridge should "münden" at the gate: nudge it towards the castle's lower-right.
    // Move bridge further LEFT so it lines up better with the gate.
    final float bridgeX = cx + 71f;
    final float bridgeY = cy - 143f;

    es.spawn(EntityType.LANDMARK_CASTLE, castleX, castleY);
    es.spawn(EntityType.LANDMARK_BRIDGE, bridgeX, bridgeY);
  }

  private static void setGroundKeepWater(World world, int areaW, int areaH, int tx, int ty, int id) {
    if (world == null) return;
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    if (cx >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W) return;
    if (cy >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H) return;

    Chunk c = world.chunk(cx, cy);
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.groundId[idx] = (short) id;
  }

  private static void setRockWall(World world, int areaW, int areaH, int tx, int ty) {
    if (world == null) return;
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    if (cx >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W) return;
    if (cy >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H) return;

    Chunk c = world.chunk(cx, cy);
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.groundId[idx] = (short) TileIds.GROUND_ROCK;
    c.layers.collisionMask[idx] = 1;
    c.layers.waterMask[idx] = 0;
  }

  // Nicht fertiges Feature:
  // placeTerracedMountain(...) + setMountainTile(...)
  // (Unused right now; keeping as commented code for later.)
  /*
  private static void placeTerracedMountain(World world, int areaW, int areaH, int x0, int y0, int radius, int maxHeight) {
    if (world == null) return;
    if (radius <= 0 || maxHeight <= 0) return;

    int r2 = radius * radius;
    for (int ty = y0 - radius; ty <= y0 + radius; ty++) {
      for (int tx = x0 - radius; tx <= x0 + radius; tx++) {
        int dx = tx - x0;
        int dy = ty - y0;
        int d2 = dx * dx + dy * dy;
        if (d2 > r2) continue;

        // Don't stomp authored/procedural water.
        if (isWaterAt(world, tx, ty)) continue;

        // Terraces: outer band=1, mid=2, inner=3 (for maxHeight=3).
        float t = 1f - (float) d2 / (float) r2; // 0..1
        int h;
        if (maxHeight == 1) {
          h = 1;
        } else {
          // Map t to [1..maxHeight] with 3-ish steps.
          // For maxHeight=3 this yields: ~outer=1, mid=2, core=3.
          h = 1 + (int) Math.floor(t * maxHeight);
          if (h > maxHeight) h = maxHeight;
          if (h < 1) h = 1;
        }

        setMountainTile(world, areaW, areaH, tx, ty, h);
      }
    }
  }

  private static void setMountainTile(World world, int areaW, int areaH, int tx, int ty, int h) {
    if (world == null) return;
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;

    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    if (cx >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W) return;
    if (cy >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H) return;

    Chunk c = world.chunk(cx, cy);
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    int idx = lx + ly * World.CHUNK_SIZE;

    // Visual: rock ground + heightLevel cliffs.
    c.layers.groundId[idx] = (short) TileIds.GROUND_ROCK;
    c.layers.heightLevel[idx] = (byte) Math.max(0, Math.min(255, h));

    // Gameplay: for now, block the mountain tiles (so it behaves like a real obstacle).
    c.layers.collisionMask[idx] = 1;
    c.layers.waterMask[idx] = 0;
  }
  */

  private static void fillCircleGround(World world, int areaW, int areaH, int x0, int y0, int r, int id) {
    int r2 = r * r;
    for (int ty = y0 - r; ty <= y0 + r; ty++) {
      for (int tx = x0 - r; tx <= x0 + r; tx++) {
        int dx = tx - x0;
        int dy = ty - y0;
        if (dx * dx + dy * dy <= r2) {
          // Don't overwrite water with ground.
          if (isWaterAt(world, tx, ty)) continue;
          setGroundKeepWater(world, areaW, areaH, tx, ty, id);
        }
      }
    }
  }

  private static void fillCircleWater(World world, int areaW, int areaH, int x0, int y0, int r, long seed) {
    // int r2 = r * r; // unused
    for (int ty = y0 - r - 2; ty <= y0 + r + 2; ty++) {
      for (int tx = x0 - r - 2; tx <= x0 + r + 2; tx++) {
        int dx = tx - x0;
        int dy = ty - y0;
        float d2 = dx * dx + dy * dy;
        if (d2 > (r + 2) * (r + 2)) continue;

        // ragged edge
        float edge = (hash01(seed, tx, ty) - 0.5f) * 3.5f;
        float rr = r + edge;
        if (d2 <= rr * rr) {
          setWaterTile(world, areaW, areaH, tx, ty);
        }
      }
    }
  }

  private static void paintSandAroundWater(World world, int areaW, int areaH, int maxDist, long seed) {
    // Cheap distance band: for each land tile, look in a (2*maxDist+1)^2 window for water.
    // maxDist is small (<=4) so this is fine for one HOME load.
    for (int ty = 0; ty < areaH; ty++) {
      for (int tx = 0; tx < areaW; tx++) {
        if (isWaterAt(world, tx, ty)) continue;

        int best = 999;
        for (int dy = -maxDist; dy <= maxDist; dy++) {
          for (int dx = -maxDist; dx <= maxDist; dx++) {
            int md = Math.abs(dx) + Math.abs(dy);
            if (md == 0 || md > maxDist) continue;
            if (isWaterAt(world, tx + dx, ty + dy)) {
              if (md < best) best = md;
            }
          }
        }
        if (best == 999) continue;

        // Make shore wider and a bit irregular.
        float n = hash01(seed, tx, ty);
        int widen = (n < 0.25f ? 1 : 0); // occasionally push one tile further
        if (best <= (maxDist - 1 + widen)) {
          setGroundKeepWater(world, areaW, areaH, tx, ty, TileIds.GROUND_SAND);
        }
      }
    }
  }

  private static void drawDirtCorridor(World world, int areaW, int areaH, int x0, int y0, int x1, int y1) {
    // Simple Bresenham-like corridor + thickness.
    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);
    int sx = x0 < x1 ? 1 : -1;
    int sy = y0 < y1 ? 1 : -1;
    int err = dx - dy;

    int x = x0;
    int y = y0;
    while (true) {
      // thickness
      for (int oy = -2; oy <= 2; oy++) {
        for (int ox = -2; ox <= 2; ox++) {
          if (ox * ox + oy * oy > 6) continue;
          if (isWaterAt(world, x + ox, y + oy)) continue;
          setGroundKeepWater(world, areaW, areaH, x + ox, y + oy, TileIds.GROUND_DIRT);
        }
      }

      if (x == x1 && y == y1) break;
      int e2 = 2 * err;
      if (e2 > -dy) { err -= dy; x += sx; }
      if (e2 < dx) { err += dx; y += sy; }
    }
  }

  private static boolean isWaterAt(World world, int tx, int ty) {
    if (world == null) return false;
    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);
    Chunk c = world.peekChunk(cx, cy);
    if (c == null) return false;
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.waterMask[idx] != 0;
  }

  private static void bakeShoreMask4(World world, int areaW, int areaH) {
    if (world == null) return;
    for (int ty = 0; ty < areaH; ty++) {
      for (int tx = 0; tx < areaW; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        Chunk c = world.chunk(cx, cy);
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;

        boolean isWater = c.layers.waterMask[idx] != 0;
        if (isWater) {
          c.layers.shoreMask4[idx] = 0;
          continue;
        }

        boolean n = isWaterAt(world, tx, ty + 1);
        boolean e = isWaterAt(world, tx + 1, ty);
        boolean s = isWaterAt(world, tx, ty - 1);
        boolean w = isWaterAt(world, tx - 1, ty);
        c.layers.shoreMask4[idx] = (byte) ((n ? 1 : 0) | (e ? 2 : 0) | (s ? 4 : 0) | (w ? 8 : 0));
      }
    }
  }

  private static void spawnDeerCluster(Entities es, long seed, int centerTx, int centerTy, int count) {
    if (es == null) return;
    Random r = new Random(seed);
    for (int i = 0; i < count; i++) {
      int tx = centerTx + (r.nextInt(21) - 10);
      int ty = centerTy + (r.nextInt(21) - 10);
      float wx = (tx + 0.5f) * World.TILE_WORLD;
      float wy = (ty + 0.5f) * World.TILE_WORLD;
      es.spawn(EntityType.ANIMAL_DEER, wx, wy);
    }
  }

  private static void spawnTreesOrganic(World world, Entities es, long seed, int areaW, int areaH, int targetTrees) {
    if (world == null || es == null) return;
    Random r = new Random(seed);

    int spawned = 0;
    int tries = 0;
    while (spawned < targetTrees && tries < targetTrees * 60) {
      tries++;
      int tx = 8 + r.nextInt(Math.max(1, areaW - 16));
      int ty = 8 + r.nextInt(Math.max(1, areaH - 16));

      // Avoid central clearing zone.
      if (tx > 140 && tx < 244 && ty > 120 && ty < 240) continue;

      // Avoid paths (rough cross corridors).
      if (Math.abs(tx - 192) <= 4 || Math.abs(ty - 168) <= 4) continue;

      // Avoid water + sand beach band.
      if (isWaterAt(world, tx, ty)) continue;
      int gid = groundAt(world, tx, ty);
      if (gid == TileIds.GROUND_SAND) continue;
      if (gid == TileIds.GROUND_ROCK) continue;
      if (gid == TileIds.GROUND_SNOW) continue;

      float wx = (tx + 0.5f) * World.TILE_WORLD;
      float wy = (ty + 0.5f) * World.TILE_WORLD;

      // Keep some distance to the merchant.
      float mdx = wx - (200.5f * World.TILE_WORLD);
      float mdy = wy - (182.5f * World.TILE_WORLD);
      if (mdx * mdx + mdy * mdy < (65f * 65f)) continue;

      int e = es.spawn(EntityType.NODE_TREE, wx, wy);
      if (e >= 0) spawned++;
    }
  }

  private static int groundAt(World world, int tx, int ty) {
    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);
    Chunk c = world.peekChunk(cx, cy);
    if (c == null) return TileIds.GROUND_GRASS;
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.groundId[idx] & 0xFF;
  }

  private static void scatterDecoOnGrass(World world, long seed, int areaW, int areaH) {
    if (world == null) return;

    for (int ty = 6; ty < areaH - 6; ty++) {
      for (int tx = 6; tx < areaW - 6; tx++) {
        if (isWaterAt(world, tx, ty)) continue;

        int gid = groundAt(world, tx, ty);
        if (gid != TileIds.GROUND_GRASS) continue;

        // Keep clearing & roads readable.
        if (tx > 150 && tx < 235 && ty > 130 && ty < 230) continue;
        if (Math.abs(tx - 192) <= 4 || Math.abs(ty - 168) <= 4) continue;

        float roll = hash01(seed, tx, ty);
        if (roll < 0.004f) {
          setDeco(world, tx, ty, (byte) 2, (byte) ((int) (hash01(seed ^ 0x55L, tx, ty) * 4) & 3));
        } else if (roll < 0.010f) {
          setDeco(world, tx, ty, (byte) 1, (byte) ((int) (hash01(seed ^ 0xAA55L, tx, ty) * 4) & 3));
        }
      }
    }
  }

  private static void setDeco(World world, int tx, int ty, byte decoId, byte decoVar) {
    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    Chunk c = world.peekChunk(cx, cy);
    if (c == null) return;
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.decoId[idx] = decoId;
    c.layers.decoVar[idx] = decoVar;
  }

  private static int floorDiv(int a, int b) {
    int r = a / b;
    if ((a ^ b) < 0 && (r * b != a)) r--;
    return r;
  }

  private static int mod(int a, int b) {
    int m = a % b;
    if (m < 0) m += b;
    return m;
  }

  /** Deterministic hash in [0,1) for organic wobble/scatter. */
  private static float hash01(long seed, int x, int y) {
    long h = seed;
    h ^= (long) x * 0x9E3779B97F4A7C15L;
    h ^= (long) y * 0xC2B2AE3D27D4EB4FL;
    h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
    h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
    h = (h ^ (h >>> 33));
    return ((h >>> 40) & 0xFFFFFF) / (float) 0x1000000;
  }

  private static int bitIndex(int tx, int ty, int wTiles) {
    return tx + ty * wTiles;
  }

  private static boolean bitGet(byte[] bits, int bit) {
    if (bits == null || bit < 0) return false;
    int i = bit >>> 3;
    if (i < 0 || i >= bits.length) return false;
    int m = 1 << (bit & 7);
    return (bits[i] & m) != 0;
  }

  private static void bitSet(byte[] bits, int bit, boolean on) {
    if (bits == null || bit < 0) return;
    int i = bit >>> 3;
    if (i < 0 || i >= bits.length) return;
    int m = 1 << (bit & 7);
    if (on) bits[i] = (byte) (bits[i] | m);
    else bits[i] = (byte) (bits[i] & ~m);
  }

  /** Build a deterministic 60% tree presence mask for FOREST_01, excluding roads/water. */
  private static byte[] buildForest01TreePresence(World world, int areaW, int areaH, long seed) {
    int w = Math.max(0, areaW);
    int h = Math.max(0, areaH);
    int n = w * h;
    int bytes = (n + 7) >>> 3;
    byte[] out = new byte[bytes];

    // Targets: 1.5% of ALL tiles, but we will not place on road/water.
    // (Reduced to 10% of the previous 15% density.)
    int targetTotal = (int) Math.round(n * 0.015);

    // Define 4 zones by nearest enemy-zone centers (hardcoded to the authored layout).
    // These centers match the circles in FOREST_01.area.json.
    int[] zcx = {279, 74, 287, 71};
    int[] zcy = {83, 89, 290, 294};
    int zones = 4;

    // Collect candidates per zone.
    java.util.ArrayList<int[]> cand = new java.util.ArrayList<>(zones);
    java.util.ArrayList<long[]> keys = new java.util.ArrayList<>(zones);

    int[] counts = new int[zones];
    for (int i = 0; i < zones; i++) counts[i] = 0;

    // First pass: count candidates.
    for (int ty = 0; ty < h; ty++) {
      for (int tx = 0; tx < w; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        // IMPORTANT: ensure chunk exists (area reset creates a clean world; chunks are created on-demand).
        Chunk c = world.chunk(cx, cy);
        if (c == null) continue;
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;
        if (c.layers.waterMask[idx] != 0) continue;
        if (c.layers.roadMask[idx] != 0) continue;

        // Assign to nearest zone center.
        int best = 0;
        int bestD2 = Integer.MAX_VALUE;
        for (int z = 0; z < zones; z++) {
          int dx = tx - zcx[z];
          int dy = ty - zcy[z];
          int d2 = dx * dx + dy * dy;
          if (d2 < bestD2) { bestD2 = d2; best = z; }
        }
        counts[best]++;
      }
    }

    // Allocate arrays.
    for (int z = 0; z < zones; z++) {
      cand.add(new int[counts[z]]);
      keys.add(new long[counts[z]]);
      counts[z] = 0; // reuse as write cursor
    }

    // Second pass: fill arrays with hash keys for deterministic selection.
    for (int ty = 0; ty < h; ty++) {
      for (int tx = 0; tx < w; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        // IMPORTANT: ensure chunk exists (area reset creates a clean world; chunks are created on-demand).
        Chunk c = world.chunk(cx, cy);
        if (c == null) continue;
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;
        if (c.layers.waterMask[idx] != 0) continue;
        if (c.layers.roadMask[idx] != 0) continue;

        int best = 0;
        int bestD2 = Integer.MAX_VALUE;
        for (int z = 0; z < zones; z++) {
          int dx = tx - zcx[z];
          int dy = ty - zcy[z];
          int d2 = dx * dx + dy * dy;
          if (d2 < bestD2) { bestD2 = d2; best = z; }
        }

        int pos = counts[best]++;
        int tile = bitIndex(tx, ty, w);
        cand.get(best)[pos] = tile;

        // 24-bit hash + tile id packed into a long for sorting.
        int h24 = (int) (hash01(seed ^ 0xC0FFEE1234L, tx, ty) * 16777216.0f) & 0xFFFFFF;
        keys.get(best)[pos] = (((long) h24) << 32) | (tile & 0xFFFFFFFFL);
      }
    }

    // Targets per zone: equal split as requested, with spillover if a zone lacks candidates.
    int base = targetTotal / zones;
    int rem = targetTotal - base * zones;
    int[] want = new int[zones];
    for (int z = 0; z < zones; z++) want[z] = base + (z < rem ? 1 : 0);

    // Place trees.
    int placed = 0;
    for (int z = 0; z < zones; z++) {
      long[] ks = keys.get(z);
      java.util.Arrays.sort(ks);

      int take = Math.min(want[z], ks.length);
      for (int i = 0; i < take; i++) {
        int tile = (int) (ks[i] & 0xFFFFFFFFL);
        bitSet(out, tile, true);
      }
      placed += take;
      want[z] -= take;
    }

    // Spillover: if we couldn't place enough in a zone (should be rare), fill from other zones.
    if (placed < targetTotal) {
      for (int z = 0; z < zones && placed < targetTotal; z++) {
        long[] ks = keys.get(z);
        for (int i = 0; i < ks.length && placed < targetTotal; i++) {
          int tile = (int) (ks[i] & 0xFFFFFFFFL);
          if (bitGet(out, tile)) continue;
          bitSet(out, tile, true);
          placed++;
        }
      }
    }

    return out;
  }

  /**
   * Build a deterministic tree presence mask for HOME.
   *
   * Rules:
   * - Only place on GRASS ground tiles (so dirt paths stay clear automatically)
   * - Never place on water/road masks
   * - Keep a clear radius around the center clearing
   */
  private static byte[] buildHomeTreePresence(World world, int areaW, int areaH, long seed) {
    int w = Math.max(0, areaW);
    int h = Math.max(0, areaH);
    int n = w * h;
    int bytes = (n + 7) >>> 3;
    byte[] out = new byte[bytes];
    if (world == null) return out;

    final int cx0 = 192;
    final int cy0 = 168;
    final int clearR = 30; // keep central clearing open
    final int clearR2 = clearR * clearR;

    // Target density: ~0.8% of tiles (HOME should be open-ish).
    int target = (int) Math.round(n * 0.008);

    // Collect candidates with hash keys.
    java.util.ArrayList<Long> keys = new java.util.ArrayList<>(Math.max(128, target * 3));
    for (int ty = 0; ty < h; ty++) {
      for (int tx = 0; tx < w; tx++) {
        int dx = tx - cx0;
        int dy = ty - cy0;
        if (dx * dx + dy * dy <= clearR2) continue;

        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        Chunk c = world.chunk(cx, cy);
        if (c == null) continue;
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;

        if (c.layers.waterMask[idx] != 0) continue;
        if (c.layers.roadMask[idx] != 0) continue;
        int g = c.layers.groundId[idx] & 0xFF;
        if (g != com.yourgame.survival.world.TileIds.GROUND_GRASS) continue;

        int tile = bitIndex(tx, ty, w);
        int h24 = (int) (hash01(seed ^ 0xABCD1234EF01L, tx, ty) * 16777216.0f) & 0xFFFFFF;
        long k = (((long) h24) << 32) | (tile & 0xFFFFFFFFL);
        keys.add(k);
      }
    }

    // Sort keys and take top N.
    keys.sort(java.util.Comparator.naturalOrder());
    int take = Math.min(target, keys.size());
    for (int i = 0; i < take; i++) {
      int tile = (int) (keys.get(i) & 0xFFFFFFFFL);
      bitSet(out, tile, true);
    }
    return out;
  }

  private static byte[] loadOrInitTreeCutBits(GameScreen gs, int areaW, int areaH) {
    int n = Math.max(0, areaW) * Math.max(0, areaH);
    int bytes = (n + 7) >>> 3;

    byte[] out = new byte[bytes];
    try {
      if (gs == null) return out;
      var wm = gs.areaWorldMapState();
      if (wm == null) return out;
      com.yourgame.survival.worldmap.AreaCoord c = new com.yourgame.survival.worldmap.AreaCoord(wm.curAx, wm.curAy);
      var st = wm.areaStates.get(c);
      if (st == null || st.treeCutB64 == null || st.treeCutB64.isEmpty()) return out;
      if (st.treeCutW != areaW || st.treeCutH != areaH) return out;
      byte[] raw = java.util.Base64.getDecoder().decode(st.treeCutB64);
      if (raw != null && raw.length == out.length) return raw;
    } catch (Throwable ignored) {}
    return out;
  }

  private static void spawnForestTreesAndDeer(GameScreen gs, int areaW, int areaH, long seed) {
    Entities es = gs.areaEntities();
    if (es == null) return;

    Random r = new Random(seed ^ 0xF045E7L);

    // Tree placement: spacing in tiles.
    final int step = com.yourgame.survival.tuning.TuningAreas.FOREST_TREE_STEP_TILES;

    for (int ty = 2; ty < areaH - 2; ty += step) {
      for (int tx = 2; tx < areaW - 2; tx += step) {
        int jx = tx + (r.nextInt(3) - 1);
        int jy = ty + (r.nextInt(3) - 1);

        float wx = (jx + 0.5f) * World.TILE_WORLD;
        float wy = (jy + 0.5f) * World.TILE_WORLD;

        int e = es.spawn(EntityType.NODE_TREE, wx, wy);
        if (e < 0) break;
      }
    }

    final int deerTarget = com.yourgame.survival.tuning.TuningAreas.FOREST_DEER_TARGET;
    final float minDist = com.yourgame.survival.tuning.TuningAreas.FOREST_DEER_MIN_DIST_WU;
    final float min2 = minDist * minDist;

    final float minTreeDist = com.yourgame.survival.tuning.TuningAreas.FOREST_DEER_MIN_TREE_DIST_WU;
    final float minTree2 = minTreeDist * minTreeDist;

    int deerSpawned = 0;
    int attempts = 0;
    int maxAttempts = 2000;

    while (deerSpawned < deerTarget && attempts < maxAttempts) {
      attempts++;
      int tx = 2 + r.nextInt(Math.max(1, areaW - 4));
      int ty = 2 + r.nextInt(Math.max(1, areaH - 4));
      float wx = (tx + 0.5f) * World.TILE_WORLD;
      float wy = (ty + 0.5f) * World.TILE_WORLD;

      boolean near = false;
      for (int i = 0; i < Entities.MAX; i++) {
        if (!es.alive[i]) continue;
        if (es.type[i] != EntityType.ANIMAL_DEER) continue;
        float dx = es.x[i] - wx;
        float dy = es.y[i] - wy;
        if (dx * dx + dy * dy < min2) { near = true; break; }
      }
      if (near) continue;

      boolean nearTree = false;
      for (int i = 0; i < Entities.MAX; i++) {
        if (!es.alive[i]) continue;
        if (es.type[i] != EntityType.NODE_TREE) continue;
        float dx = es.x[i] - wx;
        float dy = es.y[i] - wy;
        if (dx * dx + dy * dy < minTree2) { nearTree = true; break; }
      }
      if (nearTree) continue;

      int e = es.spawn(EntityType.ANIMAL_DEER, wx, wy);
      if (e >= 0) deerSpawned++;
    }
  }

  // ============================================================
  // Corner-mask baking (Area-based: from ground grid, no noise)
  // ============================================================

  /**
   * Compute corner masks for all 5 edge types from the ground grid.
   * This replaces DefaultTransitionMaskBuilder for area-based worlds.
   */
  private static void bakeCornerMasksFromGround(World world, int areaW, int areaH) {
    if (world == null) return;

    for (int ty = 0; ty < areaH; ty++) {
      for (int tx = 0; tx < areaW; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        Chunk c = world.peekChunk(cx, cy);
        if (c == null) continue;
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;

        if (c.layers.waterMask[idx] != 0) {
          c.layers.grassCornerMask16[idx] = 0;
          c.layers.dirtCornerMask16[idx] = 0;
          c.layers.sandCornerMask16[idx] = 0;
          c.layers.rockCornerMask16[idx] = 0;
          c.layers.snowCornerMask16[idx] = 0;
          continue;
        }

        short base = (short) (c.layers.groundId[idx] & 0xFF);

        c.layers.grassCornerMask16[idx] = (base == TileIds.GROUND_GRASS
            || !com.yourgame.survival.world.TransitionRules.allowTransition(base, TileIds.GROUND_GRASS))
            ? 0 : (byte) computeCornerMask(world, tx, ty, TileIds.GROUND_GRASS);

        c.layers.dirtCornerMask16[idx] = (base == TileIds.GROUND_DIRT
            || !com.yourgame.survival.world.TransitionRules.allowTransition(base, TileIds.GROUND_DIRT))
            ? 0 : (byte) computeCornerMask(world, tx, ty, TileIds.GROUND_DIRT);

        c.layers.sandCornerMask16[idx] = (base == TileIds.GROUND_SAND
            || !com.yourgame.survival.world.TransitionRules.allowTransition(base, TileIds.GROUND_SAND))
            ? 0 : (byte) computeCornerMask(world, tx, ty, TileIds.GROUND_SAND);

        c.layers.rockCornerMask16[idx] = (base == TileIds.GROUND_ROCK
            || !com.yourgame.survival.world.TransitionRules.allowTransition(base, TileIds.GROUND_ROCK))
            ? 0 : (byte) computeCornerMask(world, tx, ty, TileIds.GROUND_ROCK);

        c.layers.snowCornerMask16[idx] = (base == TileIds.GROUND_SNOW
            || !com.yourgame.survival.world.TransitionRules.allowTransition(base, TileIds.GROUND_SNOW))
            ? 0 : (byte) computeCornerMask(world, tx, ty, TileIds.GROUND_SNOW);
      }
    }
  }

  private static int computeCornerMask(World world, int tx, int ty, short targetGid) {
    int nw = cornerInside(world, tx, ty + 1, targetGid);
    int ne = cornerInside(world, tx + 1, ty + 1, targetGid);
    int se = cornerInside(world, tx + 1, ty, targetGid);
    int sw = cornerInside(world, tx, ty, targetGid);
    return nw | (ne << 1) | (se << 2) | (sw << 3);
  }

  private static int cornerInside(World world, int cornerX, int cornerY, short targetGid) {
    int count = 0;
    for (int dy = 0; dy >= -1; dy--) {
      for (int dx = 0; dx >= -1; dx--) {
        int gid = peekGroundAt(world, cornerX + dx, cornerY + dy);
        if (gid == (targetGid & 0xFF)) count++;
      }
    }
    return count >= 2 ? 1 : 0;
  }

  private static int peekGroundAt(World world, int tx, int ty) {
    if (world == null) return TileIds.GROUND_GRASS;
    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    Chunk c = world.peekChunk(cx, cy);
    if (c == null) return TileIds.GROUND_GRASS;
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);
    return c.layers.groundId[lx + ly * World.CHUNK_SIZE] & 0xFF;
  }

  /**
   * Apply pre-baked corner masks from V2 JSON (patches format).
   */
  private static void applyCornerMasksFromJson(World world, int areaW, int areaH, JsonValue cornerMasks) {
    if (world == null || cornerMasks == null) return;

    applyCornerMaskLayer(world, areaW, areaH, cornerMasks.get("grass"), "grass");
    applyCornerMaskLayer(world, areaW, areaH, cornerMasks.get("dirt"), "dirt");
    applyCornerMaskLayer(world, areaW, areaH, cornerMasks.get("sand"), "sand");
    applyCornerMaskLayer(world, areaW, areaH, cornerMasks.get("rock"), "rock");
    applyCornerMaskLayer(world, areaW, areaH, cornerMasks.get("snow"), "snow");
  }

  private static void applyCornerMaskLayer(World world, int areaW, int areaH, JsonValue patches, String type) {
    if (patches == null) return;

    for (JsonValue p = patches.child; p != null; p = p.next) {
      int tx = p.getInt("x", -1);
      int ty = p.getInt("y", -1);
      int m = p.getInt("m", 0);
      if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) continue;
      if (m == 0) continue;

      int cx = tx / World.CHUNK_SIZE;
      int cy = ty / World.CHUNK_SIZE;
      Chunk c = world.peekChunk(cx, cy);
      if (c == null) continue;
      int lx = tx - cx * World.CHUNK_SIZE;
      int ly = ty - cy * World.CHUNK_SIZE;
      int idx = lx + ly * World.CHUNK_SIZE;

      switch (type) {
        case "grass" -> c.layers.grassCornerMask16[idx] = (byte) m;
        case "dirt" -> c.layers.dirtCornerMask16[idx] = (byte) m;
        case "sand" -> c.layers.sandCornerMask16[idx] = (byte) m;
        case "rock" -> c.layers.rockCornerMask16[idx] = (byte) m;
        case "snow" -> c.layers.snowCornerMask16[idx] = (byte) m;
      }
    }
  }

  // ============================================================
  // Road adjacency baking + height fix
  // ============================================================

  /**
   * Compute roadMask4 (4-neighbor adjacency) for all road tiles,
   * and reset heightLevel to 0 on road tiles to fix the "floating road" visual bug.
   */
  private static void bakeRoadAdjacencyAndFixHeight(World world, int areaW, int areaH) {
    if (world == null) return;

    for (int ty = 0; ty < areaH; ty++) {
      for (int tx = 0; tx < areaW; tx++) {
        int cx = tx / World.CHUNK_SIZE;
        int cy = ty / World.CHUNK_SIZE;
        Chunk c = world.peekChunk(cx, cy);
        if (c == null) continue;
        int lx = tx - cx * World.CHUNK_SIZE;
        int ly = ty - cy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;

        if (c.layers.roadMask[idx] == 0) continue;

        // Fix height: road tiles must be flat (no cliff shadows).
        c.layers.heightLevel[idx] = 0;

        // Compute 4-neighbor road adjacency.
        boolean n = peekRoad(world, tx, ty + 1);
        boolean e = peekRoad(world, tx + 1, ty);
        boolean s = peekRoad(world, tx, ty - 1);
        boolean w = peekRoad(world, tx - 1, ty);
        c.layers.roadMask4[idx] = (byte) ((n ? 1 : 0) | (e ? 2 : 0) | (s ? 4 : 0) | (w ? 8 : 0));
      }
    }
  }

  private static boolean peekRoad(World world, int tx, int ty) {
    if (world == null) return false;
    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    Chunk c = world.peekChunk(cx, cy);
    if (c == null) return false;
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);
    return c.layers.roadMask[lx + ly * World.CHUNK_SIZE] != 0;
  }
}

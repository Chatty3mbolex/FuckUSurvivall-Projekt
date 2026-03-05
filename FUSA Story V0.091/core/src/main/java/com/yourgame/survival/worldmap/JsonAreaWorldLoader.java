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

    // 5) Spawn area-local content.
    if (WorldMapRuntime.T_HOME.equals(templateId)) {
      beautifyHome(gs, w, h, areaSeed);
    }
    if (WorldMapRuntime.T_FOREST.equals(templateId)) {
      spawnForestTreesAndDeer(gs, w, h, areaSeed);
    }

    // 6) Move player to spawn marker (if present).
    JsonValue markers = root.get("markers");
    if (markers != null) {
      JsonValue sp = markers.get("playerSpawn");
      if (sp != null) {
        int tx = sp.getInt("x", 64);
        int ty = sp.getInt("y", 64);
        gs.areaSetPlayerWorldPos((tx + 0.5f) * World.TILE_WORLD, (ty + 0.5f) * World.TILE_WORLD);
      }
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

  /** Deterministic area seed derived from worldSeed + templateId. */
  private static long areaSeed(GameScreen gs, String templateId) {
    long s = gs.areaGetWorldSeed();
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
          }
        }
      }
    }

    // 3) Apply rectangle fills.
    JsonValue fills = (ground != null) ? ground.get("fills") : null;
    if (fills != null) {
      for (JsonValue f = fills.child; f != null; f = f.next) {
        JsonValue r = f.get("rect");
        if (r == null) continue;
        int x0 = r.getInt("x", 0);
        int y0 = r.getInt("y", 0);
        int w = r.getInt("w", 0);
        int h = r.getInt("h", 0);
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
    int r2 = r * r;
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
}

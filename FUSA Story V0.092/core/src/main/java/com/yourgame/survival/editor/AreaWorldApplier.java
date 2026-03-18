package com.yourgame.survival.editor;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.World;

/**
 * Applies an authored .area.json file onto a preview World (tiles only).
 *
 * This is editor-side glue so WorldEditorScreen can evolve into an AreaEditor
 * without pulling in BiomeSystem editor logic.
 */
public final class AreaWorldApplier {
  private AreaWorldApplier() {}

  public static JsonValue parse(String jsonText) {
    if (jsonText == null) return null;
    try {
      return new JsonReader().parse(jsonText);
    } catch (Throwable t) {
      return null;
    }
  }

  public static void apply(World world, JsonValue area) {
    if (world == null || area == null) return;

    JsonValue size = area.get("size");
    int areaW = (size != null) ? size.getInt("w", 0) : 0;
    int areaH = (size != null) ? size.getInt("h", 0) : 0;
    if (areaW <= 0 || areaH <= 0) return;

    JsonValue layers = area.get("layers");
    JsonValue ground = (layers != null) ? layers.get("ground") : null;
    JsonValue road = (layers != null) ? layers.get("road") : null;

    int defaultId = (ground != null) ? ground.getInt("defaultId", 0) : 0;

    ensureChunks(world, areaW, areaH);
    fillDefault(world, areaW, areaH, defaultId);
    applyGroundFills(world, areaW, areaH, defaultId, ground);
    applyRoadFills(world, areaW, areaH, road);
  }

  private static void ensureChunks(World world, int areaW, int areaH) {
    int maxTx = Math.max(0, areaW - 1);
    int maxTy = Math.max(0, areaH - 1);
    int maxCx = Math.max(0, maxTx / World.CHUNK_SIZE);
    int maxCy = Math.max(0, maxTy / World.CHUNK_SIZE);
    for (int cy = 0; cy <= maxCy; cy++) {
      for (int cx = 0; cx <= maxCx; cx++) {
        world.chunk(cx, cy);
      }
    }
  }

  private static void fillDefault(World world, int areaW, int areaH, int defaultId) {
    int maxTx = Math.max(0, areaW - 1);
    int maxTy = Math.max(0, areaH - 1);
    int maxCx = Math.max(0, maxTx / World.CHUNK_SIZE);
    int maxCy = Math.max(0, maxTy / World.CHUNK_SIZE);

    for (int cy = 0; cy <= maxCy; cy++) {
      for (int cx = 0; cx <= maxCx; cx++) {
        Chunk c = world.chunk(cx, cy);
        int size = c.layers.size;
        for (int ly = 0; ly < size; ly++) {
          int ty = cy * World.CHUNK_SIZE + ly;
          if (ty >= areaH) continue;
          for (int lx = 0; lx < size; lx++) {
            int tx = cx * World.CHUNK_SIZE + lx;
            if (tx >= areaW) continue;
            int idx = lx + ly * size;

            c.layers.groundId[idx] = (short) (defaultId & 0xFF);
            c.layers.waterMask[idx] = 0;
            c.layers.collisionMask[idx] = 0;
            c.layers.roadMask[idx] = 0;
            c.layers.roadMask4[idx] = 0;

            // Keep editor preview neutral.
            c.layers.overlayId[idx] = 0;
            c.layers.decoId[idx] = 0;
            c.layers.decoVar[idx] = 0;

            c.layers.shoreMask4[idx] = 0;
            c.layers.grassCornerMask16[idx] = 0;
            c.layers.dirtCornerMask16[idx] = 0;
            c.layers.sandCornerMask16[idx] = 0;
            c.layers.rockCornerMask16[idx] = 0;
            c.layers.snowCornerMask16[idx] = 0;
            c.layers.heightLevel[idx] = 0;
          }
        }
      }
    }
  }

  private static void applyGroundFills(World world, int areaW, int areaH, int defaultId, JsonValue ground) {
    if (ground == null) return;
    JsonValue fills = ground.get("fills");
    if (fills == null) return;

    for (JsonValue f = fills.child; f != null; f = f.next) {
      int x0 = f.getInt("x", 0);
      int y0 = f.getInt("y", 0);
      int w = f.getInt("w", 0);
      int h = f.getInt("h", 0);
      int id = f.getInt("id", defaultId);
      fillRectGround(world, areaW, areaH, x0, y0, w, h, id);
    }
  }

  private static void applyRoadFills(World world, int areaW, int areaH, JsonValue road) {
    if (road == null) return;
    JsonValue fills = road.get("fills");
    if (fills == null) return;

    for (JsonValue f = fills.child; f != null; f = f.next) {
      int x0 = f.getInt("x", 0);
      int y0 = f.getInt("y", 0);
      int w = f.getInt("w", 0);
      int h = f.getInt("h", 0);
      int v = f.getInt("v", 1);
      if (v == 0) continue;
      fillRectRoad(world, areaW, areaH, x0, y0, w, h);
    }
  }

  private static void fillRectGround(World world, int areaW, int areaH, int x0, int y0, int w, int h, int id) {
    int x1 = Math.min(areaW, x0 + Math.max(0, w));
    int y1 = Math.min(areaH, y0 + Math.max(0, h));
    for (int y = Math.max(0, y0); y < y1; y++) {
      for (int x = Math.max(0, x0); x < x1; x++) {
        setGround(world, areaW, areaH, x, y, id);
      }
    }
  }

  private static void fillRectRoad(World world, int areaW, int areaH, int x0, int y0, int w, int h) {
    int x1 = Math.min(areaW, x0 + Math.max(0, w));
    int y1 = Math.min(areaH, y0 + Math.max(0, h));
    for (int y = Math.max(0, y0); y < y1; y++) {
      for (int x = Math.max(0, x0); x < x1; x++) {
        setRoad(world, areaW, areaH, x, y);
      }
    }
  }

  private static void setGround(World world, int areaW, int areaH, int tx, int ty, int id) {
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    Chunk c = world.chunk(cx, cy);
    int idx = lx + ly * c.layers.size;
    c.layers.groundId[idx] = (short) (id & 0xFF);
    c.layers.waterMask[idx] = 0;
    c.layers.collisionMask[idx] = 0;
  }

  private static void setRoad(World world, int areaW, int areaH, int tx, int ty) {
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    Chunk c = world.chunk(cx, cy);
    int idx = lx + ly * c.layers.size;
    c.layers.roadMask[idx] = 1;
    // Minimal: show a generic road tile until we implement proper adjacency baking.
    c.layers.roadMask4[idx] = (byte) 15;
  }
}


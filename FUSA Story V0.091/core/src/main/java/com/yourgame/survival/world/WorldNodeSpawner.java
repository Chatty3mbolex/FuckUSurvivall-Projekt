package com.yourgame.survival.world;

import com.yourgame.survival.biome.BiomeSystem;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.entity.EntityType;
import com.yourgame.survival.util.LongKeySet;

import java.util.ArrayList;

/**
 * Deterministic, seed-based spawner for harvest nodes (trees/rocks/ores) + POI templates.
 * Spawns per chunk once (runtime cache), and consults WorldNodes.removed to avoid respawn after harvest.
 */
public final class WorldNodeSpawner {
  private final World world;
  private final long seed;
  private final LongKeySet spawnedChunks = new LongKeySet(4096);

  // Kept to preserve the "feel" of the old spawner jitter (it was based on step=8).
  private static final int CELL_TILES = 8;

  public WorldNodeSpawner(World world, long seed) {
    this.world = world;
    this.seed = seed;
  }

  public void resetSpawnedChunks() {
    spawnedChunks.clear();
  }

  public void ensureAround(Entities es, WorldNodes nodes, float px, float py, int radiusChunks) {
    int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);
    int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);

    for (int dy = -radiusChunks; dy <= radiusChunks; dy++) {
      for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
        int cx = ccx + dx;
        int cy = ccy + dy;
        long ck = com.yourgame.survival.util.PackCoord.key(cx, cy);
        if (spawnedChunks.contains(ck)) continue;

        // Never force chunk generation here; request it and only spawn when it is actually loaded.
        Chunk peek = world.peekChunk(cx, cy);
        if (peek == null) {
          world.requestChunk(cx, cy);
          continue;
        }

        if (spawnChunk(es, nodes, cx, cy, peek)) {
          spawnedChunks.add(ck);
        }
      }
    }
  }

  private boolean spawnChunk(Entities es, WorldNodes nodes, int cx, int cy, Chunk chunk) {

    int baseTx = cx * World.CHUNK_SIZE;
    int baseTy = cy * World.CHUNK_SIZE;

    // Determine chunk's biome from center tile.
    int midTx = baseTx + (World.CHUNK_SIZE / 2);
    int midTy = baseTy + (World.CHUNK_SIZE / 2);
    Biome b = Biome.byId(world.biomeIdAtTile(midTx, midTy));

    BiomeSystem bs = world.biomes();
    BiomeSystem.BiomeDef bd = bs.def(b);
    int edgeW = Math.max(0, Math.min(World.CHUNK_SIZE / 2, bd.edgeWidthTiles));
    TileLayers layers = chunk.layers;

    // Chunk field averages for density shaping (deterministic).
    float avgVeg01 = 0f;
    float avgRock01 = 0f;
    int landN = 0;
    for (int i = 0; i < World.CHUNK_SIZE * World.CHUNK_SIZE; i++) {
      if (layers.waterMask[i] != 0) continue;
      avgVeg01 += field01(layers.vegetation[i]);
      avgRock01 += field01(layers.rockiness[i]);
      landN++;
    }
    if (landN > 0) {
      avgVeg01 /= landN;
      avgRock01 /= landN;
    }

    // 1) Count-based spawn rules (zone-aware via constraints in the rule).
    ArrayList<BiomeSystem.SpawnRule> rules = bs.spawnRulesFor(b);
    for (int ri = 0; ri < rules.size(); ri++) {
      BiomeSystem.SpawnRule r = rules.get(ri);
      EntityType t = mapType(r.type);
      if (t == null) continue;

      int count = countForRule(r, cx, cy, ri);
      // Density shaping: baseline from rules, steered by world fields.
      if (t == EntityType.NODE_TREE) {
        float f = 0.65f + avgVeg01 * 0.9f;
        if (r.allowTreeOverlap) f *= 1.15f;
        count = Math.max(0, Math.round(count * f));
      } else if (t == EntityType.NODE_BUSH) {
        float f = 0.60f + avgVeg01 * 1.0f;
        count = Math.max(0, Math.round(count * f));
      } else if (t == EntityType.NODE_ROCK || t == EntityType.NODE_ORE_IRON) {
        float f = 0.70f + avgRock01 * 0.8f;
        count = Math.max(0, Math.round(count * f));
      }
      for (int k = 0; k < count; k++) {
        // deterministic placement attempts
        boolean placed = false;
        for (int att = 0; att < 24 && !placed; att++) {
          long h = mix64(seed ^ (long) cx * 0x9E3779B97F4A7C15L ^ (long) cy * 0xC2B2AE3D27D4EB4FL ^ (ri * 0xD6E8FEB86659FD93L) ^ (k * 0x94D049BB133111EBL) ^ att);

          ZonePick zp = pickZoneTile(bs, b, r, bd, edgeW, h);
          if (zp == null) continue;
          int tx = baseTx + zp.lx;
          int ty = baseTy + zp.ly;

          // Optional EDGE neighbor constraint
          if (r.neighborBiome != null && ("EDGE".equalsIgnoreCase(r.zone) || (r.zone != null && r.zone.toUpperCase().startsWith("EDGE")))) {
            String want = r.neighborBiome.trim();
            if (!want.equals("*")) {
              Biome nb = neighborBiomeForEdge(tx, ty, baseTx, baseTy, zp.side);
              if (nb == null || !nb.name().equalsIgnoreCase(want)) continue;
            }
          }

          // ROAD / WATERLINE zone filters (tile-local, cheap)
          int lidx = zp.lx + zp.ly * World.CHUNK_SIZE;
          if ("ROAD".equalsIgnoreCase(r.zone)) {
            if (layers.roadMask[lidx] == 0) continue;
          }
          if ("WATERLINE".equalsIgnoreCase(r.zone)) {
            if (layers.waterMask[lidx] != 0) continue;
            if (layers.shoreMask4[lidx] == 0) continue;
          }

          // Zone filter (minimal v1): WATER means only water; otherwise land.
          boolean water = (layers.waterMask[lidx] != 0);
          if ("WATER".equalsIgnoreCase(r.zone)) {
            if (!water) continue;
          } else {
            if (water) continue;
            if (layers.collisionMask[lidx] != 0) continue;
          }

          if (!passesFieldFilters(t, r, layers, lidx)) continue;

          if (nodes.isRemoved(t, tx, ty)) continue;

          float wx = (tx + 0.5f) * World.TILE_WORLD;
          float wy = (ty + 0.5f) * World.TILE_WORLD;

          // Trees: off-grid jitter (visual) while keeping anchor tile for removal key.
          if (t == EntityType.NODE_TREE) {
            long h2 = mix64(h ^ 0xD6E8FEB86659FD93L);
            float rx = ((h2 >>> 40) & 0xFFFFFF) / (float) 0x1000000; // 0..1
            float ry = ((h2 >>> 16) & 0xFFFFFF) / (float) 0x1000000;

            float maxJx = World.TILE_WORLD * (CELL_TILES * clamp01(r.treeJitterX));
            float maxJy = World.TILE_WORLD * (CELL_TILES * clamp01(r.treeJitterY));
            wx += (rx * 2f - 1f) * maxJx;
            wy += (ry * 2f - 1f) * maxJy;
          }

          // Bushes: small off-grid jitter (visual) while keeping anchor tile for removal key.
          if (t == EntityType.NODE_BUSH) {
            long h2 = mix64(h ^ 0xB4B82E39E5D1F2A7L);
            float rx = ((h2 >>> 40) & 0xFFFFFF) / (float) 0x1000000;
            float ry = ((h2 >>> 16) & 0xFFFFFF) / (float) 0x1000000;
            float maxJ = World.TILE_WORLD * (2.0f * clamp01(r.treeJitterX));
            wx += (rx * 2f - 1f) * maxJ;
            wy += (ry * 2f - 1f) * maxJ;
          }

          // Collision/stacking: keep old behavior
          if (hasNearbyNode(es, wx, wy, 10f, t, r.allowTreeOverlap)) continue;

          es.spawn(t, wx, wy);
          placed = true;
        }
      }
    }

    // 2) POI templates (deterministic). Roads are generated independently via BiomeSystem.
    ArrayList<BiomeSystem.PoiSpawn> pois = bs.computePoisForChunk(cx, cy, b);
    for (int i = 0; i < pois.size(); i++) {
      BiomeSystem.PoiSpawn p = pois.get(i);

      float wx = (p.tx + 0.5f) * World.TILE_WORLD;
      float wy = (p.ty + 0.5f) * World.TILE_WORLD;

      if ("TREASURE_CHEST".equalsIgnoreCase(p.key)) {
        // Chest: no road requirement.
        // Requirement (FUSA): where a chest stands, there should be NO automatic road to it.
        // The road planner only targets POIs with requiresRoad=true, and TREASURE_CHEST is configured
        // with requiresRoad=false + connectRadius=0 in assets/config/biomes.json.
        // As an extra guardrail, we also avoid placing the chest on an already-generated road tile.
        int lidx = (p.tx - baseTx) + (p.ty - baseTy) * World.CHUNK_SIZE;
        boolean onRoad = (lidx >= 0 && lidx < layers.roadMask.length && layers.roadMask[lidx] != 0);
        if (!onRoad) {
          if (!hasNearbyAny(es, wx, wy, 24f, EntityType.BUILD_CHEST)) {
            es.spawn(EntityType.BUILD_CHEST, wx, wy);
          }
        }
      } else if ("OLD_MINE".equalsIgnoreCase(p.key)) {
        // Mine: spawn a small ore/rock cluster plus a chest.
        spawnMineCluster(es, nodes, p.tx, p.ty);
      }
    }

    return true;
  }

  private void spawnMineCluster(Entities es, WorldNodes nodes, int tx0, int ty0) {
    // center chest
    float cx = (tx0 + 0.5f) * World.TILE_WORLD;
    float cy = (ty0 + 0.5f) * World.TILE_WORLD;

    if (!hasNearbyAny(es, cx, cy, 24f, EntityType.BUILD_CHEST)) {
      es.spawn(EntityType.BUILD_CHEST, cx, cy);
    }

    // deterministic small ring
    for (int i = 0; i < 6; i++) {
      long h = mix64(seed ^ 0xA55A5AA55AA55AA5L ^ (long) tx0 * 0x9E3779B97F4A7C15L ^ (long) ty0 * 0xC2B2AE3D27D4EB4FL ^ i);
      float ang = ((h >>> 40) & 0xFFFFFF) / (float) 0x1000000 * 6.283185f;
      float rad = 18f + (((h >>> 16) & 0xFFFF) / 65535f) * 22f;

      float wx = cx + (float) Math.cos(ang) * rad;
      float wy = cy + (float) Math.sin(ang) * rad;

      int tx = (int) Math.floor(wx / World.TILE_WORLD);
      int ty = (int) Math.floor(wy / World.TILE_WORLD);

      if (world.isWaterAtTile(tx, ty) || world.isBlockedAtTile(tx, ty)) continue;

      EntityType t = (i % 2 == 0) ? EntityType.NODE_ORE_IRON : EntityType.NODE_ROCK;
      if (nodes.isRemoved(t, tx, ty)) continue;

      if (hasNearbyNode(es, wx, wy, 12f, t, false)) continue;
      es.spawn(t, wx, wy);
    }
  }

  private static int countForRule(BiomeSystem.SpawnRule r, int cx, int cy, int ri) {
    int min = Math.max(0, r.minCount);
    int max = Math.max(min, r.maxCount);

    if (max == min) return min;

    long h = mix64(0xBAD0C0DECAFEBABEL ^ (long) cx * 0x9E3779B97F4A7C15L ^ (long) cy * 0xC2B2AE3D27D4EB4FL ^ (ri * 0xD6E8FEB86659FD93L));
    int span = max - min + 1;

    // Bias distribution for TREE counts so that high values are rarer, but still possible.
    // This matches the design request: min=20 max=64 in forest, where 64 should be unlikely but non-zero.
    if (r != null && r.type != null && r.type.equalsIgnoreCase("TREE") && span > 1) {
      // u in [0..1)
      float u = ((h >>> 40) & 0xFFFFFFL) / (float) 0x1000000;
      // square biases towards 0
      float b = u * u;
      int off = Math.min(span - 1, (int) Math.floor(b * span));
      return min + off;
    }

    return min + (int) (Math.floorMod(h, span));
  }

  private static EntityType mapType(String s) {
    if (s == null) return null;
    return switch (s.toUpperCase()) {
      case "TREE" -> EntityType.NODE_TREE;
      case "ROCK" -> EntityType.NODE_ROCK;
      case "IRON" -> EntityType.NODE_ORE_IRON;
      case "BUSH" -> EntityType.NODE_BUSH;
      case "FISH" -> EntityType.NODE_FISH_SPOT;
      default -> null;
    };
  }

  /**
   * Pick a deterministic tile inside the requested zone.
   * This implements the editor-driven "zone density" concept (min/max counts per zone).
   */
  private static ZonePick pickZoneTile(BiomeSystem bs, Biome biome, BiomeSystem.SpawnRule r, BiomeSystem.BiomeDef bd, int edgeW, long h) {
    int size = World.CHUNK_SIZE;

    String zone = (r.zone == null) ? "CORE" : r.zone.trim();
    String zUp = zone.toUpperCase();

    // MASK:<name> (painted zone mask)
    if (zUp.startsWith("MASK:")) {
      String name = zone.substring("MASK:".length()).trim();
      if (name.isEmpty() || bs == null || biome == null) return null;
      // Try a few deterministic candidates.
      for (int i = 0; i < 64; i++) {
        long hh = mix64(h ^ (i * 0x9E3779B97F4A7C15L));
        int lx = (int) Math.floorMod(hh, size);
        int ly = (int) Math.floorMod(hh >>> 32, size);
        if (bs.zoneMaskAt(biome, name, lx, ly)) {
          return new ZonePick(lx, ly, '*');
        }
      }
      return null;
    }

    // CUSTOM:<name>
    if (zUp.startsWith("CUSTOM:")) {
      String name = zone.substring("CUSTOM:".length()).trim();
      for (int i = 0; i < bd.customZones.size(); i++) {
        BiomeSystem.CustomZone z = bd.customZones.get(i);
        if (z == null || z.name == null) continue;
        if (!z.name.equalsIgnoreCase(name)) continue;

        int w = Math.max(0, z.w);
        int hh = Math.max(0, z.h);
        if (w <= 0 || hh <= 0) return null;

        int lx = clamp(z.x, 0, size - 1) + (int) Math.floorMod(h, w);
        int ly = clamp(z.y, 0, size - 1) + (int) Math.floorMod(h >>> 32, hh);
        lx = clamp(lx, 0, size - 1);
        ly = clamp(ly, 0, size - 1);
        return new ZonePick(lx, ly, '*');
      }
      return null;
    }

    // EDGE zone
    if ("EDGE".equals(zUp) || zUp.startsWith("EDGE")) {
      if (edgeW <= 0) return null;

      char side = '*';
      if (r.side != null && !r.side.isEmpty()) {
        side = Character.toUpperCase(r.side.trim().charAt(0));
      }
      if (side == '*') {
        int sel = (int) ((h >>> 60) & 3);
        side = (sel == 0) ? 'N' : (sel == 1) ? 'E' : (sel == 2) ? 'S' : 'W';
      }

      int x0, y0, w, hh;
      switch (side) {
        case 'N' -> { x0 = 0; y0 = size - edgeW; w = size; hh = edgeW; }
        case 'E' -> { x0 = size - edgeW; y0 = 0; w = edgeW; hh = size; }
        case 'S' -> { x0 = 0; y0 = 0; w = size; hh = edgeW; }
        case 'W' -> { x0 = 0; y0 = 0; w = edgeW; hh = size; }
        default -> { x0 = 0; y0 = 0; w = size; hh = size; }
      }

      if (w <= 0 || hh <= 0) return null;
      int lx = x0 + (int) Math.floorMod(h, w);
      int ly = y0 + (int) Math.floorMod(h >>> 32, hh);
      lx = clamp(lx, 0, size - 1);
      ly = clamp(ly, 0, size - 1);
      return new ZonePick(lx, ly, side);
    }

    // CORE zone
    if ("CORE".equals(zUp)) {
      int x0 = edgeW;
      int y0 = edgeW;
      int w = size - edgeW * 2;
      int hh = size - edgeW * 2;
      if (w <= 0 || hh <= 0) { x0 = 0; y0 = 0; w = size; hh = size; }

      int lx = x0 + (int) Math.floorMod(h, w);
      int ly = y0 + (int) Math.floorMod(h >>> 32, hh);
      lx = clamp(lx, 0, size - 1);
      ly = clamp(ly, 0, size - 1);
      return new ZonePick(lx, ly, '*');
    }

    // Default: any tile (WATER/ROAD/WATERLINE are filtered later).
    int lx = (int) Math.floorMod(h, size);
    int ly = (int) Math.floorMod(h >>> 32, size);
    return new ZonePick(lx, ly, '*');
  }

  private Biome neighborBiomeForEdge(int tx, int ty, int baseTx, int baseTy, char side) {
    int size = World.CHUNK_SIZE;
    int ntx = tx;
    int nty = ty;
    switch (side) {
      case 'N' -> nty = baseTy + size;
      case 'E' -> ntx = baseTx + size;
      case 'S' -> nty = baseTy - 1;
      case 'W' -> ntx = baseTx - 1;
      default -> {
        return null;
      }
    }
    return Biome.byId(world.biomeIdAtTile(ntx, nty));
  }

  private static final class ZonePick {
    final int lx;
    final int ly;
    final char side;

    ZonePick(int lx, int ly, char side) {
      this.lx = lx;
      this.ly = ly;
      this.side = side;
    }
  }

  private static int clamp(int v, int lo, int hi) {
    return (v < lo) ? lo : (v > hi) ? hi : v;
  }

  private static boolean hasNearbyNode(Entities es, float wx, float wy, float r, EntityType spawning, boolean allowTreeOverlap) {
    float r2 = r * r;
    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      EntityType t = es.type[i];

      // Only check against world nodes/buildings that matter for spacing.
      if (t != EntityType.NODE_TREE && t != EntityType.NODE_ROCK && t != EntityType.NODE_ORE_IRON && t != EntityType.NODE_STUMP && t != EntityType.NODE_BUSH && t != EntityType.NODE_FISH_SPOT && t != EntityType.BUILD_CHEST)
        continue;

      if (allowTreeOverlap && spawning == EntityType.NODE_TREE && t == EntityType.NODE_TREE) continue;

      float dx = es.x[i] - wx;
      float dy = es.y[i] - wy;
      if (dx * dx + dy * dy <= r2) return true;
    }
    return false;
  }

  private static boolean hasNearbyAny(Entities es, float wx, float wy, float r, EntityType tWanted) {
    float r2 = r * r;
    for (int i = 0; i < Entities.MAX; i++) {
      if (!es.alive[i]) continue;
      if (es.type[i] != tWanted) continue;
      float dx = es.x[i] - wx;
      float dy = es.y[i] - wy;
      if (dx * dx + dy * dy <= r2) return true;
    }
    return false;
  }


  private static float field01(byte b) {
    return (b & 0xFF) / 255f;
  }

  private static boolean passesFieldFilters(EntityType t, BiomeSystem.SpawnRule r, TileLayers layers, int lidx) {
    float veg = field01(layers.vegetation[lidx]);
    float rock = field01(layers.rockiness[lidx]);
    float path = field01(layers.pathField[lidx]);
    float h = field01(layers.height[lidx]);

    if (layers.waterMask[lidx] != 0) return false;
    if (layers.collisionMask[lidx] != 0) return false;

    return switch (t) {
      case NODE_TREE -> (veg >= 0.35f) && (rock <= 0.75f) && (path <= 0.88f) && (h <= 0.92f);
      case NODE_BUSH -> (veg >= 0.40f) && (rock <= 0.70f) && (path <= 0.82f);
      case NODE_ROCK, NODE_ORE_IRON -> (rock >= 0.45f) && (h >= 0.40f);
      default -> true;
    };
  }
  private static float clamp01(float v) {
    if (v < 0f) return 0f;
    if (v > 1f) return 1f;
    return v;
  }

  private static long mix64(long z) {
    z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
    z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
    return z ^ (z >>> 33);
  }

  // Nicht fertiges Feature:
  // seedForRule(...) + computeZoneAreaTiles(...)
  // (Unused right now; keeping as commented code for later.)
  /*
  private static long seedForRule(BiomeSystem.SpawnRule r, int cx, int cy, int ri) {
    long h = 0x9E3779B97F4A7C15L;
    h ^= (long) cx * 0xBF58476D1CE4E5B9L;
    h ^= (long) cy * 0x94D049BB133111EBL;
    h ^= (long) ri * 0xD6E8FEB86659FD93L;
    if (r != null && r.type != null) h ^= r.type.hashCode() * 0x9E3779B97F4A7C15L;
    if (r != null && r.zone != null) h ^= r.zone.hashCode() * 0xBF58476D1CE4E5B9L;
    return h;
  }

  private static int computeZoneAreaTiles(BiomeSystem bs, Biome biome, BiomeSystem.BiomeDef bd, TileLayers layers, int edgeW, String zone) {
    int size = World.CHUNK_SIZE;
    if (zone == null) zone = "CORE";
    String z = zone.trim();
    String up = z.toUpperCase();
    int area = 0;

    // WATER: count water tiles
    if (up.equals("WATER")) {
      for (int i = 0; i < size * size; i++) if (layers.waterMask[i] != 0) area++;
      return area;
    }

    // MASK:<name>: count alpha pixels in mask
    if (up.startsWith("MASK:")) {
      String name = z.substring("MASK:".length()).trim();
      if (name.isEmpty() || bs == null || biome == null) return 0;
      for (int ly = 0; ly < size; ly++) {
        for (int lx = 0; lx < size; lx++) {
          if (bs.zoneMaskAt(biome, name, lx, ly)) area++;
        }
      }
      return area;
    }

    // EDGE / CORE: band area
    if (up.equals("EDGE") || up.equals("CORE")) {
      for (int ly = 0; ly < size; ly++) {
        for (int lx = 0; lx < size; lx++) {
          boolean isEdge = (lx < edgeW) || (ly < edgeW) || (lx >= size - edgeW) || (ly >= size - edgeW);
          if (up.equals("EDGE")) { if (isEdge) area++; }
          else { if (!isEdge) area++; }
        }
      }
      return area;
    }

    // CUSTOM:<name> rectangles
    if (up.startsWith("CUSTOM:")) {
      String name = z.substring("CUSTOM:".length()).trim();
      if (name.isEmpty()) return 0;
      for (int i = 0; i < bd.customZones.size(); i++) {
        BiomeSystem.CustomZone cz = bd.customZones.get(i);
        if (cz == null || cz.name == null) continue;
        if (!cz.name.equalsIgnoreCase(name)) continue;
        int x0 = Math.max(0, cz.x), y0 = Math.max(0, cz.y);
        int x1 = Math.min(size, cz.x + cz.w), y1 = Math.min(size, cz.y + cz.h);
        if (x1 > x0 && y1 > y0) area += (x1 - x0) * (y1 - y0);
      }
      return area;
    }

    // fallback: whole chunk
    return size * size;
  }
  */

}

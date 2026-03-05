package com.yourgame.survival.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.TransitionRules;
import com.yourgame.survival.world.World;

/** Block C: draws chunks as tiles from the TextureAtlas (ground-only). */
public final class ChunkRenderer {
  private final TilesetRegions tiles;
  // Nicht fertiges Feature: used only for safe neighbor sampling in blob-mask calculation (currently unused).
  // private World world;

  public ChunkRenderer(TilesetRegions tiles) {
    this.tiles = tiles;
  }

  public void draw(SpriteBatch batch, World world, float camX, float camY, int radiusChunks) {
    // Nicht fertiges Feature: // this.world = world; // (unused)
    int ccx = (int) Math.floor((camX / World.TILE_WORLD) / World.CHUNK_SIZE);
    int ccy = (int) Math.floor((camY / World.TILE_WORLD) / World.CHUNK_SIZE);

    float worldChunkW = World.CHUNK_SIZE * World.TILE_WORLD;

    for (int dy = -radiusChunks; dy <= radiusChunks; dy++) {
      for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
        int cx = ccx + dx;
        int cy = ccy + dy;
        Chunk c = world.peekChunk(cx, cy);
        if (c == null) continue;

        float x0 = cx * worldChunkW;
        float y0 = cy * worldChunkW;

        int size = c.layers.size;
        for (int ly = 0; ly < size; ly++) {
          for (int lx = 0; lx < size; lx++) {
            int idx = lx + ly * size;

            float x = x0 + lx * World.TILE_WORLD;
            float y = y0 + ly * World.TILE_WORLD;

            boolean isWater = (c.layers.waterMask[idx] != 0);

            // Base layer: WATER is the bottom-most level.
            // If this tile is water, draw ONLY water (no ground below).
            short gid = (short)(c.layers.groundId[idx] & 0xFF);
            if (isWater) {
              batch.draw(tiles.waterFill(), x, y, World.TILE_WORLD, World.TILE_WORLD);
            } else {
              // Ground
              TextureRegion ground = tiles.ground(gid);
              int rot = tiles.groundRotationDeg(gid);
              if (rot == 0) {
                batch.draw(ground, x, y, World.TILE_WORLD, World.TILE_WORLD);
              } else {
                float o = World.TILE_WORLD * 0.5f;
                batch.draw(ground, x, y, o, o, World.TILE_WORLD, World.TILE_WORLD, 1f, 1f, rot);
              }
            }

            
            // Ground transitions (baked edging)
            if (!isWater && gid != TileIds.GROUND_LAVA) {
              // Overlap-only transitions using baked corner masks (stable across chunk borders).
              // Renderer must not sample neighbor chunks.

              // Note: masks are already baked with hard transition rules, but we keep a
              // render-side guardrail too (prevents surprises if old chunks are loaded).

              if (gid != TileIds.GROUND_GRASS && TransitionRules.allowTransition(gid, TileIds.GROUND_GRASS)) {
                int gm = c.layers.grassCornerMask16[idx] & 0xFF;
                if (gm != 0) batch.draw(tiles.edgeGrass(gm), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }

              if (gid != TileIds.GROUND_DIRT && TransitionRules.allowTransition(gid, TileIds.GROUND_DIRT)) {
                int dm = c.layers.dirtCornerMask16[idx] & 0xFF;
                if (dm != 0) batch.draw(tiles.edgeDirt(dm), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }

              if (gid != TileIds.GROUND_SAND && TransitionRules.allowTransition(gid, TileIds.GROUND_SAND)) {
                int sm = c.layers.sandCornerMask16[idx] & 0xFF;
                if (sm != 0) batch.draw(tiles.edgeSand(sm), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }

              if (gid != TileIds.GROUND_ROCK && TransitionRules.allowTransition(gid, TileIds.GROUND_ROCK)) {
                int rm = c.layers.rockCornerMask16[idx] & 0xFF;
                if (rm != 0) batch.draw(tiles.edgeRock(rm), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }

              if (gid != TileIds.GROUND_SNOW && TransitionRules.allowTransition(gid, TileIds.GROUND_SNOW)) {
                int sn = c.layers.snowCornerMask16[idx] & 0xFF;
                if (sn != 0) batch.draw(tiles.edgeSnow(sn), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }
            }
// Water overlay is already drawn as the base layer for water tiles.
            if (!isWater) {
              // Shore autotile overlay: draw only on land tiles adjacent to water.
              int sm = c.layers.shoreMask4[idx] & 0xff;
              if (sm != 0) {
                batch.draw(tiles.shore(sm), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }

              // Road autotile: adjacency is precomputed in the generator.
              if (c.layers.roadMask[idx] != 0) {
                int rm = c.layers.roadMask4[idx] & 0xff;
                batch.draw(tiles.road(rm), x, y, World.TILE_WORLD, World.TILE_WORLD);
              }

              // WorldGen deco (purely visual)
              if (c.layers.decoId[idx] != 0) {
                TextureRegion d = tiles.deco(c.layers.decoId[idx], c.layers.decoVar[idx]);
                if (d != null) {
                  // Keep baked decos aligned to their tile (non-jittered).
                  batch.draw(d, x, y, World.TILE_WORLD, World.TILE_WORLD);
                }
              }

              // Terraced cliffs (placeholder): draw a dark wall on edges to lower neighbors.
              // This is purely visual for now. Gameplay collision will be added later.
              {
                int h0 = c.layers.heightLevel[idx] & 0xFF;
                if (h0 > 0) {
                  // sample 4-neighbors (cross-chunk via World peek)
                  int tx = cx * World.CHUNK_SIZE + lx;
                  int ty = cy * World.CHUNK_SIZE + ly;

                  int hn = heightAtTilePeek(world, tx, ty + 1, h0);
                  int he = heightAtTilePeek(world, tx + 1, ty, h0);
                  int hs = heightAtTilePeek(world, tx, ty - 1, h0);
                  int hw = heightAtTilePeek(world, tx - 1, ty, h0);

                  float wall = World.TILE_WORLD * 0.22f;
                  float k = 0.12f + 0.10f * Math.min(4, h0);
                  batch.setColor(0f, 0f, 0f, k);
                  if (hn < h0) batch.draw(tiles.waterFill(), x, y + World.TILE_WORLD - wall, World.TILE_WORLD, wall);
                  if (hs < h0) batch.draw(tiles.waterFill(), x, y, World.TILE_WORLD, wall);
                  if (he < h0) batch.draw(tiles.waterFill(), x + World.TILE_WORLD - wall, y, wall, World.TILE_WORLD);
                  if (hw < h0) batch.draw(tiles.waterFill(), x, y, wall, World.TILE_WORLD);
                  batch.setColor(1f, 1f, 1f, 1f);
                }
              }

              // (Procedural grass jitter is drawn once per chunk after the tile loop.)
            }
          }
        }

        // Procedural grass jitter (free-standing, non-grid): scatter a few tufts per chunk.
        // This is render-time only (no baked layer), deterministic from seed+chunk.
        {
          // Tune: how many attempts per chunk.
          final int attempts = 140;
          for (int i = 0; i < attempts; i++) {
            // Pick a random world position inside the chunk (in tile coordinates + fractional).
            float fx = hash01(0xA0B1C2D3E4F50607L, cx * 997 + i * 13, cy * 991 + i * 17);
            float fy = hash01(0x1021324354657687L, cx * 991 + i * 19, cy * 997 + i * 23);

            int tx = cx * World.CHUNK_SIZE + (int) (fx * World.CHUNK_SIZE);
            int ty = cy * World.CHUNK_SIZE + (int) (fy * World.CHUNK_SIZE);

            int lx2 = tx - cx * World.CHUNK_SIZE;
            int ly2 = ty - cy * World.CHUNK_SIZE;
            if (lx2 < 0 || ly2 < 0 || lx2 >= World.CHUNK_SIZE || ly2 >= World.CHUNK_SIZE) continue;
            int idx2 = lx2 + ly2 * World.CHUNK_SIZE;

            // Only on grass tiles, avoid shore band and roads.
            if ((c.layers.groundId[idx2] & 0xFF) != TileIds.GROUND_GRASS) continue;
            int wd2 = c.layers.waterDist[idx2] & 0xFF;
            if (wd2 == 0 || wd2 <= 2) continue;
            if (c.layers.roadMask[idx2] != 0) continue;

            // Density based on vegetation (saftig but still gated).
            int veg2 = c.layers.vegetation[idx2] & 0xFF;
            float h = hash01(0xCAFEBABE11223344L, tx, ty);
            if (veg2 < 70 || h < 0.55f) continue;

            // Choose variant.
            int var = (int) (hash01(0xABCDEF1122334455L, tx, ty) * 4f) & 3;
            TextureRegion tuft = tiles.deco((byte) 1, (byte) var);
            if (tuft == null) continue;

            // World position (free): offset within tile in world units.
            float ox = hash01(0x5566778899AABBCCL, tx, ty) * World.TILE_WORLD;
            float oy = hash01(0x33445566778899AAL, tx, ty) * World.TILE_WORLD;

            float px = (tx * World.TILE_WORLD) + ox;
            float py = (ty * World.TILE_WORLD) + oy;

            // Snap to pixel grid (0.5 world units = 1 texel at 32px per tile) to avoid "cut" look.
            px = Math.round(px * 2f) / 2f;
            py = Math.round(py * 2f) / 2f;

            batch.draw(tuft, px, py, World.TILE_WORLD, World.TILE_WORLD);
          }
        }

      }
    }
  }

  // Legacy helpers: kept for compatibility with the plan, but not used in the hot draw-loop.

  /** 4-neighbor water adjacency mask for shore overlay (legacy). bit0=N, bit1=E, bit2=S, bit3=W. */
  @SuppressWarnings("unused")
  private static int shoreMask(World world, int cx, int cy, int lx, int ly) {
    int tx = cx * World.CHUNK_SIZE + lx;
    int ty = cy * World.CHUNK_SIZE + ly;

    boolean n = sampleWater(world, tx, ty + 1);
    boolean e = sampleWater(world, tx + 1, ty);
    boolean s = sampleWater(world, tx, ty - 1);
    boolean w = sampleWater(world, tx - 1, ty);

    return (n ? 1 : 0) | (e ? 2 : 0) | (s ? 4 : 0) | (w ? 8 : 0);
  }

  @SuppressWarnings("unused")
  private static int roadMask(World world, int cx, int cy, int lx, int ly) {
    int tx = cx * World.CHUNK_SIZE + lx;
    int ty = cy * World.CHUNK_SIZE + ly;

    boolean n = sampleRoad(world, tx, ty + 1);
    boolean e = sampleRoad(world, tx + 1, ty);
    boolean s = sampleRoad(world, tx, ty - 1);
    boolean w = sampleRoad(world, tx - 1, ty);

    return (n ? 1 : 0) | (e ? 2 : 0) | (s ? 4 : 0) | (w ? 8 : 0);
  }

  private static boolean sampleWater(World world, int tx, int ty) {
    // no TileSample allocation
    return world.isWaterAtTile(tx, ty);
  }

  private static boolean sampleRoad(World world, int tx, int ty) {
    // direct access avoids float conversions + keeps it deterministic at borders
    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);

    Chunk c = world.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.roadMask[idx] != 0;
  }

  /** Peek heightLevel at tile coords. Uses defaultValue if chunk is not loaded. */
  private static int heightAtTilePeek(World world, int tx, int ty, int defaultValue) {
    if (world == null) return defaultValue;

    int cx = floorDiv(tx, World.CHUNK_SIZE);
    int cy = floorDiv(ty, World.CHUNK_SIZE);
    int lx = mod(tx, World.CHUNK_SIZE);
    int ly = mod(ty, World.CHUNK_SIZE);

    Chunk c = world.peekChunk(cx, cy);
    if (c == null || c.layers == null || c.layers.heightLevel == null) return defaultValue;
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.heightLevel[idx] & 0xFF;
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

  /** Deterministic hash in [0,1) for jitter offsets. */
  private static float hash01(long seed, int x, int y) {
    long h = seed;
    h ^= (long) x * 0x9E3779B97F4A7C15L;
    h ^= (long) y * 0xC2B2AE3D27D4EB4FL;
    // mix64
    h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
    h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
    h = (h ^ (h >>> 33));
    return ((h >>> 40) & 0xFFFFFF) / (float) 0x1000000;
  }

  /**
   * Hard transition matrix (from transitions.md).
   * Returns true if a transition overlay for targetGroundId is allowed on top of baseGroundId.
   */

  public void dispose() {
    // nothing (atlas owned by TilesetRegions)
  }

  // drawMs(...) removed: pair-key transition overlays are no longer supported.

}


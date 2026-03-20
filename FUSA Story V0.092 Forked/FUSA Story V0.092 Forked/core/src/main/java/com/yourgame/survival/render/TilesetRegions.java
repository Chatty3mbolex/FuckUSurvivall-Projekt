package com.yourgame.survival.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Block C: atlas-backed tile region lookup (ground + overlays). */
public final class TilesetRegions {
  private final TextureAtlas atlas;
  Texture propsTex;
  private static final int TILE = 32;

  // Optional legacy blob terrain sheets (fallback only)
  private Texture grassDirtEdgeTex;
  private Texture dirtGrassEdgeTex;
  private final TextureRegion[] blobGrassEdgeFallback = new TextureRegion[16];
  private final TextureRegion[] blobDirtEdgeFallback = new TextureRegion[16];

  private final TextureRegion groundGrass;
  private final TextureRegion groundDirt;
  private final TextureRegion groundSand;
  private final TextureRegion groundRock;
  private final TextureRegion groundSnow;
  private final TextureRegion groundLava;

  // Configurable ground mapping
  private TextureRegion[] groundById;
  private int[] groundRotDeg;

  private final TextureRegion waterFill;
  private final TextureRegion[] shoreMask = new TextureRegion[16];
  private final TextureRegion[] roadMask = new TextureRegion[16];
  private final TextureRegion[] edgeGrass = new TextureRegion[16];
  private final TextureRegion[] edgeDirt = new TextureRegion[16];
  private final TextureRegion[] edgeSand = new TextureRegion[16];
  private final TextureRegion[] edgeRock = new TextureRegion[16];
  private final TextureRegion[] edgeSnow = new TextureRegion[16];

  public TilesetRegions() {
    this.atlas = AtlasLoader.loadPreferFs("static");

    // legacy fallback sheets (optional)
    try {
      grassDirtEdgeTex = new Texture(Gdx.files.internal("terrain/Grass_Dirt_Stripes_set.png"));
      dirtGrassEdgeTex = new Texture(Gdx.files.internal("terrain/Dirt_Grass_Stripes_set.png"));
      grassDirtEdgeTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
      dirtGrassEdgeTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
      slice4x4TL(grassDirtEdgeTex, blobGrassEdgeFallback);
      slice4x4TL(dirtGrassEdgeTex, blobDirtEdgeFallback);
    } catch (Throwable ignored) {
      grassDirtEdgeTex = dirtGrassEdgeTex = null;
      for (int i = 0; i < 16; i++) { blobGrassEdgeFallback[i] = null; blobDirtEdgeFallback[i] = null; }
    }

    // Props
    try {
      this.propsTex = new Texture(Gdx.files.internal("props.png"));
      this.propsTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    } catch (Throwable ignored) { this.propsTex = null; }

    // base ground / water must come from ATLAS (no tiles.png fallback)
    // These are only used as initial mapping entries; config may override.
    this.groundGrass = req("ts_r00_c00", 0);
    this.groundDirt  = req("ts_r00_c01", 0);
    this.groundSand  = req("ts_sand", 0);

    this.groundRock  = req("ground_rock", 0);
    this.groundSnow  = req("ground_snow", 0);
    this.groundLava  = req("ground_lava", 0);

    this.waterFill = req("water_fill", 0);
    for (int m = 0; m < 16; m++) shoreMask[m] = req("shore_mask", m);
    for (int m = 0; m < 16; m++) roadMask[m] = req("road_mask", m);
    for (int m = 0; m < 16; m++) edgeGrass[m] = req("edge_grass", m);
    for (int m = 0; m < 16; m++) edgeDirt[m]  = req("edge_dirt", m);
    for (int m = 0; m < 16; m++) edgeSand[m]  = req("edge_sand", m);
    for (int m = 0; m < 16; m++) edgeRock[m]  = req("edge_rock", m);
    for (int m = 0; m < 16; m++) edgeSnow[m]  = req("edge_snow", m);

    // Deco (4 variants each)
    for (int i = 0; i < 4; i++) decoGrassTuft[i] = req("deco_grass_tuft", i);
    for (int i = 0; i < 4; i++) decoFlower[i] = req("deco_flower", i);
    for (int i = 0; i < 4; i++) decoWaterRock[i] = req("deco_water_rock", i);

    TilesetConfig cfg = TilesetConfig.tryLoad();
    int n = (cfg != null && cfg.ground != null ? cfg.ground.length : 6);
    groundById = new TextureRegion[n];
    groundRotDeg = new int[n];
    applyConfig(cfg);
  }

  // TILESHEET / tiles.png is intentionally disabled (all tiles must come from ATLAS).

  private TextureRegion req(String name, int index) {
    TextureAtlas.AtlasRegion r = atlas.findRegion(name, index);
    if (r == null) r = atlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing atlas region: " + name + " (idx=" + index + ")");
    return r;
  }

  private void applyConfig(TilesetConfig cfg) {
    for (int i = 0; i < groundById.length; i++) {
      groundById[i] = null;
      groundRotDeg[i] = 0;
    }
    groundById[0] = groundGrass;
    groundById[1] = groundDirt;
    groundById[2] = groundSand;
    groundById[3] = groundRock;
    groundById[4] = groundSnow;
    groundById[5] = groundLava;

    if (cfg == null) return;

    for (int id = 0; id < groundById.length && id < cfg.ground.length; id++) {
      TilesetConfig.Ground g = cfg.ground[id];
      if (g == null) continue;
      groundRotDeg[id] = TilesetConfig.normalizeRot(g.rotation);

      try {
        TextureRegion r = resolveRegion(g);
        if (r != null) groundById[id] = r;
      } catch (Throwable ignored) {}
    }
  }

  private TextureRegion resolveRegion(TilesetConfig.Ground g) {
    if (g == null) return null;
    if (g.source == TilesetConfig.Source.TILESHEET) {
      throw new IllegalStateException("TILESHEET source is disabled. Provide ATLAS region/index instead. (col=" + g.col + ", row=" + g.row + ")");
    }
    if (g.source == TilesetConfig.Source.ATLAS) {
      if (g.region != null && !g.region.isBlank()) {
        return req(g.region.trim(), g.index);
      }
    }
    return null;
  }

  // Legacy accessors (used only by legacy code paths; kept as fallback assets).
  TextureRegion blobEdgeOnGrass(int mask) {
    return blobDirtEdgeFallback[mask & 15];
  }

  TextureRegion blobEdgeOnDirt(int mask) {
    return blobGrassEdgeFallback[mask & 15];
  }

  public int groundRotationDeg(short groundId) {
    int id = groundId & 0xFF;
    if (id < 0 || id >= groundRotDeg.length) return 0;
    return groundRotDeg[id];
  }

  public TextureRegion ground(short groundId) {
    int id = groundId & 0xFF;
    if (id >= 0 && id < groundById.length) {
      TextureRegion r = groundById[id];
      if (r != null) return r;
    }
    throw new IllegalStateException("Missing ground region for id=" + id + " (all ground must be ATLAS-mapped)");
  }

  public TextureRegion waterFill() { return waterFill; }
  public TextureRegion shore(int mask) { return shoreMask[mask & 15]; }
  public TextureRegion road(int mask) { return roadMask[mask & 15]; }
  public TextureRegion edgeGrass(int mask) { return edgeGrass[mask & 15]; }
  public TextureRegion edgeDirt(int mask) { return edgeDirt[mask & 15]; }
  public TextureRegion edgeSand(int mask) { return edgeSand[mask & 15]; }
  public TextureRegion edgeRock(int mask) { return edgeRock[mask & 15]; }
  public TextureRegion edgeSnow(int mask) { return edgeSnow[mask & 15]; }

  // ---- WorldGen decorations (purely visual) ----
  private final TextureRegion[] decoGrassTuft = new TextureRegion[4];
  private final TextureRegion[] decoFlower = new TextureRegion[4];
  private final TextureRegion[] decoWaterRock = new TextureRegion[4];

  public TextureRegion deco(byte decoId, byte decoVar) {
    int v = (decoVar & 0xFF) % 4;
    int id = decoId & 0xFF;
    if (id == 1) return decoGrassTuft[v];
    if (id == 2) return decoFlower[v];
    if (id == 3) return decoWaterRock[v];
    return null;
  }

  public void dispose() {
    atlas.dispose();
    if (propsTex != null) propsTex.dispose();
    if (grassDirtEdgeTex != null) grassDirtEdgeTex.dispose();
    if (dirtGrassEdgeTex != null) dirtGrassEdgeTex.dispose();
  }

  /** slices a 4x4 grid (32px per cell) from top-left and writes to out[0..15] row-major. */
  private static void slice4x4TL(Texture tex, TextureRegion[] out) {
    int tile = TILE;
    for (int ry = 0; ry < 4; ry++) {
      for (int rx = 0; rx < 4; rx++) {
        int x = rx * tile;
        int yTop = ry * tile;
        int y = tex.getHeight() - yTop - tile;
        out[rx + ry * 4] = new TextureRegion(tex, x, y, tile, tile);
      }
    }
  }
}

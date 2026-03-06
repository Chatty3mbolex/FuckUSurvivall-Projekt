package com.yourgame.survival.biome;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Biome runtime + editor-backed configuration.
 *
 * Source of truth:
 * - Desktop dev: config/biomes.json (working dir usually assets/)
 * - Release run folder: <runDir>/config/biomes.json
 *
 * NOTE: This is intentionally self-contained (no extra files) to keep the "touch each file once" rule.
 */
public final class BiomeSystem {
  public static final String LOCAL_PATH = "config/biomes.json";
  public static final String ZONEMASK_DIR = "config/biome_masks";

  public final long seed;

  /** Per-biome defs (indexed by Biome.ordinal()). */
  private final BiomeDef[] defs = new BiomeDef[Biome.values().length];

  /** Hard caps to avoid unbounded RAM growth during streaming/editor usage. */
  private static final int MAX_ZONEMASK_PIXMAP_CACHE = 96;

  /** LRU cache for loaded zone mask pixmaps. Key: biomeName + "|" + zoneName (lowercase). */
  private final LinkedHashMap<String, Pixmap> zoneMaskCache = new LinkedHashMap<>(128, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Pixmap> eldest) {
      if (size() <= MAX_ZONEMASK_PIXMAP_CACHE) return false;
      try {
        Pixmap pm = eldest.getValue();
        if (pm != null) pm.dispose();
      } catch (Throwable ignored) {}
      return true;
    }
  };

  // Height masks are per-biome and small; keep them cached while BiomeSystem lives.
  private final HashMap<String, Pixmap> heightMaskCache = new HashMap<>();
  private final HashMap<String, Pixmap> decoMaskCache = new HashMap<>();

  public BiomeSystem(long seed) {
    this.seed = seed;
    loadOrCreate();
  }

  public BiomeDef def(Biome b) {
    BiomeDef d = defs[b.ordinal()];
    if (d == null) {
      // hard fallback (should not happen)
      d = BiomeDef.defaultsFor(b);
      defs[b.ordinal()] = d;
    }
    return d;
  }

  /** Returns current config as pretty JSON. */
  public String toJson() {
    StringBuilder sb = new StringBuilder(8192);
    sb.append("{\n");
    sb.append("  \"version\": 1,\n");
    sb.append("  \"biomes\": {\n");

    boolean first = true;
    for (Biome b : Biome.values()) {
      if (!first) sb.append(",\n");
      first = false;
      sb.append("    \"").append(b.name()).append("\": ");
      sb.append(def(b).toJson(4));
    }

    sb.append("\n  }\n");
    sb.append("}\n");
    return sb.toString();
  }

  /** Writes current config to config/biomes.json (UTF-8). */
  public void save() {
    FileHandle fh = Gdx.files.local(LOCAL_PATH);
    fh.parent().mkdirs();
    fh.writeString(toJson(), false, "UTF-8");
  }

  /** Replace one biome definition (used by editor apply). */
  public void setBiomeDef(Biome biome, BiomeDef d) {
    if (d == null) return;
    defs[biome.ordinal()] = d;
    // Invalidate caches (pixmaps are heavy; dispose before clearing).
    disposeZoneMaskCache();
    disposeHeightMaskCache();
    disposeDecoMaskCache();
  }

  private void disposeZoneMaskCache() {
    try {
      for (Pixmap pm : zoneMaskCache.values()) {
        try { if (pm != null) pm.dispose(); } catch (Throwable ignored) {}
      }
    } catch (Throwable ignored) {}
    try { zoneMaskCache.clear(); } catch (Throwable ignored) {}
  }

  private void disposeHeightMaskCache() {
    try {
      for (Pixmap pm : heightMaskCache.values()) {
        try { if (pm != null) pm.dispose(); } catch (Throwable ignored) {}
      }
    } catch (Throwable ignored) {}
    try { heightMaskCache.clear(); } catch (Throwable ignored) {}
  }

  private void disposeDecoMaskCache() {
    try {
      for (Pixmap pm : decoMaskCache.values()) {
        try { if (pm != null) pm.dispose(); } catch (Throwable ignored) {}
      }
    } catch (Throwable ignored) {}
    try { decoMaskCache.clear(); } catch (Throwable ignored) {}
  }

  /** Returns true if local tile (lx,ly) is inside an enabled zone mask for this biome. */
  public boolean zoneMaskAt(Biome biome, String zoneName, int lx, int ly) {
    if (biome == null || zoneName == null) return false;
    if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) return false;

    BiomeDef d = def(biome);
    ZoneMaskDef zm = d.findZoneMask(zoneName);
    if (zm == null || zm.file == null || zm.file.isEmpty()) return false;

    Pixmap pm = getOrLoadZoneMaskPixmap(biome, zoneName, zm.file);
    if (pm == null) return false;
    int rgba = pm.getPixel(lx, ly);
    int a = (rgba) & 0xFF; // Pixmap stores RGBA8888; alpha is lowest byte
    return a > 0;
  }

  /** Editor/runtime helper: load (or return cached) zone mask pixmap. */
  public Pixmap getOrLoadZoneMaskPixmap(Biome biome, String zoneName, String file) {
    String key = biome.name() + "|" + zoneName.toLowerCase();
    Pixmap cached = zoneMaskCache.get(key);
    if (cached != null) return cached;

    try {
      FileHandle fh = Gdx.files.local(file);
      if (!fh.exists()) return null;
      Pixmap pm = new Pixmap(fh);
      zoneMaskCache.put(key, pm);
      return pm;
    } catch (Throwable ignored) {
      return null;
    }
  }

  public static final String HEIGHTMASK_DIR = "config/biome_height";
  public static final String DECOMASK_DIR = "config/biome_deco";

  /** Editor/runtime helper: load (or return cached) height mask pixmap for biome. */
  public Pixmap getOrLoadHeightMaskPixmap(Biome biome) {
    if (biome == null) return null;
    BiomeDef d = def(biome);
    if (d.heightMaskFile == null || d.heightMaskFile.isEmpty()) return null;

    Pixmap cached = heightMaskCache.get(biome.name());
    if (cached != null) return cached;

    try {
      FileHandle fh = Gdx.files.local(d.heightMaskFile);
      if (!fh.exists()) return null;
      Pixmap pm = new Pixmap(fh);
      heightMaskCache.put(biome.name(), pm);
      return pm;
    } catch (Throwable ignored) {
      return null;
    }
  }

  /** Height level at local tile coords for biome (0..15). Returns 0 if no mask. */
  public int heightMaskLevelAt(Biome biome, int lx, int ly) {
    if (biome == null) return 0;
    if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) return 0;

    Pixmap pm = getOrLoadHeightMaskPixmap(biome);
    return HeightMaskUtil.decodeHeightLevel(pm, lx, ly);
  }

  /** Write a zone mask pixmap as PNG to a stable path under config/biome_masks. */
  public static String writeZoneMaskPng(Biome biome, String zoneName, Pixmap pm) {
    String safeZone = zoneName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    String file = ZONEMASK_DIR + "/" + biome.name().toLowerCase() + "_" + safeZone.toLowerCase() + ".png";
    FileHandle fh = Gdx.files.local(file);
    fh.parent().mkdirs();
    PixmapIO.writePNG(fh, pm);
    return file;
  }

  /** Write a biome height mask as PNG to config/biome_height/<biome>.png (values in red channel). */
  public static String writeHeightMaskPng(Biome biome, Pixmap pm) {
    String file = HEIGHTMASK_DIR + "/" + biome.name().toLowerCase() + "_height.png";
    FileHandle fh = Gdx.files.local(file);
    fh.parent().mkdirs();
    PixmapIO.writePNG(fh, pm);
    return file;
  }

  public Pixmap getOrLoadDecoMaskPixmap(Biome biome) {
    if (biome == null) return null;
    BiomeDef d = def(biome);
    if (d.decoMaskFile == null || d.decoMaskFile.isEmpty()) return null;

    Pixmap cached = decoMaskCache.get(biome.name());
    if (cached != null) return cached;

    try {
      FileHandle fh = Gdx.files.local(d.decoMaskFile);
      if (!fh.exists()) return null;
      Pixmap pm = new Pixmap(fh);
      decoMaskCache.put(biome.name(), pm);
      return pm;
    } catch (Throwable ignored) {
      return null;
    }
  }

  /** Deco density (0..255). Defaults to 255 if no mask (full default deco). */
  public int decoMaskValueAt(Biome biome, int lx, int ly) {
    if (biome == null) return 255;
    if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) return 255;
    Pixmap pm = getOrLoadDecoMaskPixmap(biome);
    return DecoMaskUtil.decode(pm, lx, ly);
  }

  public static String writeDecoMaskPng(Biome biome, Pixmap pm) {
    String file = DECOMASK_DIR + "/" + biome.name().toLowerCase() + "_deco.png";
    FileHandle fh = Gdx.files.local(file);
    fh.parent().mkdirs();
    PixmapIO.writePNG(fh, pm);
    return file;
  }

  /**
   * Derive groundId for a tile based on biome + edge profiles.
   * This is purely a render/decoration decision; collision/water stays driven by TileLayers masks.
   */
  public short groundFor(Biome center, Biome neighbor, boolean inEdgeBand) {
    BiomeDef d = def(center);
    if (!inEdgeBand || neighbor == null || neighbor == center) return d.groundId;

    // Neighbor-specific override (e.g. mountain edge to forest/grass).
    Short o = d.edgeGroundOverride.get(neighbor.name());
    if (o != null) return o;
    // Fallback: if neighbor is water-ish, allow beach sand.
    if (neighbor == Biome.WATER || neighbor == Biome.RIVERBANK) {
      Short o2 = d.edgeGroundOverride.get("_WATER");
      if (o2 != null) return o2;
    }
    return d.groundId;
  }

  // Roads are generated in the worldgen pipeline (WorldRoadPlanner + RoadAdjacencyBaker).

  /** Deterministically compute POIs for a chunk; used by spawner and road planner. */
  public ArrayList<PoiSpawn> computePoisForChunk(final int cx, final int cy, final Biome biome) {
    final BiomeDef d = def(biome);
    ArrayList<PoiSpawn> out = new ArrayList<>();
    if (d.poiTemplates.isEmpty()) return out;

    final int baseTx = cx * World.CHUNK_SIZE;
    final int baseTy = cy * World.CHUNK_SIZE;

    // One RNG stream per chunk.
    long h0 = mix64(seed ^ 0xC0FFEE1234ABCD55L ^ (long) cx * 0x9E3779B97F4A7C15L ^ (long) cy * 0xC2B2AE3D27D4EB4FL);

    for (int i = 0; i < d.poiTemplates.size(); i++) {
      PoiTemplate t = d.poiTemplates.get(i);
      int count = t.minCount;
      if (t.maxCount > t.minCount) {
        long h = mix64(h0 ^ (i * 0x9E3779B97F4A7C15L));
        int span = t.maxCount - t.minCount + 1;
        count = t.minCount + (int) (Math.floorMod(h, span));
      }

      for (int k = 0; k < count; k++) {
        long hk = mix64(h0 ^ (i * 0xD6E8FEB86659FD93L) ^ (k * 0x94D049BB133111EBL));

        // position inside chunk (avoid edges a bit)
        int pad = Math.max(2, Math.min(12, t.edgePadTiles));
        int tx = baseTx + pad + (int) (Math.floorMod(hk >>> 1, World.CHUNK_SIZE - pad * 2));
        int ty = baseTy + pad + (int) (Math.floorMod(hk >>> 33, World.CHUNK_SIZE - pad * 2));

        // Keep deterministic but allow biome-specific placement restrictions later.
        out.add(new PoiSpawn(t.key, tx, ty, t.requiresRoad, t.roadClass, t.connectRadiusTiles));
      }
    }

    return out;
  }

  /**
   * Spawn rules are count-based (min/max) per chunk zone.
   * The editor edits these rules; the runtime spawner uses them.
   */
  public ArrayList<SpawnRule> spawnRulesFor(Biome biome) {
    return def(biome).spawnRules;
  }

  // ----------------- load / parse -----------------

  private void loadOrCreate() {
    FileHandle fh = Gdx.files.local(LOCAL_PATH);

    if (!fh.exists()) {
      // Create defaults
      for (Biome b : Biome.values()) defs[b.ordinal()] = BiomeDef.defaultsFor(b);
      save();
      return;
    }

    // Parse
    try {
      JsonValue root = new JsonReader().parse(fh.readString("UTF-8"));
      JsonValue biomes = root.get("biomes");
      if (biomes == null) throw new RuntimeException("biomes missing");

      for (Biome b : Biome.values()) {
        JsonValue bj = biomes.get(b.name());
        if (bj == null) {
          defs[b.ordinal()] = BiomeDef.defaultsFor(b);
        } else {
          defs[b.ordinal()] = BiomeDef.fromJson(b, bj);
        }
      }
    } catch (RuntimeException e) {
      // Fail-safe: do not crash game; revert to defaults and rewrite.
      for (Biome b : Biome.values()) defs[b.ordinal()] = BiomeDef.defaultsFor(b);
      save();
    }
  }

  private static long mix64(long z) {
    z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
    z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
    return z ^ (z >>> 33);
  }

  // ----------------- nested DTOs -----------------

  public static final class BiomeDef {
    public short groundId;
    /** Stable seed used by the editor preview (so loadâ†’preview is identical). */
    public long previewSeed = 0L;

    public boolean roadsEnabled;
    /** Width of the EDGE zone in tiles (used by spawner zone placement + edge blending intent). */
    public int edgeWidthTiles = 6;

    // For edge blending: neighbor biome name -> override groundId
    public final HashMap<String, Short> edgeGroundOverride = new HashMap<>();

    /** Optional repeating custom zones inside a chunk (rectangles in local tile coords). */
    public final ArrayList<CustomZone> customZones = new ArrayList<>();

    /** Optional painted zone masks (PNG under config/biome_masks). */
    public final ArrayList<ZoneMaskDef> zoneMasks = new ArrayList<>();

    /** Optional terraced height mask (PNG under config/biome_height). Values in red channel, 0..15. */
    public String heightMaskFile = "";

    /** Optional deco density mask (PNG under config/biome_deco). Values in red channel, 0..255. */
    public String decoMaskFile = "";

    /** Optional node stamp rules derived into spawnRules. */
    public final ArrayList<NodeStampRuleDef> nodeStampRules = new ArrayList<>();

    /** Optional encounter stamp rules for EncounterSpawner (orcs/deer). */
    public final ArrayList<EncounterStampRuleDef> encounterStampRules = new ArrayList<>();

    /** Optional editor-authored height rules driven by zone masks (preview/derive). */
    public final ArrayList<ZoneHeightRuleDef> zoneHeightRules = new ArrayList<>();

    public final ArrayList<SpawnRule> spawnRules = new ArrayList<>();
    public final ArrayList<PoiTemplate> poiTemplates = new ArrayList<>();

    public static BiomeDef defaultsFor(Biome b) {
      BiomeDef d = new BiomeDef();

      d.groundId = switch (b) {
        case BEACH -> TileIds.GROUND_SAND;
        case WATER, RIVERBANK -> TileIds.GROUND_SAND;
        case MOUNTAIN, VOLCANIC -> TileIds.GROUND_ROCK;
        case SNOWHIGHLAND -> TileIds.GROUND_SNOW;
        case LAVA -> TileIds.GROUND_LAVA;
        case ASHFIELD -> TileIds.GROUND_DIRT;
        case SWAMP -> TileIds.GROUND_DIRT;
        case FOREST, GRASSLAND, ISLANDS -> TileIds.GROUND_GRASS;
      };

      d.roadsEnabled = (b != Biome.WATER && b != Biome.LAVA);
      
      d.previewSeed = 1337L + (b.ordinal() * 99991L);
d.edgeWidthTiles = 6;

      // Generic edge overrides (can be edited in biomes.json)
      if (b == Biome.MOUNTAIN || b == Biome.SNOWHIGHLAND) {
        d.edgeGroundOverride.put(Biome.FOREST.name(), TileIds.GROUND_GRASS);
        d.edgeGroundOverride.put(Biome.GRASSLAND.name(), TileIds.GROUND_GRASS);
        d.edgeGroundOverride.put("_WATER", TileIds.GROUND_SAND);
      }
      if (b == Biome.FOREST) {
        d.edgeGroundOverride.put(Biome.BEACH.name(), TileIds.GROUND_SAND);
      }

      // Default spawn rules (count-based): low density.
      // CORE rules
      // Forest should be much denser (configured below).
      d.spawnRules.add(new SpawnRule("CORE", "TREE", 3, 8, true, 0.42f, 0.28f));
      d.spawnRules.add(new SpawnRule("CORE", "ROCK", 1, 3, false, 0f, 0f));

      // Example edge rule: if highland meets forest/grass, allow a bit of tree continuation in the EDGE band.
      if (b == Biome.MOUNTAIN || b == Biome.SNOWHIGHLAND) {
        SpawnRule edgeTrees = new SpawnRule("EDGE", "TREE", 1, 4, true, 0.42f, 0.28f);
        edgeTrees.neighborBiome = "FOREST";
        edgeTrees.side = "*";
        d.spawnRules.add(edgeTrees);
      }

      if (b == Biome.MOUNTAIN || b == Biome.SNOWHIGHLAND || b == Biome.VOLCANIC || b == Biome.ASHFIELD) {
        d.spawnRules.add(new SpawnRule("CORE", "IRON", 1, 4, false, 0f, 0f));
      }

      if (b == Biome.WATER || b == Biome.RIVERBANK || b == Biome.BEACH) {
        d.spawnRules.add(new SpawnRule("WATER", "FISH", 0, 1, false, 0f, 0f));
      }

      // Forest density override: 20..64 trees per chunk (max should be rare, but non-zero).
      if (b == Biome.FOREST) {
        for (int i = 0; i < d.spawnRules.size(); i++) {
          SpawnRule r = d.spawnRules.get(i);
          if (r != null && "TREE".equalsIgnoreCase(r.type) && "CORE".equalsIgnoreCase(r.zone)) {
            r.minCount = 20;
            r.maxCount = 64;
            r.allowTreeOverlap = false;
            break;
          }
        }
      }

      // POI templates
      if (b == Biome.MOUNTAIN || b == Biome.VOLCANIC || b == Biome.ASHFIELD) {
        d.poiTemplates.add(new PoiTemplate("OLD_MINE", 0.35f, 0, 1, true, "service", 28));
      }
      d.poiTemplates.add(new PoiTemplate("TREASURE_CHEST", 0.65f, 0, 2, false, "none", 0));

      return d;
    }

    public ZoneMaskDef findZoneMask(String name) {
      if (name == null) return null;
      for (int i = 0; i < zoneMasks.size(); i++) {
        ZoneMaskDef z = zoneMasks.get(i);
        if (z == null || z.name == null) continue;
        if (z.name.equalsIgnoreCase(name)) return z;
      }
      return null;
    }

    public String toJson(int indent) {
      String sp = " ".repeat(Math.max(0, indent));
      String sp2 = " ".repeat(Math.max(0, indent + 2));
      String sp3 = " ".repeat(Math.max(0, indent + 4));

      StringBuilder sb = new StringBuilder(2048);
      sb.append("{\n");
      sb.append(sp2).append("\"groundId\": ").append(groundId).append(",\n");
      sb.append(sp2).append("\"previewSeed\": ").append(previewSeed).append(",\n");
      sb.append(sp2).append("\"roadsEnabled\": ").append(roadsEnabled).append(",\n");
      sb.append(sp2).append("\"edgeWidthTiles\": ").append(edgeWidthTiles).append(",\n");

      // edge overrides
      sb.append(sp2).append("\"edgeGroundOverride\": {");
      if (!edgeGroundOverride.isEmpty()) {
        sb.append("\n");
        boolean first = true;
        for (var e : edgeGroundOverride.entrySet()) {
          if (!first) sb.append(",\n");
          first = false;
          sb.append(sp3).append("\"").append(escape(e.getKey())).append("\": ").append(e.getValue());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("},\n");

      // custom zones
      sb.append(sp2).append("\"customZones\": [");
      if (!customZones.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < customZones.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(customZones.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("],\n");

      // painted zone masks
      sb.append(sp2).append("\"zoneMasks\": [");
      if (!zoneMasks.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < zoneMasks.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(zoneMasks.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("],\n");

      sb.append(sp2).append("\"heightMaskFile\": \"").append(escape(heightMaskFile)).append("\",\n");
      sb.append(sp2).append("\"decoMaskFile\": \"").append(escape(decoMaskFile)).append("\",\n");

      sb.append(sp2).append("\"nodeStampRules\": [");
      if (!nodeStampRules.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < nodeStampRules.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(nodeStampRules.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("],\n");

      sb.append(sp2).append("\"encounterStampRules\": [");
      if (!encounterStampRules.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < encounterStampRules.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(encounterStampRules.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("],\n");

      // zone height rules (zoneName -> delta)
      sb.append(sp2).append("\"zoneHeightRules\": [");
      if (!zoneHeightRules.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < zoneHeightRules.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(zoneHeightRules.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("],\n");

      // spawn rules
      sb.append(sp2).append("\"spawnRules\": [");
      if (!spawnRules.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < spawnRules.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(spawnRules.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("],\n");

      // POIs
      sb.append(sp2).append("\"poiTemplates\": [");
      if (!poiTemplates.isEmpty()) {
        sb.append("\n");
        for (int i = 0; i < poiTemplates.size(); i++) {
          if (i > 0) sb.append(",\n");
          sb.append(sp3).append(poiTemplates.get(i).toJson());
        }
        sb.append("\n").append(sp2);
      }
      sb.append("]\n");

      sb.append(sp).append("}");
      return sb.toString();
    }

    public static BiomeDef fromJson(Biome biome, JsonValue bj) {
      BiomeDef d = new BiomeDef();

      d.groundId = (short) bj.getInt("groundId", defaultsFor(biome).groundId);
      d.previewSeed = bj.getLong("previewSeed", defaultsFor(biome).previewSeed);
      d.roadsEnabled = bj.getBoolean("roadsEnabled", defaultsFor(biome).roadsEnabled);
      d.edgeWidthTiles = bj.getInt("edgeWidthTiles", defaultsFor(biome).edgeWidthTiles);

      JsonValue eg = bj.get("edgeGroundOverride");
      if (eg != null) {
        for (JsonValue c = eg.child; c != null; c = c.next) {
          String k = c.name;
          short v = (short) c.asInt();
          d.edgeGroundOverride.put(k, v);
        }
      }

      JsonValue cz = bj.get("customZones");
      if (cz != null) {
        for (JsonValue c = cz.child; c != null; c = c.next) {
          CustomZone z = CustomZone.fromJson(c);
          if (z != null) d.customZones.add(z);
        }
      }

      JsonValue zm = bj.get("zoneMasks");
      if (zm != null) {
        for (JsonValue c = zm.child; c != null; c = c.next) {
          ZoneMaskDef z = ZoneMaskDef.fromJson(c);
          if (z != null) d.zoneMasks.add(z);
        }
      }

      d.heightMaskFile = bj.getString("heightMaskFile", "");
      d.decoMaskFile = bj.getString("decoMaskFile", "");

      JsonValue nsr = bj.get("nodeStampRules");
      if (nsr != null) {
        for (JsonValue c = nsr.child; c != null; c = c.next) {
          NodeStampRuleDef r = NodeStampRuleDef.fromJson(c);
          if (r != null) d.nodeStampRules.add(r);
        }
      }

      JsonValue esr = bj.get("encounterStampRules");
      if (esr != null) {
        for (JsonValue c = esr.child; c != null; c = c.next) {
          EncounterStampRuleDef r = EncounterStampRuleDef.fromJson(c);
          if (r != null) d.encounterStampRules.add(r);
        }
      }

      JsonValue zhr = bj.get("zoneHeightRules");
      if (zhr != null) {
        for (JsonValue c = zhr.child; c != null; c = c.next) {
          ZoneHeightRuleDef r = ZoneHeightRuleDef.fromJson(c);
          if (r != null) d.zoneHeightRules.add(r);
        }
      }

      JsonValue sr = bj.get("spawnRules");
      if (sr != null) {
        for (JsonValue c = sr.child; c != null; c = c.next) {
          SpawnRule r = SpawnRule.fromJson(c);
          if (r != null) d.spawnRules.add(r);
        }
      }

      JsonValue pt = bj.get("poiTemplates");
      if (pt != null) {
        for (JsonValue c = pt.child; c != null; c = c.next) {
          PoiTemplate p = PoiTemplate.fromJson(c);
          if (p != null) d.poiTemplates.add(p);
        }
      }

      // Fail-safe: if lists empty, add defaults so biomes are not barren.
      if (d.spawnRules.isEmpty() && d.poiTemplates.isEmpty()) {
        BiomeDef def = defaultsFor(biome);
        d.spawnRules.addAll(def.spawnRules);
        d.poiTemplates.addAll(def.poiTemplates);
      }

      // Derive node stamp rules into spawn rules (MASK:<name>). Remove previously derived ones first.
      if (!d.nodeStampRules.isEmpty()) {
        for (int i = d.spawnRules.size() - 1; i >= 0; i--) {
          SpawnRule r = d.spawnRules.get(i);
          if (r == null || r.zone == null) continue;
          if (r.zone.toUpperCase().startsWith("MASK:STAMP_NODE_")) d.spawnRules.remove(i);
        }
        for (int i = 0; i < d.nodeStampRules.size(); i++) {
          NodeStampRuleDef ns = d.nodeStampRules.get(i);
          if (ns == null || ns.type == null || ns.mask == null || ns.mask.isEmpty()) continue;
          SpawnRule sr2 = new SpawnRule("MASK:" + ns.mask, ns.type, ns.min, ns.max, ns.allowOverlap, ns.jitterX, ns.jitterY);
          d.spawnRules.add(sr2);
        }
      }

      return d;
    }

    private static String escape(String s) {
      return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
  }

  /** Editor-authored node stamp rule. Derived into SpawnRule with zone=MASK:<mask>. */
  public static final class NodeStampRuleDef {
    public String type;     // TREE|ROCK|IRON|BUSH
    public String mask;     // zone mask name (saved in zoneMasks)
    public int min;
    public int max;
    public boolean allowOverlap;
    public float jitterX;
    public float jitterY;

    public String toJson() {
      return "{ \"type\": \"" + type + "\", \"mask\": \"" + mask + "\", \"min\": " + min + ", \"max\": " + max +
          ", \"allowOverlap\": " + allowOverlap + ", \"jitterX\": " + jitterX + ", \"jitterY\": " + jitterY + " }";
    }

    public static NodeStampRuleDef fromJson(JsonValue j) {
      if (j == null) return null;
      NodeStampRuleDef r = new NodeStampRuleDef();
      r.type = j.getString("type", "TREE");
      r.mask = j.getString("mask", "");
      r.min = j.getInt("min", 0);
      r.max = j.getInt("max", r.min);
      r.allowOverlap = j.getBoolean("allowOverlap", false);
      r.jitterX = j.getFloat("jitterX", 0f);
      r.jitterY = j.getFloat("jitterY", 0f);
      return r;
    }
  }

  public static final class EncounterStampRuleDef {
    public String type;    // ORK_GRUNT|ANIMAL_DEER
    public String mask;    // zone mask name
    public int maxLoaded;  // cap while player is in this biome
    public float respawnMin;
    public float respawnMax;

    public String toJson() {
      return "{ \"type\": \"" + type + "\", \"mask\": \"" + mask + "\", \"maxLoaded\": " + maxLoaded +
          ", \"respawnMin\": " + respawnMin + ", \"respawnMax\": " + respawnMax + " }";
    }

    public static EncounterStampRuleDef fromJson(JsonValue j) {
      if (j == null) return null;
      EncounterStampRuleDef r = new EncounterStampRuleDef();
      r.type = j.getString("type", "ORK_GRUNT");
      r.mask = j.getString("mask", "");
      r.maxLoaded = j.getInt("maxLoaded", 0);
      r.respawnMin = j.getFloat("respawnMin", 8f);
      r.respawnMax = j.getFloat("respawnMax", 18f);
      return r;
    }
  }

  public static final class ZoneHeightRuleDef {
    public String zone;
    public int delta;

    public ZoneHeightRuleDef() {}

    public ZoneHeightRuleDef(String zone, int delta) {
      this.zone = zone;
      this.delta = delta;
    }

    public String toJson() {
      String z = (zone == null) ? "zone" : zone;
      return "{ \"zone\": \"" + z + "\", \"delta\": " + delta + " }";
    }

    public static ZoneHeightRuleDef fromJson(JsonValue j) {
      if (j == null) return null;
      ZoneHeightRuleDef r = new ZoneHeightRuleDef();
      r.zone = j.getString("zone", "zone");
      r.delta = j.getInt("delta", 0);
      return r;
    }
  }

  public static final class SpawnRule {
    public String zone;       // CORE | EDGE | WATER | ROAD | CUSTOM:<id>
    public String type;       // TREE | ROCK | IRON | BUSH | FISH
    /** Optional side filter for EDGE rules: N|E|S|W|* (default: "*") */
    public String side;
    /** Optional neighbor biome filter for EDGE rules (e.g. "BEACH", "FOREST", "*") */
    public String neighborBiome;
    public int minCount;
    public int maxCount;
    /** Optional density-based counts (per tile) for zone scaling. If >=0, overrides min/maxCount via zone area. */
    public float densityMin = -1f;
    public float densityMax = -1f;

    public boolean allowTreeOverlap;

    // Trees only: jitter in tiles as fraction of step (kept compatible with old logic)
    public float treeJitterX;
    public float treeJitterY;

    public SpawnRule() {}

    public SpawnRule(String zone, String type, int minCount, int maxCount, boolean allowTreeOverlap, float treeJitterX, float treeJitterY) {
      this.zone = zone;
      this.type = type;
      this.minCount = minCount;
      this.maxCount = maxCount;
      this.allowTreeOverlap = allowTreeOverlap;
      this.treeJitterX = treeJitterX;
      this.treeJitterY = treeJitterY;
    }

    public String toJson() {
      String s = "{ \"zone\": \"" + zone + "\", \"type\": \"" + type + "\", \"min\": " + minCount +
          ", \"max\": " + maxCount + ", \"allowTreeOverlap\": " + allowTreeOverlap +
          ", \"treeJitterX\": " + treeJitterX + ", \"treeJitterY\": " + treeJitterY;
      if (side != null) s += ", \"side\": \"" + side + "\"";
      if (neighborBiome != null) s += ", \"neighborBiome\": \"" + neighborBiome + "\"";
      return s + " }";
    }

    public static SpawnRule fromJson(JsonValue j) {
      if (j == null) return null;
      SpawnRule r = new SpawnRule();
      r.zone = j.getString("zone", "CORE");
      r.type = j.getString("type", "TREE");
      r.minCount = j.getInt("min", 0);
      r.maxCount = j.getInt("max", r.minCount);
      r.allowTreeOverlap = j.getBoolean("allowTreeOverlap", false);
      r.treeJitterX = j.getFloat("treeJitterX", 0f);
      r.treeJitterY = j.getFloat("treeJitterY", 0f);
      r.side = j.getString("side", null);
      r.neighborBiome = j.getString("neighborBiome", null);
      return r;
    }
  }

  /** Simple repeating rectangle zone inside a chunk (local tile coordinates). */
  public static final class CustomZone {
    public String name;
    public int x;
    public int y;
    public int w;
    public int h;

    public CustomZone() {}

    public CustomZone(String name, int x, int y, int w, int h) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
    }

    public String toJson() {
      return "{ \"name\": \"" + name + "\", \"x\": " + x + ", \"y\": " + y + ", \"w\": " + w + ", \"h\": " + h + " }";
    }

    public static CustomZone fromJson(JsonValue j) {
      if (j == null) return null;
      CustomZone z = new CustomZone();
      z.name = j.getString("name", "ZONE");
      z.x = j.getInt("x", 0);
      z.y = j.getInt("y", 0);
      z.w = j.getInt("w", 0);
      z.h = j.getInt("h", 0);
      return z;
    }
  }

  public static final class PoiTemplate {
    public String key;
    public float weight;
    public int minCount;
    public int maxCount;

    public boolean requiresRoad;
    public String roadClass;
    public int connectRadiusTiles;

    public int edgePadTiles = 6;

    public PoiTemplate() {}

    public PoiTemplate(String key, float weight, int minCount, int maxCount, boolean requiresRoad, String roadClass, int connectRadiusTiles) {
      this.key = key;
      this.weight = weight;
      this.minCount = minCount;
      this.maxCount = maxCount;
      this.requiresRoad = requiresRoad;
      this.roadClass = roadClass;
      this.connectRadiusTiles = connectRadiusTiles;
    }

    public String toJson() {
      return "{ \"key\": \"" + key + "\", \"weight\": " + weight + ", \"min\": " + minCount + ", \"max\": " + maxCount +
          ", \"requiresRoad\": " + requiresRoad + ", \"roadClass\": \"" + roadClass + "\", \"connectRadius\": " + connectRadiusTiles +
          ", \"edgePadTiles\": " + edgePadTiles + " }";
    }

    public static PoiTemplate fromJson(JsonValue j) {
      if (j == null) return null;
      PoiTemplate p = new PoiTemplate();
      p.key = j.getString("key", "POI");
      p.weight = j.getFloat("weight", 1f);
      p.minCount = j.getInt("min", 0);
      p.maxCount = j.getInt("max", p.minCount);
      p.requiresRoad = j.getBoolean("requiresRoad", false);
      p.roadClass = j.getString("roadClass", "service");
      p.connectRadiusTiles = j.getInt("connectRadius", 24);
      p.edgePadTiles = j.getInt("edgePadTiles", 6);
      return p;
    }
  }

  public static final class PoiSpawn {
    public final String key;
    public final int tx;
    public final int ty;
    public final boolean requiresRoad;
    public final String roadClass;
    public final int connectRadiusTiles;

    public PoiSpawn(String key, int tx, int ty, boolean requiresRoad, String roadClass, int connectRadiusTiles) {
      this.key = key;
      this.tx = tx;
      this.ty = ty;
      this.requiresRoad = requiresRoad;
      this.roadClass = roadClass;
      this.connectRadiusTiles = connectRadiusTiles;
    }
  }

  /** Named zone mask reference, stored as a PNG file under config/biome_masks/. */
  public static final class ZoneMaskDef {
    public String name;
    public String file;

    public ZoneMaskDef() {}

    public ZoneMaskDef(String name, String file) {
      this.name = name;
      this.file = file;
    }

    public String toJson() {
      return "{ \"name\": \"" + name + "\", \"file\": \"" + file + "\" }";
    }

    public static ZoneMaskDef fromJson(JsonValue j) {
      if (j == null) return null;
      ZoneMaskDef z = new ZoneMaskDef();
      z.name = j.getString("name", "ZONE");
      z.file = j.getString("file", "");
      return z;
    }
  }

  // Nicht fertiges Feature:
  // Anchor + Node were intended as local helper structs for zone stamping / anchors,
  // but are currently unused. Keeping as commented code for later re-enable.
  /*
  private static final class Anchor {
    public final int tx;
    public final int ty;

    private Anchor(int tx, int ty) {
      this.tx = tx;
      this.ty = ty;
    }
  }

  private static final class Node {
    public final int tx;
    public final int ty;

    private Node(int tx, int ty) {
      this.tx = tx;
      this.ty = ty;
    }
  }
  */
}




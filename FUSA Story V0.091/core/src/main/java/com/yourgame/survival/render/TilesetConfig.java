package com.yourgame.survival.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Locale;

/**
 * tileset.json loader/saver for:
 * - ground (logical groundId -> sprite mapping + properties)
 *
 * Note (V0.073): Pair-based transitions were removed.
 * Terrain blending is rendered via overlap-only edge_<material> atlas overlays.
 */
public final class TilesetConfig {
  public static final int MIN_GROUND = 6; // TileIds 0..5
  public static final int MAX_ID = 255;

  public enum Source { TILESHEET, ATLAS }

  public static final class Ground {
    public String name = null;

    // mapping keys
    public Source source = Source.TILESHEET;
    public int col = 0;
    public int row = 0;
    public String region = null;
    public int index = 0;
    public int rotation = 0;

    // common booleans
    public boolean blocked = false;
    public boolean water = false;
    public boolean lava = false;

    // characteristics
    public float friction = 1.0f;
    public float speedMultiplier = 1.0f;
    public String footstepSfx = "";
    public float damagePerSecond = 0.0f;

    // dynamic boolean flags
    public final HashMap<String, Boolean> flags = new HashMap<>();
  }

  public Ground[] ground;

  public TilesetConfig() {
    this.ground = new Ground[MIN_GROUND];
    for (int i = 0; i < ground.length; i++) ground[i] = new Ground();
  }

  public void ensureSize(int n) {
    if (n < MIN_GROUND) n = MIN_GROUND;
    if (n > MAX_ID + 1) n = MAX_ID + 1;
    if (ground != null && ground.length >= n) return;

    Ground[] old = ground;
    Ground[] neu = new Ground[n];
    for (int i = 0; i < n; i++) {
      if (old != null && i < old.length && old[i] != null) neu[i] = old[i];
      else neu[i] = new Ground();
    }
    ground = neu;
  }

  public static TilesetConfig tryLoad() {
    try {
      FileHandle fh = Gdx.files.internal("config/tileset.json");
      if (fh == null || !fh.exists()) return null;
      return load(fh);
    } catch (Throwable ignored) {
      return null;
    }
  }

  public static TilesetConfig load(FileHandle fh) {
    try {
      if (fh == null || !fh.exists()) return null;
      JsonValue root = new JsonReader().parse(fh.readString("UTF-8"));

      TilesetConfig cfg = new TilesetConfig();

      // ---------- ground ----------
      JsonValue g = root.get("ground");
      if (g == null) return null;

      int maxId = -1;
      for (JsonValue e = g.child; e != null; e = e.next) {
        int id;
        try { id = Integer.parseInt(e.name); } catch (Throwable ignored) { continue; }
        if (id < 0 || id > MAX_ID) continue;
        if (id > maxId) maxId = id;
      }
      cfg.ensureSize(Math.max(MIN_GROUND, maxId + 1));

      for (JsonValue e = g.child; e != null; e = e.next) {
        int id;
        try { id = Integer.parseInt(e.name); } catch (Throwable ignored) { continue; }
        if (id < 0 || id >= cfg.ground.length) continue;
        parseInto(cfg.ground[id], e);
      }

      // Pair-based transitions intentionally not loaded anymore.
      return cfg;
    } catch (Throwable ignored) {
      return null;
    }
  }

  private static void parseInto(Ground out, JsonValue e) {
    if (out == null || e == null) return;

    out.name = e.getString("name", out.name);

    String src = e.getString("source", "TILESHEET");
    out.source = "ATLAS".equalsIgnoreCase(src) ? Source.ATLAS : Source.TILESHEET;
    out.col = e.getInt("col", out.col);
    out.row = e.getInt("row", out.row);
    out.region = e.getString("region", out.region);
    out.index = e.getInt("index", out.index);
    out.rotation = normalizeRot(e.getInt("rotation", out.rotation));

    out.blocked = e.getBoolean("blocked", out.blocked);
    out.water = e.getBoolean("water", out.water);
    out.lava = e.getBoolean("lava", out.lava);

    out.friction = (float) e.getDouble("friction", out.friction);
    out.speedMultiplier = (float) e.getDouble("speedMultiplier", out.speedMultiplier);
    out.footstepSfx = e.getString("footstepSfx", out.footstepSfx);
    out.damagePerSecond = (float) e.getDouble("damagePerSecond", out.damagePerSecond);

    out.flags.clear();
    for (JsonValue c = e.child; c != null; c = c.next) {
      String k = c.name;
      if (k == null) continue;
      if (isMappingKey(k)) continue;
      if ("name".equals(k)) continue;
      if ("blocked".equals(k) || "water".equals(k) || "lava".equals(k)) continue;
      if ("friction".equals(k) || "speedMultiplier".equals(k) || "footstepSfx".equals(k) || "damagePerSecond".equals(k)) continue;
      if (c.isBoolean()) out.flags.put(k, c.asBoolean());
    }
  }

  private static boolean isMappingKey(String k) {
    return "source".equals(k) || "col".equals(k) || "row".equals(k) || "region".equals(k) || "index".equals(k) || "rotation".equals(k);
  }

  public void save(FileHandle fh) {
    if (fh == null) return;

    StringBuilder sb = new StringBuilder(256 * 1024);
    sb.append("{\n");
    sb.append("  \"version\": 1,\n");

    // ground
    sb.append("  \"ground\": {\n");
    for (int id = 0; id < ground.length; id++) {
      sb.append("    \"").append(id).append("\": ");
      sb.append(toJson(ground[id]));
      sb.append(id == ground.length - 1 ? "\n" : ",\n");
    }
    sb.append("  }\n");
    sb.append("}\n");

    fh.writeString(sb.toString(), false, "UTF-8");
  }

  private static String toJson(Ground g) {
    if (g == null) g = new Ground();

    StringBuilder sb = new StringBuilder(512);
    sb.append("{ ");

    if (g.name != null && !g.name.isBlank()) {
      sb.append("\"name\": \"").append(escapeJson(g.name)).append("\", ");
    }

    sb.append("\"source\": \"").append(g.source == Source.ATLAS ? "ATLAS" : "TILESHEET").append("\"");
    sb.append(", \"col\": ").append(g.col);
    sb.append(", \"row\": ").append(g.row);
    if (g.region != null && !g.region.isBlank()) {
      sb.append(", \"region\": \"").append(escapeJson(g.region.trim())).append("\"");
    }
    sb.append(", \"index\": ").append(g.index);
    sb.append(", \"rotation\": ").append(normalizeRot(g.rotation));

    sb.append(", \"blocked\": ").append(g.blocked ? "true" : "false");
    sb.append(", \"water\": ").append(g.water ? "true" : "false");
    sb.append(", \"lava\": ").append(g.lava ? "true" : "false");

    sb.append(", \"friction\": ").append(trimFloat(g.friction));
    sb.append(", \"speedMultiplier\": ").append(trimFloat(g.speedMultiplier));
    sb.append(", \"footstepSfx\": \"").append(escapeJson(g.footstepSfx == null ? "" : g.footstepSfx)).append("\"");
    sb.append(", \"damagePerSecond\": ").append(trimFloat(g.damagePerSecond));

    if (g.flags != null && !g.flags.isEmpty()) {
      java.util.ArrayList<String> keys = new java.util.ArrayList<>(g.flags.keySet());
      java.util.Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
      for (String k : keys) {
        if (k == null) continue;
        if (isMappingKey(k) || "name".equals(k) || "blocked".equals(k) || "water".equals(k) || "lava".equals(k)) continue;
        if ("friction".equals(k) || "speedMultiplier".equals(k) || "footstepSfx".equals(k) || "damagePerSecond".equals(k)) continue;
        Boolean v = g.flags.get(k);
        if (v == null) continue;
        sb.append(", \"").append(escapeJson(k)).append("\": ").append(v ? "true" : "false");
      }
    }

    sb.append(" }");
    return sb.toString();
  }

  private static String trimFloat(float v) {
    if (Float.isNaN(v) || Float.isInfinite(v)) return "0.0";
    String s = String.format(Locale.ROOT, "%.4f", v);
    while (s.contains(".") && (s.endsWith("0") || s.endsWith("."))) {
      if (s.endsWith(".")) { s += "0"; break; }
      s = s.substring(0, s.length() - 1);
    }
    return s;
  }

  private static String escapeJson(String s) {
    if (s == null) return "";
    return s.replace("\\", "\\\\").replace("\"", "");
  }

  public static int normalizeRot(int deg) {
    int d = deg % 360;
    if (d < 0) d += 360;
    if (d < 45) return 0;
    if (d < 135) return 90;
    if (d < 225) return 180;
    if (d < 315) return 270;
    return 0;
  }
}

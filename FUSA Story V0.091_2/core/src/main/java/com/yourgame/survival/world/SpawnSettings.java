package com.yourgame.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Externalized spawn settings for WorldNodeSpawner.
 *
 * Local path (Desktop): relative to working directory.
 * - Dev desktop run: workingDir is assets/ -> assets/config/spawn_settings.json
 * - Release: workingDir is run folder -> <runDir>/config/spawn_settings.json
 */
public final class SpawnSettings {
  public static final String LOCAL_PATH = "config/spawn_settings.json";

  public int step = 8;
  public float nearbyRadius = 10f;

  // CUMULATIVE thresholds per biome (match current WorldNodeSpawner.pickType logic)
  public final float[] treeT = new float[Biome.values().length];
  public final float[] rockT = new float[Biome.values().length];
  public final float[] ironT = new float[Biome.values().length];

  private static SpawnSettings cached;
  private static long cachedMtime = -2L;

  private SpawnSettings() {
    applyDefaults();
  }

  /** Returns cached settings, reloads if file mtime changes, creates default file if missing. */
  public static SpawnSettings get() {
    // Gdx must be available when called (we call this lazily from the spawner at runtime).
    FileHandle fh = Gdx.files.local(LOCAL_PATH);

    // If file missing: create once with defaults (so editor has something to edit)
    if (!fh.exists()) {
      if (cached == null) cached = new SpawnSettings();
      // Always ensure directory exists.
      fh.parent().mkdirs();
      fh.writeString(cached.toJson(), false, "UTF-8");
      cachedMtime = safeMtime(fh);
      return cached;
    }

    long m = safeMtime(fh);
    if (cached != null && m == cachedMtime) return cached;

    // Reload from JSON (fallback to defaults if parse fails)
    SpawnSettings s = new SpawnSettings();
    try {
      String txt = fh.readString("UTF-8");
      JsonValue root = new JsonReader().parse(txt);
      s.applyFromJson(root);
    } catch (Throwable ignored) {
      // keep defaults
    }

    cached = s;
    cachedMtime = m;
    return cached;
  }

  private static long safeMtime(FileHandle fh) {
    try { return fh.file().lastModified(); }
    catch (Throwable ignored) { return -1L; }
  }

  private void applyDefaults() {
    // Base
    step = 8;
    nearbyRadius = 10f;

    // Zero everything first
    for (int i = 0; i < treeT.length; i++) { treeT[i] = 0f; rockT[i] = 0f; ironT[i] = 0f; }

    // Match current WorldNodeSpawner.pickType switch exactly:
    // FOREST
    set(Biome.FOREST, 0.22f, 0.26f, 0f);

    // GRASSLAND + ISLANDS
    set(Biome.GRASSLAND, 0.10f, 0.13f, 0f);
    set(Biome.ISLANDS, 0.10f, 0.13f, 0f);

    // BEACH + RIVERBANK + SWAMP
    set(Biome.BEACH, 0.06f, 0f, 0f);
    set(Biome.RIVERBANK, 0.06f, 0f, 0f);
    set(Biome.SWAMP, 0.06f, 0f, 0f);

    // MOUNTAIN + SNOWHIGHLAND + VOLCANIC + ASHFIELD
    set(Biome.MOUNTAIN, 0f, 0.18f, 0.22f);
    set(Biome.SNOWHIGHLAND, 0f, 0.18f, 0.22f);
    set(Biome.VOLCANIC, 0f, 0.18f, 0.22f);
    set(Biome.ASHFIELD, 0f, 0.18f, 0.22f);
  }

  private void set(Biome b, float tree, float rock, float iron) {
    int i = b.ordinal();
    treeT[i] = tree;
    rockT[i] = rock;
    ironT[i] = iron;
  }

  private void applyFromJson(JsonValue root) {
    if (root == null) return;

    // Optional: base fields
    JsonValue base = root.get("nodeSpawner");
    if (base == null) base = root; // allow flat schema too

    step = base.getInt("step", step);
    nearbyRadius = base.getFloat("nearbyRadius", nearbyRadius);

    // Optional: biome map
    JsonValue biomes = base.get("biomes");
    if (biomes == null) return;

    // For each biome key, update thresholds if present
    for (Biome b : Biome.values()) {
      JsonValue bj = biomes.get(b.name());
      if (bj == null) continue;

      float tree = bj.getFloat("tree", treeT[b.ordinal()]);
      float rock = bj.getFloat("rock", rockT[b.ordinal()]);
      float iron = bj.getFloat("iron", ironT[b.ordinal()]);
      set(b, clamp01(tree), clamp01(rock), clamp01(iron));
    }
  }

  private static float clamp01(float v) {
    if (v < 0f) return 0f;
    if (v > 1f) return 1f;
    return v;
  }

  public String toJson() {
    StringBuilder sb = new StringBuilder(2048);
    sb.append("{\n");
    sb.append("  \"version\": 1,\n");
    sb.append("  \"nodeSpawner\": {\n");
    sb.append("    \"step\": ").append(step).append(",\n");
    sb.append("    \"nearbyRadius\": ").append(nearbyRadius).append(",\n");
    sb.append("    \"biomes\": {\n");

    boolean first = true;
    for (Biome b : Biome.values()) {
      // Only write biomes that actually matter (non-zero or default ones)
      float t = treeT[b.ordinal()];
      float r = rockT[b.ordinal()];
      float i = ironT[b.ordinal()];
      if (t == 0f && r == 0f && i == 0f) continue;

      if (!first) sb.append(",\n");
      first = false;
      sb.append("      \"").append(b.name()).append("\": { ");
      sb.append("\"tree\": ").append(t).append(", ");
      sb.append("\"rock\": ").append(r).append(", ");
      sb.append("\"iron\": ").append(i).append(" }");
    }
    sb.append("\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }
}

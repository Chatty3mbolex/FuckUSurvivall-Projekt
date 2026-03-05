package com.yourgame.survival.worldgen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Externalized WorldGen configuration.
 *
 * Failsafe rule: if config is missing/invalid, defaults are used.
 */
public final class WorldGenConfig {
  public static final String LOCAL_PATH = "config/worldgen.json";

  // --- Noise (must match legacy hardcoded values by default) ---
  public float heightFreq = 0.018f;
  public int heightOctaves = 3;

  public float heatFreq = 0.006f;
  public int heatOctaves = 2;

  public float moistFreq = 0.0085f;
  public int moistOctaves = 2;

  public float vegFreq = 0.020f;
  public int vegOctaves = 2;

  public float rockFreq = 0.030f;
  public int rockOctaves = 2;

  public float pathFreq = 0.040f;
  public int pathOctaves = 1;

  // --- Water ---
  /** BFS radius for waterDist field (old: 6). */
  public int waterDistMaxR = 6;

  // --- Edge blending ---
  /** Optional global cap; per-biome edgeWidthTiles is still respected. */
  public int edgeWidthCapTiles = 32;

  // --- Roads ---
  /** Master toggle (useful for isolating generation stalls). */
  public boolean roadsEnabled = true;


  public static WorldGenConfig defaults() {
    return new WorldGenConfig();
  }

  public static WorldGenConfig loadOrDefault() {
    FileHandle fh = Gdx.files.local(LOCAL_PATH);

    // Hard bootstrap: if local config is missing, copy the default from assets (internal) into local.
    // This ensures the game always uses the SAME path for runtime tweaks: LOCAL_PATH.
    if (!fh.exists()) {
      try {
        FileHandle internal = Gdx.files.internal("config/worldgen.json");
        if (internal.exists()) {
          try { fh.parent().mkdirs(); } catch (Throwable ignored) {}
          internal.copyTo(fh);
        }
      } catch (Throwable ignored) {}
    }

    try {
      if (!fh.exists()) return defaults();

      JsonValue root = new JsonReader().parse(fh.readString("UTF-8"));
      WorldGenConfig c = defaults();

      JsonValue noise = root.get("noise");
      if (noise != null) {
        JsonValue height = noise.get("height");
        if (height != null) {
          c.heightFreq = height.getFloat("freq", c.heightFreq);
          c.heightOctaves = height.getInt("octaves", c.heightOctaves);
        }
        JsonValue heat = noise.get("heat");
        if (heat != null) {
          c.heatFreq = heat.getFloat("freq", c.heatFreq);
          c.heatOctaves = heat.getInt("octaves", c.heatOctaves);
        }
        JsonValue moist = noise.get("moist");
        if (moist != null) {
          c.moistFreq = moist.getFloat("freq", c.moistFreq);
          c.moistOctaves = moist.getInt("octaves", c.moistOctaves);
        }
        JsonValue veg = noise.get("veg");
        if (veg != null) {
          c.vegFreq = veg.getFloat("freq", c.vegFreq);
          c.vegOctaves = veg.getInt("octaves", c.vegOctaves);
        }
        JsonValue rock = noise.get("rock");
        if (rock != null) {
          c.rockFreq = rock.getFloat("freq", c.rockFreq);
          c.rockOctaves = rock.getInt("octaves", c.rockOctaves);
        }
        JsonValue path = noise.get("path");
        if (path != null) {
          c.pathFreq = path.getFloat("freq", c.pathFreq);
          c.pathOctaves = path.getInt("octaves", c.pathOctaves);
        }
      }

      JsonValue water = root.get("water");
      if (water != null) {
        c.waterDistMaxR = water.getInt("waterDistMaxR", c.waterDistMaxR);
      }

      JsonValue edge = root.get("edgeBlend");
      if (edge != null) {
        c.edgeWidthCapTiles = edge.getInt("edgeWidthCapTiles", c.edgeWidthCapTiles);
      }

      JsonValue roads = root.get("roads");
      if (roads != null) {
        c.roadsEnabled = roads.getBoolean("enabled", c.roadsEnabled);
      }

      return c;
    } catch (Throwable ignored) {
      // If local config exists but is broken, try to restore from internal defaults once.
      try {
        FileHandle internal = Gdx.files.internal("config/worldgen.json");
        if (internal.exists()) {
          try { fh.parent().mkdirs(); } catch (Throwable ignored2) {}
          internal.copyTo(fh);
          JsonValue root = new JsonReader().parse(fh.readString("UTF-8"));
          WorldGenConfig c = defaults();

          JsonValue noise = root.get("noise");
          if (noise != null) {
            JsonValue height = noise.get("height");
            if (height != null) {
              c.heightFreq = height.getFloat("freq", c.heightFreq);
              c.heightOctaves = height.getInt("octaves", c.heightOctaves);
            }
            JsonValue heat = noise.get("heat");
            if (heat != null) {
              c.heatFreq = heat.getFloat("freq", c.heatFreq);
              c.heatOctaves = heat.getInt("octaves", c.heatOctaves);
            }
            JsonValue moist = noise.get("moist");
            if (moist != null) {
              c.moistFreq = moist.getFloat("freq", c.moistFreq);
              c.moistOctaves = moist.getInt("octaves", c.moistOctaves);
            }
            JsonValue veg = noise.get("veg");
            if (veg != null) {
              c.vegFreq = veg.getFloat("freq", c.vegFreq);
              c.vegOctaves = veg.getInt("octaves", c.vegOctaves);
            }
            JsonValue rock = noise.get("rock");
            if (rock != null) {
              c.rockFreq = rock.getFloat("freq", c.rockFreq);
              c.rockOctaves = rock.getInt("octaves", c.rockOctaves);
            }
            JsonValue path = noise.get("path");
            if (path != null) {
              c.pathFreq = path.getFloat("freq", c.pathFreq);
              c.pathOctaves = path.getInt("octaves", c.pathOctaves);
            }
          }

          JsonValue water = root.get("water");
          if (water != null) {
            c.waterDistMaxR = water.getInt("waterDistMaxR", c.waterDistMaxR);
          }

          JsonValue edge = root.get("edgeBlend");
          if (edge != null) {
            c.edgeWidthCapTiles = edge.getInt("edgeWidthCapTiles", c.edgeWidthCapTiles);
          }

          JsonValue roads = root.get("roads");
          if (roads != null) {
            c.roadsEnabled = roads.getBoolean("enabled", c.roadsEnabled);
          }

          return c;
        }
      } catch (Throwable ignored2) {}

      return defaults();
    }
  }
}

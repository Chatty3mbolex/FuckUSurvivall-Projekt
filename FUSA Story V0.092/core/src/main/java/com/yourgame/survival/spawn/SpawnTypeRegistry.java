package com.yourgame.survival.spawn;

import com.yourgame.survival.entity.EntityType;

import java.util.EnumMap;

/**
 * Central registry for SpawnTypeId -> SpawnTypeDef.
 *
 * Start broad + undefined; progressively map ids to runtime types.
 */
public final class SpawnTypeRegistry {
  private static final EnumMap<SpawnTypeId, SpawnTypeDef> defs = new EnumMap<>(SpawnTypeId.class);

  static {
    // Defaults
    for (SpawnTypeId id : SpawnTypeId.values()) {
      defs.put(id, new SpawnTypeDef(id));
    }

    // Auto-categorize by naming convention (kept coarse).
    for (SpawnTypeId id : SpawnTypeId.values()) {
      SpawnTypeDef d = defs.get(id);
      if (d == null) continue;
      String n = id.name();

      if (n.startsWith("ENEMY_")) {
        d.cat(SpawnCategory.ENEMY);
      } else if (n.startsWith("ANIMAL_") || n.startsWith("FISH_")) {
        d.cat(SpawnCategory.ANIMAL);
      } else if (n.startsWith("TREE_") || n.startsWith("BUSH_") || n.startsWith("ROCK_") || n.startsWith("ORE_") || n.equals("STUMP")) {
        d.cat(SpawnCategory.NODE);
      } else if (n.contains("CHEST") || n.contains("RUIN") || n.contains("CAVE")) {
        d.cat(SpawnCategory.POI);
      } else if (n.endsWith("_DECOR") || n.endsWith("_PATCH") || n.endsWith("_TILES_PATCH")
          || n.startsWith("PLANT_") || n.startsWith("GROUND_")
          || n.contains("SHELL") || n.contains("KELP") || n.contains("SEAWEED")
          || n.contains("WATER_") || n.contains("LAVA_") || n.contains("ICE_")
          || n.contains("MUD_") || n.contains("SAND_") || n.contains("ASH_")
          || n.contains("EMBER") || n.contains("STEAM") || n.contains("GEYSER")
          || n.contains("ROOT_") || n.contains("LOG_")) {
        d.cat(SpawnCategory.DECO);
      } else if (n.startsWith("FRUIT_") || n.endsWith("_DROP")) {
        d.cat(SpawnCategory.ITEM);
      }
    }

    // Known existing runtime entities/nodes right now (hard mapping overrides)
    defs.get(SpawnTypeId.ANIMAL_DEER).cat(SpawnCategory.ANIMAL).map(EntityType.ANIMAL_DEER);

    // Everything else may remain UNDEFINED until implemented.
  }

  private SpawnTypeRegistry() {}

  public static SpawnTypeDef def(SpawnTypeId id) {
    return defs.get(id);
  }
}

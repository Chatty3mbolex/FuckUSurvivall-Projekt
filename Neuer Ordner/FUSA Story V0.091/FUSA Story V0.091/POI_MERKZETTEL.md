# POI_MERKZETTEL.md (scan)

Ziel: Alles im Code/Config finden, was mit POIs zu tun hat (auch indirekt: Chests, Road-Connector, Marker-Sprites, Spawn-Pipeline).

## 1) BiomeSystem – POI Templates + Deterministische Platzierung
**Datei:** `core/src/main/java/com/yourgame/survival/biome/BiomeSystem.java`

### Zentrale API
- `computePoisForChunk(int cx, int cy, Biome b) -> ArrayList<PoiSpawn>`
  - erzeugt POIs pro Chunk deterministisch aus `poiTemplates`.

### Datenstrukturen
- `BiomeDef.poiTemplates : ArrayList<PoiTemplate>`
- `PoiTemplate`
  - `key` (String)  
  - `weight`  
  - `minCount`, `maxCount`  
  - `requiresRoad` (boolean)
  - `roadClass` (String)
  - `connectRadiusTiles` (int)
  - `edgePadTiles` (int)
  - JSON: `PoiTemplate.fromJson(...)` / `toJson()`
- `PoiSpawn`
  - `key` (String)
  - `tx`, `ty` (tile coords)
  - `requiresRoad`
  - `roadClass`
  - `connectRadiusTiles`

### Hardcoded default POIs (aktueller Stand)
- `OLD_MINE`
- `TREASURE_CHEST`

## 2) Config – Biome POI Templates
**Datei:** `assets/config/biomes.json`
- Enthält `poiTemplates` pro Biome/Area.
- Fields relevant: `key`, `weight`, `min`, `max`, `requiresRoad`, `roadClass`, `connectRadius`, `edgePadTiles`

(Backup-Files existieren: `assets/config/biomes.json.bak.*`)

## 3) WorldNodeSpawner – Umsetzung der POI-Spawns in echte Entities
**Datei:** `core/src/main/java/com/yourgame/survival/world/WorldNodeSpawner.java`

### POI Loop
- `ArrayList<BiomeSystem.PoiSpawn> pois = bs.computePoisForChunk(cx, cy, b);`
- POI wird per `p.key` entschieden:
  - `TREASURE_CHEST`:
    - spawnt `EntityType.BUILD_CHEST` (wenn nicht auf Road-Tile + kein Nearby-Chest)
    - Guard: `onRoad` Check via `layers.roadMask[lidx]`
    - Distanz-Check: `hasNearbyAny(..., EntityType.BUILD_CHEST)`
  - `OLD_MINE`:
    - `spawnMineCluster(...)`
    - spawnt zentral `EntityType.BUILD_CHEST` + Ring aus Nodes (`NODE_ORE_IRON` / `NODE_ROCK`)

## 4) Roads / Road-POI-Kopplung
**Datei:** `core/src/main/java/com/yourgame/survival/worldgen/roads/WorldRoadPlanner.java`
- nutzt POI templates/flags (Kommentar: „source of POI templates/road enable flags“)
- Anchor/Connect Radius relevant: `requiresRoad`, `connectRadiusTiles`, `roadClass`

## 5) Chest-System (POI-Loot / Interaktion / Speicherung)

### Runtime State
**Datei:** `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
- `ChestStore chestStore`
- `int openChestE`
- `drawChestPanel()` UI
- `tryToggleChest()` (öffnet/schließt via Entity in Reichweite)

### Datenspeicher
**Datei:** `core/src/main/java/com/yourgame/survival/data/ChestStore.java`
- verwaltet ArrayList<Inventory> chests
- `createChest()`, `get(idx)`, `clear()`

### Save/Load
**Datei:** `core/src/main/java/com/yourgame/survival/data/SaveManager.java`
- Speichert für `EntityType.BUILD_CHEST` extra payload: `"chest": ...`
- Beim Load: re-alloc chest index und befüllt `ChestStore`

## 6) Rendering / Sprites für POI-relevante Objekte

### Chest Sprite (World-Objekt)
**Datei:** `core/src/main/java/com/yourgame/survival/render/EntityRegions.java`
- Kommentar: `build_chest.png` ist „reserved for multiplayer POI chest (server-spawned)“
- Aktuelles Mapping: `buildChest = reqStatic("build_crate_small")`
- `case BUILD_CHEST -> buildChest`

### POI Marker Sprites (separat)
**Datei:** `core/src/main/java/com/yourgame/survival/render/EntityRegions.java`
- `staticRegion(String name)` → direkte Atlas-Region lookup ("used by MP POI markers")

## 7) Editor / Authoring (POI Kategorie)

**Datei:** `core/src/main/java/com/yourgame/survival/spawn/SpawnTypeId.java`
- Enthält viele authorable ids (CHEST_*, RUIN_*, CAVE_*) – aktuell nicht alle runtime-gemappt.

**Datei:** `core/src/main/java/com/yourgame/survival/spawn/SpawnTypeRegistry.java`
- Auto-kategorisiert `SpawnCategory.POI` wenn Name contains `CHEST`/`RUIN`/`CAVE`.

**Datei:** `core/src/main/java/com/yourgame/survival/editor/WorldEditorScreen.java`
- Farbcoding: `case POI, BUILD -> ...`

## 8) POI Keys vs SpawnTypeIds vs EntityTypes (WICHTIGES Mismatch-Risiko)
- Worldgen POIs (heute) benutzen **String keys** (`TREASURE_CHEST`, `OLD_MINE`).
- Editor/Registry benutzen **SpawnTypeId Enum** (CHEST_COMMON, CHEST_RARE, ...), aber diese sind noch nicht im Runtime-WorldNodeSpawner angebunden.
- In-World echte Objekte sind `EntityType.*` (z.B. `BUILD_CHEST`).

---

## Offene TODOs / Fragen für heute
1) Sollen POIs als **eigene EntityTypes** existieren (z.B. POI_CHEST vs BUILD_CHEST)?
2) Sollen POIs Loot-Tabellen haben (items.json/recipes.json/shops.json?) oder hardcoded?
3) Wie sollen POIs gerendert werden: als Welt-Objekt-Sprite, zusätzlich als Marker (staticRegion)?
4) Welche POI keys sollen supported werden (Liste)?

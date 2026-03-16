# BIOME_AREA_MERKZETTEL.md (scan)

Ziel: Alles über Biome + Gebiete/Areas (Story-Mode) lokalisieren: Datenmodelle, Config-Files, Loader, Worldgen Pipeline, Biome-Klassifizierung, Tile/Ground Mapping, Masks.

## 0) Quick Map / Begriffe
- **Biome** = Kategorie/Regelwerk für Tiles/Spawns/POIs/Roads.
- **Area / Gebiet** = ein geladenes „Level“/Template (HOME, GEN_*), das in `assets/areas/*.area.json` beschrieben ist und in den WorldMap-Flow eingebunden ist.
- Das Spiel läuft in **Areas-only** (siehe GameScreen Kommentare: Procedural mode removed).

---

## 1) Biome Runtime + Config

### Zentrale Klasse
**Datei:** `core/src/main/java/com/yourgame/survival/biome/BiomeSystem.java`

**Hauptaufgaben:**
- Hält `BiomeDef[] defs` (indexed by `Biome.ordinal()`)
- Lädt/schreibt `config/biomes.json`
- Liefert:
  - `spawnRulesFor(Biome)` → Node-Spawn-Regeln pro Chunk
  - `computePoisForChunk(...)` → POIs pro Chunk (wird auch vom Road Planner genutzt)
  - `groundFor(center, neighbor, inEdgeBand)` → groundId Ableitung inkl. Edge-Blending
  - Zone-Mask / Height-Mask / Deco-Mask Zugriff

**Config-Pfade:**
- `config/biomes.json` (LOCAL_PATH)
- Zone masks: `config/biome_masks/`
- Height masks: `config/biome_height/`
- Deco masks: `config/biome_deco/`

**BiomeDef (wichtigste Felder):**
- `short groundId` (default ground tile id)
- `long previewSeed`
- `boolean roadsEnabled`
- `int edgeWidthTiles`
- `Map<String,Integer> edgeGroundOverride` (neighbor biome name -> groundId)
- Optional Zones:
  - repeating rect zones (chunk-local)
  - painted zone masks (PNG)
  - terraced height mask (PNG)
  - deco density mask (PNG)
- `ArrayList<SpawnRule> spawnRules`
- `ArrayList<PoiTemplate> poiTemplates`

**SpawnRule (Node stamps):**
- Zone/Type ("TREE", "ROCK", "IRON", "FISH" etc.)
- `minCount/maxCount` per chunk
- Jitter params (treeJitterX/Y) etc.
- Optional neighbor filter for EDGE rules (`neighborBiome`)
- Es gibt Defaults + Fail-safe: wenn JSON leer => defaults werden wieder eingefügt.

**POI (Biome-bezogen):**
- `PoiTemplate` / `PoiSpawn` (siehe POI_MERKZETTEL.md; hier nur die Verbindung: POIs werden pro Biome definiert und deterministisch pro Chunk erzeugt)

### Unterstützende Utils
**Dateien:**
- `core/src/main/java/com/yourgame/survival/biome/HeightMaskUtil.java`
- `core/src/main/java/com/yourgame/survival/biome/DecoMaskUtil.java`

---

## 2) Biome Enum + Tile IDs + Transition Rules

### Biome Enum
**Datei:** `core/src/main/java/com/yourgame/survival/world/Biome.java`
- Definiert die Biome-Typen, die von BiomeSystem/Classifier genutzt werden.

### Tile IDs
**Datei:** `core/src/main/java/com/yourgame/survival/world/TileIds.java`
- Definiert Konstanten für ground/overlay IDs (wichtig für `BiomeSystem.groundFor` und TilesetConfig).

### Transition/Adjacency
**Dateien:**
- `core/src/main/java/com/yourgame/survival/world/TransitionRules.java`
- `core/src/main/java/com/yourgame/survival/worldgen/adjacency/*`
- `core/src/main/java/com/yourgame/survival/worldgen/paint/*`

Zweck: Übergänge zwischen Biomen/Tiles (edge masks, adjacency baking).

---

## 3) Worldgen Pipeline (Chunks/Tiles/Roads)

### Worldgen Config
**Datei:** `core/src/main/java/com/yourgame/survival/worldgen/WorldGenConfig.java`
- Lädt `config/worldgen.json`

**Asset:** `assets/config/worldgen.json`

### Pipeline/Orchestrator
**Dateien:**
- `core/src/main/java/com/yourgame/survival/worldgen/pipeline/ChunkGenOrchestrator.java`
- `core/src/main/java/com/yourgame/survival/worldgen/WorldGenerator.java`
- `core/src/main/java/com/yourgame/survival/worldgen/WorldGenContext.java`

### Biome Classifier
**Dateien:**
- `core/src/main/java/com/yourgame/survival/worldgen/biome/BiomeClassifier.java`
- `core/src/main/java/com/yourgame/survival/worldgen/biome/DefaultBiomeClassifier.java`

### Roads
**Dateien:**
- `core/src/main/java/com/yourgame/survival/worldgen/roads/WorldRoadPlanner.java`
- `core/src/main/java/com/yourgame/survival/worldgen/roads/RoadPlanner.java`
- `core/src/main/java/com/yourgame/survival/worldgen/roads/RoadAdjacencyBaker.java`

BiomeSystem Hinweis: Roads werden in der worldgen pipeline generiert, und POI Templates/Flags sind Eingabe dafür.

---

## 4) World / Chunks / Layers

**Dateien:**
- `core/src/main/java/com/yourgame/survival/world/World.java`
- `core/src/main/java/com/yourgame/survival/world/Chunk.java`
- `core/src/main/java/com/yourgame/survival/world/ChunkStore.java`
- `core/src/main/java/com/yourgame/survival/world/TileLayers.java`

Relevante Layer:
- `groundId[]`
- `waterMask[]`
- `collisionMask[]`
- `roadMask[]`
- `overlayId[]`
- `decoId[]`, `decoVar[]`

---

## 5) Areas / Gebiete / WorldMap (Story Mode)

### WorldMap Runtime + State
**Dateien:**
- `core/src/main/java/com/yourgame/survival/worldmap/WorldMapRuntime.java`
- `core/src/main/java/com/yourgame/survival/worldmap/WorldMapState.java`
- `core/src/main/java/com/yourgame/survival/worldmap/WorldMapTravelController.java`
- `core/src/main/java/com/yourgame/survival/worldmap/AreaTemplate*.java` (Registry/Template/Coord)

**Alpha Template IDs (hardcoded):**
- `HOME` (`WorldMapRuntime.T_HOME`)
- `GEN_GRASSLAND`
- `GEN_ROCKY_FIELDS`
- `GEN_LIGHT_FOREST`

### Area Loader (Asset-backed)
**Datei:** `core/src/main/java/com/yourgame/survival/worldmap/JsonAreaWorldLoader.java`
- Lädt: `assets/areas/<templateId>.area.json`
- Setzt Welt zurück: `gs.areaResetWorld(areaSeed)`
- Löscht area-lokale Entities: `gs.areaClearAreaLocalEntities()`
- Wendet Layer an:
  - ground: defaultId + fills + patches
  - optional water mask: fills + patches
  - cleared: collision/roads/overlay/deco
- Spawnt area-lokale „Deko/Life“:
  - HOME: `beautifyHome(...)`
  - GEN_LIGHT_FOREST: `spawnForestTreesAndDeer(...)`
- Optional Marker: `markers.playerSpawn` (tile coords)

### Area Assets vorhanden
**Ordner:** `assets/areas/`
- `HOME.area.json`
- `GEN_GRASSLAND.area.json`
- `GEN_ROCKY_FIELDS.area.json`
- `GEN_LIGHT_FOREST.area.json`

---

## 6) Spawn Settings (per Biome?)
**Datei:** `core/src/main/java/com/yourgame/survival/world/SpawnSettings.java`
**Asset:** `assets/config/spawn_settings.json`
- regelt spawn tuning / caps / weights (Details bei Bedarf separat aufschlüsseln).

---

## 7) GameScreen Integration (Area Mode)
**Datei:** `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
- Hält `WorldMapState worldMap` + `WorldMapRuntime worldMapRt` + `WorldMapTravelController`.
- Kommentare: Spiel läuft in Areas-only.
- Home-area special rule: „HOME must spawn NO enemies“ (bei Encounters).

---

## Offene TODOs / Fragen für heute
1) Biomes.json vs Area JSON: Welche Quelle ist „Master“ für Tiles? (Area JSON überschreibt ground layer komplett beim Laden.)
2) Welche POIs sind area-authored vs biome-procedural? (WorldNodeSpawner nutzt BiomeSystem.computePoisForChunk → wirkt dann in Areas genauso, wenn NodeSpawner läuft.)
3) Roads: In authored Areas wird `roadMask` beim Laden auf 0 gesetzt – sollen Areas eigene Roads haben?

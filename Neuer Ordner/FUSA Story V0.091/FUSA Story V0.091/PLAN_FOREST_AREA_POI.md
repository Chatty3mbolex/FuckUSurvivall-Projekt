# PLAN – Forest Area #1 (FOREST_01) + POI Rework + Node/Ork/Chest Systems

**Scope (per user request 2026-03-06):**
- Create the first *fixed* Forest area based on `Gebietslayout_forest.png` + legend.
- Remove the two existing POI templates (`TREASURE_CHEST`, `OLD_MINE`) completely.
- Add a new one-time POI: **Hidden Chest** (`Hidden_Chest.png`) with loot rules, disappear when empty, no respawn.
- Add fixed, non-respawning nodes (trees + some rocks/iron/bush) placed from the painted template.
- Add enemy spawn zones (10–15 Orks per zone, respawn 1–2 in-game days, wander inside zone, obey attention rules like existing wandering orks).
- Fix item icon mapping: **Arrow (id 47) must use `icon: item_47`**.

**Constraints / Guardrails / Failsafes**
- **No online/network tools** (browser/memory_search/image/curl/etc.).
- **Minimize file touches**: each file edited in one consolidated pass.
- **When editing a file in multiple places:** apply edits **bottom → top** to avoid line-number drift.
- **No compilation while coding**: only compile once at the end.
- UI guardrail already learned: **never early-return while ScissorStack is pushed**.
- When adding new stateful systems (POI consumed / node removed), ensure **Save/Load** persists state.

---

## 0) Inputs / Assets / IDs (confirmed)

### Item IDs (final)
- Arrow = **47** (ammo)
- Copper coins = **31**
- Gold coin = **33**
- Iron ore = **2**
- Sword = **20**

### POI Sprite
- Hidden chest sprite source PNG: `assets/atlas/src_static/Hidden_Chest.png`
- MUST be pulled from **static atlas** like everything else.

### Template images
- Area layout image: `Gebietslayout_forest.png`
- Legend: `Gebietslayout_forest_legende.png`

---

## 1) One-time scan output to keep in mind (current line anchors)

### Remove old POIs
- **BiomeSystem.java**: default POIs at lines **461–466** (as of now)
  - `OLD_MINE` at **463**
  - `TREASURE_CHEST` at **465**
- **WorldNodeSpawner.java**: POI spawn handling at lines **183–208**
  - TREASURE_CHEST branch **191–203**
  - OLD_MINE branch **204–207**

### Arrow icon bug
- **assets/data/items.json**: Arrow entry at **line 55** currently has `"icon":"item_8"`.

### Entity types (current)
- **EntityType.java**: `BUILD_CHEST` at **line 17**, `ITEM_DROP` at **line 26**.

> Note: all line numbers in this plan are “pre-change” reference points.

---

## 2) Deliverables (end state)

1) `assets/data/items.json`: Arrow icon fixed to `item_47`.
2) New authored area asset: `assets/areas/FOREST_01.area.json` derived from the painted template.
3) Loader supports new area layers beyond ground/water:
   - road mask
   - node placements
   - enemy spawn zones
   - POI (hidden chest)
4) New runtime entity type for POI hidden chest (recommended): `POI_CHEST_HIDDEN`.
5) POI chest loot system + persistence + remove-on-empty + toast texts.
6) Node permanence: harvested/removed nodes do **not respawn** in this area.

---

## 3) Implementation plan (files, edits, ordering)

### Step A — Fix Arrow icon mapping (single edit)
**File:** `assets/data/items.json` (≈ 49 lines)
- Locate Arrow item definition (currently line **55**):
  - From: `{"id":47,...,"icon":"item_8"...}`
  - To:   `{"id":47,...,"icon":"item_47"...}`

**Guardrail:** keep JSON valid (no trailing commas errors).

---

### Step B — Remove old POIs globally (Biome + Spawner)

#### B1) Remove default POI templates
**File:** `core/src/main/java/com/yourgame/survival/biome/BiomeSystem.java` (≈ 835 lines)
- **Bottom→top edit order within file** (only one consolidated patch, but conceptually do lowest block last).

**Target block:** around lines **461–466**:
```java
// POI templates
if (b == Biome.MOUNTAIN || b == Biome.VOLCANIC || b == Biome.ASHFIELD) {
  d.poiTemplates.add(new PoiTemplate("OLD_MINE", ...));
}
d.poiTemplates.add(new PoiTemplate("TREASURE_CHEST", ...));
```
**Change:** delete these defaults entirely OR replace with an empty list default.

**Fail-safe:** keep the “biomes not barren” fail-safe logic intact: it currently re-adds defaults if both spawnRules and poiTemplates are empty (see around lines **671–675**). Adjust that logic so it **does not re-insert old POIs**:
- Either: only re-add spawnRules, not poiTemplates.
- Or: re-add poiTemplates only if we want any global defaults (we don’t).

#### B2) Remove spawner branches for old POIs
**File:** `core/src/main/java/com/yourgame/survival/world/WorldNodeSpawner.java` (≈ 461 lines)
- Target block lines **183–208**.
- Remove both:
  - `TREASURE_CHEST` branch (191–203)
  - `OLD_MINE` branch (204–207)
- After removal, the POI loop can be left empty, or replaced with new per-area POI mechanism.

**Fail-safe:** leave the rest of node spawning intact.

---

### Step C — Introduce FOREST_01 authored area (no randomness except allowed)

#### C1) Add new area JSON
**File (new):** `assets/areas/FOREST_01.area.json`

**Structure proposal** (extending current area format):
```json
{
  "size": {"w":384,"h":384},
  "layers": {
    "ground": {"defaultId": <grassOrDirt>, "fills": [...], "patches": [...]},
    "road":   {"fills": [...], "patches": [...]}
  },
  "markers": {
    "playerSpawn": {"x":...,"y":...},
    "poi": [ {"kind":"HIDDEN_CHEST", "x":...,"y":...} ],
    "nodes": [
      {"t":"NODE_TREE","x":..,"y":..},
      {"t":"NODE_ROCK","x":..,"y":..},
      {"t":"NODE_ORE_IRON","x":..,"y":..},
      {"t":"NODE_BUSH","x":..,"y":..}
    ],
    "enemyZones": [
      {"kind":"ORK_ZONE", "cx":..,"cy":.., "r":.., "min":10, "max":15, "respawnDaysMin":1, "respawnDaysMax":2}
    ]
  }
}
```

**Conversion tool (offline):** create a local script that reads `Gebietslayout_forest.png` and outputs:
- ground fills/patches
- road mask patches
- node positions
- enemy zone circles
- poi chest point

**Guardrails for conversion:**
- Use strict color matching or nearest-color buckets (avoid anti-aliased edges causing noise).
- Snap all outputs to tile coordinates 0..383.
- Keep everything deterministic for this template.

**Files for tool (new):**
- `tools/area_from_layout_forest.py` (or similar)
- Output: `assets/areas/FOREST_01.area.json`

---

### Step D — Extend Area loader to apply road + spawn nodes + zones + POI

**File:** `core/src/main/java/com/yourgame/survival/worldmap/JsonAreaWorldLoader.java` (≈ 682 lines)

Current loader already:
- loads ground fills/patches
- optional water
- clears masks (collision/road/overlay/deco)
- spawns some home/forest fluff

**Required additions:**

1) **Road layer application**
- Add parsing for `layers.road` and write into `TileLayers.roadMask[idx]` (e.g. 1 for road tiles).
- Keep existing “clear masks” behavior but apply road after clearing.

2) **Node spawning from markers.nodes**
- For each node marker, spawn `EntityType.NODE_*` at tile center.
- Respect `WorldNodes.isRemoved(...)` so nodes do not respawn if harvested.

3) **Enemy zones from markers.enemyZones**
- Store zone definitions into GameScreen runtime state for periodic respawn logic.
  - (Prefer: a new small data holder class `EnemyZoneDef` under GameScreen or a new system class.)

4) **POI placement from markers.poi**
- Spawn `POI_CHEST_HIDDEN` entity at tile center.
- Ensure persistence (see Step F).

**Fail-safes:**
- If any section is missing in JSON, loader should silently skip (area still loads).
- Clamp coordinates.

**Edit order inside file:** bottom→top if multiple blocks are inserted; but prefer one large insertion near `loadInto()` where layers/markers are processed.

---

### Step E — Add new POI entity type + rendering

#### E1) Add new EntityType
**File:** `core/src/main/java/com/yourgame/survival/entity/EntityType.java` (lines 1–27)
- Insert new enum constant:
  - `POI_CHEST_HIDDEN,`

**Placement:** near other BUILD/landmark types (e.g. after `BUILD_LAMP` or after `LANDMARK_BRIDGE`).

**Warning:** this changes ordinal() values. We must ensure saves don’t rely on ordinals. (Your SaveManager uses `EntityType.valueOf(name)` for load, so it’s safe.)

#### E2) Rendering mapping
**File:** `core/src/main/java/com/yourgame/survival/render/EntityRegions.java`
- Add `TextureRegion poiChestHidden;` and load:
  - `poiChestHidden = reqStatic("Hidden_Chest");`
- In `forEntity(...)` switch add:
  - `case POI_CHEST_HIDDEN -> poiChestHidden;`

**Fail-safe:** if missing atlas region, throw early as other reqStatic does.

#### E3) Metrics
**File:** `core/src/main/java/com/yourgame/survival/entity/EntityMetrics.java`
- Add drawW/drawH cases for `POI_CHEST_HIDDEN`.
  - Likely reuse `DRAW_W_BUILD_CHEST` or create new constants in `TuningEntities`.

**Files potentially touched:**
- `core/src/main/java/com/yourgame/survival/tuning/TuningEntities.java` (add fixed draw width for POI chest if needed)

---

### Step F — Hidden POI chest: loot + persistence + disappear-on-empty

We need to keep this chest **separate** from normal BUILD_CHEST:
- Unique sprite
- One-time spawn per area template
- Loot rules
- Permanent removal when empty

#### F1) Data model for POI chest content
Option 1 (minimal changes): reuse existing `ChestStore` inventories.
- When spawning `POI_CHEST_HIDDEN`, allocate a chest index in `ChestStore` and store it in `entities.data0[e]`.
- Then the existing chest UI can show its contents with minimal new UI.

**Files:**
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
  - allow opening POI chest in `tryToggleChest()` (it currently checks for `EntityType.BUILD_CHEST`).
  - add “found” toast text for 15s on open.
  - detect when chest becomes empty -> despawn entity and show “disappeared” toast 15s.
  - mark POI as consumed (see F2)

#### F2) Permanent removal tracking for POIs
We need persistence so the POI does not come back when re-entering area.

Add a new persistent set to `WorldMapState` (recommended):
- e.g. `HashSet<String> consumedPois` with keys like `"FOREST_01|HIDDEN_CHEST|tx|ty"`.

**Files:**
- `core/src/main/java/com/yourgame/survival/worldmap/WorldMapState.java`
- `core/src/main/java/com/yourgame/survival/data/SaveManager.java`
  - save/load this new list.

Loader (JsonAreaWorldLoader) must:
- Before spawning POI, check `worldMapState` consumed set; if consumed => skip spawn.

**Fail-safe:** if save lacks this field, treat as empty set.

#### F3) Loot generation
When POI chest is first created (if it has no inventory yet), fill it:
- Arrows (id 47): 0..20
- Copper coins (id 31): 0..500
- Iron ore (id 2): 0..20
- Gold coin (id 33): 0..1 with very low probability
- Sword (id 20): 0..1 with very low probability
- All counts random “always random amount” (can include 0). Gold/Sword rare.

**Where to implement:**
- In loader when creating the chest inventory for this POI.
- Seed RNG deterministically using `areaSeed + tx/ty` so loot is stable across loads until taken.

**Files:**
- `JsonAreaWorldLoader.java` (loot fill function)

---

### Step G — Enemy spawn zones (Orks) with respawn and wandering

We need:
- fixed zones (from template circles)
- spawn 10–15 orks per zone
- respawn after 1–2 in-game days
- orks wander within zone
- follow existing attention rule

**Implementation approach:**
1) Add `EnemyZone` runtime list in `GameScreen`:
   - fields: center (world), radius (world), desiredCountMin/Max, respawnTimer, seeded RNG key.
2) On area load, populate these zones from JSON.
3) On tick (simulation), for each zone:
   - count current alive orks in zone
   - if below target and respawnTimer <= 0: spawn missing
   - set respawnTimer to random days in [1,2]
4) For wandering + attention:
   - reuse existing ork AI code path used for wandering orks.
   - enforce zone bounds by steering: if ork outside radius, set its wander target back inside.

**Files likely:**
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java` (zone state + tick + spawn)
- Possibly existing AI system file if it exists for wandering orks (search for wander logic in systems package).

**Fail-safe:** if zones missing, do nothing.

---

### Step H — Nodes are non-repeating (harvest -> removed)

Requirement: nodes placed from the area are **not respawning** after harvest.

Currently:
- `WorldNodes` supports `isRemoved(...)` and spawner respects it.
- But harvesting code kills entities without marking removed.

**Plan:**
- When a node is harvested and killed (tree/rock/ore/bush/fishspot), compute its tile coordinate and call `worldNodes.removed.add(WorldNodes.key(type, tx, ty))`.

**Where best:**
- In `GameScreen` where harvest event is applied (it already has world/WorldNodes and knows event position and harvestedType).

**Files:**
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java` around harvest tick handling (~ lines 1738+)
  - after `es.kill` is executed via HarvestSystem, we still have event with `harvestedType` + `x/y`.
  - mark removed based on `harvestedType` and event position.

**Fail-safe:** only mark removed for node types, ignore others.

---

## 4) File touch list (intended: once per file)

1) `assets/data/items.json` (Arrow icon)
2) `core/src/main/java/com/yourgame/survival/biome/BiomeSystem.java` (remove old POI defaults + fail-safe adjustment)
3) `core/src/main/java/com/yourgame/survival/world/WorldNodeSpawner.java` (remove old POI branches)
4) `assets/areas/FOREST_01.area.json` (new)
5) `tools/area_from_layout_forest.py` (new)
6) `core/src/main/java/com/yourgame/survival/worldmap/JsonAreaWorldLoader.java` (apply road + spawn nodes + zones + POI + loot)
7) `core/src/main/java/com/yourgame/survival/entity/EntityType.java` (add POI entity)
8) `core/src/main/java/com/yourgame/survival/render/EntityRegions.java` (sprite mapping)
9) `core/src/main/java/com/yourgame/survival/entity/EntityMetrics.java` (+ maybe `TuningEntities.java`)
10) `core/src/main/java/com/yourgame/survival/worldmap/WorldMapState.java` (persist consumed POIs)
11) `core/src/main/java/com/yourgame/survival/data/SaveManager.java` (save/load consumed POIs)
12) `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
    - open POI chest UI + toasts
    - apply harvest removed nodes
    - enemy zone tick/spawn

> If we find existing wandering ork AI hooks in a separate system, that file may be added, but we’ll keep it to a minimum.

---

## 5) End-of-work actions (only after all code edits)
1) Run a single compile/build (`:core:compileJava` + desktop run if needed).
2) Test plan:
   - Enter `FOREST_01`
   - Verify road mask draws correctly
   - Verify trees/nodes appear in expected rings
   - Harvest nodes -> leave area -> re-enter -> nodes stay removed
   - Enemy zones spawn 10–15 orks and keep them inside radius
   - Wait/fast-forward 1–2 in-game days -> respawn if killed
   - POI hidden chest spawns once, loot random, disappears when empty and never returns
   - Toast messages show 15 seconds on open and on disappear
3) Commit changes (exclude build artifacts / .gradle / core/build).

---

## 6) Notes about existing workspace noise (do NOT include in commit)
Your repo currently shows many modified binary/build files and atlas outputs. For a clean commit:
- Add/update `.gitignore` for `.gradle/`, `core/build/`, `core/bin/`, `assets/atlas/*.png` (unless you want packed atlases committed).
- Only stage source + data assets intentionally.

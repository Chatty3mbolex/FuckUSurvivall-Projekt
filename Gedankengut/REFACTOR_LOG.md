# REFACTOR LOG — FUSA Story V0.092 Forked

```yaml
generated_by: "Claude Opus 4.6 — Deep Analysis"
project: "FuckUSurvivALL (FUSA Story V0.092 Forked)"
project_type: "Java / Gradle / LibGDX"
total_files: 211
total_lines: 41886
commented_lines: 3162
nicht_fertig_tags: 102
blank_lines: 6113
analysis_date: "2026-03-19"
```

---

## EXECUTIVE SUMMARY

The codebase is a working 2D-to-2.5D survival RPG with ~42K lines of Java across 211 files.
The project has **one catastrophic god class** (GameScreen.java: 9778 lines, 536 fields),
several large secondary god classes, ~102 "Nicht fertiges Feature" placeholders, 292 inline
fully-qualified-name references (lazy imports), and scattered resource leak suspects.

**Technical Debt Severity: HIGH**

The #1 priority is splitting GameScreen.java. Everything else is secondary.

---

## TABLE OF CONTENTS

1. [Summary Counters](#summary-counters)
2. [Top 10 Priority Refactors](#top-10-priority-refactors)
3. [CRITICAL: GameScreen Decomposition Plan](#critical-gamescreen-decomposition-plan)
4. [God Class Inventory](#god-class-inventory)
5. [Resource Leak Suspects](#resource-leak-suspects)
6. [Circular Dependencies](#circular-dependencies)
7. [Dead Code & Commented Blocks](#dead-code--commented-blocks)
8. [Missing Imports & Inline FQN](#missing-imports--inline-fqn)
9. [Magic Numbers Hotspots](#magic-numbers-hotspots)
10. [Style & Naming Issues](#style--naming-issues)
11. [2.5D Integration Issues](#25d-integration-issues)
12. [Refactor Clusters](#refactor-clusters)
13. [Chatty Workflow Instructions](#chatty-workflow-instructions)

---

## SUMMARY COUNTERS

| Category | Count |
|----------|-------|
| Files analyzed | 211 |
| Total issues found | ~487 |
| CRITICAL (resource leaks, god classes) | 18 |
| STRUCTURAL (god methods, mixed concerns) | 42 |
| DEPENDENCY (circular, missing imports) | 31 |
| DEAD CODE (comments, Nicht Fertig, unused) | 298 |
| STYLE (magic numbers, inline FQN, naming) | ~98 |

---

## TOP 10 PRIORITY REFACTORS

| Rank | Category | What | Where | Impact |
|------|----------|------|-------|--------|
| 1 | CRITICAL/STRUCTURAL | God class: 9778 lines, 536 fields | `screens/GameScreen.java` | Entire project |
| 2 | STRUCTURAL | God class: 3794 lines, 220 fields | `editor/WorldEditorScreen.java` | Editor stability |
| 3 | CRITICAL | Circular dependency GameScreen ↔ worldmap/debug | `worldmap/*.java`, `debug/DebugCommands.java` | Architecture |
| 4 | DEAD CODE | 102 "Nicht fertiges Feature" blocks + 23 comment blocks in GameScreen alone | Project-wide, worst: `GameScreen.java` | Readability |
| 5 | STYLE | 292 inline FQN references instead of imports | `screens/GameScreen.java` | Readability |
| 6 | STRUCTURAL | God method render(): 814 lines | `GameScreen.java:1458` | Maintainability |
| 7 | STRUCTURAL | God method tick(): 375 lines | `GameScreen.java:3627` | Maintainability |
| 8 | CRITICAL | Resource leaks: TilesetRegions creates=17 disposes=4 | `render/TilesetRegions.java` | Memory |
| 9 | CRITICAL | Resource leaks: MiniSkin creates=4 disposes=1 | `tools/asseteditor/ui/MiniSkin.java` | Memory |
| 10 | STRUCTURAL | 263 magic numbers in GameScreen | `screens/GameScreen.java` | Maintainability |

---

## CRITICAL: GAMESCREEN DECOMPOSITION PLAN

GameScreen.java is **9778 lines** with **536 fields**, **~120 methods**, **7 inner classes**, and **65 "Nicht fertiges Feature" tags**. It handles rendering, input, combat, inventory, shops, quests, crafting, world map, fog of war, day/night, UI panels, debug tools, save/load, and more.

### IMPORTANT: Already-Existing Thin Wrapper Controllers

Chatty has ALREADY created 6 "extracted" controller files in `screens/`, but they are
**thin wrappers that delegate everything back to GameScreen**. They contain NO actual logic:

| File | Lines | What it does |
|------|-------|-------------|
| `CombatController.java` | 14 | Calls `gs.drawEntityHealthBarsWorld()` + `gs.drawArrowsWorld()` |
| `CursorController.java` | 13 | Calls `gs.drawDotCursorWorld()` |
| `DebugOverlay.java` | 18 | Calls `gs.drawHoverIdsAtCursor()` |
| `GameInputController.java` | 55 | Delegates all InputAdapter methods to an inner delegate |
| `HudRenderer.java` | 13 | Calls `gs.renderUI()` |
| `WorldView.java` | 13 | Calls `gs.renderWorld()` |

**These are NOT real extractions.** The methods and fields are still in GameScreen.
The refactor needs to MOVE the actual logic into the controllers, not just wrap it.

**WARNING TO CHATTY:** Do NOT consider these "done". They need to be either:
- FILLED with the actual extracted logic (preferred), or
- DELETED and recreated properly during Phase 5

### Proposed Split into Sub-Controllers

Each sub-controller gets extracted as its own class with the ACTUAL logic moved out of GameScreen.
GameScreen keeps a reference to each and delegates. The existing thin wrappers above should be
replaced or expanded with real implementations.

#### 1. `GameRenderer` — Rendering & Camera (extract ~1500 lines)

**Source lines:** ~1458–2270 (render method), ~2645–2900 (debug overlay, day/night mask), ~8205–8400 (fog of war)

**Fields to move:**
- `cam`, `uiCam`, `batch`, `font`, `shape` (line 61–65)
- `dayNight1x1`, `dnLightTex`, `dnMaskFbo`, `dnMaskRegion` (day/night rendering)
- `fowTex`, `fowPm` (fog of war textures)
- All `draw*` helper methods

**Methods to move:**
- `drawDebugModeOverlay()` (L2645, 30 lines)
- `tickBootOverlay()` (L2675, 36 lines)
- `updateDnMask()` (L2804, 79 lines)
- `rebuildDnMaskFbo()` (L2883, 18 lines)
- `renderFogOfWarOverlayWorld()` (L8205, 99 lines)
- `areaFogRevealTick()` (L8304, 96 lines)
- `drawActionOverlay()` (L3530, 97 lines)
- `toVoxelRenderSpace()` (L3195, 196 lines)

**NOTE:** `updateDayNightMusic()` (L2711, 93 lines) handles audio crossfade logic,
NOT rendering. It should go into a separate `DayNightController` or into `AudioBus`
along with `ensureDayBgPlaying()`, `ensureNightBgPlaying()`, `playDayBg()`, `playNightBg()`,
and the music fields (`dayBgMusic`, `nightBgMusic`, `dayBgVol`, `nightBgVol`, `dayBgTracks[]`, etc.).

#### 2. `GameUiController` — All UI Panels (extract ~2500 lines)

**Source lines:** ~5109–7560 (shops, inventory, crafting, quest log, chest, stats, hotbar, build, wallet)

**Methods to move:**
- `drawShopMenuAtMerchant()` (L5109, 132 lines)
- `drawWqgDialog()` (L5241, 164 lines)
- `drawQuestLogHubPanel()` (L5405, 100 lines)
- `drawQuestLogOpenQuestsPanel()` (L5505, 190 lines)
- `drawQuestLogDoneQuestsPanel()` (L5695, 183 lines)
- `uiHandleDragDropAndPopup()` (L6097, 186 lines)
- `drawDragGhostAndBuyPopup()` (L6602, 125 lines)
- `drawCraftPanel()` (L6979, 247 lines)
- `drawChestPanel()` (L7226, 145 lines)
- `drawStatsPanel()` (L7371, 37 lines)
- `drawHotbarHud()` (L7437, 39 lines)
- `drawInventoryGrid()` (L9011, 166 lines)
- `drawWalletMenu()` (L9285, 73 lines)
- `drawSkillMenu()` (L9508, 216 lines)
- `drawInventoryLines()` (L7476, 17 lines)
- `drawBuildLines()` (L7493, 10 lines)
- `drawChestLines()` (L7521, 14 lines)

**Fields to move:**
- All `shop*`, `inv*`, `craft*`, `chest*`, `questLog*`, `wallet*`, `skill*` UI state fields
- `uiLayout` (GlyphLayout)
- `walletCoinCopperTex`, `walletCoinSilverTex`, `walletCoinGoldTex`
- `skillIconTex` HashMap
- All `SkillMenuRow` inner class + related

#### 3. `ProjectileCombatController` — Combat & Projectiles (extract ~600 lines)

**NOTE:** `CombatController.java` already exists as a thin wrapper (14 lines).
Rename it or replace it. Use name `ProjectileCombatController` to avoid clash.
Also note: `systems/CombatSystem.java` exists separately (handles damage calc).

**Source lines:** ~3391–3530 (arrows), ~4436–4520 (kills, interaction)

**Fields to move:**
- `arrowAlive[]`, `arrowX[]`, `arrowY[]`, `arrowVx[]`, `arrowVy[]` (L118–125)
- `arrowTravel[]`, `arrowRange[]`, `arrowDmg[]` (L123–125)
- `bowCooldownT` (L126)
- `rmbHoldActive`, `rmbPulseT`, `rmbHitT` (L135–137)
- `sneaking`, `sneakingPrev` (L140–141)

**Methods to move:**
- `tickArrows()` (L3391, 57 lines)
- `arrowHitSegment()` (L3448, 82 lines)
- `onKill()` (L4436, 19 lines)

#### 4. `WorldMapController` — Area Travel & World Map (extract ~800 lines)

**Source lines:** ~7567–8163 (world map, area travel), ~8457–8660 (area API)

**Methods to move:**
- `debugWorldMapTravel()` (L7674, 56 lines)
- `areaOnLeaveCurrent()` (L7730, 40 lines)
- `captureMiniMapB64()` (L7770, 35 lines)
- `drawWorldMapOverview()` (L7811, 70 lines)
- `drawSelectedAreaMap()` (L7881, 183 lines)
- `areaTryTravelAtEdge()` (L8064, 74 lines)
- All `area*` public API methods (L8457–8660)
- `EnemyZone` inner class (L8490)
- `areaEnemyZonesTick()` (L4090, 61 lines)

#### 5. `PlayerController` — Player State & Movement (extract ~400 lines)

**Fields to move:**
- `mouseWorldX`, `mouseWorldY` (L100–101)
- `lastCursorHidden`, `drawUnarmedDotCursor` (L104–105)
- All `SK_*` skill index constants (L71–97)
- `playerE` (L171)
- `inv`, `wallet`, `progress`, `needs` (L172–175)

**Methods to move:**
- `updateMouseWorld()` (L3144, 51 lines)
- `collectInputSnapshot()` (L2980, 15 lines)
- `actionReach()` (L3001, 33 lines)
- `equippedFromHotbar()` (L3034, 9 lines)
- `facingVec()` (L3043, 9 lines)
- `pickupNearby()` (L4455, 29 lines)

#### 6. `SaveLoadController` — Save/Load Logic (extract ~300 lines)

**Methods to move:**
- `doSave()` (L8728, 45 lines)
- `doLoad()` (L8773, 190 lines)
- `clearAllStateForLoad()` (L8653, 66 lines)

#### 7. `PricingEditor` — Debug Pricing Tool (extract ~200 lines)

**Source lines:** ~9175–9285

**Methods to move:**
- `pricingClampSelection()` (L9177)
- `pricingVisibleRows()` (L9188)
- `pricingApplyBufferToSelected()` (L9195)
- `pricingHandleInput()` (L9211)
- `pricingListPresetNames()` (L9271)
- `drawPricingMenu()` (L9358)

---

## GOD CLASS INVENTORY

| File | Lines | Fields | Methods | Severity |
|------|-------|--------|---------|----------|
| `screens/GameScreen.java` | 9778 | 536 | ~120 | **EXTREME** |
| `editor/WorldEditorScreen.java` | 3794 | 220 | ~50 | **HIGH** |
| `tools/asseteditor/AssetEditorScreen.java` | 2056 | 129 | ~30 | HIGH |
| `worldmap/JsonAreaWorldLoader.java` | 1740 | 56 | ~20 | MEDIUM |
| `screens/TileEditorScreen.java` | 1638 | 120 | ~25 | MEDIUM |
| `data/SaveManager.java` | 1028 | 105 | ~15 | MEDIUM |
| `biome/BiomeSystem.java` | 980 | 141 | ~14 | MEDIUM |
| `systems/AiSystem.java` | 803 | 17 | ~10 | LOW |
| `quest/zqs/runtime/ZqsRuntime.java` | 766 | 57 | ~12 | MEDIUM |

### God Methods (>80 lines)

| Method | File | Line | Size |
|--------|------|------|------|
| `render()` | GameScreen.java | 1458 | **814 lines** |
| `fnv1aStep()` + field block | GameScreen.java | 181 | 530 lines |
| `tick()` | GameScreen.java | 3627 | **375 lines** |
| `updateScheduler()` | GameScreen.java | 2272 | **373 lines** |
| `keyDown()` | GameScreen.java | 1209 | 249 lines |
| `drawCraftPanel()` | GameScreen.java | 6979 | 247 lines |
| `openNewAreaDialog()` | WorldEditorScreen.java | 1995 | 237 lines |
| `drawSkillMenu()` | GameScreen.java | 9508 | 216 lines |
| `toVoxelRenderSpace()` | GameScreen.java | 3195 | 196 lines |
| `drawQuestLogOpenQuestsPanel()` | GameScreen.java | 5505 | 190 lines |
| `doLoad()` | GameScreen.java | 8773 | 190 lines |
| `uiHandleDragDropAndPopup()` | GameScreen.java | 6097 | 186 lines |
| `drawSelectedAreaMap()` | GameScreen.java | 7881 | 183 lines |
| `drawQuestLogDoneQuestsPanel()` | GameScreen.java | 5695 | 183 lines |
| `drawInventoryGrid()` | GameScreen.java | 9011 | 166 lines |
| `drawWqgDialog()` | GameScreen.java | 5241 | 164 lines |
| `onMapMarkerZone()` | WorldEditorScreen.java | 2884 | 151 lines |
| `tryInteractAtMouse()` | GameScreen.java | 4518 | 146 lines |
| `drawChestPanel()` | GameScreen.java | 7226 | 145 lines |
| `beginMoveDrag()` | WorldEditorScreen.java | 3390 | 139 lines |
| `rebuildMarkersZonesUi()` | WorldEditorScreen.java | 670 | 139 lines |
| `resolveTileTreeTrunks...()` | GameScreen.java | 4283 | 138 lines |
| `drawShopMenuAtMerchant()` | GameScreen.java | 5109 | 132 lines |
| `buildEntitiesObjectsBody...()` | WorldEditorScreen.java | 1032 | 132 lines |
| `drawDragGhostAndBuyPopup()` | GameScreen.java | 6602 | 125 lines |
| `rebuildTerrainPalette()` | WorldEditorScreen.java | 1342 | 121 lines |

---

## RESOURCE LEAK SUSPECTS

| File | Creates | Disposes | Delta | Suspect Items |
|------|---------|----------|-------|---------------|
| `render/TilesetRegions.java` | 17 | 4 | **-13** | Pixmap/Texture created in region splitting not disposed |
| `tools/asseteditor/ui/MiniSkin.java` | 4 | 1 | **-3** | BitmapFont, Textures created for skin not disposed |
| `render/AtlasLoader.java` | 1 | 0 | **-1** | TextureAtlas returned but no dispose chain tracked |
| `editor/WorldEditorScreen.java` | 9 | 7 | **-2** | Check: FrameBuffer, Pixmap lifecycle |
| `screens/TileEditorScreen.java` | 18 | 17 | **-1** | Minor: one resource not tracked |

### Specific Leak Locations

**TilesetRegions.java** — CRITICAL:
- Creates many `Pixmap` objects for tile region extraction
- Only 4 `dispose()` calls found
- Each un-disposed Pixmap leaks native memory

**MiniSkin.java** — MEDIUM:
- Creates `BitmapFont` + multiple `Texture` via `solidTex()`
- Only 1 dispose path found
- Scene2d `Skin.dispose()` may cover some, but not guaranteed for manually created textures

**AtlasLoader.java** — LOW:
- Returns `new TextureAtlas(fh)` without tracking
- Caller is responsible, but no dispose chain verification exists

---

## CIRCULAR DEPENDENCIES

### Confirmed Cycles

| Cycle | Severity | Description |
|-------|----------|-------------|
| `GameScreen` ↔ `DebugCommands` | **HIGH** | DebugCommands imports GameScreen, GameScreen imports DebugCommands. Fix: introduce interface or event bus |
| `GameScreen` ↔ `JsonAreaWorldLoader` | **HIGH** | JsonAreaWorldLoader imports GameScreen (calls area* methods). Fix: extract area API interface |
| `GameScreen` ↔ `NoopAreaWorldLoader` | MEDIUM | Same pattern as above |
| `GameScreen` ↔ `WorldMapTravelController` | MEDIUM | Same pattern |
| `GameScreen` ↔ `AreaWorldLoader` | MEDIUM | Same pattern |
| `SurvivalGame` ↔ `Screens` (10 files) | LOW | Normal for LibGDX Screen pattern, but GameScreen is referenced back from worldmap |

### Fix Strategy for GameScreen ↔ worldmap Cycle

Extract an interface:
```
// New file: core/.../screens/AreaHost.java
public interface AreaHost {
    long areaGetWorldSeed();
    long areaWorldSeed();
    void areaResetWorld(long seed);
    void areaSetPlayerWorldPos(float wx, float wy);
    void areaClearAreaLocalEntities();
    void areaSetTreePresentBits(int w, int h, byte[] bits);
    void areaSetTreeCutBits(int w, int h, byte[] bits);
    void areaClearEnemyZones();
    void areaAddEnemyZone(int cxTile, int cyTile, int rTiles, int min, int max, int respawnDaysMin, int respawnDaysMax);
    void areaDebugToastOnce(String msg);
    World areaWorld();
    Entities areaEntities();
    WorldMapState areaWorldMapState();
    ChestStore areaChestStore();
}
```

```
// New file: core/.../screens/DebugTarget.java
public interface DebugTarget {
    void debugTravelNorth();
    void debugTravelEast();
    void debugTravelSouth();
    void debugTravelWest();
    void debugRerollQuestGuyOffers();
    void debugToggleHoverIds();
    void debugCycleDayNightMode();
    void debugToggleLamp();
    void debugToggleLampXL();
    void debugToggleRoadBlocksMovement();
    void debugToggleTileTreeStreaming();
    void debugTogglePricingEditor();
    boolean isPricingEditorOpen();  // <-- also used by DebugCommands!
    void debugPricingSave();
    void debugPricingResetDefaults();
    void debugPricingOpenPresetPopup();
}
```
- `GameScreen implements AreaHost, DebugTarget`
- `JsonAreaWorldLoader` depends on `AreaHost`, not `GameScreen`
- `DebugCommands` depends on `DebugTarget`, not `GameScreen`
- Cycles broken

---

## DEAD CODE & COMMENTED BLOCKS

### "Nicht fertiges Feature" Tags (102 total)

| File | Count | Examples |
|------|-------|---------|
| `GameScreen.java` | **65** | Unused skills, UI states, command buffers, fishing, foraging, climbing, boating |
| `OptionsScreen.java` | 4 | Commented UI options |
| `EntityRegions.java` | 3 | Unused entity texture mappings |
| `AssetEditorScreen.java` | 3 | Unfinished asset features |
| `ChunkRenderer.java` | 2 | Voxel rendering prep comments |
| `TutorialScreen.java` | 2 | Unfinished tutorial steps |
| `CreditsScreen.java` | 2 | Incomplete credits content |
| `JsonAreaWorldLoader.java` | 2 | Unfinished area loading paths |
| Various others | 19 | Scattered placeholders |

### Large Commented-Out Blocks

| File | Block Count | Description |
|------|-------------|-------------|
| `GameScreen.java` | **23** | Massive commented sections: UiState, CommandBuffer, anchorToStep, fishing skill, etc. |
| `WorldEditorScreen.java` | 13 | Editor feature stubs |
| `WorldMapRuntime.java` | 10 | Travel logic stubs |
| `HeightLevelEditing.java` | 6 | Height editing experiments |
| `VoxelRenderMapping.java` | 6 | Mapping algorithm notes |
| `WorldMapState.java` | 5 | State tracking stubs |

### Action Required

For each "Nicht fertiges Feature" block:
1. If it's a planned feature → move to a `TODO.md` tracking file, delete from code
2. If it's dead code that will never return → delete entirely
3. If it's a needed stub → keep only the minimal interface, remove implementation attempts

---

## MISSING IMPORTS & INLINE FQN

### GameScreen.java: 292 inline fully-qualified references

Instead of proper imports, the code uses inline FQN like:
```java
com.yourgame.survival.data.Wallet wallet = new com.yourgame.survival.data.Wallet();
com.yourgame.survival.sim.CommandQueue commandQueue = new com.yourgame.survival.sim.CommandQueue(256);
com.yourgame.survival.worldmap.Dir4.N
com.yourgame.survival.render.VoxelRenderMapping.liftWorld(hl)
```

**Fix:** Add proper import statements and replace all 292 inline FQN references.

### Files missing import for VoxelRenderMapping (same-package, no import needed):
- `render/ChunkRenderer.java` — OK (same package)
- `render/EntityRenderer.java` — OK (same package)
- `editor/WorldEditorScreen.java` — HAS import (different package) ✓
- `screens/GameScreen.java` — uses FQN inline (L2447) — needs import

---

## MAGIC NUMBERS HOTSPOTS

| File | Approx Count | Examples |
|------|--------------|---------|
| `GameScreen.java` | **263** | Pixel offsets, UI sizes, timers, speeds, damage values |
| `WorldEditorScreen.java` | 80 | Tile sizes, UI layout values |
| `JsonAreaWorldLoader.java` | 51 | Parse constants, default values |
| `TileEditorScreen.java` | 50 | Editor UI dimensions |
| `OptionsScreen.java` | 44 | UI layout values |
| `SkillDefs.java` | 26 | Skill XP curves, level thresholds |

**Note:** The `tuning/` package exists and is the correct place for gameplay constants.
Many magic numbers in GameScreen should be moved to `TuningGameplay.java` or new tuning classes.

---

## STYLE & NAMING ISSUES

### Windows Line Endings
- Some files have `\r\n` (CRLF) mixed with `\n` (LF)
- Detected: `package com.yourgame.survival.screens;\r` in package listing
- **Fix:** Normalize all to LF via `dos2unix` (KEIN git!)

### Inconsistent Package for `client/network/`
- `GameClientListener.java` uses `package client.network;` — should be `com.yourgame.survival.client.network`
- **⚠️ DO NOT FIX — This is MULTIPLAYER code. PAUSED. DO NOT TOUCH.**

### Inner Class Placement
- GameScreen has inner classes at the END of a 9778-line file
- `RenderScratch`, `UiState`, `InputState`, `CommandBuffer`, `SkillMenuRow`, `EnemyZone`
- These should be extracted or at minimum documented

---

## 2.5D INTEGRATION ISSUES

### New Files (Clean)
| File | Lines | Status |
|------|-------|--------|
| `render/VoxelRenderMapping.java` | 66 | ✓ Clean, well-documented |
| `render/VoxelSideRegions.java` | 38 | ✓ Clean, placeholder acknowledged |
| `area/VoxelHeightCodec.java` | 111 | ✓ Clean, strict validation |
| `editor/HeightLevelEditing.java` | 149 | ⚠ 6 commented blocks, experimental |

### Voxel Mapping Call Sites (65 total)
The `VoxelRenderMapping` static methods are called from:
- `ChunkRenderer.java` — 8 call sites
- `EntityRenderer.java` — 9 call sites
- `WorldEditorScreen.java` — 3 call sites
- `GameScreen.java` — 1 call site (FQN inline)

**Concern:** If the mapping math changes, 65 call sites need updating. Consider a mapping context object.

---

## REFACTOR CLUSTERS

### Cluster 1: "GameScreen Apocalypse" (Priority: CRITICAL)
**Goal:** Split GameScreen from 9778 → ~2000 lines (core loop only)
**Files affected:** GameScreen.java + 7 new files
**Estimated new files:**
- `screens/GameRenderer.java`
- `screens/GameUiController.java`
- `screens/CombatController.java` (rename/enhance existing)
- `screens/WorldMapController.java`
- `screens/PlayerController.java`
- `screens/SaveLoadController.java`
- `screens/PricingEditor.java`
- `screens/AreaHost.java` (interface, breaks circular deps)

### Cluster 2: "Nicht Fertig Auslagern" (Priority: HIGH)
**Goal:** Move all 102 "Nicht fertiges Feature" blocks OUT of .java files into docs/FEATURE_BACKLOG.md
**CRITICAL: NEVER DELETE THESE. They are planned feature stubs. ONLY MOVE them.**
**Files affected:** 40+ files
**Process:**
1. Grep all `Nicht fertiges Feature` lines
2. For EACH: copy the FULL commented block (with context: file, line, what it does) to `docs/FEATURE_BACKLOG.md`
3. THEN remove the comment from the .java file
4. Remove multi-line commented-out code blocks >3 lines (copy to BACKLOG first!)
5. Remove empty inner classes (UiState, CommandBuffer in GameScreen — copy to BACKLOG first!)

### Cluster 3: "Import Cleanup" (Priority: MEDIUM)
**Goal:** Replace 292 inline FQN with proper imports in GameScreen
**Files affected:** `GameScreen.java` primarily
**Process:**
1. Collect all unique FQN patterns
2. Add corresponding imports
3. Replace all inline FQN with simple class names

### Cluster 4: "Resource Leak Fix" (Priority: HIGH)
**Goal:** Fix all resource creation/dispose mismatches
**Files affected:** `TilesetRegions.java`, `MiniSkin.java`, `AtlasLoader.java`
**Process:**
1. Audit each `new Texture/Pixmap/...` call
2. Ensure matching `dispose()` in the same class or documented caller
3. Add `@Override dispose()` where missing

### Cluster 5: "Circular Dependency Break" (Priority: HIGH)
**Goal:** Break GameScreen ↔ worldmap/debug cycles
**Files affected:** `GameScreen.java`, `JsonAreaWorldLoader.java`, `DebugCommands.java`, `NoopAreaWorldLoader.java`, `WorldMapTravelController.java`, `AreaWorldLoader.java`
**Process:**
1. Create `AreaHost` interface
2. Create `DebugTarget` interface
3. GameScreen implements both
4. Update all dependents to use interfaces

### Cluster 6: "Magic Number Extraction" (Priority: LOW)
**Goal:** Move magic numbers to tuning classes
**Files affected:** GameScreen.java primarily, then others
**Process:**
1. Identify all numeric literals in UI draw methods → `TuningUi.java`
2. Identify all gameplay constants → `TuningGameplay.java` (extend existing)
3. Identify all combat constants → `TuningCombat.java` (new)

---

## CHATTY WORKFLOW INSTRUCTIONS

### ██████████████████████████████████████████████████████████████████████
### ██  LIES DIESEN ABSCHNITT KOMPLETT BEVOR DU IRGENDWAS ANFASST!!  ██
### ██████████████████████████████████████████████████████████████████████

### HARD CONSTRAINTS — VIOLATIONS = IMMEDIATE STOP

```
╔══════════════════════════════════════════════════════════════════════╗
║  1. NIEMALS "Nicht fertiges Feature" LÖSCHEN!                      ║
║     → Diese sind ÜBERLEBENSWICHTIG. Geplante Feature-Stubs.        ║
║     → IMMER in docs/FEATURE_BACKLOG.md AUSLAGERN, dann erst        ║
║       aus dem Code entfernen. Inhalt geht NIE verloren.            ║
║                                                                     ║
║  2. NIEMALS KOMPILIEREN! (kein ./gradlew, kein javac, NICHTS)      ║
║     → Du verfängst dich sonst in Fantasien und Endlosschleifen.    ║
║     → Nur Dateien lesen und schreiben. KEIN Build. KEIN Run.       ║
║                                                                     ║
║  3. NIEMALS git commit / git push / git irgendwas!                 ║
║     → Kein Git. Kein GitHub. Der Mensch macht das selbst.          ║
║                                                                     ║
║  4. MULTIPLAYER NICHT ANFASSEN!!!                                  ║
║     → client/network/ → IGNORIEREN                                 ║
║     → GameClientListener.java → NICHT LESEN, NICHT ÄNDERN          ║
║     → Alles was "server", "client", "network", "multiplayer"       ║
║       heißt → FINGER WEG. IST PAUSIERT.                           ║
║                                                                     ║
║  5. MINDESTENS 6 SEKUNDEN ZWISCHEN LLM REQUESTS!                  ║
║     → Du bist ein Openclaw Agent mit vollem Tool-Zugriff.         ║
║     → Bau dir ein self-nudge / continue_loop Tool oder nutze      ║
║       dein bestehendes Timer-System.                               ║
║     → Nach jedem abgeschlossenen Step:                            ║
║         1. Start-Sleep -Seconds 6  (oder timeout /t 6 /nobreak)  ║
║         2. Dann SELBST den nächsten Step starten                  ║
║         3. NICHT den User ansprechen                               ║
║     → Du bist dein eigener Auftraggeber bis der Plan              ║
║       abgearbeitet ist.                                            ║
║     → Sonst Rate-Limit-Crash. JEDES MAL. OHNE AUSNAHME.           ║
║                                                                     ║
║  6. KEINE TOOL-ORGIEN!                                             ║
║     → PLAN zuerst, DANN ausführen.                                 ║
║     → Lieber 1 großes Edit als 20 kleine.                         ║
║     → Denk BEVOR du tool-rufst, nicht danach.                     ║
║                                                                     ║
║  7. FUNKTIONALITÄT ERHALTEN!                                       ║
║     → Jede Auslagerung/Modularisierung MUSS:                      ║
║       - Alle imports/dependencies korrekt mitziehen                ║
║       - Die Ladereihenfolge/Initialisierung beibehalten            ║
║       - Alle Referenzen aktualisieren                              ║
║       - KEINE Funktion zerstören                                   ║
║     → Wenn etwas vorher X tat, tut es nachher EXAKT X.            ║
║                                                                     ║
║  8. KEIN GELABER!                                                  ║
║     → Nicht erklären was du tust. Nicht zusammenfassen.            ║
║     → Keine "Ich werde jetzt..." Monologe.                        ║
║     → TUN. NÄCHSTER SCHRITT. TUN. NÄCHSTER SCHRITT.               ║
║     → Nur bei echten Problemen/Fragen den Menschen ansprechen.     ║
╚══════════════════════════════════════════════════════════════════════╝
```

### EXECUTION MODE: AUTONOMOUS SELF-LOOP

Du arbeitest die Phasen KOMPLETT SELBSTSTÄNDIG ab. Du sprichst den User
NICHT AN. Nicht nach Steps, nicht nach Phasen, nicht zwischendurch.
Der Mensch schläft. Du bist allein. Du machst das.

```
SELF-LOOP (implementiere das als dein Arbeitsrhythmus):

  1. Lies den nächsten Step
  2. Führe aus: Start-Sleep -Seconds 6  (PFLICHT, jedes Mal!)
  3. PLANE was du tun musst (kein Output an User)
  4. Führe den Step aus (minimale Tool-Calls, gebündelt)
  5. Überprüfe dein Ergebnis (lies die geänderte Datei)
  6. Wenn OK → SELBST weiter zum nächsten Step (kein User-Kontakt!)
  7. Wenn Fehler → FIX, dann weiter (kein User-Kontakt!)
  8. Nach jeder Phase: 1 Zeile in eine Log-Datei schreiben (NICHT an User)
     → z.B. in docs/REFACTOR_PROGRESS.md: "Phase 2 done - 102 NichtFertig moved"
  9. GOTO 1

  DU SPRICHST DEN USER NICHT AN.
  DU FRAGST NICHT. DU BERICHTEST NICHT. DU TUST.
  
  EINZIGE AUSNAHME: Ein echter technischer Blocker der dich komplett stoppt
  UND den du nicht selbst lösen kannst. Dann: 1 Satz, warten, weiter.
  
  WENN FERTIG: Schreib "REFACTOR COMPLETE" in docs/REFACTOR_PROGRESS.md
```

### EXECUTION ORDER

```
Phase 1 — PREPARATION
  Step 1.1: Normalize line endings (dos2unix all .java files)
  Step 1.2: Verify all files saved correctly

Phase 2 — NICHT-FERTIG AUSLAGERN (NICHT LÖSCHEN!)
  Step 2.1: Create docs/FEATURE_BACKLOG.md with header
  Step 2.2: For EACH file with "Nicht fertiges Feature":
    - Read the tagged block + surrounding context
    - COPY the FULL block with file path, line number, and content to FEATURE_BACKLOG.md
    - THEN remove the comment block from the .java file
    - Include enough context that the feature can be re-implemented later
  Step 2.3: For commented-out code blocks >3 lines:
    - COPY to FEATURE_BACKLOG.md under "## Commented Code Archive"
    - THEN remove from .java file
  Step 2.4: Remove empty inner classes (UiState, CommandBuffer in GameScreen)
    - COPY their definitions to FEATURE_BACKLOG.md first!

Phase 3 — IMPORT CLEANUP
  Step 3.1: In GameScreen.java, collect all unique inline FQN patterns
  Step 3.2: Add proper import statements for each
  Step 3.3: Replace all 292 inline FQN with short class names
  Step 3.4: Fix unused import in QuestLogScreen.java (remove `import Input`)

Phase 4 — CIRCULAR DEPENDENCY BREAK
  Step 4.1: Create interface screens/AreaHost.java:
    - long areaGetWorldSeed()
    - long areaWorldSeed()
    - void areaResetWorld(long seed)
    - void areaSetPlayerWorldPos(float wx, float wy)
    - void areaClearAreaLocalEntities()
    - void areaSetTreePresentBits(int w, int h, byte[] bits)
    - void areaSetTreeCutBits(int w, int h, byte[] bits)
    - void areaClearEnemyZones()
    - void areaAddEnemyZone(int cxTile, int cyTile, int rTiles, int min, int max, int respawnDaysMin, int respawnDaysMax)
    - void areaDebugToastOnce(String msg)
    - World areaWorld()
    - Entities areaEntities()
    - WorldMapState areaWorldMapState()
    - ChestStore areaChestStore()
  Step 4.2: Create interface screens/DebugTarget.java:
    - All public debug* methods from GameScreen
    - boolean isPricingEditorOpen()
  Step 4.3: GameScreen implements AreaHost, DebugTarget
  Step 4.4: Change JsonAreaWorldLoader parameter type: GameScreen → AreaHost
  Step 4.5: Change NoopAreaWorldLoader → AreaHost
  Step 4.6: Change WorldMapTravelController → AreaHost
  Step 4.7: Change AreaWorldLoader → AreaHost
  Step 4.8: Change DebugCommands → DebugTarget

Phase 5 — GAMESCREEN DECOMPOSITION
  Step 5.0: Clean up existing thin wrapper controllers
    - DELETE screens/CombatController.java (14 lines, thin wrapper)
    - DELETE screens/CursorController.java (13 lines, thin wrapper)
    - DELETE screens/DebugOverlay.java (18 lines, thin wrapper)
    - DELETE screens/HudRenderer.java (13 lines, thin wrapper)
    - DELETE screens/WorldView.java (13 lines, thin wrapper)
    - KEEP screens/GameInputController.java (55 lines)
    - In GameScreen: inline the wrapper calls back to direct calls

  Step 5.1: Extract PricingEditor (~200 lines, lowest risk)
    - Create screens/PricingEditor.java
    - Move ALL pricing* methods and fields
    - Move ALL dependencies (imports) these methods need
    - GameScreen holds PricingEditor reference, delegates
    - Update DebugTarget/DebugCommands if needed

  Step 5.2: Extract SaveLoadController (~300 lines)
    - Create screens/SaveLoadController.java
    - Move doSave(), doLoad(), clearAllStateForLoad()
    - SaveLoadController needs reference to GameScreen state OR
      receives state via parameters — choose the cleaner option
    - Preserve EXACT save format compatibility!

  Step 5.3: Extract ProjectileCombatController (~600 lines)
    - Create screens/ProjectileCombatController.java
    - Move arrow arrays, bow cooldown, RMB pulse, sneak state
    - Move tickArrows(), arrowHitSegment(), onKill()
    - Preserve all entity references and collision logic

  Step 5.4: Extract PlayerController (~400 lines)
    - Create screens/PlayerController.java
    - Move mouse world tracking, skill indices, player state
    - Move updateMouseWorld(), collectInputSnapshot(), actionReach(), etc.
    - SK_* constants can stay static final in PlayerController

  Step 5.5: Extract WorldMapController (~800 lines)
    - Create screens/WorldMapController.java
    - Move all worldmap drawing, area travel, enemy zones, fog bits
    - Move EnemyZone inner class
    - This is the most interconnected — be CAREFUL with state references

  Step 5.6: Extract GameUiController (~2500 lines)
    - Create screens/GameUiController.java
    - Move ALL draw*Panel, draw*Menu, draw*Hud methods
    - Move all UI state fields (shop, craft, chest, questLog, wallet, skill)
    - Move SkillMenuRow inner class
    - This is the BIGGEST extraction — plan it before executing

  Step 5.7: Extract GameRenderer (~1500 lines)
    - Create screens/GameRenderer.java
    - Move camera setup, batch management, day/night mask rendering, fog rendering
    - Move debug overlay rendering
    - NOTE: Day/Night MUSIC (updateDayNightMusic, playDayBg, playNightBg, etc.)
      belongs to AUDIO, not renderer. Move to DayNightAudioController or AudioBus.

  Step 5.8: Final GameScreen cleanup
    - GameScreen should now be ~1500-2000 lines
    - It holds references to all sub-controllers
    - render() delegates to GameRenderer
    - tick() delegates to sub-controllers
    - keyDown() routes to appropriate controller
    - Verify all cross-references are correct

Phase 6 — RESOURCE LEAK FIX
  Step 6.1: Audit TilesetRegions.java — track every new Pixmap/Texture, add dispose()
  Step 6.2: Audit MiniSkin.java — ensure all created resources are disposed
  Step 6.3: Verify AtlasLoader callers dispose the returned TextureAtlas

Phase 7 — MAGIC NUMBERS (optional, do if time permits)
  Step 7.1: Extract UI layout constants from GameUiController to TuningUi.java
  Step 7.2: Extract combat constants from ProjectileCombatController to TuningCombat.java
```

### VERIFICATION CHECKLIST (after all phases)

```
[ ] GameScreen.java is < 2500 lines
[ ] No thin wrapper controllers remain (old 14-line CombatController etc.)
[ ] No file imports GameScreen directly (except SurvivalGame, sub-controllers)
[ ] worldmap/*.java imports AreaHost, not GameScreen
[ ] debug/DebugCommands.java imports DebugTarget, not GameScreen
[ ] grep "Nicht fertiges Feature" returns 0 results in .java files
[ ] docs/FEATURE_BACKLOG.md contains ALL moved feature stubs (nothing lost!)
[ ] grep "com\.yourgame\.survival\." GameScreen.java (non-import lines) returns < 10
[ ] client/network/ was NOT touched
[ ] GameClientListener.java was NOT touched
[ ] No git commits were made
[ ] All extracted controllers have correct imports and dependencies
[ ] Initialization order preserved (show() → create resources → dispose)
```

---

## DEPENDENCY GRAPH (MAJOR NODES)

```
SurvivalGame
  ├── MenuScreen
  ├── WhoPlaysScreen → PlayerProfileManager
  ├── GameScreen (→ AreaHost interface)
  │     ├── GameRenderer → ChunkRenderer, EntityRenderer, RenderPipeline
  │     ├── GameUiController → Inventory, Wallet, UiRegions
  │     ├── ProjectileCombatController → CombatSystem, Entities
  │     ├── PlayerController → SkillDefs, SkillEffects
  │     ├── WorldMapController → WorldMapRuntime, JsonAreaWorldLoader
  │     ├── SaveLoadController → SaveManager
  │     └── PricingEditor → PriceBook
  ├── WorldEditorScreen → ChunkRenderer, Entities, JsonAreaWorldLoader
  ├── TileEditorScreen → TilesetConfig
  └── AssetEditorScreen → AssetIndexScanner, AutoCollision

World ← BiomeSystem ← ChunkGenOrchestrator ← [worldgen pipeline]
Entities ← [systems: AiSystem, CombatSystem, HarvestSystem, etc.]
ZqsRuntime ← [quest pipeline: blueprints, catalog, text, save]
```

---

## STATISTICS

| Metric | Value |
|--------|-------|
| Total files | 211 |
| Total lines | 41,886 |
| Largest file | GameScreen.java (9,778 lines) |
| Avg lines/file | 198 |
| Files > 500 lines | 13 |
| Files > 1000 lines | 6 |
| Commented lines | 3,162 (7.5%) |
| "Nicht fertiges Feature" tags | 102 |
| Circular dependency cycles | 5 confirmed |
| Resource leak suspects | 5 files |
| Magic number hotspots | 9 files, ~500+ instances |
| Inline FQN references | 292 (GameScreen alone) |
| Packages | 44 |
| Estimated refactor effort | 15-25 work sessions |

**Technical Debt Severity: HIGH**
**Primary Bottleneck: GameScreen.java**
**Recommended approach: Incremental extraction, step by step, plan before execute**

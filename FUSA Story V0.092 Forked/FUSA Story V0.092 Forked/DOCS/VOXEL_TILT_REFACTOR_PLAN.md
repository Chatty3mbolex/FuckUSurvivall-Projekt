# FUSA — Voxel Raster + 2.5D Tilt: Surgical Refactor Plan (Bauplan)

Project root (law):
`C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\FUSA Story V0.092 Forked`

Companion reference (current systems map):
- `DOCS/SYSTEMS_REFERENCE_FOR_VOXEL_TILT_REFACTOR.md`

Non‑negotiables
- OFFLINE ONLY.
- **NO git commits**.
- This plan prepares code/files **without integrating** into runtime unless explicitly requested later.
- No "fallback" behavior that hides missing content.

---

## 0) Goal (one sentence)
Turn the current 2D tile world into a **voxel‑column world** (height per (x,y) tile) while keeping:
- the logical grid `(x,y)` intact,
- camera as orthographic/2D input,
- rendering as 2D sprite batch with a **2.5D tilt illusion**,
- systems (AI, saves, areas, spawns) coherent and explicitly adapted.

---

## 1) Precise target model (what "voxel raster" means here)

### 1.1 Minimal voxel definition (Phase 1)
- World is a heightfield of **columns** per tile coordinate:
  - `h(tx,ty) = heightLevel` (integer 0..N; currently code uses 0..15 from biome mask)
- No caves, no stacked different materials per z-cell in Phase 1.
- The "surface" tile type is still `groundId(tx,ty)`.

### 1.2 Rendering target illusion
- Top face of each tile is rendered at a screen offset based on height.
- Side faces are rendered where neighbor height is lower.
- A global "tilt" is applied by transforming the world-to-screen mapping (Y compression / shear) in the renderer (not by rotating the camera).

### 1.3 Interaction target
- Player, livings, objects exist on the **top surface** for now:
  - Their logical position stays in world 2D `(wx,wy)`.
  - Visual render offset considers tile height under them.

---

## 2) Inventory of affected systems (dependencies + ownership)

This section lists what is affected, who owns data, and what must be removed/changed.

### 2.1 World data ownership
Files:
- `world/World.java` — coordinate conversions, chunk streaming, queries incl. `heightLevelAt*Peek`.
- `world/TileLayers.java` — per-tile arrays incl. `heightLevel[]`.

Ownership:
- `TileLayers.heightLevel[]` is already the canonical per-tile terraced height.

Dependency notes:
- Any renderer or spawn system that assumes flat tiles implicitly depends on `heightLevel=0`.

### 2.2 Procedural generation pipeline
Files:
- `worldgen/paint/DefaultTilePainter.java` — writes `heightLevel` from biome mask.
- `biome/BiomeSystem.java` — loads height mask pixmap and provides `heightMaskLevelAt`.

Ownership:
- Procedural world height is derived from seed + biome mask.

### 2.3 Authored areas (critical seam)
Files:
- `worldmap/JsonAreaWorldLoader.java` — applies authored area into GameScreen.
- `editor/AreaWorldApplier.java` — applies authored area into editor preview.

Current behavior that **must be intentionally changed** for voxel refactor:
- Both loaders explicitly zero height fields:
  - `L.heightLevel[idx] = 0;`
  - plus other procedural fields.

Dependency impact:
- Area editing currently **cannot** author height; voxel refactor requires:
  - persisted height in `.area.json`
  - apply-time restoration into `TileLayers.heightLevel`.

### 2.4 Rendering
Files:
- `render/ChunkRenderer.java` — draws tilemap chunks; already has a heightLevel peek helper.
- `render/EntityRenderer.java` — draws entities with sort key + `Entities.layerBias`.

Ownership:
- ChunkRenderer owns tile drawing and is the primary place for fake-tilt mapping.

Dependencies:
- EntityRenderer ordering must be consistent with tile top/side faces.

### 2.5 Entities, AI, spawns
Files:
- `entity/Entities.java` — stores world positions; has `layerBias[]`.
- `systems/AiSystem.java` — movement uses `isWaterAtWorldPeek` and `isBlockedAtWorldPeek` only.
- `world/WorldNodeSpawner.java` — spawns nodes/items by tile center (world units).
- `worldmap/JsonAreaWorldLoader.java` — spawns authored nodes/objects by tile center.

Dependency impact:
- Spawns remain tile-centered; voxel visuals add render offset.
- Movement/collision is not height-aware yet; must be explicitly decided.

### 2.6 Saves
File:
- `data/SaveManager.java` — saves full entity snapshot, removed procedural nodes, world map, ZQS.

Dependency impact:
- If height is purely procedural: no save change.
- If height becomes player-authored (areas/editor, building): save must include height deltas.

---

## 3) What must be removed (surgical removals / dead paths)

### 3.1 Remove “flat-only” assumptions
Do NOT remove code blindly; instead remove only the places that enforce flatness where voxel height must be allowed.

Concrete flatness enforcers (must be replaced or narrowed):
- `JsonAreaWorldLoader.applyGround(...)` sets `L.heightLevel[idx]=0` for authored areas.
- `AreaWorldApplier.fillDefault(...)` sets `c.layers.heightLevel[idx]=0`.

Plan:
- Replace “always zero” with:
  - “zero unless JSON provides heightLevel layer” (editor) and
  - “apply JSON heightLevel after default fill” (runtime area loader)

### 3.2 Identify any renderer "guardrails" that clamp height to 0
Action:
- Search `ChunkRenderer` and `TilesetRegions` for clamps/guards around heightLevel.
- Keep the guardrails only if they’re truly needed (e.g., preventing out-of-range values).

---

## 4) New data format additions (Area JSON)

### 4.1 Add an authored height layer to `.area.json`
Target schema addition (choose one; Phase 1 prefers Base64 dense):

Option A (dense, fast):
```json
"layers": {
  "heightLevel": { "w": 384, "h": 384, "b64": "..." }
}
```
- Encoding: unsigned byte per tile (0..15 or 0..255), row-major.

Option B (sparse patches, human-editable):
```json
"layers": {
  "heightLevel": { "patches": [ {"x":1,"y":2,"h":3}, ... ] }
}
```

Reference points in current code (do not guess):
- Similar Base64 approach already exists for `tileTrees` persistence in `WorldEditorScreen.persistTileTreeBitsToJson()`.
- Similar w/h validation exists in JsonAreaWorldLoader when reading `tileTrees`.

### 4.2 Save/load seam definition
- Editor must:
  - initialize heightLevel to 0 (flat) when creating a new area
  - allow editing
  - persist it into JSON on save
- Runtime must:
  - apply heightLevel from JSON onto `TileLayers.heightLevel[]`
  - avoid re-zeroing it afterwards.

---

## 5) New code modules (prepare, don’t integrate)

### 5.1 New helper: `VoxelHeightCodec`
New file to prepare:
- `core/src/main/java/com/yourgame/survival/area/VoxelHeightCodec.java`
Responsibilities:
- encode/decode `byte[]` heightLevel to/from Base64
- validate w/h and expected size
- utility to read/write into `JsonValue` under `layers.heightLevel`

Reference patterns:
- `WorldEditorScreen.persistTileTreeBitsToJson()` (Base64 encode/decode)
- `JsonAreaWorldLoader` tileTrees read path.

### 5.2 New helper: `VoxelRenderMapping`
New file to prepare:
- `core/src/main/java/com/yourgame/survival/render/VoxelRenderMapping.java`
Responsibilities:
- define constants:
  - `HEIGHT_STEP_PX`
  - `TILT_Y_SCALE` (or parameters)
- functions to compute:
  - screen offset for a tile top based on `(tx,ty,heightLevel)`
  - whether a side face is visible and what offset it has

Reference points:
- `ChunkRenderer` current draw loop + its height peek helper.

### 5.3 Prepare cliff/side tile concept without content fallbacks
New file to prepare:
- `core/src/main/java/com/yourgame/survival/render/VoxelSideRegions.java`
Responsibilities:
- mapping from `groundId` (or material) to side-face TextureRegions

Hard rule:
- Missing side regions must be explicit (fail fast) or be visually obvious debug tiles per your project conventions.

---

## 6) Renderer refactor plan (ChunkRenderer + EntityRenderer)

### 6.1 ChunkRenderer changes (Phase 1)
- Add a second pass (or integrate into pass) to draw tile top faces at offset.
- Draw side faces (south/west) where neighbor height is lower.
- Ensure road/water masks still align to top face.

Dependencies:
- Must use `TileLayers.heightLevel[idx]`.
- Must respect chunk boundaries when reading neighbor heights.

### 6.2 EntityRenderer ordering and height
Current:
- Entities have `layerBias` and a sort key.

Required:
- Define entity visual Y used for ordering:
  - base `es.y[e]` plus a derived visual lift from tile height under entity.

Reference points:
- `World.heightLevelAtWorldPeek(wx,wy,default)` exists but is a peek query.

Risk:
- Doing height lookup per entity per frame can be expensive; consider caching tile height in entity payload later.

---

## 7) Editor plan (WorldEditorScreen)

### 7.1 Height editing UX
- Add a new edit mode for heightLevel:
  - Use brush size.
  - Use wheel to raise/lower when a “height tool” is active.
  - Ensure wheel zoom remains accessible (modifier key or explicit mode).

### 7.2 Persistence
- On save: call `persistHeightLevelToJson()` similar to `persistTileTreeBitsToJson()`.
- On load: read heightLevel from JSON into editor arrays and apply to preview world.

Reference seams:
- Editor preview application currently uses `JsonAreaWorldLoader.applyForEditor(...)` which calls `applyGround` that zeros heightLevel.

Plan:
- Introduce an “applyHeightFromJsonAfterFill” step in applyForEditor.

---

## 8) Spawns, movement, collision — what must be decided

### 8.1 Spawns
- Procedural spawns: remain tile-centered; visual lift is render-only.
- Authored nodes/objects: remain tile-centered.

### 8.2 Movement
Phase 1: movement ignores height.
- `AiSystem` uses `isWaterAtWorldPeek` and `isBlockedAtWorldPeek`.

Phase 2+: optional slope rules:
- disallow moving to tile with `abs(h2-h1)>1`

This is explicitly staged so the refactor does not explode.

---

## 9) Savegame implications (explicit)

Case A: Procedural world only (no player-authored height)
- Height regenerates from seed and biome masks.
- No save changes.

Case B: Areas / player building modifies height
- Must save:
  - either the height delta map per area
  - or persistent heightLevel grid in save

Reference seam:
- `SaveManager` already persists:
  - WorldMap area states, fog, tree cut masks.
- A height delta could be stored similarly (Base64) per area.

---

## 10) Step-by-step execution plan (preparation only)

1) Create planning artifacts (state/checklist) for the refactor.
2) Add new helper files (codec + mapping + side regions) as **stubs with comments and TODO markers**, referencing current call sites.
3) Add documentation of removal points (where heightLevel is currently zeroed) with exact line anchors.
4) Prepare editor persistence methods (empty skeletons) without wiring them into runtime yet.

Deliverables of this phase
- Plan docs + prepared code files (compilable stubs) + no integration.

---

## 11) Open questions / blockers (must be answered before integration)
- Height value range: stick to 0..15 (biome mask) or extend?
- Side-face art: which materials exist (dirt/rock/snow)?
- Wheel binding policy in editor: modifier key vs tool-mode steals wheel.
- Movement rule: ignore height or add constraints.

(Questions are listed here for completeness; do not proceed to integration without decisions.)

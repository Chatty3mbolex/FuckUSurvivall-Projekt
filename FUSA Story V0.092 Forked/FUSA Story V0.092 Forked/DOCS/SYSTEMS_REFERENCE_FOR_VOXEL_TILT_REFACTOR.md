# FUSA V0.092 Forked — Systems Reference for “Voxel Raster + 2.5D Tilt” Refactor

Project root (authoritative):
`C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\FUSA Story V0.092 Forked`

Purpose of this document
- A single, **code-referenced** map of the current systems that will be affected by a refactor where:
  - the logical grid remains (x,y) tiles,
  - but tiles can be "pulled upward" (voxel-column / heightLevel driven),
  - camera stays 2D/2.5D (orthographic) while rendering simulates depth.
- This is written as a **reference**, not a design pitch.

---

## 1) Coordinate system / units (the foundation)

### 1.1 World units vs tile units
File: `core/src/main/java/com/yourgame/survival/world/World.java`
- `World.CHUNK_SIZE = 64` tiles
- `World.TILE_WORLD = 16f` world-units per tile

Conversions (canonical):
- world → tile:
  - `tx = floor(wx / TILE_WORLD)`
  - `ty = floor(wy / TILE_WORLD)`
  - See: `biomeIdAtWorld`, `isWaterAtWorld`, `isBlockedAtWorld`
- tile → world center:
  - `wx = (tx + 0.5f) * TILE_WORLD`
  - `wy = (ty + 0.5f) * TILE_WORLD`
  - Used in spawners/loaders (areas + procedural)

### 1.2 Chunks and local indexing
File: `World.java`
- tile → chunk:
  - `cx = floorDivInt(tx, CHUNK_SIZE)`
  - `cy = floorDivInt(ty, CHUNK_SIZE)`
  - `lx = modPositive(tx, CHUNK_SIZE)`
  - `ly = modPositive(ty, CHUNK_SIZE)`
  - `idx = lx + ly * CHUNK_SIZE`
- world streaming requests use chunk coords derived from world coords:
  - `ccx = floor((wx/TILE_WORLD)/CHUNK_SIZE)`

File: `core/src/main/java/com/yourgame/survival/world/Chunk.java`
- `Chunk` is a thin wrapper: `{cx, cy, TileLayers layers}`

Implication for voxel/height refactor
- Any per-tile “height/stack” must map cleanly to the same `idx` (lx + ly*CHUNK_SIZE).

---

## 2) Current camera system (player-facing camera)

### 2.1 GameScreen cameras
File: `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
- World camera:
  - `private OrthographicCamera cam;`
  - created in `show()`:
    - `cam = new OrthographicCamera();`
    - `cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());`
- UI camera:
  - `private OrthographicCamera uiCam;` (separate)

### 2.2 Zoom handling + bindings
File: `GameScreen.show()`
- `cam.zoom` is set initially:
  - `cam.zoom = ZOOM_MAX;` (comment indicates forced maximal zoom-out)
- Mouse wheel is handled by a local `InputAdapter` ("BLOCK B: capture mouse wheel for zoom")
  - BUT: wheel is also multiplexed for other UI panels (quest popup, quest log panels, craft panel, skills panel).
  - Only when those panels are not active does wheel map into camera zoom via `pendingScrollY`.

Key insight for refactor
- Camera input is not “pure camera”: wheel is a shared binding depending on UI state.
- A “tilt” simulation must not break `cam.unproject/project` expectations for UI hitboxes.

### 2.3 Camera binding to player / clamping
File: `GameScreen` (later blocks)
- There is explicit logic that distinguishes:
  - `cam.position` being clamped to area bounds in AREA_MODE
  - and “player center” being different from camera center
  - (comment: “in AREA_MODE camera is clamped at edges”)

Refactor implication
- If you introduce height-based screen offsets, you must decide whether clamping uses:
  - base 2D world position,
  - or “visual position” (world + height offset). Using visual coords for clamp will feel wrong.

---

## 3) Current world system (generation + runtime representation)

### 3.1 World wrapper: streaming + queries
File: `World.java`
- World owns:
  - `BiomeSystem biomes`
  - `ChunkStore store` (backed by generator pipeline)
- Chunk lifecycle:
  - `chunk(cx,cy)` generates if missing
  - `peekChunk(cx,cy)` does not generate
  - `requestChunk(cx,cy)` queues generation
  - `tickStreaming(maxNewChunks)` consumes budget

### 3.2 Tile data structure
File: `core/src/main/java/com/yourgame/survival/world/TileLayers.java`
(Referenced earlier; key fields confirmed in project searches.)
- Contains the per-tile arrays:
  - `groundId[]`, `waterMask[]`, `roadMask[]`, `collisionMask[]`, etc.
  - Procedural fields: `biomeId[]`, `height[]`, `moisture[]`, …
  - **Terraced height:** `heightLevel[]`

### 3.3 Height exists already
File: `World.java`
- `heightLevelAtTilePeek(...)` and `heightLevelAtWorldPeek(...)` exist.
  - returns `c.layers.heightLevel[idx] & 0xFF`

File: `worldgen/paint/DefaultTilePainter.java` + `biome/BiomeSystem.java`
- `heightLevel` is filled from a biome height mask PNG (red channel 0..15).

Refactor implication
- The codebase already has a terraced height channel suitable for “voxel column height”.

---

## 4) Area system (authored areas) — build, load, save

### 4.1 Area files and schema
Runtime/editor uses `assets/areas/*.area.json`.

Key schema components (as currently applied):
- `size: { w, h }`
- `layers`:
  - `ground` with `defaultId`, `fills`, `patches`
  - `water` (mask)
  - `road` (mask)
  - optional: `cornerMasks` (prebaked)
- `markers`:
  - `playerSpawn`
  - `nodes` (entity spawns by EntityType name)
  - `poi`
  - `enemyZones`
  - `objects` (new schema) with `layerBias`
- top-level `tileTrees` bitmasks (present/cut) as Base64

### 4.2 Runtime loader: apply authored area into GameScreen
File: `core/src/main/java/com/yourgame/survival/worldmap/JsonAreaWorldLoader.java`
- Applies layers (ground/water/road), bakes corner masks, road adjacency.
- Applies markers:
  - `applyAuthoredObjects(...)`: spawns `markers.objects[]` and sets `Entities.layerBias`
  - `markers.nodes[]`: spawns entities by `EntityType.valueOf(tn)` (skips NODE_TREE)
  - special tile-tree streaming for templates FOREST_01 and HOME

Important: Authored areas are intentionally neutralized for procedural fields
File: `JsonAreaWorldLoader.applyGround(...)` (also similarly in editor applier)
- While filling defaults, it sets multiple generator fields to 0 including:
  - `L.heightLevel[idx] = 0;`

This is the current reason height doesn’t carry into authored areas.

### 4.3 Editor-side glue applier (tiles only)
File: `core/src/main/java/com/yourgame/survival/editor/AreaWorldApplier.java`
- Similar default fill logic.
- Explicitly sets `c.layers.heightLevel[idx] = 0;` (neutral preview).

### 4.4 Area editor (WorldEditorScreen)
File: `core/src/main/java/com/yourgame/survival/editor/WorldEditorScreen.java`
Responsibilities:
- Loads internal `areas/<templateId>.area.json` and applies to preview world via `JsonAreaWorldLoader.applyForEditor`.
- Saves back to disk with `saveCurrentAreaToDisk()`:
  - writes `areaJson.prettyPrint(...)` to a resolved write dir.
- Persistence:
  - `tileTrees` are persisted into JSON (Base64) via `persistTileTreeBitsToJson()`.

Refactor implication
- If voxel/height becomes authorable, it must become:
  - an additional persisted JSON layer (e.g. `layers.heightLevel` or a top-level `heightLevelB64`),
  - and `JsonAreaWorldLoader.applyGround` must stop zeroing heightLevel (or must apply from JSON after zeroing).

---

## 5) Entity system (objects, items, livings) — coords and runtime state

### 5.1 Entities container (SoA)
File: `core/src/main/java/com/yourgame/survival/entity/Entities.java`
- `spawn(EntityType t, float px, float py)` stores world coords
- Facing dir is cardinal: `dir[e]=2` (default South)
- Generic payload fields used by AI/animations:
  - `vx, vy` motion hint
  - `dir` last facing
  - `faceLock`, `aiT`, `aiT2`, `rot`, `aiF0`, `aiF1`, `data0`
- Item drop payload:
  - `itemId`, `itemAmount`
- **Render layering:** `layerBias[]`

Refactor implication
- If voxel height affects visuals, entities likely need a “stand-on height” term at render time.
  - Option A: derive from `World.heightLevelAtWorldPeek(x,y)` every frame (cost)
  - Option B: cache “z” per entity at spawn and update when moving across tiles.

---

## 6) Spawn systems (everything that spawns)

### 6.1 Procedural nodes spawner (world streaming)
File: `core/src/main/java/com/yourgame/survival/world/WorldNodeSpawner.java`
- Purpose:
  - deterministic seed-based spawns per chunk
  - consults `WorldNodes.removed` to avoid respawn
- Entry:
  - `ensureAround(es, nodes, px, py, radiusChunks)`
  - computes player chunk and iterates radius
  - does NOT force generation: if chunk missing, it requests it and skips
- Spawns are placed in world coords using tile center conversion.
- Special behaviors:
  - tree/bush jitter (visual) while keeping anchor tile for removal key

### 6.2 Removed nodes persistence
File: `core/src/main/java/com/yourgame/survival/world/WorldNodes.java`
- `WorldNodes.key(type, tx, ty)` packs:
  - 8 bits typeId + 28 bits x + 28 bits y (biased)
- Used by save/load (`SaveManager`) to persist removed nodes

### 6.3 Area-authored spawns
File: `JsonAreaWorldLoader.java`
- `markers.objects[]` → spawn EntityType with `layerBias`
- `markers.nodes[]` → spawn EntityType at tile center
- special template behaviors: HOME/FOREST tile-tree streaming

### 6.4 AI-driven movement (wander)
File: `core/src/main/java/com/yourgame/survival/systems/AiSystem.java`
- Ticks ORK_GRUNT, ANIMAL_DEER, ANIMAL_CHICKEN.
- Movement uses world collision/water checks:
  - `world.isWaterAtWorldPeek(nx,ny, default)`
  - `world.isBlockedAtWorldPeek(nx,ny, default)`
- Wander leash uses entity home position:
  - `homeX/homeY` and `wanderRadius`

Refactor implication
- If voxel height changes collision or movement cost, `isBlockedAt...` must be extended or a new height-aware query must be introduced.

---

## 7) Save system (savegame) — what must be considered

File: `core/src/main/java/com/yourgame/survival/data/SaveManager.java`

### 7.1 Save scope (major blocks)
Save writes:
- seed, player position, inventory, wallet, progress, needs, day time/index
- UI layout positions
- **FULL entity snapshot** (entities array):
  - type, x,y, vx,vy, dir, faceLock, aiT, aiT2, rot, aiF0, aiF1, d0, hp/hpMax, flags, itemId/itemAmount
  - chests embedded for chest entities
- merchant system state
- ZQS save block under key `"zqs"`
- procedural removed nodes (`WorldNodes.removed`) under key `"removedNodes"`
- WorldMap state (known areas, edges, exits, fog, treecut masks, removedAuthoredNodes, etc.)

### 7.2 Load behavior that matters for refactor
- Entities are fully wiped and respawned from save JSON.
- Removed procedural nodes are restored into `worldNodes.removed`.

Refactor implications
- If voxel height becomes part of authored areas or procedural world state, you must decide:
  - Is height purely regenerated from seed + biome masks (then no save change), OR
  - Is height player-authored / mutable (then height must be added to save).
- If entity “z” becomes explicit state, it must be saved or derived deterministically.

---

## 8) Height / voxel prerequisites already present

Confirmed present:
- `TileLayers.heightLevel[]` (terraced, editor + future gameplay comment)
- `World.heightLevelAtTilePeek/WorldPeek` accessors
- Worldgen reads a biome height mask PNG and assigns 0..15 levels

Confirmed present BUT currently disabled for authored areas:
- `JsonAreaWorldLoader.applyGround(...)` zeros `heightLevel` during default fill
- `AreaWorldApplier.fillDefault(...)` zeros `heightLevel`

This is the concrete integration seam for voxel-column authoring.

---

## 9) Checklist of “small things” that will break / must be handled in the refactor

Camera / input
- Wheel is shared (zoom + multiple panels). Any new wheel-driven height editing must not conflict.
- `cam.unproject/project` assumptions must remain coherent.

Rendering
- Entity sort key currently supports `Entities.layerBias`.
- If tiles gain visual height, entity vs tile depth ordering must be specified.

Spawning
- All tile-centered spawns use `(tx+0.5)*TILE_WORLD`.
- If “top surface” height changes, spawn world Y may need a visual offset only (not logic).

Saves
- Full entity snapshot currently has no explicit z/height.
- WorldNodes removed keys are tile-based and ignore height.

Areas
- HeightLevel is currently neutralized in both runtime area loader and editor applier.
- Editor save already persists tileTrees; height would need a similar persistence block.

AI/movement
- Uses water/blocked queries only; height is currently not a movement constraint.

---

## 10) Where to look next (code entrypoints)

- Camera + input + zoom: `screens/GameScreen.java`
- World coordinate + chunk math: `world/World.java`
- Tile storage: `world/TileLayers.java`
- Procedural node spawn: `world/WorldNodeSpawner.java`
- Removed node persistence: `world/WorldNodes.java`
- Save/load: `data/SaveManager.java`
- Area runtime loader: `worldmap/JsonAreaWorldLoader.java`
- Area editor: `editor/WorldEditorScreen.java`
- Editor tile apply (simple): `editor/AreaWorldApplier.java`

---

## Status
This document is a reference snapshot based on live code reads/searches in the Forked project.

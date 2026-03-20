# Checklist — Voxel Raster + 2.5D Tilt Refactor (Preparation Phase)

Constraints
- OFFLINE ONLY
- NO compile/run unless told
- NO git commits
- NO fallbacks that hide missing content
- No drive-by fixes

## Planning / analysis
- [x] Systems reference exists (camera/world/areas/spawns/saves/editor)
- [x] Surgical plan written with explicit removal points + dependency mapping

## Prepared code (no integration)
- [x] Add `VoxelHeightCodec` stub (Base64 heightLevel encode/decode) with references
- [x] Add `VoxelRenderMapping` stub (height step + tilt mapping) with references
- [x] Add `VoxelSideRegions` stub (side-face region mapping) with references

## Editor scaffolding (no behavior change)
- [x] Add helper skeleton for persisting `layers.heightLevel` into area JSON
- [x] Add helper skeleton for reading `layers.heightLevel` from area JSON

## Sanity
- [ ] No runtime wiring changes yet (NOTE: integration has started; this item will be replaced by integration-specific checks)
- [ ] No commits
- [ ] New files only added under explicit packages + documented in plan

## Renderer scaffolding (no behavior change)
- [x] Add unused voxel/tilt scaffolding hooks in ChunkRenderer and EntityRenderer

## Area loader scaffolding (no behavior change)
- [x] Add unused authored heightLevel hooks in JsonAreaWorldLoader + AreaWorldApplier

## Height editing utility (no wiring yet)
- [x] Add standalone HeightLevelEditing utility (export/import/brush placeholders)

## Height tool integration (editor)
- [x] Add HEIGHT as terrain layer target and allow brush to modify TileLayers.heightLevel

## Rendering (tiles) — first visible voxel step
- [x] ChunkRenderer lifts tile top faces using heightLevel and applies basic 2.5D tilt mapping

## Rendering (cliffs) — voxel faces
- [x] ChunkRenderer draws vertical faces (south/west) for heightLevel cliffs

## Side-face material mapping
- [x] VoxelSideRegions provides a single mapping point for side-face regions (placeholder now, real art later)

## Next TODO (content)
- [ ] Replace placeholder side faces with dedicated atlas regions once available

## Side-face quality (Phase 1)
- [x] Side faces reuse ground tile texture (tinted) for material consistency (no new art required)

## Tuning knobs
- [ ] Tune HEIGHT_STEP_WORLD and TILT_Y_SCALE for desired voxel depth and readability

## Mapping consistency
- [x] Render-space mapping uses VoxelRenderMapping.mapY/mapX helpers (no duplicated shear/tilt math)

## Rendering (procedural jitter) — match voxel lift + tilt
- [x] Procedural grass jitter positions are lifted/tilted with their tile height

## Gameplay rules (Phase 1)
- [x] Movement/AI rules remain height-agnostic (documented) OR introduce slope constraint (optional)

## Rendering (entities) — match voxel lift + tilt
- [x] EntityRenderer uses world.heightLevelAtWorldPeek to apply lift/tilt to entity sprites + sort key

## Rendering (tileTrees) — match voxel lift + tilt
- [x] EntityRenderer.drawTileTrees applies heightLevel lift + tilt

## Editor picking (voxel/tilt)
- [x] screen->tile mapping compensates tilt + lift so paint/move/zone/tileTree interactions remain accurate

## Gameplay input/UI projections (voxel/tilt)
- [x] GameScreen cam.project/unproject call sites compensate tilt + lift so UI anchors and mouseWorld remain correct

## Cursor warp (aim clamp)
- [x] Cursor warp-to-ring projects render-space coordinates under voxel/tilt

## World overlays (voxel/tilt)
- [x] Cursor + entity health bars render in voxel render-space

## Dot cursor helper (voxel/tilt)
- [x] drawDotCursorWorld maps input coords through toVoxelRenderSpace

## Projectiles/lines (voxel/tilt)
- [x] Arrow/projectile visualization is mapped to voxel render-space

## Action overlay (voxel/tilt)
- [x] FOV cone + action ring draw in voxel render-space

## World-space veils/shape overlays (voxel/tilt)
- [x] Area red-zone veil is drawn in voxel render-space (tilt-safe)

## Fog of War overlay (voxel/tilt)
- [x] FoW texture is drawn as a mapped quad (parallelogram) in voxel render-space

## Fullscreen overlays (day/night, boot)
- [x] Fullscreen overlays are screen-space and remain unchanged under voxel/tilt

## Spawn + streaming (Phase 1)
- [x] Spawn/movement math remains logical 2D (no voxel offsets applied to px/py, spawns, collision)

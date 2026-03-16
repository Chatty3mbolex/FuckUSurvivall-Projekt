# WorldEditor Implementation Checklist

> Guardrail: **NO COMPILE/RUN** until the entire editor is implemented.

Legend:
- [ ] todo
- [x] done
- [~] partial

## Phase 1 — Replacement skeleton + stable input
- [x] New package `com.yourgame.survival.editor`
- [x] New `WorldEditorScreen` skeleton exists
- [x] DesktopLauncher routes `--biome-editor/--editor` -> WorldEditor (replacement)
- [x] SurvivalGame can boot WorldEditor
- [x] HUD toggle (F3 + button)
- [~] Mouse mapping single source of truth (uses viewport.unproject; needs verification later)
- [x] 3x3 chunk grid overlay
- [x] 9 slot UI (3x3 SelectBoxes)
- [~] Refresh World (hard rebuild world object; needs full dispose rules later)

## Phase 2 — Example Map generator
- [x] Editor-only generator `EditorWorldGenerator` forces biome per chunk
- [~] Slots selection affects preview without requiring manual restart (AutoRefresh toggle added)
- [x] Visual slot labels on chunks

## Phase 3 — Painting across 3x3 (replace under cursor)
- [~] Convert mouse world->tile->chunk mapping (GROUND/ROAD/ZONE_MASK/HEIGHT started)
- [~] Hard edit bounds enforcement (tile bounds check added)
- [~] Tool system (buttons + scaffolding added; GROUND/ROAD/ZONE_MASK/HEIGHT wired)

## Phase 4 — True terraced height (voxel)
- [~] Add `heightLevel` layer to TileLayers (added)
- [x] Height brush complete: raise/lower (Erase/CTRL), flatten, reset (editor UI wired)
- [~] Cliff rendering (placeholder quads first) (now cross-chunk via peek)
- [~] Player collision respects height steps (movement blocks on height delta)
- [~] Zone-based height (preview apply) (ZoneHeightApplier + UI)

## Phase 5 — Auto transitions + rules
- [~] Re-bake transition masks after ground edits (editor dirty-rect rebake added)
- [x] Centralize allow-matrix (TransitionRules)

## Phase 6 — Derive + Save + Backups
- [~] biomes.json backup first + atomic write (EditorSaveUtil)
- [~] Zone mask PNG write (BiomeSystem.writeZoneMaskPng)
- [~] Height mask PNG write (per-biome, atomic) + biomes.json reference (heightMaskFile)
- [~] Save derived zone-height rules into biomes.json (per-biome, not global)
- [~] Derive node spawn rules from stamps (nodeStampRules -> spawnRules via MASK:)
- [ ] Derive height rules from painted data (beyond zone-height + height masks)

## Final
- [x] Remove legacy `BiomeEditorScreen` entrypoint usage (code removed)
- [ ] One-time compile + run sanity

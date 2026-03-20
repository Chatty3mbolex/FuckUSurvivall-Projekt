# Voxel Phase 1 — Behavior Notes (Height without gameplay)

Project: FUSA Story V0.092 Forked

Purpose
- Document the explicit Phase 1 policy for the voxel/height refactor.
- Avoid accidental half-constraints that make AI/player movement feel broken.

## Policy (Phase 1)
- `TileLayers.heightLevel` affects **rendering only** (visual lift + tilt + cliffs).
- Movement and collision remain **height-agnostic**:
  - `World.isBlockedAtWorldPeek(...)` remains the collision authority.
  - `World.isWaterAtWorldPeek(...)` remains the water authority.
  - AI uses these two checks only (see `systems/AiSystem.java`).

## Why
- Height-aware gameplay requires an explicit design for:
  - allowed slope/step height
  - pathfinding cost and constraints
  - cliff blocking vs visual-only cliffs
  - interaction (harvest nodes, pickups) at different heights
- Until those decisions are made, any partial constraint will create non-obvious bugs.

## Seam for Phase 2+
- Height-aware movement can be introduced by comparing:
  - `world.heightLevelAtWorldPeek(current)` vs `world.heightLevelAtWorldPeek(next)`
  - and blocking if `abs(dh) > stepLimit`.
- This should be implemented in the movement resolver(s), not in rendering.

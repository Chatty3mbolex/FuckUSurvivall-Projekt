# WorldEditor (Replacement for --biome-editor)

**DO NOT COMPILE until implementation is complete.**

This folder contains the new world authoring editor.

Goals:
- Remove legacy `BiomeEditorScreen` entrypoint (deleted).
- Provide two modes:
  - NUR_BIOME (single biome everywhere)
  - EXAMPLE_MAP (3x3 chunk slots, user-selectable biomes)
- Hard edit bounds: 3x3 chunks (cx/cy in [-1..1])
- Paint tools: biome/ground/height/roads/zones/stamps/entities
- Automatic transitions after ground paint.
- True terraced height (voxel-style): per-tile heightLevel + cliff rendering (placeholder quads first).
- Refresh: rebuild preview world from current settings; no old chunk cache.
- Save: derive settings + persist masks + backups.

Implementation guardrail:
- No gradle build/run until the whole editor is implemented.

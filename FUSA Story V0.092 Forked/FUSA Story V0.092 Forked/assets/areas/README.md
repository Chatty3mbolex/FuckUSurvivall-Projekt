# assets/areas

This folder contains **hand-authored Area templates** for the new story-first WorldMap system.

## Design goals (alpha scaffolding)

- **No procedural generation** for Area contents.
- Savegames store only `templateId` per discovered grid coordinate; the templates themselves live here as assets.
- Alpha templates:
  - `HOME`
  - `GEN_GRASSLAND`
  - `GEN_ROCKY_FIELDS`
  - `GEN_LIGHT_FOREST`

## File format

Each Area is described by a JSON file:

`assets/areas/<templateId>.area.json`

We keep the format versioned via `schema`.

### Schema: `FUSA_AREA_V1`

- `templateId`: stable ID, used in savegames (must not change for existing saves)
- `size`: fixed tile grid size
- `layers.ground`: **only layer used in alpha**
  - `defaultId`: ground tile id (see `assets/config/tileset.json`)
  - `fills`: rectangles that override default
  - `patches`: single-tile overrides
- `exits`: N/E/S/W sockets (currently only metadata, used by WorldMap runtime)
- `markers.playerSpawn`: where the player should appear when entering
- `reserved`: rectangles reserved for future build placement (e.g. houses)

### Ground tile IDs (existing in the game)

From `assets/config/tileset.json`:
- `0` = GRASS
- `1` = DIRT
- `2` = SAND
- `3` = ROCK
- `4` = SNOW
- `5` = LAVA (blocked)

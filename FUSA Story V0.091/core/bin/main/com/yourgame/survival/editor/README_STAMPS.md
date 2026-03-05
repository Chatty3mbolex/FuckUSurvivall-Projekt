# Stamps (Nodes + Encounters) in WorldEditor

- Node stamps and Encounter stamps are implemented as **painted zone masks**.
- The editor paints into zone mask names:
  - `STAMP_NODE_TREE|ROCK|IRON|BUSH`
  - `STAMP_ENCOUNTER_ORK_GRUNT|ANIMAL_DEER`

These masks are saved like any other zone mask under `config/biome_masks/...png` and referenced by name.

The *rules* are saved into `config/biomes.json`:
- `nodeStampRules`: type + mask + min/max + jitter
- `encounterStampRules`: type + mask + maxLoaded + respawnMin/Max

Runtime:
- `WorldNodeSpawner` already supports `zone=MASK:<name>`, so node stamp rules are derived into spawnRules at load.
- `EncounterSpawner` uses encounter stamp rules to constrain spawns to the painted mask.

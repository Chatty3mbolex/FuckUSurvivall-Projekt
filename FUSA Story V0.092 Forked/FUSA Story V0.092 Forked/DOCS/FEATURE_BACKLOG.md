# FEATURE_BACKLOG (extracted from code comments)

Purpose:
- Archive "Nicht fertiges Feature" blocks and large commented-out code blocks.
- Preserve content (NEVER delete) while keeping .java clean.

---

## Nicht fertiges Feature Archive

(Entries are appended as: file path + line range + original block.)

---

## Commented Code Archive

(Entries are appended as: file path + line range + original block.)

---

### Nicht fertiges Feature

- time: 2026-03-20 04:28:48
- file: core\src\main\java\com\yourgame\survival\biome\BiomeSystem.java
- lines: 953-955

`java
  // Nicht fertiges Feature:
  // Anchor + Node were intended as local helper structs for zone stamping / anchors,
  // but are currently unused. Keeping as commented code for later re-enable.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\render\ChunkRenderer.java
- lines: 13-14

`java
  // Nicht fertiges Feature: used only for safe neighbor sampling in blob-mask calculation (currently unused).
  // private World world;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\render\ChunkRenderer.java
- lines: 42-42

`java
    // Nicht fertiges Feature: // this.world = world; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\render\EntityRegions.java
- lines: 8-8

`java
// Nicht fertiges Feature: // import com.yourgame.survival.entity.Entities; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\render\EntityRegions.java
- lines: 87-87

`java
  // Nicht fertiges Feature: unused convenience overload (kept for later).
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\render\EntityRegions.java
- lines: 114-114

`java
  // Nicht fertiges Feature: unused overload (kept for later sprite packs with indexed static regions)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\CreditsScreen.java
- lines: 16-17

`java
  // Nicht fertiges Feature: game reference currently unused on this screen.
  // private final SurvivalGame game;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\CreditsScreen.java
- lines: 26-26

`java
    // Nicht fertiges Feature: // this.game = game; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 48-48

`java
// Nicht fertiges Feature: // import com.yourgame.survival.world.SpawnSettings; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 51-52

`java
// Nicht fertiges Feature: duplicate import (FileHandle is already imported above)
// import com.badlogic.gdx.files.FileHandle;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 71-73

`java
  // Nicht fertiges Feature: unused skills (kept for later re-enable)
  // private static final int SK_FISHING = SkillDefs.indexOf("Fishing");
  // private static final int SK_FORAGING = SkillDefs.indexOf("Foraging");
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 84-86

`java
  // Nicht fertiges Feature: unused skills (kept for later re-enable)
  // private static final int SK_CLIMBING = SkillDefs.indexOf("Climbing");
  // private static final int SK_BOATING = SkillDefs.indexOf("Boating");
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 119-120

`java
  // Harvest SFX (hold-to-hack)
// Nicht fertiges Feature:   private float hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 140-140

`java
// Nicht fertiges Feature:   private final UiState uiState = new UiState();
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 141-141

`java
// Nicht fertiges Feature:   private final CommandBuffer commands = new CommandBuffer();
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 144-144

`java
// Nicht fertiges Feature:   private com.yourgame.survival.sim.SimContext simContext = null;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 165-165

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 360-365

`java
// Nicht fertiges Feature:   private boolean inVoid = false;
// Nicht fertiges Feature:   private float voidTimer = 0f;
  // Void penalty stage:
  // 0 = not penalized yet
  // 1 = 50% HP penalty applied, next expiry kills player
// Nicht fertiges Feature:   private int voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 370-370

`java
// Nicht fertiges Feature:   private boolean mapZoomOpen = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 380-381

`java
  // Snapshot invalidation: update the map UI only when something changed.
// Nicht fertiges Feature:   private boolean worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 509-509

`java
// Nicht fertiges Feature:   private boolean hotbarLayoutValid = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 540-540

`java
// Nicht fertiges Feature:   private String sellBuffer = "";
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 556-558

`java
  // resolved equipped tool each frame (tools must exist in inventory + be in hotbar)
  // Nicht fertiges Feature: unused (logic uses equippedFromHotbar() + method param instead)
  // private int equippedItemId = -1;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:49
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 623-623

`java
// Nicht fertiges Feature:   private boolean playerAimInit = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 641-643

`java
// Nicht fertiges Feature:   private static final float REACH_BUILD_REMOVE = com.yourgame.survival.tuning.TuningGameplay.REACH_BUILD_REMOVE;
// Nicht fertiges Feature:   private static final float REACH_SHOP = com.yourgame.survival.tuning.TuningGameplay.REACH_SHOP;
// Nicht fertiges Feature:   private static final float REACH_BED = com.yourgame.survival.tuning.TuningGameplay.REACH_BED;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 791-792

`java
    // Block 8: refresh sim context references
// Nicht fertiges Feature:     simContext = new com.yourgame.survival.sim.SimContext(world, entities, inv, wallet, progress, needs, priceBook);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 793-793

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1573-1575

`java
// Nicht fertiges Feature:         inVoid = false;
// Nicht fertiges Feature:         voidTimer = 0f;
// Nicht fertiges Feature:         voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1584-1584

`java
// Nicht fertiges Feature:         mapZoomOpen = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1586-1586

`java
// Nicht fertiges Feature:         worldMapDirty = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1591-1591

`java
// Nicht fertiges Feature:         mapZoomOpen = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1640-1640

`java
// Nicht fertiges Feature:             mapZoomOpen = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1824-1824

`java
// Nicht fertiges Feature:     playerAimInit = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1991-1991

`java
// Nicht fertiges Feature:           hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1997-1998

`java
      // release stops hack loop immediately
// Nicht fertiges Feature:       hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 2439-2439

`java
// Nicht fertiges Feature:           mapZoomOpen = (mapViewMode == 1);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 2936-2939

`java
    // Nicht fertiges Feature: sampled input values are currently never read
    // out.delta = delta;
    // out.mx = Gdx.input.getX();
    // out.my = Gdx.input.getY();
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 3854-3857

`java
      // Areas-only: player can never step into VOID.
// Nicht fertiges Feature:       inVoid = false;
// Nicht fertiges Feature:       voidTimer = 0f;
// Nicht fertiges Feature:       voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 3869-3869

`java
// Nicht fertiges Feature:       inVoid = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 3870-3871

`java
// Nicht fertiges Feature:       voidTimer = 0f;
// Nicht fertiges Feature:       voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4423-4423

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4990-4990

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6445-6445

`java
// Nicht fertiges Feature:   sellBuffer = String.valueOf(sellAmount);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6453-6453

`java
// Nicht fertiges Feature:   sellBuffer = String.valueOf(v);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6462-6462

`java
// Nicht fertiges Feature:   sellBuffer = "";
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6673-6673

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7387-7387

`java
// Nicht fertiges Feature:     hotbarLayoutValid = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:50
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7408-7408

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7424-7424

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7451-7451

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7631-7631

`java
// Nicht fertiges Feature:           worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7882-7882

`java
// Nicht fertiges Feature:                     worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8037-8037

`java
// Nicht fertiges Feature:       worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8064-8064

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8075-8075

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8121-8121

`java
// Nicht fertiges Feature:         worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8320-8320

`java
// Nicht fertiges Feature:         worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8361-8361

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8624-8624

`java
    // Nicht fertiges Feature: sellBuffer = "";
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8869-8869

`java
// Nicht fertiges Feature:       hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9664-9664

`java
  // Nicht fertiges Feature: unused placeholder type (no current gameflow usage)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9672-9675

`java
    // Nicht fertiges Feature: currently never read
    // float delta;
    // int mx;
    // int my;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9674-9674

`java
  // Nicht fertiges Feature: unused placeholder type (no current gameflow usage)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9683-9683

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\OptionsScreen.java
- lines: 30-34

`java
  // Nicht fertiges Feature: unused layout cache (can be re-enabled if we reuse absolute anchors)
  // private float x0;
  // private float y0;
  // Nicht fertiges Feature: unused (button hit-tests use local sizes)
  // private float w;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\OptionsScreen.java
- lines: 82-82

`java
    // Nicht fertiges Feature: w = 520 * UI_SCALE; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\OptionsScreen.java
- lines: 92-95

`java
    // Nicht fertiges Feature: unused layout cache
    // x0 = (wScreen - w) * 0.5f;
    // y0 = hScreen * 0.5f + 140;
    // Suppress "never read" hints for the params while the cache is disabled.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\SaveLoadScreen.java
- lines: 314-314

`java
  // Nicht fertiges Feature: unused helper overload
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\TileEditorScreen.java
- lines: 35-35

`java
// Nicht fertiges Feature: // import java.util.Locale; // (unused; uses fully-qualified java.util.Locale.US below)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\TutorialScreen.java
- lines: 19-20

`java
  // Nicht fertiges Feature: unused
  // private final SurvivalGame game;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\screens\TutorialScreen.java
- lines: 31-31

`java
    // Nicht fertiges Feature: this.game = game; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\systems\EntityCollisionSystem.java
- lines: 18-19

`java
    // Nicht fertiges Feature: reserved for special-case player-vs-entity collision tuning
    // int player = -1;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:51
- file: core\src\main\java\com\yourgame\survival\systems\EntityCollisionSystem.java
- lines: 28-28

`java
      // Nicht fertiges Feature: if (t == EntityType.PLAYER) player = i; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\systems\HarvestSystem.java
- lines: 102-103

`java
        // Only harvest from below / lower side (not from the crown).
        // Nicht fertiges Feature: // final float treeW = EntityMetrics.drawW(EntityType.NODE_TREE); // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\AssetEditorScreen.java
- lines: 33-34

`java
// Nicht fertiges Feature: unused imports (we use fully-qualified names below)
// import java.nio.file.Files;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\AssetEditorScreen.java
- lines: 758-758

`java
  // Nicht fertiges Feature: unused helper (we do not open FileDialogs on Windows)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\AssetEditorScreen.java
- lines: 1766-1767

`java
    // Nicht fertiges Feature: unused local
    // TextureAtlas.AtlasRegion r = currentFrames.get(0);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\scan\AssetIndexScanner.java
- lines: 16-16

`java
// Nicht fertiges Feature: // import java.util.HashSet; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\scan\AssetIndexScanner.java
- lines: 18-18

`java
// Nicht fertiges Feature: // import java.util.Set; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\ui\MiniSkin.java
- lines: 3-3

`java
// Nicht fertiges Feature: // import com.badlogic.gdx.Gdx; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\world\WorldNodeSpawner.java
- lines: 457-459

`java
  // Nicht fertiges Feature:
  // seedForRule(...) + computeZoneAreaTiles(...)
  // (Unused right now; keeping as commented code for later.)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\adjacency\DefaultAdjacencyBuilder.java
- lines: 4-4

`java
// Nicht fertiges Feature: import com.yourgame.survival.world.TileIds; // unused
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\adjacency\DefaultAdjacencyBuilder.java
- lines: 8-8

`java
// Nicht fertiges Feature: import com.yourgame.survival.worldgen.util.GenMath; // unused
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\adjacency\DefaultAdjacencyBuilder.java
- lines: 61-62

`java
  // Nicht fertiges Feature:
  // groundAtGlobalTile(...) (unused helper right now)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\deco\DefaultDecoScatter.java
- lines: 25-25

`java
      // Nicht fertiges Feature: // int rock = l.rockiness[idx] & 0xFF; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\paint\DefaultEdgeGroundBlender.java
- lines: 4-4

`java
// Nicht fertiges Feature: // import com.yourgame.survival.world.TileIds; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\paint\DefaultTilePainter.java
- lines: 6-6

`java
// Nicht fertiges Feature: // import com.yourgame.survival.world.World; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\roads\RoadAdjacencyBaker.java
- lines: 4-4

`java
// Nicht fertiges Feature: // import com.yourgame.survival.world.World; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldgen\water\DefaultWaterPostProcessor.java
- lines: 6-6

`java
// Nicht fertiges Feature: // import com.yourgame.survival.world.World; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldmap\JsonAreaWorldLoader.java
- lines: 800-800

`java
      // Nicht fertiges Feature: // Random r = new Random(seed ^ 0xBEEFF00DL); // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldmap\JsonAreaWorldLoader.java
- lines: 962-964

`java
  // Nicht fertiges Feature:
  // placeTerracedMountain(...) + setMountainTile(...)
  // (Unused right now; keeping as commented code for later.)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 04:28:52
- file: core\src\main\java\com\yourgame\survival\worldmap\WorldMapRuntime.java
- lines: 154-154

`java
    // Nicht fertiges Feature: // AreaCoord cur = new AreaCoord(st.curAx, st.curAy); // (unused)
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\biome\BiomeSystem.java
- lines: 953-973

`java
  /*
  private static final class Anchor {
    public final int tx;
    public final int ty;

    private Anchor(int tx, int ty) {
      this.tx = tx;
      this.ty = ty;
    }
  }

  private static final class Node {
    public final int tx;
    public final int ty;

    private Node(int tx, int ty) {
      this.tx = tx;
      this.ty = ty;
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\render\EntityRegions.java
- lines: 87-93

`java
  /*
  private TextureRegion reqLiving(String name) {
    TextureAtlas.AtlasRegion r = livingAtlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing living atlas region: " + name);
    return r;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\render\EntityRegions.java
- lines: 107-114

`java
  /*
  private TextureRegion reqStatic(String name, int index) {
    TextureAtlas.AtlasRegion r = staticAtlas.findRegion(name, index);
    if (r == null) r = staticAtlas.findRegion(name);
    if (r == null) throw new IllegalStateException("Missing static atlas region: " + name + " (idx=" + index + ")");
    return r;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 165-171

`java
  /*
  private static long fnv1aStep(long h, long v) {
    // 64-bit FNV-1a
    h ^= v;
    return h * 0x100000001b3L;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 786-818

`java
  /*
  private void preloadChunksBlocking(int radiusChunks, int timeoutMs) {
    if (world == null) return;

    int r = Math.max(0, radiusChunks);
    long t0 = System.currentTimeMillis();

    world.requestAroundWorld(px, py, r);

    for (;;) {
      // Commit everything that's ready right now.
      world.tickStreaming(10_000);

      // Check if all chunks in the preload square are available.
      int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);
      int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);
      boolean ok = true;
      for (int dy = -r; dy <= r && ok; dy++) {
        for (int dx = -r; dx <= r; dx++) {
          if (world.peekChunk(ccx + dx, ccy + dy) == null) { ok = false; break; }
        }
      }
      if (ok) return;

      if (timeoutMs > 0 && (System.currentTimeMillis() - t0) > timeoutMs) return;

      try { Thread.sleep(2); } catch (InterruptedException ignored) {}

      // Keep requesting (in case something got evicted/never queued).
      world.requestAroundWorld(px, py, r);
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4383-4396

`java
  /*
  private boolean isNearBuild(EntityType t, float range) {
    float eff = range + com.yourgame.survival.entity.EntityMetrics.radius(t);
    float r2 = eff * eff;
    for (int i=0;i<Entities.MAX;i++) {
      if (!entities.alive[i]) continue;
      if (entities.type[i] != t) continue;
      float dx = entities.x[i] - px;
      float dy = entities.y[i] - py;
      if (dx*dx + dy*dy <= r2) return true;
    }
    return false;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4936-4961

`java
  /*
  private void shopBuyIndex(int idx, int amount) {
    if (idx < 0 || idx >= shopOfferCount) return;
    if (amount <= 0) return;
    int itemId = shopItemId[idx];
    int priceEach = shopBuy[idx];

    // Barter/Negotiation: better prices per level (5% each level)
    int barterLv = (SK_BARTER >= 0 && SK_BARTER < progress.skillLv.length) ? progress.skillLv[SK_BARTER] : 1;
    int negoLv = (SK_NEGOTIATION >= 0 && SK_NEGOTIATION < progress.skillLv.length) ? progress.skillLv[SK_NEGOTIATION] : 1;
    float buyMul = 1f;
    buyMul *= Math.max(0.20f, 1f - 0.05f * Math.max(0, barterLv - 1));
    buyMul *= Math.max(0.35f, 1f - 0.05f * Math.max(0, negoLv - 1));
    priceEach = Math.max(1, (int) Math.floor(priceEach * buyMul));

    long total = (long) priceEach * amount;
    if (total <= 0) return;

    if (wallet.spendCopper(total)) {
      inv.add(itemId, amount);
      game.audio.sfx("audio/sfx/craft.wav", game.audio.sfxVolume(game.settings));
    } else {
      game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6593-6612

`java
  /*
  private void drawCraftLines(float x, float y) {
    // legacy text renderer (still used inside the craft panel)
    int[] outs = {40, 14, 15};
    for (int i=0;i<outs.length;i++) {
      com.yourgame.survival.data.RecipeDef r = craft.findByOutput(data.recipes, outs[i]);
      if (r == null) continue;
      String name = (data.items[r.outItemId] != null) ? data.items[r.outItemId].name : ("item_" + r.outItemId);
      StringBuilder req = new StringBuilder();
      for (int k=0;k<r.inItemId.length;k++) {
        int id = r.inItemId[k];
        String inName = (data.items[id] != null) ? data.items[id].name : ("item_"+id);
        if (k>0) req.append(", ");
        req.append(inName).append("x").append(r.inAmount[k]);
      }
      boolean ok = craft.canCraft(inv, r);
      font.draw(batch, (i+1) + ") " + name + (ok?" [OK] ":" [NO] ") + "<= " + req, x, y - i*22f);
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7308-7322

`java
  /*
  private void drawInventoryLines(float x, float y) {
    // legacy (unused) - kept for quick debug
    int shown = 0;
    for (int id=0; id<inv.countsById.length; id++) {
      int c = inv.countsById[id];
      if (c <= 0) continue;
      String name = (data.items[id] != null) ? data.items[id].name : ("item_"+id);
      font.draw(batch, name + ": " + c, x, y - shown*18);
      shown++;
      if (shown >= 12) break;
    }
    if (shown == 0) font.draw(batch, "(empty)", x, y);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7309-7318

`java
  /*
  private void drawBuildLines(float x, float y) {
    String[] names = {"Workbench","Bed","Campfire","Lamp"};
    for (int i=0;i<names.length;i++) {
      String sel = (i == buildSel) ? ">" : " ";
      font.draw(batch, sel + (i+1) + ") " + names[i] + " rot=" + (int)buildRot, x, y - i*18);
    }
    font.draw(batch, "Placement rule: not on water.", x, y - names.length*18 - 10);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7326-7339

`java
  /*
  private void drawChestLines(float x, float y) {
    if (openChestE < 0) return;
    int idx = entities.data0[openChestE];
    com.yourgame.survival.data.Inventory chest = chestStore.get(idx);
    if (chest == null) {
      font.draw(batch, "(broken chest)", x, y);
      return;
    }

    font.draw(batch, "Player wood=" + inv.countsById[0] + " stone=" + inv.countsById[1] + " coins=" + inv.countsById[31], x, y);
    font.draw(batch, "Chest  wood=" + chest.countsById[0] + " stone=" + chest.countsById[1] + " coins=" + chest.countsById[31], x, y - 18);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7925-7934

`java
  /*
  private static boolean fogBitGet(byte[] fog, int idx) {
    if (fog == null) return false;
    int bi = idx >> 3;
    int bit = idx & 7;
    if (bi < 0 || bi >= fog.length) return false;
    int mask = 1 << bit;
    return (fog[bi] & mask) != 0;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7926-7937

`java
  /*
  private static boolean fogBitSet(byte[] fog, int idx) {
    if (fog == null) return false;
    int bi = idx >> 3;
    int bit = idx & 7;
    if (bi < 0 || bi >= fog.length) return false;
    byte mask = (byte) (1 << bit);
    if ((fog[bi] & mask) != 0) return false;
    fog[bi] |= mask;
    return true;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9175-9483

`java
        font.draw(batch, "(keine Presets gefunden in: pricing_presets/*.json)", x, popupY);
      } else {
        for (int i = 0; i < pricingPresetNames.length; i++) {
          font.draw(batch, (i + 1) + ") " + pricingPresetNames[i], x, popupY - i * 18f);
        }
      }
    }
  }

  private void preloadSkillIcons() {
    if (skillIconsLoaded) return;

    for (int i = 0; i < SkillDefs.COUNT; i++) {
      String path = SkillDefs.ICON_PATH[i];
      if (path == null || path.isEmpty()) continue;

      try {
        FileHandle fh = Gdx.files.internal(path);
        if (fh == null || !fh.exists()) continue;

        Texture t = new Texture(fh);
        // crisp pixel art
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skillIconTex.put(i, t);
      } catch (Throwable ignored) {
        // keep menu usable even if one icon fails
      }
    }

    skillIconsLoaded = true;
  }

  private Texture getSkillIconTex(int idx) {
    if (idx < 0 || idx >= SkillDefs.COUNT) return null;
    return skillIconTex.get(idx);
  }

  private static String keyCatSub(String catKey, String subLabel) {
    return catKey + "::" + subLabel;
  }

  private void addSkillMenuSub(String catKey, String sub, String... skillIds) {
    String subKey = keyCatSub(catKey, sub);
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER2, sub, -1, catKey, subKey));
    skillMenuSubOpen.putIfAbsent(subKey, true);

    for (String sid : skillIds) {
      int idx = SkillDefs.indexOf(sid);
      if (idx >= 0) skillMenuRows.add(new SkillMenuRow(SkillMenuRow.SKILL, SkillDefs.NAME_DE[idx], idx, catKey, subKey));
    }
  }

  private void buildSkillMenuRows() {
    if (skillMenuRowsBuilt) return;
    skillMenuRows.clear();

    // Matches docs/Vision_Skizze.png structure: 5 categories with subcategories.
    // Ressourcen
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Ressourcen", -1, "Ressourcen", null));
    skillMenuCatOpen.putIfAbsent("Ressourcen", true);
    addSkillMenuSub("Ressourcen", "Sammeln", "Mining", "Woodcutting", "Herbalism", "Hunting", "Fishing", "Foraging");
    addSkillMenuSub("Ressourcen", "Verarbeitung", "Smelting", "Tanning", "Alchemy", "Cooking");

    // Kampf
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Kampf", -1, "Kampf", null));
    skillMenuCatOpen.putIfAbsent("Kampf", true);
    addSkillMenuSub("Kampf", "Waffen", "CombatMelee", "CombatRanged", "WeaponCraft", "DualWield", "ShieldMastery");
    addSkillMenuSub("Kampf", "Verteidigung", "ArmorCraft", "Dodging", "Parrying", "Toughness");
    addSkillMenuSub("Kampf", "Taktik", "Stealth", "Tracking", "Looting");

    // Soziales
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Soziales", -1, "Soziales", null));
    skillMenuCatOpen.putIfAbsent("Soziales", true);
    addSkillMenuSub("Soziales", "Handel", "Trading", "Barter", "Intimidation", "Persuasion");
    addSkillMenuSub("Soziales", "Support", "Leadership", "Negotiation", "Reputation");

    // Person
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Person", -1, "Person", null));
    skillMenuCatOpen.putIfAbsent("Person", true);
    addSkillMenuSub("Person", "Vitals", "Health", "Stamina", "HungerControl", "SleepControl");
    addSkillMenuSub("Person", "Mobilität", "CarryCapacity", "Climbing", "Boating", "FlyingEfficiency");
    addSkillMenuSub("Person", "Survival", "ColdResistance", "HeatResistance");

    // Andere
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Andere", -1, "Andere", null));
    skillMenuCatOpen.putIfAbsent("Andere", true);
    addSkillMenuSub("Andere", "Bauen", "Building");
    addSkillMenuSub("Andere", "Übernatürlich", "MagicAffinity", "SpiritSight", "Blessing");
    addSkillMenuSub("Andere", "Utility", "Crafting", "Engineering", "ToolDurability", "Navigation", "Scavenging");

    skillMenuRowsBuilt = true;
  }

  private void drawSkillMenu() {
    // Modal overlay. Simulation is paused elsewhere.
    float mx = Gdx.input.getX();
    float my = uiMouseYUp();

    // Close (ESC). Note: P toggling is handled in the main input section to avoid instant close on the open-press.
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      skillsOpen = false;
      return;
    }

    // Fit to screen (avoid negative x/y on smaller resolutions)
    float screenW = Gdx.graphics.getWidth();
    float screenH = Gdx.graphics.getHeight();
    float w = Math.min(1120f, screenW - 40f);
    float h = Math.min(720f, screenH - 40f);
    w = Math.max(520f, w);
    h = Math.max(360f, h);
    float x0 = (screenW - w) * 0.5f;
    float y0 = (screenH - h) * 0.5f;
    if (x0 < 20f) x0 = 20f;
    if (y0 < 20f) y0 = 20f;

    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(uiRegions.panelSlots, x0, y0, w, h);

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(1.6f * UI_FONT_SCALE);
    font.draw(batch, "SKILLBAUM", x0 + 36f, y0 + h - 28f);

    font.getData().setScale(1.0f * UI_FONT_SCALE);
    font.draw(batch, "Abenteurer Lvl: " + progress.level + "   XP: " + progress.xp + "/" + progress.xpToNext + "   Skillpunkte: " + progress.skillPoints, x0 + 36f, y0 + h - 70f);
    font.draw(batch, "ESC oder P = schließen | Klick auf Skill = +1", x0 + 36f, y0 + h - 104f);

    float listX = x0 + 36f;
    float listYTop = y0 + h - 150f;
    float listYBottom = y0 + 36f;

    // row metrics
    float rowSkillH = 42f * UI_FONT_SCALE;
    float rowHeader1H = 44f * UI_FONT_SCALE;
    float rowHeader2H = 38f * UI_FONT_SCALE;

    // Compute total content height (visible rows only) to clamp scroll.
    float totalH = 0f;
    for (int ri = 0; ri < skillMenuRows.size(); ri++) {
      SkillMenuRow r = skillMenuRows.get(ri);
      boolean visible = true;
      if (r.kind != SkillMenuRow.HEADER1) {
        visible = skillMenuCatOpen.getOrDefault(r.catKey, true);
        if (visible && r.kind == SkillMenuRow.SKILL) {
          visible = skillMenuSubOpen.getOrDefault(r.subKey, true);
        }
        if (visible && r.kind == SkillMenuRow.HEADER2) {
          visible = true; // sub headers are visible when category is open
        }
      }
      if (!visible) continue;
      float rh = (r.kind == SkillMenuRow.SKILL) ? (42f * UI_FONT_SCALE) : (r.kind == SkillMenuRow.HEADER1 ? (44f * UI_FONT_SCALE) : (38f * UI_FONT_SCALE));
      totalH += rh;
    }
    float viewH = Math.max(1f, listYTop - listYBottom);
    float maxScroll = Math.max(0f, totalH - viewH);
    if (skillMenuScrollPx > maxScroll) skillMenuScrollPx = maxScroll;

    String catHoverKey = null;
    String subHoverKey = null;

    skillHover = -1;
    float y = listYTop + skillMenuScrollPx;
    for (int ri = 0; ri < skillMenuRows.size(); ri++) {
      SkillMenuRow r = skillMenuRows.get(ri);

      if (r.kind != SkillMenuRow.HEADER1) {
        boolean catOpen = skillMenuCatOpen.getOrDefault(r.catKey, true);
        if (!catOpen) continue;

        // skills are only visible if their subcategory is open
        if (r.kind == SkillMenuRow.SKILL) {
          boolean subOpen = skillMenuSubOpen.getOrDefault(r.subKey, true);
          if (!subOpen) continue;
        }
      }

      float rh = (r.kind == SkillMenuRow.SKILL) ? rowSkillH : (r.kind == SkillMenuRow.HEADER1 ? rowHeader1H : rowHeader2H);
      float ry = y;
      // skip rows above the visible window
      if (ry > listYTop + rh) {
        y -= rh;
        continue;
      }
      // stop when we are past the bottom
      if (ry < listYBottom) break;

      switch (r.kind) {
        case SkillMenuRow.HEADER1 -> {
          boolean hov = mx >= listX && mx <= listX + (w - 72f) && my >= ry - rh + 6f && my <= ry + 6f;
          if (hov) catHoverKey = r.catKey;

          boolean open = skillMenuCatOpen.getOrDefault(r.catKey, true);
          String twisty = open ? "[-]" : "[+]";

          // Category: distinct big button
          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, listX, ry - rh + 8f, w - 72f, rh - 8f);

          font.setColor(0f, 0f, 0f, 1f);
          font.getData().setScale(1.20f * UI_FONT_SCALE);
          font.draw(batch, twisty + "  " + r.label, listX + 16f, ry - 12f);
        }
        case SkillMenuRow.HEADER2 -> {
          // Subcategory indent (about "2 tiles"), keep right edge aligned by shrinking width.
          float subIndent = 64f;
          float subX = listX + 10f + subIndent;
          float subW = (w - 72f) - 20f - subIndent;

          boolean hov = mx >= subX && mx <= subX + subW && my >= ry - rh + 6f && my <= ry + 6f;
          if (hov) subHoverKey = r.subKey;

          boolean open = skillMenuSubOpen.getOrDefault(r.subKey, true);
          String twisty = open ? "[-]" : "[+]";

          // Subcategory: distinct medium row
          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, subX, ry - rh + 10f, subW, rh - 12f);

          font.setColor(0f, 0f, 0f, 1f);
          font.getData().setScale(1.05f * UI_FONT_SCALE);
          font.draw(batch, twisty + "  " + r.label, subX + 18f, ry - 12f);
        }
        default -> {
          int i = r.skillIndex;

        // Skill indent (relative to subcategory position), keep right edge aligned.
        float subIndentBase = 64f;
        float skillIndent = subIndentBase + 64f;
        float sx = listX + skillIndent;
        float sw = (w - 72f) - skillIndent;

        boolean hov = mx >= sx && mx <= sx + sw && my >= ry - rh + 6f && my <= ry + 6f;
        if (hov) skillHover = i;

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, sx, ry - rh + 8f, sw, rh - 8f);

        int lv = (i >= 0 && i < progress.skillLv.length) ? progress.skillLv[i] : 0;
        int cost = SkillEffects.upgradeCost(lv);
        String line = SkillDefs.NAME_DE[i] + "   [" + lv + "/50]" + "   Kosten: " + cost;

        float iconS = Math.min(32f * UI_FONT_SCALE, rh - 12f);
        Texture iconTex = getSkillIconTex(i);
        float textX = sx + 20f;
        if (iconTex != null) {
          float ix = sx + 10f;
          float iy = (ry - rh + 8f) + ((rh - 8f) - iconS) * 0.5f;

          // Keep aspect ratio (avoid squashing tall/wide sprites)
          float tw = Math.max(1f, iconTex.getWidth());
          float th = Math.max(1f, iconTex.getHeight());
          float s = Math.min(iconS / tw, iconS / th);
          float dw = tw * s;
          float dh = th * s;
          float dx = ix + (iconS - dw) * 0.5f;
          float dy = iy + (iconS - dh) * 0.5f;

          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(iconTex, dx, dy, dw, dh);
          textX = ix + iconS + 14f;
        }

        font.setColor(0f, 0f, 0f, 1f);
        font.getData().setScale(1.0f * UI_FONT_SCALE);
        font.draw(batch, line, textX, ry - 12f);
        }
      }

      y -= rh;
    }

    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
      // Toggle category dropdown
      if (catHoverKey != null) {
        boolean open = skillMenuCatOpen.getOrDefault(catHoverKey, true);
        skillMenuCatOpen.put(catHoverKey, !open);
        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));
        return;
      }

      // Toggle subcategory dropdown
      if (subHoverKey != null) {
        boolean open = skillMenuSubOpen.getOrDefault(subHoverKey, true);
        skillMenuSubOpen.put(subHoverKey, !open);
        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));
        return;
      }

      // Click skill
      if (skillHover >= 0) {
        int i = skillHover;
        if (i >= 0 && i < progress.skillLv.length) {
          int curLv = progress.skillLv[i];
          int cost = SkillEffects.upgradeCost(curLv);

          if (curLv < 50 && progress.skillPoints >= cost) {
             
          } else {
            game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));
          }
        } else {
          game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));
        }
      }
    }
  }

  /** Ensures cursor state is re-applied after leaving a modal screen (Pause/Options/etc). */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9194-9199

`java
  /*
  private static final class UiState {
    // Placeholder for future UI state consolidation.
    boolean reserved;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9198-9204

`java
  /*
  private static final class CommandBuffer {
    // Placeholder for future command buffering.
    int size = 0;
    void clear() { size = 0; }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9200-9207

`java
  /*
  private static int anchorToStep(int tile, int step) {
    if (step <= 1) return tile;
    // Round to nearest multiple of step (works for negative coordinates too).
    int m = Math.round(tile / (float) step);
    return m * step;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\screens\SaveLoadScreen.java
- lines: 314-318

`java
  /*
  private void drawButton(String text, float x, float y) {
    drawButton(text, x, y, 1f, 1f, 1f);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:41
- file: core\src\main\java\com\yourgame\survival\tools\asseteditor\AssetEditorScreen.java
- lines: 758-763

`java
  /*
  private void onChooseCandidateFolder() {
    // Deprecated: we intentionally do NOT open java.awt.FileDialog here because it can hard-crash LWJGL3 on Windows.
    setStatus("INFO: paste folder path and press Set Folder");
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:42
- file: core\src\main\java\com\yourgame\survival\world\WorldNodeSpawner.java
- lines: 457-523

`java
  /*
  private static long seedForRule(BiomeSystem.SpawnRule r, int cx, int cy, int ri) {
    long h = 0x9E3779B97F4A7C15L;
    h ^= (long) cx * 0xBF58476D1CE4E5B9L;
    h ^= (long) cy * 0x94D049BB133111EBL;
    h ^= (long) ri * 0xD6E8FEB86659FD93L;
    if (r != null && r.type != null) h ^= r.type.hashCode() * 0x9E3779B97F4A7C15L;
    if (r != null && r.zone != null) h ^= r.zone.hashCode() * 0xBF58476D1CE4E5B9L;
    return h;
  }

  private static int computeZoneAreaTiles(BiomeSystem bs, Biome biome, BiomeSystem.BiomeDef bd, TileLayers layers, int edgeW, String zone) {
    int size = World.CHUNK_SIZE;
    if (zone == null) zone = "CORE";
    String z = zone.trim();
    String up = z.toUpperCase();
    int area = 0;

    // WATER: count water tiles
    if (up.equals("WATER")) {
      for (int i = 0; i < size * size; i++) if (layers.waterMask[i] != 0) area++;
      return area;
    }

    // MASK:<name>: count alpha pixels in mask
    if (up.startsWith("MASK:")) {
      String name = z.substring("MASK:".length()).trim();
      if (name.isEmpty() || bs == null || biome == null) return 0;
      for (int ly = 0; ly < size; ly++) {
        for (int lx = 0; lx < size; lx++) {
          if (bs.zoneMaskAt(biome, name, lx, ly)) area++;
        }
      }
      return area;
    }

    // EDGE / CORE: band area
    if (up.equals("EDGE") || up.equals("CORE")) {
      for (int ly = 0; ly < size; ly++) {
        for (int lx = 0; lx < size; lx++) {
          boolean isEdge = (lx < edgeW) || (ly < edgeW) || (lx >= size - edgeW) || (ly >= size - edgeW);
          if (up.equals("EDGE")) { if (isEdge) area++; }
          else { if (!isEdge) area++; }
        }
      }
      return area;
    }

    // CUSTOM:<name> rectangles
    if (up.startsWith("CUSTOM:")) {
      String name = z.substring("CUSTOM:".length()).trim();
      if (name.isEmpty()) return 0;
      for (int i = 0; i < bd.customZones.size(); i++) {
        BiomeSystem.CustomZone cz = bd.customZones.get(i);
        if (cz == null || cz.name == null) continue;
        if (!cz.name.equalsIgnoreCase(name)) continue;
        int x0 = Math.max(0, cz.x), y0 = Math.max(0, cz.y);
        int x1 = Math.min(size, cz.x + cz.w), y1 = Math.min(size, cz.y + cz.h);
        if (x1 > x0 && y1 > y0) area += (x1 - x0) * (y1 - y0);
      }
      return area;
    }

    // fallback: whole chunk
    return size * size;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:42
- file: core\src\main\java\com\yourgame\survival\worldgen\adjacency\DefaultAdjacencyBuilder.java
- lines: 61-95

`java
  /*
  private short groundAtGlobalTile(final int tx, final int ty, final WorldGenContext ctx) {
    // Must match the legacy ground decision for deterministic edge masks.
    final float height = noise.fbm01(ctx.seed ^ 0xA1B2C3D4E5F60718L, tx, ty, ctx.config.heightFreq, ctx.config.heightOctaves);
    final float heat = noise.fbm01(ctx.seed ^ 0x1122334455667788L, tx, ty, ctx.config.heatFreq, ctx.config.heatOctaves);
    final float moist = noise.fbm01(ctx.seed ^ 0x8877665544332211L, tx, ty, ctx.config.moistFreq, ctx.config.moistOctaves);

    final Biome b = classifier.classify(height, heat, moist);
    final boolean water = (b == Biome.WATER || b == Biome.RIVERBANK);

    short g = ctx.biomes.def(b).groundId;

    // mirror micro-classify (forcedBiome is never applied in global query)
    if (!water) {
      float veg01mc = GenMath.clamp01(moist * (1f - GenMath.clamp01((height - 0.70f) * 2.2f))
          * (0.65f + 0.35f * noise.fbm01(ctx.seed ^ 0x55AA55AA55AA55AAL, tx, ty, ctx.config.vegFreq, ctx.config.vegOctaves)));

      float rock01mc = GenMath.clamp01(GenMath.clamp01((height - 0.55f) * 1.6f)
          * (0.55f + 0.45f * noise.fbm01(ctx.seed ^ 0xCC33CC33CC33CC33L, tx, ty, ctx.config.rockFreq, ctx.config.rockOctaves)));

      float path01mc = GenMath.clamp01(noise.fbm01(ctx.seed ^ 0x0F0E0D0C0B0A0908L, tx, ty, ctx.config.pathFreq, ctx.config.pathOctaves)
          * (1f - veg01mc * 0.6f));

      if ((height > 0.78f && rock01mc > 0.60f) || b == Biome.MOUNTAIN) {
        g = TileIds.GROUND_ROCK;
      } else if ((path01mc > 0.68f && veg01mc < 0.62f) || (moist < 0.32f && veg01mc < 0.55f)) {
        g = TileIds.GROUND_DIRT;
      } else if (b == Biome.BEACH) {
        g = TileIds.GROUND_SAND;
      }
    }

    return g;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 04:35:42
- file: core\src\main\java\com\yourgame\survival\worldmap\JsonAreaWorldLoader.java
- lines: 962-1019

`java
  /*
  private static void placeTerracedMountain(World world, int areaW, int areaH, int x0, int y0, int radius, int maxHeight) {
    if (world == null) return;
    if (radius <= 0 || maxHeight <= 0) return;

    int r2 = radius * radius;
    for (int ty = y0 - radius; ty <= y0 + radius; ty++) {
      for (int tx = x0 - radius; tx <= x0 + radius; tx++) {
        int dx = tx - x0;
        int dy = ty - y0;
        int d2 = dx * dx + dy * dy;
        if (d2 > r2) continue;

        // Don't stomp authored/procedural water.
        if (isWaterAt(world, tx, ty)) continue;

        // Terraces: outer band=1, mid=2, inner=3 (for maxHeight=3).
        float t = 1f - (float) d2 / (float) r2; // 0..1
        int h;
        if (maxHeight == 1) {
          h = 1;
        } else {
          // Map t to [1..maxHeight] with 3-ish steps.
          // For maxHeight=3 this yields: ~outer=1, mid=2, core=3.
          h = 1 + (int) Math.floor(t * maxHeight);
          if (h > maxHeight) h = maxHeight;
          if (h < 1) h = 1;
        }

        setMountainTile(world, areaW, areaH, tx, ty, h);
      }
    }
  }

  private static void setMountainTile(World world, int areaW, int areaH, int tx, int ty, int h) {
    if (world == null) return;
    if (tx < 0 || ty < 0 || tx >= areaW || ty >= areaH) return;

    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    if (cx < 0 || cy < 0) return;
    if (cx >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_W) return;
    if (cy >= com.yourgame.survival.tuning.TuningAreas.AREA_CHUNKS_H) return;

    Chunk c = world.chunk(cx, cy);
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    int idx = lx + ly * World.CHUNK_SIZE;

    // Visual: rock ground + heightLevel cliffs.
    c.layers.groundId[idx] = (short) TileIds.GROUND_ROCK;
    c.layers.heightLevel[idx] = (byte) Math.max(0, Math.min(255, h));

    // Gameplay: for now, block the mountain tiles (so it behaves like a real obstacle).
    c.layers.collisionMask[idx] = 1;
    c.layers.waterMask[idx] = 0;
  }
  */
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 152-152

`java
// Nicht fertiges Feature: // import SpawnSettings; // (unused)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 155-156

`java
// Nicht fertiges Feature: duplicate import (FileHandle is already imported above)
// import com.badlogic.gdx.files.FileHandle;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 175-177

`java
  // Nicht fertiges Feature: unused skills (kept for later re-enable)
  // private static final int SK_FISHING = SkillDefs.indexOf("Fishing");
  // private static final int SK_FORAGING = SkillDefs.indexOf("Foraging");
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 188-190

`java
  // Nicht fertiges Feature: unused skills (kept for later re-enable)
  // private static final int SK_CLIMBING = SkillDefs.indexOf("Climbing");
  // private static final int SK_BOATING = SkillDefs.indexOf("Boating");
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 223-224

`java
  // Harvest SFX (hold-to-hack)
// Nicht fertiges Feature:   private float hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 244-244

`java
// Nicht fertiges Feature:   private final UiState uiState = new UiState();
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 245-245

`java
// Nicht fertiges Feature:   private final CommandBuffer commands = new CommandBuffer();
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 248-248

`java
// Nicht fertiges Feature:   private SimContext simContext = null;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 269-269

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 464-469

`java
// Nicht fertiges Feature:   private boolean inVoid = false;
// Nicht fertiges Feature:   private float voidTimer = 0f;
  // Void penalty stage:
  // 0 = not penalized yet
  // 1 = 50% HP penalty applied, next expiry kills player
// Nicht fertiges Feature:   private int voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 474-474

`java
// Nicht fertiges Feature:   private boolean mapZoomOpen = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 484-485

`java
  // Snapshot invalidation: update the map UI only when something changed.
// Nicht fertiges Feature:   private boolean worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 613-613

`java
// Nicht fertiges Feature:   private boolean hotbarLayoutValid = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 644-644

`java
// Nicht fertiges Feature:   private String sellBuffer = "";
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:39
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 660-662

`java
  // resolved equipped tool each frame (tools must exist in inventory + be in hotbar)
  // Nicht fertiges Feature: unused (logic uses equippedFromHotbar() + method param instead)
  // private int equippedItemId = -1;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 727-727

`java
// Nicht fertiges Feature:   private boolean playerAimInit = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 745-747

`java
// Nicht fertiges Feature:   private static final float REACH_BUILD_REMOVE = REACH_BUILD_REMOVE;
// Nicht fertiges Feature:   private static final float REACH_SHOP = REACH_SHOP;
// Nicht fertiges Feature:   private static final float REACH_BED = REACH_BED;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 891-892

`java
    // Block 8: refresh sim context references
// Nicht fertiges Feature:     simContext = new SimContext(world, entities, inv, wallet, progress, needs, priceBook);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 893-893

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1668-1670

`java
// Nicht fertiges Feature:         inVoid = false;
// Nicht fertiges Feature:         voidTimer = 0f;
// Nicht fertiges Feature:         voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1679-1679

`java
// Nicht fertiges Feature:         mapZoomOpen = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1681-1681

`java
// Nicht fertiges Feature:         worldMapDirty = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1686-1686

`java
// Nicht fertiges Feature:         mapZoomOpen = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1735-1735

`java
// Nicht fertiges Feature:             mapZoomOpen = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 1919-1919

`java
// Nicht fertiges Feature:     playerAimInit = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 2086-2086

`java
// Nicht fertiges Feature:           hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 2092-2093

`java
      // release stops hack loop immediately
// Nicht fertiges Feature:       hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 2493-2493

`java
// Nicht fertiges Feature:           mapZoomOpen = (mapViewMode == 1);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 2987-2990

`java
    // Nicht fertiges Feature: sampled input values are currently never read
    // out.delta = delta;
    // out.mx = Gdx.input.getX();
    // out.my = Gdx.input.getY();
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 3789-3792

`java
      // Areas-only: player can never step into VOID.
// Nicht fertiges Feature:       inVoid = false;
// Nicht fertiges Feature:       voidTimer = 0f;
// Nicht fertiges Feature:       voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 3804-3804

`java
// Nicht fertiges Feature:       inVoid = false;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 3805-3806

`java
// Nicht fertiges Feature:       voidTimer = 0f;
// Nicht fertiges Feature:       voidStage = 0;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4358-4358

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4925-4925

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6368-6368

`java
// Nicht fertiges Feature:   sellBuffer = String.valueOf(sellAmount);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6376-6376

`java
// Nicht fertiges Feature:   sellBuffer = String.valueOf(v);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:40
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6385-6385

`java
// Nicht fertiges Feature:   sellBuffer = "";
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6596-6596

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7310-7310

`java
// Nicht fertiges Feature:     hotbarLayoutValid = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7331-7331

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7347-7347

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7374-7374

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7554-7554

`java
// Nicht fertiges Feature:           worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7805-7805

`java
// Nicht fertiges Feature:                     worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7960-7960

`java
// Nicht fertiges Feature:       worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7987-7987

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7998-7998

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8044-8044

`java
// Nicht fertiges Feature:         worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8235-8235

`java
// Nicht fertiges Feature:         worldMapDirty = true;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8276-8276

`java
  // Nicht fertiges Feature: commented out unused method
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8539-8539

`java
    // Nicht fertiges Feature: sellBuffer = "";
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 8784-8784

`java
// Nicht fertiges Feature:       hackSfxT = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9577-9577

`java
  // Nicht fertiges Feature: unused placeholder type (no current gameflow usage)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9585-9588

`java
    // Nicht fertiges Feature: currently never read
    // float delta;
    // int mx;
    // int my;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9587-9587

`java
  // Nicht fertiges Feature: unused placeholder type (no current gameflow usage)
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:20:41
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9596-9596

`java
  // Nicht fertiges Feature: commented out unused block
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 269-275

`java
  /*
  private static long fnv1aStep(long h, long v) {
    // 64-bit FNV-1a
    h ^= v;
    return h * 0x100000001b3L;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 886-918

`java
  /*
  private void preloadChunksBlocking(int radiusChunks, int timeoutMs) {
    if (world == null) return;

    int r = Math.max(0, radiusChunks);
    long t0 = System.currentTimeMillis();

    world.requestAroundWorld(px, py, r);

    for (;;) {
      // Commit everything that's ready right now.
      world.tickStreaming(10_000);

      // Check if all chunks in the preload square are available.
      int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);
      int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);
      boolean ok = true;
      for (int dy = -r; dy <= r && ok; dy++) {
        for (int dx = -r; dx <= r; dx++) {
          if (world.peekChunk(ccx + dx, ccy + dy) == null) { ok = false; break; }
        }
      }
      if (ok) return;

      if (timeoutMs > 0 && (System.currentTimeMillis() - t0) > timeoutMs) return;

      try { Thread.sleep(2); } catch (InterruptedException ignored) {}

      // Keep requesting (in case something got evicted/never queued).
      world.requestAroundWorld(px, py, r);
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4318-4331

`java
  /*
  private boolean isNearBuild(EntityType t, float range) {
    float eff = range + EntityMetrics.radius(t);
    float r2 = eff * eff;
    for (int i=0;i<Entities.MAX;i++) {
      if (!entities.alive[i]) continue;
      if (entities.type[i] != t) continue;
      float dx = entities.x[i] - px;
      float dy = entities.y[i] - py;
      if (dx*dx + dy*dy <= r2) return true;
    }
    return false;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4871-4896

`java
  /*
  private void shopBuyIndex(int idx, int amount) {
    if (idx < 0 || idx >= shopOfferCount) return;
    if (amount <= 0) return;
    int itemId = shopItemId[idx];
    int priceEach = shopBuy[idx];

    // Barter/Negotiation: better prices per level (5% each level)
    int barterLv = (SK_BARTER >= 0 && SK_BARTER < progress.skillLv.length) ? progress.skillLv[SK_BARTER] : 1;
    int negoLv = (SK_NEGOTIATION >= 0 && SK_NEGOTIATION < progress.skillLv.length) ? progress.skillLv[SK_NEGOTIATION] : 1;
    float buyMul = 1f;
    buyMul *= Math.max(0.20f, 1f - 0.05f * Math.max(0, barterLv - 1));
    buyMul *= Math.max(0.35f, 1f - 0.05f * Math.max(0, negoLv - 1));
    priceEach = Math.max(1, (int) Math.floor(priceEach * buyMul));

    long total = (long) priceEach * amount;
    if (total <= 0) return;

    if (wallet.spendCopper(total)) {
      inv.add(itemId, amount);
      game.audio.sfx("audio/sfx/craft.wav", game.audio.sfxVolume(game.settings));
    } else {
      game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 6516-6535

`java
  /*
  private void drawCraftLines(float x, float y) {
    // legacy text renderer (still used inside the craft panel)
    int[] outs = {40, 14, 15};
    for (int i=0;i<outs.length;i++) {
      RecipeDef r = craft.findByOutput(data.recipes, outs[i]);
      if (r == null) continue;
      String name = (data.items[r.outItemId] != null) ? data.items[r.outItemId].name : ("item_" + r.outItemId);
      StringBuilder req = new StringBuilder();
      for (int k=0;k<r.inItemId.length;k++) {
        int id = r.inItemId[k];
        String inName = (data.items[id] != null) ? data.items[id].name : ("item_"+id);
        if (k>0) req.append(", ");
        req.append(inName).append("x").append(r.inAmount[k]);
      }
      boolean ok = craft.canCraft(inv, r);
      font.draw(batch, (i+1) + ") " + name + (ok?" [OK] ":" [NO] ") + "<= " + req, x, y - i*22f);
    }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7231-7245

`java
  /*
  private void drawInventoryLines(float x, float y) {
    // legacy (unused) - kept for quick debug
    int shown = 0;
    for (int id=0; id<inv.countsById.length; id++) {
      int c = inv.countsById[id];
      if (c <= 0) continue;
      String name = (data.items[id] != null) ? data.items[id].name : ("item_"+id);
      font.draw(batch, name + ": " + c, x, y - shown*18);
      shown++;
      if (shown >= 12) break;
    }
    if (shown == 0) font.draw(batch, "(empty)", x, y);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7232-7241

`java
  /*
  private void drawBuildLines(float x, float y) {
    String[] names = {"Workbench","Bed","Campfire","Lamp"};
    for (int i=0;i<names.length;i++) {
      String sel = (i == buildSel) ? ">" : " ";
      font.draw(batch, sel + (i+1) + ") " + names[i] + " rot=" + (int)buildRot, x, y - i*18);
    }
    font.draw(batch, "Placement rule: not on water.", x, y - names.length*18 - 10);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7249-7262

`java
  /*
  private void drawChestLines(float x, float y) {
    if (openChestE < 0) return;
    int idx = entities.data0[openChestE];
    Inventory chest = chestStore.get(idx);
    if (chest == null) {
      font.draw(batch, "(broken chest)", x, y);
      return;
    }

    font.draw(batch, "Player wood=" + inv.countsById[0] + " stone=" + inv.countsById[1] + " coins=" + inv.countsById[31], x, y);
    font.draw(batch, "Chest  wood=" + chest.countsById[0] + " stone=" + chest.countsById[1] + " coins=" + chest.countsById[31], x, y - 18);
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7848-7857

`java
  /*
  private static boolean fogBitGet(byte[] fog, int idx) {
    if (fog == null) return false;
    int bi = idx >> 3;
    int bit = idx & 7;
    if (bi < 0 || bi >= fog.length) return false;
    int mask = 1 << bit;
    return (fog[bi] & mask) != 0;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 7849-7860

`java
  /*
  private static boolean fogBitSet(byte[] fog, int idx) {
    if (fog == null) return false;
    int bi = idx >> 3;
    int bit = idx & 7;
    if (bi < 0 || bi >= fog.length) return false;
    byte mask = (byte) (1 << bit);
    if ((fog[bi] & mask) != 0) return false;
    fog[bi] |= mask;
    return true;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9090-9398

`java
        font.draw(batch, "(keine Presets gefunden in: pricing_presets/*.json)", x, popupY);
      } else {
        for (int i = 0; i < pricingPresetNames.length; i++) {
          font.draw(batch, (i + 1) + ") " + pricingPresetNames[i], x, popupY - i * 18f);
        }
      }
    }
  }

  private void preloadSkillIcons() {
    if (skillIconsLoaded) return;

    for (int i = 0; i < SkillDefs.COUNT; i++) {
      String path = SkillDefs.ICON_PATH[i];
      if (path == null || path.isEmpty()) continue;

      try {
        FileHandle fh = Gdx.files.internal(path);
        if (fh == null || !fh.exists()) continue;

        Texture t = new Texture(fh);
        // crisp pixel art
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        skillIconTex.put(i, t);
      } catch (Throwable ignored) {
        // keep menu usable even if one icon fails
      }
    }

    skillIconsLoaded = true;
  }

  private Texture getSkillIconTex(int idx) {
    if (idx < 0 || idx >= SkillDefs.COUNT) return null;
    return skillIconTex.get(idx);
  }

  private static String keyCatSub(String catKey, String subLabel) {
    return catKey + "::" + subLabel;
  }

  private void addSkillMenuSub(String catKey, String sub, String... skillIds) {
    String subKey = keyCatSub(catKey, sub);
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER2, sub, -1, catKey, subKey));
    skillMenuSubOpen.putIfAbsent(subKey, true);

    for (String sid : skillIds) {
      int idx = SkillDefs.indexOf(sid);
      if (idx >= 0) skillMenuRows.add(new SkillMenuRow(SkillMenuRow.SKILL, SkillDefs.NAME_DE[idx], idx, catKey, subKey));
    }
  }

  private void buildSkillMenuRows() {
    if (skillMenuRowsBuilt) return;
    skillMenuRows.clear();

    // Matches docs/Vision_Skizze.png structure: 5 categories with subcategories.
    // Ressourcen
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Ressourcen", -1, "Ressourcen", null));
    skillMenuCatOpen.putIfAbsent("Ressourcen", true);
    addSkillMenuSub("Ressourcen", "Sammeln", "Mining", "Woodcutting", "Herbalism", "Hunting", "Fishing", "Foraging");
    addSkillMenuSub("Ressourcen", "Verarbeitung", "Smelting", "Tanning", "Alchemy", "Cooking");

    // Kampf
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Kampf", -1, "Kampf", null));
    skillMenuCatOpen.putIfAbsent("Kampf", true);
    addSkillMenuSub("Kampf", "Waffen", "CombatMelee", "CombatRanged", "WeaponCraft", "DualWield", "ShieldMastery");
    addSkillMenuSub("Kampf", "Verteidigung", "ArmorCraft", "Dodging", "Parrying", "Toughness");
    addSkillMenuSub("Kampf", "Taktik", "Stealth", "Tracking", "Looting");

    // Soziales
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Soziales", -1, "Soziales", null));
    skillMenuCatOpen.putIfAbsent("Soziales", true);
    addSkillMenuSub("Soziales", "Handel", "Trading", "Barter", "Intimidation", "Persuasion");
    addSkillMenuSub("Soziales", "Support", "Leadership", "Negotiation", "Reputation");

    // Person
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Person", -1, "Person", null));
    skillMenuCatOpen.putIfAbsent("Person", true);
    addSkillMenuSub("Person", "Vitals", "Health", "Stamina", "HungerControl", "SleepControl");
    addSkillMenuSub("Person", "Mobilität", "CarryCapacity", "Climbing", "Boating", "FlyingEfficiency");
    addSkillMenuSub("Person", "Survival", "ColdResistance", "HeatResistance");

    // Andere
    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Andere", -1, "Andere", null));
    skillMenuCatOpen.putIfAbsent("Andere", true);
    addSkillMenuSub("Andere", "Bauen", "Building");
    addSkillMenuSub("Andere", "Übernatürlich", "MagicAffinity", "SpiritSight", "Blessing");
    addSkillMenuSub("Andere", "Utility", "Crafting", "Engineering", "ToolDurability", "Navigation", "Scavenging");

    skillMenuRowsBuilt = true;
  }

  private void drawSkillMenu() {
    // Modal overlay. Simulation is paused elsewhere.
    float mx = Gdx.input.getX();
    float my = uiMouseYUp();

    // Close (ESC). Note: P toggling is handled in the main input section to avoid instant close on the open-press.
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      skillsOpen = false;
      return;
    }

    // Fit to screen (avoid negative x/y on smaller resolutions)
    float screenW = Gdx.graphics.getWidth();
    float screenH = Gdx.graphics.getHeight();
    float w = Math.min(1120f, screenW - 40f);
    float h = Math.min(720f, screenH - 40f);
    w = Math.max(520f, w);
    h = Math.max(360f, h);
    float x0 = (screenW - w) * 0.5f;
    float y0 = (screenH - h) * 0.5f;
    if (x0 < 20f) x0 = 20f;
    if (y0 < 20f) y0 = 20f;

    batch.setColor(1f, 1f, 1f, 1f);
    batch.draw(uiRegions.panelSlots, x0, y0, w, h);

    font.setColor(0f, 0f, 0f, 1f);
    font.getData().setScale(1.6f * UI_FONT_SCALE);
    font.draw(batch, "SKILLBAUM", x0 + 36f, y0 + h - 28f);

    font.getData().setScale(1.0f * UI_FONT_SCALE);
    font.draw(batch, "Abenteurer Lvl: " + progress.level + "   XP: " + progress.xp + "/" + progress.xpToNext + "   Skillpunkte: " + progress.skillPoints, x0 + 36f, y0 + h - 70f);
    font.draw(batch, "ESC oder P = schließen | Klick auf Skill = +1", x0 + 36f, y0 + h - 104f);

    float listX = x0 + 36f;
    float listYTop = y0 + h - 150f;
    float listYBottom = y0 + 36f;

    // row metrics
    float rowSkillH = 42f * UI_FONT_SCALE;
    float rowHeader1H = 44f * UI_FONT_SCALE;
    float rowHeader2H = 38f * UI_FONT_SCALE;

    // Compute total content height (visible rows only) to clamp scroll.
    float totalH = 0f;
    for (int ri = 0; ri < skillMenuRows.size(); ri++) {
      SkillMenuRow r = skillMenuRows.get(ri);
      boolean visible = true;
      if (r.kind != SkillMenuRow.HEADER1) {
        visible = skillMenuCatOpen.getOrDefault(r.catKey, true);
        if (visible && r.kind == SkillMenuRow.SKILL) {
          visible = skillMenuSubOpen.getOrDefault(r.subKey, true);
        }
        if (visible && r.kind == SkillMenuRow.HEADER2) {
          visible = true; // sub headers are visible when category is open
        }
      }
      if (!visible) continue;
      float rh = (r.kind == SkillMenuRow.SKILL) ? (42f * UI_FONT_SCALE) : (r.kind == SkillMenuRow.HEADER1 ? (44f * UI_FONT_SCALE) : (38f * UI_FONT_SCALE));
      totalH += rh;
    }
    float viewH = Math.max(1f, listYTop - listYBottom);
    float maxScroll = Math.max(0f, totalH - viewH);
    if (skillMenuScrollPx > maxScroll) skillMenuScrollPx = maxScroll;

    String catHoverKey = null;
    String subHoverKey = null;

    skillHover = -1;
    float y = listYTop + skillMenuScrollPx;
    for (int ri = 0; ri < skillMenuRows.size(); ri++) {
      SkillMenuRow r = skillMenuRows.get(ri);

      if (r.kind != SkillMenuRow.HEADER1) {
        boolean catOpen = skillMenuCatOpen.getOrDefault(r.catKey, true);
        if (!catOpen) continue;

        // skills are only visible if their subcategory is open
        if (r.kind == SkillMenuRow.SKILL) {
          boolean subOpen = skillMenuSubOpen.getOrDefault(r.subKey, true);
          if (!subOpen) continue;
        }
      }

      float rh = (r.kind == SkillMenuRow.SKILL) ? rowSkillH : (r.kind == SkillMenuRow.HEADER1 ? rowHeader1H : rowHeader2H);
      float ry = y;
      // skip rows above the visible window
      if (ry > listYTop + rh) {
        y -= rh;
        continue;
      }
      // stop when we are past the bottom
      if (ry < listYBottom) break;

      switch (r.kind) {
        case SkillMenuRow.HEADER1 -> {
          boolean hov = mx >= listX && mx <= listX + (w - 72f) && my >= ry - rh + 6f && my <= ry + 6f;
          if (hov) catHoverKey = r.catKey;

          boolean open = skillMenuCatOpen.getOrDefault(r.catKey, true);
          String twisty = open ? "[-]" : "[+]";

          // Category: distinct big button
          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, listX, ry - rh + 8f, w - 72f, rh - 8f);

          font.setColor(0f, 0f, 0f, 1f);
          font.getData().setScale(1.20f * UI_FONT_SCALE);
          font.draw(batch, twisty + "  " + r.label, listX + 16f, ry - 12f);
        }
        case SkillMenuRow.HEADER2 -> {
          // Subcategory indent (about "2 tiles"), keep right edge aligned by shrinking width.
          float subIndent = 64f;
          float subX = listX + 10f + subIndent;
          float subW = (w - 72f) - 20f - subIndent;

          boolean hov = mx >= subX && mx <= subX + subW && my >= ry - rh + 6f && my <= ry + 6f;
          if (hov) subHoverKey = r.subKey;

          boolean open = skillMenuSubOpen.getOrDefault(r.subKey, true);
          String twisty = open ? "[-]" : "[+]";

          // Subcategory: distinct medium row
          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, subX, ry - rh + 10f, subW, rh - 12f);

          font.setColor(0f, 0f, 0f, 1f);
          font.getData().setScale(1.05f * UI_FONT_SCALE);
          font.draw(batch, twisty + "  " + r.label, subX + 18f, ry - 12f);
        }
        default -> {
          int i = r.skillIndex;

        // Skill indent (relative to subcategory position), keep right edge aligned.
        float subIndentBase = 64f;
        float skillIndent = subIndentBase + 64f;
        float sx = listX + skillIndent;
        float sw = (w - 72f) - skillIndent;

        boolean hov = mx >= sx && mx <= sx + sw && my >= ry - rh + 6f && my <= ry + 6f;
        if (hov) skillHover = i;

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, sx, ry - rh + 8f, sw, rh - 8f);

        int lv = (i >= 0 && i < progress.skillLv.length) ? progress.skillLv[i] : 0;
        int cost = SkillEffects.upgradeCost(lv);
        String line = SkillDefs.NAME_DE[i] + "   [" + lv + "/50]" + "   Kosten: " + cost;

        float iconS = Math.min(32f * UI_FONT_SCALE, rh - 12f);
        Texture iconTex = getSkillIconTex(i);
        float textX = sx + 20f;
        if (iconTex != null) {
          float ix = sx + 10f;
          float iy = (ry - rh + 8f) + ((rh - 8f) - iconS) * 0.5f;

          // Keep aspect ratio (avoid squashing tall/wide sprites)
          float tw = Math.max(1f, iconTex.getWidth());
          float th = Math.max(1f, iconTex.getHeight());
          float s = Math.min(iconS / tw, iconS / th);
          float dw = tw * s;
          float dh = th * s;
          float dx = ix + (iconS - dw) * 0.5f;
          float dy = iy + (iconS - dh) * 0.5f;

          batch.setColor(1f, 1f, 1f, 1f);
          batch.draw(iconTex, dx, dy, dw, dh);
          textX = ix + iconS + 14f;
        }

        font.setColor(0f, 0f, 0f, 1f);
        font.getData().setScale(1.0f * UI_FONT_SCALE);
        font.draw(batch, line, textX, ry - 12f);
        }
      }

      y -= rh;
    }

    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
      // Toggle category dropdown
      if (catHoverKey != null) {
        boolean open = skillMenuCatOpen.getOrDefault(catHoverKey, true);
        skillMenuCatOpen.put(catHoverKey, !open);
        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));
        return;
      }

      // Toggle subcategory dropdown
      if (subHoverKey != null) {
        boolean open = skillMenuSubOpen.getOrDefault(subHoverKey, true);
        skillMenuSubOpen.put(subHoverKey, !open);
        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));
        return;
      }

      // Click skill
      if (skillHover >= 0) {
        int i = skillHover;
        if (i >= 0 && i < progress.skillLv.length) {
          int curLv = progress.skillLv[i];
          int cost = SkillEffects.upgradeCost(curLv);

          if (curLv < 50 && progress.skillPoints >= cost) {
             
          } else {
            game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));
          }
        } else {
          game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));
        }
      }
    }
  }

  /** Ensures cursor state is re-applied after leaving a modal screen (Pause/Options/etc). */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9107-9112

`java
  /*
  private static final class UiState {
    // Placeholder for future UI state consolidation.
    boolean reserved;
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9111-9117

`java
  /*
  private static final class CommandBuffer {
    // Placeholder for future command buffering.
    int size = 0;
    void clear() { size = 0; }
  }
  */
`\n

### Commented-out code block

- time: 2026-03-20 11:20:54
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 9113-9120

`java
  /*
  private static int anchorToStep(int tile, int step) {
    if (step <= 1) return tile;
    // Round to nearest multiple of step (works for negative coordinates too).
    int m = Math.round(tile / (float) step);
    return m * step;
  }
  */
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 4-4

`java
import com.yourgame.survival.data.Inventory;\nimport com.yourgame.survival.data.SkillDefs;\nimport com.yourgame.survival.data.SkillEffects;\nimport com.yourgame.survival.entity.Entities;\nimport com.yourgame.survival.entity.EntityMetrics;\nimport com.yourgame.survival.entity.EntityType;\nimport com.yourgame.survival.render.ChunkRenderer;\nimport com.yourgame.survival.render.EntityRegions;\nimport com.yourgame.survival.render.EntityRenderer;\nimport com.yourgame.survival.render.RenderPipeline;\nimport com.yourgame.survival.render.TilesetRegions;\nimport com.yourgame.survival.render.UiRegions;\nimport com.yourgame.survival.systems.AiSystem;\nimport com.yourgame.survival.systems.CombatSystem;\nimport com.yourgame.survival.systems.EncounterSpawner;\nimport com.yourgame.survival.systems.EntityCollisionSystem;\nimport com.yourgame.survival.systems.SystemScheduler;\nimport com.yourgame.survival.world.Biome;\nimport com.yourgame.survival.world.World;\nimport com.yourgame.survival.world.TileIds;\n// Nicht fertiges Feature: // import com.yourgame.survival.world.SpawnSettings; // (unused)\nimport com.yourgame.survival.world.WorldNodeSpawner;\nimport com.yourgame.survival.world.WorldNodes;\n\n// Nicht fertiges Feature: duplicate import (FileHandle is already imported above)\n// import com.badlogic.gdx.files.FileHandle;\nimport java.util.HashMap;\nimport java.util.ArrayList;\n\n/** Block-3 target: endless chunk stream, visible biomes, basic collision constraints. */\npublic final class GameScreen extends ScreenAdapter implements AreaHost, DebugTarget {
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 110-110

`java
private final SurvivalGame game;\n\n  private OrthographicCamera cam;\n  private OrthographicCamera uiCam;\n  private SpriteBatch batch;\n  private BitmapFont font;\n  private ShapeRenderer shape;\n\n  // UI text readability: 200% scale, black (except MainMenu/Pause which are separate screens)\n  private static final float UI_FONT_SCALE = UI_FONT_SCALE;\n\n  // Skill indices (cached to avoid repeated string lookups per-frame)\n  private static final int SK_MINING = SkillDefs.indexOf("Mining");\n  private static final int SK_WOODCUTTING = SkillDefs.indexOf("Woodcutting");\n  private static final int SK_HUNTING = SkillDefs.indexOf("Hunting");\n  // Nicht fertiges Feature: unused skills (kept for later re-enable)\n  // private static final int SK_FISHING = SkillDefs.indexOf("Fishing");\n  // private static final int SK_FORAGING = SkillDefs.indexOf("Foraging");\n  private static final int SK_LOOTING = SkillDefs.indexOf("Looting");\n  private static final int SK_COMBAT_MELEE = SkillDefs.indexOf("CombatMelee");\n  private static final int SK_COMBAT_RANGED = SkillDefs.indexOf("CombatRanged");\n  private static final int SK_HEALTH = SkillDefs.indexOf("Health");\n  private static final int SK_STAMINA = SkillDefs.indexOf("Stamina");\n  private static final int SK_SPRINTING = SkillDefs.indexOf("Sprinting");\n  private static final int SK_HUNGER_CTRL = SkillDefs.indexOf("HungerControl");\n  private static final int SK_SLEEP_CTRL = SkillDefs.indexOf("SleepControl");\n  private static final int SK_BARTER = SkillDefs.indexOf("Barter");\n  private static final int SK_NEGOTIATION = SkillDefs.indexOf("Negotiation");\n  private static final int SK_TRADING = SkillDefs.indexOf("Trading");\n  private static final int SK_STEALTH = SkillDefs.indexOf("Stealth");\n  private static final int SK_INTIMIDATION = SkillDefs.indexOf("Intimidation");\n  // Nicht fertiges Feature: unused skills (kept for later re-enable)\n  // private static final int SK_CLIMBING = SkillDefs.indexOf("Climbing");\n  // private static final int SK_BOATING = SkillDefs.indexOf("Boating");\n\n  // Movement speed skill: SkillDefs has no explicit "Speed"; we use Navigation as the movement-speed progression.\n  private static final int SK_SPEED = SkillDefs.indexOf("Navigation");\n  private static final int SK_COLD_RES = SkillDefs.indexOf("ColdResistance");\n  private static final int SK_HEAT_RES = SkillDefs.indexOf("HeatResistance");\n\n  // Mouse aim in world coords (may be clamped to the action ring)\n  private float mouseWorldX = 0f;\n  private float mouseWorldY = 0f;\n\n  // Cursor rendering: hide OS cursor inside action ring and draw custom cursor.\n  private boolean lastCursorHidden = false;\n  private boolean drawUnarmedDotCursor = false;\n\n  // Debug: show hovered IDs at cursor (toggle with F3)\n  boolean debugHoverIds = true;\n\n  // Debug: make road tiles blocking so we can detect invisible roadMask areas.\n  private boolean debugRoadBlocksMovement = false;\n\n  // ===== Ranged combat: Bow arrows (projectile) =====\n  private static final int ARROW_MAX = 64;\n  // Ammo item id (must match items.json)\n  private static final int ITEM_ARROW = 47;\n  private static final float ARROW_HIT_RADIUS = 6.0f; // projectile thickness for reliable hits (tight-ish)\n  private final boolean[] arrowAlive = new boolean[ARROW_MAX];\n  private final float[] arrowX = new float[ARROW_MAX];\n  private final float[] arrowY = new float[ARROW_MAX];\n  private final float[] arrowVx = new float[ARROW_MAX];\n  private final float[] arrowVy = new float[ARROW_MAX];\n  private final float[] arrowTravel = new float[ARROW_MAX];\n  private final float[] arrowRange = new float[ARROW_MAX];\n  private final float[] arrowDmg = new float[ARROW_MAX];\n  private float bowCooldownT = 0f;\n\n  // Harvest SFX (hold-to-hack)\n// Nicht fertiges Feature:   private float hackSfxT = 0f;\n\n  // RMB hold behavior: emulate repeated click (press/release) at fixed cadence.\n  // This lets harvesting/using tools look like discrete swings while still being hold-to-use.\n  private static final float RMB_PULSE_PERIOD = 0.5f;\n  private static final float RMB_HIT_WINDOW = 0.18f;\n  private boolean rmbHoldActive = false;\n  private float rmbPulseT = 0f;\n  private float rmbHitT = 0f;\n\n  // Sneak (hold V)\n  private boolean sneaking = false;\n  private boolean sneakingPrev = false;\n\n  private final SystemScheduler scheduler = new SystemScheduler();\n\n  // Block 7: render/input scratch + future state/command buffering (structural only)\n  private final RenderScratch scratch = new RenderScratch();\n  // UI: reuse a single GlyphLayout for wrapping/measuring without per-frame allocations.\n  private final com.badlogic.gdx.graphics.g2d.GlyphLayout uiLayout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();\n  // Scratch arrays for panel size calculations.\n  private final float[] scratchInvMax = new float[2];\n// Nicht fertiges Feature:   private final UiState uiState = new UiState();\n  private final InputState inputState = new InputState();\n// Nicht fertiges Feature:   private final CommandBuffer commands = new CommandBuffer();\n\n  // Block 8: deterministic command pipeline scaffolding\n  private final CommandQueue commandQueue = new CommandQueue(256);\n// Nicht fertiges Feature:   private SimContext simContext = null;\n\n  private final long worldSeed;\n  private World world;\n  private final WorldNodes worldNodes = new WorldNodes();\n  private WorldNodeSpawner nodeSpawner;\n\n  private TilesetRegions tiles;\n  private ChunkRenderer chunkRenderer;\n\n  // (obsolete netcode removed)\n\n  // Block 4 bootstrap\n  private final Entities entities = new Entities();\n  private int playerE = -1;\n  private final Inventory inv = new Inventory();\n  private final Wallet wallet = new Wallet();\n  private final PlayerProgress progress = new PlayerProgress();\n  private final SurvivalNeeds needs = new SurvivalNeeds();\n  // Ensure the player starts a new game at full HP (after hpMax is computed from skills).\n  private boolean forceFullHpOnce = true;\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private static long fnv1aStep(long h, long v) {\n    // 64-bit FNV-1a\n    h ^= v;\n    return h * 0x100000001b3L;\n  }\n  */\n\n  // (obsolete snapshot hashing removed)\n\n  // (obsolete replication code removed)\n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n\n  \n  private final DayNightSystem dayNight = new DayNightSystem();\n\n  // Day/Night visuals\n  private Texture dayNight1x1;\n  private Texture dnLightTex;      // radial gradient (debug lamp)\n  private FrameBuffer dnMaskFbo;\n  private TextureRegion dnMaskRegion;\n\n  // Fog of War overlay (playfield): low-res alpha texture scaled over the area\n  private Pixmap fowPm;\n  private Texture fowTex;\n  private float dnLampScreenX = 0f;\n  private float dnLampScreenY = 0f;\n  private boolean dnLampScreenValid = false;\n  private float dnVisual = 1f;     // current 0..1 (0=night,1=day)\n  private float dnWarm = 0f;       // current 0..1 warm twilight\n  private float dnTarget = 1f;\n  private float dnWarmTarget = 0f;\n  private int dnDebugMode = 0;     // 0=off, 1=force-day, 2=force-night\n  private boolean dnLightOn = false;   // F5 debug lamp\n  private boolean dnLightXLOn = false; // Shift+L extra-large debug lamp\n\n  // Day/Night BG music (absolute paths, sequential loop playlists)\n  private static final float DN_MIDPOINT = 0.5f;\n  private static final float DN_SWITCH_SEC = 2.0f;\n  private static final float TRACK_FADE_IN_SEC = 1.25f;\n\n  private boolean dnSideDay = true;\n  private boolean dnSwitching = false;\n  private float dnSwitchT = 0f;\n  private boolean dnSwitchToDay = true;\n\n  private float dayTrackFadeInT = TRACK_FADE_IN_SEC;\n  private float nightTrackFadeInT = TRACK_FADE_IN_SEC;\n\n  private final String[] dayBgTracks = {\n    "C:/Users/kuehn/Downloads/geovanebruny-16-acoustic-piano-life-deserves-16309.mp3",\n    "C:/Users/kuehn/Downloads/dmassaiii-dreaded-lullaby-255495.mp3",\n  };\n  private final String[] nightBgTracks = {\n    "C:/Users/kuehn/Downloads/dream-protocol-broken-dreams-but-wex27ll-remain-r1-212005.mp3",\n    "C:/Users/kuehn/Downloads/pixelmaniax-varnen-377757.mp3",\n  };\n  private int dayBgIdx = 0;\n  private int nightBgIdx = 0;\n  private Music dayBgMusic;\n  private Music nightBgMusic;\n  private float dayBgVol = 1f;\n  private float nightBgVol = 0f;\n\n  private final DataRegistry data = new DataRegistry();\n  private final CraftSystem craft = new CraftSystem();\n  private MerchantSystem merchants;\n  private WanderQuestGuySystem questGuy;\n\n  // ZQS runtime facade (concept flow 4).\n  private ZqsRuntime zqsRt;\n\n  // ZQS docking layer for WanderQuestGuy.\n  private WanderQuestGuyDock zqsWqgDock;\n\n  // ZQS SaveBlock (persisted inside slot JSON under key "zqs").\n  private final ZqsSaveBlock zqsSave = new ZqsSaveBlock();\n\n  private final com.badlogic.gdx.utils.IntIntMap zqsCraftedOutputCounts = new com.badlogic.gdx.utils.IntIntMap();\n\n  // Task 9: runtime-only quest log (persist later)\n  private final QuestLog questLog = new QuestLog();\n\n  // Central item pricing DB (COPPER based)\n  private final PriceBook priceBook = new PriceBook(60);\n\n  // Pricing editor (Shift+F12)\n  private boolean pricingOpen = false;\n  private boolean pricingPresetPopup = false;\n  private int pricingSel = 0;\n  private int pricingScroll = 0;\n  private final int[] pricingEditCopper = new int[60];\n  private String pricingEditBuffer = "";\n  private String[] pricingPresetNames = new String[0];\n\n  private final AiSystem ai = new AiSystem();\n  private final CombatSystem combat = new CombatSystem();\n  private final EntityCollisionSystem entityCollision = new EntityCollisionSystem();\n  private final EncounterSpawner encounterSpawner = new EncounterSpawner();\n\n  // WorldMap state (v3 save). This is the persistent data that goes into savegames.\n  private final WorldMapState worldMap = new WorldMapState();\n\n  // WorldMap runtime (rules engine). Kept separate from the state so we can change rules\n  // without breaking savegames.\n  //\n  // Decision:\n  // - We construct the alpha registry in code for now.\n  // - Later this will be loaded from assets (JSON etc.), but the template IDs must remain stable\n  //   because savegames store them.\n  private final WorldMapRuntime worldMapRt =\n      new WorldMapRuntime(\n          WorldMapRuntime.createAlphaRegistry());\n\n  // WorldMap travel glue (runtime state change + world loading).\n  private final WorldMapTravelController worldMapTravel =\n      new WorldMapTravelController(\n          worldMapRt,\n          new JsonAreaWorldLoader());\n\n  // ============================================================\n  // FUSA Story mode (AREAS ONLY)\n  // ============================================================\n  // Procedural mode has been removed: the game always runs in Areas-only mode.\n  // Guardrail: we must NOT stream/generate biomes/chunks in the background during play.\n  private static final boolean AREA_MODE = true;\n\n  // ============================================================\n  // Debug toggles (dev)\n  // ============================================================\n\n  // F10: verbose tile-tree streaming debug overlay via toast.\n  private boolean dbgTileTrees = false;\n  private float dbgTileTreesToastCooldown = 0f;\n\n  // Tile-tree harvest progress cache (tile-based; avoids using Entities slots).\n  private int tileTreeHitTx = -1;\n  private int tileTreeHitTy = -1;\n  private float tileTreeHitHp = 0f;\n  private float tileTreeHitUiT = 0f;\n\n  // Runtime seconds since this GameScreen instance started (used for simple cooldowns).\n  private float runtimeSec = 0f;\n\n  // Tile-tree policy:\n  // - false: trees never get cut/removed and never leave stumps (harvest yields drops only).\n  // - true: trees are cut persistently via areaTreeCutBits (stumps).\n  private static final boolean TILE_TREES_FELL_ON_HARVEST = true;\n\n  // Anti-exploit: cooldown before the same tile-tree can yield drops again.\n  private static final float TILE_TREE_HARVEST_COOLDOWN_SEC = 35f;\n  private final com.badlogic.gdx.utils.IntFloatMap tileTreeNextHarvestAtSec = new com.badlogic.gdx.utils.IntFloatMap();\n\n  // One-shot cleanup: if older builds spawned NODE_TREE/NODE_STUMP entities, kill them once.\n  private boolean tileTreeEntitiesPurged = false;\n\n  // Area bounds state (computed from player position)\n  private boolean inRedZone = false;\n// Nicht fertiges Feature:   private boolean inVoid = false;\n// Nicht fertiges Feature:   private float voidTimer = 0f;\n  // Void penalty stage:\n  // 0 = not penalized yet\n  // 1 = 50% HP penalty applied, next expiry kills player\n// Nicht fertiges Feature:   private int voidStage = 0;\n\n  // WorldMap UI (Shift+M)\n  private boolean mapOpen = false;\n  private float mapPanX = 0f;\n  private float mapPanY = 0f;\n  private boolean mapDragging = false;\n  private float mapDragLastX = 0f;\n  private float mapDragLastY = 0f;\n  private int mapSelectedAx = 0;\n  private int mapSelectedAy = 0;\n// Nicht fertiges Feature:   private boolean mapZoomOpen = false;\n\n  // Map view mode: 0 = world overview, 1 = area map (live or war-stand).\n  private int mapViewMode = 0;\n\n  // Simple transition (zoom + crossfade) between world overview and area map.\n  private boolean mapTransActive = false;\n  private float mapTransT = 0f;   // 0..1\n  private int mapTransFrom = 0;\n  private int mapTransTo = 0;\n\n  // Snapshot invalidation: update the map UI only when something changed.\n// Nicht fertiges Feature:   private boolean worldMapDirty = true;\n\n  // Fog of War (persistent explored + dynamic visible)\n  private float fowAccT = 0f;\n\n  private final HarvestSystem harvest = new HarvestSystem();\n  private final BuildingSystem building = new BuildingSystem();\n\n  // UI state\n  private boolean shopOpen = false;\n  private boolean craftOpen = false;\n\n  // Food effects (smooth over time): fill hunger first, then HP.\n  private float pendingFoodHunger = 0f;\n  private float pendingFoodHp = 0f;\n  private boolean invOpen = false;\n  // Inventory panel placement: when opened while other panels are present, snap to next free spot.\n  private boolean invJustOpened = false;\n  private boolean walletOpen = false;\n  private int openMerchantE = -1;\n\n  // ---------------- WanderQuestGuy dialog (WQG) ----------------\n\n  /** True while the WQG dialog is open (modal; freezes world + releases mouse). */\n  private boolean questPopupOpen = false;\n\n  /** Entity index of the currently open quest guy dialog. */\n  private int openQuestGuyE = -1;\n\n  /** WQG dialog scroll position (pixels; 0 = top). */\n  private float wqgScrollPx = 0f;\n\n  /** Cached composed dialog text (greeting + offers + etc.). */\n  private String wqgDialogText = "";\n\n  /** Cached wrapped lines for the dialog text (computed when dialog text changes). */\n  private final java.util.ArrayList<String> wqgWrapped = new java.util.ArrayList<>();\n\n  /** When >0, the dialog will close after this epoch second (auto-close only in specific states). */\n  private long wqgAutoCloseAtEpochSec = 0L;\n\n  /** If non-empty, the farewell is appended to the dialog text and auto-close may start. */\n  private String wqgFarewellText = "";\n  // Questlogbook UI (Shift+Q): hub + two sub-windows (open/done).\n  private boolean questLogHubOpen = false;\n  private boolean questLogOpenQuestsOpen = false;\n  private boolean questLogDoneQuestsOpen = false;\n  private float questLogOpenScrollPx = 0f;\n  private float questLogDoneScrollPx = 0f;\n\n  // Questlog window positions (drag + persistent in save).\n  private float questHubAnchorX = Float.NaN, questHubAnchorY = Float.NaN;\n  private float questOpenAnchorX = Float.NaN, questOpenAnchorY = Float.NaN;\n  private float questDoneAnchorX = Float.NaN, questDoneAnchorY = Float.NaN;\n  private boolean questHubDrag = false, questOpenDrag = false, questDoneDrag = false;\n  private float questHubDragDx = 0f, questHubDragDy = 0f;\n  private float questOpenDragDx = 0f, questOpenDragDy = 0f;\n  private float questDoneDragDx = 0f, questDoneDragDy = 0f;\n\n  // Questlog side panel bounds (for wheel scrolling hit tests). Updated each frame while panels are drawn.\n  private float questLogOpenPanelX = 0f, questLogOpenPanelY = 0f, questLogOpenPanelW = 0f, questLogOpenPanelH = 0f;
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 115-115

`java
\n  // Boot/preload overlay (covers the world at start, then fades out; blocks simulation until done)\n  private static final float BOOT_OVERLAY_FILL_SEC = 8.0f;\n  private static final float BOOT_OVERLAY_FADE_SEC = 1.0f;\n  private boolean bootOverlayActive = true;\n  private boolean bootOverlayControl = false;\n  private float bootOverlayT = 0f;\n  private float bootOverlayFadeT = 0f;\n  private float bootOverlayFill = 0f;\n  private float bootOverlayA = 1f;\n\n  // Inventory panel drag (screen-space)\n  private boolean invDrag = false;\n  private float invDragDx = 0f;\n  private float invDragDy = 0f;\n  private float invAnchorX = Float.NaN;\n  private float invAnchorY = Float.NaN;\n\n  // Popup panels (build/craft/chest) drag + anchors (screen-space)\n  private boolean buildDrag = false;\n  private float buildDragDx = 0f;\n  private float buildDragDy = 0f;\n  private float buildAnchorX = Float.NaN;\n  private float buildAnchorY = Float.NaN;\n\n  private boolean craftDrag = false;\n  private float craftDragDx = 0f;\n  private float craftDragDy = 0f;\n  private float craftAnchorX = Float.NaN;\n  private float craftAnchorY = Float.NaN;\n\n  private boolean chestDrag = false;\n  private float chestDragDx = 0f;\n  private float chestDragDy = 0f;\n  private float chestAnchorX = Float.NaN;\n  private float chestAnchorY = Float.NaN;\n\n  // Block 9 economy: runtime shop offers (fixed + daily wandering)\n  private final int[] shopItemId = new int[12];\n  private final int[] shopBuy = new int[12];\n  private final int[] shopSell = new int[12];\n  private int shopOfferCount = 0;\n  private float prevDayT = 0f;\n  private int dayIndex = 0;\n\n  // --- UI layout caches (for drag & drop hit tests) ---\n  // Build panel\n  private boolean buildLayoutValid = false;\n  private float buildPanelX0, buildPanelY0, buildPanelW, buildPanelH;\n  private float buildSlotsX0, buildSlotsY0, buildSlotPx, buildPadPx;\n  private int buildSlotsCount = 0;\n  private float shopSlotsX0, shopSlotsY0, shopSlotPx, shopPadPx;\n  private final int shopCols = 6;\n  private final int shopRows = 2;\n  private float shopPanelX0, shopPanelY0, shopPanelW, shopPanelH;\n  private boolean shopLayoutValid = false;\n\n  private float invNormSlotsX0, invNormSlotsY0;\n  private float invToolSlotsX0, invToolSlotsY0;\n  private float invSlotPx, invPadPx;\n  private int invNormRowsDrawn = 0;\n  private int invToolRowsDrawn = 0;\n\n  private float hotbarX0, hotbarY0, hotbarSlotPx, hotbarPadPx;\n// Nicht fertiges Feature:   private boolean hotbarLayoutValid = false;\n\n  // --- Drag & popup state ---\n  private static final int DRAG_SRC_NONE = 0;\n  private static final int DRAG_SRC_SHOP = 1;\n  private static final int DRAG_SRC_TOOLINV = 2;\n  private static final int DRAG_SRC_HOTBAR = 3;\n  private static final int DRAG_SRC_NORMINV = 4;\n  private static final int DRAG_SRC_BUILD = 5;\n\n  private boolean dragArmed = false;\n  private boolean dragActive = false;\n  private int dragSrc = DRAG_SRC_NONE;\n  private int dragItemId = -1;\n  private int dragIndex = -1; // shop offer idx OR tool slot idx\n  private float dragStartX, dragStartY;\n\n  private boolean buyPopup = false;\n  private int buyOfferIdx = -1;\n  private int buyItemId = -1;\n  private int buyPreferredSlot = -1;\n  private boolean buyPreferredToolArea = false;\n  private int buyAmount = 1;\n  private int buyMax = 1;\n  private String buyBuffer = "";\n  private int buyPriceEach = 0;\n\n\n  private boolean sellPopup = false;\n  private int sellItemId = -1;\n  private int sellAmount = 1;\n  private int sellMax = 1;\n// Nicht fertiges Feature:   private String sellBuffer = "";\n  private int sellPriceEach = 0;\n  private int sellPriceEachRaw = 0;\n\n  // Craft UI (panel): qty input per recipe + hover requirements\n  private int craftHoverIdx = -1;\n  private int craftQtyFocusIdx = -1;\n  private String craftQtyBuffer = "1";\n  private int[] craftQtyByRecipe = new int[0];\n  private float craftScroll = 0f;\n\n  private boolean prevLmbDown = false;\n\n  // Skill menu (P)\n  private boolean skillsOpen = false;\n  private int skillHover = -1;\n\n  // resolved equipped tool each frame (tools must exist in inventory + be in hotbar)\n  // Nicht fertiges Feature: unused (logic uses equippedFromHotbar() + method param instead)\n  // private int equippedItemId = -1;\n\n  private final int[] hotbar = new int[8];\n  private int hotbarSel = 0;\n\n  // Building\n  private boolean buildMode = false;\n  private float buildRot = 0f;\n  private int buildSel = 0; // 0..4\n  private final ChestStore chestStore = new ChestStore();\n  private int openChestE = -1;\n\n  private RenderPipeline renderPipeline;\n  private EntityRegions entityRegions;\n  private EntityRenderer entityRenderer;\n  private UiRegions uiRegions;\n\n  // Wallet coin icons (Moneda*)\n  private Texture walletCoinCopperTex;\n  private Texture walletCoinSilverTex;\n  private Texture walletCoinGoldTex;\n  private TextureRegion walletCoinCopperIcon;\n  private TextureRegion walletCoinSilverIcon;\n  private TextureRegion walletCoinGoldIcon;\n\n  // Skill menu icons (loaded once; avoid loading inside render)\n  private final HashMap<Integer, Texture> skillIconTex = new HashMap<>();\n  private boolean skillIconsLoaded = false;\n\n  private static final class SkillMenuRow {\n    static final int HEADER1 = 1;\n    static final int HEADER2 = 2;\n    static final int SKILL = 3;\n\n    final int kind;\n    final String label;\n    final int skillIndex; // only for SKILL\n    final String catKey;  // for visibility / toggling (category)\n    final String subKey;  // for visibility / toggling (subcategory)\n\n    SkillMenuRow(int kind, String label, int skillIndex, String catKey, String subKey) {\n      this.kind = kind;\n      this.label = label;\n      this.skillIndex = skillIndex;\n      this.catKey = catKey;\n      this.subKey = subKey;\n    }\n  }\n\n  private final ArrayList<SkillMenuRow> skillMenuRows = new ArrayList<>(128);\n  private boolean skillMenuRowsBuilt = false;\n  private final HashMap<String, Boolean> skillMenuCatOpen = new HashMap<>();\n  private final HashMap<String, Boolean> skillMenuSubOpen = new HashMap<>();\n\n  // Block 11 save/load toast\n  private String toast = "";\n  private float toastT = 0f;\n\n  // Minimal player state\n  private float px = 0f;\n  private float py = 0f;\n\n  // Player movement smoothing (accelerate/brake; avoids abrupt stop)\n  private float playerVx = 0f;\n  private float playerVy = 0f;\n\n  // Player facing smoothing: angle (radians) that we steer toward the mouse, clamped to the front hemisphere.\n  private float playerAimA = 0f;\n// Nicht fertiges Feature:   private boolean playerAimInit = false;\n\n  // Progression constraints (Block 3)\n  private boolean hasBoat = false;\n  private boolean hasClimb = false;\n\n  private final int streamRadiusChunks = 1;\n\n  // Startup warmup: keep streaming radius at 0 for a short time to avoid a generation burst on "New Game".\n  // This reduces immediate chunk generation from (2r+1)^2 to 1, then ramps to the configured radius.\n  private int streamWarmupFrames = 45;\n\n  // Loaded chunk rect (computed once per tick)\n  private int loadedMinCx, loadedMaxCx, loadedMinCy, loadedMaxCy;\n\n  // Base reaches (world units). Effective reach is base + EntityMetrics.radius(target).\n  private static final float REACH_HARVEST = REACH_HARVEST;\n  private static final float REACH_COMBAT = REACH_COMBAT;\n  private static final float REACH_PICKUP = REACH_PICKUP;\n// Nicht fertiges Feature:   private static final float REACH_BUILD_REMOVE = REACH_BUILD_REMOVE;\n// Nicht fertiges Feature:   private static final float REACH_SHOP = REACH_SHOP;\n// Nicht fertiges Feature:   private static final float REACH_BED = REACH_BED;\n\n  // Action cone: must stay in front of the player (max +/-60° around facing => 120° total).\n  private static final float ACTION_FOV_DEG = ACTION_FOV_DEG;\n\n  // Purely visual: hand/tool swing animation duration (seconds).\n  private static final float HAND_SWING_DUR = HAND_SWING_DUR;\n\n  // Default action ring radius (world units). Individual items can override this later for balancing.\n  private static final float ACTION_RADIUS_DEFAULT = ACTION_RADIUS_DEFAULT;\n\n  // BLOCK B: camera zoom (performance: less visible tiles)\n  private static final float ZOOM_DEFAULT = ZOOM_DEFAULT; // current standard\n  private static final float ZOOM_MIN = ZOOM_MIN;\n  // Max zoom-out is significantly closer than before (was 0.70).\n  private static final float ZOOM_MAX = ZOOM_MAX;\n  private static final float ZOOM_STEP_WHEEL = ZOOM_STEP_WHEEL;\n  private static final float ZOOM_STEP_KEYS = ZOOM_STEP_KEYS;\n\n  private int pendingScrollY = 0;\n\n  // Skill menu scrolling\n  private int pendingSkillScrollY = 0;\n  private float skillMenuScrollPx = 0f;\n\n  // NOTE: LibGDX calls show() again when we resume from PauseScreen via setScreen(resumeTo).\n  // Guard against re-initializing and re-spawning entities on resume.\n  private boolean shownOnce = false;\n  private InputAdapter inputAdapter;\n\n  // Modularization controllers (Stable 0.003)  // NOTE: Thin wrapper controllers were removed (Phase 5.0). Keep direct calls.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 115-115

`java
  private GameInputController inputController;\n\n  public GameScreen(SurvivalGame game) {\n    this(game, nextUniqueSeed24Long());\n  }\n\n  \n\n  public GameScreen(SurvivalGame game, long worldSeed) {\n    this.game = game;\n    this.worldSeed = worldSeed;\n    resetWorld(worldSeed);\n\n    // New game start time: fixed to day so the player doesn't spawn into immediate darkness.\n    // (t=0 is midnight in daylightPhased())\n    dayNight.t = 0.25f;\n    dnVisual = 1f;\n    dnWarm = 0f;\n\n    this.merchants = new MerchantSystem(worldSeed);\n    this.questGuy = new WanderQuestGuySystem(worldSeed);\n\n    // ZQS exists independently; WanderQuestGuy docks into it (dock layer).\n    this.zqsRt = new ZqsRuntime(worldSeed);\n    this.zqsWqgDock = new WanderQuestGuyDock(zqsRt);\n    this.questGuy.bindZqsDock(zqsWqgDock);\n\n    this.questGuy.bindZqsContextProvider(() -> {\n      var c = new ZqsConversationContext();\n      // ZQS runtimeSec must be stable across save/load; use real epoch seconds.\n      long nowSec = System.currentTimeMillis() / 1000L;\n      c.epochSec = nowSec;\n      c.runtimeSec = nowSec;\n      c.debugHqMode = "EXCLUDE_HQ";\n      // time_of_day\n      float t = dayNight.t;\n      if (t < 0.23f) c.timeOfDay = "morning";\n      else if (t < 0.55f) c.timeOfDay = "day";\n      else if (t < 0.78f) c.timeOfDay = "evening";\n      else c.timeOfDay = "night";\n\n      // quest counts\n      c.openQuestsCount = (questLog != null) ? questLog.size() : 0;\n      c.completedQuestsCount = 0;\n      if (questLog != null) {\n        for (int i = 0; i < questLog.entries.size; i++) {\n          var e = questLog.entries.get(i);\n          if (e != null && e.status == COMPLETED) c.completedQuestsCount++;\n        }\n      }\n\n      // worldstress_zone (Plan thresholds)\n      int oq = c.openQuestsCount;\n      if (oq <= 2) c.worldstressZone = "ruhig";\n      else if (oq <= 5) c.worldstressZone = "belebt";\n      else c.worldstressZone = "hektisch";\n\n      return c;\n    });\n\n    // Ensure defaults for new sessions.\n    zqsSave.setDefaults();\n  }\n\n  private static String lastSeed24 = null;\n\n  // 24 hex chars (12 random bytes) -> hashed into a long for worldgen.\n  private static long nextUniqueSeed24Long() {\n    java.security.SecureRandom r = new java.security.SecureRandom();\n    for (int tries = 0; tries < 10; tries++) {\n      byte[] b = new byte[12];\n      r.nextBytes(b);\n      String s = toHex24(b);\n      if (lastSeed24 == null || !lastSeed24.equals(s)) {\n        lastSeed24 = s;\n        return seedLongFrom24(s);\n      }\n    }\n    // fallback: time-based (still unique)\n    String s = Long.toHexString(System.nanoTime()) + Long.toHexString(System.currentTimeMillis());\n    if (s.length() < 24) s = ("000000000000000000000000" + s).substring(s.length());\n    if (s.length() > 24) s = s.substring(0, 24);\n    lastSeed24 = s;\n    return seedLongFrom24(s);\n  }\n\n  private static String toHex24(byte[] b12) {\n    char[] hex = "0123456789abcdef".toCharArray();\n    char[] out = new char[24];\n    for (int i = 0; i < 12; i++) {\n      int v = b12[i] & 0xFF;\n      out[i * 2] = hex[v >>> 4];\n      out[i * 2 + 1] = hex[v & 15];\n    }\n    return new String(out);\n  }\n\n  private static long seedLongFrom24(String s24) {\n    // Rule from user: seed should be 24 chars; we keep it at 24.\n    try {\n      java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");\n      byte[] h = md.digest(s24.getBytes(java.nio.charset.StandardCharsets.UTF_8));\n      long v = 0L;\n      for (int i = 0; i < 8; i++) v = (v << 8) | (h[i] & 0xFFL);\n      return v;\n    } catch (java.security.NoSuchAlgorithmException | RuntimeException e) {\n      return s24.hashCode();\n    }\n  }\n\n  private void resetWorld(long seed) {\n    world = new World(seed);\n    nodeSpawner = new WorldNodeSpawner(world, seed);\n    nodeSpawner.resetSpawnedChunks();\n\n    // Block 8: refresh sim context references\n// Nicht fertiges Feature:     simContext = new SimContext(world, entities, inv, wallet, progress, needs, priceBook);\n  }\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private void preloadChunksBlocking(int radiusChunks, int timeoutMs) {\n    if (world == null) return;\n\n    int r = Math.max(0, radiusChunks);\n    long t0 = System.currentTimeMillis();\n\n    world.requestAroundWorld(px, py, r);\n\n    for (;;) {\n      // Commit everything that's ready right now.\n      world.tickStreaming(10_000);\n\n      // Check if all chunks in the preload square are available.\n      int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);\n      int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);\n      boolean ok = true;\n      for (int dy = -r; dy <= r && ok; dy++) {\n        for (int dx = -r; dx <= r; dx++) {\n          if (world.peekChunk(ccx + dx, ccy + dy) == null) { ok = false; break; }\n        }\n      }\n      if (ok) return;\n\n      if (timeoutMs > 0 && (System.currentTimeMillis() - t0) > timeoutMs) return;\n\n      try { Thread.sleep(2); } catch (InterruptedException ignored) {}\n\n      // Keep requesting (in case something got evicted/never queued).\n      world.requestAroundWorld(px, py, r);\n    }\n  }\n  */\n\n  private void sanitizeSpawnForWorld() {\n    // Only adjust if the tile is not walkable.\n    // IMPORTANT: must NOT generate chunks (use peek queries).\n    try {\n      boolean w0 = world.isWaterAtWorldPeek(px, py, true);\n      boolean b0 = world.isBlockedAtWorldPeek(px, py, true);\n      int id0 = world.biomeIdAtWorldPeek(px, py, Biome.GRASSLAND.id & 0xff);\n      if (!w0 && !b0 && Biome.byId(id0) != Biome.LAVA) return;\n\n      final float step = World.TILE_WORLD; // tile-sized steps\n      final int maxR = 64; // search radius in tiles\n\n      // Perimeter scan for first valid tile.\n      for (int r = 1; r <= maxR; r++) {\n        // top/bottom edges\n        for (int dx = -r; dx <= r; dx++) {\n          float x1 = px + dx * step;\n          float yTop = py + r * step;\n          boolean wTop = world.isWaterAtWorldPeek(x1, yTop, true);\n          boolean bTop = world.isBlockedAtWorldPeek(x1, yTop, true);\n          int idTop = world.biomeIdAtWorldPeek(x1, yTop, Biome.GRASSLAND.id & 0xff);\n          if (!wTop && !bTop && Biome.byId(idTop) != Biome.LAVA) { px = x1; py = yTop; return; }\n\n          float yBot = py - r * step;\n          boolean wBot = world.isWaterAtWorldPeek(x1, yBot, true);\n          boolean bBot = world.isBlockedAtWorldPeek(x1, yBot, true);\n          int idBot = world.biomeIdAtWorldPeek(x1, yBot, Biome.GRASSLAND.id & 0xff);\n          if (!wBot && !bBot && Biome.byId(idBot) != Biome.LAVA) { px = x1; py = yBot; return; }\n        }\n        // left/right edges (skip corners already checked)\n        for (int dy = -r + 1; dy <= r - 1; dy++) {\n          float y1 = py + dy * step;\n          float xRight = px + r * step;\n          boolean wR = world.isWaterAtWorldPeek(xRight, y1, true);\n          boolean bR = world.isBlockedAtWorldPeek(xRight, y1, true);\n          int idR = world.biomeIdAtWorldPeek(xRight, y1, Biome.GRASSLAND.id & 0xff);\n          if (!wR && !bR && Biome.byId(idR) != Biome.LAVA) { px = xRight; py = y1; return; }\n\n          float xLeft = px - r * step;\n          boolean wL = world.isWaterAtWorldPeek(xLeft, y1, true);\n          boolean bL = world.isBlockedAtWorldPeek(xLeft, y1, true);\n          int idL = world.biomeIdAtWorldPeek(xLeft, y1, Biome.GRASSLAND.id & 0xff);\n          if (!wL && !bL && Biome.byId(idL) != Biome.LAVA) { px = xLeft; py = y1; return; }\n        }\n      }\n    } catch (Throwable ignored) {\n      // If anything goes wrong, keep the original spawn.\n    }\n  }\n\n  private void spawnInitialEncounters(int orcsTarget, int deerTarget, int streamRadius) {\n    // IMPORTANT (FUSA / Areas-only): entity streaming + culling must be based on the CAMERA center,\n    // not the player position.\n    // Reason: in AREA_MODE the camera is clamped near edges, so cam.center != player.\n    // If we stream/cull around the player we can end up with "no trees visible" near borders.\n    float camCenterX = px;\n    float camCenterY = py;\n    if (AREA_MODE) {\n      float tw = World.TILE_WORLD;\n      float areaW = AREA_W_TILES * tw;\n      float areaH = AREA_H_TILES * tw;\n\n      float halfW = (cam.viewportWidth * cam.zoom) * 0.5f;\n      float halfH = (cam.viewportHeight * cam.zoom) * 0.5f;\n\n      camCenterX = MathUtils.clamp(px, halfW, areaW - halfW);\n      camCenterY = MathUtils.clamp(py, halfH, areaH - halfH);\n    }\n\n    int ccx = (int) Math.floor((camCenterX / World.TILE_WORLD) / World.CHUNK_SIZE);\n    int ccy = (int) Math.floor((camCenterY / World.TILE_WORLD) / World.CHUNK_SIZE);\n\n    int r = Math.max(0, streamRadius);\n    int minCx = ccx - r;\n    int maxCx = ccx + r;\n    int minCy = ccy - r;\n    int maxCy = ccy + r;\n\n    spawnMany(EntityType.ORK_GRUNT, orcsTarget, minCx, maxCx, minCy, maxCy);\n    spawnMany(EntityType.ANIMAL_DEER, deerTarget, minCx, maxCx, minCy, maxCy);\n  }\n\n  private void spawnMany(EntityType t, int target, int minCx, int maxCx, int minCy, int maxCy) {\n    // Keep spawns away from the player.\n    float minPlayerDist = ENCOUNTER_MIN_PLAYER_DIST_WU;\n    float min2 = minPlayerDist * minPlayerDist;\n\n    // Default: avoid stacking encounter entities.\n    // NOTE: for orks we deliberately allow tighter clustering (requested) via pack spawning below.\n    float minEntityDist = ENCOUNTER_MIN_ENTITY_DIST_WU;\n    float minE2 = minEntityDist * minEntityDist;\n\n    int spawned = 0;\n    int attempts = 0;\n    int maxAttempts = Math.max(400, target * 80);\n\n    final boolean isOrk = (t == EntityType.ORK_GRUNT);\n\n    while (spawned < target && attempts < maxAttempts) {\n      attempts++;\n\n      int cx = randRange(minCx, maxCx);\n      int cy = randRange(minCy, maxCy);\n\n      int tx = cx * World.CHUNK_SIZE + randRange(0, World.CHUNK_SIZE - 1);\n      int ty = cy * World.CHUNK_SIZE + randRange(0, World.CHUNK_SIZE - 1);\n\n      float wx = (tx + 0.5f) * World.TILE_WORLD;\n      float wy = (ty + 0.5f) * World.TILE_WORLD;\n\n      // MUST NOT generate chunks for encounter placement.\n      if (world.isWaterAtWorldPeek(wx, wy, true)) continue;\n      if (world.isBlockedAtWorldPeek(wx, wy, true)) continue;\n      int bid = world.biomeIdAtWorldPeek(wx, wy, Biome.GRASSLAND.id & 0xff);\n      if (Biome.byId(bid) == Biome.LAVA) continue;\n\n      float dx = wx - px;\n      float dy = wy - py;\n      if (dx * dx + dy * dy < min2) continue;\n\n      if (!isOrk) {\n        // Deer etc: keep them spread out.\n        boolean nearOther = false;\n        for (int k = 0; k < Entities.MAX; k++) {\n          if (!entities.alive[k]) continue;\n          EntityType ot = entities.type[k];\n          if (ot != EntityType.ORK_GRUNT && ot != EntityType.ANIMAL_DEER) continue;\n          float ox = entities.x[k] - wx;\n          float oy = entities.y[k] - wy;\n          if (ox * ox + oy * oy < minE2) { nearOther = true; break; }\n        }\n        if (nearOther) continue;\n\n        if (entities.spawn(t, wx, wy) >= 0) spawned++;\n        continue;\n      }\n\n      // Orks: spawn in small packs around a valid center point (more "gebndelt").\n      // Do NOT change ork stats/AI; only initial spatial distribution.\n      int pack = 3 + randRange(0, 3); // 3..6\n      float packR = ORK_PACK_RADIUS_WU; // tight radius\n      float minPackDist = ORK_PACK_MIN_DIST_WU; // avoid exact stacking\n      float minPackDist2 = minPackDist * minPackDist;\n\n      for (int i = 0; i < pack && spawned < target; i++) {\n        float ox = (randRange(-1000, 1000) / 1000f) * packR;\n        float oy = (randRange(-1000, 1000) / 1000f) * packR;\n        float sx = wx + ox;\n        float sy = wy + oy;\n\n        if (world.isWaterAtWorldPeek(sx, sy, true)) continue;\n        if (world.isBlockedAtWorldPeek(sx, sy, true)) continue;\n        int bid2 = world.biomeIdAtWorldPeek(sx, sy, Biome.GRASSLAND.id & 0xff);\n        if (Biome.byId(bid2) == Biome.LAVA) continue;\n\n        // avoid placing on top of existing encounter entities\n        boolean tooClose = false;\n        for (int k = 0; k < Entities.MAX; k++) {\n          if (!entities.alive[k]) continue;\n          EntityType ot = entities.type[k];\n          if (ot != EntityType.ORK_GRUNT && ot != EntityType.ANIMAL_DEER) continue;\n          float dx2 = entities.x[k] - sx;\n          float dy2 = entities.y[k] - sy;\n          if (dx2 * dx2 + dy2 * dy2 < minPackDist2) { tooClose = true; break; }\n        }\n        if (tooClose) continue;\n\n        if (entities.spawn(t, sx, sy) >= 0) {\n          spawned++;\n        }\n      }\n\n      // If nothing could be placed, keep trying other centers.\n      // (No-op: this loop proceeds to the next center anyway; keep flag for clarity.)\n      // if (!placedAny) continue;\n    }\n  }\n\n  @Override\n  public void show() {\n    if (shownOnce) {\n      if (inputAdapter != null) Gdx.input.setInputProcessor(inputAdapter);\n      // Ensure cameras match current window size on resume.\n      resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n      game.audio.applySettings(game.settings);\n      return;\n    }\n    shownOnce = true;\n\n    // Areas-only: do NOT preload/stream procedural chunks here.\n    // Area content is loaded explicitly via JsonAreaWorldLoader (ground layer applied into fixed area chunks).\n\n    cam = new OrthographicCamera();\n    cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n\n    uiCam = new OrthographicCamera();\n    uiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n    uiCam.update();\n\n    // BLOCK 3: fix camera at maximal zoom-out (30% less than previous standard)\n    cam.zoom = ZOOM_MAX;\n\n    batch = new SpriteBatch();\n    font = new BitmapFont();\n    font.getData().setScale(UI_FONT_SCALE);\n    font.setColor(0f, 0f, 0f, 1f);\n    shape = new ShapeRenderer();\n\n    // Day/Night overlay texture\n    try {\n      Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);\n      pm.setColor(Color.WHITE);\n      pm.fill();\n      dayNight1x1 = new Texture(pm);\n      pm.dispose();\n    } catch (Throwable ignored) {\n      dayNight1x1 = null;\n    }\n\n    // Day/Night debug lamp texture (radial gradient)\n    try {\n      final int sz = 128;\n      Pixmap pm = new Pixmap(sz, sz, Pixmap.Format.RGBA8888);\n      pm.setBlending(Pixmap.Blending.None);\n      for (int y = 0; y < sz; y++) {\n        for (int x = 0; x < sz; x++) {\n          float dx = (x + 0.5f) - sz * 0.5f;\n          float dy = (y + 0.5f) - sz * 0.5f;\n          float d = (float)Math.sqrt(dx * dx + dy * dy) / (sz * 0.5f);\n          float a = 1f - d;\n          if (a < 0f) a = 0f;\n          // softer falloff\n          a = a * a;\n          pm.setColor(1f, 1f, 1f, a);\n          pm.drawPixel(x, y);\n        }\n      }\n      dnLightTex = new Texture(pm);\n      pm.dispose();\n    } catch (Throwable ignored) {\n      dnLightTex = null;\n    }\n\n    // Recreate Day/Night mask framebuffer\n    rebuildDnMaskFbo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n\n    // Start day music immediately (night starts when it becomes night)\n    dayBgIdx = 0;\n    nightBgIdx = 0;\n    dnSideDay = (dnVisual >= DN_MIDPOINT);\n    dnSwitching = false;\n    dnSwitchT = 0f;\n    dnSwitchToDay = dnSideDay;\n    playDayBg(true);\n    // nightBgMusic stays null until needed\n\n    // Boot/preload overlay state (covers world initially and then fades out; blocks control until done)\n    bootOverlayActive = true;\n    bootOverlayControl = false;\n    bootOverlayT = 0f;\n    bootOverlayFadeT = 0f;\n    bootOverlayFill = 0f;\n    bootOverlayA = 1f;\n\n    // BLOCK B: capture mouse wheel for zoom + pricing editor numeric input\n    inputAdapter = new InputAdapter() {\n      @Override\n      public boolean scrolled(float amountX, float amountY) {\n        // amountY: positive usually = scroll down\n        if (questPopupOpen) {\n          // WQG dialog scroll (text area).\n          // Positive amountY = scroll down => move text up (increase scroll).\n          wqgScrollPx += (float) Math.signum(amountY) * 90f;\n          return true;\n        }\n        // Questlogbook scroll (side panels).\n        if (questLogOpenQuestsOpen || questLogDoneQuestsOpen) {\n          float mx = Gdx.input.getX();\n          float my = uiMouseYUp();\n          float step = (float) Math.signum(amountY) * 90f;\n          if (questLogOpenQuestsOpen && hitRect(mx, my, questLogOpenPanelX, questLogOpenPanelY, questLogOpenPanelW, questLogOpenPanelH)) {\n            questLogOpenScrollPx += step;\n            return true;\n          }\n          if (questLogDoneQuestsOpen && hitRect(mx, my, questLogDonePanelX, questLogDonePanelY, questLogDonePanelW, questLogDonePanelH)) {\n            questLogDoneScrollPx += step;\n            return true;\n          }\n        }\n        if (craftOpen) {\n          // scroll craft list (do not zoom while craft panel is open)\n          craftScroll += (float) Math.signum(amountY) * 90f;\n          return true;\n        }\n        if (skillsOpen) {\n          pendingSkillScrollY += (int) Math.signum(amountY);\n        } else {\n          // Invert wheel so "scroll up" zooms IN (closer) and "scroll down" zooms OUT.\n          pendingScrollY -= (int) Math.signum(amountY);\n        }\n        return true;\n      }\n\n      @Override\n      public boolean keyTyped(char character) {\n        // Buy popup numeric input (quantity)\n        if (buyPopup) {\n          if (character >= '0' && character <= '9') {\n            if (buyBuffer.length() < 5) {\n              buyBuffer += character;\n              buyApplyBuffer();\n            }\n            return true;\n          }\n          return false;\n        }\n\n        // Craft panel qty input\n        if (craftOpen && craftQtyFocusIdx >= 0) {\n          if (character >= '0' && character <= '9') {\n            if (craftQtyBuffer.length() < 5) {\n              // avoid leading zeros\n              if (craftQtyBuffer.equals("0")) craftQtyBuffer = "";\n              craftQtyBuffer += character;\n              craftApplyQtyBuffer();\n            }\n            return true;\n          }\n          return false;\n        }\n\n        // Pricing editor numeric input\n        if (!pricingOpen) return false;\n        if (pricingPresetPopup) return false;\n\n        if (character >= '0' && character <= '9') {\n          if (pricingEditBuffer.length() < 9) {\n            pricingEditBuffer += character;\n            pricingApplyBufferToSelected();\n          }\n          return true;\n        }\n        return false;\n      }\n\n      @Override\n      public boolean keyDown(int keycode) {\n        if (buyPopup) {\n          if (keycode == Input.Keys.BACKSPACE) {\n            if (!buyBuffer.isEmpty()) {\n              buyBuffer = buyBuffer.substring(0, buyBuffer.length() - 1);\n              buyApplyBuffer();\n            }\n            return true;\n          }\n          if (keycode == Input.Keys.ENTER) {\n            buyConfirm();\n            return true;\n          }\n          return false;\n        }\n\n        // Craft qty editing\n        if (craftOpen && craftQtyFocusIdx >= 0) {\n          if (keycode == Input.Keys.BACKSPACE) {\n            if (!craftQtyBuffer.isEmpty()) {\n              craftQtyBuffer = craftQtyBuffer.substring(0, craftQtyBuffer.length() - 1);\n              if (craftQtyBuffer.isEmpty()) craftQtyBuffer = "0";\n              craftApplyQtyBuffer();\n            }\n            return true;\n          }\n          if (keycode == Input.Keys.ENTER) {\n            craftApplyQtyBuffer();\n            craftQtyFocusIdx = -1;\n            return true;\n          }\n          if (keycode == Input.Keys.ESCAPE) {\n            craftQtyFocusIdx = -1;\n            return true;\n          }\n          return false;\n        }\n\n        if (!pricingOpen) return false;\n\n        if (!pricingPresetPopup) {\n          if (keycode == Input.Keys.BACKSPACE) {\n            if (!pricingEditBuffer.isEmpty()) {\n              pricingEditBuffer = pricingEditBuffer.substring(0, pricingEditBuffer.length() - 1);\n              pricingApplyBufferToSelected();\n            }\n            return true;\n          }\n          if (keycode == Input.Keys.ENTER) {\n            pricingApplyBufferToSelected();\n            return true;\n          }\n        }\n\n        return false;\n      }\n    };\n    // Wrap input adapter into modular controller (no behavior change)\n    inputController = new GameInputController(inputAdapter);\n    inputAdapter = inputController;\n    Gdx.input.setInputProcessor(inputAdapter);\n\n    renderPipeline = new RenderPipeline();\n\n    tiles = new TilesetRegions();\n    chunkRenderer = new ChunkRenderer(tiles);\n\n    entityRegions = new EntityRegions();\n    entityRenderer = new EntityRenderer(entityRegions);\n    uiRegions = new UiRegions();\n\n    // (obsolete POI sprite setup removed)\n\n    // Controllers (wrappers; keep behavior identical)    // Thin wrapper controllers removed; keep behavior via direct calls.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 122-122

`java
        spawnInitialEncounters(\n            isHomeArea ? 0 : INITIAL_ORC_CAP,\n            isHomeArea ? 0 : INITIAL_DEER_CAP,\n            r0);\n      }\n\n      // Starter items (DEV): add 1 of every defined item so you can inspect sprites one by one.\n      // (Tools/weapons have stackMax=1, normal items stack.)\n      if (data != null && data.items != null) {\n        for (int itemId = 0; itemId < data.items.length; itemId++) {\n          if (data.items[itemId] == null) continue;\n          inv.add(itemId, 1);\n        }\n      }\n\n      // DEV CHEAT (requested): set sprint skill to level 20 if present.\n      if (SK_SPRINTING >= 0 && SK_SPRINTING < progress.skillLv.length) {\n        progress.skillLv[SK_SPRINTING] = 20;\n      }\n    }\n\n    // Hotbar defaults\n    for (int i=0;i<hotbar.length;i++) hotbar[i] = -1;\n    // Start with Axe equipped, Pickaxe next to it.\n    hotbar[0] = 14;\n    hotbar[1] = 15;\n    hotbarSel = 0;\n  }\n\n  @Override\n  public void render(float delta) {\n    // (obsolete debug counters removed)\n\n    tickBootOverlay(delta);\n\n    // Controls/input are blocked while the boot overlay is active.\n    if (!bootOverlayControl) {\n      // Still render the world + UI (overlay covers it), but do not process inputs.\n      int selectedTool = equippedFromHotbar();\n      if (playerE >= 0) entities.data0[playerE] = selectedTool; // equipped tool/weapon for rendering\n\n      /* ===== SIMULATION TICK ===== */\n      updateScheduler(delta);\n\n      // (obsolete client-sync notes removed)\n\n      /* ===== WORLD DRAW ===== */\n      worldView.renderWorld(delta, selectedTool);\n\n      // (obsolete rendering removed)\n\n      // (obsolete night-skip overlay removed)\n\n      // Day/Night visuals target + smoothing (never instant, even via debug)\n      {\n        float rawLight;\n        float rawWarm;\n        switch (dnDebugMode) {\n          case 1 -> {\n            rawLight = 1f;\n            rawWarm = 0f;\n          }\n          case 2 -> {\n            rawLight = 0f;\n            rawWarm = 0f;\n          }\n          default -> {\n            rawLight = dayNight.daylightPhased();\n            rawWarm = dayNight.warmTwilight();\n          }\n        }\n        dnTarget = rawLight;\n        dnWarmTarget = rawWarm;\n\n        float k = 1f - (float)Math.exp(-delta / 2.5f); // ~2.5s smoothing\n        dnVisual += (dnTarget - dnVisual) * k;\n        dnWarm += (dnWarmTarget - dnWarm) * k;\n      }\n\n      // Day/Night BG music: crossfade day<->night based on dnVisual.\n      updateDayNightMusic(delta);\n\n      // Day/Night mask for debug lamp (must run outside UI batch)\n      updateDnMask();\n\n      // World-space action overlay (ring + FOV cone)\n      // NOTE: boot overlay path runs before fovFx/fovFy are computed in the normal input path.\n      // Controls are blocked anyway, so we skip drawing the action overlay here.\n\n      /* ===== UI DRAW ===== */\n      hudRenderer.renderUI();\n      return;\n    }\n\n    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {\n      if (buyPopup) {\n        buyPopup = false;\n        buyBuffer = "";\n        dragArmed = false;\n        dragActive = false;\n      } else if (questLogHubOpen || questLogOpenQuestsOpen || questLogDoneQuestsOpen) {\n        // Close quest logbook windows (sub-windows first).\n        if (questLogOpenQuestsOpen) {\n          questLogOpenQuestsOpen = false;\n        } else if (questLogDoneQuestsOpen) {\n          questLogDoneQuestsOpen = false;\n        } else {\n          questLogHubOpen = false;\n        }\n      } else if (pricingOpen) {\n        pricingOpen = false;\n        pricingPresetPopup = false;\n      } else if (walletOpen) {\n        walletOpen = false;\n      } else if (questPopupOpen) {\n        questPopupOpen = false;\n        openQuestGuyE = -1;\n      } else {\n        game.setScreen(new PauseScreen(game, this));\n        return;\n      }\n    }\n\n    // Skill menu: apply scroll wheel to menu (disable zoom while menu is open)\n    if (skillsOpen && pendingSkillScrollY != 0) {\n      float step = 48f * UI_FONT_SCALE;\n      skillMenuScrollPx += pendingSkillScrollY * step;\n      if (skillMenuScrollPx < 0f) skillMenuScrollPx = 0f;\n      pendingSkillScrollY = 0;\n    }\n\n    // BLOCK B: zoom controls\n    if (!skillsOpen && pendingScrollY != 0) {\n      cam.zoom = MathUtils.clamp(cam.zoom + pendingScrollY * ZOOM_STEP_WHEEL, ZOOM_MIN, ZOOM_MAX);\n      pendingScrollY = 0;\n    }\n    if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS) || Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {\n      cam.zoom = MathUtils.clamp(cam.zoom - ZOOM_STEP_KEYS, ZOOM_MIN, ZOOM_MAX);\n    }\n    if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {\n      cam.zoom = MathUtils.clamp(cam.zoom + ZOOM_STEP_KEYS, ZOOM_MIN, ZOOM_MAX);\n    }\n\n    // Debug toggles for constraints\n    if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) hasBoat = !hasBoat;\n    if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) hasClimb = !hasClimb;\n\n    // (DBG TileTrees hotkey handled in tick(); render() input can be blocked by the boot overlay.)\n\n    // SHIFT helper (used by multiple debug/editor hotkeys below)\n    boolean shiftHeld = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);\n\n    // ===== Area travel (Shift+T) =====\n    // Rule:\n    // - Crossing into VOID does NOT auto-travel.\n    // - Travel is only possible when holding SHIFT+T while in red zone OR already in void.\n    // - Penalties only apply while standing in void.\n    // Areas-only: travel can be triggered only while standing in the red zone (still inside the area).\n    if (AREA_MODE && shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.T) && inRedZone) {\n      int tx = (int) Math.floor(px / World.TILE_WORLD);\n      int ty = (int) Math.floor(py / World.TILE_WORLD);\n      int w = AREA_W_TILES;\n      int h = AREA_H_TILES;\n\n      Dir4 dir;\n      // Areas-only: we never allow stepping outside the area, so direction is always chosen from inside.\n      // Choose the nearest edge.\n      {\n        int distL = tx;\n        int distR = (w - 1) - tx;\n        int distB = ty;\n        int distT = (h - 1) - ty;\n        int minD = Math.min(Math.min(distL, distR), Math.min(distB, distT));\n        if (minD == distL) dir = W;\n        else if (minD == distR) dir = E;\n        else if (minD == distB) dir = S;\n        else dir = N;\n      }\n\n      boolean ok = areaTryTravelAtEdge(dir);\n      if (ok) {\n// Nicht fertiges Feature:         inVoid = false;\n// Nicht fertiges Feature:         voidTimer = 0f;\n// Nicht fertiges Feature:         voidStage = 0;\n      }\n    }\n\n    // ===== WorldMap UI (Shift+M) =====\n    if (shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.M)) {\n      mapOpen = !mapOpen;\n      mapDragging = false;\n\n      if (mapOpen) {\n        // Open: start in world overview.\n        mapViewMode = 0;\n// Nicht fertiges Feature:         mapZoomOpen = false;\n        mapTransActive = false;\n        mapTransT = 0f;\n// Nicht fertiges Feature:         worldMapDirty = false;\n        mapSelectedAx = worldMap.curAx;\n        mapSelectedAy = worldMap.curAy;\n      } else {\n        // Close: reset.\n        mapViewMode = 0;\n// Nicht fertiges Feature:         mapZoomOpen = false;\n        mapTransActive = false;\n        mapTransT = 0f;\n      }\n    }\n\n    // Back from area map to world map (ESC while map is open)\n    if (mapOpen && mapViewMode == 1 && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {\n      mapTransActive = true;\n      mapTransT = 0f;\n      mapTransFrom = 1;\n      mapTransTo = 0;\n    }\n\n    // Map panning (drag)\n    if (mapOpen) {\n      float mx = Gdx.input.getX();\n      float my = Gdx.input.getY();\n      if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {\n        if (!mapDragging) {\n          mapDragging = true;\n          mapDragLastX = mx;\n          mapDragLastY = my;\n        } else {\n          float dx = mx - mapDragLastX;\n          float dy = my - mapDragLastY;\n          mapPanX += dx;\n          mapPanY -= dy;\n          mapDragLastX = mx;\n          mapDragLastY = my;\n        }\n      } else {\n        mapDragging = false;\n      }\n\n      // Map click select + zoom\n      if (Gdx.input.justTouched()) {\n        float sx = mx;\n        float sy = (Gdx.graphics.getHeight() - my);\n        float cx0 = (Gdx.graphics.getWidth() * 0.5f) + mapPanX;\n        float cy0 = (Gdx.graphics.getHeight() * 0.5f) + mapPanY;\n        float cell = 40f;\n\n        for (java.util.Map.Entry<AreaCoord, String> e : worldMap.knownAreas.entrySet()) {\n          AreaCoord c = e.getKey();\n          float bx = cx0 + c.ax * cell;\n          float by = cy0 + c.ay * cell;\n          if (sx >= bx - cell * 0.5f && sx <= bx + cell * 0.5f && sy >= by - cell * 0.5f && sy <= by + cell * 0.5f) {\n            mapSelectedAx = c.ax;\n            mapSelectedAy = c.ay;\n// Nicht fertiges Feature:             mapZoomOpen = true;\n\n            // Transition to area map (war-stand or live if current).\n            mapTransActive = true;\n            mapTransT = 0f;\n            mapTransFrom = 0;\n            mapTransTo = 1;\n            break;\n          }\n        }\n      }\n    }\n\n    // Debug/Dev keybinds are handled centrally via DebugCommands.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 123-123

`java
\n    // Toggle UI\n    if (!pricingOpen && !walletOpen && Gdx.input.isKeyJustPressed(Input.Keys.B)) {\n      buildMode = !buildMode;\n      if (buildMode) { invOpen = false; craftOpen = false; shopOpen = false; openChestE = -1; }\n    }\n\n    if (!pricingOpen && !walletOpen && Gdx.input.isKeyJustPressed(Input.Keys.I)) {\n      invOpen = !invOpen;\n      if (invOpen) {\n        // Allow inventory to be opened together with other panels.\n        // If it would overlap, it will snap to the next free spot on first draw.\n        buildMode = false;\n        openChestE = -1;\n        invJustOpened = true;\n        // Force re-placement on first draw (panel size is computed there).\n        invAnchorX = Float.NaN;\n        invAnchorY = Float.NaN;\n      } else {\n        invDrag = false;\n      }\n    }\n    if (!pricingOpen && !walletOpen && Gdx.input.isKeyJustPressed(Input.Keys.C)) {\n      craftOpen = !craftOpen;\n      if (craftOpen) { invOpen = false; shopOpen = false; buildMode = false; openChestE = -1; }\n    }\n\n    if (pricingOpen) {\n      pricingHandleInput();\n    }\n\n    // Build selection + rotation\n    if (buildMode) {\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) buildSel = 0;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) buildSel = 1;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) buildSel = 2;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) buildSel = 3;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) buildRot -= 15f;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.E)) buildRot += 15f;\n    }\n\n    // Interact (E)\n    // (obsolete netcode interaction stub removed)\n\n    // Hotbar select 1..8\n    if (!shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0) {\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) hotbarSel = 0;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) hotbarSel = 1;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) hotbarSel = 2;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) hotbarSel = 3;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) hotbarSel = 4;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) hotbarSel = 5;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) hotbarSel = 6;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) hotbarSel = 7;\n    }\n\n    int selectedTool = equippedFromHotbar();\n    if (playerE >= 0) entities.data0[playerE] = selectedTool; // equipped tool/weapon for rendering\n\n    // Eat meat: fills Hunger first (+50 per meat, smooth), then when Hunger is full it heals HP (+25 per meat, smooth).\n    boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);\n    boolean wantEat = (!shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0) &&\n        ((shiftPressed && Gdx.input.isKeyJustPressed(Input.Keys.J)) || Gdx.input.isKeyJustPressed(Input.Keys.H));\n\n    if (wantEat) {\n      if (inv.spend(28, 1)) {\n        pendingFoodHunger += 50f;\n        pendingFoodHp += 25f;\n        game.audio.sfx("audio/sfx/eat.wav", game.audio.sfxVolume(game.settings));\n      } else {\n        game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n      }\n    }\n\n    // Skill menu (P) – pauses simulation\n    if (!shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0 && Gdx.input.isKeyJustPressed(Input.Keys.P)) {\n      skillsOpen = !skillsOpen;\n      if (skillsOpen) {\n        skillMenuScrollPx = 0f;\n        preloadSkillIcons();\n        buildSkillMenuRows();\n      }\n    }\n\n    // Sleep / bed (SHIFT+P)\n    if (!shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0 && shiftPressed && Gdx.input.isKeyJustPressed(Input.Keys.P)) {\n       \n    }\n\n    // When a modal UI is open, do NOT warp/clamp the OS cursor.\n    // Clamp cursor to action ring only when no modal UI is open.\n    // When WQG dialog is open we must release mouse (no warping).\n    updateMouseWorld(!shopOpen && !craftOpen && !invOpen && !buildMode && !walletOpen\n        && !questLogHubOpen && !questLogOpenQuestsOpen && !questLogDoneQuestsOpen\n        && openChestE < 0 && !skillsOpen && !questPopupOpen);\n\n    // Cursor mode:\n    // - Default: small crosshair (set in SurvivalGame)\n    // - Inside the action ring (non-modal): hide hardware cursor so the in-world aim UI is clean\n    {\n    boolean modal = shopOpen || craftOpen || invOpen || buildMode || pricingOpen || walletOpen\n        || questLogHubOpen || questLogOpenQuestsOpen || questLogDoneQuestsOpen\n        || openChestE >= 0 || skillsOpen || questPopupOpen;\n      boolean wantHidden = false;\n      drawUnarmedDotCursor = false;\n      if (!modal) {\n        float r = actionReach(selectedTool);\n        float dx = mouseWorldX - px;\n        float dy = mouseWorldY - py;\n        if (dx * dx + dy * dy <= r * r + 0.001f) {\n          wantHidden = true;\n          if (selectedTool < 0) drawUnarmedDotCursor = true;\n        }\n      }\n      if (wantHidden != lastCursorHidden) {\n        if (wantHidden) game.setCursorHidden();\n        else game.setCursorCrosshair();\n        lastCursorHidden = wantHidden;\n      }\n    }\n\n    // FOV forward vector follows mouse direction, but never into the player's back.\n    // Requested: ONLY PLAYER should smoothly face toward the mouse (relative) to avoid stiff snapping.\n    byte facingDir = (playerE >= 0) ? entities.dir[playerE] : (byte)2;\n    Vector2 baseF = scratch.v2a;\n    facingVec(facingDir, baseF);\n\n    // Desired aim dir: mouse direction, but clamped to the front hemisphere (keep "nothing in my back" rule).\n    float desiredFx = baseF.x;\n    float desiredFy = baseF.y;\n    float mdx = mouseWorldX - px;\n    float mdy = mouseWorldY - py;\n    float ml2 = mdx * mdx + mdy * mdy;\n    if (ml2 > 1e-6f) {\n      float invLen = (float) (1.0 / Math.sqrt(ml2));\n      float ax = mdx * invLen;\n      float ay = mdy * invLen;\n      if (baseF.x * ax + baseF.y * ay >= 0f) {\n        desiredFx = ax;\n        desiredFy = ay;\n      }\n    }\n\n    // Aim angle: hard face toward desired direction (no smoothing) as requested.\n    float desiredA = (float) Math.atan2(desiredFy, desiredFx);\n    playerAimA = desiredA;\n// Nicht fertiges Feature:     playerAimInit = true;\n\n    float fovFx = desiredFx;\n    float fovFy = desiredFy;\n\n    // Equipped-in-hand follows mouse (only when no UI/modes are open).\n    if (!shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0 && playerE >= 0) {\n      entities.dir[playerE] = dirFromVel(fovFx, fovFy, entities.dir[playerE]);\n    }\n\n    // LMB: build place / remove OR harvest OR interact (all world actions are LMB)\n    // Holding LMB emulates repeated click (press/release) at fixed cadence.\n    // Damage/work is only applied during the hit window, not during the pause.\n    final boolean rmbAllowed = (!shopOpen && !craftOpen && !invOpen && !walletOpen && !pricingOpen && !skillsOpen && !mapOpen\n        && openChestE < 0 && !questPopupOpen);\n    final boolean rmbPhysJust = rmbAllowed && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);\n    final boolean rmbPhysDown = rmbAllowed && Gdx.input.isButtonPressed(Input.Buttons.LEFT);\n\n    // Merchant/chest interaction on click (must happen before harvest/combat).\n    boolean lmbClickHandled = false;\n    if (rmbPhysJust) {\n      if (tryInteractAtMouse(fovFx, fovFy)) {\n        lmbClickHandled = true;\n        rmbHoldActive = false;\n        rmbPulseT = 0f;\n        rmbHitT = 0f;\n      }\n    }\n\n    boolean rmbJust = false;\n    boolean rmbDown = false;\n    if (rmbPhysJust && !lmbClickHandled) {\n      rmbHoldActive = true;\n      rmbPulseT = RMB_PULSE_PERIOD;\n      rmbHitT = RMB_HIT_WINDOW;\n      rmbJust = true;\n      rmbDown = true;\n    } else if (rmbPhysDown && !lmbClickHandled) {\n      if (!rmbHoldActive) {\n        rmbHoldActive = true;\n        rmbPulseT = RMB_PULSE_PERIOD;\n        rmbHitT = RMB_HIT_WINDOW;\n        rmbJust = true;\n        rmbDown = true;\n      } else {\n        rmbPulseT -= delta;\n        rmbHitT -= delta;\n        if (rmbPulseT <= 0f) {\n          // Start next virtual click.\n          do {\n            rmbPulseT += RMB_PULSE_PERIOD;\n          } while (rmbPulseT <= 0f);\n          rmbHitT = RMB_HIT_WINDOW;\n          rmbJust = true;\n        }\n        rmbDown = (rmbHitT > 0f);\n      }\n    } else {\n      rmbHoldActive = false;\n      rmbPulseT = 0f;\n      rmbHitT = 0f;\n    }\n    if (!lmbClickHandled && (rmbJust || rmbDown)) {\n      if (buildMode) {\n        if (rmbJust) {\n          boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);\n          if (shift) {\n             \n          } else {\n            if (!isInsideFov(px, py, mouseWorldX, mouseWorldY, fovFx, fovFy, ACTION_FOV_DEG)) {\n              game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n            } else {\n              placeBuildAtMouse(mouseWorldX, mouseWorldY);\n            }\n          }\n        }\n      } else {\n        if (rmbDown) {\n          // Skill-based harvest speed: +5% per level (Woodcutting for trees, Mining for rocks/ore)\n          int wcLv = (SK_WOODCUTTING >= 0 && SK_WOODCUTTING < progress.skillLv.length) ? progress.skillLv[SK_WOODCUTTING] : 1;\n          int miningLv = (SK_MINING >= 0 && SK_MINING < progress.skillLv.length) ? progress.skillLv[SK_MINING] : 1;\n          float speedMul = 1f;\n          if (selectedTool == 14) speedMul = SkillEffects.mul5(wcLv);\n          if (selectedTool == 15) speedMul = SkillEffects.mul5(miningLv);\n          float harvestDt = delta * speedMul;\n          // If out of stamina, you can't keep harvesting.\n          if ((selectedTool == 14 || selectedTool == 15) && needs.stamina <= 0.01f) {\n            harvestDt = 0f;\n          }\n\n          // Continuous harvest tick (LMB hold). If we hit nothing, only 10% stamina cost.\n          if (harvestDt > 0f) {\n            float fovDeg = actionFovDeg();\n            // Areas-only: tile-trees are NOT Entities (prevents hitting Entities.MAX=2048).\n            // Try harvesting a tile-tree first; if none is hit, fall back to entity-based harvesting.\n            HarvestTick ht = null;\n            if (AREA_MODE && selectedTool == 14) {\n              ht = tryHarvestTileTreeFov(\n                  px, py,\n                  mouseWorldX, mouseWorldY,\n                  fovFx, fovFy,\n                  fovDeg,\n                  REACH_HARVEST,\n                  selectedTool,\n                  harvestDt);\n            }\n            if (ht == null || !ht.didWork()) {\n              ht = harvest.tickHarvestFov(\n                  entities,\n                  px, py,\n                  mouseWorldX, mouseWorldY,\n                  fovFx, fovFy,\n                  fovDeg,\n                  REACH_HARVEST,\n                  selectedTool,\n                  harvestDt);\n            }\n\n            boolean didWork = (ht != null && ht.didWork());\n\n            // If we didn't hit a harvest-node, allow mining ROCK ground tiles with Pickaxe.\n            if (!didWork && selectedTool == 15) {\n              if (tryMineRockTile(mouseWorldX, mouseWorldY, REACH_HARVEST, selectedTool)) {\n                didWork = true;\n              }\n            }\n\n            // Stamina: full cost if we did work; 10% if we swung into empty.\n            if (selectedTool == 14 || selectedTool == 15) {\n              float baseCostPerSec = 6.0f;\n              float cost = baseCostPerSec * harvestDt;\n              if (!didWork) cost *= 0.10f;\n              needs.stamina = Math.max(0f, needs.stamina - cost);\n            }\n\n            if (ht != null && ht.event() != null) {\n              var ev = ht.event();\n              entities.spawnDrop(ev.dropItemId(), ev.dropAmount(), ev.x(), ev.y());\n\n              // FUSA Story: tile-tree persistence (optional)\n              try {\n                if (AREA_MODE && areaTreePresentBits != null && areaTreeCutBits != null && areaTreeW > 0 && areaTreeH > 0) {\n                  EntityType htType = ev.harvestedType();\n                  if (TILE_TREES_FELL_ON_HARVEST && htType == EntityType.NODE_TREE && ev.replaceWithStump()) {\n                    // NOTE: tile-tree events use a custom Y anchor (ty*TILE_WORLD + 34) so the sprite foot sits on the tile.\n                    // If we floor(y / TILE_WORLD) we'd mark the wrong tile (typically +2 tiles).\n                    int tx = (int) Math.floor(ev.x() / World.TILE_WORLD);\n\n                    int ty = (int) Math.floor((ev.y() - 34.0f) / World.TILE_WORLD);\n                    // Fallback (safety) if the adjusted form produces nonsense.\n                    if (ty < 0 || ty >= areaTreeH) {\n                      ty = (int) Math.floor(ev.y() / World.TILE_WORLD);\n                    }\n                    if (tx >= 0 && ty >= 0 && tx < areaTreeW && ty < areaTreeH) {\n                      int bit = bitIndex(tx, ty, areaTreeW);\n                      if (bitGet(areaTreePresentBits, bit)) {\n                        bitSet(areaTreeCutBits, bit, true);\n                      }\n                    }\n                  }\n                }\n              } catch (Throwable ignored) {}\n\n              game.audio.sfx("audio/sfx/pickup.wav", game.audio.sfxVolume(game.settings));\n            }\n          }\n\n        } else if (rmbJust) {\n// Nicht fertiges Feature:           hackSfxT = 0f;\n          game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n        }\n      }\n    }\n\n    if (!rmbDown) {\n      // release stops hack loop immediately\n// Nicht fertiges Feature:       hackSfxT = 0f;\n    }\n\n    // Combat (LMB)\n    // Tools (axe/pickaxe) are reserved for harvesting on LMB.\n    // If the click was used for interaction (merchant/chest), do not attack.\n    if (!lmbClickHandled && !shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0 && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)\n        && selectedTool != 14 && selectedTool != 15) {\n      // Visual: trigger hand swing on any attack/use attempt.\n      // Requested: NO swing animation for Bow (it shoots an arrow).\n      if (playerE >= 0 && selectedTool != 22) {\n        entities.aiF0[playerE] = HAND_SWING_DUR;\n      }\n      // Weapon handling\n      if (selectedTool >= 20 && selectedTool <= 27) {\n        // Bow (ID 22): uses arrows as ammo.\n        if (selectedTool == 22) {\n          if (bowCooldownT <= 0f) {\n            // Bow: do not spend stamina if no arrows.\n            if (inv.countsById[ITEM_ARROW] <= 0) {\n              game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n            } else {\n              // Spend one arrow.\n              inv.spend(ITEM_ARROW, 1);\n\n              // Only now spend stamina.\n              if (needs.stamina < 6.0f) {\n                game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n              } else {\n                needs.stamina = Math.max(0f, needs.stamina - 6.0f);\n\n                int rangedLv = (SK_COMBAT_RANGED >= 0 && SK_COMBAT_RANGED < progress.skillLv.length) ? progress.skillLv[SK_COMBAT_RANGED] : 1;\n                float mul = SkillEffects.mul5(rangedLv);\n                float baseReload = 0.75f;\n                float reload = baseReload / mul;\n\n                // Fire projectile\n                float dmg = 12f * mul;\n                float speed = 620f;\n                float range = 1200f;\n                float spread = 0.06f / Math.max(1e-3f, mul);\n                if (fireBowArrow(px, py, mouseWorldX, mouseWorldY, fovFx, fovFy, actionFovDeg(), speed, range, dmg, spread)) {\n                  bowCooldownT = reload;\n                  game.audio.sfx("audio/sfx/attack_ranged.wav", game.audio.sfxVolume(game.settings));\n                }\n              }\n            }\n          }\n        } else {\n        // Melee weapons: simple dmg/range tuning by weapon id.\n        float dmg = switch (selectedTool) {\n          case 20 -> 14f; // Sword\n          case 21 -> 12f; // Spear\n          case 23 -> 10f; // Dagger\n          case 24 -> 15f; // Mace\n          case 25 -> 18f; // Battle Axe\n          case 26 -> 13f; // Crystal Staff\n          case 27 -> 16f; // Crossbow (fallback melee)\n          default -> 12f;\n        };\n        float range = switch (selectedTool) {\n          case 21 -> REACH_COMBAT * 1.25f; // Spear longer\n          case 23 -> REACH_COMBAT * 0.85f; // Dagger shorter\n          default -> REACH_COMBAT;\n        };\n\n        int meleeLv = (SK_COMBAT_MELEE >= 0 && SK_COMBAT_MELEE < progress.skillLv.length) ? progress.skillLv[SK_COMBAT_MELEE] : 1;\n        float meleeMul = SkillEffects.mul5(meleeLv);\n        dmg *= meleeMul;\n        // Note: requested extra range mainly for fists/knife, but applying mildly to melee keeps it consistent.\n        range *= meleeMul;\n\n        // Execute melee attack.\n        var k = combat.meleeFov(entities, px, py, mouseWorldX, mouseWorldY, fovFx, fovFy, actionFovDeg(), range, dmg);\n        if (k != null) onKill(k);\n        if (playerE >= 0) entities.aiF0[playerE] = HAND_SWING_DUR;\n\n        game.audio.sfx("audio/sfx/attack_melee.wav", game.audio.sfxVolume(game.settings));\n      }\n      } else {\n        // Unarmed melee (uses the same CombatMelee scaling).\n        int meleeLv = (SK_COMBAT_MELEE >= 0 && SK_COMBAT_MELEE < progress.skillLv.length) ? progress.skillLv[SK_COMBAT_MELEE] : 1;\n        float meleeMul = SkillEffects.mul5(meleeLv);\n        float dmg = 10f * meleeMul;\n        float range = REACH_COMBAT * meleeMul;\n\n        var k = combat.meleeFov(entities, px, py, mouseWorldX, mouseWorldY, fovFx, fovFy, actionFovDeg(), range, dmg);\n        if (k != null) onKill(k);\n        if (playerE >= 0) entities.aiF0[playerE] = HAND_SWING_DUR;\n\n        game.audio.sfx("audio/sfx/attack_melee.wav", game.audio.sfxVolume(game.settings));\n      }\n    }\n\n    // Bow special: allow a melee shove/smack on RIGHT CLICK while holding the bow.\n    if (!shopOpen && !craftOpen && !invOpen && !buildMode && openChestE < 0 && selectedTool == 22\n        && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {\n      // small melee hit\n      float dmg = 6f;\n      float range = REACH_COMBAT * 0.75f;\n      var k = combat.meleeFov(entities, px, py, mouseWorldX, mouseWorldY, fovFx, fovFy, actionFovDeg(), range, dmg);\n      if (k != null) onKill(k);\n      game.audio.sfx("audio/sfx/attack_melee.wav", game.audio.sfxVolume(game.settings));\n      if (playerE >= 0) entities.aiF0[playerE] = HAND_SWING_DUR;\n    }\n\n    // Debug toggles are handled centrally via DebugCommands.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 123-123

`java
\n    // Quest logbook hub (Shift+Q)\n    if (shiftHeld && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {\n      questLogHubOpen = !questLogHubOpen;\n      if (questLogHubOpen) {\n        // Close other modals.\n        shopOpen = false;\n        craftOpen = false;\n        invOpen = false;\n        buildMode = false;\n        walletOpen = false;\n        pricingOpen = false;\n        pricingPresetPopup = false;\n        openChestE = -1;\n        // WQG popup is independent; keep it as-is.\n        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n      } else {\n        // Closing hub also closes both sub-windows.\n        questLogOpenQuestsOpen = false;\n        questLogDoneQuestsOpen = false;\n        game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n      }\n    }\n\n    // Interact / pickup (E)\n    if (!pricingOpen && !walletOpen && Gdx.input.isKeyJustPressed(Input.Keys.E)) {\n      if (tryToggleShop()) {\n        // handled\n      } else if (tryToggleQuestGuy()) {\n        // handled\n      } else {\n        pickupNearby();\n      }\n    }\n\n    if (zqsRt != null) {\n      long nowSec = System.currentTimeMillis() / 1000L;\n      zqsRt.refreshQuestLifecycle(buildZqsProgressSnapshot(nowSec), nowSec);\n    }\n\n    // WQG dialog: offer accept/decline/turn-in is handled via explicit UI buttons (no debug keybinds).\n\n    // UI interactions: shop buy/sell + drag-to-buy + tool->hotbar drag + hotbar reordering + buy popup\n    float uiMx = Gdx.input.getX();\n    float uiMy = uiMouseYUp();\n    boolean hoverHotbar = (hotbarHit(uiMx, uiMy) >= 0);\n    if (shopOpen || invOpen || buildMode || buyPopup || sellPopup || dragArmed || dragActive || hoverHotbar) {\n      uiHandleDragDropAndPopup();\n    }\n\n    // Craft shortcut: craft workbench (key 1) / axe (2) / pickaxe (3)\n    if (craftOpen) {\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) craftByOutput(40);\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) craftByOutput(14);\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) craftByOutput(15);\n    }\n\n    // Chest transfer shortcuts\n    if (openChestE >= 0) {\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) chestStoreFromPlayer(0, 5);\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) chestStoreFromPlayer(1, 5);\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) chestStoreFromPlayer(31, 5);\n      if (Gdx.input.isKeyJustPressed(Input.Keys.F)) chestTakeAll();\n    }\n    collectInputSnapshot(delta, inputState);\n    applyCommands(commandQueue);\n\n    /* ===== SIMULATION TICK ===== */\n    // Simulation must keep running during the boot overlay to hide initial load spikes.\n    updateScheduler(delta);\n\n    // Smoothing must run in the normal gameplay path as well (not only during boot overlay).\n    \n\n    // Debug: one compact line/sec for MP position + camera + entity coupling.\n    \n\n    /* ===== WORLD DRAW ===== */\n    worldView.renderWorld(delta, selectedTool);\n\n    // Day/Night visuals target + smoothing (never instant, even via debug)\n    {\n      float rawLight;\n      float rawWarm;\n      switch (dnDebugMode) {\n        case 1 -> {\n          rawLight = 1f;\n          rawWarm = 0f;\n        }\n        case 2 -> {\n          rawLight = 0f;\n          rawWarm = 0f;\n        }\n        default -> {\n          rawLight = dayNight.daylightPhased();\n          rawWarm = dayNight.warmTwilight();\n        }\n      }\n      dnTarget = rawLight;\n      dnWarmTarget = rawWarm;\n\n      float k = 1f - (float)Math.exp(-delta / 2.5f); // ~2.5s smoothing\n      dnVisual += (dnTarget - dnVisual) * k;\n      dnWarm += (dnWarmTarget - dnWarm) * k;\n    }\n\n    // Day/Night BG music: crossfade day<->night based on dnVisual.\n    updateDayNightMusic(delta);\n\n    // Day/Night mask for debug lamp (must run outside UI batch)\n    updateDnMask();\n\n    // World-space action overlay (ring + FOV cone)\n    if (!shopOpen && !craftOpen && !invOpen && !walletOpen) {\n      drawActionOverlay(fovFx, fovFy);\n    }\n\n    /* ===== UI DRAW ===== */\n    hudRenderer.renderUI();\n\n  }\n\n  private void updateScheduler(float delta) {\n    // Pause simulation when a modal menu is open (skill + craft).\n    if (!skillsOpen && !craftOpen) {\n      scheduler.update(delta, this::tick);\n    }\n  }\n  void renderWorld(float delta, int selectedTool) {\n    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);\n    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);\n\n    // Camera clamp inside the current Area (player cannot enter VOID).\n    if (AREA_MODE) {\n      float tw = World.TILE_WORLD;\n      float areaW = AREA_W_TILES * tw;\n      float areaH = AREA_H_TILES * tw;\n\n      float halfW = (cam.viewportWidth * cam.zoom) * 0.5f;\n      float halfH = (cam.viewportHeight * cam.zoom) * 0.5f;\n\n      float cx = MathUtils.clamp(px, halfW, areaW - halfW);\n      float cy = MathUtils.clamp(py, halfH, areaH - halfH);\n      cam.position.set(cx, cy, 0f);\n    } else {\n      cam.position.set(px, py, 0f);\n    }\n    cam.update();\n\n    batch.setProjectionMatrix(cam.combined);\n    batch.begin();\n    // Guardrail: ensure batch state is sane. Some render paths (entities/fog) change blending/color.\n    batch.enableBlending();\n    batch.setColor(1f, 1f, 1f, 1f);\n    // Areas-only: always draw enough chunks to fully cover the camera viewport (+margin).\n    float chunkWorld = World.TILE_WORLD * World.CHUNK_SIZE;\n    float halfW = (cam.viewportWidth * cam.zoom) * 0.5f;\n    float halfH = (cam.viewportHeight * cam.zoom) * 0.5f;\n    int rx = (int) Math.ceil(halfW / chunkWorld) + 2;\n    int ry = (int) Math.ceil(halfH / chunkWorld) + 2;\n    int r = Math.max(rx, ry);\n    // IMPORTANT: draw around the camera center (camera is clamped near edges).\n    chunkRenderer.draw(batch, world, cam.position.x, cam.position.y, r);\n    entityRenderer.tick(delta);\n\n    // FUSA Story: dense forests are drawn from tile bitmasks (NOT as Entities).\n    // Layering policy:\n    // - Stumps: draw BELOW entities (player should not be hidden by stumps).\n    // - Trees: draw ABOVE entities (player walks "under" the canopy).\n    if (AREA_MODE && areaTreePresentBits != null && areaTreeCutBits != null && areaTreeW > 0 && areaTreeH > 0) {\n      entityRenderer.drawTileTrees(batch, world, areaTreePresentBits, areaTreeCutBits, areaTreeW, areaTreeH,\n          cam.position.x, cam.position.y, r,\n          false, true,\n          !TILE_TREES_FELL_ON_HARVEST);\n    }\n\n    boolean uiBlocksHand = shopOpen || craftOpen || invOpen || buildMode || pricingOpen || walletOpen\n        || questLogHubOpen || questLogOpenQuestsOpen || questLogDoneQuestsOpen\n        || openChestE >= 0 || questPopupOpen;\n    entityRenderer.setPlayerHandVisible(!uiBlocksHand);\n    entityRenderer.draw(batch, entities, loadedMinCx, loadedMaxCx, loadedMinCy, loadedMaxCy);\n\n    if (AREA_MODE && areaTreePresentBits != null && areaTreeCutBits != null && areaTreeW > 0 && areaTreeH > 0) {\n      entityRenderer.drawTileTrees(batch, world, areaTreePresentBits, areaTreeCutBits, areaTreeW, areaTreeH,\n          cam.position.x, cam.position.y, r,\n          true, false,\n          !TILE_TREES_FELL_ON_HARVEST);\n    }\n    batch.end();\n\n    // ============================================================\n    // Story world: Fog of War overlay in the PLAYFIELD\n    // ============================================================\n    // Variant 2 (as requested):\n    // - Unknown = black\n    // - Explored = dark fog overlay (content stays visible)\n    // - Visible now = clear, with soft edge\n    if (AREA_MODE && shape != null) {\n      renderFogOfWarOverlayWorld();\n    }\n\n    // ============================================================\n    // Area bounds: red border veil (10 tiles wide)\n    // ============================================================\n    if (AREA_MODE && shape != null && inRedZone) {\n      try {\n        shape.setProjectionMatrix(cam.combined);\n        shape.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);\n        shape.setColor(1f, 0f, 0f, 0.22f);\n\n        float tw = TILE_WORLD;\n        float w = AREA_W_TILES * tw;\n        float h = AREA_H_TILES * tw;\n        float rz = RED_ZONE_TILES * tw;\n\n        shape.rect(0f, 0f, rz, h);\n        shape.rect(w - rz, 0f, rz, h);\n        shape.rect(0f, 0f, w, rz);\n        shape.rect(0f, h - rz, w, rz);\n\n        shape.end();\n      } catch (Throwable ignored) {\n        try { shape.end(); } catch (Throwable ignored2) {}\n      }\n    }\n\n    // World overlays (health bars + projectiles)\n    combatController.renderWorldOverlays();\n\n    // Custom cursor (unarmed): visible point cursor, hardware cursor is hidden.\n    if (drawUnarmedDotCursor) {\n      cursorController.renderWorldCursor(mouseWorldX, mouseWorldY);\n    }\n  }\n  void renderUI() {\n    // UI overlay (screen space) — keep it independent from world camera\n    batch.setProjectionMatrix(uiCam.combined);\n    batch.begin();\n    batch.setColor(1f, 1f, 1f, 1f);\n\n    // UI only (no text HUD): draw compact stat panel bottom-left.\n    drawStatsPanel();\n\n    // ============================================================\n    // Area bounds warning UI (FUSA)\n    // ============================================================\n    if (AREA_MODE) {\n      if (inRedZone) {\n        font.setColor(1f, 0.2f, 0.2f, 1f);\n        font.getData().setScale(1.0f * UI_FONT_SCALE);\n        font.draw(batch, "Gebietsende, wechseln oder zurück gehen!", 20, Gdx.graphics.getHeight() - 80);\n        font.setColor(0f, 0f, 0f, 1f);\n      }\n      // VOID removed: player cannot enter the black outside-of-area strip.\n\n      // Direction arrows (only while in red zone; alpha: all sides are exits)\n      if (inRedZone) {\n        try {\n          com.badlogic.gdx.math.Vector3 v = new com.badlogic.gdx.math.Vector3(px, py, 0f);\n          cam.project(v);\n          float sy = Math.max(24f, Math.min(Gdx.graphics.getHeight() - 24f, v.y));\n\n          int tx = (int) Math.floor(px / World.TILE_WORLD);\n          int ty = (int) Math.floor(py / World.TILE_WORLD);\n          int w = AREA_W_TILES;\n          int h = AREA_H_TILES;\n          int rz = RED_ZONE_TILES;\n\n          boolean exitN = true, exitE = true, exitS = true, exitW = true;\n\n          font.setColor(1f, 1f, 1f, 1f);\n          font.getData().setScale(1.1f * UI_FONT_SCALE);\n\n          if (exitW && tx < rz) font.draw(batch, "<", 10, sy);\n          if (exitE && tx >= (w - rz)) font.draw(batch, ">", Gdx.graphics.getWidth() - 20, sy);\n          if (exitS && ty < rz) font.draw(batch, "v", (Gdx.graphics.getWidth() * 0.5f), 28);\n          if (exitN && ty >= (h - rz)) font.draw(batch, "^", (Gdx.graphics.getWidth() * 0.5f), Gdx.graphics.getHeight() - 28);\n\n          font.setColor(0f, 0f, 0f, 1f);\n        } catch (Throwable ignored) {}\n      }\n    }\n\n    // ============================================================\n    // WorldMap UI (Shift+M) (FUSA)\n    // ============================================================\n    if (mapOpen) {\n      // Update transition\n      if (mapTransActive) {\n        mapTransT += Gdx.graphics.getDeltaTime() / 0.25f; // 250ms\n        if (mapTransT >= 1f) {\n          mapTransT = 1f;\n          mapTransActive = false;\n          mapViewMode = mapTransTo;\n// Nicht fertiges Feature:           mapZoomOpen = (mapViewMode == 1);\n        }\n      }\n\n      // Render either world overview or area map (with optional transition).\n      float t = mapTransActive ? smooth01(mapTransT) : 1f;\n\n      if (mapTransActive) {\n        // Crossfade+zoom between two modes.\n        if (mapTransFrom == 0 && mapTransTo == 1) {\n          drawWorldMapOverview(1f - t, 1.0f + 0.30f * t);\n          drawSelectedAreaMap(t, 0.85f + 0.15f * t);\n        } else if (mapTransFrom == 1 && mapTransTo == 0) {\n          drawSelectedAreaMap(1f - t, 1.0f + 0.30f * t);\n          drawWorldMapOverview(t, 0.85f + 0.15f * t);\n        }\n      } else {\n        if (mapViewMode == 0) {\n          drawWorldMapOverview(1f, 1f);\n        } else {\n          drawSelectedAreaMap(1f, 1f);\n        }\n      }\n    }\n\n    // Toast stays (short, contextual).
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 171-171

`java
\n  private void tickBootOverlay(float delta) {\n    if (!bootOverlayActive) {\n      bootOverlayControl = true;\n      bootOverlayFill = 1f;\n      bootOverlayA = 0f;\n      return;\n    }\n\n    // Fill phase\n    bootOverlayT += delta;\n    float f = bootOverlayT / BOOT_OVERLAY_FILL_SEC;\n    if (f < 0f) f = 0f;\n    if (f > 1f) f = 1f;\n    bootOverlayFill = f;\n\n    // Fade-out phase after fully filled\n    if (bootOverlayFill >= 0.999f) {\n      bootOverlayFadeT += delta;\n      float k = 1f - (bootOverlayFadeT / BOOT_OVERLAY_FADE_SEC);\n      if (k < 0f) k = 0f;\n      if (k > 1f) k = 1f;\n      bootOverlayA = k;\n\n      if (bootOverlayFadeT >= BOOT_OVERLAY_FADE_SEC || bootOverlayA <= 0.001f) {\n        bootOverlayActive = false;\n        bootOverlayControl = true;\n        bootOverlayFill = 1f;\n        bootOverlayA = 0f;\n      }\n    } else {\n      bootOverlayA = 1f;\n      bootOverlayControl = false;\n      bootOverlayFadeT = 0f;\n    }\n  }\n\n  private void updateDayNightMusic(float delta) {\n    // Rules:\n    // - Crossfade is ONLY allowed on the day<->night switch.\n    // - The switch happens once when dnVisual crosses the midpoint (50%).\n\n    // Midpoint side (>= 0.5 = day side)\n    boolean sideDay = dnVisual >= DN_MIDPOINT;\n\n    // Start a switch only when the side changes (once per cycle)\n    if (sideDay != dnSideDay) {\n      dnSideDay = sideDay;\n      dnSwitching = true;\n      dnSwitchT = 0f;\n      dnSwitchToDay = sideDay;\n    }\n\n    float targetDay;\n    float targetNight;\n\n    if (dnSwitching) {\n      dnSwitchT += delta;\n      float a = dnSwitchT / DN_SWITCH_SEC;\n      if (a < 0f) a = 0f;\n      if (a > 1f) a = 1f;\n\n      if (dnSwitchToDay) {\n        // night -> day\n        targetDay = a;\n        targetNight = 1f - a;\n      } else {\n        // day -> night\n        targetDay = 1f - a;\n        targetNight = a;\n      }\n\n      if (a >= 1f) {\n        dnSwitching = false;\n      }\n    } else {\n      targetDay = sideDay ? 1f : 0f;\n      targetNight = sideDay ? 0f : 1f;\n    }\n\n    // Ensure required tracks are playing.\n    if (dnSwitching) {\n      ensureDayBgPlaying();\n      ensureNightBgPlaying();\n    } else {\n      if (targetDay > 0.5f) ensureDayBgPlaying();\n      if (targetNight > 0.5f) ensureNightBgPlaying();\n    }\n\n    // Track fade-in (for playlist transitions; no cross-over between tracks)\n    if (dayBgMusic != null) {\n      dayTrackFadeInT += delta;\n      if (dayTrackFadeInT > TRACK_FADE_IN_SEC) dayTrackFadeInT = TRACK_FADE_IN_SEC;\n    }\n    if (nightBgMusic != null) {\n      nightTrackFadeInT += delta;\n      if (nightTrackFadeInT > TRACK_FADE_IN_SEC) nightTrackFadeInT = TRACK_FADE_IN_SEC;\n    }\n\n    float inDay = dayTrackFadeInT / TRACK_FADE_IN_SEC;\n    float inNight = nightTrackFadeInT / TRACK_FADE_IN_SEC;\n    if (inDay < 0f) inDay = 0f;\n    if (inDay > 1f) inDay = 1f;\n    if (inNight < 0f) inNight = 0f;\n    if (inNight > 1f) inNight = 1f;\n\n    dayBgVol = targetDay;\n    nightBgVol = targetNight;\n\n    float base = game.settings.masterVolume * game.settings.musicVolume;\n    if (base < 0f) base = 0f;\n    if (base > 1f) base = 1f;\n\n    if (dayBgMusic != null) {\n      float v = dayBgVol * base * inDay;\n      dayBgMusic.setVolume(v);\n      if (!dnSwitching && dayBgVol <= 0.001f) {\n        try { dayBgMusic.pause(); } catch (Throwable ignored) {}\n      }\n    }\n\n    if (nightBgMusic != null) {\n      float v = nightBgVol * base * inNight;\n      nightBgMusic.setVolume(v);\n      if (!dnSwitching && nightBgVol <= 0.001f) {\n        try { nightBgMusic.pause(); } catch (Throwable ignored) {}\n      }\n    }\n  }\n\n  private void updateDnMask() {\n    dnLampScreenValid = false;\n\n    // Only build mask when lamp is on and it's not full day.\n    if (!dnLightOn && !dnLightXLOn) return;\n    float nightFactor = 1f - dnVisual;\n    if (nightFactor <= 0.02f) return;\n\n    if (dnMaskFbo == null || dnMaskRegion == null) {\n      rebuildDnMaskFbo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n      if (dnMaskFbo == null || dnMaskRegion == null) return;\n    }\n\n    // Compute lamp screen position (player + offset toward mouse)\n    Vector3 sp = new Vector3(px, py, 0f);\n    cam.project(sp);\n\n    float dx = mouseWorldX - px;\n    float dy = mouseWorldY - py;\n    float len = (float)Math.sqrt(dx * dx + dy * dy);\n    float ox = 0f, oy = 0f;\n    if (len > 0.001f) {\n      ox = (dx / len) * 30f;\n      oy = (dy / len) * 30f;\n    }\n\n    dnLampScreenX = sp.x + ox;\n    dnLampScreenY = sp.y + oy;\n    dnLampScreenValid = true;\n\n    // Render mask into FBO: start with alpha=1 everywhere, then reduce alpha around lamp.\n    dnMaskFbo.begin();\n    Gdx.gl.glClearColor(1f, 1f, 1f, 1f);\n    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);\n\n    if (dnLightTex != null) {\n      batch.setProjectionMatrix(uiCam.combined);\n      batch.begin();\n\n      // Only affect ALPHA: RGB stays 1.0 so the texture doesn't tint the darkness color.\n      batch.flush();\n      batch.setBlendFunctionSeparate(GL20.GL_ZERO, GL20.GL_ONE, GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);\n\n      // Fixed tuned strength (alpha reduction) scaled by nightFactor.\n      float strength = 0.2625f * nightFactor;\n      if (strength < 0f) strength = 0f;\n      if (strength > 1f) strength = 1f;\n\n      if (dnLightOn) {\n        float radiusPx = 300f;\n        float size = radiusPx * 2f;\n        batch.setColor(1f, 1f, 1f, strength);\n        batch.draw(dnLightTex, dnLampScreenX - radiusPx, dnLampScreenY - radiusPx, size, size);\n      }\n\n      if (dnLightXLOn) {\n        // Exact copy of lamp, but +300% size => 4x radius (300 -> 1200)\n        float radiusPx = 1200f;\n        float size = radiusPx * 2f;\n        batch.setColor(1f, 1f, 1f, strength);\n        batch.draw(dnLightTex, dnLampScreenX - radiusPx, dnLampScreenY - radiusPx, size, size);\n      }\n\n      batch.flush();\n      batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);\n      batch.setColor(1f, 1f, 1f, 1f);\n\n      batch.end();\n    }\n\n    dnMaskFbo.end();\n  }\n\n  private void rebuildDnMaskFbo(int w, int h) {\n    try { if (dnMaskFbo != null) dnMaskFbo.dispose(); } catch (Throwable ignored) {}\n    dnMaskFbo = null;\n    dnMaskRegion = null;\n\n    if (w <= 0 || h <= 0) return;\n\n    try {\n      dnMaskFbo = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);\n      Texture t = dnMaskFbo.getColorBufferTexture();\n      dnMaskRegion = new TextureRegion(t);\n      dnMaskRegion.flip(false, true); // FBO texture is Y-flipped\n    } catch (Throwable ignored) {\n      dnMaskFbo = null;\n      dnMaskRegion = null;\n    }\n  }\n\n  private void ensureDayBgPlaying() {\n    if (dayBgTracks.length == 0) return;\n    if (dayBgMusic == null) {\n      playDayBg(true);\n      return;\n    }\n    try {\n      if (!dayBgMusic.isPlaying()) dayBgMusic.play();\n    } catch (Throwable ignored) {}\n  }\n\n  private void ensureNightBgPlaying() {\n    if (nightBgTracks.length == 0) return;\n    if (nightBgMusic == null) {\n      playNightBg(true);\n      return;\n    }\n    try {\n      if (!nightBgMusic.isPlaying()) nightBgMusic.play();\n    } catch (Throwable ignored) {}\n  }\n\n  private void playDayBg(boolean fromStart) {\n    if (dayBgTracks.length == 0) return;\n    if (dayBgIdx < 0 || dayBgIdx >= dayBgTracks.length) dayBgIdx = 0;\n\n    try { if (dayBgMusic != null) dayBgMusic.dispose(); } catch (Throwable ignored) {}\n    dayBgMusic = null;\n\n    String p = dayBgTracks[dayBgIdx];\n    try {\n      FileHandle fh = Gdx.files.absolute(p);\n      dayBgMusic = Gdx.audio.newMusic(fh);\n      dayBgMusic.setLooping(false);\n      float base = game.settings.masterVolume * game.settings.musicVolume;\n      if (base < 0f) base = 0f;\n      if (base > 1f) base = 1f;\n      dayTrackFadeInT = 0f;\n      dayBgMusic.setVolume(0f * base);\n      dayBgMusic.setOnCompletionListener(m -> {\n        dayBgIdx = (dayBgIdx + 1) % dayBgTracks.length;\n        playDayBg(true);\n      });\n      if (fromStart) dayBgMusic.setPosition(0f);\n      dayBgMusic.play();\n    } catch (Throwable ignored) {\n      dayBgMusic = null;\n    }\n  }\n\n  private void playNightBg(boolean fromStart) {\n    if (nightBgTracks.length == 0) return;\n    if (nightBgIdx < 0 || nightBgIdx >= nightBgTracks.length) nightBgIdx = 0;\n\n    try { if (nightBgMusic != null) nightBgMusic.dispose(); } catch (Throwable ignored) {}\n    nightBgMusic = null;\n\n    String p = nightBgTracks[nightBgIdx];\n    try {\n      FileHandle fh = Gdx.files.absolute(p);\n      nightBgMusic = Gdx.audio.newMusic(fh);\n      nightBgMusic.setLooping(false);\n      float base = game.settings.masterVolume * game.settings.musicVolume;\n      if (base < 0f) base = 0f;\n      if (base > 1f) base = 1f;\n      nightTrackFadeInT = 0f;\n      nightBgMusic.setVolume(0f * base);\n      nightBgMusic.setOnCompletionListener(m -> {\n        nightBgIdx = (nightBgIdx + 1) % nightBgTracks.length;\n        playNightBg(true);\n      });\n      if (fromStart) nightBgMusic.setPosition(0f);\n      nightBgMusic.play();\n    } catch (Throwable ignored) {\n      nightBgMusic = null;\n    }\n  }\n\n  @SuppressWarnings("unused")\n  private void collectInputSnapshot(float delta, InputState out) {\n    // Block 8 scaffolding: a single place to sample inputs and (later) produce commands.\n    // Keep it minimal for now (behavior stays as-is).\n\n    // Nicht fertiges Feature: sampled input values are currently never read\n    // out.delta = delta;\n    // out.mx = Gdx.input.getX();\n    // out.my = Gdx.input.getY();\n\n    // Command queue is currently unused (gameplay still executes immediately), but we clear it\n    // so it can be safely filled later without leaking old data.\n    commandQueue.clear();\n  }\n\n  @SuppressWarnings("unused")\n  private void applyCommands(CommandQueue commands) {\n    // Block 8 scaffolding: command queue exists for future deterministic simulation.\n    // For now, gameplay is still executed immediately at input sites.\n  }\n\n  /** Base action radius (coupled for mouse constraint + interaction reach). Skill hook goes here. */\n  private float actionReach(int equippedItemId) {\n    // NOTE: keep values centralized so we can individualize tools/weapons later.\n\n    float base = ACTION_RADIUS_DEFAULT;\n    if (equippedItemId < 0) {\n      // Unarmed: affected by CombatMelee range.\n      int meleeLv = (SK_COMBAT_MELEE >= 0 && SK_COMBAT_MELEE < progress.skillLv.length) ? progress.skillLv[SK_COMBAT_MELEE] : 1;\n      float mul = SkillEffects.mul5(meleeLv);\n      return base * mul;\n    }\n\n    // For now: everything is clamped to the same ring radius, but per-item (not a global toggle).\n    float r = switch (equippedItemId) {\n      // Tools\n      case 14, 15, 16, 17, 18, 19, 36 -> base;\n\n      // Weapons\n      case 20, 21, 22, 23, 24, 25, 26, 27 -> base;\n\n      default -> base;\n    };\n\n    // Requested: CombatMelee increases action zone / reach especially for fist + knife.\n    if (equippedItemId == 23) { // dagger as "messer"\n      int meleeLv = (SK_COMBAT_MELEE >= 0 && SK_COMBAT_MELEE < progress.skillLv.length) ? progress.skillLv[SK_COMBAT_MELEE] : 1;\n      float mul = SkillEffects.mul5(meleeLv);\n      r *= mul;\n    }\n\n    return r;\n  }\n\n  /** Tools/weapons are only usable when they're on the hotbar AND you actually own at least 1 in inventory. */\n  private int equippedFromHotbar() {\n    if (hotbarSel < 0 || hotbarSel >= hotbar.length) return -1;\n    int id = hotbar[hotbarSel];\n    if (id < 0) return -1;\n    if (!inv.isToolItem(id)) return -1;\n    if (!inv.hasItem(id)) return -1;\n    return id;\n  }\n\n  private static void facingVec(byte facingDir, Vector2 out) {\n    switch (facingDir) {\n      case 0 -> { out.x = 0f; out.y = 1f; }  // N\n      case 1 -> { out.x = 1f; out.y = 0f; }  // E\n      case 2 -> { out.x = 0f; out.y = -1f; } // S\n      default -> { out.x = -1f; out.y = 0f; } // W\n    }\n  }\n\n  private static boolean isInsideFov(float ox, float oy, float tx, float ty, float fwdX, float fwdY, float fovDeg) {\n    float dx = tx - ox;\n    float dy = ty - oy;\n    float len2 = dx*dx + dy*dy;\n    if (len2 <= 1e-6f) return true;\n\n    float inv = (float)(1.0 / Math.sqrt(len2));\n    float ax = dx * inv;\n    float ay = dy * inv;\n\n    float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));\n    return (fwdX * ax + fwdY * ay) >= cosHalf;\n  }\n\n  private static final int REMOVED_KIND_MINED_ROCK_TILE = 255;\n\n  /** Pickaxe-only: mine a ROCK ground tile into GRASS and drop 1x Stone. */\n  private boolean tryMineRockTile(float aimWx, float aimWy, float range, int toolItemId) {\n    if (toolItemId != 15) return false; // Pickaxe only\n    if (world == null) return false;\n\n    // Must be in reach.\n    float dx = aimWx - px;\n    float dy = aimWy - py;\n    if (dx * dx + dy * dy > range * range) return false;\n\n    // Tile coords\n    int tx = (int) Math.floor(aimWx / World.TILE_WORLD);\n    int ty = (int) Math.floor(aimWy / World.TILE_WORLD);\n\n    // Areas-only mode uses non-negative tiles; keep it safe anyway.\n    if (tx < 0 || ty < 0) return false;\n\n    int cx = tx / World.CHUNK_SIZE;\n    int cy = ty / World.CHUNK_SIZE;\n\n    // Do NOT generate chunks while mining.\n    Chunk c = world.peekChunk(cx, cy);\n    if (c == null || c.layers == null) return false;\n\n    int lx = tx - cx * World.CHUNK_SIZE;\n    int ly = ty - cy * World.CHUNK_SIZE;\n    if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) return false;\n    int idx = lx + ly * World.CHUNK_SIZE;\n\n    if (c.layers.groundId[idx] != TileIds.GROUND_ROCK) return false;\n\n    // Mine it: rock -> grass, remove collision.\n    c.layers.groundId[idx] = TileIds.GROUND_GRASS;\n    c.layers.collisionMask[idx] = 0;\n\n    // Persist: remember this tile was mined (saved via SaveManager's removedNodes).\n    worldNodes.removed.add(WorldNodes.keyRaw(REMOVED_KIND_MINED_ROCK_TILE, tx, ty));\n\n    // 1 stone per tile.\n    float dropX = (tx + 0.5f) * World.TILE_WORLD;\n    float dropY = (ty + 0.5f) * World.TILE_WORLD;\n    entities.spawnDrop(1, 1, dropX, dropY); // itemId 1 = Stone\n    game.audio.sfx("audio/sfx/pickup.wav", game.audio.sfxVolume(game.settings));\n    return true;\n  }\n\n  /** Re-apply mined rock tiles after loading a save (world was rebuilt). */\n  private void applyMinedRockTilesFromSave() {\n    try {\n      if (world == null || worldNodes == null || worldNodes.removed == null) return;\n      com.badlogic.gdx.utils.LongArray ks = worldNodes.removed.keys();\n      if (ks == null) return;\n\n      for (int i = 0; i < ks.size; i++) {\n        long k = ks.get(i);\n        if (WorldNodes.rawTypeId(k) != REMOVED_KIND_MINED_ROCK_TILE) continue;\n        int tx = WorldNodes.rawTx(k);\n        int ty = WorldNodes.rawTy(k);\n        if (tx < 0 || ty < 0) continue;\n\n        int cx = tx / World.CHUNK_SIZE;\n        int cy = ty / World.CHUNK_SIZE;\n        Chunk c = world.chunk(cx, cy); // OK to create on load/apply\n        int lx = tx - cx * World.CHUNK_SIZE;\n        int ly = ty - cy * World.CHUNK_SIZE;\n        if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) continue;\n        int idx = lx + ly * World.CHUNK_SIZE;\n        if (c.layers == null) continue;\n\n        c.layers.groundId[idx] = TileIds.GROUND_GRASS;\n        c.layers.collisionMask[idx] = 0;\n      }\n    } catch (Throwable ignored) {}\n  }\n\n  /** Updates mouseWorldX/Y and optionally clamps the OS cursor to the action ring (player-centered). */\n  private void updateMouseWorld(boolean clampToRing) {\n    Vector3 v = scratch.v3a;\n    v.set(Gdx.input.getX(), Gdx.input.getY(), 0f);\n    cam.unproject(v);\n\n    float wx = v.x;\n    float wy = v.y;\n\n    if (clampToRing) {\n      float r = actionReach(equippedFromHotbar());\n      float dx = wx - px;\n      float dy = wy - py;\n      float d2 = dx*dx + dy*dy;\n      float r2 = r * r;\n      if (d2 > r2 && d2 > 1e-6f) {\n        float invLen = (float)(1.0 / Math.sqrt(d2));\n        wx = px + dx * invLen * r;\n        wy = py + dy * invLen * r;\n\n        // Warp cursor back onto the ring so the player can never "aim" outside.\n        Vector3 p = scratch.v3b;\n        p.set(wx, wy, 0f);\n        cam.project(p);\n        int sx = (int)p.x;\n        int sy = (int)(Gdx.graphics.getHeight() - p.y);\n        Gdx.input.setCursorPosition(sx, sy);\n      }\n    }\n\n    mouseWorldX = wx;\n    mouseWorldY = wy;\n  }\n  void drawEntityHealthBarsWorld() {\n    // Draw simple health bars + numeric hp over each entity.\n    shape.setProjectionMatrix(cam.combined);\n\n    Gdx.gl.glEnable(GL20.GL_BLEND);\n    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);\n\n    // 1) bars (filled)\n    shape.begin(ShapeRenderer.ShapeType.Filled);\n\n    // Tile-tree harvest HP bar (AREA_MODE)\n    try {\n      if (AREA_MODE && tileTreeHitUiT > 0f && tileTreeHitHp > 0f && tileTreeHitTx >= 0 && tileTreeHitTy >= 0) {\n        float hpMax = 20f;\n        float p = MathUtils.clamp(tileTreeHitHp / hpMax, 0f, 1f);\n\n        float x = (tileTreeHitTx + 0.5f) * World.TILE_WORLD;\n        float y = (tileTreeHitTy * World.TILE_WORLD) + 34.0f;\n        float w = EntityMetrics.drawW(EntityType.NODE_TREE);\n        float h = EntityMetrics.drawH(EntityType.NODE_TREE);\n\n        float barW = Math.max(10f, w * 0.85f);\n        float barH = 3f;\n        float bx = x - barW * 0.5f;\n        float by = y + h * 0.5f + 6f;\n\n        shape.setColor(0f, 0f, 0f, 0.65f);\n        shape.rect(bx - 1f, by - 1f, barW + 2f, barH + 2f);\n\n        float rr = (1f - p);\n        float gg = p;\n        shape.setColor(rr, gg, 0.1f, 0.90f);\n        shape.rect(bx, by, barW * p, barH);\n      }\n    } catch (Throwable ignored) {}\n\n    for (int e = 0; e < Entities.MAX; e++) {\n      if (!entities.alive[e]) continue;\n      float hpMax = entities.hpMax[e];\n      if (hpMax <= 0f) continue;\n\n      // Only moving entities should show health bars (player always shows).\n      // Exception: show bars for damaged orcs/deer even when idle (so combat feedback is reliable).\n      EntityType t0 = entities.type[e];\n      if (t0 != EntityType.PLAYER) {\n        float mv2 = entities.vx[e] * entities.vx[e] + entities.vy[e] * entities.vy[e];\n        boolean damaged = entities.hp[e] < entities.hpMax[e] - 0.01f;\n        boolean important = (t0 == EntityType.ORK_GRUNT || t0 == EntityType.ANIMAL_DEER);\n        if (!damaged && !important) {\n          if (mv2 <= (5f * 5f)) continue;\n        }\n      }\n\n      float hp = Math.max(0f, entities.hp[e]);\n      float p = MathUtils.clamp(hp / hpMax, 0f, 1f);\n\n      EntityType t = entities.type[e];\n      float w = EntityMetrics.drawW(t);\n      float h = EntityMetrics.drawH(t);\n\n      float x = entities.x[e];\n      float y = entities.y[e];\n\n      // Only show bars when reasonably near the camera/player to reduce clutter.\n      if (Math.abs(x - px) > 700f || Math.abs(y - py) > 520f) continue;\n\n      float barW = Math.max(10f, w * 0.85f * 1.15f); // +15% wider\n      float barH = (t == EntityType.ANIMAL_DEER) ? 2f : 3f; // deer: extra slim\n      float bx = x - barW * 0.5f;\n      float by = y + h * 0.5f + 6f;\n\n      // background\n      shape.setColor(0f, 0f, 0f, 0.65f);\n      shape.rect(bx - 1f, by - 1f, barW + 2f, barH + 2f);\n\n      // fill (green->red)\n      float r = (1f - p);\n      float g = p;\n      shape.setColor(r, g, 0.1f, 0.90f);\n      shape.rect(bx, by, barW * p, barH);\n    }\n    shape.end();\n\n    // 2) numeric hp text (disabled: bars only)\n    // Intentionally not drawing numbers to reduce clutter.\n\n    Gdx.gl.glDisable(GL20.GL_BLEND);\n  }\n\n  private boolean fireBowArrow(\n      float ox, float oy,\n      float aimX, float aimY,\n      float fwdX, float fwdY,\n      float fovDeg,\n      float speed,\n      float maxRange,\n      float dmg,\n      float spreadRad\n  ) {\n    // Aim dir\n    float dx = aimX - ox;\n    float dy = aimY - oy;\n    float len2 = dx * dx + dy * dy;\n    if (len2 <= 1e-6f) return false;\n\n    float invLen = (float) (1.0 / Math.sqrt(len2));\n    float ax = dx * invLen;\n    float ay = dy * invLen;\n\n    // FOV check against facing\n    float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));\n    if (fwdX * ax + fwdY * ay < cosHalf) return false;\n\n    // Spread (precision): rotate by random angle in [-spreadRad, +spreadRad]\n    if (spreadRad > 1e-6f) {\n      float ang = (MathUtils.random() * 2f - 1f) * spreadRad;\n      float c = (float) Math.cos(ang);\n      float s = (float) Math.sin(ang);\n      float rx = ax * c - ay * s;\n      float ry = ax * s + ay * c;\n      ax = rx;\n      ay = ry;\n    }\n\n    // Find free arrow slot\n    int slot = -1;\n    for (int i = 0; i < ARROW_MAX; i++) {\n      if (!arrowAlive[i]) { slot = i; break; }\n    }\n    if (slot < 0) return true; // silently drop (no alloc)\n\n    arrowAlive[slot] = true;\n    arrowX[slot] = ox;\n    arrowY[slot] = oy;\n    arrowVx[slot] = ax * speed;\n    arrowVy[slot] = ay * speed;\n    arrowTravel[slot] = 0f;\n    arrowRange[slot] = maxRange;\n    arrowDmg[slot] = dmg;\n    return true;\n  }\n\n  private void tickArrows(float dt) {\n    // Sub-step to avoid tunneling at high arrow speed.\n    final float maxStep = 14f;\n\n    for (int i = 0; i < ARROW_MAX; i++) {\n      if (!arrowAlive[i]) continue;\n\n      float x = arrowX[i];\n      float y = arrowY[i];\n\n      float vx = arrowVx[i];\n      float vy = arrowVy[i];\n      float spd = (float) Math.sqrt(vx * vx + vy * vy);\n      if (spd <= 1e-6f) { arrowAlive[i] = false; continue; }\n\n      float total = spd * dt;\n      int steps = Math.max(1, (int) Math.ceil(total / maxStep));\n      float sdt = dt / steps;\n\n      for (int s = 0; s < steps; s++) {\n        float x2 = x + vx * sdt;\n        float y2 = y + vy * sdt;\n\n        arrowLastKill = null;\n        int hitE = arrowHitSegment(x, y, x2, y2, arrowDmg[i]);\n        if (hitE >= 0) {\n          // Arrow disappears on ANY hit (even if target survives)\n          arrowAlive[i] = false;\n          game.audio.sfx("audio/sfx/ork_hit.wav", game.audio.sfxVolume(game.settings));\n          if (arrowLastKill != null) {\n            onKill(arrowLastKill);\n          }\n          break;\n        }\n\n        // advance\n        arrowTravel[i] += spd * sdt;\n        if (arrowTravel[i] >= arrowRange[i]) {\n          arrowAlive[i] = false;\n          break;\n        }\n\n        x = x2;\n        y = y2;\n      }\n\n      if (!arrowAlive[i]) continue;\n      arrowX[i] = x;\n      arrowY[i] = y;\n    }\n  }\n\n  private CombatSystem.Kill arrowLastKill = null;\n\n  /**\n   * @return hit entity index, or -1 if no hit. If a kill happens, arrowLastKill is set.\n   */\n  private int arrowHitSegment(float x0, float y0, float x1, float y1, float dmg) {\n    // Visual-only arrows use dmg=0; they must never collide with local mobs.\n    if (dmg <= 0f) return -1;\n    // segment direction + length\n    float dx = x1 - x0;\n    float dy = y1 - y0;\n    float len2 = dx * dx + dy * dy;\n    if (len2 <= 1e-6f) return -1;\n\n    float invLen = (float) (1.0 / Math.sqrt(len2));\n    float ax = dx * invLen;\n    float ay = dy * invLen;\n    float segLen = (float) Math.sqrt(len2);\n\n    int best = -1;\n    float bestT = Float.POSITIVE_INFINITY;\n\n    for (int e = 0; e < Entities.MAX; e++) {\n      if (!entities.alive[e]) continue;\n      EntityType t = entities.type[e];\n      if (t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;\n\n      float cx = entities.x[e] - x0;\n      float cy = entities.y[e] - y0;\n\n      float proj = cx * ax + cy * ay;\n      if (proj < 0f || proj > segLen) continue;\n\n      float closest2 = cx * cx + cy * cy - proj * proj;\n      // Effective hit radius = target body hit radius + projectile thickness.\n      float r = EntityMetrics.hitRadius(t) + ARROW_HIT_RADIUS;\n      if (closest2 > r * r) continue;\n\n      if (proj < bestT) { bestT = proj; best = e; }\n    }\n\n    if (best < 0) return -1;\n\n    EntityType t = entities.type[best];\n    entities.hp[best] -= dmg;\n    if (entities.hp[best] <= 0f) {\n      float ex = entities.x[best];\n      float ey = entities.y[best];\n      entities.kill(best);\n      arrowLastKill = new CombatSystem.Kill(t, ex, ey);\n    }\n\n    return best;\n  }\n  void drawArrowsWorld() {\n    // Minimal world-space arrow visualization (line). No ammo system.\n    shape.setProjectionMatrix(cam.combined);\n    Gdx.gl.glEnable(GL20.GL_BLEND);\n    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);\n\n    // Initial (small) projectile visualization\n    float tail = 10f;\n\n    shape.begin(ShapeRenderer.ShapeType.Line);\n    shape.setColor(0.95f, 0.95f, 0.95f, 0.95f);\n    for (int i = 0; i < ARROW_MAX; i++) {\n      if (!arrowAlive[i]) continue;\n      float x = arrowX[i];\n      float y = arrowY[i];\n      float vx = arrowVx[i];\n      float vy = arrowVy[i];\n      float invLen = 1.0f / Math.max(1e-6f, (float) Math.sqrt(vx * vx + vy * vy));\n      float nx = vx * invLen;\n      float ny = vy * invLen;\n      shape.line(x, y, x - nx * tail, y - ny * tail);\n    }\n    shape.end();\n\n    Gdx.gl.glDisable(GL20.GL_BLEND);\n  }\n\n  private void drawActionOverlay(float fwdX, float fwdY) {\n    float r = actionReach(equippedFromHotbar());\n\n    float baseAng = (float)Math.atan2(fwdY, fwdX);\n\n    float half = (float)Math.toRadians(ACTION_FOV_DEG * 0.5f);\n    float a0 = baseAng - half;\n    float a1 = baseAng + half;\n\n    // Filled FOV sector (blue, half-transparent)\n    Gdx.gl.glEnable(GL20.GL_BLEND);\n    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);\n\n    shape.setProjectionMatrix(cam.combined);\n    shape.begin(ShapeRenderer.ShapeType.Filled);\n    shape.setColor(new Color(0.10f, 0.55f, 1.0f, 0.35f));\n\n    int seg = 28; // smooth enough\n    float da = (a1 - a0) / seg;\n    for (int i=0;i<seg;i++) {\n      float aa0 = a0 + da * i;\n      float aa1 = a0 + da * (i+1);\n      float x0 = px + (float)Math.cos(aa0) * r;\n      float y0 = py + (float)Math.sin(aa0) * r;\n      float x1 = px + (float)Math.cos(aa1) * r;\n      float y1 = py + (float)Math.sin(aa1) * r;\n      shape.triangle(px, py, x0, y0, x1, y1);\n    }\n    shape.end();\n\n    // Action ring outline (red)\n    shape.begin(ShapeRenderer.ShapeType.Line);\n    shape.setColor(new Color(1f, 0.15f, 0.15f, 0.95f));\n    shape.circle(px, py, r, 64);\n    shape.end();\n\n    Gdx.gl.glDisable(GL20.GL_BLEND);\n  }\n  void drawDotCursorWorld(float x, float y) {\n    // White point cursor (visible, not tiny)\n    shape.setProjectionMatrix(cam.combined);\n\n    Gdx.gl.glEnable(GL20.GL_BLEND);\n    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);\n\n    float rOuter = 4.0f;\n    float rInner = 2.6f;\n\n    // outline\n    shape.begin(ShapeRenderer.ShapeType.Filled);\n    shape.setColor(0f, 0f, 0f, 0.85f);\n    shape.circle(x, y, rOuter, 18);\n    shape.setColor(1f, 1f, 1f, 0.95f);\n    shape.circle(x, y, rInner, 18);\n    shape.end();\n\n    Gdx.gl.glDisable(GL20.GL_BLEND);\n  }\n\n  /* ===== SIMULATION TICK ===== */\n  private void tick(float dt) {\n    // toast\n    if (toastT > 0f) toastT -= dt;\n\n    runtimeSec += dt;\n\n    if (tileTreeHitUiT > 0f) tileTreeHitUiT = Math.max(0f, tileTreeHitUiT - dt);\n\n    // Tile-tree streaming debug toggle (F10) is handled centrally via DebugCommands.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 171-171

`java
\n    // Streaming warmup: defer heavy chunk generation for a few frames right after entering gameplay.\n    if (streamWarmupFrames > 0) streamWarmupFrames--;\n    final int streamR = AREA_MODE ? 0 : ((streamWarmupFrames > 0) ? 0 : streamRadiusChunks);\n\n    // Player hand swing FX timer (purely visual; rendered by EntityRenderer).\n    if (playerE >= 0) {\n      // Reuse aiF0[] for PLAYER as "swing remaining seconds".\n      entities.aiF0[playerE] = Math.max(0f, entities.aiF0[playerE] - dt);\n    }\n\n    // Day/Night + needs\n    dayNight.tick(dt);\n\n    // Skill-based needs modifiers (cheap: just a few multipliers)\n    int healthLv = (SK_HEALTH >= 0 && SK_HEALTH < progress.skillLv.length) ? progress.skillLv[SK_HEALTH] : 1;\n    int staminaLv = (SK_STAMINA >= 0 && SK_STAMINA < progress.skillLv.length) ? progress.skillLv[SK_STAMINA] : 1;\n    int hungerCtrlLv = (SK_HUNGER_CTRL >= 0 && SK_HUNGER_CTRL < progress.skillLv.length) ? progress.skillLv[SK_HUNGER_CTRL] : 1;\n    int sleepCtrlLv = (SK_SLEEP_CTRL >= 0 && SK_SLEEP_CTRL < progress.skillLv.length) ? progress.skillLv[SK_SLEEP_CTRL] : 1;\n\n    // Align core baselines with current feel, but keep skill effects consistent.\n    final float HP_BASE = 500f;\n    final float STAM_BASE = 100f;\n    final float HUNGER_BASE = 200f;\n\n    needs.hpMax = HP_BASE * SkillEffects.mul5(healthLv);\n    needs.staminaMax = STAM_BASE * SkillEffects.mul5(staminaLv);\n    needs.hungerMax = HUNGER_BASE;\n\n    // New game: once per fresh start, snap to full needs after max values are defined.\n    // Requirement: must start at 100% hunger when starting a new game.\n    if (forceFullHpOnce) {\n      needs.hp = needs.hpMax;\n      needs.stamina = needs.staminaMax;\n      needs.mana = needs.manaMax;\n      needs.hunger = needs.hungerMax;\n      needs.sleep = needs.sleepMax;\n      forceFullHpOnce = false;\n    }\n\n    // Drains remain skill-modified (Singleplayer progression keeps working).\n    needs.hungerDrainMul = SkillEffects.drainMul5(hungerCtrlLv, 0.10f);\n    needs.sleepDrainMul = SkillEffects.drainMul5(sleepCtrlLv, 0.10f);\n\n    if (needs.hp > needs.hpMax) needs.hp = needs.hpMax;\n    if (needs.stamina > needs.staminaMax) needs.stamina = needs.staminaMax;\n\n    needs.tick(dt);\n\n    // Apply smooth food effects AFTER drains for this tick.\n    if (pendingFoodHunger > 0f || pendingFoodHp > 0f) {\n      final float HUNGER_FILL_PER_SEC = 38f; // 50 hunger in ~1.3s\n      final float HP_FILL_PER_SEC = 28f;     // 25 hp in <1s\n\n      // 1) Hunger first\n      if (pendingFoodHunger > 0f && needs.hunger < needs.hungerMax - 1e-3f) {\n        float want = Math.min(pendingFoodHunger, HUNGER_FILL_PER_SEC * dt);\n        float space = (needs.hungerMax - needs.hunger);\n        float give = Math.min(want, space);\n        needs.hunger += give;\n        pendingFoodHunger -= give;\n      }\n\n      // If hunger is full, discard any remaining hunger gain (can't apply) and allow HP heal.\n      if (needs.hunger >= needs.hungerMax - 1e-3f) {\n        pendingFoodHunger = 0f;\n      }\n\n      // 2) HP only after hunger is full\n      if (pendingFoodHp > 0f && needs.hunger >= needs.hungerMax - 1e-3f && needs.hp < needs.hpMax - 1e-3f) {\n        float want = Math.min(pendingFoodHp, HP_FILL_PER_SEC * dt);\n        float space = (needs.hpMax - needs.hp);\n        float give = Math.min(want, space);\n        needs.hp += give;\n        pendingFoodHp -= give;\n      }\n\n      if (pendingFoodHunger < 0f) pendingFoodHunger = 0f;\n      if (pendingFoodHp < 0f) pendingFoodHp = 0f;\n    }\n\n    // Respawn on death: always return to full HP + full hunger.\n    // Requirement: must respawn at 100% hunger.\n    if (needs.hp <= 0f) {\n      needs.hp = needs.hpMax;\n      needs.hunger = needs.hungerMax;\n      toast = "Respawn";\n      toastT = 1.6f;\n    }\n\n    // new day rollover => refresh wandering shop offers\n    if (dayNight.t < prevDayT) {\n      dayIndex++;\n      regenShopOffers();\n    }\n    prevDayT = dayNight.t;\n\n    // Biome damage (Cold/Heat) + mitigation (MUST NOT generate chunks)\n    //\n    // Requirement (FUSA Areas-only mode): biomes must not drive gameplay by default.\n    if (!AREA_MODE) {\n      int hereBiomeId = world.biomeIdAtWorldPeek(px, py, Biome.GRASSLAND.id & 0xff);\n      Biome hb = Biome.byId(hereBiomeId);\n\n      float coldDps = (hb == Biome.SNOWHIGHLAND) ? 0.35f : (hb == Biome.MOUNTAIN ? 0.15f : 0f);\n      float heatDps = (hb == Biome.VOLCANIC) ? 0.30f : (hb == Biome.ASHFIELD ? 0.12f : 0f);\n\n      if (coldDps > 0f) {\n        int coldLv = (SK_COLD_RES >= 0 && SK_COLD_RES < progress.skillLv.length) ? progress.skillLv[SK_COLD_RES] : 1;\n        float mul = SkillEffects.drainMul5(coldLv, 0.15f);\n        needs.hp -= coldDps * dt * mul;\n      }\n      if (heatDps > 0f) {\n        int heatLv = (SK_HEAT_RES >= 0 && SK_HEAT_RES < progress.skillLv.length) ? progress.skillLv[SK_HEAT_RES] : 1;\n        float mul = SkillEffects.drainMul5(heatLv, 0.15f);\n        needs.hp -= heatDps * dt * mul;\n      }\n      if (needs.hp < 0f) needs.hp = 0f;\n    }\n\n    float ix = 0f;\n    float iy = 0f;\n\n    // Player movement\n    // Requirement (WQG dialog): freeze player movement while the popup is open, but keep mouse free.\n    if (!questPopupOpen) {\n      if (Gdx.input.isKeyPressed(Input.Keys.W)) iy += 1f;\n      if (Gdx.input.isKeyPressed(Input.Keys.S)) iy -= 1f;\n      if (Gdx.input.isKeyPressed(Input.Keys.A)) ix -= 1f;\n      if (Gdx.input.isKeyPressed(Input.Keys.D)) ix += 1f;\n    }\n\n    // Diagonal movement: reduce only lateral (X) component for better control feel.\n    if (ix != 0f && iy != 0f) ix *= 0.70f;\n\n    // Movement speed tuning:\n    // Old values (Base 200 +60/level) were way too fast and felt like "perma sprint".\n    // Baseline movement speed (Navigation).\n    // Requested: normal walk speed at skill level 1 should be 60.\n    int speedLv = (SK_SPEED >= 0 && SK_SPEED < progress.skillLv.length) ? progress.skillLv[SK_SPEED] : 1;\n    float baseSpeed = 60f + 12f * Math.max(0, speedLv - 1);\n\n    // Sneak: hard override to 30 (toggle V)\n    if (sneaking) {\n      baseSpeed = 30f;\n    }\n\n    float speed = baseSpeed;\n\n    // Sprint: +5% over base. Additional +3% per level of the sprint-related skill (we use Stamina).\n    boolean sprintKey = (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));\n    boolean moving = (ix != 0f || iy != 0f);\n    boolean sprinting = (!sneaking && sprintKey && moving && needs.stamina > 0f);\n    if (sprinting) {\n      int sprintLv = (SK_SPRINTING >= 0 && SK_SPRINTING < progress.skillLv.length) ? progress.skillLv[SK_SPRINTING] : 1;\n      float sprintBonus = 0.05f + 0.03f * Math.max(0, sprintLv - 1);\n      speed *= (1f + sprintBonus);\n      needs.stamina = Math.max(0f, needs.stamina - 8.0f * dt);\n    }\n\n    // NOTE: traversal skills (Climbing/Boating) currently gate access, not movement speed.\n\n    // Local movement + collision.\n    // Smooth accel/brake (both sides): blend current velocity toward desired.\n    float desiredVx = ix * speed;\n    float desiredVy = iy * speed;\n    float accel = 15.0f;\n    float a = MathUtils.clamp(accel * dt, 0f, 1f);\n    playerVx = MathUtils.lerp(playerVx, desiredVx, a);\n    playerVy = MathUtils.lerp(playerVy, desiredVy, a);\n\n    float nx = px + playerVx * dt;\n    float ny = py + playerVy * dt;\n\n    // Story world (Areas-only): player must NOT leave the Area bounds.\n    // (No walking into the black VOID strip.)\n\n    // Collision constraints (MUST NOT generate chunks).\n    //\n    // Areas-only requirement:\n    // - No background generation.\n    // - Player movement is clamped to the Area bounds (no VOID walking).\n    //\n    // We use *peek* queries with safe defaults.\n    boolean coll;\n\n    // Areas-only: use *peek* queries.\n    // Outside loaded chunks => defaults are NOT blocking.\n    coll = world.isBlockedAtWorldPeek(nx, ny, false);\n\n    // DEBUG: optionally treat roadMask as blocking to visualize (in movement) where roads exist.\n    if (!coll && debugRoadBlocksMovement) {\n      int tx = (int) Math.floor(nx / World.TILE_WORLD);\n      int ty = (int) Math.floor(ny / World.TILE_WORLD);\n      Chunk c = world.peekChunk(tx / World.CHUNK_SIZE, ty / World.CHUNK_SIZE);\n      if (c != null && c.layers != null) {\n        int lx = tx - (tx / World.CHUNK_SIZE) * World.CHUNK_SIZE;\n        int ly = ty - (ty / World.CHUNK_SIZE) * World.CHUNK_SIZE;\n        if (lx >= 0 && ly >= 0 && lx < World.CHUNK_SIZE && ly < World.CHUNK_SIZE) {\n          int idx = lx + ly * World.CHUNK_SIZE;\n          if (c.layers.roadMask[idx] != 0) coll = true;\n        }\n      }\n    }\n\n    boolean blocked = false;\n    if (coll) blocked = true;\n\n    // Story world (Areas-only): hard block stepping outside the Area.\n    if (AREA_MODE) {\n      float tw = World.TILE_WORLD;\n      float areaW = AREA_W_TILES * tw;\n      float areaH = AREA_H_TILES * tw;\n      boolean outside = (nx < 0f || ny < 0f || nx >= areaW || ny >= areaH);\n      if (outside) blocked = true;\n    }\n    // Procedural biome constraints removed (Areas-only mode).\n\n    if (!blocked) {\n      px = nx;\n      py = ny;\n      // keep entity player position + motion hint synced\n      if (playerE >= 0) {\n        entities.x[playerE] = px;\n        entities.y[playerE] = py;\n        entities.vx[playerE] = playerVx;\n        entities.vy[playerE] = playerVy;\n        // Facing is controlled by mouse/FOV aim (not by movement direction).\n      }\n    } else {\n      // blocked: brake quickly\n      playerVx = MathUtils.lerp(playerVx, 0f, a);\n      playerVy = MathUtils.lerp(playerVy, 0f, a);\n      if (playerE >= 0) {\n        entities.vx[playerE] = playerVx;\n        entities.vy[playerE] = playerVy;\n      }\n    }\n\n    int ccx = (int) Math.floor((px / World.TILE_WORLD) / World.CHUNK_SIZE);\n    int ccy = (int) Math.floor((py / World.TILE_WORLD) / World.CHUNK_SIZE);\n\n    // In story-world (area) we still want ALL entities in the visible screen area rendered,\n    // so we use a view-based radius here.\n    int viewR = streamR;\n    if (AREA_MODE) {\n      float chunkWorld = World.TILE_WORLD * World.CHUNK_SIZE;\n      float halfW = (cam.viewportWidth * cam.zoom) * 0.5f;\n      float halfH = (cam.viewportHeight * cam.zoom) * 0.5f;\n      // Keep in sync with renderWorld() chunk draw radius (+margin), plus one extra chunk\n      // so tile-tree streaming doesn't lag behind camera motion.\n      int rx = (int) Math.ceil(halfW / chunkWorld) + 2;\n      int ry = (int) Math.ceil(halfH / chunkWorld) + 2;\n      viewR = Math.max(rx, ry) + 1;\n    }\n\n    loadedMinCx = ccx - viewR;\n    loadedMaxCx = ccx + viewR;\n    loadedMinCy = ccy - viewR;\n    loadedMaxCy = ccy + viewR;\n\n    // (Entity pool guardrails: tree entities are no longer spawned; drops are capped elsewhere if needed.)\n\n    // ============================================================\n    // Area bounds logic: red zone + void timer + penalties\n    // ============================================================\n    if (AREA_MODE) {\n      int tx = (int) Math.floor(px / World.TILE_WORLD);\n      int ty = (int) Math.floor(py / World.TILE_WORLD);\n\n      // Areas-only: player can never step into VOID.\n// Nicht fertiges Feature:       inVoid = false;\n// Nicht fertiges Feature:       voidTimer = 0f;\n// Nicht fertiges Feature:       voidStage = 0;\n\n      // Red zone warning: within N tiles of any area edge.\n      int w = AREA_W_TILES;\n      int h = AREA_H_TILES;\n      int rz = RED_ZONE_TILES;\n      int distL = tx;\n      int distR = (w - 1) - tx;\n      int distB = ty;\n      int distT = (h - 1) - ty;\n      int minD = Math.min(Math.min(distL, distR), Math.min(distB, distT));\n      inRedZone = (minD >= 0) && (minD < rz);\n\n      // Fog of War: persistently reveal explored area while playing.\n      areaFogRevealTick(dt);\n    } else {\n// Nicht fertiges Feature:       inVoid = false;\n      inRedZone = false;\n// Nicht fertiges Feature:       voidTimer = 0f;\n// Nicht fertiges Feature:       voidStage = 0;\n    }\n\n    // Areas-only: no background chunk streaming here.\n\n    // AI updates (slightly stronger at night)\n    int stealthLv = (SK_STEALTH >= 0 && SK_STEALTH < progress.skillLv.length) ? progress.skillLv[SK_STEALTH] : 1;\n    float stealthMul = SkillEffects.drainMul5(stealthLv, 0.25f);\n\n    int intLv = (SK_INTIMIDATION >= 0 && SK_INTIMIDATION < progress.skillLv.length) ? progress.skillLv[SK_INTIMIDATION] : 1;\n    float intimidateChance = Math.min(0.90f, 0.05f * Math.max(0, intLv - 1));\n\n    ai.tick(entities, playerE, world, loadedMinCx, loadedMaxCx, loadedMinCy, loadedMaxCy, dt, stealthMul, intimidateChance, sneaking, moving, sprinting);\n\n    // FUSA Story: Wander_Quest_Guy (HOME quest NPC)\n    // - Only relevant in Areas-only mode.\n    // - Ticked here so movement is included in collision resolution.\n    boolean isHomeArea = (worldMap != null && T_HOME.equals(worldMap.curTemplateId));
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 171-171

`java
    if (questGuy != null) {\n    // World freeze while WQG dialog is open (design requirement).\n    if (!questPopupOpen) {\n      float playerReach = actionReach(equippedFromHotbar());\n      questGuy.tick(entities, world, px, py, playerReach, dt, isHomeArea);\n    }\n    }\n\n    // Prevent player/orc/animal overlap (simple separation)\n    entityCollision.resolve(entities, loadedMinCx, loadedMaxCx, loadedMinCy, loadedMaxCy);\n\n    // Tile-tree trunk collision (AREA_MODE): prevent entities from sliding into the trunk while allowing\n    // walking "under" the canopy. (Stumps are excluded.)\n    if (AREA_MODE) {\n      resolveTileTreeTrunksInLoadedWindow();\n    }\n    if (playerE >= 0) {\n      px = entities.x[playerE];\n      py = entities.y[playerE];\n    }\n\n    // Auto-close merchant popup when leaving the action radius.\n    if (shopOpen && openMerchantE >= 0 && openMerchantE < Entities.MAX && entities.alive[openMerchantE]\n        && (entities.type[openMerchantE] == EntityType.MERCHANT_ELF || entities.type[openMerchantE] == EntityType.MERCHANT_WANDERING)) {\n      float eff = actionReach(equippedFromHotbar()) + EntityMetrics.radius(entities.type[openMerchantE]);\n      float dx = entities.x[openMerchantE] - px;\n      float dy = entities.y[openMerchantE] - py;\n      if (dx * dx + dy * dy > eff * eff) {\n        shopOpen = false;\n        openMerchantE = -1;\n        buyPopup = false;\n        buyBuffer = "";\n        dragArmed = false;\n        dragActive = false;\n      }\n    }\n\n    // Areas-only: no background procedural node spawning.\n\n    // Areas-only: merchant simulation/spawn removed from this tick path.\n    // TODO (story): if merchants exist in area JSON later, tick them here explicitly.\n\n    // Bow cooldown + arrows\n    if (bowCooldownT > 0f) bowCooldownT = Math.max(0f, bowCooldownT - dt);\n    tickArrows(dt);\n\n    // Areas-only: procedural encounter respawn removed.\n    // TODO (story): spawn encounters via area templates or scripted events.\n\n    // Authored enemy zones (FUSA Story)\n    areaEnemyZonesTick(dt);\n\n    // Authored tile-tree streaming (FUSA Story)\n    areaTileTreesTick(dt);\n  }\n\n  private static int clampInt(int v, int lo, int hi) {\n    if (v < lo) return lo;\n    if (v > hi) return hi;\n    return v;\n  }\n\n  /** Tile-tree harvest (AREA_MODE): apply DPS to the focused tree tile and produce a HarvestEvent when felled. */\n  private HarvestTick tryHarvestTileTreeFov(\n      float px, float py,\n      float aimX, float aimY,\n      float fwdX, float fwdY,\n      float fovDeg,\n      float range,\n      int toolItemId,\n      float dt\n  ) {\n    if (!AREA_MODE) return null;\n    if (toolItemId != 14) return null; // Axe only\n    if (dt <= 0f) return null;\n    if (areaTreePresentBits == null || areaTreeCutBits == null || areaTreeW <= 0 || areaTreeH <= 0) return null;\n\n    // FOV gate (same principle as HarvestSystem)\n    float adx = aimX - px;\n    float ady = aimY - py;\n    float al2 = adx * adx + ady * ady;\n    if (al2 <= 1e-6f) return null;\n    float ainv = (float) (1.0 / Math.sqrt(al2));\n    float ax = adx * ainv;\n    float ay = ady * ainv;\n    float cosHalf = (float) Math.cos(Math.toRadians(fovDeg * 0.5));\n    if (fwdX * ax + fwdY * ay < cosHalf) return null;\n\n    // Target tile at aim point.\n    int tx = (int) Math.floor(aimX / World.TILE_WORLD);\n    int ty = (int) Math.floor(aimY / World.TILE_WORLD);\n    if (tx < 0 || ty < 0 || tx >= areaTreeW || ty >= areaTreeH) return null;\n\n    int bit = bitIndex(tx, ty, areaTreeW);\n    if (!bitGet(areaTreePresentBits, bit)) return null;\n    if (TILE_TREES_FELL_ON_HARVEST && bitGet(areaTreeCutBits, bit)) return null;\n\n    // Cooldown: limit how often a single tile-tree can yield drops.\n    float nextAt = tileTreeNextHarvestAtSec.get(bit, -1f);\n    if (nextAt > 0f && runtimeSec < nextAt) return null;\n\n    // Range gate\n    float treeX = (tx + 0.5f) * World.TILE_WORLD;\n    float treeY = (ty * World.TILE_WORLD) + 34.0f;\n    float dx = treeX - px;\n    float dy = treeY - py;\n    float eff = range + EntityMetrics.radius(EntityType.NODE_TREE);\n    if (dx * dx + dy * dy > eff * eff) return null;\n\n    // "Below" + "near trunk" gate (mirrors HarvestSystem feel)\n    float treeW = EntityMetrics.drawW(EntityType.NODE_TREE);\n    float treeH = EntityMetrics.drawH(EntityType.NODE_TREE);\n    boolean below = py <= (treeY - treeH * 0.10f);\n    boolean nearTrunk = Math.abs(dx) <= (treeW * 0.35f);\n    if (!below || !nearTrunk) return null;\n\n    // DPS + HP model (simple, tile-local cache)\n    float dps = DPS_TREE_AXE;\n    float maxHp = 20f; // matches Entities.defaultHp(NODE_TREE)\n\n    if (tileTreeHitTx != tx || tileTreeHitTy != ty || tileTreeHitHp <= 0f) {\n      tileTreeHitTx = tx;\n      tileTreeHitTy = ty;\n      tileTreeHitHp = maxHp;\n    }\n\n    tileTreeHitHp -= dps * dt;\n    if (tileTreeHitHp > 0f) {\n      tileTreeHitUiT = 0.55f;\n      return new HarvestTick(true, null);\n    }\n\n    // Harvest complete:\n    // - If TILE_TREES_FELL_ON_HARVEST=false: tree stays, no stump, yields drops on cooldown.\n    // - If true: caller will mark cutBit and render stump.\n    tileTreeHitHp = 0f;\n    tileTreeHitUiT = 0.75f;\n    tileTreeNextHarvestAtSec.put(bit, runtimeSec + TILE_TREE_HARVEST_COOLDOWN_SEC);\n\n    HarvestEvent ev =\n        new HarvestEvent(0, 5, TILE_TREES_FELL_ON_HARVEST, EntityType.NODE_TREE, treeX, treeY);\n    return new HarvestTick(true, ev);\n  }\n\n  private void areaEnemyZonesTick(float dt) {\n    if (enemyZones.size == 0) return;\n\n    final float tw = World.TILE_WORLD;\n\n    for (int zi = 0; zi < enemyZones.size; zi++) {\n      EnemyZone z = enemyZones.get(zi);\n      if (z == null) continue;\n\n      // Count orks currently inside (loose: within radius+2 tiles)\n      float cx = (z.cxTile + 0.5f) * tw;\n      float cy = (z.cyTile + 0.5f) * tw;\n      float rr = (z.rTiles + 2.0f) * tw;\n      float rr2 = rr * rr;\n\n      int count = 0;\n      for (int e = 0; e < Entities.MAX; e++) {\n        if (!entities.alive[e]) continue;\n        if (entities.type[e] != EntityType.ORK_GRUNT) continue;\n        float dx = entities.x[e] - cx;\n        float dy = entities.y[e] - cy;\n        if (dx * dx + dy * dy <= rr2) count++;\n      }\n\n      if (dayIndex < z.nextRespawnDayIndex) {\n        // Also keep orks pulled inside the zone boundary.\n        clampOrksTowardZone(cx, cy, z.rTiles * tw);\n        continue;\n      }\n\n      int want = Math.max(0, z.targetCount - count);\n      if (want <= 0) {\n        clampOrksTowardZone(cx, cy, z.rTiles * tw);\n        continue;\n      }\n\n      java.util.Random r = new java.util.Random(z.seed ^ (long) dayIndex * 0xD1B54A32D192ED03L);\n\n      // Spawn missing orks.\n      for (int k = 0; k < want; k++) {\n        // random point in circle\n        float ang = (float) (r.nextFloat() * Math.PI * 2.0);\n        float rad = (float) Math.sqrt(r.nextFloat()) * (z.rTiles * tw * 0.88f);\n        float wx = cx + (float) Math.cos(ang) * rad;\n        float wy = cy + (float) Math.sin(ang) * rad;\n\n        // Skip if blocked/water.\n        if (world.isWaterAtWorldPeek(wx, wy, true)) continue;\n        if (world.isBlockedAtWorldPeek(wx, wy, true)) continue;\n\n        entities.spawn(EntityType.ORK_GRUNT, wx, wy);\n      }\n\n      // Schedule next respawn window: 1..2 days (random)\n      int daysSpan = z.respawnDaysMin + (z.respawnDaysMax > z.respawnDaysMin ? r.nextInt(z.respawnDaysMax - z.respawnDaysMin + 1) : 0);\n      z.nextRespawnDayIndex = dayIndex + Math.max(1, daysSpan);\n\n      clampOrksTowardZone(cx, cy, z.rTiles * tw);\n    }\n  }\n\n  private void clampOrksTowardZone(float cx, float cy, float r) {\n    if (r <= 1e-3f) return;\n    float pullR = r * 1.06f;\n    float pullR2 = pullR * pullR;\n\n    for (int e = 0; e < Entities.MAX; e++) {\n      if (!entities.alive[e]) continue;\n      if (entities.type[e] != EntityType.ORK_GRUNT) continue;\n\n      float dx = entities.x[e] - cx;\n      float dy = entities.y[e] - cy;\n      float d2 = dx * dx + dy * dy;\n\n      if (d2 <= pullR2) continue;\n\n      // If the orc is currently alert/chasing the player, do NOT override its AI velocity.\n      // Requirement: stop wandering/clamping while chasing; resume wandering when shaken off.\n      if (entities.aiF1[e] > 0.15f) continue;\n\n      float inv = 1.0f / (float) Math.sqrt(Math.max(1e-6f, d2));\n      float vx = -dx * inv;\n      float vy = -dy * inv;\n\n      // Force a gentle pull back inside the zone.\n      float spd = 60f;\n      entities.vx[e] = vx * spd;\n      entities.vy[e] = vy * spd;\n      entities.rot[e] = (float) Math.atan2(entities.vy[e], entities.vx[e]);\n    }\n  }\n\n  /**\n   * Tile-tree streaming (FUSA Story):\n   * - Trees/stumps are NOT spawned as Entities (prevents Entities.MAX exhaustion).\n   * - Instead, we update tile collision based on presence/cut bits.\n   * - Rendering uses EntityRenderer.drawTileTrees() directly from bitmasks.\n   */\n  private void areaTileTreesTick(float dt) {\n    if (!AREA_MODE) return;\n    if (areaTreePresentBits == null || areaTreeCutBits == null) {\n      areaDebugToastOnce("TileTrees tick: missing bits (present=" + (areaTreePresentBits != null) + ", cut=" + (areaTreeCutBits != null) + ")");\n      return;\n    }\n    if (areaTreeW <= 0 || areaTreeH <= 0) {\n      areaDebugToastOnce("TileTrees tick: invalid size w=" + areaTreeW + " h=" + areaTreeH);\n      return;\n    }\n\n    // One-shot purge: older versions spawned NODE_TREE/NODE_STUMP as Entities and could hit Entities.MAX.\n    if (!tileTreeEntitiesPurged) {\n      for (int e = 0; e < Entities.MAX; e++) {\n        if (!entities.alive[e]) continue;\n        EntityType t = entities.type[e];\n        if (t == EntityType.NODE_TREE || t == EntityType.NODE_STUMP) {\n          entities.kill(e);\n        }\n      }\n      tileTreeEntitiesPurged = true;\n    }\n\n    // Debug telemetry (cheap counters; shown via toast)\n    int dbgPresentTiles = 0;\n    int dbgSkipRoad = 0;\n    int dbgSkipWater = 0;\n    int dbgBlockTiles = 0;\n    int dbgCutTiles = 0;\n\n    // Update collision mask for visible tiles.\n    for (int cy = loadedMinCy; cy <= loadedMaxCy; cy++) {\n      for (int cx = loadedMinCx; cx <= loadedMaxCx; cx++) {\n        Chunk c = world.peekChunk(cx, cy);\n        if (c == null || c.layers == null) continue;\n\n        int baseTx = cx * World.CHUNK_SIZE;\n        int baseTy = cy * World.CHUNK_SIZE;\n\n        for (int ly = 0; ly < World.CHUNK_SIZE; ly++) {\n          int ty = baseTy + ly;\n          if (ty < 0 || ty >= areaTreeH) continue;\n          for (int lx = 0; lx < World.CHUNK_SIZE; lx++) {\n            int tx = baseTx + lx;\n            if (tx < 0 || tx >= areaTreeW) continue;\n\n            int bit = bitIndex(tx, ty, areaTreeW);\n            if (!bitGet(areaTreePresentBits, bit)) continue;\n            dbgPresentTiles++;\n\n            int idx = lx + ly * World.CHUNK_SIZE;\n\n            // Road/water tiles must stay tree-free.\n            if (c.layers.roadMask[idx] != 0) {\n              c.layers.collisionMask[idx] = 0;\n              dbgSkipRoad++;\n              continue;\n            }\n            if (c.layers.waterMask[idx] != 0) {\n              c.layers.collisionMask[idx] = 0;\n              dbgSkipWater++;\n              continue;\n            }\n\n            boolean cut = bitGet(areaTreeCutBits, bit);\n            // IMPORTANT: allow movement under the canopy.\n            // Trunk collision is handled separately (AABB around trunk center), so we do NOT block the whole tile.\n            c.layers.collisionMask[idx] = 0;\n            if (cut) dbgCutTiles++; else dbgBlockTiles++;\n          }\n        }\n      }\n    }\n\n    // Debug toast (rate-limited)\n    if (dbgTileTrees) {\n      dbgTileTreesToastCooldown -= dt;\n      if (dbgTileTreesToastCooldown <= 0f) {\n        dbgTileTreesToastCooldown = 0.75f;\n        toast = "DBG TileTrees: w=" + areaTreeW + " h=" + areaTreeH\n            + " presentTiles=" + dbgPresentTiles\n            + " skip(road=" + dbgSkipRoad + ",water=" + dbgSkipWater + ")"\n            + " coll(block=" + dbgBlockTiles + ",cut=" + dbgCutTiles + ")";\n        toastT = 0.90f;\n      }\n    }\n  }\n\n  /**\n   * Resolves trunk-only collision against tile-trees inside the loaded chunk window.\n   *\n   * This matches the previous feel of NODE_TREE entity collisions:\n   * - only a small trunk box blocks\n   * - canopy is passable (player can walk "under" it)\n   */\n  private void resolveTileTreeTrunksInLoadedWindow() {\n    if (!AREA_MODE) return;\n    if (areaTreePresentBits == null || areaTreeCutBits == null || areaTreeW <= 0 || areaTreeH <= 0) return;\n    if (world == null || entities == null) return;\n\n    final float treeTrunkHalf = TREE_TRUNK_HALF;\n    final float treeTrunkCenterFromBottom = TREE_TRUNK_CENTER_FROM_BOTTOM_PX;\n    final float treeH = EntityMetrics.drawH(EntityType.NODE_TREE);\n    final float treeBottomOffset = 34.0f - (treeH * 0.5f); // yCenter=ty*16+34\n\n    final float stumpHalfW = STUMP_COLLIDER_HALF_W;\n    final float stumpHalfH = STUMP_COLLIDER_HALF_H;\n    final float stumpCenterFromBottom = STUMP_COLLIDER_CENTER_FROM_BOTTOM_PX;\n    final float stumpH = EntityMetrics.drawH(EntityType.NODE_STUMP);\n    final float stumpBottomOffset = 2.0f - (stumpH * 0.5f); // yCenter=ty*16+2\n\n    for (int e = 0; e < Entities.MAX; e++) {\n      if (!entities.alive[e]) continue;\n      EntityType t = entities.type[e];\n      if (t != EntityType.PLAYER && t != EntityType.ORK_GRUNT && t != EntityType.ANIMAL_DEER) continue;\n\n      // Respect the loaded window for non-always-active entities.\n      if (!entities.isAlwaysActive(e)) {\n        int ecx = (int) Math.floor((entities.x[e] / World.TILE_WORLD) / World.CHUNK_SIZE);\n        int ecy = (int) Math.floor((entities.y[e] / World.TILE_WORLD) / World.CHUNK_SIZE);\n        if (ecx < loadedMinCx || ecx > loadedMaxCx || ecy < loadedMinCy || ecy > loadedMaxCy) continue;\n      }\n\n      float px = entities.x[e];\n      float py = entities.y[e];\n      final float ox = px;\n      final float oy = py;\n\n      float moverHalf = Math.max(3f, EntityMetrics.collisionRadius(t));\n\n      int tx0 = (int) Math.floor(px / World.TILE_WORLD);\n      int ty0 = (int) Math.floor(py / World.TILE_WORLD);\n\n      // Only need a tiny neighborhood (trunk extents < 1 tile).\n      for (int ty = ty0 - 1; ty <= ty0 + 1; ty++) {\n        if (ty < 0 || ty >= areaTreeH) continue;\n        for (int tx = tx0 - 1; tx <= tx0 + 1; tx++) {\n          if (tx < 0 || tx >= areaTreeW) continue;\n\n          int bit = bitIndex(tx, ty, areaTreeW);\n          if (!bitGet(areaTreePresentBits, bit)) continue;\n          boolean cut = TILE_TREES_FELL_ON_HARVEST && bitGet(areaTreeCutBits, bit);\n\n          // Ignore road/water tiles to match render/selection rules.\n          int cx = tx / World.CHUNK_SIZE;\n          int cy = ty / World.CHUNK_SIZE;\n          Chunk c = world.peekChunk(cx, cy);\n          if (c == null || c.layers == null) continue;\n          int lx = tx - cx * World.CHUNK_SIZE;\n          int ly = ty - cy * World.CHUNK_SIZE;\n          int idx = lx + ly * World.CHUNK_SIZE;\n          if (c.layers.roadMask[idx] != 0) continue;\n          if (c.layers.waterMask[idx] != 0) continue;\n\n          float colCx = (tx + 0.5f) * World.TILE_WORLD;\n          float colCy;\n          float halfW;\n          float halfH;\n\n          if (!cut) {\n            // TREE trunk collider (small box)\n            float bottom = (ty * World.TILE_WORLD) + treeBottomOffset;\n            colCy = bottom + treeTrunkCenterFromBottom;\n            halfW = treeTrunkHalf;\n            halfH = treeTrunkHalf;\n          } else {\n            // STUMP silhouette collider (AABB around stump body)\n            float bottom = (ty * World.TILE_WORLD) + stumpBottomOffset;\n            colCy = bottom + stumpCenterFromBottom;\n            halfW = stumpHalfW;\n            halfH = stumpHalfH;\n          }\n\n          float dx = px - colCx;\n          float dy = py - colCy;\n\n          float overlapX = (moverHalf + halfW) - Math.abs(dx);\n          if (overlapX <= 0f) continue;\n          float overlapY = (moverHalf + halfH) - Math.abs(dy);\n          if (overlapY <= 0f) continue;\n\n          // Resolve along least penetration.\n          if (overlapX < overlapY) {\n            float sx = (dx < 0f) ? -1f : 1f;\n            px += sx * overlapX;\n          } else {\n            float sy = (dy < 0f) ? -1f : 1f;\n            py += sy * overlapY;\n          }\n        }\n      }\n\n      // If we got pushed by a trunk, also steer wandering orks away from the obstacle.\n      // Otherwise they can "slide" along dense trunk lines and look like they march on a border.\n      if (t == EntityType.ORK_GRUNT) {\n        float mdx = px - ox;\n        float mdy = py - oy;\n        if (mdx * mdx + mdy * mdy > 1e-6f) {\n          // Only apply when not actively chasing/alert.\n          if (entities.aiF1[e] <= 0.15f) {\n            // Force a direction refresh soon in the AI.\n            entities.aiT[e] = 0f;\n            // Dampen velocity so it doesn't keep pushing into the same trunk line.\n            entities.vx[e] *= 0.20f;\n            entities.vy[e] *= 0.20f;\n            // Turn left/right deterministically.\n            if (((e ^ dayIndex) & 1) == 0) {\n              // left\n              entities.dir[e] = (byte) switch (entities.dir[e]) {\n                case 0 -> 3;\n                case 1 -> 0;\n                case 2 -> 1;\n                default -> 2;\n              };\n            } else {\n              // right\n              entities.dir[e] = (byte) switch (entities.dir[e]) {\n                case 0 -> 1;\n                case 1 -> 2;\n                case 2 -> 3;\n                default -> 0;\n              };\n            }\n          }\n        }\n      }\n\n      entities.x[e] = px;\n      entities.y[e] = py;\n    }\n  }\n\n  @Override\n  public void resize(int width, int height) {\n    // keep current zoom; setToOrtho resets projection params\n    float z = (cam != null) ? cam.zoom : ZOOM_DEFAULT;\n    cam.setToOrtho(false, width, height);\n    cam.zoom = MathUtils.clamp(z, ZOOM_MIN, ZOOM_MAX);\n\n    if (uiCam != null) {\n      uiCam.setToOrtho(false, width, height);\n      uiCam.update();\n    }\n\n    // Recreate Day/Night mask framebuffer on resize\n    rebuildDnMaskFbo(width, height);\n  }\n\n  private void onKill(CombatSystem.Kill k) {\n    encounterSpawner.onKilled(k.type());\n    // XP + loot on kill\n    if (k.type() == EntityType.ORK_GRUNT) {\n      progress.addXp(data.orkGrunt.xp);\n      int coins = randRange(data.orkGrunt.coinMin, data.orkGrunt.coinMax);\n      int lootingLv = (SK_LOOTING >= 0 && SK_LOOTING < progress.skillLv.length) ? progress.skillLv[SK_LOOTING] : 1;\n      coins = SkillEffects.bonusAmountMul5(coins, lootingLv);\n      // Drop copper coins; wallet absorbs on pickup\n      entities.spawnDrop(31, coins, k.x(), k.y());\n    } else if (k.type() == EntityType.ANIMAL_DEER) {\n      progress.addXp(1);\n      int meat = randRange(Math.max(1, data.deer.meatMin), Math.max(1, data.deer.meatMax));\n      int huntingLv = (SK_HUNTING >= 0 && SK_HUNTING < progress.skillLv.length) ? progress.skillLv[SK_HUNTING] : 1;\n      meat = SkillEffects.bonusAmountMul5(meat, huntingLv);\n      entities.spawnDrop(28, meat, k.x(), k.y());\n    }\n  }\n\n  private void pickupNearby() {\n    float eff = REACH_PICKUP + EntityMetrics.radius(EntityType.ITEM_DROP);\n    float r2 = eff * eff;\n    for (int i=0;i<Entities.MAX;i++) {\n      if (!entities.alive[i]) continue;\n      if (entities.type[i] != EntityType.ITEM_DROP) continue;\n      float dx = entities.x[i] - px;\n      float dy = entities.y[i] - py;\n      if (dx*dx + dy*dy <= r2) {\n        int id = entities.itemId[i];\n        int amt = entities.itemAmount[i];\n\n        // currency items go to wallet, not inventory\n        if (id >= 31 && id <= 33) {\n          int each = Math.max(0, priceBook.getBaseCopper(id));\n          wallet.addCopper((long) each * Math.max(0, amt));\n        } else {\n          inv.add(id, amt);\n        }\n\n        entities.kill(i);\n        game.audio.sfx("audio/sfx/pickup.wav", game.audio.sfxVolume(game.settings));\n        return;\n      }\n    }\n  }\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private boolean isNearBuild(EntityType t, float range) {\n    float eff = range + EntityMetrics.radius(t);\n    float r2 = eff * eff;\n    for (int i=0;i<Entities.MAX;i++) {\n      if (!entities.alive[i]) continue;\n      if (entities.type[i] != t) continue;\n      float dx = entities.x[i] - px;\n      float dy = entities.y[i] - py;\n      if (dx*dx + dy*dy <= r2) return true;\n    }\n    return false;\n  }\n  */\n\n  private float actionFovDeg() {\n    // NOTE: requested: skills can expand FoV/actionrange.\n    // Minimal implementation: Navigation skill slightly widens the effective interaction FoV.\n    int navLv = (SK_SPEED >= 0 && SK_SPEED < progress.skillLv.length) ? progress.skillLv[SK_SPEED] : 1;\n    float mul = 1f + 0.01f * Math.max(0, navLv - 1);\n    if (mul > 1.25f) mul = 1.25f;\n    return ACTION_FOV_DEG * mul;\n  }\n\n  private boolean pointInSilhouette(int e, float wx, float wy) {\n    if (e < 0 || e >= Entities.MAX) return false;\n    if (!entities.alive[e]) return false;\n    EntityType t = entities.type[e];\n    float w = EntityMetrics.drawW(t);\n    float h = EntityMetrics.drawH(t);\n    float x0 = entities.x[e] - w * 0.5f;\n    float y0 = entities.y[e] - h * 0.5f;\n    return wx >= x0 && wx <= x0 + w && wy >= y0 && wy <= y0 + h;\n  }\n\n  private boolean tryInteractAtMouse(float fovFx, float fovFy) {\n    // All world interactions must be within the player's action radius and inside the FoV.\n    // Additionally, the target must be under the mouse cursor silhouette.\n    if (walletOpen || pricingOpen || skillsOpen || mapOpen) return false;\n\n    // While the WQG dialog is open, block in-world interaction.\n    // Rule: from that point on, only interact with the popup until it is closed.\n    if (questPopupOpen) return false;\n\n    // Chest/merchant cannot be opened while another modal is open.\n    if (craftOpen || invOpen || buildMode || openChestE >= 0) {\n      // Allow closing chest by click if it's open.\n      if (openChestE >= 0) {\n        openChestE = -1;\n        game.audio.sfx("audio/sfx/close_chest.wav", game.audio.sfxVolume(game.settings));\n        return true;\n      }\n      return false;\n    }\n\n    float reach = actionReach(equippedFromHotbar());\n    float fovDeg = actionFovDeg();\n\n    int best = -1;\n    float bestD2 = Float.POSITIVE_INFINITY;\n\n    // Candidates: merchant + quest guy + chest + item drops.\n    for (int i = 0; i < Entities.MAX; i++) {\n      if (!entities.alive[i]) continue;\n      EntityType t = entities.type[i];\n      boolean ok = (t == EntityType.MERCHANT_ELF || t == EntityType.MERCHANT_WANDERING || t == EntityType.WANDER_QUEST_GUY\n          || t == EntityType.BUILD_CHEST || t == EntityType.POI_CHEST_HIDDEN || t == EntityType.ITEM_DROP);\n      if (!ok) continue;\n\n      // Must be clickable on silhouette\n      if (!pointInSilhouette(i, mouseWorldX, mouseWorldY)) continue;\n\n      // Must be inside FoV\n      if (!isInsideFov(px, py, entities.x[i], entities.y[i], fovFx, fovFy, fovDeg)) continue;\n\n      // Must be in action radius (player range + target radius)\n      float eff = reach + EntityMetrics.radius(t);\n      float dx = entities.x[i] - px;\n      float dy = entities.y[i] - py;\n      if (dx * dx + dy * dy > eff * eff) continue;\n\n      // Prefer nearest to cursor\n      float mx = entities.x[i] - mouseWorldX;\n      float my = entities.y[i] - mouseWorldY;\n      float md2 = mx * mx + my * my;\n      if (md2 < bestD2) {\n        bestD2 = md2;\n        best = i;\n      }\n    }\n\n    if (best < 0) return false;\n\n    EntityType bt = entities.type[best];\n    if (bt == EntityType.BUILD_CHEST || bt == EntityType.POI_CHEST_HIDDEN) {\n      openChestE = best;\n      shopOpen = false;\n      craftOpen = false;\n      invOpen = false;\n      buildMode = false;\n      game.audio.sfx("audio/sfx/open_chest.wav", game.audio.sfxVolume(game.settings));\n      if (bt == EntityType.POI_CHEST_HIDDEN) {\n        toast = "Hidden Chest gefunden";\n        toastT = 15f;\n      }\n      return true;\n    }\n\n    if (bt == EntityType.MERCHANT_ELF || bt == EntityType.MERCHANT_WANDERING) {\n      shopOpen = !shopOpen;\n      if (shopOpen) {\n        openMerchantE = best;\n        craftOpen = false;\n        walletOpen = false;\n        syncShopFromMerchant(openMerchantE);\n        if (Float.isNaN(invAnchorX) || Float.isNaN(invAnchorY)) {\n          invAnchorX = 0f;\n          invAnchorY = 0f;\n        }\n        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n      } else {\n        openMerchantE = -1;\n        buyPopup = false;\n        buyBuffer = "";\n        dragArmed = false;\n        dragActive = false;\n        game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n      }\n      return true;\n    }\n\n    // Auto-close quest popup when leaving the action radius.\n    if (questPopupOpen && openQuestGuyE >= 0 && openQuestGuyE < Entities.MAX && entities.alive[openQuestGuyE]\n        && (entities.type[openQuestGuyE] == EntityType.WANDER_QUEST_GUY)) {\n      float eff = actionReach(equippedFromHotbar()) + EntityMetrics.radius(entities.type[openQuestGuyE]);\n      float dx = entities.x[openQuestGuyE] - px;\n      float dy = entities.y[openQuestGuyE] - py;\n      if (dx * dx + dy * dy > eff * eff) {\n        questPopupOpen = false;\n        openQuestGuyE = -1;\n      }\n    }\n\n    if (bt == EntityType.WANDER_QUEST_GUY) {\n      questPopupOpen = !questPopupOpen;\n      if (questPopupOpen) {\n        openQuestGuyE = best;\n        if (questGuy != null) questGuy.onPopupOpened();\n        // Close other modals.\n        shopOpen = false;\n        craftOpen = false;\n        walletOpen = false;\n        invOpen = false;\n        buildMode = false;\n        openChestE = -1;\n        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n      } else {\n        openQuestGuyE = -1;\n        game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n      }\n      return true;\n    }\n\n    if (bt == EntityType.ITEM_DROP) {\n      // Click-to-pickup (within reach) like in-world interaction.\n      int id = entities.itemId[best];\n      int amt = entities.itemAmount[best];\n      if (id >= 31 && id <= 33) {\n        int each = Math.max(0, priceBook.getBaseCopper(id));\n        wallet.addCopper((long) each * Math.max(0, amt));\n      } else {\n        inv.add(id, amt);\n      }\n      entities.kill(best);\n      game.audio.sfx("audio/sfx/pickup.wav", game.audio.sfxVolume(game.settings));\n      return true;\n    }\n\n    return false;\n  }\n\n  private boolean tryToggleShop() {\n    // Wallet open blocks shop toggle\n    if (walletOpen) return false;\n\n    // Chest interaction has priority\n    if (tryToggleChest()) return true;\n\n    // Legacy toggle (keyboard): nearest merchant inside action radius AND inside FoV.\n    float reach = actionReach(equippedFromHotbar());\n    float fovDeg = actionFovDeg();\n    // use current aim dir\n    float fovFx = (float) Math.cos(playerAimA);\n    float fovFy = (float) Math.sin(playerAimA);\n\n    float eff = reach + EntityMetrics.radius(EntityType.MERCHANT_ELF);\n    float r2 = eff * eff;\n    for (int i=0;i<Entities.MAX;i++) {\n      if (!entities.alive[i]) continue;\n      if (entities.type[i] != EntityType.MERCHANT_ELF && entities.type[i] != EntityType.MERCHANT_WANDERING) continue;\n      if (!isInsideFov(px, py, entities.x[i], entities.y[i], fovFx, fovFy, fovDeg)) continue;\n      float dx = entities.x[i] - px;\n      float dy = entities.y[i] - py;\n      if (dx*dx + dy*dy <= r2) {\n        shopOpen = !shopOpen;\n        if (shopOpen) {\n          openMerchantE = i;\n          craftOpen = false;\n          walletOpen = false;\n          syncShopFromMerchant(openMerchantE);\n          if (Float.isNaN(invAnchorX) || Float.isNaN(invAnchorY)) {\n            invAnchorX = 0f;\n            invAnchorY = 0f;\n          }\n          game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n        } else {\n          openMerchantE = -1;\n          buyPopup = false;\n          buyBuffer = "";\n          dragArmed = false;\n          dragActive = false;\n          game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n        }\n        return true;\n      }\n    }\n\n    // If shop open, E closes\n    if (shopOpen) {\n      shopOpen = false;\n      openMerchantE = -1;\n      buyPopup = false;\n      buyBuffer = "";\n      dragArmed = false;\n      dragActive = false;\n      game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n      return true;\n    }\n    return false;\n  }\n\n  private boolean tryToggleQuestGuy() {\n    // Any other modal blocks quest interaction.\n    if (walletOpen || pricingOpen || skillsOpen || mapOpen) return false;\n    if (craftOpen || invOpen || buildMode || openChestE >= 0 || shopOpen) return false;\n\n    // If popup already open: E should behave like pressing the explicit "Gehen" button.\n    // (We do NOT close immediately: farewell must be shown first.)\n    if (questPopupOpen) {\n      wqgRequestLeave("left");\n      return true;\n    }\n\n    // Nearest quest guy inside action radius AND inside FoV.\n    float reach = actionReach(equippedFromHotbar());\n    float fovDeg = actionFovDeg();\n    float fovFx = (float) Math.cos(playerAimA);\n    float fovFy = (float) Math.sin(playerAimA);\n\n    float eff = reach + EntityMetrics.radius(EntityType.WANDER_QUEST_GUY);\n    float r2 = eff * eff;\n    for (int i=0;i<Entities.MAX;i++) {\n      if (!entities.alive[i]) continue;\n      if (entities.type[i] != EntityType.WANDER_QUEST_GUY) continue;\n      if (!isInsideFov(px, py, entities.x[i], entities.y[i], fovFx, fovFy, fovDeg)) continue;\n      float dx = entities.x[i] - px;\n      float dy = entities.y[i] - py;\n      if (dx*dx + dy*dy <= r2) {\n        openQuestGuyDialog(i);\n        return true;\n      }\n    }\n\n    return false;\n  }\n\n  /** Opens the WQG dialog for the given entity index (must be a WANDER_QUEST_GUY). */\n  private void openQuestGuyDialog(int e) {\n    questPopupOpen = true;\n    openQuestGuyE = e;\n\n    // Close other modals.\n    shopOpen = false;\n    craftOpen = false;\n    walletOpen = false;\n    invOpen = false;\n    buildMode = false;\n    openChestE = -1;\n\n    // Reset dialog UI state (MUST be fresh per popup; no old content allowed).\n    wqgScrollPx = 0f;\n    wqgDialogText = "";\n    wqgWrapped.clear();\n    wqgFarewellText = "";\n    wqgAutoCloseAtEpochSec = 0L;\n\n    if (questGuy != null) questGuy.onPopupOpened();\n\n    // Compose initial dialog text (greeting + offers or N/A + farewell + auto-close).\n    wqgRefreshDialogTextOnOpen();\n\n    game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n  }\n\n  /**\n   * Builds the on-open dialog content according to design:\n   * - Greeting always first.\n   * - If no offers OR generation blocked/timer-not-due => show N/A phrase, then farewell, then start 30s auto-close.\n   */\n  private void wqgRefreshDialogTextOnOpen() {\n    long nowSec = System.currentTimeMillis() / 1000L;\n\n    ZqsConversationContext ctx =\n        (questGuy != null) ? questGuyCtxSafe() : new ZqsConversationContext();\n\n    String greet = (questGuy != null) ? questGuy.greeting() : "";\n\n    // Hard rule: if player already has too many open quests, do NOT generate/keep offers.\n    // Required behavior: N/A phrase -> farewell -> timer -> close.\n    final int OPEN_QUESTS_CAP = 10;\n    int openCnt = (questLog != null) ? questLog.size() : 0;\n    if (openCnt >= OPEN_QUESTS_CAP) {\n      ctx.openQuestsCount = openCnt;\n      ctx.nqGenerationPossible = false;\n      ctx.blockReason = "cap_reached";\n      ctx.naReason = "cap_reached";\n      // Safety: if offers exist for any reason, discard them now.\n      if (questGuy != null) {\n        // Decline all offers ("Gehen" semantics: not accepted => verworfen).\n        while (questGuy.offerCount() > 0) {\n          if (!questGuy.declineOffer(0, nowSec)) break;\n        }\n        questGuy.discardAllOffers();\n      }\n    }\n\n    int nOffers = (questGuy != null) ? questGuy.offerCount() : 0;\n\n    // If no offers, we must show strict N/A (generator preference), then farewell, then auto-close.\n    if (nOffers <= 0) {\n      // ZQS requires a non-empty na_reason to pick a required assignment_na snippet.\n      // If the context provider didn't set one, fall back to a safe default.\n      if (ctx.naReason == null || ctx.naReason.trim().isEmpty()) {\n        ctx.naReason = "rolled_zero";\n      }\n      String na = (zqsWqgDock != null) ? zqsWqgDock.buildAssignmentNa(ctx) : "";\n      // Dummy fallback text is NOT allowed; if dock missing this is a wiring bug.\n      if (na == null || na.isEmpty()) na = "(MISSING_ZQS_NA_TEXT)"; // DUMMY: should never happen; indicates missing snippet DB wiring.\n\n      // Farewell uses conversation_result.\n      ctx.conversationResult = "no_offer";\n      String farewell = (zqsWqgDock != null) ? zqsWqgDock.buildFarewell(ctx) : "";\n      if (farewell == null || farewell.isEmpty()) farewell = "(MISSING_ZQS_FAREWELL_TEXT)"; // DUMMY: should never happen.\n\n      wqgFarewellText = farewell;\n      wqgAutoCloseAtEpochSec = nowSec + 30L; // Rule: auto-close after farewell.\n\n      wqgDialogText = joinWqgText(greet, na, farewell);\n      wqgWrapDialogText();\n      wqgScrollToBottom();\n      return;\n    }\n\n    // Offers exist: show greeting + offers. No auto-close.\n    wqgDialogText = joinWqgText(greet, buildOfferListText(), "");\n    wqgWrapDialogText();\n    wqgScrollToTop();\n  }\n\n  private ZqsConversationContext questGuyCtxSafe() {\n    // We build context exactly like the bound provider does, but without leaking implementation.\n    // If provider is missing, we still provide epochSec/runtimeSec to keep ZQS deterministic.\n    long nowSec = System.currentTimeMillis() / 1000L;\n    ZqsConversationContext c = new ZqsConversationContext();\n    c.epochSec = nowSec;\n    c.runtimeSec = nowSec;\n    c.openQuestsCount = (questLog != null) ? questLog.size() : 0;\n    // completedQuestsCount is tracked in existing context code path; keep 0 here if unavailable.\n    return c;\n  }\n\n  /** Request leaving the WQG dialog (explicit button or E). Always shows farewell first. */\n  private void wqgRequestLeave(String result) {\n    if (!questPopupOpen) return;\n    long nowSec = System.currentTimeMillis() / 1000L;\n\n    // Rule: leaving without accepting means all currently offered quests are considered discarded (verworfen).\n    if (questGuy != null) {\n      while (questGuy.offerCount() > 0) {\n        if (!questGuy.declineOffer(0, nowSec)) break;\n      }\n      questGuy.discardAllOffers();\n    }\n\n    ZqsConversationContext ctx = questGuyCtxSafe();\n    ctx.conversationResult = (result != null) ? result : "left";\n    String farewell = (zqsWqgDock != null) ? zqsWqgDock.buildFarewell(ctx) : "";\n    if (farewell == null || farewell.isEmpty()) farewell = "(MISSING_ZQS_FAREWELL_TEXT)"; // DUMMY.\n    wqgFarewellText = farewell;\n\n    // Close after farewell. If offers are non-empty we still close (explicit leave).\n    // Delay is intentionally small so the farewell is visible for a moment.\n    // DUMMY: If you want a different UX timing, replace this constant.\n    wqgAutoCloseAtEpochSec = nowSec + 2L;\n\n    // Append farewell to visible text.\n    wqgDialogText = joinWqgText(wqgDialogText, "", farewell);\n    wqgWrapDialogText();\n    wqgScrollToBottom();\n    game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n  }\n\n  private static String joinWqgText(String a, String b, String c) {\n    String x = (a == null) ? "" : a.trim();\n    String y = (b == null) ? "" : b.trim();\n    String z = (c == null) ? "" : c.trim();\n    String out = x;\n    if (!y.isEmpty()) out = out.isEmpty() ? y : (out + "\n\n" + y);\n    if (!z.isEmpty()) out = out.isEmpty() ? z : (out + "\n\n" + z);\n    return out;\n  }\n\n  private String buildOfferListText() {\n    StringBuilder sb = new StringBuilder(512);\n    int n = (questGuy != null) ? questGuy.offerCount() : 0;\n    for (int i = 0; i < n; i++) {\n      var q = questGuy.offer(i);\n      if (q == null) continue;\n      if (sb.length() > 0) sb.append("\n\n");\n      // q.title contains the full generated offer text (assignment + reward).\n      sb.append("Angebot ").append(i + 1).append(":\n");\n      sb.append(q.title);\n    }\n    return sb.toString();\n  }\n\n  private void wqgScrollToTop() { wqgScrollPx = 0f; }\n  private void wqgScrollToBottom() {\n    // actual max scroll depends on wrapped lines; resolved in draw.\n    wqgScrollPx = 1_000_000f;\n  }\n\n  private void wqgWrapDialogText() {\n    wqgWrapped.clear();\n    // Wrap is computed in draw using GlyphLayout; we keep this list as a placeholder cache.\n    // DUMMY: This method currently only resets the cache; real wrapping is done in drawWqgDialog().\n    // Reason: wrapping depends on runtime panel width, which depends on screen size.\n  }\n\n  /**\n   * Calculates the maximum inventory panel width/height for the given UI scale.\n   * out[0]=panelW, out[1]=panelH.\n   */\n  private void calcInventoryMaxPanelWH(float uiScale, float[] out) {\n    final int gridW = GRID_W;\n    int normalRowsCap = Math.max(GRID_MIN_H,\n        (inv.normalSlotCount() + gridW - 1) / gridW);\n    int toolRowsCap = TOOL_MAX_ROWS;\n\n    float slot = 144f * uiScale;\n    float pad = 20f * uiScale;\n    float normalH = normalRowsCap * slot + (normalRowsCap - 1) * pad;\n    float toolH = toolRowsCap * slot + (toolRowsCap - 1) * pad;\n    float labelH = 52f * uiScale;\n    float innerW = gridW * slot + (gridW - 1) * pad;\n    float innerH = normalH + labelH + toolH;\n    float panelPad = 52f * uiScale;\n    float titleH = 96f * uiScale;\n    float panelW = innerW + panelPad * 2f;\n    float panelH = innerH + panelPad * 2f + titleH;\n\n    if (out != null && out.length >= 2) {\n      out[0] = panelW;\n      out[1] = panelH;\n    }\n  }\n\n  private boolean tryToggleChest() {\n    // E closes chest\n    if (openChestE >= 0) {\n      openChestE = -1;\n      game.audio.sfx("audio/sfx/close_chest.wav", game.audio.sfxVolume(game.settings));\n      return true;\n    }\n\n    float r = CHEST_OPEN_RANGE_WU;\n    float r2 = r * r;\n    for (int i=0;i<Entities.MAX;i++) {\n      if (!entities.alive[i]) continue;\n      if (entities.type[i] != EntityType.BUILD_CHEST && entities.type[i] != EntityType.POI_CHEST_HIDDEN) continue;\n      float dx = entities.x[i] - px;\n      float dy = entities.y[i] - py;\n      if (dx*dx + dy*dy <= r2) {\n        openChestE = i;\n        shopOpen = false;\n        craftOpen = false;\n        invOpen = false;\n        buildMode = false;\n        game.audio.sfx("audio/sfx/open_chest.wav", game.audio.sfxVolume(game.settings));\n        if (entities.type[i] == EntityType.POI_CHEST_HIDDEN) {\n          toast = "Hidden Chest gefunden";\n          toastT = 15f;\n        }\n        return true;\n      }\n    }\n\n    return false;\n  }\n\n  private void regenShopOffers() {\n    shopOfferCount = 0;\n\n    // fixed offers first (prices derived from PriceBook: buy=sell*3)\n    if (data.fixedShop != null) {\n      for (int i=0;i<data.fixedShop.offerCount && shopOfferCount < shopItemId.length;i++) {\n        int id = data.fixedShop.itemId[i];\n        shopItemId[shopOfferCount] = id;\n        int base = Math.max(1, priceBook.getBaseCopper(id));\n        shopSell[shopOfferCount] = base;\n        shopBuy[shopOfferCount] = Math.max(1, base * 3);\n        shopOfferCount++;\n      }\n    }\n\n    // daily wandering offers appended\n    if (data.wanderingPoolItems != null && data.wanderingPoolItems.length > 0) {\n      int tradingLv = (SK_TRADING >= 0 && SK_TRADING < progress.skillLv.length) ? progress.skillLv[SK_TRADING] : 1;\n      int want = 3 + Math.max(0, (tradingLv - 1) / 10);\n      for (int k=0;k<want && shopOfferCount < shopItemId.length;k++) {\n        int tries = 0;\n        int pick = -1;\n        while (tries < 20) {\n          int id = data.wanderingPoolItems[(int)(Math.random() * data.wanderingPoolItems.length)];\n          boolean dup = false;\n          for (int j=0;j<shopOfferCount;j++) {\n            if (shopItemId[j] == id) { dup = true; break; }\n          }\n          if (!dup) { pick = id; break; }\n          tries++;\n        }\n        if (pick < 0) break;\n\n        int base = Math.max(1, priceBook.getBaseCopper(pick));\n        shopItemId[shopOfferCount] = pick;\n        shopSell[shopOfferCount] = base;\n        shopBuy[shopOfferCount] = Math.max(1, base * 3);\n        shopOfferCount++;\n      }\n    }\n  }\n\n  private void syncShopFromMerchant(int merchantE) {\n    if (merchantE < 0 || merchantE >= Entities.MAX) return;\n    if (!entities.alive[merchantE]) return;\n    boolean wandering = entities.type[merchantE] == EntityType.MERCHANT_WANDERING;\n    var s = merchants.stateFor(merchantE, wandering);\n    merchants.ensureFresh(s, data, priceBook, progress);\n\n    shopOfferCount = Math.min(shopItemId.length, s.offerCount);\n    for (int i = 0; i < shopOfferCount; i++) {\n      shopItemId[i] = s.itemId[i];\n      shopBuy[i] = s.buy[i];\n      shopSell[i] = s.sell[i];\n    }\n  }\n\n  // Nicht fertiges Feature: commented out unused block\n  /*\n  private void shopBuyIndex(int idx, int amount) {\n    if (idx < 0 || idx >= shopOfferCount) return;\n    if (amount <= 0) return;\n    int itemId = shopItemId[idx];\n    int priceEach = shopBuy[idx];\n\n    // Barter/Negotiation: better prices per level (5% each level)\n    int barterLv = (SK_BARTER >= 0 && SK_BARTER < progress.skillLv.length) ? progress.skillLv[SK_BARTER] : 1;\n    int negoLv = (SK_NEGOTIATION >= 0 && SK_NEGOTIATION < progress.skillLv.length) ? progress.skillLv[SK_NEGOTIATION] : 1;\n    float buyMul = 1f;\n    buyMul *= Math.max(0.20f, 1f - 0.05f * Math.max(0, barterLv - 1));\n    buyMul *= Math.max(0.35f, 1f - 0.05f * Math.max(0, negoLv - 1));\n    priceEach = Math.max(1, (int) Math.floor(priceEach * buyMul));\n\n    long total = (long) priceEach * amount;\n    if (total <= 0) return;\n\n    if (wallet.spendCopper(total)) {\n      inv.add(itemId, amount);\n      game.audio.sfx("audio/sfx/craft.wav", game.audio.sfxVolume(game.settings));\n    } else {\n      game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n    }\n  }\n  */\n\n  /** Sell any itemId to merchant (used by inventory→shop drag). priceEachRaw is base copper before skills. */\n  private void shopSellItem(int itemId, int priceEachRaw, int amount) {\n    if (itemId < 0) return;\n    if (amount <= 0) return;\n    int priceEach = priceEachRaw;\n    if (priceEach <= 0) {\n      priceEach = Math.max(1, priceBook.getBaseCopper(itemId));\n    }\n\n    // Barter/Negotiation: better sell prices per level (5% each level)\n    int barterLv = (SK_BARTER >= 0 && SK_BARTER < progress.skillLv.length) ? progress.skillLv[SK_BARTER] : 1;\n    int negoLv = (SK_NEGOTIATION >= 0 && SK_NEGOTIATION < progress.skillLv.length) ? progress.skillLv[SK_NEGOTIATION] : 1;\n    float sellMul = 1f;\n    sellMul *= 1f + 0.05f * Math.max(0, barterLv - 1);\n    sellMul *= 1f + 0.05f * Math.max(0, negoLv - 1);\n    priceEach = Math.max(1, (int) Math.floor(priceEach * sellMul));\n\n    int sold = 0;\n    for (int i = 0; i < amount; i++) {\n      if (!inv.spend(itemId, 1)) break;\n      sold++;\n    }\n    if (sold <= 0) {\n      game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n      return;\n    }\n\n    wallet.addCopper((long) priceEach * sold);\n    game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n  }\n\n  private void drawShopMenuAtMerchant() {\n    if (openMerchantE < 0 || openMerchantE >= Entities.MAX || !entities.alive[openMerchantE] || (entities.type[openMerchantE] != EntityType.MERCHANT_ELF && entities.type[openMerchantE] != EntityType.MERCHANT_WANDERING)) {\n      shopLayoutValid = false;\n      // fallback: behave like old text HUD\n      font.setColor(0f, 0f, 0f, 1f);\n      font.getData().setScale(1.15f * UI_FONT_SCALE);\n      font.draw(batch, "SHOP (drag shop→inventory = BUY | drag inventory→shop = SELL | RMB quick buy | CTRL x10 | SHIFT x100 | E close)", 20, Gdx.graphics.getHeight() - 130);\n      int lines = Math.min(6, shopOfferCount);\n      for (int i=0;i<lines;i++) {\n        int id = shopItemId[i];\n        String name = (id >= 0 && id < data.items.length && data.items[id] != null) ? data.items[id].name : ("item_" + id);\n        font.draw(batch, name + "  buy=" + shopBuy[i] + " sell=" + shopSell[i], 20, Gdx.graphics.getHeight() - 150 - i*18);\n      }\n      font.getData().setScale(1.0f * UI_FONT_SCALE);\n      font.setColor(0f, 0f, 0f, 1f);\n      return;\n    }\n\n    // Merchant head position (world -> screen)\n\n    // Merchant head position (world -> screen)\n    float mxw = entities.x[openMerchantE];\n    float myw = entities.y[openMerchantE];\n\n    // Block 0: avoid per-frame allocations in render loop.\n    Vector3 sp = scratch.v3a;\n    sp.set(mxw, myw, 0f);\n    cam.project(sp);\n\n    float headOffset = EntityMetrics.drawH(EntityType.MERCHANT_ELF) * 0.60f;\n    Vector3 spHead = scratch.v3b;\n    spHead.set(mxw, myw + headOffset, 0f);\n    cam.project(spHead);\n\n    // Layout: use inventory slot art (big & readable)\n    float slot = 144f;\n    float pad = 18f;\n\n    int cols = 6;\n    int rows = 2;\n\n    float innerW = cols * slot + (cols - 1) * pad;\n    float innerH = rows * slot + (rows - 1) * pad;\n\n    float panelPad = 40f;\n    float panelW = innerW + panelPad * 2f;\n    float panelH = innerH + panelPad * 2f + 76f; // title\n\n    // Anchor above merchant head\n    float x0 = spHead.x - panelW * 0.5f;\n    float y0 = spHead.y + 18f;\n\n    // clamp to screen\n    x0 = MathUtils.clamp(x0, 0f, Math.max(0f, Gdx.graphics.getWidth() - panelW));\n    y0 = MathUtils.clamp(y0, 0f, Math.max(0f, Gdx.graphics.getHeight() - panelH));\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    font.getData().setScale(1.6f * 1.15f);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.draw(batch, "MERCHANT (drag shop→inventory = BUY | drag inventory→shop = SELL | RMB quick buy | CTRL x10 | SHIFT x100 | E close)", x0 + panelPad, y0 + panelH - 22f);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n\n    float sx0 = x0 + panelPad;\n    float sy0 = y0 + panelPad;\n\n    // cache for hit-tests\n    shopPanelX0 = x0;\n    shopPanelY0 = y0;\n    shopPanelW = panelW;\n    shopPanelH = panelH;\n    shopSlotsX0 = sx0;\n    shopSlotsY0 = sy0;\n    shopSlotPx = slot;\n    shopPadPx = pad;\n    shopLayoutValid = true;\n\n    int shown = Math.min(cols * rows, shopOfferCount);\n    for (int i=0;i<cols*rows;i++) {\n      float sx = sx0 + (i % cols) * (slot + pad);\n      float sy = sy0 + (i / cols) * (slot + pad);\n\n      batch.draw(uiRegions.slot, sx, sy, slot, slot);\n\n      if (i < shown) {\n        int itemId = shopItemId[i];\n        if (itemId >= 0) {\n          // icon\n          TextureRegion icon = entityRegions.itemIcon(itemId);\n          float iw = slot * 0.72f;\n          float ih = slot * 0.72f;\n          batch.draw(icon, sx + (slot - iw) * 0.5f, sy + (slot - ih) * 0.5f + 10f, iw, ih);\n\n          // price labels (style like Chatty2: Buy (red) above, Sell (green) below)\n          font.getData().setScale(1.02f * UI_FONT_SCALE);\n\n          int row = i / cols;\n          boolean topRow = row == rows - 1;\n\n          float buyY = sy + slot + (topRow ? 22f : 2f);\n          float sellY = sy - 12f;\n\n          font.setColor(1f, 0f, 0f, 1f);\n          font.draw(batch, "Buy " + shopBuy[i], sx + 8f, buyY);\n\n          font.setColor(0.1f, 0.6f, 0.1f, 1f);\n          font.draw(batch, "Sell " + shopSell[i], sx + 8f, sellY);\n\n          font.getData().setScale(1.0f * UI_FONT_SCALE);\n          font.setColor(0f, 0f, 0f, 1f);\n        }\n      }\n    }\n\n    // line indicator from merchant head to panel (subtle)\n    batch.setColor(1f, 1f, 1f, 1f);\n  }\n\n  private void drawWqgDialog() {\n    if (openQuestGuyE < 0 || openQuestGuyE >= Entities.MAX || !entities.alive[openQuestGuyE]\n        || entities.type[openQuestGuyE] != EntityType.WANDER_QUEST_GUY) {\n      questPopupOpen = false;\n      openQuestGuyE = -1;\n      wqgAutoCloseAtEpochSec = 0L;\n      return;\n    }\n\n    // Auto-close (timer is set only when offers are empty and farewell was shown).\n    if (wqgAutoCloseAtEpochSec > 0L) {\n      long nowSec = System.currentTimeMillis() / 1000L;\n      if (nowSec >= wqgAutoCloseAtEpochSec) {\n        questPopupOpen = false;\n        openQuestGuyE = -1;\n        wqgAutoCloseAtEpochSec = 0L;\n        return;\n      }\n    }\n\n    // Panel size: maximum inventory panel size.\n    final int gridW = GRID_W;\n    int normalRowsCap = Math.max(GRID_MIN_H, (inv.normalSlotCount() + gridW - 1) / gridW);\n    int toolRowsCap = TOOL_MAX_ROWS;\n\n    float uiScale = 0.85f;\n    float slot = 144f * uiScale;\n    float pad = 20f * uiScale;\n    float normalH = normalRowsCap * slot + (normalRowsCap - 1) * pad;\n    float toolH = toolRowsCap * slot + (toolRowsCap - 1) * pad;\n    float labelH = 52f * uiScale;\n    float innerW = gridW * slot + (gridW - 1) * pad;\n    float innerH = normalH + labelH + toolH;\n    float panelPad = 52f * uiScale;\n    float titleH = 96f * uiScale;\n    float panelW = innerW + panelPad * 2f;\n    float panelH = innerH + panelPad * 2f + titleH;\n\n    float x0 = (Gdx.graphics.getWidth() - panelW) * 0.5f;\n    float y0 = 0f;\n    x0 = MathUtils.clamp(x0, 0f, Math.max(0f, Gdx.graphics.getWidth() - panelW));\n    y0 = MathUtils.clamp(y0, 0f, Math.max(0f, Gdx.graphics.getHeight() - panelH));\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    // Header\n    font.getData().setScale(2.15f * uiScale);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.draw(batch, "WANDER_QUEST_GUY", x0 + panelPad, y0 + panelH - 28f * uiScale);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n\n    // Buttons\n    float btnH = 86f * uiScale;\n    float btnPad = 16f * uiScale;\n    float btnY = y0 + panelPad;\n    float btnW = (panelW - panelPad * 2f - btnPad * 2f) / 3f;\n    float btnX0 = x0 + panelPad;\n    float btnAcceptX = btnX0;\n    float btnDeclineX = btnX0 + (btnW + btnPad);\n    float btnGoX = btnX0 + (btnW + btnPad) * 2f;\n\n    // Scrollable text region\n    float textX0 = x0 + panelPad;\n    float textY0 = btnY + btnH + 18f * uiScale;\n    float textW = panelW - panelPad * 2f;\n    float textH = panelH - titleH - panelPad - (btnH + 18f * uiScale) - panelPad;\n\n    // Clip text (use the same scratch rectangles as other UI panels, e.g. Craft panel)\n    Rectangle clip = scratch.r0;\n    clip.set(textX0, textY0, textW, textH);\n    Rectangle scissors = scratch.r1;\n    ScissorStack.calculateScissors(uiCam, batch.getTransformMatrix(), clip, scissors);\n    batch.flush();\n    ScissorStack.pushScissors(scissors);\n\n    // Wrap + draw (friendly to existing UI coords: use BitmapFont wrapping instead of custom word splitting)\n    float wrapW = textW - 8f;\n    font.setColor(0f, 0f, 0f, 1f);\n    // WQG dialog is text-heavy; keep it visibly larger than default UI labels.\n    font.getData().setScale(1.25f * uiScale);\n\n    String text = (wqgDialogText != null) ? wqgDialogText : "";\n    uiLayout.setText(font, text, Color.BLACK, wrapW, Align.left, true);\n    float contentH = Math.max(textH, uiLayout.height);\n    float maxScroll = Math.max(0f, contentH - textH);\n    wqgScrollPx = MathUtils.clamp(wqgScrollPx, 0f, maxScroll);\n\n    // Draw from top of the text region; scrolling moves content up/down.\n    float drawY = textY0 + textH - 8f + wqgScrollPx;\n    font.draw(batch, text, textX0 + 4f, drawY, wrapW, Align.left, true);\n\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n\n    batch.flush();\n    ScissorStack.popScissors();\n\n    // Draw buttons\n    float mx = Gdx.input.getX();\n    float my = uiMouseYUp();\n    boolean click = Gdx.input.justTouched();\n    int nOffers = (questGuy != null) ? questGuy.offerCount() : 0;\n    boolean hasOffer = nOffers > 0;\n\n    // DUMMY: apply to first offer only (selection UI must be added later).\n    int selIdx = 0;\n\n    batch.setColor(1f, 1f, 1f, hasOffer ? 1f : 0.35f);\n    batch.draw(uiRegions.button, btnAcceptX, btnY, btnW, btnH);\n    batch.setColor(1f, 1f, 1f, hasOffer ? 1f : 0.35f);\n    batch.draw(uiRegions.button, btnDeclineX, btnY, btnW, btnH);\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.button, btnGoX, btnY, btnW, btnH);\n\n    font.getData().setScale(1.15f * uiScale);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.draw(batch, "ANNEHMEN", btnAcceptX + 18f * uiScale, btnY + 52f * uiScale);\n    font.draw(batch, "ABLEHNEN", btnDeclineX + 18f * uiScale, btnY + 52f * uiScale);\n    font.draw(batch, "GEHEN", btnGoX + 18f * uiScale, btnY + 52f * uiScale);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n\n    if (click) {\n      long nowSec = System.currentTimeMillis() / 1000L;\n\n      if (hitRect(mx, my, btnGoX, btnY, btnW, btnH)) {\n        wqgRequestLeave("left");\n      }\n      if (hasOffer && hitRect(mx, my, btnAcceptX, btnY, btnW, btnH)) {\n        if (questGuy.acceptOffer(selIdx, questLog, nowSec)) {\n          wqgDialogText = joinWqgText((questGuy != null) ? questGuy.greeting() : "", buildOfferListText(), "");\n          if (questGuy.offerCount() <= 0) {\n            var ctx = questGuyCtxSafe();\n            ctx.conversationResult = "accepted";\n            String farewell = (zqsWqgDock != null) ? zqsWqgDock.buildFarewell(ctx) : "";\n            if (farewell == null || farewell.isEmpty()) farewell = "(MISSING_ZQS_FAREWELL_TEXT)";\n            wqgFarewellText = farewell;\n            wqgAutoCloseAtEpochSec = nowSec + 30L;\n            wqgDialogText = joinWqgText(wqgDialogText, "", farewell);\n          }\n          wqgScrollToBottom();\n        }\n      }\n      if (hasOffer && hitRect(mx, my, btnDeclineX, btnY, btnW, btnH)) {\n        if (questGuy.declineOffer(selIdx, nowSec)) {\n          wqgDialogText = joinWqgText((questGuy != null) ? questGuy.greeting() : "", buildOfferListText(), "");\n          if (questGuy.offerCount() <= 0) {\n            var ctx = questGuyCtxSafe();\n            ctx.conversationResult = "declined";\n            String farewell = (zqsWqgDock != null) ? zqsWqgDock.buildFarewell(ctx) : "";\n            if (farewell == null || farewell.isEmpty()) farewell = "(MISSING_ZQS_FAREWELL_TEXT)";\n            wqgFarewellText = farewell;\n            wqgAutoCloseAtEpochSec = nowSec + 30L;\n            wqgDialogText = joinWqgText(wqgDialogText, "", farewell);\n          }\n          wqgScrollToBottom();\n        }\n      }\n    }\n\n    font.setColor(0f, 0f, 0f, 1f);\n  }\n\n  // ---------------- Questlogbuch (Schema UI) ----------------\n\n  private void drawQuestLogHubPanel() {\n    // Center window: Questlogbuch hub. Buttons toggle the two sub-windows.\n    // Hub sizing: same width as inventory-max, but only 25% of its height.\n    float uiScale = 0.85f;\n    float[] invMax = scratchInvMax;\n    calcInventoryMaxPanelWH(uiScale, invMax);\n    float panelW = invMax[0];\n    float panelH = invMax[1] * 0.25f;\n\n    float x0Default = (Gdx.graphics.getWidth() - panelW) * 0.5f;\n    float y0Default = (Gdx.graphics.getHeight() - panelH) * 0.5f;\n\n    if (Float.isNaN(questHubAnchorX) || Float.isNaN(questHubAnchorY)) {\n      questHubAnchorX = x0Default;\n      questHubAnchorY = y0Default;\n    }\n\n    // Drag handling (head zone)\n    boolean allowPanelDrag = !pricingOpen && !walletOpen && !buyPopup && !sellPopup && !dragArmed && !dragActive;\n    if (allowPanelDrag) {\n      float mx = Gdx.input.getX();\n      float my = uiMouseYUp();\n      float headH = 64f * uiScale;\n      if (Gdx.input.justTouched()) {\n        float headY = questHubAnchorY + panelH - headH;\n        if (hitRect(mx, my, questHubAnchorX, headY, panelW, headH)) {\n          questHubDrag = true;\n          questHubDragDx = mx - questHubAnchorX;\n          questHubDragDy = my - questHubAnchorY;\n        }\n      }\n      if (questHubDrag) {\n        if (Gdx.input.isTouched()) {\n          questHubAnchorX = mx - questHubDragDx;\n          questHubAnchorY = my - questHubDragDy;\n        } else {\n          questHubDrag = false;\n        }\n      }\n    } else {\n      questHubDrag = false;\n    }\n\n    // clamp\n    questHubAnchorX = MathUtils.clamp(questHubAnchorX, 0f, Math.max(0f, Gdx.graphics.getWidth() - panelW));\n    questHubAnchorY = MathUtils.clamp(questHubAnchorY, 0f, Math.max(0f, Gdx.graphics.getHeight() - panelH));\n\n    float x0 = questHubAnchorX;\n    float y0 = questHubAnchorY;\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    font.setColor(0f, 0f, 0f, 1f);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 409-409

`java
\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.setColor(0f, 0f, 0f, 1f);\n  }\n\n\n\n// ---------------- Drag & Drop + Buy Popup (Merchant) ----------------\n\nprivate static float uiMouseYUp() {\n  return Gdx.graphics.getHeight() - Gdx.input.getY();\n}\n  void drawHoverIdsAtCursor() {\n  // Screen-space cursor position\n  float mx = Gdx.input.getX();\n  float my = uiMouseYUp();\n\n  // World-space info at cursor\n  int tx = (int)Math.floor(mouseWorldX / World.TILE_WORLD);\n  int ty = (int)Math.floor(mouseWorldY / World.TILE_WORLD);\n\n  int biome = world.biomeIdAtTile(tx, ty);\n  boolean water = world.isWaterAtTile(tx, ty);\n  boolean blocked = world.isBlockedAtTile(tx, ty);\n\n  int e = pickEntityNear(mouseWorldX, mouseWorldY);\n\n  StringBuilder sb = new StringBuilder(192);\n  sb.append("tile[").append(tx).append(',').append(ty).append("] biome=").append(biome);\n  if (water) sb.append(" water");\n  if (blocked) sb.append(" blocked");\n\n  // FUSA Story debug: show tile-tree presence/cut state at cursor.\n  if (AREA_MODE) {\n    sb.append(" | tid=").append(worldMap != null ? worldMap.curTemplateId : "");\n    if (areaTreePresentBits == null || areaTreeCutBits == null || areaTreeW <= 0 || areaTreeH <= 0) {\n      sb.append(" | tileTrees=OFF");\n    } else if (tx >= 0 && ty >= 0 && tx < areaTreeW && ty < areaTreeH) {\n      int bit = bitIndex(tx, ty, areaTreeW);\n      boolean presentT = bitGet(areaTreePresentBits, bit);\n      boolean cutT = bitGet(areaTreeCutBits, bit);\n      if (presentT) sb.append(cutT ? " | tree=STUMP" : " | tree=TREE");\n      else sb.append(" | tree=none");\n    } else {\n      sb.append(" | tree=oob");\n    }\n  }\n\n  if (e >= 0) {\n    sb.append("  |  e=").append(e).append(" ").append(entities.type[e]);\n    if (entities.type[e] == EntityType.ITEM_DROP) {\n      sb.append(" item=").append(entities.itemId[e]).append(" x").append(entities.itemAmount[e]);\n    } else if (entities.type[e] == EntityType.MERCHANT_ELF || entities.type[e] == EntityType.MERCHANT_WANDERING) {\n      sb.append(" data0=").append(entities.data0[e]);\n    }\n  }\n\n  // Draw slightly offset from cursor so it doesn't sit under the pointer.\n  float x = mx + 14f;\n  float y = my + 18f;\n\n  // Keep inside screen bounds (roughly; we keep it simple).\n  float maxX = Gdx.graphics.getWidth() - 10f;\n  float maxY = Gdx.graphics.getHeight() - 10f;\n  if (x > maxX) x = maxX;\n  if (y > maxY) y = maxY;\n\n  font.setColor(1f, 0f, 0f, 1f);\n  font.getData().setScale(0.85f * UI_FONT_SCALE);\n  font.draw(batch, sb.toString(), x, y);\n  font.getData().setScale(1.0f * UI_FONT_SCALE);\n  font.setColor(0f, 0f, 0f, 1f);\n}\n\nprivate int pickEntityNear(float wx, float wy) {\n  int best = -1;\n  float bestD2 = Float.POSITIVE_INFINITY;\n\n  for (int i = 0; i < Entities.MAX; i++) {\n    if (!entities.alive[i]) continue;\n\n    float dx = entities.x[i] - wx;\n    float dy = entities.y[i] - wy;\n\n    float r = EntityMetrics.radius(entities.type[i]);\n    // Make debug selection a bit more forgiving than interaction.\n    float rr = (r + 10f);\n    float d2 = dx*dx + dy*dy;\n    if (d2 <= rr*rr && d2 < bestD2) {\n      bestD2 = d2;\n      best = i;\n    }\n  }\n\n  return best;\n}\n\nprivate int shopHitSlot(float mx, float my) {\n  if (!shopOpen) return -1;\n  if (!shopLayoutValid) return -1;\n  int cols = shopCols;\n  int rows = shopRows;\n  int shown = Math.min(cols * rows, shopOfferCount);\n  for (int i = 0; i < shown; i++) {\n    float sx = shopSlotsX0 + (i % cols) * (shopSlotPx + shopPadPx);\n    float sy = shopSlotsY0 + (i / cols) * (shopSlotPx + shopPadPx);\n    if (mx >= sx && mx <= sx + shopSlotPx && my >= sy && my <= sy + shopSlotPx) return i;\n  }\n  return -1;\n}\n\nprivate int invHitToolSlot(float mx, float my) {\n  if (!invOpen) return -1;\n  if (invSlotPx <= 0f) return -1;\n  int gridW = Inventory.GRID_W;\n  int rows = Math.max(0, invToolRowsDrawn);\n  for (int y = 0; y < rows; y++) {\n    for (int x = 0; x < gridW; x++) {\n      int idx = y * gridW + x;\n      float sx = invToolSlotsX0 + x * (invSlotPx + invPadPx);\n      float sy = invToolSlotsY0 + y * (invSlotPx + invPadPx);\n      if (mx >= sx && mx <= sx + invSlotPx && my >= sy && my <= sy + invSlotPx) return idx;\n    }\n  }\n  return -1;\n}\n\nprivate int invHitNormalSlot(float mx, float my) {\n  if (!invOpen) return -1;\n  if (invSlotPx <= 0f) return -1;\n  int gridW = Inventory.GRID_W;\n  int rows = Math.max(0, invNormRowsDrawn);\n  for (int y = 0; y < rows; y++) {\n    for (int x = 0; x < gridW; x++) {\n      int idx = y * gridW + x;\n      float sx = invNormSlotsX0 + x * (invSlotPx + invPadPx);\n      float sy = invNormSlotsY0 + y * (invSlotPx + invPadPx);\n      if (mx >= sx && mx <= sx + invSlotPx && my >= sy && my <= sy + invSlotPx) return idx;\n    }\n  }\n  return -1;\n}\n\nprivate int hotbarHit(float mx, float my) {\n  // hotbar is always visible; layout is deterministic even if not cached yet\n  float slot = (hotbarSlotPx > 0f) ? hotbarSlotPx : 144f;\n  float pad = (hotbarPadPx > 0f) ? hotbarPadPx : 18f;\n  float barW = hotbar.length * slot + (hotbar.length - 1) * pad;\n  float x0 = (hotbarSlotPx > 0f) ? hotbarX0 : (Gdx.graphics.getWidth() - barW) * 0.5f;\n  float y0 = (hotbarSlotPx > 0f) ? hotbarY0 : 0f;\n\n  for (int i = 0; i < hotbar.length; i++) {\n    float sx = x0 + i * (slot + pad);\n    float sy = y0;\n    if (mx >= sx && mx <= sx + slot && my >= sy && my <= sy + slot) return i;\n  }\n  return -1;\n}\n\nprivate int hotbarNearestSlot(float mx, float my) {\n  // Choose the slot whose center is nearest at drop time.\n  float slot = (hotbarSlotPx > 0f) ? hotbarSlotPx : 144f;\n  float pad = (hotbarPadPx > 0f) ? hotbarPadPx : 18f;\n  float barW = hotbar.length * slot + (hotbar.length - 1) * pad;\n  float x0 = (hotbarSlotPx > 0f) ? hotbarX0 : (Gdx.graphics.getWidth() - barW) * 0.5f;\n  float y0 = (hotbarSlotPx > 0f) ? hotbarY0 : 0f;\n\n  int best = -1;\n  float bestD2 = Float.POSITIVE_INFINITY;\n  for (int i = 0; i < hotbar.length; i++) {\n    float sx = x0 + i * (slot + pad);\n    float sy = y0;\n    float cx = sx + slot * 0.5f;\n    float cy = sy + slot * 0.5f;\n    float dx = mx - cx;\n    float dy = my - cy;\n    float d2 = dx * dx + dy * dy;\n    if (d2 < bestD2) { bestD2 = d2; best = i; }\n  }\n  return best;\n}\n\nprivate void hotbarMoveWithShift(int from, int to) {\n  if (from < 0 || from >= hotbar.length) return;\n  if (to < 0 || to >= hotbar.length) return;\n  if (from == to) return;\n\n  int moving = hotbar[from];\n  if (moving < 0) return;\n\n  // Remove first.\n  hotbar[from] = -1;\n\n  if (hotbar[to] < 0) {\n    hotbar[to] = moving;\n    return;\n  }\n\n  // Prefer shifting to the right; if no free slot to the right, shift left.\n  int free = -1;\n  for (int i = to; i < hotbar.length; i++) {\n    if (hotbar[i] < 0) { free = i; break; }\n  }\n  if (free >= 0) {\n    for (int i = free; i > to; i--) {\n      hotbar[i] = hotbar[i - 1];\n    }\n    hotbar[to] = moving;\n    return;\n  }\n\n  for (int i = to; i >= 0; i--) {\n    if (hotbar[i] < 0) { free = i; break; }\n  }\n  if (free >= 0) {\n    for (int i = free; i < to; i++) {\n      hotbar[i] = hotbar[i + 1];\n    }\n    hotbar[to] = moving;\n    return;\n  }\n\n  // No free slot: fallback to swap (never drop/lose items).\n  int tmp = hotbar[to];\n  hotbar[to] = moving;\n  hotbar[from] = tmp;\n}\n\nprivate void uiHandleDragDropAndPopup() {\n  float mx = Gdx.input.getX();\n  float my = uiMouseYUp();\n\n  boolean lmbDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);\n  boolean lmbJustPressed = lmbDown && !prevLmbDown;\n  boolean lmbJustReleased = !lmbDown && prevLmbDown;\n\n  // While popup is active: only popup interactions.\n  if (buyPopup || sellPopup) {\n    if (lmbJustReleased) {\n      if (buyPopup) buyHandleMouseClick(mx, my);\n      if (sellPopup) sellHandleMouseClick(mx, my);\n    }\n    prevLmbDown = lmbDown;\n    return;\n  }\n\n  // Arm drag from shop slot OR from build slot OR from inventory slots OR from hotbar slot.\n  if (lmbJustPressed) {\n    int hoverShop = shopHitSlot(mx, my);\n    if (hoverShop >= 0) {\n      dragArmed = true;\n      dragActive = false;\n      dragSrc = DRAG_SRC_SHOP;\n      dragIndex = hoverShop;\n      dragItemId = shopItemId[hoverShop];\n      dragStartX = mx;\n      dragStartY = my;\n    } else {\n      int hoverBuild = buildHitSlot(mx, my);\n      if (hoverBuild >= 0) {\n        dragArmed = true;\n        dragActive = false;\n        dragSrc = DRAG_SRC_BUILD;\n        dragIndex = hoverBuild;\n        dragItemId = -1;\n        dragStartX = mx;\n        dragStartY = my;\n      } else {\n        int toolSlot = invHitToolSlot(mx, my);\n        if (toolSlot >= 0) {\n          int id = inv.getToolItemId(toolSlot);\n          int c = inv.getToolCount(toolSlot);\n          if (id >= 0 && c > 0) {\n            dragArmed = true;\n            dragActive = false;\n            dragSrc = DRAG_SRC_TOOLINV;\n            dragIndex = toolSlot;\n            dragItemId = id;\n            dragStartX = mx;\n            dragStartY = my;\n          }\n        } else {\n          int normalSlot = invHitNormalSlot(mx, my);\n          if (normalSlot >= 0) {\n            int id = inv.getNormalItemId(normalSlot);\n            int c = inv.getNormalCount(normalSlot);\n            if (id >= 0 && c > 0) {\n              dragArmed = true;\n              dragActive = false;\n              dragSrc = DRAG_SRC_NORMINV;\n              dragIndex = normalSlot;\n              dragItemId = id;\n              dragStartX = mx;\n              dragStartY = my;\n            }\n          } else {\n            int hb = hotbarHit(mx, my);\n            if (hb >= 0) {\n              int id = hotbar[hb];\n              if (id >= 0) {\n                dragArmed = true;\n                dragActive = false;\n                dragSrc = DRAG_SRC_HOTBAR;\n                dragIndex = hb;\n                dragItemId = id;\n                dragStartX = mx;\n                dragStartY = my;\n              }\n            }\n          }\n        }\n      }\n    }\n  }\n\n  // Promote armed drag into active drag if user moves far enough.\n  if (lmbDown && dragArmed && !dragActive) {\n    float dx = mx - dragStartX;\n    float dy = my - dragStartY;\n    if (dx * dx + dy * dy > 9f * 9f) {\n      dragActive = true;\n    }\n  }\n\n  // While dragging build icon: rotate in 8 angles with R (45° steps)\n  if (dragArmed && dragActive && dragSrc == DRAG_SRC_BUILD && Gdx.input.isKeyJustPressed(Input.Keys.R)) {\n    buildRot = (buildRot + 45f) % 360f;\n  }\n\n  // Drop / click resolution.\n  if (lmbJustReleased && dragArmed) {\n    if (dragActive) {\n      switch (dragSrc) {\n        case DRAG_SRC_SHOP -> {\n          // Drop shop item into inventory -> open buy popup\n          int normalSlot = invHitNormalSlot(mx, my);\n          int toolSlot = invHitToolSlot(mx, my);\n          if (normalSlot >= 0 || toolSlot >= 0) {\n            openBuyPopup(dragIndex, normalSlot, toolSlot);\n          } else {\n            // dropped nowhere -> ignore\n            game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n        case DRAG_SRC_BUILD -> {\n          // Drop build icon into world -> place\n          buildSel = MathUtils.clamp(dragIndex, 0, 4);\n          // Only place if not dropped inside the build panel\n          if (!(buildLayoutValid && mx >= buildPanelX0 && mx <= buildPanelX0 + buildPanelW && my >= buildPanelY0 && my <= buildPanelY0 + buildPanelH)) {\n            placeBuildAtMouse(mouseWorldX, mouseWorldY);\n            game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n          } else {\n            game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n        case DRAG_SRC_TOOLINV -> {\n          // Drop inventory item onto merchant panel => SELL (popup)\n          if (shopOpen && shopLayoutValid && mx >= shopPanelX0 && mx <= shopPanelX0 + shopPanelW && my >= shopPanelY0 && my <= shopPanelY0 + shopPanelH) {\n            openSellPopup(dragItemId, dragSrc, dragIndex);\n          } else {\n            // Tool inventory: Drop tool onto hotbar OR reorder inside tool inventory.\n            int hb = hotbarHit(mx, my);\n            if (hb >= 0) {\n              hotbar[hb] = dragItemId;\n              game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n            } else {\n              int dropTool = invHitToolSlot(mx, my);\n              if (dropTool >= 0 && dropTool != dragIndex) {\n                inv.swapToolSlots(dragIndex, dropTool);\n                game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n              } else {\n                game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n              }\n            }\n          }\n        }\n        case DRAG_SRC_NORMINV -> {\n          // Drop inventory item onto merchant panel => SELL (popup)\n          if (shopOpen && shopLayoutValid && mx >= shopPanelX0 && mx <= shopPanelX0 + shopPanelW && my >= shopPanelY0 && my <= shopPanelY0 + shopPanelH) {\n            openSellPopup(dragItemId, dragSrc, dragIndex);\n          } else {\n            // Normal inventory drag dropped elsewhere -> ignore\n            game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n        case DRAG_SRC_HOTBAR -> {\n          // Reorder hotbar by drag&drop.\n          int target = hotbarNearestSlot(mx, my);\n          if (target >= 0) {\n            hotbarMoveWithShift(dragIndex, target);\n            game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n          } else {\n            game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n        default -> {\n          // ignore\n        }\n      }\n    } else {\n      // Not a drag: do nothing on LMB (avoid accidental sell/buy). RMB is quick-buy.\n    }\n\n    // reset drag state\n    dragArmed = false;\n    dragActive = false;\n    dragSrc = DRAG_SRC_NONE;\n    dragItemId = -1;\n    dragIndex = -1;\n  }\n\n  prevLmbDown = lmbDown;\n}\n\nprivate void openBuyPopup(int offerIdx, int normalSlot, int toolSlot) {\n  if (offerIdx < 0 || offerIdx >= shopOfferCount) return;\n  int itemId = shopItemId[offerIdx];\n  int priceEach = shopBuy[offerIdx];\n  if (itemId < 0 || priceEach <= 0) return;\n\n  boolean isTool = inv.isToolItem(itemId);\n\n  buyPopup = true;\n  buyOfferIdx = offerIdx;\n  buyItemId = itemId;\n  buyPriceEach = priceEach;\n\n  // Preferred target: only meaningful in the correct area.\n  if (isTool) {\n    buyPreferredToolArea = true;\n    buyPreferredSlot = (toolSlot >= 0) ? toolSlot : -1;\n  } else {\n    buyPreferredToolArea = false;\n    buyPreferredSlot = (normalSlot >= 0) ? normalSlot : -1;\n  }\n\n  long affordable = wallet.copper / (long) priceEach;\n  long cap = isTool ? (long) inv.maxAddable(itemId) * (long) inv.stackMax(itemId) : 9_999L;\n  long max = Math.min(affordable, cap);\n  if (max < 1) {\n    buyPopup = false;\n    game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n    toast = "Not enough coins / no space";\n    toastT = 3f;\n    return;\n  }\n\n  buyMax = (int) Math.min(Integer.MAX_VALUE, max);\n  buyAmount = MathUtils.clamp(1, 1, buyMax);\n  buyBuffer = String.valueOf(buyAmount);\n\n  game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n}\n\nprivate void buySetAmount(int v) {\n  if (!buyPopup) return;\n  v = MathUtils.clamp(v, 1, Math.max(1, buyMax));\n  buyAmount = v;\n  buyBuffer = String.valueOf(v);\n}\n\nprivate void buyApplyBuffer() {\n  if (!buyPopup) return;\n  if (buyBuffer == null || buyBuffer.isEmpty()) {\n    buyAmount = 1;\n    return;\n  }\n  try {\n    int v = Integer.parseInt(buyBuffer);\n    v = MathUtils.clamp(v, 1, Math.max(1, buyMax));\n    buyAmount = v;\n  } catch (NumberFormatException ignored) {\n    buyAmount = 1;\n  }\n}\n\nprivate void craftApplyQtyBuffer() {\n  if (!(craftOpen && craftQtyFocusIdx >= 0)) return;\n  if (craftQtyBuffer == null || craftQtyBuffer.isEmpty()) craftQtyBuffer = "1";\n  try {\n    int v = Integer.parseInt(craftQtyBuffer);\n    v = MathUtils.clamp(v, 0, 99999);\n    if (craftQtyFocusIdx >= 0 && craftQtyFocusIdx < craftQtyByRecipe.length) {\n      craftQtyByRecipe[craftQtyFocusIdx] = Math.max(0, v);\n    }\n  } catch (NumberFormatException ignored) {\n    if (craftQtyFocusIdx >= 0 && craftQtyFocusIdx < craftQtyByRecipe.length) craftQtyByRecipe[craftQtyFocusIdx] = 1;\n  }\n}\n\nprivate void buyCancel() {\n  buyPopup = false;\n  buyOfferIdx = -1;\n  buyItemId = -1;\n  buyPreferredSlot = -1;\n  buyPreferredToolArea = false;\n  buyAmount = 1;\n  buyMax = 1;\n  buyBuffer = "";\n  dragArmed = false;\n  dragActive = false;\n  dragSrc = DRAG_SRC_NONE;\n  dragItemId = -1;\n  dragIndex = -1;\n}\n\nprivate void buyConfirm() {\n  if (!buyPopup) return;\n  if (buyOfferIdx < 0 || buyOfferIdx >= shopOfferCount) { buyCancel(); return; }\n  if (buyItemId < 0 || buyItemId >= 60) { buyCancel(); return; }\n  if (buyPriceEach <= 0) { buyCancel(); return; }\n\n  int want = MathUtils.clamp(buyAmount, 1, Math.max(1, buyMax));\n  long affordable = wallet.copper / (long) buyPriceEach;\n  if (affordable <= 0) {\n    game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n    toast = "Not enough coins";\n    toastT = 3f;\n    return;\n  }\n\n  boolean isTool = inv.isToolItem(buyItemId);\n  long cap = isTool ? (long) inv.maxAddable(buyItemId) * (long) inv.stackMax(buyItemId) : 9_999L;\n  int canBuy = (int) Math.min((long) want, Math.min(affordable, cap));\n  if (canBuy <= 0) {\n    game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n    toast = "No space";\n    toastT = 3f;\n    return;\n  }\n\n  int added = inv.addPreferred(buyItemId, canBuy, buyPreferredToolArea, buyPreferredSlot);\n  if (added <= 0) {\n    game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n    toast = "No space";\n    toastT = 3f;\n    return;\n  }\n\n  wallet.spendCopper((long) buyPriceEach * (long) added);\n  game.audio.sfx("audio/sfx/craft.wav", game.audio.sfxVolume(game.settings));\n  toast = (added == want) ? ("Bought x" + added) : ("Bought x" + added + " (full)");\n  toastT = 3f;\n  buyCancel();\n}\n\nprivate void buyHandleMouseClick(float mx, float my) {\n  // popup layout (must match drawDragGhostAndBuyPopup)\n  float w = 720f;\n  float h = 360f;\n  float x0 = (Gdx.graphics.getWidth() - w) * 0.5f;\n  float y0 = (Gdx.graphics.getHeight() - h) * 0.5f;\n\n  float btnW = 220f;\n  float btnH = 72f;\n  float pad = 28f;\n  float buyX = x0 + w - pad - btnW;\n  float buyY = y0 + pad;\n  float cancelX = x0 + pad;\n  float cancelY = y0 + pad;\n\n  // qty buttons\n  float qy = y0 + h * 0.5f - 28f;\n  float qx = x0 + w * 0.5f - 220f;\n  float smallW = 96f;\n  float smallH = 64f;\n\n  // -10, -1, +1, +10, MAX\n  float b0x = qx;\n  float b1x = qx + (smallW + 14f);\n  float b2x = qx + 2f * (smallW + 14f);\n  float b3x = qx + 3f * (smallW + 14f);\n  float b4x = qx + 4f * (smallW + 14f);\n\n  if (mx >= cancelX && mx <= cancelX + btnW && my >= cancelY && my <= cancelY + btnH) {\n    game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n    buyCancel();\n    return;\n  }\n  if (mx >= buyX && mx <= buyX + btnW && my >= buyY && my <= buyY + btnH) {\n    buyConfirm();\n    return;\n  }\n\n  // Qty buttons (refactor: switch instead of if chain)\n  if (my >= qy && my <= qy + smallH) {\n    int btn = -1;\n    if (mx >= b0x && mx <= b0x + smallW) btn = 0;\n    else if (mx >= b1x && mx <= b1x + smallW) btn = 1;\n    else if (mx >= b2x && mx <= b2x + smallW) btn = 2;\n    else if (mx >= b3x && mx <= b3x + smallW) btn = 3;\n    else if (mx >= b4x && mx <= b4x + smallW) btn = 4;\n\n    if (btn >= 0) {\n      switch (btn) {\n        case 0 -> buySetAmount(buyAmount - 10);\n        case 1 -> buySetAmount(buyAmount - 1);\n        case 2 -> buySetAmount(buyAmount + 1);\n        case 3 -> buySetAmount(buyAmount + 10);\n        case 4 -> buySetAmount(buyMax);\n        default -> { /* no-op */ }\n      }\n    }\n  }\n}\n\n// --- SELL POPUP (mirror of buy popup, minimal logic for compile + functionality) ---\n\nprivate void openSellPopup(int itemId, int src, int srcIndex) {\n  if (itemId < 0) return;\n\n  // Determine how many items are available in the dragged slot.\n  int available = 0;\n  if (src == DRAG_SRC_NORMINV) {\n    available = inv.getNormalCount(srcIndex);\n  } else if (src == DRAG_SRC_TOOLINV) {\n    available = inv.getToolCount(srcIndex);\n  }\n  if (available <= 0) {\n    game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n    return;\n  }\n\n  sellPopup = true;\n  sellItemId = itemId;\n  sellPriceEachRaw = Math.max(1, priceBook.getBaseCopper(itemId));\n\n  // Apply the same skill multiplier as shopSellItem() so UI matches actual payout.\n  int barterLv = (SK_BARTER >= 0 && SK_BARTER < progress.skillLv.length) ? progress.skillLv[SK_BARTER] : 1;\n  int negoLv = (SK_NEGOTIATION >= 0 && SK_NEGOTIATION < progress.skillLv.length) ? progress.skillLv[SK_NEGOTIATION] : 1;\n  float sellMul = 1f;\n  sellMul *= 1f + 0.05f * Math.max(0, barterLv - 1);\n  sellMul *= 1f + 0.05f * Math.max(0, negoLv - 1);\n  sellPriceEach = Math.max(1, (int) Math.floor(sellPriceEachRaw * sellMul));\n\n  sellMax = Math.max(1, available);\n  sellAmount = 1;\n// Nicht fertiges Feature:   sellBuffer = String.valueOf(sellAmount);\n\n  game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n}\n\nprivate void sellSetAmount(int v) {\n  if (!sellPopup) return;\n  v = MathUtils.clamp(v, 1, Math.max(1, sellMax));\n  sellAmount = v;\n// Nicht fertiges Feature:   sellBuffer = String.valueOf(v);\n}\n\nprivate void sellCancel() {\n  sellPopup = false;\n  sellItemId = -1;\n  sellAmount = 1;\n  sellMax = 1;\n  sellPriceEach = 0;\n  sellPriceEachRaw = 0;\n// Nicht fertiges Feature:   sellBuffer = "";\n  dragArmed = false;\n  dragActive = false;\n  dragSrc = DRAG_SRC_NONE;\n  dragItemId = -1;\n  dragIndex = -1;\n}\n\nprivate void sellConfirm() {\n  if (!sellPopup) return;\n  int want = MathUtils.clamp(sellAmount, 1, Math.max(1, sellMax));\n  shopSellItem(sellItemId, sellPriceEachRaw, want);\n  toast = "Sold x" + want;\n  toastT = 3f;\n  sellCancel();\n}\n\nprivate void sellHandleMouseClick(float mx, float my) {\n  // popup layout (must match drawDragGhostAndBuyPopup)\n  float w = 720f;\n  float h = 360f;\n  float x0 = (Gdx.graphics.getWidth() - w) * 0.5f;\n  float y0 = (Gdx.graphics.getHeight() - h) * 0.5f;\n\n  float btnW = 220f;\n  float btnH = 72f;\n  float pad = 28f;\n  float okX = x0 + w - pad - btnW;\n  float okY = y0 + pad;\n  float cancelX = x0 + pad;\n  float cancelY = y0 + pad;\n\n  // qty buttons\n  float qy = y0 + h * 0.5f - 28f;\n  float qx = x0 + w * 0.5f - 220f;\n  float smallW = 96f;\n  float smallH = 64f;\n\n  // -10, -1, +1, +10, MAX\n  float b0x = qx;\n  float b1x = qx + (smallW + 14f);\n  float b2x = qx + 2f * (smallW + 14f);\n  float b3x = qx + 3f * (smallW + 14f);\n  float b4x = qx + 4f * (smallW + 14f);\n\n  if (mx >= cancelX && mx <= cancelX + btnW && my >= cancelY && my <= cancelY + btnH) {\n    game.audio.sfx("audio/sfx/ui_back.wav", game.audio.sfxVolume(game.settings));\n    sellCancel();\n    return;\n  }\n  if (mx >= okX && mx <= okX + btnW && my >= okY && my <= okY + btnH) {\n    sellConfirm();\n    return;\n  }\n\n  // Qty buttons (refactor: switch instead of if chain)\n  if (my >= qy && my <= qy + smallH) {\n    int btn = -1;\n    if (mx >= b0x && mx <= b0x + smallW) btn = 0;\n    else if (mx >= b1x && mx <= b1x + smallW) btn = 1;\n    else if (mx >= b2x && mx <= b2x + smallW) btn = 2;\n    else if (mx >= b3x && mx <= b3x + smallW) btn = 3;\n    else if (mx >= b4x && mx <= b4x + smallW) btn = 4;\n\n    if (btn >= 0) {\n      switch (btn) {\n        case 0 -> sellSetAmount(sellAmount - 10);\n        case 1 -> sellSetAmount(sellAmount - 1);\n        case 2 -> sellSetAmount(sellAmount + 1);\n        case 3 -> sellSetAmount(sellAmount + 10);\n        case 4 -> sellSetAmount(sellMax);\n        default -> { /* no-op */ }\n      }\n    }\n  }\n}\n\nprivate void drawDragGhostAndBuyPopup() {\n  float mx = Gdx.input.getX();\n  float my = uiMouseYUp();\n\n  // Drag ghost\n  if (dragActive) {\n    TextureRegion icon = null;\n    if (dragSrc == DRAG_SRC_BUILD) {\n      EntityType[] buildTypes = {\n          EntityType.BUILD_WORKBENCH,\n          EntityType.BUILD_BED,\n          EntityType.BUILD_CAMPFIRE,\n          EntityType.BUILD_LAMP\n      };\n      int idx = MathUtils.clamp(dragIndex, 0, buildTypes.length - 1);\n    icon = entityRegions.forEntity(buildTypes[idx], buildRot, -1, 0f, 0f, (byte)2, 0f);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 409-409

`java
    } else if (dragItemId >= 0) {\n      icon = entityRegions.itemIcon(dragItemId);\n    }\n\n    if (icon != null) {\n      float s = 128f;\n      batch.setColor(1f, 1f, 1f, 0.85f);\n      batch.draw(icon, mx - s * 0.5f, my - s * 0.5f, s, s);\n      batch.setColor(1f, 1f, 1f, 1f);\n    }\n  }\n\n  // Trade popup (modal)\n  if (!buyPopup && !sellPopup) return;\n\n  float w = 720f;\n  float h = 360f;\n  float x0 = (Gdx.graphics.getWidth() - w) * 0.5f;\n  float y0 = (Gdx.graphics.getHeight() - h) * 0.5f;\n\n  batch.setColor(1f, 1f, 1f, 1f);\n  batch.draw(uiRegions.panelSlots, x0, y0, w, h);\n\n  boolean tradeBuy = buyPopup;\n  int itemId = tradeBuy ? buyItemId : sellItemId;\n  int priceEach = tradeBuy ? buyPriceEach : sellPriceEach;\n  int maxQty = tradeBuy ? buyMax : sellMax;\n  int qty = tradeBuy ? buyAmount : sellAmount;\n\n  String name = (itemId >= 0 && itemId < data.items.length && data.items[itemId] != null) ? data.items[itemId].name : ("item_" + itemId);\n\n  // Header\n  font.getData().setScale(1.7f);\n  font.setColor(0f, 0f, 0f, 1f);\n  font.draw(batch, (tradeBuy ? "BUY: " : "SELL: ") + name, x0 + 28f, y0 + h - 26f);\n  font.getData().setScale(1.0f * UI_FONT_SCALE);\n  font.setColor(0f, 0f, 0f, 1f);\n\n  // Icon\n  TextureRegion icon = entityRegions.itemIcon(itemId);\n  if (icon != null) {\n    float s = 140f;\n    batch.draw(icon, x0 + 44f, y0 + h - 210f, s, s);\n  }\n\n  // Info\n  font.setColor(0f, 0f, 0f, 1f);\n  font.draw(batch, "Price each: " + priceEach + "c", x0 + 220f, y0 + h - 92f);\n  if (tradeBuy) {\n    font.draw(batch, "Max: " + maxQty + "    Wallet: " + wallet.copper + "c", x0 + 220f, y0 + h - 114f);\n  } else {\n    long total = (long) priceEach * (long) qty;\n    font.draw(batch, "Max: " + maxQty + "    Total: +" + total + "c", x0 + 220f, y0 + h - 114f);\n  }\n  font.setColor(0f, 0f, 0f, 1f);\n\n  // Quantity display\n  font.getData().setScale(1.6f);\n  font.setColor(0f, 0f, 0f, 1f);\n  font.draw(batch, "QTY: " + qty, x0 + w * 0.5f - 72f, y0 + h * 0.62f);\n  font.getData().setScale(1.0f * UI_FONT_SCALE);\n  font.setColor(0f, 0f, 0f, 1f);\n\n  // Buttons\n  boolean lmbDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);\n\n  float btnW = 220f;\n  float btnH = 72f;\n  float pad = 28f;\n  float okX = x0 + w - pad - btnW;\n  float okY = y0 + pad;\n  float cancelX = x0 + pad;\n  float cancelY = y0 + pad;\n\n  boolean hoverOk = mx >= okX && mx <= okX + btnW && my >= okY && my <= okY + btnH;\n  boolean hoverCancel = mx >= cancelX && mx <= cancelX + btnW && my >= cancelY && my <= cancelY + btnH;\n\n  batch.draw((hoverCancel && lmbDown) ? uiRegions.buttonPressed : uiRegions.button, cancelX, cancelY, btnW, btnH);\n  batch.draw((hoverOk && lmbDown) ? uiRegions.buttonPressed : uiRegions.button, okX, okY, btnW, btnH);\n\n  font.setColor(0f, 0f, 0f, 1f);\n  font.getData().setScale(1.3f);\n  font.draw(batch, "CANCEL", cancelX + 48f, cancelY + 48f);\n  font.draw(batch, tradeBuy ? "BUY" : "SELL", okX + 78f, okY + 48f);\n  font.getData().setScale(1.0f * UI_FONT_SCALE);\n  font.setColor(0f, 0f, 0f, 1f);\n\n  // Qty +/- buttons\n  float qy = y0 + h * 0.5f - 28f;\n  float qx = x0 + w * 0.5f - 220f;\n  float smallW = 96f;\n  float smallH = 64f;\n  float gap = 14f;\n\n  String[] labs = {"-10", "-1", "+1", "+10", "MAX"};\n  for (int i = 0; i < 5; i++) {\n    float bx = qx + i * (smallW + gap);\n    boolean hov = mx >= bx && mx <= bx + smallW && my >= qy && my <= qy + smallH;\n    batch.draw((hov && lmbDown) ? uiRegions.buttonPressed : uiRegions.button, bx, qy, smallW, smallH);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.05f);\n    font.draw(batch, labs[i], bx + 22f, qy + 42f);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.setColor(0f, 0f, 0f, 1f);\n  }\n\n}\n\n\nprivate void craftByOutput(int outItemId) {\n    RecipeDef r = craft.findByOutput(data.recipes, outItemId);\n    if (r == null) return;\n    craft.craft(inv, r);\n\n    int outId = r.outItemId;\n    int outAmount = Math.max(1, r.outAmount);\n    zqsCraftedOutputCounts.put(outId, zqsCraftedOutputCounts.get(outId, 0) + outAmount);\n  }\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private void drawCraftLines(float x, float y) {\n    // legacy text renderer (still used inside the craft panel)\n    int[] outs = {40, 14, 15};\n    for (int i=0;i<outs.length;i++) {\n      RecipeDef r = craft.findByOutput(data.recipes, outs[i]);\n      if (r == null) continue;\n      String name = (data.items[r.outItemId] != null) ? data.items[r.outItemId].name : ("item_" + r.outItemId);\n      StringBuilder req = new StringBuilder();\n      for (int k=0;k<r.inItemId.length;k++) {\n        int id = r.inItemId[k];\n        String inName = (data.items[id] != null) ? data.items[id].name : ("item_"+id);\n        if (k>0) req.append(", ");\n        req.append(inName).append("x").append(r.inAmount[k]);\n      }\n      boolean ok = craft.canCraft(inv, r);\n      font.draw(batch, (i+1) + ") " + name + (ok?" [OK] ":" [NO] ") + "<= " + req, x, y - i*22f);\n    }\n  }\n  */\n\n  private static boolean rectsOverlap(float ax, float ay, float aw, float ah, float bx, float by, float bw, float bh, float pad) {\n    float aL = ax - pad, aR = ax + aw + pad;\n    float aB = ay - pad, aT = ay + ah + pad;\n    float bL = bx - pad, bR = bx + bw + pad;\n    float bB = by - pad, bT = by + bh + pad;\n    return (aL < bR && aR > bL && aB < bT && aT > bB);\n  }\n\n  private ZqsQuestProgressSnapshot buildZqsProgressSnapshot(long nowSec) {\n    ZqsQuestProgressSnapshot s = new ZqsQuestProgressSnapshot();\n    s.epochSec = nowSec;\n    s.currentAreaTemplateId = (worldMap != null && worldMap.curTemplateId != null) ? worldMap.curTemplateId : "";\n\n    if (worldMap != null) {\n      s.consumedPois.clear();\n      s.consumedPois.addAll(worldMap.consumedPois);\n      s.removedAuthoredNodes.clear();\n      s.removedAuthoredNodes.addAll(worldMap.removedAuthoredNodes);\n    }\n\n    s.inventoryCounts.clear();\n    if (inv != null && inv.countsById != null) {\n      for (int i = 0; i < inv.countsById.length; i++) {\n        int c = inv.countsById[i];\n        if (c != 0) s.inventoryCounts.put(i, c);\n      }\n    }\n\n    s.craftedOutputCounts.clear();\n    for (com.badlogic.gdx.utils.IntIntMap.Entry e : zqsCraftedOutputCounts.entries()) {\n      s.craftedOutputCounts.put(e.key, e.value);\n    }\n    return s;\n  }\n\n  private void placePanelAvoiding(float panelW, float panelH, float prefX, float prefY,\n                                 float avoidX, float avoidY, float avoidW, float avoidH,\n                                 java.util.function.BiConsumer<Float, Float> out) {\n    float w = Gdx.graphics.getWidth();\n    float h = Gdx.graphics.getHeight();\n\n    // If preferred spot is fine, keep it.\n    float x0 = MathUtils.clamp(prefX, 0f, Math.max(0f, w - panelW));\n    float y0 = MathUtils.clamp(prefY, 0f, Math.max(0f, h - panelH));\n    if (!rectsOverlap(x0, y0, panelW, panelH, avoidX, avoidY, avoidW, avoidH, 8f)) {\n      out.accept(x0, y0);\n      return;\n    }\n\n    // Search a grid of candidate positions and pick the closest that doesn't overlap.\n    float step = 40f;\n    float bestD2 = Float.POSITIVE_INFINITY;\n    float bestX = x0, bestY = y0;\n\n    for (float yy = 0f; yy <= h - panelH; yy += step) {\n      for (float xx = 0f; xx <= w - panelW; xx += step) {\n        if (rectsOverlap(xx, yy, panelW, panelH, avoidX, avoidY, avoidW, avoidH, 8f)) continue;\n        float dx = xx - x0;\n        float dy = yy - y0;\n        float d2 = dx * dx + dy * dy;\n        if (d2 < bestD2) { bestD2 = d2; bestX = xx; bestY = yy; }\n      }\n    }\n\n    out.accept(bestX, bestY);\n  }\n\n  private void ensurePanelAnchor(float panelW, float panelH, boolean forBuild, boolean forCraft, boolean forChest) {\n    float w = Gdx.graphics.getWidth();\n    float h = Gdx.graphics.getHeight();\n\n    // Pick different default spots per panel.\n    float baseX = forBuild ? w * 0.06f : (forCraft ? w * 0.60f : w * 0.08f);\n    float baseY = forBuild ? h * 0.62f : (forCraft ? h * 0.62f : h * 0.18f);\n\n    // Small random-ish jitter (so panels don't stack perfectly). Deterministic enough per run.\n    float jx = (float) ((Math.random() - 0.5) * 80.0);\n    float jy = (float) ((Math.random() - 0.5) * 60.0);\n\n    float x0 = MathUtils.clamp(baseX + jx, 0f, Math.max(0f, w - panelW));\n    float y0 = MathUtils.clamp(baseY + jy, 0f, Math.max(0f, h - panelH));\n\n    if (forBuild) {\n      if (Float.isNaN(buildAnchorX) || Float.isNaN(buildAnchorY)) { buildAnchorX = x0; buildAnchorY = y0; }\n    }\n    if (forCraft) {\n      if (Float.isNaN(craftAnchorX) || Float.isNaN(craftAnchorY)) { craftAnchorX = x0; craftAnchorY = y0; }\n    }\n    if (forChest) {\n      if (Float.isNaN(chestAnchorX) || Float.isNaN(chestAnchorY)) { chestAnchorX = x0; chestAnchorY = y0; }\n    }\n  }\n\n  private void handlePanelDrag(boolean want, float[] axay, boolean[] dragFlag, float[] dragDxDy, float panelW, float panelH, float headerH) {\n    if (!want) {\n      dragFlag[0] = false;\n      return;\n    }\n\n    float mx = Gdx.input.getX();\n    float my = uiMouseYUp();\n\n    // start drag if just touched in header\n    if (!dragFlag[0] && Gdx.input.justTouched()) {\n      float x0 = axay[0];\n      float y0 = axay[1];\n      if (mx >= x0 && mx <= x0 + panelW && my >= y0 + panelH - headerH && my <= y0 + panelH) {\n        dragFlag[0] = true;\n        dragDxDy[0] = mx - x0;\n        dragDxDy[1] = my - y0;\n      }\n    }\n\n    // stop drag on release\n    if (dragFlag[0] && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {\n      dragFlag[0] = false;\n    }\n\n    // update\n    if (dragFlag[0]) {\n      float w = Gdx.graphics.getWidth();\n      float h = Gdx.graphics.getHeight();\n      axay[0] = MathUtils.clamp(mx - dragDxDy[0], 0f, Math.max(0f, w - panelW));\n      axay[1] = MathUtils.clamp(my - dragDxDy[1], 0f, Math.max(0f, h - panelH));\n    }\n  }\n\n  private static boolean hitRect(float px, float py, float x, float y, float w, float h) {\n    return px >= x && px <= x + w && py >= y && py <= y + h;\n  }\n\n  private int buildHitSlot(float mx, float my) {\n    if (!buildMode) return -1;\n    if (!buildLayoutValid) return -1;\n    for (int i = 0; i < buildSlotsCount; i++) {\n      float sx = buildSlotsX0 + i * (buildSlotPx + buildPadPx);\n      float sy = buildSlotsY0;\n      if (hitRect(mx, my, sx, sy, buildSlotPx, buildSlotPx)) return i;\n    }\n    return -1;\n  }\n\n  private void drawBuildPanel() {\n    float panelW = 560f;\n    float panelH = 360f;\n    float headerH = 70f;\n\n    ensurePanelAnchor(panelW, panelH, true, false, false);\n\n    float[] axay = new float[]{buildAnchorX, buildAnchorY};\n    boolean[] d = new boolean[]{buildDrag};\n    float[] dd = new float[]{buildDragDx, buildDragDy};\n    handlePanelDrag(true, axay, d, dd, panelW, panelH, headerH);\n    buildAnchorX = axay[0]; buildAnchorY = axay[1];\n    buildDrag = d[0]; buildDragDx = dd[0]; buildDragDy = dd[1];\n\n    float x0 = buildAnchorX;\n    float y0 = buildAnchorY;\n\n    batch.setColor(1f,1f,1f,1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    font.setColor(0f,0f,0f,1f);\n    font.getData().setScale(1.45f * UI_FONT_SCALE);\n    font.draw(batch, "BUILD MODE", x0 + 28f, y0 + panelH - 24f);\n\n    font.getData().setScale(1.05f * UI_FONT_SCALE);\n    font.draw(batch, "B toggle | Drag icon to world = build | Q/E rotate | Shift+RMB remove", x0 + 28f, y0 + panelH - 56f);\n\n    // Icon row (draggable)\n    float slot = 92f;\n    float pad = 16f;\n    float sx0 = x0 + 28f;\n    float sy0 = y0 + panelH - 170f;\n\n    // cache for drag hit-tests\n    buildLayoutValid = true;\n    buildPanelX0 = x0;\n    buildPanelY0 = y0;\n    buildPanelW = panelW;\n    buildPanelH = panelH;\n    buildSlotsX0 = sx0;\n    buildSlotsY0 = sy0;\n    buildSlotPx = slot;\n    buildPadPx = pad;\n\n    EntityType[] buildTypes = {\n        EntityType.BUILD_WORKBENCH,\n        EntityType.BUILD_BED,\n        EntityType.BUILD_CAMPFIRE,\n        EntityType.BUILD_LAMP\n    };\n\n    // NOTE: selection is via drag (no click-to-select)\n\n    buildSlotsCount = buildTypes.length;\n\n    for (int i = 0; i < buildTypes.length; i++) {\n      float sx = sx0 + i * (slot + pad);\n      float sy = sy0;\n      boolean selected = (i == buildSel);\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(selected ? uiRegions.slotPressed : uiRegions.slot, sx, sy, slot, slot);\n\n    TextureRegion icon = entityRegions.forEntity(buildTypes[i], 0f, -1, 0f, 0f, (byte)2, 0f);
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 409-409

`java
      if (icon != null) {\n        float iw = slot * 0.74f;\n        float ih = slot * 0.74f;\n        batch.draw(icon, sx + (slot - iw) * 0.5f, sy + (slot - ih) * 0.5f, iw, ih);\n      }\n\n      // Drag-only selection (handled in uiHandleDragDropAndPopup)\n    }\n\n    // Rotation + rule (still text, but now inside panel and large/black)\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.draw(batch, "Rotation: " + (int) buildRot + "°", x0 + 28f, y0 + 120f);\n    font.draw(batch, "Rule: cannot place on water.", x0 + 28f, y0 + 92f);\n  }\n\n  private void drawCraftPanel() {\n    // Craft panel\n    float panelW = 1230f;\n    float panelH = 540f;\n    float headerH = 70f;\n\n    ensurePanelAnchor(panelW, panelH, false, true, false);\n\n    float[] axay = new float[]{craftAnchorX, craftAnchorY};\n    boolean[] d = new boolean[]{craftDrag};\n    float[] dd = new float[]{craftDragDx, craftDragDy};\n    handlePanelDrag(true, axay, d, dd, panelW, panelH, headerH);\n    craftAnchorX = axay[0]; craftAnchorY = axay[1];\n    craftDrag = d[0]; craftDragDx = dd[0]; craftDragDy = dd[1];\n\n    float x0 = craftAnchorX;\n    float y0 = craftAnchorY;\n\n    // base panel\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    // title\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.55f * UI_FONT_SCALE);\n    font.draw(batch, "CRAFT", x0 + 28f, y0 + panelH - 24f);\n\n    font.getData().setScale(1.05f * UI_FONT_SCALE);\n    font.draw(batch, "Scroll wheel = list | Click qty box to type | Click 'Jetzt craften'", x0 + 28f, y0 + panelH - 56f);\n\n    // recipe grid\n    float slot = 108f;\n    float pad = 22f;\n    int cols = 5;\n\n    float listX0 = x0 + 28f;\n    float listTopY = y0 + panelH - 110f; // top baseline\n\n    float mx = Gdx.input.getX();\n    float my = uiMouseYUp();\n\n    // Only handle interaction when cursor is inside the craft panel.\n    boolean click = Gdx.input.justTouched() && hitRect(mx, my, x0, y0, panelW, panelH);\n\n    // Clip drawing + interaction to list area so background and interactive area match.\n    float clipX = x0 + 18f;\n    float clipY = y0 + 18f;\n    float clipW = panelW - 36f;\n    float clipH = panelH - (headerH + 28f);\n    Rectangle clip = scratch.r0;\n    clip.set(clipX, clipY, clipW, clipH);\n    Rectangle scissors = scratch.r1;\n    ScissorStack.calculateScissors(uiCam, batch.getTransformMatrix(), clip, scissors);\n    batch.flush();\n    ScissorStack.pushScissors(scissors);\n\n    craftHoverIdx = -1;\n\n    int n = data.recipes.size();\n    if (craftQtyByRecipe == null || craftQtyByRecipe.length != n) {\n      craftQtyByRecipe = new int[n];\n      for (int i = 0; i < n; i++) craftQtyByRecipe[i] = 1;\n      craftQtyFocusIdx = -1;\n      craftQtyBuffer = "1";\n    }\n\n    float rowH = slot + 56f + pad; // slot + qty/button row + pad\n\n    // clamp scroll\n    int rows = (int) Math.ceil(n / (double) cols);\n    float visibleH = panelH - 150f;\n    float contentH = rows * rowH;\n    float maxScroll = Math.max(0f, contentH - visibleH);\n    craftScroll = MathUtils.clamp(craftScroll, 0f, maxScroll);\n\n    // draw items\n    int idx = 0;\n    for (int r = 0; r < rows; r++) {\n      float baseY = listTopY - r * rowH + craftScroll;\n\n      // skip rows far outside\n      if (baseY < y0 - 220f) { idx += cols; continue; }\n      if (baseY > y0 + panelH + 220f) { idx += cols; continue; }\n\n      for (int c = 0; c < cols && idx < n; c++, idx++) {\n        var rec = data.recipes.get(idx);\n        int outId = rec.outItemId;\n\n        float sx = listX0 + c * (slot + pad);\n        float sy = baseY - slot;\n\n        boolean can1 = craft.canCraft(inv, rec);\n\n        // slot bg\n        batch.setColor(1f, 1f, 1f, 1f);\n        batch.draw(uiRegions.slot, sx, sy, slot, slot);\n\n        // output icon (dark if cannot craft)\n        TextureRegion icon = entityRegions.itemIcon(outId);\n        if (!can1) batch.setColor(1f, 1f, 1f, 0.30f);\n        if (icon != null) {\n          float iw = slot * 0.78f;\n          float ih = slot * 0.78f;\n          batch.draw(icon, sx + (slot - iw) * 0.5f, sy + (slot - ih) * 0.5f, iw, ih);\n        }\n        batch.setColor(1f, 1f, 1f, 1f);\n\n        // hover detection\n        boolean overIcon = hitRect(mx, my, sx, sy, slot, slot);\n        if (overIcon) craftHoverIdx = idx;\n\n        // qty box under icon\n        float qx = sx;\n        float qy = sy - 44f;\n        float qw = slot * 0.58f;\n        float qh = 40f;\n        batch.draw((craftQtyFocusIdx == idx) ? uiRegions.slotPressed : uiRegions.slot, qx, qy, qw, qh);\n\n        // qty text\n        int qv = (idx >= 0 && idx < craftQtyByRecipe.length) ? craftQtyByRecipe[idx] : 1;\n        font.setColor(0f, 0f, 0f, 1f);\n        font.getData().setScale(0.95f * UI_FONT_SCALE);\n        font.draw(batch, String.valueOf(qv), qx + 12f, qy + 28f);\n\n        // craft button\n        float bx = qx + qw + 10f;\n        float by = qy;\n        float bw = slot - (qw + 10f);\n        float bh = qh;\n\n        boolean canSome = can1; // gate button visibility\n        if (!canSome) batch.setColor(1f, 1f, 1f, 0.35f);\n        batch.draw(uiRegions.button, bx, by, bw, bh);\n        batch.setColor(1f, 1f, 1f, 1f);\n\n        font.setColor(0f, 0f, 0f, 1f);\n        font.getData().setScale(0.85f * UI_FONT_SCALE);\n        font.draw(batch, "Jetzt", bx + 10f, by + 26f);\n\n        // click interactions (only when inside list clip)\n        boolean inList = hitRect(mx, my, clipX, clipY, clipW, clipH);\n        if (click && inList) {\n          if (hitRect(mx, my, qx, qy, qw, qh)) {\n            craftQtyFocusIdx = idx;\n            craftQtyBuffer = String.valueOf(Math.max(0, qv));\n            // IMPORTANT: never early-return while a scissor is pushed (would freeze the viewport/clip).\n            // Consume click so it can't also trigger crafting in the same frame.\n            click = false;\n            continue;\n          }\n\n          if (hitRect(mx, my, bx, by, bw, bh)) {\n            int want = (idx >= 0 && idx < craftQtyByRecipe.length) ? craftQtyByRecipe[idx] : 1;\n            want = MathUtils.clamp(want, 1, 99999);\n            int made = 0;\n            for (int k = 0; k < want; k++) {\n              if (!craft.craft(inv, rec)) break;\n              int craftedOutId = rec.outItemId;\n              int outAmount = Math.max(1, rec.outAmount);\n              zqsCraftedOutputCounts.put(craftedOutId, zqsCraftedOutputCounts.get(craftedOutId, 0) + outAmount);\n              made++;\n            }\n            if (made > 0) game.audio.sfx("audio/sfx/craft.wav", game.audio.sfxVolume(game.settings));\n            else game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n      }\n    }\n\n    batch.flush();\n    ScissorStack.popScissors();\n\n    // Hover requirements panel (icons only) + tooltips\n    if (craftHoverIdx >= 0 && craftHoverIdx < data.recipes.size()) {\n      var rec = data.recipes.get(craftHoverIdx);\n      float reqW = 520f;\n      float reqH = 150f;\n      float rx0 = MathUtils.clamp(x0 + 24f, 0f, Math.max(0f, Gdx.graphics.getWidth() - reqW));\n      float ry0 = MathUtils.clamp(y0 + panelH + 10f, 0f, Math.max(0f, Gdx.graphics.getHeight() - reqH));\n\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(uiRegions.panelSlots, rx0, ry0, reqW, reqH);\n\n      float is = 72f;\n      float ip = 14f;\n      float ix0 = rx0 + 22f;\n      float iy0 = ry0 + reqH - 22f - is;\n\n      // Track tooltip\n      int tipItemId = -1;\n\n      // output icon\n      int outIdUi = rec.outItemId;\n      batch.draw(uiRegions.slotPressed, ix0, iy0, is, is);\n      TextureRegion outIcon = entityRegions.itemIcon(outIdUi);\n      if (outIcon != null) {\n        float iw = is * 0.78f;\n        float ih = is * 0.78f;\n        batch.draw(outIcon, ix0 + (is - iw) * 0.5f, iy0 + (is - ih) * 0.5f, iw, ih);\n      }\n      if (hitRect(mx, my, ix0, iy0, is, is)) tipItemId = outIdUi;\n\n      // arrow-ish separator\n      font.setColor(0f, 0f, 0f, 1f);\n      font.getData().setScale(1.2f * UI_FONT_SCALE);\n      font.draw(batch, "=>", ix0 + is + 14f, iy0 + 50f);\n\n      float inX = ix0 + is + 60f;\n      for (int k = 0; k < rec.inItemId.length; k++) {\n        int inId = rec.inItemId[k];\n        int need = rec.inAmount[k];\n        float sx = inX + k * (is + ip);\n        float sy = iy0;\n\n        batch.setColor(1f, 1f, 1f, 1f);\n        batch.draw(uiRegions.slot, sx, sy, is, is);\n        TextureRegion ic = entityRegions.itemIcon(inId);\n        if (ic != null) {\n          float iw = is * 0.78f;\n          float ih = is * 0.78f;\n          batch.draw(ic, sx + (is - iw) * 0.5f, sy + (is - ih) * 0.5f, iw, ih);\n        }\n\n        // count overlay (number only)\n        font.setColor(0f, 0f, 0f, 1f);\n        font.getData().setScale(0.95f * UI_FONT_SCALE);\n        font.draw(batch, String.valueOf(need), sx + 8f, sy + 22f);\n\n        if (hitRect(mx, my, sx, sy, is, is)) tipItemId = inId;\n      }\n\n      // Tooltip for item name (screen name)\n      if (tipItemId >= 0 && tipItemId < data.items.length && data.items[tipItemId] != null) {\n        String nm = data.items[tipItemId].name;\n        float tw = 380f;\n        float th = 56f;\n        float tx = MathUtils.clamp(mx + 18f, 0f, Math.max(0f, Gdx.graphics.getWidth() - tw));\n        float ty = MathUtils.clamp(my + 18f, 0f, Math.max(0f, Gdx.graphics.getHeight() - th));\n        batch.setColor(1f, 1f, 1f, 1f);\n        batch.draw(uiRegions.panel, tx, ty, tw, th);\n        font.setColor(0f, 0f, 0f, 1f);\n        font.getData().setScale(1.0f * UI_FONT_SCALE);\n        font.draw(batch, nm, tx + 16f, ty + 36f);\n      }\n    }\n  }\n\n  private void drawChestPanel() {\n    float panelW = 760f;\n    float panelH = 320f;\n    float headerH = 70f;\n\n    ensurePanelAnchor(panelW, panelH, false, false, true);\n\n    float[] axay = new float[]{chestAnchorX, chestAnchorY};\n    boolean[] d = new boolean[]{chestDrag};\n    float[] dd = new float[]{chestDragDx, chestDragDy};\n    handlePanelDrag(true, axay, d, dd, panelW, panelH, headerH);\n    chestAnchorX = axay[0]; chestAnchorY = axay[1];\n    chestDrag = d[0]; chestDragDx = dd[0]; chestDragDy = dd[1];\n\n    float x0 = chestAnchorX;\n    float y0 = chestAnchorY;\n\n    batch.setColor(1f,1f,1f,1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    font.setColor(0f,0f,0f,1f);\n    font.getData().setScale(1.45f * UI_FONT_SCALE);\n    font.draw(batch, "CHEST", x0 + 28f, y0 + panelH - 24f);\n\n    font.getData().setScale(1.05f * UI_FONT_SCALE);\n    font.draw(batch, "Click slots to transfer (Shift x10 | Ctrl x100) | E close | F take-all", x0 + 28f, y0 + panelH - 56f);\n\n    if (openChestE < 0) return;\n    int idx = entities.data0[openChestE];\n    Inventory chest = chestStore.get(idx);\n    if (chest == null) {\n      font.getData().setScale(1.0f * UI_FONT_SCALE);\n      font.draw(batch, "(broken chest)", x0 + 28f, y0 + panelH - 92f);\n      return;\n    }\n\n    int amount = 1;\n    boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);\n    boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);\n    if (shift) amount = 10;\n    if (ctrl) amount = 100;\n\n    // Tracked resources/items for chests (includes POI Hidden Chest loot).\n    // NOTE: kept fixed for UI simplicity (no scroll list yet).\n    int[] ids = {0, 1, 2, 47, 31, 33, 20};\n    String[] labels = {"Wood", "Stone", "Iron", "Arrows", "Copper", "Gold", "Sword"};\n\n    float slot = 96f;\n    float pad = 18f;\n    float colGap = 70f;\n\n    float leftX = x0 + 40f;\n    float topY = y0 + panelH - 170f;\n\n    // Column headers\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.1f * UI_FONT_SCALE);\n    font.draw(batch, "PLAYER", leftX, topY + 126f);\n    font.draw(batch, "CHEST", leftX + slot + colGap, topY + 126f);\n\n    float mx = Gdx.input.getX();\n    float my = uiMouseYUp();\n    boolean click = Gdx.input.justTouched();\n\n    for (int i = 0; i < ids.length; i++) {\n      int itemId = ids[i];\n      float rowY = topY - i * (slot + pad);\n\n      // Player slot\n      float pxs = leftX;\n      float pys = rowY;\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(uiRegions.slot, pxs, pys, slot, slot);\n      TextureRegion icon = entityRegions.itemIcon(itemId);\n      if (icon != null) {\n        float iw = slot * 0.76f;\n        float ih = slot * 0.76f;\n        batch.draw(icon, pxs + (slot - iw) * 0.5f, pys + (slot - ih) * 0.5f, iw, ih);\n      }\n\n      // Chest slot\n      float cxs = leftX + slot + colGap;\n      float cys = rowY;\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(uiRegions.slot, cxs, cys, slot, slot);\n      if (icon != null) {\n        float iw = slot * 0.76f;\n        float ih = slot * 0.76f;\n        batch.draw(icon, cxs + (slot - iw) * 0.5f, cys + (slot - ih) * 0.5f, iw, ih);\n      }\n\n      // Counts + label\n      font.setColor(0f, 0f, 0f, 1f);\n      font.getData().setScale(1.0f * UI_FONT_SCALE);\n      font.draw(batch, labels[i], leftX + slot * 2f + colGap + 18f, rowY + 66f);\n      font.draw(batch, String.valueOf(inv.countsById[itemId]), pxs + 10f, pys + 22f);\n      font.draw(batch, String.valueOf(chest.countsById[itemId]), cxs + 10f, cys + 22f);\n\n      if (click) {\n        // Click player slot => move to chest\n        if (hitRect(mx, my, pxs, pys, slot, slot)) {\n          int have = inv.countsById[itemId];\n          int a = Math.min(amount, have);\n          if (a > 0 && inv.spend(itemId, a)) {\n            chest.add(itemId, a);\n            game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n          } else {\n            game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n        // Click chest slot => move to player\n        if (hitRect(mx, my, cxs, cys, slot, slot)) {\n          int have = chest.countsById[itemId];\n          int a = Math.min(amount, have);\n          if (a > 0 && chest.spend(itemId, a)) {\n            inv.add(itemId, a);\n            game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n          } else {\n            game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n          }\n        }\n      }\n    }\n\n    // POI Hidden Chest: if empty after transfers, remove permanently.\n    if (openChestE >= 0 && entities.alive[openChestE] && entities.type[openChestE] == EntityType.POI_CHEST_HIDDEN) {\n      boolean empty = true;\n      for (int id = 0; id < chest.countsById.length; id++) {\n        if (chest.countsById[id] > 0) { empty = false; break; }\n      }\n      if (empty) {\n        // Mark consumed and remove the entity so it never comes back.\n        String tid = (worldMap != null) ? worldMap.curTemplateId : "";\n        int tx = (int) Math.floor(entities.x[openChestE] / World.TILE_WORLD);\n        int ty = (int) Math.floor(entities.y[openChestE] / World.TILE_WORLD);\n        String key = tid + "|HIDDEN_CHEST|" + tx + "|" + ty;\n        if (worldMap != null) worldMap.consumedPois.add(key);\n        entities.kill(openChestE);\n        openChestE = -1;\n        toast = "Die Truhe verschwindet...";\n        toastT = 15f;\n      }\n    }\n  }\n\n  private void drawStatsPanel() {\n    // Compact stat panel (bottom-left). UI elements only.\n    float pad = 16f;\n    float x0 = 18f;\n    float y0 = 18f + 144f + 18f; // keep clear of hotbar\n\n    // Requested: 4x wider and half as high.\n    float barW = Math.min(1120f, Gdx.graphics.getWidth() - 40f);\n    float barH = 20f;\n    float gap = 8f;\n\n    float panelW = barW + pad * 2f;\n    float panelH = (barH * 5f) + (gap * 4f) + pad * 2f;\n\n    // Requested: background only 5% wider on both sides\n    float bgExtra = panelW * 0.05f;\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0 - bgExtra, y0, panelW + bgExtra * 2f, panelH);\n\n    float x = x0 + pad;\n    float y = y0 + panelH - pad - barH;\n\n    // Bars: solid color. Names BEFORE numbers.\n    // Colors: HP red, Ausdauer yellow, Mana blue, Hunger green, Müdigkeit dark blue.\n    drawStatBarSolid("HP", x, y, barW, barH, 0.90f, 0.20f, 0.20f, needs.hp, needs.hpMax);\n    y -= (barH + gap);\n    drawStatBarSolid("Ausdauer", x, y, barW, barH, 0.95f, 0.90f, 0.20f, needs.stamina, needs.staminaMax);\n    y -= (barH + gap);\n    drawStatBarSolid("Mana", x, y, barW, barH, 0.20f, 0.45f, 0.95f, needs.mana, needs.manaMax);\n    y -= (barH + gap);\n    drawStatBarSolid("Hunger", x, y, barW, barH, 0.20f, 0.80f, 0.25f, needs.hunger, needs.hungerMax);\n    y -= (barH + gap);\n    drawStatBarSolid("Muedigkeit", x, y, barW, barH, 0.12f, 0.20f, 0.55f, needs.sleep, needs.sleepMax);\n\n    batch.setColor(1f, 1f, 1f, 1f);\n  }\n\n  private void drawStatBarSolid(String name, float x, float y, float w, float h, float r, float g, float b, float v, float vmax) {\n    // Frame/background\n    batch.setColor(0f, 0f, 0f, 0.55f);\n    batch.draw(renderPipeline.white, x, y, w, h);\n\n    // Fill\n    float ratio = (vmax <= 1e-6f) ? 0f : MathUtils.clamp(v / vmax, 0f, 1f);\n    if (ratio > 0f) {\n      batch.setColor(r, g, b, 0.95f);\n      batch.draw(renderPipeline.white, x + 2f, y + 2f, Math.max(0f, (w - 4f) * ratio), Math.max(0f, h - 4f));\n    }\n\n    // Border\n    batch.setColor(0f, 0f, 0f, 0.85f);\n    batch.draw(renderPipeline.white, x, y, w, 2f);\n    batch.draw(renderPipeline.white, x, y + h - 2f, w, 2f);\n    batch.draw(renderPipeline.white, x, y, 2f, h);\n    batch.draw(renderPipeline.white, x + w - 2f, y, 2f, h);\n\n    // black text inside: NAME before numbers\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(0.95f * UI_FONT_SCALE);\n    String s = name + ": " + ((int) Math.ceil(v)) + "/" + ((int) Math.ceil(vmax));\n    font.draw(batch, s, x + 10f, y + h * 0.75f);\n    font.setColor(0f, 0f, 0f, 1f);\n\n    batch.setColor(1f, 1f, 1f, 1f);\n  }\n\n  private void drawHotbarHud() {\n    // Bottom anchored HUD: slot icons + selection frame.\n    float slot = 144f;\n    float pad = 18f;\n\n    float barW = hotbar.length * slot + (hotbar.length - 1) * pad;\n\n    float x0 = (Gdx.graphics.getWidth() - barW) * 0.5f;\n    float y0 = 0f; // bottom edge flush with screen bottom\n\n    // cache for hit-tests\n    hotbarX0 = x0;\n    hotbarY0 = y0;\n    hotbarSlotPx = slot;\n    hotbarPadPx = pad;\n// Nicht fertiges Feature:     hotbarLayoutValid = true;\n\n    for (int i=0;i<hotbar.length;i++) {\n      float sx = x0 + i * (slot + pad);\n      float sy = y0;\n\n      boolean selected = (i == hotbarSel);\n      batch.draw(selected ? uiRegions.slotPressed : uiRegions.slot, sx, sy, slot, slot);\n\n      int id = hotbar[i];\n      if (id >= 0) {\n        TextureRegion icon = entityRegions.itemIcon(id);\n        float iw = slot * 0.72f;\n        float ih = slot * 0.72f;\n        boolean owned = inv.hasItem(id);\n        if (!owned) batch.setColor(1f, 1f, 1f, 0.35f);\n        if (icon != null) batch.draw(icon, sx + (slot - iw) * 0.5f, sy + (slot - ih) * 0.5f, iw, ih);\n        if (!owned) batch.setColor(1f, 1f, 1f, 1f);\n      }\n    }\n  }\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private void drawInventoryLines(float x, float y) {\n    // legacy (unused) - kept for quick debug\n    int shown = 0;\n    for (int id=0; id<inv.countsById.length; id++) {\n      int c = inv.countsById[id];\n      if (c <= 0) continue;\n      String name = (data.items[id] != null) ? data.items[id].name : ("item_"+id);\n      font.draw(batch, name + ": " + c, x, y - shown*18);\n      shown++;\n      if (shown >= 12) break;\n    }\n    if (shown == 0) font.draw(batch, "(empty)", x, y);\n  }\n  */\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private void drawBuildLines(float x, float y) {\n    String[] names = {"Workbench","Bed","Campfire","Lamp"};\n    for (int i=0;i<names.length;i++) {\n      String sel = (i == buildSel) ? ">" : " ";\n      font.draw(batch, sel + (i+1) + ") " + names[i] + " rot=" + (int)buildRot, x, y - i*18);\n    }\n    font.draw(batch, "Placement rule: not on water.", x, y - names.length*18 - 10);\n  }\n  */\n\n  private void placeBuildAtMouse(float wx, float wy) {\n    if (!building.canPlace(world, wx, wy)) return;\n\n    EntityType t = switch (buildSel) {\n      case 0 -> EntityType.BUILD_WORKBENCH;\n      case 1 -> EntityType.BUILD_BED;\n      case 2 -> EntityType.BUILD_CAMPFIRE;\n      case 3 -> EntityType.BUILD_LAMP;\n      default -> EntityType.BUILD_WORKBENCH;\n    };\n\n    // Local build placement\n    building.place(entities, t, wx, wy, buildRot, -1);\n    game.audio.sfx("audio/sfx/place.wav", game.audio.sfxVolume(game.settings));\n  }\n\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private void drawChestLines(float x, float y) {\n    if (openChestE < 0) return;\n    int idx = entities.data0[openChestE];\n    Inventory chest = chestStore.get(idx);\n    if (chest == null) {\n      font.draw(batch, "(broken chest)", x, y);\n      return;\n    }\n\n    font.draw(batch, "Player wood=" + inv.countsById[0] + " stone=" + inv.countsById[1] + " coins=" + inv.countsById[31], x, y);\n    font.draw(batch, "Chest  wood=" + chest.countsById[0] + " stone=" + chest.countsById[1] + " coins=" + chest.countsById[31], x, y - 18);\n  }\n  */\n\n  private void chestStoreFromPlayer(int itemId, int amount) {\n    if (openChestE < 0) return;\n    int idx = entities.data0[openChestE];\n    Inventory chest = chestStore.get(idx);\n    if (chest == null) return;\n\n    int have = inv.hasItem(itemId) ? inv.countsById[itemId] : 0;\n    int a = Math.min(amount, have);\n    if (a <= 0) return;\n    if (!inv.spend(itemId, a)) return;\n    chest.add(itemId, a);\n  }\n\n  private void chestTakeAll() {\n    if (openChestE < 0) return;\n    int idx = entities.data0[openChestE];\n    Inventory chest = chestStore.get(idx);\n    if (chest == null) return;\n\n    for (int id=0; id<chest.countsById.length; id++) {\n      int c = chest.countsById[id];\n      if (c <= 0) continue;\n      inv.add(id, c);\n      chest.spend(id, c);\n    }\n  }\n\n  private int randRange(int a, int b) {
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 510-510

`java
   * IMPORTANT:\n   * - This is scaffolding. Final travel will be automatic at exits/edges or via WorldMap UI.\n   * - The loader currently used is {@code NoopAreaWorldLoader}, so this does NOT modify the current\n   *   procedural world. It only mutates {@link WorldMapState}.\n   *\n   * Why keep it in GameScreen?\n   * - We already have an input loop here and a toast system.\n   * - This lets us quickly verify: save->load preserves knownAreas/frontier/edges.\n   */\n  private void debugWorldMapTravel(Dir4 dir) {\n    try {\n      // Seed choice:\n      // - We use worldSeed because it's already per-save and stored in saves.\n      // - Later we may introduce a dedicated mapSeed, but worldSeed is fine for alpha.\n      long seed = worldSeed;\n\n      // Store "war stand" for the area we are leaving.\n      areaOnLeaveCurrent();\n\n      AreaTravelResult r = worldMapTravel.travel(this, worldMap, dir, seed);\n      if (r == null) {\n        toast = "WorldMap travel: ???";\n        toastT = 2.0f;\n        return;\n      }\n\n      // Human-readable debug feedback.\n      // We keep it short because it may be used frequently.\n      switch (r.code) {\n        case OK -> {\n          // Alpha exits: all sides are exits for now.\n          AreaCoord c1 = new AreaCoord(worldMap.curAx, worldMap.curAy);\n          if (!worldMap.exitsByArea.containsKey(c1)) {\n            worldMap.exitsByArea.put(c1, new AreaExits(true, true, true, true));\n          }\n// Nicht fertiges Feature:           worldMapDirty = true;\n          toast = "WorldMap: entered " + r.templateId + " @(" + worldMap.curAx + "," + worldMap.curAy + ")";\n          toastT = 2.2f;\n        }\n        case NO_EXIT -> {\n          toast = "WorldMap: no exit";\n          toastT = 2.0f;\n        }\n        case INVALID_PLACEMENT -> {\n          toast = "WorldMap: invalid placement";\n          toastT = 2.2f;\n        }\n        case LOAD_FAILED -> {\n          toast = "WorldMap: load failed: " + r.templateId;\n          toastT = 2.6f;\n        }\n      }\n    } catch (Throwable t) {\n      toast = "WorldMap travel error";\n      toastT = 2.6f;\n    }\n  }\n\n  /**\n   * Save the "war stand" snapshot for the current area.\n   *\n   * Requirement:\n   * - When zooming into a NOT-current area, the UI must show the last-known state when we left.\n   * - The current area is shown as live.\n   */\n  private void areaOnLeaveCurrent() {\n    try {\n      AreaCoord c = new AreaCoord(worldMap.curAx, worldMap.curAy);\n\n      // Ensure exits exist (alpha: all sides).\n      if (!worldMap.exitsByArea.containsKey(c)) {\n        worldMap.exitsByArea.put(c, new AreaExits(true, true, true, true));\n      }\n\n      // Preserve existing per-area state (fog) and only update war-stand snapshot fields.\n      AreaState st = worldMap.areaStates.get(c);\n      if (st == null) st = new AreaState();\n\n      st.ax = c.ax;\n      st.ay = c.ay;\n      st.templateId = worldMap.curTemplateId;\n\n      // Mini-map snapshot: downsample area tiles to a compact grid.\n      st.miniScale = MAP_MINI_SCALE;\n      st.miniW = AREA_W_TILES / st.miniScale;\n      st.miniH = AREA_H_TILES / st.miniScale;\n      st.miniMapB64 = captureMiniMapB64(st.miniScale, st.miniW, st.miniH);\n\n      st.leftDayIndex = dayIndex;\n      st.leftDayT = dayNight.t;\n\n      // Persist tile-tree cuts (so harvested trees do not respawn).\n      try {\n        if (TILE_TREES_FELL_ON_HARVEST && AREA_MODE && areaTreeCutBits != null && areaTreeW > 0 && areaTreeH > 0) {\n          st.treeCutW = areaTreeW;\n          st.treeCutH = areaTreeH;\n          st.treeCutB64 = java.util.Base64.getEncoder().encodeToString(areaTreeCutBits);\n        }\n      } catch (Throwable ignored2) {}\n\n      worldMap.areaStates.put(c, st);\n    } catch (Throwable ignored) {}\n  }\n\n  /** Capture a downsampled groundId map and encode it as base64. */\n  private String captureMiniMapB64(int scale, int miniW, int miniH) {\n    try {\n      if (world == null) return "";\n      if (scale <= 0 || miniW <= 0 || miniH <= 0) return "";\n\n      byte[] out = new byte[miniW * miniH];\n      int idx = 0;\n      for (int my = 0; my < miniH; my++) {\n        int ty = my * scale;\n        for (int mx = 0; mx < miniW; mx++) {\n          int tx = mx * scale;\n\n          int cx = tx / World.CHUNK_SIZE;\n          int cy = ty / World.CHUNK_SIZE;\n          int lx = tx - cx * World.CHUNK_SIZE;\n          int ly = ty - cy * World.CHUNK_SIZE;\n\n          short gid = 0;\n          Chunk c = world.peekChunk(cx, cy);\n          if (c != null && c.layers != null) {\n            int li = lx + ly * World.CHUNK_SIZE;\n            if (li >= 0 && li < c.layers.groundId.length) {\n              gid = c.layers.groundId[li];\n            }\n          }\n          out[idx++] = (byte) (gid & 0xFF);\n        }\n      }\n\n      return java.util.Base64.getEncoder().encodeToString(out);\n    } catch (Throwable t) {\n      return "";\n    }\n  }\n\n  private static float smooth01(float x) {\n    if (x <= 0f) return 0f;\n    if (x >= 1f) return 1f;\n    return x * x * (3f - 2f * x);\n  }\n\n  private void drawWorldMapOverview(float alpha, float scale) {\n    try {\n      if (shape == null || font == null) return;\n\n      // Background dim\n      shape.setProjectionMatrix(uiCam.combined);\n      shape.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);\n      shape.setColor(0f, 0f, 0f, 0.85f * alpha);\n      shape.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n      shape.end();\n\n      float cx0 = (Gdx.graphics.getWidth() * 0.5f) + mapPanX;\n      float cy0 = (Gdx.graphics.getHeight() * 0.5f) + mapPanY;\n      float cell = 40f * scale;\n\n      // Tiles\n      shape.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);\n      for (java.util.Map.Entry<AreaCoord, String> e : worldMap.knownAreas.entrySet()) {\n        AreaCoord c = e.getKey();\n        String tid = e.getValue();\n\n        float bx = cx0 + c.ax * cell;\n        float by = cy0 + c.ay * cell;\n\n        boolean selected = (c.ax == mapSelectedAx && c.ay == mapSelectedAy);\n        boolean current = (c.ax == worldMap.curAx && c.ay == worldMap.curAy);\n\n        // Base color by template\n        float r = 0.25f, g = 0.25f, b = 0.25f;\n        switch (tid) {\n          case T_HOME -> { r = 0.30f; g = 0.35f; b = 0.45f; }
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 510-510

`java
          case "GEN_GRASSLAND" -> { r = 0.20f; g = 0.55f; b = 0.20f; }\n          case "GEN_ROCKY_FIELDS" -> { r = 0.45f; g = 0.45f; b = 0.45f; }\n          case "GEN_LIGHT_FOREST" -> { r = 0.18f; g = 0.35f; b = 0.18f; }\n          default -> { /* keep base */ }\n        }\n\n        float a = 0.85f * alpha;\n        shape.setColor(r, g, b, a);\n        shape.rect(bx - cell * 0.45f, by - cell * 0.45f, cell * 0.90f, cell * 0.90f);\n\n        // Selected/current highlight border\n        if (selected || current) {\n          shape.setColor(current ? 1f : 1f, current ? 1f : 1f, current ? 1f : 0.2f, 1f * alpha);\n          float bw = cell * 0.92f;\n          float bh = cell * 0.92f;\n          float x = bx - bw * 0.5f;\n          float y = by - bh * 0.5f;\n          // simple thick border\n          float t = 2f;\n          shape.rect(x, y, bw, t);\n          shape.rect(x, y + bh - t, bw, t);\n          shape.rect(x, y, t, bh);\n          shape.rect(x + bw - t, y, t, bh);\n        }\n      }\n      shape.end();\n\n      // Text\n      batch.setColor(1f, 1f, 1f, alpha);\n      font.getData().setScale(1.0f * UI_FONT_SCALE);\n      font.draw(batch, "MAP (Shift+M)", 20, Gdx.graphics.getHeight() - 160);\n      font.draw(batch, "Known areas: " + worldMap.knownAreas.size(), 20, Gdx.graphics.getHeight() - 190);\n      font.draw(batch, "Click area to view map", 20, Gdx.graphics.getHeight() - 220);\n      batch.setColor(1f, 1f, 1f, 1f);\n    } catch (Throwable ignored) {\n      try { shape.end(); } catch (Throwable ignored2) {}\n    }\n  }\n\n  private void drawSelectedAreaMap(float alpha, float scale) {\n    try {\n      if (shape == null || font == null) return;\n\n      // Background dim\n      shape.setProjectionMatrix(uiCam.combined);\n      shape.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);\n      shape.setColor(0f, 0f, 0f, 0.92f * alpha);\n      shape.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());\n      shape.end();\n\n      boolean isCurrent = (mapSelectedAx == worldMap.curAx && mapSelectedAy == worldMap.curAy);\n\n      // Map rect\n      float maxSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.78f * scale;\n      float x0 = (Gdx.graphics.getWidth() - maxSize) * 0.5f;\n      float y0 = (Gdx.graphics.getHeight() - maxSize) * 0.5f;\n\n      int miniScale = 4;\n      int miniW = AREA_W_TILES / miniScale;\n      int miniH = AREA_H_TILES / miniScale;\n\n      byte[] mapBytes = null;\n\n      if (isCurrent) {\n        mapBytes = captureMiniMapBytesLive(miniScale, miniW, miniH);\n      } else {\n        AreaCoord c = new AreaCoord(mapSelectedAx, mapSelectedAy);\n        AreaState st = worldMap.areaStates.get(c);\n        if (st != null && st.miniMapB64 != null && !st.miniMapB64.isEmpty()) {\n          try { mapBytes = java.util.Base64.getDecoder().decode(st.miniMapB64); } catch (Throwable ignored) { mapBytes = null; }\n        }\n      }\n\n      // Decode persistent fog alpha (0..255)\n      byte[] fogABytes = null;\n      {\n        AreaCoord c = new AreaCoord(mapSelectedAx, mapSelectedAy);\n        AreaState st = worldMap.areaStates.get(c);\n        if (st != null) {\n          // Determine expected alpha-map size for this stored area.\n          int sc = (st.fogScale > 0) ? st.fogScale : FOW_ALPHA_SCALE_TILES;\n          int fw = AREA_W_TILES / sc;\n          int fh = AREA_H_TILES / sc;\n          if (st.fogBitsB64 != null && !st.fogBitsB64.isEmpty()) {\n            try {\n              byte[] raw = java.util.Base64.getDecoder().decode(st.fogBitsB64);\n              if (raw != null) {\n                if (raw.length == fw * fh) {\n                  fogABytes = raw;\n                } else {\n                  // old bitset -> alpha conversion\n                  int bits = fw * fh;\n                  int bytesOld = (bits + 7) >> 3;\n                  if (raw.length == bytesOld) {\n                    int exploredA = (int) (FOW_EXPLORED_ALPHA * 255f);\n                    if (exploredA < 0) exploredA = 0;\n                    if (exploredA > 255) exploredA = 255;\n                    fogABytes = new byte[fw * fh];\n                    for (int i = 0; i < fogABytes.length; i++) {\n                      int bi = i >> 3;\n                      int bit = i & 7;\n                      int mask = 1 << bit;\n                      boolean explored = (raw[bi] & mask) != 0;\n                      fogABytes[i] = (byte) (explored ? exploredA : 255);\n                    }\n                    // upgrade stored payload\n                    st.fogScale = sc;\n                    st.fogW = fw;\n                    st.fogH = fh;\n                    st.fogBitsB64 = java.util.Base64.getEncoder().encodeToString(fogABytes);\n// Nicht fertiges Feature:                     worldMapDirty = true;\n                  }\n                }\n              }\n            } catch (Throwable ignored) { fogABytes = null; }\n          }\n        }\n      }\n\n      // Visible radius (current area only): action ring + margin\n      int pMx = -9999, pMy = -9999;\n      float rVisMini = 0f;\n      float fadeMini = 0f;\n      if (isCurrent) {\n        int ptx = (int) Math.floor(px / World.TILE_WORLD);\n        int pty = (int) Math.floor(py / World.TILE_WORLD);\n        pMx = ptx / miniScale;\n        pMy = pty / miniScale;\n\n        float actionR = actionReach(equippedFromHotbar());\n        float marginWu = FOW_VISIBLE_MARGIN_TILES * World.TILE_WORLD;\n        float rVisTiles = (actionR + marginWu) / World.TILE_WORLD;\n        rVisMini = rVisTiles / (float) miniScale;\n\n        fadeMini = Math.max(1f, (float) FOW_EDGE_FADE_TILES / (float) miniScale);\n      }\n\n      // Draw minimap tiles + fog overlay\n      shape.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);\n      float cw = maxSize / (float) miniW;\n      float ch = maxSize / (float) miniH;\n\n      for (int my = 0; my < miniH; my++) {\n        for (int mx = 0; mx < miniW; mx++) {\n          int idx = mx + my * miniW;\n          int gid = (mapBytes != null && idx >= 0 && idx < mapBytes.length) ? (mapBytes[idx] & 0xFF) : 0;\n\n          // groundId colors (0..5 from tileset.json)\n          float r = 0.15f, g = 0.15f, b = 0.15f;\n          switch (gid) {\n            case 0 -> { r = 0.15f; g = 0.55f; b = 0.20f; } // GRASS\n            case 1 -> { r = 0.45f; g = 0.30f; b = 0.15f; } // DIRT\n            case 2 -> { r = 0.70f; g = 0.65f; b = 0.25f; } // SAND\n            case 3 -> { r = 0.45f; g = 0.45f; b = 0.45f; } // ROCK\n            case 4 -> { r = 0.85f; g = 0.90f; b = 0.95f; } // SNOW\n            case 5 -> { r = 0.85f; g = 0.15f; b = 0.10f; } // LAVA\n          }\n\n          float rx = x0 + mx * cw;\n          float ry = y0 + my * ch;\n\n          // base tile\n          shape.setColor(r, g, b, 1f * alpha);\n          shape.rect(rx, ry, cw + 0.5f, ch + 0.5f);\n\n          // fog overlay alpha (from persistent alpha-map)\n          float fogA = 1.0f;\n          if (fogABytes != null) {\n            // Map minimap cell -> fog alpha cell (different resolution)\n            int sc = FOW_ALPHA_SCALE_TILES;\n            int fw = AREA_W_TILES / sc;\n            int fh = AREA_H_TILES / sc;\n\n            // Convert minimap cell coords back to tile coords, then to fog cell.\n            int tx = mx * miniScale;\n            int ty = my * miniScale;\n            int fx = MathUtils.clamp(tx / sc, 0, fw - 1);\n            int fy = MathUtils.clamp(ty / sc, 0, fh - 1);\n            int fi = fx + fy * fw;\n            int aByte = fogABytes[fi] & 0xFF;\n            fogA = aByte / 255f;\n          }\n\n          // Live visibility (current area only): clear circle with soft edge over explored\n          if (isCurrent) {\n            float dx = (mx + 0.5f) - (pMx + 0.5f);\n            float dy = (my + 0.5f) - (pMy + 0.5f);\n            float d = (float) Math.sqrt(dx * dx + dy * dy);\n            if (d <= rVisMini) {\n              fogA = 0f;\n            } else if (d <= rVisMini + fadeMini) {\n              float t = (d - rVisMini) / fadeMini;\n              t = t * t * (3f - 2f * t);\n              fogA = fogA * t;\n            }\n          }\n\n          if (fogA > 1e-3f) {\n            shape.setColor(0f, 0f, 0f, fogA * alpha);\n            shape.rect(rx, ry, cw + 0.5f, ch + 0.5f);\n          }\n        }\n      }\n      shape.end();\n\n      // Text\n      batch.setColor(1f, 1f, 1f, alpha);\n      font.getData().setScale(1.0f * UI_FONT_SCALE);\n      font.draw(batch, "AREA (" + mapSelectedAx + "," + mapSelectedAy + ")" + (isCurrent ? " [LIVE]" : " [WAR]"), 20, Gdx.graphics.getHeight() - 160);\n      font.draw(batch, "ESC = Back", 20, Gdx.graphics.getHeight() - 190);\n      if (!isCurrent && mapBytes == null) {\n        font.draw(batch, "No saved snapshot yet", 20, Gdx.graphics.getHeight() - 220);\n      }\n\n      // Fog of War info (map-only)\n      font.draw(batch, "FoW: unknown=black, explored=fog, visible=clear", 20, Gdx.graphics.getHeight() - 220);\n      batch.setColor(1f, 1f, 1f, 1f);\n    } catch (Throwable ignored) {\n      try { shape.end(); } catch (Throwable ignored2) {}\n    }\n  }\n\n  private boolean areaTryTravelAtEdge(Dir4 dir) {\n    try {\n      // Save war-stand for current area before leaving.\n      areaOnLeaveCurrent();\n\n      long seed = worldSeed;\n      AreaTravelResult r = worldMapTravel.travel(this, worldMap, dir, seed);\n      if (r == null || r.code != OK) {\n        toast = "WorldMap: travel blocked";\n        toastT = 1.4f;\n        return false;\n      }\n\n      // Alpha exits: ensure exits exist.\n      AreaCoord c1 = new AreaCoord(worldMap.curAx, worldMap.curAy);\n      if (!worldMap.exitsByArea.containsKey(c1)) {\n        worldMap.exitsByArea.put(c1, new AreaExits(true, true, true, true));\n      }\n\n      // Ensure fog alpha map storage exists immediately for the new area.\n      {\n        AreaState st = worldMap.areaStates.get(c1);\n        if (st == null) st = new AreaState();\n        st.ax = c1.ax;\n        st.ay = c1.ay;\n        st.templateId = worldMap.curTemplateId;\n\n        int sc = FOW_ALPHA_SCALE_TILES;\n        int fw = AREA_W_TILES / sc;\n        int fh = AREA_H_TILES / sc;\n        st.fogScale = sc;\n        st.fogW = fw;\n        st.fogH = fh;\n        if (st.fogBitsB64 == null || st.fogBitsB64.isEmpty()) {\n          byte[] a = new byte[fw * fh];\n          java.util.Arrays.fill(a, (byte) 255);\n          st.fogBitsB64 = java.util.Base64.getEncoder().encodeToString(a);\n        }\n        worldMap.areaStates.put(c1, st);\n      }\n\n      // Force next reveal tick to run immediately after travel.\n      fowAccT = FOW_REVEAL_STEP_SEC;\n\n// Nicht fertiges Feature:       worldMapDirty = true;\n\n      // Place player just inside the opposite edge.\n      float tw = World.TILE_WORLD;\n      float w = AREA_W_TILES * tw;\n      float h = AREA_H_TILES * tw;\n      float pad = 2f * tw;\n      switch (dir) {\n        case W -> px = w - pad;\n        case E -> px = pad;\n        case S -> py = h - pad;\n        case N -> py = pad;\n      }\n      // Keep other axis (roughly) stable.\n      px = MathUtils.clamp(px, pad, w - pad);\n      py = MathUtils.clamp(py, pad, h - pad);\n      if (playerE >= 0) { entities.x[playerE] = px; entities.y[playerE] = py; }\n\n      toast = "Gebiet: " + worldMap.curTemplateId + " (" + worldMap.curAx + "," + worldMap.curAy + ")";\n      toastT = 1.8f;\n      return true;\n    } catch (Throwable t) {\n      toast = "Travel error";\n      toastT = 1.8f;\n      return false;\n    }\n  }\n\n  // Nicht fertiges Feature: commented out unused block\n  /*\n  private static boolean fogBitGet(byte[] fog, int idx) {\n    if (fog == null) return false;\n    int bi = idx >> 3;\n    int bit = idx & 7;\n    if (bi < 0 || bi >= fog.length) return false;\n    int mask = 1 << bit;\n    return (fog[bi] & mask) != 0;\n  }\n  */\n\n  // Nicht fertiges Feature: commented out unused block\n  /*\n  private static boolean fogBitSet(byte[] fog, int idx) {\n    if (fog == null) return false;\n    int bi = idx >> 3;\n    int bit = idx & 7;\n    if (bi < 0 || bi >= fog.length) return false;\n    byte mask = (byte) (1 << bit);\n    if ((fog[bi] & mask) != 0) return false;\n    fog[bi] |= mask;\n    return true;\n  }\n  */\n\n  private byte[] getCurrentAreaFogAlphaBytesOrNull(int fogW, int fogH) {\n    try {\n      if (worldMap == null) return null;\n      AreaCoord c = new AreaCoord(worldMap.curAx, worldMap.curAy);\n      AreaState st = worldMap.areaStates.get(c);\n      if (st == null || st.fogBitsB64 == null || st.fogBitsB64.isEmpty()) return null;\n\n      byte[] raw = java.util.Base64.getDecoder().decode(st.fogBitsB64);\n      if (raw == null) return null;\n\n      // New format: fogW*fogH bytes (0..255 alpha)\n      if (raw.length == fogW * fogH) return raw;\n\n      // Backward compat: old bitset ((w*h+7)/8). Convert to alpha bytes.\n      int bits = fogW * fogH;\n      int bytesOld = (bits + 7) >> 3;\n      if (raw.length == bytesOld) {\n        int exploredA = (int) (FOW_EXPLORED_ALPHA * 255f);\n        if (exploredA < 0) exploredA = 0;\n        if (exploredA > 255) exploredA = 255;\n        byte[] a = new byte[fogW * fogH];\n        for (int i = 0; i < fogW * fogH; i++) {\n          int bi = i >> 3;\n          int bit = i & 7;\n          int mask = 1 << bit;\n          boolean explored = bi >= 0 && bi < raw.length && ((raw[bi] & mask) != 0);\n          a[i] = (byte) (explored ? exploredA : 255);\n        }\n        // upgrade in-memory save payload\n        st.fogBitsB64 = java.util.Base64.getEncoder().encodeToString(a);\n        st.fogW = fogW;\n        st.fogH = fogH;\n        st.fogScale = FOW_ALPHA_SCALE_TILES;\n// Nicht fertiges Feature:         worldMapDirty = true;\n        return a;\n      }\n\n      return null;\n    } catch (Throwable ignored) {\n      return null;\n    }\n  }\n\n  private void renderFogOfWarOverlayWorld() {\n    try {\n      // StarCraft-like FoW:\n      // - unknown = black (alpha=1)\n      // - explored = light translucent fog (alpha=FOW_EXPLORED_ALPHA)\n      // - visible now = clear (alpha=0), soft edge\n      if (batch == null) return;\n\n      // Use higher-res alpha map so clearing is not raster-y.\n      int scale = FOW_ALPHA_SCALE_TILES;\n      int wTiles = AREA_W_TILES;\n      int hTiles = AREA_H_TILES;\n      int fogW = wTiles / scale;\n      int fogH = hTiles / scale;\n\n      byte[] fogABytes = getCurrentAreaFogAlphaBytesOrNull(fogW, fogH);\n      if (fogABytes == null || fogABytes.length != fogW * fogH) {\n        fogABytes = new byte[fogW * fogH];\n        java.util.Arrays.fill(fogABytes, (byte) 255);\n      }\n\n      // Ensure backing pixmap/texture\n      if (fowPm == null || fowPm.getWidth() != fogW || fowPm.getHeight() != fogH) {\n        try { if (fowPm != null) fowPm.dispose(); } catch (Throwable ignored) {}\n        try { if (fowTex != null) fowTex.dispose(); } catch (Throwable ignored) {}\n        fowPm = new Pixmap(fogW, fogH, Pixmap.Format.RGBA8888);\n        fowPm.setBlending(Pixmap.Blending.None);\n        fowTex = new Texture(fowPm);\n        fowTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);\n      }\n\n      // Player pos in tiles -> fog cells\n      int ptx = (int) Math.floor(px / World.TILE_WORLD);\n      int pty = (int) Math.floor(py / World.TILE_WORLD);\n      float pFx = (ptx + 0.5f) / (float) scale;\n      float pFy = (pty + 0.5f) / (float) scale;\n\n      // Clear radius (circle): action ring + margin, cut off slightly (requested earlier)\n      float actionR = actionReach(equippedFromHotbar());\n      float marginWu = FOW_VISIBLE_MARGIN_TILES * World.TILE_WORLD;\n      float rClearTilesRaw = (actionR + marginWu) / World.TILE_WORLD;\n      float rClearTiles = Math.max(2f, rClearTilesRaw - FOW_CLEAR_CUTOFF_FORWARD_TILES);\n      float rClear = rClearTiles / (float) scale;\n\n      float fadeTiles = FOW_EDGE_FADE_TILES;\n      float fade = Math.max(1f / (float) scale, fadeTiles / (float) scale);\n\n      for (int y = 0; y < fogH; y++) {\n        for (int x = 0; x < fogW; x++) {\n          int idx = x + y * fogW;\n          float a0 = (fogABytes[idx] & 0xFF) / 255f;\n\n          // Apply "visible now" clear circle with soft edge.\n          float dx = (x + 0.5f) - pFx;\n          float dy = (y + 0.5f) - pFy;\n          float d = (float) Math.sqrt(dx * dx + dy * dy);\n\n          float a = a0;\n          if (d <= rClear) {\n            a = 0f;\n          } else if (d <= rClear + fade) {\n            float t = (d - rClear) / Math.max(1e-3f, fade);\n            t = t * t * (3f - 2f * t);\n            a = a0 * t;\n          }\n\n          // Write pixel (flip Y)\n          int pyPm = (fogH - 1) - y;\n          fowPm.setColor(0f, 0f, 0f, MathUtils.clamp(a, 0f, 1f));\n          fowPm.drawPixel(x, pyPm);\n        }\n      }\n\n      fowTex.draw(fowPm, 0, 0);\n\n      // Draw scaled over the area in world coordinates.\n      float cellWu = World.TILE_WORLD * scale;\n      float wWu = fogW * cellWu;\n      float hWu = fogH * cellWu;\n\n      batch.setProjectionMatrix(cam.combined);\n      batch.begin();\n      batch.enableBlending();\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(fowTex, 0f, 0f, wWu, hWu);\n      batch.end();\n    } catch (Throwable ignored) {\n      try { batch.end(); } catch (Throwable ignored2) {}\n    }\n  }\n\n  private void areaFogRevealTick(float dt) {\n    try {\n      if (!AREA_MODE) return;\n      if (worldMap == null) return;\n\n      fowAccT += dt;\n      if (fowAccT < FOW_REVEAL_STEP_SEC) return;\n      fowAccT = 0f;\n\n      // Persistent explored fog alpha map (higher-res than minimap)\n      int scale = FOW_ALPHA_SCALE_TILES;\n      int fogW = AREA_W_TILES / scale;\n      int fogH = AREA_H_TILES / scale;\n\n      AreaCoord c = new AreaCoord(worldMap.curAx, worldMap.curAy);\n      AreaState st = worldMap.areaStates.get(c);\n      if (st == null) {\n        st = new AreaState();\n        st.ax = c.ax;\n        st.ay = c.ay;\n        st.templateId = worldMap.curTemplateId;\n        worldMap.areaStates.put(c, st);\n      }\n\n      // Ensure fog storage metadata\n      st.fogScale = scale;\n      st.fogW = fogW;\n      st.fogH = fogH;\n\n      // Load or initialize alpha bytes\n      byte[] a = getCurrentAreaFogAlphaBytesOrNull(fogW, fogH);\n      if (a == null || a.length != fogW * fogH) {\n        a = new byte[fogW * fogH];\n        java.util.Arrays.fill(a, (byte) 255);\n      }\n\n      int exploredA = (int) (FOW_EXPLORED_ALPHA * 255f);\n      if (exploredA < 0) exploredA = 0;\n      if (exploredA > 255) exploredA = 255;\n\n      // Brush: circular erase with falloff (in tiles)\n      float rOuterTiles = FOW_EXPLORE_RADIUS_TILES;\n      float falloffTiles = FOW_EXPLORE_FALLOFF_TILES;\n      float rInnerTiles = Math.max(0f, rOuterTiles - falloffTiles);\n\n      // Center in fog-cell space\n      int ptx = (int) Math.floor(px / World.TILE_WORLD);\n      int pty = (int) Math.floor(py / World.TILE_WORLD);\n      float pFx = (ptx + 0.5f) / (float) scale;\n      float pFy = (pty + 0.5f) / (float) scale;\n\n      float rOuter = rOuterTiles / (float) scale;\n      int rBox = (int) Math.ceil(rOuter) + 2;\n\n      boolean changed = false;\n      for (int dy = -rBox; dy <= rBox; dy++) {\n        int y = (int) Math.floor(pFy) + dy;\n        if (y < 0 || y >= fogH) continue;\n        for (int dx = -rBox; dx <= rBox; dx++) {\n          int x = (int) Math.floor(pFx) + dx;\n          if (x < 0 || x >= fogW) continue;\n\n          float cx = (x + 0.5f) - pFx;\n          float cy = (y + 0.5f) - pFy;\n          float dCells = (float) Math.sqrt(cx * cx + cy * cy);\n          float dTiles = dCells * scale;\n          if (dTiles > rOuterTiles) continue;\n\n          float t;\n          if (dTiles <= rInnerTiles || falloffTiles <= 1e-3f) {\n            t = 0f;\n          } else {\n            t = (dTiles - rInnerTiles) / falloffTiles;\n            if (t < 0f) t = 0f;\n            if (t > 1f) t = 1f;\n            // smoothstep\n            t = t * t * (3f - 2f * t);\n          }\n\n          int targetA = (int) (exploredA + (255 - exploredA) * t);\n          int idx = x + y * fogW;\n          int cur = a[idx] & 0xFF;\n          if (targetA < cur) {\n            a[idx] = (byte) targetA;\n            changed = true;\n          }\n        }\n      }\n\n      if (changed) {\n        st.fogBitsB64 = java.util.Base64.getEncoder().encodeToString(a);\n// Nicht fertiges Feature:         worldMapDirty = true;\n      }\n    } catch (Throwable ignored) {}\n  }\n\n  private byte[] captureMiniMapBytesLive(int scale, int miniW, int miniH) {\n    try {\n      if (world == null) return null;\n      byte[] out = new byte[miniW * miniH];\n      int idx = 0;\n      for (int my = 0; my < miniH; my++) {\n        int ty = my * scale;\n        for (int mx = 0; mx < miniW; mx++) {\n          int tx = mx * scale;\n          int cx = tx / World.CHUNK_SIZE;\n          int cy = ty / World.CHUNK_SIZE;\n          int lx = tx - cx * World.CHUNK_SIZE;\n          int ly = ty - cy * World.CHUNK_SIZE;\n          short gid = 0;\n          Chunk c = world.peekChunk(cx, cy);\n          if (c != null && c.layers != null) {\n            int li = lx + ly * World.CHUNK_SIZE;\n            if (li >= 0 && li < c.layers.groundId.length) gid = c.layers.groundId[li];\n          }\n          out[idx++] = (byte) (gid & 0xFF);\n        }\n      }\n      return out;\n    } catch (Throwable t) {\n      return null;\n    }\n  }\n\n  private static byte dirFromVel(float vx, float vy, byte last) {\n    float ax = Math.abs(vx);\n    float ay = Math.abs(vy);\n    if (ax < 1e-3f && ay < 1e-3f) return last;\n    if (ax > ay) return (byte) ((vx >= 0f) ? 1 : 3); // E/W\n    return (byte) ((vy >= 0f) ? 0 : 2); // N/S\n  }\n\n  @Deprecated\n  // Nicht fertiges Feature: commented out unused method\n  /*\n  private void ensureEncounters() {\n    // Replaced by EncounterSpawner (Block 12). Intentionally left as no-op.\n  }\n  */\n\n  // =====================================================================\n  // Area/WorldMap loading helpers (alpha scaffolding)\n  // =====================================================================\n  //\n  // These methods form the controlled "seam" where the story-first Area system\n  // can swap the world content without exposing internal fields directly.\n  //\n  // Design decision:\n  // - The Area loader operates *through* GameScreen.\n  // - Reason: GameScreen owns multiple coupled systems (World, node spawner,\n  //   simContext, player position, etc.). A narrow API reduces accidental bugs.\n\n  /** @return the base world seed stored in saves (stable per save slot). */\n  public long areaGetWorldSeed() {\n    return worldSeed;\n  }\n\n  /** @return same as {@link #areaGetWorldSeed()}, but avoids any deprecated symbol in callers. */\n  public long areaWorldSeed() {\n    return worldSeed;\n  }\n\n  /** @return current world instance (after any area load/reset). */\n  public World areaWorld() {\n    return world;\n  }\n\n  /** @return global entity container (used by loader for spawning nodes/animals). */\n  public Entities areaEntities() {\n    return entities;\n  }\n\n  /** @return persistent WorldMapState (per-save) used for one-time POIs and authored node removals. */\n  public WorldMapState areaWorldMapState() {\n    return worldMap;\n  }\n\n  /** @return chest store used by both build-chests and POI chests. */\n  public ChestStore areaChestStore() {\n    return chestStore;\n  }\n\n  // ============================================================\n  // Authored enemy zones (FUSA Story)\n  // ============================================================\n\n  private static final class EnemyZone {\n    int cxTile;\n    int cyTile;\n    int rTiles;\n    int targetMin;\n    int targetMax;\n    int respawnDaysMin;\n    int respawnDaysMax;\n\n    int targetCount;\n    int nextRespawnDayIndex;\n\n    long seed;\n  }\n\n  private final com.badlogic.gdx.utils.Array<EnemyZone> enemyZones = new com.badlogic.gdx.utils.Array<>(8);\n\n  // ============================================================\n  // Authored tile-tree field (FUSA Story)\n  // ============================================================\n\n  // Presence mask: 1 bit per tile (384*384 bits => 18432 bytes). Generated deterministically on area load.\n  private byte[] areaTreePresentBits = null;\n  // Cut mask: 1 bit per tile. Persisted per-area state (so trees don't come back after save/load).\n  private byte[] areaTreeCutBits = null;\n  private int areaTreeW = 0;\n  private int areaTreeH = 0;\n  private boolean areaTreeDebugToastOnce = false;\n  private boolean areaTreeInitToastOnce = false;\n\n  public void areaSetTreePresentBits(int wTiles, int hTiles, byte[] bits) {\n    this.areaTreeW = Math.max(0, wTiles);\n    this.areaTreeH = Math.max(0, hTiles);\n    this.areaTreePresentBits = bits;\n    this.areaTreeDebugToastOnce = false;\n  }\n\n  public void areaSetTreeCutBits(int wTiles, int hTiles, byte[] bits) {\n    this.areaTreeW = Math.max(0, wTiles);\n    this.areaTreeH = Math.max(0, hTiles);\n    this.areaTreeCutBits = bits;\n  }\n\n  // One-shot debug toast helper for diagnosing area load/spawn issues.\n  public void areaDebugToastOnce(String msg) {\n    if (msg == null || msg.isEmpty()) return;\n    if (areaTreeInitToastOnce) return;\n    toast = msg;\n    toastT = 6.0f;\n    areaTreeInitToastOnce = true;\n  }\n\n  private static int bitIndex(int tx, int ty, int wTiles) {\n    return tx + ty * wTiles;\n  }\n\n  private static boolean bitGet(byte[] bits, int bit) {\n    if (bits == null || bit < 0) return false;\n    int i = bit >>> 3;\n    if (i < 0 || i >= bits.length) return false;\n    int m = 1 << (bit & 7);\n    return (bits[i] & m) != 0;\n  }\n\n  private static void bitSet(byte[] bits, int bit, boolean on) {\n    if (bits == null || bit < 0) return;\n    int i = bit >>> 3;\n    if (i < 0 || i >= bits.length) return;\n    int m = 1 << (bit & 7);\n    if (on) bits[i] = (byte) (bits[i] | m);\n    else bits[i] = (byte) (bits[i] & ~m);\n  }\n\n  public void areaClearEnemyZones() {\n    enemyZones.clear();\n  }\n\n  public void areaAddEnemyZone(int cxTile, int cyTile, int rTiles, int min, int max, int respawnDaysMin, int respawnDaysMax) {\n    EnemyZone z = new EnemyZone();\n    z.cxTile = cxTile;\n    z.cyTile = cyTile;\n    z.rTiles = Math.max(1, rTiles);\n    z.targetMin = Math.max(0, min);\n    z.targetMax = Math.max(z.targetMin, max);\n    z.respawnDaysMin = Math.max(1, respawnDaysMin);\n    z.respawnDaysMax = Math.max(z.respawnDaysMin, respawnDaysMax);\n\n    // Deterministic per-zone seed\n    long s = worldSeed;\n    s ^= (long) cxTile * 0x9E3779B97F4A7C15L;\n    s ^= (long) cyTile * 0xC2B2AE3D27D4EB4FL;\n    s ^= (long) z.rTiles * 0x165667B19E3779F9L;\n    z.seed = s;\n\n    java.util.Random r = new java.util.Random(z.seed ^ 0xA11CE55EL);\n    z.targetCount = z.targetMin + (z.targetMax > z.targetMin ? r.nextInt(z.targetMax - z.targetMin + 1) : 0);\n    z.nextRespawnDayIndex = dayIndex; // allow spawn immediately\n\n    enemyZones.add(z);\n  }\n\n  /**\n   * Resets the World instance to a clean state using the provided seed.\n   *\n   * Decision + rationale:\n   * - We reset the world when loading an area to avoid procedural generation artifacts.\n   * - A hand-authored area must be deterministic and must not contain roads/water/etc.\n   *   that came from the procedural pipeline.\n   */\n  public void areaResetWorld(long seed) {\n    resetWorld(seed);\n    // Ensure node spawner starts "fresh" for the new world.\n    try { if (nodeSpawner != null) nodeSpawner.resetSpawnedChunks(); } catch (Throwable ignored) {}\n  }\n\n  /**\n   * Clears entities that are considered "area-local" (procedural nodes + encounters).\n   *\n   * We KEEP:\n   * - PLAYER (obviously)\n   * - BUILD_* (player-placed objects)\n   * - merchants (if present) (treat as world objects for now)\n   */\n  public void areaClearAreaLocalEntities() {\n    for (int i = 0; i < MAX; i++) {\n      if (!entities.alive[i]) continue;\n      EntityType t = entities.type[i];\n      if (t == PLAYER) continue;\n\n      // Keep player builds.\n      if (t == BUILD_CHEST\n          || t == BUILD_WORKBENCH\n          || t == BUILD_BED\n          || t == BUILD_CAMPFIRE\n          || t == BUILD_LAMP\n          || t == POI_CHEST_HIDDEN) {\n        continue;\n      }\n\n      // Keep merchants for now.\n      if (t == MERCHANT_ELF\n          || t == MERCHANT_WANDERING) {\n        continue;\n      }\n\n      // Remove nodes + encounters + drops.\n      entities.kill(i);\n    }\n\n    // Also clear node "removed" cache so trees in a new area can exist even\n    // if a player harvested trees in a previous area.\n    //\n    // Rationale:\n    // - In the final system, WorldNodes.removed should be per-area.\n    // - For scaffolding, we reset it on area load to avoid confusing "missing trees".\n    try { worldNodes.removed.clear(); } catch (Throwable ignored) {}\n  }\n\n  /**\n   * Clears ALL runtime state that must never survive across save/load.\n   *\n   * This is intentionally aggressive: it prevents ghost entities / duplicated players after loading.\n   */\n  private void clearAllStateForLoad() {\n    // Entities: wipe everything first; load will restore from snapshot.\n    for (int i = 0; i < Entities.MAX; i++) {\n      entities.alive[i] = false;\n      entities.type[i] = null;\n      entities.flags[i] = 0;\n      entities.itemId[i] = -1;\n      entities.itemAmount[i] = 0;\n      entities.data0[i] = -1;\n      entities.hp[i] = 0f;\n      entities.hpMax[i] = 0f;\n      entities.vx[i] = 0f;\n      entities.vy[i] = 0f;\n      entities.faceLock[i] = 0f;\n      entities.aiT[i] = 0f;\n      entities.aiT2[i] = 0f;\n      entities.aiF0[i] = 0f;\n      entities.aiF1[i] = 0f;\n      entities.rot[i] = 0f;\n    }\n    playerE = -1;\n\n    // Containers/caches that should not leak between runs.\n    try { chestStore.clear(); } catch (Throwable ignored) {}\n    try { worldNodes.removed.clear(); } catch (Throwable ignored) {}\n\n    // WorldMap is per-save; clear before importing from save.\n    try {\n      worldMap.knownAreas.clear();\n      worldMap.frontier.clear();\n      worldMap.edges.clear();\n      worldMap.exitsByArea.clear();\n      worldMap.areaStates.clear();\n      worldMap.consumedPois.clear();\n      worldMap.removedAuthoredNodes.clear();\n      worldMap.setCurrent(0, 0, "");\n    } catch (Throwable ignored) {}\n\n    // Transient UI state.\n    shopOpen = false;\n    invOpen = false;\n    craftOpen = false;\n    buildMode = false;\n    openChestE = -1;\n\n    buyPopup = false;\n    sellPopup = false;\n    buyBuffer = "";\n    // Nicht fertiges Feature: sellBuffer = "";\n    dragArmed = false;\n    dragActive = false;\n    dragSrc = DRAG_SRC_NONE;\n    dragItemId = -1;\n    dragIndex = -1;\n\n    pricingOpen = false;\n    pricingPresetPopup = false;\n\n    // Projectiles\n    for (int i = 0; i < ARROW_MAX; i++) arrowAlive[i] = false;\n\n    // CommandQueue scaffolding\n    commandQueue.clear();\n  }\n\n  /** Sets player position and keeps the PLAYER entity in sync. */\n  public void areaSetPlayerWorldPos(float wx, float wy) {\n    px = wx;\n    py = wy;\n    if (playerE >= 0) {\n      entities.x[playerE] = wx;\n      entities.y[playerE] = wy;\n    }\n  }\n\n  public void doSave(int slot) {\n    doSave(slot, "Slot " + slot);\n  }\n\n  public void doSave(int slot, String name) {\n    try {\n      SaveManager.saveSlot(\n          slot,\n          name,\n          worldSeed,\n          px, py,\n          inv,\n          wallet,\n          progress,\n          needs,\n          dayNight.t,\n          dayIndex,\n          hotbar,\n          hotbarSel,\n          hasBoat,\n          hasClimb,\n          invAnchorX,\n          invAnchorY,\n          questHubAnchorX,\n          questHubAnchorY,\n          questOpenAnchorX,\n          questOpenAnchorY,\n          questDoneAnchorX,\n          questDoneAnchorY,\n          entities,\n          chestStore,\n          worldNodes,\n          merchants,\n          questGuy,\n          questLog,\n          zqsSave,\n          worldMap\n      );\n      toast = "Saved: slot" + slot;\n      toastT = 2.5f;\n    } catch (Throwable t) {\n      toast = "Save failed";\n      toastT = 3.5f;\n    }\n  }\n  public void doLoad(int slot) {\n    try {\n      // HARD RULE: loading a save must not keep any runtime cache/state from prior runs.\n      // If we don't wipe entities/world caches first, old entities can survive and you can "see a copy" of yourself.\n      clearAllStateForLoad();\n\n      long[] seed = new long[1];\n      float[] pxpy = new float[2];\n      float[] dt = new float[1];\n      int[] dIdx = new int[1];\n      int[] hb = new int[8];\n      int[] hbSel = new int[1];\n      boolean[] boat = new boolean[1];\n      boolean[] climb = new boolean[1];\n      float[] uiPos = new float[8];\n\n      boolean ok = SaveManager.loadSlot(\n          slot,\n          seed,\n          pxpy,\n          inv,\n          wallet,\n          progress,\n          needs,\n          dt,\n          dIdx,\n          hb,\n          hbSel,\n          boat,\n          climb,\n          uiPos,\n          entities,\n          chestStore,\n          worldNodes,\n          null,\n          null,\n          questLog,\n          zqsSave,\n          worldMap\n      );\n      if (!ok) {\n        toast = "No save in slot" + slot;\n        toastT = 2.5f;\n        return;\n      }\n\n      // Inventory slot layout may be loaded (v2). As a fallback for older saves, rebuild from totals.\n      // (If slots were imported, rebuildSlotsFromCounts() would destroy organization.)\n      // Heuristic: if normal slots are still minimal and empty but counts exist, rebuild.\n      boolean anyCounts = false;\n      for (int i = 0; i < inv.countsById.length; i++) { if (inv.countsById[i] > 0) { anyCounts = true; break; } }\n      boolean anySlot = false;\n      for (int i = 0; i < inv.normalSlotCount(); i++) { if (inv.getNormalItemId(i) >= 0 && inv.getNormalCount(i) > 0) { anySlot = true; break; } }\n      for (int i = 0; i < inv.toolSlotCount() && !anySlot; i++) { if (inv.getToolItemId(i) >= 0 && inv.getToolCount(i) > 0) { anySlot = true; break; } }\n      if (anyCounts && !anySlot) inv.rebuildSlotsFromCounts();\n\n      // restore scalar state\n      px = pxpy[0];\n      py = pxpy[1];\n      // Loading a save should preserve saved HP (do not force full HP like on new game).\n      forceFullHpOnce = false;\n      dayNight.t = dt[0];\n      prevDayT = dayNight.t;\n\n      hasBoat = boat[0];\n      hasClimb = climb[0];\n\n      // UI layout positions (optional)\n      if (uiPos != null && uiPos.length >= 8) {\n        if (!Float.isNaN(uiPos[0]) && !Float.isNaN(uiPos[1])) { invAnchorX = uiPos[0]; invAnchorY = uiPos[1]; }\n        if (!Float.isNaN(uiPos[2]) && !Float.isNaN(uiPos[3])) { questHubAnchorX = uiPos[2]; questHubAnchorY = uiPos[3]; }\n        if (!Float.isNaN(uiPos[4]) && !Float.isNaN(uiPos[5])) { questOpenAnchorX = uiPos[4]; questOpenAnchorY = uiPos[5]; }\n        if (!Float.isNaN(uiPos[6]) && !Float.isNaN(uiPos[7])) { questDoneAnchorX = uiPos[6]; questDoneAnchorY = uiPos[7]; }\n      }\n\n      // Destroy active world and rebuild from save seed.\n      resetWorld(seed[0]);\n\n      // Areas-only mode: reload the current area tilemap into the new world.\n      if (AREA_MODE) {\n        try {\n          new JsonAreaWorldLoader().loadInto(this, worldMap.curTemplateId, null);\n        } catch (Throwable ignored) {}\n      }\n\n      // Re-apply mined ROCK tiles (rock->grass) from save.\n      applyMinedRockTilesFromSave();\n\n      merchants = new MerchantSystem(seed[0]);\n      merchants.importFromSave(SaveManager.readMerchantSys(slot), entities);\n\n      questGuy = new WanderQuestGuySystem(seed[0]);\n      // Old questSys save block removed; WanderQuestGuySystem has no persisted offer/timer state.\n\n      // Recreate + bind ZQS runtime facade and dock.\n      zqsRt = new ZqsRuntime(seed[0]);\n      zqsWqgDock = new WanderQuestGuyDock(zqsRt);\n      questGuy.bindZqsDock(zqsWqgDock);\n\n      questGuy.bindZqsContextProvider(() -> {\n        var c = new ZqsConversationContext();\n        // ZQS runtimeSec must be stable across save/load; use real epoch seconds.\n        long nowSec = System.currentTimeMillis() / 1000L;\n        c.epochSec = nowSec;\n        c.runtimeSec = nowSec;\n        c.debugHqMode = "EXCLUDE_HQ";\n        float t = dayNight.t;\n        if (t < 0.23f) c.timeOfDay = "morning";\n        else if (t < 0.55f) c.timeOfDay = "day";\n        else if (t < 0.78f) c.timeOfDay = "evening";\n        else c.timeOfDay = "night";\n\n        c.openQuestsCount = (questLog != null) ? questLog.size() : 0;\n        c.completedQuestsCount = 0;\n        if (questLog != null) {\n          for (int i = 0; i < questLog.entries.size; i++) {\n            var e = questLog.entries.get(i);\n            if (e != null && e.status == COMPLETED) c.completedQuestsCount++;\n          }\n        }\n\n        int oq = c.openQuestsCount;\n        if (oq <= 2) c.worldstressZone = "ruhig";\n        else if (oq <= 5) c.worldstressZone = "belebt";\n        else c.worldstressZone = "hektisch";\n\n        return c;\n      });\n\n      zqsRt.bind(data, progress, zqsSave);\n      zqsRt.bindWorldMap(worldMap);\n      zqsRt.bindEntities(entities);\n      encounterSpawner.resetTimers();\n\n      // restore scalar state\n      dayIndex = dIdx[0];\n      System.arraycopy(hb, 0, hotbar, 0, Math.min(hotbar.length, hb.length));\n      hotbarSel = (hbSel[0] >= 0 && hbSel[0] < hotbar.length) ? hbSel[0] : 0;\n\n      // Entities were restored from save snapshot; find the player entity.\n      playerE = -1;\n      for (int e = 0; e < Entities.MAX; e++) {\n        if (!entities.alive[e]) continue;\n        if (entities.type[e] == EntityType.PLAYER) { playerE = e; break; }\n      }\n      if (playerE >= 0) {\n        entities.setAlwaysActive(playerE, true);\n        px = entities.x[playerE];\n        py = entities.y[playerE];\n      } else {\n        // fallback\n        playerE = entities.spawn(EntityType.PLAYER, px, py);\n        entities.setAlwaysActive(playerE, true);\n      }\n\n      // BLOCK D: procedural nodes (ensure around, but do NOT overwrite existing restored entities)\n      // Use warmup-limited radius to avoid a generation burst right after loading.\n      int r0 = (streamWarmupFrames > 0) ? 0 : streamRadiusChunks;\n      // Node visibility/removal: do not spawn local procedural nodes here.\n      if (true) {\n        nodeSpawner.ensureAround(entities, worldNodes, px, py, r0);\n      }\n\n      shopOpen = false;\n      invOpen = false;\n      craftOpen = false;\n      buildMode = false;\n      openChestE = -1;\n\n      // Clear transient caches/state (projectiles, UI drags/popups, cooldowns) before continuing.\n      buyPopup = false;\n      buyBuffer = "";\n      dragArmed = false;\n      dragActive = false;\n// Nicht fertiges Feature:       hackSfxT = 0f;\n      bowCooldownT = 0f;\n      for (int i = 0; i < ARROW_MAX; i++) arrowAlive[i] = false;\n      commandQueue.clear();\n\n      regenShopOffers();\n\n      toast = "Loaded: slot" + slot;\n      toastT = 2.5f;\n    } catch (Throwable t) {\n      toast = "Load failed";\n      toastT = 3.5f;\n    }\n  }\n\n  @Override\n  public void dispose() {\n    if (batch != null) batch.dispose();\n    if (font != null) font.dispose();\n    if (chunkRenderer != null) chunkRenderer.dispose();\n    if (tiles != null) tiles.dispose();\n    if (entityRegions != null) entityRegions.dispose();\n    if (renderPipeline != null) renderPipeline.dispose();\n    if (uiRegions != null) uiRegions.dispose();\n\n    for (Texture t : skillIconTex.values()) {\n      try { if (t != null) t.dispose(); } catch (Throwable ignored) {}\n    }\n    skillIconTex.clear();\n\n    if (walletCoinCopperTex != null) walletCoinCopperTex.dispose();\n    if (walletCoinSilverTex != null) walletCoinSilverTex.dispose();\n    if (walletCoinGoldTex != null) walletCoinGoldTex.dispose();\n    if (dayNight1x1 != null) {\n      dayNight1x1.dispose();\n      dayNight1x1 = null;\n    }\n    if (dnLightTex != null) {\n      dnLightTex.dispose();\n      dnLightTex = null;\n    }\n    if (dnMaskFbo != null) {\n      try { dnMaskFbo.dispose(); } catch (Throwable ignored) {}\n      dnMaskFbo = null;\n    }\n    dnMaskRegion = null;\n\n    try { if (dayBgMusic != null) dayBgMusic.dispose(); } catch (Throwable ignored) {}\n    dayBgMusic = null;\n    try { if (nightBgMusic != null) nightBgMusic.dispose(); } catch (Throwable ignored) {}\n    nightBgMusic = null;\n\n    if (shape != null) shape.dispose();\n\n    if (fowTex != null) {\n      try { fowTex.dispose(); } catch (Throwable ignored) {}\n      fowTex = null;\n    }\n    if (fowPm != null) {\n      try { fowPm.dispose(); } catch (Throwable ignored) {}\n      fowPm = null;\n    }\n  }\n\n  private void drawInventoryGrid() {\n    // Inventory: normal grid + tool grid underneath.\n    final int gridW = GRID_W;\n    int normalRows = inv.normalRowsUsed();\n    int toolRows = inv.visibleToolRows();\n\n    float uiScale = 0.85f; // requested: inventory UI 15% smaller\n\n    float slot = 144f * uiScale;\n    float pad = 20f * uiScale;\n\n    float normalH = normalRows * slot + (normalRows - 1) * pad;\n    float toolH = toolRows * slot + (toolRows - 1) * pad;\n    float labelH = 52f * uiScale;\n\n    float innerW = gridW * slot + (gridW - 1) * pad;\n    float innerH = normalH + labelH + toolH;\n\n    float panelPad = 52f * uiScale;\n    float titleH = 96f * uiScale;\n    float panelW = innerW + panelPad * 2f;\n    float panelH = innerH + panelPad * 2f + titleH;\n\n    // anchored bottom-center (default) but movable by drag\n    float x0Default = (Gdx.graphics.getWidth() - panelW) * 0.5f;\n    float y0Default = 0f;\n\n    if (Float.isNaN(invAnchorX) || Float.isNaN(invAnchorY) || (invAnchorX == 0f && invAnchorY == 0f) || invJustOpened) {\n      float xPick = x0Default;\n      float yPick = y0Default;\n\n      // If craft panel is open, avoid placing inventory on top of it.\n      if (craftOpen) {\n        final float craftW = 1230f;\n        final float craftH = 540f;\n        final float cx = craftAnchorX;\n        final float cy = craftAnchorY;\n\n        final float[] outPos = new float[2];\n        placePanelAvoiding(panelW, panelH, x0Default, y0Default, cx, cy, craftW, craftH, (xx, yy) -> {\n          outPos[0] = xx;\n          outPos[1] = yy;\n        });\n        xPick = outPos[0];\n        yPick = outPos[1];\n      }\n\n      invAnchorX = xPick;\n      invAnchorY = yPick;\n      invJustOpened = false;\n    }\n\n    // Drag handling (panel header) - disabled while modal UI / drag&drop is active\n    boolean allowPanelDrag = !pricingOpen && !walletOpen && !buyPopup && !sellPopup && !dragArmed && !dragActive;\n    if (allowPanelDrag) {\n      float mx = Gdx.input.getX();\n      float my = Gdx.graphics.getHeight() - Gdx.input.getY();\n      if (Gdx.input.justTouched()) {\n        float headerY = invAnchorY + panelH - titleH;\n        if (mx >= invAnchorX && mx <= invAnchorX + panelW && my >= headerY && my <= invAnchorY + panelH) {\n          invDrag = true;\n          invDragDx = mx - invAnchorX;\n          invDragDy = my - invAnchorY;\n        }\n      }\n      if (invDrag) {\n        if (Gdx.input.isTouched()) {\n          invAnchorX = mx - invDragDx;\n          invAnchorY = my - invDragDy;\n        } else {\n          invDrag = false;\n        }\n      }\n    } else {\n      invDrag = false;\n    }\n\n    // clamp to screen so it stays reachable\n    invAnchorX = MathUtils.clamp(invAnchorX, 0f, Math.max(0f, Gdx.graphics.getWidth() - panelW));\n    invAnchorY = MathUtils.clamp(invAnchorY, 0f, Math.max(0f, Gdx.graphics.getHeight() - panelH));\n\n    float x0 = invAnchorX;\n    float y0 = invAnchorY;\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, panelW, panelH);\n\n    font.getData().setScale(2.0f * uiScale);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.draw(batch, "INVENTORY (tools -> hotbar)", x0 + panelPad, y0 + panelH - 28f * uiScale);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.setColor(0f, 0f, 0f, 1f);\n\n    float sx0 = x0 + panelPad;\n\n    float toolY0 = y0 + panelPad;\n    // Add a clearer gap between normal items and tools.\n    float labelY0 = toolY0 + toolH + 34f * uiScale;\n    float normalY0 = labelY0 + labelH;\n\n    // cache layout for hit tests\n    invSlotPx = slot;\n    invPadPx = pad;\n    invToolSlotsX0 = sx0;\n    invToolSlotsY0 = toolY0;\n    invNormSlotsX0 = sx0;\n    invNormSlotsY0 = normalY0;\n    invToolRowsDrawn = toolRows;\n    invNormRowsDrawn = normalRows;\n\n    // tool grid\n    font.getData().setScale(1.3f * uiScale);\n    font.setColor(0f, 0f, 0f, 1f);\n    font.draw(batch, "TOOLS", sx0, labelY0 + 36f * uiScale);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.setColor(0f, 0f, 0f, 1f);\n\n    for (int y = 0; y < toolRows; y++) {\n      for (int x = 0; x < gridW; x++) {\n        int idx = y * gridW + x;\n        float sx = sx0 + x * (slot + pad);\n        float sy = toolY0 + y * (slot + pad);\n        batch.draw(uiRegions.slot, sx, sy, slot, slot);\n\n        int itemId = inv.getToolItemId(idx);\n        int count = inv.getToolCount(idx);\n        if (itemId >= 0 && count > 0) {\n          TextureRegion icon = entityRegions.itemIcon(itemId);\n          float iw = slot * 0.70f;\n          float ih = slot * 0.70f;\n          if (icon != null) batch.draw(icon, sx + (slot - iw) * 0.5f, sy + (slot - ih) * 0.5f, iw, ih);\n          if (count > 1) {\n            font.setColor(0f, 0f, 0f, 1f);\n            font.draw(batch, String.valueOf(count), sx + 6f, sy + 16f);\n            font.setColor(0f, 0f, 0f, 1f);\n          }\n        }\n      }\n    }\n\n    // normal grid\n    for (int y = 0; y < normalRows; y++) {\n      for (int x = 0; x < gridW; x++) {\n        int idx = y * gridW + x;\n        float sx = sx0 + x * (slot + pad);\n        float sy = normalY0 + y * (slot + pad);\n        batch.draw(uiRegions.slot, sx, sy, slot, slot);\n\n        int itemId = inv.getNormalItemId(idx);\n        int count = inv.getNormalCount(idx);\n        if (itemId >= 0 && count > 0) {\n          TextureRegion icon = entityRegions.itemIcon(itemId);\n          float iw = slot * 0.70f;\n          float ih = slot * 0.70f;\n          if (icon != null) batch.draw(icon, sx + (slot - iw) * 0.5f, sy + (slot - ih) * 0.5f, iw, ih);\n\n          font.setColor(0f, 0f, 0f, 1f);\n          font.draw(batch, String.valueOf(count), sx + 6f, sy + 16f);\n          font.setColor(0f, 0f, 0f, 1f);\n        }\n      }\n    }\n  }\n\n  // ---------------- Pricing editor (Shift+F12) ----------------\n\n  private void pricingClampSelection() {\n    if (pricingSel < 0) pricingSel = 0;\n    if (pricingSel >= pricingEditCopper.length) pricingSel = pricingEditCopper.length - 1;\n    if (pricingSel < pricingScroll) pricingScroll = pricingSel;\n    int visible = pricingVisibleRows();\n    if (pricingSel >= pricingScroll + visible) pricingScroll = pricingSel - visible + 1;\n    if (pricingScroll < 0) pricingScroll = 0;\n    int maxScroll = Math.max(0, pricingEditCopper.length - visible);\n    if (pricingScroll > maxScroll) pricingScroll = maxScroll;\n  }\n\n  private int pricingVisibleRows() {\n    // depends on screen height; keep it cheap\n    int h = Gdx.graphics.getHeight();\n    // title + buttons + margins => roughly 200px reserved; row height ~ 22\n    return MathUtils.clamp((h - 260) / 22, 6, 26);\n  }\n\n  private void pricingApplyBufferToSelected() {\n    if (pricingSel < 0 || pricingSel >= pricingEditCopper.length) return;\n    if (pricingEditBuffer == null || pricingEditBuffer.isEmpty()) {\n      pricingEditCopper[pricingSel] = 0;\n      return;\n    }\n    try {\n      long v = Long.parseLong(pricingEditBuffer);\n      if (v < 0) v = 0;\n      if (v > 2_000_000_000L) v = 2_000_000_000L;\n      pricingEditCopper[pricingSel] = (int) v;\n    } catch (NumberFormatException ignored) {\n      // ignore\n    }\n  }\n\n  private void pricingHandleInput() {\n    // navigation\n    if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {\n      pricingSel--;\n      pricingClampSelection();\n      pricingEditBuffer = String.valueOf(pricingEditCopper[pricingSel]);\n    }\n    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {\n      pricingSel++;\n      pricingClampSelection();\n      pricingEditBuffer = String.valueOf(pricingEditCopper[pricingSel]);\n    }\n    if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {\n      pricingSel -= pricingVisibleRows();\n      pricingClampSelection();\n      pricingEditBuffer = String.valueOf(pricingEditCopper[pricingSel]);\n    }\n    if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {\n      pricingSel += pricingVisibleRows();\n      pricingClampSelection();\n      pricingEditBuffer = String.valueOf(pricingEditCopper[pricingSel]);\n    }\n\n    // actions (F5/F8/F9) are handled centrally via DebugCommands.
`\n

### Nicht fertiges Feature

- time: 2026-03-20 11:23:51
- file: core\src\main\java\com\yourgame\survival\screens\GameScreen.java
- lines: 510-510

`java
\n    if (pricingPresetPopup) {\n      // choose preset 1..9\n      int pick = -1;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) pick = 0;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) pick = 1;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) pick = 2;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) pick = 3;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) pick = 4;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) pick = 5;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) pick = 6;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) pick = 7;\n      if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) pick = 8;\n\n      if (pick >= 0 && pricingPresetNames != null && pick < pricingPresetNames.length) {\n        PriceBook tmp = new PriceBook(60);\n        tmp.copyFrom(pricingEditCopper);\n        boolean ok = tmp.applyPresetLocal(pricingPresetNames[pick]);\n        if (ok) {\n          tmp.copyTo(pricingEditCopper);\n          pricingEditBuffer = String.valueOf(pricingEditCopper[pricingSel]);\n          toast = "Preset loaded (not saved)";\n          toastT = 2.5f;\n        } else {\n          toast = "Preset load failed";\n          toastT = 2.5f;\n        }\n        pricingPresetPopup = false;\n      }\n\n      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {\n        pricingPresetPopup = false;\n      }\n    }\n  }\n\n  private String[] pricingListPresetNames() {\n    try {\n      com.badlogic.gdx.files.FileHandle dir = Gdx.files.local("pricing_presets");\n      if (!dir.exists()) return new String[0];\n      com.badlogic.gdx.files.FileHandle[] files = dir.list("json");\n      int n = Math.min(9, files.length);\n      String[] names = new String[n];\n      for (int i = 0; i < n; i++) names[i] = files[i].name();\n      return names;\n    } catch (Throwable t) {\n      return new String[0];\n    }\n  }\n\n  private void drawWalletMenu() {\n    float w = Math.min(560f, Gdx.graphics.getWidth() - 40f);\n    float h = 300f;\n    float x0 = (Gdx.graphics.getWidth() - w) * 0.5f;\n    float y0 = (Gdx.graphics.getHeight() - h) * 0.5f;\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, w, h);\n\n    float pad = 28f;\n    float x = x0 + pad;\n    float yTop = y0 + h - pad;\n\n    long copper = wallet.copper;\n    double silver = copper / 100.0;      // 100 copper = 1 silver\n    double gold = copper / 100000.0;     // 1000 silver = 1 gold => 100000 copper\n\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.6f * 1.15f);\n    font.draw(batch, "WALLET (G close)", x, yTop);\n\n    float rowGap = 66f;\n    float rowTop = yTop - 70f;\n\n    float iconFrame = 54f;\n    float iconInner = 42f;\n\n    float valueBoxH = 48f;\n    float valueX = x + iconFrame + 14f;\n    float valueW = (x0 + w - pad) - valueX;\n\n    font.getData().setScale(1.15f * UI_FONT_SCALE);\n\n    // Kupfer\n    {\n      float by = rowTop;\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(uiRegions.slot, x, by - iconFrame + 10f, iconFrame, iconFrame);\n      if (walletCoinCopperIcon != null) {\n        batch.draw(walletCoinCopperIcon, x + (iconFrame - iconInner) * 0.5f, by - iconFrame + 10f + (iconFrame - iconInner) * 0.5f, iconInner, iconInner);\n      }\n      batch.draw(uiRegions.button, valueX, by - valueBoxH + 10f, valueW, valueBoxH);\n      font.draw(batch, String.format("%d", copper), valueX + 16f, by - 8f);\n    }\n\n    // Silber\n    {\n      float by = rowTop - rowGap;\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(uiRegions.slot, x, by - iconFrame + 10f, iconFrame, iconFrame);\n      if (walletCoinSilverIcon != null) {\n        batch.draw(walletCoinSilverIcon, x + (iconFrame - iconInner) * 0.5f, by - iconFrame + 10f + (iconFrame - iconInner) * 0.5f, iconInner, iconInner);\n      }\n      batch.draw(uiRegions.button, valueX, by - valueBoxH + 10f, valueW, valueBoxH);\n      font.draw(batch, String.format("%.2f", silver), valueX + 16f, by - 8f);\n    }\n\n    // Gold\n    {\n      float by = rowTop - rowGap * 2f;\n      batch.setColor(1f, 1f, 1f, 1f);\n      batch.draw(uiRegions.slot, x, by - iconFrame + 10f, iconFrame, iconFrame);\n      if (walletCoinGoldIcon != null) {\n        batch.draw(walletCoinGoldIcon, x + (iconFrame - iconInner) * 0.5f, by - iconFrame + 10f + (iconFrame - iconInner) * 0.5f, iconInner, iconInner);\n      }\n      batch.draw(uiRegions.button, valueX, by - valueBoxH + 10f, valueW, valueBoxH);\n      font.draw(batch, String.format("%.4f", gold), valueX + 16f, by - 8f);\n    }\n\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.setColor(0f, 0f, 0f, 1f);\n  }\n\n  private void drawPricingMenu() {\n    float w = Math.min(1120f, Gdx.graphics.getWidth() - 40f);\n    float h = Math.min(720f, Gdx.graphics.getHeight() - 40f);\n    float x0 = (Gdx.graphics.getWidth() - w) * 0.5f;\n    float y0 = (Gdx.graphics.getHeight() - h) * 0.5f;\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, w, h);\n\n    float pad = 26f;\n    float x = x0 + pad;\n    float yTop = y0 + h - pad;\n\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.6f * 1.15f);\n    font.draw(batch, "PRICING (Shift+F12 close) | F5 Save | F8 Reset(defaults) | F9 Preset", x, yTop);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n\n    float y = yTop - 40f;\n    font.getData().setScale(1.0f * 1.15f);\n    font.draw(batch, "[Item]   Current(Copper)   Current(Gold)     New(Copper)   New(Gold)", x, y);\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    y -= 22f;\n\n    int visible = pricingVisibleRows();\n    int start = pricingScroll;\n    int end = Math.min(pricingEditCopper.length, start + visible);\n\n    for (int itemId = start; itemId < end; itemId++) {\n      boolean sel = (itemId == pricingSel);\n      if (sel) {\n        // simple highlight bar\n        batch.setColor(1f, 1f, 1f, 0.10f);\n        batch.draw(uiRegions.slotPressed, x - 10f, y - 16f, w - pad * 2f + 20f, 22f);\n        batch.setColor(1f, 1f, 1f, 1f);\n      }\n\n      String name = (itemId >= 0 && itemId < data.items.length && data.items[itemId] != null) ? data.items[itemId].name : ("item_" + itemId);\n      int cur = priceBook.getBaseCopper(itemId);\n      int neu = pricingEditCopper[itemId];\n\n      String curGold = PriceBook.fmtGoldFromCopper(cur);\n      String newGold = PriceBook.fmtGoldFromCopper(neu);\n\n      String newCopperStr = (sel ? (pricingEditBuffer.isEmpty() ? "" : pricingEditBuffer) : String.valueOf(neu));\n\n      font.draw(batch, String.format("%02d %-18s %8d   %-10s   %8s   %-10s", itemId, name, cur, curGold, newCopperStr, newGold), x, y);\n      y -= 22f;\n    }\n\n    if (pricingPresetPopup) {\n      float popupY = y0 + 120f;\n      font.getData().setScale(1.3f);\n      font.draw(batch, "PRESET wählen (1..9) | ESC abbrechen", x, popupY);\n      font.getData().setScale(1.0f * UI_FONT_SCALE);\n      popupY -= 24f;\n      if (pricingPresetNames == null || pricingPresetNames.length == 0) {\n        font.draw(batch, "(keine Presets gefunden in: pricing_presets/*.json)", x, popupY);\n      } else {\n        for (int i = 0; i < pricingPresetNames.length; i++) {\n          font.draw(batch, (i + 1) + ") " + pricingPresetNames[i], x, popupY - i * 18f);\n        }\n      }\n    }\n  }\n\n  private void preloadSkillIcons() {\n    if (skillIconsLoaded) return;\n\n    for (int i = 0; i < SkillDefs.COUNT; i++) {\n      String path = SkillDefs.ICON_PATH[i];\n      if (path == null || path.isEmpty()) continue;\n\n      try {\n        FileHandle fh = Gdx.files.internal(path);\n        if (fh == null || !fh.exists()) continue;\n\n        Texture t = new Texture(fh);\n        // crisp pixel art\n        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);\n        skillIconTex.put(i, t);\n      } catch (Throwable ignored) {\n        // keep menu usable even if one icon fails\n      }\n    }\n\n    skillIconsLoaded = true;\n  }\n\n  private Texture getSkillIconTex(int idx) {\n    if (idx < 0 || idx >= SkillDefs.COUNT) return null;\n    return skillIconTex.get(idx);\n  }\n\n  private static String keyCatSub(String catKey, String subLabel) {\n    return catKey + "::" + subLabel;\n  }\n\n  private void addSkillMenuSub(String catKey, String sub, String... skillIds) {\n    String subKey = keyCatSub(catKey, sub);\n    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER2, sub, -1, catKey, subKey));\n    skillMenuSubOpen.putIfAbsent(subKey, true);\n\n    for (String sid : skillIds) {\n      int idx = SkillDefs.indexOf(sid);\n      if (idx >= 0) skillMenuRows.add(new SkillMenuRow(SkillMenuRow.SKILL, SkillDefs.NAME_DE[idx], idx, catKey, subKey));\n    }\n  }\n\n  private void buildSkillMenuRows() {\n    if (skillMenuRowsBuilt) return;\n    skillMenuRows.clear();\n\n    // Matches docs/Vision_Skizze.png structure: 5 categories with subcategories.\n    // Ressourcen\n    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Ressourcen", -1, "Ressourcen", null));\n    skillMenuCatOpen.putIfAbsent("Ressourcen", true);\n    addSkillMenuSub("Ressourcen", "Sammeln", "Mining", "Woodcutting", "Herbalism", "Hunting", "Fishing", "Foraging");\n    addSkillMenuSub("Ressourcen", "Verarbeitung", "Smelting", "Tanning", "Alchemy", "Cooking");\n\n    // Kampf\n    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Kampf", -1, "Kampf", null));\n    skillMenuCatOpen.putIfAbsent("Kampf", true);\n    addSkillMenuSub("Kampf", "Waffen", "CombatMelee", "CombatRanged", "WeaponCraft", "DualWield", "ShieldMastery");\n    addSkillMenuSub("Kampf", "Verteidigung", "ArmorCraft", "Dodging", "Parrying", "Toughness");\n    addSkillMenuSub("Kampf", "Taktik", "Stealth", "Tracking", "Looting");\n\n    // Soziales\n    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Soziales", -1, "Soziales", null));\n    skillMenuCatOpen.putIfAbsent("Soziales", true);\n    addSkillMenuSub("Soziales", "Handel", "Trading", "Barter", "Intimidation", "Persuasion");\n    addSkillMenuSub("Soziales", "Support", "Leadership", "Negotiation", "Reputation");\n\n    // Person\n    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Person", -1, "Person", null));\n    skillMenuCatOpen.putIfAbsent("Person", true);\n    addSkillMenuSub("Person", "Vitals", "Health", "Stamina", "HungerControl", "SleepControl");\n    addSkillMenuSub("Person", "Mobilität", "CarryCapacity", "Climbing", "Boating", "FlyingEfficiency");\n    addSkillMenuSub("Person", "Survival", "ColdResistance", "HeatResistance");\n\n    // Andere\n    skillMenuRows.add(new SkillMenuRow(SkillMenuRow.HEADER1, "Andere", -1, "Andere", null));\n    skillMenuCatOpen.putIfAbsent("Andere", true);\n    addSkillMenuSub("Andere", "Bauen", "Building");\n    addSkillMenuSub("Andere", "Übernatürlich", "MagicAffinity", "SpiritSight", "Blessing");\n    addSkillMenuSub("Andere", "Utility", "Crafting", "Engineering", "ToolDurability", "Navigation", "Scavenging");\n\n    skillMenuRowsBuilt = true;\n  }\n\n  private void drawSkillMenu() {\n    // Modal overlay. Simulation is paused elsewhere.\n    float mx = Gdx.input.getX();\n    float my = uiMouseYUp();\n\n    // Close (ESC). Note: P toggling is handled in the main input section to avoid instant close on the open-press.\n    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {\n      skillsOpen = false;\n      return;\n    }\n\n    // Fit to screen (avoid negative x/y on smaller resolutions)\n    float screenW = Gdx.graphics.getWidth();\n    float screenH = Gdx.graphics.getHeight();\n    float w = Math.min(1120f, screenW - 40f);\n    float h = Math.min(720f, screenH - 40f);\n    w = Math.max(520f, w);\n    h = Math.max(360f, h);\n    float x0 = (screenW - w) * 0.5f;\n    float y0 = (screenH - h) * 0.5f;\n    if (x0 < 20f) x0 = 20f;\n    if (y0 < 20f) y0 = 20f;\n\n    batch.setColor(1f, 1f, 1f, 1f);\n    batch.draw(uiRegions.panelSlots, x0, y0, w, h);\n\n    font.setColor(0f, 0f, 0f, 1f);\n    font.getData().setScale(1.6f * UI_FONT_SCALE);\n    font.draw(batch, "SKILLBAUM", x0 + 36f, y0 + h - 28f);\n\n    font.getData().setScale(1.0f * UI_FONT_SCALE);\n    font.draw(batch, "Abenteurer Lvl: " + progress.level + "   XP: " + progress.xp + "/" + progress.xpToNext + "   Skillpunkte: " + progress.skillPoints, x0 + 36f, y0 + h - 70f);\n    font.draw(batch, "ESC oder P = schließen | Klick auf Skill = +1", x0 + 36f, y0 + h - 104f);\n\n    float listX = x0 + 36f;\n    float listYTop = y0 + h - 150f;\n    float listYBottom = y0 + 36f;\n\n    // row metrics\n    float rowSkillH = 42f * UI_FONT_SCALE;\n    float rowHeader1H = 44f * UI_FONT_SCALE;\n    float rowHeader2H = 38f * UI_FONT_SCALE;\n\n    // Compute total content height (visible rows only) to clamp scroll.\n    float totalH = 0f;\n    for (int ri = 0; ri < skillMenuRows.size(); ri++) {\n      SkillMenuRow r = skillMenuRows.get(ri);\n      boolean visible = true;\n      if (r.kind != SkillMenuRow.HEADER1) {\n        visible = skillMenuCatOpen.getOrDefault(r.catKey, true);\n        if (visible && r.kind == SkillMenuRow.SKILL) {\n          visible = skillMenuSubOpen.getOrDefault(r.subKey, true);\n        }\n        if (visible && r.kind == SkillMenuRow.HEADER2) {\n          visible = true; // sub headers are visible when category is open\n        }\n      }\n      if (!visible) continue;\n      float rh = (r.kind == SkillMenuRow.SKILL) ? (42f * UI_FONT_SCALE) : (r.kind == SkillMenuRow.HEADER1 ? (44f * UI_FONT_SCALE) : (38f * UI_FONT_SCALE));\n      totalH += rh;\n    }\n    float viewH = Math.max(1f, listYTop - listYBottom);\n    float maxScroll = Math.max(0f, totalH - viewH);\n    if (skillMenuScrollPx > maxScroll) skillMenuScrollPx = maxScroll;\n\n    String catHoverKey = null;\n    String subHoverKey = null;\n\n    skillHover = -1;\n    float y = listYTop + skillMenuScrollPx;\n    for (int ri = 0; ri < skillMenuRows.size(); ri++) {\n      SkillMenuRow r = skillMenuRows.get(ri);\n\n      if (r.kind != SkillMenuRow.HEADER1) {\n        boolean catOpen = skillMenuCatOpen.getOrDefault(r.catKey, true);\n        if (!catOpen) continue;\n\n        // skills are only visible if their subcategory is open\n        if (r.kind == SkillMenuRow.SKILL) {\n          boolean subOpen = skillMenuSubOpen.getOrDefault(r.subKey, true);\n          if (!subOpen) continue;\n        }\n      }\n\n      float rh = (r.kind == SkillMenuRow.SKILL) ? rowSkillH : (r.kind == SkillMenuRow.HEADER1 ? rowHeader1H : rowHeader2H);\n      float ry = y;\n      // skip rows above the visible window\n      if (ry > listYTop + rh) {\n        y -= rh;\n        continue;\n      }\n      // stop when we are past the bottom\n      if (ry < listYBottom) break;\n\n      switch (r.kind) {\n        case SkillMenuRow.HEADER1 -> {\n          boolean hov = mx >= listX && mx <= listX + (w - 72f) && my >= ry - rh + 6f && my <= ry + 6f;\n          if (hov) catHoverKey = r.catKey;\n\n          boolean open = skillMenuCatOpen.getOrDefault(r.catKey, true);\n          String twisty = open ? "[-]" : "[+]";\n\n          // Category: distinct big button\n          batch.setColor(1f, 1f, 1f, 1f);\n          batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, listX, ry - rh + 8f, w - 72f, rh - 8f);\n\n          font.setColor(0f, 0f, 0f, 1f);\n          font.getData().setScale(1.20f * UI_FONT_SCALE);\n          font.draw(batch, twisty + "  " + r.label, listX + 16f, ry - 12f);\n        }\n        case SkillMenuRow.HEADER2 -> {\n          // Subcategory indent (about "2 tiles"), keep right edge aligned by shrinking width.\n          float subIndent = 64f;\n          float subX = listX + 10f + subIndent;\n          float subW = (w - 72f) - 20f - subIndent;\n\n          boolean hov = mx >= subX && mx <= subX + subW && my >= ry - rh + 6f && my <= ry + 6f;\n          if (hov) subHoverKey = r.subKey;\n\n          boolean open = skillMenuSubOpen.getOrDefault(r.subKey, true);\n          String twisty = open ? "[-]" : "[+]";\n\n          // Subcategory: distinct medium row\n          batch.setColor(1f, 1f, 1f, 1f);\n          batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, subX, ry - rh + 10f, subW, rh - 12f);\n\n          font.setColor(0f, 0f, 0f, 1f);\n          font.getData().setScale(1.05f * UI_FONT_SCALE);\n          font.draw(batch, twisty + "  " + r.label, subX + 18f, ry - 12f);\n        }\n        default -> {\n          int i = r.skillIndex;\n\n        // Skill indent (relative to subcategory position), keep right edge aligned.\n        float subIndentBase = 64f;\n        float skillIndent = subIndentBase + 64f;\n        float sx = listX + skillIndent;\n        float sw = (w - 72f) - skillIndent;\n\n        boolean hov = mx >= sx && mx <= sx + sw && my >= ry - rh + 6f && my <= ry + 6f;\n        if (hov) skillHover = i;\n\n        batch.setColor(1f, 1f, 1f, 1f);\n        batch.draw(hov ? uiRegions.buttonPressed : uiRegions.button, sx, ry - rh + 8f, sw, rh - 8f);\n\n        int lv = (i >= 0 && i < progress.skillLv.length) ? progress.skillLv[i] : 0;\n        int cost = SkillEffects.upgradeCost(lv);\n        String line = SkillDefs.NAME_DE[i] + "   [" + lv + "/50]" + "   Kosten: " + cost;\n\n        float iconS = Math.min(32f * UI_FONT_SCALE, rh - 12f);\n        Texture iconTex = getSkillIconTex(i);\n        float textX = sx + 20f;\n        if (iconTex != null) {\n          float ix = sx + 10f;\n          float iy = (ry - rh + 8f) + ((rh - 8f) - iconS) * 0.5f;\n\n          // Keep aspect ratio (avoid squashing tall/wide sprites)\n          float tw = Math.max(1f, iconTex.getWidth());\n          float th = Math.max(1f, iconTex.getHeight());\n          float s = Math.min(iconS / tw, iconS / th);\n          float dw = tw * s;\n          float dh = th * s;\n          float dx = ix + (iconS - dw) * 0.5f;\n          float dy = iy + (iconS - dh) * 0.5f;\n\n          batch.setColor(1f, 1f, 1f, 1f);\n          batch.draw(iconTex, dx, dy, dw, dh);\n          textX = ix + iconS + 14f;\n        }\n\n        font.setColor(0f, 0f, 0f, 1f);\n        font.getData().setScale(1.0f * UI_FONT_SCALE);\n        font.draw(batch, line, textX, ry - 12f);\n        }\n      }\n\n      y -= rh;\n    }\n\n    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {\n      // Toggle category dropdown\n      if (catHoverKey != null) {\n        boolean open = skillMenuCatOpen.getOrDefault(catHoverKey, true);\n        skillMenuCatOpen.put(catHoverKey, !open);\n        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n        return;\n      }\n\n      // Toggle subcategory dropdown\n      if (subHoverKey != null) {\n        boolean open = skillMenuSubOpen.getOrDefault(subHoverKey, true);\n        skillMenuSubOpen.put(subHoverKey, !open);\n        game.audio.sfx("audio/sfx/ui_click.wav", game.audio.sfxVolume(game.settings));\n        return;\n      }\n\n      // Click skill\n      if (skillHover >= 0) {\n        int i = skillHover;\n        if (i >= 0 && i < progress.skillLv.length) {\n          int curLv = progress.skillLv[i];\n          int cost = SkillEffects.upgradeCost(curLv);\n\n          if (curLv < 50 && progress.skillPoints >= cost) {\n             \n          } else {\n            game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n          }\n        } else {\n          game.audio.sfx("audio/sfx/ui_error.wav", game.audio.sfxVolume(game.settings));\n        }\n      }\n    }\n  }\n\n  /** Ensures cursor state is re-applied after leaving a modal screen (Pause/Options/etc). */\n  public void forceCursorResync() {\n    lastCursorHidden = !lastCursorHidden;\n  }\n\n  // -----------------------------------------------------------------------------\n  // Block 7 helper types (structural only; keep allocations out of hot paths)\n  // -----------------------------------------------------------------------------\n\n  private static final class RenderScratch {\n    final Vector2 v2a = new Vector2();\n    final Vector3 v3a = new Vector3();\n    final Vector3 v3b = new Vector3();\n\n    final Rectangle r0 = new Rectangle();\n    final Rectangle r1 = new Rectangle();\n  }\n\n  // Nicht fertiges Feature: unused placeholder type (no current gameflow usage)\n  /*\n  private static final class UiState {\n    // Placeholder for future UI state consolidation.\n    boolean reserved;\n  }\n  */\n\n  private static final class InputState {\n    // Nicht fertiges Feature: currently never read\n    // float delta;\n    // int mx;\n    // int my;\n  }\n\n  // Nicht fertiges Feature: unused placeholder type (no current gameflow usage)\n  /*\n  private static final class CommandBuffer {\n    // Placeholder for future command buffering.\n    int size = 0;\n    void clear() { size = 0; }\n  }\n  */\n\n\n  // Nicht fertiges Feature: commented out unused block\n  /*\n  private static int anchorToStep(int tile, int step) {\n    if (step <= 1) return tile;\n    // Round to nearest multiple of step (works for negative coordinates too).\n    int m = Math.round(tile / (float) step);\n    return m * step;\n  }\n  */\n\n}\n
`\n

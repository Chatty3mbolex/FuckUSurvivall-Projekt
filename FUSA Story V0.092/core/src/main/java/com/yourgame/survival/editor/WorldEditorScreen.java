package com.yourgame.survival.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.render.ChunkRenderer;
import com.yourgame.survival.render.EntityRegions;
import com.yourgame.survival.render.EntityRenderer;
import com.yourgame.survival.render.TilesetRegions;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.World;
import com.yourgame.survival.worldmap.JsonAreaWorldLoader;

/**
 * WorldEditor screen.
 *
 * NOTE:
 * This screen used to be a biome/zone/height/deco editor.
 * It is now being repurposed into an Area editor for assets/areas/*.area.json.
 *
 * Current focus: remove biome-editor dirt and keep a slim preview of an authored area.
 */
public final class WorldEditorScreen extends ScreenAdapter {
  private static final long PREVIEW_SEED = 1234567L;
  private static final String AREA_DIR_INTERNAL = "areas";
  private static final String AREA_DIR_ABS = "assets/areas";
  private static final String AREA_EXT = ".area.json";

  private final SurvivalGame game;

  private Stage stage;
  // ---------------------------------------------------------------------------
  // UI "boxes" (stacked vertically, clamped to screen top/bottom).
  // Each box uses a nested head/body/bottom layout.
  //
  // IMPORTANT: This is intentionally scaffolded with DUMMIES first.
  // We will wire real controls into each section step-by-step.
  // ---------------------------------------------------------------------------

  private VisWindow boxMapLoader;
  private VisWindow boxTerrainEdit;
  private VisWindow boxMarkersZones;
  private VisWindow boxEntitiesObjects;
  private VisWindow boxMouseTools;

  // --- Map loader box widgets (REAL, already working) ---
  private SelectBox<String> typeBox;
  private SelectBox<String> areaBox;
  private TextButton btnLoad;
  private TextButton btnNew;
  private TextButton btnSave;
  private TextButton btnPath;
  private Label lblStatus;

  // Debug: remember last load/save paths so we can show them in a readable popup.
  private String lastLoadedAbsPath = null;
  private String lastSavedAbsPath = null;

  // --- Terrain edit box (DUMMY placeholders) ---
  // Terrain palette UI (ground ids + water).
  private Table terrainPalette;
  private SelectBox<String> terrainLayerBox;
  // NOTE: mouse tools (action/mode) are UNIVERSAL and live in the Mouse tools box.
  private Slider brushSizeSlider;
  private Label brushSizeLabel;
  private int brushSize = 1; // 1..9 (square)

  // Terrain palette selection (single active selection across categories).
  private int selectedGroundId = -1;      // 0..N-1, only full ground tiles
  private boolean selectedWater = false;  // water tool selected

  // Terrain palette visuals
  private TextureRegionDrawable terrainSelectedBorder;
  private java.util.HashMap<Integer, Image> groundIdToBorder = new java.util.HashMap<>();
  private Image waterBorder;

  // Area edit dirty flag (preview + JSON in-memory modifications).
  private boolean areaDirty = false;

  // HOME tile-tree preview (editor overlay). Runtime uses GameScreen tile-tree bits.
  // Editor has no GameScreen, so we compute a deterministic presence mask for visualization.
  private byte[] editorTreePresentBits = null;
  private int editorTreeW = 0;
  private int editorTreeH = 0;

  // TileTrees sprite preview (editor-only): render actual NODE_TREE sprites for TileTree bits.
  // This is NOT stored in JSON; it exists so HOME/FOREST look like ingame.
  private byte[] editorTileTreePresentBits = null;
  private byte[] editorTileTreeCutBits = null;
  private int editorTileTreeW = 0;
  private int editorTileTreeH = 0;

  // --- Markers & zones box (DUMMY placeholders) ---
  // Markers & zones tools.
  private Table markersZonesRoot;
  private Table markerButtons;
  private Table zoneButtons;
  private ButtonGroup<TextButton> markerGroup;
  private ButtonGroup<TextButton> zoneGroup;
  // Uses universal ToolMode from Mouse tools box.

  // Selection: marker vs zone is mutually exclusive.
  private boolean markerActive = false;
  private boolean zoneActive = false;
  private String selectedMarkerKind = null; // e.g. PLAYER_SPAWN, POI:<kind>, NODE:<EntityType>
  private String selectedZoneKind = null;   // e.g. enemy zone kind

  // Drag state for zone operations
  private boolean zoneDragging = false;
  private int zoneDragStartTx = -1;
  private int zoneDragStartTy = -1;
  private JsonValue zoneDragTarget = null; // existing zone object when MOVE/ADJUST

  // Drag state for marker/node/objects MOVE (click-hold-drag-release)
  private boolean moveDragging = false;
  private JsonValue moveDragTarget = null; // points to the JSON object being moved
  private String moveDragKind = null;      // "PLAYER_SPAWN" | "NODE" | "POI" | "OBJECT"
  private com.yourgame.survival.entity.EntityType movePreviewEntityType = null;
  private int movePreviewEntityIndex = -1; // exact preview entity to move (fixes "wrong sprite moves")

  // Drag state for TileTrees (bitmask MOVE). Editor-only.
  private boolean tileTreeDragging = false;
  private int tileTreeDragSrcTx = -1;
  private int tileTreeDragSrcTy = -1;
  private int tileTreeDragLastTx = -1;
  private int tileTreeDragLastTy = -1;

  // --- Entities & objects box (DUMMY placeholders) ---
  // DUMMY: will become object list + selected entity properties (incl. layerBias).
  private Label dummyEntities;

  // --- Objects authoring UI (markers.objects[]) ---
  // NOTE: "Objects" here are authored entity placements (landmarks/props/etc.).
  // They are stored in area JSON under: markers.objects[]
  private Table objectsUiRoot;
  private com.badlogic.gdx.scenes.scene2d.ui.List<String> objectsList;
  private VisScrollPane objectsScroll;
  private TextButton btnObjectsRescan;
  private TextButton btnObjectJump;
  private SelectBox<String> objectTypeBox;
  private TextButton btnObjectTool;
  private Label lblObjectsCount;

  // Selected object details (read-only for step 1; editing/placing comes later).
  private TextField tfObjType;
  private TextField tfObjX;
  private TextField tfObjY;
  private TextField tfObjLayerBias;
  private Slider objLayerBiasSlider;

  private final java.util.ArrayList<JsonValue> objectsJson = new java.util.ArrayList<>();
  private int selectedObjectIndex = -1;
  private JsonValue selectedObjectJson = null;

  // Object tool selection (cross-category with terrain/markers/zones).
  private boolean objectActive = false;
  private String selectedObjectType = null; // EntityType name

  // --- Mouse tools box (DUMMY placeholders) ---
  // DUMMY placeholders for mouse tools (we wire basic actions already).
  private Label dummyMouse;

  private enum LayerTarget { GROUND, ROAD, WATER }
  // Universal editor mode (applies to terrain, markers/zones, objects).
  private enum ToolMode { REPLACE, SET, DELETE, MOVE, PLACE, DEFINE, ADJUST }

  private LayerTarget layerTarget = LayerTarget.GROUND;
  private ToolMode toolMode = ToolMode.REPLACE;

  // LMB paint state
  private boolean lmbPainting = false;
  private int lastPaintTx = Integer.MIN_VALUE;
  private int lastPaintTy = Integer.MIN_VALUE;

  // Universal delete (eraser) throttling: allow "hold to delete" but slower than painting.
  private long lastDeleteAtMs = 0L;
  private int lastDeleteTx = Integer.MIN_VALUE;
  private int lastDeleteTy = Integer.MIN_VALUE;
  private static final long DELETE_HOLD_INTERVAL_MS = 80L;

  private String[] allTemplateIds = new String[0];
  private String[] allTypes = new String[] { "ALL" };

  private SpriteBatch batch;
  private ShapeRenderer shapes;
  private BitmapFont hudFont;

  private OrthographicCamera cam;
  private Viewport viewport;

  // Preview world (forced single biome; then overridden by authored area JSON)
  private World previewWorld;
  private TilesetRegions tiles;
  private ChunkRenderer chunkRenderer;

  private Entities entities;
  private EntityRegions entityRegions;
  private EntityRenderer entityRenderer;

  // Loaded area json (raw)
  private JsonValue areaJson;

  // Cached area dimensions (tiles)
  private int areaW = 0;
  private int areaH = 0;

  // Current selected template id
  private String currentTemplateId = "FOREST_01";

  private boolean panning = false;
  private final Vector2 panLast = new Vector2();

  public WorldEditorScreen(SurvivalGame game) {
    this.game = game;
  }

  @Override
  public void show() {
    try { game.setCursorCrosshair(); } catch (Throwable ignored) {}

    if (!VisUI.isLoaded()) VisUI.load();

    batch = new SpriteBatch();
    shapes = new ShapeRenderer();
    hudFont = new BitmapFont();

    stage = new Stage(new ScreenViewport());

    cam = new OrthographicCamera();
    cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.zoom = 0.70f;

    // Center camera roughly inside the first chunk.
    float wChunk = World.CHUNK_SIZE * World.TILE_WORLD;
    cam.position.set(wChunk * 0.5f, wChunk * 0.5f, 0f);
    cam.update();

    viewport = new ScreenViewport(cam);
    viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

    // Input ordering matters:
    // - We want map zoom/pan to work even if Scene2D holds a scrollFocus from prior UI interaction.
    // - We still want UI widgets (ScrollPanes etc.) to receive wheel events when the pointer is over UI.
    // Therefore, route input through InputHandler FIRST. It will:
    //   - return false when pointer is over UI -> Stage gets the event
    //   - return true when pointer is over the map -> Stage does not consume it
    InputMultiplexer mux = new InputMultiplexer();
    mux.addProcessor(new InputHandler());
    mux.addProcessor(stage);
    Gdx.input.setInputProcessor(mux);

    // Preview renderer
    tiles = new TilesetRegions();
    chunkRenderer = new ChunkRenderer(tiles);

    entities = new Entities();
    entityRegions = new EntityRegions();
    entityRenderer = new EntityRenderer(entityRegions);

    // Build preview world with a forced biome so we have valid chunks/arrays,
    // then apply the authored area JSON on top.
    previewWorld = new World(PREVIEW_SEED, null, Biome.GRASSLAND);

    buildUi();
    rescanAreas();
    selectTemplateIfExists(currentTemplateId);
    loadSelectedAreaFromUi();
  }

  private void buildUi() {
    // Build individual stacked boxes (NOT one huge wrapper), each with head/body/bottom.
    boxMapLoader = buildBox("Map loader", buildMapLoaderBody());
    boxTerrainEdit = buildBox("Terrain edit", buildTerrainEditBodyDummy());
    boxMarkersZones = buildBox("Markers + zones", buildMarkersZonesBodyDummy());
    boxEntitiesObjects = buildBox("Entities + objects", buildEntitiesObjectsBodyDummy());
    boxMouseTools = buildBox("Mouse tools", buildMouseToolsBodyDummy());

    // Add in a stable order. Layout/position is handled per-frame (clamped).
    stage.addActor(boxMapLoader);
    stage.addActor(boxTerrainEdit);
    stage.addActor(boxMarkersZones);
    stage.addActor(boxEntitiesObjects);
    stage.addActor(boxMouseTools);

    layoutUiBoxes();

    typeBox.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        refreshAreaDropdown();
      }
    });

    btnLoad.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        loadSelectedAreaFromUi();
      }
    });

    btnNew.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        openNewAreaDialog();
      }
    });

    btnSave.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        saveCurrentAreaToDisk();
      }
    });

    btnPath.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        showPathsPopup();
      }
    });
  }

  private void showPathsPopup() {
    try {
      VisWindow w = new VisWindow("Paths");
      w.setModal(true);
      w.setMovable(true);
      w.setResizable(false);

      String txt =
          "Loaded from:\n" + (lastLoadedAbsPath == null ? "(none)" : lastLoadedAbsPath) +
          "\n\nSaved to:\n" + (lastSavedAbsPath == null ? "(none)" : lastSavedAbsPath);

      com.badlogic.gdx.scenes.scene2d.ui.TextArea ta = new com.badlogic.gdx.scenes.scene2d.ui.TextArea(txt, VisUI.getSkin());
      ta.setDisabled(true);

      VisScrollPane sp = new VisScrollPane(ta);
      sp.setFadeScrollBars(false);
      sp.setScrollingDisabled(true, false);

      TextButton close = new TextButton("Close", VisUI.getSkin());
      close.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
          w.remove();
        }
      });

      Table root = new Table(VisUI.getSkin());
      root.defaults().pad(6).left();
      root.add(sp).width(720).height(220).row();
      root.add(close).width(160).left();

      w.add(root);
      w.pack();
      w.setPosition(
          (Gdx.graphics.getWidth() - w.getWidth()) * 0.5f,
          (Gdx.graphics.getHeight() - w.getHeight()) * 0.5f);
      stage.addActor(w);
    } catch (Throwable ignored) {}
  }

  /**
   * Writes the current in-memory JsonValue back to `assets/areas/<templateId>.area.json`.
   *
   * This is the required next step so editor changes are not "temporary".
   */
  private void saveCurrentAreaToDisk() {
    try {
      if (areaJson == null || currentTemplateId == null || currentTemplateId.isBlank()) {
        setStatus("Nothing to save");
        return;
      }

      // Editor-only persistence bridge:
      // Persist TileTrees bitmasks directly into JSON (NOT as markers.nodes).
      persistTileTreeBitsToJson();

      FileHandle dir = resolveAreasWriteDir();
      if (dir == null) {
        setStatus("Missing write dir (assets/areas)");
        return;
      }
      FileHandle out = dir.child(currentTemplateId + AREA_EXT);

      // Pretty print (stable, human-editable).
      String txt = areaJson.prettyPrint(JsonWriter.OutputType.json, 2);
      out.writeString(txt + "\n", false, "UTF-8");

      areaDirty = false;
      String p;
      try { p = out.file().getAbsolutePath(); } catch (Throwable t) { p = out.path(); }
      lastSavedAbsPath = p;
      setStatus("Saved: " + currentTemplateId + "\n" + "to: " + shortPath(p, 84));
    } catch (Throwable t) {
      setStatus("Save failed");
    }
  }

  /**
   * Persists editor TileTrees (bitmask layer) into JSON as Base64, so runtime can load the exact same trees.
   * Format (top-level):
   * {
   *   "tileTrees": { "w":384, "h":384, "presentB64":"...", "cutB64":"..." }
   * }
   */
  private void persistTileTreeBitsToJson() {
    try {
      if (areaJson == null) return;
      if (editorTileTreePresentBits == null || editorTileTreeW <= 0 || editorTileTreeH <= 0) return;
      if (editorTileTreeW != areaW || editorTileTreeH != areaH) return;

      JsonValue tt = areaJson.get("tileTrees");
      if (tt == null) {
        tt = new JsonValue(JsonValue.ValueType.object);
        areaJson.addChild("tileTrees", tt);
      }

      tt.remove("w");
      tt.remove("h");
      tt.remove("presentB64");
      tt.remove("cutB64");
      tt.addChild("w", new JsonValue(editorTileTreeW));
      tt.addChild("h", new JsonValue(editorTileTreeH));

      String pb64 = java.util.Base64.getEncoder().encodeToString(editorTileTreePresentBits);
      String cb64 = (editorTileTreeCutBits != null) ? java.util.Base64.getEncoder().encodeToString(editorTileTreeCutBits) : "";
      tt.addChild("presentB64", new JsonValue(pb64));
      tt.addChild("cutB64", new JsonValue(cb64));

      areaDirty = true;
    } catch (Throwable ignored) {}
  }

  /**
   * Resolves a writable `assets/areas` directory across different launch CWDs.
   *
   * Desktop launchers sometimes set CWD to `.../assets`, so `assets/areas` would resolve to
   * `.../assets/assets/areas` (non-existent). This probes a few safe candidates and creates the folder.
   */
  private FileHandle resolveAreasWriteDir() {
    try {
      // 0) Prefer the same directory the game/editor READ from (internal "areas").
      // On Desktop in dev, this is typically a real folder and writable.
      try {
        FileHandle internalAreas = Gdx.files.internal(AREA_DIR_INTERNAL);
        if (internalAreas != null && internalAreas.exists() && internalAreas.isDirectory()) {
          // Probe writability: create+delete a tiny temp file.
          FileHandle probe = internalAreas.child(".write_probe.tmp");
          try {
            probe.writeString("x", false, "UTF-8");
            probe.delete();
            return internalAreas;
          } catch (Throwable ignored) {
            try { if (probe.exists()) probe.delete(); } catch (Throwable ignored2) {}
          }
        }
      } catch (Throwable ignored) {}

      String[] candidates = new String[] {
          AREA_DIR_ABS,
          "./" + AREA_DIR_ABS,
          "../" + AREA_DIR_ABS,
          "./areas",
          "../areas"
      };
      for (String p : candidates) {
        if (p == null || p.isBlank()) continue;
        FileHandle d = Gdx.files.absolute(p);
        if (d == null) continue;

        // Guard against the classic mistake: if CWD is already `assets`, then `assets/areas`
        // becomes `assets/assets/areas`. If we detect that pattern, hop one level up.
        try {
          String abs = d.file().getAbsolutePath().replace('/', '\\');
          if (abs.contains("\\assets\\assets\\areas")) {
            String fixed = abs.replace("\\assets\\assets\\areas", "\\assets\\areas");
            FileHandle df = Gdx.files.absolute(fixed);
            if (df != null) d = df;
          }
        } catch (Throwable ignored) {}

        if (!d.exists()) {
          try { d.mkdirs(); } catch (Throwable ignored) {}
        }
        if (d.exists() && d.isDirectory()) return d;
      }
    } catch (Throwable ignored) {}
    return null;
  }

  // ---------------------------------------------------------------------------
  // New-Area dialog helpers (base ground)
  // ---------------------------------------------------------------------------

  private int[] buildGroundIdList() {
    // Discover ground ids dynamically by probing TilesetRegions (atlas/config can add ids).
    int n = 0;
    if (tiles != null) {
      for (int i = 0; i < 256; i++) {
        try { tiles.ground((short) i); n = i + 1; }
        catch (Throwable t) { break; }
      }
    }
    if (n <= 0) n = 6;
    int[] ids = new int[n];
    for (int i = 0; i < n; i++) ids[i] = i;
    return ids;
  }

  private String[] buildGroundLabels(int[] ids) {
    if (ids == null) return new String[] { "0" };
    String[] out = new String[ids.length];
    for (int i = 0; i < ids.length; i++) {
      int id = ids[i];
      String name = switch (id) {
        case com.yourgame.survival.world.TileIds.GROUND_GRASS -> "GRASS";
        case com.yourgame.survival.world.TileIds.GROUND_DIRT -> "DIRT";
        case com.yourgame.survival.world.TileIds.GROUND_SAND -> "SAND";
        case com.yourgame.survival.world.TileIds.GROUND_ROCK -> "ROCK";
        case com.yourgame.survival.world.TileIds.GROUND_SNOW -> "SNOW";
        case com.yourgame.survival.world.TileIds.GROUND_LAVA -> "LAVA";
        default -> "GROUND";
      };
      out[i] = id + "  " + name;
    }
    return out;
  }

  private int selectedGroundIdFromBox(SelectBox<String> groundBox, int[] groundIds) {
    if (groundBox == null || groundIds == null || groundIds.length == 0) return 0;
    int idx = groundBox.getSelectedIndex();
    if (idx < 0 || idx >= groundIds.length) return 0;
    return groundIds[idx];
  }

  private void selectGroundInBox(SelectBox<String> groundBox, int[] groundIds, int id) {
    if (groundBox == null || groundIds == null) return;
    for (int i = 0; i < groundIds.length; i++) {
      if (groundIds[i] == id) {
        groundBox.setSelectedIndex(i);
        return;
      }
    }
    groundBox.setSelectedIndex(0);
  }

  /** For known types: base ground MUST follow <TYPE>_01. If missing, fallback to GRASS (0). */
  private int loadDefaultGroundIdForType01(String type) {
    try {
      if (type == null || type.isBlank()) return com.yourgame.survival.world.TileIds.GROUND_GRASS;
      String id = normalizeType(type) + "_01";
      FileHandle fh = Gdx.files.internal(AREA_DIR_INTERNAL + "/" + id + AREA_EXT);
      if (fh == null || !fh.exists()) return com.yourgame.survival.world.TileIds.GROUND_GRASS;
      String txt = fh.readString("UTF-8");
      JsonValue j = AreaWorldApplier.parse(txt);
      JsonValue layers = (j != null) ? j.get("layers") : null;
      JsonValue g = (layers != null) ? layers.get("ground") : null;
      return (g != null) ? g.getInt("defaultId", com.yourgame.survival.world.TileIds.GROUND_GRASS) : com.yourgame.survival.world.TileIds.GROUND_GRASS;
    } catch (Throwable t) {
      return com.yourgame.survival.world.TileIds.GROUND_GRASS;
    }
  }

  // ---------------------------------------------------------------------------
  // Markers + zones UI + JSON ops
  // ---------------------------------------------------------------------------

  private void rebuildMarkersZonesUi() {
    if (zoneButtons == null || markerButtons == null) return;
    zoneButtons.clearChildren();
    markerButtons.clearChildren();
    markerGroup = new ButtonGroup<>();
    zoneGroup = new ButtonGroup<>();
    markerGroup.setMinCheckCount(0);
    markerGroup.setMaxCheckCount(1);
    markerGroup.setUncheckLast(true);
    zoneGroup.setMinCheckCount(0);
    zoneGroup.setMaxCheckCount(1);
    zoneGroup.setUncheckLast(true);

    // If nothing loaded yet, just show static common entries.
    java.util.ArrayList<String> zoneKinds = new java.util.ArrayList<>();
    java.util.ArrayList<String> markerKinds = new java.util.ArrayList<>();

    // Markers
    markerKinds.add("PLAYER_SPAWN");
    // Nodes: allow drawing ANY known NODE_* type.
    // NOTE: NODE_TREE is allowed as an explicit, authored node (dev-only) and is movable.
    for (com.yourgame.survival.entity.EntityType et : com.yourgame.survival.entity.EntityType.values()) {
      if (et.name().startsWith("NODE_")) markerKinds.add("NODE:" + et.name());
    }

    // Extract from JSON if present.
    if (areaJson != null) {
      JsonValue markers = areaJson.get("markers");
      if (markers != null) {
        JsonValue poi = markers.get("poi");
        if (poi != null) {
          java.util.HashSet<String> kinds = new java.util.HashSet<>();
          for (JsonValue p = poi.child; p != null; p = p.next) {
            String k = p.getString("kind", "").trim();
            if (!k.isEmpty()) kinds.add("POI:" + k);
          }
          markerKinds.addAll(kinds);
        }
        JsonValue nodes = markers.get("nodes");
        if (nodes != null) {
          java.util.HashSet<String> kinds = new java.util.HashSet<>();
          for (JsonValue n = nodes.child; n != null; n = n.next) {
            String tn = n.getString("t", "").trim();
            if (!tn.isEmpty()) {
              // Only keep nodes that are real EntityTypes (otherwise they won't have sprites).
              try {
                com.yourgame.survival.entity.EntityType.valueOf(tn);
                kinds.add("NODE:" + tn);
              } catch (Throwable ignored) {}
            }
          }
          markerKinds.addAll(kinds);
        }
        JsonValue ez = markers.get("enemyZones");
        if (ez != null) {
          java.util.HashSet<String> kinds = new java.util.HashSet<>();
          for (JsonValue z = ez.child; z != null; z = z.next) {
            String k = z.getString("kind", "").trim();
            if (!k.isEmpty()) kinds.add(k);
          }
          zoneKinds.addAll(kinds);
        }
      }
    }

    // Provide a few default zone kinds if none exist yet.
    if (zoneKinds.isEmpty()) {
      zoneKinds.add("default");
      zoneKinds.add("wolves");
      zoneKinds.add("bandits");
    }

    java.util.Collections.sort(zoneKinds);
    java.util.Collections.sort(markerKinds);

    // Zones buttons
    int cols = 2;
    int c = 0;
    for (String k : zoneKinds) {
      final String kind = k;
      TextButton b = new TextButton(kind, VisUI.getSkin(), "toggle");
      zoneGroup.add(b);
      b.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
          if (!b.isChecked()) return;
          // Selecting a zone deactivates marker selection.
          zoneActive = true;
          markerActive = false;
          selectedZoneKind = kind;
          selectedMarkerKind = null;
          // cross-category reset (terrain/object selections must be cleared)
          selectedGroundId = -1;
          selectedWater = false;
          objectActive = false;
          selectedObjectType = null;
          if (btnObjectTool != null) btnObjectTool.setChecked(false);
          refreshMarkersZonesHighlight();
          refreshTerrainPaletteHighlight();
        }
      });
      zoneButtons.add(b).width(170);
      c++;
      if (c % cols == 0) zoneButtons.row();
    }

    // Markers buttons
    c = 0;
    for (String mk : markerKinds) {
      final String kind = mk;
      TextButton b = new TextButton(kind, VisUI.getSkin(), "toggle");
      markerGroup.add(b);
      b.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
          if (!b.isChecked()) return;
          markerActive = true;
          zoneActive = false;
          selectedMarkerKind = kind;
          selectedZoneKind = null;
          // Cross-category invalidation
          selectedGroundId = -1;
          selectedWater = false;
          objectActive = false;
          selectedObjectType = null;
          if (btnObjectTool != null) btnObjectTool.setChecked(false);
          refreshMarkersZonesHighlight();
          refreshTerrainPaletteHighlight();
        }
      });
      markerButtons.add(b).width(170);
      c++;
      if (c % cols == 0) markerButtons.row();
    }

    refreshMarkersZonesHighlight();

    // IMPORTANT: content is inside scroll panes; repack the window so it doesn't "lose" rows after rebuild.
    try { if (boxMarkersZones != null) boxMarkersZones.pack(); } catch (Throwable ignored) {}
  }

  private void refreshMarkersZonesHighlight() {
    // Green border requirement for selected type: we use button tint as step-1 (VisUI toggle backgrounds are skin-dependent).
    // (If needed, we can later clone styles with a border drawable like the terrain palette.)
    if (zoneButtons != null) {
      for (com.badlogic.gdx.scenes.scene2d.Actor a : zoneButtons.getChildren()) {
        if (a instanceof TextButton tb) {
          boolean on = zoneActive && selectedZoneKind != null && tb.getText().toString().equals(selectedZoneKind);
          tb.setColor(on ? 0.6f : 1f, on ? 1f : 1f, on ? 0.6f : 1f, 1f);
        }
      }
    }
    if (markerButtons != null) {
      for (com.badlogic.gdx.scenes.scene2d.Actor a : markerButtons.getChildren()) {
        if (a instanceof TextButton tb) {
          boolean on = markerActive && selectedMarkerKind != null && tb.getText().toString().equals(selectedMarkerKind);
          tb.setColor(on ? 0.6f : 1f, on ? 1f : 1f, on ? 0.6f : 1f, 1f);
        }
      }
    }
  }

  /** Builds a section "box" with head/body/bottom nesting. */
  private VisWindow buildBox(String title, Table bodyContent) {
    VisWindow w = new VisWindow(title);
    w.setResizable(false);
    w.setMovable(false);
    w.align(Align.topLeft);

    Table root = new Table(VisUI.getSkin());
    root.defaults().pad(4).left();

    // Head
    Table head = new Table(VisUI.getSkin());
    head.defaults().pad(2).left();
    head.add(new Label(title, VisUI.getSkin())).left();

    // Body
    Table body = new Table(VisUI.getSkin());
    body.defaults().pad(2).left();
    if (bodyContent != null) body.add(bodyContent).left();

    // Bottom
    Table bottom = new Table(VisUI.getSkin());
    bottom.defaults().pad(2).left();
    // DUMMY: bottom area reserved for per-box hints/status/buttons.
    bottom.add(new Label("", VisUI.getSkin())).left();

    root.add(head).left().row();
    root.add(body).left().row();
    root.add(bottom).left().row();

    w.add(root);
    w.pack();
    return w;
  }

  /**
   * Computes vertical stacking + clamps the box column to the screen.
   *
   * Rule: boxes are stacked top->bottom with a small gap.
   * If the stack would go off-screen, we clamp the column so the bottom stays visible.
   */
  private void layoutUiBoxes() {
    float margin = 10f;
    float gap = 8f;
    float x = margin;
    float top = Gdx.graphics.getHeight() - margin;

    VisWindow[] ws = new VisWindow[] {
        boxMapLoader, boxTerrainEdit, boxMarkersZones, boxEntitiesObjects, boxMouseTools
    };
    float totalH = 0f;
    for (VisWindow w : ws) {
      if (w == null) continue;
      totalH += w.getHeight();
    }
    totalH += gap * (ws.length - 1);

    float y0 = top;
    // Clamp: if total stack taller than screen, pin to top and accept overflow for now.
    // (Later we can add per-box scrolling; this is just the dummy scaffold stage.)
    if (totalH <= Gdx.graphics.getHeight() - margin * 2f) {
      // Also ensure bottom not below margin.
      float bottom = top - totalH;
      if (bottom < margin) {
        y0 += (margin - bottom);
      }
    }

    float y = y0;
    for (VisWindow w : ws) {
      if (w == null) continue;
      w.setPosition(x, y - w.getHeight());
      y -= w.getHeight() + gap;
    }
  }

  // ---------------------------------------------------------------------------
  // Box body builders
  // ---------------------------------------------------------------------------

  /** Map loader body: this is the existing working controls, placed into the new box system. */
  private Table buildMapLoaderBody() {
    Table root = new Table(VisUI.getSkin());
    root.defaults().pad(2).left();

    typeBox = new SelectBox<>(VisUI.getSkin());
    areaBox = new SelectBox<>(VisUI.getSkin());
    btnLoad = new TextButton("Load", VisUI.getSkin());
    btnNew = new TextButton("New Area", VisUI.getSkin());
    btnSave = new TextButton("Save", VisUI.getSkin());
    btnPath = new TextButton("Paths", VisUI.getSkin());

    lblStatus = new Label("", VisUI.getSkin());
    lblStatus.setWrap(true);
    lblStatus.setAlignment(Align.topLeft);

    root.add(new Label("Type", VisUI.getSkin())).left();
    root.add(typeBox).width(220).row();

    root.add(new Label("Area", VisUI.getSkin())).left();
    root.add(areaBox).width(220).row();

    Table btnRow = new Table(VisUI.getSkin());
    btnRow.defaults().pad(2);
    btnRow.add(btnLoad).width(104);
    btnRow.add(btnNew).width(104);
    btnRow.row();
    btnRow.add(btnSave).width(216).colspan(2);
    btnRow.row();
    btnRow.add(btnPath).width(216).colspan(2);
    root.add(btnRow).colspan(2).left().row();

    // Wider so long status lines are less likely to clip; wrap handles the rest.
    root.add(lblStatus).colspan(2).width(520).left().row();

    return root;
  }

  private Table buildTerrainEditBodyDummy() {
    Table t = new Table(VisUI.getSkin());
    t.defaults().pad(2).left();

    // Layer target
    terrainLayerBox = new SelectBox<>(VisUI.getSkin());
    terrainLayerBox.setItems("GROUND", "ROAD", "WATER");
    terrainLayerBox.setSelected("GROUND");
    terrainLayerBox.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        String s = terrainLayerBox.getSelected();
        try { layerTarget = LayerTarget.valueOf(s); } catch (Throwable ignored) {}
        // Switching layer invalidates the last placement selection across categories.
        clearAllToolSelections();
        refreshTerrainPaletteHighlight();
      }
    });

    Table row0 = new Table(VisUI.getSkin());
    row0.defaults().pad(2).left();
    row0.add(new Label("Layer", VisUI.getSkin()));
    row0.add(terrainLayerBox).width(150);
    row0.add(new Label("(Action is in Mouse tools)", VisUI.getSkin())).padLeft(10);
    t.add(row0).left().row();

    // Brush size (square). Requirement: configurable brush size in Terrain.
    brushSizeSlider = new Slider(1, 9, 1, false, VisUI.getSkin());
    brushSizeSlider.setValue(1);
    brushSizeLabel = new Label("Brush: 1", VisUI.getSkin());
    brushSizeSlider.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        brushSize = Math.max(1, Math.min(9, Math.round(brushSizeSlider.getValue())));
        brushSizeLabel.setText("Brush: " + brushSize);
      }
    });
    Table rowBrush = new Table(VisUI.getSkin());
    rowBrush.defaults().pad(2).left();
    rowBrush.add(brushSizeLabel).width(80);
    rowBrush.add(brushSizeSlider).width(280);
    t.add(rowBrush).left().row();

    terrainPalette = new Table(VisUI.getSkin());
    terrainPalette.defaults().pad(2);
    t.add(terrainPalette).left().row();

    // Build palette now (requires tiles already constructed).
    rebuildTerrainPalette();

    return t;
  }

  private Table buildMarkersZonesBodyDummy() {
    // NOTE: This is the real Markers/Zones tool scaffold.
    // It is intentionally simple: buttons are rebuilt from the currently loaded area JSON.
    // The behavior is wired (place/define/delete/move/adjust) and writes into areaJson.
    Table t = new Table(VisUI.getSkin());
    t.defaults().pad(2).left();

    markersZonesRoot = t;

    t.add(new Label("(Mode is universal in Mouse tools)", VisUI.getSkin())).left().row();

    t.add(new Label("Zones", VisUI.getSkin())).left().row();
    zoneButtons = new Table(VisUI.getSkin());
    zoneButtons.defaults().pad(2).left();
    VisScrollPane zoneScroll = new VisScrollPane(zoneButtons);
    zoneScroll.setFadeScrollBars(false);
    zoneScroll.setScrollingDisabled(true, false);
    zoneScroll.setOverscroll(false, false);
    t.add(zoneScroll).width(360).height(110).left().row();

    t.add(new Label("Markers", VisUI.getSkin())).left().row();
    markerButtons = new Table(VisUI.getSkin());
    markerButtons.defaults().pad(2).left();
    VisScrollPane markerScroll = new VisScrollPane(markerButtons);
    markerScroll.setFadeScrollBars(false);
    markerScroll.setScrollingDisabled(true, false);
    markerScroll.setOverscroll(false, false);
    t.add(markerScroll).width(360).height(180).left().row();

    rebuildMarkersZonesUi();
    return t;
  }

  private Table buildEntitiesObjectsBodyDummy() {
    // Step 1: Objects list + selected object properties.
    // Objects = markers.objects[] entries authored into .area.json.
    Table t = new Table(VisUI.getSkin());
    t.defaults().pad(2).left();

    objectsUiRoot = t;

    // Header row
    Table head = new Table(VisUI.getSkin());
    head.defaults().pad(2).left();
    btnObjectsRescan = new TextButton("Rescan", VisUI.getSkin());
    btnObjectJump = new TextButton("Jump", VisUI.getSkin());

    // Object placement tool
    objectTypeBox = new SelectBox<>(VisUI.getSkin());
    objectTypeBox.setItems(buildAllEntityTypeNamesForObjects());
    objectTypeBox.setSelectedIndex(0);
    btnObjectTool = new TextButton("Object Tool", VisUI.getSkin(), "toggle");

    lblObjectsCount = new Label("Objects: 0", VisUI.getSkin());
    head.add(lblObjectsCount).left().width(120);
    head.add(btnObjectsRescan).width(120).left();
    head.add(btnObjectJump).width(120).left();
    t.add(head).left().row();

    Table toolRow = new Table(VisUI.getSkin());
    toolRow.defaults().pad(2).left();
    toolRow.add(new Label("Place type", VisUI.getSkin())).width(70);
    toolRow.add(objectTypeBox).width(210);
    toolRow.add(btnObjectTool).width(120);
    t.add(toolRow).left().row();

    // List
    objectsList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(VisUI.getSkin());
    objectsList.setItems(new String[0]);
    objectsScroll = new VisScrollPane(objectsList);
    objectsScroll.setFadeScrollBars(false);
    objectsScroll.setScrollingDisabled(true, false);
    objectsScroll.setOverscroll(false, false);
    t.add(objectsScroll).width(360).height(140).left().row();

    // Selected details (read-only for now)
    tfObjType = new TextField("", VisUI.getSkin());
    tfObjX = new TextField("", VisUI.getSkin());
    tfObjY = new TextField("", VisUI.getSkin());
    tfObjLayerBias = new TextField("", VisUI.getSkin());
    tfObjType.setDisabled(true);
    tfObjX.setDisabled(true);
    tfObjY.setDisabled(true);
    // layerBias is editable via slider; we keep the text field read-only as a numeric display.
    tfObjLayerBias.setDisabled(true);

    objLayerBiasSlider = new Slider(-200f, 200f, 1f, false, VisUI.getSkin());
    objLayerBiasSlider.setValue(0f);

    Table props = new Table(VisUI.getSkin());
    props.defaults().pad(2).left();
    props.add(new Label("type", VisUI.getSkin())).width(70);
    props.add(tfObjType).width(260).row();
    props.add(new Label("x", VisUI.getSkin())).width(70);
    props.add(tfObjX).width(260).row();
    props.add(new Label("y", VisUI.getSkin())).width(70);
    props.add(tfObjY).width(260).row();
    props.add(new Label("layerBias", VisUI.getSkin())).width(70);
    props.add(tfObjLayerBias).width(260).row();

    Table lb = new Table(VisUI.getSkin());
    lb.defaults().pad(2).left();
    lb.add(new Label("layerBias", VisUI.getSkin())).width(70);
    lb.add(objLayerBiasSlider).width(260);
    t.add(lb).left().row();

    t.add(props).left().row();

    // Wiring
    btnObjectsRescan.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        rebuildObjectsUi();
      }
    });

    btnObjectJump.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        jumpCameraToSelectedObject();
      }
    });

    btnObjectTool.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        if (!btnObjectTool.isChecked()) {
          // Turning it off just clears object tool selection.
          objectActive = false;
          selectedObjectType = null;
          return;
        }

        // Turning it on must invalidate other cross-category selections.
        clearAllToolSelections();
        objectActive = true;
        selectedObjectType = (objectTypeBox != null) ? objectTypeBox.getSelected() : null;
        // Keep the toggle checked (clearAllToolSelections resets objectActive).
        btnObjectTool.setChecked(true);
      }
    });

    objectTypeBox.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        if (!objectActive) return;
        selectedObjectType = objectTypeBox.getSelected();
      }
    });

    objectsList.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        selectObjectFromList(objectsList.getSelectedIndex());
      }
    });

    objLayerBiasSlider.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        // Only apply when we actually have a selected object.
        if (selectedObjectJson == null) return;
        float v = objLayerBiasSlider.getValue();
        setSelectedObjectLayerBias(v);
      }
    });

    rebuildObjectsUi();
    return t;
  }

  private String[] buildAllEntityTypeNamesForObjects() {
    // Minimal policy: expose all EntityTypes except NODE_TREE (tile-tree streaming) to avoid guessing.
    java.util.ArrayList<String> out = new java.util.ArrayList<>();
    for (com.yourgame.survival.entity.EntityType t : com.yourgame.survival.entity.EntityType.values()) {
      if (t == com.yourgame.survival.entity.EntityType.NODE_TREE) continue;
      out.add(t.name());
    }
    java.util.Collections.sort(out);
    if (out.isEmpty()) out.add("LANDMARK_CASTLE");
    return out.toArray(new String[0]);
  }

  // ---------------------------------------------------------------------------
  // Objects UI (markers.objects[])
  // ---------------------------------------------------------------------------

  private void rebuildObjectsUi() {
    objectsJson.clear();
    selectedObjectIndex = -1;
    selectedObjectJson = null;

    if (objectsList == null || lblObjectsCount == null) return;

    java.util.ArrayList<String> labels = new java.util.ArrayList<>();
    JsonValue markers = (areaJson != null) ? areaJson.get("markers") : null;
    JsonValue objs = (markers != null) ? markers.get("objects") : null;
    if (objs != null) {
      int i = 0;
      for (JsonValue o = objs.child; o != null; o = o.next) {
        objectsJson.add(o);
        String type = o.getString("type", "").trim();
        int x = o.getInt("x", 0);
        int y = o.getInt("y", 0);
        float lb = o.getFloat("layerBias", 0f);
        // Stable, readable list label.
        labels.add("#" + i + "  " + type + " @(" + x + "," + y + ")  lb=" + lb);
        i++;
      }
    }

    lblObjectsCount.setText("Objects: " + objectsJson.size());
    objectsList.setItems(labels.toArray(new String[0]));
    applySelectedObjectToFields(null);
  }

  private void selectObjectFromList(int idx) {
    if (idx < 0 || idx >= objectsJson.size()) {
      selectedObjectIndex = -1;
      selectedObjectJson = null;
      applySelectedObjectToFields(null);
      return;
    }
    selectedObjectIndex = idx;
    selectedObjectJson = objectsJson.get(idx);
    applySelectedObjectToFields(selectedObjectJson);
  }

  private void applySelectedObjectToFields(JsonValue o) {
    if (tfObjType == null) return;
    if (o == null) {
      tfObjType.setText("");
      tfObjX.setText("");
      tfObjY.setText("");
      tfObjLayerBias.setText("");
      if (objLayerBiasSlider != null) objLayerBiasSlider.setValue(0f);
      return;
    }

    tfObjType.setText(o.getString("type", ""));
    tfObjX.setText(String.valueOf(o.getInt("x", 0)));
    tfObjY.setText(String.valueOf(o.getInt("y", 0)));
    float lb = o.getFloat("layerBias", 0f);
    tfObjLayerBias.setText(String.valueOf(lb));
    if (objLayerBiasSlider != null) {
      // Prevent accidental ChangeListener feedback loops: setting value is fine here.
      objLayerBiasSlider.setValue(lb);
    }
  }

  private void setSelectedObjectLayerBias(float layerBias) {
    if (selectedObjectJson == null) return;
    int tx = selectedObjectJson.getInt("x", -1);
    int ty = selectedObjectJson.getInt("y", -1);
    if (tx < 0 || ty < 0) return;

    // Write to JSON (overwrite key).
    try {
      selectedObjectJson.remove("layerBias");
      selectedObjectJson.addChild("layerBias", new JsonValue(layerBias));
    } catch (Throwable ignored) {
      // If remove/add fails for some JsonValue reason, at least do not crash the editor.
    }

    // Update UI display field.
    if (tfObjLayerBias != null) tfObjLayerBias.setText(String.valueOf(layerBias));

    // Update preview entity (best-effort): find entity by type and tile coordinate.
    String typeName = selectedObjectJson.getString("type", "");
    com.yourgame.survival.entity.EntityType et = null;
    try { et = com.yourgame.survival.entity.EntityType.valueOf(typeName.trim()); }
    catch (Throwable ignored) { et = null; }
    if (entities != null && et != null) {
      int e = findPreviewEntityAt(et, tx, ty);
      if (e >= 0) entities.layerBias[e] = layerBias;
    }

    areaDirty = true;
    // Refresh list label (shows lb=...)
    rebuildObjectsUi();
    // Keep the same selection index if possible.
    if (objectsList != null && selectedObjectIndex >= 0 && selectedObjectIndex < objectsJson.size()) {
      try { objectsList.setSelectedIndex(selectedObjectIndex); } catch (Throwable ignored) {}
    }
  }

  private void jumpCameraToSelectedObject() {
    if (cam == null) return;
    if (selectedObjectJson == null) return;
    int tx = selectedObjectJson.getInt("x", -1);
    int ty = selectedObjectJson.getInt("y", -1);
    if (tx < 0 || ty < 0) return;

    float wx = (tx + 0.5f) * World.TILE_WORLD;
    float wy = (ty + 0.5f) * World.TILE_WORLD;
    cam.position.set(wx, wy, 0f);
    cam.update();
  }

  private Table buildMouseToolsBodyDummy() {
    Table t = new Table(VisUI.getSkin());
    t.defaults().pad(2).left();
    // Universal tool mode selector (applies to ALL editor sections).
    SelectBox<String> modeBox = new SelectBox<>(VisUI.getSkin());
    modeBox.setItems("REPLACE", "SET", "DELETE", "MOVE", "PLACE", "DEFINE", "ADJUST");
    modeBox.setSelected("REPLACE");
    modeBox.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        try { toolMode = ToolMode.valueOf(modeBox.getSelected()); }
        catch (Throwable ignored) { toolMode = ToolMode.REPLACE; }
        // Mode switching must NOT leave any previous tool selected.
        clearAllToolSelections();
      }
    });

    Table row0 = new Table(VisUI.getSkin());
    row0.defaults().pad(2).left();
    row0.add(new Label("Mode", VisUI.getSkin()));
    row0.add(modeBox).width(260);
    t.add(row0).left().row();

    dummyMouse = new Label("Cursor mapping: viewport.unproject (always under cursor)", VisUI.getSkin());
    t.add(dummyMouse).width(360).left().row();
    return t;
  }

  /**
   * Clears ALL placement selections across categories.
   *
   * Requirement: when switching between objects/terrain/markers (cross categories),
   * the previous placement selection must become invalid so we never place 2 things at once.
   */
  private void clearAllToolSelections() {
    selectedGroundId = -1;
    selectedWater = false;

    markerActive = false;
    zoneActive = false;
    selectedMarkerKind = null;
    selectedZoneKind = null;

    objectActive = false;
    selectedObjectType = null;

    // (Future: entities/objects selection)
    refreshTerrainPaletteHighlight();
    refreshMarkersZonesHighlight();
  }

  private void rebuildTerrainPalette() {
    if (terrainPalette == null) return;
    terrainPalette.clearChildren();
    groundIdToBorder.clear();
    waterBorder = null;

    if (terrainSelectedBorder == null) {
      // Build a transparent texture with a GREEN border (no fill).
      // This is the requested "green frame" for the currently selected tool.
      Pixmap pm = new Pixmap(28, 28, Pixmap.Format.RGBA8888);
      pm.setColor(0, 0, 0, 0);
      pm.fill();
      pm.setColor(0f, 1f, 0f, 1f);
      // 2px border
      for (int i = 0; i < 2; i++) {
        pm.drawRectangle(i, i, 28 - i * 2, 28 - i * 2);
      }
      Texture tex = new Texture(pm);
      pm.dispose();
      terrainSelectedBorder = new TextureRegionDrawable(new TextureRegion(tex));
    }

    // Ground IDs: dynamic (atlas/config can add more). We discover count by probing tiles.ground().
    int n = 0;
    if (tiles != null) {
      // TilesetRegions throws if id unmapped, so probe until failure.
      for (int i = 0; i < 256; i++) {
        try {
          tiles.ground((short) i);
          n = i + 1;
        } catch (Throwable t) {
          break;
        }
      }
    }

    // Palette grid
    int cols = 8;
    int c = 0;
    for (int id = 0; id < n; id++) {
      final int fid = id;
      Image tileImg = new Image(new TextureRegionDrawable(tiles.ground((short) fid)));
      tileImg.setScaling(com.badlogic.gdx.utils.Scaling.stretch);

      Image border = new Image(terrainSelectedBorder);
      border.setVisible(false);
      groundIdToBorder.put(fid, border);

      Stack cell = new Stack();
      cell.add(tileImg);
      cell.add(border);

      ImageButton b = new ImageButton(VisUI.getSkin());
      b.add(cell).grow();
      b.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
          // Selecting a terrain tile invalidates other placement selections.
          clearAllToolSelections();
          selectedGroundId = fid;
          selectedWater = false;
          refreshTerrainPaletteHighlight();
        }
      });
      terrainPalette.add(b).size(28, 28);
      c++;
      if (c % cols == 0) terrainPalette.row();
    }

    // Water tool button
    if (tiles != null) {
      Image waterImg = new Image(new TextureRegionDrawable(tiles.waterFill()));
      waterImg.setScaling(com.badlogic.gdx.utils.Scaling.stretch);

      waterBorder = new Image(terrainSelectedBorder);
      waterBorder.setVisible(false);

      Stack wcell = new Stack();
      wcell.add(waterImg);
      wcell.add(waterBorder);

      final ImageButton wb = new ImageButton(VisUI.getSkin());
      wb.add(wcell).grow();
      wb.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
          clearAllToolSelections();
          selectedWater = true;
          selectedGroundId = -1;
          refreshTerrainPaletteHighlight();
        }
      });
      terrainPalette.row();
      terrainPalette.add(new Label("Water", VisUI.getSkin())).left().colspan(cols).row();
      terrainPalette.add(wb).size(28, 28).left();
    }

    // Editor-only: TileTree icon entry (DEV reference only).
    // Requirement: show TileTrees next to ground tiles as a single standalone so it's obvious which tree system it is.
    // NOTE: This does NOT paint anything. TileTrees are not authored via JSON right now.
    if (entityRegions != null) {
      try {
        var t = com.yourgame.survival.entity.EntityType.NODE_TREE;
      var reg = entityRegions.forEntity(t, 0f, -1, 0f, 0f, (byte) 2, 0f);
        terrainPalette.row();
        terrainPalette.add(new Label("TileTree (preview)", VisUI.getSkin())).left().colspan(cols).row();
        ImageButton b = new ImageButton(new TextureRegionDrawable(reg));
        // No listener: reference only.
        terrainPalette.add(b).size(28, 28).left();
      } catch (Throwable ignored) {}
    }

    // NOTE: Lava is a GROUND tile id (not a separate mask like water). It appears in the grid if mapped.
    // DUMMY-NOTE: If we later want a labeled "Lava" quick-pick, we can add it above without changing the tile system.

    refreshTerrainPaletteHighlight();
  }

  /**
   * Very cheap visual highlight: tint selected tool/button.
   *
   * (Green border would require a custom drawable; tint is the minimal version.)
   */
  private void refreshTerrainPaletteHighlight() {
    if (terrainPalette == null) return;

    for (Image border : groundIdToBorder.values()) {
      if (border != null) border.setVisible(false);
    }
    if (waterBorder != null) waterBorder.setVisible(false);

    if (selectedGroundId >= 0) {
      Image b = groundIdToBorder.get(selectedGroundId);
      if (b != null) b.setVisible(true);
    }
    if (selectedWater && waterBorder != null) {
      waterBorder.setVisible(true);
    }
  }

  /** Build HOME tree presence bits for editor overlay (deterministic, excludes non-grass). */
  private void buildEditorTreePreviewIfNeeded() {
    editorTreePresentBits = null;
    editorTreeW = editorTreeH = 0;
    if (previewWorld == null) return;
    if (areaW <= 0 || areaH <= 0) return;
    if (!"HOME_01".equalsIgnoreCase(currentTemplateId)) return;

    int w = areaW;
    int h = areaH;
    int n = w * h;
    int bytes = (n + 7) >>> 3;
    byte[] out = new byte[bytes];

    final int cx0 = 192;
    final int cy0 = 168;
    final int clearR = 30;
    final int clearR2 = clearR * clearR;
    int target = (int) Math.round(n * 0.008);

    java.util.ArrayList<Long> keys = new java.util.ArrayList<>(Math.max(128, target * 3));
    long seed = 0xC0FFEE42L; // stable editor preview seed
    for (int ty = 0; ty < h; ty++) {
      for (int tx = 0; tx < w; tx++) {
        int dx = tx - cx0;
        int dy = ty - cy0;
        if (dx * dx + dy * dy <= clearR2) continue;

        int ccx = tx / World.CHUNK_SIZE;
        int ccy = ty / World.CHUNK_SIZE;
        var c = previewWorld.chunk(ccx, ccy);
        int lx = tx - ccx * World.CHUNK_SIZE;
        int ly = ty - ccy * World.CHUNK_SIZE;
        int idx = lx + ly * World.CHUNK_SIZE;

        if (c.layers.waterMask[idx] != 0) continue;
        if (c.layers.roadMask[idx] != 0) continue;
        int g = c.layers.groundId[idx] & 0xFF;
        if (g != com.yourgame.survival.world.TileIds.GROUND_GRASS) continue;

        int bit = tx + ty * w;
        int h24 = (int) (hash01(seed ^ 0xABCD1234EF01L, tx, ty) * 16777216.0f) & 0xFFFFFF;
        long k = (((long) h24) << 32) | (bit & 0xFFFFFFFFL);
        keys.add(k);
      }
    }

    keys.sort(java.util.Comparator.naturalOrder());
    int take = Math.min(target, keys.size());
    for (int i = 0; i < take; i++) {
      int bit = (int) (keys.get(i) & 0xFFFFFFFFL);
      int bi = bit >>> 3;
      int m = 1 << (bit & 7);
      if (bi >= 0 && bi < out.length) out[bi] = (byte) (out[bi] | m);
    }

    editorTreePresentBits = out;
    editorTreeW = w;
    editorTreeH = h;
  }

  /** Small deterministic hash -> [0,1). (Local copy for editor-only preview masks.) */
  private static float hash01(long seed, int x, int y) {
    long h = seed;
    h ^= (long) x * 0x9E3779B97F4A7C15L;
    h ^= (long) y * 0xC2B2AE3D27D4EB4FL;
    h ^= (h >>> 33);
    h *= 0xFF51AFD7ED558CCDL;
    h ^= (h >>> 33);
    h *= 0xC4CEB9FE1A85EC53L;
    h ^= (h >>> 33);
    // Use top 24 bits for stable float fraction.
    int v = (int) ((h >>> 40) & 0xFFFFFF);
    return (v & 0xFFFFFF) / 16777216.0f;
  }

  private void setStatus(String msg) {
    if (lblStatus == null) return;
    lblStatus.setText(msg == null ? "" : msg);
  }

  /** Shortens long absolute paths so they remain readable inside the UI label width. */
  private static String shortPath(String p, int maxChars) {
    if (p == null) return "";
    String s = p.replace('/', '\\');
    if (maxChars <= 0) return s;
    if (s.length() <= maxChars) return s;
    return "..." + s.substring(s.length() - maxChars);
  }

  private static String normalizeType(String s) {
    if (s == null) return "";
    String t = s.trim().toUpperCase();
    // allow only [A-Z0-9_]
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < t.length(); i++) {
      char c = t.charAt(i);
      if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_') sb.append(c);
    }
    return sb.toString();
  }

  private static boolean isValidTemplateId(String id) {
    if (id == null) return false;
    int k = id.lastIndexOf('_');
    if (k <= 0 || k >= id.length() - 1) return false;
    String suffix = id.substring(k + 1);
    if (suffix.length() != 2) return false;
    if (!Character.isDigit(suffix.charAt(0)) || !Character.isDigit(suffix.charAt(1))) return false;
    int n = Integer.parseInt(suffix);
    return n >= 1 && n <= 99;
  }

  private static String typeOf(String templateId) {
    if (templateId == null) return "";
    int k = templateId.indexOf('_');
    if (k <= 0) return "";
    return templateId.substring(0, k);
  }

  private void rescanAreas() {
    try {
      FileHandle dir = Gdx.files.internal(AREA_DIR_INTERNAL);
      if (dir == null || !dir.exists()) {
        allTemplateIds = new String[0];
        allTypes = new String[] { "ALL" };
        refreshTypeDropdown();
        refreshAreaDropdown();
        setStatus("Missing dir: " + AREA_DIR_INTERNAL);
        return;
      }
      FileHandle[] files = dir.list();
      java.util.ArrayList<String> ids = new java.util.ArrayList<>();
      java.util.HashSet<String> types = new java.util.HashSet<>();
      for (FileHandle f : files) {
        if (f == null) continue;
        String n = f.name();
        if (n == null || !n.endsWith(AREA_EXT)) continue;
        String id = n.substring(0, n.length() - AREA_EXT.length());
        if (!isValidTemplateId(id)) continue;
        ids.add(id);
        types.add(typeOf(id));
      }
      java.util.Collections.sort(ids);
      java.util.ArrayList<String> tlist = new java.util.ArrayList<>();
      tlist.add("ALL");
      java.util.ArrayList<String> ts = new java.util.ArrayList<>(types);
      java.util.Collections.sort(ts);
      tlist.addAll(ts);
      allTemplateIds = ids.toArray(new String[0]);
      allTypes = tlist.toArray(new String[0]);

      refreshTypeDropdown();
      refreshAreaDropdown();
      setStatus("Found " + allTemplateIds.length + " areas");
    } catch (Throwable t) {
      allTemplateIds = new String[0];
      allTypes = new String[] { "ALL" };
      refreshTypeDropdown();
      refreshAreaDropdown();
      setStatus("Scan failed");
    }
  }


  private void refreshTypeDropdown() {
    if (typeBox == null) return;
    String prev = null;
    try { prev = typeBox.getSelected(); } catch (Throwable ignored) {}
    typeBox.setItems(allTypes);
    if (prev != null) {
      for (String s : allTypes) {
        if (prev.equals(s)) { typeBox.setSelected(prev); break; }
      }
    }
  }

  private void refreshAreaDropdown() {
    if (areaBox == null) return;
    String wantType = (typeBox != null) ? typeBox.getSelected() : "ALL";
    java.util.ArrayList<String> ids = new java.util.ArrayList<>();
    for (String id : allTemplateIds) {
      if (id == null) continue;
      if (wantType == null || "ALL".equals(wantType) || wantType.equals(typeOf(id))) {
        ids.add(id);
      }
    }
    if (ids.isEmpty()) {
      areaBox.setItems(new String[0]);
      return;
    }
    String prev = null;
    try { prev = areaBox.getSelected(); } catch (Throwable ignored) {}
    areaBox.setItems(ids.toArray(new String[0]));
    if (prev != null) {
      for (String s : ids) {
        if (prev.equals(s)) { areaBox.setSelected(prev); break; }
      }
    }
  }

  private void selectTemplateIfExists(String templateId) {
    if (templateId == null) return;
    String t = typeOf(templateId);
    if (typeBox != null) {
      for (String s : allTypes) {
        if (s.equals(t)) { typeBox.setSelected(s); break; }
      }
    }
    refreshAreaDropdown();
    if (areaBox != null) {
      try { areaBox.setSelected(templateId); } catch (Throwable ignored) {}
    }
  }

  private void loadSelectedAreaFromUi() {
    String id = null;
    try { id = (areaBox != null) ? areaBox.getSelected() : null; } catch (Throwable ignored) {}
    if (id == null || id.trim().isEmpty()) {
      setStatus("No area selected");
      return;
    }
    currentTemplateId = id.trim();
    reloadArea();
  }

  private void reloadArea() {
    try {
      FileHandle fh = Gdx.files.internal(AREA_DIR_INTERNAL + "/" + currentTemplateId + AREA_EXT);
      if (fh == null || !fh.exists()) {
        areaJson = null;
        areaW = 0;
        areaH = 0;
        return;
      }
      String txt = fh.readString("UTF-8");
      areaJson = AreaWorldApplier.parse(txt);

      // Cache dimensions ASAP (used by editor-only previews like HOME tile-tree overlay).
      JsonValue size = (areaJson != null) ? areaJson.get("size") : null;
      areaW = (size != null) ? size.getInt("w", 0) : 0;
      areaH = (size != null) ? size.getInt("h", 0) : 0;

      // Hard reset of editor interaction state on area switch.
      // Requirement: no state mixing (mode selections, drags, palette picks) between areas.
      areaDirty = false;
      lmbPainting = false;
      moveDragging = false;
      moveDragTarget = null;
      moveDragKind = null;
      movePreviewEntityType = null;
      movePreviewEntityIndex = -1;
      zoneDragging = false;
      zoneDragTarget = null;
      clearAllToolSelections();

      // Apply tiles + entities using the SAME loader logic as the game.
      JsonAreaWorldLoader.applyForEditor(previewWorld, entities, currentTemplateId, areaJson);

      // Tree preview (HOME only for now): show tree markers in editor.
      buildEditorTreePreviewIfNeeded();

      // TileTrees (HOME + FOREST_01): build sprite-preview bits so editor matches ingame visuals.
      buildEditorTileTreeBitsIfNeeded();

      // Rebuild marker/zone UI from newly loaded JSON.
      rebuildMarkersZonesUi();

      // Rebuild objects UI from newly loaded JSON.
      rebuildObjectsUi();

      // Auto-fit camera to full area (prevents "despawn at edge" look).
      fitCameraToArea();

      String p;
      try { p = fh.file().getAbsolutePath(); } catch (Throwable t) { p = fh.path(); }
      lastLoadedAbsPath = p;
      setStatus("Loaded: " + currentTemplateId + "\n" + "from: " + shortPath(p, 84));
    } catch (Throwable t) {
      areaJson = null;
      areaW = 0;
      areaH = 0;
      setStatus("Load failed");
    }
  }

  /**
   * Editor-only TileTrees: generate present/cut bitmasks for templates that use tile-tree streaming ingame.
   *
   * - HOME_01: sparse deterministic trees on grass-only.
   * - FOREST_01: deterministic dense trees (same concept as runtime; algorithm here is a simplified mirror).
   */
  private void buildEditorTileTreeBitsIfNeeded() {
    editorTileTreePresentBits = null;
    editorTileTreeCutBits = null;
    editorTileTreeW = editorTileTreeH = 0;
    if (previewWorld == null) return;
    if (areaW <= 0 || areaH <= 0) return;

    boolean wants = "HOME_01".equalsIgnoreCase(currentTemplateId) || "FOREST_01".equalsIgnoreCase(currentTemplateId);
    if (!wants) return;

    int w = areaW;
    int h = areaH;
    int n = w * h;
    int bytes = (n + 7) >>> 3;
    byte[] present = new byte[bytes];
    byte[] cut = new byte[bytes];

    if ("HOME_01".equalsIgnoreCase(currentTemplateId)) {
      // Reuse the same policy as the old HOME preview: grass-only, no road/water, central clearing open.
      final int cx0 = 192;
      final int cy0 = 168;
      final int clearR = 30;
      final int clearR2 = clearR * clearR;
      int target = (int) Math.round(n * 0.008);

      java.util.ArrayList<Long> keys = new java.util.ArrayList<>(Math.max(128, target * 3));
      long seed = 0xC0FFEE42L; // stable editor preview seed
      for (int ty = 0; ty < h; ty++) {
        for (int tx = 0; tx < w; tx++) {
          int dx = tx - cx0;
          int dy = ty - cy0;
          if (dx * dx + dy * dy <= clearR2) continue;

          int ccx = tx / World.CHUNK_SIZE;
          int ccy = ty / World.CHUNK_SIZE;
          var c = previewWorld.chunk(ccx, ccy);
          int lx = tx - ccx * World.CHUNK_SIZE;
          int ly = ty - ccy * World.CHUNK_SIZE;
          int idx = lx + ly * World.CHUNK_SIZE;

          if (c.layers.waterMask[idx] != 0) continue;
          if (c.layers.roadMask[idx] != 0) continue;
          int g = c.layers.groundId[idx] & 0xFF;
          if (g != com.yourgame.survival.world.TileIds.GROUND_GRASS) continue;

          int bit = tx + ty * w;
          int h24 = (int) (hash01(seed ^ 0xABCD1234EF01L, tx, ty) * 16777216.0f) & 0xFFFFFF;
          long k = (((long) h24) << 32) | (bit & 0xFFFFFFFFL);
          keys.add(k);
        }
      }
      keys.sort(java.util.Comparator.naturalOrder());
      int take = Math.min(target, keys.size());
      for (int i = 0; i < take; i++) {
        int bit = (int) (keys.get(i) & 0xFFFFFFFFL);
        int bi = bit >>> 3;
        int m = 1 << (bit & 7);
        if (bi >= 0 && bi < present.length) present[bi] = (byte) (present[bi] | m);
      }
    } else {
      // FOREST_01: keep a deterministic moderate density on non-road/non-water tiles.
      // (Editor mirror; runtime has a more elaborate zoned distribution.)
      int target = (int) Math.round(n * 0.015);
      java.util.ArrayList<Long> keys = new java.util.ArrayList<>(Math.max(256, target * 3));
      long seed = 0xFEEDBEEFL;
      for (int ty = 0; ty < h; ty++) {
        for (int tx = 0; tx < w; tx++) {
          int ccx = tx / World.CHUNK_SIZE;
          int ccy = ty / World.CHUNK_SIZE;
          var c = previewWorld.chunk(ccx, ccy);
          int lx = tx - ccx * World.CHUNK_SIZE;
          int ly = ty - ccy * World.CHUNK_SIZE;
          int idx = lx + ly * World.CHUNK_SIZE;
          if (c.layers.waterMask[idx] != 0) continue;
          if (c.layers.roadMask[idx] != 0) continue;

          int bit = tx + ty * w;
          int h24 = (int) (hash01(seed ^ 0xC0FFEE1234L, tx, ty) * 16777216.0f) & 0xFFFFFF;
          long k = (((long) h24) << 32) | (bit & 0xFFFFFFFFL);
          keys.add(k);
        }
      }
      keys.sort(java.util.Comparator.naturalOrder());
      int take = Math.min(target, keys.size());
      for (int i = 0; i < take; i++) {
        int bit = (int) (keys.get(i) & 0xFFFFFFFFL);
        int bi = bit >>> 3;
        int m = 1 << (bit & 7);
        if (bi >= 0 && bi < present.length) present[bi] = (byte) (present[bi] | m);
      }
    }

    // If JSON already provides tileTrees bitmasks, prefer them (persistence).
    try {
      JsonValue tt = (areaJson != null) ? areaJson.get("tileTrees") : null;
      if (tt != null) {
        int jw = tt.getInt("w", -1);
        int jh = tt.getInt("h", -1);
        String pb64 = tt.getString("presentB64", "");
        String cb64 = tt.getString("cutB64", "");
        if (jw == w && jh == h && pb64 != null && !pb64.isBlank()) {
          byte[] p2 = java.util.Base64.getDecoder().decode(pb64);
          if (p2 != null && p2.length == present.length) present = p2;
          if (cb64 != null && !cb64.isBlank()) {
            byte[] c2 = java.util.Base64.getDecoder().decode(cb64);
            if (c2 != null && c2.length == cut.length) cut = c2;
          }
        }
      }
    } catch (Throwable ignored) {}

    editorTileTreePresentBits = present;
    editorTileTreeCutBits = cut;
    editorTileTreeW = w;
    editorTileTreeH = h;
  }

  private void fitCameraToArea() {
    if (cam == null) return;
    if (areaW <= 0 || areaH <= 0) return;

    float ww = areaW * World.TILE_WORLD;
    float wh = areaH * World.TILE_WORLD;

    cam.position.set(ww * 0.5f, wh * 0.5f, 0f);

    float sw = Math.max(1f, Gdx.graphics.getWidth());
    float sh = Math.max(1f, Gdx.graphics.getHeight());
    float zx = ww / sw;
    float zy = wh / sh;
    float z = Math.max(zx, zy);

    // Add some padding.
    z *= 1.06f;
    cam.zoom = Math.max(0.10f, Math.min(8.0f, z));
    cam.update();
  }

  /** Returns true if the pointer is currently over any UI box (so map interactions must not trigger). */
  private boolean isPointerOverUi(int screenX, int screenY) {
    float sx = screenX;
    float sy = Gdx.graphics.getHeight() - screenY;

    // Prefer Scene2D hit testing. This correctly detects UI popups that are NOT inside the
    // VisWindow bounds (e.g. SelectBox dropdown list), preventing the map InputHandler from
    // eating clicks meant for UI.
    try {
      if (stage != null) {
        com.badlogic.gdx.scenes.scene2d.Actor hit = stage.hit(sx, sy, true);
        if (hit != null) return true;
      }
    } catch (Throwable ignored) {}

    VisWindow[] ws = new VisWindow[] { boxMapLoader, boxTerrainEdit, boxMarkersZones, boxEntitiesObjects, boxMouseTools };
    for (VisWindow w : ws) {
      if (w == null) continue;
      if (!w.isVisible()) continue;
      if (sx >= w.getX() && sx <= (w.getX() + w.getWidth()) && sy >= w.getY() && sy <= (w.getY() + w.getHeight())) {
        return true;
      }
    }
    return false;
  }

  private int nextSuffixForType(String type) {
    if (type == null) return 1;
    int max = 0;
    for (String id : allTemplateIds) {
      if (id == null) continue;
      if (!type.equals(typeOf(id))) continue;
      int k = id.lastIndexOf('_');
      if (k < 0 || k + 3 != id.length()) continue;
      String suf = id.substring(k + 1);
      if (suf.length() != 2) continue;
      if (!Character.isDigit(suf.charAt(0)) || !Character.isDigit(suf.charAt(1))) continue;
      int n = Integer.parseInt(suf);
      if (n > max) max = n;
    }
    int next = max + 1;
    if (next < 1) next = 1;
    return next;
  }

  private static String fmt2(int n) {
    if (n < 0) n = 0;
    if (n < 10) return "0" + n;
    return String.valueOf(n);
  }

  private void openNewAreaDialog() {
    // Minimal dialog for now (no styling wars).
    final VisWindow w = new VisWindow("New Area");
    w.setModal(true);
    w.setMovable(true);
    w.setResizable(false);

    Table root = new Table(VisUI.getSkin());
    root.defaults().pad(4).left();

    final SelectBox<String> knownTypeBox = new SelectBox<>(VisUI.getSkin());
    // Known types exclude ALL
    java.util.ArrayList<String> kt = new java.util.ArrayList<>();
    for (String s : allTypes) if (s != null && !"ALL".equals(s)) kt.add(s);
    if (kt.isEmpty()) kt.add("FOREST");
    knownTypeBox.setItems(kt.toArray(new String[0]));

    final TextField newTypeField = new TextField("", VisUI.getSkin());
    newTypeField.setMessageText("NEW TYPE (A-Z0-9_)");

    final TextField wField = new TextField("384", VisUI.getSkin());
    final TextField hField = new TextField("384", VisUI.getSkin());

    // Base ground selector
    final SelectBox<String> groundBox = new SelectBox<>(VisUI.getSkin());
    final int[] groundIds = buildGroundIdList();
    groundBox.setItems(buildGroundLabels(groundIds));
    groundBox.setSelectedIndex(0);

    final Label namePreview = new Label("", VisUI.getSkin());

    final TextButton btnModeKnown = new TextButton("Known Type", VisUI.getSkin());
    final TextButton btnModeNew = new TextButton("New Type", VisUI.getSkin());
    btnModeKnown.setChecked(true);

    final TextButton btnCreate = new TextButton("Create", VisUI.getSkin());
    final TextButton btnReset = new TextButton("Cancel (reset)", VisUI.getSkin());
    final TextButton btnClose = new TextButton("Cancel (close)", VisUI.getSkin());

    // state
    final boolean[] useNewType = new boolean[] { false };
    final String[] baseType = new String[] { knownTypeBox.getSelected() };
    final int[] baseW = new int[] { 384 };
    final int[] baseH = new int[] { 384 };
    final int[] baseGroundId = new int[] { 0 };

    final Runnable syncPreview = () -> {
      String type;
      if (useNewType[0]) {
        type = normalizeType(newTypeField.getText());
      } else {
        type = normalizeType(knownTypeBox.getSelected());
      }
      int next = nextSuffixForType(type);
      String id = type + "_" + fmt2(next);
      namePreview.setText("Will create: " + id);

      // dirty check for enabling close
      boolean dirty = false;
      if (useNewType[0]) {
        dirty |= !normalizeType(newTypeField.getText()).equals(normalizeType(baseType[0]));
      } else {
        dirty |= !normalizeType(knownTypeBox.getSelected()).equals(normalizeType(baseType[0]));
      }
      dirty |= !wField.getText().trim().equals(String.valueOf(baseW[0]));
      dirty |= !hField.getText().trim().equals(String.valueOf(baseH[0]));
      dirty |= selectedGroundIdFromBox(groundBox, groundIds) != baseGroundId[0];
      btnClose.setDisabled(dirty);
    };

    final Runnable doReset = () -> {
      useNewType[0] = false;
      btnModeKnown.setChecked(true);
      btnModeNew.setChecked(false);
      newTypeField.setText("");
      knownTypeBox.setSelected(kt.get(0));
      wField.setText("384");
      hField.setText("384");
      // Known type => ground follows TYPE_01 defaultId (field disabled).
      int gid = loadDefaultGroundIdForType01(normalizeType(knownTypeBox.getSelected()));
      baseGroundId[0] = gid;
      selectGroundInBox(groundBox, groundIds, gid);
      groundBox.setDisabled(true);
      baseType[0] = knownTypeBox.getSelected();
      baseW[0] = 384;
      baseH[0] = 384;
      syncPreview.run();
    };

    // layout
    root.add(btnModeKnown).width(140);
    root.add(btnModeNew).width(140).row();

    root.add(new Label("Known type", VisUI.getSkin()));
    root.add(knownTypeBox).width(240).row();

    root.add(new Label("New type", VisUI.getSkin()));
    root.add(newTypeField).width(240).row();

    root.add(new Label("Size (w,h)", VisUI.getSkin()));
    Table sz = new Table(VisUI.getSkin());
    sz.add(wField).width(80);
    sz.add(new Label("x", VisUI.getSkin())).padLeft(6).padRight(6);
    sz.add(hField).width(80);
    root.add(sz).left().row();

    root.add(new Label("Base ground", VisUI.getSkin()));
    root.add(groundBox).width(240).row();

    root.add(namePreview).colspan(2).row();

    Table br = new Table(VisUI.getSkin());
    br.defaults().pad(2);
    br.add(btnCreate).width(110);
    br.add(btnReset).width(140);
    br.add(btnClose).width(140);
    root.add(br).colspan(2).left().row();

    w.add(root);
    w.pack();
    w.setPosition(
        (Gdx.graphics.getWidth() - w.getWidth()) * 0.5f,
        (Gdx.graphics.getHeight() - w.getHeight()) * 0.5f);
    stage.addActor(w);

    // wiring
    // Guard against recursive ChangeListener triggering when we programmatically flip the other toggle.
    final boolean[] modeSync = new boolean[] { false };
    btnModeKnown.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        if (modeSync[0]) return;
        if (!btnModeKnown.isChecked()) return;
        modeSync[0] = true;
        useNewType[0] = false;
        if (!btnModeKnown.isChecked()) btnModeKnown.setChecked(true);
        if (btnModeNew.isChecked()) btnModeNew.setChecked(false);
        // Known type => base ground follows TYPE_01 (locked)
        String t = normalizeType(knownTypeBox.getSelected());
        int gid = loadDefaultGroundIdForType01(t);
        baseGroundId[0] = gid;
        selectGroundInBox(groundBox, groundIds, gid);
        groundBox.setDisabled(true);
        syncPreview.run();
        modeSync[0] = false;
      }
    });
    btnModeNew.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        if (modeSync[0]) return;
        if (!btnModeNew.isChecked()) return;
        modeSync[0] = true;
        useNewType[0] = true;
        if (btnModeKnown.isChecked()) btnModeKnown.setChecked(false);
        if (!btnModeNew.isChecked()) btnModeNew.setChecked(true);
        // New type => base ground is user-selectable
        groundBox.setDisabled(false);
        syncPreview.run();
        modeSync[0] = false;
      }
    });

    knownTypeBox.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        if (!useNewType[0]) {
          String t = normalizeType(knownTypeBox.getSelected());
          int gid = loadDefaultGroundIdForType01(t);
          baseGroundId[0] = gid;
          selectGroundInBox(groundBox, groundIds, gid);
          groundBox.setDisabled(true);
        }
        syncPreview.run();
      }
    });

    groundBox.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        syncPreview.run();
      }
    });

    newTypeField.setTextFieldListener((tf, c) -> {
      // normalize on the fly
      String norm = normalizeType(newTypeField.getText());
      if (!norm.equals(newTypeField.getText())) {
        int pos = newTypeField.getCursorPosition();
        newTypeField.setText(norm);
        newTypeField.setCursorPosition(Math.min(pos, norm.length()));
      }
      syncPreview.run();
    });

    wField.setTextFieldListener((tf, c) -> syncPreview.run());
    hField.setTextFieldListener((tf, c) -> syncPreview.run());

    btnReset.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        doReset.run();
      }
    });
    btnClose.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        if (!btnClose.isDisabled()) w.remove();
      }
    });

    btnCreate.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        String type = useNewType[0] ? normalizeType(newTypeField.getText()) : normalizeType(knownTypeBox.getSelected());
        if (type.isEmpty()) { setStatus("Type required"); return; }

        int next = nextSuffixForType(type);
        if (next > 99) { setStatus("Max suffix 99 reached for " + type); return; }
        String id = type + "_" + fmt2(next);

        int wv, hv;
        try { wv = Integer.parseInt(wField.getText().trim()); } catch (Throwable t) { wv = 384; }
        try { hv = Integer.parseInt(hField.getText().trim()); } catch (Throwable t) { hv = 384; }
        if (wv <= 0 || hv <= 0) { setStatus("Invalid size"); return; }

        int groundDefault = selectedGroundIdFromBox(groundBox, groundIds);
        if (!useNewType[0]) {
          // For known type we enforce the TYPE_01 base ground (locked)
          groundDefault = loadDefaultGroundIdForType01(type);
        }

        if (createNewAreaFile(id, wv, hv, groundDefault)) {
          w.remove();
          rescanAreas();
          selectTemplateIfExists(id);
          loadSelectedAreaFromUi();
        }
      }
    });

    doReset.run();
  }

  private boolean createNewAreaFile(String templateId, int w, int h, int defaultGroundId) {
    try {
      if (templateId == null || templateId.isEmpty()) return false;
      if (!isValidTemplateId(templateId)) {
        setStatus("Invalid template id: " + templateId);
        return false;
      }

      // Authoring file in project assets folder.
      FileHandle dir = resolveAreasWriteDir();
      if (dir == null) {
        setStatus("Missing write dir (assets/areas)");
        return false;
      }
      FileHandle out = dir.child(templateId + AREA_EXT);
      if (out.exists()) {
        setStatus("Already exists: " + templateId);
        return false;
      }

      // Schema-compliant file (minimal, but with persisted default ground).
      String json = "{\n" +
          "  \"schema\": \"FUSA_AREA_V1\",\n" +
          "  \"templateId\": \"" + templateId + "\",\n" +
          "  \"name\": \"" + templateId + "\",\n" +
          "  \"size\": { \"w\": " + w + ", \"h\": " + h + " },\n" +
          "  \"layers\": {\n" +
          "    \"ground\": {\n" +
          "      \"defaultId\": " + Math.max(0, defaultGroundId) + ",\n" +
          "      \"fills\": [],\n" +
          "      \"patches\": []\n" +
          "    },\n" +
          "    \"road\": {\n" +
          "      \"fills\": [],\n" +
          "      \"patches\": []\n" +
          "    },\n" +
          "    \"water\": {\n" +
          "      \"fills\": [],\n" +
          "      \"patches\": []\n" +
          "    }\n" +
          "  },\n" +
          "  \"markers\": {\n" +
          "    \"playerSpawn\": { \"x\": 0, \"y\": 0 },\n" +
          "    \"poi\": [],\n" +
          "    \"nodes\": [],\n" +
          "    \"enemyZones\": [],\n" +
          "    \"objects\": []\n" +
          "  }\n" +
          "}\n";

      out.writeString(json, false, "UTF-8");
      String p;
      try { p = out.file().getAbsolutePath(); } catch (Throwable t) { p = out.path(); }
      setStatus("Created: " + templateId + "\n" + "at: " + shortPath(p, 84));
      return true;
    } catch (Throwable t) {
      setStatus("Create failed");
      return false;
    }
  }

  @Override
  public void render(float delta) {
    // Save shortcut
    if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
      saveCurrentAreaToDisk();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      game.setScreen(new com.yourgame.survival.screens.MenuScreen(game));
      return;
    }

    // Manual reload (temporary helper)
    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) reloadArea();

    cam.update();
    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0.08f, 0.08f, 0.09f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    if (previewWorld != null) {
      // Draw enough chunks to cover the whole authored area.
      int r = 2;
      if (areaW > 0 && areaH > 0) {
        int maxCx = Math.max(0, (areaW - 1) / World.CHUNK_SIZE);
        int maxCy = Math.max(0, (areaH - 1) / World.CHUNK_SIZE);
        r = Math.max(maxCx, maxCy) + 2;
      }
      chunkRenderer.draw(batch, previewWorld, cam.position.x, cam.position.y, r);

      // Editor-only TileTrees sprite preview (matches ingame concept).
      drawEditorTileTrees(batch);
    }
    batch.end();

    // Entities preview (objects/landmarks/nodes/etc.) - uses the same y-sorting renderer as the game.
    if (entityRenderer != null && entities != null) {
      entityRenderer.tick(delta);
      batch.setProjectionMatrix(cam.combined);
      batch.begin();
      // No culling in editor preview.
      entityRenderer.draw(batch, entities, -9999, 9999, -9999, 9999);
      batch.end();
    }

    // Markers overlay (playerSpawn / nodes / poi / enemyZones)
    drawMarkersOverlay();

    // UI
    if (stage != null) {
      // Keep the box stack clamped while resizing/window changes.
      layoutUiBoxes();
      stage.act(delta);
      stage.draw();
    }

    // Minimal HUD
    batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    batch.begin();
    hudFont.draw(batch, "AREA EDITOR (repurposed WorldEditor)", 16, Gdx.graphics.getHeight() - 16);
    hudFont.draw(batch, "File: " + currentTemplateId + AREA_EXT, 16, Gdx.graphics.getHeight() - 36);
    hudFont.draw(batch, "R = reload | Ctrl+S = save | ESC = back", 16, Gdx.graphics.getHeight() - 56);
    if (areaDirty) hudFont.draw(batch, "*DIRTY*", 16, Gdx.graphics.getHeight() - 76);
    batch.end();
  }

  /** Draws TileTree-present bits as NODE_TREE sprites (editor-only). No center markers for these. */
  private void drawEditorTileTrees(SpriteBatch batch) {
    if (editorTileTreePresentBits == null || editorTileTreeW != areaW || editorTileTreeH != areaH) return;
    if (entityRegions == null) return;
    com.yourgame.survival.entity.EntityType t = com.yourgame.survival.entity.EntityType.NODE_TREE;
      com.badlogic.gdx.graphics.g2d.TextureRegion r = entityRegions.forEntity(t, 0f, -1, 0f, 0f, (byte) 2, 0f);
    float w = com.yourgame.survival.entity.EntityMetrics.drawW(t);
    float h = com.yourgame.survival.entity.EntityMetrics.drawH(t);

    int wTiles = editorTileTreeW;
    int max = wTiles * editorTileTreeH;
    for (int bit = 0; bit < max; bit++) {
      int i = bit >>> 3;
      int m = 1 << (bit & 7);
      if (i < 0 || i >= editorTileTreePresentBits.length) break;
      if ((editorTileTreePresentBits[i] & m) == 0) continue;
      if (editorTileTreeCutBits != null && i < editorTileTreeCutBits.length && (editorTileTreeCutBits[i] & m) != 0) continue;

      int tx = bit % wTiles;
      int ty = bit / wTiles;
      float x = (tx + 0.5f) * World.TILE_WORLD;
      float y = (ty + 0.5f) * World.TILE_WORLD;
      batch.draw(r, x - w / 2f, y - h / 2f, w, h);
    }
  }

  private void drawMarkersOverlay() {
    if (shapes == null || areaJson == null) return;
    JsonValue markers = areaJson.get("markers");
    if (markers == null) return;

    shapes.setProjectionMatrix(cam.combined);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    shapes.begin(ShapeRenderer.ShapeType.Line);

    // player spawn
    JsonValue sp = markers.get("playerSpawn");
    if (sp != null) {
      int tx = sp.getInt("x", 0);
      int ty = sp.getInt("y", 0);
      float x = (tx + 0.5f) * World.TILE_WORLD;
      float y = (ty + 0.5f) * World.TILE_WORLD;
      shapes.setColor(0.15f, 0.85f, 0.25f, 1f);
      shapes.circle(x, y, World.TILE_WORLD * 0.40f, 18);
      shapes.line(x - 10, y, x + 10, y);
      shapes.line(x, y - 10, x, y + 10);
    }

    // POIs
    JsonValue poi = markers.get("poi");
    if (poi != null) {
      shapes.setColor(0.25f, 0.65f, 1.00f, 1f);
      for (JsonValue p = poi.child; p != null; p = p.next) {
        int tx = p.getInt("x", 0);
        int ty = p.getInt("y", 0);
        float x = (tx + 0.5f) * World.TILE_WORLD;
        float y = (ty + 0.5f) * World.TILE_WORLD;
        shapes.rect(x - 6, y - 6, 12, 12);
      }
    }

    // Nodes
    JsonValue nodes = markers.get("nodes");
    if (nodes != null) {
      shapes.setColor(1.00f, 0.85f, 0.25f, 1f);
      for (JsonValue n = nodes.child; n != null; n = n.next) {
        int tx = n.getInt("x", 0);
        int ty = n.getInt("y", 0);
        float x = (tx + 0.5f) * World.TILE_WORLD;
        float y = (ty + 0.5f) * World.TILE_WORLD;
        shapes.triangle(x, y + 8, x - 7, y - 6, x + 7, y - 6);
      }
    }

    // Enemy zones
    JsonValue ez = markers.get("enemyZones");
    if (ez != null) {
      shapes.setColor(1.00f, 0.25f, 0.25f, 0.95f);
      for (JsonValue z = ez.child; z != null; z = z.next) {
        int cx = z.getInt("cx", 0);
        int cy = z.getInt("cy", 0);
        int r = z.getInt("r", 0);
        float x = (cx + 0.5f) * World.TILE_WORLD;
        float y = (cy + 0.5f) * World.TILE_WORLD;
        float rr = Math.max(0f, r) * World.TILE_WORLD;
        if (rr > 0.5f) shapes.circle(x, y, rr, 48);
      }
    }

    // HOME tile-tree presence preview
    if ("HOME_01".equalsIgnoreCase(currentTemplateId) && editorTreePresentBits != null
        && editorTreeW == areaW && editorTreeH == areaH) {
      shapes.setColor(0.10f, 0.85f, 0.10f, 0.35f);
      int wTiles = editorTreeW;
      int hTiles = editorTreeH;
      int max = wTiles * hTiles;
      for (int bit = 0; bit < max; bit++) {
        int i = bit >>> 3;
        int m = 1 << (bit & 7);
        if (i < 0 || i >= editorTreePresentBits.length) break;
        if ((editorTreePresentBits[i] & m) == 0) continue;
        int tx = bit % wTiles;
        int ty = bit / wTiles;
        float x = (tx + 0.5f) * World.TILE_WORLD;
        float y = (ty + 0.5f) * World.TILE_WORLD;
        shapes.triangle(x, y + 6, x - 5, y - 4, x + 5, y - 4);
      }
    }

    // Editor-only: Center markers for ALL placeable non-tile things (entities/objects/nodes/poi).
    // Requirement: everything placeable on the world gets a small center marker,
    // EXCEPT tiles and TileTrees.
    if (entities != null) {
      shapes.setColor(0.95f, 0.35f, 1.00f, 0.65f); // magenta-ish
      for (int i = 0; i < com.yourgame.survival.entity.Entities.MAX; i++) {
        if (!entities.alive[i]) continue;
        com.yourgame.survival.entity.EntityType t = entities.type[i];
        if (t == com.yourgame.survival.entity.EntityType.NODE_TREE) continue; // TileTrees are not marked
        float x = entities.x[i];
        float y = entities.y[i];
        shapes.triangle(x, y + 5, x - 4, y - 3, x + 4, y - 3);
      }
    }

    // Selected object highlight (markers.objects[] selection)
    if (selectedObjectJson != null) {
      int tx = selectedObjectJson.getInt("x", -1);
      int ty = selectedObjectJson.getInt("y", -1);
      if (tx >= 0 && ty >= 0) {
        float x = (tx + 0.5f) * World.TILE_WORLD;
        float y = (ty + 0.5f) * World.TILE_WORLD;
        shapes.setColor(0.10f, 0.95f, 0.95f, 0.95f); // cyan highlight
        float r = World.TILE_WORLD * 0.55f;
        shapes.circle(x, y, r, 24);
        shapes.line(x - r, y, x + r, y);
        shapes.line(x, y - r, x, y + r);
      }
    }

    shapes.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }

  @Override
  public void resize(int width, int height) {
    if (viewport != null) viewport.update(width, height, false);
    if (cam != null) cam.setToOrtho(false, width, height);
    fitCameraToArea();
  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    try { if (batch != null) batch.dispose(); } catch (Throwable ignored) {}
    try { if (shapes != null) shapes.dispose(); } catch (Throwable ignored) {}
    try { if (hudFont != null) hudFont.dispose(); } catch (Throwable ignored) {}
    try { if (tiles != null) tiles.dispose(); } catch (Throwable ignored) {}
    try { if (stage != null) stage.dispose(); } catch (Throwable ignored) {}
    try { if (entityRegions != null) entityRegions.dispose(); } catch (Throwable ignored) {}
    batch = null;
    shapes = null;
    hudFont = null;
    tiles = null;
    chunkRenderer = null;
    stage = null;
    previewWorld = null;
    areaJson = null;
    entities = null;
    entityRegions = null;
    entityRenderer = null;
  }

  private final class InputHandler extends InputAdapter {
    @Override
    public boolean scrolled(float amountX, float amountY) {
      // If the cursor is over UI, let UI widgets (scroll panes etc.) consume the wheel.
      // Otherwise, always treat the wheel as camera zoom.
      //
      // Important bugfix:
      // After selecting a marker/node tool inside a ScrollPane, Scene2D can keep a global
      // scrollFocus, causing the Stage to consume wheel events even when the mouse is over
      // the map. Clearing scrollFocus when not over UI restores reliable zoom.
      if (isPointerOverUi(Gdx.input.getX(), Gdx.input.getY())) {
        return false;
      }

      try {
        if (stage != null) stage.setScrollFocus(null);
      } catch (Throwable ignored) {}

      // Safety: if we missed a button-up (window focus etc.), stop painting/panning.
      if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) lmbPainting = false;
      if (!Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) panning = false;

      float dz = (amountY > 0) ? 1.08f : 0.92f;
      cam.zoom = Math.max(0.10f, Math.min(4.0f, cam.zoom * dz));
      return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      if (button == Input.Buttons.MIDDLE) {
        panning = true;
        panLast.set(screenX, screenY);
        return true;
      }
      if (button == Input.Buttons.LEFT) {
        // Only start painting/dragging when actually interacting with the map (not UI).
        if (isPointerOverUi(screenX, screenY)) return false;

        lmbPainting = true;
        lastPaintTx = Integer.MIN_VALUE;
        lastPaintTy = Integer.MIN_VALUE;

        // Universal MOVE drag: markers/nodes/objects are moved by click-hold-drag-release.
        if (toolMode == ToolMode.MOVE) {
          beginMoveDrag(screenX, screenY);
        }

        // If zones are active and mode is drag-based, begin drag.
        if (zoneActive && (toolMode == ToolMode.DEFINE || toolMode == ToolMode.MOVE || toolMode == ToolMode.ADJUST)) {
          beginZoneDrag(screenX, screenY);
        }
        // Paint immediately.
        onMapPaint(screenX, screenY);
        return true;
      }
      return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      if (button == Input.Buttons.MIDDLE) {
        panning = false;
        return true;
      }
      if (button == Input.Buttons.LEFT) {
        if (moveDragging) endMoveDrag(screenX, screenY);
        if (zoneDragging) endZoneDrag(screenX, screenY);
        lmbPainting = false;
        return true;
      }
      return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
      if (panning) {
        float dx = screenX - panLast.x;
        float dy = screenY - panLast.y;
        panLast.set(screenX, screenY);

        // Move camera in world space
        cam.position.add(-dx * cam.zoom, +dy * cam.zoom, 0f);
        return true;
      }
      if (lmbPainting) {
        if (moveDragging) {
          if (tileTreeDragging) updateTileTreeDrag(screenX, screenY);
          else updateMoveDrag(screenX, screenY);
        } else if (zoneDragging) {
          updateZoneDrag(screenX, screenY);
        } else {
          onMapPaint(screenX, screenY);
        }
        return true;
      }
      return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
      // Paint while holding LMB (continuous brush).
      if (lmbPainting) {
        onMapPaint(screenX, screenY);
        return true;
      }
      return false;
    }

    private Vector3 unproject(int sx, int sy) {
      Vector3 v = new Vector3(sx, sy, 0f);
      viewport.unproject(v);
      return v;
    }
  }

  private void onMapPaint(int screenX, int screenY) {
    if (previewWorld == null) return;

    if (isPointerOverUi(screenX, screenY)) return;

    // During MOVE drag we never paint.
    if (moveDragging) return;

    // If mode is MOVE and we didn't hit anything draggable, also do not paint.
    if (toolMode == ToolMode.MOVE) return;

    // UI hit guarding is handled by isPointerOverUi().

    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    // Universal delete must be able to delete *everything* regardless of which tool category
    // was previously selected.
    if (toolMode == ToolMode.DELETE) {
      onMapUniversalDelete(tx, ty);
      return;
    }

    // Markers/Zones/Objects tools have priority over Terrain painting when active.
    if (markerActive || zoneActive || objectActive) {
      onMapMarkerZone(tx, ty);
      return;
    }

    // Avoid re-applying the same tile repeatedly while dragging.
    if (tx == lastPaintTx && ty == lastPaintTy) return;
    lastPaintTx = tx;
    lastPaintTy = ty;

    // Auto-select water tool when WATER layer is active.
    // (Prevents "can't draw water" due to selection being cleared by action/layer changes.)
    if (layerTarget == LayerTarget.WATER) {
      selectedWater = true;
      selectedGroundId = -1;
      refreshTerrainPaletteHighlight();
    }

    // Brush: apply square around cursor.
    int r = Math.max(0, (brushSize - 1) / 2);
    int x0 = tx - r;
    int x1 = tx + r;
    int y0 = ty - r;
    int y1 = ty + r;

    for (int yy = y0; yy <= y1; yy++) {
      for (int xx = x0; xx <= x1; xx++) {
        if (xx < 0 || yy < 0 || (areaW > 0 && xx >= areaW) || (areaH > 0 && yy >= areaH)) continue;
        applyToolAt(xx, yy);
      }
    }
  }

  /**
   * Universal delete / eraser.
   *
   * Requirement (editor): must be able to delete everything by click or hold-drag.
   * This is intentionally slower than painting.
   */
  private void onMapUniversalDelete(int tx, int ty) {
    long now = System.currentTimeMillis();
    boolean sameTile = (tx == lastDeleteTx && ty == lastDeleteTy);
    if (sameTile && (now - lastDeleteAtMs) < DELETE_HOLD_INTERVAL_MS) return;
    lastDeleteAtMs = now;
    lastDeleteTx = tx;
    lastDeleteTy = ty;

    // Apply around cursor based on brush size (like painting), but throttled by the logic above.
    int r = Math.max(0, (brushSize - 1) / 2);
    int x0 = tx - r;
    int x1 = tx + r;
    int y0 = ty - r;
    int y1 = ty + r;

    for (int yy = y0; yy <= y1; yy++) {
      for (int xx = x0; xx <= x1; xx++) {
        if (xx < 0 || yy < 0 || (areaW > 0 && xx >= areaW) || (areaH > 0 && yy >= areaH)) continue;
        if (deleteAtTile(xx, yy)) {
          // Delete is idempotent; keep going.
        }
      }
    }
  }

  /** Deletes the topmost thing at tile, in a stable priority order. Returns true if something changed. */
  private boolean deleteAtTile(int tx, int ty) {
    if (areaJson == null) return false;
    JsonValue markers = ensureObj(areaJson, "markers");

    // 1) Objects (markers.objects[])
    JsonValue objs = markers.get("objects");
    if (objs != null) {
      JsonValue o = findFirstAt(objs, tx, ty);
      if (o != null) {
        String typ = o.getString("type", "");
        removePreviewEntityForObject(typ, tx, ty);
        o.remove();
        areaDirty = true;
        rebuildObjectsUi();
        return true;
      }
    }

    // 2) Nodes (markers.nodes[])
    JsonValue nodes = markers.get("nodes");
    if (nodes != null) {
      JsonValue n = findFirstAt(nodes, tx, ty);
      if (n != null) {
        String nodeType = n.getString("t", "");
        removePreviewEntityForNode(nodeType, tx, ty);
        n.remove();
        areaDirty = true;
        return true;
      }
    }

    // 3) POIs (markers.poi[])
    JsonValue poi = markers.get("poi");
    if (poi != null) {
      JsonValue p = findFirstAt(poi, tx, ty);
      if (p != null) {
        String kind = p.getString("kind", "");
        removePreviewEntityForPoi(kind, tx, ty);
        p.remove();
        areaDirty = true;
        return true;
      }
    }

    // 4) Player spawn (playerSpawn)
    JsonValue sp = markers.get("playerSpawn");
    if (sp != null) {
      int sx = sp.getInt("x", Integer.MIN_VALUE);
      int sy = sp.getInt("y", Integer.MIN_VALUE);
      if (sx == tx && sy == ty) {
        // "Delete" = reset to neutral (0,0).
        sp.remove("x"); sp.remove("y");
        sp.addChild("x", new JsonValue(0));
        sp.addChild("y", new JsonValue(0));
        areaDirty = true;
        return true;
      }
    }

    // 5) Enemy zones (contains tile)
    JsonValue ez = markers.get("enemyZones");
    if (ez != null) {
      JsonValue z = findZoneContaining(ez, tx, ty);
      if (z != null) {
        z.remove();
        areaDirty = true;
        return true;
      }
    }

    // 6) Road mask off
    JsonValue layers = areaJson.get("layers");
    JsonValue road = (layers != null) ? layers.get("road") : null;
    if (road != null) {
      writeRoadPatch(tx, ty, false);
      areaDirty = true;
      return true;
    }

    // 7) Water mask off
    JsonValue water = (layers != null) ? layers.get("water") : null;
    if (water != null) {
      writeWaterPatch(tx, ty, false);
      areaDirty = true;
      return true;
    }

    // 8) Ground back to default
    JsonValue ground = (layers != null) ? layers.get("ground") : null;
    int def = (ground != null) ? ground.getInt("defaultId", 0) : 0;
    writeGroundPatch(tx, ty, def);
    areaDirty = true;
    return true;
  }

  private void onMapMarkerZone(int tx, int ty) {
    if (areaJson == null) return;
    JsonValue markers = ensureObj(areaJson, "markers");

    // --- OBJECTS (markers.objects[]) ---
    if (objectActive) {
      JsonValue objs = ensureArray(markers, "objects");

      if (toolMode == ToolMode.DELETE) {
        JsonValue hit = findFirstAt(objs, tx, ty);
        if (hit != null) {
          // Kill matching preview entity (best-effort).
          String typ = hit.getString("type", "");
          removePreviewEntityForObject(typ, tx, ty);
          hit.remove();
          areaDirty = true;
          rebuildObjectsUi();
        }
        return;
      }

      if (toolMode == ToolMode.PLACE) {
        if (selectedObjectType == null || selectedObjectType.isBlank()) return;

        // Avoid duplicates: if there is already an object at this tile, overwrite its type.
        JsonValue hit = findFirstAt(objs, tx, ty);
        if (hit == null) {
          JsonValue o = new JsonValue(JsonValue.ValueType.object);
          o.addChild("type", new JsonValue(selectedObjectType.trim()));
          o.addChild("layerBias", new JsonValue(0f));
          o.addChild("x", new JsonValue(tx));
          o.addChild("y", new JsonValue(ty));
          objs.addChild(o);
        } else {
          // Update existing.
          String prev = hit.getString("type", "");
          removePreviewEntityForObject(prev, tx, ty);
          hit.remove("type");
          hit.addChild("type", new JsonValue(selectedObjectType.trim()));
          // keep layerBias if present
          hit.remove("x"); hit.remove("y");
          hit.addChild("x", new JsonValue(tx));
          hit.addChild("y", new JsonValue(ty));
        }

        spawnPreviewEntityForObject(selectedObjectType, tx, ty);
        areaDirty = true;
        rebuildObjectsUi();
        return;
      }

      // MOVE is handled by existing drag code.
      return;
    }

    // --- ZONES ---
    if (zoneActive) {
      String kind = (selectedZoneKind == null || selectedZoneKind.isBlank()) ? "default" : selectedZoneKind;
      JsonValue ez = ensureArray(markers, "enemyZones");

      if (toolMode == ToolMode.DELETE) {
        // Delete: remove first zone that contains the tile.
        JsonValue hit = findZoneContaining(ez, tx, ty);
        if (hit != null) {
          hit.remove();
          areaDirty = true;
        }
        return;
      }

      if (toolMode == ToolMode.PLACE) {
        // PLACE for zones = create a small default zone at cursor.
        JsonValue z = new JsonValue(JsonValue.ValueType.object);
        z.addChild("kind", new JsonValue(kind));
        z.addChild("cx", new JsonValue(tx));
        z.addChild("cy", new JsonValue(ty));
        z.addChild("r", new JsonValue(8));
        z.addChild("min", new JsonValue(0));
        z.addChild("max", new JsonValue(0));
        z.addChild("respawnDaysMin", new JsonValue(1));
        z.addChild("respawnDaysMax", new JsonValue(2));
        ez.addChild(z);
        areaDirty = true;
        return;
      }

      // DEFINE/MOVE/ADJUST are drag-based; handled by touchDown/Dragged/Up state.
      return;
    }

    // --- MARKERS ---
    if (!markerActive) return;
    if (selectedMarkerKind == null || selectedMarkerKind.isBlank()) return;

    if (toolMode == ToolMode.DELETE) {
      if ("PLAYER_SPAWN".equals(selectedMarkerKind)) {
        // Delete spawn: reset to 0,0
        JsonValue sp = ensureObj(markers, "playerSpawn");
        sp.remove("x"); sp.remove("y");
        sp.addChild("x", new JsonValue(0));
        sp.addChild("y", new JsonValue(0));
        areaDirty = true;
      } else if (selectedMarkerKind.startsWith("POI:")) {
        String poiKind = selectedMarkerKind.substring("POI:".length());
        JsonValue poi = ensureArray(markers, "poi");
        removeFirstAt(poi, tx, ty, "kind", poiKind);
        removePreviewEntityForPoi(poiKind, tx, ty);
      } else if (selectedMarkerKind.startsWith("NODE:")) {
        String nodeType = selectedMarkerKind.substring("NODE:".length());
        JsonValue nodes = ensureArray(markers, "nodes");
        removeFirstAt(nodes, tx, ty, "t", nodeType);
        removePreviewEntityForNode(nodeType, tx, ty);
      }
      return;
    }

    if (toolMode == ToolMode.MOVE || toolMode == ToolMode.ADJUST || toolMode == ToolMode.DEFINE) {
      // MOVE is handled by drag picking. ADJUST/DEFINE are zone modes.
      return;
    }

    // PLACE
    if ("PLAYER_SPAWN".equals(selectedMarkerKind)) {
      JsonValue sp = ensureObj(markers, "playerSpawn");
      sp.remove("x"); sp.remove("y");
      sp.addChild("x", new JsonValue(tx));
      sp.addChild("y", new JsonValue(ty));
      areaDirty = true;
      return;
    }

    if (selectedMarkerKind.startsWith("POI:")) {
      String poiKind = selectedMarkerKind.substring("POI:".length());
      JsonValue poi = ensureArray(markers, "poi");
      upsertMarker(poi, tx, ty, "kind", poiKind);
      spawnPreviewEntityForPoi(poiKind, tx, ty);
      return;
    }

    if (selectedMarkerKind.startsWith("NODE:")) {
      String nodeType = selectedMarkerKind.substring("NODE:".length());
      JsonValue nodes = ensureArray(markers, "nodes");
      upsertMarker(nodes, tx, ty, "t", nodeType);
      spawnPreviewEntityForNode(nodeType, tx, ty);
    }
  }

  // ---------------------------------------------------------------------------
  // Preview entity sync for marker edits (so sprites appear immediately)
  // ---------------------------------------------------------------------------

  private void spawnPreviewEntityForNode(String nodeType, int tx, int ty) {
    if (entities == null) return;
    if (nodeType == null || nodeType.isBlank()) return;
    String t = nodeType.trim();
    // NODE_TREE is allowed as an authored node (dev-only) and is movable.

    com.yourgame.survival.entity.EntityType et = null;
    try {
      et = com.yourgame.survival.entity.EntityType.valueOf(t);
    } catch (Throwable ignored) {}
    if (et == null) return;

    if (findPreviewEntityAt(et, tx, ty) >= 0) return;
    float wx = (tx + 0.5f) * World.TILE_WORLD;
    float wy = (ty + 0.5f) * World.TILE_WORLD;
    entities.spawn(et, wx, wy);
  }

  private void removePreviewEntityForNode(String nodeType, int tx, int ty) {
    if (entities == null) return;
    if (nodeType == null || nodeType.isBlank()) return;
    String t = nodeType.trim();
    com.yourgame.survival.entity.EntityType et = null;
    try { et = com.yourgame.survival.entity.EntityType.valueOf(t); } catch (Throwable ignored) {}
    if (et == null) return;
    int idx = findPreviewEntityAt(et, tx, ty);
    if (idx >= 0) entities.kill(idx);
  }

  private void spawnPreviewEntityForPoi(String poiKind, int tx, int ty) {
    if (entities == null) return;
    if (poiKind == null) return;
    // Only POIs that have actual entities.
    if (!poiKind.trim().equalsIgnoreCase("CHEST_HIDDEN")) return;
    com.yourgame.survival.entity.EntityType et = com.yourgame.survival.entity.EntityType.POI_CHEST_HIDDEN;
    if (findPreviewEntityAt(et, tx, ty) >= 0) return;
    float wx = (tx + 0.5f) * World.TILE_WORLD;
    float wy = (ty + 0.5f) * World.TILE_WORLD;
    entities.spawn(et, wx, wy);
  }

  private void removePreviewEntityForPoi(String poiKind, int tx, int ty) {
    if (entities == null) return;
    if (poiKind == null) return;
    if (!poiKind.trim().equalsIgnoreCase("CHEST_HIDDEN")) return;
    int idx = findPreviewEntityAt(com.yourgame.survival.entity.EntityType.POI_CHEST_HIDDEN, tx, ty);
    if (idx >= 0) entities.kill(idx);
  }

  private void spawnPreviewEntityForObject(String entityTypeName, int tx, int ty) {
    if (entities == null) return;
    if (entityTypeName == null || entityTypeName.isBlank()) return;
    com.yourgame.survival.entity.EntityType et = null;
    try { et = com.yourgame.survival.entity.EntityType.valueOf(entityTypeName.trim()); }
    catch (Throwable ignored) { et = null; }
    if (et == null) return;

    if (findPreviewEntityAt(et, tx, ty) >= 0) return;
    float wx = (tx + 0.5f) * World.TILE_WORLD;
    float wy = (ty + 0.5f) * World.TILE_WORLD;
    int idx = entities.spawn(et, wx, wy);
    if (idx >= 0) {
      // If the authored object has layerBias, it will be applied on reload.
      // For immediate preview on placement, keep default 0.
      entities.layerBias[idx] = 0f;
    }
  }

  private void removePreviewEntityForObject(String entityTypeName, int tx, int ty) {
    if (entities == null) return;
    if (entityTypeName == null || entityTypeName.isBlank()) return;
    com.yourgame.survival.entity.EntityType et = null;
    try { et = com.yourgame.survival.entity.EntityType.valueOf(entityTypeName.trim()); }
    catch (Throwable ignored) { et = null; }
    if (et == null) return;

    int idx = findPreviewEntityAt(et, tx, ty);
    if (idx >= 0) entities.kill(idx);
  }

  private int findPreviewEntityAt(com.yourgame.survival.entity.EntityType et, int tx, int ty) {
    if (entities == null || et == null) return -1;
    float wx = (tx + 0.5f) * World.TILE_WORLD;
    float wy = (ty + 0.5f) * World.TILE_WORLD;
    float eps = World.TILE_WORLD * 0.25f;
    float eps2 = eps * eps;
    for (int i = 0; i < com.yourgame.survival.entity.Entities.MAX; i++) {
      if (!entities.alive[i]) continue;
      if (entities.type[i] != et) continue;
      float dx = entities.x[i] - wx;
      float dy = entities.y[i] - wy;
      if (dx * dx + dy * dy <= eps2) return i;
    }
    return -1;
  }

  /** World-space hit test for preview entities (used for MOVE pick on big sprites like Castle). */
  private int findPreviewEntityHit(int screenX, int screenY) {
    if (entities == null) return -1;
    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    float wx = w.x;
    float wy = w.y;
    int best = -1;
    float bestD2 = Float.MAX_VALUE;
    float r = World.TILE_WORLD * 1.25f;
    float r2 = r * r;
    for (int i = 0; i < com.yourgame.survival.entity.Entities.MAX; i++) {
      if (!entities.alive[i]) continue;
      float dx = entities.x[i] - wx;
      float dy = entities.y[i] - wy;
      float d2 = dx * dx + dy * dy;
      if (d2 <= r2 && d2 < bestD2) {
        best = i;
        bestD2 = d2;
      }
    }
    return best;
  }

  private void upsertMarker(JsonValue arr, int tx, int ty, String keyName, String keyValue) {
    if (arr == null) return;
    for (JsonValue p = arr.child; p != null; p = p.next) {
      if (p.getInt("x", Integer.MIN_VALUE) == tx && p.getInt("y", Integer.MIN_VALUE) == ty
          && keyValue.equals(p.getString(keyName, ""))) {
        // already present
        return;
      }
    }
    JsonValue obj = new JsonValue(JsonValue.ValueType.object);
    obj.addChild(keyName, new JsonValue(keyValue));
    obj.addChild("x", new JsonValue(tx));
    obj.addChild("y", new JsonValue(ty));
    arr.addChild(obj);
    areaDirty = true;
  }

  private void removeFirstAt(JsonValue arr, int tx, int ty, String keyName, String keyValue) {
    if (arr == null) return;
    for (JsonValue p = arr.child; p != null; p = p.next) {
      if (p.getInt("x", Integer.MIN_VALUE) == tx && p.getInt("y", Integer.MIN_VALUE) == ty
          && keyValue.equals(p.getString(keyName, ""))) {
        p.remove();
        areaDirty = true;
        return;
      }
    }
  }

  private JsonValue findZoneContaining(JsonValue zones, int tx, int ty) {
    if (zones == null) return null;
    for (JsonValue z = zones.child; z != null; z = z.next) {
      int cx = z.getInt("cx", Integer.MIN_VALUE);
      int cy = z.getInt("cy", Integer.MIN_VALUE);
      int r = z.getInt("r", 0);
      if (cx == Integer.MIN_VALUE || cy == Integer.MIN_VALUE || r <= 0) continue;
      int dx = tx - cx;
      int dy = ty - cy;
      if (dx * dx + dy * dy <= r * r) return z;
    }
    return null;
  }

  private void applyToolAt(int tx, int ty) {
    switch (layerTarget) {
      case GROUND -> {
        int def = groundDefaultId();
        int cur = getGroundTile(tx, ty);

        if (toolMode == ToolMode.DELETE) {
          if (cur != def) {
            setGroundTile(tx, ty, def);
            writeGroundPatch(tx, ty, def);
          }
          return;
        }
        if (toolMode == ToolMode.MOVE) return;
        if (selectedGroundId < 0) return;

        int target = selectedGroundId;
        if (toolMode == ToolMode.SET) {
          // SET = only place if still default
          if (cur != def) return;
        }

        if (cur != target) {
          setGroundTile(tx, ty, target);
          writeGroundPatch(tx, ty, target);
        }
      }
      case WATER -> {
        boolean cur = getWaterTile(tx, ty);
        if (toolMode == ToolMode.MOVE) return;
        if (toolMode == ToolMode.DELETE) {
          if (cur) {
            setWaterTile(tx, ty, false);
            writeWaterPatch(tx, ty, false);
          }
          return;
        }
        // selectedWater is forced true when layer=WATER.

        if (toolMode == ToolMode.SET) {
          if (cur) return;
        }
        if (!cur) {
          setWaterTile(tx, ty, true);
          writeWaterPatch(tx, ty, true);
        }
      }
      case ROAD -> {
        boolean cur = getRoadTile(tx, ty);
        if (toolMode == ToolMode.MOVE) return;
        if (toolMode == ToolMode.DELETE) {
          if (cur) {
            setRoadTile(tx, ty, false);
            writeRoadPatch(tx, ty, false);
          }
          return;
        }
        // road tool = on/off (no palette)
        if (toolMode == ToolMode.SET) {
          if (cur) return;
        }
        if (!cur) {
          setRoadTile(tx, ty, true);
          writeRoadPatch(tx, ty, true);
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Zone drag operations (DEFINE/MOVE/ADJUST)
  // ---------------------------------------------------------------------------

  private void beginZoneDrag(int screenX, int screenY) {
    if (isPointerOverUi(screenX, screenY)) return;
    if (areaJson == null) return;

    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    JsonValue markers = ensureObj(areaJson, "markers");
    JsonValue ez = ensureArray(markers, "enemyZones");

    zoneDragging = true;
    zoneDragStartTx = tx;
    zoneDragStartTy = ty;
    zoneDragTarget = null;

    if (toolMode == ToolMode.MOVE || toolMode == ToolMode.ADJUST) {
      zoneDragTarget = findZoneContaining(ez, tx, ty);
      // If nothing hit, cancel drag.
      if (zoneDragTarget == null) {
        zoneDragging = false;
      }
    }
  }

  private void updateZoneDrag(int screenX, int screenY) {
    if (!zoneDragging || areaJson == null) return;
    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    JsonValue markers = ensureObj(areaJson, "markers");
    JsonValue ez = ensureArray(markers, "enemyZones");

    if (toolMode == ToolMode.DEFINE) {
      // Live preview not persisted; commit on release.
      return;
    }

    if (zoneDragTarget == null) return;
    if (toolMode == ToolMode.MOVE) {
      // Move center to cursor.
      zoneDragTarget.remove("cx"); zoneDragTarget.remove("cy");
      zoneDragTarget.addChild("cx", new JsonValue(tx));
      zoneDragTarget.addChild("cy", new JsonValue(ty));
      areaDirty = true;
      return;
    }

    if (toolMode == ToolMode.ADJUST) {
      int cx = zoneDragTarget.getInt("cx", zoneDragStartTx);
      int cy = zoneDragTarget.getInt("cy", zoneDragStartTy);
      int dx = tx - cx;
      int dy = ty - cy;
      int r = (int) Math.max(1, Math.round(Math.sqrt(dx * dx + dy * dy)));
      zoneDragTarget.remove("r");
      zoneDragTarget.addChild("r", new JsonValue(r));
      areaDirty = true;
    }
  }

  private void endZoneDrag(int screenX, int screenY) {
    if (!zoneDragging || areaJson == null) { zoneDragging = false; return; }
    zoneDragging = false;

    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    if (toolMode != ToolMode.DEFINE) return;

    // DEFINE: create a new zone with center at drag start and radius to release point.
    int dx = tx - zoneDragStartTx;
    int dy = ty - zoneDragStartTy;
    int r = (int) Math.max(1, Math.round(Math.sqrt(dx * dx + dy * dy)));

    JsonValue markers = ensureObj(areaJson, "markers");
    JsonValue ez = ensureArray(markers, "enemyZones");
    String kind = (selectedZoneKind == null || selectedZoneKind.isBlank()) ? "default" : selectedZoneKind;

    JsonValue z = new JsonValue(JsonValue.ValueType.object);
    z.addChild("kind", new JsonValue(kind));
    z.addChild("cx", new JsonValue(zoneDragStartTx));
    z.addChild("cy", new JsonValue(zoneDragStartTy));
    z.addChild("r", new JsonValue(r));
    z.addChild("min", new JsonValue(0));
    z.addChild("max", new JsonValue(0));
    z.addChild("respawnDaysMin", new JsonValue(1));
    z.addChild("respawnDaysMax", new JsonValue(2));
    ez.addChild(z);
    areaDirty = true;
  }

  // ---------------------------------------------------------------------------
  // MOVE drag for nodes/markers/objects
  // ---------------------------------------------------------------------------

  private void beginMoveDrag(int screenX, int screenY) {
    moveDragging = false;
    moveDragTarget = null;
    moveDragKind = null;
    movePreviewEntityType = null;
    movePreviewEntityIndex = -1;

    tileTreeDragging = false;
    tileTreeDragSrcTx = tileTreeDragSrcTy = -1;
    tileTreeDragLastTx = tileTreeDragLastTy = -1;

    if (isPointerOverUi(screenX, screenY)) return;
    if (areaJson == null) return;

    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    // TileTrees move (bitmask) has priority when clicking on a TileTree sprite.
    if (isTileTreePresentAt(tx, ty)) {
      moveDragging = true;
      tileTreeDragging = true;
      tileTreeDragSrcTx = tx;
      tileTreeDragSrcTy = ty;
      tileTreeDragLastTx = tx;
      tileTreeDragLastTy = ty;
      return;
    }

    JsonValue markers = areaJson.get("markers");
    if (markers == null) return;

    // Priority: objects -> nodes -> poi -> playerSpawn
    JsonValue objs = markers.get("objects");
    if (objs != null) {
      JsonValue o = findFirstAt(objs, tx, ty);
      if (o != null) {
        moveDragging = true;
        moveDragTarget = o;
        moveDragKind = "OBJECT";
        try {
          movePreviewEntityType = com.yourgame.survival.entity.EntityType.valueOf(o.getString("type", ""));
        } catch (Throwable ignored) { movePreviewEntityType = null; }
        if (movePreviewEntityType != null) movePreviewEntityIndex = findPreviewEntityAt(movePreviewEntityType, tx, ty);
        return;
      }
    }

    // Sprite hit-test fallback (castle/bridge etc. are large; click may not land on the exact tile).
    int hit = findPreviewEntityHit(screenX, screenY);
    if (hit >= 0 && entities != null && entities.alive[hit]) {
      com.yourgame.survival.entity.EntityType ht = entities.type[hit];
      int htx = (int) Math.floor(entities.x[hit] / World.TILE_WORLD);
      int hty = (int) Math.floor(entities.y[hit] / World.TILE_WORLD);

      if (objs != null) {
        for (JsonValue o = objs.child; o != null; o = o.next) {
          String typ = o.getString("type", "");
          if (typ == null || typ.isBlank()) continue;
          com.yourgame.survival.entity.EntityType et;
          try { et = com.yourgame.survival.entity.EntityType.valueOf(typ.trim()); }
          catch (Throwable ignored) { continue; }
          if (et != ht) continue;
          if (o.getInt("x", Integer.MIN_VALUE) == htx && o.getInt("y", Integer.MIN_VALUE) == hty) {
            moveDragging = true;
            moveDragTarget = o;
            moveDragKind = "OBJECT";
            movePreviewEntityType = ht;
            movePreviewEntityIndex = hit;
            return;
          }
        }
      }

      JsonValue nodes2 = markers.get("nodes");
      if (nodes2 != null && ht.name().startsWith("NODE_")) {
        for (JsonValue n = nodes2.child; n != null; n = n.next) {
          String tt = n.getString("t", "");
          if (tt == null || tt.isBlank()) continue;
          com.yourgame.survival.entity.EntityType et;
          try { et = com.yourgame.survival.entity.EntityType.valueOf(tt.trim()); }
          catch (Throwable ignored) { continue; }
          if (et != ht) continue;
          if (n.getInt("x", Integer.MIN_VALUE) == htx && n.getInt("y", Integer.MIN_VALUE) == hty) {
            moveDragging = true;
            moveDragTarget = n;
            moveDragKind = "NODE";
            movePreviewEntityType = ht;
            movePreviewEntityIndex = hit;
            return;
          }
        }
      }
    }

    JsonValue nodes = markers.get("nodes");
    if (nodes != null) {
      JsonValue n = findFirstAt(nodes, tx, ty);
      if (n != null) {
        moveDragging = true;
        moveDragTarget = n;
        moveDragKind = "NODE";
        String t = n.getString("t", "");
        try { movePreviewEntityType = com.yourgame.survival.entity.EntityType.valueOf(t); }
        catch (Throwable ignored) { movePreviewEntityType = null; }
        if (movePreviewEntityType != null) movePreviewEntityIndex = findPreviewEntityAt(movePreviewEntityType, tx, ty);
        return;
      }
    }

    JsonValue poi = markers.get("poi");
    if (poi != null) {
      JsonValue p = findFirstAt(poi, tx, ty);
      if (p != null) {
        moveDragging = true;
        moveDragTarget = p;
        moveDragKind = "POI";
        String kind = p.getString("kind", "");
        if (kind != null && kind.trim().equalsIgnoreCase("CHEST_HIDDEN")) {
          movePreviewEntityType = com.yourgame.survival.entity.EntityType.POI_CHEST_HIDDEN;
        }
        if (movePreviewEntityType != null) movePreviewEntityIndex = findPreviewEntityAt(movePreviewEntityType, tx, ty);
        return;
      }
    }

    JsonValue sp = markers.get("playerSpawn");
    if (sp != null) {
      int sx = sp.getInt("x", Integer.MIN_VALUE);
      int sy = sp.getInt("y", Integer.MIN_VALUE);
      if (sx == tx && sy == ty) {
        moveDragging = true;
        moveDragTarget = sp;
        moveDragKind = "PLAYER_SPAWN";
      }
    }
  }

  private boolean isTileTreePresentAt(int tx, int ty) {
    if (editorTileTreePresentBits == null) return false;
    if (tx < 0 || ty < 0 || tx >= editorTileTreeW || ty >= editorTileTreeH) return false;
    int bit = tx + ty * editorTileTreeW;
    int i = bit >>> 3;
    int m = 1 << (bit & 7);
    if (i < 0 || i >= editorTileTreePresentBits.length) return false;
    return (editorTileTreePresentBits[i] & m) != 0;
  }

  private void setTileTreePresentAt(int tx, int ty, boolean on) {
    if (editorTileTreePresentBits == null) return;
    if (tx < 0 || ty < 0 || tx >= editorTileTreeW || ty >= editorTileTreeH) return;
    int bit = tx + ty * editorTileTreeW;
    int i = bit >>> 3;
    int m = 1 << (bit & 7);
    if (i < 0 || i >= editorTileTreePresentBits.length) return;
    if (on) editorTileTreePresentBits[i] = (byte) (editorTileTreePresentBits[i] | m);
    else editorTileTreePresentBits[i] = (byte) (editorTileTreePresentBits[i] & ~m);
  }

  private void moveTileTreeCutBit(int fromTx, int fromTy, int toTx, int toTy) {
    if (editorTileTreeCutBits == null) return;
    if (fromTx < 0 || fromTy < 0 || toTx < 0 || toTy < 0) return;
    if (fromTx >= editorTileTreeW || toTx >= editorTileTreeW) return;
    if (fromTy >= editorTileTreeH || toTy >= editorTileTreeH) return;
    int b0 = fromTx + fromTy * editorTileTreeW;
    int b1 = toTx + toTy * editorTileTreeW;
    int i0 = b0 >>> 3, m0 = 1 << (b0 & 7);
    int i1 = b1 >>> 3, m1 = 1 << (b1 & 7);
    if (i0 < 0 || i0 >= editorTileTreeCutBits.length) return;
    if (i1 < 0 || i1 >= editorTileTreeCutBits.length) return;
    boolean cut = (editorTileTreeCutBits[i0] & m0) != 0;
    // clear src
    editorTileTreeCutBits[i0] = (byte) (editorTileTreeCutBits[i0] & ~m0);
    // set dst
    if (cut) editorTileTreeCutBits[i1] = (byte) (editorTileTreeCutBits[i1] | m1);
    else editorTileTreeCutBits[i1] = (byte) (editorTileTreeCutBits[i1] & ~m1);
  }

  private void updateTileTreeDrag(int screenX, int screenY) {
    if (!tileTreeDragging) return;
    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    if (tx == tileTreeDragLastTx && ty == tileTreeDragLastTy) return;

    // Move bit from last position to new.
    if (!isTileTreePresentAt(tileTreeDragLastTx, tileTreeDragLastTy)) {
      // If somehow missing, re-assert at last.
      setTileTreePresentAt(tileTreeDragLastTx, tileTreeDragLastTy, true);
    }
    setTileTreePresentAt(tileTreeDragLastTx, tileTreeDragLastTy, false);
    setTileTreePresentAt(tx, ty, true);
    moveTileTreeCutBit(tileTreeDragLastTx, tileTreeDragLastTy, tx, ty);

    tileTreeDragLastTx = tx;
    tileTreeDragLastTy = ty;
  }

  private void updateMoveDrag(int screenX, int screenY) {
    if (!moveDragging || moveDragTarget == null || areaJson == null) return;

    Vector3 w = new Vector3(screenX, screenY, 0f);
    viewport.unproject(w);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    if (tx < 0 || ty < 0 || (areaW > 0 && tx >= areaW) || (areaH > 0 && ty >= areaH)) return;

    // Write JSON x/y live so it visually follows the drag.
    moveDragTarget.remove("x");
    moveDragTarget.remove("y");
    moveDragTarget.addChild("x", new JsonValue(tx));
    moveDragTarget.addChild("y", new JsonValue(ty));
    areaDirty = true;

    // Update preview entity sprite position live.
    if (entities != null && movePreviewEntityIndex >= 0) {
      if (movePreviewEntityIndex < com.yourgame.survival.entity.Entities.MAX && entities.alive[movePreviewEntityIndex]) {
        entities.x[movePreviewEntityIndex] = (tx + 0.5f) * World.TILE_WORLD;
        entities.y[movePreviewEntityIndex] = (ty + 0.5f) * World.TILE_WORLD;
      }
    }
  }

  private void endMoveDrag(int screenX, int screenY) {
    // Drag already wrote JSON live; just clear state.
    moveDragging = false;
    moveDragTarget = null;
    moveDragKind = null;
    movePreviewEntityType = null;
    movePreviewEntityIndex = -1;

    tileTreeDragging = false;
    tileTreeDragSrcTx = tileTreeDragSrcTy = -1;
    tileTreeDragLastTx = tileTreeDragLastTy = -1;
  }

  private JsonValue findFirstAt(JsonValue arr, int tx, int ty) {
    if (arr == null) return null;
    for (JsonValue p = arr.child; p != null; p = p.next) {
      if (p.getInt("x", Integer.MIN_VALUE) == tx && p.getInt("y", Integer.MIN_VALUE) == ty) return p;
    }
    return null;
  }

  private int groundDefaultId() {
    try {
      JsonValue layers = (areaJson != null) ? areaJson.get("layers") : null;
      JsonValue g = (layers != null) ? layers.get("ground") : null;
      return (g != null) ? g.getInt("defaultId", 0) : 0;
    } catch (Throwable t) {
      return 0;
    }
  }

  // Minimal direct world edits (preview only). JSON persistence will be wired next.
  private void setGroundTile(int tx, int ty, int id) {
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    var c = previewWorld.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.groundId[idx] = (short) id;
    c.layers.waterMask[idx] = 0;
    c.layers.collisionMask[idx] = 0;
  }

  private int getGroundTile(int tx, int ty) {
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    var c = previewWorld.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.groundId[idx] & 0xFF;
  }

  private void setWaterTile(int tx, int ty, boolean on) {
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    var c = previewWorld.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.waterMask[idx] = (byte) (on ? 1 : 0);
    c.layers.collisionMask[idx] = 0;
  }

  private boolean getWaterTile(int tx, int ty) {
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    var c = previewWorld.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.waterMask[idx] != 0;
  }

  private void setRoadTile(int tx, int ty, boolean on) {
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    var c = previewWorld.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    c.layers.roadMask[idx] = (byte) (on ? 1 : 0);
    // Minimal visual until we wire adjacency baking in editor.
    c.layers.roadMask4[idx] = (byte) (on ? 15 : 0);
  }

  private boolean getRoadTile(int tx, int ty) {
    int cx = tx / World.CHUNK_SIZE;
    int cy = ty / World.CHUNK_SIZE;
    int lx = tx - cx * World.CHUNK_SIZE;
    int ly = ty - cy * World.CHUNK_SIZE;
    var c = previewWorld.chunk(cx, cy);
    int idx = lx + ly * World.CHUNK_SIZE;
    return c.layers.roadMask[idx] != 0;
  }

  // ---------------------------------------------------------------------------
  // In-memory JSON writeback (so edits are not "GPT coderest" that disappears).
  // File saving happens in the Map loader section later.
  // ---------------------------------------------------------------------------

  private JsonValue ensureObj(JsonValue parent, String key) {
    JsonValue v = (parent != null) ? parent.get(key) : null;
    if (v != null) return v;
    v = new JsonValue(JsonValue.ValueType.object);
    if (parent != null) parent.addChild(key, v);
    return v;
  }

  private JsonValue ensureArray(JsonValue parent, String key) {
    JsonValue v = (parent != null) ? parent.get(key) : null;
    if (v != null) return v;
    v = new JsonValue(JsonValue.ValueType.array);
    if (parent != null) parent.addChild(key, v);
    return v;
  }

  private void writeGroundPatch(int tx, int ty, int id) {
    if (areaJson == null) return;
    JsonValue layers = ensureObj(areaJson, "layers");
    JsonValue g = ensureObj(layers, "ground");
    int def = g.getInt("defaultId", 0);
    JsonValue patches = ensureArray(g, "patches");
    upsertPatch(patches, tx, ty, (id == def) ? null : Integer.valueOf(id), "id");
    areaDirty = true;
  }

  private void writeWaterPatch(int tx, int ty, boolean on) {
    if (areaJson == null) return;
    JsonValue layers = ensureObj(areaJson, "layers");
    JsonValue w = ensureObj(layers, "water");
    JsonValue patches = ensureArray(w, "patches");
    upsertPatch(patches, tx, ty, on ? Integer.valueOf(1) : null, "v");
    areaDirty = true;
  }

  private void writeRoadPatch(int tx, int ty, boolean on) {
    if (areaJson == null) return;
    JsonValue layers = ensureObj(areaJson, "layers");
    JsonValue r = ensureObj(layers, "road");
    JsonValue patches = ensureArray(r, "patches");
    upsertPatch(patches, tx, ty, on ? Integer.valueOf(1) : null, "v");
    areaDirty = true;
  }

  /**
   * Upsert/remove a patch entry in an array shaped like: {x:int, y:int, <valueKey>:int}.
   * If value == null: removes the entry for (x,y) if it exists.
   */
  private void upsertPatch(JsonValue patches, int tx, int ty, Integer value, String valueKey) {
    if (patches == null) return;
    JsonValue found = null;
    for (JsonValue p = patches.child; p != null; p = p.next) {
      if (p.getInt("x", Integer.MIN_VALUE) == tx && p.getInt("y", Integer.MIN_VALUE) == ty) {
        found = p;
        break;
      }
    }
    if (value == null) {
      if (found != null) found.remove();
      return;
    }
    if (found == null) {
      JsonValue obj = new JsonValue(JsonValue.ValueType.object);
      obj.addChild("x", new JsonValue(tx));
      obj.addChild("y", new JsonValue(ty));
      obj.addChild(valueKey, new JsonValue(value));
      patches.addChild(obj);
    } else {
      JsonValue vv = found.get(valueKey);
      if (vv == null) {
        found.addChild(valueKey, new JsonValue(value));
      } else {
        // libGDX JsonValue can't set(int) directly; replace the child value.
        vv.set(new JsonValue(value));
      }
    }
  }
}

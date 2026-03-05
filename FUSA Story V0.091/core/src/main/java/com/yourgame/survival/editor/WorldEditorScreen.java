package com.yourgame.survival.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.biome.BiomeSystem;
import com.yourgame.survival.render.ChunkRenderer;
import com.yourgame.survival.render.TilesetRegions;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.Chunk;
import com.yourgame.survival.world.TileIds;
import com.yourgame.survival.world.World;
import com.yourgame.survival.spawn.SpawnTypeId;
import com.yourgame.survival.spawn.SpawnTypeRegistry;

/**
 * Replacement editor (DO NOT COMPILE until fully implemented).
 *
 * Phase 1 skeleton: camera + stable unproject + HUD + 3x3 bounds visualization + mode/slots UI.
 */
public final class WorldEditorScreen extends ScreenAdapter {
  private final SurvivalGame game;

  private Stage stage;
  private SpriteBatch batch;
  private ShapeRenderer shapes;

  private OrthographicCamera cam;
  private Viewport viewport;

  private BitmapFont hudFont;
  private EditorDebugHud hud;

  // World preview (will later be replaced with editor-specific generator/world)
  private World previewWorld;
  private BiomeSystem biomes;
  private TilesetRegions tiles;
  private ChunkRenderer chunkRenderer;
  private EditorWorldGenerator editorGen;

  private boolean autoRefresh = false;

  private EditorMode mode = EditorMode.NUR_BIOME;
  private Biome selectedBiome = Biome.GRASSLAND;
  private final SlotGrid slotGrid = new SlotGrid();

  // Palette/tool state.
  private final ToolState tool = new ToolState();

  private EditorTransitionBaker transitionBaker;

  // Zone masks: per biome, per zone name -> pixmap/texture.
  private final java.util.HashMap<String, java.util.HashMap<String, Pixmap>> zonePix = new java.util.HashMap<>();
  private final java.util.HashMap<String, java.util.HashMap<String, Texture>> zoneTex = new java.util.HashMap<>();

  // Height masks: per biome -> pixmap (64x64, values in red channel 0..15)
  private final java.util.HashMap<String, Pixmap> heightPix = new java.util.HashMap<>();
  private final java.util.HashSet<String> heightDirty = new java.util.HashSet<>();

  // Deco masks: per biome -> pixmap (64x64, values in red channel 0..255)
  private final java.util.HashMap<String, Pixmap> decoPix = new java.util.HashMap<>();
  private final java.util.HashSet<String> decoDirty = new java.util.HashSet<>();

  // Node stamp rules per biome (type -> rule)
  private final java.util.HashMap<String, java.util.HashMap<String, com.yourgame.survival.biome.BiomeSystem.NodeStampRuleDef>> nodeRulesByBiome = new java.util.HashMap<>();

  // Encounter stamp rules per biome (type -> rule)
  private final java.util.HashMap<String, java.util.HashMap<String, com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef>> encounterRulesByBiome = new java.util.HashMap<>();

  // Zone->Height rules per biome.
  private final java.util.HashMap<String, java.util.ArrayList<ZoneHeightRule>> zoneHeightRulesByBiome = new java.util.HashMap<>();
  private int lastZoneHDelta = 0;

  private VisWindow ui;
  private SelectBox<String> modeBox;
  private SelectBox<String> biomeBox;

  // Generic 2D array creation triggers an unchecked warning in Java.
  // We keep it as a 2D array because the UI is a fixed 3x3 grid.
  @SuppressWarnings("unchecked")
  private final SelectBox<String>[][] slotBoxes = (SelectBox<String>[][]) new SelectBox<?>[3][3];

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
    hud = new EditorDebugHud(hudFont);

    stage = new Stage(new ScreenViewport());

    cam = new OrthographicCamera();
    cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    cam.zoom = 0.70f;
    float wChunk = World.CHUNK_SIZE * World.TILE_WORLD;
    cam.position.set(wChunk * 0.5f, wChunk * 0.5f, 0f);
    cam.update();

    viewport = new ScreenViewport(cam);
    viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

    InputMultiplexer mux = new InputMultiplexer();
    // Stage first: UI must get priority (SelectBoxes, buttons, etc.).
    mux.addProcessor(stage);
    mux.addProcessor(new InputHandler());
    Gdx.input.setInputProcessor(mux);

    // Preview world (editor generator, hard 3x3 chunk window)
    rebuildWorld(true);

    tiles = new TilesetRegions();
    chunkRenderer = new ChunkRenderer(tiles);

    transitionBaker = new EditorTransitionBaker(previewWorld);

    buildUi();
  }

  private static void setToolBtn(TextButton b, boolean on) {
    if (b == null) return;
    if (on) {
      b.setColor(0.25f, 0.85f, 0.25f, 1f);
    } else {
      b.setColor(0.75f, 0.75f, 0.75f, 1f);
    }
  }

  private void updateSlotUiEnabled() {
    boolean on = (mode == EditorMode.EXAMPLE_MAP);
    for (int sy = 0; sy < 3; sy++) {
      for (int sx = 0; sx < 3; sx++) {
        SelectBox<String> sb = slotBoxes[sx][sy];
        if (sb == null) continue;
        sb.setDisabled(!on);
      }
    }
  }

  private void buildUi() {
    Skin skin = VisUI.getSkin();

    ui = new VisWindow("World Editor");
    ui.setMovable(false);
    ui.setResizable(false);

    Table root = new Table(skin);
    root.defaults().pad(4);

    modeBox = new SelectBox<>(skin);
    modeBox.setItems("NUR_BIOME", "EXAMPLE_MAP");
    modeBox.setSelected("NUR_BIOME");

    biomeBox = new SelectBox<>(skin);
    String[] names = new String[Biome.values().length];
    for (int i = 0; i < Biome.values().length; i++) names[i] = Biome.values()[i].name();
    biomeBox.setItems(names);
    biomeBox.setSelected(selectedBiome.name());

    TextButton btnRefresh = new TextButton("Refresh World", skin);
    TextButton btnSave = new TextButton("Derive + Save", skin);
    TextButton btnAuto = new TextButton("AutoRefresh: OFF", skin);
    TextButton btnHud = new TextButton("HUD: ON", skin);

    root.add(new Label("Mode", skin)).left();
    root.add(modeBox).growX().row();
    root.add(new Label("Selected Biome", skin)).left();
    root.add(biomeBox).growX().row();

    root.add(btnRefresh).growX().row();
    root.add(btnSave).growX().row();
    root.add(btnAuto).growX().row();
    root.add(btnHud).growX().row();

    root.add(new Label("Example Map Slots (3x3)", skin)).left().colspan(2).row();

    // Slot grid: top row first in UI
    for (int sy = 2; sy >= 0; sy--) {
      for (int sx = 0; sx < 3; sx++) {
        SelectBox<String> sb = new SelectBox<>(skin);
        sb.setItems(names);
        sb.setSelected(slotGrid.get(sx, sy).name());
        slotBoxes[sx][sy] = sb;
        root.add(sb).width(180);
      }
      root.row();
    }

    // ---- Palette (buttons that highlight green when active) ----
    root.add(new Label("Palette", skin)).left().colspan(2).padTop(10).row();

    // Tool buttons (2 columns)
    TextButton btnToolGround = new TextButton("GROUND", skin);
    TextButton btnToolHeight = new TextButton("HEIGHT", skin);
    TextButton btnToolRoad = new TextButton("ROAD", skin);
    TextButton btnToolZone = new TextButton("ZONE", skin);
    TextButton btnToolBiome = new TextButton("BIOME", skin);
    TextButton btnToolDeco = new TextButton("DECO", skin);
    TextButton btnToolNode = new TextButton("NODE", skin);
    TextButton btnToolEntity = new TextButton("ENTITY", skin);

    Table toolGrid = new Table(skin);
    toolGrid.defaults().pad(2).width(150);
    toolGrid.add(btnToolGround);
    toolGrid.add(btnToolHeight).row();
    toolGrid.add(btnToolRoad);
    toolGrid.add(btnToolZone).row();
    toolGrid.add(btnToolBiome);
    toolGrid.add(btnToolDeco).row();
    toolGrid.add(btnToolNode);
    toolGrid.add(btnToolEntity).row();

    root.add(toolGrid).left().colspan(2).row();

    TextButton btnErase = new TextButton("Erase: OFF", skin);
    TextButton btnRadMinus = new TextButton("Radius-", skin);
    TextButton btnRadPlus = new TextButton("Radius+", skin);

    // Height delta controls
    TextButton btnHMinus = new TextButton("H-", skin);
    TextButton btnHPlus = new TextButton("H+", skin);
    Label lblH = new Label("HΔ=" + tool.heightDelta, skin);

    TextButton btnFlatten = new TextButton("Flatten: OFF", skin);
    TextButton btnResetH = new TextButton("Reset Height", skin);

    // Zone height rule controls (apply delta to heightLevel inside active zone)
    TextButton btnZHMinus = new TextButton("ZoneH-", skin);
    TextButton btnZHPlus = new TextButton("ZoneH+", skin);
    Label lblZH = new Label("ZoneHΔ=0", skin);
    TextButton btnApplyZH = new TextButton("Apply Zone Height", skin);
    TextButton btnClearZH = new TextButton("Clear Zone Height Rules", skin);
    final int[] zhDelta = new int[]{lastZoneHDelta};
    lblZH.setText("ZoneHΔ=" + zhDelta[0]);

    // Deco controls
    TextButton btnDecoMinus = new TextButton("Deco-", skin);
    TextButton btnDecoPlus = new TextButton("Deco+", skin);
    Label lblDeco = new Label("DecoΔ=" + decoDelta, skin);

    // Object placement controls (free, no snap). These are editor-only visible markers for now.
    SelectBox<String> spawnTypeBox = new SelectBox<>(skin);
    String[] spawnNames = new String[SpawnTypeId.values().length];
    for (int i = 0; i < SpawnTypeId.values().length; i++) spawnNames[i] = SpawnTypeId.values()[i].name();
    spawnTypeBox.setItems(spawnNames);
    spawnTypeBox.setSelected(activeSpawnType.name());

    // Zone name (used by ZONE_MASK and placement gating)
    TextField zoneField = new TextField(tool.activeZone, skin);
    TextButton btnSetZone = new TextButton("Set Zone", skin);

    // Node stamp controls (legacy rule authoring; kept)
    SelectBox<String> nodeTypeBox = new SelectBox<>(skin);
    nodeTypeBox.setItems("TREE", "ROCK", "IRON", "BUSH");
    nodeTypeBox.setSelected(activeNodeType);
    TextField nodeMinField = new TextField("2", skin);
    TextField nodeMaxField = new TextField("6", skin);
    TextField nodeJxField = new TextField("0.42", skin);
    TextField nodeJyField = new TextField("0.28", skin);
    CheckBox nodeOverlap = new CheckBox("AllowOverlap", skin);
    TextButton btnSetNodeRule = new TextButton("Set Node Rule", skin);

    // Encounter stamp controls (legacy rule authoring; kept)
    SelectBox<String> encTypeBox = new SelectBox<>(skin);
    encTypeBox.setItems("ORK_GRUNT", "ANIMAL_DEER");
    encTypeBox.setSelected(activeEncounterType);
    TextField encMaxLoaded = new TextField("12", skin);
    TextField encMinT = new TextField("8", skin);
    TextField encMaxT = new TextField("18", skin);
    TextButton btnSetEncRule = new TextButton("Set Encounter Rule", skin);

    Table pr = new Table(skin);
    pr.defaults().padRight(6);
    pr.add(btnErase);
    pr.add(btnRadMinus);
    pr.add(btnRadPlus);
    pr.row();
    pr.add(btnHMinus);
    pr.add(lblH);
    pr.add(btnHPlus);

    pr.row();
    pr.add(btnFlatten).colspan(3).growX();
    pr.row();
    pr.add(btnResetH).colspan(3).growX();

    pr.row();
    pr.add(btnZHMinus);
    pr.add(lblZH);
    pr.add(btnZHPlus);

    pr.row();
    pr.add(btnApplyZH).colspan(3).growX();
    pr.row();
    pr.add(btnClearZH).colspan(3).growX();

    pr.row();
    pr.add(btnDecoMinus);
    pr.add(lblDeco);
    pr.add(btnDecoPlus);

    pr.row();
    pr.add(new Label("Place", skin));
    pr.add(spawnTypeBox).colspan(2).growX();

    pr.row();
    pr.add(new Label("NodeType", skin));
    pr.add(nodeTypeBox).colspan(2).growX();
    pr.row();
    pr.add(new Label("min/max", skin));
    pr.add(nodeMinField);
    pr.add(nodeMaxField);
    pr.row();
    pr.add(new Label("jitterX/Y", skin));
    pr.add(nodeJxField);
    pr.add(nodeJyField);
    pr.row();
    pr.add(nodeOverlap).colspan(3).left();
    pr.row();
    pr.add(btnSetNodeRule).colspan(3).growX();

    pr.row();
    pr.add(new Label("Encounter", skin));
    pr.add(encTypeBox).colspan(2).growX();
    pr.row();
    pr.add(new Label("maxLoaded", skin));
    pr.add(encMaxLoaded).colspan(2).growX();
    pr.row();
    pr.add(new Label("respawn", skin));
    pr.add(encMinT);
    pr.add(encMaxT);
    pr.row();
    pr.add(btnSetEncRule).colspan(3).growX();

    root.add(pr).left().colspan(2).row();

    root.add(new Label("Active Zone", skin)).left();
    root.add(zoneField).growX().row();
    root.add(btnSetZone).left().colspan(2).row();

    // --- helpers ---
    java.util.function.Consumer<ToolKind> setTool = (k) -> {
      tool.kind = k;
      // green highlight for active, gray for inactive
      setToolBtn(btnToolGround, k == ToolKind.GROUND);
      setToolBtn(btnToolHeight, k == ToolKind.HEIGHT);
      setToolBtn(btnToolRoad, k == ToolKind.ROAD);
      setToolBtn(btnToolZone, k == ToolKind.ZONE_MASK);
      setToolBtn(btnToolBiome, k == ToolKind.BIOME);
      setToolBtn(btnToolDeco, k == ToolKind.DECO);
      setToolBtn(btnToolNode, k == ToolKind.NODE_STAMP);
      setToolBtn(btnToolEntity, k == ToolKind.ENTITY_STAMP);
    };

    // initial highlight
    setTool.accept(tool.kind);

    // tool button listeners
    btnToolGround.addListener(e -> { setTool.accept(ToolKind.GROUND); return true; });
    btnToolHeight.addListener(e -> { setTool.accept(ToolKind.HEIGHT); return true; });
    btnToolRoad.addListener(e -> { setTool.accept(ToolKind.ROAD); return true; });
    btnToolZone.addListener(e -> { setTool.accept(ToolKind.ZONE_MASK); return true; });
    btnToolBiome.addListener(e -> { setTool.accept(ToolKind.BIOME); return true; });
    btnToolDeco.addListener(e -> { setTool.accept(ToolKind.DECO); return true; });
    btnToolNode.addListener(e -> { setTool.accept(ToolKind.NODE_STAMP); return true; });
    btnToolEntity.addListener(e -> { setTool.accept(ToolKind.ENTITY_STAMP); return true; });

    // palette listeners
    btnErase.addListener(e -> {
      tool.erase = !tool.erase;
      btnErase.setText(tool.erase ? "Erase: ON" : "Erase: OFF");
      return true;
    });
    btnRadMinus.addListener(e -> { tool.radius = Math.max(1, tool.radius - 1); return true; });
    btnRadPlus.addListener(e -> { tool.radius = Math.min(24, tool.radius + 1); return true; });

    btnHMinus.addListener(e -> {
      tool.heightDelta = Math.max(1, tool.heightDelta - 1);
      lblH.setText("HΔ=" + tool.heightDelta);
      return true;
    });
    btnHPlus.addListener(e -> {
      tool.heightDelta = Math.min(8, tool.heightDelta + 1);
      lblH.setText("HΔ=" + tool.heightDelta);
      return true;
    });

    btnFlatten.addListener(e -> {
      heightFlatten = !heightFlatten;
      heightFlattenTarget = -1;
      btnFlatten.setText(heightFlatten ? "Flatten: ON" : "Flatten: OFF");
      return true;
    });

    btnResetH.addListener(e -> {
      // Reset height mask for the currently selected biome.
      if (selectedBiome != null) {
        Pixmap pm = getOrCreateHeightPixmap(selectedBiome);
        HeightMaskCodec.clear(pm);
        heightDirty.add(selectedBiome.name());
      }
      // Re-apply baseline(height masks) + zone-height rules
      applyAllZoneHeightRules();
      return true;
    });

    btnZHMinus.addListener(e -> {
      zhDelta[0] = Math.max(-15, zhDelta[0] - 1);
      lastZoneHDelta = zhDelta[0];
      lblZH.setText("ZoneHΔ=" + zhDelta[0]);
      return true;
    });
    btnZHPlus.addListener(e -> {
      zhDelta[0] = Math.min(15, zhDelta[0] + 1);
      lastZoneHDelta = zhDelta[0];
      lblZH.setText("ZoneHΔ=" + zhDelta[0]);
      return true;
    });
    btnApplyZH.addListener(e -> {
      if (tool.activeZone == null || tool.activeZone.isBlank()) return true;
      String biomeKey = (selectedBiome != null) ? selectedBiome.name() : "";
      if (biomeKey.isBlank()) return true;

      ZoneHeightRule r = new ZoneHeightRule(tool.activeZone.trim(), zhDelta[0]);
      zoneHeightRulesByBiome.computeIfAbsent(biomeKey, k -> new java.util.ArrayList<>()).add(r);
      lastZoneHDelta = zhDelta[0];
      applyAllZoneHeightRules();
      return true;
    });
    btnClearZH.addListener(e -> {
      String biomeKey = (selectedBiome != null) ? selectedBiome.name() : "";
      if (!biomeKey.isBlank()) {
        zoneHeightRulesByBiome.remove(biomeKey);
      }
      applyAllZoneHeightRules();
      return true;
    });

    nodeTypeBox.addListener(e -> { activeNodeType = nodeTypeBox.getSelected(); return true; });
    encTypeBox.addListener(e -> { activeEncounterType = encTypeBox.getSelected(); return true; });

    btnDecoMinus.addListener(e -> { decoDelta = Math.max(1, decoDelta - 8); lblDeco.setText("DecoΔ=" + decoDelta); return true; });
    btnDecoPlus.addListener(e -> { decoDelta = Math.min(255, decoDelta + 8); lblDeco.setText("DecoΔ=" + decoDelta); return true; });

    btnSetNodeRule.addListener(e -> {
      if (selectedBiome == null) return true;
      String bk = selectedBiome.name();
      String type = (activeNodeType == null) ? "TREE" : activeNodeType.trim().toUpperCase();
      String mask = "STAMP_NODE_" + type;

      com.yourgame.survival.biome.BiomeSystem.NodeStampRuleDef r = new com.yourgame.survival.biome.BiomeSystem.NodeStampRuleDef();
      r.type = type;
      r.mask = mask;
      try { r.min = Integer.parseInt(nodeMinField.getText().trim()); } catch (Throwable ignored) { r.min = 0; }
      try { r.max = Integer.parseInt(nodeMaxField.getText().trim()); } catch (Throwable ignored) { r.max = r.min; }
      try { r.jitterX = Float.parseFloat(nodeJxField.getText().trim()); } catch (Throwable ignored) { r.jitterX = 0f; }
      try { r.jitterY = Float.parseFloat(nodeJyField.getText().trim()); } catch (Throwable ignored) { r.jitterY = 0f; }
      r.allowOverlap = nodeOverlap.isChecked();

      nodeRulesByBiome.computeIfAbsent(bk, k -> new java.util.HashMap<>()).put(type, r);
      // ensure mask exists so painting works
      getOrCreateZonePixmap(bk, mask);
      return true;
    });

    btnSetEncRule.addListener(e -> {
      if (selectedBiome == null) return true;
      String bk = selectedBiome.name();
      String type = (activeEncounterType == null) ? "ORK_GRUNT" : activeEncounterType.trim().toUpperCase();
      String mask = "STAMP_ENCOUNTER_" + type;

      com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef r = new com.yourgame.survival.biome.BiomeSystem.EncounterStampRuleDef();
      r.type = type;
      r.mask = mask;
      try { r.maxLoaded = Integer.parseInt(encMaxLoaded.getText().trim()); } catch (Throwable ignored) { r.maxLoaded = 0; }
      try { r.respawnMin = Float.parseFloat(encMinT.getText().trim()); } catch (Throwable ignored) { r.respawnMin = 8f; }
      try { r.respawnMax = Float.parseFloat(encMaxT.getText().trim()); } catch (Throwable ignored) { r.respawnMax = 18f; }

      encounterRulesByBiome.computeIfAbsent(bk, k -> new java.util.HashMap<>()).put(type, r);
      getOrCreateZonePixmap(bk, mask);
      return true;
    });

    spawnTypeBox.addListener(e -> {
      try { activeSpawnType = SpawnTypeId.valueOf(spawnTypeBox.getSelected()); } catch (Throwable ignored) {}
      return true;
    });

    btnSetZone.addListener(e -> {
      String z = zoneField.getText();
      if (z != null) tool.activeZone = z.trim();
      if (tool.activeZone == null || tool.activeZone.isBlank()) tool.activeZone = "zone";
      return true;
    });

    // listeners

    btnAuto.addListener(e -> {
      autoRefresh = !autoRefresh;
      btnAuto.setText(autoRefresh ? "AutoRefresh: ON" : "AutoRefresh: OFF");
      return true;
    });

    btnHud.addListener(e -> {
      hud.enabled = !hud.enabled;
      btnHud.setText(hud.enabled ? "HUD: ON" : "HUD: OFF");
      return true;
    });

    modeBox.addListener(e -> {
      String s = modeBox.getSelected();
      mode = "EXAMPLE_MAP".equalsIgnoreCase(s) ? EditorMode.EXAMPLE_MAP : EditorMode.NUR_BIOME;
      if (editorGen != null) editorGen.setMode(mode);
      updateSlotUiEnabled();

      // Mode switch must be immediately reflected.
      rebuildWorld(true);
      return true;
    });

    biomeBox.addListener(e -> {
      try { selectedBiome = Biome.valueOf(biomeBox.getSelected()); } catch (Throwable ignored) {}
      tool.biome = selectedBiome;
      if (editorGen != null) editorGen.setSelectedBiome(selectedBiome);

      // In NUR_BIOME, the selection must immediately affect the preview.
      if (mode == EditorMode.NUR_BIOME) {
        rebuildWorld(true);
      } else {
        if (autoRefresh) rebuildWorld(true);
      }
      return true;
    });

    btnRefresh.addListener(e -> {
      // Hard rebuild: reload BiomeSystem from disk and discard old chunk cache.
      rebuildWorld(true);
      return true;
    });

    btnSave.addListener(e -> {
      deriveAndSave();
      return true;
    });

    // slot changes just update slotGrid model
    for (int sy = 0; sy < 3; sy++) {
      for (int sx = 0; sx < 3; sx++) {
        final int fx = sx;
        final int fy = sy;
        slotBoxes[sx][sy].addListener(e -> {
          try { slotGrid.set(fx, fy, Biome.valueOf(slotBoxes[fx][fy].getSelected())); } catch (Throwable ignored) {}
          if (mode == EditorMode.EXAMPLE_MAP) {
            if (autoRefresh) rebuildWorld(true);
          }
          return true;
        });
      }
    }

    updateSlotUiEnabled();

    ui.add(root).grow();
    ui.pack();
    ui.setSize(Math.min(660f, Gdx.graphics.getWidth() * 0.40f), Gdx.graphics.getHeight());
    ui.setPosition(10f, 0f);
    stage.addActor(ui);
  }

  private void rebuildWorld(boolean reloadFromDisk) {
    final long seed = 1234567L;

    // Drop references (helps GC; ensures old cache can't be used).
    previewWorld = null;
    biomes = null;

    // Reload biome settings from disk by constructing a new BiomeSystem.
    // (BiomeSystem loads from local config/biomes.json)
    BiomeSystem bs = new BiomeSystem(seed);

    // Create editor generator that forces biomes per mode/slot.
    editorGen = new EditorWorldGenerator(seed, bs, slotGrid, mode, selectedBiome);

    previewWorld = new World(seed, bs, editorGen);
    biomes = bs;

    // Warm up the 3x3 chunks so preview isn't blank.
    for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
      for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
        previewWorld.chunk(cx, cy);
      }
    }

    transitionBaker = new EditorTransitionBaker(previewWorld);

    // Load stamp rules from biomes.json into editor state (so Save doesn't wipe them).
    nodeRulesByBiome.clear();
    encounterRulesByBiome.clear();
    for (Biome b : Biome.values()) {
      BiomeSystem.BiomeDef d = biomes.def(b);

      if (d.nodeStampRules != null && !d.nodeStampRules.isEmpty()) {
        var map = nodeRulesByBiome.computeIfAbsent(b.name(), k -> new java.util.HashMap<>());
        for (int i = 0; i < d.nodeStampRules.size(); i++) {
          var r = d.nodeStampRules.get(i);
          if (r == null || r.type == null) continue;
          map.put(r.type.trim().toUpperCase(), r);
        }
      }

      if (d.encounterStampRules != null && !d.encounterStampRules.isEmpty()) {
        var map = encounterRulesByBiome.computeIfAbsent(b.name(), k -> new java.util.HashMap<>());
        for (int i = 0; i < d.encounterStampRules.size(); i++) {
          var r = d.encounterStampRules.get(i);
          if (r == null || r.type == null) continue;
          map.put(r.type.trim().toUpperCase(), r);
        }
      }
    }

    // Re-apply baseline(height masks) + zone height rules on refresh.
    applyAllZoneHeightRules();
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      game.setScreen(new com.yourgame.survival.screens.MenuScreen(game));
      return;
    }

    // update HUD sampling
    updateHud();

    cam.update();
    batch.setProjectionMatrix(cam.combined);

    Gdx.gl.glClearColor(0.08f, 0.08f, 0.09f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // draw world preview
    batch.begin();
    chunkRenderer.draw(batch, previewWorld, cam.position.x, cam.position.y, 2);
    drawZoneOverlays();
    drawChunkSlotLabels();
    batch.end();

    // Draw editor-only placed object markers.
    drawPlacedObjects();

    // draw chunk grid (3x3)
    drawChunkGrid();

    // UI
    stage.act(delta);
    stage.draw();

    // HUD
    batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    batch.begin();
    hud.draw(batch);
    batch.end();
  }

  private void updateHud() {
    int sx = Gdx.input.getX();
    int sy = Gdx.input.getY();
    hud.screenX = sx;
    hud.screenY = sy;

    Vector3 w = screenToWorld(sx, sy);
    hud.worldX = w.x;
    hud.worldY = w.y;

    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);
    hud.tileX = tx;
    hud.tileY = ty;

    hud.chunkX = floorDiv(tx, World.CHUNK_SIZE);
    hud.chunkY = floorDiv(ty, World.CHUNK_SIZE);

    // sample layers under cursor (peek only)
    try {
      if (previewWorld != null && EditorBounds.isTileInside(tx, ty)) {
        int cx = EditorCoord.floorDiv(tx, World.CHUNK_SIZE);
        int cy = EditorCoord.floorDiv(ty, World.CHUNK_SIZE);
        com.yourgame.survival.world.Chunk c = previewWorld.peekChunk(cx, cy);
        if (c != null && c.layers != null) {
          int lx = EditorCoord.mod(tx, World.CHUNK_SIZE);
          int ly = EditorCoord.mod(ty, World.CHUNK_SIZE);
          int idx = lx + ly * World.CHUNK_SIZE;
          hud.groundId = c.layers.groundId[idx] & 0xFF;
          hud.heightLevel = c.layers.heightLevel[idx] & 0xFF;
        }
      }
    } catch (Throwable ignored) {}
  }

  /**
   * Single source of truth for input mapping.
   * NOTE: This is a skeleton; exact Y handling will be finalized when we remove legacy editor.
   */
  private Vector3 screenToWorld(int screenX, int screenY) {
    // Gdx input screen coords: origin TOP-left.
    // World/camera coords: origin BOTTOM-left (y up).
    // ScreenViewport.unproject(Vector2) does not reliably flip Y in all setups, so we flip here.
    int fy = Gdx.graphics.getHeight() - screenY;
    Vector2 v = new Vector2(screenX, fy);
    viewport.unproject(v);
    return new Vector3(v.x, v.y, 0f);
  }

  private void drawChunkSlotLabels() {
    if (hudFont == null) return;
    float wChunk = World.CHUNK_SIZE * World.TILE_WORLD;

    // Draw a small label for each chunk in the 3x3 window.
    for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
      for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
        Biome b;
        if (mode == EditorMode.EXAMPLE_MAP) {
          b = slotGrid.get(SlotGrid.slotX(cx), SlotGrid.slotY(cy));
        } else {
          b = selectedBiome;
        }
        String name = (b != null) ? b.name() : "(null)";

        float x = cx * wChunk + 10f;
        float y = cy * wChunk + wChunk - 10f;
        hudFont.draw(batch, "[" + cx + "," + cy + "] " + name, x, y);
      }
    }
  }

  private void drawChunkGrid() {
    float wChunk = World.CHUNK_SIZE * World.TILE_WORLD;
    shapes.setProjectionMatrix(cam.combined);
    shapes.begin(ShapeRenderer.ShapeType.Line);
    shapes.setColor(0f, 0f, 0f, 0.55f);
    for (int cy = -1; cy <= 1; cy++) {
      for (int cx = -1; cx <= 1; cx++) {
        float x0 = cx * wChunk;
        float y0 = cy * wChunk;
        shapes.rect(x0, y0, wChunk, wChunk);
      }
    }
    shapes.end();
  }

  private static int floorDiv(int a, int b) {
    int r = a / b;
    if ((a ^ b) < 0 && (r * b != a)) r--;
    return r;
  }

  @Override
  public void resize(int width, int height) {
    if (stage != null) stage.getViewport().update(width, height, true);
    if (viewport != null) viewport.update(width, height, false);
    if (ui != null) {
      ui.setSize(Math.min(660f, width * 0.40f), height);
      ui.setPosition(10f, 0f);
    }
  }

  @Override
  public void dispose() {
    try { if (stage != null) stage.dispose(); } catch (Throwable ignored) {}
    try { if (batch != null) batch.dispose(); } catch (Throwable ignored) {}
    try { if (shapes != null) shapes.dispose(); } catch (Throwable ignored) {}
    try { if (tiles != null) tiles.dispose(); } catch (Throwable ignored) {}
    try { if (hudFont != null) hudFont.dispose(); } catch (Throwable ignored) {}

    try { if (VisUI.isLoaded()) VisUI.dispose(); } catch (Throwable ignored) {}
  }

  private PaintStroke currentStroke;

  // Height tool extras
  private boolean heightFlatten = false;
  private int heightFlattenTarget = -1;

  // Deco tool
  private int decoDelta = 32;

  // Stamp/object tool state
  private String activeNodeType = "TREE";
  private String activeEncounterType = "ORK_GRUNT";
  private SpawnTypeId activeSpawnType = SpawnTypeId.ANIMAL_DEER;

  private static final class PlacedObject {
    String biomeName;
    String zoneName;
    SpawnTypeId type;
    float lx;
    float ly;
  }

  private final java.util.HashMap<String, java.util.ArrayList<PlacedObject>> placedObjectsByBiome = new java.util.HashMap<>();

  private boolean paintAt(int screenX, int screenY) {
    if (previewWorld == null) return false;

    Vector3 w = screenToWorld(screenX, screenY);
    int tx = (int) Math.floor(w.x / World.TILE_WORLD);
    int ty = (int) Math.floor(w.y / World.TILE_WORLD);

    if (!EditorBounds.isTileInside(tx, ty)) return false;

    tool.clamp();

    // Determine biome under cursor (slot biome in example map; selected biome in nur_biome).
    int cx0 = EditorCoord.floorDiv(tx, World.CHUNK_SIZE);
    int cy0 = EditorCoord.floorDiv(ty, World.CHUNK_SIZE);
    Biome under = (mode == EditorMode.EXAMPLE_MAP)
        ? slotGrid.get(SlotGrid.slotX(cx0), SlotGrid.slotY(cy0))
        : selectedBiome;

    if (tool.kind == ToolKind.GROUND) {
      short gid = tool.groundId;
      if (tool.erase) {
        // erase to selected biome default ground (best-effort)
        try { gid = biomes.def(under).groundId; } catch (Throwable ignored) {}
      }
      paintCircleGround(tx, ty, tool.radius, gid);
      return true;
    }

    if (tool.kind == ToolKind.HEIGHT) {
      boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
      int base = Math.abs(tool.heightDelta);
      int delta = (tool.erase || ctrl) ? -base : base;
      paintCircleHeight(tx, ty, tool.radius, delta);
      return true;
    }

    if (tool.kind == ToolKind.ROAD) {
      paintCircleRoad(tx, ty, tool.radius, !tool.erase);
      return true;
    }

    if (tool.kind == ToolKind.ZONE_MASK) {
      paintCircleZone(under, tx, ty, tool.radius, tool.erase);
      return true;
    }

    if (tool.kind == ToolKind.DECO) {
      paintCircleDeco(under, tx, ty, tool.radius);
      return true;
    }

    if (tool.kind == ToolKind.NODE_STAMP || tool.kind == ToolKind.ENTITY_STAMP) {
      placeObject(under, tx, ty, tool.erase);
      return true;
    }

    return false;
  }

  private void paintCircleRoad(int centerTx, int centerTy, int radius, boolean on) {
    int r = Math.max(1, radius);
    int r2 = r * r;
    byte v = (byte) (on ? 1 : 0);

    // Track which chunks were touched so we can recompute roadMask4 per-chunk.
    java.util.HashSet<Long> touched = new java.util.HashSet<>();

    for (int dy = -r; dy <= r; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        if (dx * dx + dy * dy > r2) continue;
        int tx = centerTx + dx;
        int ty = centerTy + dy;
        if (!EditorBounds.isTileInside(tx, ty)) continue;

        int cx = EditorCoord.floorDiv(tx, World.CHUNK_SIZE);
        int cy = EditorCoord.floorDiv(ty, World.CHUNK_SIZE);
        int lx = EditorCoord.mod(tx, World.CHUNK_SIZE);
        int ly = EditorCoord.mod(ty, World.CHUNK_SIZE);

        Chunk c = previewWorld.chunk(cx, cy);
        if (c == null || c.layers == null) continue;
        int idx = lx + ly * World.CHUNK_SIZE;
        c.layers.roadMask[idx] = v;
        markDirty(tx, ty);
        touched.add((((long)cx) << 32) ^ (cy & 0xffffffffL));
      }
    }

    for (long k : touched) {
      int cx = (int) (k >> 32);
      int cy = (int) (k);
      Chunk c = previewWorld.chunk(cx, cy);
      if (c != null && c.layers != null) recomputeRoadMask4(c.layers);
    }
  }

  private void paintCircleHeight(int centerTx, int centerTy, int radius, int delta) {
    int r = Math.max(1, radius);
    int r2 = r * r;

    // Determine biome under cursor (height mask is per-biome template).
    int cx0 = EditorCoord.floorDiv(centerTx, World.CHUNK_SIZE);
    int cy0 = EditorCoord.floorDiv(centerTy, World.CHUNK_SIZE);
    Biome b = (mode == EditorMode.EXAMPLE_MAP)
        ? slotGrid.get(SlotGrid.slotX(cx0), SlotGrid.slotY(cy0))
        : selectedBiome;
    if (b == null) return;

    Pixmap pm = getOrCreateHeightPixmap(b);
    if (pm == null) return;

    int lx0 = EditorCoord.mod(centerTx, World.CHUNK_SIZE);
    int ly0 = EditorCoord.mod(centerTy, World.CHUNK_SIZE);

    // Flatten: pick target from height mask on first touch of a stroke.
    if (heightFlatten && heightFlattenTarget < 0) {
      heightFlattenTarget = HeightMaskCodec.get(pm, lx0, ly0);
    }

    for (int dy = -r; dy <= r; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        if (dx * dx + dy * dy > r2) continue;
        int lx = lx0 + dx;
        int ly = ly0 + dy;
        if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) continue;

        int h = HeightMaskCodec.get(pm, lx, ly);
        if (heightFlatten) {
          h = heightFlattenTarget;
        } else {
          h = h + delta;
        }
        if (h < 0) h = 0;
        if (h > 15) h = 15;
        HeightMaskCodec.set(pm, lx, ly, h);
        heightDirty.add(b.name());

        // Apply to all chunks using this biome (template repeats).
        for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
          for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
            Biome cb = (mode == EditorMode.EXAMPLE_MAP)
                ? slotGrid.get(SlotGrid.slotX(cx), SlotGrid.slotY(cy))
                : selectedBiome;
            if (cb != b) continue;
            Chunk c = previewWorld.chunk(cx, cy);
            if (c == null || c.layers == null || c.layers.heightLevel == null) continue;
            int idx = lx + ly * World.CHUNK_SIZE;
            c.layers.heightLevel[idx] = (byte) h;
          }
        }

        // Mark dirty for stroke tracking / HUD
        int tx = (cx0 * World.CHUNK_SIZE) + lx;
        int ty = (cy0 * World.CHUNK_SIZE) + ly;
        markDirty(tx, ty);
      }
    }
  }

  private void paintCircleZone(Biome biome, int centerTx, int centerTy, int radius, boolean erase) {
    if (biome == null) return;
    String bKey = biome.name();
    String zName = (tool.activeZone == null || tool.activeZone.isBlank()) ? "zone" : tool.activeZone.trim();

    Pixmap pm = getOrCreateZonePixmap(bKey, zName);
    if (pm == null) return;

    // Paint in local chunk coordinates (0..63) because zone masks are biome templates.
    int lx0 = EditorCoord.mod(centerTx, World.CHUNK_SIZE);
    int ly0 = EditorCoord.mod(centerTy, World.CHUNK_SIZE);

    int r = Math.max(1, radius);
    int r2 = r * r;
    for (int dy = -r; dy <= r; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        if (dx * dx + dy * dy > r2) continue;
        int lx = lx0 + dx;
        int ly = ly0 + dy;
        if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) continue;
        pm.drawPixel(lx, ly, erase ? 0x00000000 : 0xFFFFFFFF);
        // zone masks are templates; no dirty marking for transitions
      }
    }

    // Update texture
    Texture t = getOrCreateZoneTexture(bKey, zName, pm);
    if (t != null) t.draw(pm, 0, 0);
  }

  private void paintCircleDeco(Biome biome, int centerTx, int centerTy, int radius) {
    if (biome == null) return;

    Pixmap pm = getOrCreateDecoPixmap(biome);
    if (pm == null) return;

    int lx0 = EditorCoord.mod(centerTx, World.CHUNK_SIZE);
    int ly0 = EditorCoord.mod(centerTy, World.CHUNK_SIZE);

    int r = Math.max(1, radius);
    int r2 = r * r;

    boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
    boolean lower = tool.erase || ctrl;

    for (int dy = -r; dy <= r; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        if (dx*dx + dy*dy > r2) continue;
        int lx = lx0 + dx;
        int ly = ly0 + dy;
        if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) continue;

        int v = DecoMaskCodec.get(pm, lx, ly);
        v = lower ? (v - decoDelta) : (v + decoDelta);
        if (v < 0) v = 0;
        if (v > 255) v = 255;
        DecoMaskCodec.set(pm, lx, ly, v);
      }
    }

    decoDirty.add(biome.name());

    // Update preview deco immediately by re-scattering deco for the 3x3 chunks.
    try {
      com.yourgame.survival.worldgen.WorldGenContext ctx = new com.yourgame.survival.worldgen.WorldGenContext(1234567L, biomes, com.yourgame.survival.worldgen.WorldGenConfig.loadOrDefault());
      com.yourgame.survival.worldgen.deco.DefaultDecoScatter scatter = new com.yourgame.survival.worldgen.deco.DefaultDecoScatter();
      for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
        for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
          Chunk c = previewWorld.chunk(cx, cy);
          if (c == null || c.layers == null) continue;
          scatter.scatter(c.layers, cx, cy, ctx);
        }
      }
    } catch (Throwable ignored) {}
  }

  private void placeObject(Biome biome, int centerTx, int centerTy, boolean erase) {
    if (biome == null) return;

    // Must have an active zone and it must exist.
    String zoneName = (tool.activeZone == null) ? "" : tool.activeZone.trim();
    if (zoneName.isBlank()) return;

    String bk = biome.name();
    java.util.HashMap<String, Pixmap> bm = zonePix.get(bk);
    if (bm == null || !bm.containsKey(zoneName)) return;

    // Placement is only allowed when inside zone mask.
    float lx = EditorCoord.mod(centerTx, World.CHUNK_SIZE) + 0.5f;
    float ly = EditorCoord.mod(centerTy, World.CHUNK_SIZE) + 0.5f;
    Pixmap pm = bm.get(zoneName);
    int ix = MathUtils.clamp((int) Math.floor(lx), 0, World.CHUNK_SIZE - 1);
    int iy = MathUtils.clamp((int) Math.floor(ly), 0, World.CHUNK_SIZE - 1);
    int a = pm.getPixel(ix, iy) & 0xFF;
    if (a == 0) return;

    java.util.ArrayList<PlacedObject> list = placedObjectsByBiome.computeIfAbsent(bk, k -> new java.util.ArrayList<>());

    if (erase) {
      // Remove nearest object of any type within brush radius.
      float r = Math.max(1f, tool.radius) * 1.0f;
      float bestD2 = r * r;
      int best = -1;
      for (int i = 0; i < list.size(); i++) {
        PlacedObject o = list.get(i);
        if (o == null) continue;
        if (!zoneName.equalsIgnoreCase(o.zoneName)) continue;
        float dx = o.lx - lx;
        float dy = o.ly - ly;
        float d2 = dx*dx + dy*dy;
        if (d2 <= bestD2) { bestD2 = d2; best = i; }
      }
      if (best >= 0) list.remove(best);
      return;
    }

    // Place new object
    PlacedObject o = new PlacedObject();
    o.biomeName = bk;
    o.zoneName = zoneName;
    o.type = activeSpawnType;

    // free placement with small deterministic jitter inside the tile
    float jx = (MathUtils.random() - 0.5f) * 0.6f;
    float jy = (MathUtils.random() - 0.5f) * 0.6f;
    o.lx = MathUtils.clamp(lx + jx, 0.1f, World.CHUNK_SIZE - 0.1f);
    o.ly = MathUtils.clamp(ly + jy, 0.1f, World.CHUNK_SIZE - 0.1f);

    list.add(o);
  }

  private Pixmap getOrCreateZonePixmap(String biomeKey, String zoneName) {
    java.util.HashMap<String, Pixmap> bm = zonePix.computeIfAbsent(biomeKey, k -> new java.util.HashMap<>());
    Pixmap pm = bm.get(zoneName);
    if (pm != null) return pm;

    pm = new Pixmap(World.CHUNK_SIZE, World.CHUNK_SIZE, Pixmap.Format.RGBA8888);
    pm.setColor(0f, 0f, 0f, 0f);
    pm.fill();
    bm.put(zoneName, pm);
    return pm;
  }

  private Texture getOrCreateZoneTexture(String biomeKey, String zoneName, Pixmap pm) {
    java.util.HashMap<String, Texture> bt = zoneTex.computeIfAbsent(biomeKey, k -> new java.util.HashMap<>());
    Texture t = bt.get(zoneName);
    if (t != null) return t;

    if (pm == null) return null;
    t = new Texture(pm);
    t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    bt.put(zoneName, t);
    return t;
  }

  private void deriveAndSave() {
    // Save pipeline: (1) backup biomes.json, (2) write zone PNGs, (3) persist rules into biomes.json, (4) atomic write.
    if (biomes == null) return;

    // 1) backup biomes.json
    EditorSaveUtil.backupLocalIfExists(com.yourgame.survival.biome.BiomeSystem.LOCAL_PATH);

    // 2) write zone masks to png + update biome defs
    for (var eBiome : zonePix.entrySet()) {
      String biomeName = eBiome.getKey();
      if (biomeName == null) continue;
      com.yourgame.survival.world.Biome b = null;
      try { b = com.yourgame.survival.world.Biome.valueOf(biomeName); } catch (Throwable ignored) {}
      if (b == null) continue;

      var zones = eBiome.getValue();
      if (zones == null) continue;
      com.yourgame.survival.biome.BiomeSystem.BiomeDef d = biomes.def(b);

      for (var ez : zones.entrySet()) {
        String zoneName = ez.getKey();
        Pixmap pm = ez.getValue();
        if (zoneName == null || pm == null) continue;

        // Write PNG (and backup old file if any)
        String file = com.yourgame.survival.biome.BiomeSystem.writeZoneMaskPng(b, zoneName, pm);

        com.yourgame.survival.biome.BiomeSystem.ZoneMaskDef zdef = d.findZoneMask(zoneName);
        if (zdef == null) {
          zdef = new com.yourgame.survival.biome.BiomeSystem.ZoneMaskDef(zoneName, file);
          d.zoneMasks.add(zdef);
        } else {
          zdef.file = file;
        }
      }
    }

    // 3) write height masks + update biome defs (only dirty biomes)
    for (String biomeName : new java.util.ArrayList<>(heightDirty)) {
      if (biomeName == null || biomeName.isBlank()) continue;
      Pixmap pm = heightPix.get(biomeName);
      if (pm == null) continue;

      com.yourgame.survival.world.Biome b = null;
      try { b = com.yourgame.survival.world.Biome.valueOf(biomeName); } catch (Throwable ignored) {}
      if (b == null) continue;

      String file = com.yourgame.survival.biome.BiomeSystem.HEIGHTMASK_DIR + "/" + b.name().toLowerCase() + "_height.png";
      EditorSaveUtil.backupLocalIfExists(file);
      try {
        EditorSaveUtil.atomicWritePngLocal(file, pm);
      } catch (Throwable ignored) {
        // last resort
        try { com.yourgame.survival.biome.BiomeSystem.writeHeightMaskPng(b, pm); } catch (Throwable ignored2) {}
      }

      com.yourgame.survival.biome.BiomeSystem.BiomeDef d = biomes.def(b);
      d.heightMaskFile = file;
    }

    // 3b) write deco masks + update biome defs (only dirty biomes)
    for (String biomeName : new java.util.ArrayList<>(decoDirty)) {
      if (biomeName == null || biomeName.isBlank()) continue;
      Pixmap pm = decoPix.get(biomeName);
      if (pm == null) continue;

      com.yourgame.survival.world.Biome b = null;
      try { b = com.yourgame.survival.world.Biome.valueOf(biomeName); } catch (Throwable ignored) {}
      if (b == null) continue;

      String file = com.yourgame.survival.biome.BiomeSystem.DECOMASK_DIR + "/" + b.name().toLowerCase() + "_deco.png";
      EditorSaveUtil.backupLocalIfExists(file);
      try {
        EditorSaveUtil.atomicWritePngLocal(file, pm);
      } catch (Throwable ignored) {
        try { com.yourgame.survival.biome.BiomeSystem.writeDecoMaskPng(b, pm); } catch (Throwable ignored2) {}
      }

      com.yourgame.survival.biome.BiomeSystem.BiomeDef d = biomes.def(b);
      d.decoMaskFile = file;
    }

    // 4) persist zone height rules per biome (NOT global)
    for (com.yourgame.survival.world.Biome b : com.yourgame.survival.world.Biome.values()) {
      String key = b.name();
      java.util.ArrayList<ZoneHeightRule> rules = zoneHeightRulesByBiome.get(key);
      if (rules == null) continue; // do not touch other biomes

      com.yourgame.survival.biome.BiomeSystem.BiomeDef d = biomes.def(b);
      d.zoneHeightRules.clear();
      for (int i = 0; i < rules.size(); i++) {
        ZoneHeightRule r = rules.get(i);
        if (r == null || r.zoneName == null || r.zoneName.isBlank()) continue;
        d.zoneHeightRules.add(new com.yourgame.survival.biome.BiomeSystem.ZoneHeightRuleDef(r.zoneName.trim(), r.delta));
      }
    }

    // 4b) persist node stamp rules + encounter stamp rules per biome
    for (com.yourgame.survival.world.Biome b : com.yourgame.survival.world.Biome.values()) {
      String bk = b.name();
      com.yourgame.survival.biome.BiomeSystem.BiomeDef d = biomes.def(b);

      var nr = nodeRulesByBiome.get(bk);
      if (nr != null) {
        d.nodeStampRules.clear();
        for (var v : nr.values()) if (v != null) d.nodeStampRules.add(v);
      }

      var er = encounterRulesByBiome.get(bk);
      if (er != null) {
        d.encounterStampRules.clear();
        for (var v : er.values()) if (v != null) d.encounterStampRules.add(v);
      }
    }

    // 5) atomic write biomes.json
    try {
      EditorSaveUtil.atomicWriteStringLocal(com.yourgame.survival.biome.BiomeSystem.LOCAL_PATH, biomes.toJson());
      heightDirty.clear();
      decoDirty.clear();
    } catch (Throwable ignored) {
      // last resort (non-atomic)
      try { biomes.save(); heightDirty.clear(); decoDirty.clear(); } catch (Throwable ignored2) {}
    }
  }

  private Pixmap getOrCreateDecoPixmap(Biome biome) {
    if (biome == null) return null;
    String key = biome.name();
    Pixmap cached = decoPix.get(key);
    if (cached != null) return cached;

    Pixmap pm = null;
    try {
      if (biomes != null) {
        String file = biomes.def(biome).decoMaskFile;
        if (file != null && !file.isBlank()) {
          com.badlogic.gdx.files.FileHandle fh = Gdx.files.local(file);
          if (fh.exists()) pm = new Pixmap(fh);
        }
      }
    } catch (Throwable ignored) {}

    if (pm == null) {
      pm = new Pixmap(World.CHUNK_SIZE, World.CHUNK_SIZE, Pixmap.Format.RGBA8888);
      DecoMaskCodec.clear(pm); // default=255 everywhere
    }

    decoPix.put(key, pm);
    return pm;
  }

  private Pixmap getOrCreateHeightPixmap(Biome biome) {
    if (biome == null) return null;
    String key = biome.name();
    Pixmap cached = heightPix.get(key);
    if (cached != null) return cached;

    // Try load from biomes.json reference
    Pixmap pm = null;
    try {
      if (biomes != null) {
        String file = biomes.def(biome).heightMaskFile;
        if (file != null && !file.isBlank()) {
          com.badlogic.gdx.files.FileHandle fh = Gdx.files.local(file);
          if (fh.exists()) pm = new Pixmap(fh);
        }
      }
    } catch (Throwable ignored) {}

    if (pm == null) {
      pm = new Pixmap(World.CHUNK_SIZE, World.CHUNK_SIZE, Pixmap.Format.RGBA8888);
      HeightMaskCodec.clear(pm);
    }

    heightPix.put(key, pm);
    return pm;
  }

  private void applyAllZoneHeightRules() {
    if (previewWorld == null) return;

    // 1) Baseline from height masks (per-biome template)
    for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
      for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
        Chunk c = previewWorld.chunk(cx, cy);
        if (c == null || c.layers == null || c.layers.heightLevel == null) continue;

        Biome b = (mode == EditorMode.EXAMPLE_MAP)
            ? slotGrid.get(SlotGrid.slotX(cx), SlotGrid.slotY(cy))
            : selectedBiome;
        Pixmap pm = getOrCreateHeightPixmap(b);

        for (int ly = 0; ly < World.CHUNK_SIZE; ly++) {
          for (int lx = 0; lx < World.CHUNK_SIZE; lx++) {
            int idx = lx + ly * World.CHUNK_SIZE;
            int gid = c.layers.groundId[idx] & 0xFF;
            boolean allow = (gid == TileIds.GROUND_GRASS
                || gid == TileIds.GROUND_DIRT
                || gid == TileIds.GROUND_ROCK
                || gid == TileIds.GROUND_SNOW);
            int h = allow ? HeightMaskCodec.get(pm, lx, ly) : 0;
            c.layers.heightLevel[idx] = (byte) h;
          }
        }
      }
    }

    // 2) Apply zone-based rules additively (per biome)
    for (var e : zoneHeightRulesByBiome.entrySet()) {
      String biomeName = e.getKey();
      java.util.ArrayList<ZoneHeightRule> rules = e.getValue();
      if (biomeName == null || rules == null) continue;

      com.yourgame.survival.world.Biome b = null;
      try { b = com.yourgame.survival.world.Biome.valueOf(biomeName); } catch (Throwable ignored) {}
      if (b == null) continue;

      for (int i = 0; i < rules.size(); i++) {
        ZoneHeightRule r = rules.get(i);
        if (r == null) continue;
        ZoneHeightApplier.apply(previewWorld, mode, selectedBiome, slotGrid, zonePix, r, b);
      }
    }
  }

  private void drawZoneOverlays() {
    float wChunk = World.CHUNK_SIZE * World.TILE_WORLD;

    // Always show all existing zones for the biome used by each chunk.
    for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
      for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
        Biome b = (mode == EditorMode.EXAMPLE_MAP)
            ? slotGrid.get(SlotGrid.slotX(cx), SlotGrid.slotY(cy))
            : selectedBiome;
        if (b == null) continue;

        String bKey = b.name();
        java.util.HashMap<String, Pixmap> bm = zonePix.get(bKey);
        if (bm == null || bm.isEmpty()) continue;

        for (var ez : bm.entrySet()) {
          String zName = ez.getKey();
          Pixmap pm = ez.getValue();
          if (zName == null || pm == null) continue;

          Texture t = getOrCreateZoneTexture(bKey, zName, pm);
          if (t == null) continue;

          // Deterministic color per zoneName; active zone is highlighted.
          float h = (zName.hashCode() & 0xFFFF) / 65535f;
          float r = 0.35f + 0.45f * (float) Math.abs(Math.sin(h * 6.283185f));
          float g = 0.35f + 0.45f * (float) Math.abs(Math.sin((h + 0.33f) * 6.283185f));
          float bb = 0.35f + 0.45f * (float) Math.abs(Math.sin((h + 0.66f) * 6.283185f));

          boolean isActive = (tool.activeZone != null && !tool.activeZone.isBlank() && zName.equalsIgnoreCase(tool.activeZone.trim()));
          float a = isActive ? 0.35f : 0.18f;

          batch.setColor(r, g, bb, a);
          batch.draw(t, cx * wChunk, cy * wChunk, wChunk, wChunk);
          batch.setColor(1f, 1f, 1f, 1f);
        }
      }
    }
  }

  private void drawPlacedObjects() {
    if (previewWorld == null) return;

    float wChunk = World.CHUNK_SIZE * World.TILE_WORLD;

    shapes.setProjectionMatrix(cam.combined);
    shapes.begin(ShapeRenderer.ShapeType.Filled);

    for (int cy = EditorConstants.MIN_C; cy <= EditorConstants.MAX_C; cy++) {
      for (int cx = EditorConstants.MIN_C; cx <= EditorConstants.MAX_C; cx++) {
        Biome b = (mode == EditorMode.EXAMPLE_MAP)
            ? slotGrid.get(SlotGrid.slotX(cx), SlotGrid.slotY(cy))
            : selectedBiome;
        if (b == null) continue;

        String bk = b.name();
        java.util.ArrayList<PlacedObject> list = placedObjectsByBiome.get(bk);
        if (list == null || list.isEmpty()) continue;

        float baseX = cx * wChunk;
        float baseY = cy * wChunk;

        for (int i = 0; i < list.size(); i++) {
          PlacedObject o = list.get(i);
          if (o == null || o.type == null) continue;

          var def = SpawnTypeRegistry.def(o.type);
          float rr = 0.9f, gg = 0.9f, bb = 0.9f;
          if (def != null) {
            switch (def.category) {
              case ENEMY -> { rr = 1.0f; gg = 0.25f; bb = 0.25f; }
              case ANIMAL -> { rr = 1.0f; gg = 0.9f; bb = 0.3f; }
              case NODE -> { rr = 0.3f; gg = 1.0f; bb = 0.3f; }
              case DECO -> { rr = 0.4f; gg = 0.7f; bb = 1.0f; }
              case POI, BUILD -> { rr = 0.9f; gg = 0.6f; bb = 1.0f; }
              case ITEM -> { rr = 0.9f; gg = 0.9f; bb = 0.9f; }
              default -> { rr = 0.95f; gg = 0.95f; bb = 0.95f; }
            }
          }

          float wx = baseX + o.lx * World.TILE_WORLD;
          float wy = baseY + o.ly * World.TILE_WORLD;
          shapes.setColor(rr, gg, bb, 0.95f);
          shapes.circle(wx, wy, World.TILE_WORLD * 0.18f, 10);
        }
      }
    }

    shapes.end();
  }

  private static void recomputeRoadMask4(com.yourgame.survival.world.TileLayers l) {
    int s = l.size;
    for (int y = 0; y < s; y++) {
      for (int x = 0; x < s; x++) {
        int idx = x + y * s;
        boolean nR = (y < s - 1) && l.roadMask[idx + s] != 0;
        boolean eR = (x < s - 1) && l.roadMask[idx + 1] != 0;
        boolean sR = (y > 0) && l.roadMask[idx - s] != 0;
        boolean wR = (x > 0) && l.roadMask[idx - 1] != 0;
        l.roadMask4[idx] = (byte) ((nR ? 1 : 0) | (eR ? 2 : 0) | (sR ? 4 : 0) | (wR ? 8 : 0));
      }
    }
  }

  private void markDirty(int tx, int ty) {
    if (currentStroke != null) currentStroke.includeTile(tx, ty);
  }

  private void paintCircleGround(int centerTx, int centerTy, int radius, short groundId) {
    int r = Math.max(1, radius);
    int r2 = r * r;

    for (int dy = -r; dy <= r; dy++) {
      for (int dx = -r; dx <= r; dx++) {
        if (dx * dx + dy * dy > r2) continue;
        int tx = centerTx + dx;
        int ty = centerTy + dy;
        if (!EditorBounds.isTileInside(tx, ty)) continue;

        int cx = EditorCoord.floorDiv(tx, World.CHUNK_SIZE);
        int cy = EditorCoord.floorDiv(ty, World.CHUNK_SIZE);
        int lx = EditorCoord.mod(tx, World.CHUNK_SIZE);
        int ly = EditorCoord.mod(ty, World.CHUNK_SIZE);

        com.yourgame.survival.world.Chunk c = previewWorld.chunk(cx, cy);
        if (c == null || c.layers == null) continue;
        int idx = lx + ly * World.CHUNK_SIZE;
        c.layers.groundId[idx] = groundId;
        markDirty(tx, ty);

        // Nudge fields to keep visuals coherent in preview.
        if (groundId == com.yourgame.survival.world.TileIds.GROUND_DIRT) {
          c.layers.pathField[idx] = (byte) 255;
          c.layers.vegetation[idx] = 0;
        } else if (groundId == com.yourgame.survival.world.TileIds.GROUND_ROCK) {
          c.layers.rockiness[idx] = (byte) 255;
          c.layers.vegetation[idx] = 0;
        } else if (groundId == com.yourgame.survival.world.TileIds.GROUND_GRASS) {
          if ((c.layers.vegetation[idx] & 0xFF) < 180) c.layers.vegetation[idx] = (byte) 180;
          if ((c.layers.pathField[idx] & 0xFF) > 40) c.layers.pathField[idx] = (byte) 40;
        }
      }
    }
  }

  private final class InputHandler extends InputAdapter {
    private boolean painting = false;

    private boolean isOverUi(int screenX, int screenY) {
      if (stage == null) return false;
      Vector2 st = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
      return stage.hit(st.x, st.y, true) != null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      if (pointer != 0) return false;

      // If the pointer is over UI, do NOT consume the event (Stage must handle it).
      if (isOverUi(screenX, screenY)) {
        painting = false;
        return false;
      }

      if (button == Input.Buttons.MIDDLE) {
        panning = true;
        panLast.set(screenX, screenY);
        return true;
      }
      if (button == Input.Buttons.LEFT) {
        painting = true;
        if (currentStroke == null) currentStroke = new PaintStroke();
        // new stroke => reset flatten target pick
        if (tool.kind == ToolKind.HEIGHT && heightFlatten) heightFlattenTarget = -1;
        return paintAt(screenX, screenY);
      }
      return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
      if (pointer != 0) return false;

      // Never pan/paint through UI.
      if (isOverUi(screenX, screenY)) return false;

      if (panning) {
        Vector3 a = screenToWorld((int) panLast.x, (int) panLast.y);
        Vector3 b = screenToWorld(screenX, screenY);
        cam.position.add(b.x - a.x, b.y - a.y, 0f);
        panLast.set(screenX, screenY);
        return true;
      }

      // Drag paint only if this drag started as a paint stroke.
      if (painting && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
        return paintAt(screenX, screenY);
      }

      return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      if (pointer != 0) return false;
      if (button == Input.Buttons.MIDDLE) {
        panning = false;
        return true;
      }

      if (button == Input.Buttons.LEFT) {
        painting = false;
        // Finish stroke: rebake transitions in a small expanded rect.
        if (currentStroke != null && !currentStroke.isEmpty() && transitionBaker != null) {
          int pad = 2;
          transitionBaker.rebakeRectTiles(
              currentStroke.minTx - pad,
              currentStroke.minTy - pad,
              currentStroke.maxTx + pad,
              currentStroke.maxTy + pad);
        }
        currentStroke = null;
        heightFlattenTarget = -1;
        return true;
      }

      return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
      // ignore scroll over UI
      int mx = Gdx.input.getX();
      int my = Gdx.input.getY();
      if (isOverUi(mx, my)) return false;

      Vector3 before = screenToWorld(mx, my);
      float z = cam.zoom;
      z *= (1f + amountY * 0.10f);
      z = Math.max(0.20f, Math.min(2.80f, z));
      cam.zoom = z;
      cam.update();
      viewport.apply(false);
      Vector3 after = screenToWorld(mx, my);
      cam.position.add(before.x - after.x, before.y - after.y, 0f);
      cam.update();
      return true;
    }

    @Override
    public boolean keyDown(int keycode) {
      if (keycode == Input.Keys.F3) {
        hud.enabled = !hud.enabled;
        return true;
      }

      // Quick palette hotkeys (temporary): 1..5 select ground material.
      if (keycode == Input.Keys.NUM_1) { tool.kind = ToolKind.GROUND; tool.groundId = com.yourgame.survival.world.TileIds.GROUND_GRASS; return true; }
      if (keycode == Input.Keys.NUM_2) { tool.kind = ToolKind.GROUND; tool.groundId = com.yourgame.survival.world.TileIds.GROUND_DIRT;  return true; }
      if (keycode == Input.Keys.NUM_3) { tool.kind = ToolKind.GROUND; tool.groundId = com.yourgame.survival.world.TileIds.GROUND_SAND;  return true; }
      if (keycode == Input.Keys.NUM_4) { tool.kind = ToolKind.GROUND; tool.groundId = com.yourgame.survival.world.TileIds.GROUND_ROCK;  return true; }
      if (keycode == Input.Keys.NUM_5) { tool.kind = ToolKind.GROUND; tool.groundId = com.yourgame.survival.world.TileIds.GROUND_SNOW;  return true; }

      return false;
    }
  }
}

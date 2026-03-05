package com.yourgame.survival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.lang.reflect.Field;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.render.TilesetConfig;
import com.yourgame.survival.tools.asseteditor.scan.ProjectRoot;
import com.yourgame.survival.tools.asseteditor.ui.MiniSkin;
import com.yourgame.survival.tools.asseteditor.ui.PreviewCanvas;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;

/**
 * Tile Editor (ground tiles, schema-driven).
 *
 * Goal (Request 4): pull TileEditor UI/workflow closer to AssetEditor.
 * - Two PreviewCanvas (IST + Candidate)
 * - Candidate folder workflow (path -> scan -> list -> preview -> stage)
 * - Stage/Commit + Status/Log
 * - 2-click arming for Save/Delete
 * - Single-tile PNG create/replace/delete under assets/atlas/src
 * - Tile properties (blocked/water/lava + dynamic boolean toggles)
 *
 * Note (V0.073): Pair-based transition editing was removed.
 * World transitions are rendered via overlap-only edge_<material> atlas overlays.
 */
public final class TileEditorScreen extends ScreenAdapter {

  /**
   * SelectBox whose popup is clamped to the visible stage bounds (prevents dropdown drifting off-screen).
   * Uses reflection because libGDX keeps the popup list private and names differ across versions.
   */
  private static final class ClampSelectBox<T> extends SelectBox<T> {
    ClampSelectBox(Skin skin) { super(skin); }

    @SuppressWarnings("deprecation")
    @Override public void showList() {
      super.showList();
      try {
        Object listObj = null;
        // libGDX field names vary: try common ones.
        for (String fn : new String[]{"selectBoxList", "list"}) {
          try {
            Field f = SelectBox.class.getDeclaredField(fn);
            f.setAccessible(true);
            listObj = f.get(this);
            if (listObj != null) break;
          } catch (Throwable ignored) {}
        }
        if (listObj == null) return;
        if (!(listObj instanceof Actor)) return;

        Stage st = getStage();
        if (st == null) return;
        float sw = st.getViewport().getWorldWidth();
        float sh = st.getViewport().getWorldHeight();

        Actor popup = (Actor) listObj;

        // Clamp width to stage.
        float w = Math.min(Math.max(getWidth(), popup.getWidth()), sw);
        popup.setWidth(w);

        // Clamp position to stage.
        float x = popup.getX();
        float y = popup.getY();
        if (x < 0) x = 0;
        if (x + w > sw) x = sw - w;
        if (y < 0) y = 0;
        if (y + popup.getHeight() > sh) y = sh - popup.getHeight();
        popup.setPosition(x, y);
      } catch (Throwable ignored) {
        // best-effort only
      }
    }
  }

  private final SurvivalGame game;
  private final Screen returnScreen;

  private Stage stage;
  private Skin skin;

  // UI: left list
  private com.badlogic.gdx.scenes.scene2d.ui.List<String> tileList;
  private ScrollPane tileScroll;

  // UI: mapping
  private TextField idField;
  private TextField nameField;
  private SelectBox<String> sourceBox;
  private TextField colField;
  private TextField rowField;
  private TextField regionField;
  private TextField indexField;
  private SelectBox<String> rotBox;

  // UI: mask viz (kept as a stable left-panel element; unused for ground-only editor)
  private Label maskInfoLabel;
  private Table maskViz;
  private Label mvNW, mvN, mvNE, mvW, mvC, mvE, mvSW, mvS, mvSE;

  // UI: properties
  private CheckBox blockedBox;
  private CheckBox waterBox;
  private CheckBox lavaBox;
  private Table dynFlagsTable;
  private TextField addFlagField;

  // UI: non-boolean characteristics
  private TextField frictionField;
  private TextField speedMulField;
  private TextField footstepSfxField;
  private TextField dpsField;

  // UI: candidate
  private TextField candidateFolderPath;
  private TextField candidateSearch;
  private com.badlogic.gdx.scenes.scene2d.ui.List<String> candidateList;
  private ScrollPane candidateScroll;
  private Label candidatePickedLabel;
  private File candidateRootDir;
  private final ArrayList<File> candidateFiles = new ArrayList<>();
  private File stagedCandidate;

  // UI: preview
  private PreviewCanvas preview;
  private PreviewCanvas candidatePreview;
  private DragAndDrop dnd;

  // UI: status/log
  private Label statusLabel;
  private Label logLabel;
  private ScrollPane logScroll;

  // 2-click arming
  private double saveArmedUntil = 0.0;
  private double deleteArmedUntil = 0.0;

  // data
  private TilesetConfig cfg;

  private int selectedId = 0;

  // Edge editor (overlap-only edge_<material>_<mask> tiles)
  private enum EditTarget { GROUND, EDGE }
  private EditTarget editTarget = EditTarget.GROUND;
  private SelectBox<String> targetBox;  // GROUND | EDGE
  private SelectBox<String> edgeMatBox; // GRASS|DIRT|SAND|ROCK|SNOW
  private SelectBox<String> edgeMaskBox; // 0..15

  // Edge grids (candidate + map preview)
  private static final String[] EDGE_MATS = { "GRASS", "DIRT", "SAND", "ROCK", "SNOW" };
  private SelectBox<String> candidateMatBox;
  private SelectBox<String> mapMatBox;
  private EdgeGridCanvas candidateGrid;
  private MapPreviewCanvas mapGrid;
  public TileEditorScreen(SurvivalGame game) {
    this(game, new MenuScreen(game));
  }

  public TileEditorScreen(SurvivalGame game, Screen returnScreen) {
    this.game = game;
    this.returnScreen = returnScreen;
  }

  @Override
  public void show() {
    skin = MiniSkin.build();
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    cfg = TilesetConfig.tryLoad();
    if (cfg == null) cfg = new TilesetConfig();

    buildUi();
    refreshTileList();
    selectTile(0);
    refreshEdgeGrids();

    // Candidate default: static atlas source folder
    try {
      File root = ProjectRoot.find();
      File def = new File(root, "assets/atlas/src_static");
      candidateFolderPath.setText(def.getAbsolutePath());
      setCandidateFolder(def);
    } catch (Throwable ignored) {
      // keep empty
    }
  }

  private void buildUi() {
    Table root = new Table();
    root.setFillParent(true);
    root.defaults().pad(6);
    stage.addActor(root);

    // LEFT: tiles
    Table left = new Table(skin);
    left.defaults().pad(4);
    left.add(new Label("Tiles", skin)).left().row();

    // Ground-only (kept for layout compatibility)
    ClampSelectBox<String> modeBox = new ClampSelectBox<>(skin);
    modeBox.setItems("GROUND");
    modeBox.setSelected("GROUND");
    modeBox.setMaxListCount(1);
    left.add(modeBox).growX().row();
    // Edit target selector (ground tile vs edge overlay tile)
    targetBox = new ClampSelectBox<>(skin);
    targetBox.setItems("GROUND", "EDGE");
    targetBox.setSelected("GROUND");
    targetBox.setMaxListCount(2);
    left.add(new Label("Edit Target", skin)).left().row();
    left.add(targetBox).growX().row();

    edgeMatBox = new ClampSelectBox<>(skin);
    edgeMatBox.setItems("GRASS", "DIRT", "SAND", "ROCK", "SNOW");
    edgeMatBox.setSelected("GRASS");
    edgeMatBox.setMaxListCount(5);

    edgeMaskBox = new ClampSelectBox<>(skin);
    edgeMaskBox.setItems(
      "0","1","2","3","4","5",
      "6","7","8","9","10","11",
      "12","13","14","15","16","17",
      "18","19","20","21","22","23",
      "24","25"
    );
    edgeMaskBox.setSelected("0");
    edgeMaskBox.setMaxListCount(12);

    left.add(new Label("Edge Material", skin)).left().row();
    left.add(edgeMatBox).growX().row();
    left.add(new Label("Edge Mask (0..25)", skin)).left().row();
    left.add(edgeMaskBox).growX().row();

    // mask visualization (unused in ground-only mode; keep the left panel stable)
    maskInfoLabel = new Label("Mask", skin);
    maskInfoLabel.setEllipsis(true);
    // Keep the left panel stable; show full text via tooltip on hover.
    Label maskTip = new Label("", skin);
    maskTip.setWrap(true);
    Tooltip<Label> maskTooltip = new Tooltip<>(maskTip);
    maskTooltip.setInstant(true);
    maskInfoLabel.addListener(maskTooltip);

    left.add(maskInfoLabel).width(260).maxWidth(260).left().row();

    maskViz = new Table(skin);
    maskViz.defaults().pad(1);
    mvNW = new Label(" ", skin);
    mvN  = new Label(" ", skin);
    mvNE = new Label(" ", skin);
    mvW  = new Label(" ", skin);
    mvC  = new Label(" ", skin);
    mvE  = new Label(" ", skin);
    mvSW = new Label(" ", skin);
    mvS  = new Label(" ", skin);
    mvSE = new Label(" ", skin);

    mvNW.setAlignment(Align.center);
    mvN.setAlignment(Align.center);
    mvNE.setAlignment(Align.center);
    mvW.setAlignment(Align.center);
    mvC.setAlignment(Align.center);
    mvE.setAlignment(Align.center);
    mvSW.setAlignment(Align.center);
    mvS.setAlignment(Align.center);
    mvSE.setAlignment(Align.center);

    maskViz.add(mvNW).minWidth(18);
    maskViz.add(mvN).minWidth(18);
    maskViz.add(mvNE).minWidth(18);
    maskViz.row();
    maskViz.add(mvW).minWidth(18);
    maskViz.add(mvC).minWidth(18);
    maskViz.add(mvE).minWidth(18);
    maskViz.row();
    maskViz.add(mvSW).minWidth(18);
    maskViz.add(mvS).minWidth(18);
    maskViz.add(mvSE).minWidth(18);

    left.add(maskViz).left().row();

    tileList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
    tileScroll = new ScrollPane(tileList, skin);
    tileScroll.setFadeScrollBars(false);
    left.add(tileScroll).width(260).growY().row();

    TextButton btnNewTile = new TextButton("New Tile", skin);
    left.add(btnNewTile).growX().row();

    // RIGHT: previews + candidate controls + edit panel
    Table right = new Table(skin);
    right.defaults().pad(4);

    preview = new PreviewCanvas();
    candidatePreview = new PreviewCanvas();

    float sh = Gdx.graphics.getHeight();
    float previewSize = Math.min(sh * 0.38f, 520f);


    // TOP: single-tile previews (IST + candidate) on the LEFT; grids on the RIGHT.
    Table previewsRow = new Table(skin);
    previewsRow.add(preview).size(previewSize, previewSize).top().padRight(10);
    previewsRow.add(candidatePreview).size(previewSize, previewSize).top();

    // Drag & Drop (like AssetEditor): drag candidate preview onto IST preview to stage it.
    dnd = new DragAndDrop();
    dnd.addSource(new DragAndDrop.Source(candidatePreview) {
      @Override public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        File f = getSelectedCandidateFile();
        if (f == null) return null;
        DragAndDrop.Payload p = new DragAndDrop.Payload();
        p.setObject(f);
        return p;
      }
    });
    dnd.addTarget(new DragAndDrop.Target(preview) {
      @Override public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        return payload != null && payload.getObject() instanceof File;
      }

      @Override public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (payload == null) return;
        Object o = payload.getObject();
        if (o instanceof File f) stageCandidate(f);
      }
    });

    // Edge grids (candidate + map preview)
    candidateGrid = new EdgeGridCanvas(6, 5);
    mapGrid = new MapPreviewCanvas(MAP_MASKS);

    Table gridsRow = new Table(skin);
    gridsRow.add(candidateGrid).size(previewSize, previewSize).top().padRight(10);
    gridsRow.add(mapGrid).size(previewSize, previewSize).top();

    candidateMatBox = new SelectBox<>(skin);
    candidateMatBox.setItems(EDGE_MATS);
    candidateMatBox.setSelected("GRASS");

    mapMatBox = new SelectBox<>(skin);
    mapMatBox.setItems(EDGE_MATS);
    mapMatBox.setSelected("GRASS");

    Table matsRow = new Table(skin);
    matsRow.defaults().pad(3);
    matsRow.add(new Label("Candidate grid material", skin)).left().padRight(6);
    matsRow.add(candidateMatBox).width(170).left();
    matsRow.add().growX();
    matsRow.add(new Label("Map preview material", skin)).right().padRight(6);
    matsRow.add(mapMatBox).width(170).right();

    Table gridsBlock = new Table(skin);
    gridsBlock.defaults().pad(2);
    gridsBlock.add(gridsRow).growX().row();
    gridsBlock.add(matsRow).growX().row();

    Table topRow = new Table(skin);
    topRow.defaults().pad(2);
    topRow.add(previewsRow).top().padRight(14);
    topRow.add(gridsBlock).top();

    right.add(topRow).growX().height(previewSize + 40).row();

    // Candidate workflow controls
    Table cand = new Table(skin);
    cand.defaults().pad(3);

    candidateFolderPath = new TextField("", skin);
    candidateFolderPath.setMessageText("Candidate folder path...");
    TextButton btnSetFolder = new TextButton("Set Folder", skin);
    candidateSearch = new TextField("", skin);
    candidateSearch.setMessageText("search...");
    candidateList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
    candidateScroll = new ScrollPane(candidateList, skin);
    candidateScroll.setFadeScrollBars(false);
    candidatePickedLabel = new Label("Staged: (none)", skin);
    candidatePickedLabel.setWrap(true);

    TextButton btnStage = new TextButton("Stage Candidate", skin);

    Table folderRow = new Table(skin);
    folderRow.add(candidateFolderPath).growX().padRight(6);
    folderRow.add(btnSetFolder).left();
    cand.add(folderRow).growX().row();
    cand.add(candidateSearch).growX().row();
    cand.add(candidateScroll).growX().minHeight(220).row();
    cand.add(btnStage).left().row();
    cand.add(candidatePickedLabel).growX().left().row();

    right.add(cand).growX().row();

    // Edit panel
    Table edit = new Table(skin);
    edit.defaults().pad(3).left();
    edit.add(new Label("Edit", skin)).left().colspan(4).row();

    idField = new TextField("0", skin);
    idField.setDisabled(true);
    nameField = new TextField("", skin);

    sourceBox = new SelectBox<>(skin);
    sourceBox.setItems("TILESHEET", "ATLAS");

    colField = new TextField("0", skin);
    rowField = new TextField("0", skin);
    regionField = new TextField("", skin);
    indexField = new TextField("0", skin);

    rotBox = new SelectBox<>(skin);
    rotBox.setItems("0", "90", "180", "270");

    blockedBox = new CheckBox("blocked", skin);
    waterBox = new CheckBox("water", skin);
    lavaBox = new CheckBox("lava", skin);

    dynFlagsTable = new Table(skin);
    dynFlagsTable.defaults().pad(2).left();

    addFlagField = new TextField("", skin);
    addFlagField.setMessageText("new boolean key");
    TextButton btnAddFlag = new TextButton("Add Toggle", skin);

    // mapping layout
    edit.add(new Label("ID", skin)).width(110);
    edit.add(idField).width(110);
    edit.add(new Label("Name", skin)).width(110);
    edit.add(nameField).growX().row();

    edit.add(new Label("Source", skin)).width(110);
    edit.add(sourceBox).width(170);
    edit.add(new Label("Rotation", skin)).width(110);
    edit.add(rotBox).width(110).row();

    edit.add(new Label("Tilesheet col", skin)).width(110);
    edit.add(colField).width(110);
    edit.add(new Label("Tilesheet row", skin)).width(110);
    edit.add(rowField).width(110).row();

    edit.add(new Label("Atlas region", skin)).width(110);
    edit.add(regionField).growX().colspan(3).row();

    edit.add(new Label("Atlas index", skin)).width(110);
    edit.add(indexField).width(110);
    edit.add().colspan(2).row();

    // properties
    edit.add(new Label("Properties", skin)).left().colspan(4).row();
    Table propsRow = new Table(skin);
    propsRow.defaults().pad(2);
    propsRow.add(blockedBox);
    propsRow.add(waterBox);
    propsRow.add(lavaBox);
    edit.add(propsRow).left().colspan(4).row();

    // characteristics
    frictionField = new TextField("1.0", skin);
    speedMulField = new TextField("1.0", skin);
    footstepSfxField = new TextField("", skin);
    dpsField = new TextField("0.0", skin);

    edit.add(new Label("friction", skin)).width(110);
    edit.add(frictionField).width(110);
    edit.add(new Label("speedMul", skin)).width(110);
    edit.add(speedMulField).width(110).row();

    edit.add(new Label("footstepSfx", skin)).width(110);
    edit.add(footstepSfxField).growX().colspan(3).row();

    edit.add(new Label("damagePerSecond", skin)).width(110);
    edit.add(dpsField).width(110);
    edit.add().colspan(2).row();

    edit.add(new Label("Extra booleans", skin)).left().colspan(4).row();
    edit.add(dynFlagsTable).growX().left().colspan(4).row();

    Table addFlagRow = new Table(skin);
    addFlagRow.add(addFlagField).growX().padRight(6);
    addFlagRow.add(btnAddFlag).left();
    edit.add(addFlagRow).growX().colspan(4).row();

    // actions
    TextButton btnApply = new TextButton("Apply (to preview)", skin);
    TextButton btnSave = new TextButton("Save tileset.json", skin);
    TextButton btnCreate = new TextButton("Create PNG", skin);
    TextButton btnReplace = new TextButton("Replace PNG", skin);
    TextButton btnDelete = new TextButton("Delete PNG", skin);
    TextButton btnRotate90 = new TextButton("Rotate90", skin);
    TextButton btnFlipX = new TextButton("FlipX", skin);
    TextButton btnFlipY = new TextButton("FlipY", skin);
    TextButton btnApplyToSrc = new TextButton("Apply-to-SRC", skin);
    TextButton btnBack = new TextButton("Back", skin);

    Table actionGrid = new Table(skin);
    actionGrid.defaults().pad(2);
    actionGrid.add(btnApply).width(220);
    actionGrid.add(btnSave).width(220);
    actionGrid.row();
    actionGrid.add(btnCreate).width(220);
    actionGrid.add(btnReplace).width(220);
    actionGrid.row();
    actionGrid.add(btnDelete).width(220);
    actionGrid.add(btnApplyToSrc).width(220);
    actionGrid.row();
    actionGrid.add(btnRotate90).width(140);
    actionGrid.add(btnFlipX).width(140);
    actionGrid.add(btnFlipY).width(140);
    actionGrid.row();
    actionGrid.add(btnBack).colspan(2).growX();

    edit.add(actionGrid).growX().colspan(4).row();

    // status/log
    statusLabel = new Label("", skin);
    statusLabel.setWrap(true);
    statusLabel.setAlignment(Align.left);

    logLabel = new Label("", skin);
    logLabel.setWrap(true);
    logLabel.setAlignment(Align.left);

    logScroll = new ScrollPane(logLabel, skin);
    logScroll.setFadeScrollBars(false);

    edit.add(statusLabel).growX().colspan(4).row();
    edit.add(logScroll).growX().minHeight(140).colspan(4).row();

    right.add(edit).growX().row();

    root.add(left).width(280).growY();
    root.add(right).grow();

    // --- listeners ---
    ChangeListener gridMatChanged = new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        refreshEdgeGrids();
      }
    };
    if (candidateMatBox != null) candidateMatBox.addListener(gridMatChanged);
    if (mapMatBox != null) mapMatBox.addListener(gridMatChanged);
    ChangeListener edgeSelChanged = new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        editTarget = "EDGE".equalsIgnoreCase(targetBox.getSelected()) ? EditTarget.EDGE : EditTarget.GROUND;
        refreshPreviewFromCfg();
        updateMaskViz();
        refreshEdgeGrids();
        // disable ground mapping controls while editing edge PNGs
        boolean groundUi = (editTarget == EditTarget.GROUND);
        nameField.setDisabled(!groundUi);
        sourceBox.setDisabled(!groundUi);
        colField.setDisabled(!groundUi);
        rowField.setDisabled(!groundUi);
        regionField.setDisabled(!groundUi);
        indexField.setDisabled(!groundUi);
        rotBox.setDisabled(!groundUi);
        blockedBox.setDisabled(!groundUi);
        waterBox.setDisabled(!groundUi);
        lavaBox.setDisabled(!groundUi);
      }
    };
    targetBox.addListener(edgeSelChanged);
    edgeMatBox.addListener(edgeSelChanged);
    edgeMaskBox.addListener(edgeSelChanged);

    tileList.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        int idx = tileList.getSelectedIndex();
        if (idx < 0) return;
        String s = tileList.getSelected();
        int id = parseLeadingInt(s, 0);
        selectTile(id);
      }
    });

    btnNewTile.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        openNewTileDialog();
      }
    });

    btnSetFolder.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        setCandidateFolder(new File(candidateFolderPath.getText()));
      }
    });

    candidateSearch.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        refreshCandidateList();
      }
    });

    candidateList.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        File f = getSelectedCandidateFile();
        if (f != null) {
          loadPixmapInto(candidatePreview, f);
          candidatePreview.setCropEditEnabled(false);
        }
      }
    });

    btnStage.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        File f = getSelectedCandidateFile();
        if (f == null) return;
        stageCandidate(f);
      }
    });

    ChangeListener mappingChanged = new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        // keep UI responsive; do not auto-save
        refreshPreviewFromUi();
      }
    };

    sourceBox.addListener(mappingChanged);
    rotBox.addListener(mappingChanged);
    blockedBox.addListener(mappingChanged);
    waterBox.addListener(mappingChanged);
    lavaBox.addListener(mappingChanged);

    frictionField.addListener(mappingChanged);
    speedMulField.addListener(mappingChanged);
    footstepSfxField.addListener(mappingChanged);
    dpsField.addListener(mappingChanged);

    btnAddFlag.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        String k = addFlagField.getText();
        if (k == null) k = "";
        k = k.trim();
        if (k.isEmpty()) return;
        TilesetConfig.Ground g = currentEntry();
        g.flags.put(k, false);
        addFlagField.setText("");
        rebuildDynFlags();
        setStatus("OK: added toggle '" + k + "'");
      }
    });

    btnApply.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        saveUiToCfg(selectedId);
        refreshPreviewFromCfg();
        setStatus("OK: applied");
      }
    });

    btnSave.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        if (!isSaveArmed()) {
          armSave();
          setStatus("ARMED: click again to SAVE tileset.json");
          return;
        }
        disarmSave();
        saveUiToCfg(selectedId);
        saveCfgToDisk();
        setStatus("OK: tileset.json saved");
      }
    });

    btnCreate.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        doCreateOrReplacePng(false);
      }
    });

    btnReplace.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        doCreateOrReplacePng(true);
      }
    });

    btnDelete.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        if (!isDeleteArmed()) {
          armDelete();
          setStatus("ARMED: click again to DELETE target PNG");
          return;
        }
        disarmDelete();
        doDeleteTargetPng();
      }
    });

    btnRotate90.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        preview.rotate90();
        setStatus("OK: rotate90");
      }
    });

    btnFlipX.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        preview.flipX();
        setStatus("OK: flipX");
      }
    });

    btnFlipY.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        preview.flipY();
        setStatus("OK: flipY");
      }
    });

    btnApplyToSrc.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        applyPreviewTransformToTargetPng();
      }
    });

    btnBack.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        game.setScreen(returnScreen);
      }
    });
  }

  private void setStatus(String s) {
    statusLabel.setText(s == null ? "" : s);
    appendLog(s);
  }

  private void appendLog(String s) {
    if (s == null || s.isBlank()) return;
    String prev = logLabel.getText().toString();
    if (prev.length() > 8000) prev = prev.substring(prev.length() - 8000);
    logLabel.setText(prev + (prev.isEmpty() ? "" : "\n") + s);
    logScroll.layout();
    logScroll.setScrollPercentY(1f);
  }

  private TilesetConfig.Ground currentEntry() {
    return ensureGround(selectedId);
  }

  private void updateMaskViz() {
    if (maskInfoLabel == null || maskViz == null) return;

    if (editTarget != EditTarget.EDGE) {
      maskInfoLabel.setText("Mask");
      mvNW.setText(" "); mvN.setText(" "); mvNE.setText(" ");
      mvW.setText(" ");  mvC.setText(" "); mvE.setText(" ");
      mvSW.setText(" "); mvS.setText(" "); mvSE.setText(" ");
      return;
    }

    int m = clampEdgeMask(parseIntSafe(edgeMaskBox.getSelected(), 0));
    maskInfoLabel.setText("CORNER4 edge_" + edgeMatBox.getSelected() + "  (NW=1 NE=2 SE=4 SW=8)");

    mvC.setText(String.valueOf(m));

    boolean nw = (m & 1) != 0;
    boolean ne = (m & 2) != 0;
    boolean se = (m & 4) != 0;
    boolean sw = (m & 8) != 0;

    mvN.setText(" "); mvE.setText(" "); mvS.setText(" "); mvW.setText(" ");
    mvNW.setText(nw ? "NW" : ".");
    mvNE.setText(ne ? "NE" : ".");
    mvSE.setText(se ? "SE" : ".");
    mvSW.setText(sw ? "SW" : ".");
  }

  private void refreshTileList() {
    ArrayList<String> items = new ArrayList<>();
    for (int id = 0; id < cfg.ground.length; id++) {
      TilesetConfig.Ground g = cfg.ground[id];
      String nm = (g != null && g.name != null && !g.name.isBlank()) ? g.name.trim() : ("ID_" + id);
      items.add(id + " " + nm);
    }
    tileList.setItems(items.toArray(new String[0]));
  }

  private void selectTile(int id) {
    if (id < 0) id = 0;
    if (id >= cfg.ground.length) id = cfg.ground.length - 1;
    selectedId = id;

    // keep list selection synced
    int listIdx = id;
    if (listIdx >= 0 && listIdx < tileList.getItems().size) {
      tileList.setSelectedIndex(listIdx);
    }

    loadUiFromCfg(id);
    refreshPreviewFromCfg();
    updateMaskViz();
  }

  private TilesetConfig.Ground ensureGround(int id) {
    if (id < 0) id = 0;
    cfg.ensureSize(id + 1);
    if (cfg.ground[id] == null) cfg.ground[id] = new TilesetConfig.Ground();
    return cfg.ground[id];
  }

  private void loadUiFromCfg(int id) {
    TilesetConfig.Ground g = currentEntry();

    idField.setText(String.valueOf(selectedId));
    nameField.setText(g.name == null ? "" : g.name);

    sourceBox.setSelected(g.source == TilesetConfig.Source.ATLAS ? "ATLAS" : "TILESHEET");
    colField.setText(String.valueOf(g.col));
    rowField.setText(String.valueOf(g.row));
    regionField.setText(g.region == null ? "" : g.region);
    indexField.setText(String.valueOf(g.index));
    rotBox.setSelected(String.valueOf(TilesetConfig.normalizeRot(g.rotation)));

    blockedBox.setChecked(g.blocked);
    waterBox.setChecked(g.water);
    lavaBox.setChecked(g.lava);

    frictionField.setText(formatFloat(g.friction));
    speedMulField.setText(formatFloat(g.speedMultiplier));
    footstepSfxField.setText(g.footstepSfx == null ? "" : g.footstepSfx);
    dpsField.setText(formatFloat(g.damagePerSecond));

    rebuildDynFlags();
  }

  private void saveUiToCfg(int id) {
    TilesetConfig.Ground g = currentEntry();

    g.name = nameField.getText();

    String src = sourceBox.getSelected();
    g.source = "ATLAS".equalsIgnoreCase(src) ? TilesetConfig.Source.ATLAS : TilesetConfig.Source.TILESHEET;
    g.col = parseIntSafe(colField.getText(), g.col);
    g.row = parseIntSafe(rowField.getText(), g.row);
    g.region = regionField.getText();
    g.index = parseIntSafe(indexField.getText(), g.index);
    g.rotation = TilesetConfig.normalizeRot(parseIntSafe(rotBox.getSelected(), 0));

    g.blocked = blockedBox.isChecked();
    g.water = waterBox.isChecked();
    g.lava = lavaBox.isChecked();

    g.friction = parseFloatSafe(frictionField.getText(), g.friction);
    g.speedMultiplier = parseFloatSafe(speedMulField.getText(), g.speedMultiplier);
    g.footstepSfx = footstepSfxField.getText();
    g.damagePerSecond = parseFloatSafe(dpsField.getText(), g.damagePerSecond);

    // dyn flags are updated live by checkboxes
  }

  private void rebuildDynFlags() {
    dynFlagsTable.clearChildren();

    TilesetConfig.Ground g = currentEntry();

    // show dynamic boolean keys (excluding known ones)
    HashSet<String> skip = new HashSet<>();
    skip.add("source");
    skip.add("col");
    skip.add("row");
    skip.add("region");
    skip.add("index");
    skip.add("rotation");
    skip.add("name");
    skip.add("blocked");
    skip.add("water");
    skip.add("lava");

    ArrayList<String> keys = new ArrayList<>();
    for (String k : g.flags.keySet()) {
      if (k == null) continue;
      if (skip.contains(k)) continue;
      keys.add(k);
    }
    keys.sort(String.CASE_INSENSITIVE_ORDER);

    int col = 0;
    for (String k : keys) {
      final String key = k;
      boolean v = Boolean.TRUE.equals(g.flags.get(key));
      CheckBox cb = new CheckBox(key, skin);
      cb.setChecked(v);
      cb.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, Actor actor) {
          g.flags.put(key, cb.isChecked());
          refreshPreviewFromUi();
        }
      });
      dynFlagsTable.add(cb).left();
      col++;
      if (col >= 3) {
        col = 0;
        dynFlagsTable.row();
      }
    }
  }

  private void refreshPreviewFromUi() {
    saveUiToCfg(selectedId);
    refreshPreviewFromCfg();
  }

  private void refreshPreviewFromCfg() {
    TilesetConfig.Ground g = currentEntry();

    // Preview priority:
    // 1) TILESHEET cell from tiles.png
    // 2) ATLAS: show src PNG (assets/atlas/src/<region>_<index>.png) if present
    if (g.source == TilesetConfig.Source.TILESHEET) {
      try {
        Pixmap sheet = new Pixmap(Gdx.files.internal("tiles.png"));
        int TILE = 32;
        int x0 = g.col * TILE;
        int yTop = g.row * TILE;
        int y0 = sheet.getHeight() - yTop - TILE;

        Pixmap pm = new Pixmap(TILE, TILE, sheet.getFormat());
        for (int y = 0; y < TILE; y++) {
          for (int x = 0; x < TILE; x++) {
            pm.drawPixel(x, y, sheet.getPixel(x0 + x, y0 + y));
          }
        }
        sheet.dispose();

        preview.setPixmap(pm);
        preview.fitToContent();
        return;
      } catch (Throwable ignored) {
        // fallback to missing
      }
    }

    File target = (editTarget == EditTarget.EDGE) ? resolveEdgeTargetPng() : resolveTargetPng(g);
    if (target != null && target.exists()) {
      loadPixmapInto(preview, target);
      preview.fitToContent();
    } else {
      preview.setMissing("missing");
    }
  }

  private void saveCfgToDisk() {
    try {
      File root = ProjectRoot.find();
      File out = new File(root, "assets/config/tileset.json");
      FileHandle fh = Gdx.files.absolute(out.getAbsolutePath());
      cfg.save(fh);
    } catch (Throwable ignored) {
      setStatus("ERR: save failed");
    }
  }

  private void doCreateOrReplacePng(boolean replace) {
    TilesetConfig.Ground g = currentEntry();
    if (g.source != TilesetConfig.Source.ATLAS) {
      setStatus("ERR: Create/Replace only valid for ATLAS source");
      return;
    }

    if (stagedCandidate == null || !stagedCandidate.exists()) {
      setStatus("ERR: no staged candidate");
      return;
    }

    File target = (editTarget == EditTarget.EDGE) ? resolveEdgeTargetPng() : resolveTargetPng(g);
    if (target == null) {
      setStatus("ERR: target path invalid");
      return;
    }

    if (!replace && target.exists()) {
      setStatus("ERR: target already exists (use Replace)");
      return;
    }

    if (replace && !target.exists()) {
      setStatus("ERR: target missing (use Create)");
      return;
    }

    try {
      Pixmap src = new Pixmap(Gdx.files.absolute(stagedCandidate.getAbsolutePath()));
      resetCandidateTransformForWrite(src);
      Pixmap out = candidatePreview.applyTransform(src);
      src.dispose();
      out = ensureSize(out, 32, 32);

      FileHandle fh = Gdx.files.absolute(target.getAbsolutePath());
      fh.parent().mkdirs();
      PixmapIO.writePNG(fh, out);
      out.dispose();

      setStatus("OK: wrote " + target.getName());
      refreshPreviewFromCfg();
      refreshEdgeGrids();
    } catch (Throwable t) {
      setStatus("ERR: write failed");
    }
  }

  private void doDeleteTargetPng() {
    TilesetConfig.Ground g = currentEntry();
    File target = (editTarget == EditTarget.EDGE) ? resolveEdgeTargetPng() : resolveTargetPng(g);
    if (target == null || !target.exists()) {
      setStatus("ERR: target missing");
      return;
    }

    try {
      FileHandle fh = Gdx.files.absolute(target.getAbsolutePath());
      fh.delete();
      setStatus("OK: deleted " + target.getName());
      refreshPreviewFromCfg();
      refreshEdgeGrids();
    } catch (Throwable ignored) {
      setStatus("ERR: delete failed");
    }
  }

  private void applyPreviewTransformToTargetPng() {
    TilesetConfig.Ground g = currentEntry();
    File target = (editTarget == EditTarget.EDGE) ? resolveEdgeTargetPng() : resolveTargetPng(g);
    if (target == null || !target.exists()) {
      setStatus("ERR: target missing");
      return;
    }

    try {
      Pixmap src = new Pixmap(Gdx.files.absolute(target.getAbsolutePath()));
      int w = src.getWidth();
      int h = src.getHeight();
      Pixmap out = preview.applyTransform(src);
      src.dispose();
      out = ensureSize(out, w, h);

      FileHandle fh = Gdx.files.absolute(target.getAbsolutePath());
      PixmapIO.writePNG(fh, out);
      out.dispose();

      setStatus("OK: applied transform to SRC");
      refreshPreviewFromCfg();
      refreshEdgeGrids();
    } catch (Throwable ignored) {
      setStatus("ERR: apply-to-src failed");
    }
  }

  private void loadPixmapInto(PreviewCanvas canvas, File f) {
    try {
      Pixmap pm = new Pixmap(Gdx.files.absolute(f.getAbsolutePath()));
      canvas.setPixmap(pm);
      canvas.fitToContent();
    } catch (Throwable ignored) {
      canvas.setMissing("missing");
    }
  }


  // --- EDGE GRID SUPPORT ---

  // Fixed demo layout for the map preview. Values are CORNER4 masks (0..15). 0 means empty.
  // Row-major, deterministic, chosen to show straight edges + corners.
  private static final int[][] MAP_MASKS = {
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 1, 3, 3, 3, 3, 2, 0},
      {0, 9, 15, 15, 15, 15, 6, 0},
      {0, 9, 15, 7, 3, 15, 6, 0},
      {0, 9, 15, 15, 15, 15, 6, 0},
      {0, 8, 12, 12, 12, 12, 4, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
  };

  private static final class EdgeGridCanvas extends Actor {
    private final int cols;
    private final int rows;
    private final Texture[] textures;
    private final TextureRegion[] regions;

    EdgeGridCanvas(int cols, int rows) {
      this.cols = cols;
      this.rows = rows;
      this.textures = new Texture[cols * rows];
      this.regions = new TextureRegion[cols * rows];
    }

    void disposeTextures() {
      for (int i = 0; i < textures.length; i++) {
        if (textures[i] != null) textures[i].dispose();
        textures[i] = null;
        regions[i] = null;
      }
    }

    void setTilePixmap(int idx, Pixmap pm) {
      if (idx < 0 || idx >= textures.length) {
        if (pm != null) pm.dispose();
        return;
      }
      if (textures[idx] != null) textures[idx].dispose();
      textures[idx] = null;
      regions[idx] = null;
      if (pm == null) return;
      Texture t = new Texture(pm);
      pm.dispose();
      textures[idx] = t;
      regions[idx] = new TextureRegion(t);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
      float w = getWidth();
      float h = getHeight();
      float cw = w / cols;
      float ch = h / rows;

      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          int idx = r * cols + c;
          TextureRegion tr = regions[idx];
          if (tr == null) continue;
          float x = getX() + c * cw;
          float y = getY() + (rows - 1 - r) * ch;
          batch.draw(tr, x, y, cw, ch);
        }
      }
    }

    void clearGrid() {
      disposeTextures();
    }

    @Override public boolean remove() {
      disposeTextures();
      return super.remove();
    }
  }

  private static final class MapPreviewCanvas extends Actor {
    private final int[][] masks;
    private final Texture[] textures;
    private final TextureRegion[] regions;

    private static Texture bg1x1;

    MapPreviewCanvas(int[][] masks) {
      this.masks = masks;
      if (bg1x1 == null) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        bg1x1 = new Texture(pm);
        pm.dispose();
      }
      int rows = masks.length;
      int cols = masks[0].length;
      this.textures = new Texture[rows * cols];
      this.regions = new TextureRegion[rows * cols];
    }

    void disposeTextures() {
      for (int i = 0; i < textures.length; i++) {
        if (textures[i] != null) textures[i].dispose();
        textures[i] = null;
        regions[i] = null;
      }
    }

    void setCellPixmap(int x, int y, Pixmap pm) {
      int rows = masks.length;
      int cols = masks[0].length;
      if (x < 0 || y < 0 || x >= cols || y >= rows) {
        if (pm != null) pm.dispose();
        return;
      }
      int idx = y * cols + x;
      if (textures[idx] != null) textures[idx].dispose();
      textures[idx] = null;
      regions[idx] = null;
      if (pm == null) return;
      Texture t = new Texture(pm);
      pm.dispose();
      textures[idx] = t;
      regions[idx] = new TextureRegion(t);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
      int rows = masks.length;
      int cols = masks[0].length;
      float w = getWidth();
      float h = getHeight();
      float cw = w / cols;
      float ch = h / rows;

      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          int idx = y * cols + x;
          float dx = getX() + x * cw;
          float dy = getY() + (rows - 1 - y) * ch;

          // background (water-ish)
          batch.setColor(0.07f, 0.10f, 0.16f, 1f);
          batch.draw(bg1x1, dx, dy, cw, ch);
          batch.setColor(Color.WHITE);

          TextureRegion tr = regions[idx];
          if (tr == null) continue;
          batch.draw(tr, dx, dy, cw, ch);
        }
      }
    }

    void clearGrid() {
      disposeTextures();
    }

    @Override public boolean remove() {
      disposeTextures();
      return super.remove();
    }
  }

  private File resolveEdgePng(String matUpper, int mask) {
    try {
      String mat = (matUpper == null ? "" : matUpper.trim().toLowerCase());
      if (mat.isEmpty()) return null;
      mask = clampEdgeMask(mask);
      File root = ProjectRoot.find();
      File srcRoot = new File(root, "assets/atlas/src_static");
      return new File(srcRoot, "edge_" + mat + "_" + mask + ".png");
    } catch (Throwable ignored) {
      return null;
    }
  }

  private Pixmap tryLoadPixmap(File f) {
    try {
      if (f == null || !f.exists()) return null;
      return new Pixmap(Gdx.files.absolute(f.getAbsolutePath()));
    } catch (Throwable ignored) {
      return null;
    }
  }

  private void refreshEdgeGrids() {
    if (candidateGrid == null || mapGrid == null || candidateMatBox == null || mapMatBox == null) return;

    String candMat = candidateMatBox.getSelected();
    String mapMat = mapMatBox.getSelected();

    // Always show grids (even in GROUND mode). Clear + refill from disk.
    candidateGrid.clearGrid();
    mapGrid.clearGrid();

    // Candidate grid: load base tiles
    for (int m = 0; m <= EDGE_MASK_MAX; m++) {
      File f = resolveEdgePng(candMat, m);
      Pixmap pm = tryLoadPixmap(f);
      candidateGrid.setTilePixmap(m, pm);
    }

    // Overlay staged candidate at current mask (position == masknr) only in EDGE mode.
    int stagedMask = clampEdgeMask(parseIntSafe(edgeMaskBox.getSelected(), 0));
    if (editTarget == EditTarget.EDGE && stagedCandidate != null && stagedCandidate.exists()) {
      try {
        Pixmap out = new Pixmap(Gdx.files.absolute(stagedCandidate.getAbsolutePath()));
        out = ensureSize(out, 32, 32);
        candidateGrid.setTilePixmap(stagedMask, out);
      } catch (Throwable ignored) {
      }
    }

    // Map preview grid
    int rows = MAP_MASKS.length;
    int cols = MAP_MASKS[0].length;
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < cols; x++) {
        int mask = clampEdgeMask(MAP_MASKS[y][x]);
        if (mask == 0) continue;
        File f = resolveEdgePng(mapMat, mask);
        Pixmap pm = tryLoadPixmap(f);
        mapGrid.setCellPixmap(x, y, pm);
      }
    }
  }
  private File resolveTargetPng(TilesetConfig.Ground g) {
    if (g == null) return null;
    String region = (g.region == null ? "" : g.region.trim());
    if (region.isEmpty()) return null;

    try {
      File root = ProjectRoot.find();
      File srcRoot = new File(root, "assets/atlas/src_static");
      if (g.index < 0) return new File(srcRoot, region + ".png");
      return new File(srcRoot, region + "_" + g.index + ".png");
    } catch (Throwable ignored) {
      return null;
    }
  }
  private File resolveEdgeTargetPng() {
    try {
      String mat = edgeMatBox.getSelected();
      if (mat == null) mat = "";
      mat = mat.trim().toLowerCase();
      if (mat.isEmpty()) return null;

      int mask = clampEdgeMask(parseIntSafe(edgeMaskBox.getSelected(), 0));

      File root = ProjectRoot.find();
      File srcRoot = new File(root, "assets/atlas/src_static");
      return new File(srcRoot, "edge_" + mat + "_" + mask + ".png");
    } catch (Throwable ignored) {
      return null;
    }
  }

  private void setCandidateFolder(File dir) {
    try {
      if (dir == null) return;
      if (!dir.exists() || !dir.isDirectory()) {
        setStatus("ERR: folder not found");
        return;
      }
      candidateRootDir = dir;
      stagedCandidate = null;
      candidatePickedLabel.setText("Staged: (none)");
      scanCandidateFiles();
      refreshCandidateList();
      setStatus("OK: candidate folder set");
    } catch (Throwable ignored) {
      setStatus("ERR: set folder failed");
    }
  }

  private void scanCandidateFiles() {
    candidateFiles.clear();

    if (candidateRootDir == null) return;

    File[] arr = candidateRootDir.listFiles((d, name) -> name != null && name.toLowerCase().endsWith(".png"));
    if (arr == null) return;

    Arrays.sort(arr, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
    candidateFiles.addAll(Arrays.asList(arr));
  }

  private void refreshCandidateList() {
    String q = candidateSearch.getText();
    if (q == null) q = "";
    q = q.trim().toLowerCase();

    ArrayList<String> items = new ArrayList<>();
    for (File f : candidateFiles) {
      String n = f.getName();
      if (!q.isEmpty() && !n.toLowerCase().contains(q)) continue;
      items.add(n);
    }

    candidateList.setItems(items.toArray(new String[0]));
    if (items.size() > 0) {
      candidateList.setSelectedIndex(0);
      File f = getSelectedCandidateFile();
      if (f != null) {
        loadPixmapInto(candidatePreview, f);
        candidatePreview.setCropEditEnabled(false);
      }
    } else {
      candidatePreview.setMissing("missing");
    }
  }

  private void stageCandidate(File f) {
    if (f == null || !f.exists()) return;
    stagedCandidate = f;
    candidatePickedLabel.setText("Staged: " + f.getName());
    setStatus("OK: staged candidate");
    refreshPreviewFromCfg();
    refreshEdgeGrids();
  }

  private void resetCandidateTransformForWrite(Pixmap src) {
    if (src == null) return;
    try {
      candidatePreview.setCropEditEnabled(false);
      candidatePreview.setCropRect(0, 0, src.getWidth(), src.getHeight());
    } catch (Throwable ignored) {
    }
  }

  private static final int EDGE_MASK_MIN = 0;
  private static final int EDGE_MASK_MAX = 25;

  private int clampEdgeMask(int m) {
    if (m < EDGE_MASK_MIN) return EDGE_MASK_MIN;
    if (m > EDGE_MASK_MAX) return EDGE_MASK_MAX;
    return m;
  }

  private Pixmap ensureSize(Pixmap pm, int w, int h) {
    if (pm == null) return null;
    if (pm.getWidth() == w && pm.getHeight() == h) return pm;
    Pixmap out = new Pixmap(w, h, Pixmap.Format.RGBA8888);
    out.setBlending(Pixmap.Blending.None);
    out.drawPixmap(pm, 0, 0, pm.getWidth(), pm.getHeight(), 0, 0, w, h);
    pm.dispose();
    return out;
  }

  private File getSelectedCandidateFile() {
    String sel = candidateList.getSelected();
    if (sel == null || sel.isEmpty()) return null;
    if (candidateRootDir == null) return null;
    File f = new File(candidateRootDir, sel);
    return f.exists() ? f : null;
  }

  private void openNewTileDialog() {
    Dialog d = new Dialog("New Tile", skin) {
      @Override protected void result(Object obj) {
        // (unused)
      }
    };

    d.getContentTable().defaults().pad(4);

    TextField idF = new TextField("", skin);
    TextField nmF = new TextField("", skin);
    TextField regF = new TextField("", skin);
    TextField idxF = new TextField("0", skin);

    d.getContentTable().add(new Label("id (0..255)", skin)).left();
    d.getContentTable().add(idF).width(160).row();
    d.getContentTable().add(new Label("name", skin)).left();
    d.getContentTable().add(nmF).width(260).row();
    d.getContentTable().add(new Label("region", skin)).left();
    d.getContentTable().add(regF).width(260).row();
    d.getContentTable().add(new Label("index", skin)).left();
    d.getContentTable().add(idxF).width(160).row();

    d.button("Create", true);
    d.button("Cancel", false);

    d.show(stage);

    for (Actor a : d.getButtonTable().getChildren()) {
      if (a instanceof TextButton tb && "Create".equalsIgnoreCase(tb.getText().toString())) {
        tb.addListener(new ChangeListener() {
          @Override public void changed(ChangeEvent event, Actor actor) {
            int id = parseIntSafe(idF.getText(), -1);
            if (id < 0 || id > TilesetConfig.MAX_ID) {
              setStatus("ERR: id out of range");
              return;
            }

            cfg.ensureSize(id + 1);
            TilesetConfig.Ground g = ensureGround(id);
            g.name = nmF.getText();
            g.source = TilesetConfig.Source.ATLAS;
            g.region = regF.getText();
            g.index = parseIntSafe(idxF.getText(), 0);
            g.rotation = 0;

            refreshTileList();
            selectTile(id);

            if (stagedCandidate != null && stagedCandidate.exists()) {
              doCreateOrReplacePng(false);
            }
          }
        });
      }
    }
  }

  private static int parseLeadingInt(String s, int fallback) {
    try {
      if (s == null) return fallback;
      int sp = s.indexOf(' ');
      String n = (sp > 0 ? s.substring(0, sp) : s);
      return Integer.parseInt(n.trim());
    } catch (Throwable ignored) {
      return fallback;
    }
  }

  private static int parseIntSafe(String s, int fallback) {
    try {
      return Integer.parseInt(s.trim());
    } catch (Throwable t) {
      return fallback;
    }
  }

  private boolean isSaveArmed() {
    return nowSec() < saveArmedUntil;
  }

  private void armSave() {
    saveArmedUntil = nowSec() + 4.0;
  }

  private void disarmSave() {
    saveArmedUntil = 0.0;
  }

  private boolean isDeleteArmed() {
    return nowSec() < deleteArmedUntil;
  }

  private void armDelete() {
    deleteArmedUntil = nowSec() + 4.0;
  }

  private void disarmDelete() {
    deleteArmedUntil = 0.0;
  }

  private static double nowSec() {
    return System.currentTimeMillis() / 1000.0;
  }

  // ---- helpers: float <-> text (used by ground fields) ----
  private static String formatFloat(float v) {
    if (Float.isNaN(v) || Float.isInfinite(v)) return "0";
    return String.format(java.util.Locale.US, "%.3f", v);
  }

  private static float parseFloatSafe(String s, float fallback) {
    if (s == null) return fallback;
    s = s.trim();
    if (s.isEmpty()) return fallback;
    s = s.replace(',', '.');
    try {
      return Float.parseFloat(s);
    } catch (Exception ignored) {
      return fallback;
    }
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    // disarm hints
    if (!isSaveArmed() && saveArmedUntil != 0.0) saveArmedUntil = 0.0;
    if (!isDeleteArmed() && deleteArmedUntil != 0.0) deleteArmedUntil = 0.0;
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    try { if (stage != null) stage.dispose(); } catch (Throwable ignored) {}
    try { if (skin != null) skin.dispose(); } catch (Throwable ignored) {}
  }
}

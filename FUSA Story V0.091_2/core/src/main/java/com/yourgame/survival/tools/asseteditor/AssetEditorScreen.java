package com.yourgame.survival.tools.asseteditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.yourgame.survival.SurvivalGame;
import com.yourgame.survival.render.AtlasLoader;
import com.yourgame.survival.screens.TileEditorScreen;
import com.yourgame.survival.tools.asseteditor.collision.AutoCollision;
import com.yourgame.survival.tools.asseteditor.model.AssetKind;
import com.yourgame.survival.tools.asseteditor.model.CollisionMeta;
import com.yourgame.survival.tools.asseteditor.model.IndexEntry;
import com.yourgame.survival.tools.asseteditor.model.MetaIO;
import com.yourgame.survival.tools.asseteditor.scan.AssetIndexScanner;
import com.yourgame.survival.tools.asseteditor.scan.ProjectRoot;
import com.yourgame.survival.tools.asseteditor.ui.MiniSkin;
import com.yourgame.survival.tools.asseteditor.ui.PreviewCanvas;

import java.io.File;
// Nicht fertiges Feature: unused imports (we use fully-qualified names below)
// import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
// import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
// import java.nio.file.DirectoryStream;
import java.util.Comparator;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.regex.Pattern;
// import java.util.regex.Matcher;

/**
 * Standalone Sprite/Tile editor (desktop). Designed for THIS project's atlas/src structure.
 * - IST index: scanned from code on startup
 * - Collision: auto from silhouette (alpha + optional BG pipette)
 * - Save: 2-click confirm, overwrites sidecar meta next to original
 */
public final class AssetEditorScreen extends ScreenAdapter {
  private final SurvivalGame game;
  private final boolean batchMode;

  // Project root (filesystem) for editor operations. Used only for resolving paths.
  private File projectRoot;
  private Path projectRootPath;

  private Stage stage;
  private Skin skin;
  private TextureAtlas atlas;

  private final Array<IndexEntry> all = new Array<>();
  private final Array<IndexEntry> filtered = new Array<>();

  // Atlas selection (new 3-atlas layout)
  private SelectBox<String> atlasSelect;
  private String atlasBaseName = "static"; // static | living | ui

  private SelectBox<String> kindFilter;
  private SelectBox<String> statusFilter;
  private TextField search;
  private com.badlogic.gdx.scenes.scene2d.ui.List<IndexEntry> list;

  private Label lblInfo;
  private Label lblPath;

  // Visible UI state (candidate/staged)
  private Label lblCandidate;
  private Label lblStaged;

  private PreviewCanvas preview;
  // Candidate preview + drag&drop replace workflow
  private PreviewCanvas candidatePreview;
  private TextButton btnLoadCandidate;
  private java.io.File candidateFile;
  // Candidate folder workflow (folder -> scan -> list -> preview -> stage)
  private java.io.File candidateRootDir;
  private final Array<CandidateFile> candidateAll = new Array<>();
  private final Array<CandidateFile> candidateFiltered = new Array<>();
  private com.badlogic.gdx.scenes.scene2d.ui.List<CandidateFile> candidateList;
  private TextField candidateSearch;
  private TextField candidateFolderPath;
  private Label candidateFolderLabel;
  private Label candidatePickedLabel;
  private java.io.File pendingCandidateFile;
  private java.io.File pendingDropFile;
  private com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop dnd;

  private static final class CandidateFile {
    final java.io.File abs;
    final String rel;
    CandidateFile(java.io.File abs, String rel) {
      this.abs = abs;
      this.rel = rel;
    }
    @Override public String toString() { return rel; }
  }

  private TextButton btnPipette;
  private Slider tolSlider;
  private Label tolLabel;

  private TextButton btnRecompute;
  private TextButton btnSave;
  private TextButton btnExport;
  private TextButton btnReplacePng;
  private TextButton btnDeleteFrame;
  private TextButton btnRepackAtlas;

  // Batch replace by prefix (e.g. vampire_walk)
  private TextField batchPrefixField;
  private TextButton batchReplacePrefixBtn;

  private TextButton btnRotate90;
  private TextButton btnFlipX;
  private TextButton btnFlipY;
  private TextButton btnApplyTransform;

  private CheckBox cropEditToggle;

  private TextField packMaxW;
  private TextField packMaxH;
  private TextField packPadX;
  private TextField packPadY;
  private CheckBox packRotation;
  private TextButton packLoad;
  private TextButton packSave;

  private Label statusLabel;
  private Label logLabel;
  private ScrollPane logScroll;

  private double saveArmedUntil = 0.0;
  private double deleteArmedUntil = 0.0;

  private IndexEntry current;
  // FrameSet for the currently selected atlas key (filled in select(); used by later requests)
  private Array<TextureAtlas.AtlasRegion> currentFrames = new Array<>();
  private int selectedFrameIndex = 0;
  // Preview animation state (editor-only)
  private boolean previewAutoPlay = true;
  private float previewAnimAcc = 0f;
  private final float previewAnimFrameSec = 0.10f; // 10 FPS default
  // Frame-strip UI (built once; content rebuilt per selection)
  private TextButton btnPreviewPlay;
  private Table frameStrip;
  private ScrollPane frameStripScroll;
  private CollisionMeta actual;
  private CollisionMeta draft;

  private final Color bgColor = new Color(0, 0, 0, 0);
  private int bgTol = 12;
  private int alphaTh = 16;

  public AssetEditorScreen(SurvivalGame game, boolean batchMode) {
    this.game = game;
    this.batchMode = batchMode;
  }

  @Override
  public void show() {
    stage = new Stage(new ScreenViewport());
    // UI scale: make everything ~50% larger while keeping layout stable.
    // ScreenViewport supports units-per-pixel scaling, which uniformly scales all Scene2D UI.
    // FIX: applyUiScale() sets unitsPerPixel based on resolution -- no manual override needed.
applyUiScale();
    skin = MiniSkin.build();

    // Resolve project root once. Never rely on hardcoded absolute paths.
    projectRoot = ProjectRoot.find();
    try {
      projectRootPath = projectRoot.toPath().toAbsolutePath().normalize();
    } catch (Throwable t) {
      projectRootPath = null;
    }

    reloadAtlas();
    rebuildIndex();

    // Batch-mode: export and exit (no UI).
    if (batchMode) {
      exportIndexFiles();
      Gdx.app.exit();
      return;
    }

    buildUi();

    // Default candidate folder: atlas source folder (project-relative).
    // This is the primary replacement workflow for sprites.
    try {
      if (candidateFolderPath != null) {
        candidateFolderPath.setText("\\assets\\atlas\\src_" + atlasBaseName);
      }
      onSetCandidateFolder();
    } catch (Throwable ignored) {
      // non-fatal
    }

    // populate packer settings fields on start
    loadPackerSettings();

    Gdx.input.setInputProcessor(stage);

    applyFilter();
    if (filtered.size > 0) {
      list.setSelectedIndex(0);
      select(filtered.get(0));
    }
  }

  /**
   * Formats a filesystem path as project-relative ("\\assets\\...") when possible.
   * Falls back to the input path if it is outside the resolved project root.
   */
  private String relPath(File f) {
    if (f == null) return "";
    try {
      if (projectRootPath != null) {
        Path p = f.toPath().toAbsolutePath().normalize();
        if (p.startsWith(projectRootPath)) {
          String rel = projectRootPath.relativize(p).toString();
          rel = rel.replace('/', '\\');
          if (!rel.startsWith("\\")) rel = "\\" + rel;
          return rel;
        }
      }
    } catch (Throwable ignored) {
    }
    return f.getPath();
  }

  private void buildUi() {
    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    // Left pane
    Table left = new Table();
    left.defaults().pad(4);

    atlasSelect = new SelectBox<>(skin);
    atlasSelect.setItems("STATIC", "LIVING", "UI");
    atlasSelect.setSelected("STATIC");

    kindFilter = new SelectBox<>(skin);
    kindFilter.setItems("ALL", "TILE", "ENTITY", "UI", "OTHER");

    statusFilter = new SelectBox<>(skin);
    statusFilter.setItems("ALL", "IST", "TEMPLATE");
    statusFilter.setSelected("ALL");

    search = new TextField("", skin);
    search.setMessageText("search...");

    list = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
    ScrollPane sp = new ScrollPane(list, skin);

    left.add(new Label("Atlas", skin)).left().row();
    left.add(atlasSelect).growX().row();

    left.add(new Label("IST Index", skin)).left().row();
    left.add(statusFilter).growX().row();
    left.add(kindFilter).growX().row();
    left.add(search).growX().row();
    left.add(sp).grow().row();

    // Right pane
    Table right = new Table();
    right.defaults().pad(4);

    lblInfo = new Label("-", skin);
    lblPath = new Label("-", skin);

    preview = new PreviewCanvas();
    preview.setListener(bg -> {
      bgColor.set(bg);
      recomputeDraft();
    });

    candidatePreview = new PreviewCanvas();
    candidatePreview.setListener(bg -> {}); // no-op

    // Candidate UI (folder-based)
    // IMPORTANT: Do NOT use java.awt.FileDialog here (can hard-crash LWJGL3 on Windows).
    // We use a pasteable folder path field + Set button instead.
    TextButton chooseFolderBtn = new TextButton("Set Folder", skin);
    candidateFolderPath = new TextField("", skin);
    candidateFolderPath.setMessageText("paste folder path here...");
    candidateFolderLabel = new Label("Folder: (none)", skin);
    candidateFolderLabel.setWrap(true);

    candidateSearch = new TextField("", skin);
    candidateSearch.setMessageText("search candidates...");

    candidatePickedLabel = new Label("Candidate: (none)", skin);
    candidatePickedLabel.setWrap(true);

    candidateList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
    ScrollPane candScroll = new ScrollPane(candidateList, skin);
    candScroll.setFadeScrollBars(false);

    TextButton stageCandidateBtn = new TextButton("Stage Candidate -> IST", skin);

    // Backward-compat button (kept to avoid breaking UI wiring); now just guides users.
    btnLoadCandidate = new TextButton("Load Candidate", skin);

    lblCandidate = new Label("Candidate: none", skin);
    lblCandidate.setWrap(true);
    lblStaged = new Label("Staged: none", skin);
    lblStaged.setWrap(true);

    btnPipette = new TextButton("Pipette BG", skin);
    tolSlider = new Slider(0, 64, 1, false, skin);
    tolSlider.setValue(bgTol);
    tolLabel = new Label("tol=" + bgTol, skin);

    btnRecompute = new TextButton("Recompute", skin);
    btnSave = new TextButton("Save", skin);
    btnExport = new TextButton("Export Index", skin);

    btnReplacePng = new TextButton("Replace PNG", skin);
    btnDeleteFrame = new TextButton("Delete Frame", skin);
    btnRepackAtlas = new TextButton("Repack Atlas", skin);

    btnRotate90 = new TextButton("Rotate 90", skin);
    btnFlipX = new TextButton("Flip X", skin);
    btnFlipY = new TextButton("Flip Y", skin);
    btnApplyTransform = new TextButton("Apply to SRC", skin);

    packMaxW = new TextField("", skin);
    packMaxH = new TextField("", skin);
    packPadX = new TextField("", skin);
    packPadY = new TextField("", skin);
    packRotation = new CheckBox("allowRotation", skin);
    packLoad = new TextButton("Load settings", skin);
    packSave = new TextButton("Save settings", skin);

    Table topInfo = new Table();
    topInfo.add(lblInfo).left().row();
    topInfo.add(lblPath).left().row();

    // Candidate controls (folder -> list -> preview -> stage)
    Table candControls = new Table(skin);
    candControls.defaults().pad(3);

    Table folderRow = new Table(skin);
    folderRow.add(candidateFolderPath).growX().padRight(6);
    folderRow.add(chooseFolderBtn).left();
    candControls.add(folderRow).growX().row();
    candControls.add(candidateFolderLabel).growX().left().row();
    candControls.add(candidateSearch).growX().left().row();
    candControls.add(candScroll).growX().minHeight(220).row();
    candControls.add(candidatePickedLabel).growX().left().row();
    candControls.add(stageCandidateBtn).left().row();
    candControls.add(lblStaged).growX().left().row();

    // --- Edit Panel (always visible) ---
    Table editPanel = new Table(skin);
    editPanel.defaults().pad(4);
    editPanel.add(new Label("Edit", skin)).left().row();

    // Actions in small rows (prevents "buttons disappear to the right")
    Table actions = new Table(skin);
    actions.defaults().pad(1);
    actions.add(btnRecompute);
    actions.add(btnSave);
    actions.add(btnExport);
    actions.row();
    actions.add(btnReplacePng);
    actions.add(btnDeleteFrame);
    actions.row();
    actions.add(btnRepackAtlas);
    TextButton btnTileEditor = new TextButton("Tile Editor", skin);
    actions.row();
    actions.add(btnTileEditor).colspan(3).growX();
// Batch replace: resolve frames by prefix from a folder that contains many sprites.
batchPrefixField = new TextField("", skin);
batchPrefixField.setMessageText("Batch prefix (e.g. vampire_walk)");
batchReplacePrefixBtn = new TextButton("Batch Replace Prefix", skin);

actions.row();
actions.add(batchPrefixField).colspan(2).growX();
actions.row();
actions.add(batchReplacePrefixBtn).colspan(2).growX();
    actions.add(btnPipette);
    actions.row();
    actions.add(tolLabel);
    actions.add(tolSlider).width(220).colspan(2);
    actions.row();
    actions.add(btnRotate90);
    actions.add(btnFlipX);
    actions.add(btnFlipY);
    actions.row();
    cropEditToggle = new CheckBox("Edit Crop", skin);
    cropEditToggle.setChecked(false);
    cropEditToggle.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        boolean on = cropEditToggle.isChecked();
        preview.setCropEditEnabled(on);
        candidatePreview.setCropEditEnabled(false); // crop edit only on IST by default
        if (on) {
          preview.setPipetteMode(false);
        }
      }
    });
    actions.add(cropEditToggle).left().colspan(3).row();
    actions.add(btnApplyTransform).colspan(3).growX();

    // Packer settings (global) — edits assets/atlas/packer-settings.json
    Table packPanel = new Table(skin);
    packPanel.defaults().pad(2);
    packPanel.add(new Label("Packer settings (global)", skin)).left().colspan(2).row();
    packPanel.add(new Label("maxW", skin)).left();
    packPanel.add(packMaxW).width(120).row();
    packPanel.add(new Label("maxH", skin)).left();
    packPanel.add(packMaxH).width(120).row();
    packPanel.add(new Label("padX", skin)).left();
    packPanel.add(packPadX).width(120).row();
    packPanel.add(new Label("padY", skin)).left();
    packPanel.add(packPadY).width(120).row();
    packPanel.add(packRotation).left().colspan(2).row();
    packPanel.add(packLoad).growX();
    packPanel.add(packSave).growX().row();

    // Status + Log
    statusLabel = new Label("", skin);
    statusLabel.setWrap(true);

    logLabel = new Label("", skin);
    logLabel.setWrap(true);
    logScroll = new ScrollPane(logLabel, skin);
    logScroll.setFadeScrollBars(false);

    editPanel.add(actions).growX().row();
    editPanel.add(packPanel).growX().row();
    editPanel.add(statusLabel).growX().row();
    editPanel.add(logScroll).growX().minHeight(160).row();

    right.add(topInfo).growX().row();

    // IST preview + Candidate preview side-by-side (same size)
    Table previewsRow = new Table(skin);
    int sh = Gdx.graphics.getHeight();
    float previewSize = Math.min(sh * 0.38f, 520f); // use the larger preview footprint as reference
    previewsRow.add(preview).size(previewSize, previewSize).top().padRight(10);
    previewsRow.add(candidatePreview).size(previewSize, previewSize).top();
    right.add(previewsRow).growX().height(previewSize + 12).row();

    // Frame-strip row (IST preview only). Candidate preview keeps its own area unchanged.
    Table framesRow = new Table(skin);
    framesRow.defaults().pad(2);
    btnPreviewPlay = new TextButton("Auto: ON", skin);
    frameStrip = new Table(skin);
    frameStrip.defaults().pad(2);
    frameStripScroll = new ScrollPane(frameStrip, skin);
    frameStripScroll.setFadeScrollBars(false);
    frameStripScroll.setScrollingDisabled(false, true);
    framesRow.add(btnPreviewPlay).left().padRight(8);
    framesRow.add(frameStripScroll).growX().height(42);
    // Keep layout symmetric with the right candidate preview column.
    framesRow.add().width(previewSize).height(42);
    right.add(framesRow).growX().row();

    // Candidate folder/list controls below previews
    right.add(candControls).growX().row();

    right.add(editPanel).growX().row();

    root.add(left).width(360).growY();
    root.add(right).grow();

    // Drag&Drop: drag candidate preview onto current preview to stage a replacement.
    dnd = new com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop();
    dnd.addSource(new com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source(candidatePreview) {
      @Override public com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload dragStart (com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer) {
        // Prefer the currently selected candidate from the folder list.
        CandidateFile cf = (candidateList != null) ? candidateList.getSelected() : null;
        java.io.File f = (cf != null) ? cf.abs : candidateFile;
        if (f == null) return null;
        com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload payload = new com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload();
        payload.setObject(f);
        payload.setDragActor(new Label("PNG", skin));
        return payload;
      }
    });
    dnd.addTarget(new com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target(preview) {
      @Override public boolean drag (com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source source, com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload payload, float x, float y, int pointer) {
        return payload != null && payload.getObject() instanceof java.io.File;
      }
      @Override public void drop (com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source source, com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (!(payload.getObject() instanceof java.io.File)) return;
        pendingDropFile = (java.io.File) payload.getObject();
        setStatus("OK: drop staged. Press Save (confirm) to apply + repack.");
        appendLog("Staged drop: " + relPath(pendingDropFile));
        if (lblStaged != null) lblStaged.setText("Staged: " + pendingDropFile.getName());
      }
    });

    // Events

    // Candidate folder events
    chooseFolderBtn.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        onSetCandidateFolder();
      }
    });

    candidateSearch.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        applyCandidateFilter();
      }
    });

    candidateList.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        CandidateFile cf = candidateList.getSelected();
        if (cf != null) loadCandidatePreview(cf.abs, cf.rel);
      }
    });

    stageCandidateBtn.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        onStageCandidate();
      }
    });
    atlasSelect.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        String sel = atlasSelect.getSelected();
        if (sel == null) sel = "STATIC";
        sel = sel.trim().toUpperCase(Locale.ROOT);
        atlasBaseName = "LIVING".equals(sel) ? "living" : ("UI".equals(sel) ? "ui" : "static");

        // Update candidate folder path to the selected atlas src folder.
        try {
          if (candidateFolderPath != null) candidateFolderPath.setText("\\assets\\atlas\\src_" + atlasBaseName);
          onSetCandidateFolder();
        } catch (Throwable ignored) {
        }

        // Reload atlas + rebuild index list.
        reloadAtlas();
        rebuildIndex();
        applyFilter();
        if (filtered.size > 0) {
          list.setSelectedIndex(0);
          select(filtered.get(0));
        }
      }
    });

    kindFilter.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        applyFilter();
      }
    });

    statusFilter.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        applyFilter();
      }
    });

    search.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        applyFilter();
      }
    });

    list.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        IndexEntry sel = list.getSelected();
        if (sel != null) select(sel);
      }
    });

    // Preview play/pause (editor-only). Does not write anything.
    btnPreviewPlay.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, Actor actor) {
        previewAutoPlay = !previewAutoPlay;
        btnPreviewPlay.setText(previewAutoPlay ? "Auto: ON" : "Auto: OFF");
        previewAnimAcc = 0f;
      }
    });

    btnPipette.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        boolean on = !preview.isPipetteMode();
        if (on && cropEditToggle != null) {
          cropEditToggle.setChecked(false);
          preview.setCropEditEnabled(false);
        }
        preview.setPipetteMode(on);
        btnPipette.setText(on ? "Pipette: ON" : "Pipette BG");
      }
    });

    tolSlider.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        bgTol = (int)tolSlider.getValue();
        tolLabel.setText("tol=" + bgTol);
        recomputeDraft();
      }
    });

    btnRecompute.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        recomputeDraft();
      }
    });

    btnSave.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        onSavePressed();
      }
    });

    btnExport.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        exportIndexFiles();
      }
    });

    btnReplacePng.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        onReplacePng();
      }
    });

    btnDeleteFrame.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        onDeleteSelectedFrame();
      }
    });

    btnRepackAtlas.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        onRepackAtlasAsync();
      }
    });

    btnTileEditor.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        game.setScreen(new TileEditorScreen(game, AssetEditorScreen.this));
      }
    });

batchReplacePrefixBtn.addListener(new ChangeListener() {
  @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
    onBatchReplacePrefixAsync();
  }
});

    btnRotate90.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        preview.rotate90();
        setStatus("OK: preview rotated");
      }
    });
    btnFlipX.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        preview.flipX();
        setStatus("OK: preview flipX");
      }
    });
    btnFlipY.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        preview.flipY();
        setStatus("OK: preview flipY");
      }
    });
    btnApplyTransform.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        applyPreviewTransformToSourcePng();
      }
    });

    btnLoadCandidate.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        // Keep the button but guide users to folder-based candidates.
        onLoadCandidate();
      }
    });

    packLoad.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        loadPackerSettings();
      }
    });
    packSave.addListener(new ChangeListener() {
      @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
        savePackerSettings();
      }
    });
  }

  private void setStatus(String msg) {
    if (statusLabel != null) statusLabel.setText(msg == null ? "" : msg);
  }

  private void appendLog(String msg) {
    if (logLabel == null) return;
    String cur = logLabel.getText() == null ? "" : logLabel.getText().toString();
    if (cur.length() > 12000) cur = cur.substring(Math.max(0, cur.length() - 8000));
    logLabel.setText(cur + (cur.isEmpty() ? "" : "\n") + (msg == null ? "" : msg));
    if (logScroll != null) {
      logScroll.layout();
      logScroll.setScrollPercentY(1f);
    }
  }

  private void applyUiScale() {
    if (stage == null) return;
    // Make the editor readable on both 1080p and 1440p/4k.
    // unitsPerPixel > 1 => UI smaller, < 1 => UI bigger.
    int w = Gdx.graphics.getWidth();
    float upp;
    if (w >= 3840) upp = 0.92f;      // 4k: slightly bigger UI
    else if (w >= 2560) upp = 0.96f; // 1440p+: slightly bigger UI
    else if (w <= 1600) upp = 1.00f; // small windows: keep neutral
    else upp = 0.98f;               // 1080p-ish: tiny bump
    ((ScreenViewport) stage.getViewport()).setUnitsPerPixel(upp);
  }

  private void onRepackAtlasAsync() {
    setStatus("Repack: running...");
    appendLog("Repack started...");
    appendLog("Using packer-settings.json: " + packerSettingsPath());

    new Thread(() -> {
      try {
        onRepackAtlasInternal();
      } catch (Exception e) {
        Gdx.app.postRunnable(() -> {
          setStatus("ERROR: Repack exception");
          appendLog("Repack ERROR: " + e);
        });
      }
    }, "asset-repack").start();
  }

  
  private void onLoadCandidate() {
    setStatus("INFO: Use folder path + Set Folder, then pick a candidate from the list.");
  }

  // Nicht fertiges Feature: unused helper (we do not open FileDialogs on Windows)
  /*
  private void onChooseCandidateFolder() {
    // Deprecated: we intentionally do NOT open java.awt.FileDialog here because it can hard-crash LWJGL3 on Windows.
    setStatus("INFO: paste folder path and press Set Folder");
  }
  */

  private void onSetCandidateFolder() {
    try {
      if (candidateFolderPath == null) {
        setStatus("ERROR: folder path field missing");
        return;
      }
      String raw = candidateFolderPath.getText();
      String path = raw == null ? "" : raw.trim();
      if (path.startsWith("\"") && path.endsWith("\"") && path.length() >= 2) {
        path = path.substring(1, path.length() - 1).trim();
      }
      if (path.isEmpty()) {
        setStatus("ERROR: folder path empty");
        return;
      }
      // Accept both absolute paths and project-relative paths like "\\assets\\atlas\\src".
      java.io.File dir = new java.io.File(path);
      if (!dir.isAbsolute() && projectRoot != null) {
        String rel = path;
        while (rel.startsWith("\\") || rel.startsWith("/")) rel = rel.substring(1);
        dir = new java.io.File(projectRoot, rel);
      }
      if (!dir.exists() || !dir.isDirectory()) {
        setStatus("ERROR: not a folder");
        appendLog("Set folder rejected: " + path);
        return;
      }
      candidateRootDir = dir;
      if (candidateFolderLabel != null) candidateFolderLabel.setText("Folder: " + relPath(candidateRootDir));
      scanCandidateFolder(candidateRootDir);
      applyCandidateFilter();
      setStatus("OK: candidates scanned: " + candidateAll.size);
      appendLog("Candidates scanned: " + candidateAll.size + " in " + relPath(candidateRootDir));
    } catch (Exception ex) {
      setStatus("ERROR: set folder failed");
      appendLog("Set folder ERROR: " + ex);
    }
  }

  private void scanCandidateFolder(java.io.File root) {
    candidateAll.clear();
    if (root == null || !root.exists()) return;
    final java.nio.file.Path base = root.toPath();
    try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.walk(base)) {
      s.filter(p -> java.nio.file.Files.isRegularFile(p))
        .filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith(".png"))
        .forEach(p -> {
          String rel = base.relativize(p).toString().replace('\\', '/');
          candidateAll.add(new CandidateFile(p.toFile(), rel));
        });
    } catch (Exception ex) {
      appendLog("Scan candidates ERROR: " + ex);
    }
  }

  private void applyCandidateFilter() {
    if (candidateList == null) return;
    candidateFiltered.clear();
    String q = (candidateSearch == null || candidateSearch.getText() == null) ? "" : candidateSearch.getText().trim().toLowerCase(Locale.ROOT);
    for (int i = 0; i < candidateAll.size; i++) {
      CandidateFile cf = candidateAll.get(i);
      if (q.isEmpty() || cf.rel.toLowerCase(Locale.ROOT).contains(q)) {
        candidateFiltered.add(cf);
      }
    }
    candidateList.setItems(candidateFiltered);
  }

  private void loadCandidatePreview(java.io.File f, String rel) {
    try {
      if (f == null || !f.exists()) {
        if (candidatePickedLabel != null) candidatePickedLabel.setText("Candidate: (missing)");
        return;
      }
      Pixmap pm = new Pixmap(Gdx.files.absolute(f.getAbsolutePath()));
      // Guard before creating a GL texture (prevents native driver crashes on huge PNGs)
      if (pm.getWidth() > 4096 || pm.getHeight() > 4096) {
        int w = pm.getWidth();
        int h = pm.getHeight();
        pm.dispose();
        setStatus("ERROR: candidate too large");
        appendLog("Candidate too large: " + rel + " (" + w + "x" + h + ")");
        return;
      }
      candidatePreview.setPixmap(pm); // PreviewCanvas owns pm afterwards
      candidatePreview.fitToContent();
      if (candidatePickedLabel != null) candidatePickedLabel.setText("Candidate: " + rel);
      setStatus("OK: candidate selected");
    } catch (Exception ex) {
      setStatus("ERROR: candidate preview load");
      appendLog("Candidate preview ERROR: " + ex);
    }
  }

  private void onStageCandidate() {
    if (candidateList == null) {
      setStatus("ERROR: candidate list not ready");
      return;
    }
    CandidateFile cf = candidateList.getSelected();
    if (cf == null) {
      setStatus("ERROR: no candidate selected");
      return;
    }
    pendingCandidateFile = cf.abs;
    pendingDropFile = cf.abs; // reuse existing apply path in onSavePressed (minimal)
    setStatus("OK: staged candidate. Press Save (confirm) to apply + repack.");
    appendLog("Staged candidate: " + relPath(cf.abs));
    if (lblStaged != null) lblStaged.setText("Staged: " + cf.abs.getName());
  }

private void onBatchReplacePrefixAsync() {
  new Thread(() -> {
    try {
      onBatchReplacePrefixInternal();
    } catch (Exception e) {
      Gdx.app.postRunnable(() -> {
        setStatus("ERROR: Batch replace failed");
        appendLog("Batch replace ERROR: " + e);
      });
    }
  }, "asseteditor-batch-replace").start();
}

private void onBatchReplacePrefixInternal() throws Exception {
  if (candidateRootDir == null || !candidateRootDir.isDirectory()) {
    Gdx.app.postRunnable(() -> setStatus("ERROR: Candidate folder not set"));
    return;
  }

  // Source prefix (files to import) - normalize user input
  String raw = batchPrefixField != null ? batchPrefixField.getText() : null;
  if (raw == null) raw = "";
  raw = raw.trim();
  // If user pasted a path (e.g. sprites/vampire_walk), keep only the last segment.
  raw = raw.replace('\\', '/');
  int lastSlash = raw.lastIndexOf('/');
  if (lastSlash >= 0) raw = raw.substring(lastSlash + 1);
  // Normalize: lower-case and remove trailing separators like '_' or '-'
  String sp = raw.toLowerCase(Locale.ROOT);
  while (sp.endsWith("_") || sp.endsWith("-")) sp = sp.substring(0, sp.length() - 1);
  final String sourcePrefix = sp;
  if (sourcePrefix.isEmpty()) {
    Gdx.app.postRunnable(() -> setStatus("ERROR: Batch prefix empty"));
    return;
  }

  // Target prefix: replace the currently selected IST entry (what the game actually references).
  // If nothing is selected, we fall back to the typed prefix (still useful for bulk import).
  final String targetPrefix = (current != null && current.name != null && !current.name.isBlank())
      ? current.name.trim()
      : sourcePrefix;

  // Detect if target already encodes a direction suffix (e.g. merchant_walk_E).
  // Editor is intentionally limited to 4-way directions: N/E/S/W.
  final java.util.regex.Matcher targetDirM = java.util.regex.Pattern.compile("^(.*?)(?:[_\\-])([NESW])$", java.util.regex.Pattern.CASE_INSENSITIVE)
      .matcher(targetPrefix);
  final boolean targetHasDirSuffix = targetDirM.matches();
  final String targetDir = targetHasDirSuffix ? targetDirM.group(2).toUpperCase(Locale.ROOT) : null;

  // Candidate files match (by filename only, not folders):
  //   vampire_walk_N_001.png / vampire-walk-E-12.png
  final java.util.regex.Pattern srcPat = java.util.regex.Pattern.compile(
      "^" + java.util.regex.Pattern.quote(sourcePrefix) + "(?:[_\\-]?)([NESW])(?:[_\\-]?)(\\d+)?\\.png$",
      java.util.regex.Pattern.CASE_INSENSITIVE);

  java.util.Map<String, java.util.List<File>> newByDir = new java.util.HashMap<>();
  newByDir.put("N", new java.util.ArrayList<>());
  newByDir.put("E", new java.util.ArrayList<>());
  newByDir.put("S", new java.util.ArrayList<>());
  newByDir.put("W", new java.util.ArrayList<>());

  // Walk recursively so subfolders are automatically supported; match only the file name.
  try (java.util.stream.Stream<java.nio.file.Path> st = java.nio.file.Files.walk(candidateRootDir.toPath())) {
    st.filter(java.nio.file.Files::isRegularFile).forEach(pp -> {
      String fn = pp.getFileName().toString();
      String low = fn.toLowerCase(Locale.ROOT);
      if (!low.endsWith(".png")) return;
      java.util.regex.Matcher mm = srcPat.matcher(low);
      if (!mm.matches()) return;
      String d = mm.group(1).toUpperCase(Locale.ROOT);
      java.util.List<File> filesForDir = newByDir.get(d);
      if (filesForDir != null) filesForDir.add(pp.toFile());
    });
  }

  // Count candidates
  int totalNew = 0;
  for (String d : new String[]{"N","E","S","W"}) totalNew += newByDir.get(d).size();
  if (totalNew == 0) {
    Gdx.app.postRunnable(() -> setStatus("ERROR: No matching PNGs for prefix '" + sourcePrefix + "' in " + candidateRootDir));
    return;
  }
  final int totalNewFinal = totalNew;

  java.util.Comparator<File> frameSort = (a, b) -> {
    String an = a.getName().toLowerCase(Locale.ROOT);
    String bn = b.getName().toLowerCase(Locale.ROOT);
    int ai = extractTrailingNumber(an);
    int bi = extractTrailingNumber(bn);
    if (ai != bi) return Integer.compare(ai, bi);
    return an.compareTo(bn);
  };
  for (String d : new String[]{"N","E","S","W"}) newByDir.get(d).sort(frameSort);

  File root = ProjectRoot.find();
  // FIX: write to the correct atlas-specific src folder.
  File srcDir = new File(root, "assets/atlas/src_" + atlasBaseName);
  if (!srcDir.exists()) {
    //noinspection ResultOfMethodCallIgnored
    srcDir.mkdirs();
  }
  if (!srcDir.exists() || !srcDir.isDirectory()) {
    final File srcDirFinal = srcDir;
    Gdx.app.postRunnable(() -> setStatus("ERROR: atlas src folder missing: " + relPath(srcDirFinal)));
    return;
  }

  // Staging for atomic overwrites
  File staging = new File(root, "assets/atlas/_staging_replace");
  //noinspection ResultOfMethodCallIgnored
  staging.mkdirs();

  Gdx.app.postRunnable(() -> appendLog("Batch Replace: sourcePrefix=" + sourcePrefix + " -> targetPrefix=" + targetPrefix +
      "  folder=" + relPath(candidateRootDir)));

  int replaced = 0;
  int deleted = 0;
  int added = 0;

  // Helper: list existing old frames for a given pattern, sorted
  java.util.function.Function<java.util.regex.Pattern, java.util.List<File>> listOld = (pat) -> {
    java.util.List<File> out = new java.util.ArrayList<>();
    File[] existing = srcDir.listFiles((dir, name) -> name != null && name.toLowerCase(Locale.ROOT).endsWith(".png"));
    if (existing != null) {
      for (File f : existing) {
        String low = f.getName().toLowerCase(Locale.ROOT);
        if (pat.matcher(low).matches()) out.add(f);
      }
    }
    out.sort(frameSort);
    return out;
  };

  if (targetHasDirSuffix) {
    // Replace ONLY the selected direction (encoded in targetPrefix already).
    java.util.List<File> newFrames = newByDir.get(targetDir);
    if (newFrames == null || newFrames.isEmpty()) {
      Gdx.app.postRunnable(() -> setStatus("ERROR: No candidate frames for target direction '" + targetDir + "' (target=" + targetPrefix + ")"));
      return;
    }

    final String targetPrefixLower = targetPrefix.toLowerCase(Locale.ROOT);
    // Old naming scheme in this mode: <targetPrefix>_<num>.png (because targetPrefix already ends with _E/_N/...)
    final java.util.regex.Pattern oldPat = java.util.regex.Pattern.compile(
        "^" + java.util.regex.Pattern.quote(targetPrefixLower) + "(?:[_\\-]?)(\\d+)?\\.png$",
        java.util.regex.Pattern.CASE_INSENSITIVE);

    java.util.List<File> oldFrames = listOld.apply(oldPat);

    Gdx.app.postRunnable(() -> appendLog("  mode=singleDir dir=" + targetDir + " new=" + newFrames.size() + " old=" + oldFrames.size()));

    int n = Math.min(newFrames.size(), oldFrames.size());
    for (int i = 0; i < n; i++) {
      File src = newFrames.get(i);
      File dst = oldFrames.get(i); // keep exact old filename
      File tmp = new File(staging, dst.getName() + ".tmp");
      java.nio.file.Files.copy(src.toPath(), tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      java.nio.file.Files.move(tmp.toPath(), dst.toPath(),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING,
          java.nio.file.StandardCopyOption.ATOMIC_MOVE);
      replaced++;
    }

    for (int i = n; i < oldFrames.size(); i++) {
      if (oldFrames.get(i).delete()) deleted++;
    }

    if (newFrames.size() > oldFrames.size()) {
      int pad = 3;
      int startIdx = 1;
      if (!oldFrames.isEmpty()) {
        String last = oldFrames.get(oldFrames.size() - 1).getName().toLowerCase(Locale.ROOT);
        pad = inferDigitPadding(last);
        startIdx = extractTrailingNumber(last) + 1;
        if (startIdx <= 0) startIdx = oldFrames.size() + 1;
      }
      for (int i = oldFrames.size(); i < newFrames.size(); i++) {
        File src = newFrames.get(i);
        String num = String.format(java.util.Locale.ROOT, "%0" + pad + "d", startIdx++);
        String dstName = targetPrefix + "_" + num + ".png"; // NOTE: no extra direction appended!
        File dst = new File(srcDir, dstName);
        File tmp = new File(staging, dstName + ".tmp");
        java.nio.file.Files.copy(src.toPath(), tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        java.nio.file.Files.move(tmp.toPath(), dst.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING,
            java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        added++;
      }
    }
  } else {
    // Replace ALL directions (4-way): old naming scheme <targetPrefix>_<dir>_<num>.png
    final String targetPrefixLower = targetPrefix.toLowerCase(Locale.ROOT);
    final java.util.regex.Pattern oldPat = java.util.regex.Pattern.compile(
        "^" + java.util.regex.Pattern.quote(targetPrefixLower) + "(?:[_\\-]?)([NESW])(?:[_\\-]?)(\\d+)?\\.png$",
        java.util.regex.Pattern.CASE_INSENSITIVE);

    // Group old by dir
    java.util.Map<String, java.util.List<File>> oldByDir = new java.util.HashMap<>();
    oldByDir.put("N", new java.util.ArrayList<>());
    oldByDir.put("E", new java.util.ArrayList<>());
    oldByDir.put("S", new java.util.ArrayList<>());
    oldByDir.put("W", new java.util.ArrayList<>());

    java.util.List<File> oldAll = listOld.apply(oldPat);
    for (File f : oldAll) {
      java.util.regex.Matcher mm = oldPat.matcher(f.getName().toLowerCase(Locale.ROOT));
      if (!mm.matches()) continue;
      String d = mm.group(1).toUpperCase(Locale.ROOT);
      java.util.List<File> filesForDir = oldByDir.get(d);
      if (filesForDir != null) filesForDir.add(f);
    }
    for (String d : new String[]{"N","E","S","W"}) oldByDir.get(d).sort(frameSort);

    for (String d : new String[]{"N","E","S","W"}) {
      java.util.List<File> newFrames = newByDir.get(d);
      java.util.List<File> oldFrames = oldByDir.get(d);
      if (newFrames == null) newFrames = java.util.Collections.emptyList();
      if (oldFrames == null) oldFrames = java.util.Collections.emptyList();

      if (!newFrames.isEmpty()) {
        final int nn = newFrames.size();
        final int oo = oldFrames.size();
        final String dd = d;
        Gdx.app.postRunnable(() -> appendLog("  " + dd + " new=" + nn + " old=" + oo));
      }

      int n = Math.min(newFrames.size(), oldFrames.size());
      for (int i = 0; i < n; i++) {
        File src = newFrames.get(i);
        File dst = oldFrames.get(i);
        File tmp = new File(staging, dst.getName() + ".tmp");
        java.nio.file.Files.copy(src.toPath(), tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        java.nio.file.Files.move(tmp.toPath(), dst.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING,
            java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        replaced++;
      }

      for (int i = n; i < oldFrames.size(); i++) {
        if (oldFrames.get(i).delete()) deleted++;
      }

      if (newFrames.size() > oldFrames.size()) {
        int pad = 3;
        int startIdx = 1;
        if (!oldFrames.isEmpty()) {
          String last = oldFrames.get(oldFrames.size() - 1).getName().toLowerCase(Locale.ROOT);
          pad = inferDigitPadding(last);
          startIdx = extractTrailingNumber(last) + 1;
          if (startIdx <= 0) startIdx = oldFrames.size() + 1;
        }
        for (int i = oldFrames.size(); i < newFrames.size(); i++) {
          File src = newFrames.get(i);
          String num = String.format(java.util.Locale.ROOT, "%0" + pad + "d", startIdx++);
          String dstName = targetPrefix + "_" + d + "_" + num + ".png";
          File dst = new File(srcDir, dstName);
          File tmp = new File(staging, dstName + ".tmp");
          java.nio.file.Files.copy(src.toPath(), tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
          java.nio.file.Files.move(tmp.toPath(), dst.toPath(),
              java.nio.file.StandardCopyOption.REPLACE_EXISTING,
              java.nio.file.StandardCopyOption.ATOMIC_MOVE);
          added++;
        }
      }
    }
  }

  final int replacedF = replaced;
  final int deletedF = deleted;
  final int addedF = added;

  Gdx.app.postRunnable(() -> appendLog("Batch Replace summary: replaced=" + replacedF + " deleted=" + deletedF + " added=" + addedF +
      " (candidates=" + totalNewFinal + ")"));

  Gdx.app.postRunnable(() -> setStatus("OK: Batch copied/replaced " + replacedF + " PNG(s). Repacking..."));
  onRepackAtlasInternal();
  Gdx.app.postRunnable(() -> setStatus("OK: Batch replace done (replaced " + replacedF + ", added " + addedF + ", deleted " + deletedF + ")"));
}


private static int extractTrailingNumber(String filenameLower) {
  int dot = filenameLower.lastIndexOf(".png");
  String s = dot > 0 ? filenameLower.substring(0, dot) : filenameLower;
  int i = s.length() - 1;
  while (i >= 0 && Character.isDigit(s.charAt(i))) i--;
  int start = i + 1;
  if (start >= s.length()) return 0;
  try {
    return Integer.parseInt(s.substring(start));
  } catch (Throwable ignored) {
    return 0;
  }
}

private static int inferDigitPadding(String filenameLower) {
  // Determine how many digits the trailing number has (e.g. _01 -> 2, _001 -> 3). Default 3.
  int dot = filenameLower.lastIndexOf(".png");
  String s = dot > 0 ? filenameLower.substring(0, dot) : filenameLower;
  int i = s.length() - 1;
  while (i >= 0 && Character.isDigit(s.charAt(i))) i--;
  int start = i + 1;
  int len = s.length() - start;
  if (len <= 0) return 3;
  return Math.min(Math.max(len, 1), 6);
}


  private void onReplacePng() {
    // IMPORTANT: no java.awt.FileDialog here. It can hard-crash LWJGL3 on Windows.
    // Replace is driven by the Candidate list (folder-based workflow):
    // 1) Set Folder -> 2) pick candidate -> 3) Replace PNG -> 4) Repack Atlas
    if (current == null || current.name == null || current.name.trim().isEmpty()) {
      setStatus("ERROR: no selection");
      return;
    }
    // Guardrail: single replace is per-frame (selectedFrameIndex) and may ONLY overwrite an existing
    // atlas/src/<name>_<index>.png file. No renames. No creating new files.
    if (currentFrames == null || currentFrames.size == 0) {
      setStatus("ERROR: no frames for selection");
      appendLog("Replace PNG aborted: current selection has no frames.");
      return;
    }
    if (selectedFrameIndex < 0 || selectedFrameIndex >= currentFrames.size) {
      setStatus("ERROR: invalid frame index");
      appendLog("Replace PNG aborted: selectedFrameIndex out of range: " + selectedFrameIndex + "/" + currentFrames.size);
      return;
    }
    try {
      // Prefer the currently selected candidate from the candidate list.
      File src = null;
      if (candidateList != null) {
        CandidateFile cf = candidateList.getSelected();
        if (cf != null && cf.abs != null && cf.abs.exists()) src = cf.abs;
      }
      // Fallback: last staged candidate (if user used Stage Candidate button earlier).
      if (src == null && pendingCandidateFile != null && pendingCandidateFile.exists()) {
        src = pendingCandidateFile;
      }
      if (src == null) {
        setStatus("ERROR: no candidate selected");
        appendLog("Replace PNG aborted: select a candidate in the Candidate list (Set Folder -> pick file).");
        return;
      }

      // Target: assets/atlas/src_<atlas>/<name>.png OR <name>_<atlasIndex>.png (MUST already exist)
      // Note: libGDX AtlasRegion.index can be -1 for non-indexed regions (single-frame names).
      TextureAtlas.AtlasRegion r = currentFrames.get(selectedFrameIndex);
      String relDst = (r.index >= 0)
          ? ("assets/atlas/src_" + atlasBaseName + "/" + current.name + "_" + r.index + ".png")
          : ("assets/atlas/src_" + atlasBaseName + "/" + current.name + ".png");
      File dst = new File(ProjectRoot.resolve(relDst));
      if (!dst.exists()) {
        setStatus("ERROR: target frame PNG missing");
        appendLog("Replace PNG aborted: target does not exist (no create allowed): " + dst.getPath());
        return;
      }
      File parent = dst.getParentFile();

      // Atomic-ish replace: copy to temp, then move over the destination.
      java.nio.file.Path dstPath = dst.toPath();
      java.nio.file.Path tmpPath = (parent == null)
          ? java.nio.file.Files.createTempFile("replace_", "_" + current.name + ".png")
          : java.nio.file.Files.createTempFile(parent.toPath(), "replace_", "_" + current.name + ".png");

      java.nio.file.Files.copy(src.toPath(), tmpPath, StandardCopyOption.REPLACE_EXISTING);
      try {
        java.nio.file.Files.move(tmpPath, dstPath,
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.ATOMIC_MOVE);
      } catch (java.nio.file.AtomicMoveNotSupportedException ignoreAtomic) {
        // ATOMIC_MOVE may not be supported on some filesystems. Fallback to non-atomic move.
        java.nio.file.Files.move(tmpPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
      }

      setStatus("OK: replaced " + dst.getPath());
      appendLog("Replace OK: " + src.getPath() + " -> " + dst.getPath());

      // Refresh preview/collision for the currently selected frame without resetting selection.
      setSelectedFrameIndex(selectedFrameIndex, false);
      recomputeDraft();
    } catch (Exception e) {
      setStatus("ERROR: Replace failed");
      appendLog("Replace ERROR: " + e);
    }
  }

  private void onDeleteSelectedFrame() {
    if (current == null || current.name == null || current.name.trim().isEmpty()) {
      setStatus("ERROR: no selection");
      return;
    }
    if (currentFrames == null || currentFrames.size == 0) {
      setStatus("ERROR: no frames for selection");
      return;
    }
    if (selectedFrameIndex < 0 || selectedFrameIndex >= currentFrames.size) {
      setStatus("ERROR: invalid frame index");
      return;
    }

    double now = com.badlogic.gdx.utils.TimeUtils.millis() / 1000.0;
    if (now > deleteArmedUntil) {
      deleteArmedUntil = now + 2.0;
      setStatus("DELETE ARMED (2s): click again to delete selected frame PNG");
      appendLog("Delete armed: " + current.name + " frameIndex=" + selectedFrameIndex);
      return;
    }
    deleteArmedUntil = 0.0;

    try {
      TextureAtlas.AtlasRegion r = currentFrames.get(selectedFrameIndex);
      String relDst = (r.index >= 0)
          ? ("assets/atlas/src_" + atlasBaseName + "/" + current.name + "_" + r.index + ".png")
          : ("assets/atlas/src_" + atlasBaseName + "/" + current.name + ".png");
      File dst = new File(ProjectRoot.resolve(relDst));

      if (!dst.exists()) {
        setStatus("ERROR: target frame PNG missing (nothing to delete)");
        appendLog("Delete aborted: target does not exist: " + dst.getPath());
        return;
      }

      boolean ok = dst.delete();
      if (!ok) {
        setStatus("ERROR: delete failed");
        appendLog("Delete failed: " + dst.getPath());
        return;
      }

      setStatus("OK: deleted " + dst.getPath());
      appendLog("Delete OK: " + dst.getPath());

      // Refresh preview without changing selection; selected frame will show missing.
      setSelectedFrameIndex(selectedFrameIndex, false);
    } catch (Throwable t) {
      setStatus("ERROR: delete failed");
      appendLog("Delete ERROR: " + t);
    }
  }


  private void onRepackAtlasInternal() throws Exception {
    File root = ProjectRoot.find();
    boolean isWindows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");

    // Robust wrapper resolution: do NOT rely on the current working directory containing gradlew.
    File gradlewWin = new File(root, "gradlew.bat");
    File gradlewNix = new File(root, "gradlew");
    File gradlew = isWindows ? gradlewWin : gradlewNix;

    if (!gradlew.exists()) {
      Gdx.app.postRunnable(() -> setStatus("ERROR: gradlew not found in project root: " + relPath(root)));
      Gdx.app.postRunnable(() -> appendLog("Expected: " + relPath(gradlew)));
      return;
    }

    java.util.List<String> cmd = new java.util.ArrayList<>();
    if (isWindows) {
      cmd.add("cmd");
      cmd.add("/c");
      cmd.add(gradlew.getAbsolutePath());
      cmd.add("packAtlas");
    } else {
      cmd.add(gradlew.getAbsolutePath());
      cmd.add("packAtlas");
    }

    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.directory(root);
    pb.redirectErrorStream(true);

    // Persist repack output so crashes / exits still leave evidence on disk.
    File crashDir = new File(root, "crash_logs");
    //noinspection ResultOfMethodCallIgnored
    crashDir.mkdirs();
    File repackLog = new File(crashDir, "repack.log");

    java.io.PrintWriter logOut;
    try {
      logOut = new java.io.PrintWriter(new java.io.FileWriter(repackLog, true));
      logOut.println("---- REPACK " + new java.util.Date() + " ----");
      logOut.println("cwd=" + relPath(root));
      logOut.println("cmd=" + cmd);
      logOut.flush();
    } catch (java.io.IOException ignored) {
      logOut = null;
    }

    Process p = pb.start();

try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
      String line;
      while ((line = br.readLine()) != null) {
        final String l = line;
        if (logOut != null) { logOut.println(l); logOut.flush(); }
        Gdx.app.postRunnable(() -> appendLog(l));
      }
    }
    if (logOut != null) { try { logOut.close(); } catch (Throwable ignored) {} }

    int code = p.waitFor();
    if (code != 0) {
      Gdx.app.postRunnable(() -> setStatus("ERROR: Repack failed (exit " + code + ")"));
      return;
    }

    // New 3-atlas pack: require at least one of the expected outputs.
    File atlasDir = new File(root, "assets/atlas");
    File staticAtlas = new File(atlasDir, "static.atlas");
    File livingAtlas = new File(atlasDir, "living.atlas");
    File uiAtlas = new File(atlasDir, "ui.atlas");
    if (!staticAtlas.exists() && !livingAtlas.exists() && !uiAtlas.exists()) {
      Gdx.app.postRunnable(() -> setStatus("ERROR: no atlas output found after pack (static/living/ui)"));
      return;
    }

    Gdx.app.postRunnable(() -> {
      setStatus("OK: repacked + reloaded");
      appendLog("Repack OK.");
      reloadAtlas();
      rebuildIndex();
      applyFilter();
      if (current != null) select(current);
    });
  }

  private String packerSettingsPath() {
    return ProjectRoot.resolve("assets/atlas/packer-settings.json");
  }

  private JsonValue loadPackerJson() {
    String p = packerSettingsPath();
    String txt = Gdx.files.absolute(p).readString("UTF-8");
    return new JsonReader().parse(txt);
  }

  private void writePackerJson(JsonValue root) {
    String p = packerSettingsPath();
    String out = root.toJson(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
    Gdx.files.absolute(p).writeString(out, false, "UTF-8");
  }

  private void loadPackerSettings() {
    try {
      JsonValue j = loadPackerJson();
      packMaxW.setText(String.valueOf(j.getInt("maxWidth", 1024)));
      packMaxH.setText(String.valueOf(j.getInt("maxHeight", 1024)));
      packPadX.setText(String.valueOf(j.getInt("paddingX", 2)));
      packPadY.setText(String.valueOf(j.getInt("paddingY", 2)));
      packRotation.setChecked(j.getBoolean("rotation", false));
      setStatus("OK: settings loaded");
      appendLog("Loaded: assets/atlas/packer-settings.json");
    } catch (Throwable t) {
      setStatus("ERROR: load settings");
      appendLog("Load settings ERROR: " + t);
    }
  }

  private void savePackerSettings() {
    try {
      JsonValue j = loadPackerJson();
      int mw = Integer.parseInt(packMaxW.getText().trim());
      int mh = Integer.parseInt(packMaxH.getText().trim());
      int px = Integer.parseInt(packPadX.getText().trim());
      int py = Integer.parseInt(packPadY.getText().trim());

      setInt(j, "maxWidth", mw);
      setInt(j, "maxHeight", mh);
      setInt(j, "paddingX", px);
      setInt(j, "paddingY", py);
      setBool(j, "rotation", packRotation.isChecked());

      writePackerJson(j);
      setStatus("OK: settings saved");
      appendLog("Saved: assets/atlas/packer-settings.json (packAtlas uses it)");
    } catch (Exception e) {
      setStatus("ERROR: save settings");
      appendLog("Save settings ERROR: " + e);
    }
  }

  private static void setInt(JsonValue root, String key, int v) {
    JsonValue n = root.get(key);
    if (n == null) {
      JsonValue child = new JsonValue((long)v);
      child.setName(key);
      root.addChild(child);
    } else {
      // JsonValue has no set(int); use set(JsonValue) to keep numeric type.
      n.set(new JsonValue((long)v));
    }
  }

  private static void setBool(JsonValue root, String key, boolean v) {
    JsonValue n = root.get(key);
    if (n == null) {
      JsonValue child = new JsonValue(v);
      child.setName(key);
      root.addChild(child);
    } else {
      n.set(v);
    }
  }

  private void applyPreviewTransformToSourcePng() {
    if (current == null || current.name == null || current.name.trim().isEmpty()) {
      setStatus("ERROR: no selection");
      return;
    }
    try {
      String p = ProjectRoot.resolve("assets/atlas/src_" + atlasBaseName + "/" + current.name + ".png");
      File f = new File(p);
      if (!f.exists()) {
        setStatus("ERROR: src png missing");
        return;
      }

      Pixmap src = new Pixmap(Gdx.files.absolute(p));

      // Capture crop before transform (SOURCE pixel coords)
      int cx = preview.getCropX();
      int cy = preview.getCropY();
      int cw = preview.getCropW();
      int ch = preview.getCropH();
      int iw = src.getWidth();
      int ih = src.getHeight();

      Pixmap out = preview.applyTransform(src);
      PixmapIO.writePNG(Gdx.files.absolute(p), out);

      // Transform crop rect with the same mapping rules as applyTransform()
      int[] tc = transformRectByPreview(cx, cy, cw, ch, iw, ih, preview.getRot90(), preview.getFlipX(), preview.getFlipY());
      writeCropToMeta(current.name, tc[0], tc[1], tc[2], tc[3]);

      src.dispose();
      out.dispose();

      setStatus("OK: applied transform to src png");
      appendLog("Apply-to-src OK: " + p);
      select(current);
      preview.fitToContent();
    } catch (Throwable t) {
      setStatus("ERROR: apply failed");
      appendLog("Apply ERROR: " + t);
    }
  }

  // Maps a SOURCE-space rect through the same rotate+flip transform used by PreviewCanvas.applyTransform().
  // Returns {x,y,w,h} in the transformed (written) image space.
  private static int[] transformRectByPreview(int x, int y, int w, int h, int iw, int ih, int rot90, boolean flipX, boolean flipY) {
    if (w <= 0 || h <= 0) return new int[]{0, 0, 0, 0};

    int w1 = (rot90 % 2 == 0) ? iw : ih;
    int h1 = (rot90 % 2 == 0) ? ih : iw;

    int x2 = x + w;
    int y2 = y + h;

    int[][] pts = new int[][]{
      {x, y}, {x2, y}, {x, y2}, {x2, y2}
    };

    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

    for (int[] p : pts) {
      int sx = p[0];
      int sy = p[1];

      // rotate in SOURCE space
      int rx, ry;
      switch (rot90 & 3) {
        case 1 -> { rx = ih - 1 - sy; ry = sx; }            // 90
        case 2 -> { rx = iw - 1 - sx; ry = ih - 1 - sy; }   // 180
        case 3 -> { rx = sy; ry = iw - 1 - sx; }            // 270
        default -> { rx = sx; ry = sy; }
      }

      // flips in OUTPUT space
      int fx = flipX ? (w1 - 1 - rx) : rx;
      int fy = flipY ? (h1 - 1 - ry) : ry;

      if (fx < minX) minX = fx;
      if (fy < minY) minY = fy;
      if (fx > maxX) maxX = fx;
      if (fy > maxY) maxY = fy;
    }

    int nw = Math.max(1, maxX - minX);
    int nh = Math.max(1, maxY - minY);
    if (minX < 0) minX = 0;
    if (minY < 0) minY = 0;
    if (minX + nw > w1) nw = Math.max(1, w1 - minX);
    if (minY + nh > h1) nh = Math.max(1, h1 - minY);
    return new int[]{minX, minY, nw, nh};
  }

  private int[] readCropFromMeta(String regionName, int fallbackW, int fallbackH) {
    try {
      com.badlogic.gdx.files.FileHandle fh = MetaIO.resolveTarget(regionName);
      if (!fh.exists()) return new int[]{0, 0, fallbackW, fallbackH};
      JsonValue root = new JsonReader().parse(fh.readString("UTF-8"));
      int x = root.getInt("cropX", 0);
      int y = root.getInt("cropY", 0);
      int w = root.getInt("cropW", fallbackW);
      int h = root.getInt("cropH", fallbackH);
      // clamp
      if (w <= 0) w = fallbackW;
      if (h <= 0) h = fallbackH;
      if (x < 0) x = 0;
      if (y < 0) y = 0;
      if (x + w > fallbackW) x = Math.max(0, fallbackW - w);
      if (y + h > fallbackH) y = Math.max(0, fallbackH - h);
      return new int[]{x, y, w, h};
    } catch (Throwable t) {
      return new int[]{0, 0, fallbackW, fallbackH};
    }
  }

  private void writeCropToMeta(String regionName, int x, int y, int w, int h) {
    try {
      com.badlogic.gdx.files.FileHandle fh = MetaIO.resolveTarget(regionName);
      JsonValue root;
      if (fh.exists()) {
        root = new JsonReader().parse(fh.readString("UTF-8"));
      } else {
        root = new JsonValue(JsonValue.ValueType.object);
      }
      setInt(root, "cropX", x);
      setInt(root, "cropY", y);
      setInt(root, "cropW", w);
      setInt(root, "cropH", h);
      fh.writeString(root.toString(), false, "UTF-8");
    } catch (Throwable ignored) {
    }
  }

  private void reloadAtlas() {
    try {
      if (atlas != null) atlas.dispose();
      atlas = AtlasLoader.loadPreferFs(atlasBaseName);
    } catch (Throwable t) {
      atlas = new TextureAtlas();
    }
  }

  private void rebuildIndex() {
    all.clear();

    // Build base index from the ACTUAL atlas regions (always correct display list).
    java.util.HashSet<String> atlasNames = new java.util.HashSet<>();
    java.util.HashSet<String> allNames = new java.util.HashSet<>();
    java.util.HashSet<String> addedEntries = new java.util.HashSet<>();
    for (com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion r : atlas.getRegions()) {
      if (r == null || r.name == null) continue;
      atlasNames.add(r.name);
      allNames.add(r.name);
      // AtlasRegions may contain multiple entries with the SAME name (frames differentiated by region.index).
      // For the IST list we want ONE entry per atlas key, not N duplicates.
      if (addedEntries.add(r.name)) {
        all.add(new IndexEntry(r.name, AssetKind.OTHER, "atlas", false));
      }
    }

    // Overlay kind-hints by scanning live code from project root.
    File root = ProjectRoot.find();
    List<IndexEntry> scanned = AssetIndexScanner.scan(root);
    if (scanned != null) {
      for (IndexEntry e : scanned) {
        if (e == null || e.name == null) continue;
        String n = e.name.trim();
        if (n.isEmpty()) continue;

        if (!atlasNames.contains(n)) {
          // TEMPLATE: referenced in code but missing in atlas.
          if (!allNames.contains(n)) {
            allNames.add(n);
            all.add(new IndexEntry(n, e.kind != null ? e.kind : AssetKind.OTHER, e.foundIn, true));
          }
          continue;
        }

        // IST: Update existing entry kind/source.
        for (int i = 0; i < all.size; i++) {
          IndexEntry cur = all.get(i);
          if (cur != null && n.equals(cur.name)) {
            AssetKind k = e.kind != null ? e.kind : cur.kind;
            all.set(i, new IndexEntry(cur.name, k, e.foundIn, false));
            break;
          }
        }
      }
    }
  }

  private void applyFilter() {
    filtered.clear();

    String k = kindFilter.getSelected();
    String s = statusFilter.getSelected();
    String q = search.getText() == null ? "" : search.getText().trim().toLowerCase();

    for (IndexEntry e : all) {
      if (s != null) {
        if ("IST".equals(s) && e.missingInAtlas) continue;
        if ("TEMPLATE".equals(s) && !e.missingInAtlas) continue;
      }
      if (k != null && !"ALL".equals(k) && e.kind != AssetKind.valueOf(k)) continue;
      if (!q.isEmpty() && (e.name == null || !nameMatchesQuery(e.name, q))) continue;
      filtered.add(e);
    }

    list.setItems(filtered);
  }

  /**
   * Segment-based, order-preserving search.
   *
   * Example: query "player walk e" matches "player_idle_walk_E_3".
   *
   * Rules:
   * - Query is tokenized by any non-alphanumeric chars.
   * - Name is tokenized by any non-alphanumeric chars.
   * - Tokens must appear in the same order (subsequence), gaps allowed.
   * - A token matches a segment if segment equals token OR segment contains token.
   * - If query is a single token, fallback to classic substring match.
   */
  private boolean nameMatchesQuery(String name, String qLower) {
    if (name == null) return false;
    String n = name.toLowerCase();
    String q = (qLower == null) ? "" : qLower.trim();
    if (q.isEmpty()) return true;

    // Tokenize query into non-empty tokens.
    String[] qtRaw = q.split("[^a-z0-9]+");
    Array<String> qTokens = new Array<>();
    for (String t : qtRaw) {
      if (t == null) continue;
      String tt = t.trim();
      if (!tt.isEmpty()) qTokens.add(tt);
    }

    // Single-token behavior stays forgiving and fast.
    if (qTokens.size <= 1) {
      return n.contains(q);
    }

    // Tokenize name into segments.
    String[] nsRaw = n.split("[^a-z0-9]+");
    Array<String> nSegs = new Array<>();
    for (String s : nsRaw) {
      if (s == null) continue;
      String ss = s.trim();
      if (!ss.isEmpty()) nSegs.add(ss);
    }
    if (nSegs.size == 0) return false;

    // Subsequence match: each query token must be found in order.
    int pos = 0;
    for (int i = 0; i < qTokens.size; i++) {
      String tok = qTokens.get(i);
      boolean found = false;
      while (pos < nSegs.size) {
        String seg = nSegs.get(pos);
        pos++;
        if (seg.equals(tok) || seg.contains(tok)) {
          found = true;
          break;
        }
      }
      if (!found) return false;
    }
    return true;
  }

  private void select(IndexEntry e) {
    current = e;    // Resolve FrameSet (all atlas regions for this key)
    currentFrames = (atlas == null) ? new Array<>() : atlas.findRegions(e.name);
    if (currentFrames == null) currentFrames = new Array<>();
    // Order by atlas index (do not parse names)
    if (currentFrames.size > 1) {
      currentFrames.sort(Comparator.comparingInt(a -> a.index));
    }
    // Guardrails: if there are no frames, do not touch preview or files in this request
    if (currentFrames.size == 0) {
      lblInfo.setText(e.kind + " : " + e.name + "  (missing in atlas)");
      lblPath.setText("(missing: atlas frames)");
      return;
    }
    selectedFrameIndex = 0;
    // Nicht fertiges Feature: unused local
    // TextureAtlas.AtlasRegion r = currentFrames.get(0);
    boolean exists = true;
    lblInfo.setText(e.kind + " : " + e.name + (exists ? "" : "  (missing in atlas)"));

    // Build frame-strip UI and show the first frame.
    rebuildFrameStrip();
    previewAutoPlay = (currentFrames.size > 1);
    if (btnPreviewPlay != null) btnPreviewPlay.setText(previewAutoPlay ? "Auto: ON" : "Auto: OFF");
    setSelectedFrameIndex(0, false);

    actual = MetaIO.load(e.name);
    if (actual == null) {
      actual = new CollisionMeta();
      actual.regionName = e.name;
      actual.kind = e.kind;
    }

    // Carry over background defaults (if present)
    bgColor.set(actual.bgR / 255f, actual.bgG / 255f, actual.bgB / 255f, actual.bgA / 255f);
    bgTol = actual.bgTolerance;
    alphaTh = actual.alphaThreshold;
    tolSlider.setValue(bgTol);
    tolLabel.setText("tol=" + bgTol);

    // Load crop rect (sidecar keys) and apply to preview
    Pixmap curPm = preview.getPixmap();
    if (curPm != null) {
      int[] c = readCropFromMeta(e.name, curPm.getWidth(), curPm.getHeight());
      preview.setCropRect(c[0], c[1], c[2], c[3]);
    }

    recomputeDraft();
  }

  private void rebuildFrameStrip() {
    if (frameStrip == null) return;
    frameStrip.clearChildren();
    if (currentFrames == null || currentFrames.size == 0) return;

    for (int i = 0; i < currentFrames.size; i++) {
      final int idx = i;
      String label = String.format(Locale.ROOT, "%03d", (i + 1));
      TextButton b = new TextButton(label, skin);
      b.addListener(new ChangeListener() {
        @Override public void changed(ChangeEvent event, Actor actor) {
          // Guardrail: frame-click only changes selection/preview. No writes.
          setSelectedFrameIndex(idx, true);
        }
      });
      frameStrip.add(b);
    }
    frameStrip.invalidateHierarchy();
    if (frameStripScroll != null) frameStripScroll.layout();
  }

  private void setSelectedFrameIndex(int idx, boolean userAction) {
    if (currentFrames == null || currentFrames.size == 0) return;
    if (idx < 0) idx = 0;
    if (idx >= currentFrames.size) idx = currentFrames.size - 1;
    selectedFrameIndex = idx;
    previewAnimAcc = 0f;

    // If user clicks a frame, stop autoplay to avoid fighting the selection.
    if (userAction) {
      previewAutoPlay = false;
      if (btnPreviewPlay != null) btnPreviewPlay.setText("Auto: OFF");
    }

    // Load and show the selected frame (prefer atlas/src/<name>_<index>.png).
    IndexEntry e = current;
    TextureAtlas.AtlasRegion r = currentFrames.get(selectedFrameIndex);
    Pixmap pm = loadPixmapForFrame(e, r);
    if (pm == null) {
      preview.setMissing(e != null ? e.name : "(missing)");
    } else {
      preview.setPixmap(pm);
      preview.fitToContent();
    }

    // Keep crop box stable across frame switches (same meta key).
    if (e != null) {
      Pixmap curPm = preview.getPixmap();
      if (curPm != null) {
        int[] c = readCropFromMeta(e.name, curPm.getWidth(), curPm.getHeight());
        preview.setCropRect(c[0], c[1], c[2], c[3]);
      }
    }
  }

  private Pixmap loadPixmapForFrame(IndexEntry e, TextureAtlas.AtlasRegion r) {
    if (e == null) return null;

    // FIX: use the correct atlas-specific src folder (src_static / src_living / src_ui).
    // Previously hardcoded to "atlas/src/" which never matched the 3-atlas layout.
    String srcFolder = "assets/atlas/src_" + atlasBaseName + "/";

    // 1) Prefer per-frame SRC: assets/atlas/src_<atlas>/<n>_<index>.png (filesystem)
    if (r != null && r.index >= 0) {
      try {
        File f = new File(ProjectRoot.resolve(srcFolder + e.name + "_" + r.index + ".png"));
        if (f.exists()) {
          lblPath.setText(relPath(f));
          return new Pixmap(Gdx.files.absolute(f.getAbsolutePath()));
        }
      } catch (Throwable ignored) {}
    }

    // 2) Non-indexed single-frame: assets/atlas/src_<atlas>/<n>.png (filesystem)
    try {
      File f = new File(ProjectRoot.resolve(srcFolder + e.name + ".png"));
      if (f.exists()) {
        lblPath.setText(relPath(f));
        return new Pixmap(Gdx.files.absolute(f.getAbsolutePath()));
      }
    } catch (Throwable ignored) {}

    // 3) Last resort: extract from atlas texture (packaged data, may differ from src)
    if (r != null) {
      lblPath.setText("(atlas region: " + e.name + " #" + r.index + " -- src PNG missing)");
      return extractPixmapFromAtlas(r);
    }

    lblPath.setText("(missing: " + srcFolder + e.name + ".png and atlas region)");
    return null;
  }
  private void recomputeDraft() {
    if (current == null) return;
    Pixmap pm = preview.getPixmap();
    if (pm == null) {
      draft = new CollisionMeta();
      draft.regionName = current.name;
      draft.kind = current.kind;
      preview.setDraft(draft);
      return;
    }

    draft = AutoCollision.compute(current.name, current.kind, pm, bgColor, bgTol, alphaTh);
    preview.setDraft(draft);
  }

  private void onSavePressed() {
    double now = Time.nowSeconds();

    if (now <= saveArmedUntil) {
      // confirm
      if (draft != null) {
        MetaIO.save(draft);
        // Persist crop box alongside the standard meta JSON.
        if (current != null) {
          writeCropToMeta(current.name, preview.getCropX(), preview.getCropY(), preview.getCropW(), preview.getCropH());
        }
        actual = draft;
      }

      // If a candidate was dropped onto the preview, apply it on Save:
      // 1) copy to assets/atlas/src_<atlas>/<region>.png
      // 2) repack atlas
      if (pendingDropFile != null && current != null && !current.missingInAtlas) {
        try {
          // FIX: respect the selected frame index so multi-frame sprites are correctly replaced.
          String dstName;
          if (currentFrames != null && currentFrames.size > 1
              && selectedFrameIndex >= 0 && selectedFrameIndex < currentFrames.size) {
            TextureAtlas.AtlasRegion r = currentFrames.get(selectedFrameIndex);
            dstName = (r.index >= 0)
                ? (current.name + "_" + r.index + ".png")
                : (current.name + ".png");
          } else {
            dstName = current.name + ".png";
          }
          java.io.File dst = new java.io.File(com.yourgame.survival.tools.asseteditor.scan.ProjectRoot.resolve(
              "assets/atlas/src_" + atlasBaseName + "/" + dstName
          ));
          dst.getParentFile().mkdirs();
          java.nio.file.Files.copy(pendingDropFile.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
          appendLog("Applied drop -> SRC: " + relPath(pendingDropFile) + " -> " + relPath(dst));
          setStatus("OK: drop applied, repacking...");
          pendingDropFile = null;
          if (lblStaged != null) lblStaged.setText("Staged: none");
          onRepackAtlasAsync();
        } catch (java.io.IOException | RuntimeException ex) {
          setStatus("ERROR: apply drop failed");
          appendLog("Apply drop ERROR: " + ex);
        }
      }

      saveArmedUntil = 0.0;
      btnSave.setText("Save");
      btnSave.setColor(Color.WHITE);
    } else {
      saveArmedUntil = now + 4.0; // 4s window
      btnSave.setText("Save (confirm)");
      btnSave.setColor(Color.RED);
    }
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { Gdx.app.exit(); return; }
    // auto un-arm
    if (saveArmedUntil > 0.0 && Time.nowSeconds() > saveArmedUntil) {
      saveArmedUntil = 0.0;
      btnSave.setText("Save");
      btnSave.setColor(Color.WHITE);
    }

    // Editor-only preview autoplay (read-only; no writes).
    if (previewAutoPlay && currentFrames != null && currentFrames.size > 1) {
      previewAnimAcc += delta;
      if (previewAnimAcc >= previewAnimFrameSec) {
        previewAnimAcc = 0f;
        int next = selectedFrameIndex + 1;
        if (next >= currentFrames.size) next = 0;
        setSelectedFrameIndex(next, false);
      }
    }

    ScreenUtils.clear(0.05f, 0.05f, 0.05f, 1f);
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    // IMPORTANT: do not re-apply UI scale on every resize.
    // Some OS/UI events (e.g. dropdown popups) can trigger transient resize notifications and cause visible "ratio jumps".
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    if (stage != null) stage.dispose();
    if (atlas != null) { atlas.dispose(); atlas = null; }
    if (skin != null) {
      // Skin disposes textures/fonts it owns.
      skin.dispose();
    }
    if (preview != null) {
      previewDispose(preview);
    }
  }

  private static void previewDispose(PreviewCanvas pc) {
    try {
      pc.disposeResources();
    } catch (Throwable ignored) {}
  }

  private static final class Time {
    static double nowSeconds() {
      return (double)System.nanoTime() / 1_000_000_000.0;
    }
  }

  private Pixmap extractPixmapFromAtlas(TextureAtlas.AtlasRegion r) {
    try {
      if (r == null) return null;
      // Ensure TextureData is prepared
      if (!r.getTexture().getTextureData().isPrepared()) {
        r.getTexture().getTextureData().prepare();
      }
      Pixmap atlasPm = r.getTexture().getTextureData().consumePixmap();
      Pixmap out = new Pixmap(r.getRegionWidth(), r.getRegionHeight(), Pixmap.Format.RGBA8888);
      out.drawPixmap(atlasPm, 0, 0, r.getRegionX(), r.getRegionY(), r.getRegionWidth(), r.getRegionHeight());
      try { r.getTexture().getTextureData().disposePixmap(); } catch (Throwable ignored) {}
      return out;
    } catch (Throwable t) {
      return null;
    }
  }

  private void exportIndexFiles() {
    try {
      File root = ProjectRoot.find();
      File outDir = new File(root, "tool_exports/asset_index");

      // Rebuild fresh export using the scanner (includes missingInAtlas).
      AssetIndexScanner.ExportBundle bundle = AssetIndexScanner.buildExport(root, atlas);
      AssetIndexScanner.writeExport(outDir, bundle);
    } catch (Throwable ignored) {}
  }

}
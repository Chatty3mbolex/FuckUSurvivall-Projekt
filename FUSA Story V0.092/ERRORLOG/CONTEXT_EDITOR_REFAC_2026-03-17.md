# IST-STAND / PLAN / PROTOKOLL (für GPT-Wechsel)

**Projekt:** `FuckUSurvivall Projekt/FUSA Story V0.091`

**Datum:** 2026-03-17

---

## 1) IST-STAND (was aktuell implementiert ist)

### Debug-/Dev-Keybinds (zentral)
- Debug-Keybinds wurden aus `GameScreen.java`/`OptionsScreen.java` ausgelagert.
- Zentrale Steuerung: `core/src/main/java/com/yourgame/survival/debug/DebugCommands.java`
- Hook ist zentral in `SurvivalGame.render()`:
  - `core/src/main/java/com/yourgame/survival/SurvivalGame.java` → `DebugCommands.tick(this);`
- Debug-Mode Toggle: **Shift+F10** (Debug-Keys nur aktiv wenn Debug-Mode aktiv).
- Debug-Overlay ist **top-most** (über Day/Night + Fog etc.) und wird performant als **ein** Multiline-Draw gerendert.

### Area-Editor (repurposed `WorldEditorScreen`)
- `WorldEditorScreen.java` ist jetzt ein Area-Editor-Grundgerüst (kein alter Biome-Editor-UI-Klotz mehr).
- Area Scan/Load: Scan von `Gdx.files.internal("areas")` und UI-Dropdowns (Type/Area).
- New Area Create schreibt nach `Gdx.files.absolute("assets/areas")`.
- Kamera passt auf Area (fit/center/zoom) und Editor rendert Tiles + Entities-Preview.
- Marker-Overlay (ShapeRenderer) zeigt: playerSpawn, poi, nodes, enemyZones.

### UI “Schachteln” (verschachtelt, vertikal, geklemmt)
- Editor-UI wurde umgebaut: **keine** große Wrapper-Window mehr.
- Stattdessen: 5 einzelne Boxen (jeweils head/body/bottom), vertikal gestapelt, clamped am Bildschirm.
  1) Map loader (real verdrahtet)
  2) Terrain edit (verdrahtet)
  3) Markers + zones (Dummy)
  4) Entities + objects (Dummy)
  5) Mouse tools (Dummy)

### Terrain Edit (aktuell umgesetzt)
- Dynamische Ground-Tile Palette:
  - Liest Ground IDs dynamisch aus `TilesetRegions.ground(id)` (Atlas/Config erweiterbar).
  - **Keine Edges** in der Palette.
- Water Tool ist separat (Wasser ist Mask-Layer, kein groundId).
- Grüner Rahmen für aktive Auswahl (echter Border-Overlay, nicht nur Tint).
- Brush Size Slider: **1..9** (square brush).
- “Hold-to-paint”: LMB halten + Drag malt kontinuierlich.
- Cursor/Zoom korrekt: Map-Interaktion nutzt `viewport.unproject` → tile unter Cursor.
- UI-Klick-Schutz: Map-Paint wird geblockt wenn `stage.hit(...) != null`.

### In-Memory JSON Writeback (Editor)
- Terrain-Edits schreiben **sofort** in `areaJson` (in-memory), nicht nur PreviewWorld:
  - `layers.ground.patches[]` mit `{x,y,id}` (Default → Patch wird entfernt)
  - `layers.water.patches[]` mit `{x,y,v}` (on=1, off → Patch entfernt)
  - `layers.road.patches[]` mit `{x,y,v}` (on=1, off → Patch entfernt)
- `areaDirty` Flag existiert.

### HOME_01: TemplateId + Objects + Terrain + Trees
- `WorldMapRuntime.T_HOME = "HOME_01"` (Suffix-System).
- `assets/areas/HOME_01.area.json`:
  - `templateId` wurde auf `HOME_01` gesetzt.
  - `markers.objects[]` enthält LANDMARK_CASTLE + LANDMARK_BRIDGE (tile coords), `layerBias` vorhanden.
- Runtime Loader:
  - `JsonAreaWorldLoader.load(...)` lädt `markers.objects[]` (authored objects) jetzt auch im Runtime-Pfad.
  - Legacy HOME terrain shaping (dirt paths + rock ring + deco) ist als **tiles-only** wieder drin (damit HOME nicht regressiert).
  - HOME tile-trees: Presence Bits werden jetzt auch für HOME deterministisch gesetzt (vorher nur FOREST_01).
- Editor:
  - HOME tile-trees werden zusätzlich als **Overlay-Dreiecke** gezeigt (Editor hat keinen GameScreen, daher eigene deterministic presence mask).

### Layering (bestehendes System + Bias)
- Bestehendes Y-Sort bleibt.
- Erweiterung:
  - `Entities.layerBias[]` hinzugefügt.
  - `EntityRenderer` nutzt `groundY(...) + layerBias + tieBreaker`.

---

## 2) PLAN (was wir gerade machen / nächste Schritte)

### A) Terrain Edit fertigziehen
1. **Persistentes Speichern auf Disk**:
   - Save-Button im Map-loader (oder Hotkey) der `areaJson` als `.area.json` zurückschreibt.
   - (Aktuell: in-memory writeback + preview; Save to file fehlt noch.)
2. Road adjacency baking / roadMask4 Update:
   - Road currently setzt `roadMask4=15` nur für das Tile (minimal). Es fehlt das Update der Nachbarn.
3. Optional: Water/shore mask recompute für Preview (wenn nötig).

### B) Markers + Zones Box verdrahten
- Exklusiv: Marker vs Zone (grüner Rahmen nur bei aktivem Typ).
- Modus (Checkbox/Buttons): setzen, definieren, löschen, verschieben, anpassen.
- Zonen-Anpassung via Drag der Ränder; Änderungen bleiben sichtbar als “dirty” bis Save.

### C) Entities + Objects Box
- Kategorisierte Auswahl (POIs, Items, Livings ohne Animation, Static Objects, Buildables).
- Dropdown + Preview + Drag&Drop auf Map (hold+drag+release place).
- Cross-category Selection Reset: bei Wechsel wird alte Platzier-Auswahl komplett demarkiert.

### D) HOME legacy Terrain authoring (langfristig)
- Legacy HOME tile shaping ist aktuell wieder aktiv (damit nix fehlt).
- Langfristig: HOME terrain komplett in JSON (`layers.ground/road/water`) authoren → legacy shaping entfernen.

---

## 3) PROTOKOLL (letzte Aktivitäten / Änderungen)

### 3.1 Runtime/Loader
- `JsonAreaWorldLoader.load(...)` erweitert:
  - `applyAuthoredObjects(...)` läuft im Runtime-Pfad.
  - Procedural HOME landmark spawn entfernt (Landmarks kommen aus JSON `markers.objects[]`).
  - HOME terrain shaping wurde als tiles-only legacy zurückgeholt (dirt paths/rocks/deco).
  - HOME tile-tree Presence Bits werden jetzt deterministisch gesetzt (analog FOREST_01, aber sparser + grass-only).

### 3.2 Assets
- `assets/areas/HOME_01.area.json`:
  - `templateId: "HOME_01"`
  - `markers.objects[]` enthält Castle/Bridge

### 3.3 Editor UI
- Editor UI in Boxen aufgeteilt (head/body/bottom je Box), gestapelt + clamped.
- Terrain Edit:
  - dynamische Ground Palette, Water tool
  - grüner Rahmen per Pixmap-Overlay
  - Brush Size Slider 1..9
  - Hold-to-paint (drag)
  - Stage hit-test blockt map-paint auf UI
  - JSON writeback in `areaJson.layers.*.patches`

### 3.4 Bugfixes
- Compile error in JSON patch upsert:
  - `JsonValue.set(Integer)` war invalid → ersetzt durch `vv.set(new JsonValue(value))`.
- Runtime StackOverflow:
  - Ursache: Toggle-Buttons im New-Area-Dialog setChecked→changed Loop.
  - Fix: Guard Flag + only react when button becomes checked.

---

## 4) WICHTIGE HINWEISE / CURRENT KNOWN ISSUES
- **Save-to-disk fehlt** noch: Edits sind in-memory in `areaJson`, aber werden noch nicht in Datei geschrieben.
- Road editing: adjacency/roadMask4 Nachbarn werden noch nicht sauber gebacken.
- HOME tile-trees im Editor sind nur Overlay (keine authored nodes), Runtime nutzt TreeBits.
- VisUI Warnung wegen libGDX version mismatch (1.14.0 vs expected 1.13.5) ist vorhanden, aber nicht Teil des aktuellen Refactors.

---

## 5) Relevante Dateien
- Editor:
  - `core/src/main/java/com/yourgame/survival/editor/WorldEditorScreen.java`
- Loader:
  - `core/src/main/java/com/yourgame/survival/worldmap/JsonAreaWorldLoader.java`
- Tiles/Regions:
  - `core/src/main/java/com/yourgame/survival/render/TilesetRegions.java`
- HOME Asset:
  - `assets/areas/HOME_01.area.json`
- HOME Template constant:
  - `core/src/main/java/com/yourgame/survival/worldmap/WorldMapRuntime.java` (T_HOME)


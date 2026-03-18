# Detailliertes Sitzungs-Log – 2026-03-17 (Editor/Area-Refactor)

> Kontext-Datei für Nachvollziehbarkeit / Weiterarbeit.
> Projekt: `FuckUSurvivall Projekt/FUSA Story V0.091`

---

## 0) Ausgangslage / Ziel
- Area-Editor (repurposed `WorldEditorScreen`) so umbauen, dass er `.area.json` zuverlässig laden/bearbeiten/speichern kann.
- Debug-Keys zentralisieren.
- HOME → HOME_01 suffixfähig machen.
- Layering: bestehendes Y-Sort behalten, nur `layerBias` ergänzen.

---

## 1) Wichtige Änderungen (chronologisch, mit Gründen)

### 1.1 Debug-/Dev-Keybinds zentralisiert
- **Neu:** `core/.../debug/DebugCommands.java`
- **Hook:** `SurvivalGame.render()` ruft `DebugCommands.tick(this)`.
- **Shift+F10** toggelt Debug-Mode; Debug-Keys nur aktiv, wenn Debug-Mode aktiv.
- Debug-Overlay: top-most + performant (1 draw mit `\n`).

### 1.2 Editor: UI auf "Schachteln" umgebaut
- Vorher: 1 großes UI-Window.
- Nachher: 5 Boxen (je Box head/body/bottom), vertikal gestapelt und geclamped:
  1) Map loader
  2) Terrain edit
  3) Markers + zones
  4) Entities + objects (Dummy)
  5) Mouse tools

### 1.3 Terrain edit (Funktions-UI)
- Dynamische **Ground-Palette** aus `TilesetRegions.ground(id)` (keine edges).
- Wasser als eigenes Tool (Mask, kein groundId).
- **Grüner Rahmen** (Pixmap-Overlay) für selektiertes Tool.
- **Brush size 1..9** (Square).
- **Hold-to-paint**: LMB halten + Drag malt weiter.
- Map-Klick nutzt **`viewport.unproject`** (Cursor exakt, zoom-invariant).
- UI-Guard: Map-Interaktion nur, wenn Pointer nicht über UI-Boxen liegt.

### 1.4 In-memory JSON writeback
- Terrain-Edits schreiben sofort in `areaJson` (nicht nur Preview-World):
  - `layers.ground.patches[] {x,y,id}` (Default => patch entfernen)
  - `layers.road.patches[] {x,y,v}`
  - `layers.water.patches[] {x,y,v}`
- `areaDirty` Flag gesetzt.

### 1.5 Save-to-disk (Editor)
- **Neu:** Save Button + **Ctrl+S**.
- Speichert `areaJson.prettyPrint(...)` zurück auf Disk.
- HUD: zeigt `*DIRTY*`.

### 1.6 New-Area Dialog Bugfix (StackOverflow)
- Ursache: zwei Toggle-Buttons (`Known Type` / `New Type`) haben sich gegenseitig per `setChecked()` getriggert.
- Fix: Guard-Flag + Listener reagieren nur bei „wird checked“.

### 1.7 HOME_01: Runtime + Editor konsistent
- `WorldMapRuntime.T_HOME = "HOME_01"`.
- `assets/areas/HOME_01.area.json`:
  - `templateId` auf `HOME_01` gesetzt.
  - `markers.objects[]` für `LANDMARK_CASTLE` + `LANDMARK_BRIDGE` ergänzt.

### 1.8 Loader: authored objects im Runtime-Pfad
- `JsonAreaWorldLoader.load(...)` lädt jetzt `markers.objects[]` auch ingame.

### 1.9 Legacy HOME Terrain shaping (tiles-only) wieder aktiviert
- Kurzzeit-Fix: Dirt-Weg + Rock-Ring etc. waren nach Procedural-Removal weg.
- Lösung: HOME Terrain shaping als **tiles-only** beibehalten (Landmarks aber weiterhin aus JSON).

### 1.10 HOME tile-trees wiederhergestellt
- Problem: HOME hatte keine Bäume mehr, weil tile-tree presence bits nur für FOREST_01 gesetzt wurden.
- Fix:
  - Runtime: HOME bekommt deterministic tree presence bits (grass-only, clearing frei, kein road/water).
  - Editor: eigener deterministic Tree-Presence Overlay (weil Editor kein GameScreen hat).

### 1.11 Markers + zones Schachtel verdrahtet
- UI:
  - Zone buttons (aus `enemyZones[].kind`, fallback defaults)
  - Marker buttons: `PLAYER_SPAWN`, `POI:*`, `NODE:*`
- Exklusiv: Marker vs Zone (nie beides aktiv).
- Map-Aktionen schreiben in JSON:
  - Markers: place/delete
  - Zones: place/define/move/adjust/delete (drag-basierend)

### 1.12 Universal Mouse Tools Mode
- Problem: Mode war zuerst in Terrain und zusätzlich in Markers/Zones.
- Fix: Mode ist jetzt **universal** in der Mouse-tools Box (`toolMode`).
- Bei Mode-Wechsel: harte Reset-Regel -> keine 2 Tools gleichzeitig aktiv.

### 1.13 Nodes: manche nicht zeichnbar
- Ursache: Editor preview mapping war zu minimal (nur TREE/BUSH/ROCK/STUMP).
- Fix:
  - Markers UI listet jetzt **alle `EntityType.NODE_*`** (außer `NODE_TREE`).
  - Loader preview spawn nutzt `EntityType.valueOf(t)` + legacy fallbacks.

### 1.14 Preview-Sprites erscheinen sofort beim Setzen von nodes/poi
- Problem: JSON geändert, aber Entities für Preview wurden nur beim Reload erzeugt.
- Fix: beim Place/Delete wird das passende preview entity direkt gespawnt/gekilled.

### 1.15 MOVE als echtes Drag&Drop
- Mode=MOVE: click-hold-drag-release.
- Fix für „falsches Sprite bewegt sich“:
  - speichert den **konkreten entity index** (`movePreviewEntityIndex`) und bewegt genau diesen.

### 1.16 Save-Pfad-Mismatch (HOME ingame ≠ Editor)
- Problem: Editor schrieb ggf. nach `assets/areas` (relativ zum CWD), während Runtime aus `internal("areas")` las.
- Fix:
  - `resolveAreasWriteDir()` bevorzugt jetzt `Gdx.files.internal("areas")` wenn es ein schreibbarer Ordner ist (write probe).
  - Save/Create Status zeigt absoluten Pfad.

### 1.17 Burg/Castle MOVE nicht pickbar
- Ursache: MOVE pickte nur via exakte tile (x/y). Große sprites werden selten genau am tile getroffen.
- Fix:
  - world-space entity hit-test (`findPreviewEntityHit`) als Fallback
  - danach Matching zurück auf `markers.objects[]` über (type + tile)

---

## 2) Aktueller Stand (was jetzt funktioniert)
- Terrain malen + brush + hold-to-paint + JSON writeback + Save.
- Marker/Zone erstellen/löschen + Zones define/move/adjust + Save.
- MOVE: nodes/poi/objects dragbar, richtiges sprite bewegt sich.
- HOME im Editor und ingame konsistent (Save-Pfad fix + HOME trees + legacy terrain).

---

## 3) Offene Punkte / Nächste Schritte
- **Entities + objects Schachtel**: Katalog/Dropdown, Preview, Drag&Drop placement, layerBias UI.
- Road/water adjacency baking im Editor (Mask-neuberechnung) – aktuell „on/off“ minimal.
- Vollständige „alles authoren“ Migration: HOME legacy terrain shaping irgendwann in JSON layers überführen (Langfristig).

---

## 4) Relevante Dateien
- Editor: `core/src/main/java/com/yourgame/survival/editor/WorldEditorScreen.java`
- Loader: `core/src/main/java/com/yourgame/survival/worldmap/JsonAreaWorldLoader.java`
- Tileset mapping: `core/src/main/java/com/yourgame/survival/render/TilesetRegions.java`
- HOME asset: `assets/areas/HOME_01.area.json`


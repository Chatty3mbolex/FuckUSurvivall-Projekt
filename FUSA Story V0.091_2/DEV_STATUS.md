# FuckUSurvivALL — Entwicklungsstand (Stand: 2026-02-26)

## 1) Terrain/Transitions (DONE)
- Pair-Transitions entfernt.
- Terrain-Übergänge laufen über Overlap/Transparency-Edges: `edge_<material>_<mask>.png`.
- Wasser/Lava sind Bottom Tiles (keine Pair-Transitions).

Kernstellen (historisch umgesetzt):
- `core/src/main/java/com/yourgame/survival/render/ChunkRenderer.java` zeichnet Overlap-Edges via `computeCornerMask(...)` + `tiles.edgeX(mask)`
- `core/src/main/java/com/yourgame/survival/render/TilesetRegions.java` lädt Edge-Regionen (edge_grass/dirt/sand/rock/snow)
- TileEditor Transition-UI/Logik entfernt

---

## 2) TileEditor — Edge-Workflow Umbau (IN PROGRESS)
Aktuelle Arbeitsversion:
- `C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\FuckUSurvivALL V0.079`

Hauptdatei mit den meisten Änderungen:
- `core/src/main/java/com/yourgame/survival/screens/TileEditorScreen.java`

### 2.1 Features (Stand V0.079)
- Edit Target Umschaltung: `GROUND` vs `EDGE`
- Edge-PNG Ziel: `assets/atlas/src/edge_<mat>_<mask>.png`
- Candidate Workflow: Folder → Liste → Preview → Stage → Create/Replace/Delete/Apply-to-SRC
- Oben rechts: 2 Raster/Grids + Material-Dropdowns
- Drag&Drop: CandidatePreview → IST Preview staget Candidate (V0.078)
- Rotate/Flip wirkt nur auf IST Preview; Persistenz über Apply-to-SRC (V0.078)

### 2.2 Raster/Masken Fixes (Stand V0.079)
- Candidate-Grid: 6×5
- Mask-Selector: 0..25
- `& 15` Trunkierungen entfernt; clamp auf 0..25
- Staged-Overlay im Grid deterministisch (raw stagedCandidate laden; nicht an candidatePreview-State gekoppelt)

Hinweis:
- Ingame-Nutzung ist weiterhin primär Corner4 0..15; 16..25 sind aktuell Editor/Reserve.

---

## 3) Dirt Tiles / Next Big Step (IN KLÄRUNG)
Arbeitsordner:
- `C:\Users\kuehn\Desktop\DIRT` enthält neue Dirt Tiles (32×32).

Problem erkannt:
- Runtime zeichnet pro Maske aktuell nur ein Overlay (`edge_dirt(mask)`), dadurch kann man nicht gleichzeitig "straight" und "edge" Varianten haben.

Entscheidung:
- Renderer soll später auf 2 Overlay-Layer erweitert werden (Straight + Edge gleichzeitig).
- Noch nicht implementiert; steht als nächstes Feature an (wieder im 5-Request Prozess).

---

## 4) Versionen (kurz)
- V0.074: Basis nach Transition-Cutover
- V0.075: Edge-Edit Target im TileEditor begonnen
- V0.076/V0.077: Grid/MapPreview + Compile-Fixes + Layout-Korrekturen
- V0.078: Drag&Drop + 32×32 Write-Guardrails + Rotate nur IST
- V0.079: Mask 0..25 + 6×5 Grid + deterministisches Overlay + remove `&15`

---

## 5) Offene ToDos (Priorität)
1) Renderer 2-Layer Dirt Overlays (straight + edge) inkl. TilesetRegions Loader + Asset-Namensschema
2) Sauberes Mapping/Import der neuen Dirt Tiles in die vorgesehenen Slots (inkl. Reserve 16/17)
3) Build/Run erst nach "BUILD OK", dann minimal fixen

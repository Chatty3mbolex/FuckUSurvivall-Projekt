# AREA SYSTEM PLAN — PNG→Area Pipeline + Corner-Mask Baking
## Version: 1.0 — Author: Claude (für FUSA Story)

---

## Ziel

Ein PNG-Bild (Gebietslayout) wird in eine fertige, spielbare Area konvertiert.
Die Area enthält: Ground-Tiles, Corner-Mask Transitions, Straßen (deterministisch
aus Seed in der Road-Zone), Nodes, Ork-Spawn-Zonen, POIs.

Beim Laden im Spiel werden Corner-Masks einmalig aus dem Ground-Grid berechnet.
Kein Noise-Sampling, kein BiomeClassifier, kein EdgeBlender.

---

## Pipeline-Übersicht

```
PNG + Legende + Seed
        │
        ▼
  [convert_area.py]     ← Offline-Tool (Python + Pillow)
        │
        ▼
  AREA_ID.area.json     ← Kompaktes JSON: Ground-Grid, Road-Zone, Nodes, Zonen, POIs
        │
        ▼
  [JsonAreaWorldLoader]  ← Java Runtime: lädt JSON, setzt Chunks, bakt Corner-Masks
        │
        ▼
  Spielbare Area         ← Ground + Transitions + Roads + Entities
```

---

## Farbpalette (Legende)

| Farbe              | RGB-Bereich           | Bedeutung          | Ground-ID |
|--------------------|-----------------------|--------------------|-----------|
| Braun (dunkel)     | R:160-190 G:90-110    | Dirt (Waldboden)   | 1         |
| Braun (hell/tan)   | R:170-200 G:130-150   | Grass-Area         | 0         |
| Rot                | R>180 G<110 B<100     | Road-Zone          | (mask)    |
| Grün (gefüllt)     | R<130 G>140 B<90      | Tree-Marker        | (node)    |
| Grün (Kreis-Linie) | R<120 G>160 B<120     | Ork-Spawn-Zone     | (zone)    |
| Gelb               | R>190 G>190 B<150     | POI (Hidden Chest) | (poi)     |
| Transparent        | A<10                  | Ignoriert          | —         |

---

## Geänderte / Neue Dateien

### Neues Tool
- `tools/convert_area.py` — Ersetzt area_from_layout_forest.py. Generisch für jede PNG.

### Neues Batch-Script
- `CONVERT_AREA.bat` — Fragt nach PNG, konvertiert, deployt Area-JSON.

### Java — Geändert
- `JsonAreaWorldLoader.java` — Corner-Mask-Baking + Road-Adjacency-Baking nach dem Laden
- `ChunkRenderer.java` — Road heightLevel=0 Fix + Road-Tile Skip bei Cliff-Schatten

### Java — Unverändert (aber Legacy-markiert)
- `DefaultEdgeGroundBlender.java` — Wird nicht mehr aufgerufen für Area-basierte Welten
- `DefaultTransitionMaskBuilder.java` — Wird nicht mehr aufgerufen für Area-basierte Welten
- `FinalTileSampler.java` — Wird nicht mehr aufgerufen für Area-basierte Welten

---

## Corner-Mask-Baking (im Java-Loader)

Nach dem Setzen aller Ground-Tiles wird pro Chunk:

1. Für jedes Tile: Lese groundId
2. Für jeden Edge-Typ (Grass, Dirt, Sand, Rock, Snow):
   - Prüfe TransitionRules.allowTransition(base, target)
   - Berechne 4-Corner Marching-Squares Mask aus den 4 Nachbar-Tiles
   - Schreibe in grassCornerMask16 / dirtCornerMask16 / etc.
3. Für Road-Tiles: Berechne roadMask4 (4-Nachbar Adjacency)
4. Für Road-Tiles: Setze heightLevel=0

Keine Noise-Calls. Keine globale Sampling-Funktion. Rein Grid-basiert.

---

## Fixes die gleichzeitig reingehen

1. **Road heightLevel=0** — Road-Tiles bekommen heightLevel=0 nach dem Laden
   → Behebt den "schwebende Straße" Bug

2. **Road Cliff-Skip** — ChunkRenderer überspringt Cliff-Schatten auf Road-Tiles
   → Zusätzliche Absicherung

3. **Road-Breite** — convert_area.py erosiert die Road-Zone (morphologisch)
   um eine schmalere, natürlichere Straße zu erzeugen

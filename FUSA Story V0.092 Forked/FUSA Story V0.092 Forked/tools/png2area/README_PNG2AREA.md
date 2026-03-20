# PNG → Area (png2area)

Dieses Paket ist der **Offline-Generator** für FUSA-`Area`-JSONs.

## Was ist das?

- Input: eine `Gebietslayout*.png` (Layout-Bild) + eine **Area-ID**
- Output: `assets/areas/<AREA_ID>.area.json`
- Das Tool läuft **ohne** Game/LibGDX (kein „Gamescreen“ nötig). Es ist ein reiner Converter.

## Start / Verwendung

### 1) Schnellstart (Windows)

- Im Projekt-Root: `CONVERT_AREA.bat` doppelklicken
  - Dieser Starter delegiert an: `tools\png2area\CONVERT_AREA.bat`

Das Script fragt:
- Welche `Gebietslayout*.png`?
- Welche `Area-ID`?

und schreibt dann:
- `assets\areas\<AREA_ID>.area.json`

### 2) Direkt via Python

```bat
python tools\png2area\convert_area.py tools\png2area\Gebietslayout_forest.png FOREST_01
```

Optionaler Output-Pfad:

```bat
python tools\png2area\convert_area.py tools\png2area\Gebietslayout_forest.png FOREST_01 --out assets\areas\FOREST_01.area.json
```

## Abhängigkeiten

- **Python 3**
- Python-Paket **Pillow**

Installation:

```bat
pip install Pillow
```

## Enthaltene Dateien

- `CONVERT_AREA.bat` – interaktiver Starter (PNG auswählen, Area-ID eingeben)
- `convert_area.py` – Universal-Converter (PNG → `*.area.json`)
- `Gebietslayout*.png` – Layout-Inputs (inkl. Legende)
- `debug_right_margin.png` – Debug/Referenzbild für Generator-Arbeit
- `area_from_layout_forest.py` – Spezial-Converter/Referenz für `FOREST_01`
- `make_gebietslayout.py` – erzeugt/aktualisiert `Gebietslayout.png` aus Atlas/Assets (Generator-Helper)

## Wichtige Hinweise

- Das Erzeugen der JSON macht eine Area **noch nicht automatisch im Game verfügbar**.
  Du musst sie entweder:
  - in der Runtime registrieren (z.B. WorldMap/Registry), oder
  - eine bestehende Area-JSON ersetzen.

- Backups/alte Layouts gehören nicht in die Runtime – aber die Layout-PNGs in diesem Ordner sind **Generator-Inputs** und sollen hier bleiben.

# FUSA Patch — Claude Session 2026-03-12

## Anleitung
Kopiere den Inhalt von `FUSA Story V0.091/` über dein bestehendes Projekt.
Die Dateien ersetzen nur die geänderten Files — alles andere bleibt unberührt.

## Geänderte Dateien (8 Java + 1 Python + 1 Batch)

### Bug 1: Ork-Klassentreffen (Orks wandern alle in eine Ecke)
- `entity/Entities.java` — Neue Felder: homeX, homeY, wanderRadius
- `systems/EncounterSpawner.java` — Setzt Home-Position beim Spawn
- `systems/AiSystem.java` — Zone-Leash: Orks bleiben in ihrer Zone

### Bug 2+3: Straße zu breit + schwebt visuell
- `worldmap/JsonAreaWorldLoader.java` — Road heightLevel=0 + Road-Adjacency Baking
- `render/ChunkRenderer.java` — Cliff-Schatten auf Roads übersprungen

### Bug 4: Edge/Transition-System neu (Area-basiert)
- `worldmap/JsonAreaWorldLoader.java` — Corner-Mask Baking aus Ground-Grid
- `tools/convert_area.py` — Neuer universeller PNG→Area Converter
- `CONVERT_AREA.bat` — Externer Helfer für Konvertierung

### Sprite-Orientierung: Waffen/Tools verdreht
- `render/EntityRenderer.java` — West-Offset korrigiert, South-Doppel-Mirror entfernt, Swing-Arc richtungsabhängig

### Objektgrößen konsistent (Proportionen)
- `tuning/TuningEntities.java` — Alle Draw-Sizes aus Sprite-Analyse korrigiert
- `entity/EntityMetrics.java` — drawH() für nicht-quadratische Sprites erweitert

## Neue Dateien
- `tools/convert_area.py` — PNG→Area Converter (Python 3 + Pillow)
- `CONVERT_AREA.bat` — Doppelklick-Helfer für Area-Konvertierung
- `AREA_SYSTEM_PLAN.md` — Architekturplan neues Area-System
- `BUG_NOTES.md` — Alle Bugs dokumentiert mit Ursache und Fix

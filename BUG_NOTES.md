# FUSA — Gesammelte Bugs & Fix-Notizen
## (Arbeitsnotizen für Claude — nicht ins Projekt einchecken)

---

### BUG 1: Ork-Klassentreffen (FIXED in lokaler Kopie)
**Problem:** Alle Orks wandern nach links oben und sammeln sich in einer Zone.
**Ursache:** Entities haben keine Home-Position. Wander-Logik hat keinen Anker/Rückholmechanismus.
**Fix:** 
- `Entities.java`: homeX[], homeY[], wanderRadius[] Felder hinzugefügt
- `EncounterSpawner.java`: Setzt Home-Position + wanderRadius (8 Tiles) beim Spawn
- `AiSystem.java`: Zone-Leash im Wander-Modus — ab 80% Radius sanfter Pull Richtung Home

---

### BUG 2: Straßen zu breit
**Problem:** Straßen wirken visuell viel zu breit.
**Ursache (vermutlich):** 
- Road-Sprites (road_mask_*.png) gehen Edge-to-Edge (32x32 voll ausgefüllt, kein Rand)
- A*-Pathing erzeugt an Kreuzungen/Abzweigungen 2-3 Tiles breite Bereiche
**Mögliche Fixes:**
- Road-Sprites mit Inset/Rand redesignen (schmalere Straße innerhalb des Tiles)
- Oder: Pathing nachbearbeiten um überflüssige Breite zu entfernen
**Status:** FIXED —
  - JsonAreaWorldLoader: bakeRoadAdjacencyAndFixHeight() setzt heightLevel=0 für Road-Tiles
  - ChunkRenderer: Cliff-Schatten werden auf Road-Tiles übersprungen
  - convert_area.py: Road-Zone wird morphologisch erodiert (1 Iteration) für natürlichere Breite

---

### BUG 4: Edges / Terrain-Transitions massiv falsch
**Problem:** Seit Anfang an falsch — so gravierend dass prozedurale Weltgen umgebaut werden musste.

**Analyse-Ergebnis — Mehrere Probleme gefunden:**

**4a) Edge-Blending ändert den GROUND selbst, nicht nur das Overlay**
- `DefaultEdgeGroundBlender.blend()` überschreibt `l.groundId[idx]` direkt
- `FinalTileSampler.finalGroundIdAt()` tut dasselbe
- D.h.: Wenn ein Grassland-Tile nahe einem Desert-Biome liegt, wird der Ground
  selbst zu Sand/Dirt geändert, nicht nur eine Edge-Transition drübergelegt
- Das erzeugt einen BREITEN Streifen geänderter Ground-Tiles an jeder Biome-Grenze
  (bis zu edgeWidthTiles=6 Tiles breit, Cap=32!)
- Die Corner-Mask-Transitions (grass/dirt/sand/rock/snow) sehen dann die GEÄNDERTEN
  groundIds und erzeugen nochmal Overlays darauf → doppelter Effekt

**4b) Edge-Blending sucht nur in 4 Richtungen (N/E/S/W), nicht diagonal**
- `DefaultEdgeGroundBlender` prüft Nachbar-Biome nur in N, dann E, dann S, dann W
- Reihenfolge: N wird bevorzugt! Wenn N einen anderen Biome findet, wird sofort
  geblended. E/S/W werden dann gar nicht geprüft
- → Systematischer Nord-Bias in der Edge-Richtung
- → Diagonale Biome-Grenzen sehen stufig/treppenartig aus

**4c) cornerInsideLocal() Threshold ist hart (>=2 von 4)**
- `cornerInsideLocal()` returned 1 wenn >=2 der 4 Nachbar-Tiles den Target-Ground haben
- Das ist ein sehr binärer Schwellwert — entweder voll an oder voll aus
- Keine Abstufung, kein weicher Übergang
- Bei Biome-Grenzen die nicht exakt an Tile-Grenzen liegen: harte Treppen

**4d) TransitionRules sind asymmetrisch und teilweise widersprüchlich**
- GRASS darf nicht auf DIRT gezeichnet werden (allowTransition GRASS→DIRT = false)
- Aber DIRT darf auf GRASS gezeichnet werden (DIRT→GRASS = true)
- Gleichzeitig ändert der EdgeBlender aber den Ground SELBST zu Grass an Biome-Grenzen
- → Die Edge-Overlays und der Ground-Override arbeiten gegeneinander

**4e) edgeWidthTiles=6 Default ist viel zu breit**
- Standard: 6 Tiles Blending-Band pro Biome
- Cap: 32 Tiles (!)
- Bei zwei benachbarten Biomen mit je 6 Tiles Edge: 12 Tiles breiter Übergangsbereich
- Das frisst kleine Biome-Inseln komplett auf

**Kern-Problem zusammengefasst:**
Das Edge-System hat zwei unabhängige Mechanismen die gegeneinander arbeiten:
1. EdgeGroundBlender → ändert den Ground-Typ selbst (breiter Streifen)
2. TransitionMaskBuilder → legt Corner-Mask-Overlays auf Basis der (bereits geänderten) Grounds
Ergebnis: Matschige, zu breite, richtungsverzerrte Übergänge.

**Status:** FERTIG — Neues Area-System implementiert:
  - `tools/convert_area.py`: Universeller PNG→Area Converter mit Corner-Mask Prebaking
  - `CONVERT_AREA.bat`: Externer Helfer (fragt nach PNG, konvertiert, deployt)
  - `JsonAreaWorldLoader.java`: Corner-Mask-Baking aus Ground-Grid + JSON-Import
  - `JsonAreaWorldLoader.java`: Road-Adjacency-Baking + heightLevel=0 Fix
  - `ChunkRenderer.java`: Cliff-Schatten werden auf Road-Tiles übersprungen
  - `AREA_SYSTEM_PLAN.md`: Architekturplan dokumentiert

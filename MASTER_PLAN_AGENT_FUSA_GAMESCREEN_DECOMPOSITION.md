# AGENT MASTER PLAN — GAMESCREEN DECOMPOSITION / FUSA STORY V0.092 FORKED

## 0. Autorität / Geltungsbereich

**Arbeitswurzel:**
```text
C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\FUSA Story V0.092 Forked\FUSA Story V0.092 Forked
```

**Zusatzzwang:**
```text
C:\Users\kuehn\Desktop\Chatty Projekt\workflow.md
```

**Wahrheitsregel:**
- Dieser Plan ist die operative Wahrheit für den Agenten.
- Der Workflow ist bindend und ergänzt diesen Plan.
- Bei Konflikt gilt:
  1. Sicherheits-/Blocker-Regeln
  2. No-Compile / No-Run / No-Commit / Offline Only
  3. Expliziter Refactor-Umfang aus Phase 5/6/7
  4. Alles andere

---

## 1. Eingebettete Zwangsregeln aus dem Auftrag

Diese Regeln sind **nicht optional** und müssen im Agentenlauf als harte Constraints geführt werden:

- Arbeite **nur** im genannten Projektpfad.
- Nutze zusätzlich `workflow.md` als bindenden Ablaufrahmen.
- **OFFLINE ONLY**.
- **Verboten:** Kompilieren, Run/Starten, Debuggen, Committen, GitHub, Web, externe APIs.
- **Nicht den Menschen ansprechen** während der Abarbeitung.
- Checkpoints gehen **nicht** an den Menschen, sondern in lokale Memory-/Protokolldateien.
- Nach **jedem Step** Memory/State/Logs aktualisieren:
  - was zuletzt getan wurde
  - warum es getan wurde
  - was als Nächstes dran ist
- **6s-Timer-Regel** einhalten:
  - Zwischen zwei LLM-/Agenten-Reasoning-Zyklen mindestens 6 Sekunden Cooldown.
  - In dieser Zeit nur deterministische lokale Script-/Dateioperationen.
- LLM-Calls **stark bündeln**.
- Scripts nur lokal/offline.
- Jedes Script **vor Ausführung vollständig prüfen**:
  - Pfad
  - Eingaben
  - Ausgabe
  - Seiteneffekte
  - Windows-/Shell-Kompatibilität
- Detailliert arbeiten.
- Nicht ablenken lassen.
- **Nie einer Spur nachgehen**, die vom Refactor-Ziel wegführt.
- Analysen detailliert und kleinteilig; keine groben Repo-Block-Fetches.
- Refactor erzeugt unvermeidlich Folgefehler; nur die beheben, die **direkt** durch den aktuellen geplanten Schritt verursacht wurden.
- Keine eigenen Routinen erfinden, die vom ersten Ziel abweichen.
- Keine unkontrollierten Subroutinen starten.

---

## 2. Operative Interpretation von „Memory“

Da der Agent offline und ohne Mensch-Ansprache arbeiten soll, bedeutet „Memory“ in diesem Plan:

- **lokale persistente Projekt-Memory-Dateien**
- **nach jedem Schritt aktualisiert**
- **vor jedem neuen Schritt erneut gelesen**
- **einzige Resume-/Checkpoint-Quelle**

Der Agent darf **nicht** auf implizites Kurzzeitgedächtnis vertrauen.

---

## 3. Agent-Arbeitsordner für Protokollierung

Im Projektroot anlegen:

```text
_agent/
```

Darin zwingend:

```text
_agent/
  00_RULES_LOCK.md
  01_REQUIREMENTS.md
  02_NON_GOALS.md
  03_ANALYSIS_LOG.md
  04_DEPENDENCY_MAP.md
  05_BLOCKERS.md
  06_RISKS.md
  07_CHANGESET_LOG.md
  08_SANITY_CHECKS.md
  09_ROLLBACK.md
  10_RESUME.md
  11_STEP_REPORTS.md
  12_FILE_TOUCH_MATRIX.md
  13_SYMBOL_MAP.md
  14_SAVE_COMPAT_NOTES.md
  15_INVARIANTS.md
  state.json
  checklist_todo.md
  checklist_done.md
  next_step.txt
  last_action.txt
  tools/
  backups/
  snapshots/
```

### Pflichtinhalt
`00_RULES_LOCK.md`
- die harten Verbote
- Projektpfad
- Planname
- Timer-Regel
- Stop-Regeln
- Phase 5/6/7 Reihenfolge

`15_INVARIANTS.md`
- keine Compile-/Run-/Debug-Aktionen
- Save-Format 100% kompatibel
- GameLoop-Reihenfolge unverändert
- keine zusätzlichen Features
- keine Seitensprünge
- keine ungeplanten Dateiänderungen
- keine direkten Mensch-Checkpoints

---

## 4. `state.json` — Pflichtschema

```json
{
  "projectRoot": "C:\\Users\\kuehn\\Desktop\\Chatty Projekt\\FuckUSurvivall Projekt\\FUSA Story V0.092 Forked\\FUSA Story V0.092 Forked",
  "planName": "AGENT MASTER PLAN — GAMESCREEN DECOMPOSITION",
  "status": "INIT",
  "currentPhase": "PHASE_5",
  "currentStep": "5.2",
  "goal": "GameScreen decomposition and leak/tuning follow-up without compile/run",
  "constraints": [
    "OFFLINE_ONLY",
    "NO_COMPILE",
    "NO_RUN",
    "NO_DEBUG",
    "NO_COMMIT",
    "NO_WEB",
    "NO_HUMAN_CHECKPOINTS",
    "6S_REASONING_COOLDOWN"
  ],
  "invariants": [
    "SAVE_FORMAT_100_PERCENT_COMPATIBLE",
    "GAMELOOP_ORDER_UNCHANGED",
    "NO_SCOPE_CREEP",
    "ONLY_PLANNED_FILES",
    "STATIC_SANITY_ONLY"
  ],
  "lastCompletedStep": "",
  "currentObjective": "",
  "lastAction": "",
  "lastWhy": "",
  "nextAction": "",
  "blockers": [],
  "attemptCounters": {
    "mechanical": 0,
    "stepFixLoop": 0,
    "scriptValidation": 0
  },
  "touchedFiles": [],
  "pendingFiles": [],
  "stepReports": [],
  "resumeTrigger": "READ__agent__10_RESUME.md + state.json + checklist_todo.md + checklist_done.md + 15_INVARIANTS.md BEFORE ANY ACTION"
}
```

---

## 5. Secure Loops — verbindliche Schleifenlogik

„Repeat until good“ wird hier **sicher und begrenzt** umgesetzt.  
Keine unendlichen Schleifen. Kein blindes Weiterprobieren.

## Loop A — Resume / Re-anchor Loop
**Immer vor jedem Arbeitsblock.**

1. Lies in dieser Reihenfolge:
   - `_agent/00_RULES_LOCK.md`
   - `_agent/15_INVARIANTS.md`
   - `_agent/state.json`
   - `_agent/10_RESUME.md`
   - `_agent/checklist_todo.md`
   - `_agent/checklist_done.md`
   - `_agent/05_BLOCKERS.md`
   - letzten Eintrag in `_agent/11_STEP_REPORTS.md`

2. Prüfe:
   - Ist `status` blockiert?
   - Ist `currentStep` gesetzt?
   - Gibt es offene Blocker?
   - Stimmen `nextAction` und `checklist_todo.md` überein?

3. Wenn Inkonsistenz:
   - **kein Code**
   - erst Protokolle korrigieren
   - Status auf `RECONCILE_STATE`

4. Nur wenn Zustand sauber:
   - weiter zu aktuellem Step

**Pass-Bedingung:** alle Resume-Dateien stimmen überein.  
**Fail-safe:** bei Widerspruch kein Edit, nur Memory-Reparatur.

---

## Loop B — Analyse-Stabilisierung
**Vor jedem neuen Step.**

Ziel: exakte Eingrenzung, keine groben Repo-Rundumschläge.

1. Identifiziere den Step.
2. Suche **gezielt** nur nach den Symbolen dieses Steps.
3. Ergänze:
   - Definitionen
   - Call-Sites
   - direkt abhängige Felder
   - direkt abhängige Helper
   - Reihenfolgeabhängigkeiten
4. Schreibe alles in:
   - `13_SYMBOL_MAP.md`
   - `04_DEPENDENCY_MAP.md`
   - `03_ANALYSIS_LOG.md`
5. Wiederhole den Scan, bis zwei aufeinanderfolgende Analyse-Pässe **keine neuen relevanten Symbole** mehr liefern.

**Pass-Bedingung:** stabiler Symbol-/Abhängigkeitsstand in zwei aufeinanderfolgenden Pässe.  
**Fail-safe:** wenn relevante Information offline nicht ermittelbar oder mehrdeutig ist → `BLOCKED_NEEDS_HUMAN_SPEC`, in `05_BLOCKERS.md` eintragen, Status stoppen.

---

## Loop C — Plan-zu-Step-Kohärenz
Vor jedem Edit:

1. Prüfe, ob der aktuelle Step in `checklist_todo.md` exakt enthält:
   - Ziel
   - betroffene Dateien
   - geplante Sub-Edits
   - Sanity-Checks
   - Rollback
2. Prüfe, ob jeder Requirement-Punkt auf mindestens einen Step gemappt ist.
3. Prüfe, ob jeder Edit **nur** der aktuellen Step-Zielmenge dient.

**Pass-Bedingung:** vollständige Abdeckung, keine offenen ungemappten Ziele.  
**Fail-safe:** kein Edit; Plan-/Checklist-Korrektur.

---

## Loop D — Edit/Sanity/Repair Loop
Pro Step, pro Datei.

1. Vor Edit:
   - Snapshot in `_agent/backups/`
   - Dateipfad in `12_FILE_TOUCH_MATRIX.md`
2. Edit durchführen
3. Sofort statische Sanity-Checks ausführen
4. Wenn Sanity fehlschlägt:
   - lokalen Fixversuch
   - erneut prüfen
5. Maximal **3 Reparaturzyklen pro mechanischem Problem**
6. Wenn nach 3 Reparaturen nicht sauber:
   - Rollback dieser Datei aus Backup
   - Step auf `BLOCKED_STEP_FIX_LIMIT`
   - exakte Ursache in `05_BLOCKERS.md`
   - keine Spekulation

**Pass-Bedingung:** alle Step-Sanity-Checks grün.  
**Fail-safe:** file-local rollback, dann Step-stop.

---

## Loop E — Memory Update Loop
**Nach jedem erfolgreich abgeschlossenen Step zwingend.**

Aktualisieren:
- `state.json`
- `last_action.txt`
- `next_step.txt`
- `10_RESUME.md`
- `11_STEP_REPORTS.md`
- `07_CHANGESET_LOG.md`
- `08_SANITY_CHECKS.md`
- `checklist_done.md`
- `checklist_todo.md`

Pflichtinhalt:
- was getan
- warum getan
- welche Dateien
- welche Risiken geprüft
- welche Sanity-Checks bestanden
- was als Nächstes kommt
- welche Invarianten weiter gelten

**Pass-Bedingung:** alle Memory-Dateien konsistent.  
**Fail-safe:** kein nächster Step, bis Memory konsistent ist.

---

## Loop F — 6s-Timer-Regel
Zwischen zwei Agenten-/Reasoning-Zyklen:

- 6 Sekunden Minimum-Cooldown
- erlaubt nur:
  - Dateilesen
  - Script-Review
  - Script-Ausführung ohne LLM
  - Log-/State-Update

Nicht erlaubt:
- neuer großer Analyseblock
- unkontrollierter neuer Editblock
- spontane Nebenspur

---

## 6. Script-Regeln

Alle Scripts liegen nur hier:

```text
_agent/tools/
```

### Zulässige Script-Arten
Nur deterministische Hilfsskripte:

- Symbol-/Callsite-Scanner
- Dateilisten-/Touch-Matrix-Generator
- Save-Key-String-Extraktor
- Diff-Zusammenfassung
- Import-/Referenz-Check
- Line-count-Check
- Protokoll-/State-Updater

### Verbotene Script-Arten
- Compiler/Build-Skripte
- Run-/Launch-Skripte
- Git-/Commit-Skripte
- automatische „Fix all“-Skripte
- Repo-weite Massenumbauten
- unkontrollierte Codegenerierung

### Script-Freigabeprozess
Vor der ersten Ausführung jedes Scripts:

1. Script vollständig lesen
2. Zweck dokumentieren
3. Inputs/Outputs dokumentieren
4. Seiteneffekte dokumentieren
5. Windows-Kompatibilität prüfen
6. in `03_ANALYSIS_LOG.md` Freigabe eintragen
7. erst dann ausführen

Wenn Script unklar ist:
- nicht ausführen
- Blocker setzen

---

## 7. Globale Invarianten für Phase 5/6/7

Diese Invarianten dürfen nie verletzt werden:

1. Save-Format 100% kompatibel
2. Tick-/Update-Reihenfolge bleibt gleich
3. Input-Verhalten bleibt logisch gleich
4. WorldMap-Travel-Reihenfolge bleibt gleich
5. UI-State-Mutationen bleiben funktionsgleich
6. Renderer rendert, aber steuert nicht
7. Day/Night Music **nicht** in Renderer
8. Keine Wrapper-/Zwischencontroller-Mimikry
9. Keine Seitensprünge in unrelated Bugs
10. Nur statische Verifikation, kein Compile/Run

---

# 8. PHASE 5 — GAMESCREEN DECOMPOSITION

## STEP 5.2 — SaveLoadController extrahieren

### Ziel
`screens/SaveLoadController.java` anlegen und genau diese Verantwortungen aus `GameScreen` extrahieren:

- `doSave()`
- `doLoad()`
- `clearAllStateForLoad()`

### Harte Regeln
- Save-Format **unverändert**
- keine Key-Änderung
- keine File-/Slot-/Serializer-Änderung
- keine neue Save-Architektur
- keine Reordering-Spielereien in Save/Load-Folgen

### Voranalyse
Der Agent muss vor dem ersten Edit exakt erfassen:
- alle Save/Load-Methoden
- alle String-Keys/Literal-Keys
- alle beteiligten Felder
- alle Reset-/Clear-Zustände
- Call-Sites
- Reihenfolge vor/nach `clearAllStateForLoad()`
- alle Serialisierungshelfer

### Betroffene Dateien
Mindestens:
- `screens/GameScreen.java`
- `screens/SaveLoadController.java`

Zusätzlich nur nach fundierter Analyse:
- direkte Save-/Serializer-Helferdateien
- keine anderen Files ohne Nachweis

### Sichere Unterreihenfolge
1. Analyse nur Save/Load-Symbole
2. `SaveLoadController.java` anlegen
3. Methoden 1:1 übernehmen
4. Abhängigkeiten sauber injizieren
5. GameScreen zuerst auf Delegation umstellen
6. Sanity prüfen
7. Altcode erst löschen, wenn Delegation stabil ist
8. Sanity erneut prüfen
9. Logs/State aktualisieren

### Sanity-Checks
- alle alten Save-Key-Strings gegen neue Vorkommen vergleichen
- keine Key-Differenz
- `doSave`, `doLoad`, `clearAllStateForLoad` existieren nur dort, wo geplant
- GameScreen delegiert statt doppelte Logik zu behalten
- keine neue Save-API entstanden
- Call-Reihenfolge textuell unverändert dokumentiert in `14_SAVE_COMPAT_NOTES.md`

### Fail-safe
Wenn auch nur ein Save-Key/Reset-Abschnitt nicht eindeutig zuordenbar ist:
- **kein Edit**
- Blocker schreiben
- Step stoppen

---

## STEP 5.3 — ProjectileCombatController extrahieren

### Ziel
`screens/ProjectileCombatController.java` anlegen und extrahieren:

- Arrow-Arrays
- Bow/RMB/Sneak-State
- `tickArrows()`
- `arrowHitSegment()`
- `onKill()`

### Harte Regeln
- keine Combat-Neulogik
- keine Balance-Änderung
- keine neuen Combat-Systeme/Eventbusse
- `onKill()` nur Ort ändern, nicht Bedeutung

### Voranalyse
Der Agent erfasst vorher exakt:
- Arrow-States
- Bow-/RMB-/Sneak-Flags
- alle Methoden, die `tickArrows()` direkt/indirekt nutzen
- alle `onKill()`-Folgen:
  - XP
  - Drops
  - Loot
  - Skill-/Quest-/Stat-Folgen
  - UI-/FX-/Audio-Folgen, wenn direkt daran gekoppelt

### Betroffene Dateien
Mindestens:
- `screens/GameScreen.java`
- `screens/ProjectileCombatController.java`

Optional nur bei nachgewiesener Direktkopplung:
- konkrete Klassen mit direkt referenzierten Combat-Feldern

### Sichere Unterreihenfolge
1. Arrow-Symbolmap
2. Controller anlegen
3. Arrays/State verschieben
4. `tickArrows()`
5. `arrowHitSegment()`
6. `onKill()` zuletzt
7. Delegation
8. Altcode-Löschung
9. Sanity

### Sanity-Checks
- keine Doppeldefinition von Arrow-State
- alle Call-Sites zeigen auf Controller
- `onKill()`-Seiteneffekte textuell identisch zu vorheriger Kette dokumentiert
- keine Combat-Konstanten geändert
- keine neue Abhängigkeit auf GameScreen direkt, wenn vermeidbar

### Fail-safe
Wenn `onKill()` ungeklärte Nebeneffekte hat:
- nicht erraten
- Analyse nachziehen
- wenn weiter unklar: Blocker

---

## STEP 5.4 — PlayerController extrahieren

### Ziel
`screens/PlayerController.java`

Extrahieren:
- Mouse-world tracking
- `SK_*` Konstanten
- Player-State
- `updateMouseWorld()`
- `collectInputSnapshot()`
- `actionReach()`
- `pickupNearby()`
- weitere direkt spielernahe Input-/Interaktionsmethoden

### Harte Regeln
- keine Cursor-/Input-Neuerfindung
- keine Reach-/Pickup-Balanceänderung
- keine Umdeutung der `SK_*`-Konstanten

### Voranalyse
- Definitionen und Nutzung aller `SK_*`
- Mouse->World-Transform-Pfade
- Input-Snapshot-Felder
- Pickup-/Reach-Reihenfolge
- Abhängigkeiten zu UI/Combat/WorldMap

### Sichere Unterreihenfolge
1. Mouse-world tracking
2. Input snapshot
3. Reach
4. Pickup
5. restlicher Player-State
6. Delegation
7. Altcode-Löschung
8. Sanity

### Sanity-Checks
- keine doppelten Player-State-Felder
- `collectInputSnapshot()`-Call-Reihenfolge dokumentiert
- `actionReach()` und `pickupNearby()` nur verschoben, nicht verändert
- keine Cursor-/Crosshair-Arbeit außerhalb Scope

### Fail-safe
Wenn Player-Input mit UI/WorldMap-State untrennbar verknotet ist:
- explizit dokumentieren
- nur minimalen sicheren Extraktionsschnitt machen
- keinen Generalumbau

---

## STEP 5.5 — WorldMapController extrahieren

### Ziel
`screens/WorldMapController.java`

Extrahieren:
- Worldmap drawing
- Travel
- Area API
- `EnemyZone`
- alle worldmap-spezifischen Detailabläufe

### Harte Regeln
- keine zweite Weltverwaltung bauen
- Area/Travel nicht semantisch ändern
- keine Entkopplung „auf Verdacht“

### Voranalyse
- Worldmap-Zeichenpfad
- Travel-Trigger
- Area-Wechsel-Folgen
- EnemyZone-Zugriffe
- Kopplung an Save/Load/UI/DayNight/Renderer

### Sichere Unterreihenfolge
1. Worldmap Symbolmap
2. Controller anlegen
3. Draw-Methoden
4. Travel-Methoden
5. Area-API
6. EnemyZone-bezogene Teile
7. Delegation
8. Altcode-Löschung
9. Sanity

### Sanity-Checks
- keine zweite Area-State-Quelle
- Travel-Methoden nur verlagert
- WorldMap-Zeichnen und Travel bleiben im selben logischen Ablauf
- keine direkten WorldMap->GameScreen-Imports hinzufügen, wenn unnötig

### Fail-safe
Wenn Weltzustandsbesitz unklar wird:
- Stop
- Dependency Map ergänzen
- keinen Architektur-Sprung machen

---

## STEP 5.6 — GameUiController extrahieren

### Ziel
`screens/GameUiController.java`

Extrahieren:
- alle UI Panels
- Menüs
- HUD-Methoden
- UI-State-Felder
- `SkillMenuRow`

### Harte Regeln
- UI zeigt/steuert, aber übernimmt keine versteckte Kernlogik
- keine Redesigns
- keine neuen UI-Systeme

### Voranalyse
- alle Panel-/HUD-/Menu-Methoden
- UI-State-Felder
- Input-Blockierung durch UI
- `SkillMenuRow`-Abhängigkeiten

### Sichere Unterreihenfolge
1. HUD
2. Panels
3. Menus
4. UI-State-Felder
5. `SkillMenuRow`
6. UI-Input-Routing
7. Delegation
8. Altcode-Löschung
9. Sanity

### Sanity-Checks
- alle UI-State-Felder nur an einer Stelle definiert
- HUD/Panel/Menu-Aufrufe delegieren sauber
- UI blockiert Input wie zuvor
- `SkillMenuRow` nicht halb in GameScreen verbleiben lassen

### Fail-safe
Wenn eine Methode gleichzeitig UI und Kernlogik mischt:
- minimalen sicheren Schnitt machen
- Rest dokumentieren
- nicht schönrefactoren

---

## STEP 5.7 — GameRenderer extrahieren

### Ziel
`screens/GameRenderer.java`

Extrahieren:
- Kamera
- Batch-/Renderfluss
- Day/Night mask
- Fog
- Debug overlay rendering

### Harte Regeln
- Renderer rendert, steuert aber nicht
- Day/Night **MUSIC bleibt draußen**
- keine State-Policy in Renderer verschieben

### Voranalyse
- Renderreihenfolge
- batch begin/end-Struktur
- Kamera-Zugriffe
- Fog/Mask/Overlay-Zugriffe
- Day/Night-Audio-Kopplungen markieren und explizit **nicht** verschieben

### Sichere Unterreihenfolge
1. Renderer-Symbolmap
2. Controller/Renderer-Datei anlegen
3. Kamera-/Batch-Pfade
4. Day/Night mask + fog
5. debug overlay
6. Delegation
7. Altcode-Löschung
8. Sanity

### Sanity-Checks
- keine Audio-/Music-Steuerung im Renderer
- Batch-/Render-Reihenfolge textuell dokumentiert
- Kamera-/Fog-/Mask-Code nur verschoben
- keine State-Mutation, die nicht Rendering ist

### Fail-safe
Wenn Methode sowohl rendert als auch steuert:
- spalten
- nur Renderteil in Renderer
- Steuerteil bleibt außerhalb oder wird separat protokolliert

---

## STEP 5.8 — Finales GameScreen Cleanup

### Ziel
GameScreen reduziert auf Orchestrator-Rolle.

Soll noch enthalten:
- Controller-Referenzen
- Initialisierung/Verdrahtung
- Tick-/Render-Reihenfolge
- Input-Routing
- Lifecycle

Soll **nicht** mehr enthalten:
- Altlogik der extrahierten Bereiche
- tote Delegationsreste
- doppelte State-Felder
- tote Helper aus alten Pfaden

### Pflichtchecks
- Cross-Refs prüfen
- doppelte Felder löschen
- tote Methoden löschen
- unnötige Imports entfernen
- Zielbereich Zeilenzahl dokumentieren

### Sanity-Checks
- `GameScreen.java` Line Count erfassen
- keine Altmethoden von 5.2–5.7 mehr aktiv
- nur Delegation/Orchestrierung bleibt
- keine Wrapper-Reste

### Fail-safe
Wenn Cleanup Gefahr läuft, Verhalten zu ändern:
- Cleanup minimal halten
- nur tote/duplizierte Reste entfernen
- keine „schöne“ Umstrukturierung

---

# 9. PHASE 6 — RESOURCE LEAK FIX

## STEP 6.1 — `render/TilesetRegions.java` Audit

### Ziel
Jede Pixmap-/Texture-Erzeugung auf Ownership und Dispose absichern.

### Voranalyse
- alle `new Pixmap`
- alle `new Texture`
- indirekte Erzeuger
- Lebensdauer
- Besitzer

### Sanity-Checks
- jede Creation mit dokumentiertem Besitzer
- jeder temporäre Pfad mit dokumentiertem Dispose
- keine Dispose-Aufrufe auf Objekte, die noch benutzt werden

### Fail-safe
Wenn Ownership unklar:
- dokumentieren
- nicht blind disposen

---

## STEP 6.2 — `tools/asseteditor/ui/MiniSkin.java` Audit

### Ziel
Alle manuell erzeugten Ressourcen sauber disposen oder Ownership dokumentieren.

### Voranalyse
- Fonts
- Pixmaps
- Textures
- Drawables
- NinePatch
- Skin-interne Ressourcen

### Sanity-Checks
- Dispose-Kette dokumentiert
- kein doppelt disposebares Objekt
- keine unbesessenen Ressourcen

---

## STEP 6.3 — `render/AtlasLoader.java` + Call-Sites

### Ziel
TextureAtlas-Lebensdauer eindeutig machen oder dokumentieren.

### Sanity-Checks
- wer lädt
- wer besitzt
- wer entsorgt
- keine Mehrfachentsorgung
- keine Lecks durch mehrfaches Laden

### Fail-safe
Wenn Ownership nicht sicher vereinheitlicht werden kann:
- dokumentieren
- keinen riskanten Dispose einbauen

---

# 10. PHASE 7 — MAGIC NUMBERS

## STEP 7.1 — `TuningUi.java`

### Ziel
Echte UI-Layout-Konstanten aus `GameUiController` extrahieren.

### Harte Regeln
- nur Layout-/Abstands-/Panel-/HUD-Werte
- keine Gameplay-Werte
- keine Semantikänderung

### Sanity-Checks
- jede extrahierte Zahl ist wirklich UI-Tuning
- keine Dateiformat-/Sentinel-/Arraygrößen versehentlich verschoben

---

## STEP 7.2 — `TuningCombat.java`

### Ziel
Echte Combat-Tuning-Konstanten aus `ProjectileCombatController` extrahieren.

### Harte Regeln
- nur Reichweiten / Geschwindigkeiten / Cooldowns / Combat-Werte
- keine Strukturkonstanten
- keine Verhaltensänderung

### Sanity-Checks
- jede Zahl ist klar Combat-Tuning
- keine Save-/Format-/Index-/Sentinel-Werte versehentlich extrahiert

---

# 11. Verifikations-Checkliste am Ende

Diese Checks sind erst nach Abschluss von Phase 5/6/7 als Gesamtcheck zulässig:

- `GameScreen.java < 2500 lines`
- keine Wrapper-Controller mehr
- kein direkter GameScreen-Import in worldmap/debug
- `"Nicht fertiges Feature"` in `.java` = 0
- `docs/FEATURE_BACKLOG.md` vollständig erhalten
- Resource leaks dokumentiert/gefixt
- Magic numbers wie geplant extrahiert

Zusätzliche statische Gesamtchecks:
- nur geplante Dateien geändert
- keine ungeplanten neuen Klassen
- keine toten Doppelmethoden
- keine doppelte Zustandsquelle für dieselben Felder
- `state.json`, Logs, Checklisten vollständig

---

# 12. Blocker-/Stop-Mechanik

Da der Agent den Menschen nicht direkt ansprechen soll, wird „call human“ aus dem Workflow so operationalisiert:  
Blocker **schriftlich persistent notieren und Lauf stoppen**. Keine Spekulation. Keine Weiterarbeit.

## Stop-Klassen
- `BLOCKED_NEEDS_HUMAN_SPEC`
- `BLOCKED_STEP_FIX_LIMIT`
- `BLOCKED_SCRIPT_UNSAFE`
- `BLOCKED_STATE_INCONSISTENT`
- `BLOCKED_SAVE_COMPAT_RISK`

## Pflicht bei Blocker
Aktualisieren:
- `05_BLOCKERS.md`
- `state.json`
- `10_RESUME.md`
- `next_step.txt`

Format in `05_BLOCKERS.md`:
- Step
- exakter Blocker
- warum blockiert
- was bereits geprüft wurde
- welche Dateien/Symbole betroffen
- welche sichere nächste Aktion nur nach Klärung möglich wäre

Dann **hart stoppen**.

---

# 13. Maximale Protokollierung — Pflichtformat nach jedem Step

In `11_STEP_REPORTS.md` immer:

```text
STEP: 5.2
STATUS: DONE / BLOCKED / ROLLED_BACK
WHY:
FILES TOUCHED:
FILES READ:
SYMBOLS ANALYZED:
CHANGES MADE:
SANITY CHECKS PASSED:
RISKS CHECKED:
ROLLBACK PATH:
NEXT STEP:
```

In `07_CHANGESET_LOG.md` immer:
- Datei
- Änderungstyp
- Zweck
- zugehöriger Step
- ob rein extrahiert / delegiert / gelöscht / dokumentiert

In `08_SANITY_CHECKS.md` immer:
- Checkname
- Step
- Ergebnis
- Belegpfad / Suchmuster

---

# 14. Konkrete Resume-Trigger

In `_agent/10_RESUME.md` muss nach jedem Step ein maschinenlesbarer Abschnitt stehen:

```text
CURRENT_PHASE=PHASE_5
CURRENT_STEP=5.3
STATUS=READY
LAST_COMPLETED_STEP=5.2
NEXT_ACTION=Analyze arrow arrays, bow/rmb/sneak state, tickArrows, arrowHitSegment, onKill
READ_FIRST=_agent/00_RULES_LOCK.md;_agent/15_INVARIANTS.md;_agent/state.json;_agent/checklist_todo.md;_agent/checklist_done.md
DO_NOT_DO=compile,run,debug,commit,web,scope_creep,user_checkpoint
```

Zusätzlich in `next_step.txt` exakt **eine** nächste Aktion.

---

# 15. Minimaler Datei-Touch-Ansatz

Für jeden Step gilt:
- nur Step-Dateien
- nur direkt nachgewiesene Abhängigkeitsdateien
- keine „vorsorglichen“ Änderungen
- jede zusätzliche Datei muss vor dem Edit in `12_FILE_TOUCH_MATRIX.md` begründet werden

Format:
```text
STEP | FILE | WHY_TOUCHED | PROVED_BY_SYMBOL
```

---

# 16. Was der Agent ausdrücklich nicht tun darf

- nicht kompilieren
- nicht starten
- nicht debuggen
- nicht committen
- nicht Browsen/Web
- keine unrelated Fehler jagen
- nicht „bei Gelegenheit“ verbessern
- keine neuen Systeme einführen
- keine Event-Busse, Services, DTO-Rewrites, Architektur-Beautification
- keine Batch-Grobscans des ganzen Projekts
- keine menschlichen Checkpoint-Nachrichten

---

# 17. Operative Reihenfolge

Exakt diese Reihenfolge:

1. Arbeitsordner `_agent/` anlegen
2. Rules/State/Checklist/Resume-Struktur anlegen
3. Phase 5.2
4. Phase 5.3
5. Phase 5.4
6. Phase 5.5
7. Phase 5.6
8. Phase 5.7
9. Phase 5.8
10. Phase 6.1
11. Phase 6.2
12. Phase 6.3
13. Phase 7.1
14. Phase 7.2
15. Gesamtverifikation
16. finales statisches Handoff

Keine andere Reihenfolge.

---

# 18. Letzte operative Regel

Wenn der Agent merkt, dass er gerade:
- rät
- abstrahiert statt prüft
- über das Ziel hinausbaut
- einen unrelated Fehler verfolgt
- Protokollierung überspringt
- den Timer ignoriert

dann sofort:
- **Edit stoppen**
- `03_ANALYSIS_LOG.md` + `10_RESUME.md` aktualisieren
- auf den letzten validen Zustand zurückspringen
- erst dann weiter

# IMPLEMENT_ZQS.md — Implementationsworkflow (PHASE_07)

> Zweck: Exakter Umsetzungs- und Resume-Workflow, damit nicht geraten wird.
> Basis: Flow-4 Workflowplan, `MODEL_ZQS.md`, `SAVE_ZQS.md`, `GENERATOR_ZQS.md`, `TEXT_ZQS.md`, `FILES_ZQS.md`.

---

## 07.01 — Reihenfolge (bindend)

Der Agent implementiert ZQS strikt in dieser Reihenfolge:

1) **Workflow-Dateien** (PHASE_00) — bereits vorhanden
2) **Analyse** (PHASE_01) — Fakten + Lückenanalyse vorhanden
3) **Datenmodelle** (PHASE_02) — `MODEL_ZQS.md`
4) **Save-Struktur** (PHASE_03) — `SAVE_ZQS.md`
5) **Text-DB-Struktur** (PHASE_05) — `TEXT_ZQS.md` + `assets/data/zqs/text_snippets_de_DE_v1.json`
6) **Generatorstruktur** (PHASE_04) — `GENERATOR_ZQS.md`
7) **QuestHistory** (Implementierung + Save/Load)
8) **QuestLog-Erweiterung** (Status/Logbuchnummer)
9) **SaveManager-Erweiterung** (ZQS SaveBlock)
10) **GameScreen/WQG-Docking** (Integration; keine Logik im NPC)
11) **Abschlusskontrolle** (PHASE_08)

---

## 07.02 — Pflichtaktionen nach jedem Schritt

Nach jedem abgeschlossenen Schritt (auch Teil-Schritt) sind Pflicht:

1) `CHECKLIST_ZQS.md` aktualisieren
2) `STATE_ZQS.json` aktualisieren:
   - `active_phase`
   - `active_step`
   - `last_completed_step`
   - `next_step`
   - `current_focus_file`
3) `DECISIONS_ZQS.md` ergänzen, **falls** eine Architekturentscheidung getroffen wurde, die im Code umgesetzt ist.
4) Bei Fehlern: `ERRORS.md` ergänzen.
5) Dummy-Pflicht: wenn etwas laut Konzept fehlt und nicht im Scope dieses Schritts liegt:
   - im Code `// DUMMY SPACE (NNN) ...` setzen
   - in `DUMMIES_ZQS.md` eintragen.

---

## 07.03 — Resume-Protokoll (vor jeder neuen Arbeitssitzung)

Vor jeder Sitzung muss in dieser Reihenfolge gelesen werden:

1) `READ_FIRST_ZQS.md`
2) `STATE_ZQS.json`
3) `CHECKLIST_ZQS.md`
4) `ERRORS.md`
5) `flow 4/zqs_master_alignment_v_1_1.md`
6) danach die aktuell betroffene fachliche Datei (`MODEL_ZQS.md` / `SAVE_ZQS.md` / `TEXT_ZQS.md` / `GENERATOR_ZQS.md` / `FILES_ZQS.md`)
7) danach die zuletzt bearbeitete Code-Datei

Dann exakt bei `next_step` weiterarbeiten.

---

## 07.04 — Verbotene Neustart-Aktion

Der Agent darf nicht erneut „den Plan überlegen“ oder den nächsten Schritt raten.
Er arbeitet exakt die Statusdatei `STATE_ZQS.json.next_step` ab.

---

## Nächster Implementations-Block (Start Produktion)

Ab jetzt (nach PHASE_07) beginnt die Produktion strikt nach 07.01.

### Startpunkt (konkret)
- Implementiere die **ZQS Save-Struktur** im Code zuerst (ZqsSaveBlock + ZqsSaveIO) gemäß `SAVE_ZQS.md`.
- Danach Loader/Registries gemäß `FILES_ZQS.md`.

**Hinweis:** Kein Kompilieren/Starten/Debuggen/Committen.

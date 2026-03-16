# CHECKLIST_ZQS.md

## PHASE_00 – Workflow-Fundament
- [x] 00.01 ERRORS.md lesen/anlegen
- [x] 00.02 Workflow-Dateien anlegen
- [x] 00.03 STATE_ZQS.json initial setzen
- [x] 00.04 Plan in READ_FIRST_ZQS.md referenzieren
- [x] 00.05 Checklist vollständig befüllen (diese Datei wird stufenweise erweitert)

## PHASE_01 – Ist-Code Analyse
- [x] 01.01 Relevante Dateien lesen und ANALYSE_ZQS.md füllen
- [~] 01.02 Faktlisten/Tabellen erzeugen (teilweise in ANALYSE_ZQS.md enthalten)
- [~] 01.03 Lückenanalyse schreiben (teils in Chat-Analyse; noch zu konsolidieren)

## PHASE_02 – Datenmodell definieren
- [x] 02.01 Katalogmodell definieren (MODEL_ZQS.md)
- [x] 02.02 Knowledge-Modell definieren (MODEL_ZQS.md)
- [x] 02.03 Blueprint-Modell definieren (MODEL_ZQS.md)
- [x] 02.04 Objective-Modell definieren (MODEL_ZQS.md)
- [x] 02.05 Reward-Modell definieren (MODEL_ZQS.md)
- [x] 02.06 GeneratedQuest-Modell definieren (MODEL_ZQS.md)
- [x] 02.07 QuestHistory-Modell definieren (MODEL_ZQS.md)

## PHASE_03 – Persistenzmodell
- [x] 03.01 Save-Block definieren (SAVE_ZQS.md)
- [x] 03.02 Unterblöcke definieren (SAVE_ZQS.md)
- [x] 03.03 Pflichtfelder pro Block (SAVE_ZQS.md)
- [x] 03.04 Migration/Defaults (SAVE_ZQS.md)

## PHASE_04 – Generatorlogik
- [x] 04.01 Inputs definieren (GENERATOR_ZQS.md)
- [x] 04.02 Filter/Erzeugungskette (GENERATOR_ZQS.md)
- [x] 04.03 deterministische Quest-ID (GENERATOR_ZQS.md)
- [x] 04.04 Wiederholungslogik (GENERATOR_ZQS.md)

## PHASE_05 – Textsystem
- [x] 05.01 Kategorien (TEXT_ZQS.md)
- [x] 05.02 Kontextparameter (TEXT_ZQS.md)
- [x] 05.03 Auswahlregeln (TEXT_ZQS.md)
- [x] 05.04 Zusammensetzung (TEXT_ZQS.md)

## PHASE_06 – Datei-Aufteilung
- [x] 06.01 neue Dateien (FILES_ZQS.md)
- [x] 06.02 bestehende Dateien (erlaubte Erweiterungen) (FILES_ZQS.md)
- [x] 06.03 Zweckbeschreibung je Datei (FILES_ZQS.md)

## PHASE_07 – Implementationsworkflow
- [x] 07.01 Reihenfolge (IMPLEMENT_ZQS.md)
- [x] 07.02 Nach jedem Schritt Statusdateien (IMPLEMENT_ZQS.md)
- [x] 07.03 Resume-Regel (IMPLEMENT_ZQS.md)
- [x] 07.04 Kein Neu-Planen (IMPLEMENT_ZQS.md)

### Produktion (07.01 Schritte)
- [x] Step1 Save-Struktur (ZqsSaveBlock/ZqsSaveIO + SaveManager + GameScreen)
- [x] Step2 Text-DB Struktur (quest/zqs/text/*)
- [x] Step3 Generatorstruktur (runtime types + generator scaffolding)
- [x] Step4 QuestHistory (history index + save/load codec)
- [x] Step5 QuestLog-Erweiterung (Status/Logbuchnummer)
- [x] Step6 SaveManager-Erweiterung (nur falls noch Lücken)
- [x] Step7 GameScreen/WQG-Docking (Integration, keine Logik im NPC)

## PHASE_08 – Abschlusskontrolle
- [x] 08.01 Vollständigkeit (FINALCHECK_ZQS.md)
- [x] 08.02 Scope (FINALCHECK_ZQS.md)
- [x] 08.03 Doku (FINALCHECK_ZQS.md)

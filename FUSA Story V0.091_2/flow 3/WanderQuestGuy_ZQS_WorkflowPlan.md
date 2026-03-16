# WanderQuestGuy / ZQS – Exakter Workflow-Plan

## Zweck

Dieses Dokument steuert ausschließlich den Arbeitsablauf.
Es definiert **wie** ein KI-Agent arbeiten muss.
Es definiert **nicht** die fachliche Questlogik im Detail.

Die fachliche Questlogik steht in `zqs_wander_quest_guy_plan1.0.md`.
Die Textlogik steht in `zqs_textpool_szenarien_analyse1.md`.
Die verbindende Dokumentordnung steht in `zqs_master_alignment_v_1_1.md`.

## Verbindliche Lesereihenfolge

1. `zqs_master_alignment_v_1_1.md`
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_textpool_szenarien_analyse1.md`
4. dieses Dokument

Erst danach darf gearbeitet werden.

## Unverhandelbare Agent-Regeln

1. Nicht kompilieren.
2. Nicht starten.
3. Nicht debuggen.
4. Nicht in Git committen.
5. Nicht nach anderen Fehlern suchen.
6. Keine eigenständigen Refactors.
7. Keine zusätzliche Architektur erfinden, die nicht in den vier ZQS-Dokumenten steht.
8. Vor jeder Arbeitseinheit zuerst `ERRORS.md` lesen.
9. Falls `ERRORS.md` nicht existiert: anlegen und den Status `Noch keine Fehler dokumentiert.` eintragen.
10. Nie mehr als 3-mal denselben Fehler machen. Nach dem 3. Fehlversuch: sofort stoppen, in `ERRORS.md` dokumentieren, Statusdateien aktualisieren.
11. Keine massenhaften Mini-LLM-Anfragen. Änderungen müssen blockweise erfolgen.
12. Der Agent darf nicht selbst nach dem nächsten Schritt raten. Er muss immer aus den Statusdateien lesen, wo weiterzumachen ist.
13. Es darf nicht vom Plan abgewichen werden.
14. Es darf nichts außerhalb des beschriebenen Umfangs geändert werden.
15. Der Agent muss den Code selbst schreiben. Keine Platzhalter-Kommentare wie „TODO später“ an Stellen, die laut Plan fertig sein müssen.

## Pflicht-Dateien für den Workflow

Diese Dateien sind Arbeitsgrundlage und müssen im Projektwurzelpfad liegen.

### `READ_FIRST_ZQS.md`
Kurze Operator-Datei mit:
- Ziel des Systems
- absoluten Verboten
- aktueller Arbeitsphase
- Verweis auf diesen Plan

### `STATE_ZQS.json`
Maschinenlesbarer Status.

Pflichtfelder:

```json
{
  "plan_name": "WanderQuestGuy_ZQS_WorkflowPlan",
  "active_phase": "PHASE_00",
  "active_step": "00.01",
  "status": "not_started",
  "last_completed_step": "",
  "next_step": "00.01",
  "current_focus_file": "",
  "allowed_to_compile": false,
  "allowed_to_commit": false,
  "max_repeat_same_error": 3,
  "error_repeat_counter": {},
  "notes": ""
}
```

### `CHECKLIST_ZQS.md`
Checkbox-Liste aller Phasen und Schritte.

### `ANALYSE_ZQS.md`
Nur Fakten aus dem Ist-Code. Keine Wünsche. Keine Spekulation.

### `ERRORS.md`
Fehlerprotokoll mit:
- Timestamp
- Phase
- Schritt
- betroffene Datei
- Fehlerbeschreibung
- Wiederholungszähler
- nächster Versuch oder Abbruch

### `DECISIONS_ZQS.md`
Darin stehen nur bewusst festgelegte Architekturentscheidungen, sobald sie umgesetzt wurden.

## Zielarchitektur als Arbeitsblöcke

Die fachliche Struktur des ZQS wird in der Umsetzung in diese Blöcke zerlegt:

1. Kataloge
2. Knowledge-State
3. Quest-Blueprints
4. Regelmodell
5. Reward-Modell
6. Textbaustein-DB
7. Quest-Instanzierung
8. Quest-Historie
9. Questlog-Struktur
10. NPC-Docking

Die inhaltliche Definition dieser Blöcke steht **nicht** hier, sondern in den anderen drei ZQS-Dokumenten.

## Exakte Umsetzungsphasen

### PHASE_00 – Workflow-Fundament anlegen

Ziel: kontrollierbare Arbeitsumgebung herstellen.

#### Schritt 00.01
`ERRORS.md` lesen.

Abbruchregel:
Wenn die Datei fehlt, nicht weiterarbeiten, bevor sie angelegt wurde.

#### Schritt 00.02
Falls nicht vorhanden, anlegen:
- `READ_FIRST_ZQS.md`
- `STATE_ZQS.json`
- `CHECKLIST_ZQS.md`
- `ANALYSE_ZQS.md`
- `DECISIONS_ZQS.md`
- `ERRORS.md`

#### Schritt 00.03
`STATE_ZQS.json` initial setzen:
- `active_phase = PHASE_00`
- `active_step = 00.03`
- `status = in_progress`

#### Schritt 00.04
Diesen Plan in `READ_FIRST_ZQS.md` referenzieren.

#### Schritt 00.05
`CHECKLIST_ZQS.md` vollständig mit allen Phasen und Schritten befüllen.

#### Kontrolle PHASE_00
Fertig nur, wenn alle 6 Workflow-Dateien vorhanden sind.

### PHASE_01 – Ist-Code analysieren und dokumentieren

Ziel: nur reale Grundlagen dokumentieren.

#### Schritt 01.01
Alle für ZQS relevanten Dateien lesen und in `ANALYSE_ZQS.md` dokumentieren.

#### Schritt 01.02
Aus diesen Dateien Faktlisten erstellen:
- reale Runtime-Items
- reale Runtime-Harvestables
- asset-definierte Livings
- runtime-aktive Livings
- aktuell vorhandene Questdatenfelder
- aktuell gespeicherte Questdatenfelder
- aktuell fehlende Pflichtfelder für ZQS

#### Schritt 01.03
Eine harte Lückenanalyse schreiben:
- was schon existiert
- was nicht existiert
- was erweitert werden muss
- was komplett neu erstellt werden muss

#### Kontrolle PHASE_01
Fertig nur, wenn `ANALYSE_ZQS.md` keine Spekulationen enthält und jede Aussage auf konkrete Datei- oder Feldbasis zurückführbar ist.

### PHASE_02 – Datenmodell des ZQS definieren

Ziel: vollständige Soll-Struktur festlegen, bevor Code geschrieben wird.

#### Schritt 02.01 – Katalogmodell definieren
Exakt festlegen, welche Katalogtypen es gibt und welche Pflichtfelder sie tragen.

#### Schritt 02.02 – Knowledge-Modell definieren
Exakt festlegen:
- welche Known-Sets existieren
- wie sie gespeichert werden
- wann ein Eintrag als bekannt gilt

#### Schritt 02.03 – Quest-Blueprint-Modell definieren
Pflichtfelder festlegen:
- `blueprintId`
- `objectiveFamily`
- `allowedTargetKinds`
- `amountRules`
- `minPlayerLevel`
- `maxPlayerLevel` optional
- `weight`
- `rewardProfileId`
- `textProfileId`
- `repeatRules`

#### Schritt 02.04 – Objective-Modell definieren
Pflichtfelder:
- `objectiveType`
- `targetType`
- `targetId`
- `targetAmount`
- `comparator / completion rule`
- optionale Bedingungen

#### Schritt 02.05 – Reward-Modell definieren
Pflichtfelder:
- `rewardType`
- `itemId` optional
- `currency amount` optional
- `xp amount` optional
- `scaling rule`

#### Schritt 02.06 – GeneratedQuest-Modell definieren
Pflichtfelder:
- `generatedQuestId`
- `blueprintId`
- `sourceNpcId` optional
- `objective tree`
- `reward block`
- `generated text ids`
- `logbookEntryId`
- `logbookEntryNo`
- `status`
- `generatedAt`
- `offeredAt`
- `acceptedAt`
- `completedAt`

#### Schritt 02.07 – QuestHistory-Modell definieren
Pflichtlisten definieren:
- `seen quest ids`
- `active quest ids`
- `completed quest ids`
- `expired quest ids`
- `declined quest ids` optional

#### Kontrolle PHASE_02
Fertig nur, wenn jedes Pflichtfeld schriftlich definiert ist.

### PHASE_03 – Persistenzmodell exakt festlegen

Ziel: vorab definieren, was gespeichert werden muss.

#### Schritt 03.01
Neuen Save-Block für ZQS definieren.

#### Schritt 03.02
Exakt festlegen, welche Unterblöcke der Save enthält:
- `catalogs snapshot` nur falls nötig
- `player knowledge`
- `blueprint state` wenn nötig
- `player quest db`
- `quest history`
- `quest log entries`
- `numbering counters`
- `rng state`

Zusatzregel:
- `OfferBuffer` / aktive Angebote am NPC sind nicht persistent
- nicht angenommene NQ werden verworfen und nicht gespeichert

#### Schritt 03.03
Für jeden Block Pflichtfelder dokumentieren.

#### Schritt 03.04
Migrationsregel für alte Saves festlegen:
- wenn Block fehlt -> saubere Defaults
- keine Kompilierung
- keine stillen Datenverluste

#### Kontrolle PHASE_03
Fertig nur, wenn ein Agent daraus Save-/Load-Code schreiben könnte, ohne nachzufragen.

### PHASE_04 – Generatorlogik definieren

Ziel: exaktes Zusammenspiel der Generatorblöcke festlegen.

#### Schritt 04.01 – Eingaben definieren
Der Generator darf nur diese Inputs verwenden:
- Spielerlevel
- Known-State
- vorhandene Kataloge
- Quest-Historie
- offene Questanzahl
- Logbuchzähler
- Weltstress
- Spielzeit
- `SL_ID / StoryLinePhase`
- `Storytiefe`
- `HQTrigger`
- `active_hq_count`

#### Schritt 04.02 – Zusammenspiel der Generatorblöcke definieren
Kein zweites lineares Fach-Reihensystem formulieren.
Der Workflow muss hier nur festhalten, welche Pflichtblöcke gemeinsam arbeiten und im Masterplan bereits abgesichert sind:
- Storyzustand
- Known-State
- Questbestand / offene Quests
- Timer / Trigger
- Zielsuche
- Blueprint-Wahl
- Wiederholungsregeln
- Gewichtung
- Zielmenge
- Belohnung
- Weltstress
- Textparameter
- Quest-ID
- `OfferBuffer` bei nicht angenommener NQ
- `PlayerQuestDB` erst bei Annahme

#### Schritt 04.03 – ID-Regel definieren
Die Quest-ID darf nicht frei erfunden sein.
Sie muss deterministisch aus Kernparametern entstehen.

#### Schritt 04.04 – Wiederholungslogik definieren
Exakt festlegen:
- wann dieselbe Questfamilie erneut erlaubt ist
- wann derselbe Target-Block erneut erlaubt ist
- wann etwas nie erneut erlaubt ist

#### Kontrolle PHASE_04
Fertig nur, wenn das Zusammenspiel der Generatorblöcke ohne zweite Fachlogik dokumentiert ist.

### PHASE_05 – Textsystem definieren

Ziel: Text nicht hartcodiert mischen, sondern formal beschreiben.

#### Schritt 05.01
Textbaustein-Kategorien definieren:
- `greeting`
- `assignment`
- `reward`
- `farewell`

Zusatzregel:
- jede Kategorie besteht aus `main -> middle -> end`

#### Schritt 05.02
Kontextparameter definieren:
- ingame time bucket
- player condition bucket
- world stress bucket
- active quest count bucket
- completed quest count bucket

#### Schritt 05.03
Regeln für Textauswahl definieren:
- Pflichtfilter
- optionale Gewichtung
- Fallback bei leerer Trefferliste

#### Schritt 05.04
Textzusammensetzung definieren:
- `greeting = main -> middle -> end`
- `assignment = main -> middle -> end`
- `reward = main -> middle -> end`
- `farewell = main -> middle -> end`

#### Kontrolle PHASE_05
Fertig nur, wenn ein Agent daraus Text-DB und Textassembler 1:1 schreiben kann.

### PHASE_06 – Arbeitsaufteilung in Dateien festlegen

Ziel: kein Systemchaos.

#### Schritt 06.01
Exakt festlegen, welche neuen Dateien erstellt werden.

Pflichtgruppen:
- `quest/zqs/catalog/*`
- `quest/zqs/knowledge/*`
- `quest/zqs/blueprint/*`
- `quest/zqs/generator/*`
- `quest/zqs/history/*`
- `quest/zqs/text/*`
- `quest/zqs/runtime/*`
- `quest/zqs/save/*` oder Integration in `SaveManager`

#### Schritt 06.02
Exakt festlegen, welche bestehenden Dateien erweitert werden dürfen:
- `QuestDef.java`
- `QuestLog.java`
- `SaveManager.java`
- `DataRegistry.java`
- `ItemDef.java`
- `PlayerProgress.java` nur wenn im Plan zwingend nötig
- `GameScreen.java` nur für Integration
- `WanderQuestGuySystem.java` nur für Docking

#### Schritt 06.03
Für jede Datei einen Satz Zweckbeschreibung notieren.

#### Kontrolle PHASE_06
Fertig nur, wenn jede Datei genau einen klaren Zweck hat.

### PHASE_07 – Implementationsworkflow für den KI-Agenten festlegen

Ziel: der Agent weiß immer, wo er dran ist.

#### Schritt 07.01 – Reihenfolge festschreiben
Der Agent arbeitet nur in dieser Reihenfolge:
1. Workflow-Dateien
2. Analyse
3. Datenmodelle
4. Save-Struktur
5. Text-DB-Struktur
6. Generatorstruktur
7. QuestHistory
8. QuestLog-Erweiterung
9. SaveManager-Erweiterung
10. GameScreen/WQG-Docking
11. Abschlusskontrolle

#### Schritt 07.02 – Nach jedem Schritt Pflichtaktionen
Nach jedem abgeschlossenen Schritt:
- `CHECKLIST_ZQS.md` aktualisieren
- `STATE_ZQS.json` aktualisieren
- `DECISIONS_ZQS.md` ergänzen, falls Architekturentscheidung getroffen wurde
- bei Fehlern `ERRORS.md` aktualisieren

#### Schritt 07.03 – Unterbrechungsregel
Vor jedem Neustart muss der Agent lesen:
1. `READ_FIRST_ZQS.md`
2. `STATE_ZQS.json`
3. `CHECKLIST_ZQS.md`
4. `ERRORS.md`
5. zuletzt bearbeitete relevante Datei

#### Schritt 07.04 – Verbotene Neustart-Aktion
Der Agent darf nicht erneut „den Plan überlegen“. Er muss exakt bei `next_step` weitermachen.

#### Kontrolle PHASE_07
Fertig nur, wenn der Resume-Workflow lückenlos ist.

### PHASE_08 – Abschlusskontrolle definieren

Ziel: sicherstellen, dass nichts vergessen wurde.

#### Schritt 08.01 – Vollständigkeitsprüfung
Prüfen gegen Soll-Liste:
- Kataloge vorhanden
- Known-State vorhanden
- Blueprint-System vorhanden
- Generator vorhanden
- Reward-System vorhanden
- Textsystem vorhanden
- History vorhanden
- Logbuchnummern vorhanden
- Save-Struktur vorhanden
- NPC-Docking vorhanden

#### Schritt 08.02 – Scope-Prüfung
Prüfen:
- nicht kompiliert
- nicht committed
- kein Fremd-Refactor
- keine ungeplanten Änderungen

#### Schritt 08.03 – Dokumentationsprüfung
Prüfen:
- alle Workflow-Dateien aktuell
- keine offenen Schritte ohne Status
- keine stille Abweichung vom Plan

#### Kontrolle PHASE_08
Nur dann abgeschlossen, wenn alle Prüfpunkte dokumentiert wurden.

## Resume-Protokoll

Vor jeder neuen Arbeitssitzung gilt diese feste Reihenfolge:

1. `READ_FIRST_ZQS.md`
2. `STATE_ZQS.json`
3. `CHECKLIST_ZQS.md`
4. `ERRORS.md`
5. `zqs_master_alignment_v_1_1.md`
6. die aktuell betroffene fachliche Datei

Danach wird exakt bei `next_step` weitergearbeitet.

## Was der Agent nicht tun darf

- Keine Features erfinden, die hier nicht beschrieben sind.
- Keine Asset-Seitenquests hineinziehen, wenn sie nicht Teil des ZQS sind.
- Keine Balanceanpassungen.
- Keine UI-Neugestaltung.
- Keine andere NPC-Logik anfassen außer Dockingpunkte.
- Keine Worldgen-Anpassung.
- Keine Kampf-/Harvest-Refactors.
- Keine „ich habe nebenbei noch …“-Änderungen.

## Erste konkrete Ausführungsvorgabe

Der erste echte Arbeitsblock nach diesem Plan ist:

1. `ERRORS.md` lesen oder anlegen.
2. Die 6 Workflow-Dateien anlegen.
3. `ANALYSE_ZQS.md` ausschließlich mit belegbaren Ist-Fakten füllen.
4. Noch keinen Produktionscode für ZQS schreiben.
5. Erst wenn PHASE_00 und PHASE_01 vollständig dokumentiert und kontrolliert sind, darf PHASE_02 beginnen.

## Operativer Kernsatz

Der `WanderQuestGuy` bleibt Ausgabepunkt.
Das ZQS wird als eigenständiges, generisches, persistentes System aufgebaut und arbeitet mit dem `WanderQuestGuy` zusammen.
Der Agent arbeitet strikt phasenweise, liest immer zuerst die Statusdateien, kompiliert nie, committet nie, weicht nie ab und dokumentiert jeden Fortschritt.

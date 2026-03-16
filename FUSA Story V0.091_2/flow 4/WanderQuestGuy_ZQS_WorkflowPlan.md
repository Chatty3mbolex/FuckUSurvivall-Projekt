# WanderQuestGuy / ZQS – Analyse der Voraussetzungen + exakter Workflow-Plan

## 0. Zweck dieses Plans

Dieser Plan beschreibt **nicht** den Code, sondern den **exakten Arbeitsablauf** zur vollständigen Erstellung eines neuen, generischen Zufalls-Quest-Systems (ZQS), an das der `WanderQuestGuy` nur andockt.

Der Plan ist so formuliert, dass ein KI-Agent mittlerer Klasse ihn **ohne Interpretationsspielraum** abarbeiten kann.

## 0.1 Verbindliche Lesereihenfolge vor Arbeitsbeginn

1. `zqs_master_alignment_v_1_1.md`
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_reward_logik_referenz_v1.md`
4. `zqs_quest_inhaltslogik_referenz_v1.md`
5. `zqs_textpool_szenarien_analyse1.md`
6. `zqs_snippetkatalog_v1.md`
7. `zqs_textpool_starter_v1.md`
8. dieses Dokument

Der Workflow-Plan ist die letzte Lesestufe vor der Umsetzung.
Er ersetzt keine Fachlogik, keine Rewardlogik, keine Inhaltslogik und keine Snippetlogik.

## 1. Unverhandelbare Agent-Regeln

1. Nicht kompilieren.
2. Nicht starten.
3. Nicht debuggen.
4. Nicht in Git committen.
5. Nicht nach anderen Fehlern suchen.
6. Keine eigenständigen Refactors.
7. Keine zusätzliche Architektur erfinden, die nicht in der ZQS-Dokumentkonstellation steht.
8. Vor jeder Arbeitseinheit **zuerst `ERRORS.md` lesen**.
9. Falls `ERRORS.md` nicht existiert: **anlegen** und den Status `Noch keine Fehler dokumentiert.` eintragen.
10. Nie mehr als **3-mal denselben Fehler** machen. Nach dem 3. Fehlversuch: sofort stoppen, in `ERRORS.md` dokumentieren, Statusdateien aktualisieren.
11. Keine massenhaften Mini-LLM-Anfragen. Änderungen müssen blockweise erfolgen.
12. Der Agent darf **nicht selbst nach dem nächsten Schritt raten**. Er muss immer aus den Statusdateien lesen, wo weiterzumachen ist.
13. Es darf **nicht vom Plan abgewichen** werden.
14. Es darf nichts außerhalb des beschriebenen Umfangs geändert werden.
15. Der Agent muss den Code **selbst schreiben**. Keine Platzhalter-Kommentare wie „TODO später“ an Stellen, die laut Plan fertig sein müssen.

## 2. Pflicht-Dateien für den Workflow

Diese Dateien sind Arbeitsgrundlage und müssen im Projektwurzelpfad liegen.

### 2.1 `READ_FIRST_ZQS.md`
Kurze Operator-Datei mit:
- Ziel des Systems
- absoluten Verboten
- aktueller Arbeitsphase
- Verweis auf diesen Plan

### 2.2 `STATE_ZQS.json`
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

### 2.3 `CHECKLIST_ZQS.md`
Checkbox-Liste aller Phasen und Schritte.

### 2.4 `ANALYSE_ZQS.md`
Nur Fakten aus dem Ist-Code. Keine Wünsche. Keine Spekulation.

### 2.5 `ERRORS.md`
Fehlerprotokoll mit:
- Timestamp
- Phase
- Schritt
- betroffene Datei
- Fehlerbeschreibung
- Wiederholungszähler
- nächster Versuch oder Abbruch

### 2.6 `DECISIONS_ZQS.md`
Darin stehen nur bewusst festgelegte Architekturentscheidungen, sobald sie umgesetzt wurden.

## 3. Hartanalyse des IST-Codes – nur bezogen auf ZQS-Voraussetzungen

## 3.1 Questgeber-Anbindung existiert bereits

Vorhanden:
- `EntityType.WANDER_QUEST_GUY`
- `WanderQuestGuySystem`
- Quest-Popup in `GameScreen`
- Questlog-Anbindung in `GameScreen`
- Save/Load-Hooks in `SaveManager`

Bedeutung:
Der NPC-Dockingpunkt ist vorhanden. Er ist **nicht** das neue System. Er ist nur ein Zusteller.

## 3.2 Questdatenmodell ist aktuell unzureichend

`QuestDef` enthält aktuell nur:
- `id`
- `kind`
- `title`
- `desc`

Es fehlen komplett:
- Zieltyp
- Zielparameter
- Bedingungen
- Fortschrittsdaten
- Belohnungsdaten
- Textbaustein-Referenzen
- Generatorherkunft
- Logbuchnummer
- Wiederholbarkeit / Historie

## 3.3 Questlog ist aktuell nur Minimalzustand

`QuestLog` kann aktuell:
- Quest aufnehmen
- Status halten (`ACCEPTED`, `COMPLETED`)
- Reihenfolge halten
- speichern/laden über `SaveManager`

Es fehlen komplett:
- echte Entry-ID
- persistente Logbuchnummer
- Fortschrittswerte
- Zielerfüllungsdaten
- getrennte Historie „angeboten / offen / erledigt / gescheitert / abgelaufen“
- generierte Questmetadaten

## 3.4 Datenbasis für Items ist teilweise vorhanden

`assets/data/items.json` enthält reale Items mit:
- `id`
- `name`
- `type`
- `stackMax`
- `value`
- `icon`
- `tags`

`DataRegistry.loadItems()` lädt aktuell nur:
- `id`
- `name`
- `type`
- `stackMax`
- `value`
- `icon`

Nicht geladen:
- `tags`

Folge:
Die JSON-Datenbasis ist breiter als die Runtime-Datenbasis. Für ZQS ist das unbrauchbar, solange `tags` nicht in die Runtime übernommen werden.

## 3.5 Harvestables sind im Runtime-Code real vorhanden

Sichere Runtime-Harvestables laut `EntityType` + `HarvestSystem`:
- `NODE_TREE`
- `NODE_ROCK`
- `NODE_ORE_IRON`
- `NODE_BUSH`
- `NODE_FISH_SPOT`

`NODE_STUMP` existiert ebenfalls, ist aber kein reguläres Ertragsziel.

## 3.6 Livings sind in Assets breiter, in Runtime aber schmaler

In Assets vorhanden:
- mehrere Tiere in `animals.json`
- mehrere Gegner in `enemies.json`

Sicher an Runtime angebunden laut Code:
- `ANIMAL_DEER`
- `ORK_GRUNT`

Folge:
Der Agent darf bei der Generatorbasis nicht blind alles aus Asset-JSON als fertiges Questziel behandeln. Es muss sauber zwischen **asset-definiert** und **runtime-aktiv** unterschieden werden.

## 3.7 Spielerwissen existiert aktuell nicht

Vorhanden:
- `PlayerProgress.level`
- `xp`
- `skillLv[]`
- Inventarstände
- `WorldMapState.knownAreas`

Nicht vorhanden:
- bekannte Items
- bekannte Ressourcen
- bekannte Livings
- bekannte Harvestables
- Discovery-/Codex-/Bestiary-State

Folge:
Die ZQS-Anforderung „Was kennt der Spieler schon?“ ist aktuell **gar nicht** modelliert und muss vollständig neu gebaut werden.

## 3.8 Generator-Datenbank existiert aktuell nicht

Nicht vorhanden:
- Quest-Blueprint-DB
- generierte Quest-DB
- Textbaustein-DB für Gruß/Aufgabe/Belohnung/Abschied
- Reward-Regelwerk
- Bedingungs-/Regelmodell `wenn/dann/sonst/oder`
- Angebots-Historie je Spielerstand

## 3.9 SaveManager hat nur Übergangs-Persistenz

Aktuell gespeichert:
- QuestGuy RNG-State
- Refresh-Timer
- Offer-IDs
- Questlog-Einträge mit ID/Kind/Status/AcceptedAt

Nicht gespeichert:
- Knowledge-State
- Generator-Historie
- offene vs. schon angebotene Questfamilien
- generierte Questdefinitionen
- Fortschritt je Questziel
- Reward-Zustand
- Textvarianten
- Logbuchnummern als eigene persistente IDs

## 4. ZQS-Zielarchitektur – exakt

Das neue System besteht aus **10 getrennten Schichten**.

### Schicht 1 – Kataloge
Einheitliche, generatorfähige Laufzeitkataloge für:
- Ressourcen
- Items
- Livings
- Harvestables
- Rewards

### Schicht 2 – Knowledge-State
Getrennte Erfassung dessen, was der Spieler kennt:
- bekannte Item-IDs
- bekannte Ressourcen-IDs
- bekannte Living-IDs
- bekannte Harvestable-IDs
- Entdeckungsquelle optional

### Schicht 3 – Quest-Blueprints
Vorlagen für Questmuster, z. B.:
- Sammeln
- Töten
- Ernten
- Liefern
- Erkunden
- Kombinationsquests

### Schicht 4 – Regelmodell
Formale Darstellung für Bedingungen:
- `ALL_OF`
- `ANY_OF`
- `IF_THEN`
- `IF_THEN_ELSE`
- optionale Nebenbedingungen

### Schicht 5 – Reward-Modell
Formale Darstellung von Belohnungen:
- Item-Belohnung
- Währung
- XP
- Mischformen

### Schicht 6 – Textbaustein-DB
Getrennte Bausteine für:
- Gruß
- Auftragseinleitung
- Zieltext
- Belohnungstext
- Abschied

### Schicht 7 – Quest-Instanzierung
Erzeugung einer konkreten Quest aus:
- Blueprint
- Zielobjekten
- Mengen
- Bedingungen
- Reward
- Text
- eindeutiger Quest-ID

### Schicht 8 – Quest-Historie
Persistente Trennung von:
- generiert
- angeboten
- angenommen
- offen
- abgeschlossen
- abgebrochen
- gescheitert/abgelaufen

### Schicht 9 – Questlog-Struktur
Persistente Logbuch-Einträge mit eigener Nummer.

### Schicht 10 – NPC-Docking
NPC liest nur aktuelle, passende Questangebote aus dem ZQS.
Kein NPC erzeugt selbst Quests.

## 5. Exakte Umsetzungsphasen

## PHASE_00 – Workflow-Fundament anlegen

Ziel: Kontrollierbare Arbeitsumgebung herstellen.

### Schritt 00.01
`ERRORS.md` lesen.

Abbruchregel:
Wenn Datei fehlt, nicht weiterarbeiten, bevor sie angelegt wurde.

### Schritt 00.02
Falls nicht vorhanden, anlegen:
- `READ_FIRST_ZQS.md`
- `STATE_ZQS.json`
- `CHECKLIST_ZQS.md`
- `ANALYSE_ZQS.md`
- `DECISIONS_ZQS.md`
- `ERRORS.md`

### Schritt 00.03
`STATE_ZQS.json` initial setzen:
- `active_phase = PHASE_00`
- `active_step = 00.03`
- `status = in_progress`

### Schritt 00.04
Diesen Plan in `READ_FIRST_ZQS.md` referenzieren.

### Schritt 00.05
`CHECKLIST_ZQS.md` vollständig mit allen Phasen/Schritten befüllen.

### Kontrolle PHASE_00
Fertig nur wenn alle 6 Workflow-Dateien vorhanden sind.

---

## PHASE_01 – Ist-Code analysieren und dokumentieren

Ziel: Nur reale Grundlagen dokumentieren.

### Schritt 01.01
Alle für ZQS relevanten Dateien lesen und in `ANALYSE_ZQS.md` dokumentieren:
- `EntityType.java`
- `QuestDef.java`
- `QuestLog.java`
- `WanderQuestGuySystem.java`
- `SaveManager.java`
- `GameScreen.java`
- `DataRegistry.java`
- `ItemDef.java`
- `AnimalDef.java`
- `EnemyDef.java`
- `HarvestSystem.java`
- `PlayerProgress.java`
- `WorldMapState.java`
- `assets/data/items.json`
- `assets/data/animals.json`
- `assets/data/enemies.json`

### Schritt 01.02
Aus diesen Dateien **Tabellen/Faktlisten** erstellen:
- reale Runtime-Items
- reale Runtime-Harvestables
- asset-definierte Livings
- runtime-aktive Livings
- aktuell vorhandene Questdatenfelder
- aktuell gespeicherte Questdatenfelder
- aktuell fehlende Pflichtfelder für ZQS

### Schritt 01.03
Eine harte Lückenanalyse schreiben:
- was schon existiert
- was nicht existiert
- was erweitert werden muss
- was komplett neu erstellt werden muss

### Kontrolle PHASE_01
Fertig nur wenn `ANALYSE_ZQS.md` keine Spekulationen enthält und jede Aussage auf konkrete Datei/Feldbasis zurückführbar ist.

---

## PHASE_02 – Datenmodell des ZQS definieren

Ziel: Vollständige Soll-Struktur festlegen, bevor Code geschrieben wird.

### Schritt 02.01 – Katalogmodell definieren
Exakt festlegen, welche Katalogtypen es gibt:
- `ResourceCatalogEntry`
- `ItemCatalogEntry`
- `LivingCatalogEntry`
- `HarvestableCatalogEntry`
- `RewardCatalogEntry`

Für jeden Typ Pflichtfelder definieren.

### Schritt 02.02 – Knowledge-Modell definieren
Exakt festlegen:
- welche Known-Sets existieren
- wie sie gespeichert werden
- wann ein Eintrag als „bekannt“ gilt

### Schritt 02.03 – Quest-Blueprint-Modell definieren
Pflichtfelder festlegen für jede Blueprint-Vorlage:
- blueprintId
- objectiveFamily
- allowedTargetKinds
- amountRules
- minPlayerLevel
- maxPlayerLevel optional
- weight
- rewardProfileId
- textProfileId
- repeatRules

### Schritt 02.04 – Objective-Modell definieren
Pflichtfelder:
- objectiveType
- targetType
- targetId
- targetAmount
- comparator / completion rule
- optional conditions

### Schritt 02.05 – Reward-Modell definieren
Pflichtfelder:
- rewardType
- itemId optional
- currency amount optional
- xp amount optional
- scaling rule

### Schritt 02.06 – GeneratedQuest-Modell definieren
Pflichtfelder:
- generatedQuestId
- blueprintId
- sourceNpcId optional
- objective tree
- reward block
- generated text ids
- logbookEntryId
- logbookEntryNo
- status
- generatedAt
- offeredAt
- acceptedAt
- completedAt

### Schritt 02.07 – QuestHistory-Modell definieren
Pflichtlisten definieren:
- seen quest ids
- active quest ids
- completed quest ids
- expired quest ids
- declined quest ids optional

### Kontrolle PHASE_02
Fertig nur wenn jedes Pflichtfeld schriftlich definiert ist.

---

## PHASE_03 – Persistenzmodell exakt festlegen

Ziel: Vorher definieren, was gespeichert werden muss.

### Schritt 03.01
Neuen Save-Block für ZQS definieren.

### Schritt 03.02
Exakt festlegen, welche Unterblöcke der Save enthält:
- catalogs snapshot nur falls nötig
- player knowledge
- blueprint state wenn nötig
- generated quest db
- active offers by npc
- quest history
- quest log entries
- numbering counters
- rng state

### Schritt 03.03
Für jeden Block Pflichtfelder dokumentieren.

### Schritt 03.04
Migrationsregel für alte Saves festlegen:
- wenn Block fehlt -> saubere Defaults
- keine Kompilierung
- keine stillen Datenverluste

### Kontrolle PHASE_03
Fertig nur wenn ein Agent daraus Save-/Load-Code schreiben könnte, ohne nachzufragen.

---

## PHASE_04 – Generatorlogik definieren

Ziel: Exakte Erzeugungsreihenfolge festlegen.

### Schritt 04.01 – Eingaben definieren
Der Generator darf nur diese Inputs verwenden:
- Spielerlevel
- Known-State
- vorhandene Kataloge
- Quest-Historie
- offene Questanzahl
- Logbuchzähler
- optional Weltstress
- optional Spielzeit

### Schritt 04.02 – Filterkette definieren
Reihenfolge exakt festlegen:
1. Blueprint-Kandidaten filtern
2. Zieltypen filtern
3. bekannte Zielobjekte filtern
4. Levelgrenzen filtern
5. Wiederholungsregeln filtern
6. Konflikt mit offenen Quests filtern
7. Gewichtung anwenden
8. Zielmenge bestimmen
9. Belohnung bestimmen
10. Text generieren
11. Quest-ID bilden
12. Quest speichern

### Schritt 04.03 – ID-Regel definieren
Die Quest-ID darf nicht frei erfunden sein.
Sie muss deterministisch aus Kernparametern entstehen, z. B.:
- blueprintId
- targetType
- targetId
- amount
- reward signature
- laufende Nummer oder hash suffix

### Schritt 04.04 – Wiederholungslogik definieren
Exakt festlegen:
- wann gleiche Questfamilie erneut erlaubt ist
- wann exakt gleicher Target-Questblock erneut erlaubt ist
- wann nie erneut erlaubt ist

### Kontrolle PHASE_04
Fertig nur wenn die Generatorpipeline als feste, lineare Reihenfolge dokumentiert ist.

---

## PHASE_05 – Textsystem definieren

Ziel: Text nicht hartcodiert mischen, sondern formal beschreiben.

### Schritt 05.01
Textbaustein-Kategorien definieren:
- greetings
- task intros
- objective fragments
- reward fragments
- farewells

### Schritt 05.02
Kontextparameter definieren:
- ingame time bucket
- player condition bucket
- world stress bucket
- active quest count bucket
- completed quest count bucket

### Schritt 05.03
Regeln für Textauswahl definieren:
- Pflichtfilter
- optionale Gewichtung
- fallback bei leerer Trefferliste

### Schritt 05.04
Textzusammensetzung definieren:
- Grußsatz
- Auftragskern
- Zielformulierung
- Belohnungssatz
- Abschied

### Kontrolle PHASE_05
Fertig nur wenn ein Agent daraus Text-DB und Textassembler 1:1 schreiben kann.

---

## PHASE_06 – Arbeitsaufteilung in Dateien festlegen

Ziel: Kein Systemchaos.

### Schritt 06.01
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

### Schritt 06.02
Exakt festlegen, welche bestehenden Dateien erweitert werden dürfen:
- `QuestDef.java`
- `QuestLog.java`
- `SaveManager.java`
- `DataRegistry.java`
- `ItemDef.java`
- `PlayerProgress.java` nur wenn im Plan zwingend nötig
- `GameScreen.java` nur für Integration
- `WanderQuestGuySystem.java` nur für Docking

### Schritt 06.03
Für jede Datei einen Satz Zweckbeschreibung notieren.

### Kontrolle PHASE_06
Fertig nur wenn jede Datei genau einen klaren Zweck hat.

---

## PHASE_07 – Implementationsworkflow für den KI-Agenten festlegen

Ziel: Der Agent weiß immer, wo er dran ist.

### Schritt 07.01 – Reihenfolge festschreiben
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

### Schritt 07.02 – Nach jedem Schritt Pflichtaktionen
Nach **jedem** abgeschlossenen Schritt:
- `CHECKLIST_ZQS.md` aktualisieren
- `STATE_ZQS.json` aktualisieren
- `DECISIONS_ZQS.md` ergänzen, falls Architekturentscheidung getroffen wurde
- bei Fehlern `ERRORS.md` aktualisieren

### Schritt 07.03 – Unterbrechungsregel
Vor jedem Neustart muss der Agent lesen:
1. `READ_FIRST_ZQS.md`
2. `STATE_ZQS.json`
3. `CHECKLIST_ZQS.md`
4. `ERRORS.md`
5. zuletzt bearbeitete relevante Datei

### Schritt 07.04 – Verbotene Neustart-Aktion
Der Agent darf nicht erneut „den Plan überlegen“. Er muss exakt bei `next_step` weitermachen.

### Kontrolle PHASE_07
Fertig nur wenn der Resume-Workflow lückenlos ist.

---

## PHASE_08 – Abschlusskontrolle definieren

Ziel: Sicherstellen, dass nichts vergessen wurde.

### Schritt 08.01 – Vollständigkeitsprüfung
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

### Schritt 08.02 – Scope-Prüfung
Prüfen:
- nicht kompiliert
- nicht committed
- kein Fremd-Refactor
- keine ungeplanten Änderungen

### Schritt 08.03 – Dokumentationsprüfung
Prüfen:
- alle Workflow-Dateien aktuell
- keine offenen Schritte ohne Status
- keine stille Abweichung vom Plan

### Kontrolle PHASE_08
Nur dann abgeschlossen, wenn alle Prüfpunkte dokumentiert wurden.

## 6. Was der Agent NICHT tun darf

- Keine Features erfinden, die hier nicht beschrieben sind.
- Keine Asset-Seitenquests hineinziehen, wenn sie nicht Teil des ZQS sind.
- Keine Balanceanpassungen.
- Keine UI-Neugestaltung.
- Keine andere NPC-Logik anfassen außer Dockingpunkte.
- Keine Worldgen-Anpassung.
- Keine Kampf-/Harvest-Refactors.
- Keine „ich habe nebenbei noch …“-Änderungen.

## 7. Erste konkrete Ausführungsvorgabe

Der **erste echte Arbeitsblock** nach diesem Plan ist:

1. `ERRORS.md` lesen oder anlegen.
2. Die 6 Workflow-Dateien anlegen.
3. `ANALYSE_ZQS.md` ausschließlich mit belegbaren Ist-Fakten füllen.
4. Noch **keinen** Produktionscode für ZQS schreiben.
5. Erst wenn PHASE_00 und PHASE_01 vollständig dokumentiert und kontrolliert sind, darf PHASE_02 beginnen.

## 8. Operativer Kernsatz

Der `WanderQuestGuy` bleibt Zusteller.
Das ZQS wird als eigenständiges, generisches, persistentes System aufgebaut.
Der Agent arbeitet strikt phasenweise, liest immer zuerst die Statusdateien, kompiliert nie, committet nie, weicht nie ab und dokumentiert jeden Fortschritt.

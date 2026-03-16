# ANALYSE_ZQS.md (IST-Fakten)

> Nur Ist-Fakten aus Code/Assets. Keine Spekulation.

## Relevante Code-Dateien (gelesen)
- `core/src/main/java/com/yourgame/survival/systems/WanderQuestGuySystem.java`
- `core/src/main/java/com/yourgame/survival/quest/QuestDef.java`
- `core/src/main/java/com/yourgame/survival/quest/QuestLog.java`
- `core/src/main/java/com/yourgame/survival/data/SaveManager.java`
- `core/src/main/java/com/yourgame/survival/data/DataRegistry.java`
- `core/src/main/java/com/yourgame/survival/data/ItemDef.java`
- `core/src/main/java/com/yourgame/survival/data/PlayerProgress.java`
- `core/src/main/java/com/yourgame/survival/entity/EntityType.java`
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java` (QuestGuy Popup + QuestLog Panel)

## 1) NPC-Docking (WanderQuestGuy)
- `EntityType` enthält `WANDER_QUEST_GUY`.
- `GameScreen` kann ein Quest-Popup für den WQG öffnen/schließen.
- `GameScreen` ruft `questGuy.onPopupOpened()` beim Öffnen auf.

## 2) Offer-Logik im IST-Code
- `WanderQuestGuySystem` hält:
  - `refreshT` (Realtime Sekunden)
  - `offers[3]` als `QuestDef`
  - `offerCount` (0..3)
  - `greeting` (UI-only)
- Refresh-Timing: `refreshT = (60*60) * (1 + rand*5)` → 1..6 Stunden.
- OfferCount: `floor(rand*4)` → 0..3.
- Offer-Pool ist aktuell ein hardcoded Array aus 3 `QuestDef`-Factory-Methoden.

## 3) Questdatenmodell IST
- `QuestDef` Felder: `id`, `kind` (`MAIN|SIDE`), `title`, `desc`.
- `QuestDef.byId(String)` liefert bekannte IDs oder Default-Quest mit Titel = id.
- Keine Objectives, keine Rewarddaten, keine Zeitwerte, keine Textprofile-IDs.

## 4) QuestLog IST
- `QuestLog.Entry`: `QuestDef def`, `Status status`, `acceptedAtRuntimeSec`.
- Status: `ACCEPTED`, `COMPLETED`.
- Keine Logbuchnummer, keine Progressdaten, keine NQ/HQ-spezifischen Status.

## 5) Save/Load IST (v6)
- `SaveManager` speichert:
  - `questSys`: `moveRng`, `refreshT`, `offerCount`, `offerId[3]` (QuestDef-IDs)
  - `questLog`: Entries mit `id`, `kind`, `status`, `acceptedAt`
- `WanderQuestGuySystem.importFromSave(JsonValue)` lädt Offer-IDs und mappt sie via `QuestDef.byId`.

## 6) DataRegistry IST
- Items werden aus `assets/data/items.json` geladen, Felder in Runtime: `id,name,type,stackMax,value,icon`.
- Item-Tags aus JSON werden nicht in `ItemDef` geladen (ItemDef hat kein tags-Feld).
- Rezepte werden geladen.
- Animals/Enemies werden nicht vollständig als Runtime-Liste gemappt; konkret:
  - `ork_grunt` wird in `DataRegistry.orkGrunt` geladen
  - `animal_deer` wird in `DataRegistry.deer` geladen

## 7) Harvestables / Runtime Entities (IST)
- `EntityType` enthält Harvestables: `NODE_TREE`, `NODE_STUMP`, `NODE_ROCK`, `NODE_ORE_IRON`, `NODE_BUSH`, `NODE_FISH_SPOT`.

## 8) PlayerProgress IST
- Felder: `level`, `xp`, `xpToNext`, `skillPoints`, `skillLv[]`.
- Kein Known-State für Items/Regions/Harvestables/Livings.

## 9) Lückenanalyse (nur Fakten aus obigen Punkten)

### 9.1 Was existiert bereits (für ZQS nutzbar als Docking/Träger)
- NPC-Docking ist vorhanden:
  - `EntityType.WANDER_QUEST_GUY`
  - `WanderQuestGuySystem` tickt/rollt Offers
  - Quest-Popup/QuestLog Panel in `GameScreen`
  - Save/Load Hooks in `SaveManager` für QuestGuy + QuestLog

### 9.2 Was existiert nicht (Pflichtteile laut Konzept fehlen im IST)
- Kein ZQS-Kernsystem:
  - keine Katalog-Schicht (`ContentCatalogRuntime` als eigenes Modell)
  - kein Knowledge-State
  - keine Blueprint-DB (im Runtime-Code)
  - kein Regelmodell (ALL_OF/ANY_OF/IF_THEN...)
  - kein Reward-Modell (als strukturierter Block)
  - keine Textbaustein-DB / Snippet-Pools
  - keine QuestHistoryIndex-Struktur
- Questdatenmodell ist zu klein (`QuestDef` hat nur id/kind/title/desc)
- Questlog ist zu klein (nur ACCEPTED/COMPLETED, keine Logbuchnummer, kein Fortschritt)

### 9.3 Was erweitert werden muss (bestehende Dateien)
- `QuestDef` muss für ZQS deutlich erweitert oder durch ZQS-eigene Records ergänzt werden.
- `QuestLog` muss für ZQS-Status/Logbuchnummern/Fortschritt erweitert werden.
- `SaveManager` muss ZQS-Blöcke persistieren (Knowledge/DB/History/Counters/RNG).
- `DataRegistry` muss Item-Tags in Runtime übernehmen, wenn Tags für Generatorfilter nötig sind.

### 9.4 Was komplett neu erstellt werden muss
- ZQS Subsystem (Kataloge/Knowledge/Blueprints/Generator/Text/History/Save) gemäß Flow-4 Dokumentkonstellation.

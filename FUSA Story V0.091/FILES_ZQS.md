# FILES_ZQS.md — Datei-Aufteilung (PHASE_06)

> Zweck: Exakte Datei-/Modulaufteilung, damit Umsetzung kein Chaos wird.
> Basis: Flow-4 Workflowplan (PHASE_06) + bisherige Model/Save/Text/Generator-Definitionen.

---

## 06.01 — Neue Dateien (werden erstellt)

Pflichtgruppen (Flow 4) + konkrete Dateien (v1):

### A) `quest/zqs/catalog/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/catalog/ContentCatalogRuntime.java`
  - Zweck: Runtime-Katalog im Code (normalisierte Listen/Lookup)
- `core/src/main/java/com/yourgame/survival/quest/zqs/catalog/CatalogLoader.java`
  - Zweck: lädt `assets/data/zqs/catalog_runtime_v1.json` in `ContentCatalogRuntime`
- `core/src/main/java/com/yourgame/survival/quest/zqs/catalog/CatalogEntry.java`
  - Zweck: gemeinsame Entry-Struktur + kind-spezifische Felder

### B) `quest/zqs/knowledge/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/knowledge/PlayerKnowledgeState.java`
  - Zweck: Known-Sets (items/regions/harvestables/livings/pois/npcs)
- `core/src/main/java/com/yourgame/survival/quest/zqs/knowledge/KnowledgeSaveIO.java`
  - Zweck: Save/Load des Knowledge-Blocks

### C) `quest/zqs/blueprint/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/blueprint/QuestBlueprint.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/blueprint/BlueprintRegistry.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/blueprint/BlueprintLoader.java`

### D) `quest/zqs/generator/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/ZqsGenerator.java`
  - orchestriert 04.01–04.04
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/ZqsQuestId.java`
  - deterministische QuestID-Regel
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/ZqsRepeatRules.java`
  - Wiederholungsprüfung

### E) `quest/zqs/history/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/history/QuestHistoryIndex.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/history/HistorySaveIO.java`

### F) `quest/zqs/text/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/text/ZqsTextEngine.java`
  - Snippet-Auswahl + Assembler (PHASE_05)
- `core/src/main/java/com/yourgame/survival/quest/zqs/text/SnippetDb.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/text/SnippetLoader.java`

### G) `quest/zqs/runtime/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/OfferBuffer.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/GeneratedQuestOffer.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/PersistentQuestRecord.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/RewardBlock.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/PlayerQuestDb.java`

### H) `quest/zqs/save/*`
- `core/src/main/java/com/yourgame/survival/quest/zqs/save/ZqsSaveBlock.java`
  - In-Memory Struktur des Save-Blocks
- `core/src/main/java/com/yourgame/survival/quest/zqs/save/ZqsSaveIO.java`
  - JSON write/read für SaveManager Integration

### I) Assets-DBs (statisch)
- `assets/data/zqs/catalog_runtime_v1.json`
- `assets/data/zqs/blueprints_v1.json`
- `assets/data/zqs/reward_profiles_v1.json`
- `assets/data/zqs/text_snippets_de_DE_v1.json`

---

## 06.02 — Bestehende Dateien, die erweitert werden dürfen

Nur diese (Flow 4):
- `core/src/main/java/com/yourgame/survival/quest/QuestDef.java`
- `core/src/main/java/com/yourgame/survival/quest/QuestLog.java`
- `core/src/main/java/com/yourgame/survival/data/SaveManager.java`
- `core/src/main/java/com/yourgame/survival/data/DataRegistry.java`
- `core/src/main/java/com/yourgame/survival/data/ItemDef.java`
- `core/src/main/java/com/yourgame/survival/data/PlayerProgress.java` (nur wenn zwingend)
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java` (nur Integration)
- `core/src/main/java/com/yourgame/survival/systems/WanderQuestGuySystem.java` (nur Docking)

---

## 06.03 — Zweckbeschreibung je Datei (ein Satz)

### ZQS neu
- `ContentCatalogRuntime.java`: hält die generatorfähigen Runtime-Inhalte als Lookups/Listen.
- `CatalogLoader.java`: lädt Runtime-Katalog-JSON in `ContentCatalogRuntime`.
- `PlayerKnowledgeState.java`: persistente Known-Sets des Spielers.
- `QuestBlueprint.java`: Datenmodell einer Blueprint-Vorlage.
- `BlueprintRegistry.java`: hält Blueprints im Speicher und bietet Lookups.
- `BlueprintLoader.java`: lädt `blueprints_v1.json` in Registry.
- `ZqsGenerator.java`: erzeugt Offers/Records strikt nach `GENERATOR_ZQS.md`.
- `ZqsQuestId.java`: bildet deterministische QuestIDs.
- `ZqsRepeatRules.java`: prüft Wiederholungssperren/Cooldowns.
- `QuestHistoryIndex.java`: hält seen/active/completed/expired/declined Sets.
- `ZqsTextEngine.java`: wählt Snippets und assembliert Texte nach `TEXT_ZQS.md`.
- `SnippetDb.java`: In-Memory Snippetstruktur.
- `SnippetLoader.java`: lädt Snippet-JSON in SnippetDb.
- `OfferBuffer.java`: runtime-only Angebote (nicht persistent).
- `PlayerQuestDb.java`: persistente Quest-Records (angenommen).
- `ZqsSaveBlock.java`: In-Memory Struktur des Saveblocks.
- `ZqsSaveIO.java`: Save/Load des gesamten ZQS-Blocks.

### ZQS existing/erweiterbar
- `QuestDef.java`: UI-kompatible Questdefinition (ggf. minimaler Wrapper auf ZQS Offer).
- `QuestLog.java`: Questlogbuch/Statusanzeige (muss ZQS-Status+Logbuchnummer tragen).
- `SaveManager.java`: persistiert/lädt ZQS-Block gemäß `SAVE_ZQS.md`.
- `DataRegistry.java`/`ItemDef.java`: stellt Item-Tags/Meta bereit, wenn Generatorfilter es brauchen.
- `GameScreen.java`: bindet ZQS in Systems/Save/Popup.
- `WanderQuestGuySystem.java`: dockt an ZQS an, erzeugt selbst keine Questlogik.

---

## Kontrolle PHASE_06
PHASE_06 ist abgeschlossen, wenn:
- neue Dateien vollständig benannt sind,
- erlaubte Erweiterungsstellen feststehen,
- jede Datei genau einen Zweck hat.

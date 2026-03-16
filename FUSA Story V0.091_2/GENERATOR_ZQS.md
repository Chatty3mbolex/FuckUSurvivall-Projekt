# GENERATOR_ZQS.md — Generatorlogik (PHASE_04)

> Zweck: Exakte Generator-Definition, die ohne Raten implementierbar ist.
> Basis: `MODEL_ZQS.md`, `SAVE_ZQS.md`, Flow-4 Dokumente.
>
> Harte Master-Regeln:
> - Generator arbeitet nur auf `ContentCatalogRuntime`.
> - Textengine erhält nur instanzierte Questdaten + Gesprächskontext.
> - OfferBuffer ist nicht persistent.
> - Nicht angenommene NQ erzeugen keinen DB-/History-Eintrag.

---

## 04.01 — Eingaben (Inputs) definieren

Der Generator darf nur diese Inputs verwenden (Flow 4):

### Pflicht-Inputs
1. `playerLevel` (int) — aus `PlayerProgress.level`
2. `PlayerKnowledgeState` — aus Save `zqs.knowledge`
3. `ContentCatalogRuntime` — aus DB `assets/data/zqs/catalog_runtime_v1.json` (oder späteres RuntimeCatalogId)
4. `QuestHistoryIndex` — aus Save `zqs.questHistoryIndex`
5. `openQuestsCount` (int) — aus QuestLog/History (NQ/HQ getrennt, wenn verfügbar)
6. `logbook counters` — aus Save `zqs.logbook.next_logbook_entry_nr` + `zqs.counters.quest_nr_counter`
7. `runtimeSec` (long) — aktuelle Spielzeit (für timestamps/cooldowns)

### Optionale Inputs (nur falls im Projekt bereits vorhanden)
8. `worldstress_zone` (ruhig|belebt|hektisch) — aus Plan-Formel, benötigt für Greeting/Farewell Filter
9. `time_of_day` (morning|day|evening|night) — aus DayNightSystem

### Explizit verbotene Inputs
- Asset-Katalog direkt
- UI-Zustand als Ersatz für Knowledge
- Zufällige Rekonstruktionen („ich nehme an, der Spieler kennt…“)

---

## 04.02 — Filterkette / Erzeugungskette (lineare Reihenfolge)

Der Generator arbeitet strikt in dieser Reihenfolge:

### Schritt 1 — Eligibility Basis (global)
1. `quest_family` festlegen (`NQ` oder `HQ`)
2. Wenn `quest_family == HQ`:
   - prüfen `active_hq_count == 0`
   - prüfen `hq_trigger == true`
   - wenn nicht erfüllt: HQ-Generation abbrechen
3. Wenn `quest_family == NQ`:
   - NQ-Timer prüfen (0–6h-Intervall aus Plan, konkrete Persistenz im Save)
   - offene Questgrenze prüfen (\>10 => N/A)

### Schritt 2 — Blueprint-Kandidaten filtern
Filtere `QuestBlueprint` nach:
- `objectiveFamily == quest_family`
- `minPlayerLevel <= playerLevel`
- `maxPlayerLevel` falls gesetzt

### Schritt 3 — Zieltypen filtern
- Aus Blueprint: `allowedTargetKinds`
- Entferne Zielkinds, für die kein Eintrag im `ContentCatalogRuntime` existiert.

### Schritt 4 — Known-State Filter
Für jeden Kandidaten-Target:
- wenn `CatalogEntry.knownRequired == true`:
  - prüfe, ob `targetId` im passenden Known-Set enthalten ist
  - sonst verwerfen

### Schritt 5 — SL_ID / Story Gate
- Verwende `slIdMax` in CatalogEntries:
  - verwerfe alle Targets mit `slIdMax > player.sl_id` (Plan 1.0)

### Schritt 6 — Wiederholungsregeln / RepeatRules
- Wiederholung wird über `QuestHistoryIndex` + `blueprintState.cooldowns` geprüft:
  - `denySameFamily`
  - `denySameTarget`
  - `cooldownHours`

### Schritt 7 — Konflikt mit offenen Quests
- Wenn identischer TargetBlock bereits in `active_quest_ids` existiert und `denySameTarget` aktiv ist → verwerfen.

### Schritt 8 — Gewichtung anwenden
- Wähle Blueprint über `weight` (nur nach Filterung)

### Schritt 9 — Zielmenge bestimmen
- wähle Menge nach `amountRules` (min/max)

### Schritt 10 — Zielobjekt wählen
- wähle konkretes Target aus RuntimeCatalog (nach obigen Filtern)

### Schritt 11 — expected_time_sec bestimmen
- Standard: `expected_time_sec = objCount * 120 * 0.86`
- Escort: `expected_time_sec = regionCount * 420 * 0.86 + 300`

### Schritt 12 — Reward bestimmen
- per `RewardProfile.rewardFormulaType` + Plan-Formeln
- berechne:
  - `base_reward_copper`
  - (später) `penalty_copper` bei Abschluss
  - `final_reward_copper`
- baue `RewardBlock`

### Schritt 13 — Textparameter erzeugen
- Erzeuge `ConversationContext` (time_of_day, worldstress_zone, counts, block_reason)
- wähle Snippets über Textengine (keine Berechnung in Textengine)
- speichere `generated_text_ids` (für Debug/Determinismus)

### Schritt 14 — Quest-ID bilden (siehe 04.03)
- QuestID deterministisch aus Kernparametern

### Schritt 15 — OfferBuffer schreiben
- Erzeuge `GeneratedQuestOffer`
- schreibe in OfferBuffer (runtime)
- persistiere NICHT

### Schritt 16 — Bei Annahme persistieren
- Offer → `PersistentQuestRecord`
- schreibe in Save-Block `zqs.playerQuestDb`
- update `QuestHistoryIndex`
- update `logbook` + `counters`

---

## 04.03 — Deterministische QuestID-Regel

### Ziel
QuestID darf nicht frei erfunden sein. Sie muss deterministisch aus Kernparametern entstehen.

### Kanonisches Kernset (Minimal)
- `quest_family` (NQ/HQ)
- `sl_id` (StoryLinePhase)
- `blueprintId`
- `targetType`
- `targetId`
- `amount`
- `rewardProfileId`
- `expected_time_sec`
- `quest_nr_counter` (persistenter Zähler) ODER Hash-Suffix

### Vorgeschlagene ID-Form (deterministisch + lesbar)

Format:
`<FAMILY><SL_ID><BP_HASH><TARGET_HASH><AMOUNT><QNR><PLAYER>`

Regeln:
- `QNR` = 5-stellig aus `zqs.counters.quest_nr_counter`
- `PLAYER` = Spielername (falls vorhanden) oder `P0`

Beispiel (schematisch):
- `NQ1A3F9B1200001P0`

Wichtig:
- QuestID enthält Phase (`sl_id`), aber nicht Storytiefe.

---

## 04.04 — Wiederholungslogik definieren

### Ziel
Exakt festlegen, wann Wiederholung erlaubt ist.

Regeln:
1. `denySameFamily=true`:
   - wenn QuestHistoryIndex bereits eine Quest mit gleicher `blueprintId` in `active` oder `completed` enthält → block.

2. `denySameTarget=true`:
   - wenn `targetType+targetId` in aktiven Quests vorkommt → block.

3. `cooldownHours>0`:
   - wenn letzter Abschlusszeitpunkt (oder Annahmezeitpunkt, falls nicht abgeschlossen) + cooldown > now → block.

Persistenz:
- `blueprintState.cooldowns` speichert `until_runtime_sec` pro `blueprintId` (oder pro targetKey, wenn denySameTarget).

---

## Kontrolle PHASE_04 (Definition of Done)

PHASE_04 ist abgeschlossen, wenn:
- Inputs exakt begrenzt sind,
- Filterkette lückenlos implementierbar ist,
- deterministische QuestID-Regel implementierbar ist,
- RepeatRules/History eindeutig sind.

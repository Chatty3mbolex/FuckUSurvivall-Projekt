# MODEL_ZQS.md — ZQS Datenmodelle (PHASE_02)

> Zweck: **schriftliche, vollständige Soll-Definition** der ZQS-Datenmodelle nach Flow 4.
> Diese Datei ist die Arbeitsgrundlage für die anschließende Implementierung.
>
> Geltung / Priorität:
> - Konflikte werden durch `flow 4/zqs_master_alignment_v_1_1.md` normiert.
> - Questlogik/Status/Story/IDs: `flow 4/zqs_wander_quest_guy_plan1.0.md`
> - Rewardlogik: `flow 4/zqs_reward_logik_referenz_v1.md`
> - Questinhalt-Freigaben: `flow 4/zqs_quest_inhaltslogik_referenz_v1.md`
> - Textparameter/Snippets: `flow 4/zqs_textpool_szenarien_analyse1.md` + `flow 4/zqs_snippetkatalog_v1.md`
>
> Harte Regeln:
> - **Keine Fallback-Systeme** außerhalb der Konstellation.
> - Generator arbeitet nur auf **ContentCatalogRuntime**.
> - Nicht angenommene NQ liegen nur im **OfferBuffer** (nicht persistent).

---

## 02.01 — Katalogmodell definieren

### Ziel
Einheitliche, generatorfähige Runtime-Kataloge. Der Generator darf **nur** daraus wählen.

### Katalog-Ebenen
1) `ContentCatalogAsset` (alles definierte Spielmaterial)
- Zweck: Text-/Alias-/Zukunft
- Nicht generatorbestimmend.

2) `ContentCatalogRuntime` (generatorfähig, freigegeben)
- Zweck: echte Zielauswahl, Rewardbasis, Validierung
- **Generator arbeitet ausschließlich hierauf.**

### Katalogtypen (Runtime)
Es gibt mindestens diese Runtime-Kataloge:
- `ResourceCatalogEntry`
- `ItemCatalogEntry`
- `HarvestableCatalogEntry`
- `LivingCatalogEntry`
- `PoiCatalogEntry`
- `NpcCatalogEntry`
- `RegionCatalogEntry`
- `RewardCatalogEntry` (optional als eigener Katalog; Rewardwerte kommen primär aus Item-Werten + Formeln)

#### Gemeinsame Pflichtfelder für *alle* CatalogEntry-Typen
- `kind` (enum/string): `resource|item|harvestable|living|poi|npc|region`
- `id` (string): stabile ID (bei Items zusätzlich `itemId` möglich)
- `name` (string): Anzeige-/Textname
- `tags` (string[]): Klassifizierung für Filter/Text (nur als Daten, nicht als Logik)
- `valueCopper` (int): ökonomischer Wert in Kupfer
  - Bei Items: direkt aus items.json
  - Bei Harvestables/Livings/POIs: abgeleitet (z.B. Drop-Wert), aber im Runtime-Katalog als Zahl hinterlegt
- `slIdMax` (int): maximale StorylinePhase/SL_ID, ab der das Ziel freigegeben ist (Plan 1.0: höhere Werte sind zu ignorieren)
- `knownRequired` (boolean): ob Known-Flag erforderlich ist

#### Item/Resource Besonderheiten
- `itemId` (int): numerische Item-ID aus `assets/data/items.json`

#### Harvestable Besonderheiten
- `harvestableId` (string) = EntityType ID (z.B. `NODE_TREE`)
- `toolRequirement` (optional string/tag): z.B. `tool:axe`
- `drop` (optional): nur als Datenhinweis (z.B. `drop:item:0`, `dropAmount:5`), nicht als Logik

### DB-Abbildung
- `assets/data/zqs/catalog_runtime_v1.json` ist die kanonische Runtime-Katalogquelle.

---

## 02.02 — Knowledge-Modell definieren

### Ziel
Persistenter Known-State als eigener Block. Wird nicht aus UI oder Zufall rekonstruiert.

### Objekt: `PlayerKnowledgeState`
Pflichtfelder (Sets / Mengen):
- `known_items` (Set<int> itemId)
- `known_regions` (Set<string> regionId)
- `known_harvestables` (Set<string> harvestableId)
- `known_livings` (Set<string> livingId)
- `known_pois` (Set<string> poiId)
- `known_npcs` (Set<string> npcId)

Optional:
- `knowledge_sources` (Map<id, source>) — z.B. `"discover"|"quest"|"dialog"` (nur wenn später gebraucht)

### Regeln
- Known-State ist Eingabe für Generator + Textengine.
- Textengine erzeugt Known-State **nie**.
- Generator darf Ziele, die `knownRequired=true` haben, nur wählen, wenn sie im passenden Known-Set enthalten sind.

### Persistenz
- Wird im Savegame als ZQS-Block gespeichert (siehe PHASE_03).

---

## 02.03 — Quest-Blueprint-Modell definieren

### Ziel
Blueprints sind Vorlagen. Sie definieren Questtyp/Subtyp, erlaubte Zielklassen, Mengenregeln, Wiederholung, Reward-/Textprofile.

### Objekt: `QuestBlueprint`
Pflichtfelder:
- `blueprintId` (string, unique)
- `objectiveFamily` (string/enum): `NQ|HQ`
- `questType` (enum): `sammeln|liefern|craften|finden|eskortieren`
- `questSubtype` (enum/string) gemäß Inhaltsreferenz:
  - `sammeln.item`
  - `sammeln.harvestable`
  - `liefern.item`
  - `craften.recipe_output`
  - `craften.delivery`
  - `finden.poi`
  - `finden.poi_loot`
  - `finden.person` (noch offen)
  - `finden.object`
  - `eskortieren.route`
- `allowedTargetKinds` (string[]): Zielklassen innerhalb erlaubter Questarten
- `amountRules` (object):
  - `min` (int)
  - `max` (int)
  - (optional) `strategy` (string) z.B. `uniform` (falls später)
- `minPlayerLevel` (int)
- `maxPlayerLevel` (int|null)
- `weight` (float)
- `rewardProfileId` (string)
- `textProfileId` (string)
- `repeatRules` (object) Pflicht:
  - `cooldownHours` (int)
  - `denySameTarget` (boolean)
  - `denySameFamily` (boolean)

### Regeln
- Blueprints werden in Eligibility gefiltert (Family, Level, Known, Repeat).
- Gewichtung wird erst nach Filter angewendet.

### DB-Abbildung
- `assets/data/zqs/blueprints_v1.json`

---

## 02.04 — Objective-Modell definieren

### Ziel
Objectives sind strukturierte Zieldefinitionen (nicht nur Text).

### Objekt: `QuestObjective`
Pflichtfelder:
- `objectiveType` (enum/string): z.B. `collect|deliver|craft|find|escort`
- `targetType` (enum/string): `item|harvestable|poi|npc|object|region|living`
- `targetId` (string)
- `targetAmount` (int)
- `completionRule` (enum/string):
  - für sammeln: `inventory_count>=amount` oder `delivered_count>=amount` (je nach Questart)
  - für finden: `visited|confirmed|looted`
  - für escort: `arrived_and_returned_no_death` (konzeptualisiert)
- `conditions` (optional): Liste formaler Bedingungen (siehe Regelmodell; aktuell als Platzhalter)

### Objective Tree
- `objectiveTree` kann 1 Objective sein oder Baumstruktur.
- Minimal zulässig: lineare Liste (später erweiterbar).

---

## 02.05 — Reward-Modell definieren

### Ziel
Reward ist strukturierter Block. Berechnung erfolgt im Generator. Text referenziert nur.

### Objekt: `RewardBlock`
Pflichtfelder:
- `rewardTotalCopper` (int)
- `rewardTextMode` (enum): `item|currency|mixed|failed`

Optionale/abhängige Felder:
- `reward_items` (List<{itemId:int, amount:int}>) optional
- `reward_currency_copper` (int) optional
- `reward_currency_silver` (int) optional
- `reward_currency_gold` (int) optional
- `xp_amount` (int) optional (nur wenn Fachlogik das später aktiviert)
- `scalingRule` (string) optional

### RewardProfile (DB)
Pflichtfelder:
- `rewardProfileId`
- `rewardFormulaType` (string): `collect|deliver|craft|craft_delivery|find_poi|find_object|escort`
- `rewardTextMode` (string)
- `allowItemRewards` (bool)
- `allowCurrencyRewards` (bool)
- `allowMixedRewards` (bool)

### Regeln
- Rewardhöhe entsteht aus:
  - Questinhalt (Mengen * Kupferwert)
  - Zeit (`expected_time_sec * 33`)
  - Penalty-Regel (elapsed > expected)
- Fehlschlag: `final_reward_copper < 0` ⇒ Status `fehlgeschlagen`.

---

## 02.06 — GeneratedQuest (Offer/Record) Modell definieren

### Ziel
Trennung: flüchtiges Angebot vs persistenter Record.

### Objekt: `GeneratedQuestOffer` (OfferBuffer)
Pflichtfelder:
- `quest_id` (string)
- `quest_family` (`NQ|HQ`)
- `quest_type`
- `quest_subtype`
- `target_block` (strukturierter Block, mindestens: targetType/targetId/name/amount/value)
- `reward_block` (RewardBlock)
- `expected_time_sec` (int)
- `text_profile_id` (string) oder `text_profile_ids` (string[])
- `status` (enum/string): `generated|offered`
- `source_npc_id` (string)
- `generated_at` (timestamp/runtimeSec)
- `offered_at` (timestamp/runtimeSec) (optional aber vorgesehen)

Wichtige Regel:
- Offer ist **nicht persistent**, solange nicht akzeptiert.

### Objekt: `PersistentQuestRecord` (PlayerQuestDB)
Pflichtfelder:
- alles aus `GeneratedQuestOffer`
- `accepted_at`
- `completed_at`
- `failed_at`
- `logbook_entry_nr` (int, persistenter Zähler)
- `accepted_location` (optional)
- `completed_location` (optional)
- `giver_npc_id` (string)
- `final_status` (enum):
  - NQ: `aktiv|abgabebereit|erledigt|fehlgeschlagen`
  - HQ: `generiert|aktiv|abgabebereit|erledigt`

### QuestID Regel (nur Modell, Implementierung später)
- QuestID ist deterministisch aus Kernparametern:
  - blueprintId
  - targetType
  - targetId
  - amount
  - reward signature
  - + running number oder hash suffix

---

## 02.07 — QuestHistory Modell definieren

### Ziel
Leichter Index über Quests, um RepeatRules und UI/Statusübersichten zu stützen.

### Objekt: `QuestHistoryIndex`
Pflichtlisten:
- `seen_quest_ids` (Set<string>)
- `active_quest_ids` (Set<string>)
- `completed_quest_ids` (Set<string>)
- `expired_quest_ids` (Set<string>)

Optional:
- `declined_quest_ids` (Set<string>)

### Regeln
- Nicht angenommene NQ:
  - bleiben nur OfferBuffer
  - gehen **nicht** in HistoryIndex
- Angenommene Quests:
  - werden in History/DB geführt
- `fehlgeschlagen` bleibt Endstatus und taucht in History als „bereits vorhanden“ auf.

---

## Kontrolle PHASE_02 (Definition of Done)
PHASE_02 ist nur dann abgeschlossen, wenn:
- jedes oben genannte Objekt **alle Pflichtfelder** schriftlich enthält,
- jede Regel zur Trennung OfferBuffer/DB/History klar ist,
- und daraus Save-/Load + Generator + Textengine implementierbar sind, ohne zu raten.

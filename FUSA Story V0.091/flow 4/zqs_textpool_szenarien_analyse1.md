# ZQS Textpool-Szenarien

## Konstellationsrolle

Dieses Dokument bleibt die primäre Referenz für Text- und Inhaltsanbindung, Szenariofelder und Textparameter.

Für Rewardlogik gilt primär:
- `zqs_reward_logik_referenz_v1.md`

Für möglichen Questinhalt gilt primär:
- `zqs_quest_inhaltslogik_referenz_v1.md`

Dieses Dokument überschreibt keine Rewardformel und keine freigegebene Questart.


## Zweck
Dieses Dokument ist die getrennte Arbeitsbasis für die **Text- und Teiltextpools**.  
Es verbindet drei Dinge:

1. die aus der ZIP extrahierten Spielinhalte,
2. die Relevanz dieser Inhalte für den ZQS-/WQG-Plan,
3. die **Szenario-Templates** als Parameterfelder für spätere Textbausteine.

## Analysebasis aus der ZIP
Ausgewertet wurden vor allem diese Dateien:

- `assets/data/items.json`
- `assets/data/animals.json`
- `assets/data/enemies.json`
- `assets/data/recipes.json`
- `assets/data/shops.json`
- `core/.../HarvestSystem.java`
- `core/.../EntityType.java`
- `core/.../SpawnTypeId.java`
- `core/.../JsonAreaWorldLoader.java`
- `core/.../WanderQuestGuySystem.java`
- `core/.../QuestDef.java`
- `core/.../QuestLog.java`

## Wichtige harte Beobachtungen
- **Kupferwert** ist technisch bereits sauber an `items.json -> value` gekoppelt.
- **Explizite Kupferwerte** gibt es direkt nur für **Items**.
- **Tiere, Gegner und Harvestables** haben keinen eigenen Kupferwert-Feldblock; ihr wirtschaftlicher Wert ergibt sich aktuell über **Drops / Loot**.
- Für den ZQS ist das gut genug, weil deine Belohnungsformeln ohnehin mit dem **Wert der beteiligten Items** arbeiten.
- Die aktuell real umgesetzten Harvestables im Runtime-System sind:
  - `NODE_TREE`
  - `NODE_ROCK`
  - `NODE_ORE_IRON`
  - `NODE_BUSH`
  - `NODE_FISH_SPOT`
- Die aktuell realen Tiere aus `animals.json` sind:
  - Deer
  - Boar
  - Wolf
  - Rabbit
  - Bear
  - Fox
- Die aktuell realen Gegner aus `enemies.json` sind:
  - `ork_grunt`
  - `ork_spearman`
  - `ork_archer`
  - `ork_shaman`
  - `ork_brute`
  - `ork_guard`
- Zusätzlich existiert eine **breitere Spawn-Liste** in `SpawnTypeId.java`, aber vieles davon ist noch **nicht** auf Runtime-Entities gemappt. Für die Textpools ist deshalb zuerst der **aktuell echte Runtime-Bestand** relevanter.

## Vollständige Itemliste mit Kupferwert

### resource

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 0 | Wood | 1 | resource, wood |
| 1 | Stone | 1 | resource, stone |
| 2 | Iron Ore | 3 | ore, iron |
| 3 | Copper Ore | 3 | ore, copper |
| 4 | Coal | 2 | ore, coal |
| 5 | Silver Ore | 6 | ore, silver |
| 6 | Gold Ore | 8 | ore, gold |
| 7 | Crystal | 10 | resource, crystal |
| 8 | Herbs | 2 | resource, herb |
| 9 | Clay | 2 | resource, clay |
| 47 | Arrow | 1 | ammo, arrow |

### material

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 10 | Iron Ingot | 7 | ingot, iron |
| 11 | Copper Ingot | 6 | ingot, copper |
| 12 | Silver Ingot | 12 | ingot, silver |
| 13 | Gold Ingot | 16 | ingot, gold |

### tool

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 14 | Axe | 25 | tool, axe |
| 15 | Pickaxe | 25 | tool, pickaxe |
| 16 | Knife | 20 | tool, knife |
| 17 | Hammer | 30 | tool, hammer |
| 18 | Climbing Tool | 40 | tool, climb |
| 19 | Net | 18 | tool, net |
| 36 | Angel | 35 | tool, fishing, rod |

### weapon

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 20 | Sword | 60 | weapon, melee |
| 21 | Spear | 55 | weapon, melee |
| 22 | Bow | 70 | weapon, ranged |
| 23 | Dagger | 45 | weapon, melee |
| 24 | Mace | 65 | weapon, melee |
| 25 | Battle Axe | 75 | weapon, melee |
| 26 | Crystal Staff | 90 | weapon, magic |
| 27 | Crossbow | 85 | weapon, ranged |

### build

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 40 | Workbench | 80 | build, station, crafting |
| 41 | Campfire | 30 | build, cooking, heat |
| 42 | Chest | 60 | build, storage |
| 43 | Bed | 70 | build, sleep |
| 44 | Anvil | 150 | build, station, smithing |
| 45 | Smelter | 120 | build, station, smelt |
| 46 | Lamp | 40 | build, light |

### food

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 28 | Meat | 6 | food |
| 29 | Cooked Meat | 10 | food, cooked |
| 34 | Beeren | 3 | food, forage |
| 35 | Fisch | 5 | food, fish |

### consumable

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 30 | Water | 2 | drink |

### currency

| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 31 | Münzen (Kupfer) | 1 | currency, coin, copper |
| 32 | Münzen (Silber) | 100 | currency, coin, silver |
| 33 | Münzen (Gold) | 100000 | currency, coin, gold |

## Aktuell craftbare Outputs
| Rezept | Output | Output-Kupferwert | Inputs |
| --- | --- | --- | --- |
| r_workbench | Workbench | 80 | Wood x30; Stone x10 |
| r_campfire | Campfire | 30 | Wood x12; Stone x12 |
| r_chest | Chest | 60 | Wood x24; Stone x6 |
| r_bed | Bed | 70 | Wood x16; Herbs x8 |
| r_anvil | Anvil | 150 | Iron Ingot x8; Stone x12 |
| r_smelter | Smelter | 120 | Stone x25; Clay x10 |
| r_iron_ingot | Iron Ingot | 7 | Iron Ore x3; Coal x1 |
| r_copper_ingot | Copper Ingot | 6 | Copper Ore x3; Coal x1 |
| r_silver_ingot | Silver Ingot | 12 | Silver Ore x3; Coal x1 |
| r_gold_ingot | Gold Ingot | 16 | Gold Ore x3; Coal x1 |
| r_axe | Axe | 25 | Wood x12; Stone x6 |
| r_pickaxe | Pickaxe | 25 | Wood x12; Stone x6 |
| r_hammer | Hammer | 30 | Wood x8; Stone x10 |
| r_knife | Knife | 20 | Wood x5; Stone x4 |
| r_climb | Climbing Tool | 40 | Wood x10; Iron Ingot x3 |
| r_net | Net | 18 | Wood x4; Herbs x6 |
| r_sword | Sword | 60 | Wood x8; Iron Ingot x5 |
| r_spear | Spear | 55 | Wood x10; Iron Ingot x3 |
| r_bow | Bow | 70 | Wood x12; Herbs x4 |
| r_dagger | Dagger | 45 | Wood x4; Iron Ingot x3 |
| r_mace | Mace | 65 | Wood x8; Iron Ingot x6 |
| r_battleaxe | Battle Axe | 75 | Wood x10; Iron Ingot x7 |
| r_staff | Crystal Staff | 90 | Wood x8; Crystal x5 |
| r_crossbow | Crossbow | 85 | Wood x10; Iron Ingot x4 |
| r_cooked_meat | Cooked Meat | 10 | Meat x1 |
| r_fishing_rod | Angel | 35 | Wood x6; Iron Ingot x2 |

## Aktuelle Harvestables / direkte Sammelquellen
| Harvestable | Werkzeug | Drop | Kupferwert pro erfolgreichem Harvest | Hinweis |
| --- | --- | --- | --- | --- |
| NODE_TREE | Axt (14) | Wood x5 | 5 | ersetzt durch Stumpf |
| NODE_ROCK | Pickaxe (15) | Stone x4 | 4 | verschwindet |
| NODE_ORE_IRON | Pickaxe (15) | Iron Ore x2 | 6 | verschwindet |
| NODE_BUSH | Hand | Beeren x1 | 3 | 15–60 min Regrow |
| NODE_FISH_SPOT | Angel (36) | Fisch x1 | 5 | 15–60 min Regrow |

## Aktuelle Tiere
| Tier | HP | Loot | Kupferwert Range |
| --- | --- | --- | --- |
| animal_deer | 20 | Meat 2-2 | 12–12 |
| animal_boar | 28 | Meat 2-3 | 12–18 |
| animal_wolf | 26 | Meat 1-2 | 6–12 |
| animal_rabbit | 12 | Meat 1-1 | 6–6 |
| animal_bear | 55 | Meat 3-5 | 18–30 |
| animal_fox | 16 | Meat 1-1 | 6–6 |

## Aktuelle Gegner
| Gegner | HP | DMG | XP | Loot | Kupferwert Range |
| --- | --- | --- | --- | --- | --- |
| ork_grunt | 35 | 6 | 10 | Münzen (Kupfer) 1-8, Wood 0-2 | 1–10 |
| ork_spearman | 40 | 7 | 12 | Münzen (Kupfer) 2-10, Stone 0-2 | 2–12 |
| ork_archer | 30 | 8 | 14 | Münzen (Kupfer) 3-12, Herbs 0-1 | 3–14 |
| ork_shaman | 45 | 9 | 18 | Münzen (Kupfer) 5-16, Crystal 0-1 | 5–26 |
| ork_brute | 70 | 12 | 28 | Münzen (Kupfer) 8-25, Iron Ore 0-1 | 8–28 |
| ork_guard | 60 | 11 | 24 | Münzen (Kupfer) 7-22, Copper Ore 0-1 | 7–25 |

## Weitere aktuell sichtbare / benannte Spielobjekte
### NPCs / Gesprächspartner
- `MERCHANT_ELF`
- `MERCHANT_WANDERING`
- `WANDER_QUEST_GUY`

### POIs / Landmarken / ortsgebundene Ziele
- `POI_CHEST_HIDDEN`
- `LANDMARK_CASTLE`
- `LANDMARK_BRIDGE`

### Hidden-Chest-Lootquelle
Die Hidden Chest zieht ihre Inhalte deterministisch aus:
- `Arrow` 0–20
- `Münzen (Kupfer)` 0–500
- `Iron Ore` 0–20
- `Münzen (Gold)` 0–1
- `Sword` 0–1

Das ist wichtig für spätere **Finden-Questtexte**, weil dadurch POI-Inhalte als reale Ziel-/Belohnungsquelle existieren.

## Relevanzvergleich: Spielinhalte vs. ZQS-Plan
### Direkt relevant für Auftragstexte
- **sammeln**
  - alle Items mit `type=resource`
  - alle realen Harvestables
- **liefern**
  - prinzipiell alle transportierbaren Items
- **craften / Warenlieferung**
  - alle Rezept-Outputs
- **finden**
  - POIs, versteckte Truhen, Landmarken, NPCs, Objekte
- **eskortieren**
  - braucht später eigene Regeldatei `Escort_Rules`

### Direkt relevant für Belohnungstexte
- alle Items mit Kupferwert
- alle Währungsitems
- alle berechenbaren Output-Werte aus Recipes / Harvest / Loot

### Direkt relevant für Gruß- und Abschiedstexte
Hier sind nicht die Itemnamen wichtig, sondern die **Steuerparameter**:
- Tageszeit
- Weltstress
- Debug-Schalter
- offene Quests
- erledigte Quests
- NQ/HQ-Kontext
- N/A-/Blocker-Zustände

### Was textlich bereits vorbereitet werden kann
Bereits jetzt lassen sich Textpools später sauber nach diesen Inhaltsklassen befüllen:
- Ressourcen
- Materialien
- Werkzeuge
- Waffen
- Nahrung
- Bauobjekte
- Tiere
- Gegner
- Harvestables
- POIs / Landmarken / NPCs

## Szenario-Templates für die Textteile
Ziel dieser Templates:
Nicht die fertigen Texte, sondern die **Parameterfelder**, damit später klar ist, aus welcher Kategorie welcher Text gezogen wird.

---

## TEMPLATE 1 – Grußtexte (NQ)
**Gilt für:** Hauptteil / Mittelteil / Endteil  
**Bedingung:** 3 Teile + Tageszeit + Weltstress + Debug-Schalter

### Parameterfelder
- `quest_family = NQ`
- `text_category = greeting`
- `text_part = main | middle | end`
- `time_of_day = morning | day | evening | night`
- `worldstress_zone = ruhig | belebt | hektisch`
- `debug_hq_mode = INCLUDE_HQ | EXCLUDE_HQ`
- `open_quests_count`
- `completed_quests_count`
- `personal_stress_value`
- `stress_factor_value`
- `stress_quotient_value`
- `nq_slots_free`
- `nq_generation_possible = true | false`
- `block_reason = none | cap_reached | no_valid_targets | timer_not_due | N/A`

### Einsatzlogik
- **Hauptteil** zieht primär nach `time_of_day`
- **Mittelteil** zieht primär nach `worldstress_zone`
- **Endteil** zieht nach allgemeinem Gesprächsausgang / Situation

---

## TEMPLATE 2 – Abschiedstexte (NQ)
**Gilt für:** Hauptteil / Mittelteil / Endteil  
**Bedingung:** 3 Teile + Tageszeit + Weltstress + Debug-Schalter

### Parameterfelder
- `quest_family = NQ`
- `text_category = farewell`
- `text_part = main | middle | end`
- `time_of_day = morning | day | evening | night`
- `worldstress_zone = ruhig | belebt | hektisch`
- `debug_hq_mode = INCLUDE_HQ | EXCLUDE_HQ`
- `conversation_result = accepted | declined | no_offer | inventory_full | cap_reached | blocked`
- `open_quests_count`
- `completed_quests_count`
- `stress_quotient_value`
- `nq_generated_count`
- `nq_offer_count`
- `nq_accept_count`

### Einsatzlogik
- dieselbe Zonenlogik wie bei Grußtexten
- zusätzlich stärker an `conversation_result` gekoppelt

---

## TEMPLATE 3 – Auftragstexte (NQ)
**Gilt für:** Hauptteil / Mittelteil / Endteil  
**Bedingung:** nur 3 Teile

### Parameterfelder
- `quest_family = NQ`
- `text_category = assignment`
- `text_part = main | middle | end`
- `quest_type = sammeln | liefern | craften | finden | eskortieren`
- `quest_subtype`
- `target_class = item | harvestable | animal | enemy | poi | npc | object | region`
- `target_id`
- `target_name`
- `target_tags`
- `target_value_copper`
- `target_quantity`
- `known_flag_required = true`
- `known_flag_state`
- `sl_id_current`
- `story_depth_current`
- `stress_factor_value`
- `expected_time_sec`
- `reward_formula_type`
- `quest_nr`
- `quest_id_preview`
- `source_rule = core_rules | Escort_Rules | poi_rules | loot_rules`

### Empfohlene Subtypes
- `sammeln.item`
- `sammeln.harvestable`
- `liefern.item`
- `craften.recipe_output`
- `craften.delivery`
- `finden.poi`
- `finden.poi_loot`
- `finden.person`
- `finden.object`
- `eskortieren.route`

### Einsatzlogik
- **Hauptteil** = allgemeiner Auftragsanstieg
- **Mittelteil** = konkrete Aufgabenanforderung
- **Endteil** = Abschluss / Übergabe / Framing

---

## TEMPLATE 4 – N/A-Auftragstext
**Gilt für:** kein normaler Auftrag, sondern Fallback

### Parameterfelder
- `quest_family = NQ`
- `text_category = assignment_na`
- `na_reason = rolled_zero | cap_reached | no_valid_targets | no_known_targets | timer_reset`
- `time_of_day`
- `worldstress_zone`
- `open_quests_count`
- `completed_quests_count`
- `nq_generated_count = 0`

### Einsatzlogik
- getrennt halten
- nicht ins Log
- nicht in die DB
- nur Timer reset

---

## TEMPLATE 5 – Belohnungstexte (NQ)
**Gilt für:** Hauptteil / Mittelteil / Endteil  
**Bedingung:** nur 3 Teile

### Parameterfelder
- `quest_family = NQ`
- `text_category = reward`
- `text_part = main | middle | end`
- `quest_type`
- `quest_subtype`
- `quest_id`
- `target_name`
- `target_quantity`
- `reward_total_copper`
- `reward_items`
- `reward_currency_copper`
- `reward_currency_silver`
- `reward_currency_gold`
- `reward_text_mode = item | currency | mixed`
- `expected_time_sec`
- `elapsed_time_min`
- `reward_formula_type`
- `reward_state = claimable | accepted | failed`
- `giver_name`
- `pickup_location`

### Einsatzlogik
- **Hauptteil** = Übergabe-Einstieg
- **Mittelteil** = echte Belohnung aus Quest-DB-Eintrag
- **Endteil** = Abschlussfloskel / Konsequenz

---

## TEMPLATE 6 – Datenszenario pro Questtyp
Hier geht es nicht um Satzteile, sondern um die Felder, die später fast jede Textwahl steuern.

### sammeln
- `target_class = item | harvestable`
- `target_name`
- `target_quantity`
- `target_value_copper`
- `harvest_source = tree | rock | ore | bush | fish_spot | inventory_only`

### liefern
- `delivery_item_id`
- `delivery_item_name`
- `delivery_quantity`
- `pickup_entity`
- `target_entity`
- `target_region`

### craften
- `recipe_id`
- `output_item_id`
- `output_item_name`
- `output_quantity`
- `input_items`
- `output_value_copper`

### finden
- `find_target_kind = poi | poi_loot | person | object`
- `find_target_name`
- `find_region`
- `find_content_reference`

### eskortieren
- `escort_rule_source = Escort_Rules`
- `escort_subject`
- `escort_from`
- `escort_to`
- `escort_return_required = true`
- `escort_no_death_required = true`
- `escort_region_count`

---

## TEMPLATE 7 – Inhaltsklassen für spätere Teiltextlisten
Diese Liste ist direkt für das spätere Befüllen der Textpools nützlich.

### Ressourcenziele
- Wood
- Stone
- Iron Ore
- Copper Ore
- Coal
- Silver Ore
- Gold Ore
- Crystal
- Herbs
- Clay
- Arrow

### Material-/Craftziele
- Iron Ingot
- Copper Ingot
- Silver Ingot
- Gold Ingot

### Werkzeugziele
- Axe
- Pickaxe
- Knife
- Hammer
- Climbing Tool
- Net
- Angel

### Waffenziele
- Sword
- Spear
- Bow
- Dagger
- Mace
- Battle Axe
- Crystal Staff
- Crossbow

### Bauziele
- Workbench
- Campfire
- Chest
- Bed
- Anvil
- Smelter
- Lamp

### Nahrungsziele
- Meat
- Cooked Meat
- Water
- Beeren
- Fisch

### Tierziele
- animal_deer
- animal_boar
- animal_wolf
- animal_rabbit
- animal_bear
- animal_fox

### Gegnerziele
- ork_grunt
- ork_spearman
- ork_archer
- ork_shaman
- ork_brute
- ork_guard

### Harvest-Ziele
- NODE_TREE
- NODE_ROCK
- NODE_ORE_IRON
- NODE_BUSH
- NODE_FISH_SPOT

### Orts-/POI-Ziele
- POI_CHEST_HIDDEN
- LANDMARK_CASTLE
- LANDMARK_BRIDGE
- MERCHANT_ELF
- MERCHANT_WANDERING
- WANDER_QUEST_GUY

## Nächste sinnvolle Bearbeitungsreihenfolge
1. Grußtexte-Parameterfelder bestätigen
2. Abschiedstexte-Parameterfelder bestätigen
3. Auftragstexte nach Questtyp zerlegen
4. Belohnungstexte nach `reward_text_mode` zerlegen
5. danach erst echte Teiltexte schreiben

## Korrektur der Textlogik

Die Texte werden **nicht** als ganze vorgefertigte Sätze oder ganze Questtexte gespeichert.

Stattdessen gilt:

- Alle Texte entstehen **immer aus Text-Snippets / Teiltexten**.
- Diese Snippets liegen in **Textpools mit Parametern**.
- Das System prüft zuerst die Parameter.
- Danach zieht es aus den passenden Pools die passenden Teile.
- Erst **zur Laufzeit** wird daraus der vollständige sichtbare Text zusammengesetzt.

## Interne Ablaufkette beim WanderQuestGuy

Wenn der Spieler den **WQG** anspricht, läuft intern diese Kette:

1. **Prüfung der Parameter für den Gruß**
2. **Generierung des Grußtextes aus Snippets**
3. **Ausgabe des Grußtextes**
4. **Gegebenenfalls Prüfung der Parameter für Questerstellung und Questangebot**
5. **Generierung der Questangebote**
6. **Bei Annahme:**
   - Eintrag ins **Questlogbuch**
   - Eintrag in alle **nötigen Dateien / Datenstrukturen**
7. **Prüfung der Abschiedstext-Parameter**
8. **Generierung des Abschiedstextes aus Snippets**
9. **Ausgabe des Abschiedstextes**

## Snippet-Regel

Es gilt ab jetzt ausdrücklich:

- **Keine ganzen vorgefertigten Texte**
- **Keine komplett ausgeschriebenen Standardquestsätze als feste Endtexte**
- **Immer sinnige Texte aus Snippets**

## Textpool-Prinzip

Ein Textpool besteht nicht aus fertigen Gesamtsätzen, sondern aus **geordneten Teilstücken**.

### Grußtexte
- Hauptteil-Snippets
- Mittelteil-Snippets
- Endteil-Snippets
- gesteuert über:
  - Tageszeit
  - Weltstress
  - Debug-Schalter
  - offene Quests
  - erledigte Quests

### Auftragstexte
- Hauptteil-Snippets
- Mittelteil-Snippets
- Endteil-Snippets
- gesteuert über:
  - Auftragstyp
  - Untertyp
  - Zielklasse
  - Zielobjekt
  - Zielmenge
  - SL_ID
  - Known-Flag
  - Zeitwert
  - weitere Questparameter

### Belohnungstexte
- Hauptteil-Snippets
- Mittelteil-Snippets
- Endteil-Snippets
- gesteuert über:
  - Questtyp
  - Belohnungsart
  - Belohnungswert
  - QuestID-Verweis auf DB

### Abschiedstexte
- Hauptteil-Snippets
- Mittelteil-Snippets
- Endteil-Snippets
- gesteuert über:
  - Tageszeit
  - Weltstress
  - Gesprächsausgang
  - Questannahme / keine Annahme / Blocker

## Ziel für die weitere Arbeit

Ab jetzt werden für jede Textart **keine fertigen Beispieltexte**, sondern **Snippet-Kategorien mit Parametern** gebaut.

Erst danach werden diese Kategorien mit einzelnen Teiltexten befüllt.


## Snippet-Templates je Kategorie

Nur Templates. Keine Ingame-Beispieltexte. Keine fertigen Sätze.

---

### 1. Grußtexte – Snippet-Templates

#### 1.1 Hauptteil-Snippet
**Template-ID:** `greeting.main`

**Zweck:**
Eröffnender Satzteil der Begrüßung. Muss die erste direkte Ansprache des Spielers tragen.

**Pflicht-Parameter:**
- `text_category = greeting`
- `text_part = main`
- `quest_family = NQ`
- `time_of_day = morning | day | evening | night`
- `speaker_entity = WQG`

**Codefunktion im Satzbau:**
- Einstieg
- Anrede-/Eröffnungsfunktion
- setzt Grundton für den nachfolgenden Mittelteil

**Erwarteter Snippet-Inhalt:**
- Anrede bezogen auf Tageszeit
- optional erste Situationsmarkierung
- keine Questdetails
- keine Belohnung
- kein Abschied

**Semantische Regeln:**
- muss ohne Vorwissen lesbar sein
- darf nicht wie ein vollständiger Gruß-Endsatz wirken
- muss offen genug sein, damit Mittelteil direkt anschließen kann

**Anschlussregel:**
- muss grammatisch vor einem Zustands-/Lage-Mittelteil funktionieren

**Template-Struktur:**
- `(eröffnender Anredeteil auf Basis von time_of_day)`
- `(optionaler kurzer Tonfall-/Situationsanker ohne Questangebot)`

#### 1.2 Mittelteil-Snippet
**Template-ID:** `greeting.middle`

**Zweck:**
Situativer Kern des Grußes. Bewertet Lage und Gesprächszustand.

**Pflicht-Parameter:**
- `text_category = greeting`
- `text_part = middle`
- `quest_family = NQ`
- `worldstress_zone = ruhig | belebt | hektisch`
- `debug_hq_mode = INCLUDE_HQ | EXCLUDE_HQ`
- `open_quests_count`
- `completed_quests_count`
- `stress_quotient_value`
- `nq_generation_possible = true | false`
- `block_reason = none | cap_reached | no_valid_targets | timer_not_due | N/A`

**Codefunktion im Satzbau:**
- Zustandsbeschreibung
- Lageeinordnung
- Überleitung zur Angebotsbereitschaft oder Blockade

**Erwarteter Snippet-Inhalt:**
- Lagebeschreibung auf Basis des Weltstress
- optional Hinweis auf Queststatus des Spielers
- optional Hinweis auf Angebotsmöglichkeit / Blockade
- keine konkrete Questanforderung

**Semantische Regeln:**
- muss zum Hauptteil passen
- darf keinen Abschied vorwegnehmen
- darf kein fertiger Auftrag sein
- muss mit oder ohne anschließenden Endteil funktionieren

**Anschlussregel:**
- muss direkt vor einem Endteil-Snippet stehen können, das zu Angebot / Nicht-Angebot überleitet

**Template-Struktur:**
- `(Lagebeschreibung auf Basis von worldstress_zone)`
- `(optionale Bewertung des Spielerzustands über open_quests_count / completed_quests_count)`
- `(optionaler Hinweis, ob ein Angebot möglich ist oder blockiert wird)`

#### 1.3 Endteil-Snippet
**Template-ID:** `greeting.end`

**Zweck:**
Abschluss des Grußes. Bereitet Gespräch auf Questangebot oder Nicht-Angebot vor.

**Pflicht-Parameter:**
- `text_category = greeting`
- `text_part = end`
- `quest_family = NQ`
- `nq_generation_possible = true | false`
- `block_reason = none | cap_reached | no_valid_targets | timer_not_due | N/A`
- `conversation_next_step = offer | no_offer | blocked`

**Codefunktion im Satzbau:**
- Schluss des Begrüßungsblocks
- Übergabe an Questprüfung / Angebotsausgabe

**Erwarteter Snippet-Inhalt:**
- Angebotsüberleitung
- Blockadeüberleitung
- neutrale Gesprächsweiterleitung

**Semantische Regeln:**
- muss auf Haupt- und Mittelteil logisch folgen
- darf nicht wie ein Abschied klingen
- darf noch keinen Questinhalt nennen

**Anschlussregel:**
- muss direkt vor Questangebot oder N/A-Floskel funktionieren

**Template-Struktur:**
- `(verbinder zur Questprüfung)`
- `(ankündigung eines möglichen angebots oder hinweis auf blockierten folgeschritt)`

---

### 2. Auftragstexte – Snippet-Templates

#### 2.1 Hauptteil-Snippet
**Template-ID:** `assignment.main`

**Zweck:**
Einheitlicher Einleitungsteil des Questangebots. Noch ohne konkrete Questanforderung.

**Pflicht-Parameter:**
- `text_category = assignment`
- `text_part = main`
- `quest_family = NQ`
- `quest_type = sammeln | liefern | craften | finden | eskortieren`
- `quest_subtype`
- `speaker_entity = WQG`

**Codefunktion im Satzbau:**
- Angebotsstart
- Einleitung in den Auftrag
- erzeugt Erwartung auf den Mittelteil

**Erwarteter Snippet-Inhalt:**
- Unterbreitung eines Angebots
- Kennzeichnung, dass jetzt ein Auftrag folgt
- optional Tonfallmarker
- keine konkreten Ziele

**Semantische Regeln:**
- muss mit allen Questtypen funktionieren
- darf keine questtypspezifischen Objekte fest nennen
- darf nicht bereits den Auftrag vollständig ausformulieren

**Anschlussregel:**
- muss direkt vor jedem questtypspezifischen Mittelteil stehen können

**Template-Struktur:**
- `(ankündigung eines auftrags)`
- `(einleitender verbinder zur konkreten aufgabenanforderung)`

#### 2.2 Mittelteil-Snippet – sammeln.item
**Template-ID:** `assignment.middle.collect_item`

**Pflicht-Parameter:**
- `quest_type = sammeln`
- `quest_subtype = sammeln.item`
- `target_class = item`
- `target_name`
- `target_quantity`
- `target_value_copper`
- `known_flag_state`
- `sl_id_current`
- `expected_time_sec`

**Codefunktion im Satzbau:**
- Kernanforderung des Auftrags

**Erwarteter Snippet-Inhalt:**
- konkrete Sammelanforderung
- Benennung von Zielobjekt und Menge
- optional Qualitäts-/Vollständigkeitsmarker

**Template-Struktur:**
- `(aufforderungsverb für beschaffung)`
- `(mengenangabe target_quantity)`
- `(zielobjektbezeichnung target_name)`
- `(optional vollständigkeits-/zustandsmarker)`

#### 2.3 Mittelteil-Snippet – sammeln.harvestable
**Template-ID:** `assignment.middle.collect_harvestable`

**Pflicht-Parameter:**
- `quest_type = sammeln`
- `quest_subtype = sammeln.harvestable`
- `target_class = harvestable`
- `target_name`
- `target_quantity`
- `source_node_type`
- `known_flag_state`
- `sl_id_current`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Sammelanforderung direkt aus Quelle / Weltobjekt
- Menge und Zielbezeichnung
- optional Hinweis auf direkte Herkunft

**Template-Struktur:**
- `(aufforderungsverb für sammeln/bergen/holen)`
- `(mengenangabe target_quantity)`
- `(harvestable-bezeichnung target_name)`
- `(optional herkunfts-/quellenhinweis source_node_type)`

#### 2.4 Mittelteil-Snippet – liefern
**Template-ID:** `assignment.middle.deliver`

**Pflicht-Parameter:**
- `quest_type = liefern`
- `target_class = item`
- `target_name`
- `target_quantity`
- `target_entity`
- `target_region`
- `pickup_entity`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Lieferanforderung
- Transportgut
- Zielperson / Zielort

**Template-Struktur:**
- `(transport-/lieferverb)`
- `(mengenangabe target_quantity)`
- `(liefergut target_name)`
- `(verbinder zum empfänger target_entity)`
- `(verbinder zum zielort target_region)`

#### 2.5 Mittelteil-Snippet – craften
**Template-ID:** `assignment.middle.craft`

**Pflicht-Parameter:**
- `quest_type = craften`
- `quest_subtype = craften.recipe_output`
- `recipe_id`
- `output_item_name`
- `output_quantity`
- `input_items`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Herstellungsanforderung
- Benennung des Endprodukts
- optional Hinweis, dass Rohstoffe nicht genügen

**Template-Struktur:**
- `(herstellungsverb)`
- `(mengenangabe output_quantity)`
- `(zielprodukt output_item_name)`
- `(optional klarstellung: endprodukt statt rohmaterial)`

#### 2.6 Mittelteil-Snippet – Warenlieferung
**Template-ID:** `assignment.middle.craft_delivery`

**Pflicht-Parameter:**
- `quest_type = craften`
- `quest_subtype = craften.delivery`
- `output_item_name`
- `output_quantity`
- `target_entity`
- `target_region`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- kombinierte Herstellungs- und Lieferanforderung

**Template-Struktur:**
- `(herstellungsverb)`
- `(mengenangabe output_quantity)`
- `(zielprodukt output_item_name)`
- `(verbinder zur anschließenden lieferung)`
- `(empfänger-/zielortbaustein)`

#### 2.7 Mittelteil-Snippet – finden.poi
**Template-ID:** `assignment.middle.find_poi`

**Pflicht-Parameter:**
- `quest_type = finden`
- `quest_subtype = finden.poi`
- `find_target_kind = poi`
- `target_name`
- `target_region`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Auffindauftrag für Ort / POI
- optional Rückkehr-/Bestätigungslogik

**Template-Struktur:**
- `(such-/findungsverb)`
- `(poi-bezeichnung target_name)`
- `(optional ortseinordnung target_region)`
- `(optional bestätigungs-/rückmeldebaustein)`

#### 2.8 Mittelteil-Snippet – finden.poi_loot
**Template-ID:** `assignment.middle.find_poi_loot`

**Pflicht-Parameter:**
- `quest_type = finden`
- `quest_subtype = finden.poi_loot`
- `target_name`
- `find_content_reference`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Auffinden eines Orts plus Sichern seines Inhalts

**Template-Struktur:**
- `(such-/auffindverb)`
- `(poi-bezeichnung target_name)`
- `(verbinder zur inhaltssicherung)`
- `(inhaltshinweis über find_content_reference)`

#### 2.9 Mittelteil-Snippet – finden.person
**Template-ID:** `assignment.middle.find_person`

**Status:**
- später genauer definiert

**Template-Struktur:**
- `(suchverb für personenbezogene auffindung)`
- `(personenbezeichnung)`
- `(optional orts-/zustandshinweis)`

#### 2.10 Mittelteil-Snippet – finden.object
**Template-ID:** `assignment.middle.find_object`

**Pflicht-Parameter:**
- `quest_type = finden`
- `quest_subtype = finden.object`
- `target_name`
- `target_quantity`
- `target_region`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Auffindauftrag für Objekt(e)

**Template-Struktur:**
- `(such-/bergungsverb)`
- `(mengenangabe target_quantity)`
- `(objektbezeichnung target_name)`
- `(optional ortshinweis target_region)`

#### 2.11 Mittelteil-Snippet – eskortieren
**Template-ID:** `assignment.middle.escort`

**Pflicht-Parameter:**
- `quest_type = eskortieren`
- `quest_subtype = eskortieren.route`
- `escort_rule_source = Escort_Rules`
- `escort_subject`
- `escort_from`
- `escort_to`
- `escort_return_required = true`
- `escort_no_death_required = true`
- `escort_region_count`
- `expected_time_sec`

**Erwarteter Snippet-Inhalt:**
- Escort-Zielperson / Zielobjekt
- Startpunkt
- Zielpunkt
- Rückkehrpflicht
- Überlebensbedingung

**Template-Struktur:**
- `(eskortierverb / schutzauftrag)`
- `(escort_subject)`
- `(verbinder startpunkt escort_from)`
- `(verbinder zielpunkt escort_to)`
- `(optional rückkehrmarker)`
- `(optional überlebensbedingung ohne tote)`

#### 2.12 Endteil-Snippet
**Template-ID:** `assignment.end`

**Zweck:**
Schluss des Questangebots. Muss alle Questtypen tragen können.

**Pflicht-Parameter:**
- `text_category = assignment`
- `text_part = end`
- `quest_type`
- `acceptance_required = true`

**Codefunktion im Satzbau:**
- Abschluss des Angebots
- optionale Betonung von Vollständigkeit / Rückkehr / Verlässlichkeit

**Erwarteter Snippet-Inhalt:**
- Rückkehrhinweis
- Vollständigkeitshinweis
- Verbindlichkeitshinweis
- keine Belohnungsdetails

**Template-Struktur:**
- `(abschlussverbinder)`
- `(anforderung an vollständige erledigung)`
- `(optional rückkehr-/meldungsmarker)`

#### 2.13 N/A-Snippet
**Template-ID:** `assignment.na`

**Pflicht-Parameter:**
- `text_category = assignment_na`
- `na_reason = rolled_zero | cap_reached | no_valid_targets | no_known_targets | timer_reset`
- `time_of_day`
- `worldstress_zone`

**Codefunktion im Satzbau:**
- Ausgabe, dass kein Angebot entsteht

**Erwarteter Snippet-Inhalt:**
- kein Auftrag verfügbar
- kein Logeintrag
- kein DB-Eintrag
- nur Gesprächsausgabe

**Template-Struktur:**
- `(hinweis auf fehlendes angebot)`
- `(optional kurzer grundhinweis über na_reason)`

---

### 3. Belohnungstexte – Snippet-Templates

#### 3.1 Hauptteil-Snippet
**Template-ID:** `reward.main`

**Pflicht-Parameter:**
- `text_category = reward`
- `text_part = main`
- `quest_type`
- `quest_id`
- `reward_state = claimable | accepted | failed`

**Codefunktion im Satzbau:**
- Einstieg in Belohnungsübergabe / Belohnungsbewertung

**Erwarteter Snippet-Inhalt:**
- Übergabestart
- Anerkennung der Auftragsrückkehr / Ergebnislage
- keine exakte Belohnungshöhe im Hauptteil erzwingen

**Template-Struktur:**
- `(übergabe-/auswertungsanker)`
- `(optional bestätigung des questergebnisses)`

#### 3.2 Mittelteil-Snippet – Item-Belohnung
**Template-ID:** `reward.middle.item`

**Pflicht-Parameter:**
- `reward_text_mode = item`
- `reward_items`
- `reward_total_copper`
- `quest_id`

**Codefunktion im Satzbau:**
- eigentliche Belohnungsnennung

**Erwarteter Snippet-Inhalt:**
- Benennung physischer Belohnung aus Quest-DB

**Template-Struktur:**
- `(hinweis auf physische belohnung)`
- `(belohnungsobjekte aus reward_items)`
- `(optional wertmarker reward_total_copper)`

#### 3.3 Mittelteil-Snippet – Währungsbelohnung
**Template-ID:** `reward.middle.currency`

**Pflicht-Parameter:**
- `reward_text_mode = currency`
- `reward_currency_copper`
- `reward_currency_silver`
- `reward_currency_gold`
- `reward_total_copper`

**Template-Struktur:**
- `(hinweis auf geld-/währungsbelohnung)`
- `(währungsaufteilung)`
- `(optional gesamtwert in kupfer)`

#### 3.4 Mittelteil-Snippet – Mischbelohnung
**Template-ID:** `reward.middle.mixed`

**Pflicht-Parameter:**
- `reward_text_mode = mixed`
- `reward_items`
- `reward_currency_copper`
- `reward_currency_silver`
- `reward_currency_gold`
- `reward_total_copper`

**Template-Struktur:**
- `(hinweis auf kombinierte belohnung)`
- `(objektbelohnung)`
- `(verbinder zur währungsbelohnung)`
- `(währungsaufteilung)`

#### 3.5 Mittelteil-Snippet – Fehlgeschlagen
**Template-ID:** `reward.middle.failed`

**Pflicht-Parameter:**
- `reward_state = failed`
- `final_reward_copper`

**Template-Struktur:**
- `(hinweis auf fehlgeschlagenen questabschluss)`
- `(optional hinweis auf entfallene oder negative belohnung)`

#### 3.6 Endteil-Snippet
**Template-ID:** `reward.end`

**Pflicht-Parameter:**
- `text_category = reward`
- `text_part = end`
- `reward_state = claimable | accepted | failed`

**Codefunktion im Satzbau:**
- Abschluss der Belohnungsausgabe
- Übergang in Abschied oder Logbuchstatuswechsel

**Template-Struktur:**
- `(abschluss der übergabe)`
- `(optional bestätigung des statuswechsels)`
- `(verbinder zum gesprächsende)`

---

### 4. Abschiedstexte – Snippet-Templates

#### 4.1 Hauptteil-Snippet
**Template-ID:** `farewell.main`

**Pflicht-Parameter:**
- `text_category = farewell`
- `text_part = main`
- `quest_family = NQ`
- `time_of_day = morning | day | evening | night`
- `conversation_result = accepted | declined | no_offer | inventory_full | cap_reached | blocked`

**Codefunktion im Satzbau:**
- erster Abschiedsanker nach Gesprächsausgang

**Erwarteter Snippet-Inhalt:**
- Tageszeitbezogener Abschiedsstart
- noch keine tiefere Lagebewertung

**Template-Struktur:**
- `(abschiedsanker auf basis von time_of_day)`
- `(optionaler kurzer übergang aus dem gesprächsergebnis)`

#### 4.2 Mittelteil-Snippet
**Template-ID:** `farewell.middle`

**Pflicht-Parameter:**
- `text_category = farewell`
- `text_part = middle`
- `worldstress_zone = ruhig | belebt | hektisch`
- `debug_hq_mode = INCLUDE_HQ | EXCLUDE_HQ`
- `conversation_result = accepted | declined | no_offer | inventory_full | cap_reached | blocked`
- `open_quests_count`
- `completed_quests_count`

**Codefunktion im Satzbau:**
- Lage- und Ergebniskommentar vor dem eigentlichen Abschluss

**Erwarteter Snippet-Inhalt:**
- situativer Abschiedskommentar
- Reaktion auf Annahme / Ablehnung / Blockade / N/A

**Template-Struktur:**
- `(lageeinordnung auf basis von worldstress_zone)`
- `(reaktion auf conversation_result)`
- `(optional rückbezug auf queststatus des spielers)`

#### 4.3 Endteil-Snippet
**Template-ID:** `farewell.end`

**Pflicht-Parameter:**
- `text_category = farewell`
- `text_part = end`
- `conversation_result`
- `questlog_update_done = true | false`

**Codefunktion im Satzbau:**
- endgültiger Gesprächsabbruch
- letzte Handlungsrichtung

**Erwarteter Snippet-Inhalt:**
- endgültiger Abschlusssatz
- optional Rückkehr-/Erledigungshinweis

**Template-Struktur:**
- `(endgültiger abschied)`
- `(optional rückkehr-/abschlussmarker passend zu conversation_result)`

---

### 5. Zusammensetzungsregeln für alle Kategorien

#### 5.1 Harte Reihenfolge
- `greeting = main -> middle -> end`
- `assignment = main -> middle -> end`
- `reward = main -> middle -> end`
- `farewell = main -> middle -> end`

#### 5.2 Harte Anschlusslogik
- Ein `main`-Snippet darf niemals schon wie ein kompletter Satzblock mit Schlussfunktion wirken.
- Ein `middle`-Snippet muss semantisch aus `main` weiterlaufen können.
- Ein `end`-Snippet muss wie ein echter Abschluss oder Übergang wirken.

#### 5.3 Harte Ausschlüsse
- Kein Snippet darf zugleich Gruß, Auftrag und Abschied abdecken.
- Auftragssnippets dürfen eine Belohnung **nur als Anzeige-Referenz** nennen (damit der Spieler einen Annahmegrund sieht).
  - Der Wert/Inhalt kommt aus dem **Rewardgenerator/Questdatensatz**.
  - Im Snippet sind dafür nur **Platzhalter-Referenzen** erlaubt (keine Berechnung, keine frei erfundenen Belohnungen).
- Kein Belohnungssnippet darf neue Questanforderungen formulieren.
- Kein Abschiedssnippet darf wie ein neuer Gruß klingen.

#### 5.4 Harte Parameterbindung
- Tageszeit darf nur bei `greeting` und `farewell` strukturgebend sein.
- Weltstress darf nur dort wirken, wo er im Plan definiert wurde.
- Questtypische Objektparameter dürfen nur in passenden `assignment.middle`-Snippets erscheinen.
- Belohnungswerte kommen immer über Quest-DB-Verweis, nie direkt aus isolierten Text-Snippets.

#### 5.5 Bauziel
- Wer den Plan liest, muss sofort erkennen:
  - welcher Codeblock die Parameter liefert
  - welcher Snippet-Pool abgefragt wird
  - welcher Teil des sichtbaren Textes dadurch entsteht
  - an welcher Stelle dieser Baustein im Gesamtsatz sitzt

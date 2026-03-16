# ZQS Quest-Inhaltslogik Referenz v1

## Konstellationsrolle

Dieses Dokument ist innerhalb der aktuellen ZQS-Dokumentkonstellation die primäre Referenz für möglichen Questinhalt.

Der Primärstatus gilt, weil die in den übrigen Dokumenten vorhandene Inhaltslogik hier gesammelt ist.
Punkte, die in den Quell-Dokumenten offen bleiben, bleiben auch hier offen.
Dadurch wird keine neue Logik erfunden.


## Zweck

Dieses Dokument ist die kanonische Referenz für den möglichen Inhalt einer Quest.

Es definiert ausschließlich:
- welche Questarten und Subtypen freigegeben sind
- welche Zielklassen innerhalb einer Quest verwendet werden dürfen
- welche Felder den Questinhalt beschreiben
- welche Inhaltsquellen die Dokumente bereits nennen
- welche Inhaltslisten aktuell als questrelevant beschrieben sind

Es definiert **nicht** neue Questarten.
Es definiert **nicht** neue Rewardformeln.
Es definiert **nicht** neue Textregeln außerhalb der Quell-Dokumente.

## Quellbasis

Nur diese Dokumente wurden verwendet:

1. `zqs_master_alignment_v_1_1.md`
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_textpool_szenarien_analyse1.md`
4. `zqs_snippetkatalog_v1.md`
5. `zqs_textpool_starter_v1.md`
6. `WanderQuestGuy_ZQS_WorkflowPlan.md`

## Kanonische Geltung

Für Questinhalt gilt:

1. Der fachliche Ursprung der Inhaltsregeln liegt in `zqs_wander_quest_guy_plan1.0.md`.
2. Diese Datei ist die primäre Bereichsreferenz für möglichen Questinhalt.
3. Die Kopplung und Normierung der Questarten liegt in `zqs_master_alignment_v_1_1.md`.
4. Die Feld- und Inhaltsanbindung liegt in `zqs_textpool_szenarien_analyse1.md`.
5. Der Snippetkatalog definiert, an welchen Textstellen welche Inhaltsfelder überhaupt sichtbar werden dürfen.
6. Der Workflow-Plan ist nur Arbeitssteuerung.

## Harte Grundregeln für Questinhalt

### 1. Freigegebene Questarten

Freigegeben sind nur:
- `sammeln`
- `liefern`
- `craften`
- `finden`
- `eskortieren`

### 2. Freigegebene Questsubtypen

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

### 3. Warenlieferung ist kein eigener Haupttyp

`Warenlieferung` ist normiert als:

- `quest_type = craften`
- `quest_subtype = craften.delivery`

### 4. Zielklassen sind keine Questarten

Tiere, Gegner, Regionen, NPCs, POIs und Objekte sind keine eigenen Questarten.
Sie sind nur Zielklassen innerhalb freigegebener Questarten.

### 5. Questziel-Pool = Alle heißt nicht ungefiltert

`Questziel-Pool = Alle` bedeutet:
alle Ziele, die unter den aktiven Regeln zulässig sind.

Das umfasst insbesondere:
- Questart
- Questsubtyp
- `SL_ID / StoryLinePhase`
- Known-Flag / Known-State
- freigegebene Runtime-Inhalte

### 6. Generator arbeitet auf Runtime-Ebene

Der Generator darf nur mit `ContentCatalogRuntime` arbeiten.

Die Textengine darf zusätzlich vorbereitete Klasseninfos aus `ContentCatalogAsset` nutzen, aber nicht den Zielpool frei erweitern.

### 7. Known-State filtert Questinhalte

Known-State ist eigener Prüfblock.
Mindestens betroffen sind:
- Items
- Regionen
- Harvestables
- Livings
- POIs
- NPCs

Nur Inhalte, die unter den geltenden Known-/SL-Regeln zulässig sind, dürfen als Questinhalt auftauchen.

## Questinhalt als Datenmodell

Ein Questinhalt besteht im Kern aus:

- `quest_family`
- `quest_type`
- `quest_subtype`
- `target_block`
- `expected_time_sec`
- `reward_formula_type`
- `source_rule`

Zusätzlich kommen je nach Questart die subtype-spezifischen Inhaltsfelder dazu.

## Pflicht-Felder für die inhaltliche Auswahl

Aus den Dokumenten ergeben sich als questinhaltlich relevante Felder mindestens:

- `quest_type`
- `quest_subtype`
- `target_class`
- `target_id`
- `target_name`
- `target_tags`
- `target_value_copper`
- `target_quantity`
- `known_flag_required`
- `known_flag_state`
- `sl_id_current`
- `story_depth_current`
- `stress_factor_value`
- `expected_time_sec`
- `reward_formula_type`
- `quest_nr`
- `quest_id_preview`
- `source_rule`

## Questinhalte pro Questart

## 1. sammeln

### Zielklassen
- `item`
- `harvestable`

### Datenszenario
- `target_class = item | harvestable`
- `target_name`
- `target_quantity`
- `target_value_copper`
- `harvest_source = tree | rock | ore | bush | fish_spot | inventory_only`

### subtype: `sammeln.item`

#### Pflichtfelder
- `quest_type = sammeln`
- `quest_subtype = sammeln.item`
- `target_class = item`
- `target_name`
- `target_quantity`
- `target_value_copper`
- `known_flag_state`
- `sl_id_current`
- `expected_time_sec`

### subtype: `sammeln.harvestable`

#### Pflichtfelder
- `quest_type = sammeln`
- `quest_subtype = sammeln.harvestable`
- `target_class = harvestable`
- `target_name`
- `target_quantity`
- `source_node_type`
- `known_flag_state`
- `sl_id_current`
- `expected_time_sec`

## 2. liefern

### Zielklassen
- transportierbare Items
- Ziel-Entity
- Ziel-Region
- optional Pickup-Entity

### Datenszenario
- `delivery_item_id`
- `delivery_item_name`
- `delivery_quantity`
- `pickup_entity`
- `target_entity`
- `target_region`

### subtype: `liefern.item`

#### Pflichtfelder
- `quest_type = liefern`
- `target_class = item`
- `target_name`
- `target_quantity`
- `target_entity`
- `target_region`
- `pickup_entity`
- `expected_time_sec`

## 3. craften

### Zielklassen
- Craft-Rezept
- Craft-Output
- optionale Inputliste
- bei `craften.delivery` zusätzlich Empfänger / Zielort

### Datenszenario
- `recipe_id`
- `output_item_id`
- `output_item_name`
- `output_quantity`
- `input_items`
- `output_value_copper`

### subtype: `craften.recipe_output`

#### Pflichtfelder
- `quest_type = craften`
- `quest_subtype = craften.recipe_output`
- `recipe_id`
- `output_item_name`
- `output_quantity`
- `input_items`
- `expected_time_sec`

### subtype: `craften.delivery`

#### Pflichtfelder
- `quest_type = craften`
- `quest_subtype = craften.delivery`
- `output_item_name`
- `output_quantity`
- `target_entity`
- `target_region`
- `expected_time_sec`

## 4. finden

### Zielklassen
- `poi`
- `poi_loot`
- `person`
- `object`

### Datenszenario
- `find_target_kind = poi | poi_loot | person | object`
- `find_target_name`
- `find_region`
- `find_content_reference`

### subtype: `finden.poi`

#### Pflichtfelder
- `quest_type = finden`
- `quest_subtype = finden.poi`
- `find_target_kind = poi`
- `target_name`
- `target_region`
- `expected_time_sec`

### subtype: `finden.poi_loot`

#### Pflichtfelder
- `quest_type = finden`
- `quest_subtype = finden.poi_loot`
- `target_name`
- `find_content_reference`
- `expected_time_sec`

### subtype: `finden.person`

#### Status
- später genauer definiert

#### Derzeit vorhandene Inhaltsaussage
- Person finden ist als möglicher Questinhalt vorgesehen
- die Parameter sind noch nicht fertig standardisiert

### subtype: `finden.object`

#### Pflichtfelder
- `quest_type = finden`
- `quest_subtype = finden.object`
- `target_name`
- `target_quantity`
- `target_region`
- `expected_time_sec`

## 5. eskortieren

### Datenszenario
- `escort_rule_source = Escort_Rules`
- `escort_subject`
- `escort_from`
- `escort_to`
- `escort_return_required = true`
- `escort_no_death_required = true`
- `escort_region_count`

### subtype: `eskortieren.route`

#### Pflichtfelder
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

## Inhaltsquellen, die die Dokumente bereits benennen

## 1. Ressourcen / direkte Sammelziele

### Ressourcen
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

### Materialien
| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 10 | Iron Ingot | 7 | ingot, iron |
| 11 | Copper Ingot | 6 | ingot, copper |
| 12 | Silver Ingot | 12 | ingot, silver |
| 13 | Gold Ingot | 16 | ingot, gold |

### Werkzeuge
| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 14 | Axe | 25 | tool, axe |
| 15 | Pickaxe | 25 | tool, pickaxe |
| 16 | Knife | 20 | tool, knife |
| 17 | Hammer | 30 | tool, hammer |
| 18 | Climbing Tool | 40 | tool, climb |
| 19 | Net | 18 | tool, net |
| 36 | Angel | 35 | tool, fishing, rod |

### Waffen
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

### Bauobjekte
| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 40 | Workbench | 80 | build, station, crafting |
| 41 | Campfire | 30 | build, cooking, heat |
| 42 | Chest | 60 | build, storage |
| 43 | Bed | 70 | build, sleep |
| 44 | Anvil | 150 | build, station, smithing |
| 45 | Smelter | 120 | build, station, smelt |
| 46 | Lamp | 40 | build, light |

### Nahrung
| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 28 | Meat | 6 | food |
| 29 | Cooked Meat | 10 | food, cooked |
| 34 | Beeren | 3 | food, forage |
| 35 | Fisch | 5 | food, fish |

### Verbrauchbares
| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 30 | Water | 2 | drink |

### Währung
| ID | Name | Kupferwert | Tags |
| --- | --- | --- | --- |
| 31 | Münzen (Kupfer) | 1 | currency, coin, copper |
| 32 | Münzen (Silber) | 100 | currency, coin, silver |
| 33 | Münzen (Gold) | 100000 | currency, coin, gold |

## 2. Craftbare Outputs / Herstellungsziele

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

## 3. Harvestables / direkte Weltquellen

| Harvestable | Werkzeug | Drop | Kupferwert pro erfolgreichem Harvest | Hinweis |
| --- | --- | --- | --- | --- |
| NODE_TREE | Axt (14) | Wood x5 | 5 | ersetzt durch Stumpf |
| NODE_ROCK | Pickaxe (15) | Stone x4 | 4 | verschwindet |
| NODE_ORE_IRON | Pickaxe (15) | Iron Ore x2 | 6 | verschwindet |
| NODE_BUSH | Hand | Beeren x1 | 3 | 15–60 min Regrow |
| NODE_FISH_SPOT | Angel (36) | Fisch x1 | 5 | 15–60 min Regrow |

## 4. Tiere

| Tier | HP | Loot | Kupferwert Range |
| --- | --- | --- | --- |
| animal_deer | 20 | Meat 2-2 | 12–12 |
| animal_boar | 28 | Meat 2-3 | 12–18 |
| animal_wolf | 26 | Meat 1-2 | 6–12 |
| animal_rabbit | 12 | Meat 1-1 | 6–6 |
| animal_bear | 55 | Meat 3-5 | 18–30 |
| animal_fox | 16 | Meat 1-1 | 6–6 |

### Kanonische Einordnung
Tiere sind aktuell benannte Inhaltsklasse.
Sie sind laut Master keine eigene Questart.
Sie können nur dort Inhalt werden, wo ein freigegebener Questtyp sie als Zielklasse tragen darf.

## 5. Gegner

| Gegner | HP | DMG | XP | Loot | Kupferwert Range |
| --- | --- | --- | --- | --- | --- |
| ork_grunt | 35 | 6 | 10 | Münzen (Kupfer) 1-8, Wood 0-2 | 1–10 |
| ork_spearman | 40 | 7 | 12 | Münzen (Kupfer) 2-10, Stone 0-2 | 2–12 |
| ork_archer | 30 | 8 | 14 | Münzen (Kupfer) 3-12, Herbs 0-1 | 3–14 |
| ork_shaman | 45 | 9 | 18 | Münzen (Kupfer) 5-16, Crystal 0-1 | 5–26 |
| ork_brute | 70 | 12 | 28 | Münzen (Kupfer) 8-25, Iron Ore 0-1 | 8–28 |
| ork_guard | 60 | 11 | 24 | Münzen (Kupfer) 7-22, Copper Ore 0-1 | 7–25 |

### Kanonische Einordnung
Gegner sind aktuell benannte Inhaltsklasse.
Sie sind laut Master keine eigene Questart.
Die angehängten Dokumente geben ihnen aktuell keinen eigenen freigegebenen Questsubtyp.

## 6. NPCs / Gesprächspartner

- `MERCHANT_ELF`
- `MERCHANT_WANDERING`
- `WANDER_QUEST_GUY`

### Kanonische Einordnung
NPCs sind Ziel- oder Kontextklasse.
Sie sind keine eigene Questart.

## 7. POIs / Landmarken / ortsgebundene Ziele

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

### Kanonische Einordnung
Diese Inhalte sind besonders relevant für:
- `finden.poi`
- `finden.poi_loot`

## Relevanzzuordnung nach Dokumentenlage

### Direkt relevant für Auftragstexte
- **sammeln**
  - alle Items mit `type=resource`
  - alle realen Harvestables
- **liefern**
  - prinzipiell alle transportierbaren Items
- **craften / craften.delivery**
  - alle Rezept-Outputs
- **finden**
  - POIs
  - versteckte Truhen
  - Landmarken
  - NPCs
  - Objekte
- **eskortieren**
  - verweist auf `Escort_Rules`

### Direkt relevant für Belohnungslogik
- alle Items mit Kupferwert
- alle Währungsitems
- alle berechenbaren Output-Werte aus Recipes / Harvest / Loot

### Direkt relevant für Textlogik
- `target_name`
- `target_quantity`
- `target_region`
- `target_entity`
- `pickup_entity`
- `output_item_name`
- `output_quantity`
- `find_content_reference`
- `escort_subject`
- `escort_from`
- `escort_to`

## Sichtbarkeitsregeln in der Textengine

### 1. Nur `assignment.middle` trägt konkrete Questobjekte

Questtypische Objektparameter dürfen nur in passenden `assignment.middle`-Snippets erscheinen.
Greeting und Farewell dürfen daraus keine konkreten Questinhalte machen.

### 2. Assignment nennt keine Belohnung

Questinhalt und Reward bleiben getrennt.

### 3. Reward nennt keine neue Aufgabe

Die Rewardseite darf den Questinhalt nicht neu formulieren.

## Offene oder bewusst unvollständige Punkte

Diese Punkte bleiben offen, weil die Quell-Dokumente sie nicht als fertige Fachregel ausformulieren:

1. `finden.person` ist als Inhalt vorgesehen, aber noch nicht vollständig parameterisiert
2. Tiere und Gegner sind benannte Inhaltsklassen, aber kein freigegebener eigener Questtyp
3. Zielpool-Details sind im Plan 1.0 ausdrücklich nicht weiter ausgearbeitet
4. Escort verweist auf `Escort_Rules`, die als Regelquelle genannt, aber hier nicht ausformuliert werden

## Operativer Kernsatz

Questinhalt entsteht nur aus freigegebenen Questarten, Subtypen und zulässigen Zielklassen.
Der Generator darf nur auf Runtime-freigegebene, Known-/SL-konforme Inhalte zugreifen.
Die Textengine darf diese Inhalte nur an den dafür vorgesehenen Stellen sichtbar machen.

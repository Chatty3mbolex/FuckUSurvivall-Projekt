# ZQS Reward-Logik Referenz v1

## Zweck

Dieses Dokument ist die kanonische Referenz für alle Reward-Angelegenheiten des ZQS.

Es definiert ausschließlich:
- woher Rewardwerte kommen
- wie Rewardsummen berechnet werden
- welche Zwischen- und Endwerte dabei entstehen
- wo diese Zahlen im System verwendet werden

Es definiert **nicht** den Workflow.
Es definiert **nicht** die Textbausteine selbst.
Es definiert **nicht** neue Rewardregeln außerhalb der Quell-Dokumente.

## Quellbasis

Nur diese Dokumente wurden verwendet:

1. `zqs_master_alignment_v_1_1.md`
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_textpool_szenarien_analyse1.md`
4. `zqs_snippetkatalog_v1.md`
5. `zqs_textpool_starter_v1.md`
6. `WanderQuestGuy_ZQS_WorkflowPlan.md`

## Kanonische Geltung

Für Rewardlogik gilt:

1. Die fachliche Wahrheit liegt in `zqs_wander_quest_guy_plan1.0.md`.
2. Die Kopplung zu Generator, Persistenz und NPC-Docking liegt in `zqs_master_alignment_v_1_1.md`.
3. Die Textdokumente legen nur fest, welche Reward-Felder die Textengine lesen darf.
4. Der Workflow-Plan ist nur Arbeitssteuerung und ersetzt keine Rewardformel.

## Harte Grundregeln

### 1. Reward wird vor dem Textbau berechnet

Der Reward wird vollständig berechnet, bevor ein sichtbarer Rewardtext gebaut wird.

Die Textengine:
- berechnet keine Rewardhöhe
- erfindet keine Rewardwerte
- liest nur den fertigen Rewardblock

### 2. WQG berechnet keinen Reward

Der `WanderQuestGuy` ist nur Ausgabepunkt / Vermittlungsschicht.
Er darf:
- Reward-Claim auslösen
- Rewardtexte anzeigen

Er darf nicht:
- Rewardhöhe berechnen
- Rewardformeln auswerten
- Questwerte erfinden

### 3. Wert bedeutet immer Kupferwert

Wenn in diesem Dokument `Wert` steht, ist immer der Kupferwert des beteiligten Objekts gemeint.

Das betrifft insbesondere:
- Items
- Harvestables
- Craft-Outputs
- Inhaltswerte von POIs / Loot
- weitere beteiligte Spielobjekte, sofern im Plan mit `Wert` bezeichnet

### 4. Reward kommt aus Questinhalt plus Zeit

Die Rewardberechnung speist sich aus:
- Questart / Questsubtyp
- Anzahl beteiligter Inhalte
- Kupferwert dieser Inhalte
- erwarteter Questzeit
- tatsächlicher Questzeit
- Penalty-Regel

Weltstress ist **kein** Reward-Eingabewert.
Textparameter sind **keine** Reward-Eingabewerte.

### 5. `reward_state` und `reward_text_mode` sind getrennte Achsen

Die Rewardauswertung und die Textdarstellung benutzen getrennte Zustände:

- `reward_state = claimable | accepted | failed`
- `reward_text_mode = item | currency | mixed | failed`

`failed` kann textseitig als eigener Reward-Textmodus geführt werden.
Das ändert nichts daran, dass die Fehlschlagbedingung fachlich über die Rewardrechnung bestimmt wird.

## Reward-Herkunft pro Questart

## 1. sammeln

### Eingabewerte
- `Anzahl Harvestables`
- `Wert pro Harvestable`
- `Anzahl Items`
- `Wert pro Item`
- `Zeit in sec`

### Formel
`base_reward_copper = (Anzahl Harvestables x Wert + Anzahl Items x Wert) + (Zeit in sec x 33)`

### Quelle der Zahlen
- Mengen und Objektwerte kommen aus dem Questinhalt
- Zeit kommt aus `expected_time_sec`

---

## 2. liefern

### Eingabewerte
- `Anzahl Harvestables`
- `Wert pro Harvestable`
- `Anzahl Items`
- `Wert pro Item`
- `Zeit in sec`

### Formel
`base_reward_copper = (Anzahl Harvestables x Wert + Anzahl Items x Wert) + (Zeit in sec x 33)`

### Quelle der Zahlen
- Mengen und Objektwerte kommen aus dem Lieferinhalt
- Zeit kommt aus `expected_time_sec`

---

## 3. craften

### Eingabewerte
- `Anzahl Harvestables`
- `Wert pro Harvestable`
- `Anzahl Items`
- `Wert pro Item`
- `Anzahl QItem`
- `Wert pro QItem`
- `Zeit in sec`

### Vorstufe
`QItem = (Anzahl Harvestables + Wert) + (Anzahl Items + Wert)`

### Formel
`base_reward_copper = (Anzahl Harvestables x Wert + Anzahl Items x Wert + Anzahl QItem x Wert) + (Zeit in sec x 33)`

### Quelle der Zahlen
- Inputs kommen aus dem Craft-Rezept / Questinhalt
- QItem ist Bestandteil der fachlichen Rewardlogik für Craftquests
- Zeit kommt aus `expected_time_sec`

---

## 4. craften.delivery

### Eingabewerte
- `Anzahl Harvestables`
- `Wert pro Harvestable`
- `Anzahl Items`
- `Wert pro Item`
- `Anzahl QItem`
- `Wert pro QItem`
- `Zeit in sec`

### Vorstufe
`QItem = (Anzahl Harvestables + Wert) + (Anzahl Items + Wert)`

### Formel
`base_reward_copper = (Anzahl Harvestables x Wert + Anzahl Items x Wert + Anzahl QItem x Wert) + (Zeit in sec x 33)`

### Quelle der Zahlen
- kombiniert Herstellungsinhalt und Lieferbezug
- Zeit kommt aus `expected_time_sec`

---

## 5. finden

### 5.1 finden.poi / finden.poi_loot

#### Eingabewerte
- `Wert des Inhalts`
- `Zeit in sec`

#### Formel
`base_reward_copper = Wert des Inhalts + (Zeit in sec x 33)`

#### Quelle der Zahlen
- Inhaltswert kommt aus dem Zielinhalt des POI / der Lootquelle
- Zeit kommt aus `expected_time_sec`

### 5.2 finden.person

#### Status
- später genauer definiert

#### Konsequenz
Dieses Dokument legt keine eigene Rewardformel für `finden.person` fest, weil die Quell-Dokumente hier keine fertige Berechnung liefern.

### 5.3 finden.object

#### Eingabewerte
- `Anzahl Harvestables`
- `Wert pro Harvestable`
- `Anzahl Items`
- `Wert pro Item`
- `QItem Wert`
- `Zeit in sec`

#### Formel
`base_reward_copper = (Anzahl Harvestables x Wert + Anzahl Items x Wert + QItem Wert) + (Zeit in sec x 33)`

#### Quelle der Zahlen
- kommt aus dem Objektinhalt des Questziels
- Zeit kommt aus `expected_time_sec`

---

## 6. eskortieren

### Eingabewerte
- `Zeit in sec`

### Standardformel
`base_reward_copper = (Zeit in sec x 33)`

### Sonderfall
`von -> nach -> zurückkommen ohne Tote`

Dann gilt:
`base_reward_copper = (Zeit in sec x 33) + 50%`

### Quelle der Zahlen
- Zeit kommt aus der Escort-Zeitformel
- die Sonderregel wird nur angewendet, wenn der Escort-Sonderfall exakt so vorliegt

## Zeitlogik als Reward-Eingabe

### 1. Zeit ist Pflichtteil der Rewardlogik

Bei jeder Questgenerierung muss eine Zeitangabe mit erzeugt werden.
Diese Zeitangabe definiert, wie lange die Quest dauern sollte.
Dieser Wert geht direkt in die Rewardformel ein.

### 2. Standard-Zeitformel

Für Quests mit Objekt-/Inhaltsbezug gilt:

`expected_time_sec = Anzahl Objekte (alle in der Quest) x 120 x 0.86`

### 3. Escort-Zeitformel

Für Eskortierquests gilt:

`expected_time_sec = Anzahl Gebiete zu durchqueren (alle in der Quest) x 420 x 0.86 + 300`

## Penalty-Logik

### 1. Keine Penalty

Wenn:
`elapsed_time_sec <= expected_time_sec`

Dann:
`penalty_copper = 0`

### 2. Penalty bei Überschreitung

Wenn:
`elapsed_time_sec > expected_time_sec`

Dann:
`penalty_copper = (elapsed_time_sec - expected_time_sec) x 150`

### 3. Finaler Rewardwert

`final_reward_copper = base_reward_copper - penalty_copper`

### 4. Fehlschlagbedingung

Wenn:
`final_reward_copper < 0`

Dann:
- Queststatus = `fehlgeschlagen`
- Rewardauswertung endet im Failed-Zweig
- kein regulärer Rewardanspruch

## Technische Reward-Ausgabeobjekte

## 1. GeneratedQuestOffer

Ein Angebot trägt bereits:
- `reward_block`
- `expected_time_sec`

Solange eine NQ nicht angenommen wurde:
- liegt sie nur im `OfferBuffer`
- ist nicht persistent

## 2. PersistentQuestRecord / PlayerQuestDB

Nach Annahme muss die Quest mit Rewardbezug persistiert werden.

Die Rewarddaten dienen dann als Referenz für:
- Questlogbuch
- History
- Reward-Claim
- Rewardtexte

## 3. QuestHistoryIndex

Die History speichert nicht die komplette Rewardberechnung neu, sondern den Quest-Endzustand der bereits vorhandenen Questreferenz.

Fehlgeschlagene Quests:
- bleiben als bereits vorhandene Quest in der History sichtbar
- tragen den Endstatus `fehlgeschlagen`

## Reward-Felder, die systemisch benutzt werden

Dieses Dokument definiert nicht neue Felder, sondern sammelt die in den Quell-Dateien bereits verwendeten Rewardfelder.

### Rechenwerte
- `base_reward_copper`
- `penalty_copper`
- `final_reward_copper`
- `expected_time_sec`
- `elapsed_time_sec`

### Text-/Ausgabewerte
- `reward_state`
- `reward_text_mode`
- `reward_total_copper`
- `reward_items`
- `reward_currency_copper`
- `reward_currency_silver`
- `reward_currency_gold`
- `giver_name`
- `pickup_location`

### Kontextwerte
- `quest_id`
- `quest_type`
- `quest_subtype`
- `target_name`
- `target_quantity`
- `reward_formula_type`

## Wo die Rewardzahlen eingesetzt werden

## 1. In der Questinstanzierung

Bei der Questinstanzierung müssen erzeugt oder eingetragen werden:
- `reward_block`
- `expected_time_sec`

Die Rewardberechnung gehört also in die Instanzierungs-/Generatorseite, nicht in die Textseite.

## 2. Im Questlogbuch / UI

Für angenommene Quests muss das UI anzeigen:
- wie hoch die Belohnung ist
- von wem die Quest aufgenommen wurde
- wo die Quest aufgenommen wurde
- seit wann die Quest aktiv ist

Nach Abschluss zusätzlich:
- wann sie abgeschlossen wurde

Dafür wird der Reward aus der Questreferenz gelesen.

## 3. In der Reward-Textengine

Reward-Snippets dürfen:
- Rewardwerte referenzieren
- Rewardzustand referenzieren
- Rewardmodus referenzieren

Reward-Snippets dürfen nicht:
- Reward berechnen
- Rewardformel verändern
- Questanforderungen neu formulieren

## 4. In der Fehlschlaglogik

`final_reward_copper` entscheidet:
- ob Penalty nur reduziert
- oder ob die Quest fachlich als fehlgeschlagen endet

## 5. In der Gesprächstrennung

- Assignment nennt keine Belohnung
- Reward nennt keine neue Aufgabe

Dadurch bleibt klar:
- Questinhalt entsteht im Assignment
- Rewardauswertung entsteht im Rewardblock

## Was diese Referenz bewusst nicht neu festlegt

Diese Punkte bleiben offen, weil die Quell-Dokumente sie nicht als fertige Fachregel ausformulieren:

1. Wie `reward_total_copper` intern genau aus `base_reward_copper` und `final_reward_copper` abgeleitet oder benannt wird
2. Wie der Reward konkret zwischen `item | currency | mixed` verteilt wird
3. Wie `finden.person` später exakt belohnt wird
4. Ob zusätzlich XP existieren sollen; der Workflow-Plan nennt generische Felder, die fachliche Logik Plan 1.0 liefert dafür aber keine aktive Regel

## Operativer Kernsatz

Reward entsteht aus Questinhalt plus Zeit.
Die Fachformeln kommen ausschließlich aus Plan 1.0.
Der Generator erzeugt daraus den Rewardblock.
Der `WanderQuestGuy` vermittelt ihn nur.
Die Textengine liest ihn nur.

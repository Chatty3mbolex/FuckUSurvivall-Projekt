# ZQS Textpool Starter v1

## Konstellationsrolle

Dieses Dokument ist der Starter-Datenbestand konkreter Snippets.
Es ist keine primäre Logikquelle.

Seine Vorgaben kommen aus:
- `zqs_master_alignment_v_1_1.md`
- `zqs_snippetkatalog_v1.md`
- `zqs_reward_logik_referenz_v1.md`
- `zqs_quest_inhaltslogik_referenz_v1.md`
- `zqs_textpool_szenarien_analyse1.md`


Globaldefaults für alle Snippets, sofern nicht abweichend angegeben:
- `speaker_entity: WQG`
- `language: de_DE`

Notation:
- `*` = beliebiger Wert / nicht einschränkend
- Filter sind bewusst maschinenlesbar und knapp gehalten.
- Jeder Eintrag ist ein einzelner Baustein, kein Ganztext.

---

## 1. greeting

### 1.1 greeting.main

#### 1) `greeting.main.morning.001`
- `text_category: greeting`
- `text_part: main`
- `filters: time_of_day=morning; worldstress_zone=*; debug_hq_mode=*; conversation_next_step=*; nq_generation_possible=*; block_reason=*`
- `text_template: „Morgen. Hör kurz zu“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 2) `greeting.main.day.001`
- `text_category: greeting`
- `text_part: main`
- `filters: time_of_day=day; worldstress_zone=*; debug_hq_mode=*; conversation_next_step=*; nq_generation_possible=*; block_reason=*`
- `text_template: „Gut, dass du gerade hier bist“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 3) `greeting.main.evening.001`
- `text_category: greeting`
- `text_part: main`
- `filters: time_of_day=evening|night; worldstress_zone=*; debug_hq_mode=*; conversation_next_step=*; nq_generation_possible=*; block_reason=*`
- `text_template: „Späte Stunde, aber passend“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

### 1.2 greeting.middle

#### 1) `greeting.middle.ruhig.001`
- `text_category: greeting`
- `text_part: middle`
- `filters: time_of_day=*; worldstress_zone=ruhig; debug_hq_mode=*; conversation_next_step=*; nq_generation_possible=*; block_reason=none|N/A`
- `text_template: „Im Moment ist es ruhig genug, um die Dinge sauber zu ordnen“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### 2) `greeting.middle.belebt.001`
- `text_category: greeting`
- `text_part: middle`
- `filters: time_of_day=*; worldstress_zone=belebt; debug_hq_mode=*; conversation_next_step=*; nq_generation_possible=*; block_reason=none|N/A`
- `text_template: „Heute läuft einiges durcheinander, also halte ich es knapp“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### 3) `greeting.middle.hektisch.001`
- `text_category: greeting`
- `text_part: middle`
- `filters: time_of_day=*; worldstress_zone=hektisch; debug_hq_mode=*; conversation_next_step=*; nq_generation_possible=*; block_reason=none|N/A|cap_reached|timer_not_due|no_valid_targets`
- `text_template: „Gerade ist alles unter Spannung, also verschwende ich keine Worte“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

### 1.3 greeting.end

#### 1) `greeting.end.offer.001`
- `text_category: greeting`
- `text_part: end`
- `filters: time_of_day=*; worldstress_zone=*; debug_hq_mode=*; conversation_next_step=offer; nq_generation_possible=true; block_reason=none`
- `text_template: „Ich habe etwas für dich“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 2) `greeting.end.no_offer.001`
- `text_category: greeting`
- `text_part: end`
- `filters: time_of_day=*; worldstress_zone=*; debug_hq_mode=*; conversation_next_step=no_offer; nq_generation_possible=false; block_reason=N/A|rolled_zero|timer_not_due|no_valid_targets`
- `text_template: „Heute läuft daraus nichts an“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 3) `greeting.end.blocked.001`
- `text_category: greeting`
- `text_part: end`
- `filters: time_of_day=*; worldstress_zone=*; debug_hq_mode=*; conversation_next_step=blocked; nq_generation_possible=false; block_reason=cap_reached|timer_not_due|no_valid_targets`
- `text_template: „Im Moment ist der Weg dafür blockiert“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

---

## 2. assignment

### 2.1 assignment.main

#### 1) `assignment.main.generic.001`
- `text_category: assignment`
- `text_part: main`
- `filters: quest_type=*; quest_subtype=*`
- `text_template: „Also gut, hier ist der Auftrag“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 2) `assignment.main.generic.002`
- `text_category: assignment`
- `text_part: main`
- `filters: quest_type=*; quest_subtype=*`
- `text_template: „Merke dir den Kern davon“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 3) `assignment.main.generic.003`
- `text_category: assignment`
- `text_part: main`
- `filters: quest_type=*; quest_subtype=*`
- `text_template: „Das hier ist die Sache, um die es geht“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

### 2.2 assignment.middle

#### sammeln.item

##### 1) `assignment.middle.sammeln.item.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=sammeln; quest_subtype=sammeln.item`
- `text_template: „Besorg mir {target_quantity} Stück {target_name}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.sammeln.item.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=sammeln; quest_subtype=sammeln.item`
- `text_template: „Ich brauche {target_quantity}x {target_name} in brauchbarem Zustand“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.sammeln.item.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=sammeln; quest_subtype=sammeln.item`
- `text_template: „Trag {target_quantity} Einheiten {target_name} für mich zusammen“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### sammeln.harvestable

##### 1) `assignment.middle.sammeln.harvestable.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=sammeln; quest_subtype=sammeln.harvestable`
- `text_template: „Hol {target_quantity}x {target_name} direkt aus der Umgebung“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.sammeln.harvestable.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=sammeln; quest_subtype=sammeln.harvestable`
- `text_template: „Sammle {target_quantity} Stück {target_name} dort, wo sie natürlich vorkommen“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.sammeln.harvestable.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=sammeln; quest_subtype=sammeln.harvestable`
- `text_template: „Bring mir {target_quantity} Vorkommen von {target_name} aus freier Quelle“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### liefern.item

##### 1) `assignment.middle.liefern.item.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=liefern; quest_subtype=liefern.item`
- `text_template: „Bring {target_quantity}x {target_name} zu {target_entity} nach {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.liefern.item.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=liefern; quest_subtype=liefern.item`
- `text_template: „Diese Lieferung besteht aus {target_quantity} Stück {target_name} für {target_entity} in {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.liefern.item.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=liefern; quest_subtype=liefern.item`
- `text_template: „Sorge dafür, dass {target_quantity} Einheiten {target_name} bei {target_entity} in {target_region} ankommen“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### craften.recipe_output

##### 1) `assignment.middle.craften.recipe_output.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=craften; quest_subtype=craften.recipe_output`
- `text_template: „Fertige {output_quantity}x {output_item_name} an“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.craften.recipe_output.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=craften; quest_subtype=craften.recipe_output`
- `text_template: „Ich brauche {output_quantity} Stück {output_item_name} aus ordentlicher Herstellung“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.craften.recipe_output.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=craften; quest_subtype=craften.recipe_output`
- `text_template: „Stell {output_quantity} Einheiten {output_item_name} fertig“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### craften.delivery

##### 1) `assignment.middle.craften.delivery.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=craften; quest_subtype=craften.delivery`
- `text_template: „Fertige {output_quantity}x {output_item_name} an und bring sie zu {target_entity} nach {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.craften.delivery.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=craften; quest_subtype=craften.delivery`
- `text_template: „Für {target_entity} in {target_region} werden {output_quantity} Stück {output_item_name} gebraucht, gefertigt und geliefert“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.craften.delivery.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=craften; quest_subtype=craften.delivery`
- `text_template: „Erstelle {output_quantity} Einheiten {output_item_name} und sorge dafür, dass {target_entity} sie in {target_region} erhält“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### finden.poi

##### 1) `assignment.middle.finden.poi.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.poi`
- `text_template: „Finde {target_name} in {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.finden.poi.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.poi`
- `text_template: „Ich will, dass du {target_name} ausfindig machst“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.finden.poi.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.poi`
- `text_template: „Such nach {target_name}, vorzugsweise im Bereich {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### finden.poi_loot

##### 1) `assignment.middle.finden.poi_loot.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.poi_loot`
- `text_template: „Finde {target_name} und achte dort auf {find_content_reference}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.finden.poi_loot.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.poi_loot`
- `text_template: „Mich interessiert, was bei {target_name} unter {find_content_reference} zu finden ist“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.finden.poi_loot.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.poi_loot`
- `text_template: „Geh zu {target_name} und prüf dort den Hinweis auf {find_content_reference}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### finden.person

##### 1) `assignment.middle.finden.person.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.person`
- `text_template: „Finde {target_name} und bring Gewissheit über den Aufenthaltsort“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.finden.person.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.person`
- `text_template: „Such nach {target_name}, möglichst im Bereich {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.finden.person.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.person`
- `text_template: „Spür {target_name} auf und bestätige die Spur“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### finden.object

##### 1) `assignment.middle.finden.object.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.object`
- `text_template: „Finde {target_quantity}x {target_name}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.finden.object.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.object`
- `text_template: „Ich brauche den Fund von {target_quantity} Stück {target_name} aus {target_region}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.finden.object.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=finden; quest_subtype=finden.object`
- `text_template: „Halte nach {target_quantity} Einheiten {target_name} Ausschau“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### eskortieren.route

##### 1) `assignment.middle.eskortieren.route.001`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=eskortieren; quest_subtype=eskortieren.route`
- `text_template: „Bring {escort_subject} heil von {escort_from} nach {escort_to}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `assignment.middle.eskortieren.route.002`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=eskortieren; quest_subtype=eskortieren.route`
- `text_template: „Begleite {escort_subject} sicher vom Ausgangspunkt {escort_from} bis zum Ziel {escort_to}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `assignment.middle.eskortieren.route.003`
- `text_category: assignment`
- `text_part: middle`
- `filters: quest_type=eskortieren; quest_subtype=eskortieren.route`
- `text_template: „Sorge dafür, dass {escort_subject} den Weg von {escort_from} nach {escort_to} übersteht“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

### 2.3 assignment.end

#### 1) `assignment.end.generic.001`
- `text_category: assignment`
- `text_part: end`
- `filters: quest_type=*; quest_subtype=*`
- `text_template: „Meld dich danach wieder bei mir“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 2) `assignment.end.generic.002`
- `text_category: assignment`
- `text_part: end`
- `filters: quest_type=*; quest_subtype=*`
- `text_template: „Komm erst zurück, wenn der Teil erledigt ist“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 3) `assignment.end.generic.003`
- `text_category: assignment`
- `text_part: end`
- `filters: quest_type=*; quest_subtype=*`
- `text_template: „Dann sehen wir weiter, sobald du damit durch bist“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

---

## 3. assignment_na

#### 1) `assignment_na.rolled_zero.001`
- `text_category: assignment_na`
- `text_part: na`
- `filters: na_reason=rolled_zero`
- `text_template: „Für den Moment fällt daraus nichts an“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 2) `assignment_na.cap_reached.001`
- `text_category: assignment_na`
- `text_part: na`
- `filters: na_reason=cap_reached`
- `text_template: „Bevor etwas Neues startet, muss erst Altes aus dem Weg“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 3) `assignment_na.no_valid_targets.001`
- `text_category: assignment_na`
- `text_part: na`
- `filters: na_reason=no_valid_targets|no_known_targets|timer_reset|timer_not_due`
- `text_template: „Gerade ergibt sich daraus kein brauchbarer Ansatz“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

---

## 4. reward

### 4.1 reward.main

#### 1) `reward.main.claimable.001`
- `text_category: reward`
- `text_part: main`
- `filters: reward_state=claimable; reward_text_mode=*`
- `text_template: „Gut. Dann kommt jetzt dein Ausgleich“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 2) `reward.main.accepted.001`
- `text_category: reward`
- `text_part: main`
- `filters: reward_state=accepted; reward_text_mode=*`
- `text_template: „Damit ist die Sache sauber abgeschlossen“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 3) `reward.main.failed.001`
- `text_category: reward`
- `text_part: main`
- `filters: reward_state=failed; reward_text_mode=failed`
- `text_template: „So wird daraus keine reguläre Ausgabe“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

### 4.2 reward.middle

#### reward_text_mode=item

##### 1) `reward.middle.item.001`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=item`
- `text_template: „Für dich liegt {reward_items} bereit“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `reward.middle.item.002`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=item`
- `text_template: „Der Ausgleich besteht aus {reward_items}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `reward.middle.item.003`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=item`
- `text_template: „Du erhältst dafür {reward_items}“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### reward_text_mode=currency

##### 1) `reward.middle.currency.001`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=currency`
- `text_template: „Dafür gehen {reward_currency_gold} Gold, {reward_currency_silver} Silber und {reward_currency_copper} Kupfer an dich“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `reward.middle.currency.002`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=currency`
- `text_template: „Dein Anteil liegt bei {reward_currency_gold} Gold, {reward_currency_silver} Silber und {reward_currency_copper} Kupfer“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `reward.middle.currency.003`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=currency`
- `text_template: „Ausgezahlt werden {reward_currency_gold} Gold, {reward_currency_silver} Silber und {reward_currency_copper} Kupfer“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### reward_text_mode=mixed

##### 1) `reward.middle.mixed.001`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=mixed`
- `text_template: „Für dich gibt es {reward_items} sowie {reward_currency_gold} Gold, {reward_currency_silver} Silber und {reward_currency_copper} Kupfer“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `reward.middle.mixed.002`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=mixed`
- `text_template: „Der Ausgleich setzt sich aus {reward_items} und {reward_currency_gold} Gold, {reward_currency_silver} Silber, {reward_currency_copper} Kupfer zusammen“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `reward.middle.mixed.003`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=claimable|accepted; reward_text_mode=mixed`
- `text_template: „Du bekommst {reward_items} plus {reward_currency_gold} Gold, {reward_currency_silver} Silber und {reward_currency_copper} Kupfer“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### reward_text_mode=failed

##### 1) `reward.middle.failed.001`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=failed; reward_text_mode=failed`
- `text_template: „Dafür steht diesmal nichts zur Ausgabe“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 2) `reward.middle.failed.002`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=failed; reward_text_mode=failed`
- `text_template: „Der Vorgang endet ohne regulären Ausgleich“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

##### 3) `reward.middle.failed.003`
- `text_category: reward`
- `text_part: middle`
- `filters: reward_state=failed; reward_text_mode=failed`
- `text_template: „Aus diesem Ergebnis entsteht kein Anspruch“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

### 4.3 reward.end

#### 1) `reward.end.generic.001`
- `text_category: reward`
- `text_part: end`
- `filters: reward_state=*; reward_text_mode=*`
- `text_template: „Mehr gibt es dazu nicht“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 2) `reward.end.generic.002`
- `text_category: reward`
- `text_part: end`
- `filters: reward_state=*; reward_text_mode=*`
- `text_template: „Damit ist dieser Teil beendet“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 3) `reward.end.generic.003`
- `text_category: reward`
- `text_part: end`
- `filters: reward_state=*; reward_text_mode=*`
- `text_template: „Wir sind damit durch“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

---

## 5. farewell

### 5.1 farewell.main

#### 1) `farewell.main.morning.001`
- `text_category: farewell`
- `text_part: main`
- `filters: time_of_day=morning; worldstress_zone=*; conversation_result=*`
- `text_template: „Dann geh für den Morgen deinen Weg“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 2) `farewell.main.day.001`
- `text_category: farewell`
- `text_part: main`
- `filters: time_of_day=day; worldstress_zone=*; conversation_result=*`
- `text_template: „Dann weiter für den Rest des Tages“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

#### 3) `farewell.main.evening.001`
- `text_category: farewell`
- `text_part: main`
- `filters: time_of_day=evening|night; worldstress_zone=*; conversation_result=*`
- `text_template: „Für diese Stunde reicht das“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: open`

### 5.2 farewell.middle

#### 1) `farewell.middle.accepted.001`
- `text_category: farewell`
- `text_part: middle`
- `filters: time_of_day=*; worldstress_zone=ruhig|belebt|hektisch; conversation_result=accepted`
- `text_template: „Du weißt jetzt, was anliegt“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### 2) `farewell.middle.no_offer.001`
- `text_category: farewell`
- `text_part: middle`
- `filters: time_of_day=*; worldstress_zone=ruhig|belebt|hektisch; conversation_result=no_offer|declined`
- `text_template: „Dann bleibt es vorerst bei Worten“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

#### 3) `farewell.middle.blocked.001`
- `text_category: farewell`
- `text_part: middle`
- `filters: time_of_day=*; worldstress_zone=ruhig|belebt|hektisch; conversation_result=inventory_full|cap_reached|blocked`
- `text_template: „Mehr lässt der aktuelle Zustand gerade nicht zu“`
- `needs_prefix_space: false`
- `needs_suffix_space: true`
- `punctuation_role: middle`

### 5.3 farewell.end

#### 1) `farewell.end.generic.001`
- `text_category: farewell`
- `text_part: end`
- `filters: time_of_day=*; worldstress_zone=*; conversation_result=*`
- `text_template: „Komm wieder, wenn sich etwas bewegt“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 2) `farewell.end.generic.002`
- `text_category: farewell`
- `text_part: end`
- `filters: time_of_day=*; worldstress_zone=*; conversation_result=*`
- `text_template: „Bis zum nächsten Schnittpunkt“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

#### 3) `farewell.end.generic.003`
- `text_category: farewell`
- `text_part: end`
- `filters: time_of_day=*; worldstress_zone=*; conversation_result=*`
- `text_template: „Dann endet das hier für jetzt“`
- `needs_prefix_space: false`
- `needs_suffix_space: false`
- `punctuation_role: close`

---

## Umfang

Gesamtzahl dieses Starter-Pools: **75 Snippets**
- `greeting`: 9
- `assignment`: 36
- `assignment_na`: 3
- `reward`: 18
- `farewell`: 9


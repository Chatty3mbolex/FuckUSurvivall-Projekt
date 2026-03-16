# TEXT_ZQS.md — Textsystem (PHASE_05)

> Zweck: Formale Definition des Snippet-basierten Textsystems, sodass Text-DB + Textassembler 1:1 implementierbar sind.
> Basis: Flow-4 Textdokumente + Master Alignment.
>
> Primärreferenzen:
> - `flow 4/zqs_textpool_szenarien_analyse1.md`
> - `flow 4/zqs_snippetkatalog_v1.md`
> - Snippet-DB: `assets/data/zqs/text_snippets_de_DE_v1.json`
>
> Harte Regeln:
> - Keine Ganztexte als DB-Entries.
> - Snippets werden gefiltert über Parameter.
> - Textengine berechnet keine Questwerte (Reward/Weltstress/Targets).
> - Assignment darf Reward **nur als Anzeige-Referenz** zeigen (Wert kommt aus RewardGenerator/Questdatensatz).

---

## 05.01 — Textbaustein-Kategorien

Es gibt genau diese Textfamilien (Kategorien):
1. `greeting`
2. `assignment`
3. `assignment_na`
4. `reward`
5. `farewell`

Jede Familie (außer `assignment_na`) besitzt genau drei Teile:
- `text_part = main | middle | end`

Zusammensetzung:
- `greeting = main -> middle -> end`
- `assignment = main -> middle -> end`
- `reward = main -> middle -> end`
- `farewell = main -> middle -> end`

`assignment_na` ist ein eigener N/A-Block (ein einzelnes Snippet, `text_part=na`).

---

## 05.02 — Kontextparameter (ConversationContext) definieren

### Objekt: `ConversationContext`
Pflichtfelder (Flow 4 Master):
- `time_of_day` (enum): `morning|day|evening|night`
- `worldstress_zone` (enum): `ruhig|belebt|hektisch`
- `debug_hq_mode` (enum): `INCLUDE_HQ|EXCLUDE_HQ`
- `conversation_result` (enum): `accepted|declined|no_offer|inventory_full|cap_reached|blocked`
- `nq_offer_count` (int)
- `open_quests_count` (int)
- `completed_quests_count` (int)

Zusatzfelder (aus Szenario-Template, falls verfügbar):
- `nq_generation_possible` (bool)
- `block_reason` (enum): `none|cap_reached|no_valid_targets|timer_not_due|N/A|rolled_zero`
- `stress_quotient_value` (float) (nur als Input; Berechnung außerhalb Textengine)

---

## 05.03 — Textauswahlregeln

### Allgemeine Auswahl
1. Filtere Snippets nach `text_category` und `text_part`.
2. Filtere nach Pflichtparametern (je Kategorie) gemäß Snippetkatalog.
3. Wenn mehrere Treffer:
   - (optional) Gewichtung (nicht zwingend im v1; nur wenn Feld existiert)
   - sonst uniform zufällig
4. Wenn keine Treffer:
   - **kein Fallback-System**.
   - Fehler ist ein Daten-/DB-Problem und muss sichtbar sein.

### Pflichtfilter je Kategorie

#### `greeting`
- `time_of_day`
- `worldstress_zone`
- `debug_hq_mode`
- `nq_generation_possible`
- `block_reason`
- `conversation_next_step` (`offer|no_offer|blocked`)

#### `assignment`
- `quest_type`
- `quest_subtype`

#### `assignment_na`
- `na_reason`
- `time_of_day` (optional, wenn DB es nutzt)
- `worldstress_zone` (optional, wenn DB es nutzt)

#### `reward`
- `reward_state`
- `reward_text_mode`

#### `farewell`
- `time_of_day`
- `worldstress_zone`
- `debug_hq_mode`
- `conversation_result`

---

## 05.04 — Textzusammensetzung (Assembler)

### Output-Artefakte
Bei jeder instanziierten Quest werden erzeugt:

1) `generated_text_ids`
- speichert pro Familie die verwendeten `snippet_id`s

2) `accepted_text`
- finaler sichtbarer Text bei Annahme

### Zusammensetzungsregel
- Text wird aus genau 3 Snippets pro Familie zusammengesetzt (main/middle/end), außer `assignment_na`.
- Anschlusslogik:
  - `main` darf nicht abschließen.
  - `middle` muss anschlussfähig sein.
  - `end` muss abschließen/überleiten.

### Sichtbarkeits-/Trennregeln
- `greeting` und `farewell` nennen **keine** konkreten Questziele/Mengen.
- `assignment.middle` ist der einzige Ort, wo konkrete Zielparameter stehen dürfen.
- `assignment` darf Reward nur als **Anzeige-Referenz** nennen (Platzhalter, Wert aus RewardBlock).
- `reward` nennt keine neue Questanforderung.

### Platzhalter-Füllung
- Platzhalter werden ausschließlich aus dem instanziierten Questdatensatz gefüllt:
  - `{target_quantity}`, `{target_name}`, `{target_region}`, `{target_entity}`
  - `{output_item_name}`, `{output_quantity}`
  - `{reward_currency_*}`, `{reward_items}`, `{reward_total_copper}`

Textengine darf:
- Platzhalter ersetzen
- Snippets auswählen

Textengine darf nicht:
- Targets wählen
- Reward berechnen
- Weltstress berechnen

---

## Kontrolle PHASE_05 (Definition of Done)

PHASE_05 ist abgeschlossen, wenn:
- Kategorien+Teile fix sind,
- Kontextparameter fix sind,
- Auswahlregeln fix sind,
- Assembler-Regeln fix sind,
- und daraus DB + Assembler ohne Nachfragen implementierbar sind.

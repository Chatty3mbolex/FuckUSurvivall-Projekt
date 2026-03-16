# ZQS Snippetkatalog v1 (Erstell-Regelwerk)

> Zweck: Dieses Dokument ist eine **Arbeitsanweisung zum Erstellen von Snippets** (Textbausteinen) für das ZQS.
> Es ist **kein** Quest-Formel-Dokument.
> Es ist **kein** Workflow-Dokument.
>
> Basisquellen:
> - Fachlogik: `zqs_wander_quest_guy_plan1.0.md`
> - Textlogik/Parameter: `zqs_textpool_szenarien_analyse1.md`
> - Dokumentkopplung: `zqs_master_alignment_v_1_1.md`

---

## 0) Grundprinzip (hart)

### 0.1 Keine Ganztexte
- Es dürfen **keine** fertigen Komplett-Texte erzeugt werden.
- Ein Snippet ist **immer nur ein Teil** eines später zusammengesetzten sichtbaren Textes.

### 0.2 Snippet-Familien (Kategorien)
Es gibt **genau** diese Kategorien:
1. `greeting` (Gruß)
2. `assignment` (Auftrag)
3. `assignment_na` (kein Auftrag / N/A)
4. `reward` (Belohnung)
5. `farewell` (Abschied)

**Keine Kategorie vergessen.**

### 0.3 Feste 3-Teil-Struktur (außer N/A)
Für jede Kategorie (außer `assignment_na`) gilt:
- `main -> middle -> end`

Sichtbarer Text wird immer so gebaut:
- `greeting = greeting.main + greeting.middle + greeting.end`
- `assignment = assignment.main + assignment.middle + assignment.end`
- `reward = reward.main + reward.middle + reward.end`
- `farewell = farewell.main + farewell.middle + farewell.end`

`assignment_na` ist ein **eigener** Fallback-Block (kein `main/middle/end` zwingend, sondern ein einzelnes N/A-Snippet).

### 0.4 Harte Funktions-Trennregeln
- **Kein** Snippet darf zugleich Gruß+Auftrag, Auftrag+Belohnung, Belohnung+neue Anforderung, Abschied+neuer Gruß sein.
- **Assignment**-Snippets dürfen **niemals** eine Belohnung nennen (auch nicht indirekt: „Dafür gibt’s gutes Geld“ ist schon zu nah).
- **Reward**-Snippets dürfen **niemals** neue Questanforderungen formulieren.
- **Farewell**-Snippets dürfen **niemals** wie ein neuer Gesprächsstart wirken.

### 0.5 Parameterbindung (hart)
- `time_of_day` darf **nur** `greeting` und `farewell` strukturieren.
- `worldstress_zone` darf nur dort wirken, wo er in den Texttemplates vorgesehen ist (Gruß/Abschied, ggf. Kontext-Kommentare) – nicht als versteckte Questregel.
- Quest-spezifische Objektparameter (z. B. `target_name`, `target_quantity`) dürfen **nur** in **`assignment.middle`** (und ggf. in `reward.middle`) erscheinen.
- Belohnungswerte kommen **immer** aus dem Questdatensatz/Quest-DB und werden **nur** referenziert (Platzhalter). Niemals im Snippet „ausgedacht“.

---

## 1) Snippet-Datenform (was jedes Snippet enthalten muss)

> Dieses Dokument ist textlich; die Snippets können später in JSON/YAML/DB gespeichert werden.
> Wichtig ist: **jedes Snippet muss diese Felder konzeptionell abdecken**.

### 1.1 Pflicht-Metadaten (identifizieren & einsortieren)
- `snippet_id`: eindeutig (z. B. `greeting.main.morning.001`)
- `text_category`: `greeting | assignment | assignment_na | reward | farewell`
- `text_part`: `main | middle | end | na`
- `speaker_entity`: i.d.R. `WQG` (WanderQuestGuy)
- `language`: z. B. `de_DE` (falls Mehrsprachigkeit geplant)

### 1.2 Pflicht-Filterparameter (damit Auswahl regelbasiert ist)
Snippets müssen so beschriftet sein, dass die Engine sie anhand der Parameter filtern kann.

- Für `greeting`/`farewell` typischerweise:
  - `time_of_day = morning | day | evening | night`
  - `worldstress_zone = ruhig | belebt | hektisch`
  - `debug_hq_mode = INCLUDE_HQ | EXCLUDE_HQ`
  - `conversation_result` (nur farewell) bzw. `conversation_next_step` (nur greeting)
  - `nq_generation_possible = true | false`
  - `block_reason = none | cap_reached | no_valid_targets | timer_not_due | N/A`

- Für `assignment`:
  - `quest_type = sammeln | liefern | craften | finden | eskortieren`
  - `quest_subtype` (siehe unten)

- Für `reward`:
  - `reward_state = claimable | accepted | failed`
  - `reward_text_mode = item | currency | mixed | failed`

### 1.3 Textkörper (Template statt „fertiger Inhalt“)
- `text_template`: ein Textfragment mit Platzhaltern.
- Platzhalter **dürfen** vorkommen, müssen aber zu den erlaubten Feldern passen, z. B.:
  - `{target_quantity}`, `{target_name}`, `{target_region}`, `{target_entity}`
  - `{output_item_name}`, `{output_quantity}`
  - `{reward_currency_gold}`, `{reward_items}`

### 1.4 Grammatik-/Anschlussregeln (Pflicht)
Jedes Snippet muss zusätzlich eine kurze interne Notiz erfüllen:
- `needs_prefix_space`: ob vor dem Snippet ein Leerzeichen benötigt wird
- `needs_suffix_space`: ob danach ein Leerzeichen benötigt wird
- `punctuation_role`: `open | middle | close` (soll Anschluss verhindern/erzwingen)

**Ziel:** main endet nicht wie ein Abschluss; end endet wie ein Abschluss.

---

## 2) Qualitätscheckliste (für jedes Snippet)

Bevor ein Snippet in den Pool darf, muss es diese Checks bestehen:

1. **Kategorie korrekt?** (kein Mischmasch)
2. **Teil korrekt?** (`main/middle/end/na`)
3. **Keine verbotenen Inhalte?**
   - Assignment nennt keine Belohnung
   - Reward nennt keine neue Aufgabe
   - Greeting/Farewell nennen keine konkreten Targets/Mengen
4. **Platzhalter korrekt & minimal?**
   - nur die Felder, die die Engine liefern kann
   - keine „fantasy“-Slots
5. **Anschlussfähigkeit:**
   - `main` wirkt wie Einstieg, nicht wie Schluss
   - `middle` funktioniert nach `main`
   - `end` schließt sauber ab oder leitet korrekt weiter
6. **Neutralität & Wiederverwendbarkeit:**
   - Snippet darf in vielen Situationen funktionieren (innerhalb seiner Filter)
7. **Länge:**
   - eher kurz. Ein Snippet ist ein Baustein, kein Monolog.

---

## 3) Kategorie-Anweisungen (exakt)

### 3.1 `greeting` (Gruß)

**Zweck:** Gesprächseröffnung. Setzt Ton und Lage. **Keine** Questdetails.

#### 3.1.1 Erlaubte Information im `greeting`
- Tageszeitliche Anrede/Eröffnung (nur hier!)
- Lagekommentar basierend auf `worldstress_zone`
- Meta-Hinweis, ob Angebote möglich sind (ohne Details)
- Blockade-/N/A-Hinweis **nur als Gesprächskontext**, nicht als Regel-Erklärung

#### 3.1.2 Verboten im `greeting`
- Keine Zielobjekte, keine Mengen, keine Regionsnamen, keine NPC-Namen (außer Sprecher)
- Keine Belohnungsnennung
- Kein „Nimm diese Quest…“ als fertiger Auftrag

#### 3.1.3 `greeting.main` – Anweisung
- Muss **immer** als erster Teil funktionieren.
- Muss `time_of_day` als primären Filter nutzen.
- Inhalt: Anrede + optional kurzer Tonfallanker.
- Darf **nicht** wie ein fertiger kompletter Gruß enden.

**Empfohlene Platzhalter:** keine.

#### 3.1.4 `greeting.middle` – Anweisung
- Muss Lage/Stress kommentieren (`worldstress_zone`).
- Darf optional Quest-Counts erwähnen, aber **ohne Zahlen** oder sehr allgemein (je nach Stil).
- Muss mit `nq_generation_possible` + `block_reason` kompatibel sein.

**Empfohlene Platzhalter:** keine (Counts nicht zwingend als Zahl).

#### 3.1.5 `greeting.end` – Anweisung
- Muss auf `conversation_next_step = offer | no_offer | blocked` ausgerichtet sein.
- Ziel: saubere Überleitung zum nächsten Systemschritt.
- Darf **keine** Questanforderung nennen.

---

### 3.2 `assignment` (Auftrag)

**Zweck:** Formuliert das Questangebot in 3 Teilen.

#### 3.2.1 Erlaubte Information im `assignment`
- `assignment.main`: Angebot einleiten (ohne Ziel)
- `assignment.middle`: **konkrete Anforderung** (hier dürfen Targets/Mengen rein)
- `assignment.end`: Rückkehr-/Abgabe-/Verbindlichkeitsrahmen (ohne Reward)

#### 3.2.2 Verboten im `assignment`
- **Keine Belohnung** erwähnen (auch nicht „lohnt sich“, „ich bezahle“)
- Keine Weltstress-/Tageszeit-Logik als Texttreiber (Assignment ist nicht tageszeitstrukturiert)

#### 3.2.3 Subtype-Norm (Pflicht)
Diese Subtypes sind zu verwenden:
- sammeln:
  - `sammeln.item`
  - `sammeln.harvestable`
- liefern:
  - `liefern.item`
- craften:
  - `craften.recipe_output`
  - `craften.delivery` (Warenlieferung)
- finden:
  - `finden.poi`
  - `finden.poi_loot`
  - `finden.person` (noch grob)
  - `finden.object`
- eskortieren:
  - `eskortieren.route`

#### 3.2.4 `assignment.main` – Anweisung
- Muss für **alle** Questtypen funktionieren.
- Darf keine questtypspezifischen Objekte nennen.
- Muss sprachlich eindeutig signalisieren: „Jetzt kommt der Auftrag“.

**Erlaubte Platzhalter:** keine.

#### 3.2.5 `assignment.middle` – Anweisung (questtypspezifisch)
Hier ist die **einzige** Stelle, wo die konkreten Questparameter textlich auftauchen dürfen.

##### a) `assignment.middle.collect_item` (sammeln.item)
- Muss `{target_quantity}` und `{target_name}` enthalten (oder semantisch klar ausdrücken).
- Optional: Qualitäts-/Vollständigkeitsmarker (ohne Belohnung).

##### b) `assignment.middle.collect_harvestable` (sammeln.harvestable)
- Muss `{target_quantity}` und `{target_name}` enthalten.
- Darf optional eine Quellenart andeuten (z. B. „aus der Wildnis“), aber **nicht** als exaktes Systemdetail.

##### c) `assignment.middle.deliver` (liefern.item)
- Muss `{target_quantity}`, `{target_name}` enthalten.
- Muss Empfänger/Ziel benennen: `{target_entity}` und/oder `{target_region}`.
- Pickup-Entity `{pickup_entity}` nur, wenn das System es wirklich hat.

##### d) `assignment.middle.craft` (craften.recipe_output)
- Muss `{output_quantity}` und `{output_item_name}` enthalten.
- Inputs `{input_items}` optional, wenn die Engine sie als String liefern kann.

##### e) `assignment.middle.craft_delivery` (craften.delivery)
- Muss Craft + Lieferkomponente enthalten:
  - `{output_quantity}`, `{output_item_name}`
  - `{target_entity}` und/oder `{target_region}`

##### f) `assignment.middle.find_poi` (finden.poi)
- Muss `{target_name}` (POI/Landmarke) enthalten.
- Region `{target_region}` optional.

##### g) `assignment.middle.find_poi_loot` (finden.poi_loot)
- Muss `{target_name}` enthalten.
- Muss Inhaltsreferenz über `{find_content_reference}` erlauben (ohne Loot zu spoilern, wenn nicht gewollt).

##### h) `assignment.middle.find_person` (finden.person)
- Darf vorerst generisch bleiben, aber muss als „Person finden“ erkennbar sein.
- Sobald Person-Parameter standardisiert sind, müssen Platzhalter ergänzt werden.

##### i) `assignment.middle.find_object` (finden.object)
- Muss `{target_quantity}` + `{target_name}` enthalten.
- Region `{target_region}` optional.

##### j) `assignment.middle.escort` (eskortieren.route)
- Muss `{escort_subject}`, `{escort_from}`, `{escort_to}` enthalten.
- Muss die harten Bedingungen als Textrahmen ausdrücken (Rückkehr, keine Toten), ohne Mechanik zu erklären.

#### 3.2.6 `assignment.end` – Anweisung
- Muss Abschluss/Verbindlichkeit setzen:
  - Rückkehrhinweis („Komm danach wieder“ / „Meld dich danach“) o. ä.
- Darf keine Belohnung nennen.

---

### 3.3 `assignment_na` (N/A – kein Auftrag)

**Zweck:** Ausgabe, dass **kein** Auftrag entsteht.

#### 3.3.1 Wann wird `assignment_na` benutzt?
- `na_reason = rolled_zero | cap_reached | no_valid_targets | no_known_targets | timer_reset`

#### 3.3.2 Harte Regeln
- Es darf **keinen** Logeintrag erzeugen.
- Es darf **keinen** DB-Eintrag erzeugen.
- Es ist **nur** Gesprächsausgabe.
- Inhalt: freundlich/neutraler Hinweis „heute nichts“ + optional kurzer Grund.

#### 3.3.3 Verboten
- Keine Questdetails (weil keine Quest existiert)
- Keine Belohnung
- Kein „mach X statt Y“ (keine neuen Aufgaben)

---

### 3.4 `reward` (Belohnung)

**Zweck:** Textausgabe bei Belohnungs-Claim / Quest-Auswertung.

#### 3.4.1 Erlaubte Information
- Ergebnisanker (anerkennend/neutral)
- Belohnungsnennung **aus DB-Werten** (Platzhalter)
- Bei `failed`: klare, kurze Fehl-Auswertung

#### 3.4.2 Verboten
- Keine neuen Questanforderungen
- Keine neuen Ziele
- Keine „nächstes Mal mach…“ als Auftrag (das gehört in assignment oder farewell, aber auch dort nur als Floskel, nicht als neue Quest)

#### 3.4.3 `reward.main` – Anweisung
- Einstieg in Übergabe/Auswertung.
- Muss abhängig von `reward_state` funktionieren.
- Keine exakten Zahlen erzwingen.

#### 3.4.4 `reward.middle` – Modi
Der Mittelteil hängt an `reward_text_mode`:
- `item`: nennt `{reward_items}` (Liste/String)
- `currency`: nennt `{reward_currency_*}`
- `mixed`: beides
- `failed`: ggf. `{final_reward_copper}` oder nur sinngemäß „nichts“

**Wichtig:** Der Snippettext darf Werte nennen, aber **nur über Platzhalter**.

#### 3.4.5 `reward.end` – Anweisung
- Schließt Belohnungsblock ab und leitet zu Gesprächsende über.

---

### 3.5 `farewell` (Abschied)

**Zweck:** Gespräch beenden, Ergebnis kommentieren.

#### 3.5.1 Erlaubte Information
- Tageszeitbezogener Abschiedsanker (nur hier!)
- Ergebnisbezug (`conversation_result = accepted | declined | no_offer | inventory_full | cap_reached | blocked`)
- kurzer Lage-/Stress-Kommentar (`worldstress_zone`)

#### 3.5.2 Verboten
- Keine konkreten Questdetails (Targets/Mengen)
- Keine Belohnung
- Kein neuer Gruß

#### 3.5.3 `farewell.main` – Anweisung
- Muss `time_of_day` als Filter nutzen.
- Muss klar als Abschied starten.

#### 3.5.4 `farewell.middle` – Anweisung
- Reaktion auf `conversation_result`.
- Optional Lagekommentar über `worldstress_zone`.

#### 3.5.5 `farewell.end` – Anweisung
- Finaler Abschluss.
- Optional Hinweis „komm wieder“ oder „bis dann“, passend zu Ergebnis.

---

## 4) Mindestumfang eines Pools (damit er nicht „leer läuft“)

> Ziel: Ein mittelmäßiges Modell soll genug Snippets produzieren, dass der Generator nicht ständig in Fallbacks fällt.

Empfehlung pro Sprache:

### greeting
- `main`: pro `time_of_day` mindestens **5**
- `middle`: pro `worldstress_zone` mindestens **5**
- `end`: pro `conversation_next_step` mindestens **5**

### assignment
- `main`: insgesamt mindestens **10** (questtyp-neutral)
- `middle`: pro Subtype mindestens **10**
- `end`: insgesamt mindestens **10**

### assignment_na
- pro `na_reason` mindestens **5**

### reward
- `main`: pro `reward_state` mindestens **5**
- `middle`: pro `reward_text_mode` mindestens **10**
- `end`: mindestens **5**

### farewell
- `main`: pro `time_of_day` mindestens **5**
- `middle`: pro `conversation_result` mindestens **5**
- `end`: mindestens **10**

---

## 5) Mini-Beispiele (nur Struktur, keine finalen Ingame-Texte)

> Diese Beispiele sind **absichtlich generisch** und dienen nur dazu, den Platzhalter-Stil zu zeigen.
> Sie sind **nicht** als finale Texte zu verwenden.

- `assignment.middle.collect_item` Beispielstruktur:
  - „Besorg mir {target_quantity}× {target_name} …“

- `assignment.middle.deliver` Beispielstruktur:
  - „Bring {target_quantity}× {target_name} zu {target_entity} in {target_region} …“

- `reward.middle.currency` Beispielstruktur:
  - „Du bekommst {reward_currency_silver} Silber und {reward_currency_copper} Kupfer …“

---

## 6) Abschlussregel

Ein Snippet ist nur gültig, wenn es:
- **genau eine** Kategorie-Funktion erfüllt,
- im 3-Teil-System korrekt sitzt,
- keine verbotenen Inhalte trägt,
- und anhand der vorgesehenen Parameter eindeutig auswählbar ist.

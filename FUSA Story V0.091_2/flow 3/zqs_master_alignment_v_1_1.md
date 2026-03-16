# ZQS Master Alignment v1.1

## Zweck

Dieses Dokument verbindet die drei anderen ZQS-Dateien zu einer einzigen kanonischen Leselogik.

1. `WanderQuestGuy_ZQS_WorkflowPlan.md`
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_textpool_szenarien_analyse1.md`

Ziel ist kein neuer Scope.
Ziel ist nur die eindeutige Rollenverteilung und die saubere Kopplung der vorhandenen Logik.

## Kanonische Lesereihenfolge

1. dieses Dokument
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_textpool_szenarien_analyse1.md`
4. `WanderQuestGuy_ZQS_WorkflowPlan.md`

## Dokument-Rollen

### 1. `zqs_wander_quest_guy_plan1.0.md`
Dieses Dokument ist die fachliche Hauptspezifikation.
Dort stehen:
- Questregeln
- Formeln
- Status
- Persistenzlogik
- Storylogik
- Rewardlogik
- Logbuchlogik

### 2. `zqs_textpool_szenarien_analyse1.md`
Dieses Dokument ist die Text- und Inhaltsanbindung.
Dort stehen:
- Textfamilien
- Snippet-Regeln
- Parameterfelder
- Zielklassen
- Inhaltslisten
- Textdatenbasis

### 3. `WanderQuestGuy_ZQS_WorkflowPlan.md`
Dieses Dokument ist reine Arbeitssteuerung.
Dort stehen:
- Verbote
- Resume-Logik
- Statusdateien
- Phasen
- Kontrollpunkte
- Umsetzungsreihenfolge

## Priorität bei Konflikten

1. Workflow- und Kontrollfragen -> `WanderQuestGuy_ZQS_WorkflowPlan.md`
2. Questlogik, Persistenz, Formeln, Status, Story -> `zqs_wander_quest_guy_plan1.0.md`
3. Textparameter, Inhaltsklassen, Snippet-Struktur -> `zqs_textpool_szenarien_analyse1.md`

Die Textpool-Datei darf keine fachliche Questregel überschreiben.
Der Workflow-Plan darf keine fachliche Questregel neu definieren.

## Gemeinsames Gesamtbild

Das System besteht logisch aus sechs Hauptblöcken:

1. Spielinhalt-Kataloge
2. Spielerwissen / Known-State
3. Questgenerator
4. Questpersistenz
5. Textgenerator
6. NPC-Docking

Der `WanderQuestGuy` ist nur Block 6.
Er besitzt keine Questlogik, keine Rewardlogik und keine Textlogik.
Er fragt nur beim ZQS an, liest Angebote aus und übermittelt sie.

## Verbindende Normregeln

### 1. Questarten

Freigegebene Questarten sind nur:
- sammeln
- liefern
- craften
  - Subtyp: `craften.delivery`
- finden
- eskortieren

Tiere, Gegner, Regionen, NPCs, POIs und Objekte sind keine eigenen Questarten.
Sie sind nur Zielklassen innerhalb freigegebener Questarten.

### 2. Warenlieferung

`Warenlieferung` ist normiert als Craft-Liefer-Untertyp:
- `quest_type = craften`
- `quest_subtype = craften.delivery`

### 3. Questziel-Pool

`Questziel-Pool = Alle` bedeutet:
alle Ziele, die durch Questart, SL_ID, Storyline-Phase und Known-Flag-Regeln zulässig sind.

Damit ist `Alle` kein freier Asset-Pool ohne Filter.

### 4. Known-State

Known-State ist ein eigener Speicher- und Prüfblock.
Er wird nicht aus UI oder Zufall rekonstruiert.

Mindestens betroffen sind:
- Items
- Regionen
- Harvestables
- Livings
- POIs
- NPCs

Die Textengine bekommt Known-State nur als Eingabe.
Sie erzeugt ihn nie selbst.

### 5. Runtime- und Asset-Ebene

Es gibt zwei Ebenen:

#### `ContentCatalogAsset`
Alles, was in den Inhaltsquellen des Spiels definiert ist.

#### `ContentCatalogRuntime`
Alles, was für echte Questgenerierung freigegeben ist.

Der Generator arbeitet nur auf `ContentCatalogRuntime`.
Die Textengine darf für Formulierung und Aliasbildung zusätzlich vorbereitete Klasseninfos aus `ContentCatalogAsset` nutzen.

### 6. Persistenztrennung

Nicht angenommene NQ werden verworfen.
Sie werden nicht in die DB geschrieben.
N/A wird nicht persistiert.

Für die Kopplung der Dokumente gilt deshalb diese feste Trennung:

#### `OfferBuffer`
Flüchtige aktuell generierte Angebote.
Nicht persistent.

#### `PlayerQuestDB`
Persistente Questdatenbank pro Spieler.
Nur für angenommene Quests.

#### `QuestHistoryIndex`
Leichte History-Referenz für bereits angenommene Quests und ihre Endzustände.

Nicht angenommene NQ bleiben nur im `OfferBuffer`.
Beim Verwerfen werden sie vollständig gelöscht.
Sie dürfen später erneut generiert werden.

### 7. Queststatus

NQ und HQ haben getrennte Statussysteme.

#### NQ
- aktiv
- erledigt
- abgabebereit
- fehlgeschlagen

#### HQ
- generiert
- aktiv
- abgabebereit
- erledigt

`angeboten` ist kein Queststatus.
`fehlgeschlagen` ist der kanonische Fehlschlagstatus.
`abgabebereit` zählt für die UI zum Bereich `erledigt`.
`fehlgeschlagen` bleibt eigener Endstatus und liegt in der History als bereits vorhandene Quest.

### 8. Storyzustand

Storyzustand wird aus zwei getrennten Variablen gebildet:

#### `SL_ID / StoryLinePhase`
Eigene einstellige Phasenvariable im Playersave.

#### `Storytiefe`
Separater Fortschrittswert innerhalb der aktuellen Phase:
- `0.00`
- `0.25`
- `0.50`
- `0.75`

Die QuestID trägt die Phase mit, nicht die Storytiefe.
Wenn die Storytiefe über `0.75` hinaus erhöht würde:
- `SL_ID + 1`
- Storytiefe reset

### 9. HQ-Generierung

HQs laufen über denselben Generatorkern wie NQ, aber mit eigener Regelmenge.

Pflichtpunkte:
- nur Trigger erzeugen HQ
- bei Spielstart wird `HQTrigger = 1` getriggert
- `active_hq_count == 0` ist Pflicht
- beim WanderQuestGuy gilt: nur bei Trigger oder bei Trigger und Zeit
- HQ können auch außerhalb des WanderQuestGuy generiert oder vergeben werden

### 10. Weltstress

Die Grundlogik lautet:
- `Weltstress = Persönliche Stress Stufe + Stressfaktor`
- `Persönlicher Stress = offene Quests + Storytiefe`
- `Stressfaktor = Storytiefe + StorylinePhase`

Die daraus abgeleiteten UI-Zonen, Repräsentationswerte und If-Formeln stehen vollständig in `zqs_wander_quest_guy_plan1.0.md`.

### 11. Textengine

Die Textengine arbeitet nur auf bereits instanzierten Questdaten plus Gesprächskontext.
Sie berechnet nicht selbst:
- Weltstress
- Rewardhöhe
- Queststatus
- Questzeit
- Questziele

Sie baut nur Texte aus Snippets zusammen.

### 12. NPC-Docking

NPC-Docking ist reine Vermittlungsschicht.

Der `WanderQuestGuy` darf:
- Gespräch öffnen
- Generator anfragen
- Angebote anzeigen
- Annahme auslösen
- Reward-Claim auslösen
- Textpakete anzeigen

Er darf nicht:
- Questregeln berechnen
- Known-State setzen
- Rewardhöhe berechnen
- QuestIDs entwerfen
- Weltstress berechnen
- Textbausteine selbst auswählen

## Gemeinsame Kernobjekte

Diese Objekte sind die sprachliche Brücke zwischen den Dateien.

### `PlayerStoryState`
Pflichtfelder:
- `sl_id`
- `story_depth`
- `hq_trigger`
- `active_hq_count`
- `open_nq_count`
- `completed_nq_count`

### `PlayerKnowledgeState`
Pflichtfelder:
- `known_items`
- `known_regions`
- `known_harvestables`
- `known_livings`
- `known_pois`
- `known_npcs`

### `GeneratedQuestOffer`
Pflichtfelder:
- `quest_id`
- `quest_family`
- `quest_type`
- `quest_subtype`
- `target_block`
- `reward_block`
- `expected_time_sec`
- `text_profile_ids`
- `status`
- `source_npc_id`

Nicht persistent, solange nicht akzeptiert.

### `PersistentQuestRecord`
Pflichtfelder:
- alles aus `GeneratedQuestOffer`
- `accepted_at`
- `completed_at`
- `failed_at`
- `logbook_entry_nr`
- `accepted_location`
- `completed_location`
- `giver_npc_id`
- `final_status`

### `ConversationContext`
Pflichtfelder:
- `time_of_day`
- `worldstress_zone`
- `debug_hq_mode`
- `conversation_result`
- `nq_offer_count`
- `open_quests_count`
- `completed_quests_count`

## Festes Zusammenspiel der Kernobjekte

### Für NQ-Angebote
Diese Blöcke müssen gemeinsam berücksichtigt werden.
Dieses Dokument definiert hier kein zweites alternatives Reihensystem zur Fachlogik.

Pflichtblöcke im Zusammenspiel:
- Workflowzustand lesen
- `PlayerStoryState` lesen
- `PlayerKnowledgeState` lesen
- aktive, offene und erledigte Quests lesen
- NQ-Timer prüfen
- offene Questgrenze prüfen
- valides Questziel im `ContentCatalogRuntime` suchen
- passenden Blueprint wählen
- Quest instanzieren
- Reward berechnen
- Weltstress berechnen
- Textparameter erzeugen
- Texte nach der Textpool-Logik zusammensetzen
- Quest als `GeneratedQuestOffer` in `OfferBuffer` legen
- NPC zeigt Angebot an
- bei Annahme: in `PersistentQuestRecord` überführen

### Für HQ-Angebote
Diese Blöcke müssen gemeinsam berücksichtigt werden:
- `PlayerStoryState` lesen
- prüfen `active_hq_count == 0`
- prüfen `hq_trigger == true`
- HQ-Blueprint wählen
- dieselbe Instanzierungslogik anwenden

## Platzhalter und bewusst offene Punkte

Diese Punkte bleiben absichtlich offen und werden nicht neu interpretiert:
- `HQTrigger-Auslöser` ist zurzeit nicht Teil dieses Konzepts
- `HQ-Regeln` sind als Platzhalter markiert und nicht weiter auszuarbeiten
- `Escort` verweist auf die Regelquelle `Escort_Rules`
- `Zielpool-Details` sind nicht separat ausgearbeitet
- `Weltstress-If-Formel` bleibt als fachliche Formel in Plan 1.0 verankert

## Operativer Kernsatz

Plan 1.0 enthält die fachliche Wahrheit.
Die Textpool-Datei liefert die Text- und Inhaltsparameter.
Der Workflow-Plan steuert nur die Arbeit.
Dieses Dokument sorgt nur dafür, dass alle drei Dateien dieselbe Sprache sprechen.

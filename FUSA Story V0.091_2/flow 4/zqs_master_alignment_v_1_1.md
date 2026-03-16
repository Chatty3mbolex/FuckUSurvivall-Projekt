# ZQS Master Alignment v1.1

## Zweck

Dieses Dokument ordnet die gesamte ZQS-Dokumentkonstellation.

Es definiert:
- welche Datei welche Rolle hat
- in welcher Reihenfolge gelesen werden muss
- welche Datei in welchem Bereich die primäre Referenz ist
- wie die Dokumente ohne Logikänderung zusammenarbeiten

Diese Konstellation besteht aus:

1. `zqs_master_alignment_v_1_1.md`
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_reward_logik_referenz_v1.md`
4. `zqs_quest_inhaltslogik_referenz_v1.md`
5. `zqs_textpool_szenarien_analyse1.md`
6. `zqs_snippetkatalog_v1.md`
7. `zqs_textpool_starter_v1.md`
8. `WanderQuestGuy_ZQS_WorkflowPlan.md`

## Kanonische Lesereihenfolge

1. dieses Dokument
2. `zqs_wander_quest_guy_plan1.0.md`
3. `zqs_reward_logik_referenz_v1.md`
4. `zqs_quest_inhaltslogik_referenz_v1.md`
5. `zqs_textpool_szenarien_analyse1.md`
6. `zqs_snippetkatalog_v1.md`
7. `zqs_textpool_starter_v1.md`
8. `WanderQuestGuy_ZQS_WorkflowPlan.md`

## Dokument-Rollen

### 1. `zqs_wander_quest_guy_plan1.0.md`
Fachlicher Ursprungsplan und übergreifende ZQS-Gesamtlogik.

### 2. `zqs_reward_logik_referenz_v1.md`
Gebündelte Reward-Referenz.
Diese Datei ist innerhalb der Konstellation die primäre Referenz für Rewardlogik.

### 3. `zqs_quest_inhaltslogik_referenz_v1.md`
Gebündelte Inhalts-Referenz.
Diese Datei ist innerhalb der Konstellation die primäre Referenz für möglichen Questinhalt.

### 4. `zqs_textpool_szenarien_analyse1.md`
Text- und Inhaltsanbindung, Szenariofelder, Zielklassen, Inhaltsquellen.

### 5. `zqs_snippetkatalog_v1.md`
Primäre Referenz für Snippet-Aufbau, Kategorien, Pflichtfelder und Anschlussregeln.

### 6. `zqs_textpool_starter_v1.md`
Starter-Datenbestand konkreter Snippets.
Keine primäre Logikquelle.

### 7. `WanderQuestGuy_ZQS_WorkflowPlan.md`
Reine Arbeitssteuerung, Resume-Regeln, Phasen und Kontrollpunkte.

## Primärreferenzen nach Bereich

- Arbeitssteuerung -> `WanderQuestGuy_ZQS_WorkflowPlan.md`
- Übergreifende Questlogik / Status / Persistenz / Story / IDs -> `zqs_wander_quest_guy_plan1.0.md`
- Rewardlogik -> `zqs_reward_logik_referenz_v1.md`
- Möglicher Questinhalt -> `zqs_quest_inhaltslogik_referenz_v1.md`
- Text- und Inhaltsanbindung -> `zqs_textpool_szenarien_analyse1.md`
- Snippet-Schema -> `zqs_snippetkatalog_v1.md`
- Starter-Snippetbestand -> `zqs_textpool_starter_v1.md` als Datenbestand, nicht als Logikquelle

Die beiden neuen Referenzdateien werden in dieser Konstellation als primäre Bereichsreferenzen anerkannt, weil sie die in den übrigen Dokumenten vorhandene Logik ihres Bereichs vollständig sammeln.
Punkte, die in den Quell-Dokumenten offen bleiben, bleiben auch dort offen.
Dadurch wird keine neue Logik erfunden.

## Priorität bei Konflikten

1. Workflow- und Kontrollfragen -> `WanderQuestGuy_ZQS_WorkflowPlan.md`
2. Übergreifende Questlogik, Persistenz, Formeln, Status, Story, IDs -> `zqs_wander_quest_guy_plan1.0.md`
3. Rewardlogik -> `zqs_reward_logik_referenz_v1.md`
4. Möglicher Questinhalt -> `zqs_quest_inhaltslogik_referenz_v1.md`
5. Textparameter, Inhaltsanbindung, Zielklassen, Szenariofelder -> `zqs_textpool_szenarien_analyse1.md`
6. Snippet-Struktur und Pflichtfelder -> `zqs_snippetkatalog_v1.md`
7. Konkrete Starter-Snippets -> `zqs_textpool_starter_v1.md`

Die Textpool-Datei darf keine fachliche Questregel überschreiben.
Der Workflow-Plan darf keine fachliche Questregel neu definieren.
Der Starter-Textpool darf keine Logikquelle überschreiben.

## Master-Regel (bindend)

Wenn Aussagen zwischen Dokumenten kollidieren, ist **dieses Master-Alignment die bindende Konfliktauflösung**.

- Dieses Dokument darf Logik nicht neu erfinden, aber es darf (und muss) **Normierungen und Trennlinien** setzen, damit die Konstellation widerspruchsfrei zusammenarbeitet.
- Eine Normierung in diesem Dokument ist innerhalb ihres Bereichs **verbindlich**, auch wenn ältere Formulierungen in anderen Dokumenten noch unpräzise sind.

## 2. Gemeinsames Gesamtbild

Das System besteht logisch aus **6 Hauptblöcken**:

1. **Spielinhalt-Kataloge**
2. **Spielerwissen / Known-State**
3. **Questgenerator**
4. **Questpersistenz**
5. **Textgenerator**
6. **NPC-Docking**

Der `WanderQuestGuy` ist nur Block 6.
Er besitzt keine Questlogik, keine Rewardlogik und keine Textlogik.
Er fragt nur beim ZQS an, liest Angebote aus und übermittelt sie.

---

## 3. Exakte Kopplung der drei Dokumente

## 3.1 Block 1 – Spielinhalt-Kataloge

### Quelle
- Inhaltlich aus `zqs_textpool_szenarien_analyse.md`
- Architektonisch aus `WanderQuestGuy_ZQS_WorkflowPlan.md`

### Normierte Regel
Das ZQS führt **zwei getrennte Katalogebenen**:

#### A. `ContentCatalogAsset`
Enthält alles, was in den Datenquellen des Spiels definiert ist.
Zweck:
- Textpools
- Zukunftsplanung
- später freischaltbare Ziele

#### B. `ContentCatalogRuntime`
Enthält nur das, was für das aktuelle Spielsystem **als generatorfähig freigegeben** ist.
Zweck:
- echte Zielauswahl für Quests
- echte Rewardberechnung
- echte Validierung

### Grund
Die Textpool-Datei enthält breitere Inhaltsmengen als die ursprüngliche harte Runtime-Analyse. Deshalb dürfen Asset-Inhalte und generatorfähige Runtime-Ziele **nicht** vermischt werden.

### Pflichtregel
Der Generator arbeitet **nur** auf `ContentCatalogRuntime`.
Die Textengine darf auf `ContentCatalogRuntime` und auf sprachlich vorbereitete Alias-/Klasseninfos aus `ContentCatalogAsset` zugreifen.

---

## 3.2 Block 2 – Known-State

### Quelle
- Fachlich aus `zqs_wander_quest_guy_plan1.0.md`
- strukturell aus `WanderQuestGuy_ZQS_WorkflowPlan.md`
- textseitig aus `zqs_textpool_szenarien_analyse.md`

### Normierte Regel
Der Known-State wird als **eigener Speicherblock** behandelt und nicht aus Zufall oder indirekt aus UI rekonstruiert.

Er besteht mindestens aus:
- `known_items`
- `known_regions`
- `known_harvestables`
- `known_livings`
- `known_pois`
- `known_npcs`

### Pflichtregel
Der Generator darf keine Quest erzeugen, deren Ziel einen Known-Flag verlangt, wenn dieser Known-Flag im Save nicht gesetzt ist.

### Textkopplung
Die Textengine bekommt den Known-State **nur als Eingabeparameter**, erzeugt ihn aber nie selbst.

---

## 3.3 Block 3 – Questgenerator

### Quelle
- Fachlich aus `zqs_wander_quest_guy_plan1.0.md`
- Arbeitsreihenfolge aus `WanderQuestGuy_ZQS_WorkflowPlan.md`

### Normierte Regel
Der Generator besteht aus **4 internen Stufen**:

1. **Eligibility**
2. **Blueprint-Auswahl**
3. **Questinstanzierung**
4. **Text-/Reward-Verknüpfung**

### 3.3.1 Eligibility
Vor jeder Generierung muss geprüft werden:
- Questfamilie (`NQ` / `HQ`)
- aktuelle `SL_ID`
- aktuelle `Storytiefe`
- Spielerlevel
- Known-State
- offene Quests
- aktive HQ ja/nein
- HQTrigger ja/nein
- Timerstatus für NQ
- Verfügbarkeit valider Ziele im `ContentCatalogRuntime`

### 3.3.2 Blueprint-Auswahl
Blueprints dürfen nur aus Questarten stammen, die im Plan 1.0 freigegeben sind:
- sammeln
- liefern
- craften
- Warenlieferung
- finden
- eskortieren

### Pflichtregel
Tiere, Gegner, Regionen, NPCs, POIs und Objekte aus der Textpool-Datei sind **noch keine Questarten**.
Sie sind nur **mögliche Zielklassen innerhalb freigegebener Questarten**, falls eine Regel sie bindet.

Beispiel:
- `finden.person` ist erlaubt, wenn Questart `finden` gewählt wurde.
- `enemy` ist **keine eigene Questart**, solange sie nicht separat im Fachplan freigegeben wurde.

### 3.3.3 Questinstanzierung
Die Questinstanz erzeugt:
- QuestID
- Questtyp
- Zielblock
- Rewardblock
- Textprofil-Referenzen
- Zeitvorgabe
- Queststatus initial
- Questlogbuchdaten

### 3.3.4 Text-/Reward-Verknüpfung
Die Textengine bekommt **nur bereits instanzierte Questdaten**.
Sie erfindet keine Ziele und keine Belohnungen.

---

## 3.4 Block 4 – Questpersistenz

### Quelle
- Fachlich aus `zqs_wander_quest_guy_plan1.0.md`
- strukturell aus `WanderQuestGuy_ZQS_WorkflowPlan.md`

### Harte Konfliktstelle
Im Plan 1.0 stehen zwei Aussagen, die ohne Trennung kollidieren:

1. Alle generierten Quests sollen in einer Quest-DB pro Spieler aufgezeichnet werden.
2. Nicht angenommene NQ sollen verworfen und nicht in der DB gespeichert werden.

### Normierte Auflösung
Es gibt **nicht eine**, sondern **drei** getrennte Speicherbereiche:

#### A. `OfferBuffer`
Flüchtige aktuell generierte Angebote.
Enthält:
- noch nicht angenommene NQ/HQ-Angebote
- Timer-/Slotbezug
- keine dauerhafte Quest-Historie
- keine Sperrwirkung für spätere Neugenerierung

#### B. `PlayerQuestDB`
Persistente Questdatenbank pro Spieler.
Enthält nur Quests, die mindestens einen persistenten Status erreicht haben:
- angenommen
- aktiv
- abgabebereit
- erledigt
- fehlgeschlagen
- HQ generiert/angeboten, **nur wenn der Fachplan HQ-Angebote explizit persistent verlangt**

#### C. `QuestHistoryIndex`
Leichter Referenzindex für:
- gehabt
- offen
- erledigt
- fehlgeschlagen

Nicht enthalten:
- bloß gesehen
- bloß angeboten
- abgelehnt
- verworfen ohne Annahme

### Pflichtregel
- **N/A** wird in keinen persistenten Bereich geschrieben.
- **Nicht angenommene NQ** bleiben nur im `OfferBuffer` und werden beim Verwerfen vollständig gelöscht.
- **Nicht angenommene NQ** dürfen später erneut generiert werden, auch in derselben inhaltlichen Kombination, sofern sie durch die aktuellen Eligibility-Regeln wieder gültig sind.
- **Nicht angenommene NQ** erzeugen **keinen** permanenten „gehabt“-Eintrag und **keine** Unique-Sperre.
- **Angenommene NQ** wandern beim Accept von `OfferBuffer` nach `PlayerQuestDB`.

Optional zulässig ist nur ein **flüchtiger Anti-Duplikat-Schutz innerhalb desselben Offer-Zyklus oder Refresh-Laufs**. Dieser Schutz ist rein laufzeitbezogen und darf nach dem Verwerfen nicht persistent gespeichert werden.

Damit sind beide Aussagen sauber kompatibel: Ein Angebot kann verworfen werden und dennoch später wieder auftauchen, weil nur angenommene bzw. persistent gewordene Quests in die dauerhafte Historie eingehen.

---

## 3.5 Block 5 – Textgenerator

### Quelle
- Fachlogik aus `zqs_wander_quest_guy_plan1.0.md`
- Parameter- und Kategoriemodell aus `zqs_textpool_szenarien_analyse.md`

### Normierte Regel
Die Textengine besteht aus **4 getrennten Textfamilien**:
- `greeting`
- `assignment`
- `reward`
- `farewell`

Jede Familie arbeitet mit:
- `text_part = main | middle | end`
- Parameterfiltern
- Zufall innerhalb des gültigen Pools

### Pflichtregel
Die Textengine arbeitet **niemals** direkt auf rohen Spielobjekten.
Sie arbeitet auf einer **normalisierten Questinstanz** plus Gesprächskontext.

### Eingaben Gruß/Abschied
- Tageszeitfenster
- Weltstress-Zone
- offene Quests
- erledigte Quests
- Debug-Schalter HQ ein/aus
- Gesprächsergebnis

### Eingaben Auftrag
- Questtyp
- Questsubtyp
- Zielklasse
- Zielname
- Anzahl
- Zeitvorgabe
- Known-Status
- SL_ID
- Storytiefe
- Reward-Formel-Typ

### Eingaben Belohnung
- QuestID
- Rewardblock
- Rewardmodus
- Erfolgs-/Fehlstatus
- Zeitbezug
- Giver/Ort

### Pflichtregel
Die Textengine baut nur Texte zusammen.
Sie berechnet nicht:
- Weltstress
- Rewardhöhe
- Queststatus
- Questzeit

Diese Werte müssen vorher feststehen.

---

## 3.6 Block 6 – NPC-Docking

### Quelle
- alle drei Dokumente gemeinsam

### Normierte Regel
NPC-Docking ist eine reine Vermittlungsschicht.

Der `WanderQuestGuy` darf nur:
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

---

## 4. Gemeinsame Kernobjekte, damit alle drei Dokumente dieselbe Sprache sprechen

## 4.1 `PlayerStoryState`
Verbindet Plan 1.0 und WorkflowPlan.

Pflichtfelder:
- `sl_id`
- `story_depth`
- `hq_trigger`
- `active_hq_count`
- `open_nq_count`
- `completed_nq_count`

## 4.2 `PlayerKnowledgeState`
Verbindet Plan 1.0 und Textpool-Datei.

Pflichtfelder:
- `known_items`
- `known_regions`
- `known_harvestables`
- `known_livings`
- `known_pois`
- `known_npcs`

## 4.3 `GeneratedQuestOffer`
Verbindet Generator, Text und NPC-Docking.

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

### Statusbereich Offer
Erlaubt:
- `generated`
- `offered`

Nicht persistent, solange nicht akzeptiert.

## 4.4 `PersistentQuestRecord`
Verbindet Plan 1.0, Questlog und Save/DB.

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

## 4.5 `ConversationContext`
Verbindet Textgenerator und NPC.

Pflichtfelder:
- `time_of_day`
- `worldstress_zone`
- `debug_hq_mode`
- `conversation_result`
- `nq_offer_count`
- `open_quests_count`
- `completed_quests_count`

---

## 5. Normierte Entscheidungsreihenfolge

Damit alle drei Dokumente nicht gegeneinander arbeiten, gilt diese feste Reihenfolge:

### Für NQ-Angebote
1. `ERRORS.md` / Workflowzustand lesen
2. PlayerStoryState lesen
3. PlayerKnowledgeState lesen
4. aktive/offene/erledigte Quests lesen
5. NQ-Timer prüfen
6. offene Questgrenze prüfen
7. valides Questziel im `ContentCatalogRuntime` suchen
8. Blueprint wählen
9. Quest instanzieren
10. Reward berechnen
11. Weltstress berechnen
12. Textparameter erzeugen
13. Texte zusammensetzen
14. Quest als `GeneratedQuestOffer` in `OfferBuffer` legen
15. NPC zeigt Angebot an
16. bei Annahme: in `PersistentQuestRecord` überführen

### Für HQ-Angebote
1. PlayerStoryState lesen
2. prüfen `active_hq_count == 0`
3. prüfen `hq_trigger == true`
4. danach erst HQ-Blueprint wählen
5. danach identische Instanzierungslogik

---

## 6. Harte Klärungen offener Konflikte

## 6.1 Questziel-Pool „Alle"
Der Satz „Questziel-Pool = Alle“ wird normiert zu:

- „Alle“ bedeutet **alle freigegebenen Ziele des `ContentCatalogRuntime`, die durch Questtyp, Known-State, SL_ID und Regelmodell gültig sind**.
- „Alle“ bedeutet **nicht** blind alle Asseteinträge.

## 6.2 Tiere und Gegner in Textpool-Datei
Sie bleiben erhalten, aber normiert als:
- `living content classes`
- nicht automatisch aktive Questfamilien
- nur nutzbar, wenn eine erlaubte Questart sie referenziert

## 6.3 Questlogbuchnummer
Die `Questlogbucheintrag Nr.` ist nicht bloß Array-Index, sondern wird normiert als eigener persistenter Zähler:
- `logbook_entry_nr`
- Vergabe nur bei Persistenzübergang in `PersistentQuestRecord`

## 6.4 Rewardtexte vs. Rewardberechnung
Rewardhöhe wird vollständig vor dem Textbau berechnet.
Der Rewardtext liest nur den fertigen Rewardblock.

## 6.5 N/A-Zustände
N/A ist kein Questdatensatz.
N/A erzeugt nur:
- Antworttext
- Timerreset falls Regel so sagt
- keinen DB-Eintrag
- keinen Logbucheintrag

---

## 7. Was der Agent später daraus ableiten MUSS

Ein mittelklassiger KI-Agent muss aus dieser Master-Struktur exakt verstehen:

1. **wo** gearbeitet wird
2. **welches Dokument** welche Autorität hat
3. **welche Daten zuerst** erzeugt werden müssen
4. **welche Daten nur Textparameter** sind
5. **welche Daten persistent** sind
6. **wann** eine NQ nur Angebot ist und wann sie echte Quest wird
7. **dass der WanderQuestGuy nicht das System ist**

---

## 8. Nächste präzise Verfeinerungspunkte

Die drei Dokumente verstehen sich jetzt logisch. Der nächste notwendige Feinschnitt ist:

1. exakte Feldlisten für:
   - `ContentCatalogRuntime`
   - `PlayerKnowledgeState`
   - `GeneratedQuestOffer`
   - `PersistentQuestRecord`
   - `ConversationContext`
2. exakte Statusmaschine für:
   - NQ
   - HQ
3. exakte Übergangsregeln zwischen:
   - `OfferBuffer`

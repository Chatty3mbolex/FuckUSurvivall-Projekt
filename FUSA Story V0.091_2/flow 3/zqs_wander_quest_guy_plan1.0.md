# WanderQuestGuy / ZQS – Fachlogik Plan 1.0

## Zweck

Dieses Dokument ist die kanonische Fachlogik des ZQS.
Es beschreibt nicht den Workflow, sondern die inhaltlichen Regeln des Systems.

## Geltungsbereich

- Das System unterscheidet zwischen **Hauptquest (HQ)** und **Nebenquest (NQ)**.
- Beide Questtypen laufen über denselben Generatorkern.
- Der Unterschied liegt in Text-Präfix, Auswahlregeln, möglicher Gewichtung und späterer Darstellung.
- Aktueller Arbeitsmodus: Zurzeit werden ausschließlich Texte und Teiltexte für **Nebenquests** ausgearbeitet.
- Alles, was im aktuellen Textausbau behandelt wird, ist direkt als **NQ** markiert.
- HQ werden strukturell vorbereitet, aber textlich noch nicht befüllt.

## Grunddefinitionen

- **Weltstress** = **Persönliche Stress Stufe + Stressfaktor**
- **Persönlicher Stress** = **offene Quests + Storytiefe**
- **Stressfaktor** = **Storytiefe + StorylinePhase**
- **StoryLinePhase / SL_ID** ist eine eigene Variable im Playersave.
- **Storytiefe** ist ein separater Wert.
- Die **QuestID** trägt die aktuelle Phase mit, bestimmt aber nicht die Storytiefe.

## Weltstress

### Grundformel

- **Weltstress** = **Persönliche Stress Stufe + Stressfaktor**
- **Persönlicher Stress** = **offene Quests + Storytiefe**
- **Stressfaktor** = **Storytiefe + StorylinePhase**

### UI-Stufen

- **Ruhig**: 1 - 2 offene Quests
- **Belebt**: 3 - 5 offene Quests
- **Hektisch**: 6 - 10 offene Quests
- Über 10 gleichzeitig offene Quests gehen nicht.
- Wenn **offene Quests > 10**, dann gilt für neue NQ-Generierung: **N/A**
- Das ist kein Story-Ende.
- Das gilt nicht für HQ.
- HQ werden separat über Trigger und über SL_ID gewertet.

### Entscheidungsformel für Begrüßungen

**Grundwert:**

`StressQuotient = (offene Quests + Storytiefe) / (Storytiefe + StorylinePhase)`

**Repräsentationswerte:**

- **Ruhig** = `0.495582`
- **Belebt** = `1.167863`
- **Hektisch** = `2.243511`

**Zonengrenzen:**

- Grenze zwischen **Ruhig** und **Belebt**:
  - `(0.495582 + 1.167863) / 2 = 0.831722`
- Grenze zwischen **Belebt** und **Hektisch**:
  - `(1.167863 + 2.243511) / 2 = 1.705687`

**If-Formel:**

- wenn `StressQuotient < 0.831722` -> **Ruhig**
- wenn `StressQuotient < 1.705687` -> **Belebt**
- wenn `StressQuotient >= 1.705687` -> **Hektisch**

**Verwendung:**
Dieser Endwert ist der Entscheidungsträger für die Begrüßungen.

## Storyzustand

### SL_ID / StoryLinePhase

- **SL_ID** = StoryLinePhasenID
- Die SL_ID muss im Playersave verankert sein.
- Sie sagt aus, an welcher Stelle in der Story sich der Spieler befindet, ohne dass die aktuelle Hauptquest bekannt sein muss.
- Die SL_ID ist eine eigene, einstellige Phasenvariable.
- Sie wird nicht aus der Storytiefe berechnet.
- Die Änderung der SL_ID muss vom Erledigen der Hauptquests ausgelöst werden.

### Storytiefe

Die Storytiefe bleibt ein separater Wert innerhalb der aktuellen Phase.

**Storytiefe-Stufen:**
- `0.00`
- `0.25`
- `0.50`
- `0.75`

**Regel:**
- Die Storytiefe wird separat gespeichert.
- Die QuestID bestimmt nur die Phase, nicht die Tiefe.
- Dadurch kann jede Phase in 4 Teile unterteilt werden.
- Ergebnis: 40 mögliche Quests in der Story.

**Fortschrittsregel:**
Wenn die Storytiefe über `0.75` hinaus erhöht würde:
- `SL_ID + 1`
- Storytiefe reset

### Hauptquest-Schema

- Hauptquests werden nach dem Schema **SL_ID + Storytiefe** erstellt.
- Vor dem Erstellen der nächsten Hauptquest muss zuerst abgefragt werden:
  - welche SL_ID aktuell gilt
  - welche Storytiefe aktuell gilt
- Erst danach darf über den neuen Questinhalt entschieden werden.
- Bei der Generierung der neuen QuestID müssen diese Werte in die Entscheidung einfließen.
- Bei jeder neu generierten Hauptquest wird die Storytiefe um `+0.25` erhöht.
- Dieser Wert muss im Playersavegame auftauchen und dort verwaltet werden.

## QuestID

### Definition

**QuestID** = `[typ][Stressfaktor][StorylinePhase][Questnr][Spielername]`

**Beispiele:**
- `NQ2100001EMBOLEX`
- `HQ1100021EMBOLEX`
- `NQ1300346EMBOLEX`

### Blockdefinition Questnr

- **Questnr** ist auf 5 Stellen begrenzt.
- Der Spielername steht weiterhin am Ende der QuestID.
- Die Länge des Spielernamens ist nicht begrenzt.

## Questarten

### Freigegebene Questarten

- sammeln
  - Items
  - Harvestables
- liefern
- craften
  - Subtyp: `craften.delivery` (Warenlieferung)
- finden
  - POIs und ihre Inhalte
  - Personen (später genauer definiert)
  - Objekte
- eskortieren
  - von -> nach -> zurückkommen ohne Tote

### Questziel-Pool

- Questziel-Pool = **Alle**
- Gemeint sind alle Ziele, die unter den geltenden Regeln des Systems zulässig sind.

## Verfügbarkeits- und Known-Logik

### Generelle Verfügbarkeitsregel

- In Quests dürfen nur Objekte benutzt werden, deren Wert auf selbigem oder niedrigerem StorylinePhase-Wert liegt.
- Dieser Wert wird pro Item über die **SL_ID** identifiziert.
- Alle Objekte mit höherem Wert müssen vom Generator ignoriert werden.

### NQ-Regel für SL_ID

- Bei der Generierung von Nebenquests muss in der QuestID immer der aktuell im Spielersave vorhandene SL_ID-Wert des Spielers eingetragen werden.
- Bei Nebenquests bleibt dieser Wert immer so, wie er aktuell im Playersave gespeichert ist.

### HQ-Regel für SL_ID

- Bei Hauptquests wird der aktuelle SL_ID-Wert nur einmal bei der Generierung genutzt, um den passenden Storystand zu prüfen.
- Der Hauptquest bestimmt nach seinem Erledigen den neuen SL_ID-Wert.

### Known-Flag

- Es gibt einen Known-Flag.
- Dieser gilt pro:
  - Item
  - Region
  - Harvestable
  - und weitere relevante Spielobjekte
- Über diesen Flag wird bestimmt, ob ein Inhalt dem Spieler bereits bekannt ist.

## NQ- und HQ-Generierung

### NQ-Generierungsintervall

- Die Generierung neuer Nebenquests soll zufällig zwischen 0 und 6 Stunden Echtzeit ausgelöst werden.
- Das gilt nur für Nebenquests.
- Wenn alle aktiven Nebenquests vorbei sind oder der Timer abgelaufen ist, müssen neue Nebenquests generiert werden.

### Anzahl neuer NQ vor der Generierung

- Vor der eigentlichen Generierung wird zuerst ein Zufallswert zwischen 0 und 3 ausgegeben.
- Dieser Wert bestimmt die Anzahl der neuen Quests.
- Wenn der Wert 0 ist, dann gilt:
  - **N/A**
  - es werden keine neuen Nebenquests erstellt

### N/A-Regel

- N/A wird in kein Log geschrieben.
- N/A wird nicht in die DB geschrieben.
- Bei N/A wird der Timer zurückgesetzt.

### HQ-Generierungsregel

- Nur ein Trigger kann eine HQ erzeugen.
- Eine HQ ist nur erzeugbar, wenn `aktive HQ = 0`.
- Das gilt unabhängig davon, welche Entity die HQ erzeugt.
- Eine HQ kann vom WanderQuestGuy oder von anderen Entities erzeugt werden.
- Ohne Trigger darf nie eine HQ generiert werden.
- Solange `aktive HQ > 0`, darf nie eine neue HQ generiert werden.
- Wenn die HQ-Generierung wegen einer bereits aktiven HQ verweigert wird, dann muss die Message lauten:
  - `Mach erstmal den vorherigen Quest fertig!`

### HQTrigger

- Es gibt eine eigene Definition **HQTrigger**.
- Dieser Wert muss immer abgefragt werden.
- Prüfregel:
  - `is true HQTrigger = 1?`
- Bei Spielstart wird `HQTrigger = 1` getriggert.
- Nur wenn dieser Trigger erfüllt ist, darf eine HQ-Generierung überhaupt stattfinden.

### Questannahme und HQ-Generierung

- NQ müssen beim WanderQuestGuy angenommen werden.
- HQ sind variabel.
- HQ können also auch außerhalb des WanderQuestGuy generiert oder vergeben werden.
- Für die HQ-Generierung beim WanderQuestGuy gilt:
  - nur bei Trigger
  - oder bei Trigger und Zeit
- Auch dann nutzen sie dieselbe DB.

## Queststatus

### NQ

- aktiv
- erledigt
- abgabebereit
- fehlgeschlagen

Alle NQ, die nicht angenommen wurden, müssen verworfen werden.
Sie werden nicht in der DB gespeichert.
Sie gelten nicht als Questbestand.

### HQ

- generiert
- aktiv
- abgabebereit
- erledigt

Für HQ gilt:
- verwerfen geht nicht
- HQ haben eine eigene Regel
- `angeboten` ist kein Queststatus

### Fehlschlagregel

- Wenn `final_reward_copper < 0`, dann gilt die Quest als **fehlgeschlagen**.

## Persistenz, DB und Save

### Quest-DB pro Spieler

- In die Quest-DB pro Spieler gehen nur Quests, die angenommen wurden.
- Gespeichert wird dabei die Quest selbst mit allen ihren Eigenschaften.
- Nicht angenommene NQ werden verworfen und nicht in der DB gespeichert.
- N/A wird nicht in die DB geschrieben.
- Die Quest-DB dient als Referenzspeicher für Questlogbuch und History.
- Nur angenommene Quests liegen als vollständiger Datensatz für den jeweiligen Spieler vor.

### DB / Save / Laufzeit

- QuestDB ist an das Savegame gekoppelt.
- Zweck: Entlastung.
- Im Playersave darf nur darauf hingewiesen werden, dass die Questdetails aus der DB kommen müssen.
- In den Playersave dürfen nur die Queststatus-Werte mit QuestID.
- Also z. B. nur:
  - aktiv
  - fertig
  - fehlgeschlagen
  - und weitere Statuswerte
- Die eigentlichen Questdetails bleiben in der QuestDB.
- Der Playersave enthält damit nur Status + ID-Verweis.
- Laufzeitdaten können spontan entstehen, deshalb existiert zusätzlich die DB.

### Storytiefe-Speicherort

- Die Storytiefe lebt separat im Playersave.
- Sie ist Teil verschiedener Formeln.
- Sie muss nicht in der Quest selbst verankert werden.

## Questlogbuch / UI

- Das UI soll mit denselben Assets gebaut werden wie das Inventar.
- Es soll zwei frei wählbare Oberreiter geben:
  - Hauptquests
  - Nebenquests
- Darunter soll es je Bereich zwei weitere Reiter geben:
  - offen
  - erledigt

### Inhalt des Menüs

- Alle Quests werden dort als Text angezeigt.
- Verwendet wird dabei der Text, mit dem der Spieler die Quest angenommen hat.
- Zusätzlich müssen folgende Informationen angezeigt werden:
  - wie hoch die Belohnung ist
  - von wem die Quest aufgenommen wurde
  - wo die Quest aufgenommen wurde
  - seit wann die Quest aktiv ist
- Sobald eine Quest abgeschlossen ist, muss zusätzlich angezeigt werden:
  - wann sie abgeschlossen wurde

### Verhaltensregel bei Statuswechsel

- Wenn eine Quest von aktiv/offen auf fertig/erledigt gestellt wird, muss sie selbstständig den Menüpunkt wechseln.
- Dabei müssen alle bisherigen Informationen erhalten bleiben.
- Zusätzlich muss dann angezeigt werden:
  - abgeschlossen am
  - abgeschlossen um

### Systembereiche, die das ZQS dafür abdecken muss

- Spielwissen: Ressourcen, Items, Livings, Harvestables
- Spielerwissen: Was kennt der Spieler bereits?
- Fortschritt: Spielerlevel, Questlogbucheintrag, Questlogbucheintrag-Nummer
- Queststatus:
  - intern getrennt nach NQ und HQ
  - UI-Bereiche: offen und erledigt
  - `abgabebereit` zählt für die UI zu `erledigt`
  - `fehlgeschlagen` bleibt eigener Endstatus und muss in History als bereits vorhandene Quest auftauchen
- Questgenerierung: Ziel, Regeln, Belohnung, Quest-ID
- Textgenerierung: Gruß, Auftrag, Belohnung, Abschied

## Textlogik-Anbindung

### Debug-Schalter für HQ-Mitwertung

Es gibt einen Debug-Schalter für die Textauswahl der Begrüßungen.

**Zweck:**
Er steuert, ob HQ-Zustände bei der Auswahl und Bewertung von Grüßen mit berücksichtigt oder vollständig ausgeschlossen werden.

**Debug-Modi:**

- `INCLUDE_HQ`
  - HQ-Status wird bei der Begrüßungsentscheidung mitgewertet.
  - NQ bleibt weiterhin aktiv.
  - Ergebnis: Entscheidung basiert auf NQ + HQ.

- `EXCLUDE_HQ`
  - HQ-Status wird für die Begrüßungsentscheidung vollständig ignoriert.
  - Ergebnis: Entscheidung basiert nur auf NQ.
  - Die Entscheidungslogik muss trotzdem vollständig funktionieren.

**Pflichtregel:**
Auch wenn HQ exkludiert wird, darf kein leerer oder unentscheidbarer Zustand entstehen. Der Generator muss immer auf NQ-Basis zu einer gültigen Auswahl kommen.

**Exakte Wirkung des Debug-Schalters:**
- Textwahl und Questgenerierung sind voneinander abhängig.
- Der Debug-Schalter darf nur das berühren, was im System bereits verankert ist.
- Er darf also keine neuen Regeln, neuen Zustände, neuen Datenquellen oder neue Ableitungen erzeugen.
- Er darf nur bereits definierte HQ-/NQ-Bezüge in der Entscheidung einbeziehen oder ausklammern.
- Alle anderen Systeme bleiben unverändert.

### Text-Präfix-Vorbereitung

- NQ-Präfix: aktiv, wird jetzt ausgearbeitet
- HQ-Präfix: vorbereitet, aber noch nicht befüllt

### Textkategorien

1. Grußtexte
2. Auftragstexte
3. Belohnungstexte
4. Abschiedstexte

Die konkrete Textstruktur steht in `zqs_textpool_szenarien_analyse1.md`.

## Belohnung und Zeit

### Wertesystem

- Mit **Wert** ist immer der Kupferwert gemeint.
- Das gilt für:
  - Items
  - Harvestables
  - Waffen
  - Werkzeuge
  - und weitere entsprechende Spielobjekte
- Wenn in Berechnungen von Questbelohnung, QItem, Objektwert oder Inhaltswert von Wert gesprochen wird, ist damit immer dieser Kupferwert gemeint.

### Zeitangabe bei Questgenerierung

- Bei der Generierung von Quests muss immer eine Zeitangabe mit rein.
- Diese Zeitangabe definiert, wie lange die Quest dauern sollte.
- Dieser Zeitwert ist Teil der Belohnungsberechnung.

### Zeitformeln

- Standardformel:
  - `Anzahl Objekte (alle in der Quest) x 120 x 0.86 = Zeit in s zum Erfüllen`
- Escortformel:
  - `Anzahl Gebiete zu durchqueren (alle in der Quest) x 420 x 0.86 + 300 = Zeit in s zum Erfüllen`

### Belohnungsberechnung

#### sammeln
`(Anzahl Harvestables x Wert + Anzahl Items x Wert) + (Zeit in sec x 33)`

#### liefern
`(Anzahl Harvestables x Wert + Anzahl Items x Wert) + (Zeit in sec x 33)`

#### craften
`QItem = (Anzahl Harvestables + Wert) + (Anzahl Items + Wert)`

`Belohnung craften = (Anzahl Harvestables x Wert + Anzahl Items x Wert + Anzahl QItem x Wert) + (Zeit in sec x 33)`

#### craften.delivery
`QItem = (Anzahl Harvestables + Wert) + (Anzahl Items + Wert)`

`Belohnung craften.delivery = (Anzahl Harvestables x Wert + Anzahl Items x Wert + Anzahl QItem x Wert) + (Zeit in sec x 33)`

#### finden

**POIs und ihre Inhalte**
`Wert des Inhalts + (Zeit in sec x 33)`

**Personen**
- später genauer definiert

**Objekte**
`(Anzahl Harvestables x Wert + Anzahl Items x Wert + QItem Wert) + (Zeit in sec x 33)`

#### eskortieren
`(Zeit in sec x 33)`

**Sonderfall:**
`von -> nach -> zurückkommen ohne Tote`

`(Zeit in sec x 33) + 50%`

### Penalty-Regel

`Wenn elapsed_time_sec <= expected_time_sec:`
`    penalty_copper = 0`

`Wenn elapsed_time_sec > expected_time_sec:`
`    penalty_copper = (elapsed_time_sec - expected_time_sec) x 150`

`final_reward_copper = base_reward_copper - penalty_copper`

`Wenn final_reward_copper < 0:`
`    Quest = gescheitert`

## Tageszeitfenster

Für die tageszeitabhängigen Texte gelten gängige reale Uhrzeit-Abgrenzungen. Die Uhrzeit selbst wird bereits im Spiel erzeugt.

- Morgen = `05:00` bis `10:59`
- Tag = `11:00` bis `17:59`
- Abend = `18:00` bis `21:59`
- Nacht = `22:00` bis `04:59`

## Bewusst offene oder nicht weiter ausgearbeitete Punkte

1. **HQTrigger-Auslöser**
   - zurzeit nicht Teil dieses Konzepts

2. **HQ-Regeln**
   - Platzhalter
   - zurzeit egal / nicht ausarbeiten

3. **Escort-Regelquelle**
   - wenn der Zufall Escort wählt, muss die Regel dazu auf eine Datei verweisen
   - Dateiname / Regelquelle: `Escort_Rules`

4. **Zielpool-Details**
   - zurzeit nicht Teil dieses Konzepts

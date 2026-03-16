# WanderQuestGuy / ZQS – Arbeitsplan

## Konstellationsrolle

Dieses Dokument bleibt der fachliche Ursprungsplan des ZQS.

Für zwei fachlich ausgekoppelte Bereiche gelten in der aktuellen Dokumentkonstellation zusätzlich primäre Bereichsreferenzen:

- Rewardlogik -> `zqs_reward_logik_referenz_v1.md`
- Möglicher Questinhalt -> `zqs_quest_inhaltslogik_referenz_v1.md`

Diese Bereichsreferenzen ändern keine Logik dieses Dokuments.
Sie bündeln nur die bereits in der Konstellation vorhandenen Regeln ihres Bereichs.


## Status

Code-Analyse des konkreten `WanderQuestGuy` ist in diesem Chat noch nicht möglich, weil hier keine Projektdateien vorliegen. Dieses Dokument dient deshalb zuerst als Plan- und Textspeicher für das ZufallsQuestSystem.

## Ziel

Der `WanderQuestGuy` soll nicht mehr nur feste Quests anbieten, sondern Quests aus einem ZufallsQuestSystem erhalten.

## Definitionen

- **Weltstress** = Status der Hauptquest
- **Questtyp-Unterscheidung**: Das System muss zwischen **Hauptquest** und **Nebenquest** unterscheiden.
- **Generatorlogik**: Beide Questtypen laufen über denselben Generatorkern, aber mit unterschiedlichem **Text-Präfix** und unterschiedlicher Auswahl-/Darstellungslogik.
- **Aktueller Arbeitsmodus**: Wir erstellen zurzeit **ausschließlich Texte und Teiltexte für Nebenquests**.
- **Markierung**: Alles, was wir jetzt ausarbeiten, wird direkt als **NQ** markiert.
- **HQ-Status**: Hauptquests werden im Plan bereits strukturell vorbereitet, aber noch nicht befüllt.

### Erweiterte Definition: Weltstress

- **Weltstress** = Wieviele offene Hauptquests hat der Spieler? In welcher Phase der StoryLine ist der Spieler?
- Die StoryLine-Phase wird über die **QuestID** definiert.
- Grundlage für Weltstress sind offene **Hauptquests**.

**Weltstress-Stufen:**

- **(niedrig) Ruhig**: 1 - 2 offene Quests + Stressfaktor 1 pro Quest
- **(mittel) Belebt**: 3 - 5 offene Quests + Stressfaktor 2 pro Quest
- **(hoch) Hektisch**: 6 - 10 offene Quests + Stressfaktor 3 pro Quest

**Stressfaktor:**

- **Stressfaktor** = 1. Zahl in QuestID + StorylinePhase

**StorylinePhase:**

- **StorylinePhase** = 2. Zahl in QuestID
- Die StorylinePhase ist **fix in der Hauptquest-ID eingebettet**.

### QuestID-Definition

- **QuestID** = `[typ][Stressfaktor][StorylinePhase][Questnr][Spielername]`

**Beispiele:**

- `NQ2100001EMBOLEX`
- `HQ1100021EMBOLEX`
- `NQ1300346EMBOLEX`

### Questarten

- sammeln
  - Items
  - Harvestables
- liefern
- craften
  - als Warenlieferung
- finden
  - POIs und ihre Inhalte
  - Personen (später genauer definiert)
  - Objekte
- eskortieren
  - von -> nach -> zurückkommen ohne Tote

### Generelle Verfügbarkeitsregel

- In Quests dürfen nur Objekte benutzt werden, deren Wert auf **selbigem oder niedrigerem StorylinePhase-Wert** liegt.
- Dieser Wert wird pro Item über die **SL_ID** identifiziert.
- **SL_ID** entspricht der **2. Zahl der QuestID**.
- Bei der Generierung von **Nebenquests (NQ)** muss in der QuestID **immer** der aktuell im Spielersave vorhandene **SL_ID-Wert des Spielers** eingetragen werden.
- Bei **Hauptquests (HQ)** wird der aktuelle SL_ID-Wert nur **einmal bei der Generierung** genutzt, um abzugleichen, auf welchem Storystand der Spieler gerade ist.
- Der Hauptquest bestimmt nach seinem **Erledigen** den **neuen SL_ID-Wert**.
- Alle Objekte mit höherem Wert müssen vom Generator ignoriert werden.

### Definition SL_ID

- **SL_ID** = StoryLinePhasenID
- Die SL_ID muss im **Spielersave** verankert sein.
- Sie sagt aus, an welcher Stelle in der Story sich der Spieler befindet, ohne dass die aktuelle Hauptquest bekannt sein muss.
- Dieser Wert wird nach und nach in den Hauptquests verändert, nachdem vorherige Hauptquests erledigt wurden.
- Dadurch entsteht eine flexible Story und flexible Verfügbarkeiten ohne direkte Abhängigkeit von einer einzelnen aktuell aktiven HQ.
- Die Änderung der SL_ID muss vom **Erledigen der Hauptquests** ausgelöst werden.
- Bei **Nebenquests** bleibt dieser Wert immer so, wie er aktuell im Playersavegame gespeichert ist, und genau dieser Wert muss in die neu generierte NQ-QuestID übernommen werden.
- Ein **Hauptquest** nutzt den aktuellen SL_ID-Wert nur zur Generierung und Prüfung des passenden Storystands; der neue SL_ID-Wert wird erst durch den Abschluss dieses Hauptquests festgelegt.

### Hauptquest-Schema: SL_ID und Storytiefe

- Hauptquests werden nach dem Schema **SL_ID + Storytiefe** erstellt.
- Die **Storytiefe** muss gespeichert werden.

**Storytiefe-Stufen:**

- `0.00`
- `0.25`
- `0.50`
- `0.75`

**Fortschrittsregel:**

- Wenn **Storytiefe = 1**, dann gilt:
  - **SL_ID + 1**
  - **Storytiefe reset**

**Erstellungsregel für die nächste Hauptquest:**

Vor dem Erstellen der nächsten Hauptquest muss zuerst abgefragt werden:

- welche **SL_ID** aktuell gilt
- welche **Storytiefe** aktuell gilt

Erst danach darf über den neuen Questinhalt entschieden werden.

Bei der Generierung der neuen **QuestID** müssen die vorher abgefragten Werte in die Entscheidung einfließen.

**Verwaltungsregel:**

- Bei jeder **neu generierten Hauptquest** wird die **Storytiefe um `+0.25`** erhöht.
- Dieser Wert muss im **Playersavegame** auftauchen und dort verwaltet werden.

### Quest-DB pro Spieler

- Alle Quests, die je generiert werden, müssen in einer **Quest-DB pro Spieler** aufgezeichnet werden.
- Gespeichert wird dabei der **Quest selbst mit allen seinen Eigenschaften**.
- Diese Speicherung dient **nicht** dazu, nur den Status wie offen oder erledigt festzuhalten.
- Es geht um die **reine Aufnahme des generierten Quests in die DB**.
- Die Quest-DB dient damit als **Referenzspeicher für das Questlogbuch**.
- Jede generierte Quest muss also als vollständiger Datensatz für den jeweiligen Spieler vorhanden sein, unabhängig davon, ob sie später offen, angenommen, abgeschlossen oder verworfen ist.

### Questlogbuch / UI

- Das UI soll mit denselben Assets gebaut werden wie das Inventar.
- Es soll zwei frei wählbare Oberreiter geben:
  - **Hauptquests**
  - **Nebenquests**
- Darunter soll es je Bereich zwei weitere Reiter geben:
  - **offen**
  - **erledigt**

**Inhalt des Menüs:**

- Alle Quests werden dort als Text angezeigt.
- Verwendet wird dabei der Text, mit dem der Spieler die Quest angenommen hat.
- Zusätzlich müssen folgende Informationen angezeigt werden:
  - wie hoch die Belohnung ist
  - von wem die Quest aufgenommen wurde
  - wo die Quest aufgenommen wurde
  - seit wann die Quest aktiv ist
- Sobald eine Quest abgeschlossen ist, muss zusätzlich angezeigt werden:
  - wann sie abgeschlossen wurde

**Verhaltensregel bei Statuswechsel:**

- Wenn eine Quest von **aktiv/offen** auf **fertig/erledigt** gestellt wird, muss sie selbstständig den Menüpunkt wechseln.
- Dabei müssen alle bisherigen Informationen erhalten bleiben.
- Zusätzlich muss dann angezeigt werden:
  - **abgeschlossen am**
  - **abgeschlossen um**

Das ZQS muss dafür später mindestens diese Bereiche abdecken:

- Spielwissen: Ressourcen, Items, Livings, Harvestables
- Spielerwissen: Was kennt der Spieler bereits?
- Fortschritt: Spielerlevel, Questlogbucheintrag, Questlogbucheintrag-Nummer
- Queststatus: gehabt, offen, erledigt
- Questgenerierung: Ziel, Regeln, Belohnung, Quest-ID
- Textgenerierung: Gruß, Auftrag, Belohnung, Abschied

## Questtyp-Logik für Texte

- **NQ** = Nebenquest
- **HQ** = Hauptquest
- Derselbe Generator erzeugt beide Typen.
- Der Unterschied liegt vor allem in:
  - Text-Präfix
  - Auswahlregeln
  - möglicher Gewichtung
  - späterer Präsentation im Spiel

### Debug-Schalter für HQ-Mitwertung

Es gibt einen Debug-Schalter für die Textauswahl der Begrüßungen.

**Zweck:**
Er steuert, ob **HQ-Zustände** bei der Auswahl und Bewertung von Grüßen mit berücksichtigt oder vollständig ausgeschlossen werden.

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

- **Textwahl** und **Questgenerierung** sind voneinander abhängig.
- Der Debug-Schalter darf **nur das berühren, was im System bereits verankert ist**.
- Er darf also keine neuen Regeln, neuen Zustände, neuen Datenquellen oder neue Ableitungen erzeugen.
- Er darf nur bereits definierte HQ-/NQ-Bezüge in der Entscheidung **einbeziehen** oder **ausklammern**.
- Alle anderen Systeme bleiben unverändert.

**Einsatzbereich aktuell:**
Der Schalter ist zunächst für **Grußtexte** vorgesehen.

### Text-Präfix-Vorbereitung

- **NQ-Präfix**: aktiv, wird jetzt ausgearbeitet
- **HQ-Präfix**: vorbereitet, aber noch nicht befüllt

## Textkategorien für die Generierung

1. **Grußtexte**

   - Einstiegstexte des WanderQuestGuy
   - beeinflusst durch Uhrzeit, Spielerzustand, Weltstress, offene Quests, erledigte Quests

2. **Auftragstexte**

   - eigentliche Questbeschreibung
   - basiert auf Questziel, Zieltyp, Gewichtung, Bedingungen und Varianten

3. **Belohnungstexte**

   - Text zur Questbelohnung
   - basiert auf Belohnungsart, Questaufwand, Seltenheit und Kontext

4. **Abschiedstexte**

   - Abschlussfloskeln nach Gespräch oder Questannahme
   - allgemeine oder situationsabhängige Verabschiedung

## Teiltext-Sammlung

### 1. Grußtexte [NQ aktiv | HQ vorbereitet]

**Pflichtregel vor jeder Begrüßung:**
Vor Auswahl eines Grußtextes muss der `WanderQuestGuy` immer zuerst prüfen:

- Wie viele **offene Quests** hat der Spieler?
- Wie viele **erledigte Quests** hat der Spieler?
- Ob HQ-Zustände je nach Debug-Schalter **mitgewertet** oder **übersprungen** werden

Diese Abfrage passiert **immer vor dem Gruß**.

**Struktur der Grußtexte:**

#### Morgen

- **Hauptstücke**
- **Mittelstücke**
  - Weltstress: niedrig
  - Weltstress: mittel
  - Weltstress: hoch
- **Endstücke**

#### Tag

- **Hauptstücke**
- **Mittelstücke**
  - Weltstress: niedrig
  - Weltstress: mittel
  - Weltstress: hoch
- **Endstücke**

#### Abend

- **Hauptstücke**
- **Mittelstücke**
  - Weltstress: niedrig
  - Weltstress: mittel
  - Weltstress: hoch
- **Endstücke**

#### Nacht

- **Hauptstücke**
- **Mittelstücke**
  - Weltstress: niedrig
  - Weltstress: mittel
  - Weltstress: hoch
- **Endstücke**

**Bauprinzip eines vollständigen Grußes:**

`Hauptstück + Mittelstück + Endstück`

Dabei gilt:

- Uhrzeit bestimmt den Hauptpool
- Weltstress bestimmt den Mittelstück-Pool
- Offene und erledigte Quests müssen vorher abgefragt werden und beeinflussen später die Auswahlregeln

### 2. Auftragstexte [NQ aktiv | HQ vorbereitet]

**Struktur der Auftragstexte:**

`Hauptteil + Mittelteil + Endteil`

**Grundregeln:**

- Jeder Teil hat **seine eigene Teilliste**.
- Der fertige Belohnungstext wird so gebaut, dass aus jeder passenden Teilliste **je ein Teiltext zufällig ausgewählt** wird.
- Es gibt also **keine freie Durchmischung der Teile untereinander**.
- Die Struktur bleibt immer fest:
  - Hauptteil aus der Hauptteil-Liste
  - Mittelteil aus der Mittelteil-Liste
  - Endteil aus der Endteil-Liste
**Klarstellung (Anzeige-Referenz):**
- Der Auftragstext darf dem Spieler eine **Belohnung als Anzeigetext-Referenz** zeigen, damit er einen Annahmegrund sieht.
- Der **Wert/Inhalt** der Belohnung kommt dabei **nicht** aus dem Textgenerator, sondern aus dem **Rewardgenerator/Questdatensatz**.
- Der Textgenerator zeigt nur, was bereits in der Questinstanz steht (z. B. via Platzhalter-Referenz).

- Die **QuestID** dient nur als **ID / Verweis**.
- Alle Belohnungsinformationen kommen aus dem **Quest-DB-Eintrag**, der über diese QuestID referenziert wird.

**Belohnungsinhalt im Mittelteil:**

- **Physische Belohnung**
  - z. B. Item, Ressource, Währung, Objekt
- **Textliche Belohnungsbeschreibung**
  - also die sprachliche Form der Belohnung im Satz

Der Mittelteil muss also sowohl die **reale Spielbelohnung** als auch den dazu passenden **Textbaustein** aus dem **Quest-DB-Eintrag** ableiten. Die QuestID ist dabei nur der Verweis auf diesen Eintrag.

**Annahme-/Abschlussregel:**

Wenn die Belohnung vom Spieler **akzeptiert** wurde:

- wird die **Quest-ID** im Spielersave als **beendet** markiert
- wird der **Questlogbucheintrag** entsprechend geändert
- wird der Status der Quest von offen/abgabebereit auf beendet gesetzt

#### Hauptteil

- Einheitliche oder allgemein nutzbare Liste für Einleitung der Belohnung

#### Mittelteil

- enthält die konkrete Belohnung
- liest die Belohnung über den **Quest-DB-Eintrag per QuestID-Verweis**
- enthält physische Belohnung + passenden Text

#### Endteil

- allgemeine Abschluss- oder Übergabeformulierung

#### Abschlussbedingung

- Belohnung akzeptiert -> Quest-ID im Save als beendet markieren
- Questlogbucheintrag entsprechend anpassen

### 4. Abschiedstexte [NQ aktiv | HQ vorbereitet]

**Struktur der Abschiedstexte:**

`Hauptteil + Mittelteil + Endteil`

**Grundregeln:**

- Abschiedstexte bestehen aus einer **Liste vorgefertigter Antworten**.
- Diese Antworten sind **nach Zeit sortiert und bedingt**.
- Auch hier hat jeder Teil **seine eigene Teilliste**.
- Der fertige Abschiedstext entsteht dadurch, dass aus jeder passenden Teilliste **je ein Teiltext zufällig ausgewählt** wird.
- Die Struktur bleibt fest:
  - Hauptteil aus der Hauptteil-Liste
  - Mittelteil aus der Mittelteil-Liste
  - Endteil aus der Endteil-Liste

**Zeitbedingung:**

Die Auswahl der Abschiedstexte ist an die Spielzeit gebunden:

- Morgen
- Tag
- Abend
- Nacht

Jede dieser Zeitgruppen hat ihre eigenen passenden Teiltexte.

#### Morgen

- Hauptteile
- Mittelteile
- Endteile

#### Tag

- Hauptteile
- Mittelteile
- Endteile

#### Abend

- Hauptteile
- Mittelteile
- Endteile

#### Nacht

- Hauptteile
- Mittelteile
- Endteile

## Nächster Schritt

Diese vier Kategorien werden jetzt mit Teiltexten befüllt.


### Belohnungsberechnung

Die Belohnungen errechnen sich aus:

- **Basisbelohnung für Aufgabentyp**
- **Objektwert**
- **Anzahl**
- **Zeit**

#### sammeln

`(Anzahl Harvestables x Wert + Anzahl Items x Wert) + (Zeit in sec x 33)`

#### liefern

`(Anzahl Harvestables x Wert + Anzahl Items x Wert) + (Zeit in sec x 33)`

#### craften

`QItem = (Anzahl Harvestables + Wert) + (Anzahl Items + Wert)`

`Belohnung craften = (Anzahl Harvestables x Wert + Anzahl Items x Wert + Anzahl QItem x Wert) + (Zeit in sec x 33)`

#### als Warenlieferung

`QItem = (Anzahl Harvestables + Wert) + (Anzahl Items + Wert)`

`Belohnung Warenlieferung = (Anzahl Harvestables x Wert + Anzahl Items x Wert + Anzahl QItem x Wert) + (Zeit in sec x 33)`

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

### Zeitangabe bei Questgenerierung

- Bei der Generierung von Quests muss immer eine **Zeitangabe** mit rein.
- Diese Zeitangabe definiert, **wie lange die Quest dauern sollte**.
- Dieser Zeitwert ist Teil der Belohnungsberechnung.

### HQ-Generierungsregel

- **Nur ein Trigger** kann eine **HQ** erzeugen.
- Eine HQ ist **nur erzeugbar**, wenn **aktive HQ = 0**.
- Das gilt unabhängig davon, welche Entity die HQ erzeugt.
- Eine HQ kann vom **WanderQuestGuy** oder von **anderen Entities** erzeugt werden.
- Ohne Trigger darf **nie** eine HQ generiert werden.
- Solange **aktive HQ > 0**, darf **nie** eine neue HQ generiert werden.
- Wenn die HQ-Generierung wegen einer bereits aktiven HQ verweigert wird, dann muss die Message lauten:
  - `Mach erstmal den vorherigen Quest fertig!`

### NQ-Generierungsintervall

- Die Generierung neuer **Nebenquests (NQ)** soll zufällig zwischen **0 und 6 Stunden Echtzeit** ausgelöst werden.
- Das gilt nur für **Nebenquests**.
- Wenn **alle aktiven Nebenquests vorbei** sind oder der **Timer abgelaufen** ist, müssen neue Nebenquests generiert werden.

**Anzahl neuer Quests vor der Generierung:**

- Vor der eigentlichen Generierung wird zuerst ein Zufallswert zwischen **0 und 3** ausgegeben.
- Dieser Wert bestimmt die **Anzahl der neuen Quests**.
- Wenn der Wert **0** ist, dann gilt:
  - **N/A**
  - es werden also keine neuen Nebenquests erstellt

### Tageszeitfenster

Für die tageszeitabhängigen Texte gelten gängige reale Uhrzeit-Abgrenzungen. Die Uhrzeit selbst wird bereits im Spiel erzeugt.

- **Morgen** = `05:00` bis `10:59`
- **Tag** = `11:00` bis `17:59`
- **Abend** = `18:00` bis `21:59`
- **Nacht** = `22:00` bis `04:59`

### QuestID-Blockdefinition: Questnr

- **Questnr** ist auf **5 Stellen** begrenzt.
- Der **Spielername** steht weiterhin am Ende der QuestID.
- Die Länge des Spielernamens ist dabei **nicht begrenzt**.


## Ergänzungen / Korrekturen

### HQTrigger

- Es gibt eine eigene Definition **HQTrigger**.
- Dieser Wert muss immer abgefragt werden.
- Prüfregel:
  - `is true HQTrigger = 1?`
- Nur wenn dieser Trigger erfüllt ist, darf eine HQ-Generierung überhaupt stattfinden.

### Queststatus-Regeln

**NQ:**

- aktiv
- erledigt
- abgabebereit
- fehlgeschlagen

Alle NQ, die **nicht angenommen** wurden, müssen **verworfen** werden.

- sie werden **nicht** in der DB gespeichert
- sie gelten nicht als Questbestand

**HQ:**

- generiert
- angeboten
- aktiv
- abgabebereit
- erledigt

Für HQ gilt:

- **verwerfen geht nicht**
- HQ haben eine eigene Regel

### Fehlschlagregel

- Wenn **final_reward_copper < 0**, dann gilt die Quest als **gescheitert**.

### Known-Flag

- Es gibt einen **Known-Flag**.
- Dieser gilt pro:
  - Item
  - Region
  - Harvestable
  - und weitere relevante Spielobjekte
- Über diesen Flag wird bestimmt, ob ein Inhalt dem Spieler bereits bekannt ist.

### Korrektur Weltstress

- **Weltstress** = **Persönliche Stress Stufe + Stressfaktor**
- **Persönlicher Stress** = **offene Quests + Storytiefe**

**Stress-Stufen für UI:**

- **(niedrig) Ruhig**: 1 - 2 offene Quests
- **(mittel) Belebt**: 3 - 5 offene Quests
- **(hoch) Hektisch**: 6 - 10 offene Quests
- **Über 10 gleichzeitig offene Quests** gehen nicht.
- Wenn **offene Quests > 10**, dann gilt für neue Questgenerierung:
  - **N/A**
- Das ist **kein Story-Ende**.
- Das gilt **nicht für HQ**.
- **HQ** werden separat über **Trigger** und über **SL_ID** gewertet.

### Korrektur Stressfaktor

- **Stressfaktor** = Storytiefe + StorylinePhase

### Storytiefe und QuestID

- Die **Storytiefe** wird nur gespeichert.
- Die **QuestID** bestimmt nur die **Phase**, nicht die Tiefe.
- Dadurch kann jede Phase in **4 Teile** unterteilt werden.
- Ergebnis: **40 mögliche Quests in der Story**.

### Questziel-Pool

- Questziel-Pool = **Alle**

### Questannahme und HQ-Generierung

- **NQ** müssen beim **WanderQuestGuy** angenommen werden.
- **HQ** sind variabel.
- HQ können also auch außerhalb des WanderQuestGuy generiert oder vergeben werden.
- Für die HQ-Generierung beim WanderQuestGuy gilt:
  - nur bei **Trigger**
  - oder bei **Trigger und Zeit**
- Auch dann nutzen sie dieselbe DB.

### N/A-Korrektur

- N/A wird in **kein Log** geschrieben.
- N/A wird **nicht** in die DB geschrieben.
- Bei N/A wird der Timer zurückgesetzt.

### Wertesystem

- Mit **Wert** ist immer der **Kupferwert** gemeint.
- Das gilt für:
  - Items
  - Harvestables
  - Waffen
  - Werkzeuge
  - und weitere entsprechende Spielobjekte
- Wenn in Berechnungen von Questbelohnung, QItem, Objektwert oder Inhaltswert von **Wert** gesprochen wird, ist damit immer dieser **Kupferwert** gemeint.

### Weitere Festlegungen

1. **Quest-DB-Feldregel**
- In der Quest-DB stehen **alle Informationen einer Quest, die generiert wird**.
- Der Datensatz wird angeführt von der **QuestID**.

2. **HQTrigger-Auslöser**
- Zurzeit **nicht Teil dieses Konzepts**.

3. **HQ-Regeln**
- Zurzeit **egal / nicht ausarbeiten**.

4. **Escort-Regelquelle**
- Wenn der Zufall **Escort** wählt, muss die Regel dazu auf eine Datei verweisen, aus der sie bezogen wird.
- Dateiname / Regelquelle: **`Escort_Rules`**

5. **Zeitformeln**
- Standardformel:
  - `Anzahl Objekte (alle in der Quest) x 120 x 0.86 = Zeit in s zum Erfüllen`
- Escortformel:
  - `Anzahl Gebiete zu durchqueren (alle in der Quest) x 420 x 0.86 + 300 = Zeit in s zum Erfüllen`

6. **Zielpool-Details**
- Zurzeit **nicht Teil dieses Konzepts**.

7. **Weltstress-If-Formel**
- Wird **später separat definiert**.

8. **DB / Save / Laufzeit**
- **QuestDB** ist an das **Savegame gekoppelt**.
- Zweck: **Entlastung**.
- Im **Playersave** darf nur darauf hingewiesen werden, dass die **Questdetails aus der DB** kommen müssen.
- In den Playersave dürfen nur die **Queststatus-Werte mit QuestID**.
- Also z. B. nur:
  - aktiv
  - fertig
  - fehlgeschlagen
  - und weitere Statuswerte
- Die eigentlichen Questdetails bleiben in der **QuestDB**.
- Der Playersave enthält damit nur Status + ID-Verweis.
- Laufzeitdaten können spontan entstehen, deshalb existiert zusätzlich die **DB**.

9. **Storytiefe-Speicherort**
- Die **Storytiefe** lebt **separat im Playersave**.
- Sie ist Teil verschiedener Formeln.
- Sie muss **nicht in der Quest selbst verankert** werden.

### Weltstress-If-Formel

**Grundwert:**

`StressQuotient = (offene Quests + Storytiefe) / (Storytiefe + StorylinePhase)`

**Basis für die Zonenbildung:**

- Die Zonen beruhen auf dem **Mittelwert des Mittelwerts** aller **4 Storytiefen** (`0.00`, `0.25`, `0.50`, `0.75`).
- Daraus ergeben sich diese Repräsentationswerte:
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

- Dieser Endwert ist der **Entscheidungsträger für die Begrüßungen**.

### Korrektur: StoryLinePhase / SL_ID / Storytiefe

- **StoryLinePhase / SL_ID** muss eine **eigene Variable** sein.
- Sie darf **nur einstellig** sein.
- Sie wird **nicht** aus der Storytiefe berechnet.
- Die **Storytiefe** bleibt ein **separater Wert**.
- Die StoryLinePhase / SL_ID wird erst dann **um +1 erhöht**, wenn die **Storytiefe über 0.75 hinaus erhöht würde**.
- Danach wird die **Storytiefe zurückgesetzt**.

**Regel:**

- Storytiefe läuft über:
  - `0.00`
  - `0.25`
  - `0.50`
  - `0.75`
- Nächster Schritt nach `0.75`:
  - **SL_ID + 1**
  - **Storytiefe reset**

Damit gilt:

- **SL_ID** = eigene, einstellige Phasenvariable
- **Storytiefe** = separater Fortschrittswert innerhalb dieser Phase

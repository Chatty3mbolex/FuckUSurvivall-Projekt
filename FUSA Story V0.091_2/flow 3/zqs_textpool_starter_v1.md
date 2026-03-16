# **ZQS Textpool v1.1 (Produktionsdaten)**

Dieser Pool wurde gemäß zqs\_snippetkatalog\_v1.md erstellt. Er berücksichtigt die fachliche Logik von Plan 1.0 (inkl. Zeitfaktor) und die Inhaltsklassen der Szenarien-Analyse.

## **1\. GREETING (Gruß)**

*Struktur: main (Zeit) \-\> middle (Weltstress) \-\> end (Nächster Schritt)*

### **1.1 greeting.main (Fokus: time\_of\_day)**

*Vorgabe: Mindestens 5 pro Zeitfenster.*

#### **Morgen (05:00 \- 10:59)**

1. greeting.main.morning.001: „Die Sonne steht tief über den Gipfeln.“ | filters: time\_of\_day=morning  
2. greeting.main.morning.002: „Ein kühler Start in den Zyklus.“ | filters: time\_of\_day=morning  
3. greeting.main.morning.003: „Früher als erwartet. Konzentriere dich.“ | filters: time\_of\_day=morning  
4. greeting.main.morning.004: „Der Tau ist noch nicht getrocknet.“ | filters: time\_of\_day=morning  
5. greeting.main.morning.005: „Erwachst du aus der Phasenverschiebung?“ | filters: time\_of\_day=morning

#### **Tag (11:00 \- 17:59)**

1. greeting.main.day.001: „Das Licht ist jetzt am stärksten.“ | filters: time\_of\_day=day  
2. greeting.main.day.002: „Gut, dass du gerade hier bist.“ | filters: time\_of\_day=day  
3. greeting.main.day.003: „Mitten im Geschehen, wie ich sehe.“ | filters: time\_of\_day=day  
4. greeting.main.day.004: „Die Zeit läuft uns nicht davon, aber sie wartet auch nicht.“ | filters: time\_of\_day=day  
5. greeting.main.day.005: „Präsenz bestätigt.“ | filters: time\_of\_day=day

#### **Abend (18:00 \- 21:59)**

1. greeting.main.evening.001: „Die Schatten werden länger.“ | filters: time\_of\_day=evening  
2. greeting.main.evening.002: „Ein langer Zyklus neigt sich dem Ende.“ | filters: time\_of\_day=evening  
3. greeting.main.evening.003: „Das Abendlicht verzerrt die Wahrnehmung.“ | filters: time\_of\_day=evening  
4. greeting.main.evening.004: „Noch aktiv um diese Stunde?“ | filters: time\_of\_day=evening  
5. greeting.main.evening.005: „Die Dämmerung ist die beste Zeit für Geschäfte.“ | filters: time\_of\_day=evening

#### **Nacht (22:00 \- 04:59)**

1. greeting.main.night.001: „Die Dunkelheit ist absolut.“ | filters: time\_of\_day=night  
2. greeting.main.night.002: „Nur wir beide sind noch wach.“ | filters: time\_of\_day=night  
3. greeting.main.night.003: „Die Stille der Nacht ist trügerisch.“ | filters: time\_of\_day=night  
4. greeting.main.night.004: „Leise. Die Umgebung schläft nicht nur.“ | filters: time\_of\_day=night  
5. greeting.main.night.005: „Ein nächtliches Treffen. Ungewöhnlich.“ | filters: time\_of\_day=night

### **1.2 greeting.middle (Fokus: worldstress\_zone)**

*Vorgabe: Mindestens 5 pro Zone.*

#### **Ruhig (UI: Ruhig)**

1. greeting.middle.ruhig.001: „...während die Umgebung stabil bleibt...“  
2. greeting.middle.ruhig.002: „...in dieser ungewohnten Stille...“  
3. greeting.middle.ruhig.003: „...fast schon zu friedlich hier...“  
4. greeting.middle.ruhig.004: „...ohne nennenswerte Störungen im Umfeld...“  
5. greeting.middle.ruhig.005: „...da wir gerade Kapazitäten frei haben...“

#### **Belebt (UI: Belebt)**

1. greeting.middle.belebt.001: „...mitten im normalen Treiben...“  
2. greeting.middle.belebt.002: „...während die Welt ihren Gang geht...“  
3. greeting.middle.belebt.003: „...inmitten der alltäglichen Bewegung...“  
4. greeting.middle.belebt.004: „...da gerade vieles gleichzeitig geschieht...“  
5. greeting.middle.belebt.005: „...zwischen all den kleinen Ereignissen...“

#### **Hektisch (UI: Hektisch)**

1. greeting.middle.hektisch.001: „...inmitten dieses absoluten Chaos...“  
2. greeting.middle.hektisch.002: „...trotz der kritischen Instabilität...“  
3. greeting.middle.hektisch.003: „...während alles um uns herum zerfällt...“  
4. greeting.middle.hektisch.004: „...unter dem Druck der aktuellen Ereignisse...“  
5. greeting.middle.hektisch.005: „...obwohl die Zeitkristalle vibrieren...“

### **1.3 greeting.end (Fokus: conversation\_next\_step)**

*Vorgabe: Mindestens 5 pro Schritt.*

#### **Schritt: info**

1. greeting.end.info.001: „...höre dir das hier an:“  
2. greeting.end.info.002: „...folgendes musst du wissen Counsel:“  
3. greeting.end.info.003: „...ich habe Neuigkeiten für dich:“  
4. greeting.end.info.004: „...die Lage ist wie folgt:“  
5. greeting.end.info.005: „...kurzes Update zur Situation:“

#### **Schritt: offer (Quest-Angebot)**

1. greeting.end.offer.001: „...ich hätte da eine Aufgabe für dich:“  
2. greeting.end.offer.002: „...kannst du mir bei etwas helfen?“  
3. greeting.end.offer.003: „...es gibt da eine Gelegenheit für dich:“  
4. greeting.end.offer.004: „...deine Hilfe wird benötigt:“  
5. greeting.end.offer.005: „...hast du Ressourcen für einen Auftrag frei?“

## **2\. ASSIGNMENT (Auftrag)**

*Struktur: main (Einleitung) \-\> middle (Quest-Subtype) \-\> end (Zeitvorgabe & Abschluss)*

### **2.1 assignment.main**

*Vorgabe: Mindestens 10 insgesamt.*

1. assignment.main.001: „Es geht um Folgendes:“  
2. assignment.main.002: „Die Mission ist klar:“  
3. assignment.main.003: „Folgender Plan:“  
4. assignment.main.004: „Hör genau zu:“  
5. assignment.main.005: „Die Aufgabe lautet:“  
6. assignment.main.006: „Deine Anweisung ist simpel:“  
7. assignment.main.007: „Hier ist dein Ziel:“  
8. assignment.main.008: „Konzentrier dich darauf:“  
9. assignment.main.009: „Darum sollst du dich kümmern:“  
10. assignment.main.010: „Das ist jetzt zu tun:“

### **2.2 assignment.middle (Fokus: Subtypes)**

*Vorgabe: Mindestens 10 pro Subtype.*

#### **Subtype: collect\_item (Sammeln)**

1. assignment.middle.collect.001: „...besorg mir {target\_quantity}x {target\_name} aus der Region...“  
2. assignment.middle.collect.002: „...suche und sichere {target\_quantity} Einheiten {target\_name}...“  
3. assignment.middle.collect.003: „...ich benötige dringend {target\_quantity}x {target\_name}...“  
4. assignment.middle.collect.010: „...ohne {target\_quantity}x {target\_name} kommen wir hier nicht weiter...“ (Fortlaufend bis .010 wie in Vorversion)

#### **Subtype: kill\_enemy (Eliminieren)**

1. assignment.middle.kill.001: „...eliminiere {target\_quantity}x {target\_name}, sie stören die Balance...“ (Fortlaufend bis .010 wie in Vorversion)

#### **Subtype: find\_poi (Finden)**

1. assignment.middle.find.001: „...lokalisiere den Ort {target\_name} in {target\_region}...“  
2. assignment.middle.find.002: „...suche nach Hinweisen auf {target\_name} nahe {target\_region}...“  
3. assignment.middle.find.003: „...finde das Versteck von {target\_name} und markiere es...“  
4. assignment.middle.find.004: „...spüre {target\_name} auf, wir müssen die Position kennen...“  
5. assignment.middle.find.005: „...erkunde das Gebiet um {target\_region}, um {target\_name} zu finden...“  
6. assignment.middle.find.006: „...identifiziere die genauen Koordinaten von {target\_name}...“  
7. assignment.middle.find.007: „...suche die Umgebung nach {target\_name} ab...“  
8. assignment.middle.find.008: „...wir haben die Spur von {target\_name} verloren, nimm sie wieder auf...“  
9. assignment.middle.find.009: „...durchkämme {target\_region} nach {target\_name}...“  
10. assignment.middle.find.010: „...bestätige die Existenz von {target\_name} vor Ort...“

#### **Subtype: craft\_item (Craften)**

1. assignment.middle.craft.001: „...fertige mir {target\_quantity}x {target\_name} an der Werkbank...“  
2. assignment.middle.craft.002: „...ich benötige {target\_quantity} handgefertigte {target\_name}...“  
3. assignment.middle.craft.003: „...nutze deine Ressourcen, um {target\_quantity}x {target\_name} herzustellen...“  
4. assignment.middle.craft.004: „...schmiede insgesamt {target\_quantity} Einheiten {target\_name}...“  
5. assignment.middle.craft.005: „...konstruiere {target\_quantity}x {target\_name} nach meinen Vorgaben...“  
6. assignment.middle.craft.006: „...produziere {target\_quantity}x {target\_name} von hoher Qualität...“  
7. assignment.middle.craft.007: „...verarbeite das Material zu {target\_quantity}x {target\_name}...“  
8. assignment.middle.craft.008: „...stelle sicher, dass wir {target\_quantity}x {target\_name} im Lager haben...“  
9. assignment.middle.craft.009: „...bereite {target\_quantity}x {target\_name} für den Einsatz vor...“  
10. assignment.middle.craft.010: „...wir brauchen eine Lieferung von {target\_quantity} selbstgemachten {target\_name}...“

#### **Subtype: deliver\_item (Liefern \- Hybrid)**

1. assignment.middle.deliver.001: „...bringe {target\_quantity}x {target\_name} direkt zu {target\_entity} in {target\_region}...“  
2. assignment.middle.deliver.002: „...liefere {target\_quantity} Einheiten {target\_name} bei {target\_entity} ab...“  
3. assignment.middle.deliver.003: „...transportiere {target\_quantity}x {target\_name} sicher zu {target\_entity}...“  
4. assignment.middle.deliver.004: „...übergib {target\_quantity}x {target\_name} an {target\_entity}, er erwartet dich...“  
5. assignment.middle.deliver.005: „...stelle {target\_quantity}x {target\_name} fertig und bringe sie zu {target\_entity}...“  
6. assignment.middle.deliver.006: „...logistische Priorität: {target\_quantity}x {target\_name} für {target\_entity}...“  
7. assignment.middle.deliver.007: „...versorge {target\_entity} mit {target\_quantity}x {target\_name}...“  
8. assignment.middle.deliver.008: „...übermittle {target\_quantity}x {target\_name} an den Kontaktpunkt bei {target\_entity}...“  
9. assignment.middle.deliver.009: „...bringe die Fracht ({target\_quantity}x {target\_name}) zu {target\_entity}...“  
10. assignment.middle.deliver.010: „...stelle sicher, dass {target\_entity} seine {target\_quantity}x {target\_name} erhält...“

### **2.3 assignment.end (ÜBERARBEITET: Fokus auf Zeitfaktor)**

*Vorgabe: Mindestens 10 insgesamt. Alle Snippets integrieren die expected\_time\_sec.*

1. assignment.end.001: „...und denk dran: Du hast nur {expected\_time\_sec} Sekunden Zeit, bevor der Lohn sinkt.“  
2. assignment.end.002: „...beeil dich, die Phase ist nur für {expected\_time\_sec} Sekunden stabil.“  
3. assignment.end.003: „...ich erwarte den Abschluss innerhalb der nächsten {expected\_time\_sec} Sekunden.“  
4. assignment.end.004: „...trödel nicht, nach {expected\_time\_sec} Sekunden greift die Penalty-Regel.“  
5. assignment.end.005: „...das Zeitfenster beträgt exakt {expected\_time\_sec} Sekunden. Nutze sie.“  
6. assignment.end.006: „...melde Vollzug in unter {expected\_time\_sec} Sekunden, wenn du den vollen Bonus willst.“  
7. assignment.end.007: „...dein Taktgeber ist auf {expected\_time\_sec} Sekunden eingestellt. Los jetzt.“  
8. assignment.end.008: „...jede Sekunde über {expected\_time\_sec} hinaus kostet dich bares Kupfer.“  
9. assignment.end.009: „...du hast {expected\_time\_sec} Sekunden, um diese Aufgabe fehlerfrei zu beenden.“  
10. assignment.end.010: „...die Zeitkristalle halten diese Verbindung nur für {expected\_time\_sec} Sekunden.“

## **3\. ASSIGNMENT\_NA (Nicht verfügbar)**

### **3.1 assignment\_na (Fokus: na\_reason)**

*Vorgabe: Mindestens 5 pro Grund.*

#### **Grund: cap\_reached**

1. na.cap.001: „Du hast schon zu viel angefangen. Beende erst deine offenen Aufgaben.“  
2. na.cap.002: „Deine Liste ist voll. Ich kann dir nichts Neues anbieten.“  
3. na.cap.003: „Konzentration ist der Schlüssel. Verliere dich nicht in zu vielen Quests.“  
4. na.cap.004: „Komm zurück, wenn du Platz für neue Verpflichtungen hast.“  
5. na.cap.005: „Dein Logbuch lässt keine weiteren Einträge zu.“

#### **Grund: blocked**

1. na.blocked.001: „Der aktuelle Phasenverlauf lässt keine weiteren Nebenaufgaben zu.“  
2. na.blocked.002: „Die Storytiefe ist hier noch nicht ausreichend.“  
3. na.blocked.003: „Bestimmte Ereignisse blockieren momentan meine Anfragen.“  
4. na.blocked.004: „Warte auf die nächste StorylinePhase.“  
5. na.blocked.005: „Die Zeitlinie ist an dieser Stelle gesperrt.“

## **4\. REWARD (Belohnung)**

*Struktur: main \-\> middle \-\> end*

### **4.1 reward.main (Fokus: reward\_state)**

*Vorgabe: Mindestens 5 pro Status.*

1. reward.main.standard.001: „Als Ausgleich für deine Mühen...“  
2. reward.main.standard.002: „Hier ist dein vereinbarter Anteil...“  
3. reward.main.standard.003: „Für den erfolgreichen Abschluss...“  
4. reward.main.standard.004: „Deine Belohnung steht bereit...“  
5. reward.main.standard.005: „Ich bezahle meine Schulden immer...“

### **4.2 reward.middle (Fokus: reward\_text\_mode)**

*Vorgabe: Mindestens 10 pro Modus.*

#### **Modus: currency (Währung)**

1. reward.middle.curr.001: „...erhältst du {reward\_silver} Silber.“ (Fortlaufend bis .010)

### **4.3 reward.end**

*Vorgabe: Mindestens 5\.*

1. reward.end.001: „...nutze es weise.“  
2. reward.end.002: „...wir sind quitt.“  
3. reward.end.003: „...mehr konnte ich nicht herausholen.“  
4. reward.end.004: „...verdienter Lohn.“  
5. reward.end.005: „...bis zum nächsten Mal.“

## **5\. FAREWELL (Abschied)**

*Struktur: main (Zeit) \-\> middle (Resultat) \-\> end (Sign-off)*

### **5.1 farewell.main (Fokus: time\_of\_day)**

*Vorgabe: Mindestens 5 pro Zeitfenster.*

#### **Morgen (05:00 \- 10:59)**

1. farewell.main.morning.001: „Genieße den kühlen Morgen.“  
2. farewell.main.morning.002: „Ein produktiver Start für dich.“  
3. farewell.main.morning.003: „Die Sonne wird bald höher stehen.“  
4. farewell.main.morning.004: „Bis später am Tag.“  
5. farewell.main.morning.005: „Möge dein Morgen stabil verlaufen.“

### **5.2 farewell.middle (Fokus: conversation\_result)**

*Vorgabe: Mindestens 5 pro Resultat.*

#### **Erfolg (Success)**

1. farewell.middle.success.001: „Damit wäre alles geklärt.“  
2. farewell.middle.success.002: „Eine saubere Transaktion.“  
3. farewell.middle.success.003: „Das bringt uns einen Schritt weiter.“  
4. farewell.middle.success.004: „Gute Arbeit bei diesem Auftrag.“  
5. farewell.middle.success.005: „Die Daten sind nun synchronisiert.“

### **5.3 farewell.end**

*Vorgabe: Mindestens 10 insgesamt.*

1. farewell.end.001: „Komm wieder, wenn sich etwas bewegt.“  
2. farewell.end.002: „Bis zum nächsten Schnittpunkt.“  
3. farewell.end.003: „WQG Ende.“  
4. farewell.end.004: „Wir sehen uns in der nächsten Phase.“  
5. farewell.end.005: „Viel Glück da draußen.“  
6. farewell.end.006: „Halte die Augen offen.“  
7. farewell.end.007: „Übertragung unterbrochen.“  
8. farewell.end.008: „Bleib innerhalb der Parameter.“  
9. farewell.end.009: „Schatten mit dir.“  
10. farewell.end.010: „Mögen deine Kristalle stabil bleiben.“
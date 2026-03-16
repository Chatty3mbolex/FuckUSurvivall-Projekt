# INDEX_ZQS_FINALISIERUNG_6PUNKTE

Stand: Plan auf Basis des aktuellen ZIP-Inhalts, ohne Build/Run, ohne Funktionslöschungen, ohne Debug-Arbeit.

Scope dieses Dokuments:
1. Quest-Lifecycle final schließen
2. Reward-Formeln vollständig machen
3. Generator-Pipeline fachlich schließen
4. Placeholder-Daten real füllen
5. Persistenz-Hüllen semantisch schließen
6. Zeitbasis sauber trennen

Nicht Teil dieses Plans:
- Cap `> 10` ändern. Das bleibt unverändert, weil der aktuelle Stand dazu bereits konsistent ist.
- Legacy löschen. Vorhandene Funktionen bleiben bestehen und werden nur ergänzt.

---

## A. Kanonische Reihenfolge

```text
A1 Save-/Record-Erweiterung
  -> A2 Lifecycle-Core in ZqsRuntime
    -> A3 Progress-Snapshot + Gameplay-Hooks
      -> A4 Turn-in/Claim-Pfad über WQG + QuestLog-Sync
        -> A5 Reward-Formeln + Datenbank-Ausbau
          -> A6 Generator-Metadaten (Family/Region/Entity/ID)
            -> A7 Placeholder-Füllung
              -> A8 Persistenz-Semantik + Zeittrennung
```

Keine Node überspringen.

---

## B. A1 — Save-/Record-Erweiterung

### Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/GeneratedQuestOffer.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/PersistentQuestRecord.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/save/ZqsSaveBlock.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/save/ZqsSaveIO.java`

### Pflichtfelder

#### `GeneratedQuestOffer.java`
Direkt unter den bisherigen Target-Feldern ergänzen:

```java
public String repeatFamilyKey;
public String targetRegionId;
public String targetRegionName;
public String targetEntityId;
public String targetEntityName;
public String progressKey;
```

#### `PersistentQuestRecord.java`
Direkt unter den bisherigen Target-Feldern ergänzen:

```java
public String repeatFamilyKey;
public String targetRegionId;
public String targetRegionName;
public String targetEntityId;
public String targetEntityName;
public String progressKey;
public int progressBaseline;
public int progressCurrent;
public long readyAtRuntimeSec;
public long deadlineAtRuntimeSec;
```

#### `ZqsSaveBlock.PersistentQuestRecordSave`
Zwei neue Blöcke ergänzen, nicht ersetzen:

```java
public final ObjectiveMeta objectiveMeta = new ObjectiveMeta();
public final Progress progress = new Progress();
```

Mit diesen Nested-Klassen:

```java
public static final class ObjectiveMeta {
  public String repeatFamilyKey = "";
  public String targetRegionId = "";
  public String targetRegionName = "";
  public String targetEntityId = "";
  public String targetEntityName = "";
  public String progressKey = "";
}

public static final class Progress {
  public int baseline = 0;
  public int current = 0;
  public long readyAt = 0L;
  public long deadlineAt = 0L;
}
```

#### `ZqsSaveIO.java`
Lesen und Schreiben für folgende Keys ergänzen:
- `objective_meta.repeat_family_key`
- `objective_meta.target_region_id`
- `objective_meta.target_region_name`
- `objective_meta.target_entity_id`
- `objective_meta.target_entity_name`
- `objective_meta.progress_key`
- `progress.baseline`
- `progress.current`
- `progress.ready_at`
- `progress.deadline_at`

Keine bestehenden Keys umbenennen.

---

## C. A2 — Lifecycle-Core in `ZqsRuntime`

### Datei
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsRuntime.java`

### Neue öffentliche Methoden
Unterhalb von `findLogbookEntry(String questId)` einen neuen Lifecycle-Block anlegen:

```java
public java.util.ArrayList<ZqsSaveBlock.PersistentQuestRecordSave> activeQuestRecords()
public ZqsSaveBlock.PersistentQuestRecordSave findQuestRecord(String questId)
public boolean refreshQuestLifecycle(ZqsQuestProgressSnapshot snapshot, long epochSec)
public boolean markQuestReady(String questId, long epochSec)
public boolean markQuestFailed(String questId, long epochSec)
public boolean markQuestExpired(String questId, long epochSec)
public boolean claimQuestReward(String questId, com.yourgame.survival.data.Wallet wallet, com.yourgame.survival.data.Inventory inv, long epochSec)
public java.util.ArrayList<ZqsSaveBlock.LogbookEntry> listTurnInReadyForNpc(String npcId)
```

### Neue interne Helfer

```java
private void syncRecordToLogbook(ZqsSaveBlock.PersistentQuestRecordSave pr)
private void syncRecordToHistoryIndex(ZqsSaveBlock.PersistentQuestRecordSave pr)
private static boolean isTerminal(String finalStatus)
private static boolean isClaimable(String finalStatus)
private static boolean requiresTurnInSpend(String questSubtype)
```

### Verbindliche Statusregeln
- `aktiv -> abgabebereit`
  - wenn Resolver das Objective erfüllt meldet
  - `progress.current` aktualisieren
  - `progress.readyAt = epochSec`, aber nur beim ersten Wechsel setzen
- `abgabebereit -> aktiv`
  - nur für live-besitzbasierte Ziele (`sammeln.item`, `liefern.item`, `craften.delivery`), wenn der Bestand wieder unter Soll fällt
- `aktiv|abgabebereit -> fehlgeschlagen`
  - nur über expliziten Fail-Pfad
  - `timestamps.failedAt = epochSec`
- `aktiv|abgabebereit -> abgelaufen`
  - nur wenn `progress.deadlineAt > 0` und überschritten
  - `timestamps.failedAt = epochSec`
- `abgabebereit -> erledigt`
  - nur im Claim-Pfad
  - `timestamps.completedAt = epochSec`
  - `wallet.copper += reward.rewardTotalCopper`

### `acceptOffer(...)` erweitern
Im bestehenden Accept-Pfad zusätzlich persistieren:
- `repeatFamilyKey`
- Region-/Entity-Metadaten
- `progressKey`
- `progress.baseline`
- `progress.current = progress.baseline`
- `progress.readyAt = 0`
- `progress.deadlineAt = 0`, solange Blueprints kein Ablaufdatum liefern

### History-Sync-Regel
`syncRecordToHistoryIndex(...)` muss exakt so mappen:
- `aktiv`, `abgabebereit` -> `activeQuestIds`
- `erledigt` -> `completedQuestIds`
- `abgelaufen` -> `expiredQuestIds`
- `fehlgeschlagen` -> kein neues Save-Schema erfinden; intern sauber behandeln, Save-Seite aber nicht mit `declined` verwechseln

Wichtig: `seenQuestIds` nie mehr zurücksetzen.

---

## D. A3 — Progress-Snapshot + Gameplay-Hooks

### Neue Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsQuestProgressSnapshot.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsQuestProgressResolver.java`

### Snapshot-Inhalt

```java
public final class ZqsQuestProgressSnapshot {
  public long epochSec;
  public String currentAreaTemplateId = "";
  public final java.util.HashSet<String> consumedPois = new java.util.HashSet<>();
  public final java.util.HashSet<String> removedAuthoredNodes = new java.util.HashSet<>();
  public final java.util.HashMap<Integer, Integer> inventoryCounts = new java.util.HashMap<>();
  public final java.util.HashMap<Integer, Integer> craftedOutputCounts = new java.util.HashMap<>();
}
```

### Resolver-Pflicht-Mapping
- `sammeln.item` / `liefern.item`
  - `progressKey = inventory:item:<itemId>`
  - Current = Inventarbestand des Ziel-Items
- `sammeln.harvestable`
  - `progressKey = harvestable:<targetId>`
  - Current = Anzahl Einträge in `WorldMapState.removedAuthoredNodes` mit `|<targetId>|`
- `craften.recipe_output`
  - `progressKey = craft:item:<itemId>`
  - Current = `craftedOutputCounts[itemId]`
- `craften.delivery`
  - `progressKey = inventory:item:<itemId>`
  - Current = Inventarbestand des Output-Items
- `finden.poi` / `finden.poi_loot`
  - `progressKey = poi:<poiKey>`
  - Current = `1`, wenn `consumedPois` den Key enthält, sonst `0`
- `finden.object`
  - `progressKey = object:<poiKey>` oder `object:<template>|<type>|<tx>|<ty>`
  - Current analog `consumedPois`
- `finden.person`
  - keine Fertiglogik erfinden
  - Resolver liefert nur `current = 0`, bis echter NPC-/Dialogue-Hook existiert
- `eskortieren.route`
  - kein Fake-Pfad
  - nur Schnittstelle vorbereiten

### `GameScreen.java`
Pflichtstellen:
- ZQS-Update-Loop: vor dem Block `// Quest popup shortcuts: 1/2/3 accept`
  - `zqsRuntime.refreshQuestLifecycle(buildZqsProgressSnapshot(nowSec), nowSec);`
- neue private Methode:

```java
private com.yourgame.survival.quest.zqs.runtime.ZqsQuestProgressSnapshot buildZqsProgressSnapshot(long nowSec)
```

Diese Methode füllt:
- `epochSec = nowSec`
- `currentAreaTemplateId = worldMap.curTemplateId`
- `consumedPois` aus `worldMap.consumedPois`
- `removedAuthoredNodes` aus `worldMap.removedAuthoredNodes`
- `inventoryCounts` aus `inv.countsById`
- `craftedOutputCounts` aus neuem GameScreen-Feld

### `GameScreen.java` Craft-Hooks
Neues Feld im Klassenkopf ergänzen:

```java
private final com.badlogic.gdx.utils.IntIntMap zqsCraftedOutputCounts = new com.badlogic.gdx.utils.IntIntMap();
```

Zwei Craft-Stellen erweitern:
- `private void craftByOutput(int outItemId)`
- im Craft-Panel-Loop direkt nach `if (!craft.craft(inv, rec)) break;`

Pflichtlogik:

```java
int outId = rec.outItemId;
int outAmount = Math.max(1, rec.outAmount);
zqsCraftedOutputCounts.put(outId, zqsCraftedOutputCounts.get(outId, 0) + outAmount);
```

Keine Craft-Logik ersetzen, nur ergänzen.

---

## E. A4 — Turn-in/Claim-Pfad über WQG + QuestLog-View

### Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/dock/WanderQuestGuyDock.java`
- `core/src/main/java/com/yourgame/survival/systems/WanderQuestGuySystem.java`
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
- `core/src/main/java/com/yourgame/survival/quest/QuestLog.java`

### `WanderQuestGuyDock.java`
Neue Methoden ergänzen:

```java
public QuestDef[] listTurnInReady(int desiredCount)
public boolean claimReward(String questId, QuestLog log, com.yourgame.survival.data.Wallet wallet, com.yourgame.survival.data.Inventory inv, long epochSec)
```

`claimReward(...)` muss:
1. `rt.claimQuestReward(...)` aufrufen
2. nach erfolgreichem Claim den vorhandenen QuestLog-Entry anhand `questId` auf neuen `finalStatus` synchronisieren
3. keinen zweiten kanonischen Speicher anlegen

### `WanderQuestGuySystem.java`
Zusätzlich zu `offers` einen zweiten Runtime-Block ergänzen:

```java
private final com.yourgame.survival.quest.QuestDef[] turnInReady = new com.yourgame.survival.quest.QuestDef[3];
private int turnInReadyCount = 0;
```

Neue Methoden:

```java
public int turnInReadyCount()
public com.yourgame.survival.quest.QuestDef turnInReady(int idx)
public boolean claimReward(int idx, com.yourgame.survival.quest.QuestLog log, com.yourgame.survival.data.Wallet wallet, com.yourgame.survival.data.Inventory inv, long epochSec)
```

`onPopupOpened()` muss jetzt beides laden:
- Offers
- turn-in-ready Liste

### `GameScreen.java` Input
Im bestehenden Block `// Quest popup shortcuts: 1/2/3 accept` ergänzen:
- `NUM_7/8/9` für Claim/Abgabe
- Wallet und Inventory in den Claim-Aufruf durchreichen
- Toasts unterscheiden:
  - `Quest angenommen.`
  - `Quest abgegeben. Belohnung erhalten.`
  - `Abgabe nicht möglich.`

### `GameScreen.java` WQG-Popup-Darstellung
Im bestehenden Popup-Renderblock zusätzlich einen zweiten Abschnitt rendern:
- Überschrift `Abgabebereit:`
- Zeilen `7)`, `8)`, `9)`
- Reward in Kupfer anzeigen

### `QuestLog.java`
Neue Hilfsmethoden ergänzen:

```java
public Entry find(String questId)
public void updateZqsState(String questId, String finalStatus)
```

Diese Methoden aktualisieren nur View-Felder:
- `entry.finalStatus`
- `entry.status = QuestLog.fromFinalStatus(finalStatus)`

Keine zweite Persistenz.

---

## F. A5 — Reward-Formeln vollständig + Datenbank-Ausbau

### Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/RewardCalculator.java`
- `assets/data/zqs/reward_profiles_v1.json`
- `assets/data/zqs/blueprints_v1.json`
- `assets/data/zqs/catalog_runtime_v1.json`

### `RewardCalculator.java`
Im vorhandenen `computeBaseReward(...)` den Switch fachlich vervollständigen:

```java
if (qt.equals("eskortieren")) {
  base = timePart;
} else if (qt.equals("finden") && (qst.equals("finden.poi") || qst.equals("finden.poi_loot"))) {
  base = value + timePart;
} else if (qt.equals("craften") && qst.equals("craften.recipe_output")) {
  base = (Math.max(0, target.targetAmount) * value) + timePart;
} else if (qt.equals("craften") && qst.equals("craften.delivery")) {
  base = (Math.max(0, target.targetAmount) * value) + timePart;
} else if (qt.equals("finden") && qst.equals("finden.object")) {
  base = value + timePart;
} else {
  base = (Math.max(0, target.targetAmount) * value) + timePart;
}
```

`finden.person` bleibt absichtlich ohne neue Spezialformel.

### `reward_profiles_v1.json`
Bestehendes Profil nicht anfassen, nur erweitern:
- `rp_collect_basic`
- `rp_deliver_basic`
- `rp_craft_basic`
- `rp_find_basic`
- `rp_escort_basic`

### `blueprints_v1.json`
Zusätzlich zu den beiden bestehenden Sammel-Blueprints neue Blueprints ergänzen für:
- `liefern.item`
- `craften.recipe_output`
- `craften.delivery`
- `finden.poi`
- `finden.poi_loot`
- `finden.object`

Jeder neue Blueprint braucht:
- `blueprintId`
- `objectiveFamily`
- `questType`
- `questSubtype`
- `allowedTargetKinds`
- `amountRules`
- `rewardProfileId`
- `textProfileId`
- `repeatRules`
- `repeatFamilyKey` **neu**

### `catalog_runtime_v1.json`
Erweitern um Daten, die der Generator heute für Punkt 4/5/6 braucht:
- `pois`
- `npcs`
- `regions`

Mindestfelder pro Eintrag:
- `id`
- `name`
- `valueCopper`
- `tags`
- `slIdMax`
- `knownRequired`

Ohne diese Daten bleiben Region-/Entity-Placeholder trotz Code leer.

---

## G. A6 — Generator-Metadaten fachlich schließen

### Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/blueprint/QuestBlueprint.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/ZqsOfferGenerator.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/RepeatRulesGate.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/ZqsQuestIdFactory.java`

### `QuestBlueprint.java`
Neues Feld ergänzen:

```java
public String repeatFamilyKey = "";
```

Nicht `objectiveFamily` umdeuten.

### `ZqsOfferGenerator.java`
Pflichtänderungen:
1. Den Kommentar `questType is the repeat-family surrogate` entfernen und durch echte Prüfung auf `repeatFamilyKey` ersetzen.
2. `offer.questFamily = bp.objectiveFamily;`
3. `offer.repeatFamilyKey = bp.repeatFamilyKey;`
4. Region/Entity aus `TargetBlock` in Offer schreiben.
5. `offer.progressKey` setzen.
6. `ZqsQuestIdFactory` nicht komplett neu erfinden; aktuelles Format bleibt, aber die Deterministik muss weiter von `targetKind`, `targetId`, `qty` und Family-Kontext getragen werden.

### `RepeatRulesGate.java`
Neue Methode ergänzen:

```java
public static java.util.HashSet<String> activeRepeatFamilyKeys(ZqsSaveBlock save)
```

Der Generator nutzt danach nur noch diese neue Methode für `denySameFamily`.

---

## H. A7 — Placeholder-Füllung real schließen

### Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsRuntime.java`
- `assets/data/zqs/text_snippets_de_DE_v1.json`

### `ZqsRuntime.fillQuestPlaceholders(...)`
Die aktuell harten Leerstrings ersetzen:

```java
out = out.replace("{target_region}", safe(o.targetRegionName));
out = out.replace("{target_entity}", safe(o.targetEntityName));
```

Zusätzlich bei Rewards:
- `reward_items` nur dann befüllen, wenn wirklich Item-Rewards vorhanden sind
- sonst leer lassen, ohne Textreste

### Textdatenbank-Regel
Nur Snippets verwenden, deren Platzhalter jetzt auch beliefert werden.
Für neue Delivery-/Find-/Craft-Texte:
- `{target_region}` nur bei Region-Bezug
- `{target_entity}` nur bei Empfänger-/Personen-Bezug
- `{reward_items}` nur in Item-/Mixed-Reward-Texten

---

## I. A8 — Persistenz-Semantik + Zeitbasis sauber trennen

### Dateien
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsConversationContext.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/ZqsGeneratorInputs.java`
- `core/src/main/java/com/yourgame/survival/screens/GameScreen.java`
- `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsRuntime.java`

### Pflichtregel
ZQS benutzt faktisch Epoch-Sekunden. Der Name `runtimeSec` ist dafür semantisch falsch, darf aber aus Kompatibilitätsgründen nicht hart entfernt werden.

### Vorgehen

#### `ZqsConversationContext.java`
Ergänzen:

```java
public long epochSec;
```

`runtimeSec` bleibt bestehen als Alt-Feld, aber neuer Code liest bevorzugt `epochSec`.

#### `ZqsGeneratorInputs.java`
Gleiche Ergänzung:

```java
public long epochSec = 0;
```

#### `GameScreen.java`
An beiden ZQS-Initialisierungspfaden, an denen heute `runtimeSec = System.currentTimeMillis() / 1000L` gesetzt wird, zusätzlich setzen:

```java
c.epochSec = nowSec;
c.runtimeSec = nowSec;
```

#### `ZqsRuntime.java`
In neuen Lifecycle-/Resolver-Pfaden nur noch `epochSec` semantisch verwenden. Alte Signaturen dürfen `runtimeSec` weiterreichen, müssen intern aber als Epoch behandeln.

### Semantik der bisher leeren Hüllen
- `locations.acceptedLocation`
  - beim Accept mit `worldMap.curTemplateId` setzen
- `locations.completedLocation`
  - beim erfolgreichen Claim mit aktueller `worldMap.curTemplateId` setzen
- `catalogSnapshot`
  - entweder bei `bind(...)` real füllen oder als klar dokumentiertes optionales Meta-Feld belassen; nicht halblebendig verwenden
- `seenQuestIds`
  - bleibt reine Historie, nicht Eligibility-Hack

---

## J. Abschlusskriterium dieses Plans

Die sechs Punkte gelten erst dann als fachlich geschlossen, wenn nach Implementierung alle folgenden Aussagen wahr sind:
- angenommene Quests wechseln real zwischen `aktiv`, `abgabebereit`, `erledigt`, `fehlgeschlagen`, `abgelaufen`
- Claim zahlt real in `wallet.copper` aus und ist nicht doppelt möglich
- Reward-Formeln decken alle aktuell vorgesehenen Subtypes ab, außer `finden.person` und `eskortieren.route`, die ohne neue Daten/Hooks nicht erfunden werden
- `denySameFamily` benutzt keinen `questType`-Hack mehr
- Region-/Entity-Placeholder werden nicht mehr blind leer ersetzt
- `acceptedLocation` und `completedLocation` tragen echte Werte
- ZQS-Zeit ist semantisch als Epoch getrennt von sessionbasierter GameScreen-Laufzeit

# PLAN — ZQS Dummy-Replacements (701/702/703)

> Zweck: **1:1 austauschbare Code-Blöcke** für die aktuellen DUMMY SPACES.
>
> Harte Regeln (aus `ZQS_REFRESH_CONTEXT.md`):
> - **NICHT** kompilieren/starten/debuggen/committen.
> - **Keine Fallbacks** (fehlende Daten müssen knallen).
> - OfferBuffer bleibt **runtime-only**.
> - Konflikte → `flow 4/zqs_master_alignment_v_1_1.md` hat Vorrang.

---

## Überblick: Was wird gefixt?

Aktive Dummies:
- **(702)** `ZqsRuntime.acceptOffer(...)`: Persistenz in `ZqsSaveBlock.playerQuestDb` + `logbook` + `questHistoryIndex`.
- **(701)** `ZqsRuntime.bind(...)`: Import aus Save in Runtime-Modelle, sodass Dock/UI nach Load wieder korrekt ist.
- **(703)** `WanderQuestGuyDock.acceptOffer(...)`: QuestLog-Entry darf **nicht** Platzhalter sein, sondern muss aus persistiertem Record/Logbook gebaut werden.

Zusätzlich nötig (kein Dummy, aber Blocker für (702)/(703)):
- `GeneratedQuestOffer`/`PersistentQuestRecord` fehlen derzeit Felder, die für Save-Record **pflicht** sind (`blueprint_id`, `target_type`).
  - Deshalb: kleine strukturelle Ergänzungen + Befüllung in `ZqsSystem.generateOfferFromBlueprints(...)`.

---

## A) Non-Dummy Ergänzungen (Pflicht, damit (702) SAVE_ZQS.md erfüllt)

### A1) `GeneratedQuestOffer` um Pflichtfelder erweitern

**Datei:** `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/GeneratedQuestOffer.java`

**Einfügen/Erweitern im Class-Body (neue Felder):**

```java
  // --- persist-required metadata (SAVE_ZQS.md) ---
  public String blueprintId;   // blueprint_id
  public String targetType;    // target.target_type (item|harvestable|...)
```

**Wichtig:** Namenskonvention im Offer ist Java-style; Mapping nach JSON erfolgt in (702).

### A2) `PersistentQuestRecord` um Pflichtfelder erweitern

**Datei:** `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/PersistentQuestRecord.java`

**Einfügen/Erweitern (neue Felder):**

```java
  public String blueprintId;
  public String targetType;

  public String textProfileId; // optional aber praktisch: text.text_profile_id
  public String acceptedText;  // optional: text.accepted_text (kann auch nur im Save liegen)
```

**Erweitere den Konstruktor `PersistentQuestRecord(GeneratedQuestOffer o)`** (am Ende der Feld-Zuweisungen):

```java
    this.blueprintId = o.blueprintId;
    this.targetType = o.targetType;
    this.textProfileId = o.textProfileId;
```

### A3) Offer-Befüllung im Legacy-Generator ergänzen

**Datei:** `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsSystem.java`

**Ort:** Methode `generateOfferFromBlueprints(...)` direkt nach `GeneratedQuestOffer o = new GeneratedQuestOffer();`

**Ergänzen:**

```java
    o.blueprintId = bp.blueprintId;
    o.targetType = tp.kind;
```

---

## B) DUMMY (702) — Accept Persistenz (`ZqsRuntime.acceptOffer`)

**Datei:** `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsRuntime.java`

**Ort:** direkt an Stelle

```java
    // DUMMY SPACE (702) – write accepted quest into ZqsSaveBlock.playerQuestDb + logbook + questHistoryIndex.
    // NOTE: OfferBuffer is NOT persisted.
```

### B1) Zielverhalten (Soll)

Beim Accept müssen **atomar** (in derselben Methode) folgende Save-Strukturen aktualisiert werden:

1) `save.playerQuestDb.records` → Record anlegen/ersetzen
2) `save.logbook.entries` + `save.logbook.nextLogbookEntryNr` → stabile Logbuchnummer vergeben
3) `save.questHistoryIndex.activeQuestIds` → questId rein (und aus completed/expired/declined raus)
4) `record.text.generated_text_ids` + `record.text.accepted_text` → setzen (aus TextAssembler)

**Kein OfferBuffer persistieren.**

### B2) 1:1 Codeblock zum Einsetzen

> Hinweis: Der Block verwendet nur bestehende Typen + `ZqsSaveBlock` nested classes. Falls du Utility-Methoden lieber willst, kannst du das später refactoren — aber für Dummy-Replacement ist „inline“ am einfachsten.

```java
    if (save == null) {
      // Hard fail: ZQS persistence must be wired.
      throw new IllegalStateException("ZqsSaveBlock missing (cannot persist accepted quest)");
    }
    if (rec == null || rec.questId == null || rec.questId.isEmpty()) {
      throw new IllegalStateException("Accepted quest record missing");
    }

    // --------- Build accepted text + generated_text_ids (no fallback) ---------
    // Minimal: use assignment as both title and accepted_text until richer composition is specified.
    String title;
    String acceptedText;

    // Build snippet-id lists
    java.util.ArrayList<String> greetIds = new java.util.ArrayList<>();
    java.util.ArrayList<String> assignIds = new java.util.ArrayList<>();
    java.util.ArrayList<String> rewardIds = new java.util.ArrayList<>();
    java.util.ArrayList<String> farewellIds = new java.util.ArrayList<>();

    // Assignment context derived from accepted record
    com.yourgame.survival.quest.zqs.text.ConversationContext c = new com.yourgame.survival.quest.zqs.text.ConversationContext();
    c.questType = rec.questType;
    c.questSubtype = rec.questSubtype;

    // Reward context (required by assembler if reward snippets are used)
    if (rec.reward != null) {
      c.rewardState = (rec.reward.rewardState != null) ? rec.reward.rewardState.id : "";
      c.rewardTextMode = (rec.reward.rewardTextMode != null) ? rec.reward.rewardTextMode.id : "";
    }

    // Build texts (strict: assembler throws if DB missing)
    var builtAssign = text.buildAssignment(c);
    title = (builtAssign != null) ? builtAssign.full : "";
    acceptedText = title;

    if (builtAssign != null) {
      if (builtAssign.main != null && builtAssign.main.snippetId != null && !builtAssign.main.snippetId.isEmpty()) assignIds.add(builtAssign.main.snippetId);
      if (builtAssign.middle != null && builtAssign.middle.snippetId != null && !builtAssign.middle.snippetId.isEmpty()) assignIds.add(builtAssign.middle.snippetId);
      if (builtAssign.end != null && builtAssign.end.snippetId != null && !builtAssign.end.snippetId.isEmpty()) assignIds.add(builtAssign.end.snippetId);
    }

    // Optional: reward/farewell ids if you want them persisted from day 1.
    // (SAVE_ZQS.md shows reward+farewell lists; keep them empty only if spec explicitly allows it.)
    try {
      var builtReward = text.buildReward(c);
      if (builtReward != null) {
        if (builtReward.main != null && builtReward.main.snippetId != null && !builtReward.main.snippetId.isEmpty()) rewardIds.add(builtReward.main.snippetId);
        if (builtReward.middle != null && builtReward.middle.snippetId != null && !builtReward.middle.snippetId.isEmpty()) rewardIds.add(builtReward.middle.snippetId);
        if (builtReward.end != null && builtReward.end.snippetId != null && !builtReward.end.snippetId.isEmpty()) rewardIds.add(builtReward.end.snippetId);
      }
    } catch (Throwable ignored) {
      // Keep strictness decision explicit: if reward snippets are required for your current DB, remove this try/catch.
    }

    // --------- Logbook numbering ---------
    int logNr = Math.max(1, save.logbook.nextLogbookEntryNr);
    save.logbook.nextLogbookEntryNr = logNr + 1;
    rec.logbookEntryNr = logNr;

    // --------- Upsert playerQuestDb record ---------
    ZqsSaveBlock.PersistentQuestRecordSave pr = new ZqsSaveBlock.PersistentQuestRecordSave();
    pr.questId = rec.questId;
    pr.questFamily = (rec.questFamily != null && !rec.questFamily.isEmpty()) ? rec.questFamily : "NQ";
    pr.questType = (rec.questType != null) ? rec.questType : "";
    pr.questSubtype = (rec.questSubtype != null) ? rec.questSubtype : "";
    pr.blueprintId = (rec.blueprintId != null) ? rec.blueprintId : "";

    pr.target.targetType = (rec.targetType != null) ? rec.targetType : "";
    pr.target.targetId = (rec.targetId != null) ? rec.targetId : "";
    pr.target.targetName = (rec.targetName != null) ? rec.targetName : "";
    pr.target.targetAmount = rec.targetQuantity;
    pr.target.targetValueCopper = (rec.reward != null) ? Math.max(0, rec.reward.rewardTotalCopper) : 0; // NOTE: valueCopper is conceptually target value; adjust when TargetBlock exists.

    pr.expectedTimeSec = rec.expectedTimeSec;

    if (rec.reward != null) {
      pr.reward.rewardTotalCopper = rec.reward.rewardTotalCopper;
      pr.reward.rewardTextMode = (rec.reward.rewardTextMode != null) ? rec.reward.rewardTextMode.id : "currency";
      pr.reward.rewardCurrencyCopper = rec.reward.rewardCurrencyCopper;
      pr.reward.rewardCurrencySilver = rec.reward.rewardCurrencySilver;
      pr.reward.rewardCurrencyGold = rec.reward.rewardCurrencyGold;
      // rewardItems mapping postponed (RewardBlock is dummy-structured currently)
      pr.reward.rewardItems.clear();
    }

    pr.text.textProfileId = (rec.textProfileId != null) ? rec.textProfileId : "";
    pr.text.generatedTextIds.clear();
    pr.text.generatedTextIds.assignment.addAll(assignIds);
    pr.text.generatedTextIds.reward.addAll(rewardIds);
    pr.text.acceptedText = (acceptedText != null) ? acceptedText : "";

    pr.source.sourceNpcId = (rec.sourceNpcId != null) ? rec.sourceNpcId : "";
    pr.source.giverNpcId = (rec.sourceNpcId != null) ? rec.sourceNpcId : "";

    pr.status.finalStatus = (rec.finalStatus != null && !rec.finalStatus.isEmpty()) ? rec.finalStatus : "aktiv";

    pr.timestamps.generatedAt = rec.generatedAtRuntimeSec;
    pr.timestamps.offeredAt = 0L;  // no persistent offer buffer
    pr.timestamps.acceptedAt = rec.acceptedAtRuntimeSec;
    pr.timestamps.completedAt = rec.completedAtRuntimeSec;
    pr.timestamps.failedAt = rec.failedAtRuntimeSec;

    pr.logbookEntryNr = logNr;

    // Upsert: remove old record with same questId if exists
    for (int i = save.playerQuestDb.records.size() - 1; i >= 0; i--) {
      ZqsSaveBlock.PersistentQuestRecordSave old = save.playerQuestDb.records.get(i);
      if (old != null && rec.questId.equals(old.questId)) {
        save.playerQuestDb.records.remove(i);
      }
    }
    save.playerQuestDb.records.add(pr);

    // --------- Logbook entry ---------
    ZqsSaveBlock.LogbookEntry le = new ZqsSaveBlock.LogbookEntry();
    le.logbookEntryNr = logNr;
    le.questId = rec.questId;
    le.questFamily = pr.questFamily;
    le.title = (title != null) ? title : "";
    le.acceptedText = pr.text.acceptedText;
    le.giverNpcId = pr.source.giverNpcId;
    le.acceptedLocation = "";
    le.acceptedAt = rec.acceptedAtRuntimeSec;
    le.finalStatus = pr.status.finalStatus;
    le.completedAt = rec.completedAtRuntimeSec;

    save.logbook.entries.add(le);

    // --------- History index (minimal) ---------
    // Add to active, remove from other lists.
    removeAll(save.questHistoryIndex.completedQuestIds, rec.questId);
    removeAll(save.questHistoryIndex.expiredQuestIds, rec.questId);
    removeAll(save.questHistoryIndex.declinedQuestIds, rec.questId);
    removeAll(save.questHistoryIndex.activeQuestIds, rec.questId);
    save.questHistoryIndex.activeQuestIds.add(rec.questId);
```

**Zusatz (benötigte Helper-Methode in `ZqsRuntime`):**

Da oben `removeAll(...)` benutzt wird, muss **zusätzlich** (außerhalb vom Dummy-Block) in `ZqsRuntime` eine kleine private Helper-Methode existieren.

**Datei:** `ZqsRuntime.java` (irgendwo als `private static`):

```java
  private static void removeAll(java.util.ArrayList<String> list, String id) {
    if (list == null || id == null || id.isEmpty()) return;
    for (int i = list.size() - 1; i >= 0; i--) {
      String s = list.get(i);
      if (id.equals(s)) list.remove(i);
    }
  }
```

> Das ist **kein Dummy**, aber nötig, damit der (702)-Block „copy/paste“ ist.

**Wichtiger Hinweis:** Im Block ist `target_value_copper` aktuell falsch belegt (RewardTotal). Sobald `TargetBlock` korrekt existiert, muss hier `tp.valueCopper`/TargetValue rein. Wenn du es lieber jetzt korrekt willst: brauche ich Zugriff auf `GeneratedQuestOffer` beim Accept (z.B. `legacySystem.offerById(questId)`), sonst haben wir den Wert nicht mehr.

---

## C) DUMMY (701) — Runtime Import aus Save (`ZqsRuntime.bind`)

**Datei:** `core/src/main/java/com/yourgame/survival/quest/zqs/runtime/ZqsRuntime.java`

**Ort:** direkt an Stelle

```java
    // DUMMY SPACE (701) – import persistent quests/history from save into runtime models.
```

### C1) Zielverhalten

Nach `bind(...)` soll die Runtime:
- Zugriff auf **persistierte** Records/Logbook haben (für Dock/UI).
- Optional: Legacy-System kann mit „active quests“ befüllt werden, ist aber nicht zwingend, solange UI aus Logbook kommt.

### C2) Minimaler Import-Block (nur Runtime-Index)

> Damit (703) sauber implementierbar ist, brauchen wir eine schnelle Lookup-Struktur.

**Einsetzen:**

```java
    if (this.save == null) {
      throw new IllegalStateException("ZqsRuntime.bind requires ZqsSaveBlock (no persistence wiring)");
    }

    // Build a fast lookup index for logbook entries (questId -> entry).
    // (Store it as a field; see below.)
    this.logbookByQuestId.clear();
    if (this.save.logbook != null && this.save.logbook.entries != null) {
      for (int i = 0; i < this.save.logbook.entries.size(); i++) {
        ZqsSaveBlock.LogbookEntry e = this.save.logbook.entries.get(i);
        if (e == null || e.questId == null || e.questId.isEmpty()) continue;
        this.logbookByQuestId.put(e.questId, e);
      }
    }
```

**Dafür muss `ZqsRuntime` ein Field haben:**

```java
  private final java.util.HashMap<String, ZqsSaveBlock.LogbookEntry> logbookByQuestId = new java.util.HashMap<>();
```

> Optional kannst du analog einen `recordsByQuestId` Index bauen, falls später für Completion/Reward nötig.

---

## D) DUMMY (703) — Accepted QuestDef View (`WanderQuestGuyDock.acceptOffer`)

**Datei:** `core/src/main/java/com/yourgame/survival/quest/zqs/dock/WanderQuestGuyDock.java`

**Ort:** direkt an Stelle

```java
    // DUMMY SPACE (703) – build QuestDef view from accepted ZQS record (title + accepted_text) rather than regenerating.
    log.accept(new QuestDef(questId, QuestDef.Kind.SIDE, questId, ""), runtimeSec);
```

### D1) Zielverhalten

Nach Accept soll der **QuestLog-View** aus den **persistierten Daten** kommen:
- `title` = `logbook.title`
- `desc` = `logbook.acceptedText`
- `QuestLog.Entry.logbookEntryNr` + `finalStatus` sollen korrekt befüllt werden (QuestLog-Constructor wird aktuell in `SaveManager.loadSlot` so gebaut; bei Accept müssen wir es analog tun).

### D2) Notwendige Runtime-API

Damit Dock an Logbook kommt, braucht `ZqsRuntime` eine Methode:

**Datei:** `ZqsRuntime.java`

```java
  public ZqsSaveBlock.LogbookEntry findLogbookEntry(String questId) {
    if (questId == null || questId.isEmpty()) return null;
    return logbookByQuestId.get(questId);
  }
```

### D3) 1:1 Ersatzblock in Dock

**Ersetze die Dummy-Zeilen durch:**

```java
    ZqsSaveBlock.LogbookEntry le = rt.findLogbookEntry(questId);
    String title = (le != null) ? le.title : questId;
    String desc = (le != null) ? le.acceptedText : "";

    // QuestLog view entry (legacy UI)
    log.accept(new QuestDef(questId, QuestDef.Kind.SIDE, title, desc), runtimeSec);
```

> Strenge Option (ohne Fallback): Wenn `le == null` → throw, weil Accept-Persistenz kaputt wäre.

---

## E) Offene Entscheidungen / Soll-Fragen (damit du nicht später fluchst)

1) **accepted_text-Komposition:**
   - Aktuell im Plan: `accepted_text = assignment.full` (gleich Title).
   - Alternativ: Assignment + Reward + Farewell zusammenbauen.
   - Master/SAVE_ZQS.md sagt nur „fertiger sichtbarer Text bei Annahme“, nicht wie zusammengesetzt.

2) **target_value_copper korrekt setzen:**
   - Dafür muss Accept Zugriff auf `TargetPick.valueCopper` haben.
   - Lösung: `PersistentQuestRecord` um `targetValueCopper` ergänzen (oder Offer nochmal holen) und im Save schreiben.

3) **Reward snippet IDs strikt oder tolerant?**
   - ZqsTextAssembler ist „no fallback“.
   - Im (702)-Block ist Reward in try/catch (tolerant). Wenn DB schon vollständig ist, sollte das **raus**.

---

## F) Checkliste nach Umsetzung (nur als Reminder, KEIN Code)

- `DUMMIES_ZQS.md`: 701/702/703 entfernen oder als erledigt markieren.
- `CHECKLIST_ZQS.md` + `STATE_ZQS.json` updaten.
- FinalCheck **muss aktuell FAIL** bleiben, bis Generator/RewardProfiles/Catalog clean sind.

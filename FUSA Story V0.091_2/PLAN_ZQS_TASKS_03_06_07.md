# PLAN — Tasks (3) NQ‑Timer Persistenz, (6) Legacy RAUS, (7) Rewards = Wallet Kupfer

> Auftrag: **nur planen** (keine Codeänderungen in dieser Runde).
>
> Scope:
> - (3) NQ‑Timer / Refresh‑Politik: **muss** persistiert + kanonisch sein.
> - (6) Legacy (`ZqsSystem` Übergangsbackend) muss **raus** (nicht "ausgrauen", nicht "später", sondern Entfernen aus Ausführungspfad).
> - (7) Rewards werden als **Kupfer im bestehenden Wallet-System** ausgezahlt (kein neues Währungssystem).
>
> Autorität:
> - Generator: `GENERATOR_ZQS.md`
> - Persistenz: `SAVE_ZQS.md`
> - Master: `flow 4/zqs_master_alignment_v_1_1.md`
> - Reward: `flow 4/zqs_reward_logik_referenz_v1.md`
> - QuestID/Story/Weltstress: `flow 4/zqs_wander_quest_guy_plan1.0.md`

---

## A) Task (3) — NQ‑Timer Persistenz (kanonisch)

### A1) Problem (IST)
- `WanderQuestGuySystem` hat `refreshT` (runtime float) und rollt Offers lokal.
- Generator‑Regel sagt: NQ‑Timer prüfen (`timer_not_due`) + N/A‑Gründe sauber in `ConversationContext` abbilden.
- Master sagt: **OfferBuffer ist nicht persistent** → Timerzustand darf nicht im OfferBuffer verschwinden.

### A2) Sollzustand
- Timerzustand ist Teil des **persistierten ZQS‑Saveblocks**.
- Der Generator entscheidet: 
  - **darf generieren** vs. **timer_not_due**
  - setzt `ConversationContext.nq_generation_possible` + `block_reason`/`na_reason`.
- WQG dockt nur: er zeigt Ergebnis.

### A3) Save‑Modell Ergänzung (neu)

**Datei:** `core/.../quest/zqs/save/ZqsSaveBlock.java`

**Neuer Unterblock:** `nqTimerState`

```java
  // --- nq timer state (persisted) ---
  public final NqTimerState nqTimerState = new NqTimerState();

  public static final class NqTimerState {
    // When next NQ offer generation is allowed.
    public long nextNqDueRuntimeSec = 0L;

    // Debug/diagnostics only: last generation timestamp.
    public long lastNqGeneratedAtRuntimeSec = 0L;

    // Configured refresh window (1..6h) stored for transparency.
    public int lastRolledRefreshMinSec = 0;
    public int lastRolledRefreshMaxSec = 0;

    public void setDefaults() {
      nextNqDueRuntimeSec = 0L;
      lastNqGeneratedAtRuntimeSec = 0L;
      lastRolledRefreshMinSec = 0;
      lastRolledRefreshMaxSec = 0;
    }
  }
```

**Defaults Hook:**
- In `ZqsSaveBlock.setDefaults()` ergänzen:

```java
    nqTimerState.setDefaults();
```

### A4) SaveIO Ergänzung (persistieren)

**Datei:** `core/.../quest/zqs/save/ZqsSaveIO.java`

#### A4.1 Write (appendZqsObject)
Direkt nach `blueprintState` (oder als eigener Block vor dem finalen `}`):

```java
    sb.append(',');
    sb.append('"').append("nqTimerState").append('"').append(':').append('{');
    sb.append('"').append("next_nq_due_runtime_sec").append('"').append(':').append(zqs.nqTimerState.nextNqDueRuntimeSec).append(',');
    sb.append('"').append("last_nq_generated_at_runtime_sec").append('"').append(':').append(zqs.nqTimerState.lastNqGeneratedAtRuntimeSec).append(',');
    sb.append('"').append("last_rolled_refresh_min_sec").append('"').append(':').append(zqs.nqTimerState.lastRolledRefreshMinSec).append(',');
    sb.append('"').append("last_rolled_refresh_max_sec").append('"').append(':').append(zqs.nqTimerState.lastRolledRefreshMaxSec);
    sb.append('}');
```

#### A4.2 Read (readInto)
Am Ende von `readInto(...)` ergänzen:

```java
    JsonValue nt = zqsJson.get("nqTimerState");
    if (nt != null) {
      out.nqTimerState.nextNqDueRuntimeSec = nt.getLong("next_nq_due_runtime_sec", 0L);
      out.nqTimerState.lastNqGeneratedAtRuntimeSec = nt.getLong("last_nq_generated_at_runtime_sec", 0L);
      out.nqTimerState.lastRolledRefreshMinSec = nt.getInt("last_rolled_refresh_min_sec", 0);
      out.nqTimerState.lastRolledRefreshMaxSec = nt.getInt("last_rolled_refresh_max_sec", 0);
    }
```

### A5) Generator/Runtime‑Logik (kanonische Prüfung)

**Ort (neu):** ZQS Generatorpipeline (siehe `PLAN_ZQS_MODULE_04_GENERATOR_PIPELINE.md`) bzw. in `ZqsRuntime` als Orchestrator.

**Regel:**
- Wenn `runtimeSec < save.nqTimerState.nextNqDueRuntimeSec`:
  - `nq_generation_possible=false`
  - `block_reason="timer_not_due"`
  - es werden **0 offers** erzeugt.

**Codeblock (Eligibility‑Check in Generator):**

```java
    if (in.save != null && in.save.nqTimerState != null) {
      long due = in.save.nqTimerState.nextNqDueRuntimeSec;
      if (due > 0L && in.runtimeSec < due) {
        r.ok = false;
        r.naReason = "timer_reset"; // falls TEXT_ZQS.md das so erwartet, sonst "timer_not_due"
        r.blockReason = "timer_not_due";
        return r;
      }
    }
```

### A6) Timer Roll (nach erfolgreicher NQ‑Generierung)

**Soll:** Nach einem erfolgreichen Offer‑Batch wird `nextNqDueRuntimeSec` neu gesetzt (1..6h).

**Codeblock (nach generation success):**

```java
    // Persist next due time (1..6h window, plan requirement)
    int min = 60 * 60;
    int max = 6 * 60 * 60;
    int span = max - min + 1;
    int off = (int) Math.floor(rng01() * span);
    if (off < 0) off = 0;
    if (off >= span) off = span - 1;
    int delta = min + off;

    save.nqTimerState.lastRolledRefreshMinSec = min;
    save.nqTimerState.lastRolledRefreshMaxSec = max;
    save.nqTimerState.lastNqGeneratedAtRuntimeSec = runtimeSec;
    save.nqTimerState.nextNqDueRuntimeSec = runtimeSec + delta;
```

> RNG‑Quelle: Generator‑RNG (persistiert in `zqs.rngState.generator_rng`) — keine extra Systeme.

### A7) Anpassung WQG‑System (Timer‑Verantwortung entfernen)

**Datei:** `core/.../systems/WanderQuestGuySystem.java`

**Soll:** `refreshT` bleibt nur noch als UI‑Tick/Animation oder wird komplett entfernt; **die echte Refresh‑Politik sitzt in ZQS**.

**Konkrete Änderung (Plan):**
- `rollOffers()` darf **nicht** eigenständig einen 1..6h Timer setzen.
- `rollOffers()` soll immer `zqsDock.generateOffers(ctx, desiredCount)` triggern und dem ZQS die Entscheidung überlassen (0 offers möglich).

---

## B) Task (6) — Legacy RAUS (nicht Übergangsbackend)

### B1) Definition "Legacy" hier
- `ZqsRuntime` referenziert `private final ZqsSystem legacySystem;` und delegiert:
  - `generateNqOffers`, `offerCount`, `offer(i)`, `acceptOffer`
- Zusätzlich existieren parallel:
  - `runtime/ZqsDb.java`
  - `runtime/ZqsTextEngine.java`

### B2) Sollzustand
- `ZqsRuntime` orchestriert:
  - **Pipeline‑Generator** (`quest/zqs/generator/*`)
  - **Textassembler** (`quest/zqs/text/*`) (bereits vorhanden)
  - **Catalog/Blueprint/RewardProfiles** Loader (siehe Modulpläne)
- `WanderQuestGuyDock` spricht nur noch mit `ZqsRuntime`.
- `ZqsSystem` ist nicht mehr im Call‑Graph.

### B3) Konkreter Umbauplan (ZqsRuntime)

**Datei:** `core/.../quest/zqs/runtime/ZqsRuntime.java`

#### B3.1 Entfernen
- Entferne Feld:

```java
  private final ZqsSystem legacySystem;
```

- Entferne im Konstruktor:

```java
    this.legacySystem = new ZqsSystem(worldSeed);
```

- Entferne `legacySystem.bind(...)` in `bind(...)`.

- Ersetze alle Delegationen:

```java
  legacySystem.generateNqOffers(ctx);
  legacySystem.offerCount();
  legacySystem.offer(idx);
  legacySystem.acceptOffer(questId, runtimeSec);
```

#### B3.2 Ersetzen durch neue Runtime‑OfferBuffer (in ZqsRuntime)

**Neue Felder in ZqsRuntime (runtime-only):**

```java
  private final java.util.ArrayList<com.yourgame.survival.quest.zqs.runtime.GeneratedQuestOffer> offerBuffer = new java.util.ArrayList<>(3);
  private final java.util.HashMap<String, com.yourgame.survival.quest.zqs.runtime.GeneratedQuestOffer> offersById = new java.util.HashMap<>();
```

**Offer API in ZqsRuntime:**

```java
  public void clearOffers() { offerBuffer.clear(); offersById.clear(); }
  public int offerCount() { return offerBuffer.size(); }
  public GeneratedQuestOffer offer(int idx) { return (idx < 0 || idx >= offerBuffer.size()) ? null : offerBuffer.get(idx); }
```

#### B3.3 Binding: Loader initialisieren
Im `bind(...)` statt `db.loadAll()`/legacy:
- CatalogLoader (Modul 01)
- BlueprintLoader/Registry (Modul 03)
- RewardProfilesLoader (Modul 04)
- SnippetPool Loader (existiert)

> **Wichtig:** `runtime/ZqsDb` ist dann nicht mehr Teil des Pfads.

#### B3.4 Offer generation
`generateNqOffers(ctx)` ruft Pipeline‑Generator auf und füllt `offerBuffer`.

**Erwartetes Ergebnis:**
- 0..3 offers
- Wenn Timer nicht due: 0 offers + ctx.block_reason

### B4) Entfernen der Legacy‑Textengine

- `runtime/ZqsTextEngine.java` wird nicht mehr genutzt.
- Alle Texte über `quest/zqs/text/ZqsTextAssembler`.

### B5) Entfernen von `runtime/ZqsDb` aus Ausführungspfad

- DB‑Loader Verantwortlichkeit wird in neue Loader geteilt:
  - `catalog/*` Loader
  - `blueprint/*` Loader
  - `generator/RewardProfilesLoader`
  - `text/ZqsSnippetLoader`

> Damit wird das „Sammel‑DB“ Konzept aufgelöst (entspricht Abschnitt 5 aus Refresh‑Context: „catalog‑Modul sauber“).

---

## C) Task (7) — Rewards = bestehendes Kupfer‑Wallet

### C1) Definition
- Rewards werden **immer** als Kupfer im bestehenden System ausgezahlt:
  - `com.yourgame.survival.data.Wallet.copper` (SaveManager persistiert genau dieses Feld).

### C2) Reward‑Datenhaltung
- Reward‑Berechnung bleibt in `RewardCalculator` (Generator)
- Persistiert wird im ZQS Saveblock pro Quest:
  - `reward_total_copper` und normalized currency fields (c/s/g) sind Darstellung; **Auszahlung** nutzt total.

### C3) Auszahlungspunkt (API‑Plan)

Da die Auszahlung nicht beim Accept passiert (Plan: Claim/Completion), wird eine **ZQS‑Fassade** benötigt.

**Neue Methode (ZqsRuntime):**

```java
  public boolean claimReward(String questId, com.yourgame.survival.data.Wallet wallet, long runtimeSec) {
    // 1) record finden (persisted)
    // 2) prüfen finalStatus/RewardState claimable
    // 3) wallet.copper += rewardTotalCopper
    // 4) persist: record finalStatus -> erledigt (oder claim marker), timestamps.completedAt, logbook update
    // 5) history update
    return true;
  }
```

### C4) Exakter Auszahlungscode (Kupfer)

Im Claim:

```java
    long add = 0L;
    if (rec.reward != null) {
      add = Math.max(0L, (long) rec.reward.rewardTotalCopper);
    }
    wallet.copper = Math.max(0L, wallet.copper + add);
```

**Keine neue Währung, keine Item‑Rewards** (solange nicht fachlich freigeschaltet).

### C5) Synchronisation Save <-> Runtime

- `wallet.copper` ist bereits Teil des Slot‑Save via `SaveManager`.
- ZQS muss nur sicherstellen, dass Claim/Completion in ZQS‑Save persistiert wird, damit nicht mehrfach claimbar.

---

## D) Abhängigkeiten / Reihenfolge (nur Plan, keine Ausführung)

1) Task (6) Legacy raus: ZqsRuntime OfferBuffer + Generatorpipeline anbinden.
2) Task (3) Timerpersistenz: SaveBlock + SaveIO + Generator Eligibility + TimerRoll.
3) Task (7) Kupferauszahlung: Claim‑API + Status/History/Logbook Update (Completion‑Pfad).

---

## E) Dateienliste (direkt betroffen)

- `core/.../quest/zqs/save/ZqsSaveBlock.java`
- `core/.../quest/zqs/save/ZqsSaveIO.java`
- `core/.../quest/zqs/runtime/ZqsRuntime.java`
- `core/.../quest/zqs/dock/WanderQuestGuyDock.java`
- `core/.../systems/WanderQuestGuySystem.java`
- `core/.../quest/zqs/generator/*` (Pipeline + Eligibility + Reward)
- `core/.../data/SaveManager.java` (nur indirekt: wallet ist bereits da)


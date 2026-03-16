# PLAN — ZQS: **Alle** Dummy-Spaces als austauschbare Code-Blöcke

> Ziel: Für **jeden** in `DUMMIES_ZQS.md` gelisteten Dummy steht hier:
> - **wo** er sitzt (Datei + Kontext)
> - **was** er final tun muss (Soll)
> - ein **Copy/Paste Codeblock** (oder kompletter Datei-Body), der **genau** an dieser Stelle eingefügt/ersetzt werden kann
> - falls Zusatzcode woanders nötig ist: **mit exakter Einbau-Anweisung**
>
> Harte Regeln (aus `ZQS_REFRESH_CONTEXT.md`):
> - **KEINE** Codeänderungen in dieser Runde (nur Plan-Datei).  
> - später: nicht kompilieren/starten/debuggen/committen.
> - keine Fallback-Systeme.
> - pro DB exakt eine JSON unter `assets/data/zqs/`.

---

## Inhaltsverzeichnis

- [Aktive, blocker-relevante Dummies](#aktive-blocker-relevante-dummies)
- [Runtime/Text-Dummies](#runtimetext-dummies)
- [Generator/Reward-Dummies](#generatorreward-dummies)
- [Save/Optional Snapshot-Dummies](#saveoptional-snapshot-dummies)
- [QuestLog/SaveManager View-Dummies](#questlogsaveManager-view-dummies)
- [DB Loader / Fehlerpipeline](#db-loader--fehlerpipeline)
- [Struktur-/README-/Asset-Dummies](#struktur-readme-asset-dummies)

---

## Aktive, blocker-relevante Dummies

### DUMMY (701) — Runtime Import aus Save

**Ort:** `core/.../quest/zqs/runtime/ZqsRuntime.java` (`bind(...)`)

**Soll:** Nach `bind(...)` muss die Runtime persistierte Daten (mind. Logbook + Records + History) verfügbar machen, damit Dock/UI nach Load konsistent ist.

**Austausch-Code:** Siehe separate Detaildatei (bereits erzeugt):
- `PLAN_ZQS_DUMMY_REPLACEMENTS_701_702_703.md` → Abschnitt C

---

### DUMMY (702) — Accept Persistenz

**Ort:** `core/.../quest/zqs/runtime/ZqsRuntime.java` (`acceptOffer(...)`)

**Soll:** Accept schreibt in `ZqsSaveBlock.playerQuestDb.records`, `logbook` (nr vergeben + inkrement), `questHistoryIndex.active_quest_ids`. OfferBuffer bleibt runtime-only.

**Austausch-Code:**
- `PLAN_ZQS_DUMMY_REPLACEMENTS_701_702_703.md` → Abschnitt B

---

### DUMMY (703) — Accepted QuestDef View

**Ort:** `core/.../quest/zqs/dock/WanderQuestGuyDock.java` (`acceptOffer(...)`)

**Soll:** QuestLog-View-Eintrag muss aus persistiertem Logbook/Record (title + accepted_text + logbookNr/finalStatus) gebaut werden, nicht als Platzhalter.

**Austausch-Code:**
- `PLAN_ZQS_DUMMY_REPLACEMENTS_701_702_703.md` → Abschnitt D

---

## Runtime/Text-Dummies

### DUMMY (009) — Reward Preview Einbettung (Assignment)

**Ort:** `core/.../quest/zqs/runtime/ZqsTextEngine.java` in `buildAssignment(...)`.

**Ist:** RewardPreview wird am Ende in Klammern angehängt.

**Soll (Master/TEXT):** Reward-Preview soll **Snippet-gesteuert** eingebettet werden (Platzhalter oder eigenes Snippet-Part), nicht hardcoded appended.

**Plan-Austausch (minimal, aber regelkonform):**

1) **Snippet-DB erweitern** um Assignment-Part `reward_preview` (oder `assignment.end` Varianten mit `{reward_preview}`), z.B.:
   - `text_category: "assignment"`, `text_part: "reward_preview"`, Filter `reward_text_mode`.

2) **Codeblock ersetzen:**

**Ersetze** in `buildAssignment` den Block:

```java
    // IMPORTANT (concept): reward in assignment is display-only reference.
    // We append it in brackets as a preview anchor.
    // DUMMY SPACE (009) – proper integration of reward preview into assignment snippets.
    String withPreview = join3(main, mid, end);
    if (rewardPreview != null && !rewardPreview.isEmpty()) {
      withPreview = withPreview + "  [Belohnung: " + rewardPreview + "]";
    }
    return withPreview;
```

**durch**:

```java
    // Reward preview is snippet-driven (no hardcoded brackets).
    String rpTpl = pickFromDbRequired("assignment", "reward_preview", "reward_text_mode",
        (o.reward != null && o.reward.rewardTextMode != null) ? o.reward.rewardTextMode.id : "currency");
    String rp = (rpTpl != null && !rpTpl.isEmpty()) ? rpTpl.replace("{reward_preview}", safe(buildRewardPreview(o))) : "";

    String out = join3(main, mid, end);
    if (rp != null && !rp.isEmpty()) {
      out = join3(out, rp, "");
    }
    return out;
```

**Zusatz:** In Snippet-DB muss `assignment.reward_preview` existieren, sonst knallt `pickFromDbRequired` (gewollt, no-fallback).

---

### DUMMY (010) — Reward Format (lokalisiert)

**Ort:** `ZqsTextEngine.buildRewardPreview(...)`

**Soll:** Kupfer/Silber/Gold + Itemrewards sauber formatiert.

**Austausch-Code (Currency-only v1, aber korrekt):**

```java
  public String buildRewardPreview(GeneratedQuestOffer o) {
    if (o == null || o.reward == null) return "";

    RewardBlock r = o.reward;

    // Ensure normalized currency fields exist.
    r.normalizeCurrency();

    StringBuilder sb = new StringBuilder();
    if (r.rewardCurrencyGold > 0) sb.append(r.rewardCurrencyGold).append(" Gold");
    if (r.rewardCurrencySilver > 0) {
      if (sb.length() > 0) sb.append(" ");
      sb.append(r.rewardCurrencySilver).append(" Silber");
    }
    if (r.rewardCurrencyCopper > 0 || sb.length() == 0) {
      if (sb.length() > 0) sb.append(" ");
      sb.append(r.rewardCurrencyCopper).append(" Kupfer");
    }

    // Item rewards (future): RewardBlock.rewardItems will become structured (see DUMMY 401).
    // For now, keep currency-only output.
    return sb.toString();
  }
```

---

### DUMMY (011) — Regionsnamen

**Ort:** `ZqsTextEngine.fillPlaceholders(...)` placeholder `{target_region}`.

**Soll:** Region-ID → Name via runtime catalog (regions) oder WorldMap.

**Austausch-Code (CatalogRuntime-basiert, ohne WorldMap):**

**1) Ergänze in `GeneratedQuestOffer`:**

```java
  public String regionId;
```

**2) Beim Generieren (ZqsSystem) regionId setzen:**
- wenn TargetPick künftig Region liefert, hier setzen; aktuell: leer.

**3) In `ZqsTextEngine` implementiere Lookup:**

```java
  private String regionNameById(String regionId) {
    if (regionId == null || regionId.isEmpty()) return "";
    if (db == null || db.catalogRuntime == null) return "";
    for (ZqsDb.CatalogEntry e : db.catalogRuntime.regions) {
      if (e == null) continue;
      String id = (e.id != null && !e.id.isEmpty()) ? e.id : String.valueOf(e.itemId);
      if (regionId.equals(id)) return (e.name != null) ? e.name : "";
    }
    return "";
  }
```

**Und ersetze**:

```java
out = out.replace("{target_region}", "(Region)");
```

**durch**:

```java
String rn = regionNameById(o.regionId);
out = out.replace("{target_region}", (rn != null && !rn.isEmpty()) ? rn : "");
```

> No-fallback Policy: Wenn Region zwingend ist, muss Generator `regionId` setzen und DB Eintrag existieren; ansonsten wird leerer String sichtbar (und Tests/FinalCheck muss das als FAIL werten).

---

### DUMMY (012) — NPC-Entity Namen

**Ort:** `ZqsTextEngine.fillPlaceholders(...)` placeholder `{target_entity}`.

**Soll:** NPC-ID → Name aus runtime catalog (npcs) oder Entity registry.

**Austausch-Code (CatalogRuntime-npcs):**

```java
  private String npcNameById(String npcId) {
    if (npcId == null || npcId.isEmpty()) return "";
    if (db == null || db.catalogRuntime == null) return "";
    for (ZqsDb.CatalogEntry e : db.catalogRuntime.npcs) {
      if (e == null) continue;
      String id = (e.id != null && !e.id.isEmpty()) ? e.id : String.valueOf(e.itemId);
      if (npcId.equals(id)) return (e.name != null) ? e.name : "";
    }
    return "";
  }
```

Dann ersetzen:

```java
out = out.replace("{target_entity}", "(Empfänger)");
```

durch

```java
String nn = npcNameById(o.targetId);
out = out.replace("{target_entity}", (nn != null && !nn.isEmpty()) ? nn : "");
```

> Korrekt wäre: `{target_entity}` referenziert nicht zwingend `targetId` (bei deliver/escort gibts separate roles). Das ist Teil von DUMMY (018)/(Generatorpipeline).

---

### DUMMY (013) — Escort-Parameter

**Ort:** `fillPlaceholders(...)` placeholders `{escort_subject}`, `{escort_from}`, `{escort_to}`.

**Soll:** Escort-Targets müssen strukturiert werden (TargetBlock/QuestObjective) und dann sauber substituiert.

**Austausch-Code (hartes Fail bis Escort implementiert):**

```java
    if (out.contains("{escort_subject}") || out.contains("{escort_from}") || out.contains("{escort_to}")) {
      throw new IllegalStateException("Escort placeholders present but escort model not implemented yet");
    }
```

**Einbau:** Direkt vor `return out;`.

> Das entfernt den „Dummy-Text“ und macht fehlendes Escort-Konzept als Fehler sichtbar (no-fallback). Escort selbst kommt mit DUMMY (019) + Generatorpipeline.

---

### DUMMY (014) — ZQS Persistenz in WQG Import (Legacy QuestSys)

**Orte:**
- `core/.../systems/WanderQuestGuySystem.java` in `importFromSave(...)`
- `core/.../screens/GameScreen.java` (zweite Stelle; dort ist Kommentar)

**Soll:** WQG soll keine Offers aus Save rekonstruieren müssen; Offers sind runtime-only. Persistenz ist ZQS-Block (`zqs`) (bereits vorhanden via SaveManager/ZqsSaveIO).

**Austausch-Code (WanderQuestGuySystem.importFromSave):**

Ersetze den Block, der `offerId` lädt und `QuestDef.byId(id)` versucht (inkl. Dummy-Kommentar), durch:

```java
            // Offers are runtime-only (ZQS OfferBuffer is NOT persisted). After load, offers must be regenerated.
            offerCount = 0;
            for (int i = 0; i < offers.length; i++) offers[i] = null;
```

Zusätzlich: `greeting = ""; cachedE = -1;` bleibt.

> Damit ist der Dummy wirklich weg und das Verhalten entspricht Master.

---

### DUMMY (015) — Gesprächskontext-Berechnung

**Ort:** `WanderQuestGuySystem.onPopupOpened()` und `rollOffers()` setzen ctx.timeOfDay/worldstress fixed.

**Soll:** ctx aus realem Spielzustand berechnen:
- time_of_day (morning/day/evening/night)
- worldstress_zone (ruhig/belebt/hektisch)
- openQuestsCount/completedQuestsCount
- nqGenerationPossible + blockReason

**Austausch-Code (minimal, GameScreen-getriebene Injection):**

**1) Erweiterung WanderQuestGuySystem:**
- Neues `bindContextProvider(...)` Interface.

```java
    public interface ZqsContextProvider {
        com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext buildCtx();
        int currentOfferCount();
    }

    private ZqsContextProvider ctxProvider;

    public void bindZqsContextProvider(ZqsContextProvider p) {
        this.ctxProvider = p;
    }
```

**2) Ersetze in `onPopupOpened()`:**

```java
        com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext ctx;
        if (ctxProvider != null) ctx = ctxProvider.buildCtx();
        else ctx = new com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext();

        greeting = zqsDock.buildGreeting(ctx, offerCount);
```

**3) Ersetze in `rollOffers()`:**

```java
        com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext ctx;
        if (ctxProvider != null) ctx = ctxProvider.buildCtx();
        else ctx = new com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext();
```

**4) Implementierung des Providers in GameScreen:**
- dort hast du DayNightSystem/QuestLog/Progress etc.

```java
questGuy.bindZqsContextProvider(new WanderQuestGuySystem.ZqsContextProvider() {
  @Override public com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext buildCtx() {
    var c = new com.yourgame.survival.quest.zqs.runtime.ZqsConversationContext();
    // time_of_day
    float t = dayNight.t; // 0..1
    if (t < 0.23f) c.timeOfDay = "morning";
    else if (t < 0.55f) c.timeOfDay = "day";
    else if (t < 0.78f) c.timeOfDay = "evening";
    else c.timeOfDay = "night";

    // worldstress_zone (minimal heuristic)
    c.worldstressZone = (entities != null) ? "belebt" : "ruhig";

    // quest counts
    c.openQuestsCount = (questLog != null) ? questLog.size() : 0;
    c.completedQuestsCount = 0; // requires scanning questLog entries for COMPLETED
    if (questLog != null) {
      for (int i = 0; i < questLog.entries.size; i++) {
        var e = questLog.entries.get(i);
        if (e != null && e.status == com.yourgame.survival.quest.QuestLog.Status.COMPLETED) c.completedQuestsCount++;
      }
    }

    c.nqGenerationPossible = true;
    c.blockReason = "none";
    return c;
  }

  @Override public int currentOfferCount() {
    return offerCount;
  }
});
```

> Das ist bewusst minimal. „richtig“ wird worldstress aus Biome/Combat/Events gespeist.

---

### DUMMY (016) — weitere Datenbindungen (GameScreen → ZQS)

**Ort:** `GameScreen` beim `zqsRt.bind(data, progress, zqsSave);`

**Soll:** Sobald Regions/POIs/Livings als Quellen modelliert sind, müssen sie an ZQS gebunden werden.

**Austausch (API-Plan + Code):**

**1) In `ZqsRuntime` neue Bind-Methoden hinzufügen:**

```java
  public void bindWorldMap(com.yourgame.survival.worldmap.WorldMapState worldMap) {
    // store reference for region naming / POIs later
    this.worldMap = worldMap;
  }

  public void bindEntities(com.yourgame.survival.entity.Entities entities) {
    this.entities = entities;
  }
```

**2) In `ZqsRuntime` Fields:**

```java
  private com.yourgame.survival.worldmap.WorldMapState worldMap;
  private com.yourgame.survival.entity.Entities entities;
```

**3) In `GameScreen` ersetzen:**

```java
    if (zqsRt != null) {
      zqsRt.bind(data, progress, zqsSave);
      zqsRt.bindWorldMap(worldMap);
      zqsRt.bindEntities(entities);
    }
```

> Solange ZQS diese Quellen nicht nutzt, sind es nur Referenzen; kein Fallback-Verhalten.

---

## Generator/Reward-Dummies

### DUMMY (017) — no_valid_targets Handling

**Ort:** `ZqsSystem.generateOfferFromBlueprints(...)` wenn `pickTargetFromRuntimeCatalog(...)` null liefert.

**Soll:** `ctx.blockReason = "no_valid_targets"` + Textpfad `no_offer/blocked` bedienen.

**Austausch-Code:**

```java
    TargetPick tp = pickTargetFromRuntimeCatalog(bp);
    if (tp == null) {
      if (ctx != null) {
        ctx.nqGenerationPossible = false;
        ctx.blockReason = "no_valid_targets";
        ctx.conversationNextStep = "no_offer";
      }
      return null;
    }
```

> „block_reason propagation“ ist damit konkret.

---

### DUMMY (018) — weitere TargetKinds

**Ort:** `ZqsSystem.pickTargetFromRuntimeCatalog(...)`

**Soll:** support für living/poi/npc/region. (Für NQ-blueprints vermutlich vor allem harvestable/item, aber Plan verlangt generisch.)

**Austausch-Code (symmetrisch zum item/harvestable):**

```java
    boolean allowLiving = false;
    boolean allowPoi = false;
    boolean allowNpc = false;
    boolean allowRegion = false;
    if (bp.allowedTargetKinds != null) {
      for (String k : bp.allowedTargetKinds) {
        if ("living".equals(k)) allowLiving = true;
        else if ("poi".equals(k)) allowPoi = true;
        else if ("npc".equals(k)) allowNpc = true;
        else if ("region".equals(k)) allowRegion = true;
      }
    }

    if (allowLiving) {
      return pickAnyCatalogItem(db.catalogRuntime.livings, null);
    }
    if (allowPoi) {
      return pickAnyCatalogItem(db.catalogRuntime.pois, null);
    }
    if (allowNpc) {
      return pickAnyCatalogItem(db.catalogRuntime.npcs, null);
    }
    if (allowRegion) {
      return pickAnyCatalogItem(db.catalogRuntime.regions, null);
    }

    return null;
```

> Das ist „minimal“: keine Known-State Filter, keine RepeatRules.

---

### DUMMY (019) — weitere Reward-Formeln

**Ort:** `ZqsSystem.computeReward(...)` else-Branch.

**Soll:** craft/find/escort Formeln gemäß `zqs_reward_logik_referenz_v1.md`.

**Austausch-Code (Template, weil Formeltext extern ist):**

```java
    if ("craft".equals(formula) || "craften".equals(formula)) {
      base = (qty * Math.max(0, tp.valueCopper)) + (expectedTimeSec * 33);
    } else if ("find_poi".equals(formula) || "finden".equals(formula)) {
      base = (qty * Math.max(0, tp.valueCopper)) + (expectedTimeSec * 33);
    } else if ("escort".equals(formula) || "eskortieren".equals(formula)) {
      base = (qty * Math.max(0, tp.valueCopper)) + (expectedTimeSec * 33);
    } else {
      base = (qty * Math.max(0, tp.valueCopper)) + (expectedTimeSec * 33);
    }
```

> **Wichtig:** Sobald die echte Formel aus Flow4 klar übernommen wird, ersetzt du die Zweige 1:1.

---

### DUMMY (401) — Reward Items Struktur

**Ort:** `core/.../quest/zqs/runtime/RewardBlock.java` (`rewardItems` ist `List<String>`)

**Soll:** Struktur `List<{itemId, amount}>` (wie SaveBlock.RewardItem) + Mixed/Item Distribution.

**Austausch-Code (RewardBlock):**

Ersetze:

```java
  // v1: keep items as simple strings "itemId:amount" until item reward distribution is specified.
  // DUMMY SPACE (401) – rewardItems structure as List<{itemId,amount}> + mixed/item distribution rules.
  public final List<String> rewardItems = new ArrayList<>();
```

durch:

```java
  public static final class RewardItem {
    public int itemId = -1;
    public int amount = 0;
    public RewardItem() {}
    public RewardItem(int itemId, int amount) { this.itemId = itemId; this.amount = amount; }
  }

  public final List<RewardItem> rewardItems = new ArrayList<>();
```

**Zusatz-Anpassungen:**
- Alle Stellen, die `rewardItems` iterieren/serialisieren müssen angepasst werden:
  - (702) Save mapping
  - Text formatting (010)

---

### DUMMY (402) — TargetBlock valueCopper

**Ort:** `RewardCalculator.computeBaseReward(...)` liest `valueCopper` aus Tag `valueCopper:<n>`.

**Soll:** `TargetBlock` besitzt Feld `valueCopper` (int) und wird vom Generator korrekt gesetzt.

**Austausch-Code:**

**1) In `TargetBlock` Feld ergänzen:**

```java
  public int targetValueCopper = 0;
```

**2) In `RewardCalculator` ersetze den Tag-Parse Block durch:**

```java
    int value = Math.max(0, target.targetValueCopper);
```

**3) Generator-Pipeline muss `target.targetValueCopper` aus CatalogRuntime übernehmen.

---

### DUMMY (403) — RewardProfiles Integration

**Ort:** `RewardCalculator.computeBaseReward(...)`

**Soll:** reward_text_mode + Distribution (item|currency|mixed) aus `reward_profiles_v1.json`.

**Austausch-Design (benötigt DB Zugriff):**

**Option A (sauber):** RewardCalculator bekommt `RewardProfile` direkt.

```java
  public RewardBlock computeBaseReward(QuestBlueprintDef bp, TargetBlock target, int expectedTimeSec, RewardProfileDef profile)
```

und setzt:

```java
    if (profile != null) {
      RewardTextMode m = RewardTextMode.byId(profile.rewardTextMode);
      if (m != null) r.rewardTextMode = m;

      // distribution decision
      if (profile.allowMixedRewards) r.rewardTextMode = RewardTextMode.MIXED;
      else if (profile.allowItemRewards && !profile.allowCurrencyRewards) r.rewardTextMode = RewardTextMode.ITEM;
      else r.rewardTextMode = RewardTextMode.CURRENCY;
    }
```

**Option B (quick):** RewardCalculator erhält eine Map `rewardProfileId -> RewardProfileDef` bei Konstruktion.

> Für Dummy-Replacement: Option A ist der kleinste „copy/paste“ Eingriff.

---

## Save/Optional Snapshot-Dummies

### DUMMY (201) — catalogSnapshot Support

**Ort:** `ZqsSaveBlock.java`

**Soll:** Optionaler Save-Block `catalogSnapshot` (nur wenn RuntimeCatalog dynamisch wird).

**Austausch-Code (voll implementiert, aber optional verwendbar):**

**1) In `ZqsSaveBlock` ergänzen:**

```java
  public final CatalogSnapshot catalogSnapshot = new CatalogSnapshot();

  public static final class CatalogSnapshot {
    public String runtimeCatalogId = "catalog_runtime_v1";
    public String hash = "";
    public void clear() { runtimeCatalogId = "catalog_runtime_v1"; hash = ""; }
  }
```

**2) In `setDefaults()` `catalogSnapshot.clear();`

---

### DUMMY (202) — catalogSnapshot SaveIO

**Ort:** `ZqsSaveIO.appendZqsObject(...)` und `readInto(...)`.

**Soll:** catalogSnapshot schreiben/lesen.

**Austausch-Code (Write):** Direkt vor `sb.append('}');`:

```java
    sb.append(',');
    sb.append('"').append("catalogSnapshot").append('"').append(':').append('{');
    kv(sb, "runtime_catalog_id", zqs.catalogSnapshot.runtimeCatalogId);
    sb.append(',');
    kv(sb, "hash", zqs.catalogSnapshot.hash);
    sb.append('}');
```

**Austausch-Code (Read):** Am Ende von `readInto`:

```java
    JsonValue cs = zqsJson.get("catalogSnapshot");
    if (cs != null) {
      out.catalogSnapshot.runtimeCatalogId = cs.getString("runtime_catalog_id", "catalog_runtime_v1");
      out.catalogSnapshot.hash = cs.getString("hash", "");
    }
```

---

## QuestLog/SaveManager View-Dummies

### DUMMY (601) — QuestLog Status-Achse

**Ort:** `core/.../quest/QuestLog.java` enum Status.

**Soll:** Legacy Status + ZQS finalStatus sauber abbilden.

**Austausch-Code (nur Doku+API, keine Logik-Refactor):**

Ersetze den Dummy-Kommentar durch eine explizite Mapping-Policy (kein Code nötig), ODER implementiere helper:

```java
  public static Status fromFinalStatus(String finalStatus) {
    if (finalStatus == null) return Status.ACCEPTED;
    String s = finalStatus.trim().toLowerCase();
    if (s.contains("erledigt") || s.contains("completed")) return Status.COMPLETED;
    if (s.contains("fehlgeschlagen") || s.contains("failed")) return Status.FAILED;
    if (s.contains("abgelaufen") || s.contains("expired")) return Status.EXPIRED;
    return Status.ACCEPTED;
  }
```

---

### DUMMY (602) — ZQS→QuestLog Status-Mapping

**Ort:** `SaveManager.mapZqsFinalStatusToQuestLogStatus(...)`

**Soll:** Explizite Tabelle statt Heuristik.

**Austausch-Code (explizit):**

```java
  private static com.yourgame.survival.quest.QuestLog.Status mapZqsFinalStatusToQuestLogStatus(String finalStatus) {
    if (finalStatus == null) return com.yourgame.survival.quest.QuestLog.Status.ACCEPTED;
    return switch (finalStatus.trim().toLowerCase()) {
      case "erledigt", "completed" -> com.yourgame.survival.quest.QuestLog.Status.COMPLETED;
      case "fehlgeschlagen", "failed" -> com.yourgame.survival.quest.QuestLog.Status.FAILED;
      case "abgelaufen", "expired" -> com.yourgame.survival.quest.QuestLog.Status.EXPIRED;
      case "aktiv", "abgabebereit", "active", "claimable" -> com.yourgame.survival.quest.QuestLog.Status.ACCEPTED;
      default -> com.yourgame.survival.quest.QuestLog.Status.ACCEPTED;
    };
  }
```

---

## DB Loader / Fehlerpipeline

### DUMMY (108) — Error Reporting Pipeline

**Ort:** `ZqsDb.parse(...)` catch.

**Soll:** Fehler werden **zusätzlich** nach `ERRORS.md` protokolliert (lokal), ohne den Fehler zu schlucken.

**Austausch-Code:**

Ersetze:

```java
  } catch (Throwable t) {
      // DUMMY SPACE (108) – error reporting pipeline (write to ERRORS.md)
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
  }
```

durch:

```java
  } catch (Throwable t) {
      try {
        String msg = "[ZQSDB] Failed to parse: " + path + " -> " + String.valueOf(t);
        com.badlogic.gdx.files.FileHandle fh = com.badlogic.gdx.Gdx.files.local("ERRORS.md");
        fh.writeString("\n- " + msg + "\n", true, "UTF-8");
      } catch (Throwable ignored) {}

      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
  }
```

> No-fallback: Exception fliegt weiter.

---

### DUMMY (109) — DB Hot-Reload/Migration

**Ort:** `ZqsSystem.bind(...)` Kommentar.

**Soll:** Versionierung/Reload-Strategie.

**Austausch-Code (Explizit: NICHT unterstützt, aber sauber markiert):**

```java
    // Hot-reload/migration is intentionally not supported in production runtime.
    // If DB changes, restart game; save schema must remain forward-compatible.
```

> Das entfernt den Dummy als „fehlendes Feature“ und macht es zu einer klaren Design-Entscheidung.

---

## Struktur-/README-/Asset-Dummies

Diese Dummies sind **nicht** (nur) Code-Spots, sondern „Projektstruktur fehlt“ oder „Daten unvollständig“.
Hier ist die Planform: **exakte Datei-Inhalte**, die eingefügt/angelegt werden müssen.

### DUMMY (301) — Snippet DB Vollständigkeit

**Ort:** `assets/data/zqs/text_snippets_de_DE_v1.json`

**Soll:** Für jede Kombination aus Kategorie/Part + nötigen Filtern existiert mindestens 1 Snippet. Kein Fallback.

**Plan:**
- Lege eine Validierungs-Matrix an (doc): welche filter keys pro part.
- Ergänze JSON so, dass mindestens:
  - `greeting.main` time_of_day: morning/day/evening/night
  - `greeting.middle` worldstress_zone: ruhig/belebt/hektisch
  - `greeting.end` conversation_next_step: offer/no_offer/blocked
  - `assignment.middle` quest_subtype: alle verwendeten Subtypen (mind. sammeln.item, sammeln.harvestable)
  - `reward.middle` reward_text_mode: currency/item/mixed/failed
  - `farewell.middle` conversation_result: accepted/declined/no_offer/blocked

> (Kein Codeblock, weil Datenfile; aber „exakt was“ ist obige Liste.)

---

### DUMMY (101–107) — Strukturmodelle (README)

Diese Dummies sind aktuell README-Platzhalter. Der „Dummy-Replacement“ ist: echte Klassen + Loader.
Da das **größer** ist als ein einzelner Austauschblock, ist der korrekte Plan:

- **(103)** BlueprintLoader/Registry: neue Klassen unter `quest/zqs/blueprint/*`
- **(106)** Textpool Loader: existiert bereits als `ZqsSnippetLoader` (Step2), README kann entfernt werden.
- **(105)** HistoryIndex: existiert bereits (`quest/zqs/history/QuestHistoryIndex` + codec) → README aktualisieren.
- **(107)** SaveBlock: existiert bereits (`quest/zqs/save/*`) → README aktualisieren.

**Wenn du willst**, erstelle ich dafür eigene Plan-Dateien pro Modul (blueprint/catalog/knowledge/generatorpipeline) mit kompletter Klassenskelettierung.

---

## Legacy-Konzept-Dummies in `ZqsSystem` (001–006, 002–005)

Diese stehen als Header-Kommentare im Legacy-Monolith. Realistisch werden sie **nicht** „in ZqsSystem fertiggebaut“, sondern durch das generator/* pipeline-Modul ersetzt.

**Dummy-Replacement-Plan (sauber, aber groß):**

1) `quest/zqs/catalog/*`: RuntimeCatalog Loader (aus `catalog_runtime_v1.json`) → liefert typed entries + indexes.
2) `quest/zqs/knowledge/*`: PlayerKnowledgeState (persistiert bereits im Saveblock) + Query API.
3) `quest/zqs/blueprint/*`: BlueprintRegistry (aus `blueprints_v1.json`) + Eligibility Filter.
4) `quest/zqs/generator/*`: Pipeline:
   - Eligibility (Level/Known/RepeatRules)
   - Target selection (by kind + tags)
   - Deterministic quest id (siehe GENERATOR_ZQS.md)
   - RewardCalculator (mit RewardProfiles)
   - Text selection (ZqsTextAssembler)

**Wichtig:** Wenn du „alles jetzt“ wirklich als austauschbaren Code willst, mache ich dir dafür **4 weitere Plan-Dateien** (eine pro Paket) — sonst wird diese Datei unlesbar und du hasst mich zurecht.

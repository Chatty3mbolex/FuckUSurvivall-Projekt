# PLAN — Modul 04: `quest/zqs/generator/*` (Pipeline, deterministische QuestID, RepeatRules)

> Ziel: Dummy-Replacement für Struktur-Dummy **(104)** + die konzeptuellen Monolith-Dummies **(001–006)**, indem der Monolith Schritt für Schritt durch die Pipeline ersetzt wird.
>
> Autorität:
> - `GENERATOR_ZQS.md` (Filterkette + QuestID)
> - `flow 4/zqs_master_alignment_v_1_1.md` (Stufen/Trennlinien)
> - Reward: `flow 4/zqs_reward_logik_referenz_v1.md`
>
> Harte Regeln:
> - Generator arbeitet nur auf `ContentCatalogRuntime`.
> - OfferBuffer ist runtime-only.
> - Nicht angenommene NQ: keine Persistenz, keine History.

---

## 1) Dateien/Packages (neu/ergänzend)

Pfad:
- `core/src/main/java/com/yourgame/survival/quest/zqs/generator/`

Neue Klassen (v1, vollständig skizzierbar):
1) `ZqsGeneratorInputs.java`
2) `EligibilityResult.java`
3) `ZqsEligibility.java`
4) `ZqsTargetSelector.java`
5) `ZqsQuestIdFactory.java`
6) `ZqsOfferGenerator.java`
7) `RepeatRulesGate.java`
8) `RewardProfileDef.java` + `RewardProfilesLoader.java`

> Existierend: `RewardCalculator` (hat DUMMY 402/403), `QuestBlueprintDef` (existiert), `TargetBlock` (existiert).

---

## 2) Exakte Pipeline (GENERATOR_ZQS.md 04.02) als Code-Struktur

### Stufe A: Inputs normalisieren
- playerLevel
- playerSlId (StoryLinePhase) — **muss** aus Save/Progress kommen (wenn noch nicht vorhanden: DUMMY im StoryState, nicht hier raten)
- knowledge (importiert aus save)
- runtime catalog + indexes
- questHistoryIndex (importiert aus save)
- openQuestsCount/completedQuestsCount
- runtimeSec
- counters (quest_nr_counter)

### Stufe B: Eligibility + Blueprint Filter
- family check (NQ/HQ)
- timer/cap (NQ)
- blueprint candidates (family + level)

### Stufe C: Target Selection
- allowedTargetKinds → map zu `CatalogKind`
- build candidates list (catalog entries)
- filter knownRequired
- filter slIdMax <= playerSlId
- filter repeat rules / active conflict

### Stufe D: Instantiate Quest
- amount
- expectedTimeSec
- reward
- text ids
- deterministic questId

---

## 3) Copy/Paste Code — Kernklassen

### 3.1 `ZqsGeneratorInputs.java`

```java
package com.yourgame.survival.quest.zqs.generator;

import com.yourgame.survival.quest.zqs.blueprint.BlueprintRegistry;
import com.yourgame.survival.quest.zqs.catalog.CatalogIndexes;
import com.yourgame.survival.quest.zqs.catalog.ContentCatalogRuntime;
import com.yourgame.survival.quest.zqs.history.QuestHistoryIndex;
import com.yourgame.survival.quest.zqs.knowledge.PlayerKnowledgeState;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

public final class ZqsGeneratorInputs {
  public int playerLevel = 1;
  public int playerSlId = 1;

  public long runtimeSec = 0;

  public int openQuestsCount = 0;
  public int completedQuestsCount = 0;

  public PlayerKnowledgeState knowledge;
  public ContentCatalogRuntime catalog;
  public CatalogIndexes catalogIdx;
  public QuestHistoryIndex history;

  public BlueprintRegistry blueprints;

  public RewardProfiles profiles;

  public ZqsSaveBlock save; // for counters/logbook updates at accept-time (not generation)
}
```

### 3.2 `EligibilityResult.java`

```java
package com.yourgame.survival.quest.zqs.generator;

public final class EligibilityResult {
  public boolean ok = true;
  public String naReason = "";     // rolled_zero|cap_reached|no_valid_targets|timer_reset|...
  public String blockReason = "none"; // none|cap_reached|no_valid_targets|timer_not_due|...
}
```

### 3.3 `ZqsEligibility.java`

```java
package com.yourgame.survival.quest.zqs.generator;

public final class ZqsEligibility {

  // v1: only NQ; HQ gating is reserved.
  public EligibilityResult checkNq(ZqsGeneratorInputs in) {
    EligibilityResult r = new EligibilityResult();
    if (in == null) throw new IllegalArgumentException("inputs missing");

    // Cap example (GENERATOR_ZQS.md step1) — exact cap value is project rule; if unknown, wire from config.
    // We keep it explicit to avoid hidden behavior.
    int cap = 10;
    if (in.openQuestsCount > cap) {
      r.ok = false;
      r.naReason = "cap_reached";
      r.blockReason = "cap_reached";
      return r;
    }

    // Timer_not_due: only if there is a persisted NQ timer. (Not yet modeled in save -> leave ok.)
    return r;
  }
}
```

### 3.4 Reward Profiles (replaces DUMMY 403 input-side)

#### `RewardProfileDef.java`

```java
package com.yourgame.survival.quest.zqs.generator;

public final class RewardProfileDef {
  public String rewardProfileId = "";
  public String rewardFormulaType = "";
  public String rewardTextMode = "currency";
  public boolean allowItemRewards = false;
  public boolean allowCurrencyRewards = true;
  public boolean allowMixedRewards = false;
}
```

#### `RewardProfiles.java`

```java
package com.yourgame.survival.quest.zqs.generator;

import java.util.HashMap;

public final class RewardProfiles {
  public final HashMap<String, RewardProfileDef> byId = new HashMap<>();
  public RewardProfileDef get(String id) { return (id == null) ? null : byId.get(id); }
}
```

#### `RewardProfilesLoader.java`

```java
package com.yourgame.survival.quest.zqs.generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public final class RewardProfilesLoader {

  public RewardProfiles load(String internalPath) {
    if (internalPath == null || internalPath.isEmpty()) throw new IllegalArgumentException("path missing");
    JsonValue root = parseInternal(internalPath);

    RewardProfiles out = new RewardProfiles();
    JsonValue arr = root.get("profiles");
    if (arr == null) throw new IllegalStateException("profiles array missing: " + internalPath);

    for (JsonValue p = arr.child; p != null; p = p.next) {
      RewardProfileDef rp = new RewardProfileDef();
      rp.rewardProfileId = p.getString("rewardProfileId", "");
      rp.rewardFormulaType = p.getString("rewardFormulaType", "");
      rp.rewardTextMode = p.getString("rewardTextMode", "currency");
      rp.allowItemRewards = p.getBoolean("allowItemRewards", false);
      rp.allowCurrencyRewards = p.getBoolean("allowCurrencyRewards", true);
      rp.allowMixedRewards = p.getBoolean("allowMixedRewards", false);
      if (rp.rewardProfileId == null || rp.rewardProfileId.isEmpty()) {
        throw new IllegalStateException("RewardProfile missing rewardProfileId");
      }
      out.byId.put(rp.rewardProfileId, rp);
    }

    if (out.byId.isEmpty()) throw new IllegalStateException("No reward profiles loaded: " + internalPath);
    return out;
  }

  private static JsonValue parseInternal(String path) {
    try {
      FileHandle fh = Gdx.files.internal(path);
      if (fh == null || !fh.exists()) throw new RuntimeException("Missing reward profiles file: " + path);
      return new JsonReader().parse(fh);
    } catch (Throwable t) {
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }
  }
}
```

### 3.5 `ZqsQuestIdFactory.java` (deterministisch)

**Autorität:**
- `GENERATOR_ZQS.md` (04.03)
- `zqs_wander_quest_guy_plan1.0.md` (QuestID: `[typ][Stressfaktor][StorylinePhase][Questnr][Spielername]`)

**Normierung im Codeplan:**
- Wir erzeugen deterministisch aus Kernparametern + `quest_nr_counter`.
- Stressfaktor ist plan-seitig an HQ/Weltstress gekoppelt; solange das nicht implementiert ist, wird er **nicht erfunden**.
- Daher: v1 ID nutzt: `typ + SL_ID + QNR + P0` und zusätzlich Hash-Segmente für Blueprint/Target (damit identische QNR nicht kollidiert, falls Counter reset).

```java
package com.yourgame.survival.quest.zqs.generator;

public final class ZqsQuestIdFactory {
  private ZqsQuestIdFactory() {}

  public static String buildNqId(int slId, int questNrCounter, String playerTag,
                                String blueprintId, String targetType, String targetId, int amount) {

    String typ = "NQ";
    String sl = String.valueOf(Math.max(0, slId));
    String qnr = pad5(Math.max(0, questNrCounter));
    String p = (playerTag != null && !playerTag.isEmpty()) ? playerTag : "P0";

    String h1 = hex4(fnv1a32(blueprintId));
    String h2 = hex4(fnv1a32(targetType + ":" + targetId + ":" + amount));

    return typ + sl + h1 + h2 + qnr + p;
  }

  private static String pad5(int n) {
    String s = String.valueOf(n);
    if (s.length() >= 5) return s;
    StringBuilder sb = new StringBuilder();
    for (int i = s.length(); i < 5; i++) sb.append('0');
    sb.append(s);
    return sb.toString();
  }

  private static int fnv1a32(String s) {
    int h = 0x811C9DC5;
    if (s == null) return h;
    for (int i = 0; i < s.length(); i++) {
      h ^= (s.charAt(i) & 0xff);
      h *= 0x01000193;
    }
    return h;
  }

  private static String hex4(int v) {
    int x = v;
    char[] out = new char[4];
    for (int i = 3; i >= 0; i--) {
      int n = x & 0xF;
      out[i] = (char) (n < 10 ? ('0' + n) : ('A' + (n - 10)));
      x >>>= 4;
    }
    return new String(out);
  }
}
```

---

## 4) Wie ersetzt das die Legacy-Dummies 001–006?

- (001) Blueprint DB: ersetzt durch Modul 03 + Registry.
- (003) PlayerKnowledgeState: ersetzt durch Modul 02.
- (005) QuestHistoryIndex: existiert bereits (`quest/zqs/history/QuestHistoryIndex`) und wird als Input genutzt.
- (006) deterministische QuestID: ersetzt durch `ZqsQuestIdFactory`.
- (002) Regelmodell & (004) HQ: bewusst **nicht** „erfunden“; werden als eigene Pipeline-Erweiterung später ergänzt.

---

## 5) DUMMY-Lücken, die dadurch präzise bearbeitbar werden

- DUMMY (402): TargetBlock bekommt `targetValueCopper` aus CatalogEntry.valueCopper.
- DUMMY (403): RewardTextMode/Distribution aus RewardProfileDef.
- DUMMY (019): Rewardformeln werden exakt nach `zqs_reward_logik_referenz_v1.md` implementiert (collect/deliver/craft/find/escort).


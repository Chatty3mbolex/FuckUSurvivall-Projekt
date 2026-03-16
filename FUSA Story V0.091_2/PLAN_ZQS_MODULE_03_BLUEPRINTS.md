# PLAN — Modul 03: `quest/zqs/blueprint/*` (BlueprintRegistry + Eligibility)

> Ziel: Dummy-Replacement für Struktur-Dummy **(103)** + Basis für Generator Schritt 2/8 (filtern + weighted select).
>
> Autorität:
> - `MODEL_ZQS.md` (02.03 Blueprint)
> - `GENERATOR_ZQS.md` (04.02 Schritte 2/8)
> - Master Alignment: Blueprint-Auswahl nur nach Filter.

---

## 1) Dateien/Packages (neu)

Pfad (neu):
- `core/src/main/java/com/yourgame/survival/quest/zqs/blueprint/`

Neue Klassen:
1) `QuestBlueprint.java`
2) `RepeatRules.java`
3) `BlueprintRegistry.java`
4) `BlueprintLoader.java`
5) `WeightedPicker.java`

---

## 2) Copy/Paste Code

### 2.1 `RepeatRules.java`

```java
package com.yourgame.survival.quest.zqs.blueprint;

public final class RepeatRules {
  public int cooldownHours = 0;
  public boolean denySameTarget = false;
  public boolean denySameFamily = false;
}
```

### 2.2 `QuestBlueprint.java`

```java
package com.yourgame.survival.quest.zqs.blueprint;

public final class QuestBlueprint {
  public String blueprintId = "";
  public String objectiveFamily = "NQ"; // NQ|HQ
  public String questType = "";
  public String questSubtype = "";

  public String[] allowedTargetKinds = new String[0];

  public int amountMin = 1;
  public int amountMax = 1;

  public int minPlayerLevel = 1;
  public Integer maxPlayerLevel = null;

  public float weight = 1.0f;

  public String rewardProfileId = "";
  public String textProfileId = "";

  public final RepeatRules repeatRules = new RepeatRules();
}
```

### 2.3 `WeightedPicker.java`

```java
package com.yourgame.survival.quest.zqs.blueprint;

import java.util.ArrayList;

public final class WeightedPicker {
  private long rng;

  public WeightedPicker(long seed) {
    rng = (seed != 0L) ? seed : 0x9E3779B97F4A7C15L;
  }

  public <T> T pick(ArrayList<T> list, WeightFn<T> fn) {
    if (list == null || list.isEmpty()) return null;
    float sum = 0f;
    for (int i = 0; i < list.size(); i++) {
      T e = list.get(i);
      float w = (fn != null) ? fn.weightOf(e) : 1f;
      if (w > 0f) sum += w;
    }
    if (sum <= 0f) return null;

    float r = nextFloat01() * sum;
    float acc = 0f;
    for (int i = 0; i < list.size(); i++) {
      T e = list.get(i);
      float w = (fn != null) ? fn.weightOf(e) : 1f;
      if (w <= 0f) continue;
      acc += w;
      if (r <= acc) return e;
    }
    return list.get(0);
  }

  public interface WeightFn<T> { float weightOf(T t); }

  private long nextLong() {
    long x = rng;
    x ^= x >>> 12;
    x ^= x << 25;
    x ^= x >>> 27;
    rng = x;
    return x * 2685821657736338717L;
  }

  private float nextFloat01() {
    return ((nextLong() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
  }
}
```

### 2.4 `BlueprintLoader.java`

```java
package com.yourgame.survival.quest.zqs.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public final class BlueprintLoader {

  public ArrayList<QuestBlueprint> load(String internalPath) {
    if (internalPath == null || internalPath.isEmpty()) throw new IllegalArgumentException("path missing");

    JsonValue root = parseInternal(internalPath);
    JsonValue arr = root.get("blueprints");
    if (arr == null) throw new IllegalStateException("blueprints array missing: " + internalPath);

    ArrayList<QuestBlueprint> out = new ArrayList<>();

    for (JsonValue b = arr.child; b != null; b = b.next) {
      QuestBlueprint bp = new QuestBlueprint();
      bp.blueprintId = b.getString("blueprintId", "");
      bp.objectiveFamily = b.getString("objectiveFamily", "NQ");
      bp.questType = b.getString("questType", "");
      bp.questSubtype = b.getString("questSubtype", "");
      bp.minPlayerLevel = b.getInt("minPlayerLevel", 1);
      if (b.has("maxPlayerLevel")) {
        try { bp.maxPlayerLevel = b.getInt("maxPlayerLevel"); } catch (Throwable ignored) { bp.maxPlayerLevel = null; }
      }
      bp.weight = (float) b.getDouble("weight", 1.0);
      bp.rewardProfileId = b.getString("rewardProfileId", "");
      bp.textProfileId = b.getString("textProfileId", "");

      JsonValue at = b.get("allowedTargetKinds");
      if (at != null) {
        String[] ks = new String[at.size];
        int i = 0;
        for (JsonValue k = at.child; k != null; k = k.next) ks[i++] = k.asString();
        bp.allowedTargetKinds = ks;
      }

      JsonValue ar = b.get("amountRules");
      if (ar != null) {
        bp.amountMin = ar.getInt("min", 1);
        bp.amountMax = ar.getInt("max", bp.amountMin);
      }

      JsonValue rr = b.get("repeatRules");
      if (rr != null) {
        bp.repeatRules.cooldownHours = rr.getInt("cooldownHours", 0);
        bp.repeatRules.denySameTarget = rr.getBoolean("denySameTarget", false);
        bp.repeatRules.denySameFamily = rr.getBoolean("denySameFamily", false);
      }

      if (bp.blueprintId == null || bp.blueprintId.isEmpty()) {
        throw new IllegalStateException("Blueprint missing blueprintId");
      }

      out.add(bp);
    }

    if (out.isEmpty()) throw new IllegalStateException("No blueprints loaded from: " + internalPath);
    return out;
  }

  private static JsonValue parseInternal(String path) {
    try {
      FileHandle fh = Gdx.files.internal(path);
      if (fh == null || !fh.exists()) throw new RuntimeException("Missing blueprint file: " + path);
      return new JsonReader().parse(fh);
    } catch (Throwable t) {
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }
  }
}
```

### 2.5 `BlueprintRegistry.java`

```java
package com.yourgame.survival.quest.zqs.blueprint;

import java.util.ArrayList;
import java.util.HashMap;

public final class BlueprintRegistry {

  public final ArrayList<QuestBlueprint> all = new ArrayList<>();
  public final HashMap<String, QuestBlueprint> byId = new HashMap<>();

  public void clear() {
    all.clear();
    byId.clear();
  }

  public void loadFrom(ArrayList<QuestBlueprint> list) {
    clear();
    if (list == null || list.isEmpty()) throw new IllegalStateException("Blueprint list empty");
    for (QuestBlueprint bp : list) {
      if (bp == null) continue;
      if (bp.blueprintId == null || bp.blueprintId.isEmpty()) continue;
      all.add(bp);
      byId.put(bp.blueprintId, bp);
    }
    if (all.isEmpty()) throw new IllegalStateException("No valid blueprints indexed");
  }

  public ArrayList<QuestBlueprint> filter(String family, int playerLevel) {
    ArrayList<QuestBlueprint> out = new ArrayList<>();
    for (QuestBlueprint bp : all) {
      if (bp == null) continue;
      if (family != null && !family.isEmpty() && !family.equals(bp.objectiveFamily)) continue;
      if (playerLevel < bp.minPlayerLevel) continue;
      if (bp.maxPlayerLevel != null && playerLevel > bp.maxPlayerLevel) continue;
      out.add(bp);
    }
    return out;
  }
}
```

---

## 3) Einbau-Anweisungen (später)

- Generatorpipeline lädt:
  - `BlueprintLoader().load("data/zqs/blueprints_v1.json")`
  - `BlueprintRegistry.loadFrom(...)`
- Schritt 2: `registry.filter(family, playerLevel)`
- Schritt 8: `WeightedPicker.pick(eligible, bp -> bp.weight)`

---

## 4) Dummy-Abdeckung

- (103) BlueprintLoader/Registry: vollständig.
- Legacy Dummy (001) in `ZqsSystem` wird dadurch perspektivisch obsolet (Pipeline ersetzt Monolith).


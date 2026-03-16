# PLAN — Modul 01: `quest/zqs/catalog/*` (ContentCatalogRuntime)

> Ziel: Dummy-Replacement für Struktur-Dummies **(101)** + Grundlage für Generator-Eligibility (Known/SL_ID/Tags/ValueCopper).
>
> Quelle/Autorität:
> - Master: `flow 4/zqs_master_alignment_v_1_1.md`
> - Soll-Modell: `MODEL_ZQS.md` (02.01)
> - Generator-Regeln: `GENERATOR_ZQS.md` (04.01–04.02)
>
> Harte Regeln:
> - Generator arbeitet **nur** auf `ContentCatalogRuntime`.
> - Keine Fallback-Systeme: Missing DB/Entry → Exception.

---

## 1) Dateien/Packages (neu)

Pfad (neu):
- `core/src/main/java/com/yourgame/survival/quest/zqs/catalog/`

Neue Klassen:
1. `CatalogKind.java`
2. `CatalogEntryRt.java`
3. `ContentCatalogRuntime.java`
4. `CatalogRuntimeLoader.java`
5. `CatalogIndexes.java`

> Diese Klassen ersetzen nicht sofort `ZqsDb`, aber werden als **kanonische** Runtime-Katalog-API genutzt, sobald generator/* Pipeline aktiv wird.

---

## 2) Exakter Datenvertrag (Input)

DB-Quelle bleibt:
- `assets/data/zqs/catalog_runtime_v1.json`

Erwartete Arrays:
- resources/items/harvestables/livings/pois/npcs/regions

Erwartete Felder pro Entry (MODEL_ZQS.md):
- `kind` (implizit durch Array)
- `id` (string) **oder** numeric itemId
- `name` (string)
- `tags` (string[])
- `valueCopper` (int)
- `slIdMax` (int)
- `knownRequired` (bool)
- `itemId` (int) für Items/Resources (optional: wenn id numerisch)

**Normierung:**
- `entry.key()` ist stabiler String-Key:  
  - wenn `id` non-empty → `id`  
  - else wenn `itemId >= 0` → `String.valueOf(itemId)`

---

## 3) Copy/Paste Code (vollständige Klassen)

### 3.1 `CatalogKind.java`

```java
package com.yourgame.survival.quest.zqs.catalog;

public enum CatalogKind {
  RESOURCE("resource"),
  ITEM("item"),
  HARVESTABLE("harvestable"),
  LIVING("living"),
  POI("poi"),
  NPC("npc"),
  REGION("region");

  public final String id;
  CatalogKind(String id) { this.id = id; }

  public static CatalogKind byId(String id) {
    if (id == null) return null;
    for (CatalogKind k : values()) if (k.id.equals(id)) return k;
    return null;
  }
}
```

### 3.2 `CatalogEntryRt.java`

```java
package com.yourgame.survival.quest.zqs.catalog;

import java.util.ArrayList;
import java.util.List;

public final class CatalogEntryRt {
  public CatalogKind kind;
  public String id = "";
  public int itemId = -1;
  public String name = "";
  public int valueCopper = 0;
  public int slIdMax = 0;
  public boolean knownRequired = false;
  public final List<String> tags = new ArrayList<>();

  public String key() {
    if (id != null && !id.isEmpty()) return id;
    if (itemId >= 0) return String.valueOf(itemId);
    return "";
  }

  public boolean isValid() {
    return kind != null && key() != null && !key().isEmpty();
  }

  public boolean hasTag(String tag) {
    if (tag == null || tag.isEmpty()) return false;
    for (String t : tags) if (tag.equals(t)) return true;
    return false;
  }
}
```

### 3.3 `ContentCatalogRuntime.java`

```java
package com.yourgame.survival.quest.zqs.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ContentCatalogRuntime {

  public final List<CatalogEntryRt> resources = new ArrayList<>();
  public final List<CatalogEntryRt> items = new ArrayList<>();
  public final List<CatalogEntryRt> harvestables = new ArrayList<>();
  public final List<CatalogEntryRt> livings = new ArrayList<>();
  public final List<CatalogEntryRt> pois = new ArrayList<>();
  public final List<CatalogEntryRt> npcs = new ArrayList<>();
  public final List<CatalogEntryRt> regions = new ArrayList<>();

  public void clear() {
    resources.clear();
    items.clear();
    harvestables.clear();
    livings.clear();
    pois.clear();
    npcs.clear();
    regions.clear();
  }

  public List<CatalogEntryRt> listByKind(CatalogKind kind) {
    if (kind == null) return Collections.emptyList();
    return switch (kind) {
      case RESOURCE -> resources;
      case ITEM -> items;
      case HARVESTABLE -> harvestables;
      case LIVING -> livings;
      case POI -> pois;
      case NPC -> npcs;
      case REGION -> regions;
    };
  }
}
```

### 3.4 `CatalogIndexes.java`

```java
package com.yourgame.survival.quest.zqs.catalog;

import java.util.HashMap;

public final class CatalogIndexes {
  public final HashMap<String, CatalogEntryRt> byKey = new HashMap<>();

  public void clear() { byKey.clear(); }

  public void index(ContentCatalogRuntime cat) {
    clear();
    if (cat == null) return;
    indexList(cat.resources);
    indexList(cat.items);
    indexList(cat.harvestables);
    indexList(cat.livings);
    indexList(cat.pois);
    indexList(cat.npcs);
    indexList(cat.regions);
  }

  private void indexList(java.util.List<CatalogEntryRt> list) {
    if (list == null) return;
    for (CatalogEntryRt e : list) {
      if (e == null || !e.isValid()) continue;
      byKey.put(e.key(), e);
    }
  }

  public CatalogEntryRt get(String key) {
    if (key == null || key.isEmpty()) return null;
    return byKey.get(key);
  }
}
```

### 3.5 `CatalogRuntimeLoader.java`

```java
package com.yourgame.survival.quest.zqs.catalog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public final class CatalogRuntimeLoader {

  public ContentCatalogRuntime load(String internalPath) {
    if (internalPath == null || internalPath.isEmpty()) {
      throw new IllegalArgumentException("catalog path missing");
    }

    JsonValue root = parseInternal(internalPath);
    ContentCatalogRuntime out = new ContentCatalogRuntime();

    readArray(root, "resources", CatalogKind.RESOURCE, out);
    readArray(root, "items", CatalogKind.ITEM, out);
    readArray(root, "harvestables", CatalogKind.HARVESTABLE, out);
    readArray(root, "livings", CatalogKind.LIVING, out);
    readArray(root, "pois", CatalogKind.POI, out);
    readArray(root, "npcs", CatalogKind.NPC, out);
    readArray(root, "regions", CatalogKind.REGION, out);

    // strict: empty catalog is a data error
    if (out.items.isEmpty() && out.resources.isEmpty() && out.harvestables.isEmpty()) {
      throw new IllegalStateException("CatalogRuntime is empty: " + internalPath);
    }

    return out;
  }

  private static void readArray(JsonValue root, String key, CatalogKind kind, ContentCatalogRuntime out) {
    if (root == null) throw new IllegalStateException("catalog root missing");
    JsonValue arr = root.get(key);
    if (arr == null) return; // arrays optional per current db loader, but content may be empty

    java.util.List<CatalogEntryRt> list = out.listByKind(kind);
    for (JsonValue e = arr.child; e != null; e = e.next) {
      CatalogEntryRt ce = new CatalogEntryRt();
      ce.kind = kind;

      // Allow numeric id or string id
      ce.itemId = e.getInt("id", -1);
      ce.id = e.getString("id", "");
      ce.name = e.getString("name", "");
      ce.valueCopper = e.getInt("valueCopper", e.getInt("value", 0));
      ce.slIdMax = e.getInt("slIdMax", 0);
      ce.knownRequired = e.getBoolean("knownRequired", false);

      JsonValue tags = e.get("tags");
      if (tags != null) {
        for (JsonValue tv = tags.child; tv != null; tv = tv.next) {
          String t = tv.asString();
          if (t != null && !t.isEmpty()) ce.tags.add(t);
        }
      }

      if (!ce.isValid()) {
        throw new IllegalStateException("Invalid catalog entry in " + key + ": id/itemId missing");
      }
      list.add(ce);
    }
  }

  private static JsonValue parseInternal(String path) {
    try {
      FileHandle fh = Gdx.files.internal(path);
      if (fh == null || !fh.exists()) throw new RuntimeException("Missing catalog file: " + path);
      return new JsonReader().parse(fh);
    } catch (Throwable t) {
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }
  }
}
```

---

## 4) Einbau-Anweisungen (später, wenn umgesetzt wird)

1) Generatorpipeline benutzt künftig:
- `ContentCatalogRuntime cat = new CatalogRuntimeLoader().load("data/zqs/catalog_runtime_v1.json");`
- `CatalogIndexes idx = new CatalogIndexes(); idx.index(cat);`

2) `TargetBlock.targetValueCopper` wird direkt aus `CatalogEntryRt.valueCopper` gesetzt (ersetzt DUMMY 402).

3) KnownRequired/SL_ID Filter:
- `knownRequired` wird gegen Save-Knowledge geprüft (siehe Modul 02).
- `slIdMax > playerSlId` → verwerfen.

---

## 5) Dummy-Abdeckung

- (101) ContentCatalogAsset: **noch nicht** (dieses Modul ist RuntimeCatalog). AssetCatalog kommt später, wenn Textpools Aliase brauchen.
- (102) Knowledge: siehe Modul 02.
- (104) Generatorpipeline: siehe Modul 04.


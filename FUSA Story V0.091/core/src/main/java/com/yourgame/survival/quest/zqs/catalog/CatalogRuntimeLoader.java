package com.yourgame.survival.quest.zqs.catalog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Strict loader for `assets/data/zqs/catalog_runtime_v1.json`.
 * Missing files / invalid entries must fail loudly.
 */
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
    if (out.resources.isEmpty() && out.items.isEmpty() && out.harvestables.isEmpty()
        && out.livings.isEmpty() && out.pois.isEmpty() && out.npcs.isEmpty() && out.regions.isEmpty()) {
      throw new IllegalStateException("CatalogRuntime is empty: " + internalPath);
    }

    return out;
  }

  private static void readArray(JsonValue root, String key, CatalogKind kind, ContentCatalogRuntime out) {
    if (root == null) throw new IllegalStateException("catalog root missing");
    JsonValue arr = root.get(key);
    if (arr == null) return; // array optional; content decides what is generator-valid

    java.util.List<CatalogEntryRt> list = out.listByKind(kind);
    for (JsonValue e = arr.child; e != null; e = e.next) {
      CatalogEntryRt ce = new CatalogEntryRt();
      ce.kind = kind;

      // id can be numeric (items/resources) or string (most other kinds)
      JsonValue idV = e.get("id");
      if (idV != null) {
        if (idV.isNumber()) {
          ce.itemId = idV.asInt();
        } else {
          ce.id = idV.asString();
        }
      }

      // optional itemId field (for explicit numeric id)
      if (ce.itemId < 0) {
        ce.itemId = e.getInt("itemId", -1);
      }

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
        throw new IllegalStateException("Invalid catalog entry in array '" + key + "' (id/itemId missing)");
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


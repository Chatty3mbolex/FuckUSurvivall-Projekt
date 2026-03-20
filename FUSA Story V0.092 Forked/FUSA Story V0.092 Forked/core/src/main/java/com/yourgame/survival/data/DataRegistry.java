package com.yourgame.survival.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

/** Data-driven registry loaded from assets/data/*.json (LibGDX JsonReader). */
public final class DataRegistry {
  public final ItemDef[] items = new ItemDef[60];
  public int itemCount = 60;
  public final ArrayList<RecipeDef> recipes = new ArrayList<>(128);
  public ShopDef fixedShop;
  public int[] wanderingPoolItems = new int[0];

  public final EnemyDef orkGrunt = new EnemyDef();
  public final AnimalDef deer = new AnimalDef();

  public void loadAll() {
    loadItems("data/items.json");
    loadRecipes("data/recipes.json");
    loadShops("data/shops.json");
    loadEnemies("data/enemies.json");
    loadAnimals("data/animals.json");
  }

  private void loadItems(String path) {
    JsonValue root = parse(path);
    JsonValue arr = root.get("items");
    if (arr == null) {
      Gdx.app.error("DataRegistry", "Missing key 'items' in: " + path);
      return;
    }

    boolean[] seen = new boolean[items.length];

    int maxId = -1;
    for (JsonValue it = arr.child; it != null; it = it.next) {
      ItemDef d = new ItemDef();
      d.id = it.getInt("id", -1);
      d.name = it.getString("name", "");
      d.type = it.getString("type", "");
      d.stackMax = it.getInt("stackMax", 999);
      d.value = it.getInt("value", 0);
      d.icon = it.getString("icon", "");

      // optional tags
      JsonValue tags = it.get("tags");
      if (tags != null && tags.size > 0) {
        String[] outTags = new String[tags.size];
        int ti = 0;
        for (JsonValue t = tags.child; t != null; t = t.next) {
          outTags[ti++] = t.asString();
        }
        d.tags = outTags;
      } else {
        d.tags = new String[0];
      }

      if (d.id < 0 || d.id >= items.length) {
        Gdx.app.error("DataRegistry", "Invalid item id=" + d.id + " in: " + path);
        continue;
      }
      if (seen[d.id]) {
        throw new RuntimeException("Duplicate item id=" + d.id + " in: " + path);
      }
      seen[d.id] = true;

      items[d.id] = d;
      if (d.id > maxId) maxId = d.id;
    }
    itemCount = Math.min(items.length, Math.max(0, maxId + 1));
  }

  private void loadRecipes(String path) {
    JsonValue root = parse(path);
    JsonValue arr = root.get("recipes");
    if (arr == null) {
      Gdx.app.error("DataRegistry", "Missing key 'recipes' in: " + path);
      return;
    }

    java.util.HashSet<String> seen = new java.util.HashSet<>();

    for (JsonValue r = arr.child; r != null; r = r.next) {
      RecipeDef d = new RecipeDef();
      d.id = r.getString("id", "");
      if (d.id == null || d.id.isEmpty()) {
        throw new RuntimeException("Recipe missing id in: " + path);
      }
      if (!seen.add(d.id)) {
        throw new RuntimeException("Duplicate recipe id='" + d.id + "' in: " + path);
      }

      JsonValue out = r.get("output");
      if (out == null) throw new RuntimeException("Recipe '" + d.id + "' missing output in: " + path);
      d.outItemId = out.getInt("itemId", -1);
      d.outAmount = out.getInt("amount", 0);

      JsonValue ins = r.get("inputs");
      if (ins == null) throw new RuntimeException("Recipe '" + d.id + "' missing inputs in: " + path);
      int n = ins.size;
      d.inItemId = new int[n];
      d.inAmount = new int[n];
      int i = 0;
      for (JsonValue in = ins.child; in != null; in = in.next) {
        d.inItemId[i] = in.getInt("itemId", -1);
        d.inAmount[i] = in.getInt("amount", 0);
        i++;
      }

      d.itemsOnlyChain = false;
      JsonValue tags = r.get("tags");
      if (tags != null) {
        for (JsonValue t = tags.child; t != null; t = t.next) {
          if ("itemsOnlyChain".equals(t.asString())) { d.itemsOnlyChain = true; break; }
        }
      }

      recipes.add(d);
    }
  }

  private void loadShops(String path) {
    JsonValue root = parse(path);
    JsonValue fixed = root.get("shops").get("fixed");
    ShopDef s = new ShopDef();
    s.id = fixed.getString("id", "merchant_fixed");
    JsonValue offers = fixed.get("offers");
    s.offerCount = offers.size;
    s.itemId = new int[s.offerCount];
    s.buy = new int[s.offerCount];
    s.sell = new int[s.offerCount];
    int i=0;
    for (JsonValue o = offers.child; o != null; o = o.next) {
      s.itemId[i] = o.getInt("itemId");
      s.buy[i] = o.getInt("buy");
      s.sell[i] = o.getInt("sell");
      i++;
    }
    fixedShop = s;

    // wandering pool (optional)
    JsonValue pools = root.get("shops").get("wanderingPools");
    if (pools != null && pools.size > 0) {
      JsonValue p0 = pools.child;
      JsonValue items = p0.get("items");
      if (items != null) {
        wanderingPoolItems = new int[items.size];
        int wi = 0;
        for (JsonValue it = items.child; it != null; it = it.next) {
          wanderingPoolItems[wi++] = it.asInt();
        }
      }
    }
  }

  private void loadEnemies(String path) {
    JsonValue root = parse(path);
    JsonValue arr = root.get("enemies");
    for (JsonValue e = arr.child; e != null; e = e.next) {
      String id = e.getString("id", "");
      if ("ork_grunt".equals(id)) {
        orkGrunt.id = id;
        orkGrunt.hp = e.getInt("hp", 35);
        orkGrunt.dmg = e.getInt("dmg", 6);
        orkGrunt.xp = e.getInt("xp", 10);
        // lootTable coin range
        JsonValue lt = e.get("lootTable");
        if (lt != null) {
          for (JsonValue l = lt.child; l != null; l = l.next) {
            if (l.getInt("itemId", -1) == 31) {
              orkGrunt.coinMin = l.getInt("min", 1);
              orkGrunt.coinMax = l.getInt("max", 8);
              break;
            }
          }
        }
      }
    }
  }

  private void loadAnimals(String path) {
    JsonValue root = parse(path);
    JsonValue arr = root.get("animals");
    for (JsonValue a = arr.child; a != null; a = a.next) {
      String id = a.getString("id", "");
      if ("animal_deer".equals(id)) {
        deer.id = id;
        deer.hp = a.getInt("hp", 20);
        JsonValue loot = a.get("loot");
        if (loot != null) {
          for (JsonValue l = loot.child; l != null; l = l.next) {
            if (l.getInt("itemId", -1) == 28) {
              deer.meatMin = l.getInt("min", 1);
              deer.meatMax = l.getInt("max", 2);
              break;
            }
          }
        }
      }
    }
  }

  private static JsonValue parse(String path) {
    try {
      FileHandle fh = Gdx.files.internal(path);
      if (fh == null || !fh.exists()) {
        throw new RuntimeException("Missing data file: " + path);
      }
      return new JsonReader().parse(fh);
    } catch (Throwable t) {
      Gdx.app.error("DataRegistry", "Failed to parse: " + path, t);
      throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
    }
  }
}

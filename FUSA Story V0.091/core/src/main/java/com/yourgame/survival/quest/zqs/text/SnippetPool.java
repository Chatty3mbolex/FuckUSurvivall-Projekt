package com.yourgame.survival.quest.zqs.text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * In-memory snippet DB grouped by category/part.
 */
public final class SnippetPool {
  public String language = "de_DE";
  public String speakerEntity = "WQG";

  // category -> part -> list
  public final HashMap<String, HashMap<String, ArrayList<SnippetDef>>> byCatPart = new HashMap<>();

  public void clear() {
    byCatPart.clear();
  }

  public void add(SnippetDef s) {
    if (s == null) return;
    if (s.textCategory == null || s.textCategory.isEmpty()) return;
    if (s.textPart == null || s.textPart.isEmpty()) return;
    HashMap<String, ArrayList<SnippetDef>> byPart = byCatPart.get(s.textCategory);
    if (byPart == null) {
      byPart = new HashMap<>();
      byCatPart.put(s.textCategory, byPart);
    }
    ArrayList<SnippetDef> list = byPart.get(s.textPart);
    if (list == null) {
      list = new ArrayList<>();
      byPart.put(s.textPart, list);
    }
    list.add(s);
  }

  public ArrayList<SnippetDef> list(String category, String part) {
    HashMap<String, ArrayList<SnippetDef>> byPart = byCatPart.get(category);
    if (byPart == null) return null;
    return byPart.get(part);
  }
}


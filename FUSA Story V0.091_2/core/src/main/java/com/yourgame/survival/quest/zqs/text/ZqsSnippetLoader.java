package com.yourgame.survival.quest.zqs.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Loads snippet DB JSON into a SnippetPool.
 *
 * Source: assets/data/zqs/text_snippets_de_DE_v1.json
 * No fallback systems allowed.
 */
public final class ZqsSnippetLoader {

  public SnippetPool load(String path) {
    if (path == null || path.isEmpty()) {
      throw new IllegalArgumentException("snippet path missing");
    }

    JsonValue root = parse(path);
    SnippetPool pool = new SnippetPool();
    pool.language = root.getString("language", "de_DE");
    JsonValue gd = root.get("globalDefaults");
    if (gd != null) pool.speakerEntity = gd.getString("speaker_entity", "WQG");

    JsonValue arr = root.get("snippets");
    if (arr == null) {
      throw new IllegalStateException("Missing key 'snippets' in: " + path);
    }

    for (JsonValue s = arr.child; s != null; s = s.next) {
      SnippetDef d = new SnippetDef();
      d.snippetId = s.getString("snippet_id", "");
      d.textCategory = s.getString("text_category", "");
      d.textPart = s.getString("text_part", "");
      d.template = s.getString("text_template", "");
      d.needsPrefixSpace = s.getBoolean("needs_prefix_space", false);
      d.needsSuffixSpace = s.getBoolean("needs_suffix_space", false);
      d.punctuationRole = s.getString("punctuation_role", "middle");

      JsonValue filters = s.get("filters");
      if (filters != null) {
        for (JsonValue f = filters.child; f != null; f = f.next) {
          String k = f.name;
          String v;
          if (f.isBoolean()) v = String.valueOf(f.asBoolean());
          else v = f.asString();
          d.filters.put(k, v);
        }
      }

      if (d.snippetId == null || d.snippetId.isEmpty()) {
        throw new IllegalStateException("Snippet missing snippet_id in: " + path);
      }
      if (d.textCategory == null || d.textCategory.isEmpty()) {
        throw new IllegalStateException("Snippet '" + d.snippetId + "' missing text_category");
      }
      if (d.textPart == null || d.textPart.isEmpty()) {
        throw new IllegalStateException("Snippet '" + d.snippetId + "' missing text_part");
      }
      if (d.template == null) d.template = "";

      pool.add(d);
    }

    return pool;
  }

  private static JsonValue parse(String path) {
    FileHandle fh = Gdx.files.internal(path);
    if (fh == null || !fh.exists()) {
      throw new IllegalStateException("Missing snippet DB file: " + path);
    }
    return new JsonReader().parse(fh);
  }
}


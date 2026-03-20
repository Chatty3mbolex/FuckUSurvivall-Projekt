package com.yourgame.survival.quest.zqs.text;

import java.util.HashMap;

/**
 * Snippet definition (loaded from assets/data/zqs/text_snippets_de_DE_v1.json).
 * Must follow TEXT_ZQS.md + flow4 snippetkatalog.
 */
public final class SnippetDef {
  public String snippetId = "";
  public String textCategory = ""; // greeting|assignment|assignment_na|reward|farewell
  public String textPart = "";     // main|middle|end|na
  public final HashMap<String, String> filters = new HashMap<>();
  public String template = "";

  public boolean needsPrefixSpace = false;
  public boolean needsSuffixSpace = false;
  public String punctuationRole = "middle"; // open|middle|close
}


package com.yourgame.survival.quest.zqs.runtime;

/** Canonical ZQS quest types (Codeplan v1). */
public enum QuestType {
  SAMMELN("sammeln"),
  LIEFERN("liefern"),
  CRAFTEN("craften"),
  FINDEN("finden"),
  ESKORTIEREN("eskortieren");

  public final String id;
  QuestType(String id) { this.id = id; }

  public static QuestType byId(String id) {
    if (id == null) return null;
    for (QuestType t : values()) if (t.id.equals(id)) return t;
    return null;
  }
}


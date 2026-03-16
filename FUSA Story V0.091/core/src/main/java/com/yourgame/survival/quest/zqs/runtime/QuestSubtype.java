package com.yourgame.survival.quest.zqs.runtime;

/** Canonical ZQS quest subtypes (Codeplan v1 / Inhaltsreferenz). */
public enum QuestSubtype {
  SAMMELN_ITEM("sammeln.item", QuestType.SAMMELN),
  SAMMELN_HARVESTABLE("sammeln.harvestable", QuestType.SAMMELN),

  LIEFERN_ITEM("liefern.item", QuestType.LIEFERN),

  CRAFTEN_RECIPE_OUTPUT("craften.recipe_output", QuestType.CRAFTEN),
  CRAFTEN_DELIVERY("craften.delivery", QuestType.CRAFTEN),

  FINDEN_POI("finden.poi", QuestType.FINDEN),
  FINDEN_POI_LOOT("finden.poi_loot", QuestType.FINDEN),
  FINDEN_PERSON("finden.person", QuestType.FINDEN),
  FINDEN_OBJECT("finden.object", QuestType.FINDEN),

  ESKORTIEREN_ROUTE("eskortieren.route", QuestType.ESKORTIEREN);

  public final String id;
  public final QuestType type;
  QuestSubtype(String id, QuestType type) { this.id = id; this.type = type; }

  public static QuestSubtype byId(String id) {
    if (id == null) return null;
    for (QuestSubtype s : values()) if (s.id.equals(id)) return s;
    return null;
  }
}


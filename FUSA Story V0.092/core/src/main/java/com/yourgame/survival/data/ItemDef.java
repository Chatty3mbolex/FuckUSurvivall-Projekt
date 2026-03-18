package com.yourgame.survival.data;

public final class ItemDef {
  public int id;
  public String name;
  public String type;
  public int stackMax;
  public int value;
  public String icon;

  /** Optional item tags from items.json (used by systems that need tag queries). */
  public String[] tags = new String[0];
}

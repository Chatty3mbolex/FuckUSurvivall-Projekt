package com.yourgame.survival.tools.asseteditor.model;

public final class IndexEntry {
  public final String name;      // atlas region name
  public final AssetKind kind;
  public final String foundIn;   // file hint
  public final boolean missingInAtlas;

  public IndexEntry(String name, AssetKind kind, String foundIn) {
    this(name, kind, foundIn, false);
  }

  public IndexEntry(String name, AssetKind kind, String foundIn, boolean missingInAtlas) {
    this.name = name;
    this.kind = kind;
    this.foundIn = foundIn;
    this.missingInAtlas = missingInAtlas;
  }

  @Override
  public String toString() {
    return name;
  }
}

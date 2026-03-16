package com.yourgame.survival.worldmap;

import java.util.EnumMap;

/**
 * Hand-authored area template metadata.
 *
 * IMPORTANT: This is NOT the rendered tilemap yet.
 * This is the "world stitching" description that will later point to an actual
 * map asset.
 *
 * Savegames store only the templateId per discovered area.
 * The templates themselves are data assets shipped with the game.
 */
public final class AreaTemplate {
  /** Stable ID used in savegames. Must never change for existing saves. */
  public final String id;

  /** Optional human-readable name (debug/UI). */
  public final String name;

  /** Exit sockets by direction; null means "no exit on that side". */
  public final EnumMap<Dir4, ExitSocket> exits = new EnumMap<>(Dir4.class);

  /** If true: this template is allowed to be chosen by the random picker. Home should be false. */
  public final boolean pickable;

  public AreaTemplate(String id, String name, boolean pickable) {
    this.id = (id != null) ? id : "";
    this.name = (name != null) ? name : "";
    this.pickable = pickable;
  }

  /** Convenience for authoring. */
  public AreaTemplate exit(ExitSocket s) {
    if (s != null) exits.put(s.dir, s);
    return this;
  }

  public ExitSocket exit(Dir4 d) {
    return exits.get(d);
  }
}

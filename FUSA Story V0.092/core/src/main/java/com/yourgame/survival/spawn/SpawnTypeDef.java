package com.yourgame.survival.spawn;

import com.yourgame.survival.entity.EntityType;

/**
 * Definition for a SpawnTypeId.
 * Many ids will start as category=UNDEFINED and have no mapping.
 */
public final class SpawnTypeDef {
  public final SpawnTypeId id;
  public SpawnCategory category = SpawnCategory.UNDEFINED;

  /** If true, this id needs a sprite/atlas region (or a tile/overlay) to look correct. */
  public boolean needsPng = true;

  /** Optional mapping to runtime entity type. Null means "not implemented" in runtime. */
  public EntityType mappedEntityType = null;

  public SpawnTypeDef(SpawnTypeId id) {
    this.id = id;
  }

  public SpawnTypeDef cat(SpawnCategory c) {
    if (c != null) this.category = c;
    return this;
  }

  public SpawnTypeDef png(boolean v) {
    this.needsPng = v;
    return this;
  }

  public SpawnTypeDef map(EntityType t) {
    this.mappedEntityType = t;
    return this;
  }
}

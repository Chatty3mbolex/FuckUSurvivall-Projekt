package com.yourgame.survival.editor;

import com.yourgame.survival.world.Biome;

/** Current palette selection + brush parameters (UI model). */
public final class ToolState {
  public ToolKind kind = ToolKind.GROUND;

  public boolean erase = false;
  public int radius = 2;
  public float strength = 1.0f;

  public short groundId = 0;
  public Biome biome = Biome.GRASSLAND;

  /** Height brush delta per stroke (additive). Negative lowers. */
  public int heightDelta = 1;

  public String activeZone = "zone";

  public void clamp() {
    if (radius < 1) radius = 1;
    if (radius > 24) radius = 24;
    if (strength < 0.05f) strength = 0.05f;
    if (strength > 10f) strength = 10f;
    if (heightDelta == 0) heightDelta = 1;
  }
}

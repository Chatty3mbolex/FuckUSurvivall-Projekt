package com.yourgame.survival.editor;

/**
 * Editor-defined height rule: if inside zone, add/subtract bias to heightLevel.
 * This is used to apply painted zones to the live preview (and later derived to settings).
 */
public final class ZoneHeightRule {
  public String zoneName = "zone";
  public int delta = 0; // -15..+15

  public ZoneHeightRule() {}

  public ZoneHeightRule(String zoneName, int delta) {
    if (zoneName != null && !zoneName.isBlank()) this.zoneName = zoneName.trim();
    this.delta = delta;
  }

  public int clampDelta() {
    if (delta < -15) delta = -15;
    if (delta > 15) delta = 15;
    return delta;
  }
}

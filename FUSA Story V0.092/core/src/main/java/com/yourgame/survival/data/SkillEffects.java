package com.yourgame.survival.data;

/**
 * Centralized skill effect helpers.
 *
 * Rules (from docs/skills_what.md):
 * - Per skill level: +5% effect (usually: more/speed/stronger), using (level-1) so Lv1 is baseline.
 */
public final class SkillEffects {
  private SkillEffects() {}

  /** +5% per level above 1. (Lv1 => 1.0, Lv2 => 1.05, ...). */
  public static float mul5(int level) {
    int lv = Math.max(1, level);
    return 1f + 0.05f * (lv - 1);
  }

  /** Drain reduction: 1 - 5% per level above 1, clamped. */
  public static float drainMul5(int level, float minMul) {
    int lv = Math.max(1, level);
    float m = 1f - 0.05f * (lv - 1);
    if (m < minMul) m = minMul;
    if (m > 1f) m = 1f;
    return m;
  }

  /** Multiply an integer amount by mul5(level), keep at least 1. */
  public static int bonusAmountMul5(int baseAmount, int level) {
    int base = Math.max(0, baseAmount);
    if (base <= 0) return 0;
    return Math.max(1, (int) Math.floor(base * (double) mul5(level)));
  }

  /** Skill upgrade cost rule: base 1, every 2 levels +1. */
  public static int upgradeCost(int currentLevel) {
    int lv = Math.max(0, currentLevel);
    return 1 + (lv / 2);
  }
}

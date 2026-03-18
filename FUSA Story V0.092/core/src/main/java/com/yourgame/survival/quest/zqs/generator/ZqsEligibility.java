package com.yourgame.survival.quest.zqs.generator;

/**
 * Canonical eligibility gate.
 *
 * v1 focuses on NQ. HQ rules are reserved for future extension.
 */
public final class ZqsEligibility {

  /**
   * Checks if NQ generation is allowed (cap/timer are enforced elsewhere as well).
   */
  public EligibilityResult checkNq(ZqsGeneratorInputs in) {
    if (in == null) throw new IllegalArgumentException("inputs missing");
    EligibilityResult r = new EligibilityResult();

    // Hard cap value is project-defined. Keep explicit (no hidden magic).
    int cap = 10;
    if (in.openQuestsCount > cap) {
      r.ok = false;
      r.naReason = "cap_reached";
      r.blockReason = "cap_reached";
      return r;
    }

    return r;
  }
}


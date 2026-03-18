package com.yourgame.survival.quest.zqs.text;

/**
 * Text selection context (PHASE_05 / TEXT_ZQS.md).
 *
 * Note: this class intentionally contains only selection inputs.
 * It does not compute any quest values.
 */
public final class ConversationContext {
  public String timeOfDay = ""; // morning|day|evening|night
  public String worldstressZone = ""; // ruhig|belebt|hektisch
  public String debugHqMode = ""; // INCLUDE_HQ|EXCLUDE_HQ

  public String conversationResult = ""; // accepted|declined|no_offer|inventory_full|cap_reached|blocked
  public String conversationNextStep = ""; // offer|no_offer|blocked

  public int nqOfferCount = 0;
  public int openQuestsCount = 0;
  public int completedQuestsCount = 0;

  public boolean nqGenerationPossible = true;
  public String blockReason = "none"; // none|cap_reached|no_valid_targets|timer_not_due|N/A|rolled_zero

  // Assignment
  public String questType = "";
  public String questSubtype = "";

  // Reward
  public String rewardState = ""; // claimable|accepted|failed
  public String rewardTextMode = ""; // item|currency|mixed|failed

  // N/A
  public String naReason = ""; // rolled_zero|cap_reached|no_valid_targets|no_known_targets|timer_reset
}


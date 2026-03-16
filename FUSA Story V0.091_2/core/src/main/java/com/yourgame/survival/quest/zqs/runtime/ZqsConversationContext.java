package com.yourgame.survival.quest.zqs.runtime;

/** Conversation context input for greeting/farewell selection. */
public final class ZqsConversationContext {
  public long runtimeSec;

  public String timeOfDay = "day"; // morning|day|evening|night
  public String worldstressZone = "ruhig"; // ruhig|belebt|hektisch
  public String debugHqMode = "EXCLUDE_HQ"; // INCLUDE_HQ|EXCLUDE_HQ

  public int openQuestsCount = 0;
  public int completedQuestsCount = 0;

  public int nqOfferCount = 0;

  public boolean nqGenerationPossible = true;
  public String blockReason = "none"; // none|cap_reached|no_valid_targets|timer_not_due|N/A

  public String naReason = ""; // rolled_zero|cap_reached|no_valid_targets|timer_reset|...

  public String conversationNextStep = "offer"; // offer|no_offer|blocked
  public String conversationResult = ""; // accepted|declined|no_offer|...
}

package com.yourgame.survival.quest.zqs.text;

import java.util.ArrayList;

/**
 * Snippet selection + assembly (PHASE_05 / TEXT_ZQS.md).
 * No fallback systems allowed.
 */
public final class ZqsTextAssembler {

  private final SnippetPool pool;
  private long rng;

  public ZqsTextAssembler(SnippetPool pool, long rngSeed) {
    if (pool == null) throw new IllegalArgumentException("SnippetPool missing");
    this.pool = pool;
    this.rng = (rngSeed != 0L) ? rngSeed : 0x9E3779B97F4A7C15L;
  }

  public BuiltText buildGreeting(ConversationContext ctx) {
    requireCtx(ctx);
    BuiltText out = new BuiltText();
    out.main = pickRequired("greeting", "main", ctx, "time_of_day", ctx.timeOfDay);
    out.middle = pickRequired("greeting", "middle", ctx, "worldstress_zone", ctx.worldstressZone);
    out.end = pickRequired("greeting", "end", ctx, "conversation_next_step", ctx.conversationNextStep);
    out.full = join3(out.mainText(), out.middleText(), out.endText());
    return out;
  }

  public BuiltText buildAssignment(ConversationContext ctx) {
    requireCtx(ctx);
    BuiltText out = new BuiltText();
    out.main = pickRequired("assignment", "main", ctx, "quest_type", ctx.questType);
    out.middle = pickRequired("assignment", "middle", ctx, "quest_subtype", ctx.questSubtype);
    out.end = pickRequired("assignment", "end", ctx, "quest_type", ctx.questType);
    out.full = join3(out.mainText(), out.middleText(), out.endText());
    return out;
  }

  public BuiltText buildAssignmentNa(ConversationContext ctx) {
    requireCtx(ctx);
    BuiltText out = new BuiltText();
    out.na = pickRequired("assignment_na", "na", ctx, "na_reason", ctx.naReason);
    out.full = out.naText();
    return out;
  }

  public BuiltText buildReward(ConversationContext ctx) {
    requireCtx(ctx);
    BuiltText out = new BuiltText();
    out.main = pickRequired("reward", "main", ctx, "reward_state", ctx.rewardState);
    out.middle = pickRequired("reward", "middle", ctx, "reward_text_mode", ctx.rewardTextMode);
    out.end = pickAnyRequired("reward", "end", ctx);
    out.full = join3(out.mainText(), out.middleText(), out.endText());
    return out;
  }

  public BuiltText buildFarewell(ConversationContext ctx) {
    requireCtx(ctx);
    BuiltText out = new BuiltText();
    out.main = pickRequired("farewell", "main", ctx, "time_of_day", ctx.timeOfDay);
    out.middle = pickRequired("farewell", "middle", ctx, "worldstress_zone", ctx.worldstressZone);
    out.end = pickRequired("farewell", "end", ctx, "conversation_result", ctx.conversationResult);
    out.full = join3(out.mainText(), out.middleText(), out.endText());
    return out;
  }

  // ------------------- selection -------------------

  private SnippetDef pickAnyRequired(String category, String part, ConversationContext ctx) {
    ArrayList<SnippetDef> list = pool.list(category, part);
    if (list == null || list.isEmpty()) {
      throw new IllegalStateException("No snippets for " + category + "." + part);
    }
    // Filter pass-through (only mandatory when filters exist)
    ArrayList<SnippetDef> ok = new ArrayList<>();
    for (SnippetDef s : list) {
      if (s == null) continue;
      if (matchesAllFilters(s, ctx)) ok.add(s);
    }
    if (ok.isEmpty()) {
      throw new IllegalStateException("No matching snippets for " + category + "." + part);
    }
    return ok.get(pickIndex(ok.size()));
  }

  private SnippetDef pickRequired(String category, String part, ConversationContext ctx, String key, String wanted) {
    ArrayList<SnippetDef> list = pool.list(category, part);
    if (list == null || list.isEmpty()) {
      throw new IllegalStateException("No snippets for " + category + "." + part);
    }

    ArrayList<SnippetDef> ok = new ArrayList<>();
    for (SnippetDef s : list) {
      if (s == null) continue;
      // required key must match (or be '*') if present
      String spec = (s.filters != null) ? s.filters.get(key) : null;
      if (!matchesSpec(spec, wanted)) continue;
      if (!matchesAllFilters(s, ctx)) continue;
      ok.add(s);
    }
    if (ok.isEmpty()) {
      throw new IllegalStateException("No matching snippets for " + category + "." + part + " with " + key + "=" + wanted);
    }
    return ok.get(pickIndex(ok.size()));
  }

  private boolean matchesAllFilters(SnippetDef s, ConversationContext ctx) {
    if (s == null) return false;
    if (s.filters == null || s.filters.isEmpty()) return true;

    for (var e : s.filters.entrySet()) {
      String key = e.getKey();
      String spec = e.getValue();
      if (key == null) continue;
      // '*' means not restricting
      if (spec == null || spec.isEmpty() || "*".equals(spec)) continue;

      String wanted = valueForKey(ctx, key);
      if (!matchesSpec(spec, wanted)) return false;
    }
    return true;
  }

  private static String valueForKey(ConversationContext ctx, String key) {
    if (ctx == null || key == null) return "";
    return switch (key) {
      case "time_of_day" -> ctx.timeOfDay;
      case "worldstress_zone" -> ctx.worldstressZone;
      case "debug_hq_mode" -> ctx.debugHqMode;
      case "conversation_result" -> ctx.conversationResult;
      case "conversation_next_step" -> ctx.conversationNextStep;
      case "nq_generation_possible" -> String.valueOf(ctx.nqGenerationPossible);
      case "block_reason" -> ctx.blockReason;
      case "quest_type" -> ctx.questType;
      case "quest_subtype" -> ctx.questSubtype;
      case "reward_state" -> ctx.rewardState;
      case "reward_text_mode" -> ctx.rewardTextMode;
      case "na_reason" -> ctx.naReason;
      default -> "";
    };
  }

  /**
   * Matches a filter spec against a value.
   *
   * Supported spec formats:
   * - "*" (any)
   * - "a" (exact)
   * - "a|b|c" (OR)
   * - "none|N/A" etc.
   */
  private static boolean matchesSpec(String spec, String wanted) {
    if (spec == null || spec.isEmpty() || "*".equals(spec)) return true;
    String w = (wanted == null) ? "" : wanted;
    if (spec.indexOf('|') >= 0) {
      String[] parts = spec.split("\\|");
      for (String p : parts) {
        if (p != null && p.trim().equals(w)) return true;
      }
      return false;
    }
    return spec.trim().equals(w);
  }

  private int pickIndex(int n) {
    if (n <= 1) return 0;
    int idx = (int) Math.floor(nextFloat01() * n);
    if (idx < 0) idx = 0;
    if (idx >= n) idx = n - 1;
    return idx;
  }

  private long nextLong() {
    long x = rng;
    x ^= x >>> 12;
    x ^= x << 25;
    x ^= x >>> 27;
    rng = x;
    return x * 2685821657736338717L;
  }

  private float nextFloat01() {
    return ((nextLong() >>> 40) & 0xFFFFFFL) / (float) (1 << 24);
  }

  private static void requireCtx(ConversationContext ctx) {
    if (ctx == null) throw new IllegalArgumentException("ConversationContext missing");
  }

  private static String join3(String a, String b, String c) {
    StringBuilder sb = new StringBuilder();
    if (a != null && !a.isEmpty()) sb.append(a.trim());
    if (b != null && !b.isEmpty()) {
      if (sb.length() > 0) sb.append(' ');
      sb.append(b.trim());
    }
    if (c != null && !c.isEmpty()) {
      if (sb.length() > 0) sb.append(' ');
      sb.append(c.trim());
    }
    return sb.toString();
  }

  // ------------------- built result -------------------

  public static final class BuiltText {
    public SnippetDef main;
    public SnippetDef middle;
    public SnippetDef end;
    public SnippetDef na;
    public String full = "";

    public String mainText() { return (main != null) ? main.template : ""; }
    public String middleText() { return (middle != null) ? middle.template : ""; }
    public String endText() { return (end != null) ? end.template : ""; }
    public String naText() { return (na != null) ? na.template : ""; }
  }
}

package com.yourgame.survival.quest.zqs.generator;

/**
 * Deterministic quest id factory.
 *
 * Canonical plan format includes typ/stress/SL/QNR/player.
 * Stress/player name are not invented here; v1 uses a stable placeholder playerTag.
 */
public final class ZqsQuestIdFactory {
  private ZqsQuestIdFactory() {}

  public static String buildNqId(int slId, int questNrCounter, String playerTag,
                                String blueprintId, String targetType, String targetId, int amount) {

    String typ = "NQ";
    String sl = String.valueOf(Math.max(0, slId));
    String qnr = pad5(Math.max(0, questNrCounter));
    String p = (playerTag != null && !playerTag.isEmpty()) ? playerTag : "P0";

    String h1 = hex4(fnv1a32(blueprintId));
    String h2 = hex4(fnv1a32(targetType + ":" + targetId + ":" + amount));

    return typ + sl + h1 + h2 + qnr + p;
  }

  private static String pad5(int n) {
    String s = String.valueOf(n);
    if (s.length() >= 5) return s;
    StringBuilder sb = new StringBuilder();
    for (int i = s.length(); i < 5; i++) sb.append('0');
    sb.append(s);
    return sb.toString();
  }

  private static int fnv1a32(String s) {
    int h = 0x811C9DC5;
    if (s == null) return h;
    for (int i = 0; i < s.length(); i++) {
      h ^= (s.charAt(i) & 0xff);
      h *= 0x01000193;
    }
    return h;
  }

  private static String hex4(int v) {
    int x = v;
    char[] out = new char[4];
    for (int i = 3; i >= 0; i--) {
      int n = x & 0xF;
      out[i] = (char) (n < 10 ? ('0' + n) : ('A' + (n - 10)));
      x >>>= 4;
    }
    return new String(out);
  }
}


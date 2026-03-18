package com.yourgame.survival.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Local profile storage.
 *
 * Stored data (only):
 * - username
 * - localId (random 8 digits)
 */
public final class PlayerProfileManager {
  public static final String LOCAL_PATH = "config/player_profiles.json";

  private PlayerProfileManager() {}

  public static ArrayList<PlayerProfile> loadAll() {
    ArrayList<PlayerProfile> out = new ArrayList<>();
    try {
      FileHandle fh = Gdx.files.local(LOCAL_PATH);
      if (!fh.exists()) return out;

      JsonValue root = new JsonReader().parse(fh.readString("UTF-8"));
      JsonValue arr = root.get("profiles");
      if (arr == null || !arr.isArray()) return out;

      for (JsonValue p = arr.child; p != null; p = p.next) {
        String id = p.getString("localId", "");
        String name = p.getString("username", "");
        if (id == null) id = "";
        if (name == null) name = "";
        id = id.trim();
        name = name.trim();
        if (id.length() == 0 || name.length() == 0) continue;
        out.add(new PlayerProfile(id, name));
      }
    } catch (Throwable ignored) {
      // failsafe: return empty
    }
    return out;
  }

  public static void saveAll(ArrayList<PlayerProfile> profiles) {
    if (profiles == null) profiles = new ArrayList<>();
    FileHandle fh = Gdx.files.local(LOCAL_PATH);
    try { fh.parent().mkdirs(); } catch (Throwable ignored) {}

    StringBuilder sb = new StringBuilder();
    sb.append("{\n");
    sb.append("  \"version\": 1,\n");
    sb.append("  \"profiles\": [\n");
    boolean first = true;
    for (PlayerProfile p : profiles) {
      if (p == null) continue;
      String id = safe(p.localId);
      String name = safe(p.username);
      if (id.length() == 0 || name.length() == 0) continue;
      if (!first) sb.append(",\n");
      first = false;
      sb.append("    { \"localId\": \"").append(jsonEscape(id)).append("\", \"username\": \"")
          .append(jsonEscape(name)).append("\" }");
    }
    sb.append("\n  ]\n");
    sb.append("}\n");

    fh.writeString(sb.toString(), false, "UTF-8");
  }

  public static PlayerProfile createNew(String username, ArrayList<PlayerProfile> existing) {
    String name = safe(username).trim();
    String id = generateUniqueLocalId(existing);
    return new PlayerProfile(id, name);
  }

  public static String generateUniqueLocalId(ArrayList<PlayerProfile> existing) {
    SecureRandom r = new SecureRandom();
    for (int tries = 0; tries < 1000; tries++) {
      String id = randomDigits(r, 8);
      if (!containsId(existing, id)) return id;
    }
    // fallback: still return something
    return randomDigits(r, 8);
  }

  private static boolean containsId(ArrayList<PlayerProfile> arr, String id) {
    if (arr == null) return false;
    for (PlayerProfile p : arr) {
      if (p == null) continue;
      if (id != null && id.equals(p.localId)) return true;
    }
    return false;
  }

  private static String randomDigits(SecureRandom r, int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) sb.append((char)('0' + r.nextInt(10)));
    return sb.toString();
  }

  private static String safe(String s) {
    return (s == null) ? "" : s;
  }

  private static String jsonEscape(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '\\') sb.append("\\\\");
      else if (c == '"') sb.append("\\\"");
      else if (c == '\n') sb.append("\\n");
      else if (c == '\r') sb.append("\\r");
      else if (c == '\t') sb.append("\\t");
      else sb.append(c);
    }
    return sb.toString();
  }
}

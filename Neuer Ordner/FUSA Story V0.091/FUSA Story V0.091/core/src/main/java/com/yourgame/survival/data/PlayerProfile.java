package com.yourgame.survival.data;

/**
 * Minimal player identity stored locally.
 *
 * Requirements:
 * - localId: random 8 digits (string)
 * - username: display name for other players
 */
public final class PlayerProfile {
  public String localId;   // 8 digits
  public String username;  // free text

  public PlayerProfile() {}

  public PlayerProfile(String localId, String username) {
    this.localId = localId;
    this.username = username;
  }
}

package com.yourgame.survival.systems;

public final class DayNightSystem {
  // 0..1
  public float t = 0f;

  // seconds per full day
  public float daySeconds = 4320f; // 72 min real-time per full day (24h ingame)

  // Phase durations in seconds (sum must equal daySeconds)
  public float sunriseSeconds = 240f;
  public float daySecondsPart = 2400f;
  public float sunsetSeconds = 240f;
  public float nightSecondsPart = 1440f;

  public void tick(float dt) {
    t += dt / daySeconds;
    t -= (float)Math.floor(t);
  }

  /** 0=night, 1=day */
  public float daylight() {
    // cosine: day around t=0.25..0.75
    float a = (float)Math.cos((t - 0.5f) * 2f * Math.PI);
    // a=1 at midnight, -1 at noon => invert
    float d = 0.5f - 0.5f * a;
    if (d < 0f) d = 0f;
    if (d > 1f) d = 1f;
    return d;
  }

  // Returns 0..daySeconds within the current day.
  public float dayTimeSeconds() {
    return t * daySeconds;
  }

  // 0..1 day brightness based on phases (exact 4/40/4/24 minutes @ 72min day).
  public float daylightPhased() {
    float s = dayTimeSeconds();
    float sunriseEnd = sunriseSeconds;
    float dayEnd = sunriseEnd + daySecondsPart;
    float sunsetEnd = dayEnd + sunsetSeconds;

    if (s < sunriseEnd) {
      return s / Math.max(1e-6f, sunriseSeconds);
    }
    if (s < dayEnd) return 1f;
    if (s < sunsetEnd) {
      float u = (s - dayEnd) / Math.max(1e-6f, sunsetSeconds);
      return 1f - u;
    }
    return 0f;
  }

  // 0..1 warm tint factor during dusk/dawn.
  public float warmTwilight() {
    float s = dayTimeSeconds();
    float sunriseEnd = sunriseSeconds;
    float dayEnd = sunriseEnd + daySecondsPart;
    float sunsetEnd = dayEnd + sunsetSeconds;

    if (s < sunriseEnd) {
      float u = s / Math.max(1e-6f, sunriseSeconds);
      // peak warm around middle
      return 1f - Math.abs(u - 0.5f) * 2f;
    }
    if (s < dayEnd) return 0f;
    if (s < sunsetEnd) {
      float u = (s - dayEnd) / Math.max(1e-6f, sunsetSeconds);
      return 1f - Math.abs(u - 0.5f) * 2f;
    }
    return 0f;
  }
}

package com.yourgame.survival.worldmap;

/**
 * 4-way cardinal directions for Area-grid navigation.
 *
 * Design decision:
 * - We keep this tiny enum instead of using ints (0..3) because it makes all
 *   WorldMap rules self-documenting and avoids "what does 2 mean again?" bugs.
 */
public enum Dir4 {
  N(0, 1),
  E(1, 0),
  S(0, -1),
  W(-1, 0);

  public final int dax;
  public final int day;

  Dir4(int dax, int day) {
    this.dax = dax;
    this.day = day;
  }

  public Dir4 opposite() {
    switch (this) {
      case N: return S;
      case E: return W;
      case S: return N;
      case W: return E;
    }
    return N; // unreachable, but keeps Java happy
  }
}

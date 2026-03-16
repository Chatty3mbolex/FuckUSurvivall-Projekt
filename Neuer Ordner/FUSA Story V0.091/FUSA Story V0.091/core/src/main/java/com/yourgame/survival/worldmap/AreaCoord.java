package com.yourgame.survival.worldmap;

import java.util.Objects;

/** Grid coordinate for an Area on the WorldMap. */
public final class AreaCoord {
  public final int ax;
  public final int ay;

  public AreaCoord(int ax, int ay) {
    this.ax = ax;
    this.ay = ay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AreaCoord)) return false;
    AreaCoord that = (AreaCoord) o;
    return ax == that.ax && ay == that.ay;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ax, ay);
  }

  @Override
  public String toString() {
    return "(" + ax + "," + ay + ")";
  }
}

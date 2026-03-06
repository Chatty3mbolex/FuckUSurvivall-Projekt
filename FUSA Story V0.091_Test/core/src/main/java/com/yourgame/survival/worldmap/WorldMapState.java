package com.yourgame.survival.worldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Per-player persistent WorldMap state.
 *
 * Note: This is a pure data container; generation rules/templates live elsewhere.
 */
public final class WorldMapState {
  /** areaCoord -> templateId (fixed once discovered). */
  public final Map<AreaCoord, String> knownAreas = new HashMap<>();

  /** Unknown but visible slots on the map (question marks). */
  public final Set<AreaCoord> frontier = new HashSet<>();

  /** Connectivity edges between areas (line on map). */
  public final ArrayList<Edge> edges = new ArrayList<>();

  /** Currently loaded/active area. */
  public int curAx = 0;
  public int curAy = 0;
  public String curTemplateId = "";

  public void setCurrent(int ax, int ay, String templateId) {
    this.curAx = ax;
    this.curAy = ay;
    this.curTemplateId = (templateId != null) ? templateId : "";
  }

  public static final class Edge {
    public int ax1;
    public int ay1;
    public int ax2;
    public int ay2;
    /** Optional style/type hint (e.g. road/field). Connectivity exists regardless. */
    public String type;

    public Edge() {}

    public Edge(int ax1, int ay1, int ax2, int ay2, String type) {
      this.ax1 = ax1;
      this.ay1 = ay1;
      this.ax2 = ax2;
      this.ay2 = ay2;
      this.type = (type != null) ? type : "";
    }
  }

  // ============================================================
  // Additional per-area info required by FUSA WorldMap UI
  // ============================================================

  /**
   * Per known area: which exits exist.
   *
   * Requirement:
   * - WorldMap UI must show "exit to" information.
   * - In alpha, every side is an exit, but this must still be stored in savegames
   *   for later quest-gating/one-way rules.
   */
  public final Map<AreaCoord, AreaExits> exitsByArea = new HashMap<>();

  /**
   * Per area: snapshot from the last time the player LEFT the area ("war stand").
   * This is used when zooming into an area that is not currently loaded.
   */
  public final Map<AreaCoord, AreaState> areaStates = new HashMap<>();

  public static final class AreaExits {
    public boolean n;
    public boolean e;
    public boolean s;
    public boolean w;

    public AreaExits() {}

    public AreaExits(boolean n, boolean e, boolean s, boolean w) {
      this.n = n;
      this.e = e;
      this.s = s;
      this.w = w;
    }
  }

  public static final class AreaState {
    public int ax;
    public int ay;
    public String templateId;

    // Mini-map snapshot ("war stand")
    public int miniW;
    public int miniH;
    public int miniScale;
    public String miniMapB64;

    // Fog of War (persistent)
    // Stored as a byte alpha map (fogW*fogH bytes, 0..255), base64.
    // 255 = fully black (unknown), explored cells are lowered toward exploredAlpha.
    public int fogW;
    public int fogH;
    public int fogScale;
    public String fogBitsB64;

    // Optional: when we left the area (day/night system)
    public int leftDayIndex;
    public float leftDayT;

    public AreaState() {}
  }
}


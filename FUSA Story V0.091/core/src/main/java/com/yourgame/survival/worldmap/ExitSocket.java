package com.yourgame.survival.worldmap;

/**
 * One exit/socket on a template edge.
 *
 * This is the *contract* between two Areas: "If I leave area A to the east via
 * this socket, what kind of neighbor is allowed/likely to exist there?".
 *
 * We are intentionally NOT implementing width/offset here yet.
 * Rationale:
 * - Alpha scope wants the *world structure* (grid + connectivity + fixed discovery)
 *   first.
 * - Exact edge geometry can be added later without breaking savegames, because
 *   savegames store only templateId + connections, not the detailed exit geometry.
 */
public final class ExitSocket {
  /** Which side of the area this exit is on. */
  public final Dir4 dir;

  /**
   * Semantic tag describing the connection type.
   * Examples: "road", "field", "forest_edge", "river", "city_gate".
   *
   * Important: in our WorldMap UI, the line means "passable connection exists",
   * not necessarily a road. The tag is a *hint* for later selection/visuals.
   */
  public final String tag;

  /** If false: connection exists to a specific neighbor but does NOT allow creating a new area beyond it. */
  public final boolean newArea;

  /**
   * One-way exits are allowed by design, but MUST be explicit.
   * Default should be both-way (= false for oneWay).
   */
  public final boolean oneWay;

  public ExitSocket(Dir4 dir, String tag, boolean newArea, boolean oneWay) {
    this.dir = dir;
    this.tag = (tag != null) ? tag : "";
    this.newArea = newArea;
    this.oneWay = oneWay;
  }
}

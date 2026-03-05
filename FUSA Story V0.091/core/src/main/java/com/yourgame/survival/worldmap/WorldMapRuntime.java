package com.yourgame.survival.worldmap;

import java.util.Random;

/**
 * Runtime logic for discovering and stitching Areas on the WorldMap.
 *
 * This is the "brain" that mutates {@link WorldMapState}.
 * It does NOT load real tilemaps yet (alpha scaffolding).
 *
 * Why split Runtime vs State?
 * - State is purely save/load data.
 * - Runtime contains rules (random selection, frontier expansion, against-checks).
 *   This separation keeps savegames stable while we iterate rules.
 */
public final class WorldMapRuntime {
  /**
   * Alpha scope template IDs.
   * Design decision: hardcode these strings in one place.
   * Reason: we want the project to run even before we have JSON template files.
   * Later we can load these from assets and keep IDs stable.
   */
  public static final String T_HOME = "HOME";
  public static final String T_GRASS = "GEN_GRASSLAND";
  public static final String T_ROCKY = "GEN_ROCKY_FIELDS";
  public static final String T_FOREST = "GEN_LIGHT_FOREST";

  private final AreaTemplateRegistry reg;

  public WorldMapRuntime(AreaTemplateRegistry reg) {
    this.reg = reg;
  }

  /**
   * Creates a minimal alpha registry in code (until templates are loaded from files).
   *
   * Constraints implemented:
   * - Home is NOT pickable.
   * - Alpha generator areas have "no blocked exits" (so they can connect in all 4 directions).
   *
   * We use the same tag "field" for now because tags are a weighting hint.
   * Weighting is not implemented yet (alpha wants uniform random).
   */
  public static AreaTemplateRegistry createAlphaRegistry() {
    AreaTemplateRegistry r = new AreaTemplateRegistry();

    // HOME: start area, never randomly picked.
    r.register(new AreaTemplate(T_HOME, "Home", false)
        .exit(new ExitSocket(Dir4.N, "field", true, false))
        .exit(new ExitSocket(Dir4.E, "field", true, false))
        .exit(new ExitSocket(Dir4.S, "field", true, false))
        .exit(new ExitSocket(Dir4.W, "field", true, false)));

    // Generators: pickable, all 4 exits, no blocked exits in alpha.
    r.register(new AreaTemplate(T_GRASS, "Grassland", true)
        .exit(new ExitSocket(Dir4.N, "field", true, false))
        .exit(new ExitSocket(Dir4.E, "field", true, false))
        .exit(new ExitSocket(Dir4.S, "field", true, false))
        .exit(new ExitSocket(Dir4.W, "field", true, false)));

    r.register(new AreaTemplate(T_ROCKY, "Rocky fields", true)
        .exit(new ExitSocket(Dir4.N, "field", true, false))
        .exit(new ExitSocket(Dir4.E, "field", true, false))
        .exit(new ExitSocket(Dir4.S, "field", true, false))
        .exit(new ExitSocket(Dir4.W, "field", true, false)));

    r.register(new AreaTemplate(T_FOREST, "Light forest", true)
        .exit(new ExitSocket(Dir4.N, "field", true, false))
        .exit(new ExitSocket(Dir4.E, "field", true, false))
        .exit(new ExitSocket(Dir4.S, "field", true, false))
        .exit(new ExitSocket(Dir4.W, "field", true, false)));

    return r;
  }

  /**
   * Ensures the state has a Home at (0,0).
   *
   * Design decision:
   * - We treat missing map state as "new game".
   *   Reason: older saves (v1/v2) didn't have worldMap.
   */
  public void bootstrapIfEmpty(WorldMapState st) {
    if (st == null) return;
    if (st.knownAreas.isEmpty()) {
      st.setCurrent(0, 0, T_HOME);
      st.knownAreas.put(new AreaCoord(0, 0), T_HOME);
    }

    // Frontier is derived from exits around known areas.
    // We keep it in the save because it drives the UI, but we can re-derive it.
    rebuildFrontier(st);
  }

  /**
   * Recomputes frontier from known areas + template exits.
   *
   * Important rule from design:
   * - Unknown should be shown only where a connection/exit exists.
   *   If a side has no exit => no frontier mark.
   */
  public void rebuildFrontier(WorldMapState st) {
    if (st == null) return;
    st.frontier.clear();

    for (var entry : st.knownAreas.entrySet()) {
      AreaCoord c = entry.getKey();
      String tid = entry.getValue();
      AreaTemplate t = reg.get(tid);
      if (t == null) continue;

      for (Dir4 d : Dir4.values()) {
        ExitSocket s = t.exit(d);
        if (s == null) continue;
        if (!s.newArea) continue;

        int nx = c.ax + d.dax;
        int ny = c.ay + d.day;
        AreaCoord nc = new AreaCoord(nx, ny);

        if (!st.knownAreas.containsKey(nc)) {
          st.frontier.add(nc);
        }
      }
    }
  }

  /**
   * Discovers/enters a neighbor area.
   *
   * Inputs:
   * - st: save state to mutate
   * - dir: where we are trying to go from the current area
   * - seed: per-save seed used to pick a template deterministically
   *
   * Output:
   * - The discovered neighbor templateId (existing if already discovered).
   *
   * Against-checks (Gegenchecks):
   * - In the final system we MUST ensure that if a neighbor exists and it has no
   *   exit back, we cannot create an exit towards it.
   * - In alpha scaffolding, every template has exits on all sides => the check
   *   will always pass. We still structure the code so the check exists.
   */
  public String discoverOrEnterNeighbor(WorldMapState st, Dir4 dir, long seed) {
    if (st == null) return null;
    if (dir == null) return null;

    // Nicht fertiges Feature: // AreaCoord cur = new AreaCoord(st.curAx, st.curAy); // (unused)
    String curTid = st.curTemplateId;
    AreaTemplate curT = reg.get(curTid);
    if (curT == null) return null;

    ExitSocket out = curT.exit(dir);
    if (out == null) {
      // Rule: no exit => no connection.
      return null;
    }

    int nx = st.curAx + dir.dax;
    int ny = st.curAy + dir.day;
    AreaCoord nc = new AreaCoord(nx, ny);

    // If already discovered, we just "enter" it.
    String existing = st.knownAreas.get(nc);
    if (existing != null && !existing.isEmpty()) {
      // IMPORTANT: compute the old coordinate BEFORE changing st.curAx/curAy.
      // (Otherwise we'd accidentally write a self-edge.)
      int ox = st.curAx;
      int oy = st.curAy;

      st.setCurrent(nx, ny, existing);
      addOrKeepEdge(st, ox, oy, nx, ny, out.tag);
      rebuildFrontier(st);
      return existing;
    }

    // Pick a template (alpha: uniform random among the 3 generator templates).
    String picked = pickAlphaTemplate(seed, nx, ny);

    // Against-check structure (currently trivial in alpha).
    if (!passesAgainstChecks(st, nc, picked)) {
      // In later versions we'd try other templates here.
      // For scaffolding we just refuse the discovery.
      return null;
    }

    st.knownAreas.put(nc, picked);
    st.setCurrent(nx, ny, picked);

    // Connection line: means passable transition exists.
    addOrKeepEdge(st, nx - dir.dax, ny - dir.day, nx, ny, out.tag);

    rebuildFrontier(st);
    return picked;
  }

  /**
   * Alpha template picker.
   *
   * Decision + justification:
   * - The selection must be stable per save once discovered.
   * - We do NOT need cryptographic randomness, only reproducible variety.
   * - We incorporate (ax,ay) so each coordinate has its own deterministic choice.
   */
  private static String pickAlphaTemplate(long seed, int ax, int ay) {
    // Very small deterministic hash.
    long s = seed;
    s ^= (ax * 0x9E3779B97F4A7C15L);
    s ^= (ay * 0xC2B2AE3D27D4EB4FL);

    Random r = new Random(s);
    int v = r.nextInt(3);
    if (v == 0) return T_GRASS;
    if (v == 1) return T_ROCKY;
    return T_FOREST;
  }

  /**
   * Stub for against-checks.
   *
   * Later behavior:
   * - For each neighbor that already exists, check if the neighbor template has
   *   a compatible exit back to us. If not, the candidate template is invalid.
   *
   * In alpha:
   * - All templates have all exits => always true.
   */
  private boolean passesAgainstChecks(WorldMapState st, AreaCoord target, String candidateTemplateId) {
    if (st == null || target == null) return false;

    AreaTemplate cand = reg.get(candidateTemplateId);
    if (cand == null) return false;

    // Check each of the 4 neighbors only if they already exist.
    for (Dir4 d : Dir4.values()) {
      AreaCoord nb = new AreaCoord(target.ax + d.dax, target.ay + d.day);
      String nbTid = st.knownAreas.get(nb);
      if (nbTid == null || nbTid.isEmpty()) continue;

      AreaTemplate nbT = reg.get(nbTid);
      if (nbT == null) continue;

      // If neighbor has no exit towards us, we must not have an exit towards it (and vice versa).
      // In the final implementation we also check tags/compatibility.
      boolean nbHasBack = nbT.exit(d.opposite()) != null;
      boolean candHasToNb = cand.exit(d) != null;

      if (nbHasBack != candHasToNb) {
        return false;
      }
    }

    return true;
  }

  /**
   * Adds an edge if it does not exist yet.
   *
   * Design decision:
   * - We store edges explicitly in the save, even though they can be derived
   *   from templates + knownAreas.
   * - Reason: later we may have one-way exits or special "blocked" transitions
   *   that are not symmetrical or are quest-gated.
   */
  private static void addOrKeepEdge(WorldMapState st, int ax1, int ay1, int ax2, int ay2, String type) {
    if (st == null) return;

    // Normalize (so (A,B) equals (B,A) for simple undirected alpha edges).
    int nax1 = ax1;
    int nay1 = ay1;
    int nax2 = ax2;
    int nay2 = ay2;
    if (nax2 < nax1 || (nax2 == nax1 && nay2 < nay1)) {
      int tx = nax1; int ty = nay1;
      nax1 = nax2; nay1 = nay2;
      nax2 = tx; nay2 = ty;
    }

    for (int i = 0; i < st.edges.size(); i++) {
      WorldMapState.Edge e = st.edges.get(i);
      if (e == null) continue;
      if (e.ax1 == nax1 && e.ay1 == nay1 && e.ax2 == nax2 && e.ay2 == nay2) return;
    }

    st.edges.add(new WorldMapState.Edge(nax1, nay1, nax2, nay2, type));
  }
}

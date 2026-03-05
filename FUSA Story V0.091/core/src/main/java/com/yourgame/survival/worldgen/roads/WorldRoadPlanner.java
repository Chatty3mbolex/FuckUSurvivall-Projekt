package com.yourgame.survival.worldgen.roads;

import com.yourgame.survival.biome.BiomeSystem;
import com.yourgame.survival.world.Biome;
import com.yourgame.survival.world.TileLayers;
import com.yourgame.survival.world.World;
import com.yourgame.survival.worldgen.WorldGenContext;
import com.yourgame.survival.worldgen.biome.BiomeClassifier;
import com.yourgame.survival.worldgen.noise.NoiseSampler;
import com.yourgame.survival.worldgen.util.FinalTileSampler;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * WorldGen-native road planner.
 *
 * Does NOT rely on BiomeSystem's internal biome-noise copy.
 * Biome sampling uses FinalTileSampler + worldgen config so roads stay consistent when worldgen.json changes.
 *
 * Still uses BiomeSystem as the source of POI templates/road enable flags.
 */
public final class WorldRoadPlanner implements RoadPlanner {
  // ---- HARD CAPS / BUDGETS ----
  // These are intentionally conservative so "New Game" can't freeze even if many POIs require roads.
  private static final int MAX_TARGETS_PER_CHUNK = 6;
  private static final int MAX_NODES_PER_CHUNK = 12; // anchors + targets
  private static final int MAX_ASTAR_CALLS_PER_CHUNK = 8;
  private static final int MAX_ASTAR_ITERS_PER_CALL = 2048;
  private static final int MAX_RECONSTRUCT_STEPS = 2048;

  private final NoiseSampler noise;
  private final BiomeClassifier classifier;

  // Reused buffers to avoid per-A* allocations.
  private final AStarBuffers aStar = new AStarBuffers(World.CHUNK_SIZE * World.CHUNK_SIZE);

  public WorldRoadPlanner(NoiseSampler noise, BiomeClassifier classifier) {
    this.noise = noise;
    this.classifier = classifier;
  }

  @Override
  public void buildRoadMask(TileLayers layers, int cx, int cy, WorldGenContext ctx) {
    buildRoadMask(layers.roadMask, cx, cy, ctx);
  }

  /** Package-private so generation modules can reuse the exact same road mask logic without copying. */
  void buildRoadMask(byte[] rm, int cx, int cy, WorldGenContext ctx) {
    final int size = World.CHUNK_SIZE;

    // Determine biome at chunk center with worldgen logic.
    int cTx = cx * size + (size / 2);
    int cTy = cy * size + (size / 2);
    Biome centerBiome = FinalTileSampler.biomeAt(cTx, cTy, ctx, noise, classifier);

    BiomeSystem.BiomeDef def = ctx.biomes.def(centerBiome);
    for (int i = 0; i < rm.length; i++) rm[i] = 0;
    if (!def.roadsEnabled) return;

    // POIs that require roads.
    ArrayList<BiomeSystem.PoiSpawn> pois = ctx.biomes.computePoisForChunk(cx, cy, centerBiome);
    ArrayList<BiomeSystem.PoiSpawn> targets = new ArrayList<>();
    for (int i = 0; i < pois.size(); i++) if (pois.get(i).requiresRoad) targets.add(pois.get(i));
    if (targets.isEmpty()) return;

    // Hard cap targets deterministically.
    capTargetsDeterministic(targets, ctx.seed, cx, cy, cTx, cTy);

    ArrayList<Anchor> anchors = computeBorderAnchors(cx, cy, targets, ctx);
    if (anchors.isEmpty() && targets.isEmpty()) return;

    // Nodes = anchors + targets. Build an MST on Manhattan distance to avoid dumb branches.
    ArrayList<Node> nodes = new ArrayList<>();
    for (Anchor a : anchors) nodes.add(new Node(a.tx, a.ty, true));
    for (BiomeSystem.PoiSpawn p : targets) nodes.add(new Node(p.tx, p.ty, false));

    // Hard cap total nodes deterministically (prefer anchors; then closest targets to chunk center).
    if (nodes.size() > MAX_NODES_PER_CHUNK) {
      nodes = capNodesDeterministic(nodes, ctx.seed, cTx, cTy);
    }

    int n = nodes.size();
    if (n <= 0) return;
    if (n == 1) {
      stampRoadDot(rm, cx, cy, nodes.get(0).tx, nodes.get(0).ty);
      return;
    }

    // Prim MST
    boolean[] used = new boolean[n];
    int[] parent = new int[n];
    int[] best = new int[n];
    for (int i = 0; i < n; i++) { parent[i] = -1; best[i] = Integer.MAX_VALUE; }
    best[0] = 0;

    for (int iter = 0; iter < n; iter++) {
      int v = -1;
      int bv = Integer.MAX_VALUE;
      for (int i = 0; i < n; i++) {
        if (!used[i] && best[i] < bv) { bv = best[i]; v = i; }
      }
      if (v < 0) break;
      used[v] = true;

      for (int u = 0; u < n; u++) {
        if (used[u]) continue;
        int dist = manhattan(nodes.get(v), nodes.get(u));
        if (dist < best[u]) { best[u] = dist; parent[u] = v; }
      }
    }

    // Route each MST edge with a cheap grid path.
    int aStarCalls = 0;
    for (int u = 1; u < n; u++) {
      if (aStarCalls >= MAX_ASTAR_CALLS_PER_CHUNK) break;

      int v = parent[u];
      if (v < 0) continue;
      aStarCalls++;
      carvePathAStar(rm, cx, cy, nodes.get(u).tx, nodes.get(u).ty, nodes.get(v).tx, nodes.get(v).ty,
          aStar, MAX_ASTAR_ITERS_PER_CALL, MAX_RECONSTRUCT_STEPS);
    }
  }

  private static void capTargetsDeterministic(ArrayList<BiomeSystem.PoiSpawn> targets,
                                             long seed,
                                             int cx,
                                             int cy,
                                             int cTx,
                                             int cTy) {
    if (targets.size() <= MAX_TARGETS_PER_CHUNK) return;

    final int baseTx = cx * World.CHUNK_SIZE;
    final int baseTy = cy * World.CHUNK_SIZE;

    // Prefer targets nearer to chunk center; tie-break by deterministic hash.
    targets.sort(new Comparator<>() {
      @Override
      public int compare(BiomeSystem.PoiSpawn a, BiomeSystem.PoiSpawn b) {
        int da = Math.abs(a.tx - cTx) + Math.abs(a.ty - cTy);
        int db = Math.abs(b.tx - cTx) + Math.abs(b.ty - cTy);
        if (da != db) return Integer.compare(da, db);

        long ha = mix64(seed ^ 0xD0D0A11CL ^ pack(a.tx - baseTx, a.ty - baseTy));
        long hb = mix64(seed ^ 0xD0D0A11CL ^ pack(b.tx - baseTx, b.ty - baseTy));
        return Long.compareUnsigned(ha, hb);
      }
    });

    while (targets.size() > MAX_TARGETS_PER_CHUNK) targets.remove(targets.size() - 1);
  }

  private static ArrayList<Node> capNodesDeterministic(ArrayList<Node> nodes, long seed, int cTx, int cTy) {
    // Keep all anchors if possible.
    ArrayList<Node> anchors = new ArrayList<>();
    ArrayList<Node> targets = new ArrayList<>();
    for (int i = 0; i < nodes.size(); i++) {
      Node n = nodes.get(i);
      if (n.anchor) anchors.add(n);
      else targets.add(n);
    }

    // Anchors should already be <=4; still guard deterministically.
    if (anchors.size() > MAX_NODES_PER_CHUNK) {
      anchors.sort(Comparator.comparingLong(a -> mix64(seed ^ 0xA11C0FFEE1234L ^ pack(a.tx, a.ty))));
      while (anchors.size() > MAX_NODES_PER_CHUNK) anchors.remove(anchors.size() - 1);
      return anchors;
    }

    int remaining = MAX_NODES_PER_CHUNK - anchors.size();
    if (targets.size() > remaining) {
      targets.sort(new Comparator<>() {
        @Override
        public int compare(Node a, Node b) {
          int da = Math.abs(a.tx - cTx) + Math.abs(a.ty - cTy);
          int db = Math.abs(b.tx - cTx) + Math.abs(b.ty - cTy);
          if (da != db) return Integer.compare(da, db);
          long ha = mix64(seed ^ 0xBEEF00D5L ^ pack(a.tx, a.ty));
          long hb = mix64(seed ^ 0xBEEF00D5L ^ pack(b.tx, b.ty));
          return Long.compareUnsigned(ha, hb);
        }
      });
      while (targets.size() > remaining) targets.remove(targets.size() - 1);
    }

    ArrayList<Node> out = new ArrayList<>(anchors.size() + targets.size());
    out.addAll(anchors);
    out.addAll(targets);
    return out;
  }

  private ArrayList<Anchor> computeBorderAnchors(int cx, int cy, ArrayList<BiomeSystem.PoiSpawn> targets, WorldGenContext ctx) {
    ArrayList<Anchor> out = new ArrayList<>();

    // Only create anchors if there is at least one road-required POI either in this chunk
    // OR in the adjacent chunk close enough to the border.
    if (targets.isEmpty()) return out;

    int size = World.CHUNK_SIZE;
    int baseTx = cx * size;
    int baseTy = cy * size;

    for (int side = 0; side < 4; side++) {
      boolean need = hasAnyTargetNearBorder(targets, baseTx, baseTy, side);

      // Also check neighbor chunk's road-required POIs.
      int ncx = cx;
      int ncy = cy;
      int opposite;
      switch (side) {
        case 0 -> { ncy = cy + 1; opposite = 2; }
        case 1 -> { ncx = cx + 1; opposite = 3; }
        case 2 -> { ncy = cy - 1; opposite = 0; }
        default -> { ncx = cx - 1; opposite = 1; }
      }

      if (!need) {
        int nTx = ncx * size + (size / 2);
        int nTy = ncy * size + (size / 2);
        Biome nb = FinalTileSampler.biomeAt(nTx, nTy, ctx, noise, classifier);

        ArrayList<BiomeSystem.PoiSpawn> nPois = ctx.biomes.computePoisForChunk(ncx, ncy, nb);
        ArrayList<BiomeSystem.PoiSpawn> nTargets = new ArrayList<>();
        for (int i = 0; i < nPois.size(); i++) if (nPois.get(i).requiresRoad) nTargets.add(nPois.get(i));

        int nBaseTx = ncx * size;
        int nBaseTy = ncy * size;
        need = hasAnyTargetNearBorder(nTargets, nBaseTx, nBaseTy, opposite);
      }

      if (!need) continue;

      long ek = edgeKey(cx, cy, side);
      long h = mix64(ctx.seed ^ 0xBEEFBEEF12345678L ^ ek);

      int t = 2 + (int) (Math.floorMod(h >>> 8, size - 4));
      int tx, ty;
      switch (side) {
        case 0 -> { tx = baseTx + t; ty = baseTy + (size - 1); }
        case 1 -> { tx = baseTx + (size - 1); ty = baseTy + t; }
        case 2 -> { tx = baseTx + t; ty = baseTy; }
        default -> { tx = baseTx; ty = baseTy + t; }
      }
      out.add(new Anchor(tx, ty));
    }

    return out;
  }

  private static boolean hasAnyTargetNearBorder(ArrayList<BiomeSystem.PoiSpawn> targets, int baseTx, int baseTy, int side) {
    int size = World.CHUNK_SIZE;
    for (int i = 0; i < targets.size(); i++) {
      BiomeSystem.PoiSpawn p = targets.get(i);
      int lx = p.tx - baseTx;
      int ly = p.ty - baseTy;
      int dist;
      switch (side) {
        case 0 -> dist = (size - 1) - ly;
        case 1 -> dist = (size - 1) - lx;
        case 2 -> dist = ly;
        default -> dist = lx;
      }
      if (dist <= Math.max(0, p.connectRadiusTiles)) return true;
    }
    return false;
  }

  private static void stampRoadDot(byte[] rm, int cx, int cy, int tx, int ty) {
    int baseTx = cx * World.CHUNK_SIZE;
    int baseTy = cy * World.CHUNK_SIZE;
    int lx = tx - baseTx;
    int ly = ty - baseTy;
    if (lx < 0 || ly < 0 || lx >= World.CHUNK_SIZE || ly >= World.CHUNK_SIZE) return;
    rm[lx + ly * World.CHUNK_SIZE] = 1;
  }

  private static int manhattan(Node a, Node b) {
    return Math.abs(a.tx - b.tx) + Math.abs(a.ty - b.ty);
  }

  private static void carvePathAStar(byte[] rm,
                                     int cx,
                                     int cy,
                                     int sx,
                                     int sy,
                                     int gx,
                                     int gy,
                                     AStarBuffers buf,
                                     int maxIters,
                                     int maxReconstructSteps) {
    int baseTx = cx * World.CHUNK_SIZE;
    int baseTy = cy * World.CHUNK_SIZE;
    int sxL = sx - baseTx;
    int syL = sy - baseTy;
    int gxL = gx - baseTx;
    int gyL = gy - baseTy;

    sxL = clamp(sxL, 0, World.CHUNK_SIZE - 1);
    syL = clamp(syL, 0, World.CHUNK_SIZE - 1);
    gxL = clamp(gxL, 0, World.CHUNK_SIZE - 1);
    gyL = clamp(gyL, 0, World.CHUNK_SIZE - 1);

    int size = World.CHUNK_SIZE;
    int n = size * size;
    buf.reset(n);

    int start = sxL + syL * size;
    int goal = gxL + gyL * size;

    buf.gScore[start] = 0;
    buf.fScore[start] = heuristic(sxL, syL, gxL, gyL);
    buf.open[start] = 1;

    int iters = Math.max(0, maxIters);
    for (int iter = 0; iter < iters; iter++) {
      int current = -1;
      int bestF = Integer.MAX_VALUE;
      for (int i = 0; i < n; i++) {
        if (buf.open[i] == 0) continue;
        int f = buf.fScore[i];
        if (f < bestF) { bestF = f; current = i; }
      }
      if (current < 0) break;
      if (current == goal) break;

      buf.open[current] = 0;
      buf.closed[current] = 1;

      int cxL = current % size;
      int cyL = current / size;

      // stable order N,E,S,W
      expandNeighbor(rm, size, cxL, cyL + 1, current, goal, buf);
      expandNeighbor(rm, size, cxL + 1, cyL, current, goal, buf);
      expandNeighbor(rm, size, cxL, cyL - 1, current, goal, buf);
      expandNeighbor(rm, size, cxL - 1, cyL, current, goal, buf);
    }

    int cur = goal;
    int guard = 0;
    int maxSteps = Math.max(0, maxReconstructSteps);
    while (cur >= 0 && guard++ < maxSteps) {
      int x = cur % size;
      int y = cur / size;
      rm[x + y * size] = 1;
      if (cur == start) break;
      cur = buf.came[cur];
    }
  }

  private static void expandNeighbor(byte[] rm,
                                     int size,
                                     int nx,
                                     int ny,
                                     int current,
                                     int goal,
                                     AStarBuffers buf) {
    if (nx < 0 || ny < 0 || nx >= size || ny >= size) return;
    int ni = nx + ny * size;
    if (buf.closed[ni] != 0) return;

    int stepCost = (rm[ni] != 0) ? 8 : 10;

    int tentative = safeAdd(buf.gScore[current], stepCost);
    if (tentative < buf.gScore[ni]) {
      buf.came[ni] = current;
      buf.gScore[ni] = tentative;
      int gx = goal % size;
      int gy = goal / size;
      buf.fScore[ni] = safeAdd(tentative, heuristic(nx, ny, gx, gy));
      buf.open[ni] = 1;
    }
  }

  private static int heuristic(int x, int y, int gx, int gy) {
    return (Math.abs(x - gx) + Math.abs(y - gy)) * 10;
  }

  private static int safeAdd(int a, int b) {
    if (a == Integer.MAX_VALUE) return Integer.MAX_VALUE;
    long s = (long) a + (long) b;
    return (s >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) s;
  }

  private static int clamp(int v, int lo, int hi) {
    return (v < lo) ? lo : (v > hi) ? hi : v;
  }

  private static long pack(int a, int b) {
    return (((long) a) << 32) ^ (b & 0xffffffffL);
  }

  private static long edgeKey(int cx, int cy, int side) {
    int ax = cx;
    int ay = cy;
    int bx = cx;
    int by = cy;
    switch (side) {
      case 0 -> by = cy + 1;
      case 1 -> bx = cx + 1;
      case 2 -> by = cy - 1;
      case 3 -> bx = cx - 1;
    }

    long ka = pack(ax, ay);
    long kb = pack(bx, by);
    long lo = Math.min(ka, kb);
    long hi = Math.max(ka, kb);
    return mix64(lo ^ (hi * 0x9E3779B97F4A7C15L) ^ (side * 0xC2B2AE3D27D4EB4FL));
  }

  private static long mix64(long z) {
    z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
    z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
    return z ^ (z >>> 33);
  }

  private static final class AStarBuffers {
    final int[] gScore;
    final int[] fScore;
    final int[] came;
    final byte[] open;
    final byte[] closed;

    AStarBuffers(int n) {
      gScore = new int[n];
      fScore = new int[n];
      came = new int[n];
      open = new byte[n];
      closed = new byte[n];
      reset(n);
    }

    void reset(int n) {
      for (int i = 0; i < n; i++) {
        gScore[i] = Integer.MAX_VALUE;
        fScore[i] = Integer.MAX_VALUE;
        came[i] = -1;
        open[i] = 0;
        closed[i] = 0;
      }
    }
  }

  private record Node(int tx, int ty, boolean anchor) {}
  private record Anchor(int tx, int ty) {}
}

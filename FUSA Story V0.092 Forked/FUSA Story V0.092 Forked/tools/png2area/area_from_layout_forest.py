#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Convert Gebietslayout_forest.png into assets/areas/FOREST_01.area.json

Offline-only tool.
Assumptions (based on existing Gebietslayout generator):
- Image is 4040x4040
- Tile canvas is 3840x3840 with a 100px margin on all sides
- 1 tile = 10x10 pixels
- Sample each tile by reading the pixel at tile center (margin + tx*10+5, margin+ty*10+5)

Legend mapping (user-confirmed):
- dirt: the 4 large core areas (brown)
- grass area: ring around dirt areas with smooth transitions (tan/brown)
- road: red
- trees area: ring where trees are allowed (brownish)
- trees: filled olive circles -> spawn nodes (trees + a few rocks/iron/bush)
- enemy spawn area: green circle outlines -> extract circles as zones
- POI chest: yellow fill with green outline -> one-time POI at that tile

Output JSON schema is an extension of JsonAreaWorldLoader expectations.
Missing keys should be ignored by loader.

Guardrails:
- Uses strict-ish nearest-color matching on tile-center pixels.
- Clamps all coords into [0..383].
"""

from __future__ import annotations

import json
import math
from dataclasses import dataclass
from pathlib import Path

from PIL import Image


def find_project_root(start: Path) -> Path:
    for p in [start] + list(start.parents):
        if (p / "assets").is_dir():
            return p
    return start

ROOT = find_project_root(Path(__file__).resolve().parent)
IMG_PATH = ROOT / "Gebietslayout_forest.png"
OUT_PATH = ROOT / "assets" / "areas" / "FOREST_01.area.json"

TILES_W = 384
TILES_H = 384
MARGIN = 100
TILE_PX = 10

# --- Color helpers ---

def rgb_dist2(a, b):
    return (a[0]-b[0])**2 + (a[1]-b[1])**2 + (a[2]-b[2])**2


def is_transparent(px):
    return len(px) == 4 and px[3] < 10


# Representative palette picks (will be refined by sampling from image at runtime)
# These are only used for nearest-match; the tool will also print the most common sampled colors.
PAL = {
    # These were derived from the most common sampled tile-center colors in Gebietslayout_forest.png.
    # (Printed by the tool on first run.)
    "dirt": (177, 102, 76),
    "grass": (184, 139, 78),
    "trees_area": (185, 122, 87),
    "road": (200, 95, 72),
    "trees_dot": (147, 162, 66),
    "poi_yellow": (222, 229, 35),
}


def classify_tile(rgb):
    """Classify a tile using nearest-color matching (robust vs similar browns).

    Order of operations:
    1) Hard-detect POI yellow.
    2) Nearest palette match among road/dirt/grass/trees_area/trees_dot.
    3) If the nearest is trees_dot but it's not close enough, fall back to ground.
    """
    r, g, b = rgb

    # POI: bright yellow (very distinctive)
    if r > 190 and g > 190 and b < 150:
        return "poi"

    # Nearest palette match
    choices = ("road", "dirt", "grass", "trees_area", "trees_dot")
    best = None
    best_d = 1e18
    for k in choices:
        d = rgb_dist2(rgb, PAL[k])
        if d < best_d:
            best_d = d
            best = k

    if best == "trees_dot":
        # Require a reasonably close match; otherwise treat as ground.
        if best_d > 35 * 35:
            # choose nearest ground-ish
            gbest = None
            gbest_d = 1e18
            for k in ("dirt", "grass", "trees_area"):
                d = rgb_dist2(rgb, PAL[k])
                if d < gbest_d:
                    gbest_d = d
                    gbest = k
            return gbest
        return "trees"

    return best


@dataclass
class Circle:
    cx: float
    cy: float
    r: float


def find_green_outline_circles(img: Image.Image) -> list[Circle]:
    """Detect green outline circles (enemy spawn zones).

    Approach: create a binary mask for 'green-ish outline' pixels and run connected components.
    For each component, estimate circle center/radius from bounding box.
    """
    rgba = img.convert("RGBA")
    w, h = rgba.size
    px = rgba.load()

    # Downsample factor for speed (must still preserve circles)
    ds = 2
    w2, h2 = w // ds, h // ds

    mask = [[False]*w2 for _ in range(h2)]
    for y in range(h2):
        yy = y*ds
        for x in range(w2):
            xx = x*ds
            r, g, b, a = px[xx, yy]
            if a < 20:
                continue
            # green outline threshold
            if g > 160 and r < 120 and b < 120:
                mask[y][x] = True

    seen = [[False]*w2 for _ in range(h2)]

    comps = []
    for y in range(h2):
        row = mask[y]
        for x in range(w2):
            if not row[x] or seen[y][x]:
                continue
            # BFS
            q = [(x, y)]
            seen[y][x] = True
            minx = maxx = x
            miny = maxy = y
            n = 0
            while q:
                cx, cy = q.pop()
                n += 1
                if cx < minx: minx = cx
                if cx > maxx: maxx = cx
                if cy < miny: miny = cy
                if cy > maxy: maxy = cy
                for dx, dy in ((1,0),(-1,0),(0,1),(0,-1)):
                    nx, ny = cx+dx, cy+dy
                    if nx < 0 or ny < 0 or nx >= w2 or ny >= h2:
                        continue
                    if seen[ny][nx] or not mask[ny][nx]:
                        continue
                    seen[ny][nx] = True
                    q.append((nx, ny))
            # Filter tiny comps (tree dots outlines etc.)
            bw = (maxx - minx + 1)
            bh = (maxy - miny + 1)
            if n < 400 or bw < 80 or bh < 80:
                continue
            comps.append((minx, miny, maxx, maxy, n))

    circles = []
    for minx, miny, maxx, maxy, n in comps:
        # bbox center in full-res coords
        cx = ((minx + maxx) * 0.5) * ds
        cy = ((miny + maxy) * 0.5) * ds
        # radius from bbox (approx)
        r = (max(maxx - minx, maxy - miny) * 0.5) * ds
        circles.append(Circle(cx=cx, cy=cy, r=r))

    # Deduplicate near-identical circles
    out = []
    for c in circles:
        ok = True
        for o in out:
            if (c.cx-o.cx)**2 + (c.cy-o.cy)**2 < 25 and abs(c.r-o.r) < 10:
                ok = False
                break
        if ok:
            out.append(c)

    return out


def px_to_tile(px_x: float, px_y: float):
    tx = int((px_x - MARGIN) // TILE_PX)
    ty = int((px_y - MARGIN) // TILE_PX)
    tx = max(0, min(TILES_W-1, tx))
    ty = max(0, min(TILES_H-1, ty))
    return tx, ty


def main():
    if not IMG_PATH.exists():
        raise SystemExit(f"Missing input: {IMG_PATH}")

    img = Image.open(IMG_PATH).convert("RGBA")
    w, h = img.size
    if (w, h) != (4040, 4040):
        print(f"WARNING: expected 4040x4040, got {w}x{h}")

    px = img.load()

    # Sample tile-center colors and count common values (debug / palette tuning)
    from collections import Counter
    ctr = Counter()

    ground_dirt = []
    ground_grass = []
    road_tiles = []
    tree_markers = []
    poi_tiles = []

    for ty in range(TILES_H):
        py = MARGIN + ty*TILE_PX + TILE_PX//2
        for tx in range(TILES_W):
            px_x = MARGIN + tx*TILE_PX + TILE_PX//2
            rgba = px[px_x, py]
            if rgba[3] < 10:
                continue
            rgb = rgba[:3]
            ctr[rgb] += 1
            c = classify_tile(rgb)
            if c == "road":
                road_tiles.append((tx, ty))
            elif c == "trees":
                tree_markers.append((tx, ty))
            elif c == "poi":
                poi_tiles.append((tx, ty))
            elif c == "dirt":
                ground_dirt.append((tx, ty))
            else:
                # grass or trees_area -> for now treat as grass ground
                ground_grass.append((tx, ty))

    print("Top sampled RGBs:")
    for rgb, n in ctr.most_common(15):
        print(f"  {rgb}: {n}")

    # Enemy zones from green outlines
    circles_px = find_green_outline_circles(img)
    enemy_zones = []
    for c in circles_px:
        tx, ty = px_to_tile(c.cx, c.cy)
        # radius in tiles (use px->tiles). keep fixed-ish.
        r_tiles = max(4, int(round(c.r / TILE_PX)))
        enemy_zones.append({
            "kind": "ORK_ZONE",
            "cx": tx,
            "cy": ty,
            "r": r_tiles,
            "min": 10,
            "max": 15,
            "respawnDaysMin": 1,
            "respawnDaysMax": 2
        })

    # POI: pick most central yellow tile if multiple
    poi = []
    if poi_tiles:
        # choose the one with max saturation-like heuristic = max (r+g-b)
        best = None
        best_s = -1
        for tx, ty in poi_tiles:
            px_x = MARGIN + tx*TILE_PX + TILE_PX//2
            py = MARGIN + ty*TILE_PX + TILE_PX//2
            r, g, b, a = px[px_x, py]
            s = (r+g) - b
            if s > best_s:
                best_s = s
                best = (tx, ty)
        tx, ty = best
        poi.append({"kind": "HIDDEN_CHEST", "x": tx, "y": ty})

    # Nodes: trees from markers, plus a few rocks/iron/bush deterministically.
    # We keep it fixed (no runtime randomness). Use a stride selection.
    nodes = []

    # --- Node budget guardrail ---
    # Entities.MAX is 2048, but we must leave room for player/builds/merchants/orks/drops.
    # Keep authored nodes well below that.
    MAX_AUTHORED_NODES = 1400

    # 1) Thin the painted "trees" markers aggressively (they are just a hint ring, not every dot must become a node).
    # Keep ~1/8 deterministically.
    tree_markers_sorted = sorted(set(tree_markers))
    thinned = [p for i, p in enumerate(tree_markers_sorted) if (i % 8) == 0]

    # Mix in a few rocks/iron/bush among the ring nodes.
    for i, (tx, ty) in enumerate(thinned):
        kind = "NODE_TREE"
        if i % 19 == 0:
            kind = "NODE_BUSH"
        elif i % 14 == 0:
            kind = "NODE_ORE_IRON"
        elif i % 10 == 0:
            kind = "NODE_ROCK"
        nodes.append({"t": kind, "x": tx, "y": ty})

    # 2) Dense forest inside DIRT blobs (4 separate areas).
    # We detect connected components on dirt tiles and then sample tree positions per component.
    dirt_set = set(ground_dirt)

    def dirt_components():
        seen = set()
        comps = []
        for p in dirt_set:
            if p in seen:
                continue
            stack = [p]
            seen.add(p)
            comp = []
            while stack:
                x, y = stack.pop()
                comp.append((x, y))
                for nx, ny in ((x+1,y),(x-1,y),(x,y+1),(x,y-1)):
                    q = (nx, ny)
                    if q in dirt_set and q not in seen:
                        seen.add(q)
                        stack.append(q)
            comps.append(comp)
        return comps

    comps = dirt_components()

    # Deterministic RNG from image-derived content
    base_seed = 0xF045E7

    def sample_dense_trees(comp, comp_index):
        # target density: roughly 1 tree per 1..4 tiles depending on randomness,
        # but capped to keep entity budget.
        # Interpret user request (0.3..2 tiles) as "very dense with some gaps".
        area_tiles = len(comp)
        target = min(420, max(140, area_tiles // 220))  # tuned for large dirt blobs

        r = __import__("random").Random((base_seed << 32) ^ (comp_index * 0x9E3779B97F4A7C15) ^ area_tiles)

        comp_list = list(comp)
        r.shuffle(comp_list)

        # occupancy grid for spacing (tile-based)
        occ = set()
        out = []
        for (tx, ty) in comp_list:
            if len(out) >= target:
                break

            # random spacing: 1 or 2 tiles (occasionally allow adjacent)
            minD = 1 if r.random() < 0.55 else 2

            ok = True
            for oy in range(-minD, minD+1):
                for ox in range(-minD, minD+1):
                    if (tx+ox, ty+oy) in occ:
                        ok = False
                        break
                if not ok:
                    break
            if not ok:
                continue

            occ.add((tx, ty))
            out.append((tx, ty))
        return out

    dense_points = []
    for ci, comp in enumerate(sorted(comps, key=len, reverse=True)):
        # Keep the 4 largest components (matches the 4 dirt areas)
        if ci >= 4:
            break
        dense_points.extend(sample_dense_trees(comp, ci))

    # Add dense forest nodes (all trees)
    for (tx, ty) in dense_points:
        nodes.append({"t": "NODE_TREE", "x": tx, "y": ty})

    # Final clamp to budget (deterministic)
    if len(nodes) > MAX_AUTHORED_NODES:
        nodes = nodes[:MAX_AUTHORED_NODES]

    # Ground: default grass. Add a dirt patch list from dirt tiles.
    # Compact into rectangles (simple run-length per row).
    dirt_fills = []
    dirt_set = set(ground_dirt)
    for ty in range(TILES_H):
        x = 0
        while x < TILES_W:
            if (x, ty) not in dirt_set:
                x += 1
                continue
            x0 = x
            while x < TILES_W and (x, ty) in dirt_set:
                x += 1
            x1 = x - 1
            dirt_fills.append({"x": x0, "y": ty, "w": (x1-x0+1), "h": 1, "id": 1})  # dirt id=1

    # Road: thin by one step to match desired width (~50% narrower).
    # Keep only tiles that have enough road neighbors (removes outer band / anti-aliased edges).
    road_set0 = set(road_tiles)
    road_set = set()
    for (tx, ty) in road_set0:
        n = 0
        if (tx+1, ty) in road_set0: n += 1
        if (tx-1, ty) in road_set0: n += 1
        if (tx, ty+1) in road_set0: n += 1
        if (tx, ty-1) in road_set0: n += 1
        # Require at least 2 cardinal neighbors to survive (centerline-ish)
        if n >= 2:
            road_set.add((tx, ty))

    # Compact to runs
    road_fills = []
    for ty in range(TILES_H):
        x = 0
        while x < TILES_W:
            if (x, ty) not in road_set:
                x += 1
                continue
            x0 = x
            while x < TILES_W and (x, ty) in road_set:
                x += 1
            x1 = x - 1
            road_fills.append({"x": x0, "y": ty, "w": (x1-x0+1), "h": 1, "v": 1})

    out = {
        "size": {"w": TILES_W, "h": TILES_H},
        "layers": {
            "ground": {
                "defaultId": 0,
                "fills": dirt_fills
            },
            "road": {
                "fills": road_fills
            }
        },
        "markers": {
            # player spawn left for manual tweak after first run; default center-ish
            "playerSpawn": {"x": 192, "y": 192},
            "nodes": nodes,
            "enemyZones": enemy_zones,
            "poi": poi
        }
    }

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUT_PATH.write_text(json.dumps(out, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Wrote: {OUT_PATH}")
    print(f"- dirt tiles: {len(ground_dirt)}")
    print(f"- road tiles: {len(road_tiles)}")
    print(f"- tree markers: {len(tree_markers_sorted)}")
    print(f"- enemy zones: {len(enemy_zones)}")
    print(f"- poi candidates: {len(poi_tiles)} -> used {len(poi)}")


if __name__ == "__main__":
    main()

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""convert_area.py — Universal PNG→Area converter for FUSA Story.

Usage:
    python convert_area.py <layout.png> <AREA_ID> [--seed N]

Example:
    python convert_area.py Gebietslayout_forest.png FOREST_01
    python convert_area.py Gebietslayout_swamp.png SWAMP_01 --seed 42

Output:
    assets/areas/<AREA_ID>.area.json

Features:
    - Color classification from PNG legend
    - Ground grid extraction (Grass/Dirt/Sand/Rock/Snow)
    - Road zone detection + morphological erosion for natural width
    - Tree/Node marker extraction
    - Enemy spawn zone detection (green outline circles)
    - POI detection (yellow markers)
    - Corner-mask precomputation (Marching Squares) for terrain transitions
    - Deterministic from seed
"""

from __future__ import annotations

import argparse
import json
import math
import sys
from collections import Counter
from dataclasses import dataclass, field
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("ERROR: Pillow is required. Install with: pip install Pillow")
    sys.exit(1)

# ============================================================
# Constants
# ============================================================

TILES_W = 384
TILES_H = 384
MARGIN = 100
TILE_PX = 10
MAX_AUTHORED_NODES = 1400

# Ground IDs (must match TileIds.java)
GID_GRASS = 0
GID_DIRT = 1
GID_SAND = 2
GID_ROCK = 3
GID_SNOW = 4

# ============================================================
# Color palette — representative RGB values for nearest-match
# ============================================================

PALETTE = {
    "dirt":       (177, 102, 76),
    "grass":      (184, 139, 78),
    "trees_area": (185, 122, 87),
    "road":       (200, 95, 72),
    "trees_dot":  (147, 162, 66),
    "sand":       (210, 190, 130),
    "rock":       (140, 140, 140),
    "snow":       (230, 230, 240),
}


def rgb_dist2(a, b):
    return (a[0]-b[0])**2 + (a[1]-b[1])**2 + (a[2]-b[2])**2


def classify_tile(rgb):
    r, g, b = rgb

    # POI: bright yellow
    if r > 190 and g > 190 and b < 150:
        return "poi"

    # Snow: very bright, low saturation
    if r > 210 and g > 210 and b > 210:
        return "snow"

    # Rock: gray-ish
    if abs(r - g) < 20 and abs(g - b) < 20 and r > 120 and r < 170:
        return "rock"

    # Sand: warm bright
    if r > 190 and g > 170 and b > 110 and b < 160:
        return "sand"

    # Nearest palette match for remaining
    choices = ("road", "dirt", "grass", "trees_area", "trees_dot")
    best = None
    best_d = 1e18
    for k in choices:
        d = rgb_dist2(rgb, PALETTE[k])
        if d < best_d:
            best_d = d
            best = k

    if best == "trees_dot":
        if best_d > 35 * 35:
            gbest = None
            gbest_d = 1e18
            for k in ("dirt", "grass", "trees_area"):
                d = rgb_dist2(rgb, PALETTE[k])
                if d < gbest_d:
                    gbest_d = d
                    gbest = k
            return gbest
        return "trees"

    return best


# ============================================================
# Circle detection for enemy spawn zones
# ============================================================

@dataclass
class Circle:
    cx: float
    cy: float
    r: float


def find_green_outline_circles(img: Image.Image) -> list[Circle]:
    rgba = img.convert("RGBA")
    w, h = rgba.size
    px = rgba.load()

    ds = 2
    w2, h2 = w // ds, h // ds

    mask = [[False]*w2 for _ in range(h2)]
    for y in range(h2):
        for x in range(w2):
            r, g, b, a = px[x*ds, y*ds]
            if a < 20:
                continue
            if g > 160 and r < 120 and b < 120:
                mask[y][x] = True

    seen = [[False]*w2 for _ in range(h2)]
    comps = []

    for y in range(h2):
        for x in range(w2):
            if not mask[y][x] or seen[y][x]:
                continue
            q = [(x, y)]
            seen[y][x] = True
            minx = maxx = x
            miny = maxy = y
            n = 0
            while q:
                cx, cy = q.pop()
                n += 1
                minx = min(minx, cx)
                maxx = max(maxx, cx)
                miny = min(miny, cy)
                maxy = max(maxy, cy)
                for dx2, dy2 in ((1,0),(-1,0),(0,1),(0,-1)):
                    nx, ny = cx+dx2, cy+dy2
                    if 0 <= nx < w2 and 0 <= ny < h2 and not seen[ny][nx] and mask[ny][nx]:
                        seen[ny][nx] = True
                        q.append((nx, ny))

            if n < 400 or (maxx - minx + 1) < 80 or (maxy - miny + 1) < 80:
                continue
            comps.append((minx, miny, maxx, maxy))

    circles = []
    for minx, miny, maxx, maxy in comps:
        cx = ((minx + maxx) * 0.5) * ds
        cy = ((miny + maxy) * 0.5) * ds
        r = (max(maxx - minx, maxy - miny) * 0.5) * ds
        circles.append(Circle(cx=cx, cy=cy, r=r))

    # Deduplicate
    out = []
    for c in circles:
        ok = True
        for o in out:
            if (c.cx-o.cx)**2 + (c.cy-o.cy)**2 < 625 and abs(c.r-o.r) < 50:
                ok = False
                break
        if ok:
            out.append(c)
    return out


def px_to_tile(px_x, px_y):
    tx = int((px_x - MARGIN) // TILE_PX)
    ty = int((px_y - MARGIN) // TILE_PX)
    return max(0, min(TILES_W-1, tx)), max(0, min(TILES_H-1, ty))


# ============================================================
# Marching Squares Corner Masks
# ============================================================

# Transition allow matrix (must match TransitionRules.java)
def allow_transition(base_gid: int, target_gid: int) -> bool:
    if base_gid == target_gid:
        return False
    if base_gid == GID_GRASS:
        return target_gid in (GID_SAND, GID_SNOW, GID_ROCK)
    if base_gid == GID_DIRT:
        return target_gid in (GID_SAND, GID_GRASS, GID_SNOW, GID_ROCK)
    if base_gid == GID_SAND:
        return False
    if base_gid == GID_SNOW:
        return target_gid in (GID_GRASS, GID_DIRT, GID_ROCK)
    if base_gid == GID_ROCK:
        return target_gid in (GID_SAND, GID_DIRT, GID_SNOW, GID_ROCK)
    return True


def corner_inside(ground_grid, w, h, cx, cy, target_gid):
    """Check 4 tiles sharing this corner. Return 1 if >=2 match target."""
    count = 0
    for dy in (0, -1):
        for dx in (0, -1):
            tx = cx + dx
            ty = cy + dy
            if 0 <= tx < w and 0 <= ty < h:
                if ground_grid[ty * w + tx] == target_gid:
                    count += 1
            # Out-of-bounds tiles don't match
    return 1 if count >= 2 else 0


def compute_corner_mask(ground_grid, w, h, lx, ly, target_gid):
    """Compute 4-bit marching squares mask for a tile."""
    nw = corner_inside(ground_grid, w, h, lx, ly + 1, target_gid)
    ne = corner_inside(ground_grid, w, h, lx + 1, ly + 1, target_gid)
    se = corner_inside(ground_grid, w, h, lx + 1, ly, target_gid)
    sw = corner_inside(ground_grid, w, h, lx, ly, target_gid)
    return nw | (ne << 1) | (se << 2) | (sw << 3)


def bake_all_corner_masks(ground_grid, w, h):
    """Compute corner masks for all 5 edge types. Returns dict of lists."""
    targets = {
        "grass": GID_GRASS,
        "dirt": GID_DIRT,
        "sand": GID_SAND,
        "rock": GID_ROCK,
        "snow": GID_SNOW,
    }
    masks = {k: [0] * (w * h) for k in targets}

    for ty in range(h):
        for tx in range(w):
            idx = ty * w + tx
            base = ground_grid[idx]

            for name, tgt in targets.items():
                if base == tgt or not allow_transition(base, tgt):
                    masks[name][idx] = 0
                else:
                    masks[name][idx] = compute_corner_mask(ground_grid, w, h, tx, ty, tgt)

    return masks


# ============================================================
# Road processing — morphological erosion for natural width
# ============================================================

def erode_road(road_set, iterations=1):
    """Remove outer ring of road tiles. Tiles need >=3 cardinal neighbors to survive."""
    result = set(road_set)
    for _ in range(iterations):
        to_remove = set()
        for (tx, ty) in result:
            n = sum(1 for dx, dy in ((1,0),(-1,0),(0,1),(0,-1))
                    if (tx+dx, ty+dy) in result)
            if n < 3:
                to_remove.add((tx, ty))
        result -= to_remove
    return result


def skeleton_road(road_set):
    """Thin road to approximate centerline by repeated erosion until stable."""
    current = set(road_set)
    while True:
        # Erosion: keep tiles with >=2 neighbors, but don't break connectivity
        to_remove = set()
        for (tx, ty) in current:
            n = sum(1 for dx, dy in ((1,0),(-1,0),(0,1),(0,-1))
                    if (tx+dx, ty+dy) in current)
            if n < 2:
                to_remove.add((tx, ty))

        if not to_remove:
            break
        # Don't remove if it would disconnect neighbors
        safe_remove = set()
        for p in to_remove:
            # Simple check: only remove if all neighbors of p that are in current
            # are also neighbors of each other (stays connected)
            safe_remove.add(p)

        if not safe_remove:
            break
        current -= safe_remove
        if len(current) < 10:
            break
    return current


def compute_road_mask4(road_set, w, h):
    """Compute 4-neighbor adjacency mask for road tiles."""
    result = {}
    for (tx, ty) in road_set:
        n = 1 if (tx, ty+1) in road_set else 0
        e = 1 if (tx+1, ty) in road_set else 0
        s = 1 if (tx, ty-1) in road_set else 0
        west = 1 if (tx-1, ty) in road_set else 0
        result[(tx, ty)] = n | (e << 1) | (s << 2) | (west << 3)
    return result


# ============================================================
# Node generation
# ============================================================

def generate_nodes(tree_markers, dirt_tiles, seed):
    import random
    nodes = []

    # 1) Ring nodes from tree markers (thin to ~1/8)
    sorted_markers = sorted(set(tree_markers))
    thinned = [p for i, p in enumerate(sorted_markers) if i % 8 == 0]

    for i, (tx, ty) in enumerate(thinned):
        kind = "NODE_TREE"
        if i % 19 == 0: kind = "NODE_BUSH"
        elif i % 14 == 0: kind = "NODE_ORE_IRON"
        elif i % 10 == 0: kind = "NODE_ROCK"
        nodes.append({"t": kind, "x": tx, "y": ty})

    # 2) Dense forest in dirt blobs
    dirt_set = set(dirt_tiles)
    comps = find_connected_components(dirt_set)
    comps.sort(key=len, reverse=True)

    rng = random.Random(seed)
    for ci, comp in enumerate(comps[:4]):
        target = min(420, max(140, len(comp) // 220))
        comp_list = list(comp)
        rng.shuffle(comp_list)

        occ = set()
        placed = 0
        for (tx, ty) in comp_list:
            if placed >= target:
                break
            min_d = 1 if rng.random() < 0.55 else 2
            ok = True
            for oy in range(-min_d, min_d+1):
                for ox in range(-min_d, min_d+1):
                    if (tx+ox, ty+oy) in occ:
                        ok = False
                        break
                if not ok:
                    break
            if not ok:
                continue
            occ.add((tx, ty))
            nodes.append({"t": "NODE_TREE", "x": tx, "y": ty})
            placed += 1

    if len(nodes) > MAX_AUTHORED_NODES:
        nodes = nodes[:MAX_AUTHORED_NODES]

    return nodes


def find_connected_components(tile_set):
    seen = set()
    comps = []
    for p in tile_set:
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
                if q in tile_set and q not in seen:
                    seen.add(q)
                    stack.append(q)
        comps.append(comp)
    return comps


# ============================================================
# RLE compression for ground fills
# ============================================================

def rle_fills(tile_set, ground_id):
    """Compress a set of (tx,ty) into run-length fills."""
    fills = []
    rows = {}
    for (tx, ty) in tile_set:
        rows.setdefault(ty, []).append(tx)

    for ty in sorted(rows.keys()):
        xs = sorted(rows[ty])
        i = 0
        while i < len(xs):
            x0 = xs[i]
            while i + 1 < len(xs) and xs[i+1] == xs[i] + 1:
                i += 1
            fills.append({"x": x0, "y": ty, "w": xs[i] - x0 + 1, "h": 1, "id": ground_id})
            i += 1
    return fills


def rle_road_fills(tile_set):
    fills = []
    rows = {}
    for (tx, ty) in tile_set:
        rows.setdefault(ty, []).append(tx)

    for ty in sorted(rows.keys()):
        xs = sorted(rows[ty])
        i = 0
        while i < len(xs):
            x0 = xs[i]
            while i + 1 < len(xs) and xs[i+1] == xs[i] + 1:
                i += 1
            fills.append({"x": x0, "y": ty, "w": xs[i] - x0 + 1, "h": 1, "v": 1})
            i += 1
    return fills


# ============================================================
# Corner mask RLE compression
# ============================================================

def rle_corner_masks(mask_array, w, h):
    """Compress non-zero corner mask values into patches."""
    patches = []
    for idx in range(w * h):
        v = mask_array[idx]
        if v != 0:
            tx = idx % w
            ty = idx // w
            patches.append({"x": tx, "y": ty, "m": v})
    return patches


# ============================================================
# Main conversion
# ============================================================

def convert(img_path: Path, area_id: str, seed: int) -> dict:
    img = Image.open(img_path).convert("RGBA")
    w, h = img.size
    px = img.load()

    print(f"Input: {img_path} ({w}x{h})")
    print(f"Area ID: {area_id}")
    print(f"Seed: {seed}")

    # Classify all tiles
    ground_grid = [GID_GRASS] * (TILES_W * TILES_H)
    road_raw = set()
    tree_markers = []
    poi_tiles = []
    dirt_tiles = []

    color_counter = Counter()

    for ty in range(TILES_H):
        py = MARGIN + ty * TILE_PX + TILE_PX // 2
        for tx in range(TILES_W):
            px_x = MARGIN + tx * TILE_PX + TILE_PX // 2
            if px_x >= w or py >= h:
                continue
            rgba = px[px_x, py]
            if rgba[3] < 10:
                continue

            rgb = rgba[:3]
            color_counter[rgb] += 1
            c = classify_tile(rgb)

            idx = ty * TILES_W + tx
            if c == "road":
                road_raw.add((tx, ty))
                ground_grid[idx] = GID_DIRT  # Roads are on dirt ground
            elif c == "trees":
                tree_markers.append((tx, ty))
            elif c == "poi":
                poi_tiles.append((tx, ty))
            elif c == "dirt":
                ground_grid[idx] = GID_DIRT
                dirt_tiles.append((tx, ty))
            elif c == "sand":
                ground_grid[idx] = GID_SAND
            elif c == "rock":
                ground_grid[idx] = GID_ROCK
            elif c == "snow":
                ground_grid[idx] = GID_SNOW
            elif c in ("grass", "trees_area"):
                ground_grid[idx] = GID_GRASS
            else:
                ground_grid[idx] = GID_GRASS

    # Process roads: erode for natural width
    road_eroded = erode_road(road_raw, iterations=1)

    # Compute road adjacency
    road_mask4 = compute_road_mask4(road_eroded, TILES_W, TILES_H)

    # Detect enemy spawn zones
    circles = find_green_outline_circles(img)
    enemy_zones = []
    for c in circles:
        tx, ty = px_to_tile(c.cx, c.cy)
        r_tiles = max(4, int(round(c.r / TILE_PX)))
        enemy_zones.append({
            "kind": "ORK_ZONE",
            "cx": tx, "cy": ty, "r": r_tiles,
            "min": 10, "max": 15,
            "respawnDaysMin": 1, "respawnDaysMax": 2
        })

    # POIs
    pois = []
    if poi_tiles:
        best = max(poi_tiles, key=lambda p: px[MARGIN + p[0]*TILE_PX + 5, MARGIN + p[1]*TILE_PX + 5][0]
                   + px[MARGIN + p[0]*TILE_PX + 5, MARGIN + p[1]*TILE_PX + 5][1])
        pois.append({"kind": "HIDDEN_CHEST", "x": best[0], "y": best[1]})

    # Generate nodes
    nodes = generate_nodes(tree_markers, dirt_tiles, seed)

    # Bake corner masks
    print("Baking corner masks...")
    corner_masks = bake_all_corner_masks(ground_grid, TILES_W, TILES_H)

    # Compress corner masks to patches
    corner_patches = {}
    for name, mask_arr in corner_masks.items():
        patches = rle_corner_masks(mask_arr, TILES_W, TILES_H)
        if patches:
            corner_patches[name] = patches
        non_zero = sum(1 for v in mask_arr if v != 0)
        print(f"  {name}: {non_zero} transition tiles")

    # Build ground fills (only non-grass tiles, since grass is default)
    dirt_set = {(tx, ty) for ty in range(TILES_H) for tx in range(TILES_W)
                if ground_grid[ty * TILES_W + tx] == GID_DIRT}
    sand_set = {(tx, ty) for ty in range(TILES_H) for tx in range(TILES_W)
                if ground_grid[ty * TILES_W + tx] == GID_SAND}
    rock_set = {(tx, ty) for ty in range(TILES_H) for tx in range(TILES_W)
                if ground_grid[ty * TILES_W + tx] == GID_ROCK}
    snow_set = {(tx, ty) for ty in range(TILES_H) for tx in range(TILES_W)
                if ground_grid[ty * TILES_W + tx] == GID_SNOW}

    ground_fills = []
    ground_fills.extend(rle_fills(dirt_set, GID_DIRT))
    ground_fills.extend(rle_fills(sand_set, GID_SAND))
    ground_fills.extend(rle_fills(rock_set, GID_ROCK))
    ground_fills.extend(rle_fills(snow_set, GID_SNOW))

    # Build output JSON
    out = {
        "schema": "FUSA_AREA_V2",
        "templateId": area_id,
        "name": area_id,
        "size": {"w": TILES_W, "h": TILES_H},
        "layers": {
            "ground": {
                "defaultId": GID_GRASS,
                "fills": ground_fills
            },
            "road": {
                "fills": rle_road_fills(road_eroded)
            },
            "cornerMasks": corner_patches
        },
        "exits": {
            "N": {"tag": "field", "newArea": True, "oneWay": False},
            "E": {"tag": "field", "newArea": True, "oneWay": False},
            "S": {"tag": "field", "newArea": True, "oneWay": False},
            "W": {"tag": "field", "newArea": True, "oneWay": False}
        },
        "markers": {
            "playerSpawn": {"x": TILES_W // 2, "y": TILES_H // 2},
            "nodes": nodes,
            "enemyZones": enemy_zones,
            "poi": pois
        }
    }

    # Stats
    print(f"\nResults:")
    print(f"  Ground fills: {len(ground_fills)}")
    print(f"  Road tiles: {len(road_eroded)} (from {len(road_raw)} raw)")
    print(f"  Tree markers: {len(tree_markers)}")
    print(f"  Nodes: {len(nodes)}")
    print(f"  Enemy zones: {len(enemy_zones)}")
    print(f"  POIs: {len(pois)}")

    return out


def main():
    parser = argparse.ArgumentParser(description="Convert area layout PNG to FUSA area JSON")
    parser.add_argument("png", help="Path to the layout PNG (e.g. Gebietslayout_forest.png)")
    parser.add_argument("area_id", help="Area template ID (e.g. FOREST_01)")
    parser.add_argument("--seed", type=int, default=0xF045E7, help="Deterministic seed")
    parser.add_argument("--out", help="Output path (default: assets/areas/<AREA_ID>.area.json)")
    args = parser.parse_args()

    png_path = Path(args.png)
    if not png_path.exists():
        # Try relative to project root
        root = Path(__file__).resolve().parents[1]
        png_path = root / args.png
        if not png_path.exists():
            print(f"ERROR: PNG not found: {args.png}")
            sys.exit(1)

    root = Path(__file__).resolve().parents[1]

    if args.out:
        out_path = Path(args.out)
    else:
        out_path = root / "assets" / "areas" / f"{args.area_id}.area.json"

    result = convert(png_path, args.area_id, args.seed)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"\nWrote: {out_path}")
    print(f"File size: {out_path.stat().st_size / 1024:.1f} KB")


if __name__ == "__main__":
    main()

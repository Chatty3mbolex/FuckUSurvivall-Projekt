import re
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


def find_project_root(start: Path) -> Path:
    for p in [start] + list(start.parents):
        if (p / "assets").is_dir():
            return p
    return start


PROJ = find_project_root(Path(__file__).resolve().parent)
ATLAS = PROJ / "assets" / "atlas" / "static.atlas"
PNG = PROJ / "assets" / "atlas" / "static.png"
OUT = PROJ / "tools" / "png2area" / "Gebietslayout.png"

# Canvas sizes
AREA_PX = 3840
BORDER = 100
W = AREA_PX + BORDER*2
H = AREA_PX + BORDER*2

AREA_X0 = BORDER
AREA_Y0 = BORDER
AREA_X1 = BORDER + AREA_PX
AREA_Y1 = BORDER + AREA_PX

# Parse libGDX atlas file (TexturePacker format)
def parse_atlas(path: Path):
    text = path.read_text(encoding="utf-8", errors="ignore").splitlines()
    regions = {}
    page = None
    i = 0
    while i < len(text):
        line = text[i].rstrip("\n")
        if not line.strip():
            i += 1
            continue
        # page line: ends with .png typically
        if line.strip().lower().endswith((".png", ".jpg", ".jpeg")):
            page = line.strip()
            i += 1
            # skip page metadata lines until blank or region
            while i < len(text) and text[i].startswith(" "):
                i += 1
            continue
        # region name line (no indent)
        if not line.startswith(" "):
            name = line.strip()
            meta = {}
            i += 1
            while i < len(text) and text[i].startswith(" "):
                k,v = text[i].strip().split(":",1)
                meta[k.strip()] = v.strip()
                i += 1
            # required fields
            xy = tuple(int(x.strip()) for x in meta.get("xy","0,0").split(","))
            size = tuple(int(x.strip()) for x in meta.get("size","0,0").split(","))
            index = int(meta.get("index","-1"))
            regions.setdefault(name, {})[index] = {
                "page": page,
                "x": xy[0],
                "y": xy[1],
                "w": size[0],
                "h": size[1],
                "rotate": meta.get("rotate","false").lower() == "true",
            }
            continue
        i += 1
    return regions

regions = parse_atlas(ATLAS)

atlas_img = Image.open(PNG).convert("RGBA")

# Create transparent canvas
img = Image.new("RGBA", (W,H), (0,0,0,0))
d = ImageDraw.Draw(img)

# Grid inside area: every 10px
GRID = 10
line_col = (255,255,255,40)
for x in range(AREA_X0, AREA_X1+1, GRID):
    d.line([(x, AREA_Y0), (x, AREA_Y1)], fill=line_col, width=1)
for y in range(AREA_Y0, AREA_Y1+1, GRID):
    d.line([(AREA_X0, y), (AREA_X1, y)], fill=line_col, width=1)

# Outline area border stronger
outline_col = (255,255,255,120)
d.rectangle([AREA_X0, AREA_Y0, AREA_X1, AREA_Y1], outline=outline_col, width=2)

# Tiles to show (ground tiles used by program)
# Mapped from TilesetRegions.java constructor
GROUND_REGIONS = [
    ("ts_r00_c00", 0, "GROUND_GRASS"),
    ("ts_r00_c01", 0, "GROUND_DIRT"),
    ("ts_sand", 0, "GROUND_SAND"),
    ("ground_rock", 0, "GROUND_ROCK"),
    ("ground_snow", 0, "GROUND_SNOW"),
    ("ground_lava", 0, "GROUND_LAVA"),
]

# Font (small)
try:
    font = ImageFont.truetype("arial.ttf", 10)
except Exception:
    font = ImageFont.load_default()

# Place in right margin (no overlap)
start_x = AREA_X1 + 20
start_y = AREA_Y0 + 20
step_y = 48

for idx,(rname,rindex,label) in enumerate(GROUND_REGIONS):
    entry = regions.get(rname, {}).get(rindex)
    if entry is None:
        # mark missing
        y = start_y + idx*step_y
        d.rectangle([start_x, y, start_x+10, y+10], outline=(255,0,0,200), width=1)
        d.text((start_x, y+12), f"MISSING:{rname}", fill=(255,0,0,220), font=font)
        continue

    x,y,w,h = entry["x"], entry["y"], entry["w"], entry["h"]
    crop = atlas_img.crop((x,y,x+w,y+h))
    tile = crop.resize((10,10), resample=Image.NEAREST)

    px = start_x
    py = start_y + idx*step_y
    img.alpha_composite(tile, (px,py))
    # label below tile
    d.text((px, py+12), f"{label}\n({rname})", fill=(255,255,255,200), font=font, spacing=1)

# Add a header label in the top-left margin
header_font = font
try:
    header_font = ImageFont.truetype("arial.ttf", 14)
except Exception:
    pass

d.text((10,10), "Gebiet 384x384 Tiles (10px grid)  |  Chunk: 64x64 Tiles", fill=(255,255,255,180), font=header_font)

img.save(OUT)
print("WROTE", OUT)

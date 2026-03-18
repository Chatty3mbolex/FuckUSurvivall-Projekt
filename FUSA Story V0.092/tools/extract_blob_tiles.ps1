# extract_blob_tiles.ps1
# One-shot helper: slice 4x4 (16 tiles) from stripe sheets into single PNGs under assets/atlas/src.
# Uses System.Drawing (Windows). No external deps.

$ErrorActionPreference='Stop'

$root = Split-Path -Parent $PSScriptRoot
$assets = Join-Path $root 'assets'
$srcDir = Join-Path $assets 'atlas\src'
$terrain = Join-Path $assets 'terrain'

$tile = 32

function Slice-4x4([string]$inPng,[string]$outPrefix){
  Add-Type -AssemblyName System.Drawing
  $bmp = [System.Drawing.Bitmap]::new($inPng)
  try {
    for($ry=0;$ry -lt 4;$ry++){
      for($rx=0;$rx -lt 4;$rx++){
        $x=$rx*$tile
        $y=$ry*$tile
        $rect = [System.Drawing.Rectangle]::new($x,$y,$tile,$tile)
        $piece = $bmp.Clone($rect,$bmp.PixelFormat)
        try {
          $idx = $rx + $ry*4
          $out = Join-Path $srcDir ($outPrefix + '_' + $idx + '.png')
          $piece.Save($out,[System.Drawing.Imaging.ImageFormat]::Png)
        } finally {
          $piece.Dispose()
        }
      }
    }
  } finally {
    $bmp.Dispose()
  }
}

New-Item -ItemType Directory -Path $srcDir -Force | Out-Null

$grassDirt = Join-Path $terrain 'Grass_Dirt_Stripes_set.png'
$dirtGrass = Join-Path $terrain 'Dirt_Grass_Stripes_set.png'

if(!(Test-Path -LiteralPath $grassDirt)) { throw "MISSING: $grassDirt" }
if(!(Test-Path -LiteralPath $dirtGrass)) { throw "MISSING: $dirtGrass" }

# Output matches default region/index naming from transitions json generator (change later in editor as needed)
Slice-4x4 $grassDirt 'tr_dirt_on_grass'
Slice-4x4 $dirtGrass 'tr_grass_on_dirt'

"WROTE: single PNGs into $srcDir"
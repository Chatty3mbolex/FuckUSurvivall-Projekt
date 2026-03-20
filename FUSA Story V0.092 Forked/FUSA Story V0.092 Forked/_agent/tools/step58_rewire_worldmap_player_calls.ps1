param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
$text = [System.IO.File]::ReadAllText($gsPath,[System.Text.Encoding]::UTF8)
$text = ($text -replace "`r`n","`n" -replace "`r","`n")

# Replace call sites to wrappers with controller calls.
$map = @(
  @{ m='debugWorldMapTravel'; ctl='worldMapCtl' },
  @{ m='areaEnemyZonesTick'; ctl='worldMapCtl' },
  @{ m='areaClearEnemyZones'; ctl='worldMapCtl' },
  @{ m='areaAddEnemyZone'; ctl='worldMapCtl' },
  @{ m='areaWorldMapState'; ctl='worldMapCtl' },
  @{ m='updateMouseWorld'; ctl='playerCtl' },
  @{ m='collectInputSnapshot'; ctl='playerCtl' },
  @{ m='actionReach'; ctl='playerCtl' },
  @{ m='pickupNearby'; ctl='playerCtl' }
)

foreach($e in $map){
  $m=$e.m; $ctl=$e.ctl
  # Replace calls like 'm(' that are not declarations.
  $pattern = "(?m)^(?!\\s*(public|private|protected)\\s+.*\\b" + [regex]::Escape($m) + "\\s*\\()(?<indent>\\s*)" + [regex]::Escape($m) + "\\s*\\(" 
  $text = [regex]::Replace($text, $pattern, '${indent}'+$ctl+'.'+$m+'(')
}

[System.IO.File]::WriteAllText($gsPath,$text,(New-Object System.Text.UTF8Encoding($false)))
Write-Host 'OK: rewired worldmap/player call sites to controllers'

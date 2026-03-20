param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Normalize([string]$t){ return ($t -replace "`r`n","`n" -replace "`r","`n") }
function RemoveMethodBySignature([string]$text,[string]$sigLine){
  $idx=$text.IndexOf($sigLine)
  if($idx -lt 0){ return $text }
  $open=$text.IndexOf('{',$idx)
  if($open -lt 0){ throw "Opening brace not found for $sigLine" }
  $brace=0;$end=-1
  for($i=$open;$i -lt $text.Length;$i++){
    $ch=$text[$i]
    if($ch -eq '{'){ $brace++ }
    elseif($ch -eq '}'){
      $brace--; if($brace -eq 0){ $end=$i; break }
    }
  }
  if($end -lt 0){ throw "Brace match failed for $sigLine" }
  $after=$end+1
  if($after -lt $text.Length -and $text[$after] -eq "`n"){ $after++ }
  return $text.Substring(0,$idx) + $text.Substring($after)
}

$gsPath=Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
$text=Normalize([System.IO.File]::ReadAllText($gsPath,[System.Text.Encoding]::UTF8))

# Remove wrapper methods (top-level) for worldmap + player
$wrappers=@(
  '  private void debugWorldMapTravel(Dir4 dir) {',
  '  private void areaEnemyZonesTick(float dt) {',
  '  public void areaClearEnemyZones() {',
  '  public void areaAddEnemyZone(int cxTile, int cyTile, int rTiles, int min, int max, int respawnDaysMin, int respawnDaysMax) {',
  '  public WorldMapState areaWorldMapState() {',
  '  private void updateMouseWorld(boolean clampToRing) {',
  '  private void collectInputSnapshot(float delta, InputState out) {',
  '  private float actionReach(int equippedItemId) {',
  '  private void pickupNearby() {'
)

foreach($sigLine in $wrappers){
  $text = RemoveMethodBySignature $text $sigLine
}

[System.IO.File]::WriteAllText($gsPath,$text,(New-Object System.Text.UTF8Encoding($false)))
Write-Host 'OK: removed worldmap/player wrapper methods'

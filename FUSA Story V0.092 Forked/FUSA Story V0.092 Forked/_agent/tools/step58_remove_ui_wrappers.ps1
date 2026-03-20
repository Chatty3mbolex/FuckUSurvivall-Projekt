param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Normalize([string]$t){ return ($t -replace "`r`n","`n" -replace "`r","`n") }

function RemoveMethodBySignature([string]$text, [string]$sigLine) {
  $idx = $text.IndexOf($sigLine)
  if($idx -lt 0){ return $text }
  $open = $text.IndexOf('{', $idx)
  if($open -lt 0){ throw "Opening brace not found for $sigLine" }
  $brace=0; $end=-1
  for($i=$open; $i -lt $text.Length; $i++){
    $ch=$text[$i]
    if($ch -eq '{'){ $brace++ }
    elseif($ch -eq '}'){
      $brace--
      if($brace -eq 0){ $end=$i; break }
    }
  }
  if($end -lt 0){ throw "Brace match failed for $sigLine" }
  # remove trailing newline if present
  $afterIdx=$end+1
  if($afterIdx -lt $text.Length -and $text[$afterIdx] -eq "`n"){
    $afterIdx++
  }
  return $text.Substring(0,$idx) + $text.Substring($afterIdx)
}

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
if(!(Test-Path -LiteralPath $gsPath)){ throw "GameScreen not found" }
$text = Normalize([System.IO.File]::ReadAllText($gsPath,[System.Text.Encoding]::UTF8))

$methods = @(
  'drawHotbarHud','drawBuildPanel','drawCraftPanel','drawShopMenuAtMerchant',
  'drawQuestLogHubPanel','drawQuestLogOpenQuestsPanel','drawQuestLogDoneQuestsPanel',
  'drawInventoryGrid','drawWalletMenu','drawSkillMenu'
)

foreach($m in $methods){
  $sig = "  private void $m()"  # wrapper signature (2-space indent)
  # actual signature line includes '(' so:
  $sigLine = $sig + " {" 
  # But file likely has '  private void <m>() {' on one line.
  $sigLine = "  private void $m() {"
  $text = RemoveMethodBySignature $text $sigLine
}

[System.IO.File]::WriteAllText($gsPath, $text, (New-Object System.Text.UTF8Encoding($false)))
Write-Host 'OK: removed UI wrapper methods (2-space indent)'

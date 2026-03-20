param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function ReadText([string]$p){
  $t=[System.IO.File]::ReadAllText($p,[System.Text.Encoding]::UTF8)
  return ($t -replace "`r`n","`n" -replace "`r","`n")
}

function WriteText([string]$p,[string]$t){
  $t = ($t -replace "`r`n","`n" -replace "`r","`n")
  [System.IO.File]::WriteAllText($p,$t,(New-Object System.Text.UTF8Encoding($false)))
}

function IndentMore([string]$block,[int]$spaces){
  $pref=(' ' * $spaces)
  $lines=$block -split "`n",-1
  $out=New-Object System.Collections.Generic.List[string]
  foreach($ln in $lines){
    if($ln -eq ''){ $out.Add('')|Out-Null }
    else{ $out.Add($pref + $ln)|Out-Null }
  }
  return ($out -join "`n")
}

function ReplaceMethodBody([string]$text,[string]$sig,[string]$replacementBody){
  $idx=$text.IndexOf($sig)
  if($idx -lt 0){ throw "Method signature not found: $sig" }
  $open=$text.IndexOf('{',$idx)
  if($open -lt 0){ throw "Opening brace not found: $sig" }
  $brace=0; $end=-1
  for($i=$open;$i -lt $text.Length;$i++){
    $ch=$text[$i]
    if($ch -eq '{'){ $brace++ }
    elseif($ch -eq '}'){
      $brace--
      if($brace -eq 0){ $end=$i; break }
    }
  }
  if($end -lt 0){ throw "Brace match failed: $sig" }
  $before=$text.Substring(0,$idx)
  $after=$text.Substring($end+1)
  return $before + $replacementBody + $after
}

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
$snDir  = Join-Path $ProjectRoot '_agent\snapshots\step57_methods'
if(!(Test-Path -LiteralPath $gsPath)){ throw "GameScreen not found" }

$renderWorld = ReadText (Join-Path $snDir 'method_renderWorld_body.txt')
$renderUI    = ReadText (Join-Path $snDir 'method_renderUI_body.txt')
$updateMask  = ReadText (Join-Path $snDir 'method_updateDnMask_body.txt')
$rebuildFbo  = ReadText (Join-Path $snDir 'method_rebuildDnMaskFbo_body.txt')

# Build inner class
$inner=@()
$inner += '  // -----------------------------------------------------------------------------'
$inner += '  // STEP 5.7 - GameRenderer (inner class)'
$inner += '  // Rationale: rendering extraction; updateDayNightMusic MUST remain outside.'
$inner += '  // -----------------------------------------------------------------------------'
$inner += ''
$inner += '  private final GameRenderer renderer = new GameRenderer();'
$inner += ''
$inner += '  private final class GameRenderer {'
$inner += ''
$inner += (IndentMore $renderWorld 2) -split "`n"
$inner += ''
$inner += (IndentMore $renderUI 2) -split "`n"
$inner += ''
$inner += (IndentMore $updateMask 2) -split "`n"
$inner += ''
$inner += (IndentMore $rebuildFbo 2) -split "`n"
$inner += ''
$inner += '  }'
$innerText = ($inner -join "`n") + "`n"

$text = ReadText $gsPath
if($text -match 'STEP 5\.7 - GameRenderer'){ throw 'GameRenderer already present' }

# Insert after worldMapCtl field line
$anchor='  private final WorldMapController worldMapCtl = new WorldMapController();'
$pos=$text.IndexOf($anchor)
if($pos -lt 0){ throw 'Anchor not found: worldMapCtl field' }
$insertPos=$pos + $anchor.Length
$text = $text.Substring(0,$insertPos) + "`n`n" + $innerText + $text.Substring($insertPos)

# Replace top-level methods with wrappers
$text = ReplaceMethodBody $text 'void renderWorld(float delta, int selectedTool)' "  void renderWorld(float delta, int selectedTool) {`n    renderer.renderWorld(delta, selectedTool);`n  }`n"
$text = ReplaceMethodBody $text 'void renderUI()' "  void renderUI() {`n    renderer.renderUI();`n  }`n"
$text = ReplaceMethodBody $text 'private void updateDnMask()' "  private void updateDnMask() {`n    renderer.updateDnMask();`n  }`n"
$text = ReplaceMethodBody $text 'private void rebuildDnMaskFbo(int w, int h)' "  private void rebuildDnMaskFbo(int w, int h) {`n    renderer.rebuildDnMaskFbo(w, h);`n  }`n"

WriteText $gsPath $text
Write-Host 'OK: Step 5.7 extraction applied.'

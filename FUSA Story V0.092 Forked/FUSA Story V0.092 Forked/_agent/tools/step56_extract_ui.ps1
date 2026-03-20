param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function ReadText([string]$p) {
  $t = [System.IO.File]::ReadAllText($p, [System.Text.Encoding]::UTF8)
  return ($t -replace "`r`n", "`n" -replace "`r", "`n")
}

function WriteText([string]$p, [string]$t) {
  $t = ($t -replace "`r`n", "`n" -replace "`r", "`n")
  [System.IO.File]::WriteAllText($p, $t, (New-Object System.Text.UTF8Encoding($false)))
}

function IndentMore([string]$block, [int]$spaces) {
  $pref = (' ' * $spaces)
  $lines = $block -split "`n", -1
  $out = New-Object System.Collections.Generic.List[string]
  foreach ($ln in $lines) {
    if ($ln -eq '') { $out.Add('') | Out-Null }
    else { $out.Add($pref + $ln) | Out-Null }
  }
  return ($out -join "`n")
}

function ReplaceMethodBody([string]$text, [string]$methodName, [string]$replacementBody) {
  # Find top-level method start by signature string (2-space indent)
  $sig = "  private void $methodName(" 
  $idx = $text.IndexOf($sig)
  if ($idx -lt 0) { throw "Method signature not found: $methodName" }

  # Find opening brace '{' after signature
  $open = $text.IndexOf('{', $idx)
  if ($open -lt 0) { throw "Opening brace not found: $methodName" }

  # Brace match from open
  $brace = 0
  $end = -1
  for ($i = $open; $i -lt $text.Length; $i++) {
    $ch = $text[$i]
    if ($ch -eq '{') { $brace++ }
    elseif ($ch -eq '}') {
      $brace--
      if ($brace -eq 0) { $end = $i; break }
    }
  }
  if ($end -lt 0) { throw "Brace match failed: $methodName" }

  $before = $text.Substring(0, $idx)
  $after  = $text.Substring($end + 1)

  return $before + $replacementBody + $after
}

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
if (!(Test-Path -LiteralPath $gsPath)) { throw "GameScreen not found: $gsPath" }

$snDir = Join-Path $ProjectRoot '_agent\snapshots\step56_methods'
$methodFiles = @(
  @{ name = 'drawHotbarHud'; file = 'method_drawHotbarHud_body.txt' },
  @{ name = 'drawBuildPanel'; file = 'method_drawBuildPanel_body.txt' },
  @{ name = 'drawCraftPanel'; file = 'method_drawCraftPanel_body.txt' },
  @{ name = 'drawShopMenuAtMerchant'; file = 'method_drawShopMenuAtMerchant_body.txt' },
  @{ name = 'drawQuestLogHubPanel'; file = 'method_drawQuestLogHubPanel_body.txt' },
  @{ name = 'drawQuestLogOpenQuestsPanel'; file = 'method_drawQuestLogOpenQuestsPanel_body.txt' },
  @{ name = 'drawQuestLogDoneQuestsPanel'; file = 'method_drawQuestLogDoneQuestsPanel_body.txt' },
  @{ name = 'drawInventoryGrid'; file = 'method_drawInventoryGrid_body.txt' },
  @{ name = 'drawWalletMenu'; file = 'method_drawWalletMenu_body.txt' },
  @{ name = 'drawSkillMenu'; file = 'method_drawSkillMenu_body.txt' }
)

# Build inner class content
$inner = @()
$inner += '  // -----------------------------------------------------------------------------'
$inner += '  // STEP 5.6 - GameUiController (inner class)'
$inner += '  // Rationale: preserve access to GameScreen private state without widening visibility.'
$inner += '  // -----------------------------------------------------------------------------'
$inner += ''
$inner += '  private final GameUiController uiCtl = new GameUiController();'
$inner += ''
$inner += '  private final class GameUiController {'

foreach ($mf in $methodFiles) {
  $p = Join-Path $snDir $mf.file
  if (!(Test-Path -LiteralPath $p)) { throw "Snapshot body not found: $p" }
  $body = ReadText $p
  # snapshot body starts with two-space indent; we want 4-space indent inside inner class
  $body = IndentMore $body 2
  $inner += ''
  $inner += ($body -split "`n")
}

$inner += '  }'
$innerText = ($inner -join "`n") + "`n"

# Insert inner class after playerCtl field block (before worldMapCtl block)
$text = ReadText $gsPath
if ($text -match 'STEP 5\.6 - GameUiController') { throw 'GameUiController already present' }
$anchor = '  private final PlayerController playerCtl = new PlayerController();'
$pos = $text.IndexOf($anchor)
if ($pos -lt 0) { throw 'Anchor not found (playerCtl field)' }
$insertPos = $pos + $anchor.Length
$text = $text.Substring(0, $insertPos) + "`n`n" + $innerText + $text.Substring($insertPos)

# Replace each top-level method with wrapper delegating to uiCtl
foreach ($mf in $methodFiles) {
  $m = $mf.name
  $wrapper = @()
  $wrapper += "  private void $m() {"
  $wrapper += "    uiCtl.$m();"
  $wrapper += "  }"
  $wrapperBody = ($wrapper -join "`n") + "`n"
  $text = ReplaceMethodBody $text $m $wrapperBody
}

WriteText $gsPath $text
Write-Host 'OK: Step 5.6 extraction applied.'

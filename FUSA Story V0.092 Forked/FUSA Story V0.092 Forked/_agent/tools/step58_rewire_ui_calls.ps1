param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
if(!(Test-Path -LiteralPath $gsPath)){ throw "GameScreen not found" }

$text = [System.IO.File]::ReadAllText($gsPath, [System.Text.Encoding]::UTF8)
$text = ($text -replace "`r`n","`n" -replace "`r","`n")

$methods = @(
  'drawHotbarHud','drawBuildPanel','drawCraftPanel','drawShopMenuAtMerchant',
  'drawQuestLogHubPanel','drawQuestLogOpenQuestsPanel','drawQuestLogDoneQuestsPanel',
  'drawInventoryGrid','drawWalletMenu','drawSkillMenu'
)

# Replace call sites (not signatures) to use uiCtl.<m>()
foreach($m in $methods){
  # Replace occurrences of '<m>();' that are not method declarations.
  # - Avoid lines containing 'void <m>(' and 'private void <m>(' etc.
  $pattern = "(?m)^(?!\\s*(public|private|protected)\\s+.*\\b" + [regex]::Escape($m) + "\\s*\\()(?<indent>\\s*)" + [regex]::Escape($m) + "\\s*\\(\\)\\s*;"
  $text = [regex]::Replace($text, $pattern, '${indent}uiCtl.'+$m+'();')
}

[System.IO.File]::WriteAllText($gsPath, $text, (New-Object System.Text.UTF8Encoding($false)))
Write-Host 'OK: rewired UI call sites to uiCtl.*'

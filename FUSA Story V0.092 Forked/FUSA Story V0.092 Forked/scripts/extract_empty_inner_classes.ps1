param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot,
  [Parameter(Mandatory=$true)][string]$BacklogPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Append-Backlog([string]$title, [string]$fileRel, [int]$startLine, [int]$endLine, [string[]]$blockLines) {
  $ts = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss')
  Add-Content -LiteralPath $BacklogPath -Encoding UTF8 -Value "`n### $title`n`n- time: $ts`n- file: $fileRel`n- lines: $startLine-$endLine`n`n```java"
  Add-Content -LiteralPath $BacklogPath -Encoding UTF8 -Value ($blockLines -join "`n")
  Add-Content -LiteralPath $BacklogPath -Encoding UTF8 -Value "```\n"
}

function Find-ClassStart([string[]]$lines, [string]$className) {
  for ($i=0; $i -lt $lines.Count; $i++) {
    # NOTE: Avoid \b word-boundary here because PowerShell/.NET regex handling can be surprising in some contexts.
    if ($lines[$i] -match ("class\s+" + [regex]::Escape($className))) { return $i }
  }
  return -1
}

function Find-RangeByBraces([string[]]$lines, [int]$startIdx) {
  $brace = 0
  $opened = $false
  for ($j=$startIdx; $j -lt $lines.Count; $j++) {
    foreach ($ch in $lines[$j].ToCharArray()) {
      if ($ch -eq '{') { $brace++; $opened = $true }
      elseif ($ch -eq '}') {
        if ($opened) {
          $brace--
          if ($brace -eq 0) { return @{ start=$startIdx; end=$j } }
        }
      }
    }
  }
  return $null
}

function Is-Empty-Body([string[]]$blockLines) {
  # crude: remove first line up to first '{' and last line from last '}'
  $t = ($blockLines -join "`n")
  $firstOpen = $t.IndexOf('{')
  $lastClose = $t.LastIndexOf('}')
  if ($firstOpen -lt 0 -or $lastClose -lt 0 -or $lastClose -le $firstOpen) { return $false }
  $body = $t.Substring($firstOpen+1, $lastClose-$firstOpen-1)
  # remove whitespace and comment markers
  $body2 = $body
  $body2 = [regex]::Replace($body2, "(?s)/\\*.*?\\*/", "")
  $body2 = [regex]::Replace($body2, "(?m)//.*$", "")
  $body2 = ($body2 -replace "\\s+", "")
  return ($body2.Length -eq 0)
}

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
if (!(Test-Path -LiteralPath $gsPath)) { throw "GameScreen.java not found at expected path: $gsPath" }

$lines = Get-Content -LiteralPath $gsPath -Encoding UTF8
$fileRel = $gsPath.Substring($ProjectRoot.Length+1).Replace('\\','/')

$targets = @('UiState','CommandBuffer')

# Collect ranges first (so line numbers stable during extraction)
$ranges = @()
foreach ($cn in $targets) {
  $s = Find-ClassStart $lines $cn
  if ($s -lt 0) { throw "Class $cn not found in GameScreen" }
  $r = Find-RangeByBraces $lines $s
  if ($null -eq $r) { throw "Failed to match braces for class $cn starting at line $($s+1)" }
  $blockLines = $lines[$r.start..$r.end]
  if (-not (Is-Empty-Body $blockLines)) {
    throw "Refusing to remove $cn because body is not empty (only empty placeholder classes allowed)." 
  }
  $ranges += [pscustomobject]@{ name=$cn; start=$r.start; end=$r.end }
}

# Remove in reverse order (bottom-up) to keep earlier ranges valid
$ranges = $ranges | Sort-Object start -Descending
foreach ($rg in $ranges) {
  $blockLines = $lines[$rg.start..$rg.end]
  Append-Backlog -title ("Empty inner class extracted: " + $rg.name) -fileRel $fileRel -startLine ($rg.start+1) -endLine ($rg.end+1) -blockLines $blockLines

  $before=@(); if($rg.start -gt 0){ $before = $lines[0..($rg.start-1)] }
  $after=@(); if($rg.end+1 -le $lines.Count-1){ $after = $lines[($rg.end+1)..($lines.Count-1)] }
  $lines = @($before + $after)
}

# write back LF
$text = ($lines -join "`n") + "`n"
$text = $text -replace "`r`n", "`n" -replace "`r", "`n"
[System.IO.File]::WriteAllText($gsPath, $text, (New-Object System.Text.UTF8Encoding($false)))

Write-Host "Empty inner classes removed from GameScreen: UiState, CommandBuffer"
exit 0

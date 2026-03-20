param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot,
  [Parameter(Mandatory=$true)][string]$BacklogPath,
  [string]$SingleFile
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Is-Prohibited([string]$fullPath) {
  if ($fullPath -match '\\client\\network\\') { return $true }
  if ($fullPath -match 'GameClientListener\.java$') { return $true }
  return $false
}

function Append-Backlog([string]$title, [string]$fileRel, [int]$startLine, [int]$endLine, [string[]]$blockLines) {
  $ts = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss')
  Add-Content -LiteralPath $BacklogPath -Encoding UTF8 -Value "`n### $title`n`n- time: $ts`n- file: $fileRel`n- lines: $startLine-$endLine`n`n```java"
  Add-Content -LiteralPath $BacklogPath -Encoding UTF8 -Value ($blockLines -join "`n")
  Add-Content -LiteralPath $BacklogPath -Encoding UTF8 -Value "```\n"
}

function Extract-BlockComment([string[]]$lines, [int]$hitIdx) {
  # find start of /* ... */ block containing hitIdx (0-based)
  $start = $hitIdx
  while ($start -ge 0 -and ($lines[$start] -notmatch '/\*')) { $start-- }
  if ($start -lt 0) { return $null }
  $end = $hitIdx
  while ($end -lt $lines.Count -and ($lines[$end] -notmatch '\*/')) { $end++ }
  if ($end -ge $lines.Count) { return $null }
  return @{ start=$start; end=$end }
}

function Extract-LineCommentRun([string[]]$lines, [int]$hitIdx) {
  $start = $hitIdx
  while ($start -gt 0 -and ($lines[$start-1] -match '^\s*//')) { $start-- }
  $end = $hitIdx
  while ($end+1 -lt $lines.Count -and ($lines[$end+1] -match '^\s*//')) { $end++ }
  return @{ start=$start; end=$end }
}

# Collect java files (optionally single-file)
$javaFiles = @()
if ($SingleFile -and $SingleFile.Trim().Length -gt 0) {
  if (!(Test-Path -LiteralPath $SingleFile)) { throw "SingleFile not found: $SingleFile" }
  if (Is-Prohibited $SingleFile) { throw "SingleFile is prohibited by rules: $SingleFile" }
  $javaFiles = @(Get-Item -LiteralPath $SingleFile)
} else {
  $javaFiles = Get-ChildItem -LiteralPath $ProjectRoot -Recurse -File -Filter '*.java' |
    Where-Object {
      $_.FullName -match '\\src\\main\\java\\' -and
      $_.FullName -notmatch '\\build\\|\\generated\\|\\libs\\|\\assets\\|\\resources\\'
    } |
    Sort-Object FullName
}

$removedCount = 0
$hitCount = 0

foreach ($f in $javaFiles) {
  if (Is-Prohibited $f.FullName) { continue }

  $rel = $f.FullName.Substring($ProjectRoot.Length+1).Replace('\\','/')
  $lines = Get-Content -LiteralPath $f.FullName -Encoding UTF8

  $i = 0
  $changed = $false
  while ($i -lt $lines.Count) {
    if ($lines[$i] -match 'Nicht\s+fertiges\s+Feature') {
      $hitCount++

      $range = $null
      if ($lines[$i] -match '/\*') {
        $range = Extract-BlockComment $lines $i
      } elseif ($lines[$i] -match '^\s*//') {
        $range = Extract-LineCommentRun $lines $i
      } else {
        # try block-comment search upwards (phrase inside a block)
        $range = Extract-BlockComment $lines $i
        if ($null -eq $range) {
          # fallback: treat as single line
          $range = @{ start=$i; end=$i }
        }
      }

      if ($null -eq $range) {
        throw "Failed to resolve comment block for Nicht fertiges Feature in $rel at line $($i+1)"
      }

      $start = [int]$range.start
      $end = [int]$range.end

      $blockLines = $lines[$start..$end]
      Append-Backlog -title 'Nicht fertiges Feature' -fileRel $rel -startLine ($start+1) -endLine ($end+1) -blockLines $blockLines

      # remove block from lines
      $before = @()
      if ($start -gt 0) { $before = $lines[0..($start-1)] }
      $after = @()
      if ($end+1 -le $lines.Count-1) { $after = $lines[($end+1)..($lines.Count-1)] }
      $lines = @($before + $after)

      $removedCount++
      $changed = $true
      # continue at same index (now points to next line)
      continue
    }
    $i++
  }

  if ($changed) {
    # Write back with LF (PowerShell uses CRLF, so enforce LF explicitly)
    $text = ($lines -join "`n") + "`n"
    $text = $text -replace "`r`n", "`n" -replace "`r", "`n"
    [System.IO.File]::WriteAllText($f.FullName, $text, (New-Object System.Text.UTF8Encoding($false)))
  }
}

Write-Host ("NichtFertig extraction complete. Hits=" + $hitCount + ", blocks removed=" + $removedCount)
exit 0

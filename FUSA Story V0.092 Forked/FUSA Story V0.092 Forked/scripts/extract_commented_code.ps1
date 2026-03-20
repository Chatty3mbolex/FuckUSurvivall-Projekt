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

function Looks-Like-Code([string[]]$blockLines) {
  $t = ($blockLines -join "`n")
  if ($t -match ';') { return $true }
  if ($t -match '\\bif\\b|\\bfor\\b|\\bwhile\\b|\\bswitch\\b|\\breturn\\b|\\bnew\\b') { return $true }
  if ($t -match '\\{|\\}') { return $true }
  return $false
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

$removedBlocks = 0

foreach ($f in $javaFiles) {
  if (Is-Prohibited $f.FullName) { continue }

  $rel = $f.FullName.Substring($ProjectRoot.Length+1).Replace('\\','/')
  $lines = Get-Content -LiteralPath $f.FullName -Encoding UTF8

  $i=0
  $changed=$false
  while ($i -lt $lines.Count) {
    # block comments: skip javadoc (/**)
    if ($lines[$i] -match '/\*' -and $lines[$i] -notmatch '/\*\*') {
      $start = $i
      $end = $i
      while ($end -lt $lines.Count -and ($lines[$end] -notmatch '\*/')) { $end++ }
      if ($end -ge $lines.Count) { break }

      $blockLines = $lines[$start..$end]
      $lc = $blockLines.Count
      if ($lc -gt 3 -and (Looks-Like-Code $blockLines)) {
        Append-Backlog -title 'Commented-out code block' -fileRel $rel -startLine ($start+1) -endLine ($end+1) -blockLines $blockLines

        $before=@(); if($start -gt 0){ $before = $lines[0..($start-1)] }
        $after=@(); if($end+1 -le $lines.Count-1){ $after = $lines[($end+1)..($lines.Count-1)] }
        $lines = @($before + $after)

        $removedBlocks++
        $changed=$true
        continue
      }

      $i = $end + 1
      continue
    }

    # consecutive // comment runs
    if ($lines[$i] -match '^\\s*//') {
      $start = $i
      $end = $i
      while (($end+1) -lt $lines.Count -and ($lines[$end+1] -match '^\\s*//')) { $end++ }
      $blockLines = $lines[$start..$end]
      $lc = $blockLines.Count
      if ($lc -gt 3 -and (Looks-Like-Code $blockLines)) {
        Append-Backlog -title 'Commented-out line-comment code run' -fileRel $rel -startLine ($start+1) -endLine ($end+1) -blockLines $blockLines

        $before=@(); if($start -gt 0){ $before = $lines[0..($start-1)] }
        $after=@(); if($end+1 -le $lines.Count-1){ $after = $lines[($end+1)..($lines.Count-1)] }
        $lines = @($before + $after)

        $removedBlocks++
        $changed=$true
        continue
      }

      $i = $end + 1
      continue
    }

    $i++
  }

  if ($changed) {
    $text = ($lines -join "`n") + "`n"
    $text = $text -replace "`r`n", "`n" -replace "`r", "`n"
    [System.IO.File]::WriteAllText($f.FullName, $text, (New-Object System.Text.UTF8Encoding($false)))
  }
}

Write-Host ("Commented-code extraction complete. Blocks removed=" + $removedBlocks)
exit 0

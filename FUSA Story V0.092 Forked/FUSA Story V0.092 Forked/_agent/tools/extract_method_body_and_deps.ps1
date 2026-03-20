param(
  [Parameter(Mandatory=$true)][string]$FilePath,
  [Parameter(Mandatory=$true)][string]$MethodName,
  [Parameter(Mandatory=$true)][string]$OutDir
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Normalize-LF([string]$s) {
  return ($s -replace "`r`n", "`n" -replace "`r", "`n")
}

if (!(Test-Path -LiteralPath $FilePath)) { throw "File not found: $FilePath" }
if (!(Test-Path -LiteralPath $OutDir)) { New-Item -ItemType Directory -Path $OutDir | Out-Null }

$text = Normalize-LF ([System.IO.File]::ReadAllText($FilePath, [System.Text.Encoding]::UTF8))
$lines = $text -split "`n", -1

# Find a method signature line containing "<MethodName>(".
# To avoid matching call sites, require an access modifier keyword on the same line.
$startLine = -1
$bestIndent = [int]::MaxValue
$returnTypes = @('void','boolean','int','float','double','long','String')
for ($i=0; $i -lt $lines.Length; $i++) {
  $ln = $lines[$i]
  # Must look like a declaration line, not a call-site (e.g. 'debugTravelNorth() { debugWorldMapTravel(N); }').
  $declHit = $false
  foreach ($rt in $returnTypes) {
    if ($ln.Contains($rt + ' ' + $MethodName + '(')) { $declHit = $true; break }
  }
  # Accept package-private methods too (no access modifier), but they must start at column 0/2-space indent.
  $hasAccess = ($ln -match '\b(private|public|protected)\b')
  $looksLikeTopLevel = ($ln.StartsWith('  ') -or $ln.StartsWith("`t") -or $ln.StartsWith('void '))
  if (($hasAccess -or ($looksLikeTopLevel -and -not $ln.Contains('.' + $MethodName + '('))) -and $declHit) {
    $indent = ($ln.Length - $ln.TrimStart().Length)
    if ($indent -lt $bestIndent) {
      $bestIndent = $indent
      $startLine = $i
    }
  }
}
if ($startLine -lt 0) {
  # Fallback for custom return types (e.g., WorldMapState): still avoid call sites by requiring no '.<MethodName>(' prefix.
  for ($i=0; $i -lt $lines.Length; $i++) {
    $ln = $lines[$i]
    if (($ln -match '\b(private|public|protected)\b') -and $ln.Contains($MethodName + '(') -and (-not $ln.Contains('.' + $MethodName + '('))) {
      $indent = ($ln.Length - $ln.TrimStart().Length)
      if ($indent -lt $bestIndent) {
        $bestIndent = $indent
        $startLine = $i
      }
    }
  }
}
if ($startLine -lt 0) { throw "Method not found by signature: $MethodName" }

# Find opening brace '{' starting from startLine
$openIdx = -1
$lineNo = $startLine
while ($lineNo -lt $lines.Length) {
  $pos = $lines[$lineNo].IndexOf('{')
  if ($pos -ge 0) { $openIdx = $lineNo; break }
  $lineNo++
}
if ($openIdx -lt 0) { throw "Opening brace not found for method: $MethodName" }

# Brace match from openIdx forward (counts braces in code; does NOT skip strings/comments; good enough for this file as a first pass)
$brace = 0
$endLine = -1
for ($j=$openIdx; $j -lt $lines.Length; $j++) {
  foreach ($ch in $lines[$j].ToCharArray()) {
    if ($ch -eq '{') { $brace++ }
    elseif ($ch -eq '}') {
      $brace--
      if ($brace -eq 0) { $endLine = $j; break }
    }
  }
  if ($endLine -ge 0) { break }
}
if ($endLine -lt 0) { throw "Failed to brace-match method: $MethodName" }

$bodyLines = $lines[$startLine..$endLine]

# Dependency heuristic: collect identifier-like tokens and dotted field/method accesses
$bodyText = ($bodyLines -join "`n")
$tokenMatches = [regex]::Matches($bodyText, '\b[A-Za-z_][A-Za-z0-9_]*\b')
$tokens = $tokenMatches | ForEach-Object { $_.Value } | Sort-Object -Unique

$dottedMatches = [regex]::Matches($bodyText, '\b[A-Za-z_][A-Za-z0-9_]*\.[A-Za-z_][A-Za-z0-9_]*')
$dotted = $dottedMatches | ForEach-Object { $_.Value } | Sort-Object -Unique

$baseName = ($MethodName -replace '[^A-Za-z0-9_]+','_')
$bodyOut = Join-Path $OutDir ("method_" + $baseName + "_body.txt")
$tokOut  = Join-Path $OutDir ("method_" + $baseName + "_tokens.txt")
$dotOut  = Join-Path $OutDir ("method_" + $baseName + "_dotted.txt")

($bodyLines | ForEach-Object { $_ }) | Set-Content -LiteralPath $bodyOut -Encoding UTF8
$tokens | Set-Content -LiteralPath $tokOut -Encoding UTF8
$dotted | Set-Content -LiteralPath $dotOut -Encoding UTF8

Write-Host ("OK: " + $MethodName + " lines=" + ($endLine-$startLine+1) + " body=" + $bodyOut)

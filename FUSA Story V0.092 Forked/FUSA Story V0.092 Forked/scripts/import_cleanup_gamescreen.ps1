param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$gsPath = Join-Path $ProjectRoot 'core\src\main\java\com\yourgame\survival\screens\GameScreen.java'
if (!(Test-Path -LiteralPath $gsPath)) { throw "GameScreen.java not found: $gsPath" }

$allLines = Get-Content -LiteralPath $gsPath -Encoding UTF8
$raw = $allLines -join "`n"

# Split header (package + imports) from body
$pkgIdx = ($allLines | Select-String -Pattern '^\s*package\s+' | Select-Object -First 1).LineNumber
if (-not $pkgIdx) { throw 'Package declaration not found' }

# find end of import block (first non-import after imports start)
$importStart = ($allLines | Select-String -Pattern '^\s*import\s+' | Select-Object -First 1).LineNumber
if (-not $importStart) { throw 'No imports found in GameScreen (unexpected)' }

$importEnd = $importStart
for ($i=$importStart; $i -le $allLines.Count; $i++) {
  if ($allLines[$i-1] -match '^\s*import\s+') { $importEnd = $i; continue }
  break
}

$header = @()
$header += $allLines[0..($importEnd-1)]
$bodyLines = @()
if ($importEnd -lt $allLines.Count) { $bodyLines = $allLines[$importEnd..($allLines.Count-1)] }
$body = $bodyLines -join "`n"

# Find all inline FQNs in body (exclude import/package)
$matches = [regex]::Matches($body, 'com\.yourgame\.survival\.[A-Za-z0-9_\.]+')

# Build set of class FQNs to import: choose last segment that looks like a class (starts with uppercase)
$classes = New-Object System.Collections.Generic.HashSet[string]
foreach ($m in $matches) {
  $s = $m.Value
  $parts = $s.Split('.')
  for ($k=$parts.Length-1; $k -ge 0; $k--) {
    $seg = $parts[$k]
    if ($seg.Length -gt 0 -and [char]::IsUpper($seg[0])) {
      $classFqn = ($parts[0..$k] -join '.')
      # Skip same-package imports (com.yourgame.survival.screens)
      if ($classFqn -notmatch '^com\.yourgame\.survival\.screens\.') {
        $classes.Add($classFqn) | Out-Null
      }
      break
    }
  }
}

# Read existing imports
$existingImports = New-Object System.Collections.Generic.HashSet[string]
foreach ($ln in $header) {
  if ($ln -match '^\s*import\s+([^;]+);') {
    $existingImports.Add($Matches[1].Trim()) | Out-Null
  }
}

# Compute imports to add (no wildcards)
$toAdd = $classes | Where-Object { -not $existingImports.Contains($_) } | Sort-Object

# Replace inline FQNs in body with short form (ClassName + remainder)
# For each classFqn: replace occurrences like <classFqn>.<something> or <classFqn> (type)
# Keep the remainder after the class name.
foreach ($classFqn in ($classes | Sort-Object { $_.Length } -Descending)) {
  $className = $classFqn.Split('.')[-1]
  # Replace exact classFqn occurrences when used as prefix
  # Use negative lookbehind to avoid matching inside longer identifiers.
  $pattern = [regex]::Escape($classFqn)
  $body = [regex]::Replace($body, "(?<![A-Za-z0-9_\.])$pattern", $className)
}

# Rebuild header with added imports inserted after last existing import line
if ($toAdd.Count -gt 0) {
  $newHeader = @()
  # keep everything up to importEnd-1, then insert missing imports after last import line
  $newHeader += $allLines[0..($importEnd-1)]
  foreach ($imp in $toAdd) {
    $newHeader += ("import " + $imp + ";")
  }
  $newHeaderText = $newHeader -join "`n"

  # Ensure single blank line between header and body
  $out = $newHeaderText.TrimEnd() + "`n`n" + $body.TrimStart() + "`n"
} else {
  $out = ($header -join "`n").TrimEnd() + "`n`n" + $body.TrimStart() + "`n"
}

# Enforce LF
$out = $out -replace "`r`n", "`n" -replace "`r", "`n"

[System.IO.File]::WriteAllText($gsPath, $out, (New-Object System.Text.UTF8Encoding($false)))

Write-Host ("GameScreen import cleanup complete. Inline matches found=" + $matches.Count + ", unique classes=" + $classes.Count + ", imports added=" + $toAdd.Count)
exit 0

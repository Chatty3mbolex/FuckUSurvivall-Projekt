param(
  [Parameter(Mandatory=$true)][string]$ProjectRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Normalize-LF([string]$path) {
  $bytes = [System.IO.File]::ReadAllBytes($path)
  $text  = [System.Text.Encoding]::UTF8.GetString($bytes)
  # Normalize CRLF and CR to LF
  $norm  = $text -replace "`r`n", "`n" -replace "`r", "`n"
  if ($norm -ne $text) {
    [System.IO.File]::WriteAllText($path, $norm, (New-Object System.Text.UTF8Encoding($false)))
    return $true
  }
  return $false
}

$javaFiles = Get-ChildItem -LiteralPath $ProjectRoot -Recurse -File -Filter '*.java' |
  Where-Object {
    $_.FullName -match '\\src\\main\\java\\' -and
    $_.FullName -notmatch '\\build\\|\\generated\\|\\libs\\|\\assets\\|\\resources\\'
  }

$changed = 0
foreach ($f in $javaFiles) {
  if (Normalize-LF $f.FullName) { $changed++ }
}

Write-Host ("Normalize LF complete. Files scanned=" + $javaFiles.Count + ", changed=" + $changed)
exit 0

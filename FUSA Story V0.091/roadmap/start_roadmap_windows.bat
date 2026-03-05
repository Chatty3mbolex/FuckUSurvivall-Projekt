@echo off
setlocal

REM Run from project root (FuckUSurvival/) so the server can read/write STATE.json + CHECKLIST_TODO.md
cd /d "%~dp0.."

echo Starting Roadmap UI server (write-enabled)...
echo.
echo URL: http://localhost:8011/roadmap/index.html
echo.

where node >nul 2>nul
if %errorlevel%==0 (
  node roadmap\server.js
) else (
  echo Node.js not found. Fallback: read-only python webserver (no file writes).
  echo Install Node.js (LTS) if you want toggles to write into STATE.json + CHECKLIST_TODO.md.
  echo.
  where python >nul 2>nul
  if %errorlevel%==0 (
    python -m http.server 8011
  ) else (
    echo Python not found either. Install Node.js (recommended) or Python.
    pause
  )
)

endlocal

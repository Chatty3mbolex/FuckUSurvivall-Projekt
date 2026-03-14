@echo off
setlocal EnableDelayedExpansion
title FUSA Area Converter
echo.
echo  ====================================
echo   FUSA Story — Area Converter
echo  ====================================
echo.
echo  Dieses Tool konvertiert eine PNG-Layoutdatei
echo  in eine spielbare Area (JSON).
echo.
echo  Die PNG muss im Projektordner liegen.
echo  Farblegende: siehe Gebietslayout_forest_legende.png
echo.
echo  ------------------------------------
echo.

REM --- Find project root (this script lives in project root) ---
set "ROOT=%~dp0"

REM --- List available PNG layouts ---
echo  Verfuegbare Layout-PNGs:
echo.
set count=0
for %%f in ("%ROOT%Gebietslayout*.png") do (
    set /a count+=1
    echo   [!count!] %%~nxf
    set "file_!count!=%%f"
)

if %count%==0 (
    echo  KEINE Gebietslayout-PNGs gefunden!
    echo  Lege eine Datei wie "Gebietslayout_XXX.png" im Projektordner ab.
    pause
    exit /b 1
)

echo.
set /p "choice=  Welche PNG? (Nummer eingeben): "

set "selected=!file_%choice%!"
if "!selected!"=="" (
    echo  Ungueltige Auswahl.
    pause
    exit /b 1
)

echo.
echo  Gewaehlt: !selected!
echo.

REM --- Ask for Area ID ---
set /p "areaid=  Area-ID eingeben (z.B. FOREST_02, SWAMP_01): "

if "!areaid!"=="" (
    echo  Keine Area-ID eingegeben. Abbruch.
    pause
    exit /b 1
)

echo.
echo  Konvertiere...
echo  PNG:     !selected!
echo  Area-ID: !areaid!
echo  Output:  assets\areas\!areaid!.area.json
echo.

REM --- Run Python converter ---
python "%ROOT%tools\convert_area.py" "!selected!" "!areaid!"

if %ERRORLEVEL% neq 0 (
    echo.
    echo  FEHLER bei der Konvertierung!
    echo  Stelle sicher dass Python 3 + Pillow installiert sind:
    echo    pip install Pillow
    pause
    exit /b 1
)

echo.
echo  ====================================
echo   FERTIG!
echo  ====================================
echo.
echo  Die Area wurde erstellt:
echo    assets\areas\!areaid!.area.json
echo.
echo  Um sie im Spiel verfuegbar zu machen:
echo  1. Fuege den Template-ID in WorldMapRuntime.java ein
echo     (createAlphaRegistry)
echo  2. Oder nutze sie als Ersatz fuer eine bestehende Area
echo.

pause

@echo off
setlocal
cd /d "%~dp0"
call "%~dp0tools\png2area\CONVERT_AREA.bat" %*
endlocal

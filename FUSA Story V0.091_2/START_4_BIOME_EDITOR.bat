@echo off
setlocal
cd /d "%~dp0"
call gradlew.bat :desktop:run --args="--biome-editor"
endlocal

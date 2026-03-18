@echo off
setlocal
cd /d "%~dp0"
call gradlew.bat :desktop:run --args="--asset-editor"
endlocal

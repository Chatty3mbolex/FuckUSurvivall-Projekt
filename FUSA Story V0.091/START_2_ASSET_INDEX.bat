@echo off
setlocal
cd /d "%~dp0"
REM Asset Index is part of the Asset-Editor toolchain.
REM Uses the same arg Rick used previously.
call gradlew.bat :desktop:run --args="--asset-editor#"
endlocal

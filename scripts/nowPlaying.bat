@echo off

setlocal enabledelayedexpansion

:loop
for /f %%i in ('call getVersion.bat') do set VERSION=%%i
java -jar "dist\streamline-%VERSION%.jar" --now-playing > "C:\tmp\StreamLine\nowPlaying.txt"
timeout /t 5 /nobreak >nul
goto loop

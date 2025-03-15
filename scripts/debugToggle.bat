@echo off

setlocal enabledelayedexpansion

set JAVA_FILE_WITH_CONFIG_LINE=src\main\java\com\walit\streamline\utilities\internal\StreamLineConstants.java

echo Checking log level in %JAVA_FILE_WITH_CONFIG_LINE%...

findstr /C:"level = DEBUG" "%JAVA_FILE_WITH_CONFIG_LINE%" >nul
if %errorlevel% equ 0 (
    echo Disabling debug mode...
    powershell -Command "(Get-Content '%JAVA_FILE_WITH_CONFIG_LINE%') -replace 'level = DEBUG', 'level = INFO' | Set-Content '%JAVA_FILE_WITH_CONFIG_LINE%'"
    echo Debug mode disabled for StreamLine log.
) else (
    echo Enabling debug mode...
    powershell -Command "(Get-Content '%JAVA_FILE_WITH_CONFIG_LINE%') -replace 'level = INFO', 'level = DEBUG' | Set-Content '%JAVA_FILE_WITH_CONFIG_LINE%'"
    echo Debug mode enabled for StreamLine log.
)

endlocal

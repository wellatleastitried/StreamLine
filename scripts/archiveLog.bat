@echo off

setlocal enabledelayedexpansion

set LOG_FILE=%TEMP%\StreamLine\streamline.log
set ARCHIVE_DIR=logs\archive
set ARCHIVED_LOG=%ARCHIVE_DIR%\archived_streamline.log

echo Checking if log file exists...

if exist "%LOG_FILE%" (
    echo Log file exists, saving to archive directory...
    if not exist "%ARCHIVE_DIR%" mkdir "%ARCHIVE_DIR%"
    copy /Y "%LOG_FILE%" "%ARCHIVED_LOG%"
    echo Log file has been successfully archived in %ARCHIVE_DIR%
) else (
    echo No log file exists at the moment.
)

endlocal

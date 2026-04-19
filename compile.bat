@echo off
echo =====================================================
echo  Smart Classroom Management System - Build Script
echo =====================================================
if not exist out mkdir out

:: Generate sources list
dir /s /b src\main\java\*.java > sources.txt

:: Compile
javac -cp ".;lib\mysql-connector-j-9.6.0.jar" -d out @sources.txt

if %ERRORLEVEL% == 0 (
    echo.
    echo [OK] Compilation successful!
    echo.
    echo To run:
    echo   java -cp ".;out;lib\mysql-connector-j-9.6.0.jar" com.smartclassroom.Main
) else (
    echo.
    echo [ERR] Compilation failed. Check errors above.
)
del sources.txt
pause

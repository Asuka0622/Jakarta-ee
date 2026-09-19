@echo off
rem ============================================================================
rem  One-click launcher: build + start Tomcat + deploy this project
rem      same as running:  mvn clean package cargo:run
rem
rem  Usage : double-click this file
rem  Stop  : press Ctrl + C in the black window, then press Y
rem
rem  Note  : it uses the `mvn` found on your PATH.
rem          Comments here are ASCII-only on purpose: cmd.exe mis-parses
rem          non-ASCII characters in .bat files and would break the script.
rem          Chinese documentation is in README.md.
rem ============================================================================

echo.
echo ============================================================
echo   Starting Tomcat and deploying demo1
echo.
echo   Wait until you see:
echo       [INFO] Tomcat 11.0.25 started on port [8080]
echo.
echo   Then open in browser:
echo       http://localhost:8080/demo1_war_exploded/
echo.
echo   To stop: press Ctrl + C in this window
echo ============================================================
echo.

call mvn clean package cargo:run

echo.
echo Server stopped.
pause

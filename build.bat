@echo off
REM Builds SIGATEC as a Windows application folder (no IDE, no console).
REM Output: target\installer\SIGATEC\SIGATEC.exe

setlocal
set "JAVA_HOME=C:\Users\elkin jimenez\.jdks\openjdk-26.0.2.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"

call "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.6.1\plugins\maven\lib\maven3\bin\mvn.cmd" clean package
if errorlevel 1 goto :error

copy /y target\sigatec.jar target\libs\ >nul
if errorlevel 1 goto :error

rmdir /s /q target\installer 2>nul
jpackage --type app-image --name SIGATEC --app-version 1.0 ^
  --input target\libs --main-jar sigatec.jar --main-class com.sigatec.Launcher ^
  --icon src\main\resources\com\sigatec\ui\icon.ico ^
  --dest target\installer
if errorlevel 1 goto :error

echo.
echo Listo: target\installer\SIGATEC\SIGATEC.exe
echo Comprime la carpeta target\installer\SIGATEC en un .zip para compartirla.
exit /b 0

:error
echo.
echo ERROR: el build fallo. Revisa el mensaje de arriba.
exit /b 1
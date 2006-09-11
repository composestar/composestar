@echo off

set BUILD_FILE=Development\build_total.xml
set ANT_HOME=%CD%\Development\Ant

set TARGET=select
if not "%1" == "" set TARGET=%1

if EXIST build.ini goto withini
if NOT EXIST build.ini goto withoutini

:withini
Development\Ant\bin\ant.bat -buildfile %BUILD_FILE% -propertyfile build.ini %TARGET% %2 %3 %4 %5 %6 %7 %8 %9
goto end

:withoutini
Development\Ant\bin\ant.bat -buildfile %BUILD_FILE% %TARGET% %2 %3 %4 %5 %6 %7 %8 %9
goto end


:end
if NOT ERRORLEVEL 0 goto failed
goto success

:failed
color 4f
pause

:success
@echo off
@setlocal

SET BUILD_FILE=%CD%\build.xml
SET CORE=%CD%\..\Core
SET ANT_HOME=%CORE%\Development\Ant
IF "%JAVA_HOME%" == "" set JAVA_HOME=c:\j2sdk1.4.2

SET TARGET=select
IF NOT "%1" == "" SET TARGET=%1

IF NOT EXIST "%JAVA_HOME%\bin\javac.exe" GOTO nojavahome

CALL "%CORE%\Development\Ant\bin\ant.bat" -buildfile "%BUILD_FILE%" "%TARGET%" %2 %3 %4 %5 %6 %7 %8 %9
GOTO end

:end
IF %ERRORLEVEL% NEQ 0 GOTO failed
GOTO success

:nojavahome
echo -----------------------------------------------------------------------
echo Please set the evironment variable JAVA_HOME to the location of the JDK
echo -----------------------------------------------------------------------

:failed
color 4f
pause
color 07

:success

@endlocal
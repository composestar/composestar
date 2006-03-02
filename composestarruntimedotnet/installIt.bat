echo OFF
regedit /e export.reg HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\composestar
IF EXIST export.reg goto SETTING
ECHO Cannot find registry key
set INSTALLDIR=%programfiles%\composestar\
goto CALLING
:SETTING
type export.reg | find """UninstallString""" > setter.txt
del export.reg
FOR /F "tokens=2* delims==" %%i IN (setter.txt) DO @echo %%i > setter.txt
copy set.txt + setter.txt setter.bat > copy.txt
del copy.txt

REM SETTING VARIABLES
call setter.bat

REM REMOVING QUOTES
SET INSTALLDIR=###%INSTALLDIR%###
SET INSTALLDIR=%INSTALLDIR:" ###=%
SET INSTALLDIR=%INSTALLDIR:###"=%
SET INSTALLDIR=%INSTALLDIR:###=%

REM REMOVING UNINSTALL.exe
SET INSTALLDIR=%INSTALLDIR:Uninstall.exe=%

REM CLEANUP
del setter.txt
del setter.bat
:CALLING
call %1 installit

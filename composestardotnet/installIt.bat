echo OFF
regedit /e export.reg HKEY_CURRENT_USER\Software\Microsoft\VisualStudio\7.1\AddIns\ComposestarVSAddin.Connect
type export.reg | find """ComposestarPath""" > setter.txt
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

REM CLEANUP
del setter.bat
del setter.txt
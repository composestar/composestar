@echo off
set __DMYY=%0%
set ANT_HOME=%__DMYY:\bin\AntEnv.bat=%
set PATH=%__DMYY:\AntEnv.bat=%;%PATH%
set __DMYY=
cmd
echo off
rem ****************************
rem *** Display help message ***
rem ****************************
if "%1" == "help" (
	echo [help]
	goto :eof
)
rem *** End of help message ***

echo Running Compose* System Test:

rem *************************************
rem *** Setting environment variables ***
rem *************************************
echo - Setting environment variables...
set MAKE4WIN=true
set MAKE4WIN_DRIVE=Y:
set TESTLOG=testlog.txt
set MAILSUBJECT="Compose* test report (do not reply)"
set MAILTO=dspenkel@hotmail.com
set MAILFROM=roy_spenkelink@hotmail.com
set SMTPSERVER=smtp.utwente.nl
set RETRIES=5
set RETRYTIME=2
set PUTTY_PATH=C:\local\dennis\PuTTY\
set CVS_PATH=C:\Program Files\TortoiseCVS\
set MAKE_PATH=C:\Program Files\TortoiseCVS\
set COMPOSESTAR_PATH=C:\local\dennis\composestar\
set COMPOSESTAR_EXAMPLE_PATH=C:\local\dennis\examples\

rem *** Tools ***
set PLINK_EXE=plink.exe
set CVS_EXE=cvs.exe
set MAKE_EXE=make.exe
set DEVENV_EXE=devenv.exe

rem *** End of setting environment variables ***


rem *************************
rem *** Creating log file ***
rem *************************
echo - Creating log file...
FOR /F "TOKENS=1,2*" %%A IN ('DATE/T') DO SET DATE=%%B
FOR /F "TOKENS=1,2*" %%A IN ('TIME/T') DO SET TIME=%%B
FOR /F "TOKENS=1,*" %%A IN ('VER') DO SET VER=%%B
echo Compose* System Test > %TESTLOG%
echo ********************** >> %TESTLOG%
echo Operating System: %VER%. >> %TESTLOG%
echo Test started on %DATE% at %TIME%. >> %TESTLOG%
echo. >> %TESTLOG%
rem *** End of creating log file ***


rem ********************************
rem *** Verifying required tools ***
rem ********************************
echo - Verifying tools...
which %PLINK_EXE% | find "1. " > nul
if not %ERRORLEVEL% LEQ 0 (
	set PLINK_EXE=%PUTTY_PATH%%PLINK_EXE%
	if not exist "%PLINK_EXE%" (
		echo Required tool 'PuTTY Link' not found! >> %TESTLOG%
		goto :failure
	)
)
echo Required tool 'PuTTY Link' found. >> %TESTLOG%

which %CVS_EXE% | find "1. " > nul
if not %ERRORLEVEL% LEQ 0 (
	set CVS_EXE=%CVS_PATH%%CVS_EXE%
	if not exist "%CVS_EXE%" (
		echo Required tool 'CVS' not found! >> %TESTLOG%
		goto :failure	
	)
)
echo Required tool 'CVS' found. >> testlog.txt

which %MAKE_EXE% | find "1. " > nul
if not %ERRORLEVEL% LEQ 0 (
	set CVS_EXE=%MAKE_PATH%%MAKE_EXE%
	if not exist "%MAKE_EXE%" (
		echo Required tool 'Make' not found! >> %TESTLOG%
		goto :failure	
	)
)
echo Required tool 'Make' found. >> testlog.txt

which %DEVENV_EXE% | find "1. " > nul
if not %ERRORLEVEL% LEQ 0 (
		echo Required tool 'Visual Studio' not found! >> %TESTLOG%
		goto :failure	
)
echo Required tool 'Visual Studio' found. >> testlog.txt

rem *** End of verifying required tools ***

rem ******************************************************
rem *** Add PuTTY session for Compose* CVS to registry ***
rem ******************************************************
echo - Loading PuTTY session to registry...
regedit -s putty-composestar.reg
if not %ERRORLEVEL% LEQ 1 (
	echo Failed to add PuTTY session to registry! >> %TESTLOG%
	goto :failure
)
echo PuTTY session added to registry. >> %TESTLOG%
rem *** End of adding PuTTY session for Compose* CVS to registry ***

rem **************************************
rem *** Update and build Compose* tool ***
rem **************************************

rem *** Clean Compose* folder ***
echo - Cleaning Compose*...
pushd "%COMPOSESTAR_PATH%"
call make clean > nul
if not %ERRORLEVEL% LEQ 0 (
	popd
	echo Cleaning Compose* folder failed! >> %TESTLOG%
	goto :failure
)
popd
echo Compose* folder cleaned. >> %TESTLOG%

rem *** Update Compose* from CVS ***
echo - Updating Compose* from CVS...
pushd "%COMPOSESTAR_PATH%"
set CVS_RSH=%PLINK_EXE%
set TRY=1
:update_composestar
cvs -Q -d :ext:pascal_durr@ComposestarCVS:/cvsroot/composestar -q -z9 update -d -P > nul
if not %ERRORLEVEL% LEQ 0 (
	set /a TRY=%TRY% + 1
	if %TRY% LSS %RETRIES% (
		popd
		echo Compose* update failed, will retry. >> %TESTLOG%
		pushd "%COMPOSESTAR_PATH%"
		rem sleep /p /k 00:02
		goto :update_composestar
	)

	popd
	echo Updating Compose* source from CVS failed! >> %TESTLOG%
	goto :failure
)
popd
echo Updating Compose* source from CVS succeeded. >> %TESTLOG%

rem *** Make Compose* source ***
echo - Building Compose*...
pushd "%COMPOSESTAR_PATH%"
rem if %MAKE4WIN% == true (call make4win %MAKE4WIN_DRIVE% > nul) ELSE (make src > nul) 
if not %ERRORLEVEL% LEQ 0 (
	popd
	echo Compose* build failed! >> %TESTLOG%
	goto :failure
)
popd
echo Compose* build done. >> %TESTLOG%

rem *** End of updating and building Compose* tool ***


rem ********************************
rem *** Update Compose* examples ***
rem ********************************
echo - Updating Compose* examples from CVS...
pushd "%COMPOSESTAR_EXAMPLE_PATH%"

rem *** Update Compose* examples from CVS ***
set CVS_RSH=%PLINK_EXE%
rem cvs -Q -d :ext:pascal_durr@ComposestarCVS:/cvsroot/composestar -q -z9 update -d -P > nul
if not %ERRORLEVEL% LEQ 1 (
	popd
	echo Updating Compose* examples from CVS failed! >> %TESTLOG%
	goto :failure
)
popd
echo Updating Compose* examples from CVS succeeded. >> %TESTLOG%
rem *** End of updating Compose* examples ***

call SystemTest.exe config.ini

echo. >> %TESTLOG%
echo System test completed successfully! >> %TESTLOG%

goto :eof


rem *** Sending e-mail notification for failed test***
:failure
echo.
echo Test failed, sending notification to '%MAILTO%'!
echo. >> %TESTLOG%
echo System test failed! >> %TESTLOG%
blat %TESTLOG% -subject %MAILSUBJECT% -to %MAILTO% -f %MAILFROM% -server %SMTPSERVER% > nul
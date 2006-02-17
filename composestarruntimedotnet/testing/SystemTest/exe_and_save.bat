echo off

rem *********************************
rem *** %1 is name of executable  ***
rem *** %2 is path to executable  ***
rem *** %3 is name of output file ***
rem *********************************

rem *** jump to bin directory of example ***
cd %2

rem *** delete output file if it exists ***
if exist %3 (
	del %3	
)

rem *** check if executable exists

if not exist %1 (
	echo Required executable %1 not found! >> %3
	goto :eof
)

rem *** run executable and store output in output file
CALL %1 > %3

rem *** jump back to system test directory ***

	




ECHO OFF
REM **************************************************************************
REM Batch file to run make under windows
REM
REM The Windows command prompt is having difficulties with long filenames,
REM i.e. if the Composestar source is in a long-named folder location it
REM crashed the java compiler with file not found exceptions. Use this batch
REM file to make a virtual mapping of the Composestar source folder to a
REM drive letter and avoid the problem.
REM **************************************************************************

ECHO Mapping source root to virtual drive...

IF "%1"=="" (
  SET DRIVE=z:
) ELSE (
	SET DRIVE=%1
)

SUBST %DRIVE% .
IF %ERRORLEVEL% == 0 (
	CMD /C "%DRIVE% && make src"
	SUBST /D %DRIVE%
) ELSE (
	ECHO Please specify a free drive letter.
)
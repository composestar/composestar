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
cd ..

IF "%1"=="" (
  SET DRIVE=q:
) ELSE (
	SET DRIVE=%1
)

SUBST %DRIVE% .
IF %ERRORLEVEL% == 0 (
        CMD /C "%DRIVE% && cd Core && cd Compiletime && make clean && make && cd .. && cd runtime && make clean && make && cd .. && cd .. && cd DotNET &&cd compiletime && make clean && make && cd .. &&cd runtime && make clean && make && cd .. "
	IF not %ERRORLEVEL% == 0 (
		pause
	)
	SUBST /D %DRIVE%
) ELSE (
	ECHO Please specify a free drive letter.
	pause
)

cd DotNET

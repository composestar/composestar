@ECHO OFF

REM Step 1 - Generate the reflection information
"{@SandcastlePath}ProductionTools\MRefBuilder" /out:reflection.org {@Dependencies} {@DocInternals} *.dll *.exe

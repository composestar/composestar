@echo off
@setlocal

set BUILD_FILE=build_total.xml
set ANT_HOME=%CD%\Ant
if "%JAVA_HOME%" == "" set JAVA_HOME=c:\j2sdk1.4.2

call Ant\bin\ant.bat -buildfile %BUILD_FILE% -logger org.apache.tools.ant.listener.MailLogger -propertyfile nightlybuild.properties nightlybuild

@endlocal
@echo off

IF NOT DEFINED JAVA_HOME SET /p JAVA_HOME= <"%~dp0java.home.txt"
SET JAVAV=1.6

REM check for Java %JAVAV% (or higher)
REM also fails if java.exe not found
SET JAVA=java
"%JAVA%" -version:%JAVAV%+ -version 2>NUL
IF NOT ERRORLEVEL 1 GOTO :RunAddis

REM try the JAVA_HOME
SET JAVA=%JAVA_HOME%\bin\java.exe
"%JAVA%" -version:%JAVAV%+ -version 2>NUL
IF NOT ERRORLEVEL 1 GOTO :RunAddis

GOTO :JavaMissingError

:RunAddis
"%JAVA%" -jar "%~dp0addis.jar" "%~1"
GOTO End

:JavaMissingError
wscript "%~dp0errordialog.vbs" %JAVAV%
GOTO End

:End

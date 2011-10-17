@echo off

set JAVAV=1.6
REM check for Java %JAVAV% (or higher)
REM also fails if java.exe not found
SET HAVEJAVA=TRUE
java -version:%JAVAV%+ -version 2>NUL
IF ERRORLEVEL 1 SET HAVEJAVA=FALSE

IF %HAVEJAVA%==TRUE GOTO RunAddis
GOTO JavaMissingError

:RunAddis
java -jar %~dp0addis.jar "%~1"
GOTO End

:JavaMissingError
REM FIXME: should display an error *DIALOG* so the user is notified of the failure.
wscript %~dp0errordialog.vbs %JAVAV%
GOTO End

:End

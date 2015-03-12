@echo off

call ..\..\..\skripte-dosshell\einstellungen.bat

set cp=..\..\de.bsvrz.sys.funclib.bitctrl\de.bsvrz.sys.funclib.bitctrl-runtime.jar
set cp=%cp%;..\de.bsvrz.dua.fehlertls-runtime.jar
set cp=%cp%;..\de.bsvrz.dua.fehlertls-test.jar
set cp=%cp%;..\..\junit-4.1.jar

title Pruefungen SE4 - DUA, SWE 4.DeFa

echo ========================================================
echo # Pruefungen SE4 - DUA, SWE 4.DeFa
echo #
echo # Automatischer JUnit-Test (Extra) 
echo ========================================================
echo.

%java% -cp %cp% org.junit.runner.JUnitCore de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikationTest
pause

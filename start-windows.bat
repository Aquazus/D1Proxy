@echo off
title D1Proxy
java -version 1>nul 2>nul || (
   echo Error: Java is not installed or cannot be found in your path
   echo.
   pause
   exit /b
)
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%%l%%m"
if not "%jver:~0,2%" == "11" (
   echo Error: D1Proxy can only run on Java 11
   echo Please make sure the Java in your system path is version 11 or greater
   echo.
   pause
   exit /b
)
java -Dfile.encoding=UTF-8 -jar d1proxy-1.10.4.jar
pause
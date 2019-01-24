@echo off
title D1Proxy
java -version 1>nul 2>nul || (
   echo Error: Java is not installed or cannot be found in your path
   pause
   exit /b
)
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%%l%%m"
if not "%jver:~0,2%" == "11" (
   echo Error: D1Proxy can only run on Java 11
   pause
   exit /b
)
java -jar d1proxy-1.10.3-jar-with-dependencies.jar
pause
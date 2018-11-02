#!/bin/sh
java -Dfile.encoding=UTF-8 -jar d1proxy-*.jar 2>&1 | tee d1proxy.log
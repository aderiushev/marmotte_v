@echo off
cd backups 
set day=%DATE:~0,2%
set month=%DATE:~3,2%
set year=%DATE:~6,4%
set hour=%TIME:~0,2%
set minute=%TIME:~3,2%
set second=%TIME:~6,2%
jar -cf "%day%.%month%.%year%-%hour%.%minute%.%second%".jar ../*.*
pause
@echo off

rem Запустим выбор услуги
echo Старт выбора услуги

java -cp dist/QSystem.jar ru.apertum.qsystem.Welcome -sport 3128 -cport 3129 -s localhost -wm touch -debug +med +info

pause
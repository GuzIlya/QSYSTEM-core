@echo off

rem �����⨬ �롮� ��㣨
echo ���� �롮� ��㣨

java -cp dist/QSystem.jar ru.apertum.qsystem.Welcome -sport 3128 -cport 3129 -s localhost -wm touch -debug +med +info

pause
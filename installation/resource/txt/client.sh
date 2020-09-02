#!/bin/sh
java -cp dist/QSystem.jar ru.apertum.qsystem.Client  -sport $serverPort -cport $clientPort -s $serverAdress -cfg config/clientboard.xml --point $clientPoint


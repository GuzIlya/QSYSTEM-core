﻿#!/bin/sh
java -cp dist/QSystem.jar ru.apertum.qsystem.Desktop  -sport $serverPort -cport $clientPort -s $serverAdress -cfg config/clientboard11.xml --point $clientPoint


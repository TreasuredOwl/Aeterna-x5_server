@echo off
chcp 65001 >nul
title Зареєструвати ігровий сервер
color 03
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/* com.l2journey.tools.gsregistering.BaseGameServerRegister -c
pause
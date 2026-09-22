@echo off
chcp 65001 >nul
title Aeterna - [Логин Сервер]
color 03
:start
echo Запуск Логин Сервера [Aeterna - Стабільна Версія].
echo ------------------------------------------------------------------------------

java -server -Dfile.encoding=UTF-8 -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xms128m -Xmx256m -Dlogback.configurationFile=./configuration/logback.xml -cp ./../libs/*;Loginserver.jar com.l2journey.loginserver.LoginServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end

:restart
echo.
echo Адміністратор перезапустив Логин Сервер.
echo.
goto start

:error
echo.
echo Логин Сервер неочікувано зупинився!
echo.

:end
echo.
echo Логин Сервер завершив роботу.
echo.
pause

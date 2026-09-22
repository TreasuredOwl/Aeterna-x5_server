@echo off
title L2Journey - Gameserver
color 02
:start
echo Iniciando GameServer.
echo ------------------------------------------------------------------------------

java -server -Dfile.encoding=UTF-8 -Djava.awt.headless=true -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true -Djava.util.logging.manager=com.l2journey.log.ServerLogManager -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xmx4g -Xms2g -Dlogback.configurationFile=./logback.xml -cp ./../libs/*;Gameserver.jar com.l2journey.gameserver.GameServer

REM NOTE: If you have a powerful machine, you could modify/add some extra parameters for performance, like:
REM -Xms1536m
REM -Xmx3072m
REM -XX:+AggressiveOpts
REM Use this parameters carefully, some of them could cause abnormal behavior, deadlocks, etc.
REM More info here: http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end

:restart
echo.
echo Admin Restarted Game Server.
echo.
goto start

:error
echo.
echo Game Server parou inesperadamente!
echo.

:end
echo.
echo Game Server Terminado.
echo.
pause
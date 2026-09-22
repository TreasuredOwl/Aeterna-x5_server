#!/bin/bash

echo "L2Journey - Gameserver"
echo "------------------------------------------------------------------------------"

while true; do
    echo "Iniciando GameServer."
    echo "------------------------------------------------------------------------------"

    java -server \
        -Dfile.encoding=UTF-8 \
        -Djava.awt.headless=true \
        -Dsun.java2d.d3d=false \
        -Dsun.java2d.noddraw=true \
        -Djava.util.logging.manager=com.l2journey.log.ServerLogManager \
        -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn \
        -XX:+UseZGC \
        -Xmx4g \
        -Xms2g \
        -Dlogback.configurationFile=./logback.xml \
        -cp "./../libs/*:Gameserver.jar" \
        com.l2journey.gameserver.GameServer

    # NOTE: If you have a powerful machine, you could modify/add some extra parameters for performance, like:
    # -Xms1536m
    # -Xmx3072m
    # -XX:+AggressiveOpts
    # Use this parameters carefully, some of them could cause abnormal behavior, deadlocks, etc.
    # More info here: http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html

    EXIT_CODE=$?

    if [ $EXIT_CODE -eq 2 ]; then
        echo ""
        echo "Admin Restarted Game Server."
        echo ""
        continue
    elif [ $EXIT_CODE -eq 1 ]; then
        echo ""
        echo "Game Server parou inesperadamente!"
        echo ""
        break
    else
        echo ""
        echo "Game Server Terminado."
        echo ""
        break
    fi
done
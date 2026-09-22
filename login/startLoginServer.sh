#!/bin/bash

echo "Aeterna - [Логин Сервер]"
echo "------------------------------------------------------------------------------"

while true; do
    echo "Запуск Логин Сервера [Aeterna - Стабільна Версія]."
    echo "------------------------------------------------------------------------------"

    java -server \
        -Dfile.encoding=UTF-8 \
        -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn \
        -XX:+UseZGC \
        -Xms128m \
        -Xmx256m \
        -Dlogback.configurationFile=./configuration/logback.xml \
        -cp "./../libs/*:Loginserver.jar" \
        com.l2journey.loginserver.LoginServer

    EXIT_CODE=$?

    if [ $EXIT_CODE -eq 2 ]; then
        echo ""
        echo "Адміністратор перезапустив Логин Сервер."
        echo ""
        continue
    elif [ $EXIT_CODE -eq 1 ]; then
        echo ""
        echo "Логин Сервер неочікувано зупинився!"
        echo ""
        break
    else
        echo ""
        echo "Логин Сервер завершив роботу."
        echo ""
        break
    fi
done

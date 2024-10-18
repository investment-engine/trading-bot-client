#!/bin/bash

export JAR_NAME=trading-bot-client-app.jar
export LOG_FILE=application.log

export BYBIT_API_URL=https://api.bybit.com
export BYBIT_TOKEN=ЗАМЕНИТЬ_НА_СВОЕ_ЗНАЧЕНИЕ
export BYBIT_SECRET_KEY=ЗАМЕНИТЬ_НА_СВОЕ_ЗНАЧЕНИЕ
export SIGNAL_API_URL=http://185.125.218.17:8080/api/trade-logs/open
export BALANCE_FILE_PATH=balance.properties

echo "Запуск приложения..."
nohup java -jar $JAR_NAME >> $LOG_FILE 2>&1 &

echo "Приложение запущено. Логи пишутся в $LOG_FILE"
@echo off

set JAR_NAME=trading-bot-client-app.jar
set LOG_FILE=application.log

set BYBIT_API_URL=https://api.bybit.com
set BYBIT_TOKEN=ЗАМЕНИТЬ_НА_СВОЕ_ЗНАЧЕНИЕ
set BYBIT_SECRET_KEY=ЗАМЕНИТЬ_НА_СВОЕ_ЗНАЧЕНИЕ
set SIGNAL_API_URL=http://185.125.218.17:8080/api/trade-logs/open
set BALANCE_FILE_PATH=balance.properties

echo Запуск приложения...
start /B "" java -jar %JAR_NAME% >> %LOG_FILE% 2>&1
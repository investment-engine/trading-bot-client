@echo off

set JAR_NAME=trading-bot-client-app-0.0.1-SNAPSHOT.jar
set CONFIG_FILE=application.properties
set LOG_FILE=application.log

echo Запуск приложения...
start /B "" java -jar %JAR_NAME% --spring.config.location=file:%CONFIG_FILE% >> %LOG_FILE% 2>&1
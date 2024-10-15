FROM openjdk:21-jdk

WORKDIR /app

COPY balance.properties /app/balance.properties
COPY trading-bot-client-app.jar /app/trading-bot-client-app.jar

# Устанавливаем переменные окружения.
ENV BYBIT_API_URL=https://api.bybit.com
ENV BYBIT_TOKEN=ЗАМЕНИТЬ_НА_СВОЕ_ЗНАЧЕНИЕ
ENV BYBIT_SECRET_KEY=ЗАМЕНИТЬ_НА_СВОЕ_ЗНАЧЕНИЕ
ENV SIGNAL_API_URL=http://185.125.218.17:8080/api/trade-logs/open
ENV BALANCE_FILE_PATH=balance.properties

CMD ["java", "-jar", "trading-bot-client-app.jar"]
FROM openjdk:21-jdk-slim

# Устанавливаем необходимые зависимости
RUN apt-get update && apt-get install -y \
    libxext6 libxrender1 libxtst6 libxi6 \
    xvfb x11vnc

# Копируем приложение
WORKDIR /app
COPY .env /app/.env
COPY target/short_links2-1.0-SNAPSHOT.jar app.jar

# Настраиваем виртуальный дисплей через Xvfb
ENV DISPLAY=:99
RUN Xvfb :99 -screen 0 1024x768x16 &

# Запускаем приложение через Xvfb
CMD Xvfb :99 -screen 0 1024x768x16 & java -jar app.jar

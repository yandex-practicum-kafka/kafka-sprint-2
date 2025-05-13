# Используем легковесный образ JDK 21 для сборки приложения
FROM openjdk:21-jdk-slim AS builder

# Указываем рабочую директорию для сборки
WORKDIR /app

# Копируем директорию gradle для управления зависимостями
COPY gradle gradle

# Копируем скрипт для сборки Gradle
COPY gradlew gradlew

# Копируем файл настроек Gradle
COPY settings.gradle settings.gradle

# Копируем основной файл конфигурации Gradle
COPY build.gradle build.gradle

# Копируем исходный код проекта
COPY src src

# Выполняем сборку проекта с помощью Gradle, пропуская тесты
RUN ./gradlew clean build -x test

# Используем легковесный образ JRE 21 для запуска приложения
FROM openjdk:21-jdk-slim

# Указываем рабочую директорию для выполнения приложения
WORKDIR /app

# Копируем собранный JAR-файл из предыдущего этапа сборки
COPY --from=builder /app/build/libs/KafkaStreamsApp-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

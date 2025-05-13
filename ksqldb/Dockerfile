# Используем официальный образ OpenJDK 21 JDK с уменьшенным размером в качестве этапа сборки
FROM openjdk:21-jdk-slim AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем директории и файлы Gradle для настройки сборки
COPY gradle gradle
COPY gradlew gradlew
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

# Копируем исходный код приложения
COPY src src

# Выполняем сборку проекта Gradle, пропуская тесты для ускорения сборки
RUN ./gradlew clean build -x test

# На следующем этапе используем более легкий образ OpenJDK 21 JDK для запуска приложения
FROM openjdk:21-jdk-slim

# Определяем рабочую директорию для запуска приложения
WORKDIR /app

# Копируем собранный JAR-файл из этапа builder
COPY --from=builder /app/build/libs/KsqlDbApp-0.0.1-SNAPSHOT.jar app.jar

# Определяем команду запуска приложения с помощью Java и передаваем JAR-файл
ENTRYPOINT ["java","-jar","app.jar"]

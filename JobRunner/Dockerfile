#FROM gradle:7.3.3-jdk11 AS build
#WORKDIR /home/gradle/src
#COPY --chown=gradle:gradle . /home/gradle/src
## Собираем проект и выполняем миграции
#RUN gradle build --no-daemon
#RUN gradle update --no-daemon
#
## Используйте официальный образ Java Runtime для запуска приложения
#FROM openjdk:11-jre-slim
#EXPOSE 8080
#WORKDIR /app
## Копируем только артефакт сборки и что требуется для запуска приложения
#COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]

FROM maven:3.6.3-jdk-11 AS build
WORKDIR /app

# Копируем только pom.xml и исходный код. Это позволяет утилизировать слои кэша Docker максимально эффективно,
# reusing the maven dependencies that were downloaded previously
COPY pom.xml .
COPY src ./src

# Пакетируем приложение
RUN mvn package

# Этап 2: Развертывание
FROM openjdk:11-jre-slim
WORKDIR /app

# Копируем собранный jar файл из предыдущего этапа
COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
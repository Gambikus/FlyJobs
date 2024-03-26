FROM gradle:7.3.3-jdk11 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . /home/gradle/src
# Собираем проект и выполняем миграции
RUN gradle build --no-daemon
RUN gradle update --no-daemon

# Используйте официальный образ Java Runtime для запуска приложения
FROM openjdk:11-jre-slim
EXPOSE 8080
WORKDIR /app
# Копируем только артефакт сборки и что требуется для запуска приложения
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

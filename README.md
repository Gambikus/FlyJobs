# Библиотека для запуска асинхронных операций FlyJobs

## Иcходный код
[Папка с библиотекой](JobRunner/)  
[Папка с тестовым проектом, который использует библиотеку](SampleProject/)

## Чтобы использовать библиотеку, нужно подготовить JAR
Установить артефакт в локальный репозиторий Maven через команду: 
```
mvn package

mvn install:install-file -Dfile=FlyJobs/target/library-1.0-SNAPSHOT.jar -DgroupId=com.example -DartifactId=flying-jobs -Dversion=1.0 -Dpackaging=jar
```
Далее нужно добавить библиотеку в зависимости вашего проекта через Maven/Gradle

## Для конфигурации библиотеки нужно передать строку подключения к БД, имя пользователя и пароль в класс конфигурации(Так же можно передать список идентификаторов чатов телеграм, в которые будут приходить оповещения о работе джоб), например

```
val flyJobConfig = FlyJobConfigurator(
    "jdbc:postgresql://localhost:5432/lib_db",
    "user", "password",
    listOf(-1002065857636)
)
```
## Для планирования операции нужно получить экземпляр SchedulerService из класса конфига
```
scheduler = flyJobsConfigurator.getScheduler()
```

Есть две опции для планированию: один запуск в конкретное время и запуск по CRON расписанию, а в качестве операции нужно передать ссылку на метод 

```
config.getScheduler().scheduleJob(
   "*/1 * * * *",
   Second::fail
)

config.getScheduler().scheduleJob(
    Timestamp(System.currentTimeMillis()),
    Second::print
)
```

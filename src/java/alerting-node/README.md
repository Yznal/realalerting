# alerting-node

## Настроить окружение

* Java 21
* Gradle 8.5

## Локальная проверка

Работоспособность приложения проверяет этот
тест: [AlertingNodeEntryPointIT.java](src/test/java/org/realerting/AlertingNodeEntryPointIT.java)

Также доступные конфигурации запуска можно найти в [.run](.run). Intellij IDEA подтянет их при открытии проекта.

## Установка на VM

* Собрать jar через `./gradlew clean build`
* Загрузить на виртуальную машину
* На ней же запустить через `java --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -jar <path_to_jar> <path_to_config_yaml>`

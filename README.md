## Практическая работа 3, решение
  
•   решение обязательного **Задание 1** см. в каталоге [/kafkastreams](/kafkastreams) с описанием в файле [/kafkastreams/README.md](/kafkastreams/README.md).  
•   решение необязательного **Задание 2** в процессе  
текущие наработки см. в каталоге [/ksqldb](/ksqldb) с описанием в файле [/ksqldb/README.md](/ksqldb/README.md)  

## Практическая работа 3, постановка

Вы изучили всю теорию второго модуля, а значит, пришло время попрактиковаться и выполнить проект.  

**Тема проекта:** Разработка упрощённого сервиса обмена сообщениями.  

**Цель проекта:** Закрепить знания о потоковой обработке данных и архитектуре распределённых систем, применив их на практике с использованием Kafka.  

Проект состоит из двух заданий:  

•   **Задание 1** ― обязательное. Важно выполнить эту часть, чтобы получить сертификат по окончании курса.  
•   **Задание 2** ― необязательное. Выполнение этого задания и его результаты не влияют на получение сертификата. Вы можете выполнить это задание, если хотите углубиться в тему и дополнительно попрактиковаться. Ревьюер проверит эту часть, если вы её выполните, и даст комментарии.  

### Как выполнить проект  

Выполняйте проект на своём компьютере, использовать облачные сервисы необязательно. Например, можно хранить материалы в вашем репозитории на GitHub.  

### Задание 1. Разработка функциональности с помощью библиотеки для потоковой обработки данных  

**Задача:** Создайте систему обработки потоков сообщений с функциональностью блокировки пользователей и цензуры сообщений. Разверните систему с использованием Docker-сompose и настройте необходимые топики Kafka.  

#### Подзадача 1. Блокировка нежелательных пользователей  

•   Реализуйте поток обработки, который позволяет пользователям блокировать других пользователей.  
•   Для каждого пользователя создайте отдельный список заблокированных пользователей. Список должен храниться на диске.  
•   Реализуйте автоматическую фильтрацию сообщений от нежелательных пользователей так, чтобы они не доходили до получателей.  

#### Подзадача 2. Цензура на слова  

•   Создайте список запрещённых слов, который можно динамически обновлять.  
•   Добавьте компонент, обрабатывающий поток сообщений и удаляющий или маскирующий запрещённые слова.  
•   Реализуйте логику: все сообщения должны проходить через компонент цензуры перед отправкой получателю.  

#### Инфраструктура  

•   Используйте Docker-сompose для развёртывания.  
•   Подготовьте необходимые топики Kafka, например:  
    •   `messages` ― для входящих сообщений,  
    •   `filtered_messages` ― для сообщений после обработки,  
    •   `blocked_users` ― для списка заблокированных пользователей.  

#### Тестирование  

Создайте тестовые данные для отправки в топики для проверки функций системы:   блокировки сообщений от заблокированных пользователей и цензуры сообщений.  

#### Инструменты

Выполняйте проект на одном из языков программирования: Java, Python или Go. Используйте подходящую библиотеку: Kafka Streams, Faust или Goka.  

### Как должен выглядеть результат  

Выполненное задание должно включать:  

•   Docker-compose файл, который развернёт инфраструктуру для выполнения задания.  
•   Рабочий код с чёткой структурой. Важно:  
    •   Входящие сообщения обрабатываются в соответствии с логикой блокировки и цензуры.  
    •   Сообщения корректно фильтруются по заблокированным пользователям и цензурировались.  
    •   Код логично структурирован.  
    •   Код понятно прокомментирован.  
•   Файл `README.md`, в котором есть:  
    •   описание классов и логики работы приложения,  
    •   инструкция по запуску проекта,  
    •   инструкция по проведению тестирования,  
    •   тестовые данные, которые надо отправить в топики для проверки.  

## Задание 2. Анализ и агрегирование сообщений с помощью ksqlDB  

*Это необязательное задание.* Вы можете выполнить его, чтобы глубже разобраться с потоковой обработкой в Apache Kafka.  

**Задача:** Реализуйте аналитику для системы обмена сообщениями с помощью ksqlDB.  

Считайте, что сообщения в JSON-формате содержат поля `user_id`, `recipient_id`, `message` и `timestamp`.  

### Подзадача 1. Анализ сообщений в реальном времени  

1.  Создайте поток в ksqlDB (`messages_stream`), который будет принимать входящие сообщения из Kafka.  
2.  Реализуйте таблицы, подсчитывающие:  
    •   общее количество отправленных сообщений;  
    •   количество уникальных получателей сообщений;  
    •   5 наиболее активных пользователей (по количеству отправленных сообщений).  

### Подзадача 2. Сбор статистики по пользователям  

1.  Создайте таблицу `user_statistics` для агрегирования данных по каждому пользователю.  
2.  Реализуйте запросы:  
    •   подсчёт количества сообщений, отправленных каждым пользователем;  
    •   подсчёт количества уникальных получателей для каждого пользователя;  
    •   сохранение этих данных в таблице для последующего использования.  

## Инфраструктура  

Используйте `docker-compose` для развёртывания ksqlDB.  

Подготовьте топики Kafka для входящих сообщений и результатов агрегирования:  

•   для входящих сообщений (например, `messages`);  
•   для статистики по пользователям (например, `user_statistics`).  

## Как должен выглядеть результат  

В системе:  

•   Пользователи могут выполнять запросы к ksqlDB для получения статистики.  
•   Отображаются данные в реальном времени на основе входящих сообщений.  

Выполненное задание должно включать:  

•   `docker-compose.yml` файл, который развернёт инфраструктуру для выполнения задания.  
•   `ksqldb-queries.sql` c выполненными запросами:  
    •   создание исходного потока;  
    •   создание таблицы общего количества отправленных сообщений;  
    •   создание таблицы, подсчитывающей количество уникальных получателей для всех сообщений;  
    •   создание таблицы, подсчитывающей количество сообщений, отправленных каждым пользователем;  
    •   создание таблицы для агрегирования данных по каждому пользователю.  
•   `Readme.md`, в котором есть:  
    •   инструкция по запуску проекта,  
    •   тестовые данные для ksqlDB,  
    •   инструкция по отправке тестовых данных в топик,  
    •   инструкция по проведению тестирования,  
    •   тестовые запросы в ksqlDB, чтобы проверить результаты:  
        *   вывести общее количество отправленных сообщений;  
        *   вывести уникальных получателей;  
        *   вывести без потокового обновления 5 наиболее активных пользователей.  

-- Создаем поток из топика messages
CREATE STREAM messages_stream (
    user_id VARCHAR KEY,
    recipient_id VARCHAR,
    message VARCHAR,
    timestamp BIGINT
) WITH (
    KAFKA_TOPIC = 'messages',
    VALUE_FORMAT = 'JSON'
);

-- Таблица: Общее количество отправленных сообщений
CREATE TABLE total_messages AS
SELECT
    COUNT(*) AS total_count
FROM messages_stream
GROUP BY NULL; -- Нужно использовать GROUP BY NULL для агрегатов без группировки по столбцам

-- Таблица: Количество уникальных получателей сообщений
CREATE TABLE unique_recipients AS
SELECT
    COUNT(DISTINCT recipient_id) AS unique_count
FROM messages_stream
GROUP BY NULL; -- Группируем по NULL для агрегата без группировки по другим полям

-- Создание таблицы: Топ-5 активных пользователей
CREATE TABLE top_users AS
SELECT
    user_id,
    COUNT(*) AS message_count
FROM messages_stream
GROUP BY user_id
HAVING ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY COUNT(*) DESC) <= 5;

-- Таблица: Статистика пользователей
CREATE TABLE user_statistics AS
SELECT
    user_id,
    COUNT(*) AS sent_messages,
    COUNT(DISTINCT recipient_id) AS unique_recipients
FROM messages_stream
GROUP BY user_id;

-- Создание нового потока: сообщения, перехваченные для агрегации
CREATE STREAM messages_stream_rekeyed AS
SELECT *
FROM messages_stream
PARTITION BY user_id; -- Здесь вы можете перезаписать ключи, создавая новый поток на основе существующего.

-- Таблица: Статистика пользователей с оптимизацией для доступа по ключу
CREATE TABLE user_statistics_keyed AS
SELECT
    user_id,
    COUNT(*) AS sent_messages,
    COUNT(DISTINCT recipient_id) AS unique_recipients
FROM messages_stream_rekeyed
GROUP BY user_id;

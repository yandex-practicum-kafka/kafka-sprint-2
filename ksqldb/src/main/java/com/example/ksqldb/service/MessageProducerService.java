package com.example.ksqldb.service;

import com.example.ksqldb.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    /**
     * KafkaTemplate, тип — String для ключа и значения.
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Название топика для отправки сообщений; значение из конфигурации.
     */
    @Value("${kafka.topic.messages}")
    private String messagesTopic;

    /**
     * Объект Random для генерации случайных чисел.
     */
    private final Random random = new Random();

    /**
     * Метод, запускаемый по расписанию с интервалом, заданным в конфигурации message.generation.interval.
     * Создаёт сообщение с случайными UUID отправителя и получателя, генерирует текст сообщения и отправляет в Kafka.
     */
    @Scheduled(fixedRateString = "${message.generation.interval}")
    public void sendMessage() {
        // Генерация уникальных идентификаторов отправителя и получателя
        String userId = UUID.randomUUID().toString(); 
        String recipientId = UUID.randomUUID().toString();

        // Генерация текста сообщения с использованием случайного числа
        String messageText = "Hello, kSqlDb! Message " + random.nextInt(100);

        // Текущая временная метка
        Instant timestamp = Instant.now();

        // Создание объекта сообщения
        Message message = new Message(userId, recipientId, messageText, timestamp);

        // Логирование отправляемого сообщения
        log.info("Sending message: {}", message);

        // Отправка сообщения в Kafka в виде JSON-строки
        kafkaTemplate.send(
            messagesTopic,
            userId, // ключ — ID пользователя
            String.format(
                "{\"user_id\":\"%s\", \"recipient_id\":\"%s\", \"message\":\"%s\", \"timestamp\":%d}",
                message.getUserId(),
                message.getRecipientId(),
                message.getMessage(),
                message.getTimestamp().toEpochMilli() // временная метка в миллисекундах
            )
        );
    }   
}

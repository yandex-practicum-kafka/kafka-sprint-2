package com.example.kafkastreams.service;

import com.example.kafkastreams.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@EnableScheduling
@Profile("generator")
public class MessageGeneratorService {

    /**
     * KafkaTemplate для отправки сообщений в Kafka.
     */
    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    /**
     * Название Kafka-топика для отправки сообщений.
     */
    @Value("${kafka.topic.messages}")
    private String messagesTopic;

    /**
     * Ресурс с файлом шаблонов сообщений.
     */
    @Value("${messages.file.path}")
    private Resource messagesResource;

    /**
     * Генератор случайных чисел для выбора пользователей и сообщений.
     */
    private final Random random = new Random();

    /**
     * Список шаблонов сообщений, загруженный из файла.
     */
    private List<String> messageTemplates;

    /**
     * Метод вызывается после создания бина.
     * Загружает шаблоны сообщений из указанного файла.
     */
    @PostConstruct
    public void loadMessages() {
        messageTemplates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(messagesResource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messageTemplates.add(line);
            }
        } catch (IOException e) {
            log.error("Ошибка при загрузке сообщений из файла: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить сообщения из файла: ", e);
        }
        log.info("Загружено {} сообщений из messages.txt", messageTemplates.size());
    }

    /**
     * Метод с аннотацией Scheduled, вызываемый с фиксированным интервалом 100 мс.
     * Генерирует сообщение с рандомными userId и receiverId, выбирает случайный шаблон сообщения,
     * создает объект Message и отправляет его в Kafka.
     */
    @Scheduled(fixedRate = 100)
    public void generateAndSendMessage() {
        int userId = random.nextInt(100) + 1;      // ID отправителя: случайное число от 1 до 100
        int receiverId = random.nextInt(100) + 1;  // ID получателя: случайное число от 1 до 100

        String messageFromFile = messageTemplates.get(random.nextInt(messageTemplates.size())); // случайный шаблон
        Message message = new Message(userId, receiverId, messageFromFile);

        kafkaTemplate.send(messagesTopic, message);
        log.info("Отправлено сообщение: {}", message);
    }
}

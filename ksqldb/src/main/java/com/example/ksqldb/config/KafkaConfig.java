package com.example.ksqldb.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    /**
     * Название Kafka-топика для сообщений.
     * Значение берётся из файла настроек (application.properties или application.yml) по ключу kafka.topic.messages.
     */
    @Value("${kafka.topic.messages}")
    private String messagesTopic;

    /**
     * Название Kafka-топика для статистики пользователей.
     * Значение берётся из файла настроек по ключу kafka.topic.user-statistics.
     */
    @Value("${kafka.topic.user-statistics}")
    private String userStatisticsTopic;

    /**
     * Создаёт и конфигурирует топик для сообщений.
     * Топик создаётся с 1 партицией и 1 репликой.
     *
     * @return объект NewTopic для топика сообщений
     */
    @Bean
    public NewTopic messagesTopic() {
        return TopicBuilder.name(messagesTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Создаёт и конфигурирует топик для статистики пользователей.
     * Топик создаётся с 1 партицией и 1 репликой.
     *
     * @return объект NewTopic для топика статистики
     */
    @Bean
    public NewTopic userStatisticsTopic() {
        return TopicBuilder.name(userStatisticsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

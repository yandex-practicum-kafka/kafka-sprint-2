package com.example.kafkastreams.config;

import com.example.kafkastreams.model.Message;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // Адрес Kafka брокера (подставляется из настроек)
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    // Название топика для сообщений
    @Value("${kafka.topic.messages}")
    private String messagesTopic;

    // Название топика для фильтрованных сообщений
    @Value("${kafka.topic.filtered-messages}")
    private String filteredMessagesTopic;

    // Название топика для заблокированных пользователей
    @Value("${kafka.topic.blocked-users}")
    private String blockedUsersTopic;

    // Создает топик для сообщений (количество партиций = 1, репликаций = 1)
    @Bean
    public NewTopic messagesTopic() {
        return new NewTopic(messagesTopic, 1, (short) 1);
    }

    // Создает топик для отфильтрованных сообщений
    @Bean
    public NewTopic filteredMessagesTopic() {
        return new NewTopic(filteredMessagesTopic, 1, (short) 1);
    }

    // Создает топик для заблокированных пользователей
    @Bean
    public NewTopic blockedUsersTopic() {
        return new NewTopic(blockedUsersTopic, 1, (short) 1);
    }

    // --- Конфигурация продюсера (создает сообщения) для профиля "generator" ---

    @Bean
    @Profile("generator")
    public ProducerFactory<String, Message> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Адрес Kafka брокера
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // Сериализатор ключа (строка)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Сериализатор значения (объект Message в JSON)
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @Profile("generator")
    public KafkaTemplate<String, Message> kafkaTemplate() {
        // Шаблон для отправки сообщений
        return new KafkaTemplate<>(producerFactory());
    }

    // --- Конфигурация потребителя (чтение сообщений) для профиля "processor" ---

    @Bean
    @Profile("processor")
    public ConsumerFactory<String, Message> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Адрес Kafka брокера
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // Десериализатор ключа (строка)
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Десериализатор значения (объект Message в JSON)
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Группа потребителей
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "message-group");
        // Разрешает пакеты при десериализации
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastreams.model");
        // Обработка с начала раздела (если нет коммитов)
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(
            configProps,
            new StringDeserializer(),
            new JsonDeserializer<>(Message.class)
        );
    }

    // Фабрика для прослушивания Kafka сообщений типа Message
    @Bean
    @Profile("processor")
    public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // Конфигурация другого потребителя для строковых сообщений
    @Bean
    @Profile("processor")
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Адрес Kafka
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // Десериализаторы для ключа и значения — строки
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Группа
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "string-group");
        // Обработка с начала, если нет коммитов
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new StringDeserializer());
    }

    // Фабрика для строковых сообщений
    @Bean
    @Profile("processor")
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        return factory;
    }
}

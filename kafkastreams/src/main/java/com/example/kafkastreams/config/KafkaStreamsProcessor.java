package com.example.kafkastreams.config;

import com.example.kafkastreams.model.Message;
import com.example.kafkastreams.service.BlockedUserService;
import com.example.kafkastreams.service.CensorshipService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
@Profile("processor")
@Slf4j
public class KafkaStreamsProcessor {

    // Название входного топика с исходными сообщениями
    @Value("${kafka.topic.messages}")
    private String messagesTopic;

    // Название выходного топика с отфильтрованными и цензурированными сообщениями
    @Value("${kafka.topic.filtered-messages}")
    private String filteredMessagesTopic;

    // Внедрение сервиса для проверки блокировок пользователей
    @Autowired
    private BlockedUserService blockedUserService;

    // Внедрение сервиса для цензуры текста сообщений
    @Autowired
    private CensorshipService censorshipService;

    // Адрес Kafka брокера
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    // Конфигурация Kafka Streams
    @Bean(name = "defaultKafkaStreamsConfig")
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        // Уникальный идентификатор приложения потоков
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-id");
        // Адрес Kafka брокера
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // сериализация ключа — строка
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // сериализация значения — JSON объекта Message
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(Message.class).getClass());
        // доверенные пакеты для десериализации
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkastreams.model");
        // интервал коммита offset'ов
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        // режим обработки — "точно один раз" (exactly once)
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        // смещение при старте — с начала
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // таймаут сессии
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        // интервал пинга сервера (heartbeat)
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
    
        return new KafkaStreamsConfiguration(props);
    }

    // Создание стрима для обработки сообщений
    @Bean
    public KStream<String, Message> messageProcessorStream(StreamsBuilder builder) {
        // Создаем поток из входного топика
        KStream<String, Message> messagesStream = builder.stream(messagesTopic);

        // Фильтрация сообщений по блокировкам и цензура
        KStream<String, Message> filteredStream = messagesStream
            .filter((key, message) -> {
                try {
                    // Проверка, заблокирован ли отправитель для получателя
                    boolean isBlocked = blockedUserService.isUserBlocked(message.getReceiverId(), message.getUserId());
                    log.info("Filtering message: key={}, message={}, isBlocked={}", key, message, isBlocked);
                    // Передача только тех сообщений, которые не заблокированы
                    return !isBlocked;
                } catch (Exception e) {
                    log.error("Error while filtering message: key={}, message={}: {}", key, message, e.getMessage(), e);
                    return false; // В случае ошибки — сообщение не пропускается
                }
            })
            .mapValues((key, message) -> {
                try {
                    // Цензура текста сообщения
                    String censoredMessage = censorshipService.censorMessage(message.getMessage());

                    log.info("Censoring message: key={}, message={}, censoredMessage={}", key, message, censoredMessage);
                    // Возврат нового объекта Message с цензурированным текстом
                    return new Message(message.getUserId(), message.getReceiverId(), censoredMessage, message.getTimestamp());
                } catch (Exception e) {
                    log.error("Error while censoring message: key={}, message={}: {}", key, message, e.getMessage(), e);
                    return message; // В случае ошибки — оставить оригинальное сообщение
                }
            });

        // Отправка обработанных сообщений в выходной топик
        filteredStream.to(filteredMessagesTopic);
        return filteredStream; // Возврат потока обработки
    }
}

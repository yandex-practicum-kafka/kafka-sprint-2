package com.example.kafkastreams.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaStreamsConfig {

    // Адрес Kafka брокера, подставляется из настроек приложения
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    // Здесь можно добавить дополнительную конфигурацию для Kafka Streams,
    // например, построение Topology, настройку Properties и создание Beans для потоковой обработки.
}

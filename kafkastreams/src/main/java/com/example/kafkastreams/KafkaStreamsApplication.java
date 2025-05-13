package com.example.kafkastreams;

import com.example.kafkastreams.config.KafkaStreamsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan;

/**
 * Главный класс приложения Spring Boot для запуска Kafka Streams.
 */

 @SpringBootApplication
// @Import(KafkaStreamsConfig.class)
// @ComponentScan(basePackages = {"com.example.kafkastreams"})
public class KafkaStreamsApplication {

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsApplication.class, args);
    }
}

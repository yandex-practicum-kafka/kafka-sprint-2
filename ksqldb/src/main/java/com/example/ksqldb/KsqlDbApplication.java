package com.example.ksqldb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Главный класс приложения Spring Boot, запускающий источник данных и все компоненты.
 * Включает поддержку планирования задач с помощью @EnableScheduling.
 */
@EnableScheduling
@SpringBootApplication
public class KsqlDbApplication {

    /**
     * Точка входа в приложение.
     * Запускает Spring Boot со всеми настроенными компонентами.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(KsqlDbApplication.class, args);
    }
}

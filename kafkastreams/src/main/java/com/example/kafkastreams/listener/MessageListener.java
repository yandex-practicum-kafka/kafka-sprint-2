package com.example.kafkastreams.listener;

import com.example.kafkastreams.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Slf4j
@Component
@Profile("processor")
public class MessageListener {

    /**
     * Метод прослушивает топик с отфильтрованными сообщениями.
     * Получает сообщения после фильтрации и обработки.
     *
     * @param message сообщение типа Message
     */
    @KafkaListener(topics = "${kafka.topic.filtered-messages}", groupId = "filtered-group")
    public void listenFilteredMessages(Message message) {
        // Вывод полученного сообщения в консоль
        log.info("Received message after filtering: {}", message);
    }

    /**
     * Метод прослушивает топик с всеми исходными сообщениями.
     * 
     * Можно раскомментировать для получения всех сообщений без фильтрации.
     *
     * @param message сообщение типа Message
     */
    // @KafkaListener(topics = "${kafka.topic.messages}", groupId = "all-messages-group")
    // public void listenAllMessages(Message message) {
    //     log.info("Received message without filtering: {}", message);
    // }
}

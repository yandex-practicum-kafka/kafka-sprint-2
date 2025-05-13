package com.example.kafkastreams.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    // ID пользователя, который отправил сообщение
    private int userId;

    // ID пользователя-получателя сообщения
    private int receiverId;

    // Текст сообщения
    private String message;

    /**
     * Временная метка создания сообщения.
     * Форматируется в JSON согласно шаблону ISO с миллисекундами и часовым поясом UTC.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Instant timestamp;

    /**
     * Конструктор с необходимыми полями без указания временной метки.
     * В этом случае timestamp устанавливается в текущее время.
     *
     * @param userId      ID отправителя
     * @param receiverId  ID получателя
     * @param message     текст сообщения
     */
    public Message(int userId, int receiverId, String message) {
        this.userId = userId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = Instant.now();
    }
}

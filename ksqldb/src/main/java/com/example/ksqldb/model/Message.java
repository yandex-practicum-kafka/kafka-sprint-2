package com.example.ksqldb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Класс Message представляет сообщение, отправляемое от пользователя к получателю.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    
    /**
     * ID пользователя-отправителя.
     */
    private String userId;
    
    /**
     * ID получателя сообщения.
     */
    private String recipientId;
    
    /**
     * Текст сообщения.
     */
    private String message;

    /**
     * Временная метка сообщения, указывающая, когда оно было отправлено.
     * Формат: yyyy-MM-dd'T'HH:mm:ss.SSSZ, часовой пояс - UTC.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Instant timestamp;

    /**
     * Конструктор для создания сообщения с ID отправителя, ID получателя и текстом сообщения.
     * Временная метка устанавливается на текущее время при создании сообщения.
     *
     * @param userId ID пользователя-отправителя
     * @param recipientId ID получателя сообщения
     * @param message текст сообщения
     */
    public Message(String userId, String recipientId, String message) {
        this.userId = userId;
        this.recipientId = recipientId;
        this.message = message;
        this.timestamp = Instant.now(); // Установка временной метки на текущее время
    }
}

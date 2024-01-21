package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс ApiError представляет собой объект ошибки API.
 * Свойства:
 * - errors: список объектов Error - список ошибок
 * - message: строка - сообщение об ошибке
 * - reason: строка - причина ошибки
 * - status: строка - статус ошибки
 * - timestamp: LocalDateTime - дата и время возникновения ошибки (автоматически устанавливается при создании объекта)
 */
@Getter
@Builder
public class ApiError {

    private final List<Error> errors;
    private final String message;
    private final String reason;
    private final String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
}
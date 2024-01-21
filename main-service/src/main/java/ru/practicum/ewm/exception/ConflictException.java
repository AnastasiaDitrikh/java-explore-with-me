package ru.practicum.ewm.exception;

/**
 * Исключение ConflictException является подклассом RuntimeException и используется для обработки ошибки конфликта.
 */
public class ConflictException extends RuntimeException {

    /**
     * Создает новый объект исключения ConflictException с заданным сообщением об ошибке.
     *
     * @param message сообщение об ошибке.
     */
    public ConflictException(final String message) {
        super(message);
    }
}
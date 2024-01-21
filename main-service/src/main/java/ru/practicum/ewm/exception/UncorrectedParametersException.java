package ru.practicum.ewm.exception;

/**
 * Исключение UncorrectedParametersException является подклассом RuntimeException
 * и используется для обработки ошибки "Некорректные параметры".
 */
public class UncorrectedParametersException extends RuntimeException {

    /**
     * Создает новый объект исключения UncorrectedParametersException с заданным сообщением об ошибке.
     *
     * @param message сообщение об ошибке.
     */
    public UncorrectedParametersException(String message) {
        super(message);
    }
}
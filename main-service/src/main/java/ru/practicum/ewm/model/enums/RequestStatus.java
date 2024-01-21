package ru.practicum.ewm.model.enums;

/**
 * Перечисление RequestStatus представляет статусы запроса на участие в событии.
 * Значения:
 * - PENDING: Запрос на участие ожидает подтверждения.
 * - CONFIRMED: Запрос на участие подтвержден.
 * - REJECTED: Запрос на участие отклонен.
 * - CANCELED: Запрос на участие отменен.
 */
public enum RequestStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELED
}
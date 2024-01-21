package ru.practicum.ewm.model.enums;

/**
 * Перечисление EventStatus представляет статусы события.
 * Значения:
 * - PENDING: Событие ожидает подтверждения.
 * - PUBLISHED: Событие опубликовано.
 * - CANCELED: Событие отменено.
 */
public enum EventStatus {
    PENDING,
    PUBLISHED,
    CANCELED
}
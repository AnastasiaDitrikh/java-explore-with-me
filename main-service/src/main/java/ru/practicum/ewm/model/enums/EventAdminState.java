package ru.practicum.ewm.model.enums;

import java.util.Optional;

/**
 * Перечисление EventAdminState представляет состояния администрирования события.
 * Значения:
 * - PUBLISH_EVENT: Событие опубликовано.
 * - REJECT_EVENT: Событие отклонено.
 */
public enum EventAdminState {
    PUBLISH_EVENT,
    REJECT_EVENT;

    /**
     * Преобразует строковое представление состояния в соответствующий объект EventAdminState.
     *
     * @param stringState строковое представление состояния
     * @return объект EventAdminState, соответствующий строковому представлению
     */
    public static Optional<EventAdminState> from(String stringState) {
        for (EventAdminState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
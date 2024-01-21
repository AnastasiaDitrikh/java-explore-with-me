package ru.practicum.ewm.model.enums;

import java.util.Optional;

/**
 * Перечисление EventUserState представляет состояния пользователя для события.
 * Значения:
 * - SEND_TO_REVIEW: Запрос на участие отправлен на рассмотрение.
 * - CANCEL_REVIEW: Запрос на участие отменен.
 */
public enum EventUserState {
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    /**
     * Преобразует строковое представление состояния в соответствующий объект EventUserState.
     *
     * @param stringState строковое представление состояния
     * @return объект EventUserState, соответствующий строковому представлению
     */
    public static Optional<EventUserState> from(String stringState) {
        for (EventUserState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
package ru.practicum.ewm.model.enums;

import java.util.Optional;

public enum EventUserState {
    SEND_TO_REVIEW, CANCEL_REVIEW;

    public static Optional<EventUserState> from(String stringState) {
        for (EventUserState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
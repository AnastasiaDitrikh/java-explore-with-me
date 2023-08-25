package ru.practicum.ewm.model.enums;

import java.util.Optional;

public enum EventAdminState {
    PUBLISH_EVENT, REJECT_EVENT;

    public static Optional<EventAdminState> from(String stringState) {
        for (EventAdminState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
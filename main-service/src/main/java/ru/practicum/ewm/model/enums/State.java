package ru.practicum.ewm.model.enums;

import java.util.Optional;

public enum State {
    SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
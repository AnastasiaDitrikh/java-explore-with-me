package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addNewRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.util.List;

/**
 * Интерфейс RequestService, определяющий методы для работы с запросами
 */
public interface RequestService {
    ParticipationRequestDto addNewRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
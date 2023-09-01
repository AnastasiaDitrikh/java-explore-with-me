package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getId())
                .status(request.getStatus())
                .build();
    }

    public Request toRequest(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                .event(null)
                .created(participationRequestDto.getCreated())
                .requester(null)
                .status(participationRequestDto.getStatus())
                .build();
    }
}


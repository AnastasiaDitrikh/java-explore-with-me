package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

/**
 * Класс RequestMapper предоставляет методы для преобразования объектов класса Request и ParticipationRequestDto друг в друга.
 */
@UtilityClass
public class RequestMapper {

    /**
     * Преобразует объект класса Request в объект класса ParticipationRequestDto.
     *
     * @param request объект класса Request, который требуется преобразовать
     * @return объект класса ParticipationRequestDto, созданный в результате преобразования
     */
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getId())
                .status(request.getStatus())
                .build();
    }

    /**
     * Преобразует объект класса ParticipationRequestDto в объект класса Request.
     *
     * @param participationRequestDto объект класса ParticipationRequestDto, который требуется преобразовать
     * @return объект класса Request, созданный в результате преобразования
     */
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
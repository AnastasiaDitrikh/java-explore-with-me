package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    /**
     * Обрабатывает GET запрос на получение списка событий, принадлежащих пользователю.
     *
     * @param userId - ID пользователя
     * @param from   - начальный индекс для пагинации (по умолчанию 0)
     * @param size   - количество записей на страницу для пагинации (по умолчанию 10)
     * @return список объектов EventShortDto с информацией о событиях пользователя
     */
    @GetMapping
    public List<EventShortDto> getAllEventsByUserId(@PathVariable(value = "userId") @Min(1) Long userId,
                                                    @RequestParam(value = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10")
                                                    @Positive Integer size) {
        log.info("GET запрос на получения событий пользователя с id= {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    /**
     * Обрабатывает POST запрос на добавление нового события от пользователя.
     *
     * @param userId - ID пользователя
     * @param input  - объект данных нового события
     * @return объект EventFullDto с информацией о созданном событии
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable(value = "userId") @Min(1) Long userId,
                                 @RequestBody @Valid NewEventDto input) {
        log.info("POST запрос на создание события от пользователя с id= {}", userId);
        return eventService.addNewEvent(userId, input);
    }

    /**
     * Получает полную информацию о событии, принадлежащем определенному пользователю.
     *
     * @param userId  идентификатор пользователя (минимальное значение: 1)
     * @param eventId идентификатор события (минимальное значение: 1)
     * @return объект EventFullDto с полной информацией о событии
     */
    @GetMapping("/{eventId}")
    public EventFullDto getFullEventByOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                            @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.info("GET запрос на получения полной информации о событии для пользователя с id= {}", userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    /**
     * Обновляет информацию о событии, принадлежащем определенному пользователю.
     *
     * @param userId      идентификатор пользователя (минимальное значение: 0)
     * @param eventId     идентификатор события (минимальное значение: 0)
     * @param inputUpdate объект UpdateEventUserRequest с данными для обновления события
     * @return объект EventFullDto с обновленной информацией о событии
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable(value = "userId") @Min(0) Long userId,
                                           @PathVariable(value = "eventId") @Min(0) Long eventId,
                                           @RequestBody @Valid UpdateEventUserRequest inputUpdate) {
        log.info("PATCH запрос на обновление события от пользователя с id= {}", userId);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, inputUpdate);
    }

    /**
     * Получает список всех запросов на участие в событии, принадлежащем определенному пользователю.
     *
     * @param userId  идентификатор пользователя (минимальное значение: 1)
     * @param eventId идентификатор события (минимальное значение: 1)
     * @return список объектов ParticipationRequestDto с информацией о запросах на участие в событии
     */
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId) {
        log.info("GET запрос на получение информации о всех запросах об участии в событии для пользователя с id= {}", userId);
        return eventService.getAllParticipationRequestsFromEventByOwner(userId, eventId);
    }

    /**
     * Обновляет статус запроса на участие в событии, принадлежащем определенному пользователю.
     *
     * @param userId      идентификатор пользователя (минимальное значение: 1)
     * @param eventId     идентификатор события (минимальное значение: 1)
     * @param inputUpdate объект EventRequestStatusUpdateRequest с данными для обновления статуса запроса
     * @return объект EventRequestStatusUpdateResult с результатом обновления статуса запроса
     */
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestFromOwner(@PathVariable(value = "userId") @Min(1) Long userId,
                                                                       @PathVariable(value = "eventId") @Min(1) Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest inputUpdate) {
        log.info("PATCH запрос на обновление статуса события от пользователя с id= {}", userId);
        return eventService.updateStatusRequest(userId, eventId, inputUpdate);
    }
}
package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService requestService;

    /**
     * Создает запрос на участие в событии от пользователя.
     *
     * @param userId  идентификатор пользователя (минимальное значение: 0)
     * @param eventId идентификатор события (минимальное значение: 0)
     * @return объект ParticipationRequestDto с информацией о созданном запросе
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                              @RequestParam(name = "eventId") @Min(0) Long eventId) {
        log.info("POST запрос на создание запроса на участие в событии с id= {}  пользователя с id= {}",
                eventId, userId);
        return requestService.addNewRequest(userId, eventId);
    }

    /**
     * Получает список всех запросов на участие в событиях пользователя.
     *
     * @param userId идентификатор пользователя (минимальное значение: 0)
     * @return список объектов ParticipationRequestDto с информацией о запросах на участие в событиях
     */
    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        log.info("GET запрос на получение всех запросов на участие в событиях пользователя с id= {}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    /**
     * Отменяет запрос на участие в событии, сделанный пользователем.
     *
     * @param userId    идентификатор пользователя (минимальное значение: 0)
     * @param requestId идентификатор запроса на участие (минимальное значение: 0)
     * @return объект ParticipationRequestDto с информацией об отмененном запросе
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                   @PathVariable(value = "requestId") @Min(0) Long requestId) {
        log.info("PATCH запрос на отмену запроса пользователем с id= {}", userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
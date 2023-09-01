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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                              @RequestParam(name = "eventId") @Min(0) Long eventId) {
        log.info("POST запрос на создание запроса на участие в событии с id= {}  пользователя с id= {}",
                eventId, userId);
        return requestService.addNewRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        log.info("GET запрос на получение всех запросов на участие в событиях пользователя с id= {}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                   @PathVariable(value = "requestId") @Min(0) Long requestId) {
        log.info("PATCH запрос на отмену запроса пользователем с id= {}", userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
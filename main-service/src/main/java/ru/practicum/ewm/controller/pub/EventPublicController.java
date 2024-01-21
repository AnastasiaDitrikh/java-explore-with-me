package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.SearchEventParams;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    /**
     * Получает список всех событий с применением фильтра.
     *
     * @param searchEventParams параметры фильтрации для поиска событий
     * @param request           объект HttpServletRequest
     * @return список объектов EventShortDto с информацией о событиях
     */
    @GetMapping
    public List<EventShortDto> getAllEvents(@Valid SearchEventParams searchEventParams,
                                            HttpServletRequest request) {
        log.info("GET запрос на получения событий с фильтром");
        return eventService.getAllEventFromPublic(searchEventParams, request);
    }

    /**
     * Получает полную информацию о событии по его идентификатору.
     *
     * @param eventId идентификатор события (минимальное значение: 1)
     * @param request объект HttpServletRequest
     * @return объект EventFullDto с полной информацией о событии
     */
    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                     HttpServletRequest request) {
        log.info("GET запрос на получения полной информации о событии с  id= {}", eventId);
        return eventService.getEventById(eventId, request);
    }
}
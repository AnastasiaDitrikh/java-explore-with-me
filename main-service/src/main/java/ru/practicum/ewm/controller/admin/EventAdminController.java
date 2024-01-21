package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.SearchEventParamsAdmin;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventAdminController {

    private final EventService eventService;

    /**
     * Обрабатывает GET запрос на поиск событий с использованием параметров поиска.
     *
     * @param searchEventParamsAdmin - объект параметров поиска событий
     * @return список объектов EventFullDto с информацией о найденных событиях
     */
    @GetMapping
    public List<EventFullDto> searchEvents(@Valid SearchEventParamsAdmin searchEventParamsAdmin) {
        log.info("GET запрос на получение списка событий");
        return eventService.getAllEventFromAdmin(searchEventParamsAdmin);
    }

    /**
     * Обрабатывает PATCH запрос на обновление информации о событии по его ID.
     *
     * @param eventId - ID события, которое нужно обновить
     * @param inputUpdate - объект данных для обновления информации о событии
     * @return объект EventFullDto с обновленной информацией о событии
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest inputUpdate) {
        log.info("PATCH запрос на обновление списка событий");
        return eventService.updateEventFromAdmin(eventId, inputUpdate);
    }
}
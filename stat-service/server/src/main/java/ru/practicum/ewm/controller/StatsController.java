package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewsStatsRequest;
import ru.practicum.ewm.service.StatsService;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для обработки запросов связанных со статистикой.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatsController {

    private final StatsService service;

    /**
     * Обработка POST-запроса на сохранение информации о хите на эндпоинт.
     *
     * @param hit объект EndpointHit, содержащий информацию о хите
     */
    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void hit(@RequestBody EndpointHit hit) {
        log.info("POST request to save information.");
        service.saveHit(hit);
    }

    /**
     * Обработка GET-запроса на получение статистики просмотров.
     *
     * @param start  начальная дата и время периода статистики
     * @param end    конечная дата и время периода статистики
     * @param uris   список URI для фильтрации статистики
     * @param unique флаг, указывающий нужно ли получить уникальные просмотры
     * @return список объектов ViewStats, содержащих статистику просмотров
     * @throws InvalidParameterException если переданы некорректные параметры запроса
     */
    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(defaultValue = "") List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET request to get all statistic.");
        if (end.isBefore(start)) {
            log.info("Uncorrected format of dates start {} и end {}", start, end);
            throw new InvalidParameterException("Uncorrected format of dates");
        }
        return service.getViewStatsList(
                ViewsStatsRequest.builder()
                        .start(start)
                        .end(end)
                        .uris(uris)
                        .unique(unique)
                        .build());
    }
}
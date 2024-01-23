package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewsStatsRequest;
import ru.practicum.ewm.repository.StatsRepository;

import java.util.List;

/**
 * Реализация интерфейса StatsService.
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statRepository;

    /**
     * Сохранение информации о попадании на эндпоинт
     *
     * @param hit объект EndpointHit, содержащий информацию о попадании на эндпоинт
     */
    @Override
    public void saveHit(EndpointHit hit) {
        statRepository.saveHit(hit);
    }

    /**
     * Получение статистики просмотров.
     *
     * @param request объект ViewsStatsRequest, содержащий информацию для запроса статистики
     * @return список объектов ViewStats, содержащих статистику просмотров
     */
    @Override
    public List<ViewStats> getViewStatsList(ViewsStatsRequest request) {
        if (request.isUnique()) {
            return statRepository.getUniqueStats(request);
        }
        return statRepository.getStats(request);
    }
}
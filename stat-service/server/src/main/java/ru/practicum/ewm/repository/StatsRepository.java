package ru.practicum.ewm.repository;

import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewsStatsRequest;

import java.util.List;

/**
 * Интерфейс для работы с репозиторием статистики.
 */
public interface StatsRepository {
    void saveHit(EndpointHit hit);

    List<ViewStats> getStats(ViewsStatsRequest request);

    List<ViewStats> getUniqueStats(ViewsStatsRequest request);
}
package ru.practicum.ewm.repository;

import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewsStatsRequest;

import java.util.List;

public interface StatsRepository {
    void saveHit(EndpointHit hit);

    List<ViewStats> getStats(ViewsStatsRequest request);

    List<ViewStats> getUniqueStats(ViewsStatsRequest request);
}
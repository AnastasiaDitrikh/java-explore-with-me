package ru.practicum.ewm.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.ViewsStatsRequest;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    public void saveHit(EndpointHit hit) {
        jdbcTemplate.update("INSERT INTO stats (app, uri, ip, created) VALUES (?, ?, ?, ?)",
                hit.getApp(), hit.getUri(), hit.getIp(), Timestamp.valueOf(hit.getTimestamp()));
    }

    @Override
    public List<ViewStats> getStats(ViewsStatsRequest request) {
        String query = "SELECT app, uri, COUNT (ip) AS hits FROM stats WHERE (created >= ? AND created <= ?) ";
        if (!request.getUris().isEmpty()) {
            query += createUrisQuery(request.getUris());
        }
        query += " GROUP BY app, uri ORDER BY hits DESC";
        return jdbcTemplate.query(query, viewStatsMapper, request.getStart(), request.getEnd());
    }

    @Override
    public List<ViewStats> getUniqueStats(ViewsStatsRequest request) {
        String query = "SELECT app, uri, COUNT (DISTINCT ip) AS hits FROM stats WHERE (created >= ? AND created <= ?) ";
        if (!request.getUris().isEmpty()) {
            query += createUrisQuery(request.getUris());
        }
        query += " GROUP BY app, uri ORDER BY hits DESC";
        return jdbcTemplate.query(query, viewStatsMapper, request.getStart(), request.getEnd());
    }


    private String createUrisQuery(List<String> uris) {
        StringBuilder result = new StringBuilder("AND uri IN ('");
        result.append(String.join("', '", uris));
        return result.append("') ").toString();
    }
}
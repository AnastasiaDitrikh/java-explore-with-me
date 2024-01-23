package ru.practicum.ewm.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.ViewStats;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования результата запроса к таблице статистики в объект ViewStats.
 */
@Component
public class ViewStatsMapper implements RowMapper<ViewStats> {

    /**
     * Метод для маппинга строки результата запроса в объект ViewStats.
     *
     * @param rs объект ResultSet с результатами запроса
     * @param rowNum номер строки результата запроса
     * @return объект ViewStats
     * @throws SQLException если произошла ошибка при доступе к данным в ResultSet
     */
    @Override
    public ViewStats mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ViewStats.builder()
                .app(rs.getString("app"))
                .uri(rs.getString("uri"))
                .hits(rs.getLong("hits"))
                .build();
    }
}
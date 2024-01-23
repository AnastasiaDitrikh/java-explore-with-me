package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Клиент для взаимодействия с сервисом статистики.
 */
@Service
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    /**
     * Отправка информации о попадании на эндпоинт в статистику.
     *
     * @param endpointHitDto объект EndpointHit, содержащий информацию о попадании на эндпоинт
     */
    public void postStats(EndpointHit endpointHitDto) {
        post(endpointHitDto);
    }

    /**
     * Получение статистики просмотров за определенный период.
     *
     * @param start начальная дата и время периода статистики
     * @param end конечная дата и время периода статистики
     * @param uris список URI эндпоинтов, для которых нужно получить статистику
     * @param unique флаг, указывающий нужно ли получить уникальные просмотры
     * @return объект ResponseEntity<Object>, содержащий статистику просмотров
     */
    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris,
                                           Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", String.join(",", uris),
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
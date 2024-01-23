package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Базовый клиент для взаимодействия с внешними сервисами посредством REST-запросов.
 */
@Slf4j
public class BaseClient {
    protected final RestTemplate restTemplate;

    public BaseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Выполнение GET-запроса.
     *
     * @param path       путь запроса
     * @param parameters параметры запроса
     * @return объект ResponseEntity<Object> с ответом на GET-запрос
     */
    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    /**
     * Выполнение POST-запроса.
     *
     * @param body тело запроса
     * @param <T>  тип тела запроса
     */
    protected <T> void post(T body) {
        makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    /**
     * Выполнение HTTP-запроса.
     *
     * @param method     HTTP-метод (GET, POST и т.д.)
     * @param path       путь запроса
     * @param parameters параметры запроса
     * @param body       тело запроса
     * @param <T>        тип тела запроса
     * @return объект ResponseEntity<Object> с ответом на запрос
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> responseEntity;
        try {
            if (parameters != null) {
                responseEntity = restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                responseEntity = restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareResponse(responseEntity);
    }

    /**
     * Возвращает HttpHeaders со значениями по умолчанию.
     *
     * @return объект HttpHeaders со значениями по умолчанию
     */
    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * Подготавливает и возвращает ответ на HTTP-запрос.
     *
     * @param response объект ResponseEntity<Object> с ответом на HTTP-запрос
     * @return объект ResponseEntity<Object> с ответом на запрос
     */
    private static ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
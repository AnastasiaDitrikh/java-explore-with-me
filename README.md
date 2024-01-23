# Explore with me - микросервисное приложение-афиша мероприятий

Java, Spring (Boot, Data), PostgreSQL, REST API, Docker, Mockito, JUnit, Postman, Lombok

## О проекте

Приложение для размещения мероприятий и поиска компаний для участия в них

<p align="center">
  <img src="https://pictures.s3.yandex.net/resources/S19_09-2_1674558748.png" />
</p>

Приложение содержит два микросервиса:

- main-service для бизнес-логики
- stat-service для сбора статистики просмотра событий по ip, который состоит из трех модулей (модуль общих DTO, модуль
  сервиса статистики, модуль клиента сервиса статистики)

У каждого микросервиса есть своя база данных.  
Микросервисы и базы данных запускаются в собственных Docker контейнерах (4 шт).

## Основная функциональность:

### Неавторизованные пользователи

- просматривать все события, в том числе по категориям
- видеть детали отдельных событий
- видеть закрепленные подборки событий
- оставлять комментарии

### Авторизованные пользователи

- добавление в приложение новые мероприятия, редактировать их и просматривать после добавления
- подача заявок на участие в интересующих мероприятиях
- создатель мероприятия может подтверждать заявки, которые отправили другие пользователи сервиса

### Администраторы

- добавление, изменение и удаление категорий для событий
- добавление, удаление и закрепление на главной странице подборки мероприятий
- модерация событий, размещённых пользователями, — публикация или отклонение

## Эндпоинты

### main-service

- POST /users/{userId}/events - добавить новое событие
- GET /users/{userId}/events/{eventId} - получить событие
- PATCH /users/{userId}/events/{eventId} - изменить событие
- GET /users/{userId}/events - получить события пользователя
- GET /users/{userId}/events/{eventId}/requests - получить запросы пользователя на участие в событии
- PATCH /users/{userId}/events/{eventId}/requests - изменить статус (подтверждение, отмена) заявок на участие
  пользователя в событии
  <br>

- GET /categories - получить все категории
- GET /categories/{catId} - получить категорию
  <br>

- GET /compilations - получить все подборки событий
- GET /compilations/{compId} - получить подборку событий
  <br>

- GET /admin/events - получить события по любым параметрам:
    - users - список id пользователей
    - states - список статусов события (PENDING, PUBLISHED, CANCELED)
    - categories - список id категорий событий
    - rangeStart - начало временного отрезка в формате yyyy-MM-dd HH:mm:ss
    - rangeEnd - конец временного отрезка в формате yyyy-MM-dd HH:mm:ss
    - from - параметр для пагинации
    - size - параметр для пагинации
- PATCH /admin/events/{eventId} - изменить событие
  <br>

- GET /events - получить события по любым параметрам:
    - text - текст для поиска в названии и описании событий
    - categories - список id категорий событий
    - paid - только платные события (true/false)
    - rangeStart - начало временного отрезка в формате yyyy-MM-dd HH:mm:ss
    - rangeEnd - конец временного отрезка в формате yyyy-MM-dd HH:mm:ss
    - onlyAvailable - только доступные события, т.е. у которых еще не исчерпан лимит участников (true/false)
    - sort - способ сортировки событий (EVENT_DATE, VIEWS)
    - from - параметр для пагинации
    - size - параметр для пагинации
- GET /events/{id} - получить событие
  <br>

- GET /comments/{eventId} - Получает список всех комментариев события по его идентификатору со страницами
- POST /comments/users/{userId}/events/{eventId} - запрос на добавление нового комментария от пользователя к событию
- PATCH /comments/users/{userId}/{commentId} - запрос на обновление комментария пользователем по его ID
- GET /comments/users/{userId}/comments - запрос на получение списка комментариев, созданных пользователем
- DELETE /comments/users/{userId}/{commentId} - запрос на удаление комментария пользователем по его ID
- GET /comments/users//{userId}/{commentId} - запрос на получение комментария пользователя по его ID
  <br>

- POST /users/{userId}/requests - добавить запрос на участие в событии
- GET /users/{userId}/requests - получить запросы пользователя на участие в событиях
- DELETE /users/{userId}/requests/{requestId}/cancel - отменить запрос на участие в событии
  <br>

- POST /users/{userId}/events/{eventId}/comments - добавить комментарий к событию
- PATCH /users/{userId}/events/{eventId}/comments/{commentId} - обновить комментарий
- GET /users/{userId}/events/{eventId}/comments/{commentId} - получить комментарий к событию
- DELETE /users/{userId}/events/{eventId}/comments/{commentId} - удалить комментарий к событию
- GET /users/{userId}/events/{eventId}/comments - получить список комментариев пользователя к событию
- GET /users/{userId}/comments - получить все комментарии пользователя
  <br>

- POST /admin/users - добавить пользователя
- GET /admin/users - получить всех пользователей
- DELETE /admin/users/{userId} - удалить пользователя
- POST /admin/compilations - добавить подборку событий
- DELETE /admin/compilations/{compId} - удалить подборку событий
- PATCH /admin/compilations/{compId} - обновить подборку событий
- POST /admin/categories - добавить новую категорию
- GET /admin/categories/{catId} - получить категорию событий
- DELETE /admin/categories/{catId} - удалить категорию
- GET /admin/comments/{commentId} - получить комментария по идентификатору
- GET /admin/comments/search - поиск комментариев по тексту

### stats-service

- GET /stats - Получение статистики по посещениям
- POST /hit - Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем
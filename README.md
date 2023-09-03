# java-explore-with-me
Разработано приложение ExploreWithMe (англ. «исследуй со мной»). Оно позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них. 
Это приложение — афиша. В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.
Реализована микросервисная архитектура при помощи docker/
Приложение включает в себя два сервиса:
    - основной сервис будет содержать всё необходимое для работы продукта;
    - сервис статистики будет хранить количество просмотров и позволит делать различные выборки для анализа работы приложения.
Спецификация API для обоих сервисов представлена по ссылкам:
    спецификация основного сервиса: https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json
    спецификация сервиса статистики:https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json
Хранение осуществляется в БД.

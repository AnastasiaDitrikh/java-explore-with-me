package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Location;

/**
 * Интерфейс LocationRepository, представляющий репозиторий для работы с сущностью Location.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface LocationRepository extends JpaRepository<Location, Long> {
}
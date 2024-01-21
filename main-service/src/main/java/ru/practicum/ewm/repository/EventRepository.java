package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс EventRepository, представляющий репозиторий для работы с сущностью Event.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> findByCategory(Category category);

    List<Event> findAllByIdIn(List<Long> ids);
}
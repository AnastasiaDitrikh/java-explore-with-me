package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс RequestRepository, представляющий репозиторий для работы с сущностью Request.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);


    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<List<Request>> findByEventIdAndIdIn(Long eventId, List<Long> id);
}
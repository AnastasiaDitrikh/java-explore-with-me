package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.User;

import java.util.List;

/**
 * Интерфейс UserRepository, представляющий репозиторий для работы с сущностью User.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(List<Long> ids, Pageable pageable);
}
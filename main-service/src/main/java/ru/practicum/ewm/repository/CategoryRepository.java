package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Category;

/**
 * Интерфейс CategoryRepository, представляющий репозиторий для работы с сущностью Category.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByNameIgnoreCase(String name);
}
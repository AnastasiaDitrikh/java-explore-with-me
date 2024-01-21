package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Compilation;

import java.util.List;
/**
 * Интерфейс CompilationRepository, представляющий репозиторий для работы с сущностью Compilation.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
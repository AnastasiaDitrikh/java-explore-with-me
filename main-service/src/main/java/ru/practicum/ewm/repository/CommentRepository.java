package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.comment.CountCommentsByEventDto;
import ru.practicum.ewm.model.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс CommentRepository, представляющий репозиторий для работы с сущностью Comment.
 * Расширяет JpaRepository для наследования базовых методов работы с базой данных.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent_Id(Long eventId, Pageable pageable);

    List<Comment> findByAuthor_Id(Long userId);

    Optional<Comment> findByAuthor_IdAndId(Long userId, Long id);

    @Query("select new ru.practicum.ewm.dto.comment.CountCommentsByEventDto(c.event.id, COUNT(c)) " +
            "from comments as c where c.event.id in ?1 " +
            "GROUP BY c.event.id")
    List<CountCommentsByEventDto> countCommentByEvent(List<Long> eventIds);

    @Query("select c " +
            "from comments as c " +
            "where lower(c.text) like lower(concat('%', ?1, '%') )")
    List<Comment> search(String text, Pageable pageable);
}
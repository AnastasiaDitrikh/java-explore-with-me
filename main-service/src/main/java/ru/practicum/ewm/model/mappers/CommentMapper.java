package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

/**
 * Класс CommentMapper служит для преобразования объектов Comment и связанных с ними DTO.
 */
@UtilityClass
public class CommentMapper {

    /**
     * Преобразует объект Comment в объект CommentDto.
     *
     * @param comment объект Comment для преобразования
     * @return объект CommentDto, полученный в результате преобразования
     */
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .build();
    }

    /**
     * Преобразует объект NewCommentDto в объект Comment.
     *
     * @param commentDto объект NewCommentDto для преобразования
     * @param event объект Event связанный с комментарием
     * @param user объект User связанный с комментарием
     * @return объект Comment, полученный в результате преобразования
     */
    public Comment toComment(NewCommentDto commentDto, Event event, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .lastUpdatedOn(null)
                .build();
    }

    /**
     * Преобразует объект CommentDto в объект Comment.
     *
     * @param commentDto объект CommentDto для преобразования
     * @return объект Comment, полученный в результате преобразования
     */
    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}
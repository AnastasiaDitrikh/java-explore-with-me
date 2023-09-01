package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;


@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .build();
    }

    public Comment toComment(NewCommentDto commentDto, Event event, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .lastUpdatedOn(null)
                .build();
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}
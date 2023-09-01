package ru.practicum.ewm.service;


import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto patchByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentUser(Long userId);

    Comment getUserCommentByUserAndCommentId(Long userId, Long commentId);

    List<Comment> getCommentEvent(Long eventId, Integer from, Integer size);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<Comment> search(String text, Integer from, Integer size);
}
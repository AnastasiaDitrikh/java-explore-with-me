package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<Comment> getRequestListAllCommentsEvent(@PathVariable Long eventId) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId);
    }
}
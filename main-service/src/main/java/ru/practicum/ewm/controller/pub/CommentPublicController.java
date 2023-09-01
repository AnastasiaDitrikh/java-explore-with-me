package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<Comment> getRequestListAllCommentsEvent(@PathVariable Long eventId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId, from, size);
    }
}
package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.ReportService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/comments")
public class CommentPublicController {
    private final CommentService commentService;

    private final ReportService reportService;
    @GetMapping("/events /{eventId}")
    public List<CommentDto> getRequestListAllCommentsEvent(@PathVariable Long eventId) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId);
    }

    @PostMapping("/{commentId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReport(@PathVariable Long commentId) {
        log.info("Опубликована жалоба на комментарий с id = {} ", commentId);
        reportService.addReport(commentId);
    }
}
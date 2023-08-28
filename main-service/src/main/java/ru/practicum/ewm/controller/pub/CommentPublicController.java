package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.NewReportDto;
import ru.practicum.ewm.dto.comment.ReportDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.ReportService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/comments")
public class CommentPublicController {
    private final CommentService commentService;

    private final ReportService reportService;

    @GetMapping("/{eventId}")
    public List<Comment> getRequestListAllCommentsEvent(@PathVariable Long eventId) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId);
    }

    @PostMapping("/{commentId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public ReportDto addReport(@PathVariable Long commentId, @Valid @RequestBody NewReportDto reportDto) {
        log.info("Опубликована жалоба на комментарий с id = {} ", commentId);
        return reportService.addReport(commentId, reportDto);
    }
}
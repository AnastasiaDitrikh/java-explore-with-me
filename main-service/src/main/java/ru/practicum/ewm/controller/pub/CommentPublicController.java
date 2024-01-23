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

    /**
     * Получает список всех комментариев события по его идентификатору со страницами.
     *
     * @param eventId идентификатор события
     * @param from    начальная позиция списка (по умолчанию: 0, значение должно быть неотрицательным)
     * @param size    размер страницы (по умолчанию: 10, значение должно быть положительным)
     * @return список объектов Comment с информацией о комментариях
     */
    @GetMapping("/{eventId}")
    public List<Comment> getRequestListAllCommentsEvent(@PathVariable Long eventId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId, from, size);
    }
}
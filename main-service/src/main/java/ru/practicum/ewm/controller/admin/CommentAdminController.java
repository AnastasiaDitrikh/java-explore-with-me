package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    /**
     * Обрабатывает DELETE запрос на удаление комментария по его ID.
     *
     * @param commentId - ID комментария, который нужно удалить
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("DELETE запрос на удаление комментария id = {} ", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    /**
     * Обрабатывает GET запрос на поиск комментариев по тексту.
     *
     * @param text - текст для поиска комментариев
     * @param from - начальный индекс для пагинации (по умолчанию 0)
     * @param size - количество записей на страницу для пагинации (по умолчанию 10)
     * @return список объектов Comment с найденными комментариями
     */
    @GetMapping("/search")
    public List<Comment> searchComments(@RequestParam(name = "text") String text,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET Запрос на поиск комментариев c текстом = {}", text);
        return commentService.search(text, from, size);
    }
}
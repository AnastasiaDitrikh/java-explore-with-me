package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {

    private final CommentService commentService;

    /**
     * Обрабатывает POST запрос на добавление нового комментария от пользователя к событию.
     *
     * @param userId - ID пользователя
     * @param eventId - ID события, к которому добавляется комментарий
     * @param newCommentDto - объект данных нового комментария
     * @return объект CommentDto с информацией о созданном комментарии
     */
    @PostMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST запрос на добавление комментария: {}", newCommentDto);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    /**
     * Обрабатывает PATCH запрос на обновление комментария пользователем по его ID.
     *
     * @param userId - ID пользователя
     * @param commentId - ID комментария, который нужно обновить
     * @param updateCommentDto - объект данных для обновления комментария
     * @return объект CommentDto с обновленной информацией о комментарии
     */
    @PatchMapping("/users/{userId}/{commentId}")
    public CommentDto patchRequestByUser(@PathVariable Long userId, @PathVariable Long commentId,
                                         @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("PATCH запрос на обновление пользователем с userId = {}  комментария с commentId = {} ", userId, commentId);
        return commentService.patchByUser(userId, commentId, updateCommentDto);
    }

    /**
     * Обрабатывает GET запрос на получение списка комментариев, созданных пользователем.
     *
     * @param userId - ID пользователя
     * @return список объектов CommentDto с информацией о комментариях пользователя
     */
    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getRequestListUser(@PathVariable Long userId) {
        log.info("GET запрос на получение комментариев пользователя с userId = {} ", userId);
        return commentService.getCommentUser(userId);
    }

    /**
     * Обрабатывает DELETE запрос на удаление комментария пользователем по его ID.
     *
     * @param userId - ID пользователя
     * @param commentId - ID комментария, который нужно удалить
     */
    @DeleteMapping("/users/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("DELETE запрос на удаление комментария id = {} пользователем id = {} ", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }

    /**
     * Обрабатывает GET запрос на получение комментария пользователя по его ID.
     *
     * @param userId - ID пользователя
     * @param commentId - ID комментария
     * @return объект Comment с информацией о комментарии пользователя
     */
    @GetMapping("/users/{userId}/{commentId}")
    public Comment getComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("GET запрос на получения комментария id = {} пользователем id = {} ", commentId, userId);
        return commentService.getUserCommentByUserAndCommentId(userId, commentId);
    }
}
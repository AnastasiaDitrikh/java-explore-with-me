package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventStatus;
import ru.practicum.ewm.model.mappers.CommentMapper;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.CommentService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * Обновляет комментарий пользователя по заданным идентификаторам.
     *
     * @param userId           идентификатор пользователя
     * @param commentId        идентификатор комментария
     * @param updateCommentDto объект UpdateCommentDto с обновленными данными комментария
     * @return объект CommentDto обновленного комментария
     * @throws NotFoundException              если пользователь или комментарий не найдены
     * @throws UncorrectedParametersException если прошло более часа с момента создания комментария
     */
    @Override
    @Transactional
    public CommentDto patchByUser(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthorComment(user, comment);
        LocalDateTime updateTime = LocalDateTime.now();

        if (updateTime.isAfter(comment.getCreated().plusHours(1L))) {
            throw new UncorrectedParametersException("Сообщение возможно отредактировать только в течение часа");
        }

        comment.setText(updateCommentDto.getText());
        comment.setLastUpdatedOn(updateTime);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    /**
     * Возвращает список комментариев пользователя по заданному идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список объектов CommentDto комментариев пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentUser(Long userId) {
        checkUser(userId);
        List<Comment> commentList = commentRepository.findByAuthor_Id(userId);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    /**
     * Возвращает комментарий пользователя по заданным идентификаторам пользователя и комментария.
     *
     * @param userId    идентификатор пользователя
     * @param commentId идентификатор комментария
     * @return объект Comment комментария пользователя
     * @throws NotFoundException если пользователь или комментарий не найдены
     */
    @Override
    @Transactional(readOnly = true)
    public Comment getUserCommentByUserAndCommentId(Long userId, Long commentId) {
        checkUser(userId);
        return commentRepository.findByAuthor_IdAndId(userId, commentId).orElseThrow(() -> new NotFoundException(
                String.format("У пользователя c id=%d  не найден комментарий с id=%d", userId, commentId)));
    }

    /**
     * Возвращает список комментариев события по заданному идентификатору события.
     *
     * @param eventId идентификатор события
     * @param from    начальная позиция списка
     * @param size    размер списка
     * @return список объектов Comment комментариев события
     * @throws NotFoundException если событие не найдено
     */
    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentEvent(Long eventId, Integer from, Integer size) {
        Event event = checkEvent(eventId);
        PageRequest pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEvent_Id(eventId, pageable);

    }

    /**
     * Удаляет комментарий пользователя по заданным идентификаторам пользователя и комментария.
     *
     * @param userId    идентификатор пользователя
     * @param commentId идентификатор комментария
     * @throws NotFoundException если пользователь или комментарий не найдены
     */
    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthorComment(user, comment);
        commentRepository.deleteById(commentId);
    }

    /**
     * Удаляет комментарий администратором по заданному идентификатору комментария.
     *
     * @param commentId идентификатор комментария
     * @throws NotFoundException если комментарий не найден
     */
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    /**
     * Поиск комментариев по заданному тексту с пагинацией.
     *
     * @param text текст для поиска
     * @param from начальная позиция списка
     * @param size размер списка
     * @return список объектов Comment комментариев, соответствующих тексту
     */
    @Override
    @Transactional
    public List<Comment> search(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return commentRepository.search(text, pageable);
    }

    /**
     * Создает новый комментарий пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param eventId    идентификатор события
     * @param commentDto объект NewCommentDto, содержащий данные нового комментария
     * @return объект CommentDto нового комментария
     * @throws NotFoundException              если событие или пользователь не найдены
     * @throws UncorrectedParametersException если статус события не равен PUBLISHED
     */
    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto commentDto) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new UncorrectedParametersException("Невозможно добавить комментарий к событию со статусом не PUBLISHED");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, event, user)));
    }

    /**
     * Проверяет существование события по заданному идентификатору.
     *
     * @param id идентификатор события
     * @return объект Event события
     * @throws NotFoundException если событие не найдено
     */
    private Event checkEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Событие с id=%d  не найдено", id)));
    }

    /**
     * Проверяет существование пользователя по заданному идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект User пользователя
     * @throws NotFoundException если пользователь не найден
     */
    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Пользователь c id=%d  не найден", id)));
    }

    /**
     * Проверяет существование комментария по заданному идентификатору.
     *
     * @param id идентификатор комментария
     * @return объект Comment комментария
     * @throws NotFoundException если комментарий не найден
     */
    private Comment checkComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Комментарий c id=%d  не найден", id)));
    }

    /**
     * Проверяет, что пользователь является автором комментария.
     *
     * @param user    объект User пользователя
     * @param comment объект Comment комментария
     * @throws UncorrectedParametersException если пользователь не является автором комментария
     */
    private void checkAuthorComment(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            throw new UncorrectedParametersException("Пользователь не является автором комментария");
        }
    }
}
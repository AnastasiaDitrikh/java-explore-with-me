package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventStatus;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.model.mappers.RequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * Добавляет новый запрос на участие в событии.
     *
     * @param userId  идентификатор пользователя
     * @param eventId идентификатор события
     * @return объект ParticipationRequestDto нового запроса на участие
     * @throws NotFoundException если пользователь или событие не найдены
     * @throws ConflictException если пользователь является инициатором события,
     *                           превышен лимит участников события, событие не опубликовано или запрос уже существует
     */
    @Override
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) {
        User user = checkUser(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        LocalDateTime createdOn = LocalDateTime.now();
        validateNewRequest(event, userId, eventId);
        Request request = new Request();
        request.setCreated(createdOn);
        request.setRequester(user);
        request.setEvent(event);

        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        requestRepository.save(request);

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toParticipationRequestDto(request);
    }

    /**
     * Возвращает список запросов на участие пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список объектов ParticipationRequestDto запросов на участие
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        checkUser(userId);
        List<Request> result = requestRepository.findAllByRequesterId(userId);
        return result.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    /**
     * Отменяет запрос на участие пользователя.
     *
     * @param userId    идентификатор пользователя
     * @param requestId идентификатор запроса на участие
     * @return объект ParticipationRequestDto отмененного запроса на участие
     * @throws NotFoundException              если пользователь или запрос не найдены
     * @throws UncorrectedParametersException если запрос уже отменен или отклонен
     */
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException("Запрос с id= " + requestId + " не найден"));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new UncorrectedParametersException("Запрос не подтвержден");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request requestAfterSave = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(requestAfterSave);
    }

    /**
     * Проверяет существование пользователя по заданному идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return объект User пользователя
     * @throws NotFoundException если пользователь не найден
     */
    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Категории с id = " + userId + " не существует"));
    }

    /**
     * Проверяет валидность нового запроса на участие.
     *
     * @param event   объект Event события
     * @param userId  идентификатор пользователя
     * @param eventId идентификатор события
     * @throws ConflictException если пользователь является инициатором события,
     *                           превышен лимит участников события, событие не опубликовано или запрос уже существует
     */
    private void validateNewRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id= " + userId + " не инициатор события");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("Превышен лимит участников события");
        }
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Попытка добаления дубликата");
        }
    }
}
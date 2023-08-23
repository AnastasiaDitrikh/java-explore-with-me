package ru.practicum.ewm.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHit;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.EventStatus;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.mappers.EventMapper;
import ru.practicum.ewm.model.mappers.RequestMapper;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;


    @Value("${server.application.name:ewm-service}")
    private String applicationName;


    @Override
    public List<EventFullDto> getAllEventFromAdmin(SearchEventParamsAdmin searchEventParamsAdmin) {
        PageRequest pageable = PageRequest.of(searchEventParamsAdmin.getFrom() / searchEventParamsAdmin.getSize(),
                searchEventParamsAdmin.getSize());
        Specification<Event> specification = Specification.where(null);

        List<Long> users = searchEventParamsAdmin.getUsers();
        List<String> states = searchEventParamsAdmin.getStates();
        List<Long> categories = searchEventParamsAdmin.getCategories();
        LocalDateTime rangeEnd = searchEventParamsAdmin.getRangeEnd();
        LocalDateTime rangeStart = searchEventParamsAdmin.getRangeStart();

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("eventStatus").as(String.class).in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        Page<Event> events = eventRepository.findAll(specification, pageable);
        return events.getContent().stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event oldEvent = checkEvent(eventId);
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED) || oldEvent.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new ConflictException("Можно изменить только неподтвержденное событие");
        }
        boolean hasChanges = false;
        String gotAnnotation = updateEvent.getAnnotation();
        if (gotAnnotation != null && !gotAnnotation.isBlank()) {
            oldEvent.setAnnotation(gotAnnotation);
        }
        Long gotCategory = updateEvent.getCategory();
        if (gotCategory != null) {
            Category category = checkCategory(gotCategory);
            oldEvent.setCategory(category);
            hasChanges = true;
        }
        String gotDescription = updateEvent.getDescription();
        if (gotDescription != null && !gotDescription.isBlank()) {
            oldEvent.setDescription(gotDescription);
            hasChanges = true;
        }
        LocalDateTime gotEventDate = updateEvent.getEventDate();
        if (gotEventDate != null) {
            if (gotEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new UncorrectedParametersException("Некорректные параметры даты.Дата начала " +
                        "изменяемого события должна " +
                        "быть не ранее чем за час от даты публикации.");
            }
            oldEvent.setEventDate(updateEvent.getEventDate());
            hasChanges = true;
        }
        Location updatedLocation = updateEvent.getLocation();
        Location oldLocation = oldEvent.getLocation();
        if (updatedLocation != null && oldLocation != updatedLocation) {
            Location location = locationRepository.save(updatedLocation);
            oldEvent.setLocation(location);
            hasChanges = true;
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
            hasChanges = true;
        }
        Integer gotParticipantLimit = updateEvent.getParticipantLimit();
        if (gotParticipantLimit != null) {
            oldEvent.setParticipantLimit(gotParticipantLimit);
            hasChanges = true;
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
            hasChanges = true;
        }
        String gotStateAction = updateEvent.getStateAction();
        if (gotStateAction != null) {
            if (gotStateAction.equals(State.PUBLISH_EVENT.name())) {
                oldEvent.setEventStatus(EventStatus.PUBLISHED);
                hasChanges = true;
            } else if (gotStateAction.equals(State.REJECT_EVENT.name())) {
                oldEvent.setEventStatus(EventStatus.CANCELED);
                hasChanges = true;
            }
        }
        String gotTitle = updateEvent.getTitle();
        if (gotTitle != null && !gotTitle.isBlank()) {
            oldEvent.setTitle(gotTitle);
        }
        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(oldEvent);
        }
        return eventAfterUpdate != null ? EventMapper.toEventFullDto(eventAfterUpdate) : null;
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, org.springframework.data.domain.Sort.by(Sort.Direction.ASC, "id"));
        return eventRepository.findAll(pageRequest).getContent()
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        checkUser(userId);
        Event event = checkEvenByInitiatorAndEventId(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto eventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User user = checkUser(userId);
        checkDateAndTime(LocalDateTime.now(), eventDto.getEventDate());
        Category category = checkCategory(eventDto.getCategory());
        Location location = locationRepository.save(eventDto.getLocation());
        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);
        event.setConfirmedRequests(0);
        event.setViews(0);
        event.setLocation(location);
        Event eventSaved = eventRepository.save(event);
        return EventMapper.toEventFullDto(eventSaved);
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId,
                                                      UpdateEventUserRequest inputUpdate) {
        checkUser(userId);
        boolean hasChanges = false;
        Event oldEvent = checkEvenByInitiatorAndEventId(userId, eventId);
        LocalDateTime newDate = inputUpdate.getEventDate();

        if (newDate != null) {
            checkDateAndTime(LocalDateTime.now(), newDate);
            oldEvent.setEventDate(newDate);
            hasChanges = true;
        }

        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id= " + userId + " не автор события");
        }
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Статус события не может быть обновлен, так как со статусом PUBLISHED");
        }

        String annotation = inputUpdate.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            oldEvent.setAnnotation(annotation);
            hasChanges = true;
        }

        Long categoryId = inputUpdate.getCategory();
        if (categoryId != null) {
            Category category = checkCategory(categoryId);
            oldEvent.setCategory(category);
            hasChanges = true;
        }

        String description = inputUpdate.getDescription();
        if (description != null) {
            oldEvent.setDescription(description);
            hasChanges = true;
        }

        Location location = inputUpdate.getLocation();
        if (location != null) {
            oldEvent.setLocation(location);
            hasChanges = true;
        }

        Integer participantLimit = inputUpdate.getParticipantLimit();
        if (participantLimit != null) {
            oldEvent.setParticipantLimit(participantLimit);
            hasChanges = true;
        }

        Boolean requestModeration = inputUpdate.getRequestModeration();
        if (requestModeration != null) {
            oldEvent.setRequestModeration(requestModeration);
            hasChanges = true;
        }

        State stateAction = inputUpdate.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    oldEvent.setEventStatus(EventStatus.PENDING);
                    hasChanges = true;
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setEventStatus(EventStatus.CANCELED);
                    hasChanges = true;
                    break;
            }
        }

        String title = inputUpdate.getTitle();
        if (title != null) {
            oldEvent.setTitle(title);
            hasChanges = true;
        }

        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(oldEvent);
        }

        return eventAfterUpdate != null ? EventMapper.toEventFullDto(eventAfterUpdate) : null;
    }


    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId) {
        checkUser(userId);
        checkEvenByInitiatorAndEventId(userId, eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest inputUpdate) {
        checkUser(userId);
        Event event = checkEvenByInitiatorAndEventId(userId, eventId);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Это событие не требует подтверждения запросов");
        }
        RequestStatus status = inputUpdate.getStatus();

        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                    throw new ConflictException("Лимит участников исчерпан");
                }
                CaseUpdatedStatusDto updatedStatusConfirmed = updatedStatusConfirmed(event, CaseUpdatedStatusDto.builder()
                        .idsFromUpdateStatus(new ArrayList<>(inputUpdate.getRequestIds())).build(), RequestStatus.CONFIRMED);

                List<Request> confirmedRequests = requestRepository.findAllById(updatedStatusConfirmed.getProcessedIds());
                List<Request> rejectedRequests = new ArrayList<>();
                if (updatedStatusConfirmed.getIdsFromUpdateStatus().size() != 0) {
                    List<Long> ids = updatedStatusConfirmed.getIdsFromUpdateStatus();
                    rejectedRequests = rejectRequest(ids, eventId);
                }

                return EventRequestStatusUpdateResult.builder()
                        .confirmedRequests(confirmedRequests
                                .stream()
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                        .rejectedRequests(rejectedRequests
                                .stream()
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                        .build();
            case REJECTED:
                if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                    throw new ConflictException("Лимит участников исчерпан");
                }

                final CaseUpdatedStatusDto updatedStatusReject = updatedStatusConfirmed(event, CaseUpdatedStatusDto.builder()
                        .idsFromUpdateStatus(new ArrayList<>(inputUpdate.getRequestIds())).build(), RequestStatus.REJECTED);
                List<Request> rejectRequest = requestRepository.findAllById(updatedStatusReject.getProcessedIds());

                return EventRequestStatusUpdateResult.builder()
                        .rejectedRequests(rejectRequest
                                .stream()
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                        .build();
            default:
                throw new UncorrectedParametersException("Некорректный статус - " + status);
        }
    }

    @Override
    public List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request) {

        if (searchEventParams.getRangeEnd() != null && searchEventParams.getRangeStart() != null) {
            if (searchEventParams.getRangeEnd().isBefore(searchEventParams.getRangeStart())) {
                throw new UncorrectedParametersException("Дата окончания не может быть раньше даты начала");
            }
        }

        addStatsClient(request);

        Pageable pageable = PageRequest.of(searchEventParams.getFrom() / searchEventParams.getSize(), searchEventParams.getSize());

        Specification<Event> specification = Specification.where(null);
        LocalDateTime now = LocalDateTime.now();

        if (searchEventParams.getText() != null) {
            String searchText = searchEventParams.getText().toLowerCase();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + searchText + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchText + "%")
                    ));
        }

        if (searchEventParams.getCategories() != null && !searchEventParams.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(searchEventParams.getCategories()));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(searchEventParams.getRangeStart(), now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (searchEventParams.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), searchEventParams.getRangeEnd()));
        }

        if (searchEventParams.getOnlyAvailable() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("eventStatus"), EventStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        getViewsAllEvents(resultEvents);

        return resultEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + eventId + " не опубликовано");
        }
        addStatsClient(request);
        getViewsAllEvents(List.of(event));
        return EventMapper.toEventFullDto(event);
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = " + eventId + " не существует"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
    }


    private Request checkRequestOrEvent(Long eventId, Long requestId) {
        return requestRepository.findByEventIdAndId(eventId, requestId).orElseThrow(
                () -> new NotFoundException("Запроса с id = " + requestId + " или события с id = "
                        + eventId + "не существуют"));
    }


    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категории с id = " + catId + " не существует"));
    }

    private Event checkEvenByInitiatorAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new NotFoundException("События с id = " + eventId + "и с пользователем с id = " + userId +
                        " не существует"));
    }

    private void checkDateAndTime(LocalDateTime time, LocalDateTime dateTime) {
        if (dateTime.isBefore(time.plusHours(2))) {
            throw new UncorrectedParametersException("Поле должно содержать дату, которая еще не наступила.");
        }
    }

    private void getViewsAllEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statsClient.getStats("1999-01-01 00:00:00", "2999-01-01 00:00:00",
                uris, false);

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> viewStatsList = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        Map<Long, ViewStats> viewStatsMap = viewStatsList.stream()
                .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                .collect(Collectors.toMap(
                        statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                        Function.identity()
                ));

        for (Event event : events) {
            ViewStats currentViewStats = viewStatsMap.get(event.getId());
            long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews((int) (views + 1));
        }

        eventRepository.saveAll(events);
    }

    private CaseUpdatedStatusDto updatedStatusConfirmed(Event event, CaseUpdatedStatusDto caseUpdatedStatus, RequestStatus status) {
        int freeRequest = event.getParticipantLimit() - event.getConfirmedRequests();
        List<Long> ids = caseUpdatedStatus.getIdsFromUpdateStatus();
        List<Long> processedIds = new ArrayList<>();

        for (Long id : ids) {
            if (freeRequest == 0) {
                break;
            }
            Request request = checkRequestOrEvent(event.getId(), id);
            request.setStatus(status);
            requestRepository.save(request);
            processedIds.add(request.getId());
            freeRequest--;
        }
        int confirmedRequestUpdate = event.getConfirmedRequests() + processedIds.size();
        event.setConfirmedRequests(confirmedRequestUpdate);
        eventRepository.save(event);
        caseUpdatedStatus.setProcessedIds(processedIds);
        return caseUpdatedStatus;
    }

    private List<Request> rejectRequest(List<Long> ids, Long eventId) {
        List<Request> rejectedRequests = new ArrayList<>();

        for (Long id : ids) {
            Request request = checkRequestOrEvent(eventId, id);
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                break;
            }
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
            rejectedRequests.add(request);
        }

        return rejectedRequests;
    }

    private void addStatsClient(HttpServletRequest request) {
        statsClient.postStats(EndpointHit.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
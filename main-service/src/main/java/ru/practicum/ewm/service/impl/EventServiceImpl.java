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
import ru.practicum.ewm.dto.CaseUpdatedStatusDto;
import ru.practicum.ewm.dto.comment.CountCommentsByEventDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectedParametersException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.EventAdminState;
import ru.practicum.ewm.model.enums.EventStatus;
import ru.practicum.ewm.model.enums.EventUserState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.model.mappers.EventMapper;
import ru.practicum.ewm.model.mappers.LocationMapper;
import ru.practicum.ewm.model.mappers.RequestMapper;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper;


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

        List<EventFullDto> result = events.getContent()
                .stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());

        Map<Long, List<Request>> confirmedRequestsCountMap = getConfirmedRequestsCount(events.toList());
        for (EventFullDto event : result) {
            List<Request> requests = confirmedRequestsCountMap.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequests(requests.size());
        }
        return result;
    }


    @Override
    public EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event oldEvent = checkEvent(eventId);
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED) || oldEvent.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new ConflictException("Можно изменить только неподтвержденное событие");
        }
        boolean hasChanges = false;
        Event eventForUpdate = universalUpdate(oldEvent, updateEvent);
        if (eventForUpdate == null) {
            eventForUpdate = oldEvent;
        } else {
            hasChanges = true;
        }
        LocalDateTime gotEventDate = updateEvent.getEventDate();
        if (gotEventDate != null) {
            if (gotEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new UncorrectedParametersException("Некорректные параметры даты.Дата начала " +
                        "изменяемого события должна " + "быть не ранее чем за час от даты публикации.");
            }
            eventForUpdate.setEventDate(updateEvent.getEventDate());
            hasChanges = true;
        }

        EventAdminState gotAction = updateEvent.getStateAction();
        if (gotAction != null) {
            if (EventAdminState.PUBLISH_EVENT.equals(gotAction)) {
                eventForUpdate.setEventStatus(EventStatus.PUBLISHED);
                hasChanges = true;
            } else if (EventAdminState.REJECT_EVENT.equals(gotAction)) {
                eventForUpdate.setEventStatus(EventStatus.CANCELED);
                hasChanges = true;
            }
        }
        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(eventForUpdate);
        }
        return eventAfterUpdate != null ? EventMapper.toEventFullDto(eventAfterUpdate) : null;
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest inputUpdate) {
        checkUser(userId);
        Event oldEvent = checkEvenByInitiatorAndEventId(userId, eventId);
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Статус события не может быть обновлен, так как со статусом PUBLISHED");
        }
        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь с id= " + userId + " не автор события");
        }
        Event eventForUpdate = universalUpdate(oldEvent, inputUpdate);
        boolean hasChanges = false;
        if (eventForUpdate == null) {
            eventForUpdate = oldEvent;
        } else {
            hasChanges = true;
        }
        LocalDateTime newDate = inputUpdate.getEventDate();
        if (newDate != null) {
            checkDateAndTime(LocalDateTime.now(), newDate);
            eventForUpdate.setEventDate(newDate);
            hasChanges = true;
        }
        EventUserState stateAction = inputUpdate.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    eventForUpdate.setEventStatus(EventStatus.PENDING);
                    hasChanges = true;
                    break;
                case CANCEL_REVIEW:
                    eventForUpdate.setEventStatus(EventStatus.CANCELED);
                    hasChanges = true;
                    break;
            }
        }
        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(eventForUpdate);
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
        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);
        if (eventDto.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(eventDto.getLocation()));
            event.setLocation(location);
        }
        Event eventSaved = eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventSaved);
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
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

        int confirmedRequestsCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() == confirmedRequestsCount) {
                    throw new ConflictException("Лимит участников исчерпан");
                }
                CaseUpdatedStatusDto updatedStatusConfirmed = updatedStatusConfirmed(event,
                        CaseUpdatedStatusDto.builder()
                                .idsFromUpdateStatus(new ArrayList<>(inputUpdate.getRequestIds())).build(),
                        RequestStatus.CONFIRMED, confirmedRequestsCount);

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
                if (event.getParticipantLimit() == confirmedRequestsCount) {
                    throw new ConflictException("Лимит участников исчерпан");
                }

                final CaseUpdatedStatusDto updatedStatusReject = updatedStatusConfirmed(event,
                        CaseUpdatedStatusDto.builder()
                                .idsFromUpdateStatus(new ArrayList<>(inputUpdate.getRequestIds())).build(),
                        RequestStatus.REJECTED, confirmedRequestsCount);
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
        List<EventShortDto> result = resultEvents
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        Map<Long, Long> viewStatsMap = getViewsAllEvents(resultEvents);

        List<CountCommentsByEventDto> commentsCountMap = commentRepository.countCommentByEvent(
                resultEvents.stream().map(Event::getId).collect(Collectors.toList()));
        Map<Long, Long> commentsCountToEventIdMap = commentsCountMap.stream().collect(Collectors.toMap(
                CountCommentsByEventDto::getEventId, CountCommentsByEventDto::getCountComments));

        for (EventShortDto event : result) {
            Long viewsFromMap = viewStatsMap.getOrDefault(event.getId(), 0L);
            event.setViews(viewsFromMap);

            Long commentCountFromMap = commentsCountToEventIdMap.getOrDefault(event.getId(), 0L);
            event.setComments(commentCountFromMap);
        }

        return result;
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + eventId + " не опубликовано");
        }
        addStatsClient(request);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Map<Long, Long> viewStatsMap = getViewsAllEvents(List.of(event));
        Long views = viewStatsMap.getOrDefault(event.getId(), 0L);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = " + eventId + " не существует"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
    }

    private List<Request> checkRequestOrEventList(Long eventId, List<Long> requestId) {
        return requestRepository.findByEventIdAndIdIn(eventId, requestId).orElseThrow(
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

    private Map<Long, Long> getViewsAllEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<LocalDateTime> startDates = events.stream()
                .map(Event::getCreatedDate)
                .collect(Collectors.toList());
        LocalDateTime earliestDate = startDates.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> viewStatsMap = new HashMap<>();

        if (earliestDate != null) {
            ResponseEntity<Object> response = statsClient.getStats(earliestDate, LocalDateTime.now(),
                    uris, true);

            List<ViewStats> viewStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });

            viewStatsMap = viewStatsList.stream()
                    .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(
                            statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                            ViewStats::getHits
                    ));
        }
        return viewStatsMap;
    }

    private CaseUpdatedStatusDto updatedStatusConfirmed(Event event, CaseUpdatedStatusDto caseUpdatedStatus,
                                                        RequestStatus status, int confirmedRequestsCount) {
        int freeRequest = event.getParticipantLimit() - confirmedRequestsCount;
        List<Long> ids = caseUpdatedStatus.getIdsFromUpdateStatus();
        List<Long> processedIds = new ArrayList<>();
        List<Request> requestListLoaded = checkRequestOrEventList(event.getId(), ids);
        List<Request> requestList = new ArrayList<>();

        for (Request request : requestListLoaded) {
            if (freeRequest == 0) {
                break;
            }

            request.setStatus(status);
            requestList.add(request);

            processedIds.add(request.getId());
            freeRequest--;
        }

        requestRepository.saveAll(requestList);
        caseUpdatedStatus.setProcessedIds(processedIds);
        return caseUpdatedStatus;
    }

    private List<Request> rejectRequest(List<Long> ids, Long eventId) {
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> requestList = new ArrayList<>();
        List<Request> requestListLoaded = checkRequestOrEventList(eventId, ids);

        for (Request request : requestListLoaded) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                break;
            }
            request.setStatus(RequestStatus.REJECTED);
            requestList.add(request);
            rejectedRequests.add(request);
        }
        requestRepository.saveAll(requestList);
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

    private Map<Long, List<Request>> getConfirmedRequestsCount(List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events
                .stream().map(Event::getId).collect(Collectors.toList()), RequestStatus.CONFIRMED);
        return requests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId()));
    }

    private Event universalUpdate(Event oldEvent, UpdateEventRequest updateEvent) {
        boolean hasChanges = false;
        String gotAnnotation = updateEvent.getAnnotation();
        if (gotAnnotation != null && !gotAnnotation.isBlank()) {
            oldEvent.setAnnotation(gotAnnotation);
            hasChanges = true;
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
        if (updateEvent.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEvent.getLocation());
            oldEvent.setLocation(location);
            hasChanges = true;
        }
        Integer gotParticipantLimit = updateEvent.getParticipantLimit();
        if (gotParticipantLimit != null) {
            oldEvent.setParticipantLimit(gotParticipantLimit);
            hasChanges = true;
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
            hasChanges = true;
        }
        Boolean requestModeration = updateEvent.getRequestModeration();
        if (requestModeration != null) {
            oldEvent.setRequestModeration(requestModeration);
            hasChanges = true;
        }
        String gotTitle = updateEvent.getTitle();
        if (gotTitle != null && !gotTitle.isBlank()) {
            oldEvent.setTitle(gotTitle);
            hasChanges = true;
        }
        if (!hasChanges) {

            oldEvent = null;
        }
        return oldEvent;
    }
}
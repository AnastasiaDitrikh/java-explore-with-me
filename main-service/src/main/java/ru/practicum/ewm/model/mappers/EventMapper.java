package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс EventMapper служит для преобразования объектов Event и связанных с ними DTO.
 */
@UtilityClass
public class EventMapper {

    /**
     * Преобразует объект класса NewEventDto в объект класса Event.
     *
     * @param newEventDto объект класса NewEventDto, который требуется преобразовать
     * @return объект класса Event, созданный в результате преобразования
     */
    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .id(null)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }

    /**
     * Преобразует объект класса Event в объект класса EventFullDto.
     *
     * @param event объект класса Event, который требуется преобразовать
     * @return объект класса EventFullDto, созданный в результате преобразования
     */
    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedDate())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublisherDate())
                .requestModeration(event.isRequestModeration())
                .state(event.getEventStatus())
                .title(event.getTitle())
                .build();
    }

    /**
     * Преобразует объект класса Event в объект класса EventShortDto.
     *
     * @param event объект класса Event, который требуется преобразовать
     * @return объект класса EventShortDto, созданный в результате преобразования
     */
    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    /**
     * Преобразует список объектов класса Event в список объектов класса EventShortDto.
     *
     * @param events список объектов класса Event, который требуется преобразовать
     * @return список объектов класса EventShortDto, созданный в результате преобразования
     */
    public List<EventShortDto> eventToEventShortDtoList(List<Event> events) {
        return events == null ? new ArrayList<>() :
                events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }
}
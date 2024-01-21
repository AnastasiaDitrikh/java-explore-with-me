package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.model.Location;

/**
 * Класс LocationMapper предоставляет методы для преобразования объектов класса Location и LocationDto друг в друга.
 */
@UtilityClass
public class LocationMapper {

    /**
     * Преобразует объект класса LocationDto в объект класса Location.
     *
     * @param locationDto объект класса LocationDto, который требуется преобразовать
     * @return объект класса Location, созданный в результате преобразования
     */
    public Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    /**
     * Преобразует объект класса Location в объект класса LocationDto.
     *
     * @param location объект класса Location, который требуется преобразовать
     * @return объект класса LocationDto, созданный в результате преобразования
     */
    public LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
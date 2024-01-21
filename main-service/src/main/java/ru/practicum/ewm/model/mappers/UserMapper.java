package ru.practicum.ewm.model.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.User;

/**
 * Класс UserMapper предоставляет методы для преобразования объектов класса User и связанных DTO моделей друг в друга.
 */
@UtilityClass
public class UserMapper {

    /**
     * Преобразует объект класса NewUserRequest в объект класса User.
     *
     * @param newUserRequest объект класса NewUserRequest, который требуется преобразовать
     * @return объект класса User, созданный в результате преобразования
     */
    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    /**
     * Преобразует объект класса User в объект класса UserDto.
     *
     * @param user объект класса User, который требуется преобразовать
     * @return объект класса UserDto, созданный в результате преобразования
     */
    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Преобразует объект класса User в объект класса UserShortDto.
     *
     * @param user объект класса User, который требуется преобразовать
     * @return объект класса UserShortDto, созданный в результате преобразования
     */
    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
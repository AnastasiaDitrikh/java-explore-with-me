package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;

/**
 * Интерфейс UserService, определяющий методы для работы с пользователями
 */
public interface UserService {
    UserDto addNewUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    List<UserDto> getListUsers(List<Long> ids, Integer from, Integer size);
}
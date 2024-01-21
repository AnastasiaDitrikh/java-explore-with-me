package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Обрабатывает GET запрос на получение списка пользователей.
     *
     * @param ids - список ID пользователей (необязательный параметр)
     * @param from - начальный индекс для пагинации (по умолчанию 0)
     * @param size - количество записей на страницу для пагинации (по умолчанию 10)
     * @return список объектов UserDto с информацией о пользователях
     */
    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение списка пользователей");
        return userService.getListUsers(ids, from, size);
    }

    /**
     * Обрабатывает POST запрос на создание нового пользователя.
     *
     * @param newUserRequest - объект данных нового пользователя
     * @return объект UserDto с информацией о созданном пользователе
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("POST запрос на создание пользователя");
        return userService.addNewUser(newUserRequest);
    }

    /**
     * Обрабатывает DELETE запрос на удаление пользователя по его ID.
     *
     * @param userId - ID пользователя, который нужно удалить
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE запрос на удаление пользователя");
        userService.deleteUser(userId);
    }
}
package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.mappers.UserMapper;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Добавляет нового пользователя.
     *
     * @param newUserRequest объект NewUserRequest, содержащий данные нового пользователя
     * @return объект UserDto нового пользователя
     */
    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(user);
    }

    /**
     * Удаляет пользователя по заданному идентификатору.
     *
     * @param userId идентификатор пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Возвращает список пользователей с пагинацией и возможностью фильтрации по списку идентификаторов.
     *
     * @param ids  список идентификаторов пользователей
     * @param from начальная позиция списка
     * @param size размер списка
     * @return список объектов UserDto пользователей
     */
    @Override
    public List<UserDto> getListUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return (ids != null) ? userRepository.findByIdIn(ids, page).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()) :
                userRepository.findAll(page).stream()
                        .map(UserMapper::toUserDto)
                        .collect(Collectors.toList());
    }
}
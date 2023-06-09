package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(int[] ids, Integer from, Integer size) {
        Sort startSort = Sort.by("name");
        Pageable pageable = FromSizeRequest.of(from, size, startSort);
        if (ids == null || ids.length == 0) {
            Page<User> users = userRepository.findAll(pageable);
            log.info("UserService: Данные о всех пользователях, сортировка по name");
            return UserMapper.mapToListUserDto(users);
        } else {
            Page<User> users = userRepository.getUsersByIds(ids, pageable);
            log.info("UserService: Данные о пользователях по списку={}, сортировка по name", ids);
            return UserMapper.mapToListUserDto(users);
        }
    }

    @Transactional
    @Override
    public UserDto saveNewUser(UserDto userDto) {
        User newUser = userRepository.save(UserMapper.mapToUser(userDto));
        log.info("UserService:  Зарегистрирован новый пользователь: {}", newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    @Transactional
    @Override
    public void deleteUserById(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден или недоступен", userId));
        }
        log.info("UserService: Удаление пользователя с id {}", userId);
        userRepository.deleteById(userId);
    }
}

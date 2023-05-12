package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.userToDto(userRepository.findAll());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        String email = userDto.getEmail();
        String name = userDto.getName();

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", id)));

        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", id)));
        return UserMapper.userToDto(user);
    }
}

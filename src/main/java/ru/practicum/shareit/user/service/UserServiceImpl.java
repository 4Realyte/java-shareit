package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailHasDuplicateException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        checkEmailDuplicate(user.getEmail(), user.getId());
        return UserMapper.userToDto(userDao.save(user));
    }

    @Override
    public UserDto updateUser(Map<String, String> updates, Long id) {
        String email = updates.get("email");
        String name = updates.get("name");

        User user = userDao.findById(id);

        if (email != null) {
            checkEmailDuplicate(email, id);
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        return UserMapper.userToDto(userDao.update(user, id));
    }

    @Override
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.userToDto(userDao.findById(id));
    }

    private void checkEmailDuplicate(String email, Long userId) {
        boolean isDuplicate = userDao.findAll().stream()
                .filter(u -> u.getId() != userId)
                .map(User::getEmail)
                .anyMatch(e -> e.equals(email));
        if (isDuplicate) throw new EmailHasDuplicateException("Этот email уже используется");
    }
}

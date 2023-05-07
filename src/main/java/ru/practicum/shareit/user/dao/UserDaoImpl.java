package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private static long USER_COUNTER = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        user.setId(++USER_COUNTER);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user, Long id) {
        return users.put(id, user);
    }

    @Override
    public void delete(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        }
    }

    @Override
    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", id));
        return user;
    }
}

package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User save(User user);

    User update(User user, Long id);

    void delete(Long id);

    User findById(Long id);
}

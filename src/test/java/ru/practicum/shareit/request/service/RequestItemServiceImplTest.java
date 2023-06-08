package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dao.RequestItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class RequestItemServiceImplTest {
    @Mock
    private final RequestItemRepository reqRepo;
    @Mock
    private final UserRepository userRepository;

    @Test
    void addNewRequest_shouldThrowUserNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", anyLong())));


    }
}
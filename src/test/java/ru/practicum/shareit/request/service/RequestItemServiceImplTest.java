package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dao.RequestItemRepository;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.utils.RequestItemMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestItemServiceImplTest {
    @Mock
    private RequestItemRepository reqRepo;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RequestItemServiceImpl requestItemService;
    private RequestItemDto requestItemDto;
    private User requestor;

    @BeforeEach
    void init() {
        requestItemDto = getRequestDto();
        requestor = getUser();
    }

    private static User getUser() {
        return User.builder()
                .id(1L)
                .name("Alexandr")
                .email("alex@mail.ru")
                .build();
    }

    private static RequestItemDto getRequestDto() {
        return RequestItemDto.builder()
                .id(1L)
                .description("some description")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addNewRequest_shouldThrowUserNotFoundException() {
        // when
        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", 1L)));
        // then
        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.addNewRequest(requestItemDto, 1L));

        assertThat(ex.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void addNewRequest_shouldReturnRequestDto() {
        // given
        RequestItem requestItem = RequestItemMapper.dtoToRequest(requestItemDto, requestor);
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(reqRepo.save(Mockito.any()))
                .thenReturn(requestItem);
        RequestItemDto requestItemDtoAfter = requestItemService.addNewRequest(requestItemDto, 1L);
        // then
        assertThat(requestItemDtoAfter.getDescription(), equalTo("some description"));
        assertThat(requestItemDtoAfter, instanceOf(RequestItemDto.class));

        verify(userRepository, Mockito.times(1)).findById(1L);
        verify(reqRepo, Mockito.times(1)).save(any());
        verify(userRepository, never()).existsById(anyLong());
        verifyNoMoreInteractions(reqRepo, userRepository);
    }

    @Test
    void getAllRequests_shouldThrowUserNotFoundException() {
        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        // then
        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getAllRequests(1L, 0, 10));

        assertThat(ex.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void getAllRequests_shouldReturnRequestList() {
        // given
        RequestItem requestItem = RequestItemMapper.dtoToRequest(requestItemDto, requestor);
        Page<RequestItem> page = new PageImpl<>(List.of(requestItem));

        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(reqRepo.findAllPaged(any(), anyLong()))
                .thenReturn(page);
        List<RequestItemResponseDto> dtos = requestItemService.getAllRequests(1L, 0, 10);
        // then
        assertThat(dtos, hasSize(dtos.size()));
        assertThat(dtos, hasItem(allOf(
                hasProperty("description", equalTo("some description")),
                hasProperty("created", notNullValue())
        )));
        assertThat(dtos, instanceOf(List.class));

        verify(userRepository, Mockito.times(1)).existsById(anyLong());
        verify(reqRepo, Mockito.times(1)).findAllPaged(any(), anyLong());
        verifyNoMoreInteractions(userRepository, reqRepo);
    }

    @Test
    void getRequests_shouldThrowUserNotFoundException() {
        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        // then
        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getRequests(1L));
        assertThat(ex.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void getRequests_shouldReturnRequestsList() {
        // given
        RequestItem requestItem = RequestItemMapper.dtoToRequest(requestItemDto, requestor);
        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(reqRepo.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(requestItem));
        List<RequestItemResponseDto> dtos = requestItemService.getRequests(1L);
        // then
        assertThat(dtos, hasSize(dtos.size()));
        assertThat(dtos, hasItem(allOf(
                hasProperty("description", equalTo("some description")),
                hasProperty("created", notNullValue())
        )));
        assertThat(dtos, instanceOf(List.class));

        verify(userRepository, Mockito.times(1)).existsById(anyLong());
        verify(reqRepo, Mockito.times(1)).findAllByRequestorId(1L);
        verifyNoMoreInteractions(userRepository, reqRepo);
    }

    @Test
    void getRequestById_shouldThrowUserNotFoundException() {
        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        // then
        final UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getRequestById(1L, 1L));
        assertThat(ex.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void getRequestById_shouldThrowRequestNotFoundException() {
        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(reqRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        // then
        final RequestNotFoundException ex = assertThrows(RequestNotFoundException.class,
                () -> requestItemService.getRequestById(1L, 1L));
        assertThat(ex.getMessage(), containsString("Запрос с id: 1 не обнаружен"));
    }

    @Test
    void getRequestById_shouldReturnRequestResponseDto() {
        // given
        RequestItem requestItem = RequestItemMapper.dtoToRequest(requestItemDto, requestor);
        requestItem.setId(1L);
        // when
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(reqRepo.findById(anyLong()))
                .thenReturn(Optional.of(requestItem));
        RequestItemResponseDto dto = requestItemService.getRequestById(1L, 1L);
        // then
        assertThat(dto, notNullValue());
        assertThat(dto, allOf(
                hasProperty("id", equalTo(1L)),
                hasProperty("description", containsStringIgnoringCase("some description")),
                hasProperty("created", notNullValue())
        ));
        assertThat(dto, instanceOf(RequestItemResponseDto.class));

        verify(userRepository, Mockito.times(1)).existsById(1L);
        verify(reqRepo, Mockito.times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, reqRepo);
    }
}
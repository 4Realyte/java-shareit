package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestItemServiceImplTestIT {

    private final RequestItemService requestItemService;
    private final UserService userService;

    @Test
    void addNewRequest_shouldCreateRequest() {
        RequestItemDto dto = getRequestDto();
        UserRequestDto firstUser = userService.saveUser(getUserDto("alex@mail.ru"));

        // when
        RequestItemDto result = requestItemService.addNewRequest(dto, firstUser.getId());

        // then
        assertThat(result, allOf(
                hasProperty("id", equalTo(result.getId())),
                hasProperty("description", containsStringIgnoringCase("some description")),
                hasProperty("created", equalTo(result.getCreated()))
        ));
    }

    @Test
    @DisplayName("getRequests should return requests of requestor")
    void getRequests() {
        // given
        UserRequestDto userDto = userService.saveUser(getUserDto("alex@mail.ru"));
        RequestItemDto requestDto = requestItemService.addNewRequest(getRequestDto(), userDto.getId());

        // when
        List<RequestItemResponseDto> requests = requestItemService.getRequests(userDto.getId());

        // then
        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("description", containsStringIgnoringCase("some description")),
                hasProperty("created", equalTo(requestDto.getCreated())),
                hasProperty("items", empty())
        )));
    }

    @Test
    void getRequests_shouldThrowUserNotFound() {
        // given
        UserRequestDto userDto = userService.saveUser(getUserDto("alex@mail.ru"));
        RequestItemDto requestDto = requestItemService.addNewRequest(getRequestDto(), userDto.getId());

        // when + then
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getRequests(2L));

        assertThat(ex.getMessage(), equalTo(String.format("Пользователь с id: %s не обнаружен", 2L)));
    }

    @Test
    @DisplayName("getAllRequests should return requests of other user (not requestor)")
    void getAllRequests() {
        // given
        UserRequestDto firstUser = userService.saveUser(getUserDto("alex@mail.ru"));
        UserRequestDto secondUser = userService.saveUser(getUserDto("leha@yandex.ru"));
        RequestItemDto requestDto = requestItemService.addNewRequest(getRequestDto(), firstUser.getId());

        // when
        List<RequestItemResponseDto> requests = requestItemService.getAllRequests(secondUser.getId(), 0, 10);

        // then
        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("description", containsStringIgnoringCase("some description")),
                hasProperty("created", equalTo(requestDto.getCreated())),
                hasProperty("items", empty())
        )));
    }

    @Test
    void getAllRequests_shouldThrowUserNotFound() {
        // given
        UserRequestDto firstUser = userService.saveUser(getUserDto("alex@mail.ru"));
        // when + then
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getAllRequests(2L, 0, 10));

        assertThat(ex.getMessage(), equalTo(String.format("Пользователь с id: %s не обнаружен", 2L)));
    }

    @Test
    @DisplayName("getAllRequests should return empty requests of requestor")
    void getAllRequests_requestor() {
        // given
        UserRequestDto userDto = userService.saveUser(getUserDto("alex@mail.ru"));
        // when
        List<RequestItemResponseDto> requests = requestItemService.getAllRequests(userDto.getId(), 0, 10);
        // then
        assertThat(requests, empty());
    }

    @Test
    void getAllRequestById_shouldThrowUserNotFound() {
        // given
        UserRequestDto firstUser = userService.saveUser(getUserDto("alex@mail.ru"));
        // when + then
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> requestItemService.getRequestById(2L, 1L));

        assertThat(ex.getMessage(), equalTo(String.format("Пользователь с id: %s не обнаружен", 2L)));
    }

    private static UserRequestDto getUserDto(String email) {
        return UserRequestDto.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static RequestItemDto getRequestDto() {
        return RequestItemDto.builder()
                .description("some description")
                .build();
    }
}
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void createUser_shouldReturnUser() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexa@mail.ru");
        UserRequestDto responseDto = getUserResponseDto("lexa@mail.ru");
        // when
        when(userService.saveUser(any()))
                .thenReturn(responseDto);
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.name", equalTo(responseDto.getName())),
                        jsonPath("$.email", equalTo(responseDto.getEmail()))
                );
    }

    @Test
    @SneakyThrows
    void createUser_BadRequestWhenEmailIsInvalid() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexa.ru");
        // when
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @SneakyThrows
    void createUser_BadRequestWhenNameIsInvalid() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexa@mail.ru");
        requestDto.setName("");
        // when
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @SneakyThrows
    void getAllUsers() {
        // given
        List<UserRequestDto> users = List.of(getUserRequestDto("lexa@mail.ru"),
                getUserRequestDto("sha@yandex.ru"));
        // when
        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$..email", hasItems("sha@yandex.ru", "lexa@mail.ru")),
                        jsonPath("$..name", hasSize(2))
                );
    }

    @Test
    @SneakyThrows
    void getUserById() {
        // given
        UserRequestDto user = getUserRequestDto("sha@yandex.ru");
        // when
        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name", equalTo(user.getName())),
                        jsonPath("$.email", equalTo(user.getEmail()))
                );
    }

    @Test
    @SneakyThrows
    void updateUser_BadRequestWhenEmailIsInvalid() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexa.ru");
        // when
        mvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateUser_shouldReturnUpdatedUser() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexa@mail.ru");
        UserRequestDto responseDto = getUserResponseDto("alexandr@mail.ru");
        // when
        when(userService.updateUser(any(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.email", equalTo(responseDto.getEmail()))
                );
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        // when
        mvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk()
                );
        verify(userService, Mockito.times(1)).deleteUser(anyLong());
    }

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Alex")
                .email(email)
                .build();
    }

    private static UserRequestDto getUserRequestDto(String email) {
        return UserRequestDto.builder()
                .name("Alex")
                .email(email)
                .build();
    }

    private static UserRequestDto getUserResponseDto(String email) {
        return UserRequestDto.builder()
                .id(1L)
                .name("Alex")
                .email(email)
                .build();
    }
}
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserClient client;
    @Autowired
    private MockMvc mvc;

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
        verify(client, never()).createUser(any());
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
                        status().isBadRequest()
                );
    }

    @Test
    @SneakyThrows
    void updateUser_BadRequestWhenEmailIsInvalid() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexaru");
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
    void createUser_BadRequest_WhenEmailIsNull() {
        // given
        UserRequestDto requestDto = getUserRequestDto("lexa.ru");
        requestDto.setEmail(null);
        // when
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isBadRequest());
        verify(client, never()).createUser(any());
    }

    private static UserRequestDto getUserRequestDto(String email) {
        return UserRequestDto.builder()
                .name("Alex")
                .email(email)
                .build();
    }
}
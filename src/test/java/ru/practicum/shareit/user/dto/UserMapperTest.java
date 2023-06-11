package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserMapperTest {

    @Test
    void userToDto() {
        // given
        User user = getUser(1L, "alexas@mai.ru");
        // when
        UserRequestDto result = UserMapper.userToDto(user);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void toUserShort() {
        // given
        User user = getUser(1L, "alexas@mai.ru");
        // when
        UserShortResponseDto result = UserMapper.toUserShort(user);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
    }

    @Test
    void dtoToUser() {
        // given
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("alex")
                .email("alex@mail.ru")
                .build();
        // when
        User user = UserMapper.dtoToUser(userRequestDto);
        // then
        assertThat(user, notNullValue());
        assertThat(user.getId(), nullValue());
        assertThat(user.getName(), equalTo(userRequestDto.getName()));
        assertThat(user.getEmail(), equalTo(userRequestDto.getEmail()));
    }

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Alexandr")
                .email(email)
                .build();
    }
}
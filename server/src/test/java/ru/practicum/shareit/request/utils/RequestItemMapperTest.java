package ru.practicum.shareit.request.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RequestItemMapperTest {

    @Test
    void toRequestItemDto() {
        // given
        User requestor = getUser(1L, "alexas@mai.ru");
        RequestItem requestItem = getRequest(requestor);
        // when
        RequestItemDto result = RequestItemMapper.toRequestItemDto(requestItem);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(requestItem.getId()));
        assertThat(result.getDescription(), equalTo(requestItem.getDescription()));
        assertThat(result.getCreated(), equalTo(requestItem.getCreated()));
    }

    @Test
    void dtoToRequest() {
        // given
        User requestor = getUser(1L, "alexas@mai.ru");
        RequestItemDto dto = getRequestDto();
        // when
        RequestItem result = RequestItemMapper.dtoToRequest(dto, requestor);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getDescription(), equalTo(dto.getDescription()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), empty());
    }

    @Test
    void toResponseDto() {
        // given
        User requestor = getUser(1L, "alexas@mai.ru");
        User owner = getUser(2L, "alex@mai.ru");
        Item item = getItem(owner);
        RequestItem requestItem = getRequest(requestor);
        requestItem.setItems(List.of(item));
        // when
        RequestItemResponseDto result = RequestItemMapper.toResponseDto(requestItem);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(requestItem.getId()));
        assertThat(result.getDescription(), equalTo(requestItem.getDescription()));
        assertThat(result.getCreated(), equalTo(requestItem.getCreated()));
        assertThat(result.getItems(), not(empty()));
        assertThat(result.getItems(), hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName()))
        )));
    }

    private static RequestItem getRequest(User requestor) {
        return RequestItem.builder()
                .id(1L)
                .description("some description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    private static RequestItemDto getRequestDto() {
        return RequestItemDto.builder()
                .id(1L)
                .description("some description")
                .created(LocalDateTime.now())
                .build();
    }

    private static User getUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("brush")
                .description("some brush")
                .available(true)
                .owner(owner)
                .build();
    }
}
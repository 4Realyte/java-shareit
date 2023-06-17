package ru.practicum.shareit.item.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemMapperTest {

    @Test
    void itemToDto() {
        // given
        User user = getUser(1L, "kex@mail.ru");
        Item item = getItem(user);
        // when
        ItemRequestDto result = ItemMapper.itemToDto(item);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));
    }

    @Test
    void toItemShort() {
        // given
        User user = getUser(1L, "kex@mail.ru");
        Item item = getItem(user);
        // when
        ItemShortDto result = ItemMapper.toItemShort(item);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getRequestId(), nullValue());
    }

    @Test
    void toItemResponseDto() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User owner = getUser(1L, "alex@mai.ru");
        User booker = getUser(2L, "alexa@mai.ru");
        Item item = getItem(owner);
        BookingShortDto nextBooking = BookingMapper.toShortDto(getBooking(item, booker));
        BookingShortDto lastBooking = BookingMapper.toShortDto(getBooking(item, booker));
        lastBooking.setStart(now.minusDays(2));
        lastBooking.setEnd(now.minusDays(1));
        // when
        ItemResponseDto result = ItemMapper.toItemResponseDto(item, nextBooking, lastBooking);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getLastBooking(), equalTo(lastBooking));
        assertThat(result.getNextBooking(), equalTo(nextBooking));
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getComments(), nullValue());
    }

    @Test
    void toItemResponseDtoWithComments() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User owner = getUser(1L, "alex@mai.ru");
        User booker = getUser(2L, "alexa@mai.ru");
        User author = getUser(3L, "kexa@mail.ru");
        Item item = getItem(owner);
        BookingShortDto nextBooking = BookingMapper.toShortDto(getBooking(item, booker));
        BookingShortDto lastBooking = BookingMapper.toShortDto(getBooking(item, booker));
        lastBooking.setStart(now.minusDays(2));
        lastBooking.setEnd(now.minusDays(1));
        List<CommentResponseDto> comments = List.of(CommentMapper.toResponseDto(getComment(1L, author, item)));
        // when
        ItemResponseDto result = ItemMapper.toItemResponseDto(item, nextBooking, lastBooking, comments);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getLastBooking(), equalTo(lastBooking));
        assertThat(result.getNextBooking(), equalTo(nextBooking));
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getComments(), not(empty()));
    }

    @Test
    void dtoToItem() {
        // given
        ItemRequestDto dto = getItemRequestDto();
        User owner = getUser(1L, "alex@mai.ru");
        User requestor = getUser(2L, "alexas@mai.ru");
        RequestItem request = getRequest(requestor);
        // when
        Item result = ItemMapper.dtoToItem(dto, owner, request);
        // then
        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(dto.getName()));
        assertThat(result.getRequest(), equalTo(request));
        assertThat(result.getDescription(), equalTo(dto.getDescription()));
        assertThat(result.getOwner(), equalTo(owner));
    }

    private static ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .name("brush")
                .description("some brush")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static RequestItem getRequest(User requestor) {
        return RequestItem.builder()
                .description("some description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    private static Comment getComment(Long id, User author, Item item) {
        return Comment.builder()
                .id(1L)
                .author(author)
                .item(item)
                .text("very good")
                .created(LocalDateTime.now())
                .build();
    }

    private static Booking getBooking(Item item, User booker) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
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
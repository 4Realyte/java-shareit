package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIT {
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;

    @Test
    void getItemById_whenOwner() {
        // given
        LocalDateTime now = LocalDateTime.now();

        UserRequestDto user = userService.saveUser(getUserDto("lex@mail.ru"));
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        BookingResponseDto nextBooking = bookingService.addBooking(getBookingRequestDto(
                item.getId(), now.plusDays(1L), now.plusDays(4L)), user.getId());
        // when
        ItemResponseDto result = itemService.getItemById(owner.getId(), item.getId());
        // then
        assertThat(result, notNullValue());
        assertThat(result.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(result.getComments(), empty());
    }

    @Test
    void getItemById_whenNotOwner() {
        // given
        LocalDateTime now = LocalDateTime.now();

        UserRequestDto user = userService.saveUser(getUserDto("lex@mail.ru"));
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        BookingResponseDto nextBooking = bookingService.addBooking(getBookingRequestDto(
                item.getId(), now.plusDays(1L), now.plusDays(4L)), user.getId());
        // when
        ItemResponseDto result = itemService.getItemById(user.getId(), item.getId());
        // then
        assertThat(result, notNullValue());
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        assertThat(result.getComments(), empty());
    }

    @Test
    void getItemsByOwner_bookingsNotEmptyCommentsNotNull() {
        // given
        LocalDateTime now = LocalDateTime.now();

        UserRequestDto user = userService.saveUser(getUserDto("lex@mail.ru"));
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        BookingResponseDto nextBooking = bookingService.addBooking(getBookingRequestDto(
                item.getId(), now.plusDays(1L), now.plusDays(4L)), user.getId());
        BookingResponseDto lastBooking = bookingService.addBooking(getBookingRequestDto(
                item.getId(), now.minusDays(4L), now.minusDays(1L)), user.getId());

        CommentResponseDto comment = itemService.addComment(item.getId(), getCommentDto(), user.getId());
        // when
        List<ItemResponseDto> result = itemService.getItemsByOwner(owner.getId(), 0, 10);
        // then
        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable())),
                hasProperty("nextBooking", notNullValue()),
                hasProperty("lastBooking", notNullValue()),
                hasProperty("comments", not(empty()))
        )));
    }

    @Test
    void getItemsByOwner_bookingsEmptyCommentsNull() {
        // given
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        // when
        List<ItemResponseDto> result = itemService.getItemsByOwner(owner.getId(), 0, 10);
        // then
        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable())),
                hasProperty("nextBooking", nullValue()),
                hasProperty("lastBooking", nullValue()),
                hasProperty("comments", nullValue())
        )));
    }

    @Test
    void search_findItemWithIgnoringCaseAndSubStringInput() {
        // given
        UserRequestDto user = userService.saveUser(getUserDto("lex@mail.ru"));
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        GetSearchItem search = GetSearchItem.of("BrUs", user.getId(), 0, 10);
        // when
        List<ItemRequestDto> result = itemService.search(search);
        // then
        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("name", containsStringIgnoringCase("brush")),
                hasProperty("description", containsStringIgnoringCase("brush"))
        )));
    }

    @Test
    void search_shouldReturnEmptyResult() {
        // given
        UserRequestDto user = userService.saveUser(getUserDto("lex@mail.ru"));
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        GetSearchItem search = GetSearchItem.of("prof", user.getId(), 0, 10);
        // when
        List<ItemRequestDto> result = itemService.search(search);
        // then
        assertThat(result, empty());
    }

    @Test
    void searchCommentsByText() {
        // given
        LocalDateTime now = LocalDateTime.now();
        UserRequestDto user = userService.saveUser(getUserDto("lex@mail.ru"));
        UserRequestDto owner = userService.saveUser(getUserDto("lexa@mail.ru"));
        ItemShortDto item = itemService.addNewItem(getItemDto(), owner.getId());
        BookingResponseDto lastBooking = bookingService.addBooking(getBookingRequestDto(
                item.getId(), now.minusDays(4L), now.minusDays(1L)), user.getId());
        CommentResponseDto comment = itemService.addComment(item.getId(), getCommentDto(), user.getId());
        GetSearchItem search = GetSearchItem.of("very", user.getId(), item.getId(), 0, 10);
        // when
        List<CommentResponseDto> result = itemService.searchCommentsByText(search);
        // then
        assertThat(result, not(empty()));
        assertThat(result, hasItem(allOf(
                hasProperty("id", equalTo(comment.getId())),
                hasProperty("text", containsStringIgnoringCase("very good")),
                hasProperty("authorName", equalTo(user.getName())),
                hasProperty("created", notNullValue())
        )));
    }

    private static UserRequestDto getUserDto(String email) {
        return UserRequestDto.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static CommentRequestDto getCommentDto() {
        return CommentRequestDto.builder()
                .text("very good")
                .build();
    }

    private static ItemRequestDto getItemDto() {
        return ItemRequestDto.builder()
                .name("brush")
                .description("very good brush")
                .available(true)
                .build();
    }

    private static BookingRequestDto getBookingRequestDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        return BookingRequestDto.builder()
                .status(BookingStatus.WAITING)
                .startDate(start)
                .endDate(end)
                .itemId(itemId)
                .build();
    }
}
package ru.practicum.shareit.booking.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class BookingMapperTest {

    @Test
    void dtoToBooking() {
        BookingRequestDto requestDto = getBookingRequestDto();
        Booking booking = BookingMapper.dtoToBooking(requestDto, null, null);
        assertThat(booking.getStatus(), notNullValue());
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));

    }

    @Test
    void toResponseDto() {
        User owner = getUser(1L, "alex@mai.ru");
        User booker = getUser(2L, "alexa@mai.ru");
        Item item = getItem(owner);
        Booking booking = getBooking(item, booker);
        BookingResponseDto result = BookingMapper.toResponseDto(booking);

        assertThat(result, notNullValue());
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    void toShortDto() {
        User owner = getUser(1L, "alex@mai.ru");
        User booker = getUser(2L, "alexa@mai.ru");
        Item item = getItem(owner);
        Booking booking = getBooking(item, booker);
        BookingShortDto result = BookingMapper.toShortDto(booking);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getBookerId(), equalTo(booker.getId()));
    }

    private static BookingRequestDto getBookingRequestDto() {
        return BookingRequestDto.builder()
                .id(1L)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(1L)
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

    private static Booking getBooking(Item item, User booker) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }
}
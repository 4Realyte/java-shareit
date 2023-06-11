package ru.practicum.shareit.booking.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

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

    private static BookingRequestDto getBookingRequestDto() {
        return BookingRequestDto.builder()
                .id(1L)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(1L)
                .build();
    }
}
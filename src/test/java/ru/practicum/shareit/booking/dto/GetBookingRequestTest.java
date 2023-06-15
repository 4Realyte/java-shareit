package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.State;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GetBookingRequestTest {

    @Test
    void of() {
        GetBookingRequest bookingRequest = GetBookingRequest.of(State.ALL, 1L, true, 1, 4);
        assertThat(bookingRequest.getFrom(), equalTo(0));
    }
}
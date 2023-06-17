package ru.practicum.shareit.booking.utils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.State;

import static org.hamcrest.MatcherAssert.assertThat;


class StateConverterTest {

    private StateConverter converter = new StateConverter();

    @Test
    void convert_withUnsupported() {
        State state = converter.convert("Something");
        assertThat(state, Matchers.equalTo(State.UNSUPPORTED_STATUS));
    }

    @Test
    void convert_WithSupportedStatus() {
        State state = converter.convert("all");
        assertThat(state, Matchers.equalTo(State.ALL));
    }
}
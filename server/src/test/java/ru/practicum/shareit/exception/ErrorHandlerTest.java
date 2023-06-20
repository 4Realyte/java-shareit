package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handle() {
        ItemNotAvailableException ex = new ItemNotAvailableException("some ex");
        Map<String, String> result = errorHandler.handle(ex);
        assertNotNull(result);
        assertThat(result.get("Ошибка запроса"), equalTo(ex.getMessage()));
    }

    @Test
    void handleUnsupportedEx() {
        UnknownStateException ex = new UnknownStateException("some ex");
        Map<String, String> result = errorHandler.handleUnsupportedEx(ex);
        assertNotNull(result);
        assertThat(result.get("error"), equalTo("Unknown state: " + ex.getMessage()));
    }

    @Test
    void handleNotFoundException() {
        BookingNotFoundException ex = new BookingNotFoundException("some ex");
        Map<String, String> stringStringMap = errorHandler.handleNotFoundException(ex);
        assertNotNull(stringStringMap);
        assertThat(stringStringMap.get("Ошибка запроса"), equalTo(ex.getMessage()));
    }
}
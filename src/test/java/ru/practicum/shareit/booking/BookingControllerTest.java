package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addBooking_shouldAddBookingWhenRequestIsCorrect() {
        BookingRequestDto requestDto = getBookingRequestDto();
        BookingResponseDto responseDto = getBookingResponse();
        // when
        when(bookingService.addBooking(any(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.start", notNullValue()),
                        jsonPath("$.end", notNullValue()),
                        jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class),
                        jsonPath("$.status", equalTo(responseDto.getStatus().name()))
                );
    }

    @Test
    @SneakyThrows
    void addBooking_shouldReturnBadRequest_whenUserIdHeaderIsAbsent() {
        BookingRequestDto requestDto = getBookingRequestDto();
        BookingResponseDto responseDto = getBookingResponse();
        // when
        when(bookingService.addBooking(any(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).addBooking(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void addBooking_whenInvalidDate() {
        BookingRequestDto requestDto = getBookingRequestDto();
        requestDto.setEndDate(requestDto.getStartDate());
        BookingResponseDto responseDto = getBookingResponse();
        // when
        when(bookingService.addBooking(any(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @SneakyThrows
    void getBookingById_shouldReturnBookingWhenRequestIsCorrect() {
        BookingResponseDto responseDto = getBookingResponse();

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(responseDto.getId()), Long.class),
                        jsonPath("$.start", notNullValue()),
                        jsonPath("$.end", notNullValue()),
                        jsonPath("$.booker.id", is(responseDto.getBooker().getId()), Long.class),
                        jsonPath("$.status", equalTo(responseDto.getStatus().name()))
                );
    }

    @Test
    @SneakyThrows
    void getBookingById_shouldReturnBadRequest_whenPathVariableIsIncorrect() {
        BookingResponseDto responseDto = getBookingResponse();

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/null")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void approveBooking_shouldApproveBookingWhenRequestIsCorrect() {
        BookingResponseDto responseDto = getBookingResponse();

        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(responseDto.getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void approveBooking_shouldReturnBadRequest_whenUserIdHeaderIsAbsent() {
        BookingResponseDto responseDto = getBookingResponse();

        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).approveBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_shouldReturnBookingWhenRequestIsCorrect() {
        List<BookingResponseDto> responseDto = List.of(getBookingResponse());

        when(bookingService.getAllUserBookings(any()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(responseDto.get(0).getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void getAllUserItemBookings_shouldReturnBookingWhenRequestIsCorrect() {
        List<BookingResponseDto> responseDto = List.of(getBookingResponse());

        when(bookingService.getAllUserBookings(any()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(responseDto.get(0).getId()), Long.class)
                );
    }

    private static BookingRequestDto getBookingRequestDto() {
        return BookingRequestDto.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(1L)
                .build();
    }

    private static BookingResponseDto getBookingResponse() {
        ItemShortDto item = ItemShortDto.builder()
                .id(1L)
                .name("brush")
                .description("some brush")
                .available(true)
                .build();
        UserShortResponseDto booker = UserShortResponseDto.builder()
                .id(1L)
                .name("Alex")
                .build();

        return BookingResponseDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }
}
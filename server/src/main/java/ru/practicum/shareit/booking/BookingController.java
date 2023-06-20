package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;

    @PostMapping
    public BookingResponseDto addBooking(@RequestBody BookingRequestDto dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingServiceImpl.addBooking(dto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingServiceImpl.getBookingById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestParam Boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingServiceImpl.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") State state,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "10") int size) {
        return bookingServiceImpl.getAllUserBookings(GetBookingRequest.of(state, userId, false, from, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllUserItemBookings(@RequestParam(defaultValue = "ALL") State state,
                                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "10") int size) {
        return bookingServiceImpl.getAllUserBookings(GetBookingRequest.of(state, userId, true, from, size));
    }
}

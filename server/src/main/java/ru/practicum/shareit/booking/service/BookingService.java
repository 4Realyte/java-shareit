package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(BookingRequestDto dto, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId);

    List<BookingResponseDto> getAllUserBookings(GetBookingRequest request);
}

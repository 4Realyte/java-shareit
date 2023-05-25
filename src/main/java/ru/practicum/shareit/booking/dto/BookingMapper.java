package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingRequestDto toBookingRequestDto(Booking booking) {
        return BookingRequestDto.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .itemId(booking.getItem() != null ? booking.getItem().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public static Booking dtoToBooking(BookingRequestDto bookingRequestDto, Item item, User user) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .startDate(bookingRequestDto.getStartDate())
                .endDate(bookingRequestDto.getEndDate())
                .item(item)
                .booker(user)
                .status(bookingRequestDto.getStatus() != null ? bookingRequestDto.getStatus() : BookingStatus.WAITING)
                .build();
    }

    public static BookingResponseDto toResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingResponseDto> toResponseDto(Iterable<Booking> bookings) {
        List<BookingResponseDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toResponseDto(booking));
        }
        return dtos;
    }
}

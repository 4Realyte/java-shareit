package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .itemId(booking.getItem() != null ? booking.getItem().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public static Booking dtoToBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .id(bookingDto.getId())
                .startDate(bookingDto.getStartDate())
                .endDate(bookingDto.getEndDate())
                .item(item)
                .booker(user)
                .status(bookingDto.getStatus() != null ? bookingDto.getStatus() : BookingStatus.WAITING)
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
}

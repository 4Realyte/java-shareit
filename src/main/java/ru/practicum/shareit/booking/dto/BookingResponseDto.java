package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@Builder
public class BookingResponseDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Item item;
    private User booker;
    private BookingStatus status;
}

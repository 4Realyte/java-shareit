package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

@Data
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking nextBooking;
    private Booking lastBooking;
}

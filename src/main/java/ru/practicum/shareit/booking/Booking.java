package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@Jacksonized
public class Booking {
    private long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Item item;
    private User booker;
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;
}

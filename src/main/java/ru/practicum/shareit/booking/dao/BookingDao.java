package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookingDao {
    private final Map<Long,Booking> bookingMap = new HashMap<>();

}

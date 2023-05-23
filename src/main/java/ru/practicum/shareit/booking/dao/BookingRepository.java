package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as bk " +
            "where b.id = ?1 AND (bk.id = ?2 OR i.owner.id = ?2)")
    Optional<Booking> findBooking(Long bookingId, Long userId);

    @Query("select b from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as bk " +
            "where b.id = ?1 AND i.owner.id = ?2")
    Optional<Booking> findBookingByOwner(Long bookingId, Long ownerId);

}

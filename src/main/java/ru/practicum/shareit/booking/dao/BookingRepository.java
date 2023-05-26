package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    @Query("select b from Booking as b " +
            "join  b.item as i " +
            "join  b.booker as bk " +
            "where b.id = ?1 AND (bk.id = ?2 OR i.owner.id = ?2)")
    Optional<Booking> findBooking(Long bookingId, Long userId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as bk " +
            "where b.id = ?1 AND i.owner.id = ?2")
    Optional<Booking> findBookingByOwner(Long bookingId, Long ownerId);

    @Query("select b from Booking as b " +
            "JOIN fetch b.booker as bk " +
            "join fetch b.item as i " +
            "where bk.id = ?1")
    List<Booking> findAllByBookerId(Long bookerId);


    @Query(value = "SELECT * FROM bookings as bk " +
            "JOIN items as i ON bk.item_id=i.id " +
            "JOIN users as u ON bk.booker_id=u.id " +
            "WHERE bk.item_id=(:id) AND bk.end_date < now() " +
            "ORDER BY bk.end_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> findLastBookingByItemId(@Param("id") Long id);


    @Query(value = "SELECT * FROM bookings as bk " +
            "JOIN items as i ON bk.item_id=i.id " +
            "JOIN users as u ON bk.booker_id=u.id " +
            "WHERE bk.item_id=(:id) AND bk.start_date > now() " +
            "ORDER BY bk.start_date ASC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> findNextBookingByItemId(@Param("id") Long id);

    List<Booking> findAllByItem_IdIn(List<Long> ids);
}

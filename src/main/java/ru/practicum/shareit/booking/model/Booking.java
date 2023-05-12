package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @OneToOne
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.WAITING;
}

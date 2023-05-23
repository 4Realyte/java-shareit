package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {
    @Null
    private Long id;
    @FutureOrPresent
    @NotNull
    @JsonProperty("start")
    private LocalDate startDate;
    @FutureOrPresent
    @NotNull
    @JsonProperty("end")
    private LocalDate endDate;
    @NotNull
    private Long itemId;
    private BookingStatus status;
}

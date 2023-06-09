package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.State;

@Data
@NoArgsConstructor
public class GetBookingRequest {
    private Long userId;
    private State state;
    private boolean isOwner;
    private int from;
    private int size;

    public static GetBookingRequest of(State state, Long userId, boolean isOwner,
                                       int from, int size) {
        GetBookingRequest request = new GetBookingRequest();
        request.setState(state);
        request.setUserId(userId);
        request.setOwner(isOwner);
        request.setSize(size);
        request.setFrom(from > 0 ? from / size : 0);
        return request;
    }
}

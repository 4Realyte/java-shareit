package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto addBooking(BookingDto dto, Long userId) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", dto.getItemId())));

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Вещь с id: %s не доступна для брони", item.getId()));
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId)));
        Booking booking = BookingMapper.dtoToBooking(dto, item, user);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findBooking(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронь с id: %s не обнаружена", bookingId)));
        return BookingMapper.toResponseDto(booking);
    }

    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findBookingByOwner(bookingId, ownerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Бронь с id: %s для владельца с id: %s не обнаружена", bookingId, ownerId)));
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toResponseDto(bookingRepository.save(booking));
    }
}

package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.GetBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserShortResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.QBooking.booking;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addBooking_shouldThrowItemNotFoundException() {
        BookingRequestDto dto = getBookingRequestDto();
        when(itemRepository.findById(anyLong()))
                .thenThrow(new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", 1L)));

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.addBooking(dto, 1L));
        assertThat(exception.getMessage(), containsString("Вещь с id: 1 не обнаружена"));
    }

    @Test
    void addBooking_shouldThrowItemNotAvailableException() {
        Item item = getItem(null, false);

        BookingRequestDto dto = getBookingRequestDto();
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.addBooking(dto, 1L));
        assertThat(exception.getMessage(), containsString("Вещь с id: 1 недоступна для брони"));
    }

    @Test
    void addBooking_shouldThrowUserNotFound() {
        User user = User.builder()
                .id(1L)
                .name("Peter")
                .email("peter@mail.ru")
                .build();

        Item item = getItem(user, true);

        BookingRequestDto dto = getBookingRequestDto();
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id: 1 не обнаружен"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.addBooking(dto, 1L));
        assertThat(exception.getMessage(), containsString("Пользователь с id: 1 не обнаружен"));
    }

    @Test
    void addBooking_shouldThrowResponseStatusException() {
        User user = User.builder()
                .id(1L)
                .name("Peter")
                .email("peter@mail.ru")
                .build();

        Item item = getItem(user, true);

        BookingRequestDto dto = getBookingRequestDto();
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookingService.addBooking(dto, 1L));
        assertThat(exception.getMessage(), containsString("Бронь для владельца вещи недоступна"));
    }

    @Test
    void addBooking_shouldReturnBookingResponseDto() {
        User user = User.builder()
                .id(1L)
                .name("Peter")
                .email("peter@mail.ru")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("Peter")
                .email("peters@mail.ru")
                .build();

        Item item = getItem(owner, true);
        BookingRequestDto dto = getBookingRequestDto();
        Booking booking = BookingMapper.dtoToBooking(dto, item, user);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto result = bookingService.addBooking(dto, 1L);

        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result, instanceOf(BookingResponseDto.class));
        assertThat(result.getItem(), instanceOf(ItemShortDto.class));
        assertThat(result.getBooker(), instanceOf(UserShortResponseDto.class));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getBookingById_shouldThrowBookingNotFoundEx() {
        when(bookingRepository.findBooking(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException("Бронь с id: 1 не обнаружена"));
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));
        assertThat(exception.getMessage(), containsString("Бронь с id: 1 не обнаружена"));
        assertThat(exception, instanceOf(BookingNotFoundException.class));
    }

    @Test
    void approveBooking_shouldThrowBookingNotFoundEx() {
        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException("Бронь с id: 1 для владельца с id: 1 не обнаружена"));
        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(1L, true, 1L));
        assertThat(exception.getMessage(), containsString("Бронь с id: 1 для владельца с id: 1 не обнаружена"));
        assertThat(exception, instanceOf(BookingNotFoundException.class));
    }

    @Test
    void approveBooking_shouldThrowResponseStatusEx() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> bookingService.approveBooking(1L, true, 1L));
        assertThat(exception.getMessage(), containsString("Невозможно изменить статус аренды после подтверждения"));
        assertThat(exception, instanceOf(ResponseStatusException.class));
    }

    @Test
    void approveBooking_shouldChangeStatusToRejectedOrApproved() {
        // given
        User user = User.builder()
                .id(1L)
                .name("Peter")
                .email("peters@mail.ru")
                .build();
        Item item = getItem(null,true);
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .booker(user)
                .item(item)
                .build();

        // when
        when(bookingRepository.findBookingByOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto rejected = bookingService.approveBooking(1L, false, 1L);
        // then
        assertThat(rejected.getStatus(), equalTo(BookingStatus.REJECTED));
        booking.setStatus(BookingStatus.WAITING);
        BookingResponseDto approved = bookingService.approveBooking(1L, true, 1L);
        // then
        assertThat(approved.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getAllUserBookings_shouldThrowBookingNotFoundEx() {
        // given
        List<Predicate> predicates = new ArrayList<>();
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startDate"));
        predicates.add(booking.item.owner.id.eq(1L));
        // when
        when(bookingRepository.findAll(ExpressionUtils.allOf(predicates), page))
                .thenReturn(Page.empty());
        GetBookingRequest request = GetBookingRequest.of(State.ALL, 1L, true, 0, 10);
        // then
        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getAllUserBookings(request));
        assertThat(ex.getMessage(), containsString("Пользователь с id : 1 не имеет брони"));
        assertThat(ex, instanceOf(BookingNotFoundException.class));
    }

    private static BookingRequestDto getBookingRequestDto() {
        return BookingRequestDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .itemId(1L)
                .build();
    }

    private static Item getItem(User owner, boolean available) {
        return Item.builder()
                .id(1L)
                .description("good item")
                .name("key")
                .owner(owner)
                .available(available)
                .build();
    }
}
package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestItemRepository;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestItemRepository requestItemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void addNewItem_shouldThrowUserNotFoundEx() {
        ItemRequestDto requestDto = getItemRequestDto();

        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotFoundException("Пользователь с id: 1 не обнаружен"));

        assertThrows(UserNotFoundException.class,
                () -> itemService.addNewItem(requestDto, 1L));
    }

    @Test
    void addNewItem_shouldReturnNotNullRequestId() {
        // given
        ItemRequestDto requestDto = getItemRequestDto();
        User owner = getUser("some@mail.ru");
        User user = getUser("some2@mail.ru");
        RequestItem request = getRequest(user);
        Item item = getItem(owner);
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(requestItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemShortDto result = itemService.addNewItem(requestDto, 1L);
        // then
        assertThat(result.getRequestId(), equalTo(request.getId()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestItemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository, requestItemRepository, itemRepository);
    }

    @Test
    void updateItem_shouldThrowItemUpdatingEx() {
        // given
        ItemRequestDto requestDto = getItemRequestDto();
        User owner = getUser("some@mail.ru");
        owner.setId(1L);
        User user = getUser("some2@mail.ru");
        user.setId(2L);
        Item item = getItem(owner);
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        // then
        assertThrows(ItemUpdatingException.class,
                () -> itemService.updateItem(requestDto, 2L));
    }

    @Test
    void getItemById_shouldReturnItemWithNoBookings() {
        // given
        User owner = getUser("some@mail.ru");
        owner.setId(1L);
        User user = getUser("some2@mail.ru");
        user.setId(2L);
        Item item = getItem(owner);
        Booking booking = getBooking(item, user);
        List<Comment> comments = getComments(user, item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findNextBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findLastBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(anyLong()))
                .thenReturn(comments);

        ItemResponseDto result = itemService.getItemById(2L, 1L);

        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findLastBookingByItemId(anyLong(), any());
        verify(bookingRepository, times(1)).findNextBookingByItemId(anyLong(), any());
        verify(commentRepository, times(1)).findAllByItem_IdOrderByCreatedDesc(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemById_shouldReturnItemWithBookings() {
        // given
        User owner = getUser("some@mail.ru");
        owner.setId(1L);
        User user = getUser("some2@mail.ru");
        user.setId(2L);
        Item item = getItem(owner);
        Booking booking = getBooking(item, user);
        List<Comment> comments = getComments(user, item);
        // when
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findNextBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findLastBookingByItemId(anyLong(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItem_IdOrderByCreatedDesc(anyLong()))
                .thenReturn(comments);

        ItemResponseDto result = itemService.getItemById(1L, 1L);
        // then
        assertThat(result.getNextBooking(), notNullValue());
        assertThat(result.getLastBooking(), notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findLastBookingByItemId(anyLong(), any());
        verify(bookingRepository, times(1)).findNextBookingByItemId(anyLong(), any());
        verify(commentRepository, times(1)).findAllByItem_IdOrderByCreatedDesc(anyLong());
        verifyNoMoreInteractions(userRepository, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void getItemsByOwner_shouldReturnItemsWithNotNullBookings() {
        // given
        User owner = getUser("some@mail.ru");
        owner.setId(1L);
        User user = getUser("some2@mail.ru");
        user.setId(2L);
        Item item = getItem(owner);
        List<Item> items = List.of(item);
        Booking firstBook = getBooking(item, user);
        Booking secondBook = getBooking(item, user);
        secondBook.setStartDate(LocalDateTime.now().minusDays(1L));

        List<Booking> bookings = List.of(firstBook, secondBook);
        List<Comment> comments = getComments(user, item);

        // when
        when(itemRepository.findAllByOwner_Id(anyLong(), any()))
                .thenReturn(items);
        when(bookingRepository.findAllByItem_IdIn(anyList()))
                .thenReturn(bookings);
        when(commentRepository.findAllByItemIdIn(anyList()))
                .thenReturn(comments);

        List<ItemResponseDto> result = itemService.getItemsByOwner(1L, 0, 10);
        // then
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("nextBooking", notNullValue()),
                hasProperty("lastBooking", notNullValue())
        )));
        verify(itemRepository, times(1)).findAllByOwner_Id(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItem_IdIn(anyList());
        verify(commentRepository, times(1)).findAllByItemIdIn(anyList());
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);

    }

    @Test
    void getItemsByOwner_shouldReturnItemsWithNullBookings() {
        // given
        User owner = getUser("some@mail.ru");
        owner.setId(1L);
        User user = getUser("some2@mail.ru");
        user.setId(2L);
        Item item = getItem(owner);
        List<Item> items = List.of(item);

        List<Booking> bookings = Collections.emptyList();
        List<Comment> comments = getComments(user, item);

        // when
        when(itemRepository.findAllByOwner_Id(anyLong(), any()))
                .thenReturn(items);
        when(bookingRepository.findAllByItem_IdIn(anyList()))
                .thenReturn(bookings);
        when(commentRepository.findAllByItemIdIn(anyList()))
                .thenReturn(comments);

        List<ItemResponseDto> result = itemService.getItemsByOwner(1L, 0, 10);
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(allOf(
                hasProperty("nextBooking", nullValue()),
                hasProperty("lastBooking", nullValue())
        )));
        verify(itemRepository, times(1)).findAllByOwner_Id(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItem_IdIn(anyList());
        verify(commentRepository, times(1)).findAllByItemIdIn(anyList());
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void search() {
        // given
        GetSearchItem search = GetSearchItem.of("", 1L, 0, 10);
        // when
        List<ItemRequestDto> result = itemService.search(search);
        // then
        assertThat(result, empty());
    }

    @Test
    void searchCommentsByText() {
        User owner = getUser("some@mail.ru");
        owner.setId(1L);
        User user = getUser("some2@mail.ru");
        user.setId(2L);
        Item item = getItem(owner);

        List<Comment> comments = getComments(user, item);
        GetSearchItem search = GetSearchItem.of("not empty search", 1L, 1L, 0, 10);
        when(commentRepository.searchByText(anyLong(), anyString(), any()))
                .thenReturn(comments);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        List<CommentResponseDto> result = itemService.searchCommentsByText(search);

        assertThat(result, not(empty()));
        verify(commentRepository, times(1)).searchByText(anyLong(), anyString(), any());
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(commentRepository, userRepository);
    }

    @Test
    void addComment() {
        User booker = getUser("booker@mail.ru");
        User owner = getUser("some@mail.ru");
        Item item = getItem(owner);
        Booking booking = getBooking(item, booker);
        CommentRequestDto commentDto = getCommentDto(booker, item);
        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndEndDateBefore(anyLong(), anyLong(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Пользователь с id: 1 не брал в аренду вещь с id: 1"));
        assertThrows(ResponseStatusException.class, () -> itemService.addComment(item.getId(), commentDto, 1L));
        verifyNoInteractions(commentRepository);
    }

    private static CommentRequestDto getCommentDto(User booker, Item item) {
        return CommentRequestDto.builder()
                .id(1L)
                .text("some text")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now())
                .build();
    }

    private static ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .name("item")
                .description("some Item")
                .available(true)
                .requestId(1L)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("brush")
                .description("some brush")
                .available(true)
                .owner(owner)
                .build();
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static RequestItem getRequest(User user) {
        return RequestItem.builder()
                .description("some description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    private static Booking getBooking(Item item, User booker) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }

    private static List<Comment> getComments(User user, Item item) {
        return List.of(Comment.builder()
                .id(1L)
                .text("some text")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build());
    }
}
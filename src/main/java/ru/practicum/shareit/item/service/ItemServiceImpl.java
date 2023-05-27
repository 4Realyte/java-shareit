package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ItemResponseDto addNewItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", ownerId)));
        Item item = ItemMapper.dtoToItem(itemRequestDto, owner);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Transactional
    public ItemResponseDto updateItem(ItemRequestDto itemRequestDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", ownerId)));
        Long itemId = itemRequestDto.getId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));

        checkOwner(item, ownerId);

        item.setOwner(owner);
        setAttributes(itemRequestDto, item);

        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    public ItemShortResponseDto getItemById(Long userId, Long itemId) {
        LocalDateTime cur = LocalDateTime.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id: %s не обнаружен", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));

        BookingShortDto nextBooking = bookingRepository.findNextBookingByItemId(itemId, cur)
                .map(BookingMapper::toShortDto).orElse(null);
        BookingShortDto lastBooking = bookingRepository.findLastBookingByItemId(itemId, cur)
                .map(BookingMapper::toShortDto).orElse(null);

        List<CommentResponseDto> comments = CommentMapper.toResponseDto(
                commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId));

        if (user.equals(item.getOwner())) {
            return ItemMapper.toItemShortDto(item, nextBooking, lastBooking, comments);
        } else {
            return ItemMapper.toItemShortDto(item, null, null, comments);
        }
    }

    public List<ItemShortResponseDto> getItemsByOwner(Long ownerId) {
        LocalDateTime cur = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwner_Id(ownerId);
        List<Long> ids = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAllByItem_IdIn(ids);
        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);
        if (!bookings.isEmpty()) {
            return connectBookingsAndComments(bookings, items, comments, cur);
        } else {
            return ItemMapper.toItemShortDto(items);
        }
    }

    private List<ItemShortResponseDto> connectBookingsAndComments(List<Booking> bookings, List<Item> items,
                                                                  List<Comment> comments, LocalDateTime cur) {
        Map<Long, List<Booking>> bookingMap = bookings.stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentMap = comments.stream().collect(Collectors.groupingBy(c -> c.getItem().getId()));
        List<ItemShortResponseDto> result = new ArrayList<>();
        for (Item item : items) {
            BookingShortDto nextBooking = bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isAfter(cur))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.naturalOrder()))
                    .map(BookingMapper::toShortDto)
                    .findFirst().orElse(null);

            BookingShortDto lastBooking = bookingMap.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isBefore(cur))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.reverseOrder()))
                    .map(BookingMapper::toShortDto)
                    .findFirst().orElse(null);
            List<CommentResponseDto> responseComments = CommentMapper.toResponseDto(commentMap.getOrDefault(item.getId(),
                    Collections.emptyList()));

            result.add(ItemMapper.toItemShortDto(item, nextBooking, lastBooking, responseComments));
        }
        return result;
    }

    public List<ItemRequestDto> search(String text, Long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", userId)));
        return ItemMapper.itemToDto(itemRepository.searchItemsByNameOrDescription(text));
    }

    @Override
    public CommentResponseDto addComment(Long itemId, CommentRequestDto dto, Long userId) {
        Booking booking = bookingRepository.findFirstByBooker_IdAndItem_IdAndEndDateBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Пользователь с id: %s не брал в аренду вещь с id: %s", userId, itemId)));

        Comment comment = CommentMapper.dtoToComment(dto, booking.getBooker(), booking.getItem());

        return CommentMapper.toResponseDto(commentRepository.save(comment));
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();
        if (owner == null || owner.getId() != ownerId) {
            throw new ItemUpdatingException(
                    String.format("Пользователь с id: %s не является владельцем вещи %s", ownerId, item.getName()));
        }
    }

    private void setAttributes(ItemRequestDto itemRequestDto, Item item) {
        String name = itemRequestDto.getName();
        String description = itemRequestDto.getDescription();
        Boolean available = itemRequestDto.getAvailable();
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
    }
}

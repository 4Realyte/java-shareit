package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.Item;
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

    @Transactional
    public ItemDto addNewItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", ownerId)));
        Item item = ItemMapper.dtoToItem(itemDto, owner);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", ownerId)));
        Long itemId = itemDto.getId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));

        checkOwner(item, ownerId);

        item.setOwner(owner);
        setAttributes(itemDto, item);

        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    public ItemShortResponseDto getItemById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id: %s не обнаружен", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", itemId)));
        LocalDateTime cur = LocalDateTime.now();
        BookingShortDto nextBooking = bookingRepository.findNextBookingByItemId(itemId)
                .map(BookingMapper::toShortDto).orElse(null);
        BookingShortDto lastBooking = bookingRepository.findLastBookingByItemId(itemId)
                .map(BookingMapper::toShortDto).orElse(null);
        return ItemMapper.toItemShortDto(item, nextBooking, lastBooking);
    }

    public List<ItemResponseDto> getItemsByOwner(Long ownerId) {
        LocalDateTime cur = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwner_Id(ownerId);
        List<Long> ids = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAllByItem_IdIn(ids);
        if (!bookings.isEmpty()) {
            return getItemsWithBooking(bookings, items, cur);
        } else {
            return ItemMapper.toItemResponseDto(items);
        }
    }

    private List<ItemResponseDto> getItemsWithBooking(List<Booking> bookings, List<Item> items, LocalDateTime cur) {
        Map<Long, List<Booking>> map = bookings.stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        LocalDateTime current = LocalDateTime.now();

        List<ItemResponseDto> result = new ArrayList<>();
        for (Item item : items) {
            Booking nextBooking = map.getOrDefault(item.getId(),Collections.emptyList()).stream()
                    .filter(b -> b.getStartDate().isAfter(cur))
                    .sorted(Comparator.comparing(Booking::getStartDate, Comparator.naturalOrder()))
                    .findFirst().orElse(null);

            Booking lastBooking = map.getOrDefault(item.getId(), Collections.emptyList()).stream()
                    .filter(b -> b.getEndDate().isBefore(cur))
                    .sorted(Comparator.comparing(Booking::getEndDate, Comparator.reverseOrder()))
                    .findFirst().orElse(null);

            result.add(ItemMapper.toItemResponseDto(item, nextBooking, lastBooking));
        }
        return result;
    }

    public List<ItemDto> search(String text, Long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", userId)));
        return ItemMapper.itemToDto(itemRepository.searchItemsByNameOrDescription(text));
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();
        if (owner == null || owner.getId() != ownerId) {
            throw new ItemUpdatingException(
                    String.format("Пользователь с id: %s не является владельцем вещи %s", ownerId, item.getName()));
        }
    }

    private void setAttributes(ItemDto itemDto, Item item) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
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

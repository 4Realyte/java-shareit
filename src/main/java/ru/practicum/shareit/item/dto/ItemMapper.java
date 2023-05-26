package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;


public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemDto> itemToDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(itemToDto(item));
        }
        return dtos;
    }

    public static ItemResponseDto toItemResponseDto(Item item, Booking next, Booking last) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(next)
                .lastBooking(last)
                .build();
    }

    public static List<ItemResponseDto> toItemResponseDto(List<Item> items) {
        List<ItemResponseDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemResponseDto(item, null, null));
        }
        return dtos;
    }

    public static Item dtoToItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }
}

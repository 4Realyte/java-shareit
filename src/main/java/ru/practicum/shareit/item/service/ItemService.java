package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text, Long userId);
}

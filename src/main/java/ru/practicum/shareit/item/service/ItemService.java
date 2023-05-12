package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text, Long userId);
}

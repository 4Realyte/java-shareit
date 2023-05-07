package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addNewItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(Map<String, Object> updates, Long ownerId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> search(String text, Long userId);
}

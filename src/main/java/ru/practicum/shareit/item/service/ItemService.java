package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponseDto addNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    ItemResponseDto updateItem(ItemRequestDto itemRequestDto, Long ownerId);

    ItemShortResponseDto getItemById(Long userId, Long itemId);

    List<ItemShortResponseDto> getItemsByOwner(Long ownerId);

    List<ItemRequestDto> search(String text, Long userId);

    CommentResponseDto addComment(Long itemId, CommentRequestDto dto, Long userId);
}

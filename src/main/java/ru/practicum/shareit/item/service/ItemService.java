package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemShortDto addNewItem(ItemRequestDto itemRequestDto, Long ownerId);

    ItemShortDto updateItem(ItemRequestDto itemRequestDto, Long ownerId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId, int from, int size);

    List<ItemRequestDto> search(GetSearchItem search);

    List<CommentResponseDto> searchCommentsByText(GetSearchItem search);

    CommentResponseDto addComment(Long itemId, CommentRequestDto dto, Long userId);
}

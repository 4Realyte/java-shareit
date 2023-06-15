package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;

import java.util.List;

public interface RequestItemService {
    RequestItemDto addNewRequest(RequestItemDto request, Long userId);

    List<RequestItemResponseDto> getRequests(Long userId);

    List<RequestItemResponseDto> getAllRequests(Long userId, int from, int size);

    RequestItemResponseDto getRequestById(Long userId, Long requestId);
}

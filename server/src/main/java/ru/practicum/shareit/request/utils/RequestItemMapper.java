package ru.practicum.shareit.request.utils;

import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestItemMapper {
    public static RequestItemDto toRequestItemDto(RequestItem request) {
        return RequestItemDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static RequestItem dtoToRequest(RequestItemDto dto, User requestor) {
        return RequestItem.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    public static RequestItemResponseDto toResponseDto(RequestItem request) {
        return RequestItemResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(ItemMapper.toItemShort(request.getItems()))
                .build();

    }

    public static List<RequestItemResponseDto> toResponseDto(List<RequestItem> requests) {
        List<RequestItemResponseDto> dtos = new ArrayList<>();
        for (RequestItem request : requests) {
            dtos.add(toResponseDto(request));
        }
        return dtos;
    }
}

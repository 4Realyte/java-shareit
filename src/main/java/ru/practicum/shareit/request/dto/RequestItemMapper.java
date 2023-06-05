package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
                .build();
    }

    public static RequestItemResponseDto toResponseDto(RequestItem request, List<ItemAnswerDto> answers) {
        return RequestItemResponseDto.builder()
                .description(request.getDescription())
                .created(request.getCreated())
                .answers(answers)
                .build();

    }
}

package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemAnswerDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestItemResponseDto {
    private String description;
    private LocalDateTime created;
    private List<ItemAnswerDto> answers;
}

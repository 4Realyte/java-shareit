package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class CommentRequestDto {
    private Long id;
    private String text;
    private Item item;
    private User author;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}

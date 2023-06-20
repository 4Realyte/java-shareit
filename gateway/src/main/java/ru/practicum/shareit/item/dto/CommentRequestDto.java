package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class CommentRequestDto {
    @Null
    private Long id;
    @NotBlank
    private String text;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}

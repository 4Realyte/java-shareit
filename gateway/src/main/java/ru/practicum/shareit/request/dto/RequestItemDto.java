package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestItemDto {
    private Long id;
    @NotBlank
    private String description;
    @PastOrPresent
    private LocalDateTime created;
}

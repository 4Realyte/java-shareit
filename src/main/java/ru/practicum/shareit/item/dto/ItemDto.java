package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "Имя вещи не может быть пустым")
    private String name;
    @NotBlank(message = "Поле описания не должно быть пустым")
    private String description;
    @NotNull(message = "Поле доступность к аренде должно присутствовать")
    private Boolean available;
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemDto addItem(@RequestBody @Validated(ItemDto.NewItem.class) ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemServiceImpl.addNewItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@Validated(ItemDto.UpdateItem.class) @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id")
    @NotNull Long ownerId, @PathVariable("id") Long itemId) {
        itemDto.setId(itemId);
        return itemServiceImpl.updateItem(itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId, @PathVariable("id") Long itemId) {
        return itemServiceImpl.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.search(text, userId);
    }
}

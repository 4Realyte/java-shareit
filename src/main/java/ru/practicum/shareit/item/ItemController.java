package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemServiceImpl.addNewItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody Map<String, Object> updates, @RequestHeader("X-Sharer-User-Id")
    @NotNull Long ownerId, @PathVariable("id") Long itemId) {
        updates.put("id", itemId);
        return itemServiceImpl.updateItem(updates, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId, @PathVariable("id") Long itemId) {
        return itemServiceImpl.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.search(text, userId);
    }
}

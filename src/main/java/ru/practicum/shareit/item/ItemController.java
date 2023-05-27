package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public ItemResponseDto addItem(@RequestBody @Validated(ItemRequestDto.NewItem.class) ItemRequestDto itemRequestDto,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemServiceImpl.addNewItem(itemRequestDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemResponseDto updateItem(@Validated(ItemRequestDto.UpdateItem.class) @RequestBody ItemRequestDto itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                      @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemServiceImpl.updateItem(itemRequestDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemShortResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                            @PathVariable("id") Long itemId) {
        return itemServiceImpl.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemShortResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable Long itemId,
                                         @RequestBody @Valid CommentRequestDto dto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.addComment(itemId, dto, userId);

    }
}

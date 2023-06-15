package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemShortDto addItem(@RequestBody @Validated(ItemRequestDto.NewItem.class) ItemRequestDto itemRequestDto,
                                @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemServiceImpl.addNewItem(itemRequestDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemShortDto updateItem(@Validated(ItemRequestDto.UpdateItem.class) @RequestBody ItemRequestDto itemRequestDto,
                                   @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                                   @PathVariable("id") Long itemId) {
        itemRequestDto.setId(itemId);
        return itemServiceImpl.updateItem(itemRequestDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @PathVariable("id") Long itemId) {
        return itemServiceImpl.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemServiceImpl.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam String text,
                                       @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemServiceImpl.search(GetSearchItem.of(text, userId, from, size));
    }

    @GetMapping("/{itemId}/comment/search")
    public List<CommentResponseDto> searchCommentsByText(@PathVariable Long itemId,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                         @RequestParam @NotBlank String text,
                                                         @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        return itemServiceImpl.searchCommentsByText(GetSearchItem.of(text, userId, itemId, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable Long itemId,
                                         @RequestBody @Valid CommentRequestDto dto,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemServiceImpl.addComment(itemId, dto, userId);
    }
}

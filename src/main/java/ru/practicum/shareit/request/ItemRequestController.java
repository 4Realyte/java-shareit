package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestItemService service;

    @PostMapping
    public RequestItemDto addNewRequest(@Valid @RequestBody RequestItemDto request,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return service.addNewRequest(request, ownerId);
    }

    @GetMapping
    public List<RequestItemResponseDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return service.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                       @Valid @RequestParam(defaultValue = "0") @Positive int from,
                                                       @RequestParam(defaultValue = "1") int size) {
        return service.getAllRequests(userId, from, size);
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestItemService service;

    @PostMapping
    public RequestItemDto addNewRequest(@RequestBody RequestItemDto request,
                                        @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return service.addNewRequest(request, ownerId);
    }

    @GetMapping
    public List<RequestItemResponseDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return service.getRequests(userId);
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.service.RequestItemService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestItemService service;

    @PostMapping
    public RequestItemDto addNewRequest(@RequestBody RequestItemDto request,
                                        @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return service.addNewRequest(request, ownerId);
    }

    @GetMapping
    public List<RequestItemResponseDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestItemResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return service.getRequestById(userId, requestId);
    }
}

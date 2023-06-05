package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dao.RequestItemRepository;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemMapper;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestItemService {
    private final RequestItemRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public RequestItemDto addNewRequest(RequestItemDto request, Long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", userId)));
        RequestItem requestItem = RequestItemMapper.dtoToRequest(request, requestor);
        return RequestItemMapper.toRequestItemDto(repository.save(requestItem));
    }

    @Transactional
    public List<RequestItemResponseDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }
        List<RequestItem> requests = repository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream().map(RequestItem::getId).collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        return makeRequestForResponse(requests, items);
    }

    private List<RequestItemResponseDto> makeRequestForResponse(List<RequestItem> requests, List<Item> items) {
        Map<Long, List<Item>> itemMap = items.stream().collect(Collectors.groupingBy(i -> i.getRequest().getId()));
        List<RequestItemResponseDto> result = new ArrayList<>();

        for (RequestItem request : requests) {
            List<ItemAnswerDto> itemAnswers = itemMap.getOrDefault(request.getId(), Collections.emptyList())
                    .stream()
                    .map(ItemMapper::toAnswerDto)
                    .collect(Collectors.toList());
            result.add(RequestItemMapper.toResponseDto(request, itemAnswers));
        }
        return result;
    }
}

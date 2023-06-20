package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dao.RequestItemRepository;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.utils.RequestItemMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestItemServiceImpl implements RequestItemService {
    private final RequestItemRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public RequestItemDto addNewRequest(RequestItemDto request, Long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id: %s не обнаружен", userId)));
        RequestItem requestItem = RequestItemMapper.dtoToRequest(request, requestor);
        return RequestItemMapper.toRequestItemDto(repository.save(requestItem));
    }


    public List<RequestItemResponseDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }
        List<RequestItem> requests = repository.findAllByRequestorId(userId);

        return RequestItemMapper.toResponseDto(requests);
    }

    public List<RequestItemResponseDto> getAllRequests(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }
        return repository.findAllPaged(PageRequest.of(from > 0 ? from / size : 0, size,
                        Sort.by(Sort.Direction.DESC, "created")), userId)
                .map(RequestItemMapper::toResponseDto)
                .getContent();
    }

    public RequestItemResponseDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", userId));
        }
        return RequestItemMapper.toResponseDto(repository.findById(requestId).orElseThrow(() ->
                new RequestNotFoundException(String.format("Запрос с id: %s не обнаружен", requestId))));
    }
}

package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemUpdatingException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    public ItemDto addNewItem(ItemDto itemDto, Long ownerId) {
        User owner = userDao.findById(ownerId);
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.itemToDto(itemDao.save(item));
    }

    public ItemDto updateItem(Map<String, Object> updates, Long ownerId) {
        User owner = userDao.findById(ownerId);
        Long itemId = (Long) updates.get("id");
        Item item = itemDao.findById(itemId);

        checkOwner(item, ownerId);

        item.setOwner(owner);
        setAttributes(updates, item);

        return ItemMapper.itemToDto(itemDao.update(item));
    }

    public ItemDto getItemById(Long userId, Long itemId) {
        userDao.findById(userId);
        return ItemMapper.itemToDto(itemDao.findById(itemId));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemDao.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> search(String text, Long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String textToSearch = text.toLowerCase().trim();
        userDao.findById(userId);
        return itemDao.findAll().stream()
                .filter(item -> item.getAvailable() == true)
                .filter(item -> item.getName().toLowerCase().contains(textToSearch)
                        || item.getDescription().toLowerCase().contains(textToSearch))
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(Item item, Long ownerId) {
        User owner = item.getOwner();
        if (owner == null || owner.getId() != ownerId) {
            throw new ItemUpdatingException(
                    String.format("Пользователь с id: %s не является владельцем вещи %s", ownerId, item.getName()));
        }
    }

    private void setAttributes(Map<String, Object> updates, Item item) {
        String name = (String) updates.get("name");
        String description = (String) updates.get("description");
        Boolean available = (Boolean) updates.get("available");
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
    }
}

package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger();

    public Item save(Item item) {
        item.setId(counter.incrementAndGet());
        items.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        Item item = items.get(id);
        if (item == null) throw new ItemNotFoundException(String.format("Вещь с id: %s не обнаружена", id));
        return item;
    }

    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

}

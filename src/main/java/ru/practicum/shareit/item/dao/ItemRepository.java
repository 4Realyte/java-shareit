package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(Long ownerId);

    @Query("select i from Item as i " +
            "where i.name LIKE concat('%',?1, '%') " +
            "or i.description like concat('%',?1, '%')")
    List<Item> searchItemsByNameOrDescription(String text);
}

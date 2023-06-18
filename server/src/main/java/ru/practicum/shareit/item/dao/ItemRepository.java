package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long ownerId, Pageable page);

    @Query("select i from Item as i " +
            "where (LOWER(i.name) LIKE LOWER(concat('%',?1, '%')) " +
            "or LOWER(i.description) LIKE LOWER(concat('%',?1, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByNameOrDescription(String text, Pageable page);
}

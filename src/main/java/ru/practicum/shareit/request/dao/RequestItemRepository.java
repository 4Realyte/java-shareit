package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long>, QuerydslPredicateExecutor<RequestItem> {
    List<RequestItem> findAllByRequestorIdOrderByCreatedDesc(Long userId);
}

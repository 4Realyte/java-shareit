package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long>, QuerydslPredicateExecutor<RequestItem> {
    @Query("select req from RequestItem as req " +
            "JOIN FETCH req.requestor as u " +
            "WHERE u.id = :id " +
            "order by req.created DESC")
    List<RequestItem> findAllByRequestorId(@Param("id") Long requestorId);

    @Query("select req from RequestItem as req " +
            "WHERE req.requestor.id != :userId")
    @EntityGraph(attributePaths = "items")
    Page<RequestItem> findAllPaged(Pageable page, @Param("userId") Long userId);
}

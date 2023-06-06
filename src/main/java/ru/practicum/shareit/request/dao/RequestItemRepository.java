package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long>, QuerydslPredicateExecutor<RequestItem> {
    List<RequestItem> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    @Query("select req from RequestItem as req " +
            "JOIN FETCH req.items as i " +
            "WHERE req.requestor.id = ?1 " +
            "order by req.created DESC")
    List<RequestItem> findAllByRequestorId(Long requestorId);

    /*@Query("select req from RequestItem as req " +
            "JOIN FETCH req.items as i")
    Page<RequestItem> findAllPaged(Pageable page);*/
}

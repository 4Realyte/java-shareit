package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByItem_IdOrderByCreatedDesc(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> ids);

    @Query("select c from Comment as c " +
            "JOIN c.item as i " +
            "where i.id=?1 AND LOWER(c.text) LIKE LOWER(concat('%',?2,'%')) " +
            "order by c.created DESC")
    List<Comment> searchByText(Long itemId, String text, Pageable page);
}

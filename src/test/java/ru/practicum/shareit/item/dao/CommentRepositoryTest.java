package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final TestEntityManager em;
    private final CommentRepository repository;

    @Test
    void searchByText() {
        // given
        User owner = getUser("alex@mail.ru");
        User author = getUser("alexas@mail.ru");
        em.persist(owner);
        em.persist(author);
        Item item = getItem(owner);
        em.persist(item);
        Comment comment = getComment(author, item);
        em.persist(comment);
        Pageable page = PageRequest.of(0, 10);

        // when
        List<Comment> comments = repository.searchByText(item.getId(), "good item", page);
        // then
        assertThat(comments, hasSize(1));
        assertThat(comments, hasItem(comment));
    }

    private static Comment getComment(User author, Item item) {
        return Comment.builder()
                .text("very good item")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .name("brush")
                .description("some brush")
                .available(true)
                .owner(owner)
                .build();
    }
}
package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class RequestItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private RequestItemRepository repository;

    @Test
    void findAllByRequestorId_shouldReturnRequests_whenUserIdIsCorrect() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alex@yandex.ru");

        em.persist(userOne);
        em.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        em.persist(requestOne);
        // when
        List<RequestItem> requests = repository.findAllByRequestorId(userOne.getId());
        // then
        assertThat(requests, hasSize(1));
        assertThat(requests, hasItem(allOf(
                hasProperty("description", containsString("some description")),
                hasProperty("id", equalTo(requestOne.getId())),
                hasProperty("requestor", equalTo(userOne)),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findAllByRequestorId_shouldReturnEmptyResult_whenUserIdIsInCorrect() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alex@yandex.ru");

        em.persist(userOne);
        em.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        em.persist(requestOne);
        Long incorrectId = 1000L;
        // when
        List<RequestItem> requests = repository.findAllByRequestorId(incorrectId);
        // then
        assertThat(requests, empty());
    }

    @Test
    void findAllPaged_shouldReturnRequests_whenUserIsNotRequestor() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alex@yandex.ru");

        em.persist(userOne);
        em.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        em.persist(requestOne);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<RequestItem> requestsTwo = repository.findAllPaged(page, userTwo.getId()).getContent();
        // then
        assertThat(requestsTwo, hasSize(1));
        assertThat(requestsTwo, hasItem(allOf(
                hasProperty("description", containsString("some description")),
                hasProperty("id", equalTo(requestOne.getId())),
                hasProperty("requestor", equalTo(userOne)),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void findAllPaged_shouldReturnEmptyList_whenUserIsRequestor() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alex@yandex.ru");

        em.persist(userOne);
        em.persist(userTwo);

        RequestItem requestOne = getRequest(userOne);
        em.persist(requestOne);
        Pageable page = PageRequest.of(0, 10);
        // when
        List<RequestItem> requests = repository.findAllPaged(page, userOne.getId()).getContent();
        // then
        assertThat(requests, empty());
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }

    private static RequestItem getRequest(User user) {
        return RequestItem.builder()
                .description("some description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }
}
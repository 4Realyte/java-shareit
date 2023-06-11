package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final TestEntityManager em;

    @Test
    void deleteUser() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);
        EntityManager manager = em.getEntityManager();
        // when
        userRepository.deleteById(userOne.getId());
        List<User> resultList = manager.createQuery("SELECT u FROM User as u").getResultList();
        // then
        assertThat(resultList, hasSize(1));
        assertThat(resultList, hasItem(allOf(
                hasProperty("id", equalTo(userTwo.getId())),
                hasProperty("name", equalTo(userTwo.getName()))
        )));
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }
}
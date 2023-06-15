package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.model.QUser;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;
    private final TestEntityManager em;

    @Test
    void deleteUser_shouldDeleteUser_whenIdIsCorrect() {
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

    @Test
    void deleteUser_shouldThrowDataAccessException_whenIdIsInCorrect() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);
        EntityManager manager = em.getEntityManager();
        List<User> resultList = manager.createQuery("SELECT u FROM User as u").getResultList();
        // when + then
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> userRepository.deleteById(100L));
        assertThat(resultList, hasSize(2));
    }

    @Test
    void saveUser_QUserTest() {
        // given
        User userOne = getUser("alex@mail.ru");
        userRepository.save(userOne);
        // when
        List<User> result = userRepository.findAll(QUser.user.id.eq(userOne.getId()), Pageable.unpaged()).getContent();
        assertThat(result, not(empty()));
        assertThat(result.get(0).getId(), equalTo(userOne.getId()));
    }

    @Test
    void saveUser_withDuplicateEmailShouldThrowEx() {
        // given
        User userOne = getUser("alex@mail.ru");
        userRepository.save(userOne);
        User userTwo = getUser("alex@mail.ru");
        // when + then
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> userRepository.save(userTwo));
    }

    @Test
    void findById_shouldReturnNullWhenUserNotFound() {
        // given
        User userOne = getUser("alex@mail.ru");
        User savedUser = userRepository.save(userOne);
        Long userId = savedUser.getId();
        // when
        Optional<User> result = userRepository.findById(++userId);
        // then
        assertTrue(result.isEmpty());
    }

    private static User getUser(String email) {
        return User.builder()
                .name("Alexandr")
                .email(email)
                .build();
    }
}
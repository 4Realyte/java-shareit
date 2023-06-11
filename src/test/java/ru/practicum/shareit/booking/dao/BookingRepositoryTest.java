package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;

    @Test
    void findBooking() {
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);

        Booking result = repository.findBooking(booking.getId(), userTwo.getId()).get();

        assertThat(result, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("item", notNullValue()),
                hasProperty("booker", equalTo(userTwo))
        ));
    }

    @Test
    void findBookingByOwner_whenNotOwner() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);
        // when
        Optional<Booking> result = repository.findBookingByOwner(booking.getId(), userTwo.getId());
        // then
        assertThat(result, equalTo(Optional.empty()));
    }

    @Test
    void findBookingByOwner_whenOwner() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);

        // when
        Optional<Booking> result = repository.findBookingByOwner(booking.getId(), userOne.getId());

        // then
        assertThat(result, not(equalTo(Optional.empty())));
    }

    @Test
    void findLastBookingByItemId() {
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setStartDate(LocalDateTime.now().minusDays(1L));
        em.persist(booking);

        Booking nextBooking = repository.findLastBookingByItemId(item.getId(), LocalDateTime.now()).get();

        assertThat(nextBooking, allOf(
                hasProperty("id", equalTo(booking.getId()))
        ));

    }

    @Test
    void findNextBookingByItemId() {
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);

        Booking nextBooking = repository.findNextBookingByItemId(item.getId(), LocalDateTime.now()).get();

        assertThat(nextBooking, allOf(
                hasProperty("id", equalTo(booking.getId()))
        ));
    }

    @Test
    void findFirstByBooker_IdAndItem_IdAndEndDateBefore() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setEndDate(now.minusHours(1));
        em.persist(booking);
        // when
        Booking result = repository.findFirstByBooker_IdAndItem_IdAndEndDateBefore(userTwo.getId(), item.getId(), now)
                .orElse(null);
        // then
        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("startDate", notNullValue()),
                hasProperty("endDate", notNullValue()),
                hasProperty("booker", equalTo(userTwo)),
                hasProperty("item", equalTo(item)),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        ));
    }

    @Test
    void findAllByItem_IdIn() {
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);

        List<Booking> bookings = repository.findAllByItem_IdIn(List.of(item.getId()));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(booking));
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

    private static Booking getBooking(Item item, User booker) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }
}
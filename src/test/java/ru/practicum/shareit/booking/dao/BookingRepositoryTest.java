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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;

    @Test
    void findBooking_shouldReturnNotNullBooking() {
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);

        Booking result = repository.findBooking(booking.getId(), userTwo.getId()).orElse(null);

        assertThat(result, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("item", notNullValue()),
                hasProperty("booker", equalTo(userTwo))
        ));
    }

    @Test
    void findBooking_shouldReturnEmptyResult_whenBookingNotFound() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        em.persist(booking);

        Long resultId = booking.getId();
        // when
        Optional<Booking> result = repository.findBooking(++resultId, userTwo.getId());
        // then
        assertTrue(result.isEmpty());
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
    void findBookingByOwner_shouldReturnEmptyResult_WhenOwnerIdIncorrect() {
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
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingByOwner_shouldReturnEmptyResult_WhenBookingIdIncorrect() {
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
        Optional<Booking> result = repository.findBookingByOwner(100L, userOne.getId());
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findLastBookingByItemId_shouldReturnLastBooking_whenItemIdIsCorrect() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setStartDate(LocalDateTime.now().minusDays(1L));
        em.persist(booking);
        // when
        Booking nextBooking = repository.findLastBookingByItemId(item.getId(), LocalDateTime.now()).get();
        // then
        assertThat(nextBooking, allOf(
                hasProperty("id", equalTo(booking.getId()))
        ));
    }

    @Test
    void findLastBookingByItemId_shouldReturnEmptyResult_whenItemIdIsIncorrect() {
        // given
        User userOne = getUser("alex@mail.ru");
        User userTwo = getUser("alexa@mail.ru");
        em.persist(userOne);
        em.persist(userTwo);

        Item item = getItem(userOne);
        em.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setStartDate(LocalDateTime.now().minusDays(1L));
        em.persist(booking);
        // when
        Optional<Booking> nextBooking = repository.findLastBookingByItemId(100L, LocalDateTime.now());
        // then
        assertTrue(nextBooking.isEmpty());
    }

    @Test
    void findNextBookingByItemId_shouldReturnFutureBooking_whenItemIdIsCorrect() {
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
    void findNextBookingByItemId_shouldReturnEmptyResult_whenItemIdIsInCorrect() {
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
        Optional<Booking> nextBooking = repository.findNextBookingByItemId(100L, LocalDateTime.now());
        // then
        assertTrue(nextBooking.isEmpty());
    }

    @Test
    void findFirstByBooker_IdAndItem_IdAndEndDateBefore_shouldReturnBooking() {
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
    void findFirstByBooker_IdAndItem_IdAndEndDateBefore_shouldReturnEmptyResult_whenItemIdIncorrect() {
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
        Optional<Booking> result = repository.findFirstByBooker_IdAndItem_IdAndEndDateBefore(userTwo.getId(), 100L, now);
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItem_IdIn_shouldReturnBookings() {
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
        List<Booking> bookings = repository.findAllByItem_IdIn(List.of(item.getId()));
        // then
        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(booking));
    }

    @Test
    void findAllByItem_IdIn_shouldReturnEmptyList_WhenBookingsNotFound() {
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
        List<Booking> bookings = repository.findAllByItem_IdIn(Collections.emptyList());
        // then
        assertThat(bookings, empty());
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
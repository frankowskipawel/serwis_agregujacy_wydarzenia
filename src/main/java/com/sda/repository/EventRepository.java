package com.sda.repository;

import com.sda.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    Page<Event> findAllByTitleContaining(Pageable pageable, String query); //all

    Page<Event> findAllByTitleContainingAndStartDateAfter(Pageable pageable, String query, Date date); //future

    Page<Event> findAllByTitleContainingAndEndDateBefore(Pageable pageable, String query, Date date); //past

    Page<Event> findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(Pageable pageable, String query, Date startDate, Date endDate); //ongoing

    Page<Event> findAllByUser(Pageable pageable, String query);

    default Page<Event> findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(Pageable pageable, String query, Date date) {
        return findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(pageable,query, date, date);
    }
}

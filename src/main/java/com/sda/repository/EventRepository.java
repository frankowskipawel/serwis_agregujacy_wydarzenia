package com.sda.repository;

import com.sda.entity.Event;
import com.sda.modelAPI.EventAPI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    Page<Event> findAllByTitleContaining(Pageable pageable, String query); //all



    Page<Event> findAllByTitleContainingAndStartDateAfter(Pageable pageable, String query, Date date); //future

    Page<Event> findAllByTitleContainingAndStartDateBefore(Pageable pageable, String query, Date date); //past
    Page<Event> findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(Pageable pageable, String query, Date startDate, Date endDate); //ongoing

    default Page<Event> findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(Pageable pageable, String query, Date date) {
        return findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(pageable,query, date, date);
    }

    List<Event> findAllByTitleContainingAndStartDateBefore(String query, Date date); //past

    List<Event> findByStartDateAfter(Date date); //future api
    List<Event> findAllByStartDateBefore(Date date); //past
    List<Event> findAllByStartDateBeforeAndEndDateAfter(Date startDate, Date endDate); //ongoing

}

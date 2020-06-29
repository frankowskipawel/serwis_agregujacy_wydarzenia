package com.sda.repository;

import com.sda.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    Page<Event> findAllByTitleContaining(Pageable pageable, String query);

//    Page<Event> findAllByTitleContainingAndAndStartDateAndEndTimeBefore(Pageable pageable, String query, LocalDate date, LocalTime time);

//    Page<Event> findAllByTitleContainingAndEndDateAfter(Pageable pageable, String query, LocalDate date, LocalTime time);
}

package com.sda.service;

import com.sda.model.Event;
import com.sda.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public void createEvent(Event event){
      eventRepository.save(event);
    }

    public List<Event> findAll(){
        return eventRepository.findAll();
    }

    public Page<Event> findAllPagination(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Page<Event> findAllBySearchQueryPagination(Pageable pageable, String query) {
        return eventRepository.findAllByTitleContaining(pageable, query);
    }

//    public Page<Event> findAllByTitleContainingAndAndStartDateAndEndTimeBefore(Pageable pageable, String query, LocalDate date, LocalTime time) {
//        return eventRepository.findAllByTitleContainingAndAndStartDateAndEndTimeBefore(pageable, query, date, time);
//    }

//    public Page<Event> findAllByTitleContainingAndEndDateAfter(Pageable pageable, String query, LocalDate date, LocalTime time) {
//        return eventRepository.findAllByTitleContainingAndEndDateAfter(pageable, query, date, time);
//    }

    public Optional<Event> findById(int id){
        return eventRepository.findById(id);
    }
}

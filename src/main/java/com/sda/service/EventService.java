package com.sda.service;

import com.sda.entity.Event;
import com.sda.modelAPI.EventAPI;
import com.sda.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public Page<Event> findAllFutureEvent(Pageable pageable, String query, Date date) {
        return eventRepository.findAllByTitleContainingAndStartDateAfter(pageable, query, date);
    }

    public Page<Event> findAllPastEvent(Pageable pageable, String query, Date date) {
        return eventRepository.findAllByTitleContainingAndEndDateBefore(pageable, query, date);
    }

    public List<EventAPI> findAllPastEvent(String query, Date date) {
        return eventRepository.findAllByTitleContainingAndEndDateBefore(query, date);
    }

    public Page<Event> findAllOngoingEvent(Pageable pageable, String query, Date date) {
        return eventRepository.findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(pageable, query, date);
    }

    public Optional<Event> findById(int id){
        return eventRepository.findById(id);
    }


}

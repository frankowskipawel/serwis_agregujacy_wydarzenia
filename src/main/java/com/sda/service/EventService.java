package com.sda.service;

import com.sda.model.Event;
import com.sda.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Optional<Event> findById(int id){
        return eventRepository.findById(id);
    }
}

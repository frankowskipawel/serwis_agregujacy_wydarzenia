package com.sda.service;

import com.sda.model.Event;
import com.sda.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public void createEvent(Event event){

      eventRepository.save(event);
    }
}

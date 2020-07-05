package com.sda.controller;

import com.google.gson.Gson;
import com.sda.entity.Event;
import com.sda.modelAPI.EventAPI;
import com.sda.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private EventService eventService;


    @GetMapping("/events/all")
    public String getEvents() {
        Gson gson = new Gson();
        List<Event> events = eventService.findAll();
        List<EventAPI> eventsAPI = getApiModelEvent(events);

        return gson.toJson(eventsAPI);
    }

    @GetMapping("/events/future")
    public String getFutireEvents() {
        Gson gson = new Gson();
        Date date = new Date();
        List<Event> events = eventService.findFututeEvents(date);
        List<EventAPI> eventsAPI = getApiModelEvent(events);

        return gson.toJson(eventsAPI);
    }

    @GetMapping("/events/past")
    public String getPastEvents() {
        Gson gson = new Gson();
        Date date = new Date();
        List<Event> events = eventService.findPastEvents(date);
        List<EventAPI> eventsAPI = getApiModelEvent(events);

        return gson.toJson(eventsAPI);
    }

    @GetMapping("/events/ongoing")
    public String getOngoingEvents() {
        Gson gson = new Gson();
        Date date = new Date();
        List<Event> events = eventService.findOngoingEvents(date);
        List<EventAPI> eventsAPI = getApiModelEvent(events);

        return gson.toJson(eventsAPI);
    }

    @GetMapping("/events/{id}")
    public String getEventById(@PathVariable("id") int id) {
        Gson gson = new Gson();
        Date date = new Date();
        Event event = eventService.findById(id).get();
        List<Event> events = new ArrayList<>();
        events.add(event);
        List<EventAPI> eventsAPI = getApiModelEvent(events);

        return gson.toJson(eventsAPI.get(0));
    }


    public List<EventAPI> getApiModelEvent(List<Event> events){
        List<EventAPI> eventsAPI = new ArrayList<>();
        for (Event event : events) {
            EventAPI eventAPI = new EventAPI();
            eventAPI.setTitle(event.getTitle());
            eventAPI.setDescription(event.getDescription());
            eventAPI.setCity(event.getCity());
            eventAPI.setId(event.getId());
            eventAPI.setStartDate(event.getStartDate().toString());
            eventAPI.setEndDate(event.getEndDate().toString());
            eventAPI.setPicture(event.getPicture().getFileName());
            eventAPI.setUser(event.getUser().getFirstName() + " " + event.getUser().getLastName());
            eventsAPI.add(eventAPI);
        }
        return eventsAPI;
    }
}

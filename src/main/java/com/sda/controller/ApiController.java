package com.sda.controller;

import com.google.gson.Gson;
import com.sda.entity.Event;
import com.sda.modelAPI.EventAPI;
import com.sda.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private EventService eventService;


    @GetMapping("/events")
    public String getEvents(){
        Gson gson = new Gson();
        List <Event> events = eventService.findAll();
        List<EventAPI> eventsAPI = new ArrayList<>();
        for (Event event : events) {
            EventAPI eventAPI = new EventAPI();
            eventAPI.setTitle(event.getTitle());
            eventAPI.setDescription(event.getDescription());
            eventAPI.setCity(event.getCity());
            eventAPI.setId(event.getId());
            eventAPI.setStartDate(event.getStartDate());
            eventAPI.setEndDate(event.getEndDate());
            eventAPI.setPicture(event.getPicture().getFileName());
            eventAPI.setUser(event.getUser().getFirstName()+" "+event.getUser().getLastName());
            eventsAPI.add(eventAPI);
        }

        return gson.toJson(eventsAPI);
    }
}

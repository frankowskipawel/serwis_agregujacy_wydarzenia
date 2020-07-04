package com.sda.controller;

import com.sda.entity.Event;
import com.sda.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private EventService eventService;


    @GetMapping("/events")
    public List<Event> getEvents(){

        return eventService.findAll();
    }
}

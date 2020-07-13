package com.sda.controller;

import com.google.gson.Gson;
import com.sda.entity.Event;
import com.sda.entity.User;
import com.sda.modelAPI.EventAPI;
import com.sda.service.EventService;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();



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

    @RequestMapping(value = "/events/update", method = RequestMethod.PUT)
    public EventAPI updateEvent(
            @RequestHeader("id") String id,
            @RequestHeader(value = "title", required = false) String title,
            @RequestHeader(value = "city", required = false) String city,
            @RequestHeader(value = "startDate", required = false) String startDate,
            @RequestHeader(value = "endDate", required = false) String endDate,
            @RequestHeader(value = "description", required = false) String description,
            @RequestHeader(value = "yourEmail", required = false) String email,
            @RequestHeader(value = "yourPassword", required = false) String password

    ) throws ParseException {
        User user = userService.findUsersByEmail(email);
        Event event = eventService.findById(Integer.parseInt(id)).get();
        Boolean isCorrectPassword = bCryptPasswordEncoder.matches(password, user.getPassword());
        Boolean isYourEvent = event.getUser().getEmail().equals(email);
        if (isCorrectPassword && isYourEvent) {

            if (title != null) {
                event.setTitle(title);
            }
            if (city != null) {
                event.setCity(city);
            }
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (startDate != null) {
                Date localStartDateTime = parser.parse(startDate);
                event.setStartDate(localStartDateTime);
            }
            if (endDate != null) {
                Date localEndDateTime = parser.parse(endDate);
                event.setEndDate(localEndDateTime);
            }
            if (description != null) {
                event.setDescription(description);
            }
            eventService.createEvent(event);
        }

        return getApiModelEvent(event);
    }

    @PostMapping("/events/newEvent")
    public EventAPI createNewEvent(
            @RequestHeader(value = "title") String title,
            @RequestHeader(value = "city") String city,
            @RequestHeader(value = "startDate") String startDate,
            @RequestHeader(value = "endDate") String endDate,
            @RequestHeader(value = "description") String description,
            @RequestHeader(value = "yourEmail") String email,
            @RequestHeader(value = "yourPassword") String password
    ) throws ParseException {

        User user = userService.findUsersByEmail(email);
        Event event = new Event();
        Boolean isCorrectPassword = bCryptPasswordEncoder.matches(password, user.getPassword());
        if (isCorrectPassword) {
            event.setTitle(title);
            event.setCity(city);
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date localStartDateTime = parser.parse(startDate);
            event.setStartDate(localStartDateTime);
            Date localEndDateTime = parser.parse(endDate);
            event.setEndDate(localEndDateTime);
            event.setDescription(description);
            event.setUser(user);
            eventService.createEvent(event);
        }

        return getApiModelEvent(event);
    }

    @RequestMapping(value = "/events/deleteEvent", method = RequestMethod.DELETE)
    public String deleteEvent(
            @RequestHeader(value = "id") String id,
            @RequestHeader(value = "yourEmail") String email,
            @RequestHeader(value = "yourPassword") String password
    ) {

        User user = userService.findUsersByEmail(email);
        Event event = eventService.findById(Integer.parseInt(id)).orElse(new Event());
        Boolean isCorrectPassword = bCryptPasswordEncoder.matches(password, user.getPassword());
        if (isCorrectPassword && event.getTitle() != null) {
            eventService.deleteEvent(event);
            return "delete succesfully";
        }

        return "Error";
    }


    public List<EventAPI> getApiModelEvent(List<Event> events) {
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

    public EventAPI getApiModelEvent(Event event) {

        EventAPI eventAPI = new EventAPI();
        eventAPI.setTitle(event.getTitle());
        eventAPI.setDescription(event.getDescription());
        eventAPI.setCity(event.getCity());
        eventAPI.setId(event.getId());
        eventAPI.setStartDate(event.getStartDate().toString());
        eventAPI.setEndDate(event.getEndDate().toString());
        eventAPI.setUser(event.getUser().getFirstName() + " " + event.getUser().getLastName());

        return eventAPI;
    }
}

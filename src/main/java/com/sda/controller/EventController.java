package com.sda.controller;

import com.sda.model.Event;
import com.sda.model.User;
import com.sda.service.EventService;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/")
public class EventController {

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;


    @GetMapping("/event/addEvent")
    public String addEventGet(Model model) {
        Event event = new Event();
        model.addAttribute("event", event);
        return "event/addEvent";
    }

    @PostMapping("/event/addEvent")
    public String register(@Valid Event event, BindingResult result, Model model) {
        event.setTitle(event.getTitle().trim());
        event.setDescription(event.getDescription().trim());
        event.setCity(event.getCity().trim());
        if (event.getTitle().isEmpty()) {
            model.addAttribute("alertTitle", "The field cannot contain any spaces.");
        }
        if (event.getDescription().isEmpty()) {
            model.addAttribute("alertDescription", "The field cannot contain any spaces.");
        }
        if (event.getCity().isEmpty()) {
            model.addAttribute("alertCity", "The field cannot contain any spaces.");
        }
        if (event.getTitle().isEmpty() || event.getDescription().isEmpty() ||  event.getCity().isEmpty()){
            return "event/addEvent";
        }
        if (result.hasErrors()) {
            return "event/addEvent";
        } else {

            LocalDate localStartDate = LocalDate.parse(event.getStartDateString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (localStartDate.isBefore(LocalDate.now())){
                model.addAttribute("alertStartDate", "The date cannot be a past date");
                return "event/addEvent";}

            LocalTime localStartTime = LocalTime.parse(event.getStartTimeString(), DateTimeFormatter.ofPattern("HH:mm"));

            event.setStartDate(localStartDate);
            event.setStartTime(localStartTime);

            LocalDate localEndDate = LocalDate.parse(event.getEndDateString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (localStartDate.isAfter(localEndDate)){
                model.addAttribute("alertEndDate", "The date cannot be earlier than the start date ");
                return "event/addEvent";}

            LocalTime localEndTime = LocalTime.parse(event.getEndTimeString(), DateTimeFormatter.ofPattern("HH:mm"));

            if (localStartDate.isEqual(localEndDate) && localStartTime.isAfter(localEndTime)){
                model.addAttribute("alertEndTime", "The time cannot be earlier than the start time ");
                return "event/addEvent";}

            event.setEndDate(localEndDate);
            event.setEndTime(localEndTime);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User authUser = userService.findUsersByEmail(auth.getName());
            event.setUser(authUser);
            eventService.createEvent(event);
            return "redirect:/home";
        }
    }

    @GetMapping("/eventShow")
    public String showEvent(Model model, @RequestParam("eventId") int eventId){
        Event event = eventService.findById(eventId).get();
        model.addAttribute("event", event);
        return "eventShow";
    }
}

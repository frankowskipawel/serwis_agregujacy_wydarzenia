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

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;


    @GetMapping("/addEvent")
    public String addEventGet(Model model) {
        Event event = new Event();
        model.addAttribute("event", event);
        return "event/addEvent";
    }

    @PostMapping("/addEvent")
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
            LocalDate localDate = LocalDate.parse(event.getDateString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (localDate.isBefore(LocalDate.now())){
                model.addAttribute("alertDate", "The date cannot be a past date");
                return "event/addEvent";}
            LocalTime localTime = LocalTime.parse(event.getTimeString(), DateTimeFormatter.ofPattern("HH:mm"));
            event.setDate(localDate);
            event.setTime(localTime);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User authUser = userService.findUsersByEmail(auth.getName());
            event.setUser(authUser);
            eventService.createEvent(event);
            return "redirect:/home";
        }
    }
}

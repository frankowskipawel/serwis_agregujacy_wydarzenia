package com.sda.controller;

import com.sda.model.Event;
import com.sda.model.User;
import com.sda.service.EventService;
import com.sda.service.PictureService;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Controller
@RequestMapping("/")
public class EventController {

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private PictureService pictureService;


    @GetMapping("/event/addEvent")
    public String addEventGet(Model model, @RequestParam(value = "picture", required = false) String photoFileName) {
        Event event = new Event();
        event.setPicture(pictureService.findByFileName(photoFileName));
        model.addAttribute("event", event);
        return "event/addEvent";
    }

    @PostMapping("/event/addEvent")
    public String register(@Valid @ModelAttribute("event") Event event, BindingResult result, Model model) throws ParseException {
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
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startDateTime = event.getStartDateString()+" "+event.getStartTimeString();
            Date localStartDateTime = parser.parse(startDateTime);
            String endDateTime = event.getEndDateString()+" "+event.getEndTimeString();
            Date localEndDateTime = parser.parse(endDateTime);

            if (localStartDateTime.before(new Date())){
                model.addAttribute("alertStartDate", "The date cannot be a past date");
                return "event/addEvent";}


            event.setStartDate(localStartDateTime);
            LocalDate localEndDate = LocalDate.parse(event.getEndDateString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (localStartDateTime.after(localEndDateTime)){
                model.addAttribute("alertEndDate", "The date cannot be earlier than the start date ");
                return "event/addEvent";}

            if (localStartDateTime.equals(localEndDate) && localStartDateTime.after(localEndDateTime)){
                model.addAttribute("alertEndTime", "The time cannot be earlier than the start time ");
                return "event/addEvent";}

            event.setEndDate(localEndDateTime);

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

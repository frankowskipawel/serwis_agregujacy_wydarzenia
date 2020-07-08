package com.sda.controller;


import com.sda.entity.Event;
import com.sda.entity.User;
import com.sda.entity.Comment;
import com.sda.service.CommentsService;
import com.sda.service.EventService;
import com.sda.service.PictureService;
import com.sda.service.UserService;
import com.sda.utils.EmailUtil;
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
    @Autowired

    private CommentsService commentsService;

    private Event currentEvent;
    @Autowired
    private EmailUtil emailUtill;



    @GetMapping("/event/addEvent")
    public String addEventGet(Model model, @RequestParam(value = "picture", required = false) String photoFileName) {
        Event event = new Event();
        event.setPicture(pictureService.findByFileName(photoFileName));
        model.addAttribute("event", event);
        return "event/addEvent";
    }

    @PostMapping("/event/addEvent")
    public String register(@Valid @ModelAttribute("event") Event event, BindingResult result, Model model) throws ParseException {
        if (event.getPicture().getFileName().isEmpty()) {
            event.setPicture(pictureService.findByFileName("nopictures.jpg"));
        }
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
        if (event.getTitle().isEmpty() || event.getDescription().isEmpty() || event.getCity().isEmpty()) {
            return "event/addEvent";
        }
        if (result.hasErrors()) {
            return "event/addEvent";
        } else {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startDateTime = event.getStartDateString() + " " + event.getStartTimeString();
            Date localStartDateTime = parser.parse(startDateTime);
            String endDateTime = event.getEndDateString() + " " + event.getEndTimeString();
            Date localEndDateTime = parser.parse(endDateTime);

            if (localStartDateTime.before(new Date())) {
                model.addAttribute("alertStartDate", "The date cannot be a past date");
                return "event/addEvent";
            }


            event.setStartDate(localStartDateTime);
            LocalDate localEndDate = LocalDate.parse(event.getEndDateString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (localStartDateTime.after(localEndDateTime)) {
                model.addAttribute("alertEndDate", "The date cannot be earlier than the start date ");
                return "event/addEvent";
            }

            if (localStartDateTime.equals(localEndDate) && localStartDateTime.after(localEndDateTime)) {
                model.addAttribute("alertEndTime", "The time cannot be earlier than the start time ");
                return "event/addEvent";
            }

            event.setEndDate(localEndDateTime);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User authUser = userService.findUsersByEmail(auth.getName());
            event.setUser(authUser);
            eventService.createEvent(event);
            emailUtill.sendNotificationNewEvent(event);
            return "redirect:/home";
        }
    }

    @GetMapping("/eventShow")
    public String showEvent(Model model, @RequestParam("eventId") int eventId) {
        Event event = eventService.findById(eventId).get();
        currentEvent = event;
        model.addAttribute("event", event);
        Comment comment = new Comment();
        model.addAttribute("comment", comment);
        model.addAttribute("commentlist", commentsService.findByEventOrderByDate(eventId) );
        return "eventShow";
    }

    @PostMapping("/addComment")
    public String addComment(Model model, @ModelAttribute("comment") Comment comment){
        model.addAttribute("eventId", currentEvent.getId());
        comment.setEvent(currentEvent);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        comment.setUser(userService.findUsersByEmail(auth.getName()));
        comment.setDate(new Date());
        commentsService.save(comment);
        return "redirect:/eventShow?eventId="+currentEvent.getId();
    }

    @PostMapping("/signUpForEvent")
    public String signUpForEvent(Model model, @ModelAttribute("eventId") int userId){
        Event event = eventService.findById(userId).get();
        currentEvent = event;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUsersById(userId));
        return "redirect:/home";
    }

    @GetMapping("/signUpForEvent")
    public String signUpForEventGet(Model model, @RequestParam("eventId") int userId) {
        User user = userService.findUsersById(userId);
        model.addAttribute("user", userId);
        return "redirect:/home";
    }

}

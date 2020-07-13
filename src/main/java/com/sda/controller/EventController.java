package com.sda.controller;


import com.sda.entity.Event;
import com.sda.entity.Picture;
import com.sda.entity.User;
import com.sda.entity.Comment;
import com.sda.repository.RoleRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    @Autowired
    private RoleRepository roleRepository;


    @GetMapping("/event/addEvent")
    public String addEventGet(Model model, @RequestParam(value = "picture", required = false) String photoFileName) {
        Event event = new Event();
        event.setPicture(pictureService.findByFileName(photoFileName));
        model.addAttribute("event", event);
        currentEvent = null;
        return "event/addEvent";
    }

    @PostMapping("/event/addEvent")
    public String register(@Valid @ModelAttribute("event") Event event, BindingResult result, Model model) throws ParseException {
        currentEvent = null;
        if (event.getPicture().getFileName().isEmpty()) {
            event.setPicture(pictureService.findByFileName("nopictures.jpg"));
        }
        Picture picture = pictureService.findByFileName(event.getPicture().getFileName());
        pictureService.savePicture(picture);
        event.setPicture(picture);
        event.setTitle(event.getTitle().trim());
        event.setDescription(event.getDescription().trim());
        event.setCity(event.getCity().trim());
        if (event.getTitle().isEmpty()) {
            model.addAttribute("alertTitle", "The field cannot contain any spaces.");
        } //
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

            return "redirect:/eventShow?eventId=" + event.getId();
        }
    }

    @GetMapping("/eventShow")
    public String showEvent(Model model, @RequestParam("eventId") int eventId) {
        Event event = eventService.findById(eventId).get();
        currentEvent = event;
        model.addAttribute("event", event);
        Comment comment = new Comment();
        model.addAttribute("comment", comment);
        model.addAttribute("commentlist", commentsService.findByEventOrderByDate(eventId));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());
        if (user != null && user.getSignUpEvents().contains(event)) {
            model.addAttribute("isEventSaved", true);
        }
        if (event.getUser().getEmail().equals(auth.getName()) || (user!= null && user.getRoles().contains(roleRepository.findByRole("ADMIN")))) {
            model.addAttribute("isMyEvent", true);
        }

        List<User> savedUsers = userService.findAllBySignUpEventsContains(event.getId());
        model.addAttribute("savedUsers", savedUsers);

        return "eventShow";
    }

    @PostMapping("/addComment")
    public String addComment(Model model, @ModelAttribute("comment") Comment comment) {
        model.addAttribute("eventId", currentEvent.getId());
        comment.setEvent(currentEvent);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        comment.setUser(userService.findUsersByEmail(auth.getName()));
        comment.setDate(new Date());
        commentsService.save(comment);

        return "redirect:/eventShow?eventId=" + currentEvent.getId();
    }

    @GetMapping("/event/signUpEvent")
    public String signUpEvent(Model model, @RequestParam("id") int id) {
        Event event = eventService.findById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.findUsersByEmail(auth.getName());
        if (user.getSignUpEvents() == null) {
            user.setSignUpEvents(new ArrayList<>());
        }
        if (!user.getSignUpEvents().contains(event)) {
            user.getSignUpEvents().add(event);
            model.addAttribute("eventId", id);
            userService.save(user);
        }
        return "redirect:/eventShow?eventId=" + currentEvent.getId();
    }


    @GetMapping("/event/cancelSavedEvent")
    public String cancelSavedEvent(Model model, @RequestParam("id") int id) {
        Event event = eventService.findById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());

        if (user.getSignUpEvents().contains(event)) {
            user.getSignUpEvents().remove(event);
            model.addAttribute("eventId", id);
            userService.save(user);
        }

        List<User> savedUsers = userService.findAllBySignUpEventsContains(event.getId());
        model.addAttribute("savedUsers", savedUsers);
        return "redirect:/eventShow?eventId=" + currentEvent.getId();
    }


    @GetMapping("/event/editEvent")
    public String editEvent(@RequestParam("id") int id, Model model
            , @RequestParam(value = "picture", required = false) String photoFileName) {
        Event event = eventService.findById(id).get();
        if (photoFileName != null) {
            event.getPicture().setFileName(photoFileName);
        }
        event.setStartDateString(event.getStartDate().toString().substring(0, 10));
        event.setEndDateString(event.getEndDate().toString().substring(0, 10));
        event.setStartTimeString(event.getStartDate().toString().substring(11, 16));
        event.setEndTimeString(event.getEndDate().toString().substring(11, 16));
        model.addAttribute("event", event);
        currentEvent = event;
        return "event/editEvent";
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    @GetMapping("/event/deleteEvent")
    public String deleteEvent(@RequestParam("id") int id, Model model) {
        Event event = eventService.findById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());
        if (event.getUser().getEmail().equals(auth.getName()) || user.getRoles().contains(roleRepository.findByRole("ADMIN"))) {
            eventService.deleteEvent(event);
        }
        return "redirect:/home";
    }
}

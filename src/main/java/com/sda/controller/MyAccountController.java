package com.sda.controller;

import com.sda.entity.Event;
import com.sda.entity.User;
import com.sda.repository.RoleRepository;
import com.sda.service.EventService;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/myAccount")
public class MyAccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;
    @Autowired
    private Environment environment;


    @GetMapping("/myEvents")
    public String myAccountHome(Model model, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("selectedMenu", "myEvents");
        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")), Sort.by("startDate").ascending());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());
        Page<Event> eventPages = eventService.findAllPagination(pageable);

        List<Event> events = new ArrayList<>();

        for (Event event : eventPages) {
            if (event.getUser().getEmail().equals(auth.getName())) {
                events.add(event);
            }
            if (user.getSignUpEvents().contains(event)) {
                if (!events.contains(event)) {
                    events.add(event);
                }
            }
        }

        Page<Event> eventPage = new PageImpl<>(events);

        model.addAttribute("pages", eventPage);
        int totalPages = eventPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                pageNumbers.add(i);
            }
            model.addAttribute("list", "home");
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("currentPage", page.orElse(1));
        }
        return "myAccount/myEvents";
    }

    @PostMapping("/myEvents")
    public String searchPost(Model model,
                             @RequestParam("when") String when,
                             @RequestParam("role") String role,
                             @RequestParam("startDate") String startDate,
                             @RequestParam("endDate") String endDate,
                             @RequestParam("page") Optional<Integer> page) {

        model.addAttribute("selectedMenu", "myEvents");
        model.addAttribute("when", when);
        model.addAttribute("role", role);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date localStartDate = null;
        Date localEndDate = null;
        try {
            localStartDate = parser.parse("2000-01-01 00:00");
            localEndDate = parser.parse("2099-12-31 23:59");

            if (!startDate.isEmpty()) {
                localStartDate = parser.parse(startDate + " 00:00");
            }
            if (!endDate.isEmpty()) {
                localEndDate = parser.parse(endDate + " 23:59");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")), Sort.by("startDate").ascending());
        Page<Event> eventPages = getEvents(pageable, when, "");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());

        List<Event> events = new ArrayList<>();

        for (Event event : eventPages) {
            if (event.getStartDate().after(localStartDate) && event.getEndDate().before(localEndDate)) {

                if (event.getUser().getEmail().equals(auth.getName()) && (role.equals("organizer") || role.equals("all"))) {
                    events.add(event);
                }
                if (user.getSignUpEvents().contains(event) && (role.equals("participant") || role.equals("all"))) {
                    if (!events.contains(event)) {
                        events.add(event);
                    }
                }
            }
        }

        Page<Event> eventPage = new PageImpl<>(events);

        model.addAttribute("pages", eventPage);
        int totalPages = eventPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                pageNumbers.add(i);
            }
            model.addAttribute("list", "search");
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("currentPage", page.orElse(1));
        }

        return "myAccount/myEvents";
    }


    private Page<Event> getEvents(Pageable pageable, String filter, String query) {
        Page<Event> eventPage;
        switch (filter) {
            case "all":
                eventPage = eventService.findAllBySearchQueryPagination(pageable, query);
                break;
            case "past":
                eventPage = eventService.findAllPastEvent(pageable, query, new Date());
                break;
            case "ongoing":
                eventPage = eventService.findAllOngoingEvent(pageable, query, new Date());
                break;
            case "future":
                eventPage = eventService.findAllFutureEvent(pageable, query, new Date());
                break;
            default:
                eventPage = eventService.findAllBySearchQueryPagination(pageable, query);
        }
        return eventPage;
    }

    @GetMapping("/editAccount")
    public String editAccount(Model model) {
        model.addAttribute("selectedMenu", "editAccount");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());
        model.addAttribute("user", user);

        return "myAccount/editAccount";
    }

    @PostMapping("/editAccount")
    public String editAccountPost(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "myAccount/editAccount";
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User userToSave = userService.findUsersByEmail(auth.getName());
            userToSave.setFirstName(user.getFirstName());
            userToSave.setLastName(user.getLastName());
            userToSave.setName(user.getName());

            userService.save(userToSave);
            return "redirect:/myAccount/myEvents";
        }
    }

    @PostMapping("changePassword")
    public String changePassword(@ModelAttribute("user") User user){
        User userToSave = userService.findById(user.getId()).get();
        userToSave.setPassword(user.getPassword());
        userService.createUser(userToSave);

        return "redirect:/myAccount/editAccount";
    }
}

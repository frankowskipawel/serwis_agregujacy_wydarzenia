package com.sda.controller;

import com.sda.entity.Event;
import com.sda.entity.User;
import com.sda.service.EventService;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Page<Event> eventPage = eventService.findByUser(pageable, user);
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

    @GetMapping("/mySavedEvents")
    public String mySavedEvents(Model model, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("selectedMenu", "mySavedEvents");
        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")), Sort.by("startDate").ascending());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUsersByEmail(auth.getName());
        List<Event> mySavedEvents = user.getSignUpEvents();

        Page<Event> eventPage = new PageImpl<>(mySavedEvents);

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
        return "myAccount/mySavedEvents";
    }
}

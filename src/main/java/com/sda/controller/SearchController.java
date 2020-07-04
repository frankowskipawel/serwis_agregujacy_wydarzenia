package com.sda.controller;

import com.sda.entity.Event;
import com.sda.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class SearchController {

    @Autowired
    private EventService eventService;
    @Autowired
    private Environment environment;

    private String query;
    private String filter;

    @GetMapping("/search")
    public String searchGet(Model model, @RequestParam("page") Optional<Integer> page){
        model.addAttribute("query", query);
        model.addAttribute("filterQuery", filter);
        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")), Sort.by("startDate").ascending());
        Page<Event> eventPage = getEvents(pageable, filter, query);
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

        return "search";
    }


    @PostMapping("/search")
    public String searchPost(Model model, @RequestParam("query") String query, @RequestParam("filterQuery") String filter, @RequestParam("page") Optional<Integer> page){
        model.addAttribute("query", query);
        this.query=query;
        this.filter=filter;
        model.addAttribute("filterQuery", filter);
        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")), Sort.by("startDate").ascending());
        Page<Event> eventPage = getEvents(pageable, filter, query);
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

        return "search";
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
}

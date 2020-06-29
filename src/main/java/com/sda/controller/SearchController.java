package com.sda.controller;

import com.sda.model.Event;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class SearchController {

    @Autowired
    private EventService eventService;
    @Autowired
    private Environment environment;


    @GetMapping("/search")
    public String search(Model model){
        System.out.println("getgetget");
        return "search";
    }

    @PostMapping("/search")
    public String searchPost(Model model, @RequestParam("query") String query, @RequestParam("filterQuery") String filterQuery, @RequestParam("page") Optional<Integer> page){
        model.addAttribute("query", query);

        System.out.println(filterQuery);

        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")), Sort.by("startDate").ascending().and(Sort.by("startTime").ascending()));
        Page<Event> eventPage = eventService.findAllBySearchQueryPagination(pageable, query);
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

        return "search";
    }
}

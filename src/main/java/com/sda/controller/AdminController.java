package com.sda.controller;

import com.sda.entity.User;
import com.sda.repository.RoleRepository;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    Environment environment;

    @GetMapping("/admins")
    public String adminsList(Model model, @RequestParam("page") Optional<Integer> page) {

        int currentPage = page.orElse(1);
        Pageable pageable = PageRequest.of(currentPage - 1, Integer.parseInt(environment.getProperty("quantityPerPage")));
        Page<User> admins = userService.findAllByRoles(pageable, roleRepository.findByRole("ADMIN"));
        model.addAttribute("pages", admins);
        int totalPages = admins.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                pageNumbers.add(i);
            }
            model.addAttribute("list", "home");
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("currentPage", page.orElse(1));
        }
        User user = new User();
        model.addAttribute("user", user);

        return "admin/admins";
    }

    @PostMapping("/addAdmin")
    public String addAdmin(Model model, @ModelAttribute("user") User userFromModal) {
        User user = userService.findUsersByEmail(userFromModal.getEmail());
        if (user != null) {
            user.getRoles().add(roleRepository.findByRole("ADMIN"));
            userService.save(user);
        }
        return "redirect:/admin/admins";
    }

    @GetMapping("deleteAdmin")
    public String deleteAdmin(Model model, @RequestParam("userId") int userId) {
        User user = userService.findById(userId).get();
        List<User> admins = userService.findAllByRoles(roleRepository.findByRole("ADMIN"));
        if (admins.size() > 1) {
            user.getRoles().remove(roleRepository.findByRole("ADMIN"));
            userService.save(user);
        }
        return "redirect:/admin/admins";
    }
}

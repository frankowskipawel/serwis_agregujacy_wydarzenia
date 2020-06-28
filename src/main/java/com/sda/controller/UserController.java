package com.sda.controller;

import com.sda.model.User;
import com.sda.repository.UserRepository;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model, @ModelAttribute("error") String error) {
        model.addAttribute("user", new User());
        if (error != null && error.equals("true")) {
            model.addAttribute("error", "Błędy login lub hasło");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getName()+"-----------------");
        return "login";
    }

    @PostMapping("/login")
    public String submitForm(@Valid @ModelAttribute("user") User user, BindingResult bindingResultUser, Model model) {

        User foundUser = userService.findUsersByEmail(user.getEmail());
        System.out.println(foundUser);

        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            return "home";
        } else {
            model.addAttribute("error", "Błędy login lub hasło");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        } else {
            user.setActive(true);
            userService.createUser(user);
            return "redirect:/login";
        }
    }




//
//    @GetMapping("/details")
//    public String details(Model model) {
//        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//       User portalUser = userRepository.findUsersByEmail(user.getUsername());
//        model.addAttribute("portalUser", portalUser);
//
//        return "/details";
//    }


//    @PostMapping("/details")
//    public String updateEmail(User user, BindingResult result, Model model){
//        User temporaryPortalUser = UserRepository.findById(User.getId()).get();
//        temporaryPortalUser.setEmail(user.getEmail());
//        userRepository.save(temporaryPortalUser);
//        model.addAttribute("portalUser", temporaryPortalUser);
//        return "details";
//    }

}

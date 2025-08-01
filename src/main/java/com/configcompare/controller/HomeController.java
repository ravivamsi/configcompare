package com.configcompare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Config Compare Tool");
        return "home";
    }

    @GetMapping("/github")
    public String github(Model model) {
        model.addAttribute("title", "GitHub Configuration");
        model.addAttribute("platform", "GitHub");
        return "github";
    }


} 
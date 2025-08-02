package com.configcompare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    @GetMapping("/repos.json")
    public ResponseEntity<String> reposJson() {
        try {
            Resource resource = new ClassPathResource("repos.json");
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Failed to load repositories\"}");
        }
    }

} 
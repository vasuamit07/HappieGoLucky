package com.happiegolucky.backend.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Use @Controller here (not @RestController) for HTML rendering
public class UiController {

    @GetMapping("/")
    public String index() {
        // This tells Spring/Thymeleaf to look for the file named 'index.html'
        // in src/main/resources/templates/
        return "index";
    }
}
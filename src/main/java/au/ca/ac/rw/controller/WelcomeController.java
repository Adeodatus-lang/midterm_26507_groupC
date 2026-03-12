package au.ca.ac.rw.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    
    @GetMapping("/")
    public String welcome() {
        return "Welcome to Barber Management System API! Visit /api/users to get started.";
    }
}
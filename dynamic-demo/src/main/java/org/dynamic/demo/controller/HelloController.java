package org.dynamic.demo.controller;

import org.dynamic.demo.events.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private EmailService emailService;

    @GetMapping("")
    public String hello() {
        emailService.sendEmail("beijing", "haha");
        return "Hello ! ! !";
    }

    @GetMapping("admin")
    public String admin() {
        return "Hello admin ! ! !";
    }
}

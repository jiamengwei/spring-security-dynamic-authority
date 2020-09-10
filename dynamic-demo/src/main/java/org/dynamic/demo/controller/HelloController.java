package org.dynamic.demo.controller;

import org.dynamic.demo.entity.TUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("")
    public String hello(TUser tUser) {
        return "Hello ! ! !" + tUser.toString();
    }

    @GetMapping("admin")
    public String admin() {
        return "Hello admin ! ! !";
    }
}



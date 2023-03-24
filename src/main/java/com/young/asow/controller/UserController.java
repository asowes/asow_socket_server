package com.young.asow.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/hello")
    public String test() {
        return "Hello World！";
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/hello/plus")
    public String test2() {
        return "Hello World Plus!";
    }
}

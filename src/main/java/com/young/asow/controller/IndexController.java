package com.young.asow.controller;

import com.young.asow.entity.LoginUser;
import com.young.asow.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String test() {
        return "hello word!";
    }


    @PostMapping("/register")
    public String addUser(
            final @RequestBody LoginUser user
    ) {
        try {
            userService.addUser(user);
        } catch (Exception ignored) {
            return "注册失败";
        }
        return "注册成功";
    }
}

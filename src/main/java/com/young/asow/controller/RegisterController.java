package com.young.asow.controller;

import com.young.asow.entity.Account;
import com.young.asow.response.RestResponse;
import com.young.asow.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String test() {
        return "hello word!";
    }


    @PostMapping("/user")
    public RestResponse<Object> addUser(
            final @RequestBody Account user
    ) {
        try {
            userService.addUser(user);
            return RestResponse.ok("用户创建成功");
        } catch (Exception e) {
            return RestResponse.fail(e.getMessage());
        }
    }
}

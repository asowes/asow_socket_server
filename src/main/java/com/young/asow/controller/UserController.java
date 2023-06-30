package com.young.asow.controller;

import com.young.asow.modal.UserInfoModal;
import com.young.asow.response.RestResponse;
import com.young.asow.service.UserService;
import com.young.asow.socket.WebSocketServer;
import com.young.asow.util.auth.JWTUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/socket")
    public void socket(
            @RequestParam(name = "message") String message
    ) {
        WebSocketServer.sendMessageByWayBillId(439094307840987136L, message);
    }

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

    @GetMapping("/info")
    public RestResponse<UserInfoModal> getUserInfo(
            @RequestHeader("authorization") String token
    ) {
        Long userId = JWTUtil.getUserId(token);
        UserInfoModal modal = userService.getUserByUserId(userId);
        return RestResponse.ok(modal);
    }
}

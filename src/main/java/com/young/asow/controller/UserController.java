package com.young.asow.controller;

import com.young.asow.socket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/socket")
    public void socket(
            @RequestParam(name = "message") String message
    ) {
        WebSocketServer.sendMessageByWayBillId("4", message);
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
}

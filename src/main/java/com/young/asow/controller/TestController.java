package com.young.asow.controller;

import com.young.asow.modal.ConversationModal;
import com.young.asow.response.RestResponse;
import com.young.asow.service.ChatService;
import com.young.asow.util.auth.JWTUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final ChatService chatService;

    public TestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversations")
    public RestResponse<List<ConversationModal>> getCurrentUserConversations(
            @RequestHeader("authorization") String token
    ) {
        Long userId = JWTUtil.getUserId(token);
        List<ConversationModal> dbConversations = this.chatService.getConversations2(userId);
        return RestResponse.ok(dbConversations);
    }

}

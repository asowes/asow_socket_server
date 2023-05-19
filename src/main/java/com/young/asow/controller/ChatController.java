package com.young.asow.controller;


import com.young.asow.entity.Conversation;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.service.ChatService;
import com.young.asow.util.auth.JWTUtil;
import org.springframework.web.bind.annotation.*;
import com.young.asow.response.RestResponse;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversations")
    public RestResponse<List<ConversationModal>> getCurrentUserConversations(
            @RequestHeader("authorization") String token
    ) {
        Long userId = JWTUtil.getUserId(token);
        List<ConversationModal> dbConversations = this.chatService.getConversations(userId);
        return RestResponse.ok(dbConversations);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public RestResponse<List<MessageModal>> getConversationMessages(
            @PathVariable Long conversationId
    ) {
        List<MessageModal> modals = chatService.getConversationMessages(conversationId);
        return RestResponse.ok(modals);
    }

    @PostMapping("/save/message")
    public void sendMessage(
            @RequestHeader("authorization") String token,
            @RequestBody MessageModal messageModal
    ) {
        Long fromId = JWTUtil.getUserId(token);
        chatService.saveMessageWithConversation(messageModal, fromId);
    }

    @PostMapping("/read/{conversationId}")
    public RestResponse<?> readMessage(
            @RequestHeader("authorization") String token,
            @PathVariable Long conversationId
    ) {
        Long userId = JWTUtil.getUserId(token);
        chatService.readMessage(userId, conversationId);
        return RestResponse.ok();
    }
}

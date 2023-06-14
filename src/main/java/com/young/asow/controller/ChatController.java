package com.young.asow.controller;


import com.young.asow.entity.FriendApply;
import com.young.asow.modal.*;
import com.young.asow.service.ChatService;
import com.young.asow.service.UserService;
import com.young.asow.util.auth.JWTUtil;
import org.springframework.web.bind.annotation.*;
import com.young.asow.response.RestResponse;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(
            ChatService chatService,
            UserService userService
    ) {
        this.chatService = chatService;
        this.userService = userService;
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
            @PathVariable Long conversationId,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        List<MessageModal> modals = chatService.getConversationMessages(conversationId, page);
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

    @GetMapping("/search/users")
    public RestResponse<List<UserInfoModal>> searchUsers(
            @RequestParam(name = "keyword") String keyword
    ) {
        List<UserInfoModal> modals = chatService.searchUsers(keyword);
        return RestResponse.ok(modals);
    }


    @PostMapping("/friend/apply/{accepterId}")
    public RestResponse<?> sendFriendApply(
            @RequestHeader("authorization") String token,
            @PathVariable Long accepterId
    ) {
        Long userId = JWTUtil.getUserId(token);
        chatService.applyFriend(userId, accepterId);
        return RestResponse.ok();
    }

    @GetMapping("/my/friend/apply")
    public RestResponse<List<FriendApplyModal>> myFriendApply(
            @RequestHeader("authorization") String token
    ) {
        Long userId = JWTUtil.getUserId(token);
        List<FriendApplyModal> modals = chatService.getMyFriendApply(userId);
        return RestResponse.ok(modals);
    }

    @PostMapping("/handle/friend/apply/{senderId}")
    public RestResponse<?> handleFriendApply(
            @RequestHeader("authorization") String token,
            @PathVariable Long senderId,
            @RequestBody FriendApplyModal modal
    ) {
        Long userId = JWTUtil.getUserId(token);
        chatService.handleFriendApply(userId, senderId, modal);
        return RestResponse.ok();
    }
}

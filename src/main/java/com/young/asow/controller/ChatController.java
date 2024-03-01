package com.young.asow.controller;


import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.FriendApplyModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.modal.UserInfoModal;
import com.young.asow.response.RestResponse;
import com.young.asow.service.ApplyFriendService;
import com.young.asow.service.ChatService;
import com.young.asow.util.auth.JWTUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final ApplyFriendService applyFriendService;

    public ChatController(
            ChatService chatService,
            ApplyFriendService applyFriendService
    ) {
        this.chatService = chatService;
        this.applyFriendService = applyFriendService;
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
            @RequestHeader("authorization") String token,
            @PathVariable Long conversationId,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        // todo  需要校验一下这个conversation里面是否包含用户
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
    public RestResponse<List<FriendApplyModal>> searchUsers(
            @RequestHeader("authorization") String token,
            @RequestParam(name = "keyword") String keyword
    ) {
        Long me = JWTUtil.getUserId(token);
        List<FriendApplyModal> modals = applyFriendService.searchUsers(me, keyword);
        return RestResponse.ok(modals);
    }


    @PostMapping("/send/friend/apply/{accepterId}")
    public RestResponse<?> sendFriendApply(
            @RequestHeader("authorization") String token,
            @PathVariable Long accepterId
    ) {
        Long userId = JWTUtil.getUserId(token);
        applyFriendService.applyFriend(userId, accepterId);
        return RestResponse.ok();
    }

    @GetMapping("/my/friend/apply")
    public RestResponse<List<FriendApplyModal>> myFriendApply(
            @RequestHeader("authorization") String token
    ) {
        Long userId = JWTUtil.getUserId(token);
        List<FriendApplyModal> modals = applyFriendService.getMyFriendApply(userId);
        return RestResponse.ok(modals);
    }

    @PostMapping("/handle/friend/apply/{senderId}")
    public RestResponse<?> handleFriendApply(
            @RequestHeader("authorization") String token,
            @PathVariable Long senderId,
            @RequestBody FriendApplyModal modal
    ) {
        Long userId = JWTUtil.getUserId(token);
        applyFriendService.handleFriendApply(userId, senderId, modal);
        return RestResponse.ok();
    }


    @GetMapping("/my/friends")
    public RestResponse<List<UserInfoModal>> getMyFriends(
            @RequestHeader("authorization") String token
    ) {
        Long userId = JWTUtil.getUserId(token);
        List<UserInfoModal> modals = chatService.findMyFriends(userId);
        return RestResponse.ok(modals);
    }

    @PostMapping("/group/create")
    public RestResponse<?> createChatGroup(
            @RequestHeader("authorization") String token,
            @RequestBody Map<String, List<Long>> request
    ){
        Long userId = JWTUtil.getUserId(token);
        chatService.createChatGroup(userId, request.get("groupIds"));
        return RestResponse.ok();
    }

}

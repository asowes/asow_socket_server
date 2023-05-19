package com.young.asow.socket;

import com.young.asow.entity.UserConversation;
import com.young.asow.modal.MessageModal;
import com.young.asow.service.ChatService;
import org.springframework.stereotype.Service;


@Service
public class WebSocketService {
    private final ChatService chatService;

    public WebSocketService(ChatService chatService) {
        this.chatService = chatService;
    }


    public UserConversation saveMessageWithConversation(MessageModal message, Long fromId) {
        return chatService.saveMessageWithConversation(message, fromId);
    }
}

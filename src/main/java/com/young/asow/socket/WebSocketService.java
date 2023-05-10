package com.young.asow.socket;

import com.young.asow.modal.MessageModal;
import com.young.asow.service.ChatService;
import org.springframework.stereotype.Service;


@Service
public class WebSocketService {
    private final ChatService chatService;

    public WebSocketService(ChatService chatService) {
        this.chatService = chatService;
    }


    public void saveMessageWithConversation(MessageModal message, Long fromId) {
        this.chatService.saveMessageWithConversation(message, fromId);
    }
}

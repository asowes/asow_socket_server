package com.young.asow.socket;

import com.young.asow.entity.Chat;
import com.young.asow.entity.ChatContent;
import com.young.asow.service.ChatService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebSocketService {
    private final ChatService chatService;

    public WebSocketService(ChatService chatService) {
        this.chatService = chatService;
    }


    public void saveChat(SocketMessage message) {
        Chat chat = new Chat();
        chat.setFromId(1L);
        chat.setToId(3L);
        chat.setStatus("1");

        ChatContent content = new ChatContent();
        content.setType(message.getType());
        content.setContent(message.getMessageContent());
        content.setSendTime(LocalDateTime.now());
        content.setChat(chat);

        chat.setChatContent(content);

        this.chatService.save(chat);
    }
}

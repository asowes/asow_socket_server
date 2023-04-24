package com.young.asow.service;

import com.young.asow.entity.Chat;
import com.young.asow.repository.ChatRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void save(Chat chat) {
        this.chatRepository.save(chat);
    }
}

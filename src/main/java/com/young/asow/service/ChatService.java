package com.young.asow.service;

import com.young.asow.entity.Chat;
import com.young.asow.entity.Conversation;
import com.young.asow.entity.Message;
import com.young.asow.entity.User;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.repository.ChatRepository;
import com.young.asow.repository.ConversationRepository;
import com.young.asow.repository.MessageRepository;
import com.young.asow.repository.UserRepository;
import com.young.asow.util.ConvertUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public ChatService(
            ChatRepository chatRepository,
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageRepository messageRepository
    ) {
        this.chatRepository = chatRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public void save(Chat chat) {
        this.chatRepository.save(chat);
    }

    public List<ConversationModal> getConversations(String userId) {
        List<Conversation> dbConversations = this.conversationRepository.findByFromId(userId);

        return dbConversations.stream()
                .map(conversation -> {
                    ConversationModal modal = ConvertUtil.Conversation2Modal(conversation);

                    String toId = conversation.getToId();
                    User to = userRepository
                            .findByUserId(toId)
                            .orElseThrow(() -> new BusinessException("[" + toId + "]" + " is not found"));

                    String lastMessageId = conversation.getLastMessageId();
                    Message message = messageRepository
                            .findByMessageId(lastMessageId)
                            .orElseThrow(() -> new BusinessException("[" + lastMessageId + "]" + " is not found"));

                    modal.setTo(ConvertUtil.User2Modal(to));
                    modal.setLastMessage(ConvertUtil.Message2Modal(message));

                    return modal;
                })
                .collect(Collectors.toList());
    }
}

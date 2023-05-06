package com.young.asow.service;

import com.young.asow.entity.Chat;
import com.young.asow.entity.Conversation;
import com.young.asow.entity.Message;
import com.young.asow.entity.User;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.repository.ChatRepository;
import com.young.asow.repository.ConversationRepository;
import com.young.asow.repository.MessageRepository;
import com.young.asow.repository.UserRepository;
import com.young.asow.util.ConvertUtil;
import com.young.asow.util.GenerateUtil;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
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


    public List<ConversationModal> getConversations(String userId) {
        // TODO  conversation同一个会话应该要有两条数据，且他们的conversationId是一样的
        //  fromId -> toId
        //  toId   -> fromId
        //  通过conversationId以及fromId即可查询出自己的conversation，这样就能取到unread

        // TODO 或者设计user与conversation的表chat_user_conversation 添加unread
        //  通过conversationId以及fromId查询该关联表查询自己的unread
        List<Conversation> dbConversations = this.conversationRepository.findByUserId(userId);

        return dbConversations.stream()
                .map(conversation -> {
                    ConversationModal modal = ConvertUtil.Conversation2Modal(conversation);

                    String toId = conversation.getToId();
                    User to = userRepository
                            .findByUserId(toId)
                            .orElseThrow(() -> new BusinessException("[" + toId + "]" + " is not found"));

                    String fromId = conversation.getFromId();
                    User from = userRepository
                            .findByUserId(fromId)
                            .orElseThrow(() -> new BusinessException("[" + fromId + "]" + " is not found"));

                    String lastMessageId = conversation.getLastMessageId();
                    Message message = messageRepository
                            .findByMessageId(lastMessageId)
                            .orElseThrow(() -> new BusinessException("[" + lastMessageId + "]" + " is not found"));

                    modal.setTo(ConvertUtil.User2Modal(to));
                    modal.setFrom(ConvertUtil.User2Modal(from));
                    modal.setLastMessage(ConvertUtil.Message2Modal(message));

                    return modal;
                })
                .collect(Collectors.toList());
    }

    public List<MessageModal> getConversationMessages(String conversationId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "messageId");
        Pageable pageable = PageRequest.of(0, 100, sort);
        return messageRepository
                .findMessagesByConversationId(pageable, conversationId)
                .stream()
                .map(ConvertUtil::Message2Modal)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveMessageWithConversation(MessageModal messageModal) {
        Message message = new Message();

        // 将原本的最后一条消息的isLatest设置成false
        String findConversationId = messageModal.getConversationId();
        Message lastMessage = messageRepository.findByConversationIdAndIsLatest(findConversationId, true)
                .orElseThrow(() -> new BusinessException("[" + findConversationId + "]" + " is not found"));
        lastMessage.setIsLatest(false);
        messageRepository.save(lastMessage);

        // 保存本次消息，并设置为最后一条消息
        String newMessageId = GenerateUtil.generateMessageId(findConversationId, lastMessage.getMessageId());
        message.setMessageId(newMessageId);
        message.setIsLatest(true);
        message.setConversationId(findConversationId);
        message.setFromId(messageModal.getFromId());
        message.setToId(messageModal.getToId());
        message.setSendTime(messageModal.getSendTime());
        message.setType(messageModal.getType());
        message.setContent(messageModal.getContent());
        messageRepository.save(message);

        // 将最后一条消息的id绑定到会话中
        Conversation dbConversation = conversationRepository.findByConversationId(findConversationId)
                .orElseThrow(() -> new BusinessException("[" + findConversationId + "]" + " is not found"));
        dbConversation.setLastMessageId(newMessageId);
        dbConversation.addUnread();
        conversationRepository.save(dbConversation);
    }
}

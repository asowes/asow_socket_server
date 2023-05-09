package com.young.asow.service;

import com.young.asow.entity.Conversation;
import com.young.asow.entity.Message;
import com.young.asow.entity.User;
import com.young.asow.entity.UserConversation;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.repository.ConversationRepository;
import com.young.asow.repository.MessageRepository;
import com.young.asow.repository.UserConversationRepository;
import com.young.asow.repository.UserRepository;
import com.young.asow.util.ConvertUtil;
import com.young.asow.util.GenerateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserConversationRepository userConversationRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            UserConversationRepository userConversationRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userConversationRepository = userConversationRepository;
    }

    public List<ConversationModal> getConversations2(Long userId) {
        List<UserConversation> userConversations = userConversationRepository.findByUserId(userId);
        List<ConversationModal> modals = new ArrayList<>();

        for (UserConversation userConversation : userConversations) {
            Conversation conversation = userConversation.getConversation();
            User from = conversation.getFrom();
            User to = conversation.getTo();
            ConversationModal modal = ConvertUtil.Conversation2Modal(conversation);
            modal.setUnread(userConversation.getUnread());
            modal.setFrom(ConvertUtil.User2Modal(from));
            modal.setTo(ConvertUtil.User2Modal(to));
            modals.add(modal);
        }

        return modals;
    }


    public List<ConversationModal> getConversations(Long userId) {
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

                    User to = conversation.getTo();
//                    User from = conversation.getFrom();
                    Message message = conversation.getLastMessage();

                    modal.setTo(ConvertUtil.User2Modal(to));
//                    modal.setFrom(ConvertUtil.User2Modal(from));
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
        Long findConversationId = messageModal.getConversationId();
        Message lastMessage = messageRepository.findByConversationIdAndIsLatest(findConversationId, true)
                .orElseThrow(() -> new BusinessException("[" + findConversationId + "]" + " is not found"));
        lastMessage.setIsLatest(false);
        messageRepository.save(lastMessage);

        // 保存本次消息，并设置为最后一条消息
        String newMessageId = GenerateUtil.generateMessageId(findConversationId, lastMessage.getMessageId());
        message.setMessageId(newMessageId);
        message.setIsLatest(true);
//        message.setConversationId(findConversationId);
//        message.setFromId(messageModal.getFromId());
//        message.setToId(messageModal.getToId());
        message.setSendTime(messageModal.getSendTime());
        message.setType(messageModal.getType());
        message.setContent(messageModal.getContent());
        messageRepository.save(message);

        // 将最后一条消息的id绑定到会话中
        Conversation dbConversation = conversationRepository.findById(findConversationId)
                .orElseThrow(() -> new BusinessException("[" + findConversationId + "]" + " is not found"));
//        dbConversation.setLastMessageId(newMessageId);
//        dbConversation.addUnread();
        conversationRepository.save(dbConversation);
    }
}

package com.young.asow.service;

import com.young.asow.entity.*;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.GroupUserModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.repository.*;
import com.young.asow.util.ConvertUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.young.asow.entity.Conversation.conversationIsSingle;

@Log4j2
@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserConversationRepository userConversationRepository;
    private final GroupUserRepository groupUserRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            UserConversationRepository userConversationRepository,
            GroupUserRepository groupUserRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userConversationRepository = userConversationRepository;
        this.groupUserRepository = groupUserRepository;
    }

    public List<ConversationModal> getConversations(Long userId) {
        List<ConversationModal> modals = new ArrayList<>();

        // 私聊的会话
        List<UserConversation> userConversations = userConversationRepository.findByUserId(userId);
        for (UserConversation userConversation : userConversations) {
            Conversation conversation = userConversation.getConversation();
            User from = conversation.getFrom();
            User to = conversation.getTo();
            Message last = conversation.getLastMessage();

            ConversationModal modal = ConvertUtil.Conversation2Modal(conversation, from, to);
            modal.setUnread(userConversation.getUnread());
            modal.setLastMessage(ConvertUtil.Message2LastMessage(last));
            modals.add(modal);
        }

        // 群组的会话
        List<GroupUser> groupUsers = groupUserRepository.findByUserId(userId);
        for (GroupUser groupUser : groupUsers) {
            Conversation conversation = groupUser.getChatGroup().getConversation();
            List<GroupUserModal> currentGroupUsers =
                    groupUserRepository.findByChatGroupId(conversation.getChatGroup().getId())
                            .stream()
                            .map(ConvertUtil::GroupUser2Modal)
                            .collect(Collectors.toList());
            User from = groupUser.getUser();
            Message last = conversation.getLastMessage();

            ConversationModal modal = ConvertUtil.Conversation2Modal(conversation, from, null);
            modal.setUnread(groupUser.getUnread());
            modal.setLastMessage(ConvertUtil.Message2LastMessage(last));
            modal.setGroupUsers(currentGroupUsers);
            modals.add(modal);
        }

        return modals;
    }


    /**
     * 先从数据库根据id降序排列拿到最近的消息，然后将这些数据升序排列以保证前端看到的是自上而下的
     * page往上增加就相当于获取上一段记录
     */
    public List<MessageModal> getConversationMessages(Long conversationId, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, 15, sort);
        return messageRepository
                .findMessagesByConversationId(pageable, conversationId)
                .stream()
                .map(ConvertUtil::Message2Modal)
                .sorted(Comparator.comparing(MessageModal::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MessageModal> saveMessageWithConversation(MessageModal messageModal, Long fromId) {
        Long findConversationId = messageModal.getConversationId();
        Long toId = messageModal.getToId();

        Conversation dbConversation = conversationRepository.findById(findConversationId)
                .orElseThrow(() -> new BusinessException("[" + findConversationId + "]" + " is not found"));

        User to;
        User from = userRepository.findById(fromId)
                .orElseThrow(() -> new BusinessException("[" + fromId + "]" + " is not found"));

        if (conversationIsSingle(dbConversation)) {
            to = userRepository.findById(toId)
                    .orElseThrow(() -> new BusinessException("[" + toId + "]" + " is not found"));
        } else {
            to = null;
        }

        // 保存本次消息，并设置为最后一条消息
        Message message = new Message();
        message.setConversation(dbConversation);
        message.setFrom(from);
        message.setTo(to);
        message.setSendTime(messageModal.getSendTime());
        message.setType(Message.ContentType.valueOf(messageModal.getType()));
        message.setContent(messageModal.getContent());
        messageRepository.save(message);

        // 将最后一条消息的id绑定到会话中
        dbConversation.setLastMessage(message);
        conversationRepository.save(dbConversation);

        // 给对方增加未读数量
        List<MessageModal> messageModals = new ArrayList<>();

        if (conversationIsSingle(dbConversation)) {
            UserConversation userConversation =
                    userConversationRepository.findByUserIdAndConversationId(toId, dbConversation.getId());
            userConversation.setUnread(userConversation.getUnread() + 1);

            MessageModal modal = new MessageModal();
            modal.setId(message.getId());
            modal.setUnread(userConversation.getUnread());
            modal.setToId(toId);
            messageModals.add(modal);
        } else {
            List<GroupUser> groupUsers = groupUserRepository.findByChatGroupId(dbConversation.getChatGroup().getId());
            groupUsers.stream()
                    .filter(groupUser -> !Objects.equals(groupUser.getUser().getId(), fromId))
                    .forEach(groupUser -> {
                        MessageModal modal = new MessageModal();
                        groupUser.setUnread(groupUser.getUnread() + 1);
                        modal.setId(message.getId());
                        modal.setToId(groupUser.getUser().getId());
                        modal.setUnread(groupUser.getUnread());
                        messageModals.add(modal);
                    });
        }

        return messageModals;
    }


    public void readMessage(Long userId, Long conversationId) {
        Conversation dbConversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("[" + conversationId + "]" + " is not found"));

        if (conversationIsSingle(dbConversation)) {
            UserConversation userConversation =
                    userConversationRepository.findByUserIdAndConversationId(userId, conversationId);
            userConversation.setUnread(0);
            userConversationRepository.save(userConversation);
        } else {
            GroupUser groupUser = groupUserRepository
                    .findByChatGroupIdAndUserId(dbConversation.getChatGroup().getId(), userId);
            groupUser.setUnread(0);
            groupUserRepository.save(groupUser);
        }
    }


}

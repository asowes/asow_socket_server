package com.young.asow.service;

import com.young.asow.entity.*;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.FriendApplyModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.modal.UserInfoModal;
import com.young.asow.repository.*;
import com.young.asow.util.ConvertUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserConversationRepository userConversationRepository;
    private final FriendApplyRepository friendApplyRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            UserConversationRepository userConversationRepository,
            FriendApplyRepository friendApplyRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userConversationRepository = userConversationRepository;
        this.friendApplyRepository = friendApplyRepository;
    }

    public List<ConversationModal> getConversations(Long userId) {
        List<UserConversation> userConversations = userConversationRepository.findByUserId(userId);
        List<ConversationModal> modals = new ArrayList<>();

        for (UserConversation userConversation : userConversations) {
            Conversation conversation = userConversation.getConversation();
            User from = conversation.getFrom();
            User to = conversation.getTo();
            Message last = conversation.getLastMessage();

            ConversationModal modal = ConvertUtil.Conversation2Modal(conversation);
            modal.setUnread(userConversation.getUnread());
            modal.setFrom(ConvertUtil.User2Modal(from));
            modal.setTo(ConvertUtil.User2Modal(to));
            modal.setLastMessage(ConvertUtil.Message2LastMessage(last));
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
    public MessageModal saveMessageWithConversation(MessageModal messageModal, Long fromId) {
        Long findConversationId = messageModal.getConversationId();
        Long toId = messageModal.getToId();

        Conversation dbConversation = conversationRepository.findById(findConversationId)
                .orElseThrow(() -> new BusinessException("[" + findConversationId + "]" + " is not found"));

        User from = userRepository.findById(fromId)
                .orElseThrow(() -> new BusinessException("[" + fromId + "]" + " is not found"));

        User to = userRepository.findById(toId)
                .orElseThrow(() -> new BusinessException("[" + toId + "]" + " is not found"));

        Message message = new Message();

        // 保存本次消息，并设置为最后一条消息
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
        UserConversation userConversation =
                userConversationRepository.findByUserIdAndConversationId(toId, dbConversation.getId());
        userConversation.setUnread(userConversation.getUnread() + 1);

        MessageModal modal = new MessageModal();
        modal.setUnread(userConversation.getUnread());
        modal.setId(message.getId());
        return modal;
    }


    public void readMessage(Long userId, Long conversationId) {
        UserConversation userConversation =
                userConversationRepository.findByUserIdAndConversationId(userId, conversationId);
        userConversation.setUnread(0);
        userConversationRepository.save(userConversation);
    }


    public List<UserInfoModal> searchUsers(String keyword) {
        // todo 查找的数据要返回是否添加过好友状态
        return userRepository.findByKeyword(keyword)
                .stream()
                .map(ConvertUtil::User2Modal)
                .collect(Collectors.toList());
    }


    public void applyFriend(Long userId, Long accepterId) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("[" + userId + "]" + " is not found"));

        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new BusinessException("[" + accepterId + "]" + " is not found"));

        // 先判断是否已经是好友
        List<FriendApply> friendApplyList = friendApplyRepository.findFriendApply(userId, accepterId);
        boolean hasApplying = friendApplyList
                .stream()
                .filter(friendApply ->
                        FriendApply.STATUS.APPLYING.equals(friendApply.getStatus()))
                .count() == 1;
        if (hasApplying) {
            throw new BusinessException("用户已经发送过好友请求了");
        }

        boolean hasAccept = friendApplyList
                .stream().anyMatch(friendApply ->
                        FriendApply.STATUS.ACCEPT.equals(friendApply.getStatus()));
        if (hasAccept) {
            throw new BusinessException("用户已经添加过该好友了");
        }

        FriendApply friendApply = new FriendApply();
        friendApply.setSender(sender);
        friendApply.setAccepter(accepter);
        friendApply.setApplyTime(LocalDateTime.now());
        friendApply.setStatus(FriendApply.STATUS.APPLYING);
        friendApplyRepository.save(friendApply);
    }

    public List<FriendApplyModal> getMyFriendApply(Long userId) {
        User my = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("[" + userId + "]" + " is not found"));

        return my.getAcceptApplies()
                .stream()
                .map(ac -> ConvertUtil.FriendApply2Modal(ac.getSender(), ac))
                .collect(Collectors.toList());
    }

    public void handleFriendApply(Long userId, Long senderId, FriendApplyModal modal) {
        FriendApply friendApply = friendApplyRepository.findApplying(senderId, userId)
                .orElseThrow(() -> new BusinessException("你没有发起过该好友请求"));

        FriendApply.STATUS status = FriendApply.STATUS.valueOf(modal.getStatus());

        if (Objects.equals(FriendApply.STATUS.APPLYING, status)) {
            throw new BusinessException("参数异常");
        }


        friendApply.setStatus(status);
        friendApplyRepository.save(friendApply);
    }
}

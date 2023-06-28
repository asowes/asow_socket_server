package com.young.asow.service;

import com.young.asow.entity.*;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.FriendApplyModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.repository.*;
import com.young.asow.util.ConvertUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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


    // ********************************** 放到另一个service

    public List<FriendApplyModal> searchUsers(Long meId, String keyword) {
        return userRepository.findByKeyword(keyword)
                .stream()
                .map(findUser -> {
                    FriendApply friendApply =
                            friendApplyRepository.relationship(meId, findUser.getId())
                                    .orElse(null);

                    return ConvertUtil.FriendApply2Modal(findUser, friendApply);
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void applyFriend(Long userId, Long accepterId) {
        Assert.isTrue(!Objects.equals(userId, accepterId), "不能添加自己为好友");

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("[" + userId + "]" + " is not found"));

        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new BusinessException("[" + accepterId + "]" + " is not found"));

        // 判断向对方是否发送过好友请求
        List<FriendApply> friendApplyList = friendApplyRepository.findFriendApply(userId, accepterId);
        boolean hasApplying = friendApplyList
                .stream()
                .filter(friendApply ->
                        FriendApply.STATUS.APPLYING.equals(friendApply.getStatus()))
                .count() == 1;
        if (hasApplying) {
            throw new BusinessException("用户已经发送过好友请求了");
        }

        // 判断两人是否已经是好友
        List<FriendApply> friendAcceptList = friendApplyRepository.findFriendApply(accepterId, userId);
        boolean hasAccept =
                friendApplyList
                        .stream()
                        .anyMatch(friendApply ->
                                FriendApply.STATUS.ACCEPTED.equals(friendApply.getStatus())) ||
                        friendAcceptList
                                .stream()
                                .anyMatch(friendApply ->
                                        FriendApply.STATUS.ACCEPTED.equals(friendApply.getStatus())
                                );
        if (hasAccept) {
            throw new BusinessException("用户已经添加过该好友了");
        }

        // 如果对方也正在申请添加自己为好友，那么不用等对方通过直接添加好友
        friendApplyRepository.relationship(accepterId, userId)
                .ifPresentOrElse(friendApply -> {
                    if (!FriendApply.STATUS.APPLYING.equals(friendApply.getStatus())) {
                        return;
                    }
                    friendApply.setStatus(FriendApply.STATUS.ACCEPTED);
                    friendApplyRepository.save(friendApply);
                    becomeFriendCreateConversation(sender, accepter);
                }, () -> {
                    FriendApply friendApply = new FriendApply();
                    friendApply.setSender(sender);
                    friendApply.setAccepter(accepter);
                    friendApply.setApplyTime(LocalDateTime.now());
                    friendApply.setStatus(FriendApply.STATUS.APPLYING);
                    friendApplyRepository.save(friendApply);
                });
    }

    public List<FriendApplyModal> getMyFriendApply(Long userId) {
        return friendApplyRepository.findLatestByAccepterId(userId)
                .stream()
                .map(ac -> ConvertUtil.FriendApply2Modal(ac.getSender(), ac))
                .collect(Collectors.toList());
    }

    @Transactional
    public void handleFriendApply(Long userId, Long senderId, FriendApplyModal modal) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException("[" + senderId + "]" + " is not found"));

        User accepter = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("[" + userId + "]" + " is not found"));

        FriendApply friendApply = friendApplyRepository.findApplying(senderId, userId)
                .orElseThrow(() -> new BusinessException("你没有发起过该好友请求"));

        FriendApply.STATUS status = FriendApply.STATUS.valueOf(modal.getStatus());

        if (Objects.equals(FriendApply.STATUS.APPLYING, status)) {
            throw new BusinessException("参数异常");
        }

        friendApply.setStatus(status);
        friendApply.setOperateTime(LocalDateTime.now());
        friendApplyRepository.save(friendApply);
        becomeFriendCreateConversation(sender, accepter);
    }


    private void becomeFriendCreateConversation(User sender, User accepter) {
        Conversation conversation = new Conversation();
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setFrom(sender);
        conversation.setTo(accepter);
        Conversation dbConversation = conversationRepository.save(conversation);

        UserConversation sendConversation = createUserConversation(sender, dbConversation);
        UserConversation acceptConversation = createUserConversation(accepter, dbConversation);

        dbConversation.getUserConversations().add(sendConversation);
        dbConversation.getUserConversations().add(acceptConversation);
        conversationRepository.save(dbConversation);
    }

    private UserConversation createUserConversation(User user, Conversation conversation) {
        UserConversation userConversation = new UserConversation();

        UserConversationId userConversationId = new UserConversationId();
        userConversationId.setConversationId(conversation.getId());
        userConversationId.setUserId(user.getId());

        userConversation.setUser(user);
        userConversation.setId(userConversationId);
        userConversation.setConversation(conversation);
        userConversation.setUnread(0);
        return userConversation;
    }


}

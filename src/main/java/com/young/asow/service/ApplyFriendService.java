package com.young.asow.service;

import com.alibaba.fastjson.JSONObject;
import com.young.asow.entity.*;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.FriendApplyModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.repository.*;
import com.young.asow.socket.WebSocketServer;
import com.young.asow.util.ConvertUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplyFriendService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final FriendApplyRepository friendApplyRepository;

    public ApplyFriendService(
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            FriendApplyRepository friendApplyRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.friendApplyRepository = friendApplyRepository;
    }


    public List<FriendApplyModal> searchUsers(Long meId, String keyword) {
        List<FriendApplyModal> data = new ArrayList<>();
        Set<Long> processedApplies = new HashSet<>();

        List<User> findUsers = userRepository.findByKeyword(keyword);
        findUsers.forEach(findUser -> {
            FriendApplyModal modal = ConvertUtil.FriendApply2Modal(findUser, null);
            if (Objects.equals(findUser.getId(), meId)) {
                modal.setStatus(FriendApply.STATUS.SELF.name());
                data.add(modal);
                return;
            }

            List<FriendApply> userAcceptApplies = findUser.getAcceptApplies();
            List<FriendApply> userSendApplies = findUser.getSendApplies();

            handleFriendApplies(modal, userAcceptApplies, meId, processedApplies);
            handleFriendApplies(modal, userSendApplies, meId, processedApplies);
            data.add(modal);
        });

        return data;
    }

    private void handleFriendApplies(
            FriendApplyModal modal,
            List<FriendApply> friendApplies,
            Long meId,
            Set<Long> processedApplies
    ) {
        friendApplies.stream()
                .filter(apply -> !processedApplies.contains(apply.getId()))
                .filter(apply ->
                        Objects.equals(apply.getSender().getId(), meId)
                                ||
                                Objects.equals(apply.getAccepter().getId(), meId)
                )
                .forEach(apply -> {
                    if (Objects.equals(apply.getAccepter().getId(), meId)
                            && apply.getStatus().equals(FriendApply.STATUS.APPLYING)
                    ) {
                        modal.setStatus(FriendApply.STATUS.BE_APPLIED.name());
                    } else {
                        modal.setStatus(apply.getStatus().name());
                    }
                    processedApplies.add(apply.getId());
                });
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
                    friendApply.setOperateTime(LocalDateTime.now());
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

        if (!Objects.equals(FriendApply.STATUS.ACCEPTED, status) &&
                !Objects.equals(FriendApply.STATUS.REFUSED, status)
        ) {
            throw new BusinessException("参数异常");
        }

        friendApply.setStatus(status);
        friendApply.setOperateTime(LocalDateTime.now());
        friendApplyRepository.save(friendApply);

        if (Objects.equals(FriendApply.STATUS.ACCEPTED, status)) {
            becomeFriendCreateConversation(sender, accepter);
        }
    }


    private void becomeFriendCreateConversation(User sender, User accepter) {
        Conversation conversation = new Conversation();
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setFrom(sender);
        conversation.setTo(accepter);
        Conversation dbConversation = conversationRepository.save(conversation);

        UserConversation sendConversation = createUserConversation(sender, dbConversation);
        UserConversation acceptConversation = createUserConversation(accepter, dbConversation);

        sendConversation.setUnread(1);
        dbConversation.getUserConversations().add(sendConversation);
        dbConversation.getUserConversations().add(acceptConversation);

        // 设置添加好友后的第一句问候语
        Message message = new Message();
        message.setConversation(dbConversation);
        message.setFrom(accepter);
        message.setTo(sender);
        message.setSendTime(LocalDateTime.now());
        message.setType(Message.ContentType.TEXT);
        message.setContent("我已经通过你的好友验证");

        dbConversation.setLastMessage(message);
        Conversation newConversation = conversationRepository.save(dbConversation);

        // 向前端发送websocket通知建立conversation
        MessageModal modal = new MessageModal();
        modal.setEvent("applyFriend");
        ConversationModal conversationModal = ConvertUtil.Conversation2Modal(newConversation, sender, accepter);
        conversationModal.setLastMessage(ConvertUtil.Message2LastMessage(newConversation.getLastMessage()));
        modal.setData(conversationModal);
        WebSocketServer.sendMessageByWayBillId(sender.getId(), JSONObject.toJSONString(modal));
        WebSocketServer.sendMessageByWayBillId(accepter.getId(), JSONObject.toJSONString(modal));

        // 在conversation中添加第一条消息
        MessageModal messageModal = new MessageModal();
        messageModal.setEvent("chat");
        messageModal.setId(newConversation.getLastMessage().getId());
        messageModal.setUnread(0);
        messageModal.setContent(message.getContent());
        messageModal.setType(message.getType().name());
        messageModal.setConversationId(newConversation.getId());
        messageModal.setFromId(accepter.getId());
        messageModal.setToId(sender.getId());
        messageModal.setSendTime(LocalDateTime.now());
        WebSocketServer.sendMessageByWayBillId(accepter.getId(), JSONObject.toJSONString(messageModal));
        messageModal.setUnread(1);
        WebSocketServer.sendMessageByWayBillId(sender.getId(), JSONObject.toJSONString(messageModal));
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

package com.young.asow.service;

import com.alibaba.fastjson.JSONObject;
import com.young.asow.entity.*;
import com.young.asow.exception.BusinessException;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.GroupUserModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.modal.UserInfoModal;
import com.young.asow.repository.*;
import com.young.asow.socket.WebSocketServer;
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
import java.util.stream.Stream;

import static com.young.asow.entity.Conversation.conversationIsSingle;

@Log4j2
@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserConversationRepository userConversationRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRelationshipRepository userRelationshipRepository;
    private final ChatGroupRepository chatGroupRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            UserConversationRepository userConversationRepository,
            GroupUserRepository groupUserRepository,
            UserRelationshipRepository userRelationshipRepository,
            ChatGroupRepository chatGroupRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userConversationRepository = userConversationRepository;
        this.groupUserRepository = groupUserRepository;
        this.userRelationshipRepository = userRelationshipRepository;
        this.chatGroupRepository = chatGroupRepository;
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
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

    public List<UserInfoModal> findMyFriends(Long userId) {
        List<UserRelationship> userRelationships = userRelationshipRepository.findUserRelationships(userId);
        List<UserInfoModal> friendModals = new ArrayList<>();
        userRelationships
                .forEach(userRelationship -> {
                    if (!Objects.equals(userRelationship.getUser().getId(), userId)) {
                        friendModals.add(ConvertUtil.User2Modal(userRelationship.getUser()));
                    }
                    if (!Objects.equals(userRelationship.getFriend().getId(), userId)) {
                        friendModals.add(ConvertUtil.User2Modal(userRelationship.getFriend()));
                    }
                });

        return friendModals
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }


    @Transactional
    public void createChatGroup(Long leaderId, List<Long> memberIds) {
        List<User> allMembers = userRepository.findAllByIdIn(
                Stream.concat(
                        Stream.of(leaderId),
                        memberIds.stream()
                ).collect(Collectors.toList()));

        // 创建一个Conversation
        Conversation conversation = new Conversation();
        conversation.setType(Conversation.Type.GROUP);

        // 创建一个Group，绑定ConversationId
        ChatGroup chatGroup = new ChatGroup();
        List<String> nickNames = allMembers
                .stream()
                .map(User::getNickname)
                .collect(Collectors.toList());
        chatGroup.setConversation(conversation);
        chatGroup.setName(String.join("、", nickNames));

        // 创建一个LastMessage，与Conversation双向绑定
        Message sysMessage = new Message();
        User sys = userRepository.findById(User.sysId)
                .orElseThrow(() -> new BusinessException("[" + User.sysId + "]" + " is not found"));
        sysMessage.setFrom(sys);
        sysMessage.setSendTime(LocalDateTime.now());
        sysMessage.setContent(String.join("、", nickNames) + "加入了群聊");
        sysMessage.setConversation(conversation);
        conversation.setLastMessage(sysMessage);

        ChatGroup dbGroup = chatGroupRepository.save(chatGroup);

        // 将群成员创建到GroupUser表，绑定ChatGroup
        List<GroupUser> groupUsers = allMembers
                .stream()
                .map(user -> {
                    GroupUser groupUser = new GroupUser();
                    groupUser.setUser(user);
                    groupUser.setChatGroup(dbGroup);
                    groupUser.setUnread(1);
                    return groupUser;
                })
                .collect(Collectors.toList());
        List<GroupUser> dbGroupUsers = groupUserRepository.saveAll(groupUsers);

        // 向N个GroupUser发送websocket-通知客户端创建房间
        MessageModal createModal = new MessageModal();
        createModal.setEvent("createGroup");
        ConversationModal conversationModal = ConvertUtil.Conversation2Modal(chatGroup.getConversation(), null, null);
        conversationModal.setChatGroup(ConvertUtil.ChatGroup2Modal(chatGroup));
        conversationModal.setLastMessage(ConvertUtil.Message2LastMessage(conversation.getLastMessage()));
        conversationModal.setGroupUsers(dbGroupUsers.stream().map(ConvertUtil::GroupUser2Modal).collect(Collectors.toList()));
        createModal.setData(conversationModal);
        allMembers.forEach(user -> {
            WebSocketServer.sendMessage(user.getId(), JSONObject.toJSONString(createModal));
        });

        // 向N个GroupUser发送websocket-发送问候语
        MessageModal chatModal = new MessageModal();
        Message dbMessage = chatGroup.getConversation().getLastMessage();
        chatModal.setEvent("chat");
        chatModal.setId(dbMessage.getId());
        chatModal.setUnread(0);
        chatModal.setContent(dbMessage.getContent());
        chatModal.setConversationId(chatGroup.getConversation().getId());
        chatModal.setFromId(User.sysId);
        chatModal.setSendTime(LocalDateTime.now());
        allMembers.forEach(user -> {
            WebSocketServer.sendMessage(user.getId(), JSONObject.toJSONString(chatModal));
        });

    }


}

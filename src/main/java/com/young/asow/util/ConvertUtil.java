package com.young.asow.util;


import com.young.asow.entity.*;
import com.young.asow.modal.*;
import org.springframework.beans.BeanUtils;

import java.util.Objects;
import java.util.function.Supplier;

public class ConvertUtil {

    private static <S, T> T convert(S source, Class<T> targetClass, Supplier<T> defaultValueSupplier) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            if (source != null) {
                BeanUtils.copyProperties(source, target);
                return target;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValueSupplier.get();
    }

    public static ConversationModal Conversation2Modal(Conversation conversation, User from, User to) {
        ConversationModal modal = convert(conversation, ConversationModal.class, ConversationModal::new);
        modal.setFrom(ConvertUtil.User2Modal(from));
        modal.setTo(ConvertUtil.User2Modal(to));
        modal.setType(conversation.getType().name());
        if (conversation.getType().equals(Conversation.Type.GROUP)) {
            modal.setChatGroup(ChatGroup2Modal(conversation.getChatGroup()));
        }
        return modal;
    }

    public static ChatGroupModal ChatGroup2Modal(ChatGroup chatGroup) {
        return convert(chatGroup, ChatGroupModal.class, ChatGroupModal::new);
    }

    public static UserInfoModal User2Modal(User user) {
        return convert(user, UserInfoModal.class, UserInfoModal::new);
    }

    public static MessageModal Message2Modal(Message message) {
        MessageModal modal = convert(message, MessageModal.class, MessageModal::new);
        if (Objects.isNull(message)) {
            return modal;
        }
        if (!Objects.isNull(message.getTo())) {
            modal.setToId(message.getTo().getId());
        }
        modal.setFromId(message.getFrom().getId());
        modal.setConversationId(message.getConversation().getId());
        return modal;
    }

    public static LastMessage Message2LastMessage(Message message) {
        LastMessage modal = convert(message, LastMessage.class, LastMessage::new);
        if (Objects.nonNull(message)) {
            modal.setFromId(message.getFrom().getId());
        }
        return modal;
    }

    public static FriendApplyModal FriendApply2Modal(User user, FriendApply friendApply) {
        FriendApplyModal modal = convert(user, FriendApplyModal.class, FriendApplyModal::new);
        modal.setUserId(user.getId());
        if (Objects.isNull(friendApply)) {
            modal.setId(null);
            modal.setStatus(FriendApply.STATUS.STRANGE.name());
            return modal;
        }
        modal.setId(friendApply.getId());
        modal.setStatus(friendApply.getStatus().name());
        return modal;
    }

    public static GroupUserModal GroupUser2Modal(GroupUser groupUser) {
        GroupUserModal modal = convert(groupUser, GroupUserModal.class, GroupUserModal::new);
        modal.setUserId(groupUser.getUser().getId());
        modal.setAvatar(groupUser.getUser().getAvatar());
        modal.setNickname(groupUser.getUser().getNickname());
        modal.setRole(groupUser.getRole().name());
        return modal;
    }
}

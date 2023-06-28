package com.young.asow.util;


import com.young.asow.entity.Conversation;
import com.young.asow.entity.FriendApply;
import com.young.asow.entity.Message;
import com.young.asow.entity.User;
import com.young.asow.modal.*;
import org.springframework.beans.BeanUtils;

import java.util.Objects;
import java.util.function.Supplier;

public class ConvertUtil {

    public static <S, T> T convert(S source, Class<T> targetClass, Supplier<T> defaultValueSupplier) {
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

    public static ConversationModal Conversation2Modal(Conversation conversation) {
        return convert(conversation, ConversationModal.class, ConversationModal::new);
    }

    public static UserInfoModal User2Modal(User user) {
        return convert(user, UserInfoModal.class, UserInfoModal::new);
    }

    public static MessageModal Message2Modal(Message message) {
        MessageModal modal = convert(message, MessageModal.class, MessageModal::new);
        if (Objects.isNull(message)) {
            return modal;
        }
        modal.setFromId(message.getFrom().getId());
        modal.setToId(message.getTo().getId());
        modal.setConversationId(message.getConversation().getId());
        return modal;
    }

    public static LastMessage Message2LastMessage(Message message) {
        return convert(message, LastMessage.class, LastMessage::new);
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
}

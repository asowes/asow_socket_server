package com.young.asow.util;


import com.young.asow.entity.Conversation;
import com.young.asow.entity.Message;
import com.young.asow.entity.User;
import com.young.asow.modal.ConversationModal;
import com.young.asow.modal.MessageModal;
import com.young.asow.modal.UserInfoModal;
import org.springframework.beans.BeanUtils;

public class ConvertUtil {

    public static ConversationModal Conversation2Modal(Conversation conversation) {
        ConversationModal modal = new ConversationModal();
        BeanUtils.copyProperties(conversation, modal);
        return modal;
    }

    public static UserInfoModal User2Modal(User user) {
        UserInfoModal modal = new UserInfoModal();
        BeanUtils.copyProperties(user, modal);
        return modal;
    }

    public static MessageModal Message2Modal(Message message) {
        MessageModal modal = new MessageModal();
        BeanUtils.copyProperties(message, modal);
        modal.setFromId(message.getFrom().getId());
        modal.setToId(message.getTo().getId());
        return modal;
    }
}

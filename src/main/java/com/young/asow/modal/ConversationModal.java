package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationModal {

    String conversationId;

    UserInfoModal to;

    UserInfoModal from;

    MessageModal lastMessage;

    LocalDateTime createTime;

    int unread;

    String topPriority;
}

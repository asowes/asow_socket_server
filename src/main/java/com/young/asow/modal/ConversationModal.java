package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationModal {
    Long id;

    UserInfoModal to;

    UserInfoModal from;

    LastMessage lastMessage;

    LocalDateTime createTime;

    int unread;

    String topPriority;

    String type;

    ChatGroupModal chatGroup;

    List<GroupUserModal> groupUsers;
}

package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageModal {
    Long id;

    String event;

    String messageId;

    Long conversationId;

    Long fromId;

    Long toId;

    LocalDateTime sendTime;

    String content;

    String type;

    Boolean isLatest;

    Boolean loading;

    String clientUUid;
}

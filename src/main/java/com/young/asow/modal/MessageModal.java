package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageModal {

    String event;

    String messageId;

    Long conversationId;

    String fromId;

    String toId;

    LocalDateTime sendTime;

    String content;

    String type;

    Boolean isLatest;

    Boolean loading;

    String clientUUid;
}

package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;


@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageModal extends LastMessage {

    String event;

    String messageId;

    Long conversationId;

    Long fromId;

    Long toId;

    String type;

    Boolean isLatest;

    int unread;

    Boolean loading;

    String clientUUid;
}

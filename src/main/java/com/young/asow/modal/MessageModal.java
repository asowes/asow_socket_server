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

    Long conversationId;

    Long fromId;

    Long toId;

    int unread;

    Boolean loading;

    String clientUUid;
}

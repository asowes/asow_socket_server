package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageModal {

    String messageId;

    String conversationId;

    LocalDateTime sendTime;

    String content;

    String type;

    Boolean isLatest;
}

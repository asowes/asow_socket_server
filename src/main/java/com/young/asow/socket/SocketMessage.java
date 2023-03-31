package com.young.asow.socket;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocketMessage {

    String type = "";

    String messageType = "text";

    String messageContent = "";

    String event = "";

    String toId;

    Object data = new Object();
}

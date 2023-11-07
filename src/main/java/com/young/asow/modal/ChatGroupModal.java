package com.young.asow.modal;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatGroupModal {

    Long groupId;

    LocalDateTime createTime;

    String name;

    String avatar;
}

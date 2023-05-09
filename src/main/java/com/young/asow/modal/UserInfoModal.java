package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoModal {
    Long id;

    String userId;

    String nickname;

    String avatar;
}

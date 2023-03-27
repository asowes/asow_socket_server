package com.young.asow.modal;

import lombok.*;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserModal {

    String username;

    String password;
}

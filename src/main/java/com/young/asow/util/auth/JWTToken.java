package com.young.asow.util.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JWTToken {

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String TOKEN_PREFIX_BEARER = "Bearer";

    /**
     * login user's id
     */
    Long userId;

    /**
     * issue token
     */
    String token;

    /**
     * login user's roles
     */
    Set<String> roles;

}

package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column
    @NonNull
    String username;

    @Column
    String nickname;

    @Column
    @NonNull
    String password;

    @Column
    @NonNull
    String email;

    @Column
    LocalDateTime lastLoginTime;

    @ElementCollection(fetch = FetchType.EAGER)
    Set<Authority> authorities = new HashSet<>();

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

}

package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "user")
@Getter
@Setter
//@ToString
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
    String avatar;

    @Column
    LocalDateTime lastLoginTime;

    @ElementCollection(fetch = FetchType.EAGER)
    Set<Authority> authorities = new HashSet<>();

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

//    @OneToMany(mappedBy = "from", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
//    private List<Message> fromMessages = new ArrayList<>();
//
//    @OneToMany(mappedBy = "to", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
//    private List<Message> toMessages = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private Set<UserConversation> userConversations = new HashSet<>();
}

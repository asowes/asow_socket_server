package com.young.asow.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity(name = "chat_user_relationship")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRelationship {

    @EmbeddedId
    UserRelationshipId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @MapsId("friendId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    User friend;

    /**
     * 对好友名字的备注
     */
    @Column
    String remarkName;
}
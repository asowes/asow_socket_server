package com.young.asow.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "chat_user_group")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupUser extends BaseEntity {

    @Column
    String userInGroupName;

    @Column
    int unread;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_group_id")
    private ChatGroup chatGroup;

}

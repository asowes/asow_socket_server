package com.young.asow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "chat_group")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatGroup extends BaseEntity {

    @Column
    String name;

    @Column
    String avatar;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToMany(mappedBy = "chatGroups")
    Set<User> groupUsers = new HashSet<>();

}

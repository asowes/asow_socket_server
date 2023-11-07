package com.young.asow.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "chat_conversation")
@Getter
@Setter
//@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation extends BaseEntity {

    @Column
    String topPriority;

    @Column
    Type type;

    public enum Type {
        SINGLE,
        GROUP;
    }

    public static boolean conversationIsSingle(Conversation conversation) {
        return conversation.getType().equals(Type.SINGLE);
    }

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @JsonManagedReference
    @OneToOne(mappedBy = "conversation", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_group_id")
    private ChatGroup chatGroup;

//    @OneToMany(mappedBy = "conversation", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
//    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private User from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private User to;

    @OneToMany(mappedBy = "conversation", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserConversation> userConversations = new ArrayList<>();
}

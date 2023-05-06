package com.young.asow.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "chat_conversation")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Conversation extends BaseEntity {

    //    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "conversation-id")
//    @GenericGenerator(name = "conversation-id", strategy = "com.young.asow.util.PrimaryIDGenerator")
    @Column
    String conversationId;

    @Column
    String fromId;

    @Column
    String toId;

    @Column
    String lastMessageId;

    @Column
    LocalDateTime createTime;

    @Column
    int unread = 0;

    @Column
    String topPriority;

    public void addUnread() {
        this.unread += 1;
    }
}

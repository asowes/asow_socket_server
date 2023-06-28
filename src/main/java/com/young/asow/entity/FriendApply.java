package com.young.asow.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "chat_friend_apply")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class FriendApply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepter_id")
    User accepter;

    @Column
    STATUS status;

    @Column
    LocalDateTime applyTime;

    @Column
    LocalDateTime operateTime;

    public enum STATUS {
        STRANGE,
        APPLYING,
        ACCEPTED,
        REFUSED;
    }

}

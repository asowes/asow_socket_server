package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends BaseEntity{

    @Column
    Long fromId;

    @Column
    Long toId;

    // 消息状态  1：未读  2：已读  3：撤回
    @Column
    String status;

    // 第一次创建聊天的时间
    @Column
    LocalDateTime createTime;

    @OneToOne(mappedBy = "chat", cascade = CascadeType.ALL)
    ChatContent chatContent;
}

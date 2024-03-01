package com.young.asow.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "chat_message")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
//@NoArgsConstructor
//@AllArgsConstructor
public class Message extends BaseEntity {

    @Column
    String messageId;

    @Length(max = 1024)
    @Column(columnDefinition = "varchar(1024)")
    String content;

    @Column
    LocalDateTime sendTime;

    // 消息类型  1：普通消息  2：图片  3：视频
    @Column
    ContentType type;

    // fixme from暂定是可以为空 除非后续能够加入一个系统提示来占用 否则建议为必填
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private User from;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private User to;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;


    public enum ContentType {
        TEXT,
        IMAGE;
    }
}
